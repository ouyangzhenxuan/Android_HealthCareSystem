package com.medical.senddata;

public class InsertSensor2Request {
	String sensorID;
	String[] properties;
	public InsertSensor2Request(String sensorID, String[] properties) {
		this.sensorID = sensorID;
		this.properties = properties;
	}
	public String getSensorID() {
		return sensorID;
	}
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}
	public String[] getProperties() {
		return properties;
	}
	public void setProperties(String[] properties) {
		this.properties = properties;
	}
}
