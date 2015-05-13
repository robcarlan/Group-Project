package forest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class RandomForest implements Serializable {

	private static final long serialVersionUID = -8461054545837890579L;

	private DecisionTree[] trees;

	private final int numTrees;
	private int bagSizes = -1;
	private boolean bagWithReplacement = true;

	private boolean parallel = false;
	private int numThreads = 4;

	private boolean printStats = true;

	/**
	 * Deserialise a random forest from a file
	 */
	public static RandomForest getFromFile(String fileName) {

		RandomForest obj = null;

		try {
			// Read from disk using FileInputStream
			FileInputStream f_in = new FileInputStream(fileName);

			// Read object using ObjectInputStream
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			// Read an object
			obj = (RandomForest) obj_in.readObject();

			f_in.close();
			obj_in.close();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error reading object " + fileName);
		}

		return obj;
	}

	public RandomForest(int numTrees) {
		this.numTrees = numTrees;
	}

	public RandomForest(Triple[][] treeNodes) {

		// Build each tree from its nodesS
		this.numTrees = treeNodes.length;
		trees = new DecisionTree[numTrees];
		for (int i = 0; i < numTrees; ++i) {
			trees[i] = new DecisionTree(treeNodes[i]);
		}

	}

	public Triple[][] flattenToArray() {

		Triple[][] treeNodes = new Triple[numTrees][];
		for (int i = 0; i < numTrees; ++i) {
			treeNodes[i] = trees[i].flattenToArray();
		}

		return treeNodes;

	}

	public void build(List<HashMap<String, Integer>> inputs,
			List<String> attributes) {

		if (parallel) {
			parallelBuild(inputs, attributes);
		} else {
			sequentialBuild(inputs, attributes);
		}

	}

	/**
	 * Returns the classification in .left and the proportion of trees that
	 * agreed on that in .right
	 * 
	 */
	public Pair<Integer, Float> predict(HashMap<String, Integer> input) {

		Integer[] scores = new Integer[numTrees];

		for (int i = 0; i < scores.length; ++i) {
			scores[i] = trees[i].classify(input);
		}
		Pair<Integer, Integer> max = Utils.maj(Arrays.asList(scores));

		return new Pair<Integer, Float>(max.left, (float) max.right
				/ scores.length);

	}

	/**
	 * Returns the classification
	 */
	public int predictClass(HashMap<String, Integer> input) {
		return predict(input).left;
	}

	/**
	 * Returns all the words that are in the trees in this forest
	 * 
	 */
	public Set<String> getWords() {
		Set<String> words = new HashSet<String>();
		for (DecisionTree t : trees) {
			words.addAll(t.getWords());
		}
		return words;

	}

	private void sequentialBuild(List<HashMap<String, Integer>> inputs,
			List<String> attributes) {

		trees = new DecisionTree[numTrees];
		long totalTime = 0;

		for (int i = 0; i < numTrees; ++i) {

			long startTime = System.currentTimeMillis();

			// If bagsize not set, use inputs size
			int s = bagSizes == -1 ? inputs.size() : bagSizes;
			trees[i] = new DecisionTree(bag(inputs, s), attributes);

			long duration = System.currentTimeMillis() - startTime;
			totalTime += duration;

			if (printStats) {
				System.out.println("Tree " + (i + 1) + " built");
				System.out.println("Time: " + duration + "ms");
				System.out.println("Running average: " + totalTime / (i + 1));
				System.out.println("Max words used for branch: "
						+ (attributes.size() - trees[i].minAttSize));
				System.out.println("Max depth: " + trees[i].maxDepth);
			}

		}

		// long startTime = System.currentTimeMillis();
		//
		// trees = IntStream.range(0, numTrees).parallel().mapToObj(i -> {
		// return new DecisionTree(bag(inputs, inputs.size()), new
		// ArrayList<String>(attributes));
		// }).toArray(DecisionTree[]::new);
		//
		// long totalTime = System.currentTimeMillis() - startTime;

		System.out.println("Forest built");
		System.out.println("Total time: " + totalTime + "ms");

	}

	private void parallelBuild(final List<HashMap<String, Integer>> inputs,
			final List<String> attributes) {

		int threads = numThreads;// Runtime.getRuntime().availableProcessors();
		System.out.println("Threads: " + threads);

		ExecutorService service = Executors.newFixedThreadPool(threads);

		long startTime = System.currentTimeMillis();

		// Add in all the tasks
		List<Future<DecisionTree>> futures = new ArrayList<Future<DecisionTree>>();
		for (int i = 0; i < numTrees; ++i) {
			Callable<DecisionTree> callable = new Callable<DecisionTree>() {
				public DecisionTree call() throws Exception {
					int s = bagSizes == -1 ? inputs.size() : bagSizes;
					DecisionTree t = new DecisionTree(bag(inputs, s),
							new ArrayList<String>(attributes));
					// process your input here and compute the output
					return t;
				}
			};
			futures.add(service.submit(callable));
		}

		service.shutdown();

		trees = new DecisionTree[numTrees];
		int i = 0;
		for (Future<DecisionTree> future : futures) {
			try {
				trees[i] = future.get();
				i++;
				System.out.println("Tree " + i + " built");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long totalTime = System.currentTimeMillis() - startTime;
		System.out.println("Forest built");
		System.out.println("Total time: " + totalTime + "ms");

	}

	private <T> List<T> bag(List<T> inputs, int n) {
		if (bagWithReplacement) {
			return bagWithReplacement(inputs, n);
		} else {
			return bagWithoutReplacement(inputs, n);
		}
	}

	/**
	 * Randomly picks n elements from inputs with replacement
	 */
	private static <T> List<T> bagWithReplacement(List<T> inputs, int n) {

		List<T> b = new ArrayList<T>(n);
		Random r = new Random();
		int s = inputs.size();

		for (int i = 0; i < n; ++i) {
			b.add(inputs.get(r.nextInt(s)));
		}

		return b;

	}

	/**
	 * Randomly picks n elements without replacement. If n > inputs.size returns
	 * inputs
	 */
	private static <T> List<T> bagWithoutReplacement(List<T> inputs, int n) {

		if (n > inputs.size()) {
			return inputs;
		}

		// Iterate through the list
		// At each element probability of picking it is #lefttopick /
		// #elementsleft

		Random rand = new Random();

		List<T> newAttribs = new ArrayList<T>(n);
		int s = inputs.size();
		int numLeft = n;

		for (int i = 0; i < s; ++i) {
			// Determine if we're picking this element
			if (rand.nextDouble() < ((double) numLeft / (s - i))) {
				newAttribs.add(inputs.get(i));
				numLeft--;

			}

		}

		assert numLeft == 0;

		return newAttribs;

	}

}
