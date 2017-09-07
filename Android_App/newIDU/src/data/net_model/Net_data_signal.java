package data.net_model;

public class Net_data_signal {
	public int equipid;
	public int sigid;
	public int freshtime;
	public int is_invalid; //0 --> valid, 1 --> invalid
	public int value_type;
	public String value;
	public int severity; //关联告警等级
	public String meaning;
}
