package com.medical.senddata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.medical.managesystem.DeviceRegister;
import com.medical.managesystem.R;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InsertSensor2 extends Thread{
	String sensorID;
	String[] properties = null;
	public InsertSensor2(String sensorID, String[] properties) {
		this.sensorID = sensorID;
		this.properties = properties;
	}
	String msg = getMsg(sensorID, properties);
	
		@Override
		public void run() {
			//String msg = getMsg("SAMSUNG'S DEVICES", new String[] {"int","float"});
			super.run();
			try {
				//URL url = new URL("http://localhost:8080/52n-sos-webapp/sos/soap");
				//在eclipse中运行时，要将localhost用本机IP地址代替
				//URL url = new URL("http://www.baidu.com");
				URL url = new URL("http://192.168.1.103:8080/52n-sos-webapp/sos/soap");
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
					/*if (Build.VERSION.SDK != null  
							&& Build.VERSION.SDK_INT > 13) {  
							conn.setRequestProperty("Connection", "close");  
							}*/
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
				}
				Message message = new Message();
				message.obj = sb.toString();
				handler1.sendMessage(message);
				//System.out.println(sb.toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("connecting", e.toString());
			}
		}
	
	public Handler handler1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	/*
	*@param msg 要发送的字符串
	*@param Url 要发送到的url地址
	*/
	public static String sendToSOSj(String msg, String Url) throws Exception {
		URL url = new URL(Url);
		URLConnection conn = null;
		conn = url.openConnection();
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("accept", "application/json");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setRequestProperty("Accept-Charset","UTF-8");
        conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.8");
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//        conn.setRequestProperty("accept", "*/*");
//        conn.setRequestProperty("connection", "Keep-Alive");
//        conn.setRequestProperty("user-agent",
//                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//        conn.setRequestProperty("Content-type", "application/xml"); 
        // 发送POST请求必须设置如下两行
        conn.setDoInput(true);		
		
		conn.setDoOutput(true);
		
		OutputStreamWriter writer = null;
		//这边的GBK真的是很神奇
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
	/*
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
				"                                            <sml:value>"+sensorID+"\r\n" + 
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
				"                                            <swe:value>http://www.52north.org/test/offering/"+sensorID+"</swe:value>\r\n" + 
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
				"            <swes:observableProperty>"+	properties[0] 	+                          "</swes:observableProperty>\r\n"	+ 
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
