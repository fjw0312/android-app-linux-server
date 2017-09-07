package mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import android.util.Log;
import app.main.idu.MainActivity;
import data.net_model.Net_control;
import data.net_service.DataHAL;
import data.net_service.NetHAL;
import data.pool.DataPoolModel;
import data.pool_model.Event;



//邮件 告警信息 解析 线程       fang  Add
public class EMailEventParseThread extends Thread{

	public EMailEventParseThread() {
		// TODO Auto-generated constructor stub
	}
	
	//定义告警信息数据 用于告警线程处理
	public static List<String> eventLst = new ArrayList<String>(); 
	public static List<String> old_eventLst = new ArrayList<String>();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		EmailHandler.EmailHandler_init();   //初始化 读取 邮件配置文件 
		try {			
			Thread.sleep(5000);  //延时处理
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true){
			if(EmailHandler.runFlag==false){ 
				try{
					Thread.sleep(2000); //1s的周期线程
				}catch(Exception e){
					
				}
				if("".equals(MainActivity.EventCmd)){
					continue;
				}				
			}
			eventLst.clear();
			if(DataPoolModel.getAllEvent() !=null){				
				//遍历告警项
				List<Event> lst = DataPoolModel.getAllEvent();
				for(int i=0;i<lst.size();i++){
					Event event = lst.get(i);  
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式转换
					Date date = new Date(event.starttime*1000);
					String sampletime = formatter.format(date);	
					//处理告警等级
					String grade = "一般告警";
					switch(event.grade){ 
							case 1:  grade = "通知";
								break;
							case 2:  grade = "一般告警";
								break; 
							case 3:  grade = "严重告警";
								break;
							case 4:  grade = "致命告警";
								break;
							default:
								break;
					}
							String str = "设备："+DataPoolModel.getEquipment(event.equipId).equipName.trim()+" \n"
									+"告警名称："+event.name.trim()+" \n"
									+"告警等级："+grade+" \n"
							//		+event.value+" "
									+"告警含义："+event.meaning.trim()+" \n"
									+"开始时间："+sampletime+" \n ";
							eventLst.add(str);	
				}											
			} 
			//--------------------
			//判断告警信息是否为新产生 
			for(int i=0;i<eventLst.size();i++){
					if( old_eventLst.contains(eventLst.get(i)) ){//告警之前已产生
					//	Log.e("TAG1", eventLst.get(i));
					}else{//告警新产生 
						Log.i("TAG2-新告警产生", eventLst.get(i));//新告警产生	
						//告警联动
						if(eventLst.get(i).contains(MainActivity.EventCmd.trim())){
							Log.e("TAG-EventCmd>>告警联动 告警产生", MainActivity.EventCmd);//新告警产生
							EventCmdThread eventCmdThread = new EventCmdThread();
							eventCmdThread.value = "1";
							eventCmdThread.start();
						}
						//发邮件----------------
						if(EmailHandler.runFlag){
							EmailHandler email = new EmailHandler();
							email.content = "==发生告警==\n"+eventLst.get(i);
							email.E_Handler();
						}
					} 
				}
				//判断告警消失
				if(old_eventLst.size()>eventLst.size()){ 
					for(int i=0;i<old_eventLst.size();i++){ 
						if( eventLst.contains(old_eventLst.get(i))==false ){//告警消失的告警信息
							long time = java.lang.System.currentTimeMillis();//更新采集时间	
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式转换
							Date date = new Date(time);
							String sampletime = formatter.format(date);
							String str = old_eventLst.get(i).substring(0, old_eventLst.get(i).length()-1)
									+"结束时间："+sampletime; 
							Log.i("TAG3-告警结束", str); //告警结束的信息 
							//告警联动
							if(old_eventLst.get(i).contains(MainActivity.EventCmd.trim())){
								Log.e("TAG-EventCmd>>告警联动 告警消失", MainActivity.EventCmd);//新告警产生
								EventCmdThread eventCmdThread = new EventCmdThread();
								eventCmdThread.value = "0";
								eventCmdThread.start();
							}
							//发邮件 ----------------
							if(EmailHandler.runFlag){
								EmailHandler email = new EmailHandler();
								email.content = "==告警结束==\n"+str; 
								email.E_Handler();
							}
						} 
					}
				}
				
				old_eventLst.clear();
				for(int i=0;i<eventLst.size();i++){
					old_eventLst.add(eventLst.get(i));
				}
				eventLst.clear();

				try{
					Thread.sleep(1000); //1s的周期线程
				}catch(Exception e){
					
				}
		}
	}
	//告警联动
	private class EventCmdThread extends Thread{
		String value = "";
		@Override	
		public void run() {
			// TODO Auto-generated method stub
			super.run();
	    	List<Net_control> lstCtrl = new ArrayList<Net_control>();
	    	Net_control ipcC = new Net_control();
			ipcC.equipid = 1;  //联动控制设备id
			ipcC.ctrlid = 1;   //联动控制设备cmdId 
			ipcC.valuetype = 1;  //Parameter
			ipcC.value = value;
			lstCtrl.add(ipcC);
			DataHAL.send_control_cmd(NetHAL.IP, NetHAL.Port,lstCtrl);
			Log.e("Do_EventCmd>告警联动", "控制！");//新告警产生   
			lstCtrl = null;
			ipcC = null;
		}

	}

}
