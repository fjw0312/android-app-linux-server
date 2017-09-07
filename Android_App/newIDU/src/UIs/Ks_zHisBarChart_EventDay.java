package UIs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import utils.BindExpression;
import utils.Calculator;
import utils.Expression;
import utils.RealTimeValue;
import view.Axis;
import view.UtTable;
import SAM.DataPool.NetDataModel;
import SAM.extraHisModel.HisDataDAO;
import SAM.extraHisModel.HisEvent;
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

//自定义控件  日告警分布     柱状图
public class Ks_zHisBarChart_EventDay extends ViewGroup implements VObject{

	public Ks_zHisBarChart_EventDay(Context context) {
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
	String str_prevData = "";
	String str_nowData = "";
	String str_nextData = "";
	
	boolean userExpressionFlag = false;  //使用绑定指定的设备历史告警  
	
	List<String> point_lst = null;  //存放点字符数据
	
	long x_MaxTimeValue = 0;
	long x_MinTimeValue = 0;
	float y_MaxValue = 0;
	
	float prevDay_value = 0;  //记录前一天数据
	long nowTime = 0;         //当前时间
	long prev_num_Day = 0;    //num 天前时刻 时间
	int  numDay = 4;          //获取 多少月 用电量
	
	int equipNum = 0; //设备组个数  （柱状图条数）
	String[] str_equipIds = new String[20];
	int[] i_colors = {0xFF004191,0xFF9D0012,0xFF41910E,0xDF52166A,0xEC794450,
			          0xE9314450,0xE9AD2850,0xE9B1743C,0xE937573C,0xE9C9C23C};
	
	boolean canVISIBLE = false;  //判断控件是否可见  标志变量
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件    
	{		
		super.dispatchDraw(canvas);	
		m_oPaint.setStyle(Style.FILL); 	//设置画笔为实心
		
		//遍历 信息点
		if((point_lst==null)||point_lst.size()==0) return;
		try{
				for(int i=0; i<point_lst.size(); i++){
				//		Log.e("Ks_HisBarChart_HisEvent_Day>>onDraw>>str", point_lst.get(i));
						//提取 采集时间 和 数值
						String str[] = point_lst.get(point_lst.size()-1-i).split("-");
						if(str.length < 2) continue;
						
						
						float node_x_f = myAxis.x_start+myAxis.x_unit*(i+1);
						for(int j=0;j<equipNum;j++){
							int i_value = Integer.parseInt(str[j]);
				//			Log.e("Ks_HisBarChart_HisEvent_Day>>onDraw>>i_value", str[j]);
							//绘制圆点
							float node_x = myAxis.x_start+myAxis.x_unit*(i+(float)0.6);
							float node_y = myAxis.y_start-myAxis.y_per_unit*i_value;
							RectF rectf = new RectF();
							rectf.left =  node_x + myAxis.x_unit/(equipNum+1)*j;
							rectf.top =  node_y;
							rectf.right =  node_x + myAxis.x_unit/(equipNum+1)*(j+1);
							rectf.bottom =  myAxis.y_start;
						
							m_oPaint.setColor(i_colors[j]);   //设置画笔颜色
							canvas.drawRect(rectf, m_oPaint);
							
							m_oPaint.setTextSize(18);
				    		DecimalFormat decimalFloat = new DecimalFormat("0"); //float小数点精度处理
				    		String strValue= decimalFloat.format(i_value);
							canvas.drawText(strValue, rectf.left, node_y-2, m_oPaint);  // 画出数值
						}
					
						//float f_time = Float.parseFloat(str[1]);
			
						//画x轴标签
						String strX[] = point_lst.get(point_lst.size()-1-i).split("&");
						String timeText = strX[1];
						m_oPaint.setTextSize(15);
						m_oPaint.setColor(Color.BLACK);   //设置画笔颜色
						canvas.drawText(timeText, node_x_f-40, myAxis.y_start+20, m_oPaint); // 画出  y轴 便签 设备id
				}

		}catch(Exception e){
					Log.e("Ks_HisBarChart_HisEvent_Day>>onDraw>>","绘制曲线 异常抛出！");
		}
//		Log.e("Label-dispatchDraw","into"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) { 
//		Log.e("Ks_zHisBarChart_RcYear-onLayout","into"); 
		myAxis.layout(0, 0, v_iWidth, v_iHeight);  //待测试该正规范	
		myAxis.upDataValue(v_iWidth,v_iHeight,numDay,25, numDay,y_MaxValue*(float)1.2); //坐标轴时间周期2天
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
	//	Log.e("Ks_zHisBarChart_RcYear->onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;  //返回false  让page 能捕获到onTouch();
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_zHisBarChart_RcYear-doLayout","into");
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
				
		point_lst.clear();
		if(userExpressionFlag == false){	//没有绑定时  获取数据模型  设备链表个数
			equipNum = NetDataModel.lst_poolEquipmentId.size();
		}


		//获取信号 当月 历史式子数据
		nowTime = java.lang.System.currentTimeMillis();
	
		//获取 每天的告警
		for(int k=0;k<numDay;k++){
					prev_num_Day = nowTime - 24*3600*1000 *k;
					String time_str = UtTable.getDate(prev_num_Day, "yyyy-MM-dd HH:mm:ss");
					str_prevData = time_str.substring(0, 10);
			//		Log.e("Ks_HisBarChart_HisEvent_Day>>updateValue>>str_prevNumData=", str_prevNumData);
					String strss = "";
					//获取 该天 的各个设备 告警数量 
					for(int j=0;j<equipNum;j++){
						String strid = "";
						if(userExpressionFlag){
							strid = str_equipIds[j];
						}else{
							strid = String.valueOf(NetDataModel.lst_poolEquipmentId.get(j));
						}
						
						String fileName = "hisevent-"+strid;
						//内存 保护
						List<HisEvent> hisEvent_list = new ArrayList<HisEvent>();
						synchronized (HisDataDAO.hisEvent_lst) {
							if(HisDataDAO.getHisEquipEventList(fileName) == false) continue;
							hisEvent_list = HisDataDAO.hisEvent_lst;
						}
						//遍历做容错处理  去除重复采集的告警	
						Hashtable<String,HisEvent> hast_his = new Hashtable<String,HisEvent>();
						for(int i=0; i<hisEvent_list.size(); i++){
								HisEvent his_event = hisEvent_list.get(i); 
								if(his_event==null) return false;	
								boolean flag = true;
								//判断是否有该告警开始时间的key值与信号 是否已经添加
								if(hast_his.containsKey(his_event.start_time+"#"+his_event.event_id)){
									flag = false;
									//再判断是否为同一条信号
									if("1970-01-01".equals(his_event.finish_time.substring(0,10)) )
										continue;
								}
								 
								//获取对应天的  告警类
								String strTime = his_event.start_time.substring(0,10);
								if(strTime.equals(str_prevData)){
									hast_his.put(his_event.start_time+"#"+his_event.event_id, his_event);	
								}

						}//for(i)  end 
						if(hast_his == null) return false;

						strss = strss + String.valueOf(hast_his.size())+"-";
						if(y_MaxValue < hast_his.size())  y_MaxValue = hast_his.size();
							
						hisEvent_list = null;
						hast_his = null;
					}

			       point_lst.add(strss+"&"+str_prevData);
			       Log.e("Ks_HisBarChart_EventDay>>updataValue",strss+"&"+str_prevData);
		}

		myAxis.upDataValue(v_iWidth,v_iHeight,numDay,25, numDay,y_MaxValue*(float)1.2); //坐标轴时间周期2天
        return true;  //有控件参数变化并要变化界面view，不管是text函数图形 都要返回true;
	}

	//处理绑定表达式
	public boolean parseExpression(String str_bindExpression){
		if("".equals(v_strExpression)) return false;
		
		return true;
	}
	//解析出控件表达式，返回控件表达式类
	public boolean parse_expression(){
			if("".equals(v_strExpression)) return false;
			String str[] = v_strExpression.split("-");
			equipNum = str.length;
			for(int i=0; i<equipNum; i++){
				str_equipIds[i] = str[i];
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
		     else if ("Expression".equals(strName)){
		    	v_strExpression = strValue;          //请求数据表达式 
		    	if("".equals(v_strExpression)==false){
			    	 parse_expression();
			    	 userExpressionFlag = true;
		    	}
		     }	       	 
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
