package textpreprocess;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

public class Output {

	/**
	 * Write a list of strings to a file, one string on each line
	 */
	public static void writeListToFile(String fileName, List<String> texts) {

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Error writing to: " + fileName);
			return;
		}

		for (String s : texts) {
			writer.println(s);
		}

		writer.close();
	}

	/**
	 * Writes a text matrix to a file. Each column contains one of the words in words, and each row contains
	 * an input. If the inputs contains words that are in the words array those words will not appear in the
	 * output.
	 * 
	 * @param fileName
	 * @param inputs
	 * @param words
	 */
	public static void writeTextMatrix(String fileName, List<HashMap<String, Integer>> inputs,
			String[] words, boolean columnHeaders) {

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error writing to: " + fileName);
		}

		boolean first = true;

		if (columnHeaders) {
			// Column titles
			for (String word : words) {
				if (first) {
					first = false;
				} else {
					writer.print(',');
				}
				writer.print(word);

			}
			writer.println();
		}

		for (HashMap<String, Integer> input : inputs) {

			first = true;

			for (String word : words) {
				if (first) {
					first = false;
				} else {
					writer.print(',');
				}
				writer.print(input.getOrDefault(word, 0));

			}

			writer.println();

		}

		writer.close();

	}

}
