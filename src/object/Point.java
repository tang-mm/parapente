package object;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

/**
 * class containing information of each point in igc file
 * provide also methods to calculate distance, velocity.
 * Other attribtes could be added.
 * @author tangmm
 *
 */
public class Point implements Serializable, DBWritable, Writable {

	private int idFlight;
	private long idPoint; // order in the flight
	private Date timeStamp;

	private double latitude;
	private double longitude;
	private int altitude; // altitude from GPS

	private String geohash;

	private double vLat;	// velocity in latitude
	private double vLong;
	private double vAlt;

	private static final double EARTH_RADIUS = 6378137;
	
	public Point(Date time, double lat, double lng, int alt) {
		this.timeStamp = time;
		this.latitude = lat;
		this.longitude = lng;
		this.altitude = alt;
	}
	
	public Point(int idf, long idp, Date time, double lat, double lng, int alt) {
		this.idFlight = idf;
		this.idPoint = idp;
		this.timeStamp = time;
		this.latitude = lat;
		this.longitude = lng;
		this.altitude = alt;
	}

	/**
	 * convert to radian
	 * @param d
	 * @return
	 */
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * calculate distance in meters between two successive points
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static double calDistance(double lng1, double lat1, double lng2,
			double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**
	 * calculate horizontal velocity (both in lat and long) 
	 * @param lng1 
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static double calVelocity(double lng1, double lat1, double lng2,
			double lat2, Date time1, Date time2) {
		double distance = calDistance(lng1, lat1, lng2, lat2);
		long timeDiff = (time2.getTime() - time1.getTime()) / 1000;// in seconds
		 
		 return distance/timeDiff;
	}
	
	/**
	 * velocity in latitude only (changes in longitude)
	 * @param alt1
	 * @param alt2
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static double calVeloLat(double lng1, double lng2, double lat1, Date time1, Date time2) {
		double distance = calDistance(lng1, lat1, lng2, lat1);
		long timeDiff = (time2.getTime() - time1.getTime()) / 1000; // in seconds

		return distance / timeDiff;
	}
	
	/**
	 * velocity in longitude only (changes in latitude)
	 * @param lat1
	 * @param lat2
	 * @param lng1
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static double calVeloLong(double lat1, double lat2, double lng1, Date time1, Date time2) {
		double distance = calDistance(lng1, lat1, lng1, lat2); 
		long timeDiff = (time2.getTime() - time1.getTime()) / 1000; // in seconds

		return distance / timeDiff;
	}
	
	/**
	 * calculate velocity in altitude  
	 * @param alt1
	 * @param alt2
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static double calVeloAlt(int alt1, int alt2, Date time1, Date time2) {
		int distance = alt2 - alt1; 
		long timeDiff = (time2.getTime() - time1.getTime()) / 1000; // in seconds

		return (double) distance / timeDiff;
	}

	/**
	 * convert to text string
	 * @return
	 */
	public String[] toStringArray(){ 
		java.text.DateFormat format = new java.text.SimpleDateFormat("hhmmss");  
		String time = format.format(new Date()); 
		String[] array = {String.valueOf(idPoint), String.valueOf(idFlight), time, 
				String.valueOf(latitude), String.valueOf(longitude), String.valueOf(altitude),geohash,
				String.valueOf(vLat), String.valueOf(vLong), String.valueOf(vAlt)};
		return  array;
	}
	
	
	@Override
	public void readFields(ResultSet arg0) throws SQLException { 
		
	}

	@Override
	public void write(PreparedStatement arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
        this.idPoint = in.readLong();
        this.geohash = Text.readString(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
        out.writeLong(this.idPoint);
        Text.writeString(out, this.geohash);
	}

	
	public String getGeohash() {
		return geohash;
	}

	public void setGeohash(String geohash) {
		this.geohash = geohash;
	}

	public double getvLat() {
		return vLat;
	}

	public void setvLat(double vLat) {
		this.vLat = vLat;
	}

	public double getvLong() {
		return vLong;
	}

	public void setvLong(double vLong) {
		this.vLong = vLong;
	}

	public double getvAlt() {
		return vAlt;
	}

	public void setvAlt(double vAlt) {
		this.vAlt = vAlt;
	}

	public int getIdFlight() {
		return idFlight;
	}

	public long getIdPoint() {
		return idPoint;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public static double getEarthRadius() {
		return EARTH_RADIUS;
	}

	public PointVector getPointVector() {
		return new PointVector(this.vLat, this.vLong, this.vAlt);
	}
	
	public void setIdFlight(int idf) {
		this.idFlight = idf;
	}
	
	public void setIdPoint(long idp) {
		this.idPoint  = idp;
	}



}
