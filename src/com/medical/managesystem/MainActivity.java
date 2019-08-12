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
	private GridView gv_frame;            //3*3���ܽṹ
	private ImageView iv_backgroundTop;   //����ͼƬ
	private BluetoothAdapter mBluetoothAdapter;
	//��ҳ����9�����ܵ�ͼƬ
	private int[] images_background= {R.drawable.bloodpressure,R.drawable.deviceregister,R.drawable.login,
							R.drawable.heartrate,R.drawable.devicelist,R.drawable.dataalter,R.drawable.bloodsugar,
							R.drawable.dataupload,R.drawable.carecall};
	//��ҳ����9�����ܵ�����
	private String[] names_background= {"Ѫѹ","ע���豸","��½","����","�豸����","��Ϣ����","Ѫ��","���ݽ���","����໤"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gv_frame=(GridView) findViewById(R.id.gv_frame);
		iv_backgroundTop=(ImageView) findViewById(R.id.iv_backgroundTop);
		gv_frame.setAdapter(getAdapter_functionOfBG());
		//���ø������ܵļ����¼�
		gv_frame.setOnItemClickListener(this);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent,1);
	}
	/***************************************************************************************/
	//������ҳ�湦��ͼƬ�����ֵ�adapter
	private ListAdapter getAdapter_functionOfBG() {
		List<HashMap<String, Object>> list =new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<images_background.length;i++) {
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("txt", names_background[i]);
			map.put("img", images_background[i]);
			list.add(map);
		}
		//item_mainbackground��ÿ��gridviewС���ֵĹ���
		SimpleAdapter adapter=new SimpleAdapter(getApplicationContext(), list, R.layout.item_mainbackground, 
				new String[] {"txt","img"}, new int[] {R.id.tv_tvbg,R.id.iv_itembg});
		
		return adapter;
	}
	/***************************************************************************************/
	@Override
	protected void onResume() {
		super.onResume();
		//�ص���ҳ��ʱ���ı���
		setTitle("ҽ�ƹ���ϵͳ");
	}
	@Override
	//positon��9�����ܵ���ţ�0 ~ 8
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("ouyang", "�򿪽��� "+position);//��־��ӡ���
		switch (position) {
		case 0://Ѫѹ����
			Intent bloodpressure=new Intent(MainActivity.this, BloodPressure.class);
			startActivity(bloodpressure);
			setTitle("Ѫѹ");
			break;
		case 1://ע���豸����
			if(LoginMain.login_state==true) {
				Intent deviceregister=new Intent(MainActivity.this, DeviceRegister.class);
				startActivity(deviceregister);
				setTitle("ע���豸");
				}else {
					Toast.makeText(MainActivity.this, "δ���е�½���޷��鿴ע���豸", Toast.LENGTH_SHORT).show();
				}
			break;
		case 2://��½����
			Intent login=new Intent(MainActivity.this, LoginMain.class);
			startActivity(login);
			setTitle("��½");
			break;
		case 3://���ʽ���
			Intent heartrate=new Intent(MainActivity.this, HeartRate.class);
			startActivity(heartrate);
			setTitle("����");
			break;
		case 4://�豸�������
			if(LoginMain.login_state==true) {
				Intent devicelist=new Intent(MainActivity.this, DeviceList.class);
				startActivity(devicelist);
				setTitle("�豸����");
				}else {
					Toast.makeText(MainActivity.this, "δ���е�½���޷������豸", Toast.LENGTH_SHORT).show();
				}
			break;
		case 5://��Ϣ���Ľ���
			if(LoginMain.login_state==true) {
				Intent dataalter=new Intent(MainActivity.this, DataAlter.class);
				startActivity(dataalter);
				setTitle("��Ϣ����");
				}else {
					Toast.makeText(MainActivity.this, "δ���е�½���޷�������Ϣ", Toast.LENGTH_SHORT).show();
				}
			break;
		case 6://Ѫ�ǽ���
			Intent bloodsugar=new Intent(MainActivity.this, BloodSugar.class);
			startActivity(bloodsugar);
			setTitle("Ѫ��");
			break;
		case 7://�����ϴ�����
			if(LoginMain.login_state==true) {
				Intent dataupload=new Intent(MainActivity.this, DataUpload.class);
				startActivity(dataupload);
				setTitle("�����ϴ�");
				}else {
					Toast.makeText(MainActivity.this, "δ���е�½���޷��ϴ�����", Toast.LENGTH_SHORT).show();
				}
			break;
		case 8://����໤����
			if(LoginMain.login_state==true) {
				Intent carecall=new Intent(MainActivity.this, CareCall.class);
				startActivity(carecall);
				setTitle("����໤");
				}else {
					Toast.makeText(MainActivity.this, "δ���е�½���޷�����໤", Toast.LENGTH_SHORT).show();
				}
			break;

		}
	}
}
