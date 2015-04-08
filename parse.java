import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class parse {
	//takes the whole collection of articles, (needs to get the inside of articles {... }
	
	//Regex patterns
	static String remove_top = "\\{(.|\n)*\\[";
	static String get_article = "\\{(.)+?\\},";
	
	static String get_body = "\\\"articleBody\\\": \\\"(.+?)\\\"";
	static String get_title_body = "\\\"title\\\": \\\"(.+?)\\\"," + "(.+?)" + get_body;
	
	static String special_characters = "([\\p{Punct}|(0-9)])";
	static String not_find = "not\\s([a-z]+?)\\s";
	
	static boolean printParsed = false;
	static boolean printUnparsed = false;
	static boolean printFreq = true;

	
	//Returns bare text of a list of articles
	public static ArrayList<String> getAllArticles(String input) {
		//Get rid of the enclosing JSON, retrieving the list of articles
		int startTrim = input.indexOf("[");
		int endTrim = input.lastIndexOf("]");
		input = input.substring(startTrim + 1, endTrim);
		
		//Now split into each article by separating based on ','
		//DOTALL allows us to use . to capture newlines too yay
		Pattern article_splitter = Pattern.compile(get_article, Pattern.DOTALL );
		Matcher match = article_splitter.matcher(input);
		
		ArrayList<String> formattedArticles = new ArrayList<String>(); 
		//Iterate through all the matches
		while (match.find()) {
			String strmatch = match.group();
			//Remove the last ',', and pass to parseArticle
			String articleParsed = getArticleText(strmatch.substring(0, strmatch.length() - 1));
			formattedArticles.add(articleParsed);
		} 
		

		return formattedArticles;
		//return null;
	}
	
	//input of the format { title , url , body }, returns title concatenated with body
	public static String getArticleText(String input) {
		//Take "articleBody" and format it
		Matcher matcher;
		Pattern title = Pattern.compile(get_title_body, Pattern.DOTALL);
		matcher = title.matcher(input);
		
		if (!matcher.find()) {
			//Uhoh we couldn't find the title and or body
			throw new InvalidParameterException();
		}
		//Title = group 1
		String article_title = matcher.group(1);
		//Body = group 3
		String article_body = matcher.group(3);
		
		return article_title + " " + article_body;
	}
	
	//Takes body and title, returns parsed version
	public static HashMap<String, IntPair> parseArticle(String article) {
		//We need to : remove punctuation, digits etc.#
		
		if (printUnparsed) System.out.print(article + "\n");
		article = article.replaceAll("'", ""); //Special case to replace apostrophes with no space
		article = article.replaceAll(special_characters, " "); //Delete any non alphabetical character
		article = article.replaceAll("( )+", " "); //Deleting a number cna lead to multiple spaces, so remove those to ensure one space between words
		//Convert to lower case
		article = article.toLowerCase();
		
		//Look for nots
		HashMap<String, IntPair> frequencies = new HashMap<String, IntPair>();
		
		Pattern not_finder = Pattern.compile(not_find);
		Matcher match = not_finder.matcher(article);
		while (match.find()) {
			String negative = match.group(1);
			onNegativeOccurence(frequencies, negative);
		}
		
		//Convert into individual words
		String[] words = article.split(" ");
		
		//Remove common words
		for (String word : words) {
			//System.out.print(word + "\n");
			
			onPositiveOccurence(frequencies, word); 
			//TODO :: Still counts not x as a positive occurence aswell
		}
		
		//Remove common words
		
		if (printParsed) System.out.print(article + "\n");
		
		if (printFreq) printFrequencies(frequencies);
		return frequencies;
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
	
	private static void printFrequencies(HashMap<String, IntPair> freq) {
		for (Entry<String, IntPair> t : freq.entrySet()){
			System.out.print(t.getKey() + " Positive: " + t.getValue().left + " Negative: " + t.getValue().right + "\n");
		}
	}
	
	//Parses every article in the array
	public static ArrayList<HashMap<String, IntPair>> parseAll(ArrayList<String> formattedArticles) {
		ArrayList<HashMap<String,IntPair>> parsed = new ArrayList<HashMap<String,IntPair>>();
		
		for(String str : formattedArticles){
			parsed.add(parseArticle(str));
		}
		
		return parsed;
	}
	
	public static void main(String[] args){
		if (args.length == 0) {
			//use the test file
			try {
				String content = new Scanner(new File("C:\\test.txt")).useDelimiter("\\Z").next();
				ArrayList<String> articleText = getAllArticles(content);
				parseAll(articleText);
			} catch (IOException e) {
				System.out.print(e.getMessage());
				return;
			}
		}
	}
}
