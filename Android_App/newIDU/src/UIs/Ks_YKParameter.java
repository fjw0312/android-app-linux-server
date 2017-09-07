package UIs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import utils.BindExpression;
import utils.Expression;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.SCmd;
import SAM.XmlCfg.xml_cmdCfg;
import SAM.XmlCfg.xml_cmdCfg.CommandParameter;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

//import data.pool.DataPoolModel;
//import data.pool_model.SCmd;
//import data.pool_model.equipment_cell.SCmdCfg;
//import data.pool_model.equipment_cell.SCmdCfg.CmdParameaningCfg;
import android.R.anim;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
//button.postInvalidate();  在非ui线程刷新view
//button.invalidate();       必须在ui线程刷新view
//在非ui线程刷新ui一般有2种方式    postInvalidate   Handler
//遥控 控制 控件  
//made in kstar
//自定义控件YkParameter  使用SPinner Button   //目前存在bug adapter会多加载成员显示  采用的一个线程存在
public class Ks_YKParameter extends ViewGroup implements VObject{

	public Ks_YKParameter(Context context) {
		super(context);
		
		scmd = new SCmd();
		//实例化该控件的组合元素控件		
		button = new Button(context);
		button.setText("设置");
		button.setGravity(Gravity.CENTER); //目前存在字体位置偏上 debug
		button.setOnClickListener(l);
		spinner = new Spinner(context);
//		spinner.setBackgroundColor(Color.GRAY);
		adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		adapter.add("请选择↓");
		textview = new TextView(context);
		textview.setText("请选择↓");
		textview.setGravity(Gravity.CENTER);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			 public void onItemSelected(			
	                    AdapterView<?> parent, View view, int position, long id) {	
	            //	Log.e("YKParameter->onItemSelected","into onItemSelected");
	            }

	            public void onNothingSelected(AdapterView<?> parent) {
	            //	Log.e("YKParameter->onItemSelected","into onNothingSelected");
	            }
		});
		//监听spinner点击子item  无效
		spinner.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			
			@Override
			public void onChildViewRemoved(View arg0, View arg1) {
				// TODO Auto-generated method stub
			//	Log.e("YKParameter->onChildViewRemoved>>","into onChildViewRemoved！");
			}
			
			@Override
			public void onChildViewAdded(View arg0, View arg1) {
				// TODO Auto-generated method stub
			//	Log.e("YKParameter->onChildViewAdded>>","into onChildViewAdded！");
			}
		});
		//无法监听到spinner item的触摸
		spinner.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
//				Log.e("YKParameter->spinner.onTouch>>","into onTouch！");
				return false;
			}
		});
		//子元素添加到该容器上
		addView(spinner);
		addView(button); 
		addView(textview); 
		
//		mythread.start();
	}
	//实例化button的点击事件监听
	private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Log.e("YKParameter->onClick>>","into！"); 
			if(arg0==button){										
				String cloose = (String) spinner.getSelectedItem();
				//比较选择的meaning  获取 对应的value
				Log.e("YKParameter->onClick>>cloose", cloose); 
				
				Iterator<String> lst = CommandMeaninglst.keySet().iterator();
				while(lst.hasNext()){
					String value = lst.next();
					if( cloose.equals(CommandMeaninglst.get(value)) ){
						scmd.value = value;
						NetDataModel.addSCmd(scmd);
						Toast.makeText(m_MainWindow.getContext(), "发送成功！", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				Log.e("YKParameter->onClick>>","未获取到 对应数据 发送！"); 
			}		
		}
	};

	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 10:
				doInvalidate();
				break;
			default:
				break;
			}			
		}
	};
	private Thread mythread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub 
	//		while(true){ 
				try{
					Thread.sleep(500);
				}catch(Exception e){ 
					
				}
				String cloose = (String) spinner.getSelectedItem();
				if(cloose.equals(OldText)==false){
					OldText = cloose;
					
					myHandler.sendEmptyMessage(10);				
				}
	//		}
		}
	});
	
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "YkParameter";           //控件类型
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
	String v_strCmdExpression = "Binding{[Cmd[Equip:1-Temp:173-Command:1-Parameter:1-Value:1]]}";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
	
	float v_fButtonWidthPer = (float)0.4;
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	//定义控件使用的元素
	Spinner spinner;
	Button button;
	TextView textview;
	//辅助变量
	int spinnerWidth;  //edittext的宽度
	private ArrayAdapter<String> adapter;
	private ArrayList<String> strlst;
	String OldText="请选择↓";
	BindExpression cmdbindExpression = null;  //控制绑定处理类
	int cmdbindExpressionItem_num = 0;     //控制绑定子项 的个数      只处理单项绑定
	Expression cmdExpression = null;     //控制表达式子项类
	SCmd scmd = null; 
	int parameterId = 0;
//	List<CmdParameaningCfg> CmdParameaningCfg_lst = new ArrayList<CmdParameaningCfg>();
	HashMap<String, String> CommandMeaninglst = new HashMap<String, String>();
	boolean getParameanFlag = true;

		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		 
//		canvas.drawColor(Color.LTGRAY);   //设置viewgroup的底板颜色 
		float fontSize = v_iHeight*(float)0.35/MainActivity.densityPer;
		button.setTextSize(fontSize);
		button.setPadding(0, (int)v_iHeight/2-(int)(fontSize*MainActivity.densityPer), 0, 0); //动态加载button 其大小太小会挤掉字体 故修改里面元素位置 

		textview.setTextSize(v_iHeight*(float)0.25);
		textview.setText(OldText);

		drawChild(canvas, spinner, getDrawingTime());
		drawChild(canvas, button, getDrawingTime());
		drawChild(canvas, textview, getDrawingTime());
//		Log.e("YKParameter->dispatchDraw>>","into！"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout 
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Label-onLayout","into"); 	
		spinnerWidth = (int)(v_iWidth*((float)1-v_fButtonWidthPer));
		spinner.layout(5, 5, spinnerWidth-5, v_iHeight-5);  //待测试该正规范		
		textview.layout(5, 5, spinnerWidth-5, v_iHeight-5);  //待测试该正规范	
		button.layout(spinnerWidth, 0, v_iWidth, v_iHeight);  //待测试该正规范	
//		Log.e("YKParameter->onLayout>>","into！"); 
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){  
		super.onTouchEvent(event);
		Log.e("Ks_YKParameter->onTouchEvent","into");		 
		//invalidate();   //通知当前view 重绘制自己
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(mythread.isAlive()==false){
				mythread.start();
			}
		}
		
		return true;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数      
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("YKParameter->doLayout>>","into！"); 
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
	//更新控件数值函数     传入字符串  返回是否数值写入成功    只获取一次SCmdcfg 数据
	public boolean  updataValue(String strValue) {
		if(getParameanFlag == false) return false;
		try{
//			SCmdCfg scmdCfg = DataPoolModel.getSCmdCfg(scmd.equipId, scmd.cmdId);
			xml_cmdCfg scmdCfg = NetDataModel.getSCmdCfg(scmd.equipId, scmd.cmdId);
			CommandMeaninglst = scmdCfg.CommandParameterlst.get(String.valueOf(parameterId)).CommandMeaninglst;
			if( (CommandMeaninglst != null) &&(CommandMeaninglst.size()!=0) ){	
				Iterator<String> vla_lst = CommandMeaninglst.keySet().iterator();
				while(vla_lst.hasNext()){
					String valueId = vla_lst.next();
					adapter.add(CommandMeaninglst.get(valueId));
					Log.i("Ks_YKParameter>>updataValue>>adapter.add", CommandMeaninglst.get(valueId));
				}
				getParameanFlag = false; //获取到 控制 配置  设置 获取到标志变量  使不在更新获取
				return true;
			}
		}catch(Exception e){
		//	Log.i("Ks_YKParameter>>updataValue>>","未获取到 SCmdCfg 异常抛出！");		
		}			
		
		return false;  //不 需 实时 刷新数据
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
	@Override  //该线程为 ui 子线程 不该获取 数据线程池 数据
	public boolean parseExpression(String str_bindExpression) {
		// TODO Auto-generated method stub
		if("".equals(v_strCmdExpression)==false){
			cmdbindExpression = new BindExpression();
			cmdbindExpressionItem_num = cmdbindExpression.getBindExpression_ItemLst(v_strCmdExpression);	
			if(cmdbindExpression != null){
				String str_bindItem = cmdbindExpression.itemBindExpression_lst.get(0); //单绑定
				List<Expression> expression_lst = cmdbindExpression.itemExpression_ht.get(str_bindItem);
				cmdExpression = expression_lst.get(0); //单运算
				//获取 控制 内容成员item		
				scmd.equipId = cmdExpression.equip_ExId;
				scmd.cmdId = cmdExpression.command_ExId;
				scmd.valueType = 1;
				parameterId = cmdExpression.parameter_ExId;	
				Log.i("Ks_YKParameter>>updataValue>>控件id"+v_strID,scmd.equipId+"  "+scmd.cmdId);
			}	   
				
		}
		return false;
	}
}
