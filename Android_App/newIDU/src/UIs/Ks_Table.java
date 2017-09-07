package UIs;


import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//自定义控件Table  使用new draw canvas方式
public class Ks_Table extends ViewGroup implements VObject{

	public Ks_Table(Context context) {
		super(context);
		//实例化该控件的组合元素控件

		//子元素添加到该容器上

	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Table";           //控件类型
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
	
	int v_iRadius = 0;   //圆角半径
	int v_iRowNum = 1;  //行数
	int v_iColNum = 1;  //列数 
	boolean v_bIsHasHead = false;   //是否有表头
	float v_fHeadPer = (float)0.3;   //表格头的比例大小
	float v_fFirstRowPer = (float)0.3;  //第一行的比例大小
	float v_fFirstColPer = (float)0.3;  //第一列的比例大小
	int v_iHeadBackgroundColor = 0x00000000;    //表头颜色
	int v_iFirstRowBackgroundColor = 0x00000000; //第一行颜色
	int v_iFirstColBackgroundColor = 0x00000000; //第一列颜色
	int v_iTableBackgroundColor = 0x00000000; //表格中间内容颜色
	int v_iLineThickness =3; //线条大小
	int v_iLineColor = 0xFF000000;  //线条颜色
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	//定义控件使用的元素

	//辅助变量

		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //设置viewgroup的底板颜色 
		
		if(v_bIsHasHead==false){
			v_iFirstRowBackgroundColor=v_iHeadBackgroundColor;
		}else{
			v_iFirstRowBackgroundColor = v_iTableBackgroundColor;
		}
		
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果     
		mPaint.setStrokeWidth(v_iLineThickness);    //设置线条宽度
		RectF rectf = new RectF();
		rectf.left = 0+v_iLineThickness/2;
		rectf.top = 0+v_iLineThickness/2;
		rectf.right = v_iWidth-v_iLineThickness/2;
		rectf.bottom = v_iHeight-v_iLineThickness/2;
		
		//处理是否有表头 表头比例与第一行比例
		if(v_bIsHasHead){
			v_fHeadPer = v_fFirstRowPer;
			v_fFirstRowPer = 0;
			v_iRowNum++;
		}
		
		//绘制表头
		RectF rectf1 = new RectF();
		rectf1.left = rectf.left;
		rectf1.top = rectf.top;
		rectf1.right = rectf.left;
		rectf1.bottom = rectf.top;
		if(v_bIsHasHead){			
			rectf1.left = rectf.left;
			rectf1.top = rectf.top;
			rectf1.right = rectf.right;
			rectf1.bottom = rectf.left+v_iHeight*v_fHeadPer;
			mPaint.setColor(v_iHeadBackgroundColor);
			mPaint.setStyle(Paint.Style.FILL);  
			canvas.drawRoundRect(rectf1, v_iRadius, v_iRadius, mPaint);
			mPaint.setColor(v_iLineColor);
			mPaint.setStyle(Paint.Style.STROKE);  
			canvas.drawRoundRect(rectf1, v_iRadius, v_iRadius, mPaint);
		}
		//绘制表的第一行 底板颜色
		RectF rectf2 = new RectF();
		rectf2.left = rectf.left;
		rectf2.top = rectf1.bottom;
		rectf2.right = rectf.right; 
		rectf2.bottom = rectf1.bottom + v_iHeight*v_fFirstRowPer;
		mPaint.setColor(v_iFirstRowBackgroundColor);
		mPaint.setStyle(Paint.Style.FILL);  
		canvas.drawRoundRect(rectf2, v_iRadius, v_iRadius, mPaint);
 
		
		//绘制表的第一列 底板颜色
		RectF rectf3 = new RectF();
		rectf3.left = rectf.left;
		rectf3.top = rectf1.bottom;
		rectf3.right = rectf.left + v_iWidth * v_fFirstColPer;
		rectf3.bottom = rectf.bottom ;
		mPaint.setColor(v_iFirstColBackgroundColor);
		mPaint.setStyle(Paint.Style.FILL);  
		canvas.drawRoundRect(rectf3, v_iRadius, v_iRadius, mPaint);

		//绘制表的中间 底板颜色
		RectF rectf4 = new RectF();
		rectf4.left = rectf3.right;
		rectf4.top = rectf2.bottom;
		rectf4.right = rectf.right;
		rectf4.bottom = rectf.bottom;
		mPaint.setColor(v_iTableBackgroundColor);
//		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL);  
		canvas.drawRoundRect(rectf4, v_iRadius, v_iRadius, mPaint); 
		
		//绘制表格中间线条  横线
		mPaint.setColor(v_iLineColor);
		float y_per = (rectf.bottom-rectf2.bottom)/(v_iRowNum-1);  
		for(int i=0;i<v_iRowNum-1;i++){
			canvas.drawLine(rectf.left, rectf2.bottom+y_per*i, rectf.right, rectf2.bottom+y_per*i, mPaint);
		}
		//绘制表格中间线条  竖线
		float x_per = (rectf.right-rectf3.right)/(v_iColNum-1); 
		for(int i=0;i<v_iColNum-1;i++){
			canvas.drawLine(rectf3.right+x_per*i, rectf2.top, rectf3.right+x_per*i, rectf.bottom, mPaint);
		}
   
		//绘制 表格 边框
		mPaint.setColor(v_iLineColor);
		mPaint.setStyle(Paint.Style.STROKE); 
		canvas.drawRoundRect(rectf2, v_iRadius, v_iRadius, mPaint);//绘制第一行边框
		canvas.drawRoundRect(rectf3, v_iRadius, v_iRadius, mPaint);//绘制第一行边框
		canvas.drawRoundRect(rectf, v_iRadius, v_iRadius, mPaint);//绘制外边框
//		Log.e("ks_Table>>onDraw","into"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("ks_Table>>onLayout","into"); 		
		
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_Table-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("ks_Table>>doLayout","into"); 
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
	
		return false;   //不刷新 控件
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
			 
			  else if ("RowNum".equals(strName)) 
			       	 	v_iRowNum = Integer.parseInt(strValue);
			  else if ("ColNum".equals(strName)) 
		       	 		v_iColNum = Integer.parseInt(strValue);
			  else if ("IsHasHead".equals(strName)){
				  if("True".equals(strValue)){
					  v_bIsHasHead = true; 
				  }
				  else  v_bIsHasHead = false;
			  }
			  else if ("HeadPer".equals(strName)) 
	       	 			v_fHeadPer = Float.parseFloat(strValue);
			  else if ("FirstRowRatio".equals(strName)) 
	       	 			v_fFirstRowPer = Float.parseFloat(strValue);
			  else if ("FirstColRatio".equals(strName)) 
				  		v_fFirstColPer = Float.parseFloat(strValue);
			  else if ("HeadBackgroundColor".equals(strName)) 
				  		v_iHeadBackgroundColor = Color.parseColor(strValue); 
			  else if ("FirstRowBackgroundColor".equals(strName)) 
				  		v_iFirstRowBackgroundColor = Color.parseColor(strValue); 
			  else if ("FirstColBackgroundColor".equals(strName)) 
			  		v_iFirstColBackgroundColor = Color.parseColor(strValue); 
			  else if ("TableBackgroundColor".equals(strName)) 
			  		v_iTableBackgroundColor = Color.parseColor(strValue); 
			  else if ("LineThickness".equals(strName)) 
				  v_iLineThickness = Integer.parseInt(strValue);
			  else if ("LineColor".equals(strName)) 
			  		v_iLineColor = Color.parseColor(strValue); 
			 
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
		     else if ("Radius".equals(strName))
		    	 v_iRadius = Integer.parseInt(strValue);
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
