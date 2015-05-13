package textpreprocess;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Utils {

	/**
	 * Turns a list of text into hashmaps. May use stemming. If acceptedWords is
	 * passed in, only words in that set will be included. If a text has length
	 * < minLength it will not be added
	 * 
	 */
	public static List<HashMap<String, Integer>> textsToHashMaps(
			List<String> texts, boolean stemming, Set<String> acceptedWords,
			int minLength) {

		List<HashMap<String, Integer>> inputs = new ArrayList<HashMap<String, Integer>>();

		Stemmer stemmer = new Stemmer();

		for (String s : texts) {

			HashMap<String, Integer> input = new HashMap<String, Integer>();

			String[] words = s.split("\\P{L}+");

			for (int i = 0; i < words.length; ++i) {
				String w = words[i];
				w = w.toLowerCase();

				if (stemming) {
					stemmer.add(w.toCharArray(), w.length());
					stemmer.stem();
					w = stemmer.toString();
				}

				// Skip words that aren't in acceptedWords
				if (acceptedWords != null && !acceptedWords.contains(w))
					continue;

				// Add to current text
				input.put(w, input.getOrDefault(w, 0) + 1);
				// input.put(w, 1);

				words[i] = w;
				// // Add in bigrams
				// if (i > 0
				// && ((acceptedWords == null) || (acceptedWords != null &&
				// acceptedWords
				// .contains(words[i - 1])))) {
				// // Add to current text
				// String bigram = words[i - 1] + "-------------------" +
				// words[i];
				// input.put(bigram, input.getOrDefault(bigram, 0) + 1);
				// }

			}

			if (input.size() >= minLength) {
				inputs.add(input);
			}
		}

		return inputs;

	}

	/**
	 * Convenience method to read several files
	 */
	public static List<String> readFiles(boolean stemming, Charset charset,
			String fileName, String... fileNames) {

		List<String> texts = Input.readFile(fileName, charset);
		for (String s : fileNames) {
			texts.addAll(Input.readFile(s, charset));
		}

		return texts;

	}

	/**
	 * Returns a hashmap of all the words in the list, along with how many texts
	 * they appeared in
	 * 
	 * @param fileName
	 * @param stemming
	 * @return
	 */
	public static HashMap<String, Integer> extractWords(List<String> texts,
			boolean stemming) {

		HashMap<String, Integer> allWords = new HashMap<String, Integer>();

		Stemmer stemmer = new Stemmer();

		for (String s : texts) {

			HashSet<String> checked = new HashSet<String>();

			String[] words = s.split("\\P{L}+");

			for (int i = 0; i < words.length; ++i) {
				String w = words[i];
				w = w.toLowerCase();

				if (stemming) {
					stemmer.add(w.toCharArray(), w.length());
					stemmer.stem();
					w = stemmer.toString();
				}

				// Add in word, only add once for each line
				if (!checked.contains(w)) {
					checked.add(w);
					allWords.put(w, allWords.getOrDefault(w, 0) + 1);

				}

				words[i] = w;
				// // Add in bigrams
				// if (i > 0) {
				// // Add to current text
				// String bigram = words[i - 1] + "-------------------" +
				// words[i];
				// allWords.put(bigram, allWords.getOrDefault(bigram, 0) + 1);
				// }
			}

		}

		return allWords;
	}

	/**
	 * Adds an entry to all maps in the list
	 */
	public static <U, V> void addEntry(List<? extends Map<U, V>> inputs, U key,
			V value) {
		for (Map<U, V> i : inputs) {
			i.put(key, value);
		}

	}

	/**
	 * Removes entries that have a value <= low or >= high
	 */
	public static <U> void filterByFrequency(Map<U, Integer> inputs, int low,
			int high) {
		Iterator<Entry<U, Integer>> it = inputs.entrySet().iterator();
		while (it.hasNext()) {
			int value = it.next().getValue();
			if (value <= low || value >= high) {
				it.remove();
			}
		}
	}

}
