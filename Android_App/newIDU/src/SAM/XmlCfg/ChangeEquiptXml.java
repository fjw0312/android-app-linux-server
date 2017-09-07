package SAM.XmlCfg;

import utils.JDOM_Xml;
import android.util.Log;


// 修改  xml 设备配置文件的入口线程   -》JDOM_Xml
public class ChangeEquiptXml extends Thread{

	public ChangeEquiptXml(String strFile) {
		// TODO Auto-generated constructor stub
		fileName = strFile;
	}
	public int mode = 0;
	public String strAttId1 ="";
	public String strAttIdValue1 = "";
	public String strAttId2 = "";
	public String strAttIdValue2 = "";
	public String strAttribute = "";
	public String newValue = "";
	public String fileName = "";		
	public void setArgv(int i_mode, String i_strAttId1, String i_strAttIdValue1, String i_strAttId2,
			String i_strAttIdValue2,String  i_strAttribute,String i_newValue){	
		mode = i_mode;
		strAttId1 = i_strAttId1;
		strAttIdValue1 = i_strAttIdValue1;
		strAttId2 = i_strAttId2; 
		strAttIdValue2 = i_strAttIdValue2;
		strAttribute = i_strAttribute;
		newValue = i_newValue;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try{
			JDOM_Xml jdom_xml = new JDOM_Xml(fileName);
			if(mode>10){
				jdom_xml.change_SECXXXX(mode, strAttId1, strAttIdValue1, strAttId2, strAttIdValue2, strAttribute, newValue);
			}else{
				jdom_xml.change_EquipXXXX(mode, strAttId1, strAttIdValue1, strAttribute, newValue);
			}
		}catch(Exception e){
			Log.e("xmlDataModel->ChangeEquiptXml","异常抛出！");
		}
	}

}
