package com.medical.managesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.medical.senddata.InsertSensor2;
import com.medical.senddata.InsertSensor2Request;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/*
**设备注册页面，显示当前连接的传感器设备的名称，以及使用按钮点击将传感器设备注册到服务器中
*/
public class DeviceRegister extends Activity implements OnItemClickListener {
	static boolean isDiscovered;
	private BluetoothAdapter mBluetoothAdapter;
	
	private AcceptThread acceptThread;
	private AcceptThread2 acceptThread2;
	private AcceptThread3 acceptThread3;
	
	private InsertSensor3 insertThread;
	private InsertSensor3 insertThread2;
	private InsertSensor3 insertThread3;
	
	private final UUID MY_UUID = UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c");//和客户端相同的UUID
	private final UUID MY_UUID2 = UUID.fromString("a65c8f21-ac27-5d8c-b687-52e3ac876bd1");//第二个客户端的UUID
	private final UUID MY_UUID3 = UUID.fromString("71c3da2c-b1c4-e68d-91cd-c214ba87ef24");//第三个客户端的UUID
	private final String NAME = "Bluetooth_Socket";
	private TextView tv_deviceconnected;
	private TextView tv_content;
	public static boolean[] connect_state= {false,false,false};
	//三个Socket
	private BluetoothServerSocket serverSocket;
	private BluetoothSocket socket;
	private BluetoothServerSocket serverSocket2;
	private BluetoothSocket socket2;
	private BluetoothServerSocket serverSocket3;
	private BluetoothSocket socket3;
	//三个输入流
	private InputStream is;
	private InputStream is2;
	private InputStream is3;
	
	private float data_floatType;
	private float data_floatType2;
	private float data_floatType3;
	
	//继承Application类，用于将数据存储入全局变量
	private MyApplicationDataSave mApp;//血压数据储存
	private MyApplicationDataSave2 mApp2;//心率数据储存
	private MyApplicationDataSave3 mApp3;//血糖数据储存
	
	private ListView lv_deviceregister;
	private List<String> bluetoothDevices = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	
	private static int itemcount = 0;
	public static String[] deviceconnected = new String[3];
	private static String sensorID;
	private static String[] properties = null;
	
	public String allsensorId;
	public static String[] singlesensorId = new String[10];
	public String allproperties;
	public static String[] singleproperties;
	private String newSensorIDs;
	private String newSensorProperties;
	private updateWiseMediThread udThread;
	private getPatientSensorinfoThread getinfothread;
	private String currentSensorID;
	
	private boolean updateOrNot=false;//标志是否需要信息更新
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceregister);
		Log.d("ouyang", "onCreate");
		tv_deviceconnected=(TextView) findViewById(R.id.tv_deviceconnected);
		lv_deviceregister=(ListView) findViewById(R.id.lv_deviceregister);
		tv_content = (TextView) findViewById(R.id.tv_content);
		//创建MyApplicationDataSave类对象
		mApp = new MyApplicationDataSave();
		mApp2 = new MyApplicationDataSave2();
		mApp3 = new MyApplicationDataSave3();
		//开启蓝牙
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//初始化线程类并开启
		
		if (!mBluetoothAdapter.isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent,1);
			//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//startActivityForResult(enableIntent, 3);
		}
		
		//开启获取用户信息线程
		new Thread(new getPatientSensorinfoThread()).start();
		
		//开启接收数据线程
		acceptThread = new AcceptThread();
		acceptThread.start();
		acceptThread2 = new AcceptThread2();
		acceptThread2.start();
		acceptThread3 = new AcceptThread3();
		acceptThread3.start();
		
		
		arrayAdapter = new ArrayAdapter<String>(DeviceRegister.this, android.R.layout.simple_list_item_1, android.R.id.text1, bluetoothDevices);
		lv_deviceregister.setAdapter(arrayAdapter);
		lv_deviceregister.setOnItemClickListener(this);
    }
	//初始化
	@Override
	public void onStart() {
		super.onStart();
		Log.d("ouyang", "onStart");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("ouyang", "onResume");
		//avtivity重新可视时加载当前已经连接的传感器设备
		if(connect_state[0]) {
				bluetoothDevices.add(MyApplicationDataSave.getDeviceName());
				lv_deviceregister.setAdapter(arrayAdapter);
		}
		if(connect_state[1]) {
				bluetoothDevices.add(MyApplicationDataSave2.getData(0));
				lv_deviceregister.setAdapter(arrayAdapter);
		}
		if(connect_state[2]) {
				bluetoothDevices.add(MyApplicationDataSave3.getData(0));
				lv_deviceregister.setAdapter(arrayAdapter);
		}
	}
	//程序关闭后
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
		}
	}
	//定义服务端线程类1
		private Handler handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Toast.makeText(getApplicationContext(), "来自传感器1（血压）的蓝牙信息正在接收", Toast.LENGTH_SHORT).show();
				
				//tv_content.append(String.valueOf(msg.obj));
				if(isNumeric(String.valueOf(msg.obj))) {
					//接收回来的是一个obj类型的数据，将其转成float类型后再进行储存
					data_floatType=Float.parseFloat(String.valueOf(msg.obj));
					Log.d("receive", ""+data_floatType);
					//将接收到的数据data_floatType存入全局变量MyApplicationDataSave.datalist[BloodPressure.data_i]里面
					mApp.setData2(data_floatType, BloodPressure.data_i);
					Log.d("DeviceRegisterData", ""+MyApplicationDataSave.getData(BloodPressure.data_i));
					//存储完一次以后，数组序号data_i自行加1
					Log.d("bp_data_i", ""+BloodPressure.data_i);
					BloodPressure.data_i = BloodPressure.data_i+1;
					Log.d("isNumeric", "succeed");
					//addEntry();
				}else {
					//发送过来的第一个数据是设备的名称，所以要单独存于一个变量，和数据分开
						bluetoothDevices.add(String.valueOf(msg.obj));
						itemcount = itemcount + 1;
						arrayAdapter.notifyDataSetChanged();
						MyApplicationDataSave.setDeviceName(String.valueOf(msg.obj));
						deviceconnected[0]=mApp.getDeviceName();
						Log.d("isNumeric", "false");
						//标志第一个设备已经连接
						connect_state[0] = true;
				}
			};
		};
		//判断是否为数字
		//用JAVA自带的函数
		public static boolean isNumeric(String str){
		   for (int i = str.length();--i>=0;){  
		       if (!Character.isDigit(str.charAt(i))){
		           return false;
		       }
		   }
		   return true;
		}
		//服务端监听客户端的线程类1
		private class AcceptThread extends Thread {
		    public AcceptThread() {
		        try {
		        	//使用Insecure的方式listen
		            //serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
		            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
		        } catch (Exception e) {
		        	Log.d("ouyang", "listen() failed 1"+e.toString());
		        }
		    }
		    public void run() {
		        try {
		            socket = serverSocket.accept();
		            is = socket.getInputStream();
		            Log.d("ouyang", "Thread1 running");
		            while(true) {
		                byte[] buffer =new byte[1024];
		                int count = is.read(buffer);
		                Message msg = new Message();
		                msg.obj = new String(buffer, 0, count, "utf-8");
		                handler1.sendMessage(msg);
		            }
		        }
		        catch (Exception e) {
		        	Log.e("ouyang", "connected failed 1"+e.toString());
		        }
		    }
	}
	/******************************************************************/
		//定义服务端线程类2
		private Handler handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Toast.makeText(getApplicationContext(), "来自传感器2（心率）的蓝牙信息正在接收", Toast.LENGTH_SHORT).show();
				
				//tv_content.append(String.valueOf(msg.obj));
				if(isNumeric(String.valueOf(msg.obj))) {
					//接收回来的是一个obj类型的数据，将其转成float类型后再进行储存
					data_floatType2=Float.parseFloat(String.valueOf(msg.obj));
					Log.d("receive", ""+data_floatType2);
					//将接收到的数据data_floatType存入全局变量MyApplicationDataSave.datalist[BloodPressure.data_i]里面
					mApp2.setData2(data_floatType2, HeartRate.data_i);
					Log.d("DeviceRegisterData", ""+MyApplicationDataSave2.getData(HeartRate.data_i));
					//存储完一次以后，数组序号data_i自行加1
					Log.d("bp_data_i", ""+HeartRate.data_i);
					HeartRate.data_i = HeartRate.data_i+1;
					Log.d("isNumeric", "succeed");
					//addEntry();
				}else {
						bluetoothDevices.add(String.valueOf(msg.obj));
						itemcount = itemcount + 1;
						lv_deviceregister.setAdapter(arrayAdapter);
						arrayAdapter.notifyDataSetChanged();
						MyApplicationDataSave2.setDeviceName(String.valueOf(msg.obj));
						deviceconnected[1]=mApp2.getDeviceName();
						Log.d("isNumeric", "false");
						//标志第二个设备已经连接
						connect_state[1] = true;
				}
			};
		};

		//服务端监听客户端的线程类2
		private class AcceptThread2 extends Thread {
		    public AcceptThread2() {
		        try {
		        	//使用Insecure的方式listen
		            //serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
		            serverSocket2 = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID2);
		        } catch (Exception e) {
		        	Log.d("ouyang", "listen() failed 2"+e.toString());
		        	
		        }
		    }
		    public void run() {
		        try {
		            socket2 = serverSocket2.accept();
		            is2 = socket2.getInputStream();
		            Log.d("ouyang", "Thread2 running");
		            while(true) {
		                byte[] buffer =new byte[1024];
		                int count = is2.read(buffer);
		                Message msg = new Message();
		                msg.obj = new String(buffer, 0, count, "utf-8");
		                handler2.sendMessage(msg);
		            }
		        }
		        catch (Exception e) {
		        	Log.e("ouyang", "connected failed 2"+e.toString());
		        }
		    }
	}
	/******************************************************************/
		//定义服务端线程类3
		private Handler handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Toast.makeText(getApplicationContext(), "来自传感器3（血糖）的蓝牙信息正在接收", Toast.LENGTH_SHORT).show();
				
				//tv_content.append(String.valueOf(msg.obj));
				if(isNumeric(String.valueOf(msg.obj))) {
					//接收回来的是一个obj类型的数据，将其转成float类型后再进行储存
					data_floatType3=Float.parseFloat(String.valueOf(msg.obj));
					Log.d("receive", ""+data_floatType3);
					//将接收到的数据data_floatType存入全局变量MyApplicationDataSave.datalist[BloodPressure.data_i]里面
					mApp3.setData2(data_floatType3, BloodSugar.data_i);
					Log.d("DeviceRegisterData", ""+MyApplicationDataSave3.getData(BloodSugar.data_i));
					//存储完一次以后，数组序号data_i自行加1
					Log.d("bp_data_i", ""+BloodSugar.data_i);
					BloodSugar.data_i = BloodSugar.data_i+1;
					Log.d("isNumeric", "succeed");
					//addEntry();
				}else {
						bluetoothDevices.add(String.valueOf(msg.obj));
						itemcount = itemcount + 1;
						arrayAdapter.notifyDataSetChanged();
						MyApplicationDataSave3.setDeviceName(String.valueOf(msg.obj));
						deviceconnected[2]=mApp3.getDeviceName();
						Log.d("isNumeric", "false");
						//标志第三个设备已经连接
						connect_state[2] = true;
				}
			};
		};
		//服务端监听客户端的线程类3
		private class AcceptThread3 extends Thread {
		    public AcceptThread3() {
		        try {
		        	//使用Insecure的方式listen
		            //serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
		            serverSocket3 = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID3);
		        } catch (Exception e) {
		        	Log.d("ouyang", "listen() failed 3"+e.toString());
		        }
		    }
		    public void run() {
		        try {
		            socket3 = serverSocket3.accept();
		            is3 = socket3.getInputStream();
		            Log.d("ouyang", "Thread3 running");
		            while(true) {
		                byte[] buffer =new byte[1024];
		                int count = is3.read(buffer);
		                Message msg = new Message();
		                msg.obj = new String(buffer, 0, count, "utf-8");
		                handler3.sendMessage(msg);
		            }
		        }
		        catch (Exception e) {
		        	Log.e("ouyang", "connected failed 3"+e.toString());
		        }
		    }
	}
	/******************************************************************/
		
		//刷新ListView
		public void onClick_DeviceRegisterRefresh(View view) {
			new Thread(new getPatientSensorinfoThread()).start();
		}
		//注册当前连接的传感器
		public void onClick_DeviceRegister(View view) {
			
		}
		//点击事件
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d("updateOrNot", updateOrNot+"");
			currentSensorID = (String) lv_deviceregister.getItemAtPosition(position);
			
			new Thread(new getPatientSensorinfoThread()).start();
			final EditText et = new EditText(this);
			if(updateOrNot==false) {
				Toast.makeText(DeviceRegister.this, "此传感器名称已存在，无法注册", Toast.LENGTH_SHORT).show();
			}else {
				Log.d("ouyang", updateOrNot+"");
				AlertDialog abc = new AlertDialog.Builder(this).setTitle("提示")
						.setView(et)
						.setMessage("请输入此传感器的属性")
						.setNegativeButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(allsensorId==null) {
									//若此时传感器信息为空
									newSensorIDs = currentSensorID;
									newSensorProperties = et.getText().toString();
								}else {
									newSensorProperties = allproperties +","+et.getText().toString();
									newSensorIDs = allsensorId +","+currentSensorID;
								}
								if(updateOrNot==true) {
									udThread = new updateWiseMediThread();
									udThread.start();
									insertThread = new InsertSensor3(currentSensorID,new String[]{et.getText().toString()});
									insertThread.start();
									
									new Thread(new getPatientSensorinfoThread()).start();
								}else {
									Toast.makeText(DeviceRegister.this, "此传感器已存在", Toast.LENGTH_SHORT).show();
								}
								
							}
						}).create();
				if(updateOrNot==true) {
					abc.show();
				}
			}
			
		}
		
		//////////////////////////////////////////////
		public class InsertSensor3 extends Thread{
			//构造函数会在创建线程时首先使用
			public String sensorID=new String();
			public String[] properties=new String[3];
			public InsertSensor3(String sensorID, String[] properties) {
				this.sensorID = sensorID;
				this.properties = properties;
			}
				@Override
				public void run() {
					//String msg = getMsg(sensorID, new String[] {"int","float"});
					String msg = getMsg(sensorID, new String[] {properties[0]});
					super.run();
					try {
						//URL url = new URL("http://localhost:8080/52n-sos-webapp/sos/soap");
						//在eclipse中运行时，要将localhost用本机IP地址代替
						//URL url = new URL("http://www.baidu.com");
						URL url = new URL("http://"+getString(R.string.ipAddress)+":8080/52n-sos-webapp/sos/soap");
						//URL url = new URL("http://192.168.1.103:8080/52n-sos-webapp/sos/soap");
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
						//这边的GBK真的是很神奇
//						writer = new OutputStreamWriter(conn.getOutputStream(),"GBK");
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
//						System.out.println(msg);
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
							Toast.makeText(getApplicationContext(), "请求失败，传感器已存在或其他原因", Toast.LENGTH_SHORT).show();
							if(sb.toString().contains(deviceconnected[0])) {insertThread.interrupt();}
							if(sb.toString().contains(deviceconnected[1])) {insertThread2.interrupt();}
							if(sb.toString().contains(deviceconnected[2])) {insertThread3.interrupt();}
							////////还需要做异常处理。现在是如果请求失败，整个程序会崩溃
							
						}
						Message message = new Message();
						message.obj = sb.toString();
						handlerURL.sendMessage(message);
						//System.out.println(sb.toString());
					} catch (MalformedURLException e) {
						e.printStackTrace();
						Log.d("connecting", e.toString());
					}
				}
			
			public Handler handlerURL = new Handler() {
				public void handleMessage(android.os.Message msg) {
					tv_content.setText(String.valueOf(msg.obj));
				};
			};
			/*插入传感器xml代码
			*@param sensorID 
			*@param properties[]
			*拼接并得到需要发送的请求字符串msg
			*/
			public String getMsg(String sensorID,String[] properties) {
				String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
						"<env:Envelope\r\n" + 
						"    xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"\r\n" + 
						"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2003/05/soap-envelope http://www.w3.org/2003/05/soap-envelope/soap-envelope.xsd\">\r\n" + 
						"    <env:Body>\r\n" + 
						"        <swes:InsertSensor\r\n" + 
						"            xmlns:swes=\"http://www.opengis.net/swes/2.0\"\r\n" + 
						"            xmlns:sos=\"http://www.opengis.net/sos/2.0\"\r\n" + 
						"            xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"\r\n" + 
						"            xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\"\r\n" + 
						"            xmlns:gml=\"http://www.opengis.net/gml\"\r\n" + 
						"            xmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n" + 
						"            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" service=\"SOS\" version=\"2.0.0\" xsi:schemaLocation=\"http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sosInsertSensor.xsd      http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swes.xsd\">\r\n" + 
						"            <swes:procedureDescriptionFormat>http://www.opengis.net/sensorML/1.0.1</swes:procedureDescriptionFormat>\r\n" + 
						"            <swes:procedureDescription>\r\n" + 
						"                <sml:SensorML version=\"1.0.1\">\r\n" + 
						"                    <sml:member>\r\n" + 
						"                        <sml:System>\r\n" + 
						"                            <!-- optional; generated if not present -->\r\n" + 
						"                            <sml:identification>\r\n" + 
						"                                <sml:IdentifierList>\r\n" + 
						"                                    <sml:identifier name=\"uniqueID\">\r\n" + 
						"                                        <sml:Term definition=\"urn:ogc:def:identifier:OGC:1.0:uniqueID\">\r\n" + 
						"                                            <sml:value>"+sensorID+"\r\n" + //这里是procedure
						"											</sml:value>\r\n" + 
						"                                        </sml:Term>\r\n" + 
						"                                    </sml:identifier>\r\n" + 
						"                                    <sml:identifier name=\"longName\">\r\n" + 
						"                                        <sml:Term definition=\"urn:ogc:def:identifier:OGC:1.0:longName\">\r\n" + 
						"                                            <sml:value>52°North Initiative for Geospatial Open Source Software GmbH (http://52north.org)</sml:value>\r\n" + 
						"                                        </sml:Term>\r\n" + 
						"                                    </sml:identifier>\r\n" + 
						"                                    <sml:identifier name=\"shortName\">\r\n" + 
						"                                        <sml:Term definition=\"urn:ogc:def:identifier:OGC:1.0:shortName\">\r\n" + 
						"                                            <sml:value>52°North GmbH</sml:value>\r\n" + 
						"                                        </sml:Term>\r\n" + 
						"                                    </sml:identifier>\r\n" + 
						"                                </sml:IdentifierList>\r\n" + 
						"                            </sml:identification>\r\n" + 
						"                            <sml:capabilities name=\"offerings\">\r\n" + 
						"                                <!-- Special capabilities used to specify offerings. -->\r\n" + 
						"                                <!-- Parsed and removed during InsertSensor/UpdateSensorDescription, added during DescribeSensor. -->\r\n" + 
						"                                <!-- Offering is generated if not specified. -->\r\n" + 
						"                                <swe:SimpleDataRecord>\r\n" + 
						"                                    <!-- Field name is used for the offering's name -->\r\n" + 
						"                                    <swe:field name=\"Offering for sensor "+sensorID+"\">\r\n" + 
						"                                        <swe:Text definition=\"urn:ogc:def:identifier:OGC:offeringID\">\r\n" + 
						"                                            <swe:value>http://www.52north.org/test/offering/"+sensorID+"</swe:value>\r\n" + //这里是offering
						"                                        </swe:Text>\r\n" + 
						"                                    </swe:field>\r\n" + 
						"                                </swe:SimpleDataRecord>\r\n" + 
						"                            </sml:capabilities>\r\n" + 
						"                            <sml:capabilities name=\"parentProcedures\">\r\n" + 
						"                                <!-- Special capabilities used to specify parent procedures. -->\r\n" + 
						"                                <!-- Parsed and removed during InsertSensor/UpdateSensorDescription, added during DescribeSensor. -->\r\n" + 
						"                                <swe:SimpleDataRecord>\r\n" + 
						"                                    <swe:field name=\"parentProcedure\">\r\n" + 
						"                                        <swe:Text>\r\n" + 
						"                                            <swe:value>http://www.52north.org/test/procedure/1</swe:value>\r\n" + 
						"                                        </swe:Text>\r\n" + 
						"                                    </swe:field>\r\n" + 
						"                                </swe:SimpleDataRecord>\r\n" + 
						"                            </sml:capabilities>\r\n" + 
						"                            <sml:capabilities name=\"featuresOfInterest\">\r\n" + 
						"                                <!-- Special capabilities used to specify features of interest. -->\r\n" + 
						"                                <!-- Parsed and removed during InsertSensor/UpdateSensorDescription, added during DescribeSensor. -->\r\n" + 
						"                                <swe:SimpleDataRecord>\r\n" + 
						"                                    <swe:field name=\"featureOfInterestID\">\r\n" + 
						"                                        <swe:Text>\r\n" + 
						"                                            <swe:value>http://www.52north.org/test/featureOfInterest/9</swe:value>\r\n" + 
						"                                        </swe:Text>\r\n" + 
						"                                    </swe:field>\r\n" + 
						"                                </swe:SimpleDataRecord>\r\n" + 
						"                            </sml:capabilities>\r\n" + 
						"                            <sml:position name=\"sensorPosition\">\r\n" + 
						"                                <swe:Position referenceFrame=\"urn:ogc:def:crs:EPSG::4326\">\r\n" + 
						"                                    <swe:location>\r\n" + 
						"                                        <swe:Vector gml:id=\"STATION_LOCATION\">\r\n" + 
						"                                            <swe:coordinate name=\"easting\">\r\n" + 
						"                                                <swe:Quantity axisID=\"x\">\r\n" + 
						"                                                    <swe:uom code=\"degree\"/>\r\n" + 
						"                                                    <swe:value>7.651968812254194</swe:value>\r\n" + 
						"                                                </swe:Quantity>\r\n" + 
						"                                            </swe:coordinate>\r\n" + 
						"                                            <swe:coordinate name=\"northing\">\r\n" + 
						"                                                <swe:Quantity axisID=\"y\">\r\n" + 
						"                                                    <swe:uom code=\"degree\"/>\r\n" + 
						"                                                    <swe:value>51.935101100104916</swe:value>\r\n" + 
						"                                                </swe:Quantity>\r\n" + 
						"                                            </swe:coordinate>\r\n" + 
						"                                            <swe:coordinate name=\"altitude\">\r\n" + 
						"                                                <swe:Quantity axisID=\"z\">\r\n" + 
						"                                                    <swe:uom code=\"m\"/>\r\n" + 
						"                                                    <swe:value>52.0</swe:value>\r\n" + 
						"                                                </swe:Quantity>\r\n" + 
						"                                            </swe:coordinate>\r\n" + 
						"                                        </swe:Vector>\r\n" + 
						"                                    </swe:location>\r\n" + 
						"                                </swe:Position>\r\n" + 
						"                            </sml:position>\r\n" + 
						"                            <sml:inputs>\r\n" + 
						"                                <sml:InputList>\r\n" + 
						"                                    <sml:input name=\"test_observable_property_9\">\r\n" + 
						"                                        <swe:ObservableProperty definition=\"http://www.52north.org/test/observableProperty/9\"/>\r\n" + 
						"                                    </sml:input>\r\n" + 
						"                                </sml:InputList>\r\n" + 
						"                            </sml:inputs>\r\n" + 
						"                            <sml:outputs>\r\n" + 
						"                                <sml:OutputList>\r\n" + 
						"                                    <sml:output name=\"test_observable_property_9_1\">\r\n" + 
						"                                        <swe:Category definition=\"http://www.52north.org/test/observableProperty/9_1\">\r\n" + 
						"                                            <swe:codeSpace xlink:href=\"NOT_DEFINED\"/>\r\n" + 
						"                                        </swe:Category>\r\n" + 
						"                                    </sml:output>\r\n" + 
						"                                    <sml:output name=\"test_observable_property_9_2\">\r\n" + 
						"                                        <swe:Count definition=\"http://www.52north.org/test/observableProperty/9_2\"/>\r\n" + 
						"                                    </sml:output>\r\n" + 
						"                                    <sml:output name=\"test_observable_property_9_3\">\r\n" + 
						"                                        <swe:Quantity definition=\""+properties[0]+"\">\r\n" + 
						"                                            <swe:uom code=\"NOT_DEFINED\"/>\r\n" + 
						"                                        </swe:Quantity>\r\n" + 
						"                                    </sml:output>\r\n" + 
						"                                    <sml:output name=\"test_observable_property_9_4\">\r\n" + 
						"                                        <swe:Text definition=\"http://www.52north.org/test/observableProperty/9_4\"/>\r\n" + 
						"                                    </sml:output>\r\n" + 
						"                                    <sml:output name=\"test_observable_property_9_5\">\r\n" + 
						"                                        <swe:Boolean definition=\"http://www.52north.org/test/observableProperty/9_5\"/>\r\n" + 
						"                                    </sml:output>\r\n" + 
						"                                </sml:OutputList>\r\n" + 
						"                            </sml:outputs>\r\n" + 
						"                        </sml:System>\r\n" + 
						"                    </sml:member>\r\n" + 
						"                </sml:SensorML>\r\n" + 
						"            </swes:procedureDescription>\r\n" + 
						"            <!-- multiple values possible -->\r\n" + 
						"            <swes:observableProperty>http://www.52north.org/test/observableProperty/9_1</swes:observableProperty>\r\n" + 
						"            <swes:observableProperty>"+	properties[0] 	+                          "</swes:observableProperty>\r\n"	+ //这里是property
						"            <swes:observableProperty>http://www.52north.org/test/observableProperty/9_3</swes:observableProperty>\r\n" + 
						"            <swes:observableProperty>http://www.52north.org/test/observableProperty/9_4</swes:observableProperty>\r\n" + 
						"            <swes:observableProperty>http://www.52north.org/test/observableProperty/9_5</swes:observableProperty>\r\n" + 
						"            <swes:observableProperty>http://www.52north.org/test/observableProperty/9_6</swes:observableProperty>\r\n" + 
						"            <swes:metadata>\r\n" + 
						"                <sos:SosInsertionMetadata>\r\n" + 
						"                    <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement</sos:observationType>\r\n" + 
						"                    <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation</sos:observationType>\r\n" + 
						"                    <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation</sos:observationType>\r\n" + 
						"                    <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation</sos:observationType>\r\n" + 
						"                    <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation</sos:observationType>\r\n" + 
						"                    <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation</sos:observationType>\r\n" + 
						"                    <!-- multiple values possible -->\r\n" + 
						"                    <sos:featureOfInterestType>http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint</sos:featureOfInterestType>\r\n" + 
						"                </sos:SosInsertionMetadata>\r\n" + 
						"            </swes:metadata>\r\n" + 
						"        </swes:InsertSensor>\r\n" + 
						"    </env:Body>\r\n" + 
						"</env:Envelope>";
				String URl = "http://localhost:8080/52n-sos-webapp/sos/soap" ;
				return msg;
			}

		}
		
	private class updateWiseMediThread extends Thread{
			
			@Override
			public void run() {
				StringBuilder url = new StringBuilder();
				url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/patientUpdate");
				//url.append("http://192.168.1.103:8080/WiseMedi/patientUpdate");
				url.append("?patient.id="+MyApplicationPatientData.getPatientID());
				//预防参数中出现特殊字符，所以对参数进行编码
				try {
					url.append("&sensorinfo.sensorId="+URLEncoder.encode(newSensorIDs, "utf8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try {
					url.append("&sensorinfo.properties="+URLEncoder.encode(newSensorProperties, "utf8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				String urlConnect = url.toString();
				StringBuffer buffer=new StringBuffer();
				super.run();
					try {
						//封装了URL对象
						URL url2=new URL(urlConnect);
						HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
						conn2.setRequestMethod("POST");
						//获取状态码
						int code = conn2.getResponseCode();
						//conn.getResponseCode();
						Log.d("bh", ""+code);
						if(code==200) {//状态码为200代表请求成功
							//获取响应消息的实体内容
							InputStreamReader reader=new InputStreamReader(conn2.getInputStream(),"UTF-8");
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
					updateWiseMedihandler.sendMessage(returncode);
				}
			}
	
	
		private Handler updateWiseMedihandler = new Handler() {
				public void handleMessage(Message msg) {
					String respond = new String(String.valueOf(msg.obj));
					if(respond.contains("SUCCESS")&&(updateOrNot==true)) {
						Toast.makeText(DeviceRegister.this, "注册传感器成功", Toast.LENGTH_SHORT).show();
						udThread.interrupt();
					}else if(updateOrNot==false){
						
					}
				};
			};
		private class getPatientSensorinfoThread implements Runnable{

				@Override
				public void run() {
					StringBuilder url = new StringBuilder();
					//url.append("http://192.168.1.103:8080/WiseMedi/GetPatientSensor");
					url.append("http://"+getString(R.string.ipAddress)+":8080/WiseMedi/GetPatientSensor");
					url.append("?patient.id="+MyApplicationPatientData.getPatientID());
					
					String urlConnect = url.toString();
					StringBuffer buffer=new StringBuffer();
					
						try {
							//封装了URL对象
							URL url2=new URL(urlConnect);
							HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
							conn2.setRequestMethod("POST");
							//获取状态码
							int code = conn2.getResponseCode();
							//conn.getResponseCode();
							Log.d("bh", ""+code);
							if(code==200) {//状态码为200代表请求成功
								
								//获取响应消息的实体内容
								InputStreamReader reader=new InputStreamReader(conn2.getInputStream(),"UTF-8");
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
						getInfoHandler.sendMessage(returncode);
					}
				
			}
			private Handler getInfoHandler = new Handler() {
				public void handleMessage(Message msg) {
					String returncode = String.valueOf(msg.obj);
					if(returncode.contains("does't has any sensor")) {
						Toast.makeText(DeviceRegister.this, "此用户暂时没有传感设备", Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(DeviceRegister.this, "成功获取传感器信息", Toast.LENGTH_SHORT).show();
						String receive = String.valueOf(msg.obj);
						allsensorId = receive.substring(0, receive.indexOf(";"));
						singlesensorId = allsensorId.split(",");
					
						allproperties = receive.substring(receive.indexOf(";")+1);
						singleproperties = allproperties.split(",");
						dataHandle();//更新updateOrNot的状态
						}
					
				};
			};
			
			private void dataHandle() {
				int count_name=0;
				for(int i=0;i<singlesensorId.length;i++) {
					Log.d("ouyang", singlesensorId[i]);
					if(singlesensorId[i].equals(currentSensorID)) {//此处bp代表的是插入传感器时所输入的传感器id
						//一定要用equal这个方法，因为只是用==判断的是内存地址，euqals用于比较内容是否相等!!!!!
						count_name = count_name+1;//在后面判断，当count_name不等于0时，代表此id已存在于当前账户，那么就不再添加
					}
				}
				for(int i=0;i<singleproperties.length;i++) {
					Log.d("ouyang", singleproperties[i]);
				}
				Log.d("count_name", ""+count_name);
				if(count_name==0) {//确保当前选中了某一项，并且有具体的ID
					//信息库中无此传感器，需要更新信息（插入此传感器）
					updateOrNot = true;
				}else {
					updateOrNot = false;
				}
			}
			private AlertDialog propertyinput() {
				final EditText et = new EditText(this);
				AlertDialog propertyinput = new AlertDialog.Builder(DeviceRegister.this)
						.setTitle("提示")
						.setView(et)
						.setMessage("请输入此传感器的属性值：")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						})
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								newSensorProperties = allproperties +","+et.getText().toString();
								newSensorIDs = allsensorId +",";
							}
						}).create();
				propertyinput.setCanceledOnTouchOutside(false);
				return propertyinput;
	}
			
}


