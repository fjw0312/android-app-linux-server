package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;

//设备模板文件的 告警配置类
public class xml_eventCfg {

	@SuppressLint("UseSparseArrays")
	public xml_eventCfg() {
		// TODO Auto-generated constructor stub
		EventConditionlst = new HashMap<String, EventCondition>();
	}
	
	public class EventCondition{
		public String ConditionId;           //告警子项id    
		public String Meaning;            //告警含义
		public String EventSeverity;         //告警等级
		public String StartOperation;     //告警比较符号
		public String StartCompareValue;   //告警开始比较值
		public String StartDelay;            //告警开始延时
		public String EndCompareValue;     //告警结束比较值
		public String EndDelay;              //告警结束延时
		
		public long  startAlarmTime;   //自定义存放  告警开始时间的变量   告警消失 为 0
		public int   nowAlarmchangeState; //告警刚刚改变状态   新告警产生：1    告警消失：-1    告警持续：2      无告警：0
	}
	
	public String EventId;                //告警id
	public String EventName;           //告警名称
	public String EventBaseId;            //告警基准id 默认-1
	public String EventType;              //告警类型
	public String StartExpression;     //告警相关信号表达式
	public String Enable;              //告警使能
	
	public HashMap<String, EventCondition> EventConditionlst = null;  //信号开关量 含义map链表

}
