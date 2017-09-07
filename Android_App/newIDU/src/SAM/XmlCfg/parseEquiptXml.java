package SAM.XmlCfg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import SAM.XmlCfg.xml_cmdCfg.CommandParameter;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import android.util.Log;
import android.util.Xml;

/**�豸ģ�� ����ִ����*/
public class parseEquiptXml {

	public parseEquiptXml(String filename) {
		// TODO Auto-generated constructor stub
		equiptCfg.fileName = filename;
		file = new File(filename);
		try{
		parseFlie();
		}catch(Exception e){
			Log.e("parseEquiptXml>>","�����豸ģ�������ļ��쳣�׳���");
		}
	}
	
	//Fields
	File file = null;  //�ļ���
	InputStream fis = null;  //��ȡ�ֽ���
	public xml_EquiptCfg equiptCfg = new xml_EquiptCfg(); //�豸������

	public void parseFlie() throws FileNotFoundException{
		//��ȡ�ֽ���
		fis = new BufferedInputStream(new FileInputStream(file));
		if(fis==null){
			Log.e("parseEquiptXml->parseFlie","���ļ����ֽ�����");
			return;
		}
		//����Xml�ֽ���
		try{
//			Log.e("parseEquiptXml->parseFlie","flag-1");
			parseStream(fis);
//			Log.e("parseEquiptXml->parseFlie","flag-2");
			fis.close(); 
		
		}catch(Exception e){
			Log.e("parseEquiptXml->parseFlie","�����ֽ����쳣�׳���");
		}
		fis = null;
	}

	public void parseStream(InputStream inStream) throws Exception{
		//�����м����
		xml_signalCfg H_sigCfg = null;
		xml_eventCfg H_eventCfg = null;
		xml_cmdCfg H_cmdCfg = null;
		CommandParameter H_commandParameter = null;


		//1.����xml������
		XmlPullParser pullParser = Xml.newPullParser();
		//2.���ý���������
		pullParser.setInput(inStream, "UTF-8");
		
		//3.��ȡXml�����¼�����
		int event = pullParser.getEventType();

		//4.�жϽڵ�����  ����ȡ��Ϣ
		while(event != XmlPullParser.END_DOCUMENT){
			switch(event){

			//a.�ļ�ͷ
			case XmlPullParser.START_DOCUMENT:
					break;
			//b.�ڵ��ͷ
			case XmlPullParser.START_TAG:
				//��ȡ�ڵ�����
				String name = pullParser.getName();
			//	Log.e("ParseXml->parseStream","XmlPullParser name="+name);
				//�жϽڵ�����
				if("EquipTemplate".equals(name)){		
					equiptCfg.EquipTemplateId = pullParser.getAttributeValue("", "EquipTemplateId");
					equiptCfg.EquipTemplateName = pullParser.getAttributeValue("", "EquipTemplateName");
					equiptCfg.EquipTemplateType = pullParser.getAttributeValue("", "EquipTemplateType");
					equiptCfg.LibName = pullParser.getAttributeValue("", "LibName");	
	
				}else if("EquipSignal".equals(name)){	//�����źŽڵ㲿��
					xml_signalCfg sigCfg = new xml_signalCfg();
					sigCfg.SignalId = pullParser.getAttributeValue("", "SignalId");
					sigCfg.SignalName = pullParser.getAttributeValue("", "SignalName");
					sigCfg.SignalBaseId = pullParser.getAttributeValue("", "SignalBaseId");
					sigCfg.SignalType = pullParser.getAttributeValue("", "SignalType");
					sigCfg.ChannelNo = pullParser.getAttributeValue("", "ChannelNo");
					sigCfg.Expression = pullParser.getAttributeValue("", "Expression");
					sigCfg.ShowPrecision = pullParser.getAttributeValue("", "ShowPrecision");
					sigCfg.Unit = pullParser.getAttributeValue("", "Unit");
					sigCfg.Enable = pullParser.getAttributeValue("", "Enable");
					equiptCfg.xml_signalCfg_lst.put(sigCfg.SignalId, sigCfg);
					equiptCfg.sig_ChannelNo_id_lst.put(sigCfg.ChannelNo, sigCfg.SignalId);
					H_sigCfg = sigCfg;
				}else if("SignalMeaning".equals(name)){
					String StateValue = pullParser.getAttributeValue("", "StateValue");
					String Meaning = pullParser.getAttributeValue("", "Meaning");					
					H_sigCfg.SignalMeaninglst.put(StateValue, Meaning);					
				}else if("EquipEvent".equals(name)){  //�����澯�ڵ㲿��
					xml_eventCfg eventCfg = new xml_eventCfg();
					eventCfg.EventId = pullParser.getAttributeValue("", "EventId");
					eventCfg.EventName = pullParser.getAttributeValue("", "EventName");
					eventCfg.EventBaseId = pullParser.getAttributeValue("", "EventBaseId");
					eventCfg.EventType = pullParser.getAttributeValue("", "EventType");
					eventCfg.StartExpression = pullParser.getAttributeValue("", "StartExpression");				
					eventCfg.Enable = pullParser.getAttributeValue("", "Enable");
					equiptCfg.xml_eventCfg_lst.put(eventCfg.EventId, eventCfg);
					H_eventCfg = eventCfg;
				}else if("EventCondition".equals(name)){
					EventCondition eventCondition = H_eventCfg.new EventCondition();
					eventCondition.ConditionId = pullParser.getAttributeValue("", "ConditionId");   
					eventCondition.Meaning = pullParser.getAttributeValue("", "Meaning");
					eventCondition.EventSeverity = pullParser.getAttributeValue("", "EventSeverity"); 
					eventCondition.StartOperation = pullParser.getAttributeValue("", "StartOperation");
					eventCondition.StartCompareValue = pullParser.getAttributeValue("", "StartCompareValue");
					eventCondition.StartDelay = pullParser.getAttributeValue("", "StartDelay"); 
					eventCondition.EndCompareValue = pullParser.getAttributeValue("", "EndCompareValue");
					eventCondition.EndDelay = pullParser.getAttributeValue("", "EndDelay"); 
					H_eventCfg.EventConditionlst.put(eventCondition.ConditionId, eventCondition);
				}else if("EquipCommand".equals(name)){ //�������ƽڵ㲿��
					xml_cmdCfg cmdCfg = new xml_cmdCfg();
					cmdCfg.CommandId = pullParser.getAttributeValue("", "CommandId"); 
					cmdCfg.CommandName = pullParser.getAttributeValue("", "CommandName");
					cmdCfg.CommandBaseId = pullParser.getAttributeValue("", "CommandBaseId");
					cmdCfg.CommandType = pullParser.getAttributeValue("", "CommandType");
					cmdCfg.CommandToken = pullParser.getAttributeValue("", "CommandToken");
					cmdCfg.Retry = pullParser.getAttributeValue("", "Retry");
					cmdCfg.Enable = pullParser.getAttributeValue("", "Enable");
					equiptCfg.xml_cmdCfg_lst.put(cmdCfg.CommandId, cmdCfg);
					H_cmdCfg = cmdCfg;
				}else if("CommandParameter".equals(name)){
					CommandParameter commandParameter = H_cmdCfg.new CommandParameter();
					commandParameter.ParameterId = pullParser.getAttributeValue("", "ParameterId");
					commandParameter.ParameterName = pullParser.getAttributeValue("", "ParameterName");
					commandParameter.UIControlType = pullParser.getAttributeValue("", "UIControlType");
					commandParameter.DataType = pullParser.getAttributeValue("", "DataType");
					H_cmdCfg.CommandParameterlst.put(commandParameter.ParameterId, commandParameter);
					H_commandParameter = commandParameter;
				}else if("CommandMeaning".equals(name)){
					String ParameterValue = pullParser.getAttributeValue("", "ParameterValue");
					String Meaning = pullParser.getAttributeValue("", "Meaning");
					H_commandParameter.CommandMeaninglst.put(ParameterValue, Meaning);
				}
				//Log.e("parseEquiptXml->parseStream","flag-k-1");		
				break;
				//c.�ڵ��β
			case XmlPullParser.END_TAG:
				break;
			}	
		//5.ָ����λ��һ���ڵ�
		event = pullParser.next();

		}	
	}


}
