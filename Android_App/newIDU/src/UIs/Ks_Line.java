package UIs;



import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//直线
//自定义控件Line  使用绘制底板drawText方式
public class Ks_Line extends ViewGroup implements VObject{

	public Ks_Line(Context context) {
		super(context);
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Line";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "";          //控件绑定表达式
	int v_iPosX = 100,v_iPosY = 100;       //控件坐标
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
	//特别参数
	float m_fStartPointX = 0;
	float m_fStartPointY = 0;
	float m_fEndPointX = 400;
	float m_fEndPointY = 400;
	float width =1;
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	//定义控件使用的元素
	//辅助变量
	float x_lenth,y_lenth;
	float x_start,y_start;
	float x_end,y_end;
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //设置viewgroup的底板颜色 
		
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果     
		mPaint.setStrokeWidth(width);    //设置线条宽度
		//mPaint.setTextSize(v_fFontSize);
		mPaint.setColor(v_iFontColor);
		canvas.drawLine(x_start, y_start,x_end, y_end, mPaint); //写一个文字字符	文字起点坐标 30 50
//		Log.e("Ks_Line-dispatchDraw","into"); 	
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Line-onLayout","into"); 		
	}
/*	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		Log.e("Ks_Line-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return true;
	}
*/
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Line-doLayout","into");
		float g_x1 = 0,g_x2 = 0;  //layout 底板rect 的2点
		float g_y1 = 0,g_y2 = 0;
	
		//提取2点的layout 坐标
		if( (m_fStartPointX<=m_fEndPointX)&&(m_fStartPointY<=m_fEndPointY) ){      //4象 线
			x_lenth = m_fEndPointX-m_fStartPointX;
			y_lenth = m_fEndPointY - m_fStartPointY;
			g_x1 = m_fStartPointX;  g_y1 = m_fStartPointY;
			g_x2 = m_fEndPointX;  g_y2 = m_fEndPointY;			
			x_start = width; y_start = width;
			x_end = x_start + x_lenth; y_end = y_start + y_lenth;
			
		}else if( (m_fStartPointX<=m_fEndPointX)&&(m_fStartPointY>=m_fEndPointY) ){//2象 线
			x_lenth = m_fEndPointX-m_fStartPointX;
			y_lenth = m_fStartPointY - m_fEndPointY; 
			g_x1 = m_fStartPointX ; g_y1 = m_fEndPointY;
			g_x2 = m_fEndPointX; g_y2 = m_fStartPointY;
			x_start = width; y_start = width + y_lenth;
			x_end = x_start + x_lenth; y_end = width;
			
		}else if((m_fStartPointX>=m_fEndPointX)&&(m_fStartPointY<=m_fEndPointY)){  //3象 线
			x_lenth = m_fStartPointX-m_fEndPointX;
			y_lenth = m_fEndPointY - m_fStartPointY;
			g_x1 = m_fEndPointX ; g_y1 = m_fStartPointY;
			g_x2 = m_fStartPointX; g_y2 = m_fEndPointY;
			x_start = width + x_lenth;  y_start = width;
			x_end = width; y_end = width + y_lenth;
			
		}else if((m_fStartPointX>=m_fEndPointX)&&(m_fStartPointY>=m_fEndPointY)){  //1象 线
			x_lenth = m_fStartPointX-m_fEndPointX;
			y_lenth = m_fStartPointY - m_fEndPointY; 
			g_x1 = m_fEndPointX ; g_y1 = m_fEndPointY;
			g_x2 = m_fStartPointX; g_y2 = m_fStartPointY;
			x_start = width + x_lenth;  y_start = width + y_lenth;
			x_end = width; y_end = width;
		}
		this.layout((int)(g_x1-width), (int)(g_y1-width), (int)(g_x2+width), (int)(g_y2+width));	
//		Log.e("Ks_Line-doLayout","into"); 	
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
		
   	  m_fStartPointX = m_fStartPointX * window.w_screenPer;
   	  m_fStartPointY = m_fStartPointY * window.h_screenPer;
 	  m_fEndPointX = m_fEndPointX * window.w_screenPer;
 	  m_fEndPointY = m_fEndPointY * window.h_screenPer;
		
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

	//	v_strContent = strValue;
	
		return false;  //不需要 更新 控件 
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
		      else if ("StartPoint".equals(strName)) {
		        	 String[] arrStr = strValue.split(",");
		        	 m_fStartPointX = Float.parseFloat(arrStr[0]);
		        	 m_fStartPointY = Float.parseFloat(arrStr[1]);
		       }
		      else if ("EndPoint".equals(strName)) {
		        	 String[] arrStr = strValue.split(",");
		        	 m_fEndPointX = Float.parseFloat(arrStr[0]);
		        	 m_fEndPointY = Float.parseFloat(arrStr[1]);
		      }
			 else if ("Width".equals(strName)) 
				 	width = Float.parseFloat(strValue);
		     else if ("Color".equals(strName)) 
		        	v_iFontColor = Color.parseColor(strValue); 
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
		     else if ("BackgroundColor".equals(strName)) 
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
		return false;
	}
	
}
