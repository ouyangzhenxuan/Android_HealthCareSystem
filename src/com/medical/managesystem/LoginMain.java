package com.medical.managesystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginMain extends Activity{
	/*登陆功能页面的控件*/
	/********************/
	private Button btn_login;
	private Button btn_btn_Register_login;
	//userID和password是登陆必须信息，username非必要
	private EditText et_userID_login;
	private EditText et_password_login;
	private TextView tv_loginstate;
	private TextView tv_userID;
	private TextView tv_password_login;
	private CheckBox cb_rememberpassword;
	private SharedPreferences config;//用于记住密码
	private LoginThread loginthread;
	public static boolean login_state=false;//标记登陆状态
	private MyApplicationPatientData mAppPatientInfo;
	/*******************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_mainbackground);
		config=getSharedPreferences("config", MODE_PRIVATE);
		btn_login=(Button) findViewById(R.id.btn_login);
		btn_btn_Register_login=(Button) findViewById(R.id.btn_Register_login);
		et_userID_login=(EditText) findViewById(R.id.et_userID_login);
		et_password_login=(EditText) findViewById(R.id.et_password_login);
		tv_userID=(TextView) findViewById(R.id.tv_userID);
		tv_password_login=(TextView) findViewById(R.id.tv_password_login);
		tv_loginstate=(TextView) findViewById(R.id.tv_loginstate);
		cb_rememberpassword=(CheckBox) findViewById(R.id.cb_rememberpassword);
		
		//是否记住了密码
		boolean isChecked=config.getBoolean("isChecked", false);
			if(isChecked) {
				et_userID_login.setText(config.getString("userID", ""));
				et_password_login.setText(config.getString("password", ""));
			}
			cb_rememberpassword.setChecked(isChecked);
			
		if(login_state==true) {
			alertAlreadyLogin().show();
		}
	}
	
	private  AlertDialog alertAlreadyLogin() {
		AlertDialog noresponse = new AlertDialog.Builder(LoginMain.this)
				.setTitle("提示")
				.setMessage("您已经登陆")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();
		noresponse.setCanceledOnTouchOutside(false);
		return noresponse;
	}
	
	//记住密码的实现
	public void onClick_Login(View view) {
		Editor edit=config.edit();
		String userID=et_userID_login.getText().toString();
		String password=et_password_login.getText().toString();
		//标志位，是否对“记住密码”打勾
		boolean isChecked=cb_rememberpassword.isChecked();
		edit.putBoolean("isChecked", isChecked);
		//记住密码
		if(isChecked) {
			edit.putString("password", password).putString("userID", userID);
		}else {
			edit.remove(password);
			edit.remove(userID);
		}
		edit.commit();//提交到本地!!!
		
		/******************************/
		//匹配数据库的登陆信息
		loginthread = new LoginThread();
		loginthread.start();
		/******************************/
	}
	
	public void onClick_NewRegister(View view) {
		Intent intent = new Intent(this, LoginMain_NewRegister.class);
		startActivity(intent);
	}
	
	public class LoginThread extends Thread{
		@Override
		public void run() {
			super.run();
			StringBuilder url = new StringBuilder();
			//url.append("http://192.168.1.103:8080/WiseMedi/patientLoginand");
			//测试
			url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/patientLoginand");
			url.append("?LoginName="+et_userID_login.getText().toString());
			url.append("&LoginPwd="+et_password_login.getText().toString());
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
				Log.d("ouyang", ""+code);
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
				Toast.makeText(LoginMain.this, "请求无响应，请检查网络设置", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} 
			Log.d("ReturnCode",buffer.toString());
			Message returncode = new Message();
			returncode.obj = buffer.toString();
			handler.sendMessage(returncode);
			}
		}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String respond = new String(String.valueOf(msg.obj));
			if(respond.contains("SUCCESS")) {
				Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
				login_state=true;//设置登陆状态为成功登陆
				mAppPatientInfo.setPatientID(Integer.parseInt(respond.substring(7)));
				loginthread.interrupt();
				finish();
			}else if(respond.toString().contains("FAILED")){
				tv_loginstate.setText("登陆失败，请检查用户名和密码是否正确");
				loginthread.interrupt();
			}
		};
	};
	
	@Override
	protected void onStop() {
		Log.d("ouyang", "loginOnStop");
		super.onStop();
	}
	@Override
	protected void onPause() {
		Log.d("ouyang", "loginOnPause");
		super.onPause();
	}
}
