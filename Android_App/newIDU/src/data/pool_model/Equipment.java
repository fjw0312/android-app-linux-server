package data.pool_model;

import java.util.Hashtable;
import data.pool_model.equipment_cell.EventCfg;
import data.pool_model.equipment_cell.SCmdCfg;
import data.pool_model.equipment_cell.SignalCfg;
import data.pool_model.equipment_cell.TiggerCfg;



/**�豸 ע��������Ϣ��  ΪӦ�ü�ص��豸������Ϣ���Ԫ ��λ-��Ҫ���������Ϣ*/
//��4�����������  signalCfg EventCfg TiggerCfg CmdCfg
public class Equipment {

	public Equipment() {
		// TODO Auto-generated constructor stub
		htSignalCfg = new Hashtable<Integer, SignalCfg>();
		htEventCfg = new Hashtable<Integer, EventCfg>();
		htTiggerCfg = new Hashtable<Integer, TiggerCfg>();
		htCmdCfg = new Hashtable<Integer, SCmdCfg>();
	}
	
	public int equipId;        //�豸id
	public int equipTempId;    //�豸���id
	public String equipCategory = "";  //�豸
	public String equipName = "";      //�豸����
	public String equipXmlfile = "";   //�豸�ļ�·����
	
	//�ź�����
	public Hashtable<Integer, SignalCfg> htSignalCfg = null;  // <�ź�ID�� �ź�������> �洢�ź�����
	
	// �澯����
	public Hashtable<Integer, EventCfg> htEventCfg = null;    // <�澯ID�� �澯������> �洢�澯����
			
	// ����������
	public Hashtable<Integer, TiggerCfg> htTiggerCfg = null;    // <�澯ID�� ������������> �洢����������
			
	// ��������  
	public Hashtable<Integer, SCmdCfg> htCmdCfg = null;          // <����ID�� ����������> �洢��������

}
