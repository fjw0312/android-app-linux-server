package data.net_model;

public class Os_active_signal {
	public int equipid;
	public int sigid;
	public long freshtime;
	public int is_invalid; //0 --> valid, 1 --> invalid
	public int value_type;
	public String value;
	public int severity; //关联告警等级
	public String name;
	public String unit;
}
