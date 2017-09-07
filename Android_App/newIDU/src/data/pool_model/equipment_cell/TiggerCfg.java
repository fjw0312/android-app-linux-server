package data.pool_model.equipment_cell;

import java.util.Hashtable;

/**设备报警包配置类信息-条件子类*/ 
public class TiggerCfg {
	/**设备告警类信息-条件子类*/
	public TiggerCfg() {
		// TODO Auto-generated constructor stub
		tTiggerConditions_ht = new Hashtable<Integer, TiggerConditionCfg>();
	}
	// 表示一个告警配置条件项
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
			 // mark标记，set时表示设哪一个< 
			//0值 startvalue 和 stopvalue 都需要设置， 
			//1值仅设置startvalue， 2值仅设置stopvalue, 3告警使能。>;
		}
	
			
		public int     tTiggerId;
		public String  tName;
		public int     tTiggerEnabled;        // 1 启用 0 停用
//		public int     tTiggerSignalid;
		public Hashtable<Integer, TiggerConditionCfg> tTiggerConditions_ht = null;  // <条件ID， 条件>  储存条件

}
