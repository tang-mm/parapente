package analysis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * class generating data insertion into database
 * @author tangmm
 *
 */
public class SqlWriter {
	private String db = null;
	private PreparedStatement pst = null;
	private int timeout = 30;
	private final int batchSize = 1000; // execute Batch every batchSize
										// statements

	private SqlWriter() {
	}

	public SqlWriter(String db) {
		this.db = db;
	}

	public SqlWriter(String dbPath, String dbName) {
		this.db = dbPath + dbName;
	}

	/**
	 * establish and return an SQLite connection
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getSQLiteConnection() throws ClassNotFoundException,
			SQLException {
		// jdbc driver registration
		String driver = "org.sqlite.JDBC";
		Class.forName(driver);

		// establish connection
		String dbUrl = "jdbc:sqlite:" + this.db;
		Connection conn = DriverManager.getConnection(dbUrl);

		return conn;
	}

	/**
	 * Generate an Insert SQL statement in the form of PrepraredStatement final
	 * form: INSERT OR REPLACE INTO table (attr0, attr1, .., attrN) VALUES (?,
	 * ?, .., ?)"
	 * 
	 * @param table
	 * @param attributes
	 * @return
	 */
	public String geneInsertStmt(String table, String... attributes) {
		String sql = "INSERT OR REPLACE INTO " + table + " (" + attributes[0];

		int length = attributes.length; // number of columns
		for (int i = 1; i < length; i++) {
			sql += ", " + attributes[i];
		}
		sql += ") VALUES (?"; // except the first column
		for (int i = 1; i < length; i++) {
			sql += ", ?";
		}
		sql += ")";

		System.out.println(sql);
		return sql;
	}

	/**
	 * execute sql insertion requests into specific table with tableID in param
	 * @param conn
	 * @param sql
	 * @param valueList
	 * @param types
	 * @throws SQLException
	 */
	public void execInsertStmt(Connection conn, String sql, int tableID,
			ArrayList<String[]> valueList) {
		try {
			this.pst = conn.prepareStatement(sql);
			pst.setQueryTimeout(timeout);
			conn.setAutoCommit(false);
		} catch (SQLException e2) {
			System.out.println("Error: cannot insert statements! ");
			e2.printStackTrace();
		}

		// get corresponding table-insertion method
		SqlWriter wr = new SqlWriter();
		Class<? extends SqlWriter> cl = wr.getClass();
		Method method = null;
		Class<?>[] arg = { PreparedStatement.class, String[].class };

		try {
			switch (tableID) {
			case 1:
				method = cl.getDeclaredMethod("tableFlight", arg);
				break;
			case 2:
				method = cl.getDeclaredMethod("tablePoint", arg);
				break;
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		if (method != null)
			System.out.println("> (Writer) Invoke Method found");
		else
			System.out.println("> (Writer) Invoke Method failed");

		// fill in SQL statement with values
		try {
			int count = 0;
			for (String[] values : valueList) {
				Object isValidObj = method.invoke(wr, pst, values);
				// test if value == null
				boolean isValid = Boolean.valueOf(isValidObj.toString());
				if (isValid == false)
					continue; // do not insert this line
				pst.addBatch();
				count++;

				if (count % batchSize == 0) {
					pst.executeBatch(); // insert batchSize statements
					System.out.println("-- Execute batch " + count); // -----
					conn.commit();
				} // fi
			} // for

			pst.executeBatch(); // insert remaining records
			System.out.println("-- Execute batch - Total : " + count); // -----
			conn.commit();
			// conn.setAutoCommit(true);
			pst.close();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * PreparedStatement setting, define data types corresponding to the table
	 * Flight tableID = 1
	 * 
	 * @param pst
	 * @param values
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private boolean tableFlight(PreparedStatement pst, String[] values)
			throws NumberFormatException, SQLException {
		try {
			pst.setInt(1, Integer.parseInt(values[0])); // idFlight
			// convert String to Date
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
			pst.setDate(2, new java.sql.Date(((Date) sdf.parse(values[1])).getTime()));

			pst.setString(3, values[2]); // pilot
			pst.setString(4, values[3]); // gliderType
			pst.setString(5, values[4]); // gliderModel
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * PreparedStatement setting, define data types corresponding to the table
	 * Point tableID = 2
	 * 
	 * @param pst
	 * @param values
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private boolean tablePoint(PreparedStatement pst, String[] values)
			throws NumberFormatException, SQLException {
		try {
			pst.setInt(1, Integer.parseInt(values[0])); // idFlight
			pst.setInt(2, Integer.parseInt(values[1])); // idPoint
			// convert String to Date
			SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
			pst.setTime(3, new java.sql.Time(((Date) sdf.parse(values[2])).getTime())); // timestamp

			pst.setDouble(4, Double.parseDouble(values[3])); // latitude
			pst.setDouble(5, Double.parseDouble(values[4])); // longitude
			pst.setInt(6, Integer.parseInt(values[5])); // altitude

			pst.setString(7, values[6]); // geohash
			pst.setDouble(8, Double.parseDouble(values[7])); // vLat
			pst.setDouble(9, Double.parseDouble(values[8])); // vLong
			pst.setDouble(10, Double.parseDouble(values[9])); // vAlt
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
}
