package UIs;

import java.util.ArrayList;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//告警阈值 设置 控件
//made by fjw0312
public class Ks_AlarmSet extends ViewGroup implements VObject{

	public Ks_AlarmSet(Context context) {
		super(context);
		
		tigger = new Tigger();
		//实例化该控件的组合元素控件	
		//实例化该控件的组合元素控件	
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		button = new Button(context);
		button.setText("设置");
		button.setGravity(Gravity.CENTER); //目前存在字体位置偏上 debug
		button.setOnClickListener(l); 
		textview = new TextView(context);
		editview = new EditText(context);
		editview.setBackground(null); //使edittext的下划线消失   
		editview.setSingleLine();  //设置为单行输入格式
		editview.setGravity(Gravity.CENTER); //目前存在字体位置偏上 debug
//		editview.setCursorVisible(false);
//		editview.setOnClickListener(l);
		editview.setInputType(EditorInfo.TYPE_CLASS_PHONE); 
		imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	
		editview.setOnFocusChangeListener(new OnFocusChangeListener() {			
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						if(arg1){
							f_color = f_color2;
							is_ant = true;
							b_runThread = true;
							editview.setText("");
							doInvalidate();
							imm.showSoftInput(editview, InputMethodManager.SHOW_FORCED);
							editview.setFocusable(true);
							editview.setFocusableInTouchMode(true);

							myThread thread = new myThread();
							thread.start(); 
						
							Log.e("Ks_AlarmSet>>onFocusChange-oooo>>>","聚焦改变进入》》》");
						}else{
							f_color = f_color1;
							is_ant = false;
							b_runThread = false;
							
							doInvalidate();
							Log.e("Ks_AlarmSet>>onFocusChange-oooo>>>","聚焦改变退出！！！");
						}
					}
		});
		
		addView(editview);
		addView(button);
		addView(textview);
	}
	private Handler myHandler= new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1: 
				textview.setText(editview.getText().toString());
		//		Log.e("Ks_AlarmSet>>myHandler>>>editview.getText()",editview.getText().toString());
				break;
			default: break;
			}
		}
		
	};
	private class myThread extends Thread{	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try{
//					Log.e("Ks_AlarmSet>>myThread>>>","thread-run");
					Thread.sleep(400);
					myHandler.sendEmptyMessage(1);
					if(b_runThread == false) break;				
				}catch(Exception e){				
				}
			}
		}
	}
   private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if(arg0==editview){	
//						editview.setFocusableInTouchMode(true); //使能光标 会自动调起键盘
//						imm.showSoftInput(editview, InputMethodManager.SHOW_FORCED);//调起键盘
			}else if(arg0==button){					
				imm.hideSoftInputFromWindow(m_MainWindow.getWindowToken(), 0);				
				editview.clearFocus();								
				textview.setText(editview.getText().toString());
				//发送控制命令
				if(TriggerExpression != null){					
					if( (tTiggerConditions_ht != null) &&(tTiggerConditions_ht.size()!=0) ){
						try{
							tigger.startvalue = Float.parseFloat( editview.getText().toString() );
				//			tigger.eventseverity = Integer.parseInt(tTiggerConditions_ht.get(String.value(tigger.conditionid).EventSeverity));						
							tigger.mark = 1;
							AddThread thread = new AddThread();
							thread.start();
					//		NetDataModel.addTigger(tigger);
							Toast.makeText(m_MainWindow.getContext(), "告警阀值成功！", Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							Log.e("Ks_AlarmSet>>onClick>>","告警阀值 设置失败  异常抛出！");
						}
					}
				}
				
			}	
			doInvalidate();
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
		String v_strType = "AlarmSet";           //控件类型
		int v_iZIndex = 1;                    //控件图层
		String v_strExpression = "Binding{[Trigger[Equip:2-Temp:175-Event:1-Condition:1]]}";          //控件绑定表达式
		int v_iPosX = 100,v_iPosY = 100;       //控件坐标
		int v_iWidth = 50,v_iHeight = 50;       //控件大小
		int v_iBackgroundColor = 0x00000000;    //控件底板颜色
		float v_fAlpha = 1.0f;                 //控件相位
		float v_fRotateAngle = 0.0f;           //控件旋转角度
		float v_fFontSize = 12.0f;              //字体 大小
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
		
		float v_fButtonWidthPer = (float)0.4;
		
		boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
		Page m_MainWindow = null;         //主页面类
		//定义控件使用的元素
		EditText editview;
		Button button;
		TextView textview;
		//辅助变量
		InputMethodManager imm;  //定义键盘管理器变量
		int editWidth;  //edittext的宽度
		BindExpression TriggerbindExpression = null;  //控制绑定处理类
		int TriggerbindExpressionItem_num = 0;     //控制绑定子项 的个数      只处理单项绑定
		Expression TriggerExpression = null;     //控制表达式子项类
		Tigger tigger = null;
		xml_eventCfg tiggerCfg = null;
		HashMap<String, EventCondition> tTiggerConditions_ht = null;
		boolean getConditionFlag = true;

		int f_color1 = 0xDDDCDCDC;
		int f_color2 = 0xFFE1A222;
		int f_color = 0xDDDCDCDC;
		boolean is_ant = false;
		boolean b_runThread = false; //没办法刷新 键盘输入 只能采用线程不断刷显示了
		
		//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
		protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
		{		
			super.dispatchDraw(canvas);		
		//	canvas.drawColor(Color.WHITE);   //设置viewgroup的底板颜色 
//			Log.e("Ks_AlarmSet>>doLayout","into");
			float fontSize = v_iHeight*(float)0.4/MainActivity.densityPer;
			textview.setTextSize(fontSize);
			textview.setTextColor(Color.RED);
			textview.setGravity(Gravity.CENTER);			
			textview.setPadding(0, (int)fontSize/2, 0, 0);
			editview.setTextSize(fontSize);
			editview.setPadding(0, 0, 0, 0);
			
			button.setTextSize(fontSize);
			button.setPadding(0, (int)v_iHeight/2-(int)(fontSize*MainActivity.densityPer), 0, 0); //动态加载button 其大小太小会挤掉字体 故修改里面元素位置 
			
			Paint mPaint = new Paint();
			mPaint.setStrokeWidth(1);    //设置线条宽度
			mPaint.setStyle(Paint.Style.FILL); 
			mPaint.setColor(Color.WHITE);
			canvas.drawRect(6, 6, editWidth-6, v_iHeight-6, mPaint); 
			mPaint.setStrokeWidth(1);    //设置线条宽度
			mPaint.setStyle(Paint.Style.STROKE); 
			mPaint.setColor(f_color);
			mPaint.setAntiAlias(is_ant);

			canvas.drawRect(3, 3, editWidth-3, v_iHeight-3, mPaint);
			//shader 只能得到渐变色的效果		
//			Shader mShader = new LinearGradient(0, 0, 2, 4, 0xDD000000, 0x00000000, TileMode.CLAMP);
//			mPaint.setShader(mShader);
//			mPaint.setShadowLayer(5, 2, 2, Color.RED); //实际该阴影效果无效
			drawChild(canvas, button, getDrawingTime());
			drawChild(canvas, editview, getDrawingTime());
			drawChild(canvas, textview, getDrawingTime());
		}
		//重写onLayout() 绘制viewGroup中所有的view底板layout 
		protected void onLayout(boolean bool, int l, int t, int r, int b) {
//			Log.e("Ks_AlarmSet>>onLayout","into");	
			editWidth = (int)(v_iWidth*((float)1-v_fButtonWidthPer));
			editview.layout(0, 0, editWidth, v_iHeight);  //待测试该正规范		
			button.layout(editWidth, 0, v_iWidth, v_iHeight-2);  //待测试该正规范	
			textview.layout(0, 0, editWidth, v_iHeight);	
		}
		//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
		public boolean onTouchEvent(MotionEvent event){
			super.onTouchEvent(event);
//			Log.e("Ks_AlarmSet-onTouchEvent","into");		 
			//invalidate();   //通知当前view 重绘制自己
			return true;
		}
		//调用Layout() 自身控件底板Layout大小位置绘制函数     
		public void doLayout(boolean bool, int l, int t, int r, int b){
//			Log.e("Ks_AlarmSet>>doLayout","into");
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
		
			if(getConditionFlag == false) return false;
			try{
				tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);
				if("true".equals(tiggerCfg.Enable)){
					tigger.enabled = 1;
				}else{
					tigger.enabled = 0;
				}
				
				tigger.enabled = 1;
				tTiggerConditions_ht = tiggerCfg.EventConditionlst;

				getConditionFlag = false; //获取到 控制 配置  设置 获取到标志变量  使不在更新获取
				return true;
				
			}catch(Exception e){
			//	Log.i("Ks_AlarmSet>>updataValue>>","未获取到 SCmdCfg 异常抛出！");		
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
				 else if ("ButtonWidthRate".equals(strName)) 
				        	v_fButtonWidthPer = Float.parseFloat(strValue);
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
		@Override
		public boolean parseExpression(String str_bindExpression) {
			// TODO Auto-generated method stub
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

					//Log.i("Ks_AlarmSet>>parseExpression>>控件id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
					return true;
				}
				
			}
			return false;
		}
}
