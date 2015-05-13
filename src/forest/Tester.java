package forest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import textpreprocess.Input;
import textpreprocess.Utils;

public class Tester {

	static final String testWord = "poso";

	public static void main(String[] args) {

		String posFileName = "positive.txt";
		String negFileName = "negative.txt";
		String neutralFileName = "neutralheadlines.txt";

		// Get a list of the words
		Map<String, Integer> allWords = Utils
				.extractWords(Input.readFiles(StandardCharsets.ISO_8859_1,
						posFileName, negFileName/*
												 * , neutralFileName
												 */), false);

		Set<String> wordSet = new HashSet<String>(allWords.keySet());

		System.out.println("Number of words: " + allWords.keySet().size());

		// Remove common and uncommon words
		Utils.filterByFrequency(allWords, 5, (int) (0.3 * 15000));

		System.out.println("Reduced number of words: "
				+ allWords.keySet().size());

		// Read in the positive and negative inputs, and split into testing and
		// training
		List<HashMap<String, Integer>> posInputs = Utils.textsToHashMaps(
				Input.readFile(posFileName, StandardCharsets.ISO_8859_1), true,
				null, 1);
		Utils.addEntry(posInputs, DecisionTree.CLASSIFICATION_STRING, 1);
		Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>> posTrainTest = split(
				posInputs, 0.9f);

		List<HashMap<String, Integer>> negInputs = Utils.textsToHashMaps(
				Input.readFile(negFileName, StandardCharsets.ISO_8859_1), true,
				null, 1);
		Utils.addEntry(negInputs, DecisionTree.CLASSIFICATION_STRING, 0);
		Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>> negTrainTest = split(
				negInputs, 0.9f);

		// List<HashMap<String, Integer>> neutralInputs = Utils.textsToHashMaps(
		// Input.readFile(neutralFileName, StandardCharsets.ISO_8859_1),
		// true, null, 1);
		// Utils.addEntry(neutralInputs, DecisionTree.CLASSIFICATION_STRING, 2);
		// Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>>
		// neutralTrainTest = split(
		// neutralInputs, 0.3f);

		List<HashMap<String, Integer>> allInputs = new ArrayList<HashMap<String, Integer>>(
				posTrainTest.left);
		allInputs.addAll(negTrainTest.left);
		// allInputs.addAll(neutralTrainTest.left);

		System.out.println("Number of inputs: " + allInputs.size());

		// classifyWords(new ArrayList(allWords.keySet()), allInputs);

		System.out.println("Tree building started");

		// RandomForest rf = new RandomForest(5);
		// rf.build(allInputs, new ArrayList<String>(allWords.keySet()));

		// serialiseObject(rf, "forest101.ser");
		// serialiseObject(rf.flattenToArray(), "forestflat.ser");

		// RandomForest rf = deserialiseObject("forest101.ser");
		RandomForest rf = RandomForest.getFromFile("forest101.ser");
		// RandomForest rf = new RandomForest(
		// (Triple[][]) deserialiseObject("forestflat.ser"));

		// // Read in the test files
		// List<String> testPosFile = Input.readFile("testpos3250.txt",
		// StandardCharsets.ISO_8859_1);
		// int pSize = testPosFile.size();
		// List<HashMap<String, Integer>> testInputPos = Utils.textsToHashMaps(
		// testPosFile, true, allWords.keySet(), 0);
		// Utils.addEntry(testInputPos, DecisionTree.CLASSIFICATION_STRING, 1);
		// System.out.println("Reduced positive to " + (float)
		// testInputPos.size()
		// / pSize);
		//
		// List<HashMap<String, Integer>> testInputNeg = Utils.textsToHashMaps(
		// Input.readFile("testneg3250.txt", StandardCharsets.ISO_8859_1),
		// true, allWords.keySet(), 5);
		// Utils.addEntry(testInputNeg, DecisionTree.CLASSIFICATION_STRING, 0);

		Pair<Float, Integer> correctPos = test(posTrainTest.right, rf, 0.2f);
		Pair<Float, Integer> correctNeg = test(negTrainTest.right, rf, 0.2f);
		// Pair<Float, Integer> correctNeutral = test(neutralTrainTest.right,
		// rf,
		// 0.2f);

		System.out.println("Got " + (correctPos.left * 100)
				+ "% correct for positive");
		System.out.println("Attempted "
				+ ((float) correctPos.right / posTrainTest.right.size() * 100)
				+ "% for positive");

		System.out.println("Got " + (correctNeg.left * 100)
				+ "% correct for negative");
		System.out.println("Attempted "
				+ ((float) correctNeg.right / negTrainTest.right.size() * 100)
				+ "% for negative");

		// System.out.println("Got " + (correctNeutral.left * 100)
		// + "% correct for neutral");
		// System.out
		// .println("Attempted "
		// + ((float) correctNeutral.right
		// / neutralTrainTest.right.size() * 100)
		// + "% for neutral");

		// for (String s : rf.getWords()) {
		// System.out.println(s);
		// }

		// testFile(rf, "testpos3250.txt", 0.6f, 1);
		// testFile(rf, "testneg3250.txt", 0.6f, 0);
		//
		// testFile(rf, "testpos3250.txt", 0.7f, 1);
		// testFile(rf, "testneg3250.txt", 0.7f, 0);
		//
		// testFile(rf, "testpos3250.txt", 0.8f, 1);
		// testFile(rf, "testneg3250.txt", 0.8f, 0);
		//
		// testFile(rf, "testpos3250.txt", 0.9f, 1);
		// testFile(rf, "testneg3250.txt", 0.9f, 0);
		//
		// testFile(rf, "testpos3250.txt", 1.0f, 1);
		// testFile(rf, "testneg3250.txt", 1.0f, 0);

	}

	private static void testFile(RandomForest rf, String fileName, float prop,
			int classification) {

		// Read in the test file
		List<String> testFile = Input.readFile(fileName,
				StandardCharsets.ISO_8859_1);
		List<HashMap<String, Integer>> testInput = Utils.textsToHashMaps(
				testFile, true, null, 0);
		Utils.addEntry(testInput, DecisionTree.CLASSIFICATION_STRING,
				classification);

		System.out.println("Testing " + testInput.size() + " cases");

		Pair<Float, Integer> correct = test(testInput, rf, prop);

		System.out.println("Got " + (correct.left * 100) + "% correct for "
				+ fileName);
		System.out.println("Attempted "
				+ ((float) correct.right / testInput.size() * 100) + " with "
				+ prop + " confidence");

	}

	/**
	 * Splits into two parts
	 */
	private static <U> Pair<List<U>, List<U>> split(List<U> inputs, float frac) {

		List<U> left = new ArrayList<U>();
		List<U> right = new ArrayList<U>();

		int num = (int) (inputs.size() * frac);
		int i = 0;

		for (U e : inputs) {
			if (i < num)
				left.add(e);
			else
				right.add(e);

			i++;

		}

		return new Pair<List<U>, List<U>>(left, right);

	}

	private static Pair<Float, Integer> test(
			List<HashMap<String, Integer>> inputs, RandomForest rf,
			float confidence) {

		int correct = 0;
		int attempted = 0;
		for (HashMap<String, Integer> in : inputs) {

			Pair<Integer, Float> c = rf.predict(in);

			if (c.right >= confidence) {
				attempted++;
				if (c.left == in.get(DecisionTree.CLASSIFICATION_STRING)) {
					// if(rf.predictClass(in) ==
					// in.get(DecisionTree.CLASSIFICATION_STRING)) {
					correct++;
				}
			}
		}

		float propCorrect = (float) correct / attempted;
		return new Pair<Float, Integer>(propCorrect, attempted);
	}

	// Classified words into positive or negative, based on whether the majority
	// of texts containing that word or positive or negative. The words are
	// ranked by entropy
	private static void classifyWords(List<String> words,
			List<HashMap<String, Integer>> inputs) {

		List<Pair<String, Double>> scoredWords = new ArrayList<Pair<String, Double>>();

		for (String word : words) {

			double splitScore = InnerNode.calculateSplitScore(inputs, word, 0);

			// split.right now contains all the texts that have at least one
			// occurrence

			scoredWords.add(new Pair<String, Double>(word, splitScore));

		}

		Collections.sort(scoredWords, new Comparator<Pair<String, Double>>() {

			@Override
			public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
				return o1.right.compareTo(o2.right);
			}

		});

		PrintWriter writer = null;
		PrintWriter writerPos = null;
		PrintWriter writerNeg = null;

		try {
			writer = new PrintWriter("wordlistsentimentheadlines.txt", "UTF-8");
			writerPos = new PrintWriter("wordlistpositiveheadlines.txt",
					"UTF-8");
			writerNeg = new PrintWriter("wordlistnegativeheadlines.txt",
					"UTF-8");

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < 500; ++i) {

			String word = scoredWords.get(i).left;

			Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>> split = InnerNode
					.split(inputs, word, 0);

			Pair<Integer, Integer> classification = DecisionTree
					.getModeClassification(split.right);
			int c = classification.left;
			String s;
			if (c == 0) {
				s = "negative";
				writerNeg.println(scoredWords.get(i).left);

			} else {
				s = "positive";
				writerPos.println(scoredWords.get(i).left);

			}

			// System.out.println(scoredWords.get(i).left + ", " + s);
			writer.println(scoredWords.get(i).left + "," + s);
		}

		writer.close();
		writerPos.close();
		writerNeg.close();

	}

	private static <E extends Serializable> void serialiseObject(E obj,
			String fileName) {

		FileOutputStream f_out;
		try {
			// Write to disk with FileOutputStream
			f_out = new FileOutputStream(fileName);

			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

			// Write object out to disk
			obj_out.writeObject(obj);

			obj_out.close();
			f_out.close();

			System.out.println("Object serialised to " + fileName);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error writing object to " + fileName);
		}

	}

	private static <E extends Serializable> E deserialiseObject(String fileName) {

		E obj = null;

		try {
			// Read from disk using FileInputStream
			FileInputStream f_in = new FileInputStream(fileName);

			// Read object using ObjectInputStream
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			// Read an object
			obj = (E) obj_in.readObject();

			f_in.close();
			obj_in.close();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error reading object " + fileName);
		}

		return obj;
	}
}
