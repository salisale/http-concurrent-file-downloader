import java.io.File;
import java.io.IOException;
/**
 * This class checks whether the current download should be in resume mode
 */
public class ResumeCheck {
	// hidden file in user document folder
	private static String getFilePath(String mFileName) {
		return System.getProperty("user.home")+File.separator+"Documents"
				+ File.separator + mFileName;
	}
	/**
	 * The download is in resume mode when the hidden file exists and 
	 * date-modified's match
	 */
	public static boolean pass(String downloadFileName, String dateModified) {
		File mFile = getHiddenFile(downloadFileName);
		return mFile.exists() && checkDateModified(mFile, dateModified);
	}
	/**
	 * Must be called every time new download is starting
	 */
	public static void startNewDownload(String downloadFileName, String dateModified) {
		File mFile = getHiddenFile(downloadFileName);
		if (!mFile.exists()) { 
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		HeaderFileHandler.writeToFile(mFile, dateModified);
	}
	/**
	 * Must be called every time download is successful
	 * It deletes the existing hidden file
	 */
	public static void finishDownload(String downloadFileName) {
		File mFile = getHiddenFile(downloadFileName);
		if (mFile.exists()) {
			mFile.delete();
		}
	}
	private static File getHiddenFile(String downloadFileName) {
		return new File("." + getFilePath(downloadFileName.substring(0, 
								downloadFileName.length()-4)
								+ "IsDownloading.txt"));
	}
	/**
	 * Check content of the file which is its first line
	 */
	private static boolean checkDateModified(File mFile, String dateModified) {
		return HeaderFileHandler.readFromFile(mFile).equals(dateModified);
	}
}
