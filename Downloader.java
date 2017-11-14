import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

public class Downloader implements Runnable {
	private String mRequest;
	private String mIP;
	private int mPort;
	private Socket mSocket;
	private long mStartingByte;
	private long mEndingByte;
	private File mFile;
	private boolean mLastThread;
	private boolean mResumeMode;
	private BufferedInputStream bis;
	private final int BUFFER_SIZE = 10240;

	public Downloader(String mIP, int mPort, String mRequest, File mFile, 
			long mStartingByte, long mEndingByte, boolean mLastThread,
			boolean mResumeMode) {
		this.mIP = mIP; this.mPort = mPort;
		this.mRequest = mRequest;
		this.mFile= mFile;
		this.mStartingByte = mStartingByte;
		this.mEndingByte = mEndingByte;
		this.mResumeMode = mResumeMode;
	}

	@Override
	public void run() {
		
		// create socket
		try {
			mSocket = new Socket(mIP, mPort);
		} catch (UnknownHostException e1) {
			System.out.println(e1.getMessage());
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}		
		
		// send another request *micro*-second later
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF8"));
			bw.write(getRangeSpecifiedRequestMessage());
			bw.flush();
			
			// get input stream
			bis = new BufferedInputStream(mSocket.getInputStream());	
			removeHeader();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	
		// write to file	
		RandomAccessFile randomAccessFile = null;
		byte[] bytes = new byte[BUFFER_SIZE];
		
		// if on resume mode, write from file length onward
		long mCurrByte = mResumeMode? mFile.length(): 0;

		try {
			randomAccessFile = new RandomAccessFile(mFile, "rw");
			randomAccessFile.seek(mCurrByte);
			int readByte = 0;
;
			while (mCurrByte <= mEndingByte) {
				readByte = bis.read(bytes, 0, BUFFER_SIZE);
				
				if (readByte == -1) {
					break;
				} else {
					randomAccessFile.write(bytes, 0, readByte);// byteArray, offSet, Len
					mCurrByte += readByte;
				}
			}
			bis.close();
			mSocket.close();
			randomAccessFile.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
	private String getRangeSpecifiedRequestMessage() {
		// if last thread, do not specify ending byte
		String toByte = mLastThread? "": String.valueOf(mEndingByte);
		return mRequest.substring(0, mRequest.length()-2) // rid of \r\n
				+ "Range: bytes=" + mStartingByte + "-" + toByte + "\r\n\r\n";
	}
	private void removeHeader() {
			// we need 4 consecutive escape characters \r\n\r\n
			int escapeCount = 0; 
			int currByte;	
		    try {
		    	while (escapeCount<4) {
		    		currByte = bis.read(); // read one byte
		    		if (currByte==13 || currByte==10) { // either \r or \n
		    			escapeCount += 1;
		    		} else {
		    			escapeCount = 0;
		    		}
		    	}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	}
	
}
