package UIs;

import java.text.DecimalFormat;
import java.util.List;

import utils.BindExpression;
import utils.Calculator;
import utils.Expression;
import utils.RealTimeValue;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
//import data.pool.DataPoolModel;
//import data.pool_model.Signal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//自定义控件Label  使用new TextView方式
public class Ks_Label extends ViewGroup implements VObject{

	public Ks_Label(Context context) {
		super(context);
		//实例化该控件的组合元素控件
		textview = new TextView(context);
		//子元素添加到该容器上
		addView(textview);
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Label";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "Binding{[Value[Equip:114-Temp:173-Signal:1]]}";//控件绑定表达式
	                      //还支持绑定信号告警等级Binding{[EventSeverity[Equip:2-Temp:175-Signal:1]]}
	int v_iPosX = 100,v_iPosY = 100;       //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //控件线条的颜色
	int  v_iStartFontColor = 0xFF008000;         //控件线条的颜色
	String v_strContent = "设置内容";        //控件字符内容
	String v_strFontFamily = "微软雅黑";      //控件文字类型 
	boolean v_bIsBold = false;               //控件线条是否加粗
	String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
	String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
	
	//字体元素对齐变量
	int v_iHorizontalGravity = Gravity.CENTER_HORIZONTAL;
	int v_iVerticalGravity = Gravity.CENTER_VERTICAL;
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_Page = null;         //主页面类
	//定义控件使用的元素
	TextView textview;
	//辅助变量
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	int times = 0;
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件    
	{		
		super.dispatchDraw(canvas);	

//		canvas.drawColor(Color.LTGRAY);   //设置viewgroup的底板颜色   
		//设置子控件元素参数                                                                             //由于该屏幕原因为使和前软件字体一致*1.2倍
		textview.setTextSize(v_fFontSize/MainActivity.densityPer*1.2f); //做字体不同屏幕的适配
		textview.setTextColor(v_iFontColor);		
		textview.setGravity(v_iHorizontalGravity|v_iVerticalGravity);
		textview.setText(v_strContent);
		textview.getPaint().setStrokeWidth(1);
		textview.getPaint().setFakeBoldText(v_bIsBold);	 
//		textview.getPaint().setAntiAlias(true);
		textview.setTypeface(Typeface.SERIF);
	
		//绘制子view
		drawChild(canvas, textview, getDrawingTime());
//		Log.e("Label-dispatchDraw","into"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) { 
//		Log.e("Label-onLayout","into"); 
	//	if(bool)
			textview.layout(0, 0, v_iWidth, v_iHeight);  //待测试该正规范	
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
	//	Log.e("Ks_Label->onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;  //返回false  让page 能捕获到onTouch();
	}
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
	public boolean doAddViewsToWindow(Page window){
		//屏幕适配处理
		v_iPosX = (int)((float)v_iPosX * window.w_screenPer);
		v_iPosY = (int)((float)v_iPosY * window.h_screenPer);
		v_iWidth = (int)((float)v_iWidth * window.w_screenPer);
		v_iHeight = (int)((float)v_iHeight * window.h_screenPer);
		
		m_Page = window;
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
	//设置控件的图层序号
	public boolean setViewsZIndex(int n){
		v_iZIndex = n;
		return true;
	}
	//设置控件绑定表达式
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
		v_bNeedUpdateFlag = false;
		if(bindExpression==null) return false;
		//Label控件 只会是单 绑定表达式  数值  故 直接获取get(0);  
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);		
		RealTimeValue realTimeValue = new RealTimeValue();	
		String newValue = realTimeValue.getRealTimeValue(expression_lst);
		Log.i("Ks_Label->updataValue-JKTAG",  newValue);
		if("".equals(newValue)) newValue = "0.0"; //表示未获取到有效数据
		if(v_strContent.equals(newValue) ) return false; //数值未改变 不更新 
		parseFontcolor(newValue);    //解析字体颜色 
		if("".equals(realTimeValue.strResultMeaning)){  //判断是 模拟量    
			v_strContent = newValue;
		}else{                                          //判断是 数字量
			v_strContent = realTimeValue.strResultMeaning;
		}		
		realTimeValue = null;
		return true;
	}
	//颜色解析函数  传入参数：显示值   fang
	public int parseFontcolor(String strValue)
	{
		v_iFontColor = v_iStartFontColor;
		if( (v_strColorExpression == null)||("".equals(v_strColorExpression)) ) return -1;
		if( (strValue == null)||("".equals(strValue)) ) return -1;
		if("0.0".equals(strValue)) return -1;		
		if( (">".equals(v_strColorExpression.substring(0,1)))!=true ) return -1;
	
		String buf[] = v_strColorExpression.split(">"); //提取表达式中的条件与颜色单元
		for(int i=1;i<buf.length;i++){
			String a[] = buf[i].split("\\[|\\]"); //处理分隔符[ ]			
//			Log.e("Label-updataValue", "比较值"+a[0]+"+颜色数值："+a[1]);
			//比较数值	
			float data = Float.parseFloat(a[0]); //获得比较值
			float value = Float.parseFloat(strValue); //输入值
			if(value > data){
				v_iFontColor = Color.parseColor(a[1]);
			}
		}	
		return v_iFontColor;
	}
	//处理绑定表达式
	public boolean parseExpression(String str_bindExpression){
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		return true;
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
		     else if ("FontColor".equals(strName)){
		    	 v_iStartFontColor = Color.parseColor(strValue); 
		    	 v_iFontColor = v_iStartFontColor;
		     }		        	
		     else if ("BackgroundColor".equals(strName)) 
		        	v_iBackgroundColor = Color.parseColor(strValue); 
		     else if ("HorizontalContentAlignment".equals(strName)){
		    	 v_strHorizontalContentAlignment = strValue;
		    	 if("Center".equals(v_strHorizontalContentAlignment)){
		    		 v_iHorizontalGravity = Gravity.CENTER_HORIZONTAL;
		    	 }else if("Left".equals(v_strHorizontalContentAlignment)){
		    		 v_iHorizontalGravity = Gravity.LEFT;
		    	 }else if("Right".equals(v_strHorizontalContentAlignment)){
		    		 v_iHorizontalGravity = Gravity.RIGHT;
		    	 }
		     }	
		     else if ("VerticalContentAlignment".equals(strName)){
		       	 	v_strVerticalContentAlignment = strValue;
		       	 if("Center".equals(v_strVerticalContentAlignment)){
		       		v_iVerticalGravity = Gravity.CENTER_VERTICAL;
		    	 }else if("Top".equals(v_strVerticalContentAlignment)){
		    		 v_iVerticalGravity = Gravity.TOP;
		    	 }else if("Bottom".equals(v_strVerticalContentAlignment)){
		    		 v_iVerticalGravity = Gravity.BOTTOM;
		    	 }
		     }
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
