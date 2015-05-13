package Naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;


public class Dic {
	private HashSet<String> positive = new HashSet<String>();
	private HashSet<String> negative = new HashSet<String>();
	
	public Dic(){
		String workingDirectory = System.getProperty("user.dir");
		Scanner scanner;
		try {
			scanner = new Scanner(new File(workingDirectory+"/src/Naive/wordlistpositive.txt"));
			while(scanner.hasNextLine()){
				positive.add(scanner.nextLine().toLowerCase());
			}
			scanner = new Scanner(new File(workingDirectory+"/src/Naive/wordlistnegative.txt"));
			while(scanner.hasNextLine()){
				negative.add(scanner.nextLine().toLowerCase());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int compare(String str){
		if(positive.contains(str)) return 1;
		if(negative.contains(str)) return -1;
		return 0;
	}

}
