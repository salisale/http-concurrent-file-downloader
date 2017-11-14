import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Hidden file records Last Modified Date
 */
public class HeaderFileHandler {
	public static void writeToFile(File file, String dateModified) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter
							(file.getAbsoluteFile(), false)); // false=replace content
			bw.write(dateModified);
			bw.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String readFromFile(File file) {
		BufferedReader br = null;
		String out = null;
		try {
			br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
			out = br.readLine();
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 
		return out;
	}

}