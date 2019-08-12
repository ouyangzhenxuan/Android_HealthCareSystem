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
	//临时变量使用
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
		if(DeviceRegister.connect_state[1]==true) {//判断改传感器连接状态
			drawThread_bs.start();
		}else {
			alertNodata().show();
		}
    }
    
    private  AlertDialog alertNodata() {
		AlertDialog noresponse = new AlertDialog.Builder(BloodSugar.this)
				.setTitle("提示")
				.setMessage("现在暂无数据，需要继续等待还是返回？")
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawThread_bs.interrupt();
						finish();
					}
				})
				.setPositiveButton("等待", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Handler().postDelayed(new Runnable() {
				            @Override
				            public void run() {
				            	if(DeviceRegister.connect_state[1]==true) {
									drawThread_bs.start();
								}else {
									drawThread_bs.interrupt();
									Toast.makeText(getApplicationContext(), "无数据，将返回主页面", Toast.LENGTH_SHORT).show();
					            	finish();
								}
				            }
				        	},3000); // 延时3秒
						}
				}).create();
		noresponse.setCanceledOnTouchOutside(false);
		return noresponse;
	}
    
		//画图线程
    	class DrawThread extends Thread{
    		@Override
    		public void run() {
    			for(int i=0;;i++) {
    				try {
    					Thread.sleep(1000);
    					//因为在DeviceRegister中data_i自加了1，所以在这里显示要减去1
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
            mChart.setNoDataTextDescription("暂时尚无数据");  
            mChart.setTouchEnabled(true);  
            // 可拖曳  
            mChart.setDragEnabled(true);  
            // 可缩放  
            mChart.setScaleEnabled(true);  
            mChart.setDrawGridBackground(false);  
            mChart.setPinchZoom(true);  
            // 设置图表的背景颜色  
            mChart.setBackgroundColor(Color.WHITE);  
            LineData data = new LineData();  
            // 数据显示的颜色  
            data.setValueTextColor(Color.BLUE);  
            // 先增加一个空的数据，随后往里面动态添加  
            mChart.setData(data);  
            // 图表的注解(只有当数据集存在时候才生效)  
            Legend l = mChart.getLegend();  
            // 可以修改图表注解部分的位置  
            // l.setPosition(LegendPosition.LEFT_OF_CHART); 
            // 线性，也可是圆  
            l.setForm(LegendForm.LINE);  
            // 颜色  
            l.setTextColor(Color.BLACK);  
            // x坐标轴  
            XAxis xl = mChart.getXAxis();  
            xl.setTextColor(Color.BLACK);  
            xl.setDrawGridLines(false);  
            xl.setAvoidFirstLastClipping(true);  
      
            // 几个x坐标轴之间才绘制？  
            xl.setSpaceBetweenLabels(5);  
      
            // 如果false，那么x坐标轴将不可见  
            xl.setEnabled(true);  
      
            // 将X坐标轴放置在底部，默认是在顶部。  
            xl.setPosition(XAxisPosition.BOTTOM);  
      
            // 图表左边的y坐标轴线  
            YAxis leftAxis = mChart.getAxisLeft();  
            leftAxis.setTextColor(Color.BLACK);  
      
            // 最大值  
            leftAxis.setAxisMaxValue(100f);  
      
            // 最小值  
            leftAxis.setAxisMinValue(1f);  
      
            // 不一定要从0开始  
            leftAxis.setStartAtZero(false);  
      
            leftAxis.setDrawGridLines(true);  
      
            YAxis rightAxis = mChart.getAxisRight();  
            // 不显示图表的右边y坐标轴线  
            rightAxis.setEnabled(false);  
      
            //限制线
            LimitLine yLimitLine1 = new LimitLine(39f, "警告值 3.9");
            yLimitLine1.setLineColor(Color.RED);
            yLimitLine1.setTextColor(Color.RED);
            leftAxis.addLimitLine(yLimitLine1);
            LimitLine yLimitLine2 = new LimitLine(61f, "警告值 6.1");
            yLimitLine2.setLineColor(Color.RED);
            yLimitLine2.setTextColor(Color.RED);
            leftAxis.addLimitLine(yLimitLine2);
    	}
    	// 添加进去一个坐标点  
        private void addEntry() {  
            LineData data = mChart.getData();  
            // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。  
            // 本例只有一个，那么就是第0条折线  
            LineDataSet set = data.getDataSetByIndex(0);  
            // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。  
            if (set == null) {  
                set = createLineDataSet();  
                data.addDataSet(set);  
            }  
            // 先添加一个x坐标轴的值  
            // 因为是从0开始，data.getXValCount()每次返回的总是全部x坐标轴上总数量，所以不必多此一举的加1  
            data.addXValue((data.getXValCount()) + "");  
      
            // 生成随机测试数  
            //float f = (float) ((Math.random()) * 20 + 50);  
            
            //获取全局变量并且赋值
            //float dataFromMyApp = Float.parseFloat(mAppBP.getData(data_i));
            
            // set.getEntryCount()获得的是所有统计图表上的数据点总量，  
            // 如从0开始一样的数组下标，那么不必多次一举的加1  
            
            //Entry entry = new Entry(f, set.getEntryCount());  
            Entry entry = new Entry(data_floatType, set.getEntryCount());  
            
            // 往linedata里面添加点。注意：addentry的第二个参数即代表折线的下标索引。  
            // 因为本例只有一个统计折线，那么就是第一个，其下标为0.  
            // 如果同一张统计图表中存在若干条统计折线，那么必须分清是针对哪一条（依据下标索引）统计折线添加。  
            data.addEntry(entry, 0);  
      
            // 像ListView那样的通知数据更新  
            mChart.notifyDataSetChanged();  
      
            // 当前统计图表中最多在x轴坐标线上显示的总量  
            mChart.setVisibleXRangeMaximum(20);  
      
            // y坐标轴线最大值  
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);  
      
            // 将坐标移动到最新  
            // 此代码将刷新图表的绘图  
            mChart.moveViewToX(data.getXValCount() - 5);  
      
            // mChart.moveViewTo(data.getXValCount()-7, 55f,  
            // AxisDependency.LEFT);  
        }  
      
        // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征  
        private LineDataSet createLineDataSet() {  
            LineDataSet set = new LineDataSet(null, "动态添加的数据");  
            set.setAxisDependency(AxisDependency.LEFT);  
            // 折线的颜色  
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
