import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import parsejson.ParseTweets;
import parsejson.ParsedArticle;


public class ClassifyTest {
	
	public static void main(String[] args) throws IOException {
		//ArrayList<ParsedArticle> parsed = ParseTweets.parseAll("headlines");
		//First 80% have been used for training - use the rest for testing
		
		//Create parsedArticles of positive / negative / neutral
		
		CombinedClassifier classifier = new CombinedClassifier();
		classifier.initialiseClassifiers();
		
		ArrayList<ParsedArticle> tweets = new ArrayList<ParsedArticle>();
			
		//Read Positive tweets
		FileReader fread = new FileReader("src/nulltweets/positive.txt");
		BufferedReader read = new BufferedReader(fread);
		String nextTweet = "";
		
		while((nextTweet = read.readLine()) != null) {
			ParsedArticle temp = ParseTweets.parseArticle(nextTweet);
			temp.sentiment = "1";
			tweets.add(temp);
		}
		
		read.close();
		
		//Read negative tweets
		fread = new FileReader("src/nulltweets/negative.txt");
		read = new BufferedReader(fread);
		
		while((nextTweet = read.readLine()) != null) {
			ParsedArticle temp = ParseTweets.parseArticle(nextTweet);
			temp.sentiment = "-1";
			tweets.add(temp);
		}
		
		read.close();
		
		int tweetIndex = (int)(12250 * 0.8f);
		int end = (int)(12250);
		
		while (tweetIndex < end) {
			classifier.classify(tweets.get(tweetIndex));
			classifier.classify(tweets.get(tweetIndex + 12250));
			if (tweetIndex % 50 == 0 ) {
				classifier.printAccuracies();
			}
			
			tweetIndex++;			
		}
		
		classifier.printAccuracies();
	}
	
}	

