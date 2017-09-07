package data.pool_model;

import java.util.Hashtable;
import data.pool_model.equipment_cell.EventCfg;
import data.pool_model.equipment_cell.SCmdCfg;
import data.pool_model.equipment_cell.SignalCfg;
import data.pool_model.equipment_cell.TiggerCfg;



/**设备 注册配置信息类  为应用监控的设备配置信息最大单元 单位-主要存放配置信息*/
//由4个子类链组成  signalCfg EventCfg TiggerCfg CmdCfg
public class Equipment {

	public Equipment() {
		// TODO Auto-generated constructor stub
		htSignalCfg = new Hashtable<Integer, SignalCfg>();
		htEventCfg = new Hashtable<Integer, EventCfg>();
		htTiggerCfg = new Hashtable<Integer, TiggerCfg>();
		htCmdCfg = new Hashtable<Integer, SCmdCfg>();
	}
	
	public int equipId;        //设备id
	public int equipTempId;    //设备编号id
	public String equipCategory = "";  //设备
	public String equipName = "";      //设备名称
	public String equipXmlfile = "";   //设备文件路径名
	
	//信号配置
	public Hashtable<Integer, SignalCfg> htSignalCfg = null;  // <信号ID， 信号配置类> 存储信号配置
	
	// 告警配置
	public Hashtable<Integer, EventCfg> htEventCfg = null;    // <告警ID， 告警配置类> 存储告警配置
			
	// 报警包配置
	public Hashtable<Integer, TiggerCfg> htTiggerCfg = null;    // <告警ID， 报警包配置类> 存储报警包配置
			
	// 控制配置  
	public Hashtable<Integer, SCmdCfg> htCmdCfg = null;          // <控制ID， 控制配置类> 存储控制配置

}
