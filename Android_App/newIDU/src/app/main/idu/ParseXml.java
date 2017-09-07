package app.main.idu;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import UIs.Ks_AlarmEdgeLabel;
import UIs.Ks_AlarmMark;
import UIs.Ks_AlarmSet;
import UIs.Ks_AllAlarmLight;
import UIs.Ks_Button;
import UIs.Ks_ButtonUpDown;
import UIs.Ks_ChangeEmailFile;
import UIs.Ks_ChangePhoneFile;
import UIs.Ks_DashBoard;
import UIs.Ks_EventLabel;
import UIs.Ks_EventList;
import UIs.Ks_Form;
import UIs.Ks_Image;
import UIs.Ks_ImageChange;
import UIs.Ks_Label;
import UIs.Ks_Line;
import UIs.Ks_Oval;
import UIs.Ks_PieChart;
import UIs.Ks_Rectangle;
import UIs.Ks_SignalList;
import UIs.Ks_Table;
import UIs.Ks_TextClock;
import UIs.Ks_YKParameter;
import UIs.Ks_YTParameter;
import UIs.Ks_zHisBarChart_EventDay;
import UIs.Ks_zHisBarChart_EventMon;
import UIs.Ks_zHisBarChart_EventYear;
import UIs.Ks_zHisBarChart_PueDay;
import UIs.Ks_zHisBarChart_PueMon;
import UIs.Ks_zHisBarChart_PueYear;
import UIs.Ks_zHisBarChart_RcDay;
import UIs.Ks_zHisBarChart_RcMon;
import UIs.Ks_zHisBarChart_RcYear;
import UIs.Ks_zHisBar_aNH;
import UIs.Ks_zHisEventList;
import UIs.Ks_zHisPueLine;
import UIs.Ks_zHisSigLine;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

//����xml�ļ���---ҳ���ļ�
public class ParseXml{
	public ParseXml(Context context) {
		pcontext = context;
		// TODO Auto-generated constructor stub
	}
	
	Context pcontext;
	File file = null;  //�ļ���
	InputStream fis = null;  //��ȡ�ֽ��� 
	HashMap<String,VObject> p_mapUis = new HashMap<String,VObject>();//�ý����ļ��Ŀؼ�������
	String strFileName = "";

	
	//�ж��ļ����ڿɶ���
	public boolean  hasFileXml(String filename){
		file = new File(filename);
		if(!file.exists()){ //�ж��ļ�/Ŀ¼�Ƿ���� �������½� 
//			Toast.makeText(pcontext, filename+"·���ļ�������!", Toast.LENGTH_LONG).show();
//			new AlertDialog.Builder(pcontext).setTitle("����") .setMessage(filename+"·���ļ�������!") .show();
			Log.e("·���ļ�","������");
			return false;
		}		
		if(!file.canRead()){ //�ж��ļ��Ƿ�ɶ�
			Log.e("·���ļ�","���ɶ�");  
			return false;
		}
		strFileName = filename;
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
		fis = null;
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
				//	Log.i("ParseXml->parseStream>>>strID", strID);
					//�жϽڵ���ʲôview�ؼ��� ��ȡ�ؼ���
					if(!newUisAddList(strID,strType)){
						Toast.makeText(pcontext, strType+" �ÿؼ���֧�֣�", Toast.LENGTH_LONG).show();
						Log.e("ParseXml->parseStream","û��⵽֧�ֵĿؼ���"+strType);
						event = pullParser.next();
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
					currentObj.setProperties(strName, strValue, strFileName);//���ÿؼ���ز���				
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
	   if("Label".equals(type)){               //����һ��֧�ֵĿؼ�ʵ������ӵ�ҳ��ؼ�������
			Ks_Label views = new Ks_Label(pcontext);
			p_mapUis.put(id,views);
		}else if("Form".equals(type)){      
			Ks_Form views = new Ks_Form(pcontext);	
			p_mapUis.put(id,views);
		}else if("StraightLine".equals(type)){
			Ks_Line views = new Ks_Line(pcontext);
			p_mapUis.put(id,views);
		}else if("Rectangle".equals(type)){
			Ks_Rectangle views = new Ks_Rectangle(pcontext);
			p_mapUis.put(id,views);
		}else if("Ellipse".equals(type)){
			Ks_Oval views = new Ks_Oval(pcontext);
			p_mapUis.put(id,views);
		}else if("Table".equals(type)){
			Ks_Table views = new Ks_Table(pcontext);
			p_mapUis.put(id,views);
		}else if("Image".equals(type)){
			Ks_Image views = new Ks_Image(pcontext);
			p_mapUis.put(id,views);
		}else if("Button".equals(type)){
			Ks_Button views = new Ks_Button(pcontext);
			p_mapUis.put(id,views);
		}else if("TextClock".equals(type)){
			Ks_TextClock views = new Ks_TextClock(pcontext);
			p_mapUis.put(id,views);
		}else if("SignalList".equals(type)){
			Ks_SignalList views = new Ks_SignalList(pcontext);
			p_mapUis.put(id,views);
		}else if("EventList".equals(type)){
			Ks_EventList views = new Ks_EventList(pcontext); 
			p_mapUis.put(id,views);
		}else if("YTParameter".equals(type)){
			Ks_YTParameter views = new Ks_YTParameter(pcontext);
			p_mapUis.put(id,views);
		}else if("YKParameter".equals(type)){
			Ks_YKParameter views = new Ks_YKParameter(pcontext);	
			p_mapUis.put(id,views);
		}else if("EventConditionStartSetter".equals(type)){
			Ks_AlarmSet views = new Ks_AlarmSet(pcontext);	 
			p_mapUis.put(id,views);
		}else if("DoubleImageButton".equals(type)){
			Ks_AlarmMark views = new Ks_AlarmMark(pcontext);	 
			p_mapUis.put(id,views);
		}else if("tigerLabel".equals(type)){
			Ks_AlarmEdgeLabel views = new Ks_AlarmEdgeLabel(pcontext);	 
			p_mapUis.put(id,views);
		}else if("Image_change".equals(type)){
			Ks_ImageChange views = new Ks_ImageChange(pcontext);	 
			p_mapUis.put(id,views);
		}else if("multi_data".equals(type)){
			Ks_PieChart views = new Ks_PieChart(pcontext);	 
			p_mapUis.put(id,views);	
		}else if("Dial_A".equals(type)){
			Ks_DashBoard views = new Ks_DashBoard(pcontext);	 
			p_mapUis.put(id,views);	
		}else if("ELabel".equals(type)){
			Ks_EventLabel views = new Ks_EventLabel(pcontext);	 
			p_mapUis.put(id,views);	
		}else if("SeeImage".equals(type)){
			Ks_AllAlarmLight views = new Ks_AllAlarmLight(pcontext);	 
			p_mapUis.put(id,views);
		}else if("ButtonUpDown".equals(type)){
			Ks_ButtonUpDown views = new Ks_ButtonUpDown(pcontext);	 
			p_mapUis.put(id,views);
		}else if("ChangePhoneFile".equals(type)){
			Ks_ChangePhoneFile views = new Ks_ChangePhoneFile(pcontext);	 
			p_mapUis.put(id,views); 
		}else if("ChangeEmailFile".equals(type)){ 
			Ks_ChangeEmailFile views = new Ks_ChangeEmailFile(pcontext);	 
			p_mapUis.put(id,views); 
		}else if("HisEvent".equals(type)){ 
			Ks_zHisEventList views = new Ks_zHisEventList(pcontext);	 
			p_mapUis.put(id,views);
		}else if("SignalCurves".equals(type)){ 
			Ks_zHisSigLine views = new Ks_zHisSigLine(pcontext);	 
			p_mapUis.put(id,views);
		}else if("AutoSigList".equals(type)){  
			Ks_zHisPueLine views = new Ks_zHisPueLine(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_Mon_NH".equals(type)){  //��Ч�ֲ�ͼ 
			Ks_zHisBar_aNH views = new Ks_zHisBar_aNH(pcontext);	 
			p_mapUis.put(id,views);
		}else if("MultiChart".equals(type)){          //��Ч�ֲ�ͼ 
			Ks_zHisBar_aNH views = new Ks_zHisBar_aNH(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_Year".equals(type)){   
			Ks_zHisBarChart_RcYear views = new Ks_zHisBarChart_RcYear(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_Mon".equals(type)){   
			Ks_zHisBarChart_RcMon views = new Ks_zHisBarChart_RcMon(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_Day".equals(type)){   
			Ks_zHisBarChart_RcDay views = new Ks_zHisBarChart_RcDay(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_PueYear".equals(type)){   
			Ks_zHisBarChart_PueYear views = new Ks_zHisBarChart_PueYear(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_PueMon".equals(type)){   
			Ks_zHisBarChart_PueMon views = new Ks_zHisBarChart_PueMon(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_PueDay".equals(type)){   
			Ks_zHisBarChart_PueDay views = new Ks_zHisBarChart_PueDay(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_HisEvent_Day".equals(type)){   
			Ks_zHisBarChart_EventDay views = new Ks_zHisBarChart_EventDay(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_HisEvent_Mon".equals(type)){   
			Ks_zHisBarChart_EventMon views = new Ks_zHisBarChart_EventMon(pcontext);	 
			p_mapUis.put(id,views);
		}else if("HisBarChart_HisEvent_Year".equals(type)){   
			Ks_zHisBarChart_EventYear views = new Ks_zHisBarChart_EventYear(pcontext);	 
			p_mapUis.put(id,views);
		}else{
			return false;
		}

		return true;
	}

}
