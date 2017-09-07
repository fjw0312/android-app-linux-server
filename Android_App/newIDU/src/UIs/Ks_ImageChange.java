package UIs;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import utils.BindExpression;
import utils.Expression;
import utils.RealTimeValue;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//自定义控件ImageChange  使用new TextView方式   实现绑定数据 动态变化图片
public class Ks_ImageChange extends ViewGroup implements VObject{

	public Ks_ImageChange(Context context) {
		super(context);		
		//实例化该控件的组合元素控件
		//子元素添加到该容器上

	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "ImageChange";           //控件类型
	int v_iZIndex = 1;                    //控件图层
	String v_strExpression = "";          //控件绑定表达式
	int v_iPosX = 100,v_iPosY = 100;       //控件坐标
	int v_iWidth = 50,v_iHeight = 50;       //控件大小
	int v_iBackgroundColor = 0x00000000;    //控件底板颜色
	float v_fAlpha = 1.0f;                 //控件相位
	float v_fRotateAngle = 0.0f;           //控件旋转角度
	float v_fFontSize = 12.0f;              //控件线条大小
	int  v_iFontColor = 0xFF008000;         //控件线条的颜色
	String v_strContent = "0";              //控件字符内容
	String v_strFontFamily = "微软雅黑";      //控件文字类型
	boolean v_bIsBold = false;               //控件线条是否加粗
	String v_strHorizontalContentAlignment = "Center"; //控件内容的横向底板对其方式
	String v_strVerticalContentAlignment = "Center";  //控件内容的纵向底板对其方式
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //字体颜色变化表达式
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
	String v_strImageExpression = "&gt;1[2.jpg]&gt;2[3.jpg]&gt;3[4.jpg]"; //图片变化表达式
		                         //Binding{>1[2.jpg]>2[3.jpg]>3[4.jpg]}
	String v_strImgPath = "";
	String v_strImage = "fjw_logo.jpg";
	String v_strStartImage = "fjw_logo.jpg";
	String passWork = "pass";
	String usr = "fang";
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识 
	Page m_MainWindow = null;         //主页面类
	//定义控件使用的元素

	//辅助变量
	Bitmap bitmap;
	Hashtable<Float, String> htValue_ImageName = null; //<大于等于的数值， 图片名称>链表
	List<Float> htValue = null;  //<比较值>
	
	//辅助变量 
	BindExpression bindExpression = null;  //绑定处理类
	int bindExpressionItem_num = 0;     //绑定子项 的个数
	Expression expression = null; //表达式子项类
	int times = 0;

	
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //设置viewgroup的底板颜色 
		
		String path[] = v_strImgPath.split(".xml");	
		String imgPath = path[0]+".files/"+v_strImage;
//		Log.e("Ks_ImageChange->dispatchDraw>>v_strImage=", imgPath); 
		bitmap = BitmapFactory.decodeFile(imgPath);  
		if(bitmap==null) return; 
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果     
		
		Rect rect = new Rect();
		Rect rect2 = new Rect();
		rect.left = 0;
		rect.top = 0;
		rect.right = bitmap.getWidth();
		rect.bottom = bitmap.getHeight();
		
		rect2.left = 0;
		rect2.top = 0;
		rect2.right = v_iWidth;
		rect2.bottom = v_iHeight;
		
		canvas.rotate(v_fRotateAngle, v_iWidth/2, v_iHeight/2);
		canvas.drawBitmap(bitmap, rect, rect2, mPaint);

		
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_ImageChange-onLayout","into"); 		
		
	}

	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_ImageChange-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		return false;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_ImageChange-doLayout","into");
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
		// 可后期 在此处 判断绑定的类型  是绑定信号value 还是告警等级EventSeverity
		RealTimeValue realTimeValue = new RealTimeValue();	
		String newValue = 	realTimeValue.getRealTimeValue(expression_lst);			
		if(v_strContent.equals(newValue) ) return false; //数值未改变 不更新 
		v_strContent = newValue;
		realTimeValue = null;
		Log.e("Ks_ImageChange>>updataValue>>","获取数据："+v_strContent);
		
		try{
		//遍历比较数值 大小 
		   if(htValue_ImageName != null){
			    boolean flag = false;
				float f_value = Float.parseFloat(v_strContent.trim());
				Log.e("Ks_ImageChange>>updataValue>>aaaa",String.valueOf(f_value));
				float f_state = f_value;
				for(int i=0; i<htValue.size();i++){		
					if(f_value >= htValue.get(i)){
						f_state = htValue.get(i);
						flag = true;
					}
				}
				
				if(flag){
					v_strImage = htValue_ImageName.get(f_state);
				}else{
					v_strImage = v_strStartImage;
				}
				
				Log.e("Ks_ImageChange>>updataValue>>rrrr",f_state+"---"+v_strImage);
				return true;    
			}
		}catch(Exception e){
			
			Log.e("Ks_ImageChange>>updataValue>>","遍历比较 数值大小 异常抛出！");
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
		     else if ("ImgSrc".equals(strName)){
		    	 v_strImgPath = path;
		    	 v_strStartImage = strValue;
		    	 v_strImage = strValue;
		     }
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
		        else if ("ImageExpression".equals(strName)) 
		    		v_strImageExpression = strValue;    //图片变化表达式
			 return true;
		}

	@Override
	public boolean parseExpression(String str_bindExpression) {
		// TODO Auto-generated method stub
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		//解析 图片绑定 表达式 >1[2.jpg]>2[3.jpg]>3[4.jpg] 
		try{
			if( (v_strImageExpression == null)||("".equals(v_strImageExpression)) ) return false;
			htValue_ImageName  = new Hashtable<Float, String>();
			htValue = new ArrayList<Float>();
	        String[] strItems = v_strImageExpression.split("\\]"); //>1[2.jpg
			for(int i=0; i<strItems.length; i++){
				String[] items = strItems[i].split("\\[|>");
				htValue_ImageName.put(Float.parseFloat(items[1]), items[2]);
				htValue.add(Float.parseFloat(items[1]));
				Log.e("Ks_ImageChange>>updataValue>>","数值："+items[1]+" 图片："+items[2]);
			}
			
			//排序
			for(int i=0;i<htValue.size();i++){
				for(int j=i+1;j<htValue.size();j++){
					float tmp1 = htValue.get(i);
					float tmp2 = htValue.get(j);
					if(tmp1>tmp2){
						htValue.set(i, tmp2);
						htValue.set(j, tmp1);
					}
				}
			}
		//	for(int i=0;i<htValue.size();i++){
		//		Log.e("Ks_ImageChange>>parseExpression",">>>>>"+String.valueOf(htValue.get(i)));
		//	}
		}catch(Exception e){
			Log.e("Ks_ImageChange>>parseExpression","解析图片表达式异常抛出！");
		}
		
		return true; 
	}	

}
