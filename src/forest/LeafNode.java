package forest;

import java.util.Set;

public class LeafNode extends Node {

	private static final long serialVersionUID = 858876369356245881L;

	static int count = 0;

	final int classification;

	public LeafNode(int classification) {
		count++;

		this.classification = classification;
	}

	public LeafNode(Triple<String, Integer, Integer>[] nodes, int index) {
		classification = nodes[index].right;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public int getClassification() {
		return classification;
	}

	@Override
	public void prettyPrint(int depth) {

		String repeated = new String(new char[depth]).replace("\0", "-");
		repeated += "Leaf : " + classification;
		System.out.println(repeated);
	}

	@Override
	public void getWords(Set<String> words) {
		return;
	}

	@Override
	public void flattenToArray(Triple[] nodes, int index) {
		nodes[index] = new Triple<String, Integer, Integer>("", -1,
				classification);

	}

}
