package data.pool;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import android.util.Log;
import data.extraHisModel.HisDataSave;
import data.net_model.Net_active_event;
import data.net_model.Net_cfg_control;
import data.net_model.Net_cfg_ctrl_parameaning;
import data.net_model.Net_cfg_event;
import data.net_model.Net_cfg_signal;
import data.net_model.Net_cfg_trigger_value;
import data.net_model.Net_control;
import data.net_model.Net_data_signal;
import data.net_model.Net_equipment;
import data.net_service.DataHAL;
import data.net_service.NetHAL;
import data.pool_model.Equipment;
import data.pool_model.Event;
import data.pool_model.SCmd;
import data.pool_model.Signal;
import data.pool_model.Tigger;
import data.pool_model.equipment_cell.EventCfg;
import data.pool_model.equipment_cell.SCmdCfg;
import data.pool_model.equipment_cell.SignalCfg;
import data.pool_model.equipment_cell.TiggerCfg;
import mail.EMailEventParseThread;

/**DataPool 的数据更新线程*/
public class DataPoolThreadRun extends Thread{
	public DataPoolThreadRun() {
		// TODO Auto-generated constructor stub
		DataPoolModel dataPool = new DataPoolModel(); //初始化数据池的 内存
	}
	//睡眠 延时函数
	void sleep_ms(int ms){
		try{ //一定延时
		sleep(ms);
		}catch(Exception e){		
		}
	}
	//在新建个子线程 用于处理  设置报警  发送控制命令
	Thread myThread = new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				sleep_ms(500);
				set_lstTigger();  //设置 告警值
					
				sleep_ms(500);
				set_lstSCmd();    //发送 控制命令
			}
		}
	});

	public void test_signal(){
//		Log.i("DataPoolThread>test_signal>>","into");
		for(int i=0;i<DataPoolModel.lst_poolEquipmentId.size();i++){
			int equipId = DataPoolModel.lst_poolEquipmentId.get(i); 
			
			update_htSignal(equipId);  // update signal  
			Hashtable<Integer, Signal> sig_ht = DataPoolModel.hm_Pool_htSignal.get(equipId);
			Iterator<Integer> sigId_lst = sig_ht.keySet().iterator();
//			Log.e("DataPoolThread>test_signal>>",String.valueOf(equipId) );
			while(sigId_lst.hasNext()){
				int s_id = sigId_lst.next();
				Signal signal = sig_ht.get(s_id);
				
//				Log.i("DataPoolThreadRun>run>>equip-"+String.valueOf(equipId)+"---信号数据 id:"+String.valueOf(s_id),
//							"name:"+signal.name+
//							" value:"+signal.value+
//							" valueType:"+signal.type+ 
//							" mean:"+signal.meaning+
//							" time:"+signal.readtime+
//							" severity:"+signal.severity
//					//		" invalid:"+signal.invalid+ 
//					//		"  uint:"+signal.unit
//							);	
				
		
			}
		}
	}
	public void test_event(){
//		Log.i("DataPoolThread>test_event>>","while(true)");
		update_lstEvent();
//		for(int i=0; i<DataPoolModel.lst_Pool_Event.size(); i++){
//			Event event = DataPoolModel.lst_Pool_Event.get(1);
//			Log.w("DataPoolThreadRun>test_event>>equip-"+String.valueOf(event.equipId)+
//					"---设备名称:"+String.valueOf(event.equipName),
//					"---告警id:"+String.valueOf(event.eventId)+
//					"name:"+event.name+
//					" mean:"+event.meaning+
//					" grad:"+event.grade+ 
//					" readtime:"+event.starttime+
//					" stoptime:"+event.stoptime+
//					" is_active:"+event.is_active);
//		}
	}
	
	@Override /**线程 数据池  数据更新*/
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		//初始化  刷新 配置设备  数据
		int times = 0;
		int num = 0;
		while(times<10){ //最多获取10次 
			Log.i("DataPoolThreadRun>run>>","into!");
			times++;
			num = init_DataPool();  //初始化  数据池 数据配置			
			sleep_ms(500); //sleep 延时 500ms			
			Log.e("DataPoolThreadRun>run>>","end!  "+"设备数量："+String.valueOf(num));
			if(num!=0) break;
		}
		System.gc();
		//初始化历史数据的日期
		HisDataSave.init_SaveDataTime();   //历史数据方面
		//启动 发送控制命令  设置报警值线程
		myThread.start();
		//启动 邮件告警功能
		EMailEventParseThread mailThread = new EMailEventParseThread();
		mailThread.start();
		//不断循环更新 信号数据  实时告警  
		while(true){
			//遍历更新   设备信号
			for(int i=0;i<DataPoolModel.lst_poolEquipmentId.size();i++){
				int equipId = DataPoolModel.lst_poolEquipmentId.get(i); 				
				update_htSignal(equipId);  // update signal
			}
			//更新  告警信息
			update_lstEvent();

			sleep_ms(800); //sleep 延时 800ms	
			//保存历史数据模型
			HisDataSave.save_HisData();     //历史数据方面
			sleep_ms(800); //sleep 延时 800ms
//			Log.e("DataPoolThreadRun>run>>while","flag!");
		}

	}
	
	
	//================================初始化 配置设备类链表部分====================================
	/**数据池 初始化   
	 * 主要初始化  设备名链表
	 * 主要初始化  设备名链表
	 * 主要初始化  设备xml文件路径名链表
	 * 主要初始化 设备配置类链表hm_poolEquipment  */
	public int init_DataPool(){  //返回 设备个数
		//get ipc设备类 链表
		List<Net_equipment> lst_IpcEquip = DataHAL.get_equipment_list(NetHAL.IP, NetHAL.Port);
		if(lst_IpcEquip==null) return 0;
		if(lst_IpcEquip.size()==0) return 0;
		for(int i=0; i<lst_IpcEquip.size(); i++){
			Equipment equipment = new Equipment();
			Net_equipment ipcEquip = lst_IpcEquip.get(i); //获取ipc_equipment
			equipment.equipId = ipcEquip.id;
			equipment.equipTempId = ipcEquip.templateId;			
			equipment.equipName = ipcEquip.name;
			equipment.equipXmlfile = ipcEquip.xmlname;
			
			DataPoolModel.hm_poolEquipment.put(equipment.equipId, equipment);
			DataPoolModel.hm_poolEquipmentName.put(equipment.equipId, equipment.equipName);
			DataPoolModel.hm_poolEquipmentXmlfile.put(equipment.equipId, equipment.equipXmlfile);
			if(DataPoolModel.lst_poolEquipmentId.contains(equipment.equipId)==false)
				DataPoolModel.lst_poolEquipmentId.add(equipment.equipId);
			
			//初始化 hm_sysEquipment链表上的设备类
			init_AllEquipment();
		}
		return DataPoolModel.lst_poolEquipmentId.size();
	}
	/**初始化 配置设备类链表*/
	public void init_AllEquipment(){
		if(DataPoolModel.hm_poolEquipment == null) return;
		//遍历设备类链表
		Iterator<Integer> id_lst = DataPoolModel.hm_poolEquipment.keySet().iterator();
		while(id_lst.hasNext()){
			int equipId = id_lst.next();
			Equipment equipment = DataPoolModel.hm_poolEquipment.get(equipId);
			//获取 设备类-信号配置链表 
			equipment.htSignalCfg = init_EquipmentHtSignalCfg(equipId,equipment);
			//获取 设备类-告警配置链表
			equipment.htEventCfg = init_EquipmentHtEventCfg(equipId,equipment);
			//获取 设备类-报警包配置链表
			equipment.htTiggerCfg = init_EquipmentHtTiggertCfg(equipId,equipment);
			//获取 设备类-控制配置链表
			equipment.htCmdCfg = init_EquipmentHtSCmdCfg(equipId,equipment);
		}
	}
	/**初始化 设备信号配置链表    顺便初始化信号类链表的部分信息*/
	public Hashtable<Integer, SignalCfg> init_EquipmentHtSignalCfg(int equipId, Equipment equipment){
		Hashtable<Integer, SignalCfg> htSignalCfg = new Hashtable<Integer, SignalCfg>();
		Hashtable<Integer, Signal> htSignal = new Hashtable<Integer, Signal>();
		//请求 List<ipc_cfg_signal>
		List<Net_cfg_signal> lst_ipcSignalCfg = DataHAL.get_signal_cfg_list(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcSignalCfg == null) return null;
		for(int i=0; i<lst_ipcSignalCfg.size();i++){
			Net_cfg_signal ipcSignalCfg = lst_ipcSignalCfg.get(i);
			SignalCfg signalCfg = new SignalCfg();
			signalCfg.sId = ipcSignalCfg.id;
			signalCfg.sName = ipcSignalCfg.name;
			signalCfg.sUnit = ipcSignalCfg.unit;
			signalCfg.sPrecision = ipcSignalCfg.precision;
			signalCfg.sDescription = ipcSignalCfg.description;
			
			htSignalCfg.put(signalCfg.sId, signalCfg);  //将 SignalCfg 添加到链表
			
			Signal signal = new Signal();
			signal.sigId = ipcSignalCfg.id;
			signal.name = ipcSignalCfg.name;
			signal.unit = ipcSignalCfg.unit;
			signal.precision = ipcSignalCfg.precision;
			signal.description = ipcSignalCfg.description;
			htSignal.put(signal.sigId, signal);				
		}
		DataPoolModel.hm_Pool_htSignal.put(equipId, htSignal); //将该设备的信号链表添加到 <设备id,信号连>
		
		return htSignalCfg;
	}
	/**初始化 设备告警配置链表*/
	public Hashtable<Integer, EventCfg> init_EquipmentHtEventCfg(int equipId, Equipment equipment){
		Hashtable<Integer, EventCfg> htEventCfg = new Hashtable<Integer, EventCfg>();
		//请求 List<ipc_cfg_event>
		List<Net_cfg_event> lst_ipcEventCfg = DataHAL.get_event_cfg_list(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcEventCfg==null) return null;
		for(int i=0; i<lst_ipcEventCfg.size();i++){
			Net_cfg_event ipcEventCfg = lst_ipcEventCfg.get(i);
			EventCfg eventCfg = new EventCfg();
			eventCfg.efEventCfgId = ipcEventCfg.id;
			eventCfg.efName = ipcEventCfg.name;
			eventCfg.efGrade = ipcEventCfg.grade;
			
			htEventCfg.put(eventCfg.efEventCfgId, eventCfg);
		}
		return htEventCfg;
	}
	/**初始化 设备告警配置链表*/
	public Hashtable<Integer, TiggerCfg> init_EquipmentHtTiggertCfg(int equipId, Equipment equipment){
		Hashtable<Integer, TiggerCfg> htTiggerCfg = new Hashtable<Integer, TiggerCfg>();
		//请求 List<ipc_cfg_event>
		List<Net_cfg_event> lst_ipcEventCfg = DataHAL.get_event_cfg_list(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcEventCfg==null) return null;
		for(int i=0; i<lst_ipcEventCfg.size();i++){
			Net_cfg_event ipcEventCfg = lst_ipcEventCfg.get(i);
			TiggerCfg tiggerCfg = new TiggerCfg();
			tiggerCfg.tTiggerId = ipcEventCfg.id;
			tiggerCfg.tName = ipcEventCfg.name;
			
			htTiggerCfg.put(tiggerCfg.tTiggerId, tiggerCfg);
		}
		//请求List<ipc_cfg_trigger_value>  配置对应的condition
		List<Net_cfg_trigger_value> lst_ipcTiggerCfg = DataHAL.get_cfg_trigger_value(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcTiggerCfg==null) return htTiggerCfg;
		for(int i=0; i<lst_ipcTiggerCfg.size();i++){
			Net_cfg_trigger_value ipcTiggerCfg = lst_ipcTiggerCfg.get(i);
			TiggerCfg tiggerCfg = htTiggerCfg.get(ipcTiggerCfg.eventid);
			tiggerCfg.tTiggerEnabled = ipcTiggerCfg.enabled;
			TiggerCfg.TiggerConditionCfg condition = tiggerCfg.new TiggerConditionCfg();
			condition.conditionid = ipcTiggerCfg.conditionid;
			condition.startcompare = ipcTiggerCfg.startvalue;
			condition.endcompare = ipcTiggerCfg.stopvalue;
			condition.severity = ipcTiggerCfg.eventseverity;
			condition.mark = ipcTiggerCfg.mark;
			tiggerCfg.tTiggerConditions_ht.put(condition.conditionid, condition);			
		}
		return htTiggerCfg;
	}
	/**初始化 设备控制配置链表*/
	public Hashtable<Integer, SCmdCfg> init_EquipmentHtSCmdCfg(int equipId, Equipment equipment){
		Hashtable<Integer, SCmdCfg> htSCmdCfg = new Hashtable<Integer, SCmdCfg>();
		//请求 List<ipc_cfg_control>
		List<Net_cfg_control> lst_ipcSCmdCfg = DataHAL.get_control_cfg_list(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcSCmdCfg==null) return null;
		for(int i=0; i<lst_ipcSCmdCfg.size();i++){
			Net_cfg_control ipcSCmdCfg = lst_ipcSCmdCfg.get(i);
			SCmdCfg scmdCfg = new SCmdCfg();
			scmdCfg.cId = ipcSCmdCfg.id;
			scmdCfg.cName = ipcSCmdCfg.name;
			scmdCfg.cUnit = ipcSCmdCfg.unit;
			scmdCfg.cMaxValue = ipcSCmdCfg.fMaxValue;
			scmdCfg.cMinValue = ipcSCmdCfg.fMinValue;
			
			htSCmdCfg.put(scmdCfg.cId, scmdCfg);
		}
		//请求List<ipc_cfg_ctrl_parameaning>  配置对应的parameaning
		List<Net_cfg_ctrl_parameaning> lst_ipcSCmdPara = DataHAL.get_control_parameaning_cfg_list(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcSCmdPara==null) return htSCmdCfg;
		for(int i=0; i<lst_ipcSCmdPara.size();i++){
			Net_cfg_ctrl_parameaning ipcSCmdPara = lst_ipcSCmdPara.get(i);
			SCmdCfg scmdCfg = htSCmdCfg.get(ipcSCmdPara.ctrlid);
			SCmdCfg.CmdParameaningCfg lst_para = scmdCfg.new CmdParameaningCfg();
			lst_para.id = ipcSCmdPara.parameterid;  
			lst_para.value = ipcSCmdPara.paramvalue;
			lst_para.meaning = ipcSCmdPara.parameaning;	
			if(scmdCfg.cPara.contains(lst_para)==false)
					scmdCfg.cPara.add(lst_para);
		}
		return htSCmdCfg;
	}
	
	//=============================end====初始化 配置设备类链表部分===end=================================
	/**更新 设备 信号链表*/
	public void update_htSignal(int equipId){
		//请求List<ipc_data_signal>
		List<Net_data_signal> ipcSignal_lst = DataHAL.get_signal_data_list(NetHAL.IP, NetHAL.Port, equipId);
		if(ipcSignal_lst==null) return;
		for(int i=0;i<ipcSignal_lst.size();i++){
			Net_data_signal ipcSignal = ipcSignal_lst.get(i);
			Signal signal = null;
			//判断是否有该条信号存在
			if(DataPoolModel.hm_Pool_htSignal.get(equipId).containsKey(ipcSignal.sigid)){
				signal = DataPoolModel.hm_Pool_htSignal.get(equipId).get(ipcSignal.sigid);			
			}else{
				signal = new Signal();
			}
			signal.equiptId = equipId;
			signal.sigId = ipcSignal.sigid;
			signal.value = ipcSignal.value;
			signal.type = ipcSignal.value_type;//发现一直为0 考虑是否通过判断mean=null 赋值 0/1
			signal.meaning = ipcSignal.meaning;
			signal.readtime = ipcSignal.freshtime;
			signal.invalid = ipcSignal.is_invalid; //通信中断时 除通信状态其他信号invalid=0 正常都为 1
			signal.severity = ipcSignal.severity;
			if(signal.invalid == 0) signal.value = "0.0";  //处理通信中断 数据清零
//			Log.w("DataPoolThreadRun>update_htSignal>>","type="+ipcSignal.value_type
//					+" mean="+ipcSignal.meaning);
			DataPoolModel.hm_Pool_htSignal.get(equipId).put(signal.sigId, signal);
		}	
	}
	/**更新 模型数据池告警链表*/
	public void update_lstEvent(){
		//请求 List<ipc_active_event>
		List<Net_active_event> ipcEvent_lst = DataHAL.get_all_active_alarm_list(NetHAL.IP, NetHAL.Port);
		
		//线程同步  保护DataPoolModel.lst_Pool_Event内存
		synchronized (DataPoolModel.lst_Pool_Event) {
			DataPoolModel.lst_Pool_Event.clear(); //清除老告警信息链表 
		}
		
		if(ipcEvent_lst==null) return;
		for(int i=0;i<ipcEvent_lst.size();i++){
			Net_active_event ipcEvent = ipcEvent_lst.get(i);
			Event event = new Event();
			event.equipId = ipcEvent.equipid;
			event.eventId = ipcEvent.eventid;
			event.meaning = ipcEvent.meaning;
			event.grade = ipcEvent.grade;
			event.starttime = ipcEvent.starttime;
			event.stoptime = ipcEvent.endtime;
			event.is_active = ipcEvent.is_active;
			event.equipName = DataPoolModel.hm_poolEquipmentName.get(event.equipId);
			event.name = 
			DataPoolModel.hm_poolEquipment.get(event.equipId).htEventCfg.get(event.eventId).efName;
			//线程同步  保护DataPoolModel.lst_Pool_Event内存
			synchronized (DataPoolModel.lst_Pool_Event) {
				DataPoolModel.lst_Pool_Event.add(event);
			}
		}
	}
	
	/**发送 模型数据池报警包链表*/
	public void set_lstTigger(){
		List<Net_cfg_trigger_value>  ipcTigger_lst= new ArrayList<Net_cfg_trigger_value>();
		synchronized (DataPoolModel.lst_Pool_Tigger) { //线程同步  保护DataPoolModel.lst_Pool_Tigger内存
			if(DataPoolModel.lst_Pool_Tigger==null) return;
			if(DataPoolModel.lst_Pool_Tigger.size()==0) return;			
			for(int i=0; i<DataPoolModel.lst_Pool_Tigger.size();i++){
				Tigger tigger = DataPoolModel.lst_Pool_Tigger.get(i);
				Net_cfg_trigger_value ipcTigger = new Net_cfg_trigger_value();
				ipcTigger.equipid = tigger.equipId;
				ipcTigger.eventid = tigger.tiggerId;
				ipcTigger.enabled = tigger.enabled;
				ipcTigger.conditionid = tigger.conditionid;
				ipcTigger.startvalue = tigger.startvalue;
				ipcTigger.stopvalue = tigger.stopvalue;
				ipcTigger.eventseverity = tigger.eventseverity;
				ipcTigger.mark = tigger.mark;
				ipcTigger_lst.add(ipcTigger);				
			}		
		}
		
		if(0 == DataHAL.set_cfg_trigger_value(NetHAL.IP, NetHAL.Port,ipcTigger_lst) ){
			Log.e("DataPoolThreadRun>set_lstTigger>>","设置告警值成功");
			//处理 告警阀值  更新
	//		DataPoolModel.hm_poolEquipment.get(equipId).htTiggerCfg.
	//		get(tiggerId).tTiggerConditions_ht.get(conditionId).startcompare = value;
		}else{
			Log.e("DataPoolThreadRun>set_lstTigger>>","设置告警值失败");
		}
		DataPoolModel.lst_Pool_Tigger.clear();
		ipcTigger_lst.clear();
		ipcTigger_lst = null;
		
	}
	/**发送 模型数据池控制类链表*/
	public void set_lstSCmd(){
		List<Net_control> ipcSCmd_lst = new  ArrayList<Net_control>();
		synchronized (DataPoolModel.lst_Pool_SCmd) {//线程同步  保护DataPoolModel.lst_Pool_SCmd内存
			if(DataPoolModel.lst_Pool_SCmd==null) return;
			if(DataPoolModel.lst_Pool_SCmd.size()==0) return;
			for(int i=0; i<DataPoolModel.lst_Pool_SCmd.size();i++){
				SCmd scmd = DataPoolModel.lst_Pool_SCmd.get(i);
				Net_control ipcSCmd = new Net_control();
				ipcSCmd.equipid = scmd.equipId;
				ipcSCmd.ctrlid = scmd.cmdId;
				ipcSCmd.valuetype = scmd.valueType;
				ipcSCmd.value = scmd.value;
				ipcSCmd_lst.add(ipcSCmd);
			}			
		}
		
		if(0 == DataHAL.send_control_cmd(NetHAL.IP, NetHAL.Port,ipcSCmd_lst) ){			
			Log.e("DataPoolThreadRun>set_lstSCmd>>","发送控制成功");
		}else{
			Log.e("DataPoolThreadRun>set_lstSCmd>>","设置控制失败");
		}
		DataPoolModel.lst_Pool_SCmd.clear();
		ipcSCmd_lst.clear();
		ipcSCmd_lst = null;
		
	}
	
}
