package com.medical.managesystem;


import android.app.Application;
//血压数据保存类
public class MyApplicationDataSave extends Application{
	private static String[] datalist = new String[1024];
	private static int data_i;
	private static String deviceName;
	public static String getDeviceName() {
		return deviceName;
	}
	public static void setDeviceName(String deviceName) {
		MyApplicationDataSave.deviceName = deviceName;
	}
	public static String[] getDatalist() {
		return datalist;
	}
	public void setDatalist(String[] datalist) {
		this.datalist = datalist;
	}
	public static int getData_i() {
		return data_i;
	}
	public static void setData_i(int data_i) {
		MyApplicationDataSave.data_i = data_i;
	}
	//将data[num]这个数据存入MyApplication这个全局变量类里面
	public static void setData(String[] data,int num) {
		MyApplicationDataSave.datalist[num] = data[num];
	}
	//得到datalist数组里面某个具体的值
	public static String getData(int num) {
		return datalist[num];
	}
	public static void setData2(float data,int num) {
		//int转String
		MyApplicationDataSave.datalist[num]=Float.toString(data);
	}
	public static void setData3(String data,int num) {
		MyApplicationDataSave.datalist[num]=data;
	}
}
