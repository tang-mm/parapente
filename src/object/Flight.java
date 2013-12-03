package object;

import java.util.Date;

/** 
 * class containing origin flight information in igc file  
 * read from file headers
 * @author tangmm
 */
public class Flight {

	private int idFlight;
	private Date date; // UTC date, DDMMYY
	private String pilot;
	private String gliderType;
	private String gliderModel; // Glider registration number
	
	public Flight() {}
	
	public Flight(int id) {
		this.idFlight = id;
	}
	
	public Flight (int id, Date dt, String pilot) {
		this.idFlight = id;
		this.date = dt;
		this.pilot = pilot;
	}
	
	public Flight (int id, Date dt, String pilot, String type) {
		this.idFlight = id;
		this.date = dt;
		this.pilot = pilot;
		this.gliderType = type;
	}

	public Flight (int id, Date dt, String pilot, String type, String model) {
		this.idFlight = id;
		this.date = dt;
		this.pilot = pilot;
		this.gliderType = type;
		this.gliderModel = model;
	}

	public String[] toStringArray() {
		java.text.DateFormat format = new java.text.SimpleDateFormat("ddMMyy");  
		String date = format.format(new Date()); 
		String[] array = {String.valueOf(idFlight), date, pilot, gliderType, gliderModel};
		return  array;
	}
	
	public void setId(int id) {
		this.idFlight = id;
	}
	
	public void setDate(Date dt) {
		this.date = dt;
	}
	
	public void setPilot(String plt) {
		this.pilot = plt;
	}
	
	public String getGliderType() {
		return gliderType;
	}

	public void setGliderType(String gliderType) {
		this.gliderType = gliderType;
	}

	public String getGliderModel() {
		return gliderModel;
	}

	public void setGliderModel(String gliderModel) {
		this.gliderModel = gliderModel;
	}

	public int getIdFlight() {
		return idFlight;
	}

	public Date getDate() {
		return date;
	}

	public String getPilot() {
		return pilot;
	}	
	
}
