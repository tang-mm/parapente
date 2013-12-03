package analysis;

import java.util.ArrayList;
import java.util.Arrays;

import object.Point;

public class TestIgcParser {
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	 	String fileName = "data/igcfile2010_0.igc";

		IgcParser parser = new IgcParser(fileName); 
		
		ArrayList<Point> points = parser.readFile();
		for (Point pt : points) {
			System.out.println(Arrays.toString(pt.toStringArray())); 
		}
 
	}

}