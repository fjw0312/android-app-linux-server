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


//�����ȡ�� ����ģ��
public class NetDataModel {
	@SuppressLint("UseSparseArrays")
	public NetDataModel() {

		//�Ƚ���xmlģ���ļ�
		parseFunction parseXml = new parseFunction();
		parseXml.fun();
		hm_xmlEquiptCfgModel = xmlDataModel.hm_xmlDataModel;  //��ȡ�����豸��ģ��
		lst_poolEquipmentId = ReadSAMIni.readSAMIni();        //��ȡ���� �豸id����
		
		
		hm_Pool_htSignal = new HashMap<Integer, Hashtable<Integer, Signal>>();
		hm_Pool_htSigValue = new HashMap<Integer, Hashtable<Integer, Float>>();
		lst_Pool_Event = new HashMap<Integer,List<Event>>();
		lst_Pool_Tigger = new HashMap<Integer,List<Tigger>>();	
//		lst_Pool_SCmd = new HashMap<Integer,List<SCmd>>();
		lst_Pool_SCmdStr = new HashMap<Integer,String>();
		lst_pool_MyStr = new HashMap<Integer,String>();
	}
	
	//ϵͳ �豸id����
	public static Hashtable<Integer,Integer> lst_poolEquipmentId = null;   //<�豸id,�豸ģ��id>	
	public static HashMap<Integer,  xml_EquiptCfg> hm_xmlEquiptCfgModel = null; //<�豸����EquipTemplateId, �豸������>
	
	//ϵͳ �豸�ź��� ����    //<�豸id, �ź�����>   <�豸id, <�ź�id, �ź���>>
	public static HashMap<Integer, Hashtable<Integer, Signal>> hm_Pool_htSignal = null; 
	public static HashMap<Integer, Hashtable<Integer, Float>> hm_Pool_htSigValue = null;//<�豸id, <�ź�id, �ź�ֵ>> 
	//ϵͳ ʵʱ�澯 ����    //<�豸id,�澯������>  ����ʵʱ����   �ʲ���List  
	public static HashMap<Integer,List<Event>> lst_Pool_Event = null; //���¼���θ澯ʱ��
	//ϵͳ  ���������� ����  //<�豸id,��������>   ʵʱ�������   �ʲ���list  
	public static HashMap<Integer,List<Tigger>> lst_Pool_Tigger = null;    
	//ϵͳ  ��������  ����     //<�豸id,������>    ʵʱ�������   �ʲ���list  
//	public static HashMap<Integer,List<SCmd>> lst_Pool_SCmd = null;
	public static HashMap<Integer,String> lst_Pool_SCmdStr = null;  //  ��������  ����     //<�豸id,�����ַ�>
	//ϵͳ  ��չ�ַ�  ����   //<�豸id,�ַ���>     ʵʱ�������   �ʲ���list
	public static HashMap<Integer,String> lst_pool_MyStr = null;
	
	
	
	
//**********************************�ⲿview���÷����ӿ�**����ӿ�*********************************
	//-------------------------�����豸��equipment �ӿ�-----------------------------------
		/**��ȡ�豸������*/
		public static  xml_EquiptCfg getEquipmentCfg(int equipId){
			if(lst_poolEquipmentId==null || hm_xmlEquiptCfgModel==null) return null;
			int TemplateId = lst_poolEquipmentId.get(equipId);
			return hm_xmlEquiptCfgModel.get(TemplateId);
		}
		/**��ȡĳ�豸�ź�����*/
		public static  HashMap<String, xml_signalCfg> getHtSignalCfg(int equipId){
			return getEquipmentCfg(equipId).xml_signalCfg_lst;
		}
		/**��ȡĳ�豸�澯��������*/
		public static  HashMap<String, xml_eventCfg> getHtEventCfg(int equipId){
			return getEquipmentCfg(equipId).xml_eventCfg_lst;
		}	
		/**��ȡĳ�豸������������*/
		public static  HashMap<String, xml_cmdCfg> getHtCmdCfg(int equipId){
			return getEquipmentCfg(equipId).xml_cmdCfg_lst;
		}
		//--------��ȡ�����豸��������µ� ������
		/**��ȡĳ�豸��ĳ���ź���*/
		public static  xml_signalCfg getSignalCfg(int equipId, int signalId){
			return getHtSignalCfg(equipId).get(String.valueOf(signalId));
		}
		/**��ȡĳ�豸��ĳ���澯������*/
		public static  xml_eventCfg getEventCfg(int equipId, int efEventCfgId){
			return getHtEventCfg(equipId).get(String.valueOf(efEventCfgId));
		}
		/**��ȡĳ�豸��ĳ������������*/
		public static  xml_cmdCfg getSCmdCfg(int equipId, int cId){
			return getHtCmdCfg(equipId).get(String.valueOf(cId));
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
		public static HashMap<Integer,List<Event>> getAllEvent(){
			if(lst_Pool_Event==null) return null;
			synchronized (lst_Pool_Event) {
				return lst_Pool_Event;
			}
		}
		/**��ȡĳ���豸ʵʱ�澯����*/  //<�澯id, �澯��>
		public static  List<Event> getHtEvent(int equipId){
			if(lst_Pool_Event==null) return null;
			synchronized (lst_Pool_Event) {		
				return lst_Pool_Event.get(equipId);
			}
		}
		/**��ȡĳ���豸-ĳ���澯 ʵʱ�澯��*/
		public static  Event getEvent(int equipId, int eventId){
			List<Event> eventLst = getHtEvent(equipId);
			if(eventLst == null) return null;
			//�����澯
			for(int i=0;i<eventLst.size();i++){
				Event event = eventLst.get(i);
				if(event.eventId == eventId){
					return event;
				}
			}
			return null;
		}
		
		//����    ����
		/**���� ĳ���豸-ĳ����������*/
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
		//����  ��ַ����
		//����  ��չ�ַ�
		/**���� ĳ���豸-����  ��չ�ַ�*/
		public static  int addMyStr(int equiptId, String mystr){
			if(lst_pool_MyStr== null) return 0;
				synchronized (lst_pool_MyStr) {
					lst_pool_MyStr.put(equiptId,mystr);
					return lst_pool_MyStr.size();
				}			
		}
		//������  ����
		/**���� ĳ���豸-ĳ��������*/
		public static  int addTigger(Tigger tigger){
			if(lst_Pool_Tigger== null || tigger==null) return 0;
			synchronized (lst_Pool_Tigger) {  //����ͬ�� ��ס
				//�޸�  ����ģ�͵ĸ澯 ����
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
								
						//���޸� �����ļ�
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
					Log.e("NetDataModel->addTigger","�쳣�׳���");
				}
			}
			return lst_Pool_Tigger.size();
		}
//====================end==���ݳ� ʵʱ����  �ź� �澯 ������ ����  �ӿ�=end========================
}
