package crunch;
  
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.PCollection;
import org.apache.crunch.PTable;
import org.apache.crunch.Pipeline;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.lib.Aggregate;
import org.apache.crunch.types.writable.Writables;

import ch.hsr.geohash.GeoHash;


/**
 *  Main to MapReduce procedure
 *  count 
 */
public class TraceAnalysis {
	
	
	public static void main(String[] args) throws Exception {
		String input = "lines.txt";
		String output = "output.txt";
		final int precision = 6;
		
		readFromDB(201060, input);
		
		// creation of Pipeline which is in charge of the operation chain
	    Pipeline pipeline = new MRPipeline(TraceAnalysis.class);

	    // read original text files, PCollection is a reference to the text read
	    PCollection<String> lines = pipeline.readTextFile(input);	// convert ResultSet to String[]
   
	    /** 
	     * Pcollection<string> pour la collection des String Geohash,
	     * quand on rencontre un code Geohash qui est dans le zone cible, count++
	     */

	    //use IgcParser here to parse file and create Point/ PointVector objects.
	    // redefine DoFn.process() to emit a new Point object when parsing a line 
	    PCollection<String> points = lines.parallelDo("point searcher", new DoFn<String, String>(){
	    	public void process(String line, Emitter<String> emitter) { 
				String pointGH = line.split("|")[6]; // find GeoHash in the line
				pointGH = pointGH.substring(0, precision); //
	    		emitter.emit(pointGH); // emit le GeoHash de chaque point
	    	}
	    }, Writables.strings());  
	  
        
	    // associate eligible PointVectors to a common point
	    PTable<String, Long> counts = Aggregate.count(points);
        pipeline.writeTextFile(counts, output);  
	    	    
	    pipeline.run(); 
	}
	
	
	/**
	 * read point data of the same idFlight from database, and write to file
	 * column seperated by '|'
	 * @param idFlight
	 */
	public static void readFromDB(int idFlight, String fileName)  {
		 Connection conn = null;
		 String tableName = "point";

		try {
			// jdbc driver registration
			String driver = "org.sqlite.JDBC";
			Class.forName(driver);

			// basic settings (name)
			String dir = "./";
			String db = dir + "test.db";
			String jdbc = "jdbc:sqlite";
			String dbUrl = jdbc + ":" + db;

			int timeOut = 30;

			conn = DriverManager.getConnection(dbUrl);
			Statement stmt = conn.createStatement();

			stmt.setQueryTimeout(timeOut);

			String execSelect = "SELECT * " + " FROM " + tableName
					+ " WHERE idf = '" + idFlight + " ';";

			ResultSet res = stmt.executeQuery(execSelect);
			ResultSetMetaData rsmd = res.getMetaData();
			int nbColumn = rsmd.getColumnCount();

			File file = new File(fileName);
			BufferedWriter bfWriter = new BufferedWriter(new FileWriter(file,
					true));
			System.out.println("-----" + "\t" + "-----");

			while (res.next()) {
				for (int i = 0; i < nbColumn; i++) {
					bfWriter.write(res.getString(i));
					bfWriter.write("|");
				}
				bfWriter.newLine();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			try {
				if (conn != null)
					conn.close(); 
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * examine if the two given points are in the same zone with the given precision
	 */
	public boolean areCommunPoints(String geohash1, String geohash2, int precision) {
		String str1 = geohash1.substring(0, precision);
		String str2 = geohash2.substring(0, precision);
		
		// compare the prefix (first 6 chars)
		int flag = GeoHash.fromGeohashString(str1).compareTo(GeoHash.fromGeohashString(str2));
		
		return (flag == 0) ? true : false;		
	}
}
