package com.example.ab001;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import service_net.net_service_thread;

import DataModel.Model;
import DataModel.Signal;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	//定义变量
	RelativeLayout layout_page = null; //定义布局页面布局容器
	MainWindow page = null; //定义显示页面类
	String strPage="";      //当前页面的名称
	
	List<String> list_strPageName = new ArrayList();//定义页面名链表数组
	HashMap<String, MainWindow> m_page = new HashMap<String, MainWindow>(); //定义<页面名,页面类>链表
	
	String pagePath ="";//页面文件的路劲
	String pageDir = "/page_path/"; //页面文件的文件夹名称
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏除app标题栏 需放setContentView之前		 
		setContentView(R.layout.activity_main);
		
		//设置窗口全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏系统标题栏     //隐藏导航栏
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		//获取活动画布窗口
		layout_page = (RelativeLayout)findViewById(R.id.layout_id);
		//获取页面文件路劲
		pagePath = Environment.getExternalStorageDirectory().getPath()+pageDir;
		Log.e("MainAcitivity-onCreate>>pagePath:", pagePath);
		
		//获取pagelist实例化页面
		if(!readPage()){
			Log.e("MainAcitivity-onCreate","获取加载页面失败");
			Toast.makeText(this, "readPage 失败！", Toast.LENGTH_LONG).show();
		}

		//将首页放入画布窗口
		if(list_strPageName.isEmpty()){
			Toast.makeText(this, "pagelist 读取结果为空！", Toast.LENGTH_LONG).show();
			Log.e("MainAcitivity-onCreate>>list_strPageName:", "isEmpty");
		}else{
		
			strPage = list_strPageName.get(0);	
			page = m_page.get(strPage);
			layout_page.addView(page, 1024, 768);   //vtu 出去标题栏、下标栏剩余约1024*635
		}
		
		//初始化网络请求数据链
		que_model_node_init();
		
		//网络数据通信线程 启动
		net_service_thread net_thread = new net_service_thread();
		net_thread.start();
		 
	}
	
	
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
			for(int i=0;i<120;i++){  //最多120个页面的设计
				String strpage = reader.readLine();
				strpage = strpage.trim(); //去除字符串左右边空格
				if("".equals(strpage)){
					break;		  //当页面读取完毕跳出循环		
				}else{
					list_strPageName.add(strpage); //将读取的页面名称放入 页面名称数组链表
				}
			}
			reader.close();
		}catch(Exception e){			
	//		Log.e("MainAcitivity-readPage","读取pagelist页面内容失败！");
		}
		//遍历页面链表加载各个页面 实例化 放入<页面名称，页面类>链表
		Iterator<String> itr = list_strPageName.iterator();
		while(itr.hasNext()){
			String strPage = itr.next(); //获取页面名称
			String strPagePath = pagePath+strPage; //获取页面问文件决定路径
			MainWindow page = new MainWindow(this); //定义页面类变量
			page.loadPage(strPagePath);            //加载页面实例化
			m_page.put(strPage, page);            //页面 放入<页面名称，页面类>链表       
			
		}
		return true;
	}
	
	
	//activity切换页面 跳转到某一页
	public void onPageChange(String new_pageName){
		MainWindow oldPage = m_page.get(strPage);         //获取当前页面类
		MainWindow newPage = m_page.get(new_pageName);    //获取将要切换新页面类
		layout_page.removeView(oldPage);   
		layout_page.addView(newPage);
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

	
	//初始化请求数据模型
	private void que_model_node_init(){
		
		//初始化 数据模型
		Model model = new Model();
/*	//方式一	
		//初始化 数据模型的数据请求链
		List<Integer>  lst = new ArrayList<Integer>();
		for(int i=1;i<4;i++){
			lst.add(i);
		}
		//初始化 数据模型基本请求数据
		Model.Model_init_lst(lst); 
*/	
		
	//方式二
	//	Model.add_get_setHt(1, 10, 0, 0); //初始化 数据请求表
	//	Model.add_set_setHt(1, 20, 0, 0); //初始化 数据请求表
		Model.add_que_setHt(1, 30, 0, 0); //初始化 数据请求表
		Model.add_que_setHt(2, 30, 0, 0); //初始化 数据请求表
	//	Model.add_cmd_setHt(1, 40, 0, 0); //初始化 数据请求表
/*		//添加get链表设备id 2 一个信号类  
		Signal sig1g = new Signal(1,"get-2-信号",200,"信号含义1");    
		Model.add_Signal_getHt(1,sig1g.signalID,sig1g);
		Signal sig2g = new Signal(2,"get-2-信号",400,"信号含义2");
		Model.add_Signal_getHt(1,sig2g.signalID,sig2g); 
		//添加set链表设备id 2 一个信号类
		Signal sig1s = new Signal(1,"set-2-信号",200,"信号含义1");
		Model.add_Signal_setHt(1,sig1s.signalID,sig1s); 
		Signal sig2s = new Signal(2,"set-2-信号",400,"信号含义2");
		Model.add_Signal_setHt(1,sig2s.signalID,sig2s);
		//添加que链表设备id 2 一个信号类
		Signal sig1 = new Signal(2,"que-2-信号",200,"信号含义2"); 
		Model.add_Signal_queHt(2,sig1.signalID,sig1);
		Signal sig2 = new Signal(3,"que-2-信号",400,"信号含义3"); 
		Model.add_Signal_queHt(2,sig2.signalID,sig2);
		Signal sig3 = new Signal(4,"que-2-信号",400,"信号含义4");
		Model.add_Signal_queHt(2,sig3.signalID,sig3);
		//添加cmd链表设备id 2 一个信号类
		Signal sig1c = new Signal(1,"cmd-2-信号",200,"信号含义1");
		Model.add_Signal_cmdHt(1,sig1c.signalID,sig1c);
		Signal sig2c = new Signal(2,"cmd-2-信号",400,"信号含义2");
		Model.add_Signal_cmdHt(1,sig2c.signalID,sig2c);
*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
