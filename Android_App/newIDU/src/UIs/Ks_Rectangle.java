package UIs;


import java.util.List;

import utils.BindExpression;
import utils.Expression;
import utils.RealTimeValue;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//自定义控件Rectangle  使用new TextView方式   目前 未处理渐变色  数值绑定 颜色改变功能
public class Ks_Rectangle extends ViewGroup implements VObject{

	public Ks_Rectangle(Context context) {
		super(context);
		//实例化该控件的组合元素控件

		//子元素添加到该容器上

	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Rectangle";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "Binding{[Value[Equip:114-Temp:173-Signal:1]]}"; //控件绑定表达式
    //还支持绑定信号告警等级Binding{[EventSeverity[Equip:2-Temp:175-Signal:1]]}
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
	int v_iFillColor=0x00000000; //方块内部填充颜色
	int v_iBorderColor =0xFF000000; //方块边框颜色
	int v_iBorderWidth = 1;  //边框线条大小
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	//定义控件使用的元素

	//辅助变量
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	
	//辅助变量
	boolean v_bIsHGradient = true; // 水平渐变
	int v_iSingleFillColor = 0x00000000;
	int v_iStartFillColor = 0x00000000;
	float[] v_fGradientColorPos = null;
	int[] v_iGradientFillColor = null;
	float v_fAlpha_2 = 1.0f;                 //控件相位

		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //设置viewgroup的底板颜色 
		//绘制方块矩形
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果     
		RectF rectf = new RectF();
		rectf.left = 0+v_iBorderWidth/2;
		rectf.top = 0+v_iBorderWidth/2;
		rectf.right = v_iWidth-v_iBorderWidth/2;
		rectf.bottom = v_iHeight-v_iBorderWidth/2;   
        // 0,0.4,#FFC0C0C0,0,#FF585858,0.5,#FFC0C0C0,1
        // 渐变颜色和渐变点
        if (v_fGradientColorPos != null) {
		    LinearGradient lg = null;
		    if (v_bIsHGradient) {
		        lg = new LinearGradient(0, v_iHeight/2, v_iWidth, v_iHeight/2, v_iGradientFillColor, 
		        		v_fGradientColorPos, TileMode.MIRROR);
		    }
		    else {
		        lg = new LinearGradient(v_iWidth/2, 0, v_iWidth/2, v_iHeight, v_iGradientFillColor, 
		        		v_fGradientColorPos, TileMode.MIRROR);      	
		    }
		    mPaint.setShader(lg);
        }
        else
        	mPaint.setColor(v_iSingleFillColor); // 仅填充单色
        
		//绘制矩形填充颜色
		mPaint.setStrokeWidth(1);    //设置线条宽度
//		mPaint.setColor(v_iFillColor);
		mPaint.setStyle(Paint.Style.FILL); 
		canvas.drawRoundRect(rectf, v_iRadius,v_iRadius, mPaint);  
		//绘制矩形边框
		if(v_iBorderWidth != 0){
			mPaint.setStrokeWidth(v_iBorderWidth);    //设置线条宽度
			mPaint.setColor(v_iBorderColor);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRoundRect(rectf, v_iRadius,v_iRadius, mPaint);	
		}
		
//		Log.e("Ks_Rectangle>>dispatchDraw","into"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Rectangle>>onLayout","into"); 		
		
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Rectangle-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Rectangle>>doLayout","into"); 
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
		v_bNeedUpdateFlag = false;
		if(bindExpression==null) return false; 
		//Label控件 只会是单 绑定表达式  数值  故 直接获取get(0);
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);

		RealTimeValue realTimeValue = new RealTimeValue();	
		String newValue = realTimeValue.getRealTimeValue(expression_lst);
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
		v_iSingleFillColor = v_iStartFillColor;
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
				v_iSingleFillColor = Color.parseColor(a[1]);
			}
		}	
		return v_iSingleFillColor;
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
		     else if ("Radius".equals(strName))
		    	 v_iRadius = Integer.parseInt(strValue);
		     else if ("BorderColor".equals(strName)) 
		        	v_iBorderColor = Color.parseColor(strValue); 
		     else if ("BorderWidth".equals(strName)) 
		        	v_iBorderWidth = Integer.parseInt(strValue); 
		     else if ("FillColor".equals(strName)){
		    	 String[] arrStr = strValue.split(",");
		        	if (arrStr.length == 1) {      //判断是纯色还是渐变色
		        		v_iSingleFillColor = Color.parseColor(strValue);
		        		v_iStartFillColor = v_iSingleFillColor;
		        	}
		        	else {
		        		if (Integer.parseInt(arrStr[0]) == 0)
		        			v_bIsHGradient = false;
		        		else
		        			v_bIsHGradient = true;
		        		v_fAlpha = Float.parseFloat(arrStr[1]);
		        		
		        		int nCount = (arrStr.length - 2) / 2;
		        		v_fGradientColorPos = new float[nCount];
		        		v_iGradientFillColor = new int[nCount];
		        		int nIndex = 0;
		        		for (int i = 2; i < arrStr.length; i += 2) {	
		        			int color = Color.parseColor(arrStr[i]);
		        			v_iGradientFillColor[nIndex] = Color.argb((int)(Color.alpha(color)*v_fAlpha), Color.red(color), Color.green(color), Color.blue(color));
		        			v_fGradientColorPos[nIndex] = Float.parseFloat(arrStr[i+1]);
		        			nIndex++;
		        		}
		        	}
		     }
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
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		return true;
	}


}
