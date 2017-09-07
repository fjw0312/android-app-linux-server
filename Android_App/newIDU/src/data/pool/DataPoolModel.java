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

/**数据池 模型--app 的数据存储类*/
//该类主要成员  设备配置信息类链表   信号类链表   告警类链表   报警包类链表   控制类链表
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
	//系统 设备类链表
	public static HashMap<Integer,  Equipment> hm_poolEquipment = null; //<设备id, 设备类>
	
	//系统 设备id链表
	public static List<Integer> lst_poolEquipmentId = null;   //<设备id>
	//系统 设备名链表
	public static HashMap<Integer, String> hm_poolEquipmentName = null; //<设备id, 设备名称>	
	//系统 设备xml文件路径名链表
	public static HashMap<Integer, String> hm_poolEquipmentXmlfile = null; //<设备id, 设备xml文件路劲名>
	
	
	//系统 设备信号链 链表    //<设备id, 信号链表>   <设备id, <信号id, 信号类>>
	public static HashMap<Integer, Hashtable<Integer, Signal>> hm_Pool_htSignal = null; 
	//系统 实时告警 链表    //<告警类链表>  由于实时更新   故采用List  不定容 2维数据不好处理 
	public static List<Event> lst_Pool_Event = null;
	//系统  报警包设置 链表  //<报警包类>   实时发送清除   故采用list  不定容 2维数据不好处理 
	public static List<Tigger> lst_Pool_Tigger = null;
	//系统  控制命令  链表     //<控制类>    实时发送清除   故采用list  不定容 2维数据不好处理 
	public static List<SCmd> lst_Pool_SCmd = null;
	
	
//	//系统 实时告警 链表    //<设备id, 告警链表>   <设备id, <告警id, 告警类>>
//	public static HashMap<Integer, Hashtable<Integer, Event>> hm_Pool_htEvent = null;
//	//系统  告警阈值设置 链表    //<设备id, 报警包链表>   <设备id, <告警id, 报警包类>>
//	public static HashMap<Integer, Hashtable<Integer, Tigger>> hm_Pool_htTigger = null;
//	//系统  控制命令 链表    //<设备id, 控制链表>   <设备id, <控制id, 控制命令类>>
//	public static HashMap<Integer, Hashtable<Integer, SCmd>> hm_Pool_htSCmd = null;
//	
	
	
	//**********************************外部view调用方法接口**北向接口*********************************

	//-------------------------配置设备类equipment 接口----------------------------
	/**获取设备类*/
	public static  Equipment getEquipment(int equipId){
		return hm_poolEquipment.get(equipId);
	}
	/**获取某设备信号链表*/
	public static  Hashtable<Integer, SignalCfg> getHtSignalCfg(int equipId){
		return hm_poolEquipment.get(equipId).htSignalCfg;
	}
	/**获取某设备告警配置链表*/
	public static  Hashtable<Integer, EventCfg> getHtEventCfg(int equipId){
		return hm_poolEquipment.get(equipId).htEventCfg;
	}	
	/**获取某设备告警包类链表*/
	public static  Hashtable<Integer, TiggerCfg> getHtTiggerCfg(int equipId){
		return hm_poolEquipment.get(equipId).htTiggerCfg;
	}
	/**获取某设备控制配置链表*/
	public static  Hashtable<Integer, SCmdCfg> getHtCmdCfg(int equipId){
		return hm_poolEquipment.get(equipId).htCmdCfg;
	}
	//--------获取配置设备类各链表下的 配置类
	/**获取某设备下某个信号类*/
	public static  SignalCfg getSignalCfg(int equipId, int signalId){
		return getHtSignalCfg(equipId).get(signalId);
	}
	/**获取某设备下某个告警配置类*/
	public static  EventCfg getEventCfg(int equipId, int efEventCfgId){
		return getHtEventCfg(equipId).get(efEventCfgId);
	}
	/**获取某设备下某个告警配置包类*/
	public static  TiggerCfg getTiggerCfg(int equipId, int tTiggerId){
		return getHtTiggerCfg(equipId).get(tTiggerId);
	}
	/**获取某设备下某个控制配置类*/
	public static  SCmdCfg getSCmdCfg(int equipId, int cId){
		return getHtCmdCfg(equipId).get(cId);
	}
	//------------------------end--配置设备类equipment 接口-end---------------------------
	
	
	//=======================数据池 实时数据  信号 告警 报警包 控制  接口==========================
	//信号  获取
	/**获取某个设备实时信号链表*/
	public static  Hashtable<Integer, Signal> getHtSignal(int equipId){ 
		if(hm_Pool_htSignal==null) return null; //容错判断
		if(hm_Pool_htSignal.containsKey(equipId)==false) return null; //容错判断
		return hm_Pool_htSignal.get(equipId);
	}
	/**获取某个设备-某条信号 实时信号类*/
	public static  Signal getSignal(int equipId, int signalId){ 
		if(getHtSignal(equipId)==null) return null;		
		if(getHtSignal(equipId).containsKey(signalId)==false) return null;	
		return getHtSignal(equipId).get(signalId);
	}
	//告警  获取
	/**获取 系统告警链表*/ 
	public static List<Event> getAllEvent(){
		if(lst_Pool_Event==null) return null;
		synchronized (lst_Pool_Event) {
			return lst_Pool_Event;
		}
	}
	/**获取某个设备实时告警链表*/  //<告警id, 告警类>
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
	/**获取某个设备-某条告警 实时告警类*/
	public static  Event getEvent(int equipId, int eventId){
		if(getHtEvent(equipId)==null) return null;
		if(getHtEvent(equipId).containsKey(eventId)==false) return null;
		return getHtEvent(equipId).get(eventId);
	}
	
	//报警包  设置
	/**设置 某个设备-某条报警包*/
	public static  int addTigger(Tigger tigger){
		if(lst_Pool_Tigger== null) return 0;
		synchronized (lst_Pool_Tigger) {  //数据同步 锁住
			lst_Pool_Tigger.add(tigger);
			return lst_Pool_Tigger.size();
		}				
	}
	//控制    设置
	/**设置 某个设备-某条控制命令*/
	public static  int addSCmd(SCmd cmd){
		if(lst_Pool_SCmd== null) return 0;
		synchronized (lst_Pool_SCmd) {
			lst_Pool_SCmd.add(cmd);
			return lst_Pool_SCmd.size();
		}			
	}
	//====================end==数据池 实时数据  信号 告警 报警包 控制  接口=end========================
	
}
