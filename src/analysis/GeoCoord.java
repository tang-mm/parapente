package analysis;

import java.lang.Math;

/**
 * convert lat/lng String "degree minute second" to decimal double values 
 *
 */
public class GeoCoord {

	/**
	 * 
	 * @param latStr, DD MM.MMM
	 * @return
	 */
	public static double convertLatitudeFromString (String latStr) throws Exception {
		if (latStr.length() != 7)
			throw new Exception("invalid Latitude String value : length should be 7! ");
		
		int latd = Integer.parseInt(latStr.substring(0, 2));
		int latm = Integer.parseInt(latStr.substring(2, 4));
		int latmm = Integer.parseInt(latStr.substring(4, 7));
		
		double lat = Math.abs(latd) + latm/60.0 + latmm/60000.0;
		if (lat < 0) return -lat;
		else return lat;
	}
	
	public static double convertLongitudeFromString (String lngStr) throws Exception {
		if (lngStr.length() != 8)
			throw new Exception("invalid Longitude String value : length should be 8! (length = " + lngStr.length() + ")");
		
		int lngd = Integer.parseInt(lngStr.substring(0, 3));
		int lngm = Integer.parseInt(lngStr.substring(3, 5));
		int lngmm = Integer.parseInt(lngStr.substring(5, 8));
		
		double lng = Math.abs(lngd) + lngm/60.0 + lngmm/60000.0;
		if (lng < 0) return -lng;
		else return lng;
	}
}
