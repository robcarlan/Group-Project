package analyse;
import java.util.ArrayList;
import java.util.HashMap;

//File
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import parsejson.ParsedArticle;
import parsejson.parse;

public class analyse {

	public static void main(String[] args) {
		try {
			
			Scanner scan = new Scanner(new File(args[0]));
			String content = scan.useDelimiter("\\Z").next();
			scan.close();
			
			//parse.java contains variables to print the articles aswell, these can be turned on or off if you want to check
			ArrayList<ParsedArticle> result = parse.parseAll(content);
			System.out.print(result.size() + " articles parsed"); 
		} catch (Exception ex) {
			System.out.print(ex.getMessage());
			return;
		}
	}
} 