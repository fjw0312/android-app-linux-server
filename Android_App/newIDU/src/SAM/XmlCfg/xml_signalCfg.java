package SAM.XmlCfg;

import java.util.HashMap;

//�豸ģ���ļ��� �ź�������
public class xml_signalCfg {

	public xml_signalCfg() {
		// TODO Auto-generated constructor stub
		SignalMeaninglst = new HashMap<String, String>();
	}
	
	public String SignalId;              //�ź�id
	public String SignalName;            //�ź���
	public String SignalBaseId;          //�źŻ�׼�豸id -1��Ĭ�ϸ��豸
	public String SignalType;            //�ź����� 1��������    0��ģ����
	public String ChannelNo;             //�ź�ͨ��
	public String Expression;         //�źű��ʽ
	public String ShowPrecision;      //���ݵ���ЧС����λ��
	public String Unit;               //��λ
	public String Enable;             //�ź�ʹ��
	
	public float value;              //�ź�ֵ
	
	public HashMap<String, String> SignalMeaninglst = null;  //�źſ����� ����map����

}
