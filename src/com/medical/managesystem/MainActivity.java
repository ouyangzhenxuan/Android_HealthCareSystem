package com.medical.managesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {
	private GridView gv_frame;            //3*3功能结构
	private ImageView iv_backgroundTop;   //背景图片
	private BluetoothAdapter mBluetoothAdapter;
	//主页面上9个功能的图片
	private int[] images_background= {R.drawable.bloodpressure,R.drawable.deviceregister,R.drawable.login,
							R.drawable.heartrate,R.drawable.devicelist,R.drawable.dataalter,R.drawable.bloodsugar,
							R.drawable.dataupload,R.drawable.carecall};
	//主页面上9个功能的名字
	private String[] names_background= {"血压","注册设备","登陆","心率","设备管理","信息更改","血糖","数据接入","请求监护"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gv_frame=(GridView) findViewById(R.id.gv_frame);
		iv_backgroundTop=(ImageView) findViewById(R.id.iv_backgroundTop);
		gv_frame.setAdapter(getAdapter_functionOfBG());
		//设置各个功能的监听事件
		gv_frame.setOnItemClickListener(this);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent,1);
	}
	/***************************************************************************************/
	//建立主页面功能图片和名字的adapter
	private ListAdapter getAdapter_functionOfBG() {
		List<HashMap<String, Object>> list =new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<images_background.length;i++) {
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("txt", names_background[i]);
			map.put("img", images_background[i]);
			list.add(map);
		}
		//item_mainbackground是每个gridview小部分的构建
		SimpleAdapter adapter=new SimpleAdapter(getApplicationContext(), list, R.layout.item_mainbackground, 
				new String[] {"txt","img"}, new int[] {R.id.tv_tvbg,R.id.iv_itembg});
		
		return adapter;
	}
	/***************************************************************************************/
	@Override
	protected void onResume() {
		super.onResume();
		//回到主页面时更改标题
		setTitle("医疗管理系统");
	}
	@Override
	//positon是9个功能的序号，0 ~ 8
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("ouyang", "打开界面 "+position);//日志打印序号
		switch (position) {
		case 0://血压界面
			Intent bloodpressure=new Intent(MainActivity.this, BloodPressure.class);
			startActivity(bloodpressure);
			setTitle("血压");
			break;
		case 1://注册设备界面
			if(LoginMain.login_state==true) {
				Intent deviceregister=new Intent(MainActivity.this, DeviceRegister.class);
				startActivity(deviceregister);
				setTitle("注册设备");
				}else {
					Toast.makeText(MainActivity.this, "未进行登陆，无法查看注册设备", Toast.LENGTH_SHORT).show();
				}
			break;
		case 2://登陆界面
			Intent login=new Intent(MainActivity.this, LoginMain.class);
			startActivity(login);
			setTitle("登陆");
			break;
		case 3://心率界面
			Intent heartrate=new Intent(MainActivity.this, HeartRate.class);
			startActivity(heartrate);
			setTitle("心率");
			break;
		case 4://设备管理界面
			if(LoginMain.login_state==true) {
				Intent devicelist=new Intent(MainActivity.this, DeviceList.class);
				startActivity(devicelist);
				setTitle("设备管理");
				}else {
					Toast.makeText(MainActivity.this, "未进行登陆，无法管理设备", Toast.LENGTH_SHORT).show();
				}
			break;
		case 5://信息更改界面
			if(LoginMain.login_state==true) {
				Intent dataalter=new Intent(MainActivity.this, DataAlter.class);
				startActivity(dataalter);
				setTitle("信息更改");
				}else {
					Toast.makeText(MainActivity.this, "未进行登陆，无法更改信息", Toast.LENGTH_SHORT).show();
				}
			break;
		case 6://血糖界面
			Intent bloodsugar=new Intent(MainActivity.this, BloodSugar.class);
			startActivity(bloodsugar);
			setTitle("血糖");
			break;
		case 7://数据上传界面
			if(LoginMain.login_state==true) {
				Intent dataupload=new Intent(MainActivity.this, DataUpload.class);
				startActivity(dataupload);
				setTitle("数据上传");
				}else {
					Toast.makeText(MainActivity.this, "未进行登陆，无法上传数据", Toast.LENGTH_SHORT).show();
				}
			break;
		case 8://请求监护界面
			if(LoginMain.login_state==true) {
				Intent carecall=new Intent(MainActivity.this, CareCall.class);
				startActivity(carecall);
				setTitle("请求监护");
				}else {
					Toast.makeText(MainActivity.this, "未进行登陆，无法请求监护", Toast.LENGTH_SHORT).show();
				}
			break;

		}
	}
}
