package data.net_model;

public class Net_history_signal {
	public int equipid;
	public int sigid;
	public String name;
	public String history_time;
	public int sig_type; //0 --> valid, 1 --> invalid
	public int value_type;
	public String value;
	public int severity; //关联告警等级
	public String unit;
}
