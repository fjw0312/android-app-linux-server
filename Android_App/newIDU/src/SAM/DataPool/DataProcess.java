package SAM.DataPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import SAM.DataPoolModel.Event;
import SAM.DataPoolModel.Signal;
import SAM.XmlCfg.xmlDataModel;
import SAM.XmlCfg.xml_EquiptCfg;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import SAM.XmlCfg.xml_signalCfg;
import SAM.extraHisModel.HisDataEventSave;

//解析 实时 数据  类
public class DataProcess {

	public DataProcess() {
		// TODO Auto-generated constructor stub
	}
	

	public static void AllEquipt_initSignal(){
		//遍历设备配置链表
		if(NetDataModel.lst_poolEquipmentId== null) return;
		Iterator<Integer> equiptId_lst = NetDataModel.lst_poolEquipmentId.keySet().iterator();
		while(equiptId_lst.hasNext()){
			int equiptId = equiptId_lst.next(); //获取 设备 id
			int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
			xml_EquiptCfg xmlEquiptCfg = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId);//获取设备配置类
			if(xmlEquiptCfg==null || xmlEquiptCfg.xml_signalCfg_lst==null) return;
			Hashtable<Integer, Float> sigValue_lst = new Hashtable<Integer, Float>();
			Hashtable<Integer, Signal> sig_lst = new Hashtable<Integer, Signal>();
			//遍历信号配置链表
			Iterator<String> sigId_lst = xmlEquiptCfg.xml_signalCfg_lst.keySet().iterator();
			while(sigId_lst.hasNext()){
				String sigId = sigId_lst.next();
				int id = Integer.parseInt(sigId);
				sigValue_lst.put(id, (float)0.00);
				
				try{ //初始化  信号类
					xml_signalCfg sigCfg = xmlEquiptCfg.xml_signalCfg_lst.get(sigId);				
					Signal sig = new Signal();
					sig.equiptId = equiptId;
					sig.sigId = id;
					sig.name = sigCfg.SignalName;
					sig.unit = sigCfg.Unit;
					sig.type = Integer.parseInt(sigCfg.SignalType);
					sig_lst.put(sig.sigId, sig);
					}catch(Exception e){
						Log.e("DataProcess->AllEquipt_initSignal","异常抛出！");
					}
			}
			NetDataModel.hm_Pool_htSigValue.put(equiptId, sigValue_lst);
			NetDataModel.hm_Pool_htSignal.put(equiptId, sig_lst);
		}
		NetDataModel.lst_Pool_Event.clear();
		NetDataModel.lst_Pool_Tigger.clear();
	//	NetDataModel.lst_Pool_SCmd.clear();
		NetDataModel.lst_Pool_SCmdStr.clear();
	}
	

	//判断 告警产生  函数
	public static void parseEquiptData(float[] rvData){
		if(rvData == null) return;
		int equiptId = (int)rvData[0];		 //获取设备  id
		Log.i("DataProcess>>parseEquiptData>>>设备id：", String.valueOf(equiptId));
		if(equiptId == 0) return;
		int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
		xml_EquiptCfg xmlEquiptCfg = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId);//获取设备模板
		//遍历 设备信号通道
		if(xmlEquiptCfg==null || xmlEquiptCfg.xml_signalCfg_lst == null) return;
		Iterator<String> sigCfgId_lst = xmlEquiptCfg.xml_signalCfg_lst.keySet().iterator();
		while(sigCfgId_lst.hasNext()){
			String Id = sigCfgId_lst.next();    //获取信号id
			int id = Integer.parseInt(Id); 
			String No = xmlEquiptCfg.xml_signalCfg_lst.get(Id).ChannelNo; //获取信号通道
			int no = Integer.parseInt(No);  
			float value = 0;
			if(no == -1){
				value = rvData[1];
			}else if(no<0){ //虚拟通道  暂时不处理 为 888
				value = 888;
			}else{
				value = rvData[no+2];   //获取通道信号的数值
			}		
			NetDataModel.hm_Pool_htSigValue.get(equiptId).put(id,value); //更新链表 信号值数据
		}
	}
	//解析告警信息
	public static void parseEquiptEvent(float[] rvData){
		if(rvData == null) return;
		int equiptId = (int)rvData[0];		 //获取设备  id   
		if(equiptId == 0) return;
		int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
		xml_EquiptCfg xmlEquiptCfg = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId);//获取设备模板
		//实例化  该设备的告警链表
		List<Event> event_lst = null;		
		if(NetDataModel.lst_Pool_Event.containsKey(equiptId)){
			event_lst = NetDataModel.lst_Pool_Event.get(equiptId);
			event_lst.clear();
		}else{
			event_lst = new ArrayList<Event>();
		}

		//遍历告警配置
		if(xmlEquiptCfg==null || xmlEquiptCfg.xml_eventCfg_lst == null) return;
		Iterator<String> eventCfgId_lst = xmlEquiptCfg.xml_eventCfg_lst.keySet().iterator();
		while(eventCfgId_lst.hasNext()){
			String EventId = eventCfgId_lst.next();  //告警配置  告警id
			int eventId = Integer.parseInt(EventId);
			xml_eventCfg xmlEventCfg= xmlEquiptCfg.xml_eventCfg_lst.get(EventId); 
			if("false".equals(xmlEventCfg.Enable)) continue;  //告警不是能
			String eventExpression = xmlEventCfg.StartExpression;
			String str[] = eventExpression.split(",|\\]");
			if(str.length<2) continue;
			String strSigid = str[1];
			int sigId = Integer.parseInt(strSigid);
			float value = NetDataModel.hm_Pool_htSigValue.get(equiptId).get(sigId);
			//遍历告警子项  对比数值是否告警
			Iterator<String> eventConditionlst = xmlEventCfg.EventConditionlst.keySet().iterator();
			while(eventConditionlst.hasNext()){
				 String strId = eventConditionlst.next();
				 EventCondition eventcondition = xmlEventCfg.EventConditionlst.get(strId);
				 String state = eventcondition.StartCompareValue;
				 String symbol = eventcondition.StartOperation;				
				 float fState = Float.parseFloat(state); 
				 if( asDeal(value,fState,symbol) ){  //产生告警
					 Event event = new Event();
					 event.equipId = equiptId;
					 event.equipName = xmlEquiptCfg.EquipTemplateName;
					 event.eventId = eventId;
					 event.name = xmlEventCfg.EventName;
					 event.meaning = eventcondition.Meaning;
					 event.grade = Integer.parseInt(eventcondition.EventSeverity); 
					 event.is_active = 1;
					 event.value = value;
					 event.stoptime = 0; 
					 if(eventcondition.startAlarmTime == 0){ //刚刚  开始告警						 
						 eventcondition.startAlarmTime = System.currentTimeMillis()/1000;  //以 s为单位; 
						 eventcondition.nowAlarmchangeState = 1;
					 }else{  //告警已经产生
						 event.starttime =  eventcondition.startAlarmTime;
						 eventcondition.nowAlarmchangeState = 2;
					 }
					 event.starttime = eventcondition.startAlarmTime;
					 //***目前存在开始时间保存不住bug 
					 //判断是否为新告警   遍历老告警 是否含有新告警的告警id						
					 event_lst.add(event); 
					 Log.i("DataProcess->parseEquiptEvent>>设备："+String.valueOf(equiptId),
							 "告警id:"+EventId+"  告警值："+state+
					         "++++信号id:"+strSigid+" 信号值："+String.valueOf(value));
				 }else{	 //无告警状态
					 
					 if(eventcondition.nowAlarmchangeState>0){ //表示告警刚刚消失
						 eventcondition.nowAlarmchangeState = -1;
						 try{
							 HisDataEventSave hisDataEventSave = new HisDataEventSave();
							 hisDataEventSave.startTime = eventcondition.startAlarmTime;
							 hisDataEventSave.strEquiptId = String.valueOf(equiptId);
							 hisDataEventSave.strEquiptName = xmlEquiptCfg.EquipTemplateName;
							 hisDataEventSave.xmlEventCfg = xmlEventCfg;
							 hisDataEventSave.eventcondition = eventcondition;
							 hisDataEventSave.value = String.valueOf(value);
							 hisDataEventSave.start();
						 }catch(Exception e){
							 Log.e("DataProcess->parseEquiptEvent","历史告警线程进入   异常抛出！");
						 }
					 }else{
						 eventcondition.nowAlarmchangeState = 0;
					 }	
					 eventcondition.startAlarmTime = 0;
				 }
			}		
		}
		NetDataModel.lst_Pool_Event.put(equiptId, event_lst);
	
	}
	
	//解析设备信号 信号类
	public static void parseEquiptSig(int equiptId)
	{
			//遍历 设备信号数据链表
			if(NetDataModel.hm_Pool_htSignal== null) return;
			int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
			Hashtable<Integer, Signal> signal_lst = NetDataModel.hm_Pool_htSignal.get(equiptId);
			long nowTime = System.currentTimeMillis()/1000;  //以 s为单位
			if(signal_lst == null) return;
			//再遍历 信号链表
			Iterator<Integer> sigId_lst = signal_lst.keySet().iterator();
			while(sigId_lst.hasNext()){
				try{
					int sigId = sigId_lst.next(); 
					Signal sig = signal_lst.get(sigId);
					float value = NetDataModel.hm_Pool_htSigValue.get(equiptId).get(sigId);
					sig.value = String.valueOf(value);
					sig.readtime = nowTime;
					//判断为 数字量 赋值 信号含义
					if(sig.type == 1){
						String strSigId = String.valueOf(sigId);
						HashMap<String, String> meanlst = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId).xml_signalCfg_lst.get(strSigId).SignalMeaninglst;
						if(meanlst == null) continue;
						Iterator<String> meanId_lst = meanlst.keySet().iterator();
						while(meanId_lst.hasNext()){
							String meanStateValue = meanId_lst.next();
							Float fValue = Float.parseFloat(meanStateValue);
							if(value == fValue){
								sig.meaning = meanlst.get(meanStateValue);
						//		Log.e("DataProcess->parseEquiptSig>>mean",String.valueOf(equiptId)+"-"+String.valueOf(sigId)+"-"+sig.meaning);
							}
						}
					 }//if end			
				}catch(Exception e){
					Log.e("DataProcess->parseEquiptSig","异常抛出！");
				}
			} //while()  end
	}	
	
	//判断比较函数
	public static boolean asDeal(float value, float state, String symbol){
		if("=".equals(symbol) && value == state){
			return true;
		}else if("!=".equals(symbol) && value != state){
			return true;
		}else if(">".equals(symbol) && value > state){
			return true;
		}else if("<".equals(symbol) && value < state){
			return true;
		}else if(">=".equals(symbol) && value >= state){
			return true;
		}else if("<=".equals(symbol) && value <= state){
			return true;
		}
		return false;
	}

}
