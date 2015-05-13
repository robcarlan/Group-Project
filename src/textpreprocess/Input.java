package textpreprocess;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Input {

	/**
	 * Reads a text file into its lines
	 * 
	 * @param fileName
	 *            The file to read
	 * @return
	 */
	public static List<String> readFile(String fileName, Charset charset) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fileName), charset);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading: " + fileName);
		}

		return lines;
	}

	/**
	 * Convenience method to read several files into lines
	 * 
	 * @param charset
	 * @param fileName
	 * @param fileNames
	 * @return
	 */
	public static List<String> readFiles(Charset charset, String fileName, String... fileNames) {

		List<String> texts = Input.readFile(fileName, charset);
		for (String s : fileNames) {
			texts.addAll(Input.readFile(s, charset));
		}

		return texts;

	}

}
