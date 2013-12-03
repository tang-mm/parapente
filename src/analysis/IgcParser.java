package analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import object.Flight;
import object.Point;

public class IgcParser {

	private BufferedReader br = null;

	private Flight flight;
	private ArrayList<Point> points;
	private GeoHashing geohashing;
	
	public IgcParser(String fileName) {
		try {
			this.br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// ex. fileName = "data/igcfile2010_12.igc" : idf = 201012
		String pref = fileName.substring(12, 16);
		String suf = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf(".")); 
		String idFlight = pref.concat(suf); 
		
		flight = new Flight(Integer.parseInt(idFlight));
		points = new ArrayList<Point>();
		geohashing = new GeoHashing(8); // precision = 8 chars
	}

	/**
	 * parse point information in IGC file
	 * 
	 * ex. B,110135,5206343N,00006198W,A,00587,00558 B: record type is a basic
	 * tracklog record [6]110135: <time> tracklog entry was recorded at 11:01:35
	 * i.e. just after 11am [7+1]5206343N: <lat> i.e. 52 degrees 06.343 minutes
	 * North [8+1]00006198W: <long> i.e. 000 degrees 06.198 minutes West A: <alt
	 * valid flag> confirming this record has a valid altitude value [5]00587:
	 * <altitude from pressure sensor> [5]00558: <altitude from GPS>
	 */

	public ArrayList<Point> readFile() {

		String record = new String();
		String line1 = new String(); // read two lines at the same time to get
		String line2 = new String(); // two successive points
		double lat1, lng1, lat2, lng2;
		int alt1, alt2;
		Date time1, time2;
		long idPoint = 0; // count IdPoint in this file
		int idFlight = this.flight.getIdFlight();

		try {
			// Header info
			br.readLine(); // first line unused  
			
			// parse file header to Flight
			while (true) {
				if ((record = br.readLine()) != null && record.length() > 0) { 
					if (record.charAt(0) == 'H') {
						parseHeaderInfo(record, flight); 
					} else
						break;
				}
			} 
			
			// read until first 'B' line
			while (true) {
				line2 = br.readLine(); 
				if (line2 != null && line2.length() > 0 && line2.charAt(0) == 'B') 
					break;
			}
			
			// first 'B' line 
			line1 = line2;
			SimpleDateFormat sdf = new SimpleDateFormat("hhmmss"); 
			time1 = (Date) sdf.parse(line1.substring(1, 7)); 
			lat1 = GeoCoord.convertLatitudeFromString(line1.substring(7, 14));   // convert degree to double 
			lng1 = GeoCoord.convertLongitudeFromString(line1.substring(15, 23)); 
			alt1 = Integer.parseInt(line1.substring(30, 35));

			Point pt0 = new Point(time1, lat1, lng1, alt1);
			pt0.setIdFlight(idFlight);
			idPoint++;
			pt0.setIdPoint(idPoint);
			pt0.setGeohash(geohashing.encode(pt0)); // GeoHash code
			points.add(pt0);

			// start from 2nd 'B' line
			while ((line2 = br.readLine()) != null) { 
				if (line2.length() == 0 || line2.charAt(0) != 'B') continue;
				
				time2 = (Date) sdf.parse(line2.substring(1, 7));
				lat2 = GeoCoord.convertLatitudeFromString(line2.substring(7, 14));   // convert degree to double 
				lng2 = GeoCoord.convertLongitudeFromString(line2.substring(15, 23));
				alt2 = Integer.parseInt(line2.substring(30, 35));

				// calculate velocity of Point1
				double vlat = Point.calVeloLat(lng1, lng2, lat1, time1, time2);
				double vlong = Point.calVeloLong(lat1, lat2, lng1, time1, time2);
				double valt = Point.calVeloAlt(alt1, alt2, time1, time2);

				// add to points collection 
				Point pt = new Point(time2, lat2, lng2, alt2);
				idPoint++;

				pt.setIdFlight(idFlight);
				pt.setIdPoint(idPoint);
				pt.setvLat(vlat);
				pt.setvLong(vlong);
				pt.setvAlt(valt);
				pt.setGeohash(geohashing.encode(pt));  // GeoHash code
				points.add(pt);
				
				// pass to next line
				line1 = line2;
				lat1 = lat2;
				lng1 = lng2;
				alt1 = alt2;

			} // while
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return points;

	}
	
	
	public static Point parseLineToPoint(String line) {
		SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");

		Date time = null;
		double lat = 0, lng = 0;
		try {
			time = (Date) sdf.parse(line.substring(1, 6));
			lat = GeoCoord.convertLatitudeFromString(line.substring(7, 14)); // convert  degree  to double
			lng = GeoCoord.convertLongitudeFromString(line.substring(15, 23));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int alt = Integer.parseInt(line.substring(30, 35));
		return new Point(time, lat, lng, alt);
	}

	
	public static Point parseLineToPoint(String line, int idf, long idp) {
		SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");

		Date time = null;
		double lat = 0, lng = 0;

		try {
			time = (Date) sdf.parse(line.substring(1, 6));
			lat = GeoCoord.convertLatitudeFromString(line.substring(7, 14)); // convert  degree  to double
			lng = GeoCoord.convertLongitudeFromString(line.substring(15, 23));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int alt = Integer.parseInt(line.substring(30, 35));
		return new Point(idf, idp, time, lat, lng, alt);
	}

	// http://code.google.com/p/opensoaring/source/browse/src/info/opensoaring/util/igc/LogParser.java?r=ea0f2ed3dfb73c292afa7f07227dd7dce007a2a8
	/**
	 * An enumeration containing all defined IGC TLCs (Three Letter Codes).
	 */
	public static enum TLC {
		ATS, CCL, CCN, CCO, CDC, CGD, CID, CLB, CM2, DAE, DAN, DB1, DB2, DOB, 
		DTE, DTM, EDN, ENL, EOF, EON, EUP, FIN, FLP, FRS, FTY, FXA, GAL, GCN, 
		GDC, GID, GLO, GPS, GSP, GTY, HDM, HDT, IAS, LAD, LOD, LOV, MAC, OAT, 
		ONT, OOI, PEV, PFC, PHO, PLT, PRS, RAI, REX, RFW, RHW, RPM, SCM, SEC, 
		SIT, SIU, STA, TAS, TDS, TEN, TPC, TRM, TRT, TZN, UND, UNT, VAR, VAT, VXA, WDI, WSP
	};

	/**
	 * Parses IGC 'I' records - File Header Information.
	 * 
	 * Formats: H[F|O|P][DTE][DDMMYY] H[F|O|P][FXA][AAA] H[F|O|P][CCC][STR(30)] 
	 * 
	 * @param record
	 *            The string containing the I record to parse
	 * @param flightProps
	 *            The FlightProperties object to be updated with the parsed info
	 */
	public static void parseHeaderInfo(String record, Flight flight) {
		// Load the TLC
		TLC recordSubType = null;
		try {
			recordSubType = TLC.valueOf(record.substring(2, 5));
		} catch (IllegalArgumentException e) {
			return;
		}

		// Fill in the flight properties object
		switch (recordSubType) {
		case DTE:
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");			
				Date date = (Date) sdf.parse(record.substring(5, 10));
				flight.setDate(date);
				System.out.println("Date= " + date.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break; 
		case PLT:  // string from ':' to the end 
			flight.setPilot(record.substring(record.indexOf(":") + 1).trim()); 
			break; 
		case GTY: 
			flight.setGliderType(record.substring(record.indexOf(":") + 1).trim());
			break;
		case GID: 
			flight.setGliderModel(record.substring(record.indexOf(":") + 1).trim());
			break;
		default:
			return;
		}
	} 
	
	public ArrayList<Point> getPoints() {
		return this.points;
	}
	
	public ArrayList<String[]> getPointsInStringArray() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (Point pt : this.points) {
			list.add(pt.toStringArray());
		}
		return list;
	}
	
	public Flight getFlight() {
		return this.flight;
	}
	
	public String[] getFlightInString() {
		return this.flight.toStringArray();
	}
}
