package Naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import parsejson.IntPair;


public class NaiveClassifier {

	public static void main(String[] args) {
		SimpleClassifier classifier = new SimpleClassifier();
		String workingDirectory = System.getProperty("user.dir");
	//	int[] results = classifier.classifyJSON(workingDirectory+"/src/reuters17april_classified.txt");
	//	for(int i = 0; i<results.length;i++){
	//		System.out.println(results[i]);
	//	}
//	}
//}
		try {
			Scanner content = new Scanner(new File(workingDirectory+"/src/Naive/negative (1).txt")); //.useDelimiter("\\Z").next();
	//		int[] result = classifier.classifyJSON(content);
		//	for(int i = 0; i<result.length;i++){
		//		System.out.println(i);
		//		System.out.println(result[i]);
		//	}
			int c = 0;
			int x = 0;
			while(content.hasNext()){
				IntPair result = classifier.classifyString(content.nextLine());
				if (result.left < 0) c++;
				if (result.right!= 0) x++;
			}
			System.out.println(c);
			System.out.println(x);
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

