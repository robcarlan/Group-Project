package parsejson;

import java.util.HashMap;;

public class ParsedArticle {
	public ParsedArticle() {
		sentiment = "";
		originalArticle = "";
		words = null;
		wordsPolarity = null;
	}
	
	public String sentiment;
	public String originalArticle;
	public HashMap<String, IntPair> wordsPolarity;
	public HashMap<String, Integer> words;
}
