package com.medical.managesystem;


import android.app.Application;
//�������ݱ�����
public class MyApplicationDataSave2 extends Application{
	private static String[] datalist = new String[1024];
	private static int data_i;
	private static String deviceName;
	
	public static String getDeviceName() {
		return deviceName;
	}
	public static void setDeviceName(String deviceName) {
		MyApplicationDataSave2.deviceName = deviceName;
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
		MyApplicationDataSave2.data_i = data_i;
	}
	//��data[num]������ݴ���MyApplication���ȫ�ֱ���������
	public static void setData(String[] data,int num) {
		MyApplicationDataSave2.datalist[num] = data[num];
	}
	//�õ�datalist��������ĳ�������ֵ
	public static String getData(int num) {
		return datalist[num];
	}
	public static void setData2(float data,int num) {
		//intתString
		MyApplicationDataSave2.datalist[num]=Float.toString(data);
	}
	//���ڴ�ȡ�豸����
	public static void setData3(String data,int num) {
		MyApplicationDataSave2.datalist[num]=data;
	}
	
}
