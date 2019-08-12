package com.medical.managesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.violin.biz.BasicSosClient;
import com.violin.factory.SosClentiFactory;
import com.violin.request.BasicRequest;
import com.violin.request.InsertObservation4_0Request;
import com.violin.response.InsertObservationResponse;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DataUpload extends Activity implements OnItemClickListener{
	private Button btn_upload;
	private TextView tv_ducontent;
	private String SOSurl;
	private ListView lv_dataupload;
	private InsertObservationThread insertObservation1Thread;
	private static double tmp_data=1.23;
	//临时变量
	private String tmpSensorID;
	private String tmpProperty;
	private String[] tmpData =new String[1024];
	private List<String> devicesConnected = new ArrayList<>();
	private ArrayAdapter<String> arrayAdapter;
	
	private static int i=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataupload);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		tv_ducontent = (TextView) findViewById(R.id.tv_ducontent);
		lv_dataupload = (ListView) findViewById(R.id.lv_dataupload);
		arrayAdapter = new ArrayAdapter<>(DataUpload.this, android.R.layout.simple_list_item_1, 
						android.R.id.text1, devicesConnected);
		lv_dataupload.setAdapter(arrayAdapter);
		lv_dataupload.setOnItemClickListener(this);
		
	}
	@Override
	protected void onStart() {
		Log.d("test", "onStart");
		super.onStart();
	}
	@Override
	protected void onResume() {
		Log.d("test", "onResume");
		for(int i=0;i<3;i++) {
			if(DeviceRegister.deviceconnected[i]!=null) {
				devicesConnected.add(DeviceRegister.deviceconnected[i]);
				arrayAdapter.notifyDataSetChanged();
			}
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		Log.d("test", "onPause");
		super.onPause();
	}
	@Override
	protected void onStop() {
		Log.d("test", "onStop");
		super.onStop();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//tmpSensorID = (String) lv_dataupload.getItemAtPosition(position);
		/*因为InsertObservation这个操作中的3个参数必须要严格对照InsertSensor时候设置的参数，但是在插入的时候可能
		 * 无法知道当时注册的时候的参数，所以下列的tmp参数是用来作为测试用途。目前sos数据库中也不能分辨这个传感器
		 * 对应的是哪个property和procedure*/
		//tmpSensorID = "GT-I950067";
		//tmpProperty = new String();
		//tmpProperty = "bloodpressure";
		
		tmpSensorID = (String) lv_dataupload.getItemAtPosition(position);
		findPropertyOfSensor();
		//通过DeviceRegister.deviceconnected[i]决定是上传哪个传感器的数据
		findDataSource();
		
		final Timer timer = new Timer();
			 timer.schedule(new TimerTask(){  
			      public void run(){
			    	  //System.out.println("Time's up!");
			    	  //开启上传数据线程
			    	  new Thread(new InsertObservationThread(tmpSensorID, getTime(), new String[]{tmpProperty}, ""+tmpData[i])).start();
			    	  i = i+1;
			    	  System.out.println(i);
			    	  //上传5次，共5个数据
			    	  if(i==5) {timer.cancel();}
			       }  
			   }, 2*1000,2000);
	}
	
	public void onClickUpload(View view) {
		//insertObservation1Thread = new InsertObservationThread(MyApplicationDataSave.getData(0), getTime(), 
				//new String[]{"bloodpressure"}, MyApplicationDataSave.getData(1));
		//new Thread(new InsertObservationThread("SAMSUNG'S DEVICES", getTime(), new String[]{"int"}, ""+tmp_data)).start(); 
		i = 0;
		final Timer timer = new Timer();
			 timer.schedule(new TimerTask(){  
			      public void run(){
			    	  //System.out.println("Time's up!");
			    	  //开启上传数据线程
			    	  new Thread(new InsertObservationThread("SAMSUNG'S DEVICES", getTime(), new String[]{"int"}, ""+tmpData[i])).start();
			    	  i = i+1;
			    	  System.out.println(i);
			    	  if(i==5) {timer.cancel();}
			       }  
			   }, 2*1000,2000); 
			
	}
	/*插入观测值时需要offering、procedure、property一致才能成功插入
	 * 点击ListView中的项目，将点中的字符串提取出来，执行getPatientSensorInfo线程，将提取的结果
	 * 与线程返回的结果比较，确定是第几个sensor对应的第几个property，将此property单独提出来，用于组建InsertSensor的XML语言
	 * */
	
	
	/*找出点中项目对应的property */
	private void findPropertyOfSensor() {
		for(int i=0;i<DeviceRegister.singlesensorId.length;i++) {
			if(DeviceRegister.singlesensorId[i]!=null) {
			  if(DeviceRegister.singlesensorId[i].equals(tmpSensorID)) {
				tmpProperty = DeviceRegister.singleproperties[i];
			  }
			}
		}
	}
	
	/*找出点中项目对应的数据储存位置 */
	private void findDataSource() {
		int position=0;
		for(int i=0;i<3;i++) {
			if(DeviceRegister.deviceconnected[i]!=null&&
					DeviceRegister.deviceconnected[i].equals(tmpSensorID)) {
				position=i;
			}
		}
		switch (position) {
		case 0:
			tmpData=MyApplicationDataSave.getDatalist();
			break;
		case 1:
			tmpData=MyApplicationDataSave2.getDatalist();
			break;
		case 2:
			tmpData=MyApplicationDataSave3.getDatalist();
			break;
		}
	}
	
	public void InsetObservation4(){
		BasicSosClient client = SosClentiFactory.NewSOSInstacce("http://localhost:8080/52n-sos-webapp/sos", SosClentiFactory.VERSION_4_0,SosClentiFactory.SOAP);
		InsertObservation4_0Request sensor = new InsertObservation4_0Request();
		sensor.setSensorID("Urn:Shu:4R:Temperature:S-1");
		sensor.setProperties(new String[]{"bloodpresure","heartbeat"});
		sensor.setValue(new String[]{"250bpm","45so"});
		InsertObservationResponse res = client.InsertObservation(sensor);
		System.out.println(res.getResult());
		
		Log.d("ouyang", "InsertObservation: "+res.getResult());
		Message msg = new Message();
		msg.obj = res.getResult() ;
		handler.sendMessage(msg);
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_ducontent.setText(String.valueOf(msg.obj));
			Toast.makeText(DataUpload.this, "上传成功", Toast.LENGTH_SHORT).show();
			tmp_data=tmp_data+1;
		};
	};
	
	public class InsertObservationThread implements Runnable{
		private String sensorID;
		private String time;
		private String[] properties = new String[5];
		private String value;
		
		public InsertObservationThread(String sensorID, String time, String[]properties, 
				String value){
			this.sensorID = sensorID;
			this.time = time;
			this.properties = properties;
			this.value = value;
		}
		@Override
		public void run() {
			String msg = InsertObservation(sensorID, time, properties, value);
			try {
				//URL url = new URL("http://localhost:8080/52n-sos-webapp/sos/soap");
				//在eclipse中运行时，要将localhost用本机IP地址代替
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
					
					////////还需要做异常处理。现在是如果请求失败，整个程序会崩溃
					
				}
				Message message = new Message();
				message.obj = sb.toString();
				handler.sendMessage(message);
				//System.out.println(sb.toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("connecting", e.toString());
			}
		}
	}
	
	//得到时间 time 
	public String getTime() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		int millsec = c.get(Calendar.MILLISECOND);
		String time = year + "-" + month + "-" + date + "T" + hour + ":"
				+ minute + ":" + sec + "." + millsec + "+08:00";
		return time;
	}
	
	
	public static String sendToSOS(String msg, String SOSurl) throws Exception {
		URL url = new URL(SOSurl);
		URLConnection conn = null;
		conn = url.openConnection();
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("accept", "application/xml");
		conn.setRequestProperty("Content-type", "application/xml");
		conn.setRequestProperty("Accept-Charset","UTF-8");
        conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.8");
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        // 发送POST请求必须设置如下两行
        conn.setDoInput(true);		
		conn.setDoOutput(true);
		
		OutputStreamWriter writer = null;
//		writer = new OutputStreamWriter(conn.getOutputStream(),"GBK");
		writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
		writer.write(msg);
//		System.out.println(msg);
		writer.flush();

		String line;
		List<String> info = new ArrayList<String>();
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			info.add(line);
		}
		writer.close();
		reader.close();
		StringBuffer sb = new StringBuffer();
		if (info != null) {
			for (int i = 0; i < info.size(); i++) {
				sb.append(info.get(i));
				sb.append("\n");
			}
		}
		if (sb.toString().contains("Exception")) {
			System.out.println(sb.toString());
		}
		return sb.toString();
	}
	/*得到需要发送给url的字符串
	* @param sensorID 传感器ID
	* @param time 观测时间
	* @param properties 传感器属性
	* @param value 观测值
	*/
	public String InsertObservation(String sensorID,String time,String[] properties,String value) {
		String str = "<env:Envelope\r\n" + 
				"    xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"\r\n" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2003/05/soap-envelope http://www.w3.org/2003/05/soap-envelope/soap-envelope.xsd\">\r\n" + 
				"    <env:Body>\r\n" + 
				"        <sos:InsertObservation\r\n" + 
				"            xmlns:sos=\"http://www.opengis.net/sos/2.0\" service=\"SOS\" version=\"2.0.0\" xsi:schemaLocation=\"http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd            http://www.opengis.net/samplingSpatial/2.0 http://schemas.opengis.net/samplingSpatial/2.0/spatialSamplingFeature.xsd\"\r\n" + 
				"            xmlns:sf=\"http://www.opengis.net/sampling/2.0\"\r\n" + 
				"            xmlns:sams=\"http://www.opengis.net/samplingSpatial/2.0\"\r\n" + 
				"            xmlns:om=\"http://www.opengis.net/om/2.0\"\r\n" + 
				"            xmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n" + 
				"            xmlns:gml=\"http://www.opengis.net/gml/3.2\"\r\n" + 
				"            xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\"\r\n" + 
				"            xmlns:swe=\"http://www.opengis.net/swe/2.0\"\r\n" + 
				"            xmlns:swes=\"http://www.opengis.net/swes/2.0\">\r\n" + 
				"            <!-- multiple offerings are possible -->\r\n" + 
				"            <sos:offering>http://www.52north.org/test/offering/"+sensorID+"</sos:offering>\r\n" + 
				"            <sos:observation>\r\n" + 
				"                <om:OM_Observation gml:id=\"o1\">\r\n" + 
				"                    <gml:description>test description for this observation</gml:description>\r\n" + 
				"                    <om:type xlink:href=\"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement\" />\r\n" + 
				"                    <om:phenomenonTime>\r\n" + 
				"                        <gml:TimeInstant gml:id=\"phenomenonTime\">\r\n" + 
				"                            <gml:timePosition>"+time+"</gml:timePosition>\r\n" + 
				"                        </gml:TimeInstant>\r\n" + 
				"                    </om:phenomenonTime>\r\n" + 
				"                    <om:resultTime xlink:href=\"#phenomenonTime\" />\r\n" + 
				"                    <om:procedure xlink:href=\""+sensorID+"\" />\r\n" + 
				"                    <om:observedProperty xlink:href=\""+properties[0]+"\" />\r\n" + 
				"                    <om:featureOfInterest>\r\n" + 
				"                        <sams:SF_SpatialSamplingFeature gml:id=\"ssf_test_feature_9\">\r\n" + 
				"                            <gml:identifier codeSpace=\"\">http://www.52north.org/test/featureOfInterest/9</gml:identifier>\r\n" + 
				"                            <gml:name>上海大学宝山校区</gml:name>\r\n" + 
				"                            <sf:type xlink:href=\"http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint\" />\r\n" + 
				"                            <sf:sampledFeature xlink:href=\"http://www.52north.org/test/featureOfInterest/1\" />\r\n" + 
				"                            <sams:shape>\r\n" + 
				"                                <gml:Point gml:id=\"PM10测试点1\">\r\n" + 
				"                                    <gml:pos srsName=\"http://www.opengis.net/def/crs/EPSG/0/4326\">51.935101100104916 7.651968812254194</gml:pos>\r\n" + 
				"                                </gml:Point>\r\n" + 
				"                            </sams:shape>\r\n" + 
				"                        </sams:SF_SpatialSamplingFeature>\r\n" + 
				"                    </om:featureOfInterest>\r\n" + 
				"                    <om:result xsi:type=\"gml:MeasureType\" uom=\"test_unit_9_3\">"+value+"</om:result>\r\n" + 
				"                </om:OM_Observation>\r\n" + 
				"            </sos:observation>\r\n" + 
				"        </sos:InsertObservation>\r\n" + 
				"    </env:Body>\r\n" + 
				"</env:Envelope>";
		//return insertObservationStr.toString();
		return str;
	}
}
