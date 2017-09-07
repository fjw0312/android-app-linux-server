package UIs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import utils.BindExpression;
import utils.Expression;
import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.SCmd;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//加减 数据 按钮    //支持绑定告警阀值 和  控制命令
public class Ks_ButtonUpDown extends ViewGroup implements VObject{

	public Ks_ButtonUpDown(Context context) {
		super(context);
		
		tigger = new Tigger();
		//实例化该控件的组合元素控件
		//实例化该控件的组合元素控件	
		imageButton1 = new ImageButton(context);
		imageButton2 = new ImageButton(context);
		// load image
				try {
					AssetManager assetManager = this.getContext().getResources().getAssets();
					InputStream is = null;
					InputStream is2 = null;
					
					if (null == s_bitUpSet1Image)
					{
						is = assetManager.open("ui/Button2-UP.png");
						s_bitUpSet1Image = BitmapFactory.decodeStream(is);
						imageButton1.setImageBitmap(s_bitUpSet1Image);
						is.close();
					}
					if (null == s_bitDownSet2Image)
					{
						is2 = assetManager.open("ui/Button2-DOWN.png");
						s_bitDownSet2Image = BitmapFactory.decodeStream(is2);
						imageButton2.setImageBitmap(s_bitDownSet2Image);
						is2.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("Ks_ButtonUpDown>>>super","异常抛出！");
				}
//				imageButton1.setImageBitmap(s_bitUpSet1Image);
//				imageButton2.setImageBitmap(s_bitDownSet2Image);
				
				imageButton1.setOnClickListener(l);
				imageButton2.setOnClickListener(l);
		        
		        m_oPaint = new Paint();
		addView(imageButton1);
		addView(imageButton2);
	}
	//图片 点击事件 实例化
		private OnClickListener l = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(arg0 == imageButton1)   addd = v_iStepValueFirst;
				if(arg0 == imageButton2)   addd = 0 - v_iStepValueFirst;
				//Log.e("Ks_ButtonUpDown>>onClick",String.valueOf(addd));
				MyThread thread = new MyThread(); //启动修改 号码 文件线程
				thread.start();	
			}
		};
		//创建 线程
		private class MyThread extends Thread{
			public void run() {
				try{
					if(addd > 0){
						myhandler.sendEmptyMessage(2);
					}else{
						myhandler.sendEmptyMessage(3);
					}
					//发送控制命令
					if(expression != null){	
						if(tiggerOrCmd_mode == 1){   //设置  告警值  
							if( (tTiggerConditions_ht != null) &&(tTiggerConditions_ht.size()!=0) ){
								try{
									String str_conditionid = String.valueOf(tigger.conditionid);
									String startvalue = tTiggerConditions_ht.get(str_conditionid).StartCompareValue;
									tigger.startvalue = Float.parseFloat(startvalue) + (float)addd;
									Log.e("Ks_ButtonUpDown>>MyThread",String.valueOf(tigger.startvalue));
									//tigger.stopvalue = tTiggerConditions_ht.get(str_conditionid).endcompare;
									//tigger.eventseverity = tTiggerConditions_ht.get(str_conditionid).severity;
									tigger.mark = 1;
									NetDataModel.addTigger(tigger);	

								}catch(Exception e){
									Log.e("Ks_ButtonUpDown>>MyThread>>","告警阀值 设置失败  异常抛出！");
								}
							}
						}else if(tiggerOrCmd_mode == 2){  //设置控制命令
							SCmd scmd = new SCmd();
							scmd.equipId = expression.equip_ExId;
							scmd.cmdId = expression.command_ExId;
							int  val = cmdStartValue + addd;
							if(val > 80000) val = 10;  //做值得封顶处理
							cmdStartValue = val;							
							scmd.value = String.valueOf(val);
							scmd.valueType = 1;
							NetDataModel.addSCmd(scmd);
						}
						
					}
					Thread.sleep(400);//800ms
					myhandler.sendEmptyMessage(1);
				}catch(Exception e){ 
					Log.e("Ks_ButtonUpDown>>MyThread"," 异常抛出！");
			    }
			}
		};
		private Handler myhandler = new Handler(){
			public void handleMessage(Message msg){
	    		switch (msg.what){
	    		case 1:   //按钮正常
	    			imageButton1.setScaleX((float)1.0);
	    			imageButton1.setScaleY((float)1.0);
	    			imageButton2.setScaleX((float)1.0);
	    			imageButton2.setScaleY((float)1.0);
					Toast.makeText(m_MainWindow.getContext(), "设置成功", Toast.LENGTH_SHORT).show();
	    			break;
	    		case 2:   //加按钮  动画
	    			imageButton1.setScaleX((float)1.3);
	    			imageButton1.setScaleY((float)1.3);
	    			break;
	    		case 3:  //减按钮  动画
	    			imageButton2.setScaleX((float)1.3);
	    			imageButton2.setScaleY((float)1.3);
	    			break;
	    		default:
	    			break;
			    }
			}
		};
	
	//Fields
		String v_strID = "";                 //控件id
		String v_strType = "ButtonUpDown";           //控件类型
		int v_iZIndex = 1;                    //控件图层
		String v_strExpression = "Binding{[Trigger[Equip:2-Temp:175-Event:1-Condition:1]]}";          //控件绑定表达式
								//Binding{[Cmd[Equip:2-Temp:175-Command:1-Parameter:1]]}   & 绑Content
		int v_iPosX = 100,v_iPosY = 100;       //控件坐标
		int v_iWidth = 50,v_iHeight = 50;       //控件大小
		int v_iBackgroundColor = 0x00000000;    //控件底板颜色
		float v_fAlpha = 1.0f;                 //控件相位 
		float v_fRotateAngle = 0.0f;           //控件旋转角度
		float v_fFontSize = 12.0f;              //字体 大小
		int  v_iFontColor = 0xFF008000;         //控件线条的颜色
		String v_strContent = "10";        //控件字符内容
		String v_strFontFamily = "微软雅黑";      //控件文字类型
		boolean v_bIsBold = false;               //控件线条是否加粗
		String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
		String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
		String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
		String v_strCmdExpression = "";             //控件控制命令表达式
		String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
		String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
		int v_iStepValueFirst = 1;     //步进值
		
		float v_fButtonWidthPer = (float)0.4;
		
		boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
		Page m_MainWindow = null;         //主页面类
		
		//定义控件使用的元素
		//辅助变量
		private Bitmap s_bitUpSet1Image = null; 
		private Bitmap s_bitDownSet2Image = null; 
		private ImageButton imageButton1;
		private ImageButton imageButton2;
		Paint m_oPaint = null;
		int addd = 1; //每次点击按钮的步进值
		int cmdStartValue = 10; //控制设置初始值 默认10
		int tiggerOrCmd_mode = 0;  //告警设置  还是控制设置  模式     1:tigger   2:cmd

		BindExpression bindExpression = null;  //控制绑定处理类
		int bindExpressionItem_num = 0;     //控制绑定子项 的个数      只处理单项绑定
		Expression expression = null;     //控制表达式子项类
		
		
		Tigger tigger = null;
		xml_eventCfg tiggerCfg = null;
		HashMap<String, EventCondition> tTiggerConditions_ht  = null;
		boolean getCfgFlag = true;

		
		//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
		protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
		{		
			super.dispatchDraw(canvas);		
		//	canvas.drawColor(Color.WHITE);   //设置viewgroup的底板颜色 
			drawChild(canvas, imageButton1, getDrawingTime());
			drawChild(canvas, imageButton2, getDrawingTime());

		}
		//重写onLayout() 绘制viewGroup中所有的view底板layout 
		protected void onLayout(boolean bool, int l, int t, int r, int b) {
//			Log.e("Ks_ButtonUpDown>>onLayout","into");	
			imageButton1.layout(5, 0, v_iWidth/2-5, v_iHeight);
			imageButton2.layout(v_iWidth/2+5, 0, v_iWidth-5, v_iHeight);

		}
		//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
		public boolean onTouchEvent(MotionEvent event){
			super.onTouchEvent(event);
//			Log.e("Ks_ButtonUpDown-onTouchEvent","into");		 
			//invalidate();   //通知当前view 重绘制自己
			return true;
		}
		//调用Layout() 自身控件底板Layout大小位置绘制函数     
		public void doLayout(boolean bool, int l, int t, int r, int b){
//			Log.e("Ks_ButtonUpDown>>doLayout","into");
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
		
			if(tiggerOrCmd_mode == 2) return false;  //控制命令时不用 外部刷新
			if(getCfgFlag == false) return false;
			try{
				tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);			
				if(tiggerCfg==null) return false;
				if("true".equals(tiggerCfg.Enable)){
					tigger.enabled = 1;
				}else{
					tigger.enabled = 0;
				}
				tTiggerConditions_ht = tiggerCfg.EventConditionlst;
				//TiggerConditionCfg tiggerConditionCfg = tTiggerConditions_ht.get(tigger.conditionid);
				getCfgFlag = false; //获取到 控制 配置  设置 获取到标志变量  使不在更新获取
				return true;
				
			}catch(Exception e){
			//	Log.i("Ks_ButtonUpDown>>updataValue>>","未获取到 SCmdCfg 异常抛出！");		
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
			     else if ("Content".equals(strName)){ 
			        	v_strContent = strValue;
			        	if("".equals(v_strContent)==false){
			        		try{
			        		cmdStartValue = Integer.parseInt(v_strContent);
			        		}catch(Exception e){
			        			
			        		}
			        	}
			     }
			     else if ("StepValueFirst".equals(strName)){
			    	 if("".equals(strValue)==false)
			        	 v_iStepValueFirst = Integer.parseInt(strValue);
			     }
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
				bindExpression = new BindExpression();
				bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);	
				if(bindExpression.itemBindExpression_lst != null){
					String str_bindItem = bindExpression.itemBindExpression_lst.get(0); //单绑定
					List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
					expression = expression_lst.get(0); //单运算
					//判断 时哪个类型的绑定
					if("Trigger".equals(expression.type)){
						//获取 报警配置 内容成员item		
						tiggerOrCmd_mode = 1;
						tigger.equipId = expression.equip_ExId;
						tigger.tiggerId = expression.event_ExId;
						tigger.conditionid = expression.condition_ExId;
					}else if("Cmd".equals(expression.type)){
						tiggerOrCmd_mode = 2;
					}
					
				//	Log.i("Ks_ButtonUpDown>>updataValue>>控件id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
					return true;
				}
				
			}
			return false;
		}
}
