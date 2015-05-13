package naivebayes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class ProbabilityHelper{
	MapHelper<String, Integer> mapHelper = new MapHelper<String, Integer>();
	public static final int additiveSmoothingConst = 5;
	
	public double prOfWordInDictionary(String word, Map<String,Integer> dictionary, int dictionarySize){
		Integer wordCountInDictionary = mapHelper.getCount(word,  dictionary);
		wordCountInDictionary += additiveSmoothingConst; 
		return (double)wordCountInDictionary/(double)dictionarySize;
	}
	
	public double prOfWord(String word, Map<String, Integer> positiveDictionary, 
						   Map<String, Integer> negativeDictionary){
		int totalPositiveCount = mapHelper.totalValueCounts(positiveDictionary);
		int totalNegativeCount = mapHelper.totalValueCounts(negativeDictionary);
		int wordCountInPositiveDictionary = mapHelper.getCount(word, positiveDictionary) + additiveSmoothingConst;
		int wordCountInNegativeDictionary = mapHelper.getCount(word, negativeDictionary) + additiveSmoothingConst;
		return (double)(wordCountInPositiveDictionary + wordCountInNegativeDictionary)/
			   (double)(totalPositiveCount + totalNegativeCount);
	}
	
	public double prPositiveGivenWord(String word, Map<String, Integer> positiveDictionary, 
			   						  Map<String, Integer> negativeDictionary){
		return 0.5 * prOfWordInDictionary(word, positiveDictionary, mapHelper.totalValueCounts(positiveDictionary)) / 
				prOfWord(word, positiveDictionary, negativeDictionary);
	}
	
	public double prNegativeGivenWord(String word, Map<String, Integer> positiveDictionary, 
			   						  Map<String, Integer> negativeDictionary){
		return 0.5 * prOfWordInDictionary(word, negativeDictionary, mapHelper.totalValueCounts(negativeDictionary)) / 
				prOfWord(word, positiveDictionary, negativeDictionary);		
	}
	
	public void findMostIndicativeWords(Map<String, Integer> positiveCounts, Map<String, Integer> negativeCounts) {
		// top 5 for positive
		System.out.println("Top 10 for positive dictionary: ");
		
		Iterator it = positiveCounts.entrySet().iterator();
		ArrayList<Pair>ap = new ArrayList<Pair>();
		
		while (it.hasNext()) {
		  Map.Entry pair = (Map.Entry)it.next();
		  if((Integer)pair.getValue() < 120) continue;
		  double probability = prPositiveGivenWord((String)pair.getKey(), positiveCounts, negativeCounts);
		  ap.add(new Pair((String)pair.getKey(), probability));
		}
		Collections.sort(ap);
		
		for(int i = 0; i < 10; i++){
			if(i == 5) System.out.println();
			System.out.print(ap.get(i).key + " = " + String.format( "%.2f", ap.get(i).value*100) + "%,  ");
		}
		
		// top 5 for negative
		System.out.println(); 
		System.out.println();
		System.out.println("Top 10 for negative dictionary: ");
		
		it = negativeCounts.entrySet().iterator();
		ap = new ArrayList<Pair>();
		while (it.hasNext()) {
			  Map.Entry pair = (Map.Entry)it.next();
			  if((Integer)pair.getValue() < 120) continue;
			  double probability = prNegativeGivenWord((String)pair.getKey(), positiveCounts, negativeCounts);
			  ap.add(new Pair((String)pair.getKey(), probability));
			}
		Collections.sort(ap);
		
		for(int i = 0; i < 10; i++){
			if(i == 5) System.out.println();
			System.out.print(ap.get(i).key + " = " + String.format( "%.2f", ap.get(i).value*100) + "%,  ");
		}
		
	}
	
	private class Pair implements Comparable<Pair>{
		private String key;
		private Double value;
		
		public Pair(String s, Double d){
			key = s;
			value = d;
		}

		public int compareTo(Pair p) {
			if(p.value >= value) return 1;
			else return -1;
		}
	}
}
