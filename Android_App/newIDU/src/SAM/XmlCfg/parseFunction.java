package SAM.XmlCfg;

import java.io.File;
import java.util.Iterator;

import SAM.XmlCfg.xml_cmdCfg.CommandParameter;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import android.util.Log;

//���� �豸ģ���߳�    //����Ϊ������ ����������
//�������߼�parseFunction-��parseEquuipt-��xmlCfg-������xml�ļ�  ==��xmlDataModel���ݴ�ű�������
public class parseFunction{
	public parseFunction() {
		// TODO Auto-generated constructor stub	
	//	xmlModel = new xmlDataModel();
	}
	
	//Fields
	String XmlFile = "/data/fjw/XmlCfg"; //�豸ģ�������ļ���
	File file = null;  //�ļ���
	String[] strfiles = null;
	int filesNum  = 0;
	//xmlDataModel xmlModel = null;
	
	public void fun() {
		// TODO Auto-generated method stub
		
		//�� �ļ���
		file = new File(XmlFile);
		if(!file.exists()){ //�ж��ļ�/Ŀ¼�Ƿ���� 
			Log.e("parseThread>>run ","XmlFile�ļ������ڣ�");
		}
		if(file.isDirectory()){ //�ж��Ƿ�Ϊ �ļ���
			strfiles = file.list();			
			filesNum = strfiles.length;
			
		}
		//���� �����豸ģ�� ���߳�
		for(int i=0;i<filesNum;i++){
			parseFileFunction(XmlFile+"/"+strfiles[i]);			
		//	Log.e("parseThread>>�ļ���",strfiles[i]);
		}			
	}

	
	public void parseFileFunction(String fileName){
		String filename = fileName;
		
		// TODO Auto-generated method stub
		parseEquiptXml pXml = new parseEquiptXml(filename); //�����豸ģ��						
		try {
				xmlDataModel.hm_xmlDataModel.put(Integer.valueOf(pXml.equiptCfg.EquipTemplateId), pXml.equiptCfg);
		//		Thread.sleep(100); //100ms
		//		TestParsePrintf(pXml.equiptCfg); //��ӡ����������
		} catch (Exception e) {			
				Log.e("parseThread>>parseFileThread>>>", "put �����ļ������쳣�׳���");
		}
	}
		

//  δʹ�õ�   ����ר��  ���Ժ���
	public void TestParsePrintf(xml_EquiptCfg equiptCfg){
		Log.e("parseThread>>TestParsePrintf","++++++++++++++++++��ʼ�����豸ģ��+++++++++++++++++++");
		Log.e("parseThread>>TestParsePrintf>>",equiptCfg.fileName+" "
				 +equiptCfg.EquipTemplateId+" "
				 +equiptCfg.EquipTemplateName+" "
				 +equiptCfg.EquipTemplateType+" ");
		Log.e("parseThread>>TestParsePrintf","++++++++++++++++++�豸ģ��������+++++++++++++++++++");
		Log.e("parseThread>>TestParsePrintf","=================�����鿴�ź�==============");
		Iterator<String> ite = equiptCfg.xml_signalCfg_lst.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			xml_signalCfg sigcfg = equiptCfg.xml_signalCfg_lst.get(key);
			Log.e("parseThread>>TestParsePrintf>>>�źţ�",sigcfg.SignalId+" "
			+sigcfg.SignalName+" "
			+sigcfg.SignalBaseId+" "
			+sigcfg.SignalType+" "
			+sigcfg.ChannelNo+" "
			+sigcfg.Expression+" "
			+sigcfg.ShowPrecision+" "
			+sigcfg.Unit+" "
			+sigcfg.Enable+" ");
			if(sigcfg.SignalMeaninglst.size()>0){
			Iterator<String> iter = sigcfg.SignalMeaninglst.keySet().iterator();
			while(iter.hasNext()){
				String key1 = iter.next();
				String value1 = sigcfg.SignalMeaninglst.get(key1);
				Log.e("parseThread>>TestParsePrintf>>>�źţ�meaning:",key1+" "
					 +value1+" ");
				}
			}
		}
		Log.e("parseThread>>TestParsePrintf","=================�鿴�źŽ���==============");
		
		Log.e("parseThread>>TestParsePrintf","=================�����鿴�澯==============");
		Iterator<String> ite10 = equiptCfg.xml_eventCfg_lst.keySet().iterator();
		while(ite10.hasNext()){
			String key = ite10.next();
			xml_eventCfg eventcfg = equiptCfg.xml_eventCfg_lst.get(key);
			Log.e("parseThread>>TestParsePrintf>>�澯��",eventcfg.EventId+" "
			+eventcfg.EventName+" "
			+eventcfg.EventBaseId+" "
			+eventcfg.EventType+" "
			+eventcfg.StartExpression+" "
			+eventcfg.Enable+" ");
			if(eventcfg.EventConditionlst.size()>0){
			Iterator<String> iter = eventcfg.EventConditionlst.keySet().iterator();
			while(iter.hasNext()){
				String key1 = iter.next();
				EventCondition Condition = eventcfg.EventConditionlst.get(key1);
				Log.e("parseThread>>TestParsePrintf>>�澯EventCondition��",Condition.ConditionId+" "
					 +Condition.Meaning+" "
					 +Condition.EventSeverity+" "
					 +Condition.StartOperation+" "
					 +Condition.StartCompareValue+" "
					  +Condition.StartDelay+" "
					  +Condition.EndCompareValue+" "
					  +Condition.EndDelay+" ");
				}
			}
		}
		Log.e("parseThread>>TestParsePrintf","=================�鿴�źŽ���==============");
		
		Log.e("parseThread>>TestParsePrintf","=================�����鿴����==============");
		Iterator<String> ite20 = equiptCfg.xml_cmdCfg_lst.keySet().iterator();
		while(ite20.hasNext()){
			String key = ite20.next();
			xml_cmdCfg cmdcfg = equiptCfg.xml_cmdCfg_lst.get(key);
			Log.e("parseThread>>TestParsePrintf>>���ƣ�",cmdcfg.CommandId+" "
			+cmdcfg.CommandName+" "
			+cmdcfg.CommandBaseId+" "
			+cmdcfg.CommandType+" "
			+cmdcfg.CommandToken+" "
			+cmdcfg.Retry+" "
			+cmdcfg.Enable+" ");
			if(cmdcfg.CommandParameterlst.size()>0){
				Iterator<String> iter = cmdcfg.CommandParameterlst.keySet().iterator();
				while(iter.hasNext()){
					String key1 = iter.next(); 
					CommandParameter Parameter = cmdcfg.CommandParameterlst.get(key1);
					Log.e("parseThread>>TestParsePrintf>>����CommandParameter��",Parameter.ParameterId+" "
						 +Parameter.ParameterName+" "
						 +Parameter.UIControlType+" "
						 +Parameter.DataType+" ");
					if(Parameter.CommandMeaninglst.size()>0){
						Iterator<String> iter2 = Parameter.CommandMeaninglst.keySet().iterator();
						while(iter2.hasNext()){
							String key2 = iter2.next();
							String value2 = Parameter.CommandMeaninglst.get(key2);
							Log.e("parseThread>>TestParsePrintf>>����CommandParameter��meaning:",key2+" "
									 +value2+" ");
							}
					}
				}
			}
		}
		Log.e("parseThread>>TestParsePrintf","=================�鿴���ƽ���==============");
				
	}

}
