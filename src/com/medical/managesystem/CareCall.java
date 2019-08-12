package com.medical.managesystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CareCall extends Activity {
	private Spinner spinner_receivemethod;
	private Switch switch_carecall;
	private TextView tv_carecall;
	private TextView tv_receivemethod;
	private TextView tv_carecallEmail;
	private TextView tv_carecallPhone;
	private TextView tv_carecallState;
	private EditText et_carecallEmail;
	private EditText et_carecallPhone;
	private Button btn_carecallconfirm;
	
	private MyApplicationPatientData mAppPatientInfo;
	private int choiceOfnotification = 0;
	
	private carecallThread carecallthread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carecall);
		spinner_receivemethod = (Spinner) findViewById(R.id.spinner_receivemethod);
		switch_carecall = (Switch) findViewById(R.id.switch_carecall);
		tv_carecall = (TextView) findViewById(R.id.tv_carecall);
		tv_receivemethod = (TextView) findViewById(R.id.tv_receivemethod);
		tv_carecallEmail = (TextView) findViewById(R.id.tv_carecallEmail);
		tv_carecallPhone = (TextView) findViewById(R.id.tv_carecallPhone);
		tv_carecallState = (TextView) findViewById(R.id.tv_carecallState);
		et_carecallEmail = (EditText) findViewById(R.id.et_carecallEmail);
		et_carecallPhone = (EditText) findViewById(R.id.et_carecallPhone);
		btn_carecallconfirm = (Button) findViewById(R.id.btn_carecallconfirm);
		
		String[] methods = {"无","邮件","短信","短信&邮件"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, methods);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_receivemethod.setAdapter(adapter);
		spinner_receivemethod.setClickable(false);
		spinner_receivemethod.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				choiceOfnotification = position;
			}
		});
		
		//设置请求监护开关状态
		switch_carecall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){  
					spinner_receivemethod.setClickable(true);
					tv_receivemethod.setTextColor(android.graphics.Color.BLACK);
	                Toast.makeText(CareCall.this, "已打开监护，请输入信息后点击确认", Toast.LENGTH_SHORT).show();  
	            }else {
	            	spinner_receivemethod.setSelection(0);
	            	spinner_receivemethod.setClickable(false);
	            	tv_receivemethod.setTextColor(android.graphics.Color.GRAY);
	            	Toast.makeText(CareCall.this, "已关闭监护", Toast.LENGTH_SHORT).show();
	            }
			}
		});
		if(LoginMain.login_state==false) {
			alertNoLogin().show();
		}
	}
	private  AlertDialog alertNoLogin() {
		AlertDialog noresponse = new AlertDialog.Builder(CareCall.this)
				.setTitle("提示")
				.setMessage("您尚未登陆，无法请求监护")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();
		noresponse.setCanceledOnTouchOutside(false);
		return noresponse;
	}
	
	public void onClickCarecallConfirm(View view) {
		carecallthread = new carecallThread();
		carecallthread.start();
	}
	private class carecallThread extends Thread{
		@Override
		public void run() {
			super.run();
			StringBuilder url = new StringBuilder();
			url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/patientMonitoring");
			//url.append("http://192.168.1.103:8080/WiseMedi/patientMonitoring");
			url.append("?patient.id="+mAppPatientInfo.getPatientID());
			url.append("&ses.email="+et_carecallEmail.getText().toString());
			url.append("&ses.SMS="+et_carecallPhone.getText().toString());
			url.append("&ses.servicetype="+choiceOfnotification);
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
				Toast.makeText(CareCall.this, "请求无响应，请检查网络设置", Toast.LENGTH_SHORT).show();
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
			if(respond.contains("Success")) {
				Toast.makeText(getApplicationContext(), "修改请求监护状态成功", Toast.LENGTH_SHORT).show();
				carecallthread.interrupt();
				finish();
			}else if(respond.toString().contains("Faild")){
				tv_carecallState.setText("修改监护请求失败");
				carecallthread.interrupt();
			}
		};
	};

}
