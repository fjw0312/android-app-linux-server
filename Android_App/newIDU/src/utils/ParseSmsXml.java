package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

//解析 短息告警配置 类
public class ParseSmsXml {

	//定义短信 告警人信息子类
	public class Person{
		public String name;
		public String phoneNumber;
		public String  enable;
		public String type;
		public String alarm_level;
	}
	
	File file = null;  //文件类
	InputStream fis = null;  //读取字节流
	public HashMap<String, Person> smsPerson_map = new HashMap<String, Person>();
	
	public ParseSmsXml(String filename){
		// TODO Auto-generated constructor stub
		file = new File(filename);
		try{
		parseFlie();
		}catch(Exception e){
			Log.e("ParseSmsXml>>","解析短信告警配置文件异常抛出！");
		}
	}
	public void parseFlie() throws FileNotFoundException{
		//获取字节流
		fis = new BufferedInputStream(new FileInputStream(file));
		if(fis==null){
			Log.e("ParseXml->getXmlStream","打开文件无字节流！");
			return;
		}
		//解析Xml字节流
		try{ 
			parseStream(fis);
			fis.close(); 
		
		}catch(Exception e){
			Log.e("ParseXml->getXmlStream","解析字节流异常抛出！");
		}
		fis = null;
	}
	public void parseStream(InputStream inStream) throws Exception{
		smsPerson_map.clear();
		
		//1.定义xml解析器
		XmlPullParser pullParser = Xml.newPullParser();
		//2.设置解析器参数
		pullParser.setInput(inStream, "UTF-8");
		//3.获取Xml解析事件类型
		int event = pullParser.getEventType();
		//4.判断节点类型  并获取信息
		while(event != XmlPullParser.END_DOCUMENT){
			switch(event){

			//a.文件头
			case XmlPullParser.START_DOCUMENT:
					break;
			//b.节点的头
			case XmlPullParser.START_TAG:
				//获取节点名称
				String name = pullParser.getName();
			//	Log.e("ParseXml->parseStream","XmlPullParser name="+name);
				//判断节点类型
				if("user".equals(name)){
					Person person = new Person();
					person.name = pullParser.getAttributeValue("", "name");
					person.phoneNumber = pullParser.getAttributeValue("", "tel_number");
					person.enable = pullParser.getAttributeValue("", "enable");
					person.type = pullParser.getAttributeValue("", "rule_type");
					smsPerson_map.put(person.name, person);
				}				
				break;
				//c.节点的尾
			case XmlPullParser.END_TAG:
				break;
			}	
		//5.指针移位下一个节点
			event = pullParser.next();
		}	
	}

}
