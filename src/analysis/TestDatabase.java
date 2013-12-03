package analysis;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * data insertion into the database
 * @author tangmm
 *
 */
public class TestDatabase {

	/**
	 * insert data into database, configuration required before execution
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		// settings
		String dbName = "test.db"; // TODO configure
		String dir = "data/"; // directory of data file
		File fdir = new File(dir);
		File[] fList = fdir.listFiles();

		for (File f : fList) { // parse all files in the directory
			String fn = f.getName();
			if (f.isFile() && fn.startsWith("igcfile") && fn.endsWith(".igc")) {

				String fileName = dir + fn; // file name

				ArrayList<String[]> valueList = new ArrayList<String[]>();

				// TODO******** Configuration **********
				// table Point
				String tableName = "Point";
				int tableID = 2; // pre-defined in SqlWriter.java
				String[] attrb = { "idp", "idf", "time", "lat", "lng", "alt",
						"geohash", "vlat", "vlng", "valt" };			
		 
				// table Flight
		/*		String tableName = "Flight";
				int tableID = 1; // pre-defined in SqlWriter.java
				String[] attrb = { "idf", "datef", "pilot", "type", "model" };
		 */
				// open database
				SqlWriter writer = new SqlWriter(dbName);
				Connection conn = writer.getSQLiteConnection();
				Statement st = conn.createStatement();

				// generate SQL insert statement model
				String sqlInsert = writer.geneInsertStmt(tableName, attrb);

				// TODO******** Configuration **********
			    valueList = getValueListPoint(writer, fileName);
			//	valueList = getValueListFlight(writer, fileName);

				// insert values into table
				long startTime = System.currentTimeMillis();
				System.out.println("> (test) Start inserting data.");

				writer.execInsertStmt(conn, sqlInsert, tableID, valueList);

			/* 	for (String[] value : valueList)
				 	System.out.println("print exec: "+ Arrays.toString(value)); */
		
				long endTime = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				System.out .println("> (test) Data insertion finished. Duration: [" + totalTime + " ms].");

				st.close();
				conn.close(); 
			}
		}
 	}

	/**
	 *  data to insert into the table Point
	 * @param writer
	 * @param fileName
	 * @return
	 */
	private static ArrayList<String[]> getValueListPoint(SqlWriter writer, String fileName) {
 	
		// read file
		IgcParser parser = new IgcParser(fileName); 
		parser.readFile();	
		
		ArrayList<String[]> valueList = new ArrayList<String[]>(); 
		valueList = parser.getPointsInStringArray();

		return valueList;	
	}
	
	/**
	 * data to insert into the table Fligth
	 * @param writer
	 * @param fileName
	 * @return
	 */
	private static ArrayList<String[]> getValueListFlight(SqlWriter writer, String fileName) {
	 	
		// read file
		IgcParser parser = new IgcParser(fileName); 
		parser.readFile();	
		
		ArrayList<String[]> valueList = new ArrayList<String[]>(); 
		valueList.add(parser.getFlightInString());

		return valueList;	
	}
}