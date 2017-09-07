package data.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import android.annotation.SuppressLint;
import android.util.Log;
import data.pool_model.Equipment;
import data.pool_model.Event;
import data.pool_model.SCmd;
import data.pool_model.Signal;
import data.pool_model.Tigger;
import data.pool_model.equipment_cell.EventCfg;
import data.pool_model.equipment_cell.SCmdCfg;
import data.pool_model.equipment_cell.SignalCfg;
import data.pool_model.equipment_cell.TiggerCfg;

/**���ݳ� ģ��--app �����ݴ洢��*/
//������Ҫ��Ա  �豸������Ϣ������   �ź�������   �澯������   ������������   ����������
@SuppressLint("UseSparseArrays")
public class DataPoolModel {
	
	@SuppressLint("UseSparseArrays")
	public DataPoolModel() {
		hm_poolEquipment = new HashMap<Integer,  Equipment>();
		lst_poolEquipmentId = new ArrayList<Integer>();
		hm_poolEquipmentName = new HashMap<Integer, String>();
		hm_poolEquipmentXmlfile = new HashMap<Integer, String>();
		
		hm_Pool_htSignal = new HashMap<Integer, Hashtable<Integer, Signal>>();
		lst_Pool_Event = new ArrayList<Event>();
		lst_Pool_Tigger = new ArrayList<Tigger>();
		lst_Pool_SCmd = new ArrayList<SCmd>();
	}
	//ϵͳ �豸������
	public static HashMap<Integer,  Equipment> hm_poolEquipment = null; //<�豸id, �豸��>
	
	//ϵͳ �豸id����
	public static List<Integer> lst_poolEquipmentId = null;   //<�豸id>
	//ϵͳ �豸������
	public static HashMap<Integer, String> hm_poolEquipmentName = null; //<�豸id, �豸����>	
	//ϵͳ �豸xml�ļ�·��������
	public static HashMap<Integer, String> hm_poolEquipmentXmlfile = null; //<�豸id, �豸xml�ļ�·����>
	
	
	//ϵͳ �豸�ź��� ����    //<�豸id, �ź�����>   <�豸id, <�ź�id, �ź���>>
	public static HashMap<Integer, Hashtable<Integer, Signal>> hm_Pool_htSignal = null; 
	//ϵͳ ʵʱ�澯 ����    //<�澯������>  ����ʵʱ����   �ʲ���List  ������ 2ά���ݲ��ô��� 
	public static List<Event> lst_Pool_Event = null;
	//ϵͳ  ���������� ����  //<��������>   ʵʱ�������   �ʲ���list  ������ 2ά���ݲ��ô��� 
	public static List<Tigger> lst_Pool_Tigger = null;
	//ϵͳ  ��������  ����     //<������>    ʵʱ�������   �ʲ���list  ������ 2ά���ݲ��ô��� 
	public static List<SCmd> lst_Pool_SCmd = null;
	
	
//	//ϵͳ ʵʱ�澯 ����    //<�豸id, �澯����>   <�豸id, <�澯id, �澯��>>
//	public static HashMap<Integer, Hashtable<Integer, Event>> hm_Pool_htEvent = null;
//	//ϵͳ  �澯��ֵ���� ����    //<�豸id, ����������>   <�豸id, <�澯id, ��������>>
//	public static HashMap<Integer, Hashtable<Integer, Tigger>> hm_Pool_htTigger = null;
//	//ϵͳ  �������� ����    //<�豸id, ��������>   <�豸id, <����id, ����������>>
//	public static HashMap<Integer, Hashtable<Integer, SCmd>> hm_Pool_htSCmd = null;
//	
	
	
	//**********************************�ⲿview���÷����ӿ�**����ӿ�*********************************

	//-------------------------�����豸��equipment �ӿ�----------------------------
	/**��ȡ�豸��*/
	public static  Equipment getEquipment(int equipId){
		return hm_poolEquipment.get(equipId);
	}
	/**��ȡĳ�豸�ź�����*/
	public static  Hashtable<Integer, SignalCfg> getHtSignalCfg(int equipId){
		return hm_poolEquipment.get(equipId).htSignalCfg;
	}
	/**��ȡĳ�豸�澯��������*/
	public static  Hashtable<Integer, EventCfg> getHtEventCfg(int equipId){
		return hm_poolEquipment.get(equipId).htEventCfg;
	}	
	/**��ȡĳ�豸�澯��������*/
	public static  Hashtable<Integer, TiggerCfg> getHtTiggerCfg(int equipId){
		return hm_poolEquipment.get(equipId).htTiggerCfg;
	}
	/**��ȡĳ�豸������������*/
	public static  Hashtable<Integer, SCmdCfg> getHtCmdCfg(int equipId){
		return hm_poolEquipment.get(equipId).htCmdCfg;
	}
	//--------��ȡ�����豸��������µ� ������
	/**��ȡĳ�豸��ĳ���ź���*/
	public static  SignalCfg getSignalCfg(int equipId, int signalId){
		return getHtSignalCfg(equipId).get(signalId);
	}
	/**��ȡĳ�豸��ĳ���澯������*/
	public static  EventCfg getEventCfg(int equipId, int efEventCfgId){
		return getHtEventCfg(equipId).get(efEventCfgId);
	}
	/**��ȡĳ�豸��ĳ���澯���ð���*/
	public static  TiggerCfg getTiggerCfg(int equipId, int tTiggerId){
		return getHtTiggerCfg(equipId).get(tTiggerId);
	}
	/**��ȡĳ�豸��ĳ������������*/
	public static  SCmdCfg getSCmdCfg(int equipId, int cId){
		return getHtCmdCfg(equipId).get(cId);
	}
	//------------------------end--�����豸��equipment �ӿ�-end---------------------------
	
	
	//=======================���ݳ� ʵʱ����  �ź� �澯 ������ ����  �ӿ�==========================
	//�ź�  ��ȡ
	/**��ȡĳ���豸ʵʱ�ź�����*/
	public static  Hashtable<Integer, Signal> getHtSignal(int equipId){ 
		if(hm_Pool_htSignal==null) return null; //�ݴ��ж�
		if(hm_Pool_htSignal.containsKey(equipId)==false) return null; //�ݴ��ж�
		return hm_Pool_htSignal.get(equipId);
	}
	/**��ȡĳ���豸-ĳ���ź� ʵʱ�ź���*/
	public static  Signal getSignal(int equipId, int signalId){ 
		if(getHtSignal(equipId)==null) return null;		
		if(getHtSignal(equipId).containsKey(signalId)==false) return null;	
		return getHtSignal(equipId).get(signalId);
	}
	//�澯  ��ȡ
	/**��ȡ ϵͳ�澯����*/ 
	public static List<Event> getAllEvent(){
		if(lst_Pool_Event==null) return null;
		synchronized (lst_Pool_Event) {
			return lst_Pool_Event;
		}
	}
	/**��ȡĳ���豸ʵʱ�澯����*/  //<�澯id, �澯��>
	public static  Hashtable<Integer, Event> getHtEvent(int equipId){
		Hashtable<Integer, Event> ht_Event = new Hashtable<Integer, Event>();
		if(lst_Pool_Event==null) return null;
		synchronized (lst_Pool_Event) {		
			for(int i=0;i<lst_Pool_Event.size();i++){
				Event event = lst_Pool_Event.get(i);
				if(event.equipId == equipId){
					ht_Event.put(event.eventId, event);
				}
			}
		}
		return ht_Event;
	}
	/**��ȡĳ���豸-ĳ���澯 ʵʱ�澯��*/
	public static  Event getEvent(int equipId, int eventId){
		if(getHtEvent(equipId)==null) return null;
		if(getHtEvent(equipId).containsKey(eventId)==false) return null;
		return getHtEvent(equipId).get(eventId);
	}
	
	//������  ����
	/**���� ĳ���豸-ĳ��������*/
	public static  int addTigger(Tigger tigger){
		if(lst_Pool_Tigger== null) return 0;
		synchronized (lst_Pool_Tigger) {  //����ͬ�� ��ס
			lst_Pool_Tigger.add(tigger);
			return lst_Pool_Tigger.size();
		}				
	}
	//����    ����
	/**���� ĳ���豸-ĳ����������*/
	public static  int addSCmd(SCmd cmd){
		if(lst_Pool_SCmd== null) return 0;
		synchronized (lst_Pool_SCmd) {
			lst_Pool_SCmd.add(cmd);
			return lst_Pool_SCmd.size();
		}			
	}
	//====================end==���ݳ� ʵʱ����  �ź� �澯 ������ ����  �ӿ�=end========================
	
}
