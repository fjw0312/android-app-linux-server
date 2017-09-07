package data.extraHisModel;  

import android.util.Log;

// ��ʷ �澯�ź� ����������   
//��ʾһ�еı��ر������ʷ�澯���� 
public class HisEvent {
	 
	public String start_time="";   //�澯ʱ��        0 
	public String finish_time="";   //����ʱ��     1	
	public String equip_name=""; //�豸��                2
	public String equipid="";    //�豸id	  3
	public String event_name="";  //�澯��	  4
//	public String sig_name="";    //�ź���           
//	public String sigid="";      //�ź�id     
	public String event_id="";    //�澯id    5
	public String severity="";   //�����澯�ȼ�  8
	public String value="";       //�ź�ֵ         11	
	public String event_mean="";  //�澯����     12
	
	public String timelaterStr = ""; //����ʱ�������ַ���
	
	//������ַ������豸����
	public boolean set_equiptName(String name){
		equip_name = name;
		return true;
	}
	//������ַ����ĸ澯��
	public boolean set_eventName(String name){
		event_name = name;
		return true;
	}

		
	//��һ���ַ���ת��Ϊ����ĳ�Ա �ַ�������
	public boolean read_string(String buf){
		
		String[] a  = new String[100];
		
		a = buf.split(",");
		timelaterStr = buf.substring(40);
//		Log.e("�����鳤��2��", Integer.toString(a.length));
//		for(int i=0;i<a.length;i++){			
//			Log.e("��ȡ��ʷ�澯�����ݣ�"+String.valueOf(i)+":", a[i]);
//		}
		if(a.length != 13){
			return false;
		}
		
		try{
		start_time = a[0];   //�澯ʱ��        0
		finish_time = a[1];  //����ʱ��     1	
		equip_name = a[2];//�豸��              2  (���Ĵ���)
		equipid = a[3];    //�豸id	  3
		event_name = a[4]; //�澯��	  4   (���Ĵ���)
		event_id = a[5];    //�澯id    5
		severity = a[8];   //�����澯�ȼ�  8
		value = a[11];      //�ź�ֵ         11	
		event_mean = a[12];  //�澯����     12
		//sig_name = "";    //�ź���         
	//	Log.e("read_string",equipid+equip_name+sigid+name+value+unit+value_type
	//			+is_invalid+severity+freshtime);
		
		
		return true;
		}catch(Exception e){
			Log.e("HisEvent>>read_string>>","�������쳣�׳���");	
		}
		return false;
	}
		
}
