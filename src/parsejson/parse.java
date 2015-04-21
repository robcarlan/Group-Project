package parsejson;

import com.google.gson.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

public class parse {
	
	static String special_characters = "([\\p{Punct}|(0-9)])";
	static String not_find = "not\\s([a-z]+?)\\s";
	
	static boolean printParsed = true;
	static boolean printUnparsed = true;
	static boolean printFreq = true;
	
	public static ArrayList<ParsedArticle> parseAll(String fName) {
		StrippedArticleStream articles = readObjectFromFile(StrippedArticleStream.class, "C:\\test.txt");
		ArrayList<ParsedArticle> parsedArticles = new ArrayList<ParsedArticle>();
		
		for (int articleItr = 0; articleItr < articles.numArticles; articleItr++) {
			ParsedArticle parsed = new ParsedArticle();
			
			StrippedArticle article = articles.articles[articleItr];
			
			String articleText = article.title + " " + article.articleBody;
			
			if (printUnparsed) System.out.print(articleText);
			
			articleText = articleText.replaceAll("'", ""); //Special case to replace apostrophes with no space
			articleText = articleText.replaceAll(special_characters, " "); //Delete any non alphabetical character
			articleText = articleText.replaceAll("( )+", " "); //Deleting a number can lead to multiple spaces, so remove those to ensure one space between words
			//Convert to lower case
			articleText = articleText.toLowerCase();
			
			parsed.originalArticle = articleText;
			
			//Look for nots
			HashMap<String, IntPair> frequencies = new HashMap<String, IntPair>();
			
			Pattern not_finder = Pattern.compile(not_find);
			Matcher match = not_finder.matcher(articleText);
			while (match.find()) {
				String negative = match.group(1);
				onNegativeOccurence(frequencies, negative);
			}
			
			String negativesRemoved = articleText.replaceAll("not [a-z]+ ", "");
			
			//Convert into individual words
			String[] words = negativesRemoved.split(" ");
			
			//Remove common words
			for (String word : words) {
				//System.out.print(word + "\n");
				
				onPositiveOccurence(frequencies, word); 
			}
			
			//Remove common words
			
			if (printParsed) System.out.print(articleText + "\n");
			
			if (printFreq) printFrequencies(frequencies);
			parsed.words = frequencies;
			
			parsedArticles.add(parsed);
		}
		
		return parsedArticles;
	}

	private static void printFrequencies(HashMap<String, IntPair> freq) {
		for (Entry<String, IntPair> t : freq.entrySet()){
			System.out.print(t.getKey() + " Positive: " + t.getValue().left + " Negative: " + t.getValue().right + "\n");
		}
	}
	
	private static void onNegativeOccurence(HashMap<String, IntPair> frequencies, String word) {
		if (word == " ") return; //Sometimes a space gets through uhoh
		if (frequencies.containsKey(word)) {
			IntPair res = frequencies.get(word);
			res.right++;
		} else {
			IntPair temp = new IntPair(0, 1);
			frequencies.put(word, temp);
		}
	}
	
	private static void onPositiveOccurence(HashMap<String, IntPair> frequencies, String word) {
		if (word == " ") return; //Sometimes a space gets through uhoh
		if (frequencies.containsKey(word)) {
			IntPair res = frequencies.get(word);
			res.left++;
		} else {
			IntPair temp = new IntPair(1, 0);
			frequencies.put(word, temp);
		}
	}
	
	/*
	 * 
	 *	gson functions 
	 *
	 */
	
    private static <T> T readObjectFromFile(Class<T> objectClass,
            String fileName) {

    // Check if file exists
    if (!Files.exists(Paths.get(fileName))) {
            System.out.println("Error reading file: " + fileName
                            + " doesn't exist");
            return null;
    }

    Gson gson = new Gson();

    // Read in the file
    try {
            return gson.fromJson(Files.newBufferedReader(Paths.get(fileName),
                            StandardCharsets.ISO_8859_1), objectClass);
    } catch (JsonSyntaxException | JsonIOException | IOException e) {
            System.out.println("Error reading from existing file " + fileName + " \n" + e.getMessage());
            return null;
            // e.printStackTrace();
    }
    }


	/**
	* Used for storing reduced version of articles with most metadata removed
	*
	*/
	private static class StrippedArticleStream {
	
	    final String id; // Feedly stream ID
	    final String title; // Feed title
	    final int numArticles; // Number of articles in this stream
	
	    StrippedArticle[] articles;
	
	    StrippedArticleStream(String id, String title, int numArticles,
	                    StrippedArticle[] articles) {
	            this.id = id;
	            this.title = title;
	            this.numArticles = numArticles;
	            this.articles = articles;
	    }
	}
	
	private static class StrippedArticle {
	
	    StrippedArticle(String title, String articleURL, String articleBody) {
	            this.title = title;
	            this.articleURL = articleURL;
	            this.articleBody = articleBody;
	            sentiment = "";
	    }
	
	    final String title;
	    final String articleURL;
	    final String articleBody;
	    final String sentiment;
	
	}
	
	public static void main(String args[]){
		parseAll("");
	}
}
