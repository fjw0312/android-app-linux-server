package UIs;

import mail.EmailSetDialog;
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
import app.main.idu.R;

//自定义控件Image  使用new TextView方式    //当绑定的是信号量时 绑定表达式应在后面加+0
public class Ks_Image extends ViewGroup implements VObject{

	public Ks_Image(Context context) {
		super(context);
		//设置该控件点击事件的监
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub 
//				Log.e("f_Image->onClick>>into", "进入点击事件！"); 
				//判断点击事件响应的类型
				if("".equals(v_strClickEvent)==false){
					if("显示桌面".equals(v_strClickEvent)){
						showWaiterAuthorizationDialog();	
					}else if("邮箱设置".equals(v_strClickEvent)){
						EmailSetDialog dialog = new EmailSetDialog(m_MainWindow.getContext());
						dialog.showPassDialog();
					}else{  //点击跳转页面
						Log.e("TAG_Image","into onClick");
						if("".equals(usr)){
							String[] arrStr = v_strClickEvent.split("\\(");
							if (m_MainWindow != null && "Show".equals(arrStr[0])) {
								String[] arrSplit = arrStr[1].split("\\)");
								m_MainWindow.changePage(arrSplit[0]+".xml"); 
							}
						}else{
							showPassDialog();  //显示权限对话框
						}
					}
				}else if("".equals(v_strUrl)==false){  
					
				}else if("".equals(v_strCmdExpression)==false){
					
				}
			}
		});
		//实例化该控件的组合元素控件
		//子元素添加到该容器上
	}
	//Fields
	String v_strID = "";                 //控件id
	String v_strType = "Image";           //控件类型
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
	String v_strCmdExpression = "";             //控件控制命令表达式
	String v_strUrl = "www.hao123.com";          //控件网页网址请求表达式
	String v_strClickEvent = "首页.xml";           //控件点击事件跳转内容
		
	String v_strImgPath = "";
	String v_strImage = "fjw_logo.jpg";
	String passWork = "pass";
	String usr = "fang";
	
	boolean v_bNeedUpdateFlag = false;            //控件类的数值更新标识
	Page m_MainWindow = null;         //主页面类
	//定义控件使用的元素

	//辅助变量
	Bitmap bitmap;
	boolean touchFlag = false;
	
	//点击 页面跳转 方法
	private void onTouch_click(){
		Log.e("f_Image->onClick>>into", "进入点击事件！"); 
		//判断点击事件响应的类型
		if("".equals(v_strClickEvent)==false){
			if("显示桌面".equals(v_strClickEvent)){
				showWaiterAuthorizationDialog();	
			}else if("邮箱设置".equals(v_strClickEvent)){
				EmailSetDialog dialog = new EmailSetDialog(m_MainWindow.getContext());
				dialog.showPassDialog();
			}else{  //点击跳转页面
				Log.e("TAG_Image","into onClick");
				if("".equals(usr)){
					String[] arrStr = v_strClickEvent.split("\\(");
					if (m_MainWindow != null && "Show".equals(arrStr[0])) {
						String[] arrSplit = arrStr[1].split("\\)");
						m_MainWindow.changePage(arrSplit[0]+".xml"); 
					}
				}else{
					showPassDialog();  //显示权限对话框
				}
			}
		}else if("".equals(v_strUrl)==false){  
			
		}else if("".equals(v_strCmdExpression)==false){
			
		}
	}
	//显示用户权限对话框
	public void showPassDialog(){
    	//LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化  
		LayoutInflater factory = LayoutInflater.from(m_MainWindow.getContext());
		//把activity_login中的控件定义在View中
		final View textEntryView = factory.inflate(R.layout.pass_dialog, null);		 
//		textEntryView.setBackgroundColor(Color.RED); //只能 设置中间部分内容
        //将LoginActivity中的控件显示在对话框中 
		
		AlertDialog.Builder dialog =new AlertDialog.Builder(m_MainWindow.getContext());
	//	dialog.getContext().setTheme(R.style.Theme_dialog); //调用自定义样式	
		dialog.setTitle("用户权限登录")  
		//对话框的标题  
//      .setTitle("用户权限登录")
       //设定显示的View
       .setView(textEntryView)
       //对话框中的“登陆”按钮的点击事件
       .setPositiveButton("确定", new DialogInterface.OnClickListener() {

           public void onClick(DialogInterface dialog, int whichButton) { 
        	   
  			//获取用户输入的“用户名”，“密码”
        	//注意：textEntryView.findViewById很重要，因为上面factory.inflate(R.layout.activity_login, null)将页面布局赋值给了textEntryView了
        	final EditText etUserName = (EditText)textEntryView.findViewById(R.id.etuserName);
            final EditText etPassword = (EditText)textEntryView.findViewById(R.id.etPWD);
            
          //将页面输入框中获得的“用户名”，“密码”转为字符串
   	        String userName = etUserName.getText().toString().trim();
   	    	String password = etPassword.getText().toString().trim();
   	    	if(userName.equals("fang") && password.equals("pass")){
				String[] arrStr = v_strClickEvent.split("\\(");
				if (m_MainWindow != null && "Show".equals(arrStr[0])) {
					String[] arrSplit = arrStr[1].split("\\)");
					m_MainWindow.changePage(arrSplit[0]+".xml");  
				}
   	    	}else if(userName.equals(usr) && password.equals(passWork)){
				String[] arrStr = v_strClickEvent.split("\\(");
				if (m_MainWindow != null && "Show".equals(arrStr[0])) {
					String[] arrSplit = arrStr[1].split("\\)");
					m_MainWindow.changePage(arrSplit[0]+".xml"); 
				}
   	    	}else{
   	    		Toast.makeText(m_MainWindow.getContext(), "密码或用户名错误", Toast.LENGTH_SHORT).show();
 //  	    		Toast.makeText(m_rRenderWindow.getContext(), "Incorrect username or password!", Toast.LENGTH_SHORT).show();
   	    	}
          }
           
        })
	    //对话框的“退出”单击事件
	   .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	   //LoginActivity.this.finish();
	         }
	   })
	      
	   //对话框的创建、显示
		.create().show();

	}
	  //显示对话框
    public void showWaiterAuthorizationDialog() { 
    	
    	//LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化
		LayoutInflater factory = LayoutInflater.from(m_MainWindow.getContext());
		//把activity_login中的控件定义在View中
		final View textEntryView = factory.inflate(R.layout.root_dialog, null);
         
        //将LoginActivity中的控件显示在对话框中
		new AlertDialog.Builder(m_MainWindow.getContext())
		//对话框的标题
       .setTitle("后台维护")
       //设定显示的View
       .setView(textEntryView)
       //对话框中的“登陆”按钮的点击事件
       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) { 
        	   
  			//获取用户输入的“用户名”，“密码”
        	//注意：textEntryView.findViewById很重要，因为上面factory.inflate(R.layout.activity_login, null)将页面布局赋值给了textEntryView了
        	final EditText etUserName = (EditText)textEntryView.findViewById(R.id.etuserName);
            final EditText etPassword = (EditText)textEntryView.findViewById(R.id.etPWD);
            
          //将页面输入框中获得的“用户名”，“密码”转为字符串
   	        String userName = etUserName.getText().toString().trim();
   	    	String password = etPassword.getText().toString().trim();
   	    	
   	    	//现在为止已经获得了字符型的用户名和密码了，接下来就是根据自己的需求来编写代码了
   	    	//这里做一个简单的测试，假定输入的用户名和密码都是1则进入其他操作页面（OperationActivity）
   	    	if(   ( userName.equals("fang") && password.equals("pass") )
   	    	    ||( userName.equals("admin") && password.equals("kstar123") ) ){
				// 发起Home指令
				Intent intent = new Intent();
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.HOME");  
				m_MainWindow.getContext().startActivity(intent);
				Intent intent2 = new Intent("android.intent.action.STATUSBAR_VISIBILITY"); //隐藏导航栏 ok
				m_MainWindow.getContext().sendBroadcast(intent2);

   	    	}else{
   	    		Toast.makeText(m_MainWindow.getContext(), "密码或用户名错误", Toast.LENGTH_SHORT).show();
   	    	}
           }
       })
       //对话框的“退出”单击事件
       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
        	   //LoginActivity.this.finish();
           }
       })
       
        //对话框的创建、显示
		.create().show();
	}
	
	//重写dispatchDraw() 遍历绘制子view 遍历调用drawChild()函数	
	protected void dispatchDraw(Canvas canvas)  //绘制viewGroup列表的所有子控件   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //设置viewgroup的底板颜色 
		
		String path[] = v_strImgPath.split(".xml");	
		String imgPath = path[0]+".files/"+v_strImage;
//		Log.e("f_Image->dispatchDraw>>v_strImage=", imgPath); 
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
		
		if(("".equals(v_strClickEvent)==false) && (touchFlag)){
			mPaint.setColor(0x500000FF);
			mPaint.setStyle(Paint.Style.FILL); 
			canvas.drawRect(rect2, mPaint);
		}

	//	bitmap.recycle();
//		Log.e("Ks_Image>>onDraw","into");		
	}
	//重写onLayout() 绘制viewGroup中所有的view底板layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Image>>onLayout","into");		
		
	}

	//重写触摸事件onTouchEvent()  需要将该view add父容器上才能使用，即需要在图窗上层
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_Image-onTouchEvent","into");
		//invalidate();   //通知当前view 重绘制自己
		 switch (event.getAction())
         {
	            case MotionEvent.ACTION_DOWN: 
	            	Log.e("TAG_Image","into onTouchEvent ACTION_DOWN");
	            	touchFlag = true;
	            	doInvalidate();
	            	break;
	            case MotionEvent.ACTION_UP:
	            	touchFlag = false; 
	            	doInvalidate();
	 //           	Log.e("TAG_Image","into onTouchEvent ACTION_UP");
	         //   	onTouch_click();
	            	break;
	            default: break;
         }
		return true;
	}
	//调用Layout() 自身控件底板Layout大小位置绘制函数     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Image>>doLayout","into");
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

	//	v_strContent = strValue;
	
		return false;   //暂时不更新 控件
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
		    	 v_strImage = strValue;
		     }
		     else if ("passWork".equals(strName)) 
		        	passWork = strValue; 
		     else if ("user".equals(strName)) 
		    	 	usr = strValue; 
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
		return false;
	}	

}
