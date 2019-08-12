package com.medical.managesystem;

import android.app.Application;

public class MyApplicationPatientData extends Application{
	static int patientID;
	static String patientName;
	static String patientPassword;
	static String patientIdentifCode;
	static String patientPhone;
	static String patientAddress;
	

	public static int getPatientID() {
		return patientID;
	}
	
	public static void setPatientID(int patientID) {
		MyApplicationPatientData.patientID = patientID;
	}
}
