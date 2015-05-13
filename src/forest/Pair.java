package forest;

import java.io.Serializable;

public class Pair<U, V> implements Serializable {

	private static final long serialVersionUID = 1L;
	public final U left;
	public final V right;

	public Pair(U left, V right) {
		this.left = left;
		this.right = right;
	}

}
