package forest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class DecisionTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1123998242297621802L;
	// A random string that hopefully isn't already used
	public static final String CLASSIFICATION_STRING = "SHDJhsfuehfe324jds";
	private static final boolean DEPTH_LIMIT = false;
	private static final int MAX_DEPTH = 200;

	private static final int MIN_NODE_SIZE = 0; // Nodes smaller than this will
												// become leaf nodes

	static int attributeNumber;

	private final Node rootNode;

	int minAttSize = 50000;
	int maxDepth = 0;
	int nodeCount = 0;

	static int flattenIndex = 1;

	public DecisionTree(List<HashMap<String, Integer>> inputs,
			List<String> attributes) {
		assert !inputs.isEmpty();
		attributeNumber = attributes.size();
		rootNode = buildTree(inputs, attributes, null, 0);
	}

	/**
	 * Build tree from an array
	 */
	public DecisionTree(Triple[] nodes) {

		assert nodes.length > 0;

		if (nodes.length > 1) {
			rootNode = new InnerNode(nodes, 0);
		} else {
			rootNode = new LeafNode(nodes, 0);
		}

	}

	/**
	 * Classify the input. Follows the nodes down the tree until we reach a leaf
	 */
	public int classify(Map<String, Integer> input) {

		Node n = rootNode;

		while (!n.isLeaf()) {
			n = ((InnerNode) n).classify(input);
		}

		return ((LeafNode) n).getClassification();

	}

	/**
	 * Flattens to an array to take up less space when storing
	 */
	public Triple[] flattenToArray() {

		System.out.println(nodeCount);
		System.out.println(InnerNode.count);

		System.out.println(LeafNode.count);

		Triple[] nodes = new Triple[nodeCount];
		rootNode.flattenToArray(nodes, 0);
		return nodes;

	}

	//
	// public void buildFromArray(Triple[] nodes) {
	//
	//
	// Stack<Node> nodeStack = new Stack<Node>();
	//
	// for(Triple n : nodes)
	//
	//
	//
	// }
	//
	// public void flattenToArrayStack() {
	//
	// int index = 0;
	// Triple[] nodes = new Triple[nodeCount];
	// Stack<Node> nodeStack = new Stack<Node>();
	// nodeStack.push(rootNode);
	//
	// while (!nodeStack.isEmpty()) {
	// Node n = nodeStack.pop();
	// if (n instanceof InnerNode) {
	// nodes[index] = new Triple(((InnerNode) n).attribute,
	// DecisionTree.flattenIndex, -1);
	// nodeStack.push(((InnerNode) n).left);
	// nodeStack.push(((InnerNode) n).right);
	// DecisionTree.flattenIndex += 2;
	// } else {
	// nodes[index] = new Triple("", -1, ((LeafNode) n).classification);
	// }
	// index++;
	//
	// }
	//
	// attribute = nodes[index].left;
	// int lChildIndex = nodes[index].middle;
	//
	// if (!nodes[lChildIndex].left.isEmpty()) {
	// left = new InnerNode(nodes, lChildIndex);
	// } else {
	// left = new LeafNode(nodes, lChildIndex);
	// }
	//
	// if (!nodes[lChildIndex + 1].left.isEmpty()) {
	// right = new InnerNode(nodes, lChildIndex + 1);
	// } else {
	// right = new LeafNode(nodes, lChildIndex + 1);
	// }
	//
	// }

	/**
	 * Returns the words in the tree. Note they are not ordered by a DFS, not by
	 * any useful measure of importance.
	 */
	public Set<String> getWords() {
		Set<String> words = new HashSet<String>();
		rootNode.getWords(words);
		return words;
	}

	public void prettyPrint() {
		rootNode.prettyPrint(0);
	}

	private Node buildTree(List<HashMap<String, Integer>> inputs,
			List<String> attributes,
			List<HashMap<String, Integer>> parentInputs, int depth) {

		nodeCount++;

		// if (!isBaseCase(inputs, attributes))
		if (depth % 20 == 0) {
			// if (depth == 5 || depth == 50) {
			// if (!isBaseCase(inputs, attributes))
			attributes = filterWords(attributes, inputs);
			// TODO this is removing words for every recursion
		}

		if (isBaseCase(inputs, attributes)
				|| (DEPTH_LIMIT && depth >= MAX_DEPTH)
				|| inputs.size() <= MIN_NODE_SIZE) {

			// Work out how many words used
			if (attributes.size() < minAttSize) {
				minAttSize = attributes.size();
			}

			if (depth > maxDepth) {
				maxDepth = depth;
			}

			// If the input list is empty, use the classification from the
			// parent
			// Otherwise use the most common classification

			if (inputs.isEmpty()) {
				return new LeafNode(getModeClassification(parentInputs).left);
			} else {
				return new LeafNode(getModeClassification(inputs).left);
			}
		} else {

			InnerNode n = new InnerNode(inputs, attributes);
			Pair<List<HashMap<String, Integer>>, List<HashMap<String, Integer>>> split = n
					.split(inputs);

			// System.out.println("Built node on " + n.attribute);
			// System.out.println("Input size: " + inputs.size());
			// System.out.println("Left size: " + split.left.size());
			//
			//
			// if (split.left.size() == 0 || split.right.size() == 0) {
			// // System.out.println("Empty split");
			// }

			// if (getModeClassification(split.left).right == split.left.size())
			// {
			// System.out.println("Left has perfect classification");
			// }
			// System.out.println("Right size: " + split.right.size());
			// if (getModeClassification(split.right).right ==
			// split.right.size()) {
			// System.out.println("Right has perfect classification");
			// }

			// Recurse on the children
			// Remove the attribute so it isn't reused
			// attributes.remove(n.attribute);
			int index = attributes.indexOf(n.attribute);
			if (index != attributes.size() - 1) {
				// If we aren't removing the last element, swap the last element
				// into its place to avoid array copy
				attributes.set(index, attributes.remove(attributes.size() - 1));
			} else {
				// If it was the last element anyway just remove it
				attributes.remove(attributes.size() - 1); // Remove last element
			}
			n.left = buildTree(split.left, new ArrayList<String>(attributes),
					inputs, depth + 1);
			n.right = buildTree(split.right, new ArrayList<String>(attributes),
					inputs, depth + 1);
			attributes.add(n.attribute);

			return n;
		}

	}

	private static boolean isBaseCase(List<HashMap<String, Integer>> inputs,
			List<String> attributes) {

		// Base case if inputs or attributes empty
		// Or if every element has the same classification
		if (inputs.isEmpty() || attributes.isEmpty()) {
			return true;
		} else if (getModeClassification(inputs).right == inputs.size())
			return true;
		else {
			return false;
		}

	}

	/**
	 * Removes words that aren't in any of the inputs, improves speed
	 * 
	 */
	private static List<String> filterWords(List<String> words,
			List<HashMap<String, Integer>> inputs) {

		int wordsRemoved = 0;

		List<String> newWords = words.parallelStream().filter(w -> {
			boolean found = false;

			// Loop through each input, and see if it contains the word
				for (HashMap<String, Integer> input : inputs) {
					if (input.get(w) != null) {
						found = true;
						break;
					}
				}

				// Remove any unfound words
				if (found) {
					return true;
				} else {
					return false;
				}
			}).collect(Collectors.toList());

		return newWords;

		// Iterator<String> it = words.iterator();
		// while (it.hasNext()) {
		// String s = it.next();
		// boolean found = false;
		//
		// // Loop through each input, and see if it contains the word
		// for (HashMap<String, Integer> input : inputs) {
		// if (input.get(s) != null) {
		// found = true;
		// break;
		// }
		// }
		//
		// // Remove any unfound words
		// if (!found) {
		// it.remove();
		// wordsRemoved++;
		// }
		//
		// }
		//
		// return words;

		// System.out.println("Removed " + wordsRemoved + "words");

	}

	/**
	 * Get the most common classification. result.left is the classification and
	 * result.right is the number of times it occurred
	 */
	static Pair<Integer, Integer> getModeClassification(
			List<HashMap<String, Integer>> inputs) {

		// HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		//
		// // Loop through and count up the occurrences of each classification
		// for (Map<String, Integer> input : inputs) {
		// int c = input.get(CLASSIFICATION_STRING);
		// counts.put(c, counts.getOrDefault(c, 0) + 1);
		//
		// }
		//
		// // Work out the max
		// int maxClassification = -1;
		// int occurrences = 0;
		//
		// for (Entry<Integer, Integer> e : counts.entrySet()) {
		// if (e.getValue() > occurrences) {
		// maxClassification = e.getKey();
		// occurrences = e.getValue();
		// }
		// }

		int count0 = 0;
		int count1 = 0;
		int count2 = 0;

		for (Map<String, Integer> input : inputs) {
			int c = input.get(CLASSIFICATION_STRING);
			if (c == 0)
				++count0;
			else if (c == 1)
				++count1;
			else
				++count2;
		}

		if (count0 > count1 && count0 > count2) {
			return new Pair<Integer, Integer>(0, count0);
		} else if (count1 > count2) {
			return new Pair<Integer, Integer>(1, count1);
		} else {
			return new Pair<Integer, Integer>(2, count2);
		}

		// return new Pair<Integer, Integer>(maxClassification, occurrences);

	}
}
