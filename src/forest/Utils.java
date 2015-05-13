package forest;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Utils {

	/**
	 * Returns the majority in a list of elements in .left and the number of times it occurred in .right. If
	 * there is a tie, the element that appeared first in the list will be returned.
	 */
	public static <T> Pair<T, Integer> maj(List<T> elems) {

		HashMap<T, Integer> counts = new HashMap<T, Integer>();

		// Count up the occurrences
		for (T elem : elems) {
			counts.put(elem, counts.getOrDefault(elem, 0) + 1);
		}

		T max = null;
		int maxCount = 0;

		for (Entry<T, Integer> e : counts.entrySet()) {
			if (max == null) {
				max = e.getKey();
				maxCount = e.getValue();
			} else {
				if (e.getValue() > maxCount) {
					max = e.getKey();
					maxCount = e.getValue();
				}
			}

		}

		return new Pair<T, Integer>(max, maxCount);

	}

}
