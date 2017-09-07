package UIs;

import java.text.DecimalFormat;
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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//仪表盘        //目前该控件的设计 只支持单项绑定
public class Ks_DashBoard extends ViewGroup implements VObject{

	public Ks_DashBoard(Context context) {
		super(context);
		//实例化该控件的组合元素控件

		//子元素添加到该容器上
		
        m_oPaint = new Paint();
        m_rRectF1 = new RectF();
        m_rRectF2 = new RectF();
        m_rRectF3 = new RectF();

	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "DashBoard";        //控件类型
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
	String v_strContent = "";        //控件字符内容
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
	float maxValue = 100;  //表盘的最大值
	float minValue = 0;  //表盘的最小值
	int scale = 24;      //表盘的刻度
	int mode = 1;        //表盘样式
	float data_value = 10; //目前的表盘数值
	int m_nBorderWidth = 2;  //线条宽度
	int m_nfillWidth = 30;  //填充的圆环宽度
	String str_value="";
	float warnPer1 = (float)0.333;  //告警圆环阀值开始比例
	float warnPer2 = (float)0.334;  //告警圆环阀值开始比例
	int warnPerColor1 = 0xE54ACA4F;   //告警圆环颜色
	int warnPerColor2 = 0xDFE9E852;   //告警圆环颜色
	int warnPerColor3 = 0xE1E8373A;   //告警圆环颜色
	Paint m_oPaint = null;  
	RectF m_rRectF1 = null;
	RectF m_rRectF2 = null;
	RectF m_rRectF3 = null;
	int m_cFillColor = 0xFFF2C0FF;
	int m_cLineColor = 0xFF5EC5EE;

	//辅助变量
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	
	int  v_iStartFontColor = 0xFF008000;         //控件线条的颜色

		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //设置viewgroup的底板颜色 
		drawPanl(canvas);
//		Log.e("Ks_DashBoard>>dispatchDraw","into"); 
	}
	//绘制 表盘
	private void drawPanl(Canvas canvas){
		float angle = 270/(maxValue-minValue) * (data_value-minValue);
		int pad = m_nBorderWidth/2+4;  //外圆边距
		//表盘 绘制区域
				m_rRectF1.left = pad+m_nfillWidth/2;
				m_rRectF1.top = pad+m_nfillWidth/2;
				if(v_iWidth<v_iHeight){    //用小的一边长度   保证为圆不变形
					m_rRectF1.right = v_iWidth-pad-m_nfillWidth/2;;
					m_rRectF1.bottom = v_iWidth-pad-m_nfillWidth/2;
				}else{
					m_rRectF1.right = v_iHeight-pad-m_nfillWidth/2;
					m_rRectF1.bottom = v_iHeight-pad-m_nfillWidth/2;
				}
				float bb = m_rRectF1.right;
				 
					//画出外圆线
					m_rRectF2.left = m_rRectF1.left-m_nfillWidth/2;
					m_rRectF2.top = m_rRectF1.top-m_nfillWidth/2;
					m_rRectF2.right = m_rRectF1.right+m_nfillWidth/2;
					m_rRectF2.bottom = m_rRectF1.bottom+m_nfillWidth/2;
					m_oPaint.setColor(0x88000000);
					m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果
					m_oPaint.setStrokeWidth(m_nBorderWidth);
					m_oPaint.setStyle(Paint.Style.STROKE);
			        canvas.drawOval(m_rRectF2, m_oPaint);  
			        
					//画出第二圆线   间隔 出m_rRectF1.right/8 的外圆边条
					m_rRectF2.left = m_rRectF2.left + bb/(float)16;
					m_rRectF2.top = m_rRectF2.top + bb/(float)16;
					m_rRectF2.right = m_rRectF2.right - bb/(float)16;
					m_rRectF2.bottom = m_rRectF2.bottom - bb/(float)16;
					m_oPaint.setColor(m_nBorderWidth);
					m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果
					m_oPaint.setStrokeWidth(m_nBorderWidth);
					m_oPaint.setStyle(Paint.Style.STROKE);
			        canvas.drawOval(m_rRectF2, m_oPaint); 
			        
					//画出外圆环   间隔 出m_rRectF1.right/8 的外圆边条
			        RectF m_rRectF_b = new RectF();
			        m_rRectF_b.left = m_rRectF2.left - bb/(float)32;
			        m_rRectF_b.top = m_rRectF2.top - bb/(float)32;
			        m_rRectF_b.right = m_rRectF2.right + bb/(float)32;
			        m_rRectF_b.bottom = m_rRectF2.bottom + bb/(float)32;
					m_oPaint.setColor(0xCEE5E2E2);
					m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果
					m_oPaint.setStrokeWidth(m_rRectF1.right/(float)16);
					m_oPaint.setStyle(Paint.Style.STROKE);
			        canvas.drawOval(m_rRectF_b, m_oPaint);
			        
					//画出中间 覆盖 圆
			        float bfbf = 4;
					m_rRectF3.left = m_rRectF2.left+m_nBorderWidth/2 +bfbf;
					m_rRectF3.top = m_rRectF2.top+m_nBorderWidth/2 +bfbf;
					m_rRectF3.right = m_rRectF2.right-m_nBorderWidth/2 -bfbf;
					m_rRectF3.bottom = m_rRectF2.bottom-m_nBorderWidth/2 -bfbf;
					m_oPaint.setColor(0xFFFBFBFB);
			//		m_oPaint.setColor(m_cBorderColor); // 仅填充背景色
					m_oPaint.setStyle(Paint.Style.FILL);   
					canvas.drawOval(m_rRectF3, m_oPaint);	
			      			
						 //画出告警圆弧
						int hh = 10;  //圆弧线条宽度 
						RectF m_rRectF_h = new RectF();
						m_rRectF_h.left = m_rRectF3.left+hh/2; 
						m_rRectF_h.top = m_rRectF3.top+hh/2; 
						m_rRectF_h.right = m_rRectF3.right-hh/2;
						m_rRectF_h.bottom = m_rRectF3.bottom-hh/2;
						
						
						m_oPaint.setStrokeWidth(hh);
						m_oPaint.setStyle(Paint.Style.STROKE); 
						
						m_oPaint.setColor(warnPerColor1); // 仅填充单色 
//						canvas.drawOval(m_rRectF3, m_oPaint); 			
						float angle_x1 = 270*warnPer1;
						 canvas.drawArc(m_rRectF_h, //弧线所使用的矩形区域大小   
								 	135,  //开始角度   
								 	angle_x1, //扫过的角度   
						            false, //是否使用中心     
						            m_oPaint); 		
						 
						m_oPaint.setColor(warnPerColor2); // 仅填充单色 
//						canvas.drawOval(m_rRectF3, m_oPaint); 			
						float angle_x2 = 270*warnPer2;
						canvas.drawArc(m_rRectF_h, //弧线所使用的矩形区域大小   
										angle_x1+135,  //开始角度   
										angle_x2, //扫过的角度   
							            false, //是否使用中心     
							            m_oPaint); 
						m_oPaint.setColor(warnPerColor3); // 仅填充单色 
						float angle_x3 = 270-angle_x1-angle_x2;
//						canvas.drawOval(m_rRectF3, m_oPaint); 							
						canvas.drawArc(m_rRectF_h, //弧线所使用的矩形区域大小   
										135+angle_x1+angle_x2,  //开始角度   
										angle_x3, //扫过的角度   
							            false, //是否使用中心     
							            m_oPaint); 


					float x_origin = (m_rRectF3.left+m_rRectF3.right)/(float)2.0;
					float y_origin = (m_rRectF3.top+m_rRectF3.bottom)/(float)2.0;
					float x_p = m_rRectF3.right - x_origin;
					float y_p = m_rRectF3.bottom - y_origin;
					
				//画出外圆刻度  粗刻度 
					m_oPaint.setColor(m_cLineColor);		
					m_oPaint.setStrokeWidth(2);				
					canvas.save();
					canvas.translate(x_origin, y_origin);	
					canvas.rotate(45);
//					canvas.rotate(-270/scale);
					for(int i=0;i<=scale;i++){						 
						 
						 canvas.drawLine(0, y_p, 0, y_p-16, m_oPaint);	
						 canvas.rotate((float)270/(float)scale);
					}
					canvas.restore();
					//画出外圆刻度  细刻度
//					m_oPaint.setColor(Color.RED);			
					m_oPaint.setStrokeWidth(1);				
					canvas.save();
					canvas.translate(x_origin, y_origin);
					canvas.rotate(45);
					float count2 = scale*5;
					for(int i=0;i<=count2;i++){						 				 
						 canvas.drawLine(0, y_p, 0, y_p-10, m_oPaint);	
						 canvas.rotate((float)270/(float)count2);
					}
					canvas.restore();
						
				//再覆盖一遍外圆	
					m_oPaint.setColor(v_iBorderColor); // 仅填充单色	
					m_oPaint.setStrokeWidth(m_nBorderWidth);
					m_oPaint.setStyle(Paint.Style.STROKE);
					m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果   
					if(m_rRectF2.bottom-m_rRectF2.top!=m_rRectF2.right-m_rRectF2.left){
						m_rRectF2.left = m_rRectF2.right+m_rRectF2.bottom-m_rRectF2.top;
					}
			        canvas.drawOval(m_rRectF2, m_oPaint);  
			        
				//画出标签
					canvas.save();	
					canvas.translate(x_origin, y_origin);			
					m_oPaint.setColor(m_cLineColor);
					m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果
					m_oPaint.setTextSize(16);
					m_oPaint.setStyle(Paint.Style.FILL);
					Path path = new Path();
					path.addArc(new RectF(-x_p, -y_p, x_p, y_p), 0, 360);		
				
					canvas.rotate(135);
					canvas.rotate(-270/scale);
					for(int i=0;i<=scale;i++){						 
						canvas.rotate(270/scale); 
//						 if(i%2==0){ //2个刻度显示一个标签
							 float label_value = minValue+( (maxValue-minValue)/scale *(float)i);
							 DecimalFormat decimalFloat = new DecimalFormat("0"); //float小数点精度处理
							 String str = decimalFloat.format(label_value); 
							 
							canvas.drawTextOnPath(str, path, -5, 30, m_oPaint);
							
//						 }
						 
					}
					canvas.restore(); 
					
				//画出三角指针
					canvas.save();	
					canvas.translate(x_origin, y_origin);
					m_oPaint.setColor(0xE500263E);
					m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果			
					m_oPaint.setStyle(Paint.Style.FILL);
					canvas.rotate(45);
					canvas.rotate(angle);
					Path path2 = new Path();
					path2.moveTo(8, 0);
					path2.lineTo(0, y_p*(float)0.8);
					path2.lineTo(-8, 0);
					path2.lineTo(8, 0);
					canvas.drawPath(path2, m_oPaint);

					canvas.restore();
					
					//画出中心点	
					m_oPaint.setStyle(Paint.Style.FILL); 
					m_oPaint.setColor(0xFFFFFFFF); // 仅填充单色					  
					canvas.drawCircle(x_origin, y_origin, 12,  m_oPaint);
					m_oPaint.setColor(0xCEE5E2E2); // 仅填充单色					  
					canvas.drawCircle(x_origin, y_origin, 12,  m_oPaint);
					m_oPaint.setStrokeWidth(1);
					m_oPaint.setColor(0x66000000); // 仅填充单色	
					m_oPaint.setStyle(Paint.Style.STROKE); 
					canvas.drawCircle(x_origin, y_origin, 12,  m_oPaint);
					
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_DashBoard>>onLayout","into"); 		
		
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_DashBoard-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_DashBoard>>doLayout","into"); 	
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
		v_strContent = newValue;		
		data_value = Float.parseFloat(newValue);
		realTimeValue = null;
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
		     else if ("Radius".equals(strName))
		    	 v_iRadius = Integer.parseInt(strValue);
		     else if ("BorderColor".equals(strName)) 
		        	v_iBorderColor = Color.parseColor(strValue); 
		     else if ("BorderWidth".equals(strName)) 
		        	v_iBorderWidth = Integer.parseInt(strValue); 
		     else if ("FillColor".equals(strName)){
		    	 v_iStartFontColor = Color.parseColor(strValue); 
		    	 v_iFillColor = v_iStartFontColor;
		     }
		     else if ("LineColor".equals(strName)) {
		        	m_cLineColor = Color.parseColor(strValue);
		        }
		        else if ("WarmPer1".equals(strName)){
		           	warnPer1 = Float.parseFloat(strValue);
		        }
		        else if ("WarmPer2".equals(strName)){
		           	warnPer2 = Float.parseFloat(strValue);
		        }
		        else if ("WarmPerColor1".equals(strName)){
		        	warnPerColor1 = Color.parseColor(strValue);
		        }
		        else if ("WarmPerColor2".equals(strName)){
		        	warnPerColor2 = Color.parseColor(strValue);
		        }
		        else if ("WarmPerColor3".equals(strName)){
		        	warnPerColor3 = Color.parseColor(strValue);
		        }
		        else if ("MaxValue".equals(strName))
		        	maxValue = Integer.parseInt(strValue);
		        else if ("scale".equals(strName))
		        	scale = Integer.parseInt(strValue);
		        else if ("mode".equals(strName))
		        	if("".equals(strValue)){ 
		        		
		        	}else{
		        		mode = Integer.parseInt(strValue);
		        	}
		     else if ("ForeColor".equals(strName)) 
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
