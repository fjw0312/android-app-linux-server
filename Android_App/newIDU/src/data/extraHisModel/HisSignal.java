package data.extraHisModel;  

import android.util.Log;

//��ʷ�ź� �� ��ʷ�ź� �������� ��
//��ʾһ�еı��ر������ʷ�ź����� 
public class HisSignal {
	
	public String equipid;    //�豸id
	public String equip_name; //�豸��
	public String sigid;      //�ź�id
	public String name;        //�ź���
	public String value;       //�ź�ֵ
	public String mean;        //����
	public String value_type;   //�ź�����
	public String is_invalid;   //0 --> valid, 1 --> invalid
	public String severity;   //�����澯�ȼ�
	public String freshtime = "0";   //��ʷʱ��
	

	//�������Աת��Ϊһ���ַ���
	public String to_string(){
		
		String buf = null;  
		String a1 = equipid + "`";
		String a2 = equip_name + "`";
		String a3 = sigid + "`";
		String a4 = name + "`";
		String a5 = value + "`";
		String a6 = mean + "`";
		String a7 = value_type + "`";
		String a8 = is_invalid + "`";
		String a9 = severity + "`";
		String a10 = freshtime;
		
		buf = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10;
		
		return buf;
	}
	//��һ���ַ���ת��Ϊ����ĳ�Ա �ַ�������
	public boolean read_string(String buf){
		
		String[] a  = new String[100];
		
		a = buf.split("`");
//		Log.w("�����鳤��2��", Integer.toString(a.length));
		if(a.length != 10){
			return false;
		}
		
		try{
		equipid = a[0];    //�豸id
		equip_name = a[1]; //�豸��
		sigid = a[2];      //�ź�id
		name = a[3];        //�ź���
		value = a[4];       //�ź�ֵ
		mean = a[5];        //��λ
		value_type = a[6];   //�ź�����
		is_invalid = a[7];   //0 --> valid, 1 --> invalid
		severity = a[8];   //�����澯�ȼ�
		freshtime = a[9];   //��ʷʱ��
	//	Log.e("read_string",equipid+equip_name+sigid+name+value+unit+value_type
	//			+is_invalid+severity+freshtime);
			
		return true;
		}catch(Exception e){
			Log.e("HisSignal>>read_string>>","�������쳣�׳���");	
		}
		return false;
	}
		
}
