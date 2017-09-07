package UIs;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import utils.BindExpression;
import utils.Calculator;
import utils.Expression;
import utils.RealTimeValue;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
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

//告警阈值回显  控件
//made by fang
public class Ks_AlarmEdgeLabel extends ViewGroup implements VObject{

	public Ks_AlarmEdgeLabel(Context context) {
		super(context);
		tigger = new Tigger();
		//实例化该控件的组合元素控件
		textview = new TextView(context); 
		//子元素添加到该容器上
		addView(textview);
	}
	//Fields
	String v_strID = "";                    //控件id
	String v_strType = "AlarmEdgeLabel";      //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "Binding{[Trigger[Equip:2-Temp:175-Event:1-Condition:1]]}";//告警阀值表达式
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
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_Page = null;         //主页面类
	//定义控件使用的元素
	TextView textview;
	//辅助变量
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	int times = 0;
	BindExpression TriggerbindExpression = null;  //控制绑定处理类
	int TriggerbindExpressionItem_num = 0;     //控制绑定子项 的个数      只处理单项绑定
	Expression TriggerExpression = null;     //控制表达式子项类
	Tigger tigger = null;
	HashMap<String, EventCondition> tTiggerConditions_ht = null;
	boolean getConditionFlag = true;
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);	
//		Log.e("Ks_AlarmEdgeLabel>>dispatchDraw","into");  
//		canvas.drawColor(Color.LTGRAY);   //设置viewgroup的底板颜色 
		//设置子控件元素参数
//		textview.getPaint().setAntiAlias(true);
		textview.setTextSize(v_fFontSize/MainActivity.densityPer);
		textview.setTextColor(v_iFontColor);
		textview.setText(v_strContent);
		textview.getPaint().setStrokeWidth(1);
		textview.getPaint().setFakeBoldText(v_bIsBold);	
//		textview.getPaint().setAntiAlias(true);
		//绘制子view
		drawChild(canvas, textview, getDrawingTime());
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_AlarmEdgeLabel>>onLayout","into"); 		
		textview.layout(0, 0, v_iWidth, v_iHeight);  //待测试该正规范 	
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_AlarmEdgeLabel>>onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_AlarmEdgeLabel>>doLayout","into");
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
		if("".equals(v_strExpression)) return false;
		if(tigger == null) return false;
		
		try{
			xml_eventCfg tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);	
			//TiggerCfg tiggerCfg = DataPoolModel.getTiggerCfg(tigger.equipId, tigger.tiggerId);
			//Log.e("Ks_AlarmEdgeLabel>>updataValue>>--f1",tiggerCfg.EventName);	
			if(tiggerCfg == null)  return false;
			int enabled = 1;
			if("true".equals(tiggerCfg.Enable)){
				tigger.enabled = 1;
			}else{
				tigger.enabled = 0;
			}
			tigger.enabled = enabled;
			tTiggerConditions_ht = tiggerCfg.EventConditionlst;
			String str_conditionid = String.valueOf(tigger.conditionid);
			if(tTiggerConditions_ht == null || tTiggerConditions_ht.get(str_conditionid)==null)  return false;
			String startvalue = tTiggerConditions_ht.get(str_conditionid).StartCompareValue;
			//String stopvalue = tTiggerConditions_ht.get(str_conditionid).EndCompareValue;
			//String eventseverity = tTiggerConditions_ht.get(str_conditionid).EventSeverity;
			
			tigger.startvalue = Float.parseFloat(startvalue);
			//tigger.stopvalue = Float.parseFloat(stopvalue);
			//tigger.eventseverity = Integer.parseInt(eventseverity); 
			
			
			//获取 告警报警阀 值  
			String value = String.valueOf(tigger.startvalue);
			//Log.e("Ks_AlarmEdgeLabel>>updataValue>>--f4",startvalue);	
			if(v_strContent.equals(value)) return false;
			v_strContent = value;
			return true;
			
		}catch(Exception e){
			Log.e("Ks_AlarmEdgeLabel>>updataValue>>","未获取到 告警阀值 异常抛出！");		
		}	
		return false;
	}
	//处理绑定表达式
	public boolean parseExpression(String str_bindExpression){
		if("".equals(v_strExpression)==false){
			TriggerbindExpression = new BindExpression();
			TriggerbindExpressionItem_num = TriggerbindExpression.getBindExpression_ItemLst(v_strExpression);	
			if(TriggerbindExpression.itemBindExpression_lst != null){
				String str_bindItem = TriggerbindExpression.itemBindExpression_lst.get(0); //单绑定
				List<Expression> expression_lst = TriggerbindExpression.itemExpression_ht.get(str_bindItem);
				TriggerExpression = expression_lst.get(0); //单运算
				//获取 报警配置 内容成员item							
				tigger.equipId = TriggerExpression.equip_ExId;
				tigger.tiggerId = TriggerExpression.event_ExId;
				tigger.conditionid = TriggerExpression.condition_ExId;

				Log.i("Ks_YKParameter>>updataValue>>控件id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
				return true;
			}

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

}
