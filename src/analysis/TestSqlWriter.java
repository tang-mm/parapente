package analysis;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.Statement;   
import java.sql.ResultSet;
import java.sql.SQLException; 

public class TestSqlWriter {
	
	public static void main(String[] args) throws ClassNotFoundException {
		// jdbc driver registration
		String driver = "org.sqlite.JDBC"; 
		Class.forName(driver);
		
		// basic settings (name)
		String dir = "./";
		String db = dir + "hello.db";
		String jdbc = "jdbc:sqlite";
		String dbUrl = jdbc + ":" + db;
		
		int timeOut = 30;
		String table = "table1 ";
		String attrb1 = "attrb1";
		String atype1 = "INT";
		String attrb2 = "attrb2";
		String atype2 = "VARCHAR";
		String value1 = "1, 'one'";
		String value2 = "2, 'two'";


	/*	String[] str1 = {"1", "one"};
		valueList.add(0, str1);
		String[] str2 = {"2", "two"};
		valueList.add(1, str2); 
		String[] str3 = {"3", "three"};
		valueList.add(2, str3);
		String[] str4 = {"4", "four"};
		valueList.add(3, str4);
		 */
		
		// instructions
		String execDrop = "DROP TABLE IF EXISTS " + table + ";";
		String execCreate = "CREATE TABLE " + table + "(" + attrb1 + " "+ atype1 + ", " + attrb2 + " "+ atype2 + ");" ;
		String execInsert1 = "INSERT OR REPLACE INTO " + table + "VALUES (" + value1 + ");" ;
		String execInsert2 = "INSERT OR REPLACE INTO " + table + "VALUES (" + value2 + ");" ;
		String execSelect = "SELECT * " + " FROM " + table + ";";
		
		System.out.println(execDrop);
		System.out.println(execCreate);
		System.out.println(execInsert1);
		System.out.println(execInsert2);
		System.out.println(execSelect);
		System.out.println("***********");
		
		// establish a connection
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(dbUrl);
			Statement stmt = conn.createStatement();
			
			stmt.setQueryTimeout(timeOut);
			stmt.executeUpdate(execDrop);
			stmt.executeUpdate(execCreate);
			stmt.executeUpdate(execInsert1);
			stmt.executeUpdate(execInsert2);
			
			ResultSet res = stmt.executeQuery(execSelect);
			System.out.println(attrb1 + "\t" + attrb2);
			System.out.println("-----" + "\t" + "-----");
			
			while(res.next()) {
				System.out.println(res.getInt(attrb1) + "\t" + res.getString(attrb2));
			}

		}catch(SQLException e){
			System.err.println(e.getMessage());
		}finally {
			try {
				if(conn != null)
					conn.close();
				
			}catch(SQLException e) {
				// connection close failed.
				System.err.println(e.getMessage());
			}
		}
	}
	
	
}
