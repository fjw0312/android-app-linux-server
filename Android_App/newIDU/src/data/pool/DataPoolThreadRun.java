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

/**DataPool �����ݸ����߳�*/
public class DataPoolThreadRun extends Thread{
	public DataPoolThreadRun() {
		// TODO Auto-generated constructor stub
		DataPoolModel dataPool = new DataPoolModel(); //��ʼ�����ݳص� �ڴ�
	}
	//˯�� ��ʱ����
	void sleep_ms(int ms){
		try{ //һ����ʱ
		sleep(ms);
		}catch(Exception e){		
		}
	}
	//���½������߳� ���ڴ���  ���ñ���  ���Ϳ�������
	Thread myThread = new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				sleep_ms(500);
				set_lstTigger();  //���� �澯ֵ
					
				sleep_ms(500);
				set_lstSCmd();    //���� ��������
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
				
//				Log.i("DataPoolThreadRun>run>>equip-"+String.valueOf(equipId)+"---�ź����� id:"+String.valueOf(s_id),
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
//					"---�豸����:"+String.valueOf(event.equipName),
//					"---�澯id:"+String.valueOf(event.eventId)+
//					"name:"+event.name+
//					" mean:"+event.meaning+
//					" grad:"+event.grade+ 
//					" readtime:"+event.starttime+
//					" stoptime:"+event.stoptime+
//					" is_active:"+event.is_active);
//		}
	}
	
	@Override /**�߳� ���ݳ�  ���ݸ���*/
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		//��ʼ��  ˢ�� �����豸  ����
		int times = 0;
		int num = 0;
		while(times<10){ //����ȡ10�� 
			Log.i("DataPoolThreadRun>run>>","into!");
			times++;
			num = init_DataPool();  //��ʼ��  ���ݳ� ��������			
			sleep_ms(500); //sleep ��ʱ 500ms			
			Log.e("DataPoolThreadRun>run>>","end!  "+"�豸������"+String.valueOf(num));
			if(num!=0) break;
		}
		System.gc();
		//��ʼ����ʷ���ݵ�����
		HisDataSave.init_SaveDataTime();   //��ʷ���ݷ���
		//���� ���Ϳ�������  ���ñ���ֵ�߳�
		myThread.start();
		//���� �ʼ��澯����
		EMailEventParseThread mailThread = new EMailEventParseThread();
		mailThread.start();
		//����ѭ������ �ź�����  ʵʱ�澯  
		while(true){
			//��������   �豸�ź�
			for(int i=0;i<DataPoolModel.lst_poolEquipmentId.size();i++){
				int equipId = DataPoolModel.lst_poolEquipmentId.get(i); 				
				update_htSignal(equipId);  // update signal
			}
			//����  �澯��Ϣ
			update_lstEvent();

			sleep_ms(800); //sleep ��ʱ 800ms	
			//������ʷ����ģ��
			HisDataSave.save_HisData();     //��ʷ���ݷ���
			sleep_ms(800); //sleep ��ʱ 800ms
//			Log.e("DataPoolThreadRun>run>>while","flag!");
		}

	}
	
	
	//================================��ʼ�� �����豸��������====================================
	/**���ݳ� ��ʼ��   
	 * ��Ҫ��ʼ��  �豸������
	 * ��Ҫ��ʼ��  �豸������
	 * ��Ҫ��ʼ��  �豸xml�ļ�·��������
	 * ��Ҫ��ʼ�� �豸����������hm_poolEquipment  */
	public int init_DataPool(){  //���� �豸����
		//get ipc�豸�� ����
		List<Net_equipment> lst_IpcEquip = DataHAL.get_equipment_list(NetHAL.IP, NetHAL.Port);
		if(lst_IpcEquip==null) return 0;
		if(lst_IpcEquip.size()==0) return 0;
		for(int i=0; i<lst_IpcEquip.size(); i++){
			Equipment equipment = new Equipment();
			Net_equipment ipcEquip = lst_IpcEquip.get(i); //��ȡipc_equipment
			equipment.equipId = ipcEquip.id;
			equipment.equipTempId = ipcEquip.templateId;			
			equipment.equipName = ipcEquip.name;
			equipment.equipXmlfile = ipcEquip.xmlname;
			
			DataPoolModel.hm_poolEquipment.put(equipment.equipId, equipment);
			DataPoolModel.hm_poolEquipmentName.put(equipment.equipId, equipment.equipName);
			DataPoolModel.hm_poolEquipmentXmlfile.put(equipment.equipId, equipment.equipXmlfile);
			if(DataPoolModel.lst_poolEquipmentId.contains(equipment.equipId)==false)
				DataPoolModel.lst_poolEquipmentId.add(equipment.equipId);
			
			//��ʼ�� hm_sysEquipment�����ϵ��豸��
			init_AllEquipment();
		}
		return DataPoolModel.lst_poolEquipmentId.size();
	}
	/**��ʼ�� �����豸������*/
	public void init_AllEquipment(){
		if(DataPoolModel.hm_poolEquipment == null) return;
		//�����豸������
		Iterator<Integer> id_lst = DataPoolModel.hm_poolEquipment.keySet().iterator();
		while(id_lst.hasNext()){
			int equipId = id_lst.next();
			Equipment equipment = DataPoolModel.hm_poolEquipment.get(equipId);
			//��ȡ �豸��-�ź��������� 
			equipment.htSignalCfg = init_EquipmentHtSignalCfg(equipId,equipment);
			//��ȡ �豸��-�澯��������
			equipment.htEventCfg = init_EquipmentHtEventCfg(equipId,equipment);
			//��ȡ �豸��-��������������
			equipment.htTiggerCfg = init_EquipmentHtTiggertCfg(equipId,equipment);
			//��ȡ �豸��-������������
			equipment.htCmdCfg = init_EquipmentHtSCmdCfg(equipId,equipment);
		}
	}
	/**��ʼ�� �豸�ź���������    ˳���ʼ���ź�������Ĳ�����Ϣ*/
	public Hashtable<Integer, SignalCfg> init_EquipmentHtSignalCfg(int equipId, Equipment equipment){
		Hashtable<Integer, SignalCfg> htSignalCfg = new Hashtable<Integer, SignalCfg>();
		Hashtable<Integer, Signal> htSignal = new Hashtable<Integer, Signal>();
		//���� List<ipc_cfg_signal>
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
			
			htSignalCfg.put(signalCfg.sId, signalCfg);  //�� SignalCfg ��ӵ�����
			
			Signal signal = new Signal();
			signal.sigId = ipcSignalCfg.id;
			signal.name = ipcSignalCfg.name;
			signal.unit = ipcSignalCfg.unit;
			signal.precision = ipcSignalCfg.precision;
			signal.description = ipcSignalCfg.description;
			htSignal.put(signal.sigId, signal);				
		}
		DataPoolModel.hm_Pool_htSignal.put(equipId, htSignal); //�����豸���ź�������ӵ� <�豸id,�ź���>
		
		return htSignalCfg;
	}
	/**��ʼ�� �豸�澯��������*/
	public Hashtable<Integer, EventCfg> init_EquipmentHtEventCfg(int equipId, Equipment equipment){
		Hashtable<Integer, EventCfg> htEventCfg = new Hashtable<Integer, EventCfg>();
		//���� List<ipc_cfg_event>
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
	/**��ʼ�� �豸�澯��������*/
	public Hashtable<Integer, TiggerCfg> init_EquipmentHtTiggertCfg(int equipId, Equipment equipment){
		Hashtable<Integer, TiggerCfg> htTiggerCfg = new Hashtable<Integer, TiggerCfg>();
		//���� List<ipc_cfg_event>
		List<Net_cfg_event> lst_ipcEventCfg = DataHAL.get_event_cfg_list(NetHAL.IP, NetHAL.Port, equipId);
		if(lst_ipcEventCfg==null) return null;
		for(int i=0; i<lst_ipcEventCfg.size();i++){
			Net_cfg_event ipcEventCfg = lst_ipcEventCfg.get(i);
			TiggerCfg tiggerCfg = new TiggerCfg();
			tiggerCfg.tTiggerId = ipcEventCfg.id;
			tiggerCfg.tName = ipcEventCfg.name;
			
			htTiggerCfg.put(tiggerCfg.tTiggerId, tiggerCfg);
		}
		//����List<ipc_cfg_trigger_value>  ���ö�Ӧ��condition
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
	/**��ʼ�� �豸������������*/
	public Hashtable<Integer, SCmdCfg> init_EquipmentHtSCmdCfg(int equipId, Equipment equipment){
		Hashtable<Integer, SCmdCfg> htSCmdCfg = new Hashtable<Integer, SCmdCfg>();
		//���� List<ipc_cfg_control>
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
		//����List<ipc_cfg_ctrl_parameaning>  ���ö�Ӧ��parameaning
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
	
	//=============================end====��ʼ�� �����豸��������===end=================================
	/**���� �豸 �ź�����*/
	public void update_htSignal(int equipId){
		//����List<ipc_data_signal>
		List<Net_data_signal> ipcSignal_lst = DataHAL.get_signal_data_list(NetHAL.IP, NetHAL.Port, equipId);
		if(ipcSignal_lst==null) return;
		for(int i=0;i<ipcSignal_lst.size();i++){
			Net_data_signal ipcSignal = ipcSignal_lst.get(i);
			Signal signal = null;
			//�ж��Ƿ��и����źŴ���
			if(DataPoolModel.hm_Pool_htSignal.get(equipId).containsKey(ipcSignal.sigid)){
				signal = DataPoolModel.hm_Pool_htSignal.get(equipId).get(ipcSignal.sigid);			
			}else{
				signal = new Signal();
			}
			signal.equiptId = equipId;
			signal.sigId = ipcSignal.sigid;
			signal.value = ipcSignal.value;
			signal.type = ipcSignal.value_type;//����һֱΪ0 �����Ƿ�ͨ���ж�mean=null ��ֵ 0/1
			signal.meaning = ipcSignal.meaning;
			signal.readtime = ipcSignal.freshtime;
			signal.invalid = ipcSignal.is_invalid; //ͨ���ж�ʱ ��ͨ��״̬�����ź�invalid=0 ������Ϊ 1
			signal.severity = ipcSignal.severity;
			if(signal.invalid == 0) signal.value = "0.0";  //����ͨ���ж� ��������
//			Log.w("DataPoolThreadRun>update_htSignal>>","type="+ipcSignal.value_type
//					+" mean="+ipcSignal.meaning);
			DataPoolModel.hm_Pool_htSignal.get(equipId).put(signal.sigId, signal);
		}	
	}
	/**���� ģ�����ݳظ澯����*/
	public void update_lstEvent(){
		//���� List<ipc_active_event>
		List<Net_active_event> ipcEvent_lst = DataHAL.get_all_active_alarm_list(NetHAL.IP, NetHAL.Port);
		
		//�߳�ͬ��  ����DataPoolModel.lst_Pool_Event�ڴ�
		synchronized (DataPoolModel.lst_Pool_Event) {
			DataPoolModel.lst_Pool_Event.clear(); //����ϸ澯��Ϣ���� 
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
			//�߳�ͬ��  ����DataPoolModel.lst_Pool_Event�ڴ�
			synchronized (DataPoolModel.lst_Pool_Event) {
				DataPoolModel.lst_Pool_Event.add(event);
			}
		}
	}
	
	/**���� ģ�����ݳر���������*/
	public void set_lstTigger(){
		List<Net_cfg_trigger_value>  ipcTigger_lst= new ArrayList<Net_cfg_trigger_value>();
		synchronized (DataPoolModel.lst_Pool_Tigger) { //�߳�ͬ��  ����DataPoolModel.lst_Pool_Tigger�ڴ�
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
			Log.e("DataPoolThreadRun>set_lstTigger>>","���ø澯ֵ�ɹ�");
			//���� �澯��ֵ  ����
	//		DataPoolModel.hm_poolEquipment.get(equipId).htTiggerCfg.
	//		get(tiggerId).tTiggerConditions_ht.get(conditionId).startcompare = value;
		}else{
			Log.e("DataPoolThreadRun>set_lstTigger>>","���ø澯ֵʧ��");
		}
		DataPoolModel.lst_Pool_Tigger.clear();
		ipcTigger_lst.clear();
		ipcTigger_lst = null;
		
	}
	/**���� ģ�����ݳؿ���������*/
	public void set_lstSCmd(){
		List<Net_control> ipcSCmd_lst = new  ArrayList<Net_control>();
		synchronized (DataPoolModel.lst_Pool_SCmd) {//�߳�ͬ��  ����DataPoolModel.lst_Pool_SCmd�ڴ�
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
			Log.e("DataPoolThreadRun>set_lstSCmd>>","���Ϳ��Ƴɹ�");
		}else{
			Log.e("DataPoolThreadRun>set_lstSCmd>>","���ÿ���ʧ��");
		}
		DataPoolModel.lst_Pool_SCmd.clear();
		ipcSCmd_lst.clear();
		ipcSCmd_lst = null;
		
	}
	
}
