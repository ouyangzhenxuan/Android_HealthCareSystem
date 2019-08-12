package com.medical.managesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginMain_NewRegister extends Activity{
	private TextView tv_registerLoginname;
	private EditText et_registerLoginname;
	private TextView tv_registerLoginpassword;
	private EditText et_registerLoginpassword;
	private TextView tv_registerIdentifCode;
	private EditText et_registerIdentifCode;
	private TextView tv_registerPhone;
	private EditText et_registerPhone;
	private TextView tv_registerAdress;
	private EditText et_registerAdress;
	private TextView tv_registerEmail;
	private EditText et_registerEmail;
	private TextView tv_registerSensorID;
	private EditText et_registerSensorID;
	private TextView tv_registerProperties;
	private EditText et_registerProperties;
	private Button btn_Register_Register;
	private TextView tv_registerstate;
	private RegisterThread registerthread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_newuserregister);
		tv_registerLoginname = (TextView) findViewById(R.id.tv_registerLoginname);
		et_registerLoginname = (EditText) findViewById(R.id.et_registerLoginname);
		tv_registerLoginpassword = (TextView) findViewById(R.id.tv_registerLoginpassword);
		et_registerLoginpassword = (EditText) findViewById(R.id.et_registerLoginpassword);
		tv_registerIdentifCode = (TextView) findViewById(R.id.tv_registerIdentifCode);
		et_registerIdentifCode = (EditText) findViewById(R.id.et_registerIdentifCode);
		tv_registerPhone = (TextView) findViewById(R.id.tv_registerPhone);
		et_registerPhone = (EditText) findViewById(R.id.et_registerPhone);
		tv_registerAdress = (TextView) findViewById(R.id.tv_registerAdress);
		et_registerAdress = (EditText) findViewById(R.id.et_registerAdress);
		tv_registerEmail = (TextView) findViewById(R.id.tv_registerEmail);
		et_registerEmail = (EditText) findViewById(R.id.et_registerEmail);
		tv_registerSensorID = (TextView) findViewById(R.id.tv_registerSensorID);
		et_registerSensorID = (EditText) findViewById(R.id.et_registerSensorID);
		tv_registerProperties = (TextView) findViewById(R.id.tv_registerProperties);
		et_registerProperties = (EditText) findViewById(R.id.et_registerProperties);
		btn_Register_Register = (Button) findViewById(R.id.btn_Register_Register);
		tv_registerstate = (TextView) findViewById(R.id.tv_registerstate);
	}
	public void onClick_NewUserRegister(View view) {
		registerthread = new RegisterThread();
		registerthread.start();
	}
	public class RegisterThread extends Thread{
		@Override
		public void run() {
			super.run();
			StringBuilder url = new StringBuilder();
			//url.append("http://192.168.1.103:8080/WiseMedi/patientRegisterand");
			url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/patientRegisterand");
			url.append("?patient.loginName="+et_registerLoginname.getText().toString());
			url.append("&patient.loginPwd="+et_registerLoginpassword.getText().toString());
			url.append("&patient.identifCode="+et_registerIdentifCode.getText().toString());
			url.append("&patient.phone="+et_registerPhone.getText().toString());
			url.append("&patient.address="+et_registerAdress.getText().toString());
			url.append("&patient.email="+et_registerEmail.getText().toString());
			url.append("&sensorinfo.sensorId="+et_registerSensorID.getText().toString());
			url.append("&sensorinfo.properties="+et_registerProperties.getText().toString());
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
			if(respond.contains("Registersuccess")) {
				Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
				registerthread.interrupt();
				finish();
			}else if(respond.toString().contains("allreadyexists")){
				tv_registerstate.setText("帐号已存在，请重新输入登陆用户名");
			}else if(respond.toString().contains("RegisterFaild")) {
				tv_registerstate.setText("未上传传感器，传感器ID为空");
			}
		};
	};
	
}
