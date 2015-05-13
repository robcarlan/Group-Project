package naivebayes;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapHelper<K, V> {

	/**
	 * Pre condition: V must be Integer
	 */
	public int totalValueCounts(Map<K,V> map){
		int totalCount = 0;
		Iterator it = map.entrySet().iterator();
		
		while (it.hasNext()) {
		  Map.Entry pair = (Map.Entry)it.next();
		  totalCount += (Integer)pair.getValue();
		}
		
		return totalCount;
	}
	
	public void updateMapCounts(Map<String, Integer> map, List<String> words){
		for(String word : words){
			Integer currentCount = map.get(word);
			if(currentCount == null){
				map.put(word, 1);
			} else {
				map.put(word,  currentCount+1);
			}
		}
	}
	
	public int getCount(String word, Map<String, Integer> map){
		Integer count = map.get(word);
		if(count == null){
			count = 0;
		}
		return count;
	}
}
