package data.pool_model;

/**Event 实时告警信息类*/
public class Event {

	public Event() {
		// TODO Auto-generated constructor stub
	}
	
	public int equipId;        //设备id
	public int  eventId;          //告警id
	public String name;     // 告警名称
	public String meaning;  //告警含义
	public int grade;       // 告警级别
	public long starttime;  // 告警开始时间
	public long stoptime;   // 告警结束时间
	public int is_active;   //告警是否存在
	public int value;    // 触发值   未获取
	public String equipName;  //设备名称
	
}
