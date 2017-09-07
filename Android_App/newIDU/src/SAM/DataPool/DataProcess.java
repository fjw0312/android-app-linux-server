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

//���� ʵʱ ����  ��
public class DataProcess {

	public DataProcess() {
		// TODO Auto-generated constructor stub
	}
	

	public static void AllEquipt_initSignal(){
		//�����豸��������
		if(NetDataModel.lst_poolEquipmentId== null) return;
		Iterator<Integer> equiptId_lst = NetDataModel.lst_poolEquipmentId.keySet().iterator();
		while(equiptId_lst.hasNext()){
			int equiptId = equiptId_lst.next(); //��ȡ �豸 id
			int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
			xml_EquiptCfg xmlEquiptCfg = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId);//��ȡ�豸������
			if(xmlEquiptCfg==null || xmlEquiptCfg.xml_signalCfg_lst==null) return;
			Hashtable<Integer, Float> sigValue_lst = new Hashtable<Integer, Float>();
			Hashtable<Integer, Signal> sig_lst = new Hashtable<Integer, Signal>();
			//�����ź���������
			Iterator<String> sigId_lst = xmlEquiptCfg.xml_signalCfg_lst.keySet().iterator();
			while(sigId_lst.hasNext()){
				String sigId = sigId_lst.next();
				int id = Integer.parseInt(sigId);
				sigValue_lst.put(id, (float)0.00);
				
				try{ //��ʼ��  �ź���
					xml_signalCfg sigCfg = xmlEquiptCfg.xml_signalCfg_lst.get(sigId);				
					Signal sig = new Signal();
					sig.equiptId = equiptId;
					sig.sigId = id;
					sig.name = sigCfg.SignalName;
					sig.unit = sigCfg.Unit;
					sig.type = Integer.parseInt(sigCfg.SignalType);
					sig_lst.put(sig.sigId, sig);
					}catch(Exception e){
						Log.e("DataProcess->AllEquipt_initSignal","�쳣�׳���");
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
	

	//�ж� �澯����  ����
	public static void parseEquiptData(float[] rvData){
		if(rvData == null) return;
		int equiptId = (int)rvData[0];		 //��ȡ�豸  id
		Log.i("DataProcess>>parseEquiptData>>>�豸id��", String.valueOf(equiptId));
		if(equiptId == 0) return;
		int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
		xml_EquiptCfg xmlEquiptCfg = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId);//��ȡ�豸ģ��
		//���� �豸�ź�ͨ��
		if(xmlEquiptCfg==null || xmlEquiptCfg.xml_signalCfg_lst == null) return;
		Iterator<String> sigCfgId_lst = xmlEquiptCfg.xml_signalCfg_lst.keySet().iterator();
		while(sigCfgId_lst.hasNext()){
			String Id = sigCfgId_lst.next();    //��ȡ�ź�id
			int id = Integer.parseInt(Id); 
			String No = xmlEquiptCfg.xml_signalCfg_lst.get(Id).ChannelNo; //��ȡ�ź�ͨ��
			int no = Integer.parseInt(No);  
			float value = 0;
			if(no == -1){
				value = rvData[1];
			}else if(no<0){ //����ͨ��  ��ʱ������ Ϊ 888
				value = 888;
			}else{
				value = rvData[no+2];   //��ȡͨ���źŵ���ֵ
			}		
			NetDataModel.hm_Pool_htSigValue.get(equiptId).put(id,value); //�������� �ź�ֵ����
		}
	}
	//�����澯��Ϣ
	public static void parseEquiptEvent(float[] rvData){
		if(rvData == null) return;
		int equiptId = (int)rvData[0];		 //��ȡ�豸  id   
		if(equiptId == 0) return;
		int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
		xml_EquiptCfg xmlEquiptCfg = NetDataModel.hm_xmlEquiptCfgModel.get(EquipTemplateId);//��ȡ�豸ģ��
		//ʵ����  ���豸�ĸ澯����
		List<Event> event_lst = null;		
		if(NetDataModel.lst_Pool_Event.containsKey(equiptId)){
			event_lst = NetDataModel.lst_Pool_Event.get(equiptId);
			event_lst.clear();
		}else{
			event_lst = new ArrayList<Event>();
		}

		//�����澯����
		if(xmlEquiptCfg==null || xmlEquiptCfg.xml_eventCfg_lst == null) return;
		Iterator<String> eventCfgId_lst = xmlEquiptCfg.xml_eventCfg_lst.keySet().iterator();
		while(eventCfgId_lst.hasNext()){
			String EventId = eventCfgId_lst.next();  //�澯����  �澯id
			int eventId = Integer.parseInt(EventId);
			xml_eventCfg xmlEventCfg= xmlEquiptCfg.xml_eventCfg_lst.get(EventId); 
			if("false".equals(xmlEventCfg.Enable)) continue;  //�澯������
			String eventExpression = xmlEventCfg.StartExpression;
			String str[] = eventExpression.split(",|\\]");
			if(str.length<2) continue;
			String strSigid = str[1];
			int sigId = Integer.parseInt(strSigid);
			float value = NetDataModel.hm_Pool_htSigValue.get(equiptId).get(sigId);
			//�����澯����  �Ա���ֵ�Ƿ�澯
			Iterator<String> eventConditionlst = xmlEventCfg.EventConditionlst.keySet().iterator();
			while(eventConditionlst.hasNext()){
				 String strId = eventConditionlst.next();
				 EventCondition eventcondition = xmlEventCfg.EventConditionlst.get(strId);
				 String state = eventcondition.StartCompareValue;
				 String symbol = eventcondition.StartOperation;				
				 float fState = Float.parseFloat(state); 
				 if( asDeal(value,fState,symbol) ){  //�����澯
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
					 if(eventcondition.startAlarmTime == 0){ //�ո�  ��ʼ�澯						 
						 eventcondition.startAlarmTime = System.currentTimeMillis()/1000;  //�� sΪ��λ; 
						 eventcondition.nowAlarmchangeState = 1;
					 }else{  //�澯�Ѿ�����
						 event.starttime =  eventcondition.startAlarmTime;
						 eventcondition.nowAlarmchangeState = 2;
					 }
					 event.starttime = eventcondition.startAlarmTime;
					 //***Ŀǰ���ڿ�ʼʱ�䱣�治סbug 
					 //�ж��Ƿ�Ϊ�¸澯   �����ϸ澯 �Ƿ����¸澯�ĸ澯id						
					 event_lst.add(event); 
					 Log.i("DataProcess->parseEquiptEvent>>�豸��"+String.valueOf(equiptId),
							 "�澯id:"+EventId+"  �澯ֵ��"+state+
					         "++++�ź�id:"+strSigid+" �ź�ֵ��"+String.valueOf(value));
				 }else{	 //�޸澯״̬
					 
					 if(eventcondition.nowAlarmchangeState>0){ //��ʾ�澯�ո���ʧ
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
							 Log.e("DataProcess->parseEquiptEvent","��ʷ�澯�߳̽���   �쳣�׳���");
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
	
	//�����豸�ź� �ź���
	public static void parseEquiptSig(int equiptId)
	{
			//���� �豸�ź���������
			if(NetDataModel.hm_Pool_htSignal== null) return;
			int EquipTemplateId = NetDataModel.lst_poolEquipmentId.get(equiptId);
			Hashtable<Integer, Signal> signal_lst = NetDataModel.hm_Pool_htSignal.get(equiptId);
			long nowTime = System.currentTimeMillis()/1000;  //�� sΪ��λ
			if(signal_lst == null) return;
			//�ٱ��� �ź�����
			Iterator<Integer> sigId_lst = signal_lst.keySet().iterator();
			while(sigId_lst.hasNext()){
				try{
					int sigId = sigId_lst.next(); 
					Signal sig = signal_lst.get(sigId);
					float value = NetDataModel.hm_Pool_htSigValue.get(equiptId).get(sigId);
					sig.value = String.valueOf(value);
					sig.readtime = nowTime;
					//�ж�Ϊ ������ ��ֵ �źź���
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
					Log.e("DataProcess->parseEquiptSig","�쳣�׳���");
				}
			} //while()  end
	}	
	
	//�жϱȽϺ���
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
