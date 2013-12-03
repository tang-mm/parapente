package object;

import java.io.Serializable;

public class PointVector implements Serializable { 
	
	private double vLat;	// velocity in latitude
	private double vLong;
	private double vAlt;
	
	public PointVector(double vLat, double vLong, double vAlt){
		this.vLat = vLat;
		this.vLong = vLong;
		this.vAlt = vAlt;
	}

	public double getvLat() {
		return vLat;
	}

	public double getvLong() {
		return vLong;
	}

	public double getvAlt() {
		return vAlt;
	}
	
	
}
