package data.pool_model.equipment_cell;

import java.util.Hashtable;

/**�豸��������������Ϣ-��������*/ 
public class TiggerCfg {
	/**�豸�澯����Ϣ-��������*/
	public TiggerCfg() {
		// TODO Auto-generated constructor stub
		tTiggerConditions_ht = new Hashtable<Integer, TiggerConditionCfg>();
	}
	// ��ʾһ���澯����������
		public class TiggerConditionCfg
		{
			public int    conditionid;
			public int    severity;
			public float  startcompare;
			public float  endcompare;
//			public int    startdelay;
//			public int    enddelay;
//			public String  meaning;
			public int   mark;
			 // mark��ǣ�setʱ��ʾ����һ��< 
			//0ֵ startvalue �� stopvalue ����Ҫ���ã� 
			//1ֵ������startvalue�� 2ֵ������stopvalue, 3�澯ʹ�ܡ�>;
		}
	
			
		public int     tTiggerId;
		public String  tName;
		public int     tTiggerEnabled;        // 1 ���� 0 ͣ��
//		public int     tTiggerSignalid;
		public Hashtable<Integer, TiggerConditionCfg> tTiggerConditions_ht = null;  // <����ID�� ����>  ��������

}
