package com.medical.managesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/*
**��ʾ�Ѿ�ע����豸����ListView��ʾ����
*/
public class DeviceList extends Activity{
	private ListView lv_devicelist;
	private Button btn_refreshDeviceList;
	private TextView tv_content;
	private List<String> bluetoothDevices = new ArrayList<String>();
	private List<String> deviceInlist = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	private Set<BluetoothDevice> pairedDevices;
	private BluetoothAdapter mBluetoothAdapter;
	private MyApplicationPatientData mAppPatientInfo;
	
	private String sensorID;
	private String sensorProperty;
	//�洢��getPatientSensor�еõ��Ĵ�����ID�Լ�����
	public String allsensorName = new String();//���ȫ��sensorID
	public static String[] singlesensorName = new String[5];
	public String allproperties = new String();//��ö���Զ��ż�����Ĵ��������ԣ�bp,hr,bs��
	public static String[] singleproperty = new String[5];//����������5����property
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devicelist);
		lv_devicelist=(ListView) findViewById(R.id.lv_devicelist);
		btn_refreshDeviceList=(Button) findViewById(R.id.btn_refreshDeviceList);
		tv_content=(TextView) findViewById(R.id.tv_content);
		//��ȡ����Ե������豸
		//�鿴��ע�ᴫ������Ϣ
		new Thread(new getPatientInfo()).start();
		//ע�������Ĳ˵�
		this.registerForContextMenu(lv_devicelist);
	}
	public void onClickRefreshDeviceLsit(View view) {
		new Thread(new getPatientInfo()).start();
	}
	
	
	private class getPatientInfo implements Runnable{
		@Override
		public void run() {
			StringBuilder url = new StringBuilder();
			//url.append("http://192.168.1.103:8080/WiseMedi/GetPatientSensor");
			url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/GetPatientSensor");
			url.append("?patient.id="+mAppPatientInfo.getPatientID());
			String urlConnect = url.toString();
			StringBuffer buffer=new StringBuffer();
			try {
				//��װ��URL����
				URL url2=new URL(urlConnect);
				HttpURLConnection conn = (HttpURLConnection)url2.openConnection();
				conn.setRequestMethod("POST");
				//��ȡ״̬��
				int code = conn.getResponseCode();
				//conn.getResponseCode();
				Log.d("ouyang", ""+code);
				if(code==200) {//״̬��Ϊ200��������ɹ�
					//��ȡ��Ӧ��Ϣ��ʵ������
					InputStreamReader reader=new InputStreamReader(conn.getInputStream(),"UTF-8");
					char[] charArr=new char[1024*8];
					int len=0;
					while((len=reader.read(charArr)) != -1) {
						//�ַ�����ת�ַ���
						String str=new String(charArr,0,len);
						//��edittext��β׷���ַ���
						buffer.append(str);
					}
				}
			}  catch (IOException e) {
				Toast.makeText(DeviceList.this, "��������Ӧ��������������", Toast.LENGTH_SHORT).show();
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
			/*-----------------------------------------------------------------------------------------*/
			
			/*-----------------------------------------------------------------------------------------*/
			if(respond.toString().contains("does't has any sensor")){
				Toast.makeText(DeviceList.this, "���û�û���κδ����豸���鿴ʧ��", Toast.LENGTH_SHORT).show();
				finish();
			}else {
				Toast.makeText(getApplicationContext(), "ˢ���豸�б�ɹ�", Toast.LENGTH_SHORT).show();
				allsensorName = new String(respond.substring(0, respond.indexOf(";")));//���ȫ��sensorID
				singlesensorName = new String[5];
				singlesensorName = allsensorName.split(",");
				allproperties = new String(respond.substring(respond.indexOf(";")+1));//��ö���Զ��ż�����Ĵ��������ԣ�bp,hr,bs��
				singleproperty = new String[5];//����������5����property
				singleproperty = allproperties.split(",");//�洢ÿ������
				deviceInlist.clear();
				for(int i=0;i<singlesensorName.length;i++) {
					deviceInlist.add(singlesensorName[i]+": "+singleproperty[i]);
				}
				arrayAdapter = new ArrayAdapter<>(DeviceList.this, android.R.layout.simple_list_item_1, android.R.id.text1, deviceInlist);
				lv_devicelist.setAdapter(arrayAdapter);
				lv_devicelist.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("����");
						menu.add(0, 0, Menu.NONE, "ɾ��");
					}
				});
				
				
			}
		};
	};
	/*
	 * Ŀǰֻ��ɾ��SOS���ݿ�Ĵ�����������ɾ��wisemedi���ݿ������Ϣ�еĴ��������ݣ�û���ṩɾ���ķ�����
	 * ��ʵҲ�ܹ�ɾ����������getPatientSensorInfo�󣬰�ɾ�������Ϣ���¸��µ�wisemedi��
	 * */
	
	private class deleteSensorWisemediThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	/*��֯ɾ��������
	 * ��singlesensorName[]�еĵ�i��Ԫ����Ϊnull�������Ԫ����ǰ�ƣ����һ��Ԫ�ر�Ϊnull*/
	private String deleteMsgWisemedi(String msg) {
		String str = new String();
		String tmp_sensorID = new String();
		String tmp_sensorProperty = new String();
		int m=0;
		for(int i=0;i<singlesensorName.length;i++) {
			if(singlesensorName[i].equals(tmp_sensorID)) {
				singlesensorName[i] = null;
				singleproperty[i]=null;
				for(m=i;m<singlesensorName.length;m++) {
					singlesensorName[m]=singlesensorName[m+1];
					singleproperty[m]=singleproperty[m+1];
					if(m==singlesensorName.length-1) {//���һ������
						singlesensorName[m]=null;
					}
				}
			}
		}
		
		
		return str;
	}
	private String findSensorProperty(String sensorID) {
		String property = new String();
		for(int i=0;i<singlesensorName.length;i++) {
			if(singlesensorName[i].equals(sensorID)) {
				//��Ϊ������ID��property��˳���Ӧ��
				property = singleproperty[i];
			}
		}
		return property;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();  
		int lvId=menuInfo.position;
		String str = (String) lv_devicelist.getItemAtPosition(lvId);
		//�õ�ѡ�������ID
		sensorID = singlesensorName[lvId];
		sensorProperty = findSensorProperty(sensorID);
		switch (item.getItemId()) {
		case 0:
			Log.d("delete", sensorID+"");
			new Thread(new deleteSensor52nThread()).start();
			break;

		}
		return super.onContextItemSelected(item);
	}
	private class deleteSensor52nThread implements Runnable{
		@Override
		public void run() {
			//String msg = getMsg(sensorID, new String[] {"int","float"});
			String msg = getMsg(sensorID);
			try {
				//URL url = new URL("http://192.168.1.103:8080/52n-sos-webapp/sos/soap");
				URL url = new URL("http://"+getString(R.string.ipAddress)+":8080/52n-sos-webapp/sos/soap");
				URLConnection conn = null;
				try {
					conn = url.openConnection();
					conn.setRequestProperty("connection", "keep-alive");
					conn.setRequestProperty("accept", "application/json");
					conn.setRequestProperty("Content-type", "application/json");
					conn.setRequestProperty("Accept-Charset","UTF-8");
			        conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.8");
			        conn.setRequestProperty("user-agent",
			                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			        conn.setDoInput(true);		
					conn.setDoOutput(true);
					if (Build.VERSION.SDK != null  
							&& Build.VERSION.SDK_INT > 13) {  
							conn.setRequestProperty("Connection", "close");  
							}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}
				OutputStreamWriter writer = null;
				//��ߵ�GBK����Ǻ�����
//				writer = new OutputStreamWriter(conn.getOutputStream(),"GBK");
				try {
					writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}
				try {
					writer.write(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}
//				System.out.println(msg);
				try {
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}

				String line = null;
				List<String> info = new ArrayList<String>();
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}
				try {
					while ((line = reader.readLine()) != null) {
						info.add(line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
					finish();
				}
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("connecting", e.toString());
				}
				StringBuffer sb = new StringBuffer();
				if (info != null) {
					for (int i = 0; i < info.size(); i++) {
						sb.append(info.get(i));
						sb.append("\n");
					}
					
				}
				if (sb.toString().contains("Exception")) {
					System.out.println(sb.toString());
					Toast.makeText(getApplicationContext(), "����ʧ�ܣ��������Ѵ��ڻ�����ԭ��", Toast.LENGTH_SHORT).show();
				
				}
				Message message = new Message();
				message.obj = sb.toString();
				handler_52n.sendMessage(message);
				//System.out.println(sb.toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.d("connecting", e.toString());
			}
		}
	}
	public Handler handler_52n = new Handler() {
		public void handleMessage(Message msg) {
			String str = (String)msg.obj;
			tv_content.setText(str);
		};
	};
	private String getMsg(String sensorID) {
		String str = new String();
		str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<env:Envelope\r\n" + 
				"    xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"\r\n" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2003/05/soap-envelope http://www.w3.org/2003/05/soap-envelope/soap-envelope.xsd\">\r\n" + 
				"    <env:Body>\r\n" + 
				"        <swes:DeleteSensor\r\n" + 
				"            xmlns:swes=\"http://www.opengis.net/swes/2.0\" service=\"SOS\" version=\"2.0.0\" xsi:schemaLocation=\"http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swes.xsd\">\r\n" + 
				"            <swes:procedure>"+sensorID+"</swes:procedure>\r\n" + 
				"        </swes:DeleteSensor>\r\n" + 
				"    </env:Body>\r\n" + 
				"</env:Envelope>";
		return str;
	}
	
}
