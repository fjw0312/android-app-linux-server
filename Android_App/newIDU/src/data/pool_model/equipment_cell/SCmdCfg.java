package data.pool_model.equipment_cell;

import java.util.ArrayList;
import java.util.List;

/**SCmd ����������*/
public class SCmdCfg {
	
	public class CmdParameaningCfg
	{
		public int id;
		public int value;
		public String meaning;
	}
	
	public SCmdCfg() {
		// TODO Auto-generated constructor stub
		cPara = new ArrayList<CmdParameaningCfg>();
	}

	public int cId;
	public float cMaxValue;
	public float cMinValue;
	public String cName;
	public String cUnit;
	public List<CmdParameaningCfg> cPara = null;  //���Ʋ���������Ϣ
	//fjw notice:���ݲ��� ����㱨������ Ϊң������ʱ��������cPara = null��//2016.9.28
}
