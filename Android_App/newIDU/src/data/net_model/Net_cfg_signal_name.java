package data.net_model;

//Attention: signalid 和 eventid 不可同为0.
public class Net_cfg_signal_name
{
	public int equipid = 0;
	public int signalid = 0;  // 大于 0 表示设置信号名
	public int eventid = 0;   // 大于 0 表示设置告警名
	public String name;
}
