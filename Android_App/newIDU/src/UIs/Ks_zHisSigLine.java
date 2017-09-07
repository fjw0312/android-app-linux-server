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
import SAM.extraHisModel.HisSignal;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

//自定义控件历史信号曲线
public class Ks_zHisSigLine extends ViewGroup implements VObject{

	public Ks_zHisSigLine(Context context) {
		super(context); 
		
		point_lst_24h = new ArrayList<String>();
		
			
		m_oPaint = new Paint();   //赋予画笔类空间
		m_oPaint.setTextSize(v_fFontSize); //设置画笔线条大小
		m_oPaint.setColor(v_iFontColor);   //设置画笔颜色
		m_oPaint.setAntiAlias(true); // 设置画笔的锯齿效果
		m_oPaint.setStyle(Paint.Style.STROKE); //设置画笔风格
		//定义选择按纽组 按钮数3个
				ridobuttons = new RadioButton[3];
				ridobuttons[0] = new RadioButton(context);
				ridobuttons[0].setText("24小时");
				ridobuttons[0].setChecked(true);
				ridobuttons[1] = new RadioButton(context);
				ridobuttons[1].setText("24 天");
				ridobuttons[2] = new RadioButton(context);
				ridobuttons[2].setText("30 天");
				for(int i=0;i<3;i++){
					ridobuttons[i].setTextColor(Color.BLACK);
			//		ridobuttons[i].setOnClickListener(l);	
				}
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
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_Page = null;         //主页面类
	//定义控件使用的元素
	RadioButton[] ridobuttons;
	Axis myAxis;
	//辅助变量
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	int times = 0;
	
	Paint m_oPaint = null; //新建画笔 类变量
	String equipSignalId = "";
	long x_MaxTimeValue = 0;
	long x_MinTimeValue = 0;
	float y_MaxValue = 0;
	List<String> point_lst_24h = null;  //存放24小时的 点字符数据
	int a_Width = 150;    //控件大小 长和宽 w h 
	int a_Height = 137;
	String str_prevData = "";
	String str_nowData = "";
	String str_nextData = "";
	
	boolean canVISIBLE = false;  //判断控件是否可见  标志变量
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件    
	{		
		super.dispatchDraw(canvas);	
		m_oPaint.setColor(v_iFontColor);   //设置画笔颜色
		m_oPaint.setStyle(Style.STROKE); 	//设置画笔为实心
		float pre_x = 0;
		float pre_y = 0;
		//遍历 信息点
		
		if((point_lst_24h==null)||(point_lst_24h.size()==0)) return;
		try{
			for(int i=0; i<point_lst_24h.size(); i++){
				//提取 采集时间 和 数值
				String str[] = point_lst_24h.get(i).split("-");
				if(str.length != 2) continue;
				float f_value = Float.parseFloat(str[0]);
				long l_time = Long.parseLong(str[1]);
				
				//绘制圆点
				float node_x = myAxis.x_start+myAxis.x_per_unit*(l_time-x_MinTimeValue);
				float node_y = myAxis.y_start-myAxis.y_per_unit*f_value;
	//			Log.e("Ks_SigLine>>onDraw>>值：", String.valueOf(f_value));
   //     		Log.e("Ks_SigLine>>onDraw>>时间差：", String.valueOf(l_time-x_MinTimeValue));
	//			canvas.drawCircle(node_x, node_y,6, m_oPaint); // 画出数值点

				//链接 连线
				if(i!=0){
					canvas.drawLine(pre_x,pre_y,node_x,node_y,m_oPaint);
				}
				pre_x = node_x;
				pre_y = node_y;
			}
			m_oPaint.setTextSize(14); //设置画笔线条大小
			m_oPaint.setColor(Color.BLUE);   //设置画笔颜色
			int x_pad = 35, y_pad = 20;  //位置 缩进
			
			canvas.drawText(str_prevData, myAxis.x_start-x_pad, myAxis.y_start+y_pad, m_oPaint);
			canvas.drawText(str_nowData, myAxis.x_lenth/2, myAxis.y_start+y_pad, m_oPaint);
			canvas.drawText(str_nextData, myAxis.x_lenth-x_pad-10, myAxis.y_start+y_pad, m_oPaint);
		}catch(Exception e){
			Log.e("Ks_SigLine>>onDraw>>","绘制曲线 异常抛出！");
		}
	
		//绘制子view
	//	drawChild(canvas, myAxis, getDrawingTime());
//		Log.e("Label-dispatchDraw","into"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) { 
//		Log.e("Label-onLayout","into"); 
	//	if(bool)
		myAxis.layout(0, 0, v_iWidth, v_iHeight);  //待测试该正规范	
		myAxis.upDataValue(v_iWidth,v_iHeight,9,10, 3600*48, 100);//坐标轴时间周期2天
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
		
		//2次1周期的数据刷新
		times++;
		if(times>1) times=0;
		
	    x_MinTimeValue = 2000000000;
		
		point_lst_24h.clear();
		//获取信号前一天历史数据
		long preDayTime = java.lang.System.currentTimeMillis() - 3600*24*1000;
		String strpreDayTime = UtTable.getDate(preDayTime, "yyyy.MM.dd HH:mm:ss");		
		String preDayfileName = equipSignalId+"#"+strpreDayTime.substring(0,10);
		str_prevData = strpreDayTime.substring(0,10);
		//Log.e("Ks_SigLine>>updateValue>>获取前一天文件", preDayfileName);
		List<HisSignal> hisSig_lst = new ArrayList<HisSignal>();
		synchronized (HisDataDAO.oneDay_hisSignal_lst) {
			if(HisDataDAO.getHisSignalList(preDayfileName) == false) ;
			else{
				hisSig_lst = HisDataDAO.oneDay_hisSignal_lst;
				if(hisSig_lst==null) return false;	

				//遍历 历史信号
				for(int i=0; i<hisSig_lst.size();i++){
					HisSignal hisSig = hisSig_lst.get(i);
					if(hisSig==null) continue;
					String value = hisSig.value;
					String readTime = hisSig.freshtime;
					long l_rTime = Long.parseLong(readTime);
					float f_value = Float.parseFloat(value);

					String strPoint = value+"-"+readTime; //将 数值与采集时间合并为一个采集点
					point_lst_24h.add(strPoint);
					
					if(y_MaxValue < f_value) y_MaxValue = f_value;
//					if(x_MaxTimeValue < l_rTime) x_MaxTimeValue = l_rTime;
//					if(x_MinTimeValue > l_rTime) x_MinTimeValue = l_rTime;
				}
			}
		}

		//获取信号当天历史数据
		long nowTime = java.lang.System.currentTimeMillis(); 
		String strnowTime = UtTable.getDate(nowTime, "yyyy.MM.dd HH:mm:ss");
		String nowfileName = equipSignalId+"#"+strnowTime.substring(0,10);
		str_nowData = strnowTime.substring(0,10);
		//Log.e("Ks_SigLine>>updateValue>>获取当天文件", nowfileName);
		synchronized (HisDataDAO.oneDay_hisSignal_lst) {
			if(HisDataDAO.getHisSignalList(nowfileName) == false) ;
			else{
				hisSig_lst = HisDataDAO.oneDay_hisSignal_lst;
				if(hisSig_lst==null) return false;
				//遍历 历史信号
				for(int i=0; i<hisSig_lst.size();i++){
					HisSignal hisSig = hisSig_lst.get(i);
					if(hisSig==null) continue;
					String value = hisSig.value;
					String readTime = hisSig.freshtime;
					long l_rTime = Long.parseLong(readTime);
					Float f_value = Float.parseFloat(value);
					
					String strPoint = value+"-"+readTime; //将 数值与采集时间合并为一个采集点
					point_lst_24h.add(strPoint);
					
					if(y_MaxValue < f_value) y_MaxValue = f_value;
//					if(x_MaxTimeValue < l_rTime) x_MaxTimeValue = l_rTime;
//					if(x_MinTimeValue > l_rTime) x_MinTimeValue = l_rTime;
				}
			}

	
		}
		//获取 昨天00:00:00的时间秒数	  将昨天凌晨 作为初始时刻时间。		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");//时间格式转换
			String yy = str_prevData + " 00:00:00";
			long l = formatter.parse(yy).getTime();
			x_MinTimeValue = l/1000;  //化成单位 s

			Log.i("Ks_SigLine>>updateValue>>> 初始时刻时间：", yy+"   "+String.valueOf(x_MinTimeValue));
		} catch (Exception e) {
			// TODO Auto-generated catch block 
			Log.e("Ks_SigLine>>updateValue>>> 初始时刻时间：", str_prevData+"异常抛出！");
			e.printStackTrace();
		}
		
		long nextTime = java.lang.System.currentTimeMillis() + 3600*24*1000;
		String strnextTime = UtTable.getDate(nextTime, "yyyy.MM.dd HH:mm:ss");
		str_nextData = strnextTime.substring(0,10);
		myAxis.upDataValue(v_iWidth,v_iHeight,9,10, 3600*48,100); //坐标轴时间周期2天
			
        return true;  //有控件参数变化并要变化界面view，不管是text函数图形 都要返回true;
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
		
		//信号列表  只会单绑定  单项运算
		if(bindExpression==null) return false;
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
		expression = expression_lst.get(0);
		
		int equipt_id = expression.equip_ExId;
		int signal_id = expression.signal_ExId;
		equipSignalId = String.valueOf(equipt_id)+"-"+String.valueOf(signal_id);
		//注册 历史信号id
		if(HisDataDAO.hisSignalId_lst.contains( equipSignalId ) ==false){
			HisDataDAO.hisSignalId_lst.add(equipSignalId);
//			Log.e("Ks_SigLine>parse_expression>equipSignalId:", equipSignalId);
		}
		
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
