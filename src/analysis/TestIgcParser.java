package analysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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

		/*// test Date format
		 * String date = "311213"; SimpleDateFormat sdf = new
		 * SimpleDateFormat("ddMMyy"); try { System.out.println(new
		 * java.sql.Date(((Date) sdf.parse(date)).getTime()));
		 * 
		 * 
		 * String time = "110135"; SimpleDateFormat sdf2 = new
		 * SimpleDateFormat("HHmmss"); System.out.println(new
		 * java.sql.Time(((Date) sdf2.parse(time)).getTime())); // timestamp
		 * 
		 * } catch (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

}