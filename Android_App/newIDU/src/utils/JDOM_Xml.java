package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


import android.util.Log;

//测试   ok
//JDOM xml 第三方jar
public class JDOM_Xml {

	public JDOM_Xml(String xmlFile) {
		// TODO Auto-generated constructor stub
		xmlfile = xmlFile;
	}
	
	String xmlfile = "";
	String elem1_EquipTemplateInfo = "EquipTemplateInfo";
	String elem2_EquipTemplate = "EquipTemplate";
	String elem3_Signals = "Signals";
	String elem3_Events = "Events";
	String elem3_Commands = "Commands";
	String elem4_Signals_EquipSignal = "EquipSignal";
	String elem4_Events_EquipEvent = "EquipEvent";
	String elem4_Commands_EquipCommand = "EquipCommand";
	String elem5_Signal_Meanings = "Meanings";
	String elem5_Events_Conditions = "Conditions";
	String elem5_Commands_CommandParameter = "CommandParameter";
	String elem6_Signal_SignalMeaning = "SignalMeaning";
	String elem6_Events_EventCondition = "EventCondition";
	
	//funtion  //ok
	public void change_EquipXXXX(int mode,String strAttId, String strAttIdValue,String strAttribute, String newValue) throws JDOMException, IOException{
		String elem3 = "#####";
		String elem4 = "#####";
		switch(mode){
			case 0: return;
			case 1:  //修改信号
				 elem3 = elem3_Signals;
				 elem4 = elem4_Signals_EquipSignal;
				break;
			case 2: //修改告警
				 elem3 = elem3_Events;
				 elem4 = elem4_Events_EquipEvent;
				break;
			case 3: //修改控制
				 elem3 = elem3_Commands;
				 elem4 = elem4_Commands_EquipCommand;
				break;

			default: break;
		}
		
		
		try{
		SAXBuilder saxBuilder = new SAXBuilder(false);
		File file = new File(xmlfile);  //老文件 获取 准备删除
		//创建件DOM xml 新文件
		org.jdom.Document docx = saxBuilder.build(new File(xmlfile));
		Element elem = docx.getRootElement(); //elem1_EquipTemplateInfo
		//修改节点信息
		List list = elem.getChild(elem2_EquipTemplate).getChild(elem3).getChildren(elem4);
		Log.v("JDOM_TAG>JDOM_Test->change_EquipSignal-1","done!");
		for(int i=0;i<list.size();i++){
			Element elem_EquipSignal = (Element)list.get(i); 
			String idValue = elem_EquipSignal.getAttribute(strAttId).getValue();
			if( Integer.parseInt(idValue) == Integer.parseInt(strAttIdValue)){
				elem_EquipSignal.setAttribute(strAttribute, newValue);
				break;
			}
		}
		Log.v("JDOM_TAG>JDOM_Test->change_EquipSignal-2","done!");
		
		//克隆新  节点信息
		Element newElement = (Element)elem.clone();
		//再创建新信息节点的 dom xml文件
		org.jdom.Document newdocx = new Document(newElement);
		Format format = Format.getRawFormat();
		format.setEncoding("utf-8");
		XMLOutputter XMLOut = new XMLOutputter(format);
		file.delete();
		XMLOut.output(newdocx, new FileOutputStream(xmlfile));	
		}catch(Exception e){
			Log.e("JDOM_TAG>JDOM_Test->change_EquipSignal","异常抛出！");
		}
	}
	
	
	//funtion  //ok
	public void change_SECXXXX(int mode,String strAttId1, String strAttIdValue1,
			String strAttId2, String strAttIdValue2,String strAttribute, String newValue) throws JDOMException, IOException{
			String elem3 = "#####";
			String elem4 = "#####";
			String elem5 = "#####";
			String elem6 = "#####";
			switch(mode){
				case 0: return;
				case 10:  //修改信号含义
					 elem3 = elem3_Signals;
					 elem4 = elem4_Signals_EquipSignal;
					 elem5 = elem5_Signal_Meanings;
					 elem6 = elem6_Signal_SignalMeaning;
					break;
				case 20: //修改告警内容
					 elem3 = elem3_Events;
					 elem4 = elem4_Events_EquipEvent;
					 elem5 = elem5_Events_Conditions;
					 elem6 = elem6_Events_EventCondition;
					break;
				case 30: //修改控制
					 elem3 = elem3_Commands;
					 elem4 = elem4_Commands_EquipCommand;
					 elem5 = elem5_Commands_CommandParameter;
					break;

				default: break;
			}
			
			try{
			SAXBuilder saxBuilder = new SAXBuilder(false);
			File file = new File(xmlfile);  //老文件 获取 准备删除
			//创建件DOM xml 新文件
			org.jdom.Document docx = saxBuilder.build(new File(xmlfile));
			Element elem = docx.getRootElement(); //elem1_EquipTemplateInfo
			
			//修改节点信息
			List list = elem.getChild(elem2_EquipTemplate).getChild(elem3).getChildren(elem4);
			Log.v("JDOM_TAG>JDOM_Test->change_SECXXXX-1","done!");
			for(int i=0;i<list.size();i++){
				Element elem_EquipSignal = (Element)list.get(i); 
				String idValue = elem_EquipSignal.getAttribute(strAttId1).getValue();
				if( Integer.parseInt(idValue) == Integer.parseInt(strAttIdValue1)){
					List listp = null;
					if(mode == 30){
						listp = elem_EquipSignal.getChildren(elem5);
					}else{
						listp = elem_EquipSignal.getChild(elem5).getChildren(elem6);
					}
					for(int j=0;j<listp.size();j++){
						Element elem_point =  (Element)listp.get(j);
						String idValue1 = elem_point.getAttribute(strAttId2).getValue();
						if( Integer.parseInt(idValue1) == Integer.parseInt(strAttIdValue2)){
							elem_point.setAttribute(strAttribute, newValue);
							break;
						}
					}
				}
			}
			Log.v("JDOM_TAG>JDOM_Test->change_SECXXXX-2","done!");
			
			//克隆新  节点信息
			Element newElement = (Element)elem.clone();
			//再创建新信息节点的 dom xml文件
			org.jdom.Document newdocx = new Document(newElement);
			Format format = Format.getRawFormat();
			format.setEncoding("utf-8");
			XMLOutputter XMLOut = new XMLOutputter(format);
			file.delete();
			XMLOut.output(newdocx, new FileOutputStream(xmlfile));	
			}catch(Exception e){
				Log.e("JDOM_TAG>JDOM_Test->change_EquipSignal","异常抛出！");
			}
	}
	
	
	//备用测试函数
	public void TestPrintf()
	{
		JDOM_Xml JDOMtest = new JDOM_Xml("/mnt/sdcard/video/EquipmentTemplateFS102HT.xml");
		try {
			JDOMtest.change_EquipXXXX(1,"SignalId", "2", "SignalName","xfxohhoxfx");
			JDOMtest.change_EquipXXXX(2,"EventId", "3", "EventName","kkkoookkk");
			JDOMtest.change_EquipXXXX(3,"CommandId", "2", "CommandName","papapa");
			JDOMtest.change_SECXXXX(10,"SignalId", "10001", "StateValue", "1","Meaning","你妈逼！");
			JDOMtest.change_SECXXXX(20,"EventId", "3", "ConditionId", "1","StartCompareValue","6666");
			JDOMtest.change_SECXXXX(30,"CommandId", "2","ParameterId", "1", "ParameterName","papapa");
		} catch (JDOMException e1) { 
			// TODO Auto-generated catch block
			Log.e("MainActivity》》JDOM_TAG","JDOMException e1");
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.e("MainActivity》》JDOM_TAG","IOException e1");
			e1.printStackTrace();
		}
	}

}
