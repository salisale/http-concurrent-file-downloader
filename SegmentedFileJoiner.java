import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SegmentedFileJoiner {
	public static void join(String mFilePath, int mCount) {
		
		File mFile = new File(mFilePath);
		
		// resume, or interrupted? how to deal with this.
		try {
			mFile.createNewFile();
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
		
	    Path mPath = Paths.get(mFilePath);
	    SeekableByteChannel sbc;
		try {
			sbc = Files.newByteChannel(mPath, StandardOpenOption.APPEND);
		    for (int i=1; i<=mCount; i++) {
		    	// get byte array from each part-file
		    	Path thisPath = Paths.get(mFilePath+i);
		    	byte[] bytes = Files.readAllBytes(thisPath);
		    	ByteBuffer bb = ByteBuffer.wrap(bytes);
		    	sbc.write(bb);
		    	// delete after merging
		    	Files.delete(thisPath); 
		    }
			sbc.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
	
}
