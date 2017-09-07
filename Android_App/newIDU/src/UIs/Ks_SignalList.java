package UIs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import utils.BindExpression;
import utils.Expression;
import view.UtTable;
import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Signal;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

//import data.pool.DataPoolModel;
//import data.pool_model.Signal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

//自定义控件SignalList 设备的信号列表 使用new draw canvas方式
//注意，由于表格需要将数据保存到表适配里，测试了任意刷新周期40ms-5s 没发现异常
public class Ks_SignalList extends ViewGroup implements VObject{

	public Ks_SignalList(Context context) {
		super(context);
//		Log.e("Ks_SignalList->","into");  
		//实例化该控件的组合元素控件
		 lstTitles = new ArrayList<String>();         //表格标题成员字符数组 
		 lstContends = new ArrayList<List<String>>(); //表格字符串成员数组 
		 
		 //定义表格view
		 table = new UtTable(context);
		 table.m_bUseTitle = true;  //表头 是否显示
		 this.addView(table);
		 
		 //表格标题  //子元素添加到该容器上
		 lstTitles.add("信号名称");
		 lstTitles.add("信号数值");
		 lstTitles.add("信号含义");
		 lstTitles.add("信号单位");
//		 lstTitles.add("告警等级");
		 lstTitles.add("采集时间");
		 textView_titles = new TextView[lstTitles.size()];
//		 for(int i=0;i<lstTitles.size();i++){
//			 textView_titles[i] = new TextView(context);
//			 textView_titles[i].setText(lstTitles.get(i));
//			 textView_titles[i].setTextColor(Color.BLACK); //字体颜色与 内容字体一样v_iFontColor
//			 textView_titles[i].setGravity(Gravity.CENTER);
//			 this.addView(textView_titles[i]);
//		 }
		 
		//获取view数 加载完成时回调  目前无用！ 多次进入  无法判定  
			this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@Override
				public void onGlobalLayout() {
					// TODO Auto-generated method stub
			//		Log.w("Ks_SignalList->OnGlobalLayoutListener>>",v_strID+"     view结束！！！！");
				
				}
			});
		 
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "SignalList";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "";          //控件绑定表达式
	int v_iPosX = 100,v_iPosY = 100;       //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //字体的颜色
	String v_strContent = "设置内容";        //控件字符内容
	String v_strFontFamily = "微软雅黑";      //控件文字类型
	boolean v_bIsBold = false;               //控件线条是否加粗
	String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
	String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
	

	int v_iLineThickness =3; //线条大小
	int v_iLineColor = 0xFF000000;  //线条颜色
//	public int m_cOddRowBackground = 0xFF000000; // 奇数
//	public int m_cEvenRowBackground = 0xFF000000; // 偶数
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	
	//定义控件使用的元素
	 UtTable table;
	 TextView[] textView_titles;

	//辅助变量
	List<String> lstTitles = null;  //表格标题成员字符数组
	List<List<String>> lstContends = null; //表格字符串成员数组
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数      信号列表 只处理单项绑定
	Expression expression = null; //表达式子项类

	boolean flag = true;
	int times = 0;
	long nowTime = 10000; //10s
	long oldTime = 0;
		
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{	
		super.dispatchDraw(canvas);		
		canvas.drawColor(v_iBackgroundColor);   //设置viewgroup的底板颜色  
		
		drawChild(canvas, table, getDrawingTime());
		
//		for(int i=0;i<textView_titles.length;i++){
//			drawChild(canvas, textView_titles[i], getDrawingTime());
//			textView_titles[i].setTextColor(Color.BLACK);
//		}		
//		Log.e("Ks_SignalList->dispatchDraw","into"); 
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Label-onLayout","into"); 
		table.notifyTableLayoutChange(0, 0, v_iWidth, v_iHeight);
		
//		int title_Height=18;
//		 for(int i=0;i<textView_titles.length;i++){ 
//			 textView_titles[i].layout(i*v_iWidth/textView_titles.length, 0, 
//					      (i+1)*v_iWidth/textView_titles.length, v_iPosY+title_Height);
//		 }
		 
//		Log.e("Ks_SignalList->onLayout","into"); 
	}
	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){ //该表格控件会影响 滑屏 屏蔽了触摸响应
		super.onTouchEvent(event);
//		Log.e("Ks_SignalList-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_SignalList-doLayout","into");
		
		this.layout(v_iPosX, v_iPosY, v_iPosX+v_iWidth, v_iPosY+v_iHeight); //绘制该view底板layout
		Log.e("Ks_SignalList->doLayout","into"); 
	}
	
	//调用invalidate() 控件更新->onDraw()调用函数 
	public void doInvalidate(){

//			Log.e("Ks_SignalList-doInvalidate","table.update");
			table.update();  //更新表格内容  
//			this.invalidate();
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
		
		if(expression==null) return false;

		//获取 设备的信号列表
//		Hashtable<Integer, Signal> ht_Signal = DataPoolModel.getHtSignal(expression.equip_ExId);
		Hashtable<Integer, Signal> ht_Signal = NetDataModel.getHtSignal(expression.equip_ExId);
		if(ht_Signal==null) return false;
		
		//信号排序处理     目前的排序算法 不够理想 后期待优化
		lstContends.clear();
		List<Integer> id_lst_l = new ArrayList<Integer>();
		Iterator<Integer> id_lst_1 = ht_Signal.keySet().iterator();
		while(id_lst_1.hasNext()){
			int id = id_lst_1.next();
			id_lst_l.add(id);
		}
		for(int i=1; i<id_lst_l.size(); i++){
			List<String> lstRow = new ArrayList<String>();
			lstRow.clear();
			Signal signal = ht_Signal.get(i);
			if(signal==null) continue;
			lstRow.add(signal.name);  //信号名  
			lstRow.add(signal.value); //信号值    
			lstRow.add(signal.meaning);  //信号含义
			lstRow.add(signal.unit);     //信号单位
//		    lstRow.add(String.valueOf(signal.severity)); //告警等级
			lstRow.add(UtTable.getDate(signal.readtime * 1000, "yyyy.MM.dd HH:mm:ss"));  //采集时间 
			lstContends.add(lstRow);  //添加表格行 		
		}
		List<String> lstRow = new ArrayList<String>();
		lstRow.clear();
		Signal signal = ht_Signal.get(10001);
		lstRow.add(signal.name);  //信号名
		lstRow.add(signal.value); //信号值
		lstRow.add(signal.meaning);  //信号含义
		lstRow.add(signal.unit);     //信号单位
//		lstRow.add(String.valueOf(signal.severity)); //告警等级 
		lstRow.add(UtTable.getDate(signal.readtime * 1000, "yyyy.MM.dd HH:mm:ss"));  //采集时间
	//	Log.e("Ks_SignalList>>updataValue", String.valueOf(signal.readtime));
		lstContends.add(lstRow);  //添加表格行 	 
		table.updateContends(lstTitles,lstContends);  //将表格头 表格行内容 填入表格  此处有内存增大的现象
		
			
//		lstContends.clear();			
//		Iterator<Integer> iter_id_lst = ht_Signal.keySet().iterator();
//		while(iter_id_lst.hasNext()){
//			int id = iter_id_lst.next();
//			Signal signal = ht_Signal.get(id);
//			List<String> lstRow = new ArrayList<String>();
//			lstRow.clear();
//			if(signal==null) return false;
//			lstRow.add(signal.name);  //信号名
//			lstRow.add(signal.value); //信号值
//			lstRow.add(signal.meaning);  //信号含义
//			lstRow.add(signal.unit);     //信号单位
//			lstRow.add(String.valueOf(signal.severity)); //告警等级 
//			lstRow.add(UtTable.getDate(signal.readtime * 1000, "yyyy.MM.dd HH:mm:ss"));  //采集时间 
//	//		Log.i("Ks_SignalList>>updataValue>>信号id",String.valueOf(id)+"  "+signal.name+
//	//					"  "+signal.value+"  "+signal.meaning+"  "+String.valueOf(signal.readtime));
//			lstContends.add(lstRow);  //添加表格行 				
//		}	
//		table.updateContends(lstTitles,lstContends);  //将表格头 表格行内容 填入表格  此处有内存增大的现象
		
		nowTime = java.lang.System.currentTimeMillis(); 
		if(nowTime-oldTime > 3000){ //3s以上 ~~4s才刷新一次
			oldTime = nowTime;
			return true;
		}else{
			return false;
		}
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

			  else if ("OddRowBackground".equals(strName)) 
				 table.m_cOddRowBackground = Color.parseColor(strValue); 
			  else if ("EvenRowBackground".equals(strName)) 
				  table.m_cEvenRowBackground = Color.parseColor(strValue); 
			 
		     else if ("Alpha".equals(strName)) 
		       	 	v_fAlpha = Float.parseFloat(strValue);
		     else if ("RotateAngle".equals(strName)) 
		        	v_fRotateAngle = Float.parseFloat(strValue);
		     else if ("Content".equals(strName)) 
		        	v_strContent = strValue;
		     else if ("FontFamily".equals(strName))
		        	v_strFontFamily = strValue;
		     else if ("FontSize".equals(strName)) //字体大小	   
		        	v_fFontSize = Float.parseFloat(strValue);	    	
		     else if ("IsBold".equals(strName))
		       	 	v_bIsBold = Boolean.parseBoolean(strValue);
		     else if ("ForeColor".equals(strName)){  //前景色 即 字体颜色
		    	 	table.m_cFontColor = Color.parseColor(strValue); 
		    	 	v_iFontColor = Color.parseColor(strValue); 
		     }
		  //   else if ("FontColor".equals(strName)) 
		  //      	v_iFontColor = Color.parseColor(strValue); 
		     else if ("BackgroundColor".equals(strName)) //背景色
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
		
		//信号列表  只会单绑定  单项运算
		if(bindExpression==null) return false;
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
		expression = expression_lst.get(0);
		
		return true;
	}

}
