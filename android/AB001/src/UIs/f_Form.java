package UIs;

import com.example.ab001.MainWindow;

import common.VObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//自定义控件Form 底板控件 主要设置底板的颜色大小
public class f_Form extends ViewGroup implements VObject{
	public f_Form(Context context) {
		super(context);
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Label";           //控件类型
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
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	//定义控件使用的元素
	//辅助变量
			
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{
//		Log.e("From-dispatchDraw","into");
		super.dispatchDraw(canvas);				    
		canvas.drawColor(v_iBackgroundColor);   //设置viewgroup的底板颜色 
			
	}
	//重写onLayout() 绘制viewGroup中所有的子view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Label-onLayout","into"); 		
						
	}
	//重写触摸事件onTouchEvent()  由于是Form 触摸事件无用处就把触摸事件屏蔽了
/*	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		Log.e("form-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return true;
	}
*/	
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Label-doLayout","into");
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
	public boolean doAddViewsToWindow(MainWindow window){
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
		if("自更新".equals(strValue)) return false;
		
		v_bNeedUpdateFlag = false;
		v_strContent = strValue;

		return true;
	}
	//控件布局参数setGravity
	public boolean setGravity(){
		return true;
	}
	//解析控件的相关参数   //Form 控件参数与其他控件参数有些不同
	public boolean setProperties(String strName, String strValue){
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
}
