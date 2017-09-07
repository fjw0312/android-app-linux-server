package app.main.idu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import utils.Expression;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

//app 页面类 布局整体页面 
public class Page extends ViewGroup{
	//类的构造
	public Page(final MainActivity context) {
		super(context);
		m_MainActivity = context;
//		this.setFocusableInTouchMode(true);
	      
		//初始化 myHandler		
		myHandler = new Handler(){ //接收到消息处理
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				switch(msg.what){  
				case 0:
					break;
				case 1:
					//获取handler 信息 更新相应uis-id 的ui
					uiID = String.valueOf(msg.obj);
				//	Log.e("MainWindow->Handler>>ID=",uiID);  
					if(p_mapUis.get(uiID)!=null)
						p_mapUis.get(uiID).doInvalidate();  //调用控件内部 ui更新函数 
					break;
				default: 
					break; 
				}
			}
		};
		imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	UI_thread uiThread;
	@Override 
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
		if(visibility == View.VISIBLE){     //控件可见
			Log.e("TAG_page  "+strPage,"into View.VISIBLE");
			pageRunFlag = true;
			if(uiThread == null ){	
				uiThread = new UI_thread();
				uiThread.start();
			}else if(uiThread.isAlive()==false){
				uiThread = new UI_thread();
				uiThread.start();
			}

		}else if(visibility == View.GONE){  //控件不可见
			Log.e("TAG_page "+strPage,"into View.GONE");
			if(pageRunFlag){
				if(uiThread.isAlive()){
					uiThread.interrupt();
				}
			}
			pageRunFlag = false;
		}	
	}
	public InputMethodManager imm;  //定义键盘管理器变量
	
	//Fields
	public MainActivity m_MainActivity; //定义所在活动
	String strPage="";//页面名称
	String uiID = ""; //需要刷新的控件
	Canvas m_canvas;
	HashMap<String, VObject> p_mapUis = new HashMap<String, VObject>();//定义一个页面<控件名id，控件类>链表
//	List<VObject> p_listUis = new ArrayList<VObject>();//定义一个页面<控件类>数组
	List<String> p_listUisId = new ArrayList<String>();//定义一个页面<控件名id>数组
	List<String> pl_listUisId = new ArrayList<String>();//定义一个页面<控件名id>数组
	
	public int w_screen = 0;     //page屏幕的宽度
	public int h_screen = 0;     //page屏幕的高度
	public float w_screenPer = 1;//page屏幕的宽度比例
	public float h_screenPer = 1;//page屏幕的高度比例
	int touchPonitNum = 0; //点击的触摸点数
	float newDistance = 0;   //触摸放大的2点之间距离 第二点刚触摸时
	float oldDistance = 0;   //触摸放大的2点之间距离 第二点移动时
	float scalePer = 1;  //界面的放大比例
	
	boolean oneOpintFlag =true; //是否单点滑屏标识变量 单点滑屏 true;
	float start_x =0;
	float start_y =0;
	float end_x =0;
	float end_y =0;
	float tranlate_x =0; //移动画布的x距离
	float tranlate_y =0; //移动画布的y距离
	float oldTranlate_x =0; //移动画布的x距离old
	float oldTranlate_y =0; //移动画布的y距离old
	
	public Handler myHandler =null;
	boolean pageRunFlag = true;

	//重写ViewGroup 的onLayout  绘制各个子view的底板位置  一旦有已在的子view.onLayout()就会有调动
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
//	    Log.e("Page>>onLayout", strPage+"  into!");
		//遍历控件数组 重新绘制子Views的底板位置	
		if(arg0){
			Iterator<String> itstr = p_listUisId.iterator(); 
			while(itstr.hasNext()){
				String id = itstr.next();
				VObject views = p_mapUis.get(id);  
				views.doLayout(arg0, arg1, arg2, arg3, arg4);
			}
			Log.e("Page>>onLayout", strPage+"  true-onLayout 子控件结束!!!!!!!");
		}
//		Log.e("Page>>onLayout", strPage+"  onLayout 子控件结束!!!!!!!");
	}
	
	//重写viewGroup 的dispatchDraw()  绘制各个子view  一旦有已在的子view.onDraw()就会有调动     
	protected void dispatchDraw(Canvas canvas){
		super.dispatchDraw(canvas);
//		Log.e("Page>>dispatchDraw", strPage+"  into!");
		//遍历控件数组 重绘子views
/*		Iterator<String> itstr = p_listUisId.iterator();
		while(itstr.hasNext()){
			String id = itstr.next();
			VObject views = p_mapUis.get(id);
			drawChild(canvas, views.getViews(), getDrawingTime()); //绘制子view 
		//	Log.e("Page>>dispatchDraw", strPage+"绘制的控件名："+id);
		}
		Log.e("Page>>dispatchDraw", strPage+" dispatchDraw 子控件Draw结束!!!!!!!");
*/	}
	//重写viewGroup onTouchEvent() 
	public boolean onTouchEvent(MotionEvent event){
		Log.e("Page>>onTouchEvent>>>","into--");
		super.onTouchEvent(event);
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(getWindowToken(), 0);	
		}
		
		//event.getAction() 为低8位触摸类型，高8位+1位触摸点个数 
		//该函数&0xff 或&MotionEvent.ACTION_MASK = event.getActionMasked()
		switch(event.getActionMasked()){ //判断触摸的类型getActionMasked() 
			case MotionEvent.ACTION_DOWN : //有手指按下
				touchPonitNum = 1;				
				start_x = event.getX(0);
				start_y = event.getY(0);
				//隐藏键盘
//				InputMethodManager imm = (InputMethodManager)m_MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
				break;
			case MotionEvent.ACTION_UP : //有手指放起
				touchPonitNum = 0;
				
				end_x = event.getX(0);
				end_y = event.getY(0);
				if((oneOpintFlag==true) && (scalePer==1)){    //切动界面
					if((start_y-end_y <200)||(end_y-start_y <200)){
						if(start_x-end_x >30 ){  //右滑屏
							changPage(1);
						}else if(end_x-start_x >30){  //左滑屏
							changPage(2);
						}
					}
				}
				Log.e("Page>>onTouchEvent>>>","into ACTION_UP！");
				oneOpintFlag = true;	
				break;
			case MotionEvent.ACTION_POINTER_DOWN: //有一个非主要的手指按下
				touchPonitNum += 1;
				oldDistance = distance(event); //获取多点触摸按下时距离
				oneOpintFlag = false;  //多点滑屏系列动作
//				Log.e("MainWindow-onTouchEvent","ACTION_POINTER_DOWN");
				break;
			case MotionEvent.ACTION_POINTER_UP: //有一个非主要的手指放起
				touchPonitNum -= 1;
//				Log.e("MainWindow-onTouchEvent","ACTION_POINTER_UP");
				break;
			case MotionEvent.ACTION_MOVE : //有手指移动
				if(touchPonitNum>=2){  //有多点触摸
					Log.e("page-onTouchEvent","ACTION_MOVE");
					newDistance = distance(event); //获取多点触摸移动时距离
					float per = (newDistance-oldDistance)/100;					
					scalePer = per + scalePer;
					if(scalePer<1) scalePer = 1;
					if(scalePer>10) scalePer = 10;
//					Log.e("MainWindow-onTouchEvent->>distance:","比例"+String.valueOf(scalePer));
					if(newDistance-oldDistance>5){ //放大						
						this.setScaleX(scalePer); //比例缩放
						this.setScaleY(scalePer);
						
					}else if(oldDistance-newDistance>5){ //缩小				
						this.setScaleX(scalePer); //比例缩放
						this.setScaleY(scalePer);			
				    }

				}else{    //单点移动
					if(scalePer>1){     //放大后拖动 屏幕位置       notices:未处理拖动到空白处
						end_x = event.getX(0);
						end_y = event.getY(0);
						tranlate_x = end_x-start_x;
						tranlate_y = end_y-start_y;
						this.setTranslationX(oldTranlate_x+tranlate_x);//移动view的底板位置
						this.setTranslationY(oldTranlate_y+tranlate_y);	
						oldTranlate_x = oldTranlate_x+tranlate_x;
						oldTranlate_y = oldTranlate_y+tranlate_y;
						Log.e("MainWindow-onTouchEvent>>oldTranlate_x",String.valueOf(oldTranlate_x));
						Log.e("MainWindow-onTouchEvent>>oldTranlate_y",String.valueOf(oldTranlate_y));
					}else{
						this.setTranslationX(0);//移动view的底板位置     
						this.setTranslationY(0);
						oldTranlate_x = 0;
						oldTranlate_y = 0;
					}
				}
				break;
				
		}
		
		return true;
		
	}
	//计算2点触摸的距离  用于放大界面
	private float distance(MotionEvent event){
		float eX = event.getX(1) - event.getX(0); // 后面的点坐标 - 前面点的坐标
		float eY = event.getY(1) - event.getY(0);
		return FloatMath.sqrt(eX * eX + eY * eY);
	}

	
	//页面加载
	@SuppressLint({ "UseSparseArrays", "FloatMath" })
	public void loadPage(String strXmlFile){
		strPage = strXmlFile;
		//解析xml页面
		ParseXml parsexml = new ParseXml(m_MainActivity);
		try{
		p_mapUis = parsexml.getXmlStream(strPage);
		}catch(Exception e){
			Log.e("MainWindow-loadPage","解析xml文件异常抛出");
		}
		parsexml = null;
		
		//提取最大的图层数值-定义<控件图层，控件类>链表并将控件链表赋予
		HashMap<Integer,VObject> p_mapIuis = new HashMap<Integer,VObject>();
		int maxZIndex = 0;
		Iterator<String> iter = p_mapUis.keySet().iterator();
		while(iter.hasNext()){
			String strViews = iter.next();
			VObject views = p_mapUis.get(strViews);
			p_mapIuis.put(views.getViewsZIndex(), views);
			if(views.getViewsZIndex()>maxZIndex){
				maxZIndex = views.getViewsZIndex();
			}		
		}
		
		//将控件按图层排序放入数组
		for(int i=0;i<maxZIndex+1;i++){
			if(p_mapIuis.containsKey(i)){   //判断该图层是否有控件
				VObject views = p_mapIuis.get(i);
//				p_listUis.add(views);
				p_listUisId.add(views.getViewsID());
			}
		}
	//	p_mapIuis = null;
		
		//遍历数组控件每个放入页面
		Iterator<String> itstr = p_listUisId.iterator();
		while(itstr.hasNext()){
			String id = itstr.next();
			VObject views = p_mapUis.get(id);
			views.doAddViewsToWindow(this); //将控件添加入页面
		}
		
		
		//解析各个控件的绑定表达式
		Iterator<String> itId = p_mapUis.keySet().iterator();
		while(itId.hasNext()){
			String id = itId.next();
			VObject obj = p_mapUis.get(id);
			if(obj==null) continue;
			obj.parseExpression(""); //解析自身控件的绑定表达式			
		}			 
		System.gc();
		//启动UI数据刷新线程
		uiThread = new UI_thread();
		uiThread.start();
	}
	
	//页面切换  //输入参数eg:page2.xml
	public void changePage(String strNewPage){
		m_MainActivity.onPageChange(strNewPage);
	}
	//页面切换  //输入参数arg 0, 1:下一页 2：前一页
	public void changPage(int arg){
		if(arg==1)
			m_MainActivity.nextPageChange();
		if(arg==2)
			m_MainActivity.prePageChange();
	}
	
	
	//实例化 一个UI数据刷新的线程
	private class  UI_thread extends Thread {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			List<VObject> Uis = p_listUis;	//<控件类>数组 //获取当前页面的控件链表
						
			while(pageRunFlag){				
				try{  //sleep 2s  
					Thread.sleep(500); //2000ms   			
					if(Page.this.isShown()==false) return;  //判断是否页面可见
					Log.i("MainWindow->UI_thread>>页面名称",strPage+"   into!");
			
					//遍历控件数组 更新控件数据
					Iterator<String> itId_str = p_mapUis.keySet().iterator();
					while(itId_str.hasNext()){
						String id = itId_str.next();
						VObject obj = p_mapUis.get(id);
						if(obj==null) continue;
						//jk mark notice:若空间内自与用户交互发生数据改变，该函数无返回true 无通知handler刷新ui
						boolean up = obj.updataValue("自更新"); //调用控件自动数据更新方法					
						String str_uiId = obj.getViewsID();
						if("".equals(str_uiId)) continue;
						if(up){  //判断控件数据更新 返回是否成功
							Message msg = new Message();
							msg.obj = str_uiId;
							msg.what = 1;
							myHandler.sendMessage(msg);   //发送handler 消息  
						}		
					}
					Thread.sleep(1500); //1000ms  			
					if(Page.this.isShown()==false) return;  //判断是否页面可见
				}catch(InterruptedException e){ 
					Log.i("page->UI_thread>>","线程中断  抛出！ "); 
					return;
				}catch(Exception e){ 
					Log.w("page->UI_thread>>","sleep 错误抛出！ "); 
				}
		//		Log.e("MainWindow->UI_thread>>页面名称"+strPage+"--","end! ");
			}
		}
	}

}
