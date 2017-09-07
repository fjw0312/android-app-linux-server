package UIs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import SAM.DataPoolModel.Event;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

//import data.pool.DataPoolModel;
//import data.pool_model.Event;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


//自定义控件Ks_EventList 总告警列表 使用new draw canvas方式  
//注意，由于表格需要将数据保存到表适配里，出现 过快刷新清除表格数据内存  异常！
public class Ks_EventList extends ViewGroup implements VObject{

	public Ks_EventList(Context context) {
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
		 lstTitles.add("告警名称");
		 lstTitles.add("告警含义");
		 lstTitles.add("告警等级");
		 lstTitles.add("开始时间");
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

		 
		//设置列表滑屏监听  主要作用：当检测到有滑屏click1=false  停止滑屏后click1=true;
			this.table.setOnScrollListener(new OnScrollListener() {	
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					// TODO Auto-generated method stub
					switch(arg1){
					case OnScrollListener.SCROLL_STATE_IDLE:  //当屏幕停止滚动
						
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"屏幕停止滚动！");
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //屏幕滚动 
						
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"屏幕滚动!");
						break;
					case OnScrollListener.SCROLL_STATE_FLING:   //手指离开后屏幕惯性滚动
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
	String v_strType = "EventList";           //控件类型
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

	//辅助变量
	List<String> lstTitles = null;  //表格标题成员字符数组
	List<List<String>> lstContends = null; //表格字符串成员数组
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数      信号列表 只处理单项绑定
	Expression expression = null; //表达式子项类


	boolean isCanUpdateViewFlag1 = true;
	boolean isCanUpdateViewFlag2 = true;
	long nowTime = 10000; //10s
	long oldTime = 0;
	
	
	// 延时 2s 刷新数据线程   等待 view 的刷新结束！
	private class thread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try{
			Thread.sleep(1800);
			}catch(Exception e){				
			}
			isCanUpdateViewFlag1 = true;
		//	Log.e("Ks_EventList->thread>>","  Thread end !");
		}	
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
				textView_titles[i].setTextSize(v_fFontSize/MainActivity.densityPer*1.2f);
			}	
		}
		
//		Log.e("Ks_EventList->dispatchDraw>>"," into！！！！");  
//		new thread().run();
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_EventList->onLayout","into"); 
		table.notifyTableLayoutChange(0, 20, v_iWidth, v_iHeight);
		
		if(table.m_bUseTitle==false){
			int title_Height=18;
			 for(int i=0;i<textView_titles.length;i++){
				 textView_titles[i].layout(i*v_iWidth/textView_titles.length, 0, 
						      (i+1)*v_iWidth/textView_titles.length, v_iPosY+title_Height);
			 }
		}
		 
		
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
		
		if(expression==null) return false;
//		if(isCanUpdateViewFlag1==false)  return false;

		List<Event> event_lst = new ArrayList<Event>();
		event_lst.clear();
		//判断是获取单个设备告警列表 还是所有告警信息列表
		if(expression.equip_ExId == 0){ //获取所有告警数据列表		
			HashMap<Integer,List<Event>> allEvent_ht = NetDataModel.getAllEvent(); 	
			if(allEvent_ht == null) return false;
			Iterator<Integer> equiptId_lst = allEvent_ht.keySet().iterator();
			while(equiptId_lst.hasNext()){
				int e_id = equiptId_lst.next();
				List<Event> itemEvent_lst = allEvent_ht.get(e_id);
				if(itemEvent_lst == null) continue;
				for(int i=0;i<itemEvent_lst.size();i++){
					event_lst.add(itemEvent_lst.get(i));
				}
			}
		}else{  //获取某个设备告警列表
			event_lst = NetDataModel.getHtEvent(expression.equip_ExId); 
		}
		nowTime = java.lang.System.currentTimeMillis(); 
		if(event_lst==null || event_lst.size()==0){

			if(nowTime-oldTime > 3800){ //3s以上 ~~4s才刷新一次 
				oldTime = nowTime;
				lstContends.clear();
				table.updateContends(lstTitles,lstContends);  //将表格头 表格行内容 填入表格  此处有内存增大的现象
				return true;
			}else{
				return false;
			}
		}
		
		isCanUpdateViewFlag1 = false;

		lstContends.clear(); 
		for(int i=0; i<event_lst.size(); i++){
			List<String> lstRow = new ArrayList<String>();
			lstRow.clear();
			Event event = event_lst.get(i);
			if(event==null) continue;
			lstRow.add(event.equipName);  //设备名称
			lstRow.add(event.name);       //告警名称
			lstRow.add(event.meaning);    //告警含义
			lstRow.add( String.valueOf(event.grade) );      //告警等级
			lstRow.add(UtTable.getDate(event.starttime * 1000, "yyyy.MM.dd HH:mm:ss"));  //开始时间 
		//	Log.i("Ks_EventList>>updataValue>>",event.equipName+
		//						"  "+event.name+"  "+event.meaning);
			lstContends.add(lstRow);  //添加表格行 
		}
		table.updateContends(lstTitles,lstContends);  //将表格头 表格行内容 填入表格  此处有内存增大的现象		

		if(nowTime-oldTime > 3800){ //3s以上 ~~4s才刷新一次 
			oldTime = nowTime;
			return true;
		}else{
			return false;
		}
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
		
		return true;
	}

}
