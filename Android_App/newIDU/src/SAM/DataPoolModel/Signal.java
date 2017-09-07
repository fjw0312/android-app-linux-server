package SAM.DataPoolModel;

/**signal 实时信号数据类*/
public class Signal {

	public int equiptId;      //设备id
	public int sigId;         //信号id
	public String name = "";  //信号名称
	public String value = ""; //信号值
	public String meaning = ""; //信号含义
	public long readtime = 0;	//采集时间
	public String unit = "";    //数值单位
	public int invalid = 0;     //是否有效
	public int type = 0;        //数值类型
	public int severity = 0;    //告警等级
	public int precision = 2;
	public String description = "";

}
