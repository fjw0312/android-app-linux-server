package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;


//�豸ģ���ļ��� �澯������
public class xml_cmdCfg {

	@SuppressLint("UseSparseArrays")
	public xml_cmdCfg() {
		// TODO Auto-generated constructor stub
		CommandParameterlst = new HashMap<String, CommandParameter>();
	}
	
	public class CommandParameter{
		public CommandParameter(){
			CommandMeaninglst = new HashMap<String, String>();
		}
		public String ParameterId;          //��������id
		public String ParameterName;     //��������
		public String UIControlType;        //������������
		public String DataType;             //����������������
		
		//�������� �������ݼ���������
		public HashMap<String, String> CommandMeaninglst = null;
	}
	
	public String CommandId;              //����id
	public String CommandName;         //��������
	public String CommandBaseId;          //���ƻ�׼id Ĭ��-1
	public String CommandType;            //��������     
	public String CommandToken;        //���Ʊ�ʶ��
	public String Retry;                  //���Ʒ��ʹ���
	public String Enable;              //����ʹ��
	
	//�������ݺ�������
	public HashMap<String, CommandParameter> CommandParameterlst = null; 

}
