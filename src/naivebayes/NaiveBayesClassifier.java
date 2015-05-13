package naivebayes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NaiveBayesClassifier implements Classifier {
	
	Set<String> stopWords = new HashSet<String>();
	MapHelper<String, Integer> mapHelper = new MapHelper<String, Integer>();
	ProbabilityHelper probabilityHelper = new ProbabilityHelper();
	String classifierType; // either tweets or headlines (training sets differ)
	
	Map<String,Integer> posDict, negDict;
	
	public NaiveBayesClassifier(String classifierType) throws IOException{
		this.classifierType = classifierType;
		loadStopWords();
		train();
	}
	
	/**
	 * Returns  1 if the given string is classified as positive.
	 * Returns -1 if the given string is classified as negative. 
	 */
	public int classify(String str){
		double positiveScore = 0;
		double negativeScore = 0;
		
		int numberOfPositiveCounts = mapHelper.totalValueCounts(posDict);
		int numberOfNegativeCounts = mapHelper.totalValueCounts(negDict);
		
		ArrayList<String> parsedString = parseString(str);
		for(String word : parsedString){		
			positiveScore += java.lang.Math.log(probabilityHelper.prOfWordInDictionary(word, posDict, numberOfPositiveCounts));
			negativeScore += java.lang.Math.log(probabilityHelper.prOfWordInDictionary(word, negDict, numberOfNegativeCounts));
		}
		
		if(positiveScore >= negativeScore) return 1;
		else return -1;
	}
			
	private ArrayList<String> read(String fileName) throws IOException{
		ArrayList<String> tweets = new ArrayList<String>();
		
		FileReader in = new FileReader(fileName);
		BufferedReader br = new BufferedReader(in);
		
		String nextLine = "";
		while((nextLine = br.readLine()) != null){
			tweets.add(nextLine);
		}
		
		br.close();
		
		return tweets;
	}
	
	private ArrayList<String> parseString(String str){
		ArrayList<String> parsedString = new ArrayList<String>();
		if(str == null) return parsedString;
		
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == '!') parsedString.add("!");
			if(str.charAt(i) == '?') parsedString.add("?");
			if(str.charAt(i) == '+') parsedString.add("+");
			if(str.charAt(i) == '-') parsedString.add("-");
		}
		
		String[] words = str.split("\\s+");
		for(int i = 0; i < words.length; i++){
			if(words[i].length() == 0) continue;
			if(words[i].charAt(0) == '$' || words[i].charAt(0) == '@'){
				continue;
			}
			
			String cleanedUpString = words[i].replaceAll("[^a-zA-Z]+", "");
			cleanedUpString = cleanedUpString.toLowerCase();
			
			if(cleanedUpString.length() < 3 && !cleanedUpString.equals("up")) continue;
			if(stopWords.contains(cleanedUpString)) continue;
			if(cleanedUpString.startsWith("http")) continue;
			if(cleanedUpString.startsWith("www")) continue;
			
			parsedString.add(cleanedUpString);
		}
			
		return parsedString;
	}
	
	private void train() throws IOException{	
		ArrayList<String> positiveTweets = read("src//" + System.getenv("OPENSHIFT_DATA_DIR")
												+classifierType+"/"+
												"positive.txt");
		ArrayList<String> negativeTweets = read("src//" + System.getenv("OPENSHIFT_DATA_DIR")
												+classifierType+"/"+
												"negative.txt");
		int N = 0; // split data into 5 equal parts
		if(classifierType == "tweets"){	N = 2450; } 
		else { N = 150; }
		
		
		posDict = new HashMap<String, Integer>();
		negDict = new HashMap<String, Integer>();
			
		for(int j = 0; j < N*4; j++){
			mapHelper.updateMapCounts(posDict, parseString(positiveTweets.get(j)));
			mapHelper.updateMapCounts(negDict, parseString(negativeTweets.get(j)));
		}
	}

	
	
	private void loadStopWords() throws IOException{
		FileReader in = new FileReader("src//" + System.getenv("OPENSHIFT_DATA_DIR")
										+classifierType+"/"+					
				                       "stopwords.txt");
		BufferedReader br = new BufferedReader(in);
		
		String nextLine = "";
		while((nextLine = br.readLine()) != null){
			ArrayList<String> words = parseString(nextLine);
			for(String w : words){
				stopWords.add(w);
			}
		}
		
		br.close();
	}
}