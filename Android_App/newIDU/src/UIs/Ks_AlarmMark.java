package UIs;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import utils.BindExpression;
import utils.Expression;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

//屏蔽告警 控件
//made in kstar   目前存在bug 当该条告警存在时 屏蔽  会使告警一直锁住存在 不管条件如何变化。
public class Ks_AlarmMark extends ViewGroup implements VObject{

	public Ks_AlarmMark(Context context) { 
		super(context);
		tigger = new Tigger();
		//实例化该控件的组合元素控件
		button = new Button(context);
		button.setOnClickListener(l); //为button设置监听器
		//将组合元素控件添加入该控件
		addView(button);
	}
	//按键监听器
	private OnClickListener l = new OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 == button){ //判断为该button的点击事件
				//判断点击事件响应的类型
				String text = "";
				if(("".equals(v_strMaskExpression)==false)&&(TriggerExpression != null)){

					   if(markFlag){      //第一次 点击屏蔽
							tigger.enabled = 0;
							String startvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).StartCompareValue;
							tigger.startvalue = Float.parseFloat(startvalue);
							//tigger.eventseverity = 0;
							markFlag = false;
							text = "告警屏蔽";
							litwith = 3;
							litColor = 0xB9DF0000;
							Log.e("button1-onClick",  text);
					   }else{            //第2次 点击告警
							tigger.enabled = 1;					
							String startvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).StartCompareValue;
							tigger.startvalue = Float.parseFloat(startvalue);							
							markFlag = true;
							text = "告警使能";
							litwith = 6;
							litColor = 0xB9009500;
							Log.e("button1-onClick",  text);
					   }
						AddThread thread = new AddThread();
						thread.start();
					 //  DataPoolModel.addTigger(tigger); 
					   Toast.makeText(m_Page.getContext(), "告警屏蔽设置！", Toast.LENGTH_SHORT).show();

					   v_strContent = text; 
				 
				}
				
				doInvalidate();
			}	
		}
	};
	private class AddThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			NetDataModel.addTigger(tigger);
		}
		
	}

	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "MarkAlarm";     //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "";          //控件绑定表达式
	int v_iPosX = 0,v_iPosY = 0;       //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //控件线条的颜色
	String v_strContent = "告警使能";        //控件字符内容
	String v_strFontFamily = "微软雅黑";      //控件文字类型
	boolean v_bIsBold = false;               //控件线条是否加粗
	String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
	String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strYTCmdExpression = "";          //控件遥调命令表达式 
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "show(首页)";           //控件点击事件跳转内容
	String v_strMaskExpression = "Binding{[Mask[Room:1-Equip:2-Event:1]]}"; //事件屏蔽表达式
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_Page = null;         //主页面类
	
	//定义控件使用的元素
	Button button;
	
	//辅助变量
	int litwith = 6;
	int litColor = 0xB9009500;
	int i = 0;
	BindExpression TriggerbindExpression = null;  //控制绑定处理类
	int TriggerbindExpressionItem_num = 0;     //控制绑定子项 的个数      只处理单项绑定
	Expression TriggerExpression = null;     //控制表达式子项类
	Tigger tigger = null;
	HashMap<String, EventCondition> tTiggerConditions_ht  = new HashMap<String, EventCondition>();
	boolean getConditionFlag = true;
	boolean markFlag = true;
			
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{
//		Log.e("Ks_AlarmMark>>dispatchDraw","into");
		super.dispatchDraw(canvas);				    
//		canvas.drawColor(Color.YELLOW);   //设置viewgroup的底板颜色 
		button.setTextSize(v_fFontSize/MainActivity.densityPer);
		button.setTextColor(v_iFontColor);
		button.setText(v_strContent);
		button.getPaint().setFakeBoldText(v_bIsBold);
		
		//绘制子view
		drawChild(canvas, button, getDrawingTime());
		Paint mPaint = new Paint();
		mPaint.setStrokeWidth(1);    //设置线条宽度
		mPaint.setStyle(Paint.Style.STROKE); 
		mPaint.setColor(litColor);
		canvas.drawRect(litwith, litwith, v_iWidth-litwith, v_iHeight-litwith, mPaint);
			
	}
	//重写onLayout() 绘制viewGroup中所有的子view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_AlarmMark>>onLayout","into");
		//绘制子view的layout
		button.layout(0, 0, v_iWidth, v_iHeight);  //待测试该正规范
						
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("form-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_AlarmMark>>doLayout","into");
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
	
		if(getConditionFlag == false) return false;
		try{
			xml_eventCfg tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);			
			if(tiggerCfg==null) return false;
			tTiggerConditions_ht = tiggerCfg.EventConditionlst;		
			if( (tTiggerConditions_ht != null) &&(tTiggerConditions_ht.size()!=0) ){ 
					tigger.conditionid = 1;
					String startvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).StartCompareValue;
					String stopvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).EndCompareValue;
					String eventseverity = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).EventSeverity;
					if("".equals(startvalue)==false){
						tigger.startvalue = Float.parseFloat(startvalue);
					}
					if("".equals(stopvalue)==false){
						tigger.stopvalue = Float.parseFloat(stopvalue);
					}
					if("".equals(eventseverity)==false){
						tigger.eventseverity = Integer.parseInt(eventseverity);
					}
					tigger.mark = 3;
					getConditionFlag = false; //获取到 控制 配置  设置 获取到标志变量  使不在更新获取
					return true;
			}
		}catch(Exception e){
				Log.e("Ks_YKParameter>>updataValue>>","未获取到 tiggerCfg 异常抛出！");
		}	
		return false;
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
		     else if ("MaskExpression".equals(strName))   
		       	 	v_strMaskExpression = strValue;          //屏蔽表达式
		     else if ("CmdExpression".equals(strName)) 
		        	v_strCmdExpression = strValue;      //控制命令表达式
		     else if ("YTCmdExpression".equals(strName)) 
		        	v_strYTCmdExpression = strValue;      //遥调命令表达式
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
		if("".equals(v_strMaskExpression)==false){
			TriggerbindExpression = new BindExpression(); 
			TriggerbindExpressionItem_num = TriggerbindExpression.getBindExpression_ItemLst(v_strMaskExpression);			
			if( (TriggerbindExpression != null)&&(TriggerbindExpression.itemBindExpression_lst != null) ){
				String str_bindItem = TriggerbindExpression.itemBindExpression_lst.get(0); //单绑定
				List<Expression> expression_lst = TriggerbindExpression.itemExpression_ht.get(str_bindItem);
				TriggerExpression = expression_lst.get(0); //单运算
				//获取 报警配置 内容成员item							
				tigger.equipId = TriggerExpression.equip_ExId;
				tigger.tiggerId = TriggerExpression.event_ExId;

				Log.e("Ks_AlarmMark>>updataValue>>控件id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
			}
			
		}	
		return false;
	}
}
