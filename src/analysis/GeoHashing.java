package analysis;

import object.Point;
import ch.hsr.geohash.GeoHash;

/** 
 * calculating of GeoHash code with third-part library 
 *
 */
public class GeoHashing {
	
	private int precision; 
	
	/**
	 * indicate the precision of GeoHash code (number of characters)
	 * @param nbChar
	 */
	public GeoHashing(int nbChar) {
		this.precision = nbChar;
	}
	
	/**
	 * get GeoHash code in String with the defined precision
	 * @param lat
	 * @param lng
	 * @return
	 */
	public String encode(double lat, double lng) {
		return GeoHash.withCharacterPrecision(lat, lng, precision).toBase32();
	}
	
	public String encode(Point pt) {
		return encode(pt.getLatitude(), pt.getLongitude());
	}
	 
	
}
