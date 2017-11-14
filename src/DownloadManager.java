import java.io.File;
import java.util.ArrayList;

public class DownloadManager {
	private String mUrl;
	private String mFileName;
	private String mFilePath; // change back to non-static
	private String response;
	private int mThreadCount;
	private HTTPRequest httpReq;
	private HTTPResponseParser parser;
	
	public DownloadManager(String mUrl, String mFileName, int mThreadCount) {
		this.mUrl = mUrl;
		this.mFileName = mFileName;
		this.mFilePath = getFilePath(mFileName);
		this.mThreadCount = mThreadCount;
	}
	public void runDownload() {
		
		// send initial request and get response
		httpReq = new HTTPRequest(mUrl);
		response = httpReq.sendRequest();
		parser = new HTTPResponseParser(response);
		
		// check response code
		if (parser.webPageError()) { // inform and exit
			System.out.println(WebPageError.getErrorMessage(parser.getResponseCode()));
			System.exit(1);
		} else if (parser.webPageRedirect()) { // redirect
			mUrl = parser.getRedirectedLocation();
			this.runDownload();
		} else if (!supportMultipleConnection()) {
			System.out.println("Multi-part Download Unsupported");
			mThreadCount = 1;
			proceed();
		} else if (parser.getContentLength()==0){ // assuming the last clause does not cover it
			mThreadCount = 1;
			proceed();
		} else { // non-concurrent falls in here (?); should work
			proceed();
		}
		
	}	
	private void proceed() {
		// check resume by checking the existence of hidden file and modified date
		if(ResumeCheck.pass(mFileName, parser.getDateModified()) &&
				parser.getResumePermission()) { // also check server permission
			System.out.println("Resuming Download. . .");
			runResumeDownload();
			ResumeCheck.finishDownload(mFileName);
		} else { // new download
			ResumeCheck.startNewDownload(mFileName, parser.getDateModified());
			runNewDownload();
			ResumeCheck.finishDownload(mFileName);
		}
	}
	private boolean supportMultipleConnection() {
		return parser.getContentLength()!=0 && parser.getMultiDownloadServerPermission();
	}
	private void runNewDownload() {
		ArrayList<ByteRange> mByteRanges = ByteRangeHandler.getByteRanges(
						parser.getContentLength(), mThreadCount);
		Thread[] threads = new Thread[mThreadCount];
		int count = 1; 
		for (ByteRange br: mByteRanges) {
			boolean lastThread = count==mThreadCount? true: false;
			Downloader downloader = new Downloader(httpReq.getIP(), httpReq.getPort(), 
					httpReq.getRequestMessage(), new File(mFilePath+count), 
					br.getStartingByte(), br.getEndingByte(), lastThread, false);
			Thread thread = new Thread(downloader);
			threads[count-1] = thread;
			thread.start();
			count++;
		}
		
		for (Thread t: threads) {
			try {
				t.join(); 
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				System.exit(1); // if thread is interrupted, shut down the program
			}
		}
	}
	private void runResumeDownload() {
		ArrayList<ByteRange> mByteRanges = ByteRangeHandler.getUnfinishedByteRanges(
					parser.getContentLength(), mFileName);
		
		// ignore user's thread count and use the previous count instead 
		Thread[] threads = new Thread[mByteRanges.size()];
		
		int count = 1; 
		for (ByteRange br: mByteRanges) {
			boolean lastThread = count==mByteRanges.size()? true: false;
			Downloader downloader = new Downloader(httpReq.getIP(), httpReq.getPort(), 
					httpReq.getRequestMessage(), new File(mFilePath+count), 
					br.getStartingByte(), br.getEndingByte(), lastThread, true);
			Thread thread = new Thread(downloader);
			threads[count-1] = thread;
			thread.start();
			count++;
		}
		
		for (Thread t: threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				System.exit(1); // if thread is interrupted, shut down the program
			}
		}
	}
	private static String getFilePath(String mFileName) {
		return System.getProperty("user.home")+File.separator+"Documents"
				+ File.separator + mFileName;
	}
	public void mergeFile() {
		SegmentedFileJoiner.join(mFilePath, mThreadCount);
	}


}
