package Naive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import parsejson.*;

public class SimpleClassifier {
	private Dic dic = new Dic();

	
	// return (sentimentment score, number of words in the dictionary)
	public IntPair classify(HashMap<String, IntPair> parsed){
		Iterator<Entry<String, IntPair>> it = parsed.entrySet().iterator();
		int score = 0;
		int words = 0;
		while(it.hasNext()){
			Entry<String, IntPair> pair = it.next();
			int polarity = dic.compare(pair.getKey());
			if(polarity!=0) words++;  // This is to test if there are words in the dictionary or not
			score += polarity*(pair.getValue().left);
			score -= polarity*(pair.getValue().right);

		}
		return new IntPair(score, words);
	}
	
	/**public int[] classifyJSON(String fname){
		ArrayList<ParsedArticle> parsed = parse.parseAll(fname);
		int size = parsed.size();
		int[] results = new int[size];
		String[] sentiments = new String[size];
		int correct = 0;
		for(int i = 0; i<size; i++){
		//	System.out.println(parsed.get(i));
			
			results[i] = classify(parsed.get(i).words);
			sentiments[i] = parsed.get(i).sentiment;
			if ((results[i]<0 && (int)Double.parseDouble(sentiments[i])<0)||(results[i]>=0&&(int)Double.parseDouble(sentiments[i])>=0)){
				correct++;
			}
		}
		System.out.println(correct);
		System.out.println(size);
		return results;
		
	}
	*/
	

	public IntPair classifyArticle(HashMap<String, IntPair> t){
		//HashMap<String, IntPair> parsed = ParseTweets.parseArticle(t).wordsPolarity;
		return classify(t);
	}	
	
	public IntPair classifyString(String t){
		HashMap<String, IntPair> parsed = ParseTweets.parseArticle(t).wordsPolarity;
		return classify(parsed);
	}
	
}
