package DataModel;

import java.util.Hashtable;
import java.util.List;

import android.util.Log;
/****
 * ģ�������д��2��ʵʱ������
 * set-���Ʒ���������
 * get-��������������
 * ����һЩ�ⲿ���÷���-�ѻ�ȡģ������
 */

/**����Ӧ�õ�����ģ��*/
public class Model {

	public Model() {
		// TODO Auto-generated constructor stub
		ht_get_equip = new Hashtable<Integer,Equipment>();
		ht_set_equip = new Hashtable<Integer,Equipment>();
		ht_que_equip = new Hashtable<Integer,Equipment>();
		ht_cmd_equip = new Hashtable<Integer,Equipment>();
	}
	
	//get-�豸������  �������ݽ�����
	public static Hashtable<Integer,Equipment> ht_get_equip;  //gDat����  ����ht get_flag = 0
	//set-�豸������  �������ݽ�����
	public static Hashtable<Integer,Equipment> ht_set_equip;  //sDat����  ����ht set_flag = 1

	//set-���������� ����������������   �ź�����Ϊ��
	public static Hashtable<Integer,Equipment> ht_que_equip;   //sQue����  ����ht set_flag = 2
	//set-���Ϳ�����������                        �ź�����Ϊ��
	public static Hashtable<Integer,Equipment> ht_cmd_equip;   //sCmd����     ����ht set_flag = 3
	
//**********************************�ⲿ���÷����ӿ�***********************************
	//������������
	public static synchronized void clear_Model_ht(int ht_flag){
		if(ht_flag==0){
			ht_get_equip.clear();
		}else if(ht_flag==1){
			ht_set_equip.clear();
		}else if(ht_flag==1){
			ht_que_equip.clear();
		}else if(ht_flag==1){
			ht_cmd_equip.clear();
		}
	}
	//**************************���������豸����************************
	//����ht_get_equip����
	public static synchronized void set_ht_get(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(0);
		ht_get_equip = new_equipment_ht;
	}
	//����ht_set_equip����
	public synchronized void set_ht_set(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(1);
		ht_set_equip = new_equipment_ht;
	}
	//����ht_get_equip����
	public static synchronized void set_ht_setQue(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(2);
		ht_que_equip = new_equipment_ht;
	}
	//����ht_set_equip����
	public static synchronized void set_ht_setCmd(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(3);
		ht_cmd_equip = new_equipment_ht;
	}
	//**************************��ȡ�����豸����************************
	//��ȡht_get_equip����
	public static synchronized Hashtable<Integer,Equipment> get_ht_getHt(){
		return ht_get_equip;
	}
	//��ȡht_set_equip����
	public static synchronized Hashtable<Integer,Equipment> get_ht_setHt(){
		return ht_set_equip;
	}
	//��ȡht_get_equip����
	public static synchronized Hashtable<Integer,Equipment> get_ht_queHt(){
		return ht_que_equip;
	}
	//��ȡht_set_equip����
	public static synchronized Hashtable<Integer,Equipment> get_ht_cmdHt(){
		return ht_cmd_equip;
	}
	
	//**************************����豸�ൽ����************************
	//���һ���豸�ൽget����   ����   ĳid  �豸��
	public static synchronized void add_Equipment_getHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_get_equip.put(EquipID,equip);
	}
	//���һ���豸�ൽget����   ����   ĳid  �豸��
	public static synchronized void add_Equipment_setHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_set_equip.put(EquipID,equip);
	}
	//���һ���豸�ൽque����   ����   ĳid  �豸��
	public static synchronized void add_Equipment_queHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_que_equip.put(EquipID,equip);
	}
	//���һ���豸�ൽcmd����   ����   ĳid  �豸��
	public static synchronized void add_Equipment_cmdHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_cmd_equip.put(EquipID,equip);
	}	
	//**************************��ȡ�����е��豸��************************
	//��ȡget�����ĳid�豸��
	public static synchronized Equipment  get_Equipment_getHt(int equipID){
		if(!ht_get_equip.containsKey(equipID)) return null;
		return ht_get_equip.get(equipID);
	}
	//��ȡset�����ĳid�豸��
	public static synchronized Equipment  get_Equipment_setHt(int equipID){
		if(!ht_set_equip.containsKey(equipID)) return null;
		return ht_set_equip.get(equipID);
	}	
	//��ȡque�����ĳid�豸��
	public static synchronized Equipment  get_Equipment_queHt(int equipID){
		if(!ht_que_equip.containsKey(equipID)) return null;
		return ht_que_equip.get(equipID);
	}	
	//��ȡcmd�����ĳid�豸��
	public static synchronized Equipment  get_Equipment_cmdHt(int equipID){
		if(!ht_cmd_equip.containsKey(equipID)) return null;
		return ht_cmd_equip.get(equipID);
	}
	//**************************����豸�������������************************	
	//���һ��get�����ĳid�豸
	public static synchronized void add_get_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_get_equip.containsKey(equipID)){
			ht_get_equip.get(equipID).type = type;
			ht_get_equip.get(equipID).siganlID = set_sigID;
			ht_get_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_get_equip.put(equipID, equip);
		}
	}
	//���һ��set�����ĳid�豸
	public static synchronized void add_set_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_set_equip.containsKey(equipID)){
			ht_set_equip.get(equipID).type = type;
			ht_set_equip.get(equipID).siganlID = set_sigID;
			ht_set_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_set_equip.put(equipID, equip);
		}
	}	
	//���һ��que�����ĳid�豸
	public static synchronized void add_que_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_que_equip.containsKey(equipID)){
			ht_que_equip.get(equipID).type = type;
			ht_que_equip.get(equipID).siganlID = set_sigID;
			ht_que_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_que_equip.put(equipID, equip);
		}
	}
	//���һ��cmd�����ĳid�豸
	public static synchronized void add_cmd_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_cmd_equip.containsKey(equipID)){
			ht_cmd_equip.get(equipID).type = type;
			ht_cmd_equip.get(equipID).siganlID = set_sigID;
			ht_cmd_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_cmd_equip.put(equipID, equip);
		}
	}	
	
	
	//**************************����ź��ൽ�����е��豸��************************
	//���һ��get�����ĳid�豸��ĳid�ź���   ʹ��Ĭ��ֵtype 
	public static synchronized void add_Signal_getHt(int equipID,int signalID, Signal signal){
		if(ht_get_equip.containsKey(equipID)){
			if(signal!=null)
				ht_get_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_get_equip.put(equipID, equip);
		}
	}
	//���һ��Set�����ĳid�豸��ĳid�ź���  ʹ��Ĭ�ϵ�type
	public static synchronized void add_Signal_setHt(int equipID,int signalID, Signal signal){
		if(ht_set_equip.containsKey(equipID)){
			if(signal!=null)
				ht_set_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_set_equip.put(equipID, equip);
		}
		
	}
	//���һ��que�����ĳid�豸��ĳid�ź���   ʹ��Ĭ��ֵtype 
	public static synchronized void add_Signal_queHt(int equipID,int signalID, Signal signal){
		if(ht_que_equip.containsKey(equipID)){
			if(signal!=null)
				ht_que_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_que_equip.put(equipID, equip);
		}
	}
	//���һ��cmd�����ĳid�豸��ĳid�ź���  ʹ��Ĭ�ϵ�type
	public static synchronized void add_Signal_cmdHt(int equipID,int signalID, Signal signal){
		if(ht_cmd_equip.containsKey(equipID)){
			if(signal!=null)
				ht_cmd_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //�豸��������ʹ��Ĭ��ֵ
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_cmd_equip.put(equipID, equip);
		}
		
	}
	
	//**************************��ȡ�����е��豸���е��ź���************************
	//��ȡget�����ĳid�豸��ĳid�ź���
	public static synchronized Signal get_Signal_getHt(int equipID,int signalID){
		if(ht_get_equip.containsKey(equipID)){
			if(ht_get_equip.get(equipID).ht_signal.containsKey(signalID))
				return ht_get_equip.get(equipID).ht_signal.get(signalID);
		}
		return null;
	}
	//��ȡSet�����ĳid�豸��ĳid�ź���
	public static synchronized Signal get_Signal_setHt(int equipID,int signalID){
		if(ht_set_equip.containsKey(equipID)){
			if(ht_set_equip.get(equipID).ht_signal.containsKey(signalID))
				return ht_set_equip.get(equipID).ht_signal.get(signalID);
		}
		return null;
	}
	
	//***********************��ʼ�� �������� �豸���б�******************************
	public static synchronized int Model_init_lst(List<Integer> lst){
		clear_Model_ht(2);
		for(int i=0; i<lst.size(); i++){
			add_que_setHt(lst.get(i), 0, 0, 0); //��ʼ�� ���������
		}
		
		return ht_que_equip.size();
	}
}
