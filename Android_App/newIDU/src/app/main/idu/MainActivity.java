package app.main.idu;
/**********
 * 版权公司：ChaoYF   
 * 设计时间：2016-10-1  designed TO 2017-2-21
 * 设计人：fang     E-mail:fjw0312@163.com
 * 版本号：初设计版本    版权问题于 fjw0312个人所有
 * ********/
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import utils.DealFile;
import data.pool.DataPoolThreadRun;
import SAM.DataPool.DataPoolRun;
import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_EquiptCfg;
import SAM.XmlCfg.xml_cmdCfg;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_signalCfg;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

//数据 接口方案  采用SAM
public class MainActivity extends Activity {

	//定义变量  
	RelativeLayout layout_page = null; //定义布局页面布局容器
	Page myPage = null; //定义显示页面类
	String strPage="";      //当前页面的名称 
	
	List<String> list_strPageName = new ArrayList<String>();//定义页面名链表数组
	HashMap<String, Page> m_page = new HashMap<String, Page>(); //定义<页面名,页面类>链表 
	
	String pagePath ="";//页面文件的路劲 
	String pageDir = "/IDU_Page/"; //页面文件的文件夹名称
	
	public static int screen_width = 1024; //屏幕分辨率 宽度
	public static int screen_height = 768; //屏幕分辨率 高度
	public static float densityPer = 1;
	boolean loadPageFlag = false; //页面加载结束标志位 
	public static String EventCmd = "";   //用于告警联动
	public static int DataCaseNum = 2;   //采用的数据方案借口   //1:dataNet  2:SAMNet

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏除app标题栏 需放setContentView之前
		setContentView(R.layout.activity_main);

		//设置窗口全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏系统标题栏     
		
		Intent intent = new Intent("android.intent.action.STATUSBAR_INVISIBILITY"); //隐藏导航栏 ok
		this.sendBroadcast(intent);
				
	//	  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//隐藏导航栏 
	//    getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);        //隐藏导航栏图标  
	//    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE); //隐藏导航栏图标 
	
		//获取屏幕分辨率
  //     Display display = getWindowManager().getDefaultDisplay();
  //     screen_width = display.getWidth();  
  //     screen_height = display.getHeight(); 
  //     Log.e("MainAcitivity-onCreate>>--f1--获取屏幕密度大小:", "screen_width="+String.valueOf(screen_width)+" screen_height="+String.valueOf(screen_height));
		
	    screen_width = this.getResources().getDisplayMetrics().widthPixels;
		screen_height = this.getResources().getDisplayMetrics().heightPixels+50; 
		densityPer = this.getResources().getDisplayMetrics().density;
//		Toast.makeText(this, "screen_width="+String.valueOf(screen_width)+"screen_height="+String.valueOf(screen_height), Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "densityPer="+String.valueOf(this.getResources().getDisplayMetrics().density), Toast.LENGTH_LONG).show();
		
		//获取告警联动  配置  
		try{
			DealFile file = new DealFile();
			String str= file.read_File_str_line("/mnt/sdcard/ECMD.txt", "EventCmd"); 
			String[] a = str.split("=");
			EventCmd = a[1];
			Log.e("MainActivity>>EventCmd",  EventCmd); 
		}catch(Exception e){
			EventCmd = "";  
		}
		
		//获取活动画布窗口  
		layout_page = (RelativeLayout)findViewById(R.id.layout_id); 
		
		//获取页面文件路劲
		pagePath = Environment.getExternalStorageDirectory().getPath()+pageDir;
		Log.e("MainAcitivity-onCreate>>pagePath:", pagePath);
		
		//将页面加载线程
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub  
				//获取pagelist实例化页面    此处很耗时 5s  考虑用线程 执行
				if(!readPage()){
					Log.e("MainAcitivity-onCreate","获取加载页面失败");
					myhandler.sendEmptyMessage(2);			
				}
				myhandler.sendEmptyMessage(3);	
				loadPageFlag = true;  //页面加载结束标志 
			}
		}).start();
		
		if(DataCaseNum == 1){       // 执行数据线程  方案：1
			DataPoolThreadRun dataThread = new DataPoolThreadRun(); 
			dataThread.start();
		}else if(DataCaseNum == 2){  // 执行数据线程  方案：2
			DataPoolRun dataRun = new DataPoolRun();
			dataRun.start();
		}
		
	}

	private Handler myhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub  1
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				String strPageName = (String)msg.obj;
				String strPagePath = pagePath+strPageName; //获取页面问文件决定路径		
				Page page = new Page(MainActivity.this); //定义页面类变量
				page.loadPage(strPagePath);            //加载页面实例化 
				m_page.put(strPageName, page);             //页面 放入<页面名称，页面类>链表    
	//			Log.e("MainActivity>>myhandler",strPageName+"   "+strPagePath);
				//加载页面到界面
				if(strPageName.equals(list_strPageName.get(0))){
					page.setVisibility(View.VISIBLE);
					strPage = strPageName;	
					myPage = page; 
				}else{
					page.setVisibility(View.GONE);
				}				
				layout_page.addView(page);	//内部视图全部呈现  耗时1s
				break;	
			case 2:
				Toast.makeText(MainActivity.this, "readPage 失败！", Toast.LENGTH_LONG).show();
				break;
			case 3:				
				Toast.makeText(MainActivity.this, "页面加载结束！", Toast.LENGTH_LONG).show();
				break;

			default: break;
			}
		}
		
	};
	
	//读取pagelist文件中页面文件名，实例化加载各个页面函数
	private boolean readPage() {
		//读取页面名称文件 
		String pagelistPath = pagePath+"pagelist";
		BufferedReader reader = null; //定义读取文件缓存字符流 
		try{ 
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(pagelistPath),"gb2312"));  //用文件字节流读取 转换成读取字符流 再装换成缓冲字符流
		}catch(Exception e){
			Log.e("MainAcitivity-readPage","读取pagelist文件失败！");
			return false;
		}		
		//获取页面名称链表
		try{
			for(int i=0;i<100;i++){  //最多100个页面的设计
				String strpage = reader.readLine();			
				if("".equals(strpage) || strpage ==null){
					break;		  //当页面读取完毕跳出循环		
				}else{
					strpage = strpage.trim(); //去除字符串左右边空格
					list_strPageName.add(strpage); //将读取的页面名称放入 页面名称数组链表
				}
			}
			reader.close();
		}catch(Exception e){	
			reader = null;
			Log.e("MainAcitivity-readPage","读取pagelist页面内容失败！");
		}
		
		//先加载首页   目的：所有页面连续加载 会 白屏过久！ 
		try{
			Message msg = new Message();
			msg.obj = list_strPageName.get(0);
			msg.what = 1;
			myhandler.sendMessage(msg);
			Thread.sleep(4*1000);
		
			//遍历页面链表加载各个页面 实例化 放入<页面名称，页面类>链表
			for(int i=1;i<list_strPageName.size();i++){
				String strPageName = list_strPageName.get(i); //获取页面名称
				Message msg2 = new Message();
				msg2.obj = strPageName;
				msg2.what = 1;
				myhandler.sendMessage(msg2); 
			
			}
		}catch(Exception e){
			Log.e("MainActivity>>readPage-1","异常抛出！");	
		}
	
		return true;
	}
	
	
	//activity切换页面 跳转到某一页 
	public void onPageChange(String new_pageName){
		if(loadPageFlag==false){
			Toast.makeText(MainActivity.this, "页面加载中，请稍后！", Toast.LENGTH_LONG).show();
			return;
		}
		Page oldPage = m_page.get(strPage);         //获取当前页面类  
		Page newPage = m_page.get(new_pageName);    //获取将要切换新页面类 
	//	layout_page.addView(newPage);   //用加载的方式  响应速度太慢了！
	//	layout_page.removeView(oldPage); 
	//	newPage.bringToFront();         //调图层显示 响应也是太慢！		
		oldPage.setVisibility(View.GONE);
		newPage.setVisibility(View.VISIBLE);
		
		oldPage.clearFocus();
//		newPage.requestFocus();
		
		Log.e("MainActivity>>onPageChange>>>","进入切换页面！");
		myPage = newPage;
		strPage = new_pageName;
	} //下一页 
	public void nextPageChange(){
		int num = list_strPageName.indexOf(strPage);
		num++;
		if(list_strPageName.size()<=num) return;
		String new_pageName = list_strPageName.get(num);
		onPageChange(new_pageName);
	}//上一页
	public void prePageChange(){
		int num = list_strPageName.indexOf(strPage);
		num--;
		if(num<0) return;
		String new_pageName = list_strPageName.get(num);
		onPageChange(new_pageName);		
	}
}
