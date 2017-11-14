import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class ByteRangeHandler {
	/**
	 * @return partitioned ByteRanges for new download
	 */
	public static ArrayList<ByteRange> getByteRanges(int bytes, int count) {
		ArrayList<ByteRange> mByteRanges = new ArrayList<ByteRange>();
		int eachLoad = bytes/count;
		for (int i=0; i<count; i++){
			int mStartingByte = eachLoad*i; 
			int mEndingByte = eachLoad*(i+1)-1;
			// if last thread, take rest of the bytes
			if (i==count-1) {mEndingByte=bytes-1;}
			mByteRanges.add(new ByteRange(mStartingByte, mEndingByte));
		}
		return mByteRanges;
	}
	/**
	 * @return unfinished ByteRanges for resuming
	 */
	public static ArrayList<ByteRange> getUnfinishedByteRanges(int bytes, String mFileName) {
		File dir = new File(getDir());
		// get part-files
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(mFileName);
		    }
		});
		
		// if not all files are created for previous download, this will fuck it up
		ArrayList<ByteRange> mFullByteRanges = getByteRanges(bytes, files.length);
		ArrayList<ByteRange> mUnfinishedByteRanges = new ArrayList<ByteRange>();
		
		int i = 0;
		for (File file: files) {
			ByteRange fbr = mFullByteRanges.get(i);
			mUnfinishedByteRanges.add(new ByteRange(
				fbr.getStartingByte() + file.length(),
				fbr.getEndingByte()));
			i++;
		}
		return mUnfinishedByteRanges;
		
	}
	private static String getDir() {
		return System.getProperty("user.home")+File.separator+"Documents";
	}
}
