package forest;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public abstract class Node implements Serializable {

	private static final long serialVersionUID = -3053777508556076476L;

	public abstract boolean isLeaf();

	public abstract void prettyPrint(int depth);

	/**
	 * Adds the words in the subtree rooted at this node to words
	 */
	public abstract void getWords(Set<String> words);

	public abstract void flattenToArray(Triple[] nodes, int index);

}
