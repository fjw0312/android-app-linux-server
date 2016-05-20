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

//����xml�ļ���
public class ParseXml{
	public ParseXml(Context context) {
		pcontext = context;
		// TODO Auto-generated constructor stub
	}
	
	Context pcontext;
	File file = null;  //�ļ���
	InputStream fis = null;  //��ȡ�ֽ���
	HashMap<String,VObject> p_mapUis = new HashMap<String,VObject>();//�ý����ļ��Ŀؼ�������

	
	//�ж��ļ����ڿɶ���
	public boolean  hasFileXml(String filename){
		file = new File(filename);
		if(!file.exists()){ //�ж��ļ�/Ŀ¼�Ƿ���� �������½�
			Log.e("·���ļ�","������");
			return false;
		}		
		if(!file.canRead()){ //�ж��ļ��Ƿ�ɶ�
			Log.e("·���ļ�","���ɶ�");  
			return false;
		}
		return true;		
	}
	
	//��ȡxml�ļ������ֽ���
	public HashMap<String,VObject> getXmlStream(String filename) throws FileNotFoundException{
		if( !hasFileXml(filename) ) return null;
		//��ȡ�ֽ���
		fis = new BufferedInputStream(new FileInputStream(file));
		if(fis==null){
			Log.e("ParseXml->getXmlStream","���ļ����ֽ�����");
			return null;
		}
		//����Xml�ֽ���
		try{
			parseStream(fis);		
			fis.close();
		}catch(Exception e){
			Log.e("ParseXml->getXmlStream","�����ֽ����쳣�׳���");
		}
		//���ؽ��������Ŀؼ�����
		return p_mapUis;
	}
	//����Xml��ÿ���ڵ㺯��
	protected void parseStream(InputStream inStream) throws Exception{
//		Log.e("ParseXml->parseStream","into!");

		VObject currentObj = null;
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
				if("Element".equals(name)){
					String strID = pullParser.getAttributeValue("", "ID");//��ȡ�ڵ�id
					String strType = pullParser.getAttributeValue("", "Type");//��ȡ�ڵ�ֵ
					
					//�жϽڵ���ʲôview�ؼ��� ��ȡ�ؼ���
					if(!newUisAddList(strID,strType)){
						Toast.makeText(pcontext, strType+" �ÿؼ���֧�֣�", Toast.LENGTH_LONG).show();
						Log.e("ParseXml->parseStream","û��⵽֧�ֵĿؼ���"+strType);
						continue;
					}
					//����id��ȡ����ǰ���ڽ����Ŀؼ���
					currentObj = p_mapUis.get(strID);
					//���õ�ǰ�Ŀؼ� id type
					currentObj.setViewsID(strID);
					currentObj.setViewsType(strType);
					
				}else if("Property".equals(name)){		
					String strName = pullParser.getAttributeValue("", "Name");
					String strValue = pullParser.getAttributeValue("", "Value");
					currentObj.setProperties(strName, strValue);//���ÿؼ���ز���				
				}
				break;
			//c.�ڵ��β
			case XmlPullParser.END_TAG:
				break;
			}	
		//5.ָ����λ��һ���ڵ�
			event = pullParser.next();
		}		
	}
	//��xml�ļ��Ŀؼ�ʵ������ӵ�����
	private boolean newUisAddList(String id, String type){
		if("Form".equals(type)){       //����һ��֧�ֵĿؼ�ʵ������ӵ�ҳ��ؼ�������
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
