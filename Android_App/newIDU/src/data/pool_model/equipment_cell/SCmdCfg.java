package data.pool_model.equipment_cell;

import java.util.ArrayList;
import java.util.List;

/**SCmd 控制配置类*/
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
	public List<CmdParameaningCfg> cPara = null;  //控制参数含义信息
	//fjw notice:根据测试 如果搞报警配置 为遥调配置时，读到的cPara = null；//2016.9.28
}
