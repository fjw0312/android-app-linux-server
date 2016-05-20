package DataModel;

import java.util.Hashtable;
import java.util.List;

import android.util.Log;
/****
 * 模型数据中存放2条实时数据链
 * set-控制发送数据链
 * get-请求数据数据链
 * 并有一些外部调用方法-已获取模型数据
 */

/**整个应用的数据模型*/
public class Model {

	public Model() {
		// TODO Auto-generated constructor stub
		ht_get_equip = new Hashtable<Integer,Equipment>();
		ht_set_equip = new Hashtable<Integer,Equipment>();
		ht_que_equip = new Hashtable<Integer,Equipment>();
		ht_cmd_equip = new Hashtable<Integer,Equipment>();
	}
	
	//get-设备数据链  接收数据解析链
	public static Hashtable<Integer,Equipment> ht_get_equip;  //gDat链表  代号ht get_flag = 0
	//set-设备数据链  发送数据解析链
	public static Hashtable<Integer,Equipment> ht_set_equip;  //sDat链表  代号ht set_flag = 1

	//set-请求数据链 发送请求数据链表   信号链表为空
	public static Hashtable<Integer,Equipment> ht_que_equip;   //sQue链表  代号ht set_flag = 2
	//set-发送控制命令链表                        信号链表为空
	public static Hashtable<Integer,Equipment> ht_cmd_equip;   //sCmd链表     代号ht set_flag = 3
	
//**********************************外部调用方法接口***********************************
	//清零链表数据
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
	//**************************设置整条设备链表************************
	//设置ht_get_equip链表
	public static synchronized void set_ht_get(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(0);
		ht_get_equip = new_equipment_ht;
	}
	//设置ht_set_equip链表
	public synchronized void set_ht_set(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(1);
		ht_set_equip = new_equipment_ht;
	}
	//设置ht_get_equip链表
	public static synchronized void set_ht_setQue(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(2);
		ht_que_equip = new_equipment_ht;
	}
	//设置ht_set_equip链表
	public static synchronized void set_ht_setCmd(Hashtable<Integer,Equipment> new_equipment_ht){
		clear_Model_ht(3);
		ht_cmd_equip = new_equipment_ht;
	}
	//**************************获取整条设备链表************************
	//获取ht_get_equip链表
	public static synchronized Hashtable<Integer,Equipment> get_ht_getHt(){
		return ht_get_equip;
	}
	//获取ht_set_equip链表
	public static synchronized Hashtable<Integer,Equipment> get_ht_setHt(){
		return ht_set_equip;
	}
	//获取ht_get_equip链表
	public static synchronized Hashtable<Integer,Equipment> get_ht_queHt(){
		return ht_que_equip;
	}
	//获取ht_set_equip链表
	public static synchronized Hashtable<Integer,Equipment> get_ht_cmdHt(){
		return ht_cmd_equip;
	}
	
	//**************************添加设备类到链表************************
	//添加一个设备类到get链表   参数   某id  设备类
	public static synchronized void add_Equipment_getHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_get_equip.put(EquipID,equip);
	}
	//添加一个设备类到get链表   参数   某id  设备类
	public static synchronized void add_Equipment_setHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_set_equip.put(EquipID,equip);
	}
	//添加一个设备类到que链表   参数   某id  设备类
	public static synchronized void add_Equipment_queHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_que_equip.put(EquipID,equip);
	}
	//添加一个设备类到cmd链表   参数   某id  设备类
	public static synchronized void add_Equipment_cmdHt(int EquipID,Equipment equip){
		if(equip==null) return;
		ht_cmd_equip.put(EquipID,equip);
	}	
	//**************************获取链表中的设备类************************
	//获取get链表的某id设备类
	public static synchronized Equipment  get_Equipment_getHt(int equipID){
		if(!ht_get_equip.containsKey(equipID)) return null;
		return ht_get_equip.get(equipID);
	}
	//获取set链表的某id设备类
	public static synchronized Equipment  get_Equipment_setHt(int equipID){
		if(!ht_set_equip.containsKey(equipID)) return null;
		return ht_set_equip.get(equipID);
	}	
	//获取que链表的某id设备类
	public static synchronized Equipment  get_Equipment_queHt(int equipID){
		if(!ht_que_equip.containsKey(equipID)) return null;
		return ht_que_equip.get(equipID);
	}	
	//获取cmd链表的某id设备类
	public static synchronized Equipment  get_Equipment_cmdHt(int equipID){
		if(!ht_cmd_equip.containsKey(equipID)) return null;
		return ht_cmd_equip.get(equipID);
	}
	//**************************添加设备参数到链表的类************************	
	//添加一个get链表的某id设备
	public static synchronized void add_get_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_get_equip.containsKey(equipID)){
			ht_get_equip.get(equipID).type = type;
			ht_get_equip.get(equipID).siganlID = set_sigID;
			ht_get_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_get_equip.put(equipID, equip);
		}
	}
	//添加一个set链表的某id设备
	public static synchronized void add_set_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_set_equip.containsKey(equipID)){
			ht_set_equip.get(equipID).type = type;
			ht_set_equip.get(equipID).siganlID = set_sigID;
			ht_set_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_set_equip.put(equipID, equip);
		}
	}	
	//添加一个que链表的某id设备
	public static synchronized void add_que_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_que_equip.containsKey(equipID)){
			ht_que_equip.get(equipID).type = type;
			ht_que_equip.get(equipID).siganlID = set_sigID;
			ht_que_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_que_equip.put(equipID, equip);
		}
	}
	//添加一个cmd链表的某id设备
	public static synchronized void add_cmd_setHt(int equipID,int type,int set_sigID,int set_paras){
	
		if(ht_cmd_equip.containsKey(equipID)){
			ht_cmd_equip.get(equipID).type = type;
			ht_cmd_equip.get(equipID).siganlID = set_sigID;
			ht_cmd_equip.get(equipID).para_data= set_paras;
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			equip.type = type;
			equip.siganlID = set_sigID;
			equip.para_data = set_paras;	
			ht_cmd_equip.put(equipID, equip);
		}
	}	
	
	
	//**************************添加信号类到链表中的设备类************************
	//添加一个get链表的某id设备中某id信号类   使用默认值type 
	public static synchronized void add_Signal_getHt(int equipID,int signalID, Signal signal){
		if(ht_get_equip.containsKey(equipID)){
			if(signal!=null)
				ht_get_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_get_equip.put(equipID, equip);
		}
	}
	//添加一个Set链表的某id设备中某id信号类  使用默认的type
	public static synchronized void add_Signal_setHt(int equipID,int signalID, Signal signal){
		if(ht_set_equip.containsKey(equipID)){
			if(signal!=null)
				ht_set_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_set_equip.put(equipID, equip);
		}
		
	}
	//添加一个que链表的某id设备中某id信号类   使用默认值type 
	public static synchronized void add_Signal_queHt(int equipID,int signalID, Signal signal){
		if(ht_que_equip.containsKey(equipID)){
			if(signal!=null)
				ht_que_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_que_equip.put(equipID, equip);
		}
	}
	//添加一个cmd链表的某id设备中某id信号类  使用默认的type
	public static synchronized void add_Signal_cmdHt(int equipID,int signalID, Signal signal){
		if(ht_cmd_equip.containsKey(equipID)){
			if(signal!=null)
				ht_cmd_equip.get(equipID).ht_signal.put(signalID, signal);
		}else{
			Equipment equip = new Equipment();
			equip.equipID = equipID; //设备其他参数使用默认值
			if(signal!=null)
				equip.ht_signal.put(signalID, signal);
			ht_cmd_equip.put(equipID, equip);
		}
		
	}
	
	//**************************获取链表中的设备类中的信号类************************
	//获取get链表的某id设备中某id信号类
	public static synchronized Signal get_Signal_getHt(int equipID,int signalID){
		if(ht_get_equip.containsKey(equipID)){
			if(ht_get_equip.get(equipID).ht_signal.containsKey(signalID))
				return ht_get_equip.get(equipID).ht_signal.get(signalID);
		}
		return null;
	}
	//获取Set链表的某id设备中某id信号类
	public static synchronized Signal get_Signal_setHt(int equipID,int signalID){
		if(ht_set_equip.containsKey(equipID)){
			if(ht_set_equip.get(equipID).ht_signal.containsKey(signalID))
				return ht_set_equip.get(equipID).ht_signal.get(signalID);
		}
		return null;
	}
	
	//***********************初始化 请求数据 设备类列表******************************
	public static synchronized int Model_init_lst(List<Integer> lst){
		clear_Model_ht(2);
		for(int i=0; i<lst.size(); i++){
			add_que_setHt(lst.get(i), 0, 0, 0); //初始化 数据请求表
		}
		
		return ht_que_equip.size();
	}
}
