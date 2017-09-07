package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;

//�豸ģ���ļ��� �澯������
public class xml_eventCfg {

	@SuppressLint("UseSparseArrays")
	public xml_eventCfg() {
		// TODO Auto-generated constructor stub
		EventConditionlst = new HashMap<String, EventCondition>();
	}
	
	public class EventCondition{
		public String ConditionId;           //�澯����id    
		public String Meaning;            //�澯����
		public String EventSeverity;         //�澯�ȼ�
		public String StartOperation;     //�澯�ȽϷ���
		public String StartCompareValue;   //�澯��ʼ�Ƚ�ֵ
		public String StartDelay;            //�澯��ʼ��ʱ
		public String EndCompareValue;     //�澯�����Ƚ�ֵ
		public String EndDelay;              //�澯������ʱ
		
		public long  startAlarmTime;   //�Զ�����  �澯��ʼʱ��ı���   �澯��ʧ Ϊ 0
		public int   nowAlarmchangeState; //�澯�ոոı�״̬   �¸澯������1    �澯��ʧ��-1    �澯������2      �޸澯��0
	}
	
	public String EventId;                //�澯id
	public String EventName;           //�澯����
	public String EventBaseId;            //�澯��׼id Ĭ��-1
	public String EventType;              //�澯����
	public String StartExpression;     //�澯����źű��ʽ
	public String Enable;              //�澯ʹ��
	
	public HashMap<String, EventCondition> EventConditionlst = null;  //�źſ����� ����map����

}
