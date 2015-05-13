package forest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InnerNode extends Node {

	private static final long serialVersionUID = -3337738599003307918L;

	static int count = 0;

	public boolean isLeaf = false;
	public Node left = null;
	public Node right = null;

	String attribute;
	private int threshold;

	// TODO optimise this, set based on actual max word frequency?
	private static final int thresholds = 1;

	private static final Random rand = new Random();

	public InnerNode(List<HashMap<String, Integer>> inputs,
			List<String> attributes) {

		count++;

		// findBestSplit(inputs, attributes);
		findBestSplit(
				inputs,
				getRandomAttributes(attributes,
						(int) (Math.sqrt(attributes.size()))));
		// findBestSplit(inputs,
		// getRandomAttributes(attributes, (int)
		// (Math.sqrt(DecisionTree.attributeNumber))));

	}

	/**
	 * Build from an array
	 */
	public InnerNode(Triple<String, Integer, Integer>[] nodes, int index) {

		attribute = nodes[index].left;
		int lChildIndex = nodes[index].middle;

		if (!nodes[lChildIndex].left.isEmpty()) {
			left = new InnerNode(nodes, lChildIndex);
		} else {
			left = new LeafNode(nodes, lChildIndex);
		}

		if (!nodes[lChildIndex + 1].left.isEmpty()) {
			right = new InnerNode(nodes, lChildIndex + 1);
		} else {
			right = new LeafNode(nodes, lChildIndex + 1);
		}

	}

	public InnerNode() {

	}

	/**
	 * Splits the inputs into <= and > based on this nodes attribute and
	 * threshold. If an input doensn't have a value for the attribute, 0 is
	 * used.
	 * 
	 * @param inputs
	 *            The inputs to split up
	 * @return A pair with left the <= set and right the > set
	 */
	public Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>> split(
			List<HashMap<String, Integer>> inputs) {

		return split(inputs, this.attribute, this.threshold);
	}

	static Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>> split(
			List<HashMap<String, Integer>> inputs, String attribute,
			int threshold) {

		List<HashMap<String, Integer>> left = new ArrayList<HashMap<String, Integer>>();
		List<HashMap<String, Integer>> right = new ArrayList<HashMap<String, Integer>>();

		for (HashMap<String, Integer> input : inputs) {
			// int value = input.getOrDefault(attribute, 0);
			if (input.getOrDefault(attribute, 0) <= threshold) {
				left.add(input);
			} else {
				right.add(input);
			}
		}

		// inputs.parallelStream().forEach(input -> {
		// int value = input.getOrDefault(attribute, 0);
		// if (value <= threshold) {
		// left.add(input);
		// } else {
		// right.add(input);
		// }
		// });

		return new Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>>(
				left, right);
	}

	/**
	 * Returns two pairs. The left pair is the left split, and the right pair
	 * the right split. Each pair then contains the number of elements in each
	 * class.
	 * 
	 * @param inputs
	 * @param attribute
	 * @param threshold
	 * @return
	 */
	private static int[] splitCount(List<HashMap<String, Integer>> inputs,
			String attribute, int threshold) {

		// int left0 = 0;
		// int left1 = 0;
		// int left2 = 0;
		// int right0 = 0;
		// int right1 = 0;
		// int right2 = 0;
		//

		// int[] left = new int[3];
		// int[] right = new int[3];
		// First 3 is left, second 3 is right split
		int[] leftRight = new int[6];

		for (HashMap<String, Integer> input : inputs) {
			int classification = input.get(DecisionTree.CLASSIFICATION_STRING);
			if (input.getOrDefault(attribute, 0) <= threshold) {
				++leftRight[classification];
			} else {
				++leftRight[classification + 3];
			}
			// This only works if val can only be 0 or 1
			// ++leftRight[classification + 3 * input.getOrDefault(attribute,
			// 0)];
		}

		// int[] left = new int[3];
		// int[] right = new int[3];
		//
		// List<Pair<Integer, Boolean>> l = inputs
		// .stream()
		// .map(m -> new Pair<Integer, Boolean>(m
		// .get(DecisionTree.CLASSIFICATION_STRING), m
		// .getOrDefault(attribute, 0) <= threshold))
		// .collect(Collectors.toList());
		//
		// for (Pair<Integer, Boolean> p : l) {
		// if (p.right) {
		// left[p.left] += 1;
		// } else {
		// right[p.left] += 1;
		// }
		// }

		// return new Pair<Triple<Integer, Integer, Integer>, Triple<Integer,
		// Integer, Integer>>(
		// new Triple<Integer, Integer, Integer>(left[0], left[1], left[2]),
		// new Triple<Integer, Integer, Integer>(right[0], right[1],
		// right[2]));

		return leftRight;
	}

	/**
	 * Determine whether we should follow the left or right branch
	 */
	public Node classify(Map<String, Integer> input) {
		if (input.getOrDefault(attribute, 0) <= this.threshold) {
			return left;
		} else {
			return right;
		}
	}

	void findBestSplit(List<HashMap<String, Integer>> inputs,
			String[] attributes) {

		// The best score and threshold for each attribute
		// Pair<Float, Integer>[] attributeScores = new Pair[attributes.size()];
		// Double[] attributeScores = new Double[attributes.length];
		// int[] attributeThresholds = new int[attributes.length];

		// double[] thresholdScores = new double[thresholds]; // Reuse array
		// each
		// loop

		// int j = 0;
		//
		// for (String a : attributes) {
		// // for (int i = 0; i < thresholds; ++i) {
		// // thresholdScores[i] = calculateSplitScore(inputs, a, i);
		// // }
		// // // Calculate the best score for each attribute
		// // // Note the index will be the threshold
		// // Pair<Double, Integer> m = min(thresholdScores);
		// // attributeScores[j] = m.left;
		// // attributeThresholds[j] = m.right;
		// //
		//
		// attributeScores[j] = calculateSplitScore(inputs, a, 0);
		// // attributeThresholds[j] = 0;
		// ++j;
		// }

		// Double[] attributeScores = Arrays.stream(attributes).parallel()
		// .map(a -> calculateSplitScore(inputs, a, 0)).toArray(Double[]::new);

		List<Double> attributeScores = Arrays.stream(attributes).parallel()
				.map(a -> calculateSplitScore(inputs, a, 0))
				.collect(Collectors.toList());

		// Calculate the best attribute score
		Pair<Double, Integer> m = min(attributeScores);

		// String bestAttribute = attributes.get(m.right);
		// int threshold = attributeThresholds[m.right];

		this.attribute = attributes[m.right];
		this.threshold = 0;// attributeThresholds[m.right];

		// System.out.println("Picked " + attributes[(m.right)] + " with score "
		// + m.left);

	}

	static double calculateSplitScore(List<HashMap<String, Integer>> inputs,
			String attrib, int threshold) {

		// Calculate the information gain

		// double entropy = calculateEntropy(inputs);

		// Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>>
		// split = split(inputs, attrib,
		// threshold);

		// Pair<Triple<Integer, Integer, Integer>, Triple<Integer, Integer,
		// Integer>> split = splitCount(
		// inputs, attrib, threshold);

		// First 3 left, second 3 right
		int[] split = splitCount(inputs, attrib, threshold);

		// Proportions of subsets

		// double pLeft = (double) (split.left.left + split.left.right +
		// split.left.middle)
		// / inputs.size();
		// double pRight = (double) (split.right.left + split.right.right +
		// split.right.middle)
		// / inputs.size();

		int totalLeft = split[0] + split[1] + split[2];
		double pLeft = (double) totalLeft / inputs.size();

		int totalRight = split[3] + split[4] + split[5];
		double pRight = (double) totalRight / inputs.size();

		// double leftScore = calculateEntropy2(split.left);
		// double rightScore = calculateEntropy2(split.right);

		double leftScore = totalLeft == 0 ? 0 : calculateEntropy2(split, 0,
				1d / totalLeft);
		double rightScore = totalRight == 0 ? 0 : calculateEntropy2(split, 3,
				1d / totalRight);

		// double leftScore = calculateGini(split.left);
		// double rightScore = calculateGini(split.right);

		// double ig = entropy - (pLeft * entropyLeft + pRight * entropyRight);

		double score = pLeft * leftScore + pRight * rightScore;

		return score;
	}

	static double calculateEntropy(List<HashMap<String, Integer>> set) {

		// Entropy 0 when set is empty
		if (set.isEmpty()) {
			return 0;
		}

		double sum = 0;
		int class0 = 0; // Num in 0 class
		int class1 = 0; // Num in 1 class
		for (HashMap<String, Integer> s : set) {
			if (s.get(DecisionTree.CLASSIFICATION_STRING) == 0) {
				class0++;
			} else {
				class1++;
			}
		}

		// Proportion of class 0 and 1
		double p0 = (double) class0 / set.size();
		double p1 = (double) class1 / set.size();

		// Prevent log of 0 error
		if (p0 == 0 || p1 == 0) {
			return 0;
		}

		double cf = Math.log(2); // For converting base 10 to 2

		sum = p0 * (Math.log10(p0) / cf) + p1 * (Math.log10(p1) / cf);

		return -1 * sum;

	}

	static double calculateEntropy2(int[] amounts, int offset,
			double totalSizeRec) {

		// int totalSize = amounts.left + amounts.right + amounts.middle;
		// int totalSize = amounts[0 + offset] + amounts[1 + offset]
		// + amounts[2 + offset];

		// Entropy 0 when set is empty
		// if (totalSize == 0) {
		// return 0;
		// }

		double sum = 0;
		// int class0 = amounts.left; // Num in 0 class
		// int class1 = amounts.right; // Num in 1 class
		// int class2 = amounts.middle;

		// int class0 = amounts[0 + offset]; // Num in 0 class
		// int class1 = amounts[1 + offset]; // Num in 1 class
		// int class2 = amounts[2 + offset];

		// Proportion of class 0 and 1
		double p0 = (double) amounts[0 + offset] * totalSizeRec;
		double p1 = (double) amounts[1 + offset] * totalSizeRec;
		double p2 = (double) amounts[2 + offset] * totalSizeRec;

		// Prevent log of 0 error
		// if (p0 == 0 || p1 == 0) {
		// return 0;
		// }

		// double cf = Math.log(2); // For converting base 10 to 2

		// sum = p0 * (Math.log10(p0) / cf) + p1 * (Math.log10(p1) / cf);
		sum = p0 == 0 ? 0 : p0 * (Math.log10(p0)) + p1 == 0 ? 0 : p1
				* (Math.log10(p1)) + p2 == 0 ? 0 : p2 * (Math.log10(p2));

		return -1 * sum;

	}

	static double calculateGini(Pair<Integer, Integer> amounts) {

		int totalSize = amounts.left + amounts.right;

		// 0 when set is empty ?
		// Will bet set to 0 later anyway as weighting proportion will be 0
		if (totalSize == 0) {
			return 0;
		}

		double sum = 0;
		int class0 = amounts.left; // Num in 0 class
		int class1 = amounts.right; // Num in 1 class

		// Proportion of class 0 and 1
		double p0 = (double) class0 / totalSize;
		double p1 = (double) class1 / totalSize;

		sum = 1 - p0 * p0 - p1 * p1;

		return sum;

	}

	/**
	 * Returns n randomly picked attributes from the list without replacement.
	 * If n > number of elements in the list, returns the original list.
	 */
	private static String[] getRandomAttributes(List<String> attributes, int n) {

		if (n > attributes.size()) {
			return attributes.toArray(new String[attributes.size()]);
		}

		// Iterate through the list
		// At each element probability of picking it is #lefttopick /
		// #elementsleft

		String[] newAttribs = new String[n];
		int s = attributes.size();
		int numLeft = n;

		for (int i = 0; i < s; ++i) {
			// Determine if we're picking this element
			if (rand.nextDouble() < ((double) numLeft / (s - i))) {
				newAttribs[--numLeft] = attributes.get(i);

			}

		}

		assert numLeft == 0;

		return newAttribs;

	}

	/**
	 * Returns the max of an array of doubles. result.left is the max and
	 * result.right is the index in the array
	 */
	private static Pair<Double, Integer> max(double[] a) {

		assert a.length > 0;

		double curMax = a[0];
		int index = 0;

		for (int i = 1; i < a.length; ++i) {
			if (a[i] > curMax) {
				curMax = a[i];
				index = i;
			}
		}

		return new Pair<Double, Integer>(curMax, index);

	}

	/**
	 * Returns the min of an array of doubles. result.left is the min and
	 * result.right is the index in the array
	 */
	private static Pair<Double, Integer> min(Double[] a) {

		assert a.length > 0;

		double curMin = a[0];
		int index = 0;

		for (int i = 1; i < a.length; ++i) {
			if (a[i] < curMin) {
				curMin = a[i];
				index = i;
			}
		}

		return new Pair<Double, Integer>(curMin, index);

	}

	private static Pair<Double, Integer> min(List<Double> a) {

		assert a.size() > 0;

		double curMin = a.get(0);
		int index = 0;

		for (int i = 1; i < a.size(); ++i) {
			if (a.get(i) < curMin) {
				curMin = a.get(i);
				index = i;
			}
		}

		return new Pair<Double, Integer>(curMin, index);

	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public void prettyPrint(int depth) {
		String repeated = new String(new char[depth]).replace("\0", "-");
		repeated += " " + attribute;
		System.out.println(repeated);
		left.prettyPrint(depth + 1);
		right.prettyPrint(depth + 1);
	}

	@Override
	public void getWords(Set<String> words) {
		words.add(attribute);
		left.getWords(words);
		right.getWords(words);
	}

	@Override
	public void flattenToArray(Triple[] nodes, int index) {
		// Store the location of left child as well, right child will be one
		// after
		// flatten index should contain next free slot
		nodes[index] = new Triple<String, Integer, Integer>(attribute,
				DecisionTree.flattenIndex, -1);
		DecisionTree.flattenIndex += 2;
		// left.flattenToArray(nodes, 2 * index + 1);
		// right.flattenToArray(nodes, 2 * index + 2);
		left.flattenToArray(nodes, DecisionTree.flattenIndex - 2);
		right.flattenToArray(nodes, DecisionTree.flattenIndex + 1 - 2);

	}
}
