package UIs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import utils.BindExpression;
import utils.Expression;
import view.UtTable;
import SAM.DataPool.NetDataModel;
import SAM.extraHisModel.HisDataDAO;
import SAM.extraHisModel.HisEvent;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemSelectedListener;


//自定义控件历史告警列表  控件
//注意，由于表格需要将数据保存到表适配里！
@SuppressLint("HandlerLeak")
public class Ks_zHisEventList extends ViewGroup implements VObject{

	public Ks_zHisEventList(Context context) {
		super(context); 
//		Log.e("Ks_EventList->","into"); 
		//实例化该控件的组合元素控件
		 lstTitles = new ArrayList<String>();         //表格标题成员字符数组
		 lstContends = new ArrayList<List<String>>(); //表格字符串成员数组 
				 
		 //定义表格view
		 table = new UtTable(context);
		 table.m_bUseTitle = false;  //表头 是否显示
		 this.addView(table);
		 
		 //表格标题  //子元素添加到该容器上
		lstTitles.add("设备名称");	
//		lstTitles.add("信号名称");			
		lstTitles.add("告警名称");
		lstTitles.add("告警含义");
		lstTitles.add("信号数值");
		lstTitles.add("告警等级");
		lstTitles.add("开始时间");
		lstTitles.add("结束时间");
		 textView_titles = new TextView[lstTitles.size()];
		 if(table.m_bUseTitle==false){
			 for(int i=0;i<lstTitles.size();i++){
				 textView_titles[i] = new TextView(context);
				 textView_titles[i].setText(lstTitles.get(i));
				 textView_titles[i].setTextColor(Color.BLACK); //字体颜色与 内容字体一样v_iFontColor
				 textView_titles[i].setGravity(Gravity.CENTER);
				 this.addView(textView_titles[i]);
			 }
		 }
		//信号名显示text	
			view_text = new TextView(context);
			view_text.setTextColor(Color.BLACK);
			view_text.setText("  设备↓   ");  //变为中文
			view_text.setTextSize(16/MainActivity.densityPer);
			view_text.setGravity(Gravity.CENTER);
			view_text.setBackgroundColor(Color.argb(100, 100, 100, 100));
			this.addView(view_text);
			
			//日期选择button
			view_timeButton = new Button(context);
			view_timeButton.setText("设置日期");   // Set Time
			view_timeButton.setTextColor(Color.BLACK);
			view_timeButton.setTextSize(16/MainActivity.densityPer);
			view_timeButton.setPadding(2, 2, 2, 2);
			view_timeButton.setOnClickListener(l);//设置该控件的监听
			this.addView(view_timeButton);
			//前一天button
			view_PerveDay = new Button(context);	
			view_PerveDay.setText("前一天");  // PreveDay
			view_PerveDay.setTextColor(Color.BLACK);
			view_PerveDay.setTextSize(16/MainActivity.densityPer);
			view_PerveDay.setPadding(2, 2, 2, 2);		
			view_PerveDay.setOnClickListener(l);//设置该控件的监听	
			this.addView(view_PerveDay);
			//后一天button
			view_NextDay = new Button(context);	
			view_NextDay.setText("后一天");  // NextDay
			view_NextDay.setTextColor(Color.BLACK);
			view_NextDay.setTextSize(16/MainActivity.densityPer);	
			view_NextDay.setPadding(2, 2, 2, 2);
			view_NextDay.setOnClickListener(l);//设置该控件的监听
			this.addView(view_NextDay);
			//接收receive
			view_Receive = new Button(context);		
			view_Receive.setText("  获取   ");
			view_Receive.setTextColor(Color.BLACK);
			view_Receive.setTextSize(16/MainActivity.densityPer);
			view_Receive.setPadding(2, 2, 2, 2);		
			view_Receive.setOnClickListener(l);	//设置该控件的监听	
			this.addView(view_Receive);
			//日期设置对话框
			calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			dialog = new DatePickerDialog(context, new OnDateSetListener() {			
				@Override
				public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub		
					num = 0;  //天数加减按纽清零
//					String text = year + "-" + month +"-" + day;				
//					Log.i("LocalList-OnDateSetListener 选择的日期是:", text);
				}
			}, year, month, day);
			//信号名选择spinner
			view_EquiptSpinner = new Spinner(context);//信号下拉列表控件
			adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item); 
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			view_EquiptSpinner.setAdapter(adapter);
			adapter_lst = new ArrayList<String>();
			adapter.add("  设备↓   ");	
			map_EquiptNameList = new HashMap<String, Integer>();
			view_EquiptSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {	
				 public void onItemSelected(			
		                    AdapterView<?> parent, View view, int position, long id) {
					 if(NetDataModel.hm_xmlEquiptCfgModel == null ) return;
					 if(NetDataModel.lst_poolEquipmentId == null ) return;
					 Iterator<Integer>id_lst = NetDataModel.lst_poolEquipmentId.keySet().iterator(); //解析下拉列表成员
			         while(id_lst.hasNext()){
			        	 int e_id = id_lst.next();
			        	 String e_name = NetDataModel.getEquipmentCfg(e_id).EquipTemplateName;
			        	 adapter_lst.add(e_name);
			        	 adapter.add(e_name);
			        	 if(e_name != null)
			        		 map_EquiptNameList.put(e_name, e_id);
			         }			            	
		            }

		            public void onNothingSelected(AdapterView<?> parent) {
		            	Log.e("HisEvent->view_SignalSpinner-OnClickListener","into onNothingSelected");
		            }
			});
			this.addView(view_EquiptSpinner);

		 
		//设置列表滑屏监听  主要作用：当检测到有滑屏click1=false  停止滑屏后click1=true;
			this.table.setOnScrollListener(new OnScrollListener() {	
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					// TODO Auto-generated method stub
					switch(arg1){
					case OnScrollListener.SCROLL_STATE_IDLE:  //当屏幕停止滚动
						slideFlag=true;
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"屏幕停止滚动！");
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //屏幕滚动 
						slideFlag=false;
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"屏幕滚动!");
						break;
					case OnScrollListener.SCROLL_STATE_FLING:   //手指离开后屏幕惯性滚动
						slideFlag=false;
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"屏幕惯性滚动!");
						break;
					}
				}
				@Override  //表格数据刷新 会有滚定监听到调用该方法
				public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub 
					//Log.e("Ks_EventList->onScroll>>","listview滑屏监听--2！");  
				}
			});
		 
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "HisEventList";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "Binding{[Equip[Equip:3]]}";          //控件绑定表达式
	int v_iPosX = 100,v_iPosY = 100;       //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //字体的颜色
	String v_strContent = "设置内容";        //控件字符内容
	String v_strFontFamily = "微软雅黑";      //控件文字类型
	boolean v_bIsBold = false;               //控件线条是否加粗
	String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
	String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
	

	int v_iLineThickness =3; //线条大小
	int v_iLineColor = 0xFF000000;  //线条颜色
//	public int m_cOddRowBackground = 0xFF000000; // 奇数
//	public int m_cEvenRowBackground = 0xFF000000; // 偶数
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	
	//定义控件使用的元素
	 UtTable table;
	 TextView[] textView_titles;
		TextView view_text;		            //信号名显示text		
		Spinner view_EquiptSpinner = null; 		//设备名选择spinner
		Button  view_timeButton;		        //日期选择button
		Button  view_PerveDay;		            //前一天button
		Button  view_NextDay;		            //后一天button
		Button  view_Receive;		            //接收receive
		
		private  DatePickerDialog  dialog;  //日期对话框选择应用
		private int year,month,day;   //对话框显示的年月日变量
		private Calendar calendar;
		private int flag = 0;
		private int num = 0; //加减按纽加减数	
		public  String get_day="";   //所要获取数据的日期
		
		private HashMap<String,Integer> map_EquiptNameList = null;  //<设备名-设备id>
		private  ArrayAdapter<String> adapter = null;
		private String closeEquiptName = "";
		public static int str_EquiptId = -1; //所需要的设备-信号id字符串	
		List<String> adapter_lst = null;
		
		private boolean slideFlag = true; // 表格 滑屏 标志
		private boolean threadDie = true; //线程 是否 结束标志

	//辅助变量
	List<String> lstTitles = null;  //表格标题成员字符数组
	List<List<String>> lstContends = null; //表格字符串成员数组
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数      信号列表 只处理单项绑定
	Expression expression = null; //表达式子项类
	int equiptId = -1;


	boolean isCanUpdateViewFlag1 = true;
	boolean isCanUpdateViewFlag2 = true;
	long nowTime = 10000; //10s
	long oldTime = 0;
	
	boolean canVISIBLE = false;  //判断控件是否可见  标志变量
	
	
	//监听器 view_Receive
	private OnClickListener l = new OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					Log.e("HisEvent-OnClickListener1", "into");   //测试正常
					// TODO Auto-generated method stub		
					
					//获取设置的日期
					int set_year = dialog.getDatePicker().getYear();
					int set_month = dialog.getDatePicker().getMonth()+1;
					int set_day = dialog.getDatePicker().getDayOfMonth();
					
					if(slideFlag==false){
						Toast.makeText(Ks_zHisEventList.this.getContext(), "滑动屏幕中···，请稍后！", Toast.LENGTH_SHORT).show();
						return;
					}
					//判断哪一个监听器的	
					if(arg0 == view_timeButton){
						dialog.show();  //弹出日期对话框
						flag = 1;	
						num = 0;
						return;
					}else if(arg0 == view_Receive){
						num = 0;
					}else if(arg0 == view_NextDay){
						num++;	
						set_day = set_day + num; //天数加一天； num有正负之分
						//判断不超过今天日期
						long now_time = java.lang.System.currentTimeMillis();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式转换
						Date date = new Date(now_time);
						String sampletime = formatter.format(date);
						String now_year = sampletime.substring(0, 4);
						String now_month = sampletime.substring(5, 7);
						String now_day = sampletime.substring(8, 10);
						int int_now_year = Integer.valueOf(now_year);
						int int_now_month = Integer.valueOf(now_month);
						int int_now_day = Integer.valueOf(now_day);							 
						//月末判断
						if(set_day > 31){
								set_day = set_day-31;
								set_month++;
								if(set_month>12){
									set_month = 1;
									set_year++;
								}
						}
						if(set_day < 1){
								set_day = set_day+31;
								set_month--;
								if(set_month<1){
									set_month = 12;
									set_year--;
								}
						}
						if((set_year==int_now_year)&&(set_month==int_now_month)&&(set_day > int_now_day)){								 
								 set_day = int_now_day;
								 num--;
						}					
				     }else if(arg0 == view_PerveDay){
					
							num--;  //num有正负之分
							set_day = set_day + num; //天数加1天
							if(set_day < 1){
								set_day = 31+set_day;
								set_month--;
								if(set_month<1){
									set_month = 12;
									set_year--;
								}
							}if(set_day > 31){
								set_day = set_day-31;
								set_month++;
								if(set_month>12){
									set_month = 1;
									set_year++;
								}
							}
							
						}
						//处理月份日期字符格式
						String str_day;
						String str_nomth;
						if(set_day<10)
						{
							str_day = "0"+String.valueOf(set_day);
						}else{
							str_day = String.valueOf(set_day);
						}
						if(set_month<10)
						{
							str_nomth = "0"+String.valueOf(set_month);
						}else{
							str_nomth = String.valueOf(set_month);
						}
						get_day = String.valueOf(set_year)+"-"+str_nomth+"-"+str_day;
									
						if("".equals(get_day)) return;
										
						//显示所选择的信号的名字						
						closeEquiptName = (String) view_EquiptSpinner.getSelectedItem();
						view_text.setText(closeEquiptName);	
						if("  设备↓   ".equals(closeEquiptName))  return;
						str_EquiptId = map_EquiptNameList.get(closeEquiptName);
						Log.e("HisEvent-OnClickListener获取的设备id:",str_EquiptId+"---"+closeEquiptName+"---");							
						
						if(threadDie){
							myThread thread = new myThread();
							thread.start(); 						
						}else{
							Toast.makeText(Ks_zHisEventList.this.getContext(), "正在加载数据，请稍后！", Toast.LENGTH_SHORT).show();
						}

		}
	};
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				doInvalidate();//更新控件 表格
				Log.e("ks_HisEvent>>myhandler>>update():", "表格刷新执行！");
				break;
			default: break;
			}
		}
		
	};
	//当有按钮 按下时通知ui刷新线程
	private class myThread extends Thread{	
		
		@SuppressWarnings("unused")
		@Override
		public void run() { 
			// TODO Auto-generated method stub
			try{
				threadDie = false;
				String fileName = "hisevent-"+String.valueOf(str_EquiptId);
				if(false){  //新版本路径  文件名
					fileName = "hisevent-"+String.valueOf(str_EquiptId)+"-"+closeEquiptName;
				}
				
				if(HisDataDAO.getHisEquipEventList(fileName)){  //提取  历史 设备 数据
					if( update() ) 
						myHandler.sendEmptyMessage(1);
					Thread.sleep(2000);  //2s 延时 使有足够的时间刷新 表格
					Log.e("ks_HisEvent>>Thread>>fileName:",fileName);
				}
			}catch(Exception e){
				Log.e("ks_HisEvent->thread>>","异常抛出！");
			}
			threadDie = true;
		}
	}
	//表格内容 数据刷新
	private boolean update(){
		lstContends.clear(); //清楚页面的以前数据 行信号  
		//获取历史数据设备列表      多个设备的历史数据列表 列表<设备表<历史信号结构体>>
		List<HisEvent> hisEvent_list = HisDataDAO.hisEvent_lst;
		//遍历做容错处理  去除重复采集的告警	
		List<String> key = new ArrayList();
		Hashtable<String,HisEvent> hast_his = new Hashtable<String,HisEvent>();
		for(int i=0; i<hisEvent_list.size(); i++){
			HisEvent his_event = hisEvent_list.get(i);
			if(his_event==null) return false;	
			boolean flag = true;
			//判断是否有该告警开始时间的key值与信号 是否已经添加
			if(hast_his.containsKey(his_event.start_time+"#"+his_event.event_id)){
						flag = false;
						//再判断是否为同一条信号
						if("1970-01-01".equals(his_event.finish_time.substring(0, 10)) )
							continue;
			}
			hast_his.put(his_event.start_time+"#"+his_event.event_id, his_event);	

			if(flag){
					key.add(his_event.start_time+"#"+his_event.event_id);
			}		
		}

		if(key == null||hast_his == null) return false;
			
		//将顺序逆转
		List<String> key2 = null;
		key2 = new ArrayList<String>();
		key2.clear();
		for(int i=key.size()-1;i>=0; i--){
			key2.add(key.get(i));  
		} 
		
		//遍历
				lstContends.clear(); //清楚页面的以前数据 行信号 
				if(key2==null) return false; 
				Iterator<String> iterator_key = key2.iterator();
				while(iterator_key.hasNext()){ 
					String his_event_key = iterator_key.next();
					if(his_event_key==null||"".equals(his_event_key)) return false;
					HisEvent his_event = hast_his.get(his_event_key);
					if(his_event == null) return false;
				    List<String> lstRow_his = new ArrayList<String>();
				    lstRow_his.clear();
				    	//对通信中断告警结束时间做判断
				    	String finishTime = his_event.finish_time;
//				    	Log.e("HisEvent_updateValue->his_event_list看截取时间：",finishTime);
				    	if(finishTime.length()<10) return false;
				    	//该处理有debug待后期完善
				    	if("1970-01-01".equals(finishTime.substring(0, 10))){  		    		  
				    		  finishTime = "null";	 
//				    		  continue;
				    	}
				    	//处理获取的时间 历史告警
				    	String startTime = his_event.start_time.substring(0, 10);//截取年月日
				    	if(startTime.equals(get_day)==false){
				//    		Log.e("HisEvent_updateValue->his_event_list看开始时间：",startTime);
				    		continue;
				    	}
				    	
//				      //重复的强制处理   	
//				    	if((lstContends!=null)||(lstContends.size()!=0)){
//					    	for(int i=0;i<lstContends.size();i++){
//						    	List<String> ls = lstContends.get(0);
//						    	String t_name = ls.get(0);
//						    	if(t_name.equals(closeEquiptName)==false)
//						    			lstContends.remove(i);
//					    	}
//				    	}
				    	String eventName = "";
				    	try{
				    	int equiptId = Integer.parseInt(his_event.equipid);
				    	int eventId = Integer.parseInt(his_event.event_id);
				    	his_event.event_name = his_event.event_name;
				    	}catch(Exception e){
				    		
				    	}
				    	
				    	lstRow_his.add(closeEquiptName);  //设备名称  			    
				    //	lstRow_his.add(name);    //信号名称		    	
				    	lstRow_his.add(his_event.event_name);//告警名称
				    	lstRow_his.add(his_event.event_mean); //告警含义
				    	lstRow_his.add(his_event.value);     //信号数值
				    	lstRow_his.add(his_event.severity);    //告警等级 
				    	lstRow_his.add(his_event.start_time); //开始时间
				    	lstRow_his.add(finishTime);//结束时间

				    	lstContends.add(lstRow_his);
				    
				 }
				table.updateContends(lstTitles, lstContends);
				lstContends.clear(); //清楚页面的以前数据 行信号
				hast_his.clear();	
				key.clear();
				key2.clear();
		return true;
	}

	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{	
		super.dispatchDraw(canvas);		
		canvas.drawColor(v_iBackgroundColor);   //设置viewgroup的底板颜色  
		
		drawChild(canvas, table, getDrawingTime());
		
		if(table.m_bUseTitle==false){
			for(int i=0;i<textView_titles.length;i++){
				drawChild(canvas, textView_titles[i], getDrawingTime());
				textView_titles[i].setTextColor(Color.BLACK);
				textView_titles[i].setTextSize(v_fFontSize/MainActivity.densityPer);
			}	
		}

		drawChild(canvas, view_Receive, getDrawingTime());
		drawChild(canvas, view_NextDay, getDrawingTime());
		drawChild(canvas, view_PerveDay, getDrawingTime());
		drawChild(canvas, view_timeButton, getDrawingTime());
		drawChild(canvas, view_EquiptSpinner, getDrawingTime());
		drawChild(canvas, view_text, getDrawingTime());
		
//		Log.e("Ks_EventList->dispatchDraw>>"," into！！！！"); 
//		new thread().run();
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_EventList->onLayout","into"); 
		table.notifyTableLayoutChange(0, 48, v_iWidth, v_iHeight);
		
		if(table.m_bUseTitle==false){
			int title_Height=18;
			 for(int i=0;i<textView_titles.length;i++){
				 textView_titles[i].layout(i*v_iWidth/textView_titles.length, 30, 
						      (i+1)*v_iWidth/textView_titles.length, 30+title_Height);
			 }
		}
		
		int width_x = v_iWidth/5;
		view_EquiptSpinner.layout(0, 0, width_x*1-10, 30); 
		view_text.layout(0, 0, width_x*1-10, 30); 
		view_timeButton.layout(width_x*1, 0, width_x*2-10, 30);
		view_PerveDay.layout(width_x*2, 0, width_x*3-10, 30);
		view_NextDay.layout(width_x*3, 0, width_x*4-10, 30);
		view_Receive.layout(width_x*4, 0, width_x*5-10, 30);
		
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);         //该表格控件会影响 滑屏 屏蔽了触摸响应
//		Log.e("Ks_EventList-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_EventList->doLayout","into");
		
		this.layout(v_iPosX, v_iPosY, v_iPosX+v_iWidth, v_iPosY+v_iHeight); //绘制该view底板layout
		 
	}
	
	//调用invalidate() 控件更新->onDraw()调用函数
	public void doInvalidate(){

//			Log.e("Ks_EventList-doInvalidate","table.update");
			table.update();  //更新表格内容  
		    //this.invalidate();
	}
	//调用requestLayout() 底板更新->onLayout()调用函数
	public void doRequestLayout(){
			this.requestLayout();
	}
	//调用addView()方法 将该视图添加进入父视图
	public boolean doAddViewsToWindow(Page window){
		//屏幕适配处理
		v_iPosX = (int)((float)v_iPosX * window.w_screenPer);
		v_iPosY = (int)((float)v_iPosY * window.h_screenPer);
		v_iWidth = (int)((float)v_iWidth * window.w_screenPer);
		v_iHeight = (int)((float)v_iHeight * window.h_screenPer);
		
		m_MainWindow = window;
		window.addView(this);
		return true;
	}
	
	//获取该控件类
	public View getViews(){
		return this;
	}
	//获取控件ID
	public String getViewsID() {
		return v_strID;
	}
	//获取控件类型
	public String getViewsType() {
		return v_strType;
	}
	//获取控件的图层序号
	public int getViewsZIndex(){
		return v_iZIndex;
	}
	//获取控件绑定表达式
	public String getViewsExpression() {
		return v_strExpression;
	}
	//获取是否更新view标识
	public boolean getNeedUpdateFlag(){
		return v_bNeedUpdateFlag;
	}

	
	//设置控件的id
	public boolean setViewsID(String id){
		v_strID = id;
		return true;
	}
	//设置控件的type
	public boolean setViewsType(String type){
		v_strType = type;
		return true;
	}
	//获取控件的图层序号
	public boolean setViewsZIndex(int n){
		v_iZIndex = n;
		return true;
	}
	//获取控件绑定表达式
	public boolean setViewsExpression(String strExpression) {
		v_strExpression = strExpression;
		return true;
	}
	//设置是否更新view标识
	public boolean setNeedUpdateFlag(boolean b_flag){
		v_bNeedUpdateFlag = b_flag;
		return true;
	}
	//更新控件数值函数     传入字符串  返回是否数值写入成功
	public boolean  updataValue(String strValue) {
		return false;
	}
	
	//控件布局参数setGravity
	public boolean setGravity(){
		return true;
	}
	//解析控件的相关参数
	public boolean setProperties(String strName, String strValue, String path){
			 if ("ZIndex".equals(strName))
		       	 	v_iZIndex = Integer.parseInt(strValue);	       	  
		     else if ("Location".equals(strName)) {
			       	String[] arrStr = strValue.split(",");
			        v_iPosX = Integer.parseInt(arrStr[0]);
			       	v_iPosY = Integer.parseInt(arrStr[1]);
		      }
		      else if ("Size".equals(strName)) {
			       	String[] arrSize = strValue.split(",");
			        v_iWidth = Integer.parseInt(arrSize[0]);
			        v_iHeight = Integer.parseInt(arrSize[1]);
		      }

			  else if ("OddRowBackground".equals(strName)) 
				 table.m_cOddRowBackground = Color.parseColor(strValue); 
			  else if ("EvenRowBackground".equals(strName)) 
				  table.m_cEvenRowBackground = Color.parseColor(strValue); 
			 
		     else if ("Alpha".equals(strName)) 
		       	 	v_fAlpha = Float.parseFloat(strValue);
		     else if ("RotateAngle".equals(strName)) 
		        	v_fRotateAngle = Float.parseFloat(strValue);
		     else if ("Content".equals(strName)) 
		        	v_strContent = strValue;
		     else if ("FontFamily".equals(strName))
		        	v_strFontFamily = strValue;
		     else if ("FontSize".equals(strName)) 	   
		        	v_fFontSize = Float.parseFloat(strValue);	    	
		     else if ("IsBold".equals(strName))
		       	 	v_bIsBold = Boolean.parseBoolean(strValue);
		     else if ("ForeColor".equals(strName)){  //前景色 即 字体颜色
		    	 	table.m_cFontColor = Color.parseColor(strValue); 
		    	 	v_iFontColor = Color.parseColor(strValue); 
		     }
		  //   else if ("FontColor".equals(strName)) 
		  //      	v_iFontColor = Color.parseColor(strValue); 
		     else if ("BackgroundColor".equals(strName)) //背景色
		        	v_iBackgroundColor = Color.parseColor(strValue); 
		     else if ("HorizontalContentAlignment".equals(strName))
		       	 	v_strHorizontalContentAlignment = strValue;
		     else if ("VerticalContentAlignment".equals(strName))
		       	 	v_strVerticalContentAlignment = strValue;
		     else if ("Expression".equals(strName))   
		       	 	v_strExpression = strValue;          //请求数据表达式
		     else if ("CmdExpression".equals(strName)) 
		        	v_strCmdExpression = strValue;      //控制命令表达式
		     else if("ColorExpression".equals(strName))
		        	v_strColorExpression = strValue;    //字体颜色变化表达式	
		     else if("ClickEvent".equals(strName))
		        	v_strClickEvent = strValue;         //点击事件表达式
		     else if("Url".equals(strName))
		        	v_strUrl = strValue;                //网页链接表达式网址
			 return true;
		}
	@Override
	public boolean parseExpression(String str_bindExpression) {
		// TODO Auto-generated method stub
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		//告警列表   只会单绑定  单项运算
		if(bindExpression==null) return false;
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
		expression = expression_lst.get(0);
		equiptId = expression.equip_ExId;
		
		return true;
	}

}
