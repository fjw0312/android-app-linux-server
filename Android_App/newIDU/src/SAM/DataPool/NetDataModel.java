package SAM.DataPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import SAM.DataPoolModel.Event;
import SAM.DataPoolModel.SCmd;
import SAM.DataPoolModel.Signal;
import SAM.DataPoolModel.Tigger;
import SAM.NetHAL.ReadSAMIni;
import SAM.XmlCfg.ChangeEquiptXml;
import SAM.XmlCfg.parseFunction;
import SAM.XmlCfg.xmlDataModel;
import SAM.XmlCfg.xml_EquiptCfg;
import SAM.XmlCfg.xml_cmdCfg;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_signalCfg;
import android.annotation.SuppressLint;
import android.util.Log;


//网络读取的 数据模型
public class NetDataModel {
	@SuppressLint("UseSparseArrays")
	public NetDataModel() {

		//先解析xml模板文件
		parseFunction parseXml = new parseFunction();
		parseXml.fun();
		hm_xmlEquiptCfgModel = xmlDataModel.hm_xmlDataModel;  //获取配置设备类模型
		lst_poolEquipmentId = ReadSAMIni.readSAMIni();        //获取配置 设备id链表
		
		
		hm_Pool_htSignal = new HashMap<Integer, Hashtable<Integer, Signal>>();
		hm_Pool_htSigValue = new HashMap<Integer, Hashtable<Integer, Float>>();
		lst_Pool_Event = new HashMap<Integer,List<Event>>();
		lst_Pool_Tigger = new HashMap<Integer,List<Tigger>>();	
//		lst_Pool_SCmd = new HashMap<Integer,List<SCmd>>();
		lst_Pool_SCmdStr = new HashMap<Integer,String>();
		lst_pool_MyStr = new HashMap<Integer,String>();
	}
	
	//系统 设备id链表
	public static Hashtable<Integer,Integer> lst_poolEquipmentId = null;   //<设备id,设备模板id>	
	public static HashMap<Integer,  xml_EquiptCfg> hm_xmlEquiptCfgModel = null; //<设备配置EquipTemplateId, 设备配置类>
	
	//系统 设备信号链 链表    //<设备id, 信号链表>   <设备id, <信号id, 信号类>>
	public static HashMap<Integer, Hashtable<Integer, Signal>> hm_Pool_htSignal = null; 
	public static HashMap<Integer, Hashtable<Integer, Float>> hm_Pool_htSigValue = null;//<设备id, <信号id, 信号值>> 
	//系统 实时告警 链表    //<设备id,告警类链表>  由于实时更新   故采用List  
	public static HashMap<Integer,List<Event>> lst_Pool_Event = null; //会记录初次告警时间
	//系统  报警包设置 链表  //<设备id,报警包类>   实时发送清除   故采用list  
	public static HashMap<Integer,List<Tigger>> lst_Pool_Tigger = null;    
	//系统  控制命令  链表     //<设备id,控制类>    实时发送清除   故采用list  
//	public static HashMap<Integer,List<SCmd>> lst_Pool_SCmd = null;
	public static HashMap<Integer,String> lst_Pool_SCmdStr = null;  //  控制命令  链表     //<设备id,控制字符>
	//系统  拓展字符  链表   //<设备id,字符串>     实时发送清除   故采用list
	public static HashMap<Integer,String> lst_pool_MyStr = null;
	
	
	
	
//**********************************外部view调用方法接口**北向接口*********************************
	//-------------------------配置设备类equipment 接口-----------------------------------
		/**获取设备配置类*/
		public static  xml_EquiptCfg getEquipmentCfg(int equipId){
			if(lst_poolEquipmentId==null || hm_xmlEquiptCfgModel==null) return null;
			int TemplateId = lst_poolEquipmentId.get(equipId);
			return hm_xmlEquiptCfgModel.get(TemplateId);
		}
		/**获取某设备信号链表*/
		public static  HashMap<String, xml_signalCfg> getHtSignalCfg(int equipId){
			return getEquipmentCfg(equipId).xml_signalCfg_lst;
		}
		/**获取某设备告警配置链表*/
		public static  HashMap<String, xml_eventCfg> getHtEventCfg(int equipId){
			return getEquipmentCfg(equipId).xml_eventCfg_lst;
		}	
		/**获取某设备控制配置链表*/
		public static  HashMap<String, xml_cmdCfg> getHtCmdCfg(int equipId){
			return getEquipmentCfg(equipId).xml_cmdCfg_lst;
		}
		//--------获取配置设备类各链表下的 配置类
		/**获取某设备下某个信号类*/
		public static  xml_signalCfg getSignalCfg(int equipId, int signalId){
			return getHtSignalCfg(equipId).get(String.valueOf(signalId));
		}
		/**获取某设备下某个告警配置类*/
		public static  xml_eventCfg getEventCfg(int equipId, int efEventCfgId){
			return getHtEventCfg(equipId).get(String.valueOf(efEventCfgId));
		}
		/**获取某设备下某个控制配置类*/
		public static  xml_cmdCfg getSCmdCfg(int equipId, int cId){
			return getHtCmdCfg(equipId).get(String.valueOf(cId));
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
		public static HashMap<Integer,List<Event>> getAllEvent(){
			if(lst_Pool_Event==null) return null;
			synchronized (lst_Pool_Event) {
				return lst_Pool_Event;
			}
		}
		/**获取某个设备实时告警链表*/  //<告警id, 告警类>
		public static  List<Event> getHtEvent(int equipId){
			if(lst_Pool_Event==null) return null;
			synchronized (lst_Pool_Event) {		
				return lst_Pool_Event.get(equipId);
			}
		}
		/**获取某个设备-某条告警 实时告警类*/
		public static  Event getEvent(int equipId, int eventId){
			List<Event> eventLst = getHtEvent(equipId);
			if(eventLst == null) return null;
			//遍历告警
			for(int i=0;i<eventLst.size();i++){
				Event event = eventLst.get(i);
				if(event.eventId == eventId){
					return event;
				}
			}
			return null;
		}
		
		//控制    设置
		/**设置 某个设备-某条控制命令*/
		public static  int addSCmd(SCmd cmd){
			if(lst_Pool_SCmdStr== null) return 0;
			xml_cmdCfg xmlCmdCfg = getSCmdCfg(cmd.equipId, cmd.cmdId);
			if(xmlCmdCfg == null) return 0;
			String cmdstr = xmlCmdCfg.CommandToken+","+cmd.value.trim();
			synchronized (lst_Pool_SCmdStr) {
				lst_Pool_SCmdStr.put(cmd.equipId,cmdstr);
				return lst_Pool_SCmdStr.size();
			}			
		}
		//控制  地址数据
		//发送  拓展字符
		/**设置 某个设备-发送  拓展字符*/
		public static  int addMyStr(int equiptId, String mystr){
			if(lst_pool_MyStr== null) return 0;
				synchronized (lst_pool_MyStr) {
					lst_pool_MyStr.put(equiptId,mystr);
					return lst_pool_MyStr.size();
				}			
		}
		//报警包  设置
		/**设置 某个设备-某条报警包*/
		public static  int addTigger(Tigger tigger){
			if(lst_Pool_Tigger== null || tigger==null) return 0;
			synchronized (lst_Pool_Tigger) {  //数据同步 锁住
				//修改  数据模型的告警 配置
				try{
					int TemplateId = lst_poolEquipmentId.get(tigger.equipId);
					String xmlFileName = hm_xmlEquiptCfgModel.get(TemplateId).fileName;
				
					xml_eventCfg eventCfg = getEventCfg(tigger.equipId,tigger.tiggerId);
					if(tigger.enabled==1 && "false".equals(eventCfg.Enable) ){
						eventCfg.Enable = "true";
						//Log.e("netDataModel>>addTigger",eventCfg.Enable+" "+ String.valueOf(tigger.enabled));
						ChangeEquiptXml changeXml = new ChangeEquiptXml(xmlFileName);	 	
						changeXml.setArgv(2, "EventId", String.valueOf(tigger.tiggerId), "", "", "Enable", "true");
						changeXml.start();
						return 0;
					}else if(tigger.enabled==0 && "true".equals(eventCfg.Enable) ){
						eventCfg.Enable = "false"; 
						//Log.e("netDataModel>>addTigger",eventCfg.Enable+" "+ String.valueOf(tigger.enabled));
						ChangeEquiptXml changeXml = new ChangeEquiptXml(xmlFileName);
						changeXml.setArgv(2, "EventId", String.valueOf(tigger.tiggerId), "", "", "Enable", "false");
						changeXml.start();
						return 0;
					}
					if(eventCfg.EventConditionlst != null){
						String tigCondition = String.valueOf(tigger.conditionid);
						eventCfg.EventConditionlst.get(tigCondition).StartCompareValue 
					           = String.valueOf(tigger.startvalue);
								
						//再修改 配置文件
						ChangeEquiptXml changeXml = new ChangeEquiptXml(xmlFileName);
						String strAttId1 = "EventId";
						String strAttIdValue1 = String.valueOf(tigger.tiggerId);
						String strAttId2 = "ConditionId";
						String strAttIdValue2 = String.valueOf(tigger.conditionid);
						String strAttribute = "StartCompareValue";
						String newValue = String.valueOf(tigger.startvalue);
						changeXml.setArgv(20, strAttId1, strAttIdValue1, strAttId2, strAttIdValue2, strAttribute, newValue);
						changeXml.start();
						return 0;
					}
				}catch(Exception e){ 
					Log.e("NetDataModel->addTigger","异常抛出！");
				}
			}
			return lst_Pool_Tigger.size();
		}
//====================end==数据池 实时数据  信号 告警 报警包 控制  接口=end========================
}
