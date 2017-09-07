package UIs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import utils.BindExpression;
import utils.Calculator;
import utils.Expression;
import utils.RealTimeValue;
import view.Axis;
import view.UtTable;
import SAM.extraHisModel.HisDataDAO;
import SAM.extraHisModel.HisFormula;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

//自定义控件  用电量分布柱状图
public class Ks_zHisBar_aNH extends ViewGroup implements VObject{

	public Ks_zHisBar_aNH(Context context) {
		super(context); 
		point_lst = new ArrayList<String>();
			
		m_oPaint = new Paint();   //赋予画笔类空间
		m_oPaint.setTextSize(v_fFontSize); //设置画笔线条大小
		m_oPaint.setColor(v_iFontColor);   //设置画笔颜色
		m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果
		m_oPaint.setStyle(Paint.Style.STROKE); //设置画笔风格

		//定义 一个坐标轴
		myAxis = new Axis(context);
		myAxis.enable_y_label = false;  //不显示 坐标 自己的y轴刻度
		myAxis.y_density = 5;  //设置y轴刻度密度				

		//子元素添加到该容器上
		addView(myAxis);
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Label";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "";//控件绑定表达式     控件无需绑定
	int v_iPosX = 100,v_iPosY = 100;       //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //控件线条的颜色
	int  v_iStartFontColor = 0xFF008000;    //控件线条的颜色
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
	Page m_Page = null;         //主页面类
	//定义控件使用的元素
	Axis myAxis;
	//辅助变量
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	int times = 0;
	
	Paint m_oPaint = null; //新建画笔 类变量

	long x_MaxTimeValue = 0;
	long x_MinTimeValue = 0;
	float y_MaxValue = 0;
	
	String str_prevData = "";
	String str_nowData = "";
	String str_nextData = "";
	
	int  x_MaxDay = 0; //最大采集时间 天
	int  x_MinDay = 32; //最小采集时间 天
	float x_MaxDayValue = 0; //最大采集时间  时间值
	float x_MinDayValue = 32; //最小采集时间  时间值
	List<String> point_lst = null;  //存放一个月 点字符数据

	float prevDay_value = 0;  //记录前一天数据
	long nowTime = 0;  //当前时间
	long prev_num_Mon = 0; //num 月前时刻 时间
	int numDay = 3; //获取 num前月 用电量
	
	boolean canVISIBLE = false;  //判断控件是否可见  标志变量
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件    
	{		
		super.dispatchDraw(canvas);	
		m_oPaint.setStyle(Style.FILL); 	//设置画笔为实心
		
		//遍历 信息点
		if((point_lst==null)||point_lst.size()==0) return;
		try{
			for(int i=0; i<numDay; i++){				
				//提取 采集时间 和 数值			
				int ii = i;
				if(point_lst.size() >= numDay){  //数据点大于numDay 时 取最后numDay个点
					ii = point_lst.size() - numDay + i;
				}else{                         //数据点小于i  时 超过size()个数退出
					if(ii > point_lst.size()-1)   return;
				}
				
	//			Log.e("Ks_HisBarChart_Mon_NH>>onDraw>>str", point_lst.get(ii));
				String str[] = point_lst.get(ii).split("-");
				if(str.length < 2) continue;		
				//float f_time = Float.parseFloat(str[1]);
				m_oPaint.setTextSize(18);
				
					float f_value_1 = Float.parseFloat(str[0]);
	//				Log.e("Ks_HisBarChart_Mon_NH>>onDraw>>f_value_1", str[0]);
					float node_x1 = myAxis.x_start+myAxis.x_unit*(i+1);
					float node_y1 = myAxis.y_start-myAxis.y_per_unit*f_value_1;
					RectF rectf1 = new RectF();
					rectf1.left =  node_x1 - myAxis.x_unit/(float)3;
					rectf1.top =  node_y1;
					rectf1.right =  rectf1.left + myAxis.x_unit*4/(float)17;
					rectf1.bottom =  myAxis.y_start;
					m_oPaint.setColor(0xFF004191);   //设置画笔颜色				
					canvas.drawRect(rectf1, m_oPaint);										
							
					
					float f_value_2 = Float.parseFloat(str[1]);
	//				Log.e("Ks_HisBarChart_Mon_NH>>onDraw>>f_value_2", str[1]);
					float node_x2 = myAxis.x_start+myAxis.x_unit*(i+1);
					float node_y2 = myAxis.y_start-myAxis.y_per_unit*f_value_2;
					RectF rectf2 = new RectF();
					rectf2.left =  rectf1.right;
					rectf2.top =  node_y2;
					rectf2.right =  rectf2.left + myAxis.x_unit*4/(float)17;
					rectf2.bottom =  myAxis.y_start;
					m_oPaint.setColor(0xFF9D0012);   //设置画笔颜色				
					canvas.drawRect(rectf2, m_oPaint);										
						
					
					float f_value_3 = Float.parseFloat(str[2]);
	//				Log.e("Ks_HisBarChart_Mon_NH>>onDraw>>f_value_3", str[2]);
					float node_x3 = myAxis.x_start+myAxis.x_unit*(i+1);
					float node_y3 = myAxis.y_start-myAxis.y_per_unit*f_value_3;
					RectF rectf3 = new RectF();
					rectf3.left =  rectf2.right;
					rectf3.top =  node_y3;
					rectf3.right =  rectf3.left + myAxis.x_unit*4/(float)17;
					rectf3.bottom =  myAxis.y_start;
					m_oPaint.setColor(0xFF41910E);   //设置画笔颜色				
					canvas.drawRect(rectf3, m_oPaint);			
					
					m_oPaint.setColor(Color.BLUE);   //设置画笔颜色
					canvas.drawText(str[0], rectf1.right-40, node_y1-2, m_oPaint);  // 画出数值
					m_oPaint.setColor(Color.RED);   //设置画笔颜色		
					canvas.drawText(str[1], rectf2.right-40, node_y2-2, m_oPaint);  // 画出数值
					m_oPaint.setColor(Color.GREEN);   //设置画笔颜色		
					canvas.drawText(str[2], rectf3.right-40, node_y3-2, m_oPaint);  // 画出数值	


				m_oPaint.setTextSize(15);
				m_oPaint.setColor(Color.BLACK);   //设置画笔颜色
				canvas.drawText(str[3], node_x1-40, myAxis.y_start+20, m_oPaint); // 画出  y轴 便签 日期
			    }
			}catch(Exception e){
				Log.e("Ks_HisBarChart_Mon_HN>>onDraw>>","绘制曲线 异常抛出！");
			}	
	//	drawChild(canvas, myAxis, getDrawingTime());
	//	Log.e("Ks_HisBarChart_Mon_HN>>onDraw>>","end"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) { 
//		Log.e("Label-onLayout","into"); 
		myAxis.layout(0, 0, v_iWidth, v_iHeight);  //待测试该正规范	
		myAxis.upDataValue(v_iWidth,v_iHeight,numDay,25, numDay,y_MaxValue*(float)1.2); //坐标轴时间周期2天
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
			myAxis.invalidate();
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
		if("".equals(HisDataDAO.hisFormulaId_lst.get(0))) return false;	
				
		point_lst.clear();

		//获取信号 当月 历史式子数据
		nowTime = java.lang.System.currentTimeMillis();
		prev_num_Mon = nowTime - (long)(numDay+1)*31*24*3600*1000; //获取numDay周天前的 时间
		String strnowTime = UtTable.getDate(nowTime, "yyyy.MM.dd HH:mm:ss");			
		String nowfileName = HisDataDAO.hisFormulaId_lst.get(0)+"#"+strnowTime.substring(0,4);
	//	str_nowData = strnowTime.substring(0,7);		
		if(HisDataDAO.getPueLine_HisFormulaList(nowfileName,12, 1)){
			 List<HisFormula> hisFormula_lst = HisDataDAO.mon_RCBar_hisFormula_lst;			
			if(hisFormula_lst==null) return false;
				//遍历 历史式子 
			for(int i=0; i<hisFormula_lst.size();i++){
					HisFormula hisFormula = hisFormula_lst.get(i);
					if(hisFormula == null ) continue;
					long  value_l_time = Long.parseLong(hisFormula.getTime) *1000;
				//	if(str_nowData.equals(value_strYearMon)){
				//	Log.e("Ks_HisBarChart_Mon>>updateValue>>strPoint：",String.valueOf(value_l_time)+"---"+String.valueOf(prev_num_Mon));
					if(value_l_time > prev_num_Mon){	//最近num个月
						String start_value = hisFormula.strContent;					
						String start_str[] = start_value.split("&");
						String end_value = hisFormula.strEndContent;						
						if("".equals(end_value)|| end_value==null)  continue;  //未有结束数据！
					
						//获取 式子的 各个电量
						float  f_start_value_T = Float.parseFloat(start_str[0]);
						float  f_start_value_H = Float.parseFloat(start_str[1]);
						float  f_end_value_T = f_start_value_T;
						float  f_end_value_H = f_start_value_H;
						if("".equals(end_value)==false){
							String end_str[] = end_value.split("&");
							f_end_value_T = Float.parseFloat(end_str[0]);
							f_end_value_H = Float.parseFloat(end_str[1]);
						}
					
						//判断 提取 哪个用电量
						float f_value_T = 0,f_value_H = 0,f_value_I = 0;
						//总用电量
						f_value_T = f_end_value_T - f_start_value_T;
						//空调/其他 用电量
						f_value_H = f_end_value_H - f_start_value_H;
						//IT 用电量
						f_value_I = (f_end_value_T - f_end_value_H) - (f_start_value_T - f_start_value_H);
						
			    		DecimalFormat decimalFloat = new DecimalFormat("0.00"); //float小数点精度处理
			    		String v_I = decimalFloat.format(f_value_I) + "-";						
						String v_H = decimalFloat.format(f_value_H) + "-";	
						String v_T = decimalFloat.format(f_value_T) + "-";	
						String v = v_I + v_H + v_T;
						String readTime_day = hisFormula.strTime.substring(0,7); //获取 年月 						
						String strPoint = v + readTime_day; //将 数值与采集时间合并为一个采集点
						point_lst.add(strPoint);
						
						if(y_MaxValue < f_value_T) y_MaxValue = f_value_T;
			//			Log.e("Ks_HisBarChart_Mon>>updateValue>>strPoint：", hisFormula_lst.get(i).strContent+"---"+hisFormula_lst.get(i).strEndContent+">>>"+strPoint);
					}
			}//end for
		}//end if
	//	Log.e("Ks_HisBarChart_Mon>>updateValue>>最大值：", String.valueOf(y_MaxValue));

		myAxis.upDataValue(v_iWidth,v_iHeight,numDay,25, numDay,y_MaxValue*(float)1.2); //坐标轴时间周期2天
        return true;  //有控件参数变化并要变化界面view，不管是text函数图形 都要返回true;
	}

	//处理绑定表达式
	public boolean parseExpression(String str_bindExpression){
		if("".equals(v_strExpression)) return false;
		
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
