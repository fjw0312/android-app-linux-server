package SAM.XmlCfg;

import java.util.HashMap;

//设备模板文件的 信号配置类
public class xml_signalCfg {

	public xml_signalCfg() {
		// TODO Auto-generated constructor stub
		SignalMeaninglst = new HashMap<String, String>();
	}
	
	public String SignalId;              //信号id
	public String SignalName;            //信号名
	public String SignalBaseId;          //信号基准设备id -1：默认该设备
	public String SignalType;            //信号类型 1：开关量    0：模拟量
	public String ChannelNo;             //信号通道
	public String Expression;         //信号表达式
	public String ShowPrecision;      //数据的有效小数点位数
	public String Unit;               //单位
	public String Enable;             //信号使能
	
	public float value;              //信号值
	
	public HashMap<String, String> SignalMeaninglst = null;  //信号开关量 含义map链表

}
