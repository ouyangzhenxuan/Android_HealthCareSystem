package com.medical.managesystem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.medical.managesystem.BloodPressure.DrawThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BloodSugar extends Activity{
	private LineChart mChart;  
	public static int  data_i=0;
	private float data_floatType;
	//��ʱ����ʹ��
	public int data_i2;
	private MyApplicationDataSave3 mAppBS;
	private DrawThread drawThread_bs;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bloodsugar);  
        mAppBS = new MyApplicationDataSave3();
        DrawChart();
        drawThread_bs = new DrawThread();
		if(DeviceRegister.connect_state[1]==true) {//�жϸĴ���������״̬
			drawThread_bs.start();
		}else {
			alertNodata().show();
		}
    }
    
    private  AlertDialog alertNodata() {
		AlertDialog noresponse = new AlertDialog.Builder(BloodSugar.this)
				.setTitle("��ʾ")
				.setMessage("�����������ݣ���Ҫ�����ȴ����Ƿ��أ�")
				.setNegativeButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawThread_bs.interrupt();
						finish();
					}
				})
				.setPositiveButton("�ȴ�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Handler().postDelayed(new Runnable() {
				            @Override
				            public void run() {
				            	if(DeviceRegister.connect_state[1]==true) {
									drawThread_bs.start();
								}else {
									drawThread_bs.interrupt();
									Toast.makeText(getApplicationContext(), "�����ݣ���������ҳ��", Toast.LENGTH_SHORT).show();
					            	finish();
								}
				            }
				        	},3000); // ��ʱ3��
						}
				}).create();
		noresponse.setCanceledOnTouchOutside(false);
		return noresponse;
	}
    
		//��ͼ�߳�
    	class DrawThread extends Thread{
    		@Override
    		public void run() {
    			for(int i=0;;i++) {
    				try {
    					Thread.sleep(1000);
    					//��Ϊ��DeviceRegister��data_i�Լ���1��������������ʾҪ��ȥ1
    					data_i2 = data_i-1;
    					Log.d("data_i", ""+data_i2);
    					Log.d("mAppBP.getData(data_i)", ""+mAppBS.getData(data_i2));
    					if(mAppBS.getData(data_i2)!=null) {
    					  Message msg = new Message();
    					  msg.obj = mAppBS.getData(data_i2);
    					  handler.sendMessage(msg);
    					}
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				}
    		}
    	}
    	private Handler handler = new Handler() {
    		public void handleMessage(android.os.Message msg) {
    			if(String.valueOf(msg.obj)!="null") {
    			data_floatType = Float.parseFloat(String.valueOf(msg.obj));
    			addEntry();
    			Log.d("DrawHandle", "DrawSucceed");
    			}
    		};
    	};
    	/***********************************************/
    	private void DrawChart() {
    		mChart = (LineChart) findViewById(R.id.chart_bs);  
    		  
            mChart.setDescription("@ZHENXUAN OUYANG");  
            mChart.setNoDataTextDescription("��ʱ��������");  
            mChart.setTouchEnabled(true);  
            // ����ҷ  
            mChart.setDragEnabled(true);  
            // ������  
            mChart.setScaleEnabled(true);  
            mChart.setDrawGridBackground(false);  
            mChart.setPinchZoom(true);  
            // ����ͼ��ı�����ɫ  
            mChart.setBackgroundColor(Color.WHITE);  
            LineData data = new LineData();  
            // ������ʾ����ɫ  
            data.setValueTextColor(Color.BLUE);  
            // ������һ���յ����ݣ���������涯̬���  
            mChart.setData(data);  
            // ͼ���ע��(ֻ�е����ݼ�����ʱ�����Ч)  
            Legend l = mChart.getLegend();  
            // �����޸�ͼ��ע�ⲿ�ֵ�λ��  
            // l.setPosition(LegendPosition.LEFT_OF_CHART); 
            // ���ԣ�Ҳ����Բ  
            l.setForm(LegendForm.LINE);  
            // ��ɫ  
            l.setTextColor(Color.BLACK);  
            // x������  
            XAxis xl = mChart.getXAxis();  
            xl.setTextColor(Color.BLACK);  
            xl.setDrawGridLines(false);  
            xl.setAvoidFirstLastClipping(true);  
      
            // ����x������֮��Ż��ƣ�  
            xl.setSpaceBetweenLabels(5);  
      
            // ���false����ôx�����Ὣ���ɼ�  
            xl.setEnabled(true);  
      
            // ��X����������ڵײ���Ĭ�����ڶ�����  
            xl.setPosition(XAxisPosition.BOTTOM);  
      
            // ͼ����ߵ�y��������  
            YAxis leftAxis = mChart.getAxisLeft();  
            leftAxis.setTextColor(Color.BLACK);  
      
            // ���ֵ  
            leftAxis.setAxisMaxValue(100f);  
      
            // ��Сֵ  
            leftAxis.setAxisMinValue(1f);  
      
            // ��һ��Ҫ��0��ʼ  
            leftAxis.setStartAtZero(false);  
      
            leftAxis.setDrawGridLines(true);  
      
            YAxis rightAxis = mChart.getAxisRight();  
            // ����ʾͼ����ұ�y��������  
            rightAxis.setEnabled(false);  
      
            //������
            LimitLine yLimitLine1 = new LimitLine(39f, "����ֵ 3.9");
            yLimitLine1.setLineColor(Color.RED);
            yLimitLine1.setTextColor(Color.RED);
            leftAxis.addLimitLine(yLimitLine1);
            LimitLine yLimitLine2 = new LimitLine(61f, "����ֵ 6.1");
            yLimitLine2.setLineColor(Color.RED);
            yLimitLine2.setTextColor(Color.RED);
            leftAxis.addLimitLine(yLimitLine2);
    	}
    	// ��ӽ�ȥһ�������  
        private void addEntry() {  
            LineData data = mChart.getData();  
            // ÿһ��LineDataSet����һ���ߣ�ÿ��ͳ��ͼ�����ͬʱ�������ɸ�ͳ�����ߣ���Щ����������һ����0��ʼ�±ꡣ  
            // ����ֻ��һ������ô���ǵ�0������  
            LineDataSet set = data.getDataSetByIndex(0);  
            // �����ͳ������ͼ��û�����ݼ����򴴽�һ��������������������˴����롣  
            if (set == null) {  
                set = createLineDataSet();  
                data.addDataSet(set);  
            }  
            // �����һ��x�������ֵ  
            // ��Ϊ�Ǵ�0��ʼ��data.getXValCount()ÿ�η��ص�����ȫ��x�������������������Բ��ض��һ�ٵļ�1  
            data.addXValue((data.getXValCount()) + "");  
      
            // �������������  
            //float f = (float) ((Math.random()) * 20 + 50);  
            
            //��ȡȫ�ֱ������Ҹ�ֵ
            //float dataFromMyApp = Float.parseFloat(mAppBP.getData(data_i));
            
            // set.getEntryCount()��õ�������ͳ��ͼ���ϵ����ݵ�������  
            // ���0��ʼһ���������±꣬��ô���ض��һ�ٵļ�1  
            
            //Entry entry = new Entry(f, set.getEntryCount());  
            Entry entry = new Entry(data_floatType, set.getEntryCount());  
            
            // ��linedata������ӵ㡣ע�⣺addentry�ĵڶ����������������ߵ��±�������  
            // ��Ϊ����ֻ��һ��ͳ�����ߣ���ô���ǵ�һ�������±�Ϊ0.  
            // ���ͬһ��ͳ��ͼ���д���������ͳ�����ߣ���ô��������������һ���������±�������ͳ��������ӡ�  
            data.addEntry(entry, 0);  
      
            // ��ListView������֪ͨ���ݸ���  
            mChart.notifyDataSetChanged();  
      
            // ��ǰͳ��ͼ���������x������������ʾ������  
            mChart.setVisibleXRangeMaximum(20);  
      
            // y�����������ֵ  
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);  
      
            // �������ƶ�������  
            // �˴��뽫ˢ��ͼ��Ļ�ͼ  
            mChart.moveViewToX(data.getXValCount() - 5);  
      
            // mChart.moveViewTo(data.getXValCount()-7, 55f,  
            // AxisDependency.LEFT);  
        }  
      
        // ��ʼ�����ݼ������һ��ͳ�����ߣ����Լ򵥵�����ǳ�ʼ��y���������ϵ�ı���  
        private LineDataSet createLineDataSet() {  
            LineDataSet set = new LineDataSet(null, "��̬��ӵ�����");  
            set.setAxisDependency(AxisDependency.LEFT);  
            // ���ߵ���ɫ  
            set.setColor(ColorTemplate.getHoloBlue());  
            set.setCircleColor(Color.RED);  
            set.setLineWidth(10f);  
            set.setCircleSize(5f);  
            set.setFillAlpha(128);  
            set.setFillColor(ColorTemplate.getHoloBlue());  
            set.setHighLightColor(Color.GREEN);  
            set.setValueTextColor(Color.BLACK);  
            set.setValueTextSize(10f);  
            set.setDrawValues(true);  
            return set;  
        }
        /******************************************************************/

}
