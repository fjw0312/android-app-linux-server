package SAM.extraHisModel;

import android.util.Log;

//���ڱ���eg: �ܵ���-�յ����ܣ��������ܣ����ڼ���PUE����  �ܺķֲ� �õ��� ��
/** ��ʷ ���ݱ��ʽ �� eg:8-2*/
public class HisFormula {

	public String getTime = ""; // ����ʽ�� ��ȡʱ��
	public String strContent = ""; //����ʽ�� ��ȡ����
	public String strTime = ""; //�ɼ�ʱ��  "yyyy.MM.dd HH:mm:ss" 
	public String strEndContent = "";//������� ʱ ʽ�� ����
	
	public HisFormula() {
		// TODO Auto-generated constructor stub
	}
	
	// add signal value
	public void add_string(String str){ 
		if("".equals(str)) return;
		if("".equals(strContent)){
			strContent = str;
		}else{
			strContent = strContent + "&" + str;	
		}	
	}
	
	//�������Աת��Ϊһ���ַ���  ���������� ����
	public String to_string(){
		String buf = "";
		String a1 = strContent + "`";
		String a2 = getTime + "`";
		String a3 = strTime + "`";
		
		buf = a1+a2+a3;
		return buf;
	}
	//��һ���ַ���ת��Ϊ����ĳ�Ա �ַ�������
	public boolean read_string(String buf){
		String[] a  = new String[100];
		
		a = buf.split("`");
//		Log.w("�����鳤��2��", Integer.toString(a.length));
		if(a.length < 1){
			return false;
		}
		try{
		strContent = a[0];    //����
		getTime = a[1];       //ʱ��
		strTime = a[2];       //ʱ��
		}catch(Exception e){
			Log.e("HisFormula>>read_string>>","�������쳣�׳���");  
			return false;
		}
		try{
			strEndContent = a[3]; 
		}catch(Exception e){
//			Log.e("HisFormula>>read_string>>","�������쳣�׳���");	
		}

		return true;
	}

}
