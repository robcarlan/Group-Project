package forest;

import java.io.Serializable;

public class Triple<U, V, T> implements Serializable {

	private static final long serialVersionUID = 1L;
	public final U left;
	public final V middle;
	public final T right;

	public Triple(U left, V middle, T right) {
		this.left = left;
		this.middle = middle;
		this.right = right;
	}

}
