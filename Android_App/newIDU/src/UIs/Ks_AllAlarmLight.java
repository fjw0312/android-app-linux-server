package UIs;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Event;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//自定义    系统总体告警灯  
public class Ks_AllAlarmLight extends ViewGroup implements VObject{
	public Ks_AllAlarmLight(Context context) {
		super(context);
		// load image 
		try {
					AssetManager assetManager = this.getContext().getResources().getAssets();
					InputStream is = null;
					
					if (null == s_bitAlarmLevel1Image)
					{
						is = assetManager.open("ui/Alarm_green.png");
						s_bitAlarmLevel1Image = BitmapFactory.decodeStream(is);
						is.close();
					}

					if (null == s_bitAlarmLevel2Image)
					{
						is = assetManager.open("ui/Alarm_red.png");
						s_bitAlarmLevel2Image = BitmapFactory.decodeStream(is);
						is.close();
					}

					if (null == s_bitAlarmLevel3Image)
					{
						is = assetManager.open("ui/Alarm_redRun.png");
						s_bitAlarmLevel3Image = BitmapFactory.decodeStream(is);
						is.close();
					}

					m_bitCurrentAlarmImage = s_bitAlarmLevel1Image;
			} catch (IOException e) {
					e.printStackTrace();
			}
		m_oPaint = new Paint();
		m_rSrcRect = new Rect();
		m_rDestRect = new Rect();
		myThread thread = new myThread();
		thread.start();
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "AllAlarmLight";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "";          //控件绑定表达式
	int v_iPosX = 0,v_iPosY = 0;           //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //控件线条的颜色
	String v_strContent = "设置内容";        //控件字符内容
	String v_strFontFamily = "微软雅黑";      //控件文字类型
	boolean v_bIsBold = false;               //控件线条是否加粗
	String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
	String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
	
	String v_strImgPath = "";
	String v_strImage = "fjw_logo.jpg";
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	//定义控件使用的元素
	//辅助变量
	private static Bitmap s_bitAlarmLevel1Image = null; 
	private static Bitmap s_bitAlarmLevel2Image = null; 
	private static Bitmap s_bitAlarmLevel3Image = null; 
	Bitmap m_bitCurrentAlarmImage = null; 
	Paint m_oPaint = null;
	Rect m_rSrcRect = null;
	Rect m_rDestRect = null;
	int EventNum = 0;  //告警数量
	int Flag = 0;   //记录目前告警状态
	
	boolean canVISIBLE = false;  //判断控件是否可见  标志变量
	myThread thread;	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
		//该控件的Visibility属性变化监听
		if(visibility == View.VISIBLE){     //控件可见
			canVISIBLE = true;
//			thread = new myThread();
//			thread.start();
		}else if(visibility == View.GONE){  //控件不可见
			if(canVISIBLE){
//				if(thread.isAlive()){
//					thread.interrupt();
//				}
			}
			canVISIBLE = false;
		}
	}	
			
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{
//		Log.e("Ks_AllAlarmLight-dispatchDraw","into");
		super.dispatchDraw(canvas);				    
		canvas.drawColor(v_iBackgroundColor);   //设置viewgroup的底板颜色 
		
		m_rSrcRect.left = 0;
		m_rSrcRect.top = 0;
		m_rSrcRect.right = m_bitCurrentAlarmImage.getWidth();
		m_rSrcRect.bottom = m_bitCurrentAlarmImage.getHeight();
		
		m_rDestRect.left = 0;
		m_rDestRect.top = 0;
		m_rDestRect.right = v_iWidth;
		m_rDestRect.bottom = v_iHeight;
		canvas.drawBitmap(m_bitCurrentAlarmImage, m_rSrcRect, m_rDestRect, m_oPaint);		
	}
	//重写onLayout() 绘制viewGroup中所有的子view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_AllAlarmLight-onLayout","into"); 		
						
	}
	//重写触摸事件onTouchEvent()  由于是Form 触摸事件无用处就把触摸事件屏蔽了
/*	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		Log.e("Ks_AllAlarmLight-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return true;
	}
*/	
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_AllAlarmLight-doLayout","into");
		this.layout(v_iPosX, v_iPosY, v_iPosX+v_iWidth, v_iPosY+v_iHeight); //绘制该view底板layout		
	}
			
	//调用invalidate() 控件更新->onDraw()调用函数
	public void doInvalidate(){
		this.invalidate();
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
	//	v_strContent = strValue;
		int num = 0;
		HashMap<Integer,List<Event>> allEvent_ht = NetDataModel.getAllEvent(); 	
		if(allEvent_ht == null){ 
			num = 0;
		}else{
			Iterator<Integer> equiptId_lst = allEvent_ht.keySet().iterator();
			while(equiptId_lst.hasNext()){
				int e_id = equiptId_lst.next();
				List<Event> itemEvent_lst = allEvent_ht.get(e_id);
				if(itemEvent_lst == null){ 
					num = num+0;
				}else{
					num = num+itemEvent_lst.size();
				}
			}
		}
		if(num==0){  //无告警状态
			Flag = 0;
		}else{       //有告警状态
			Flag = 1;
		}		
		return false;       //控件内部自己刷新
	}
	//handler
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1: 
				if(Ks_AllAlarmLight.this.isShown())
					doInvalidate();
				break;
			default: break;
			}
		}
		
	};
	//界面实时刷新线程
	private class myThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int i = 0;
			while(true){ //实时刷新
				try {
					if(Flag == 0){  //无告警状态
						if(m_bitCurrentAlarmImage!=s_bitAlarmLevel1Image){
							m_bitCurrentAlarmImage = s_bitAlarmLevel1Image;					
							myHandler.sendEmptyMessage(1);
						}
						Thread.sleep(500);
					}else if(Flag==1){ //有告警
						m_bitCurrentAlarmImage = s_bitAlarmLevel2Image;	
						myHandler.sendEmptyMessage(1);
						Thread.sleep(500);					
						m_bitCurrentAlarmImage = s_bitAlarmLevel3Image;
						myHandler.sendEmptyMessage(1);
						Thread.sleep(500);
					}
					i++;
					if(i>2*10){  //20次循环 再刷新一次
						myHandler.sendEmptyMessage(1);
						i = 0;
					}
				}catch(InterruptedException e){					
					return;	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}		
	}
	//控件布局参数setGravity
	public boolean setGravity(){
		return true;
	}
	//解析控件的相关参数   //Form 控件参数与其他控件参数有些不同
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
		     else if ("FontColor".equals(strName)) 
		        	v_iFontColor = Color.parseColor(strValue); 
		     else if ("BackColor".equals(strName)) 
		        	v_iBackgroundColor = Color.parseColor(strValue); 
		     else if ("BackImage".equals(strName)){
		    	 v_strImgPath = path;
		    	 v_strImage = strValue;
		     }
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
		return false;
	}	
}
