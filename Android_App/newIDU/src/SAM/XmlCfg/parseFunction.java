package SAM.XmlCfg;

import java.io.File;
import java.util.Iterator;

import SAM.XmlCfg.xml_cmdCfg.CommandParameter;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import android.util.Log;

//解析 设备模板线程    //该类为本包的 被外调入口类
//本包的逻辑parseFunction-》parseEquuipt-》xmlCfg-》本地xml文件  ==》xmlDataModel数据存放被访问类
public class parseFunction{
	public parseFunction() {
		// TODO Auto-generated constructor stub	
	//	xmlModel = new xmlDataModel();
	}
	
	//Fields
	String XmlFile = "/data/fjw/XmlCfg"; //设备模板所在文件夹
	File file = null;  //文件类
	String[] strfiles = null;
	int filesNum  = 0;
	//xmlDataModel xmlModel = null;
	
	public void fun() {
		// TODO Auto-generated method stub
		
		//打开 文件夹
		file = new File(XmlFile);
		if(!file.exists()){ //判断文件/目录是否存在 
			Log.e("parseThread>>run ","XmlFile文件不存在！");
		}
		if(file.isDirectory()){ //判断是否为 文件夹
			strfiles = file.list();			
			filesNum = strfiles.length;
			
		}
		//创建 解析设备模板 子线程
		for(int i=0;i<filesNum;i++){
			parseFileFunction(XmlFile+"/"+strfiles[i]);			
		//	Log.e("parseThread>>文件：",strfiles[i]);
		}			
	}

	
	public void parseFileFunction(String fileName){
		String filename = fileName;
		
		// TODO Auto-generated method stub
		parseEquiptXml pXml = new parseEquiptXml(filename); //解析设备模板						
		try {
				xmlDataModel.hm_xmlDataModel.put(Integer.valueOf(pXml.equiptCfg.EquipTemplateId), pXml.equiptCfg);
		//		Thread.sleep(100); //100ms
		//		TestParsePrintf(pXml.equiptCfg); //打印解析的数据
		} catch (Exception e) {			
				Log.e("parseThread>>parseFileThread>>>", "put 配置文件数据异常抛出！");
		}
	}
		

//  未使用的   代码专用  测试函数
	public void TestParsePrintf(xml_EquiptCfg equiptCfg){
		Log.e("parseThread>>TestParsePrintf","++++++++++++++++++开始解析设备模板+++++++++++++++++++");
		Log.e("parseThread>>TestParsePrintf>>",equiptCfg.fileName+" "
				 +equiptCfg.EquipTemplateId+" "
				 +equiptCfg.EquipTemplateName+" "
				 +equiptCfg.EquipTemplateType+" ");
		Log.e("parseThread>>TestParsePrintf","++++++++++++++++++设备模板解析完毕+++++++++++++++++++");
		Log.e("parseThread>>TestParsePrintf","=================遍历查看信号==============");
		Iterator<String> ite = equiptCfg.xml_signalCfg_lst.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			xml_signalCfg sigcfg = equiptCfg.xml_signalCfg_lst.get(key);
			Log.e("parseThread>>TestParsePrintf>>>信号：",sigcfg.SignalId+" "
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
				Log.e("parseThread>>TestParsePrintf>>>信号：meaning:",key1+" "
					 +value1+" ");
				}
			}
		}
		Log.e("parseThread>>TestParsePrintf","=================查看信号结束==============");
		
		Log.e("parseThread>>TestParsePrintf","=================遍历查看告警==============");
		Iterator<String> ite10 = equiptCfg.xml_eventCfg_lst.keySet().iterator();
		while(ite10.hasNext()){
			String key = ite10.next();
			xml_eventCfg eventcfg = equiptCfg.xml_eventCfg_lst.get(key);
			Log.e("parseThread>>TestParsePrintf>>告警：",eventcfg.EventId+" "
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
				Log.e("parseThread>>TestParsePrintf>>告警EventCondition：",Condition.ConditionId+" "
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
		Log.e("parseThread>>TestParsePrintf","=================查看信号结束==============");
		
		Log.e("parseThread>>TestParsePrintf","=================遍历查看控制==============");
		Iterator<String> ite20 = equiptCfg.xml_cmdCfg_lst.keySet().iterator();
		while(ite20.hasNext()){
			String key = ite20.next();
			xml_cmdCfg cmdcfg = equiptCfg.xml_cmdCfg_lst.get(key);
			Log.e("parseThread>>TestParsePrintf>>控制：",cmdcfg.CommandId+" "
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
					Log.e("parseThread>>TestParsePrintf>>控制CommandParameter：",Parameter.ParameterId+" "
						 +Parameter.ParameterName+" "
						 +Parameter.UIControlType+" "
						 +Parameter.DataType+" ");
					if(Parameter.CommandMeaninglst.size()>0){
						Iterator<String> iter2 = Parameter.CommandMeaninglst.keySet().iterator();
						while(iter2.hasNext()){
							String key2 = iter2.next();
							String value2 = Parameter.CommandMeaninglst.get(key2);
							Log.e("parseThread>>TestParsePrintf>>控制CommandParameter：meaning:",key2+" "
									 +value2+" ");
							}
					}
				}
			}
		}
		Log.e("parseThread>>TestParsePrintf","=================查看控制结束==============");
				
	}

}
