
public class DriverClass {
	private static String errMessage = "Invalid Input Parameters";

	public static void main(String args[]) {

		String mFileName="", mUrl="";
		int mThreadCount=0;
		
		// non-concurrent run
		if (args[0].equals("-o") && args.length == 3) { 
			mFileName = args[1]; mUrl = args[2]; mThreadCount = 1;
			runDownload(mUrl, mFileName, mThreadCount);
		// default thread count given as 5
		} else if (args[0].equals("-o") && args.length == 4 && args[2].equals("-c")) {
			mFileName = args[1]; mUrl = args[3]; mThreadCount = 5;
			runDownload(mUrl, mFileName, mThreadCount);
		} else if (args[0].equals("-o") && args.length == 5 && args[2].equals("-c")) {
			mFileName = args[1]; mUrl = args[4];
			
			// check if specified number -c is a digit
			try {
				mThreadCount = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.out.println(errMessage);
				System.exit(1);
			}	
			runDownload(mUrl, mFileName, mThreadCount);
		} else {
			System.out.println(errMessage);
			System.exit(1);
		}
	}
	private static void runDownload(String mUrl, String mFileName, int mThreadCount) {
		DownloadManager downloadManager = new DownloadManager(
				mUrl, mFileName, mThreadCount);
		downloadManager.runDownload();
		downloadManager.mergeFile();
	}
	
}
