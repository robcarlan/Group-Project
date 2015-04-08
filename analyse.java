import java.util.ArrayList;
import java.util.HashMap;

//File
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class analyse {

	public static void main(String[] args) {
		try {
			String content = new Scanner(new File(args[0])).useDelimiter("\\Z").next();
			ArrayList<HashMap<String, IntPair>> result = parse.parseText(content);
			System.out.print(result.size() + " articles parsed"); 
		} catch (Exception ex) {
			System.out.print(ex.getMessage());
			return;
		}
	}
} 