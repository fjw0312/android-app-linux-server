package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;


//�豸ģ���ļ��� �澯������
@SuppressLint("UseSparseArrays")
public class xml_EquiptCfg {

	@SuppressLint("UseSparseArrays")
	public xml_EquiptCfg() {
		// TODO Auto-generated constructor stub
		xml_signalCfg_lst = new HashMap<String, xml_signalCfg>();
		xml_eventCfg_lst = new HashMap<String, xml_eventCfg>();
		xml_cmdCfg_lst = new HashMap<String, xml_cmdCfg>();
		sig_ChannelNo_id_lst = new HashMap<String,String>();
	}
	
	public String EquipTemplateId;        //�豸ģ��id
	public String EquipTemplateName;   //�豸ģ������
	public String EquipTemplateType;      //�豸ģ�����ͺ�
	public String LibName;             //�豸ģ�����õĶ�̬������
	
	public HashMap<String, xml_signalCfg> xml_signalCfg_lst = null; //�ź���������<�ź�id, �ź�������>
	public HashMap<String, xml_eventCfg> xml_eventCfg_lst = null;   //�澯��������<�澯id, �澯������>
	public HashMap<String, xml_cmdCfg> xml_cmdCfg_lst = null;       //������������<����id, ����������>
	
	//xml�豸ģ�� �ļ���
	public String fileName;
	//�ź�ͨ��   ���� ����
	public HashMap<String,String> sig_ChannelNo_id_lst = null; //�ź�ͨ��id  �ź�id

	
}
