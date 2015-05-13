import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import forest.*;
import parsejson.*;
import naivebayes.*;
import Naive.*;

public class CombinedClassifier {
	
	boolean initialised = false;
	RandomForest rf;
	NaiveBayesClassifier nb;
	SimpleClassifier naiveClassifier;
	
	//Keep track of how accurate each individual classifier is
	int naiveCorrect, nbCorrect, forestCorrect, majCorrect = 0;
	int numberClassified = 0;
	
	public void initialiseClassifiers() throws IOException {
		//Set up random forests
		rf = RandomForest.getFromFile("src/forest101_80_nostem.ser");
	
		//Set up naive-bayes
		nb = new NaiveBayesClassifier("tweets");
		
		//Set up naive
		naiveClassifier = new SimpleClassifier();
		
		resetStats();
		
		initialised = true;
		return;
	}
	
	public void resetStats(){
		naiveCorrect = nbCorrect = forestCorrect = majCorrect = 0;
		numberClassified = 0;
	}
	
	public void printAccuracies(){
		System.out.println("Accuracies: ");
		System.out.println("Naive Bayes: " + nbCorrect + "/" + numberClassified +", (" + getPercentage(nbCorrect, numberClassified) +"%)");
		System.out.println("Random Forest: " + forestCorrect + "/" + numberClassified +", (" + getPercentage(forestCorrect, numberClassified) +"%)");
		System.out.println("Naive Method: " + naiveCorrect + "/" + numberClassified +", (" + getPercentage(naiveCorrect, numberClassified) +"%)");
		System.out.println("Majority: " + majCorrect + "/" + numberClassified +", (" + getPercentage(majCorrect, numberClassified) +"%)");
	}
	
	float getPercentage(int top, int bottom) {
		return ((float)top / (float)bottom) * 100.0f;
	}
	
	public int classify(ParsedArticle tweet) throws IOException {
		if (!initialised) initialiseClassifiers();
		
		int sentiment = -1;
		if (tweet.sentiment == "1") sentiment = 1;
		
		int rfResult = rf.predictClass(toRandomForestFormat(tweet)) == 0 ? -1 : 1; 
		int nbResult = nb.classify(toNBFormat(tweet));
		int naiveResult = naiveClassifier.classifyArticle(toNaiveFormat(tweet)).left > 0 ? 1 : -1;
		int majorityResult = (rfResult == 1 && nbResult == 1) || (rfResult == 1 && naiveResult == 1) || (nbResult == 1 && naiveResult == 1) ? 1 : -1;
		
		//Check that we have a prerecorded sentiment to measure against
		if (sentiment == rfResult) { forestCorrect += 1; }
		if (sentiment == nbResult) { nbCorrect += 1; }
		if (sentiment == naiveResult) { naiveCorrect += 1; }
		if (sentiment == majorityResult) { majCorrect += 1; }
		numberClassified++;
		
		return 1;
	}
	
	HashMap<String, Integer> toRandomForestFormat(ParsedArticle tweet) {
		return tweet.words;
	}
	
	String toNBFormat(ParsedArticle tweet) {
		return tweet.originalArticle;
	}
	
	HashMap<String, IntPair> toNaiveFormat(ParsedArticle tweet) {
		return tweet.wordsPolarity;
	}
	
}
