package data.extraHisModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import app.main.idu.VObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;



//author: fang 
//date: 2016.11.17 
//考虑到  该线程运行在每一个页面  不能实现 整个系统的整体控制所有历史控件刷新  故舍弃！
/**历史控件ui 数据界面 刷新线程*/
public class HisUisUpdateRun extends Thread{
	public HisUisUpdateRun() {
		// TODO Auto-generated constructor stub
		mapUIs= new HashMap<String,VObject>();
	}
	
	//变量
	public HashMap<String, VObject> mapUIs = null; //需要刷新 的历史控件  <控件id， 控件类>
	
	//定义Handler
	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){  
			case 0:
				break;
			case 1:
				//获取handler 信息 更新相应uis-id 的ui
				String id = String.valueOf(msg.obj);
				
				if(id != null && mapUIs.get(id).getViews().isShown())					
					mapUIs.get(id).doInvalidate();  //调用控件内部 ui更新函数
				Log.e("HisUisUpdateRun>>myHandler>>>通知刷新：",  mapUIs.get(id).getViewsType());
				break;
			default:  
				break; 
			}

		}	
	};
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try{
			Thread.sleep(1000*20);
		}catch(Exception e){
			
		}
		while(true){
			try{
//				Log.e("HisUisUpdateRun>>run()","into！");
				Thread.sleep(5000);
				if(mapUIs==null) continue;
				//遍历控件
				Iterator<String> ids = mapUIs.keySet().iterator();
				while(ids.hasNext()){
					String id = ids.next();
					VObject obj = mapUIs.get(id);
					if(obj==null) continue;
					boolean up = obj.updataValue("更新"); //调用控件自动数据更新方法					
					if(up){  //判断控件数据更新 返回是否成功
						Message msg = new Message();
						msg.obj = id;
						msg.what = 1;
						myHandler.sendMessage(msg);   //发送handler 消息 
						Log.e("HisUisUpdateRun>>run()>>>发送消息：", obj.getViewsType());
					}
				}
				
			}catch(Exception e){
				Log.e("HisUisUpdateRun>>run()","异常抛出！");
			}
		}
	}



}
