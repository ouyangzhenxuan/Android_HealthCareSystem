package com.medical.managesystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DataAlter extends Activity{
	private TextView tv_dataupdateName;
	private TextView tv_dataupdatePassword;
	private TextView tv_dataupdateIdentifCode;
	private TextView tv_dataupdatePhone;
	private TextView tv_dataupdateAdress;
	private TextView tv_dataupdateEmail;
	
	private EditText et_dataupdateName;
	private EditText et_dataupdatePassword;
	private EditText et_dataupdateIdentifCode;
	private EditText et_dataupdatePhone;
	private EditText et_dataupdateAdress;
	private EditText et_dataupdateEmail;
	
	private MyApplicationPatientData mAppPatientInfo;
	private dataUpdateThread dataupdatethread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataalter);
		tv_dataupdateName = (TextView) findViewById(R.id.tv_dataupdateName);
		tv_dataupdatePassword = (TextView) findViewById(R.id.tv_dataupdatePassword);
		tv_dataupdateIdentifCode = (TextView) findViewById(R.id.tv_dataupdateIdentifCode);
		tv_dataupdatePhone = (TextView) findViewById(R.id.tv_dataupdatePhone);
		tv_dataupdateAdress = (TextView) findViewById(R.id.tv_dataupdateAdress);
		tv_dataupdateEmail = (TextView) findViewById(R.id.tv_dataupdateEmail);
		et_dataupdateName = (EditText) findViewById(R.id.et_dataupdateName);
		et_dataupdatePassword = (EditText) findViewById(R.id.et_dataupdatePassword);
		et_dataupdateIdentifCode = (EditText) findViewById(R.id.et_dataupdateIdentifCode);
		et_dataupdatePhone = (EditText) findViewById(R.id.et_dataupdatePhone);
		et_dataupdateAdress = (EditText) findViewById(R.id.et_dataupdateAdress);
		et_dataupdateEmail = (EditText) findViewById(R.id.et_dataupdateEmail);
		
		if(LoginMain.login_state==false) {
			NoLoginAlert().show();
		}
	}
	
	private  AlertDialog NoLoginAlert() {
		AlertDialog noresponse = new AlertDialog.Builder(DataAlter.this)
				.setTitle("提示")
				.setMessage("您尚未登陆，无法修改信息")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();
		noresponse.setCanceledOnTouchOutside(false);
		return noresponse;
	}
	
	public void onClick_DataUpdate(View view) {
		dataupdatethread = new dataUpdateThread();
		dataupdatethread.start();
	}
	
	public class dataUpdateThread extends Thread{
		@Override
		public void run() {
			super.run();
			StringBuilder url = new StringBuilder();
			url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/patientUpdate");
			//url.append("http://192.168.1.103:8080/WiseMedi/patientUpdate");
			url.append("?patient.id="+MyApplicationPatientData.getPatientID());
			url.append("&patient.loginName="+et_dataupdateName.getText().toString());
			url.append("&patient.loginPwd="+et_dataupdatePassword.getText().toString());
			url.append("&patient.identifCode="+et_dataupdateIdentifCode.getText().toString());
			url.append("&patient.phone="+et_dataupdatePhone.getText().toString());
			url.append("&patient.address="+et_dataupdateAdress.getText().toString());
			url.append("&patient.email="+et_dataupdateEmail.getText().toString());
			url.append("&sensorinfo.sensorId=");
			url.append("&sensorinfo.properties=");
			String urlConnect = url.toString();
			StringBuffer buffer=new StringBuffer();
			try {
				//封装了URL对象
				URL url2=new URL(urlConnect);
				HttpURLConnection conn = (HttpURLConnection)url2.openConnection();
				conn.setRequestMethod("POST");
				//获取状态码
				int code = conn.getResponseCode();
				//conn.getResponseCode();
				Log.d("bh", ""+code);
				if(code==200) {//状态码为200代表请求成功
					//获取响应消息的实体内容
					InputStreamReader reader=new InputStreamReader(conn.getInputStream(),"UTF-8");
					char[] charArr=new char[1024*8];
					int len=0;
					while((len=reader.read(charArr)) != -1) {
						//字符数组转字符串
						String str=new String(charArr,0,len);
						//在edittext结尾追加字符串
						buffer.append(str);
					}
				}
			}  catch (IOException e) {
				//int code = conn.getResponseCode();
				e.printStackTrace();
			} 
			Log.d("ReturnCode", buffer.toString());
			Message returncode = new Message();
			returncode.obj = buffer.toString();
			handler.sendMessage(returncode);
		}
	}
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String respond = new String(String.valueOf(msg.obj));
			if(respond.contains("SUCCESS")) {
				Toast.makeText(getApplicationContext(), "信息修改成功", Toast.LENGTH_SHORT).show();
				dataupdatethread.interrupt();
				finish();
			}
		};
	};

}
