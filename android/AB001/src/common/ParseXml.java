package common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import UIs.f_Button;
import UIs.f_Form;
import UIs.f_Label;
import UIs.f_RC_Label;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.ViewGroup;
import android.widget.Toast;

//解析xml文件类
public class ParseXml{
	public ParseXml(Context context) {
		pcontext = context;
		// TODO Auto-generated constructor stub
	}
	
	Context pcontext;
	File file = null;  //文件类
	InputStream fis = null;  //读取字节流
	HashMap<String,VObject> p_mapUis = new HashMap<String,VObject>();//该解析文件的控件类链表

	
	//判断文件存在可读性
	public boolean  hasFileXml(String filename){
		file = new File(filename);
		if(!file.exists()){ //判断文件/目录是否存在 不存在新建
			Log.e("路劲文件","不存在");
			return false;
		}		
		if(!file.canRead()){ //判断文件是否可读
			Log.e("路劲文件","不可读");  
			return false;
		}
		return true;		
	}
	
	//获取xml文件输入字节流
	public HashMap<String,VObject> getXmlStream(String filename) throws FileNotFoundException{
		if( !hasFileXml(filename) ) return null;
		//获取字节流
		fis = new BufferedInputStream(new FileInputStream(file));
		if(fis==null){
			Log.e("ParseXml->getXmlStream","打开文件无字节流！");
			return null;
		}
		//解析Xml字节流
		try{
			parseStream(fis);		
			fis.close();
		}catch(Exception e){
			Log.e("ParseXml->getXmlStream","解析字节流异常抛出！");
		}
		//返回解析出来的控件链表
		return p_mapUis;
	}
	//解析Xml的每个节点函数
	protected void parseStream(InputStream inStream) throws Exception{
//		Log.e("ParseXml->parseStream","into!");

		VObject currentObj = null;
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
				if("Element".equals(name)){
					String strID = pullParser.getAttributeValue("", "ID");//获取节点id
					String strType = pullParser.getAttributeValue("", "Type");//获取节点值
					
					//判断节点是什么view控件并 提取控件类
					if(!newUisAddList(strID,strType)){
						Toast.makeText(pcontext, strType+" 该控件不支持！", Toast.LENGTH_LONG).show();
						Log.e("ParseXml->parseStream","没检测到支持的控件："+strType);
						continue;
					}
					//根据id提取出当前正在解析的控件类
					currentObj = p_mapUis.get(strID);
					//设置当前的控件 id type
					currentObj.setViewsID(strID);
					currentObj.setViewsType(strType);
					
				}else if("Property".equals(name)){		
					String strName = pullParser.getAttributeValue("", "Name");
					String strValue = pullParser.getAttributeValue("", "Value");
					currentObj.setProperties(strName, strValue);//设置控件相关参数				
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
	//将xml文件的控件实例化添加到链表
	private boolean newUisAddList(String id, String type){
		if("Form".equals(type)){       //将各一种支持的控件实例化添加到页面控件类链表
			f_Form views = new f_Form(pcontext);	
			p_mapUis.put(id,views);
		}else if("Label".equals(type)){
			f_Label views = new f_Label(pcontext);	
			p_mapUis.put(id,views);
		}else if("RC_Label".equals(type)){
			f_RC_Label views = new f_RC_Label(pcontext);	
			p_mapUis.put(id,views);
		}else if("Button".equals(type)){
			f_Button views = new f_Button(pcontext);	
			p_mapUis.put(id,views);	
		}else{
			return false;
		}

		return true;
	}

}
