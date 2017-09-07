package data.net_service;


import java.util.ArrayList;
import java.util.List;
import data.net_model.Net_active_event;
import data.net_model.Net_cfg_control;
import data.net_model.Net_cfg_ctrl_parameaning;
import data.net_model.Net_cfg_event;
import data.net_model.Net_cfg_signal;
import data.net_model.Net_cfg_signal_name;
import data.net_model.Net_cfg_trigger_value;
import data.net_model.Net_control;
import data.net_model.Net_control_value_data;
import data.net_model.Net_data_signal;
import data.net_model.Net_equipment;
import data.net_model.Net_history_signal;
import data.net_model.Os_active_event;
import data.net_model.Os_active_signal;

/**网络的数据接口类**/
/**网络数据模块的北向接口
 * author:jk
 * date:2016 10 1
 * addr:kstar*/
public class DataHAL {

	public DataHAL() {
		// TODO Auto-generated constructor stub
	}

	//发送控制命令
	public static int send_control_cmd(String ip_addr, int port, List<Net_control> control_cmds) {
		byte[] send_buf = NetDataQuery.build_control_cmd(control_cmds);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_control_cmd_ack(recv_buf);
	}
	//设置告警阈值
	public static int set_cfg_trigger_value(String ip_addr, int port, List<Net_cfg_trigger_value> trig_values) {
		byte[] send_buf = NetDataQuery.build_set_event_trigger_value(trig_values);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_set_event_trigger_value_ack(recv_buf);
	}
	//设置信号名称
	public static int set_cfg_signal_name(String ip_addr, int port, List<Net_cfg_signal_name> signal_name) {
		byte[] send_buf = NetDataQuery.build_set_signal_name(signal_name);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_set_signal_name_ack(recv_buf);
	}
	
	
	
	//获取设备列表
	public static List<Net_equipment> get_equipment_list(String ip_addr, int port) {
		byte[] send_buf = NetDataQuery.build_query_equip_list();
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_equip_list_ack(recv_buf);
	}
	
	//获取信号配置列表
	public static List<Net_cfg_signal> get_signal_cfg_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_signal_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_signal_list_ack(recv_buf);
	}
	
	//按设备ID获取实时信号列表
	public static List<Net_data_signal> get_signal_data_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_signal_rt_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_signal_list_rt_ack(recv_buf);
	}
	
	//按照设备ID和信号ID获取单条实时信号
	// WARN: 低效慎用！！！  
	public static Net_data_signal get_signal_data(String ip_addr, int port, int equipid, int signalid) {
		byte[] send_buf = NetDataQuery.build_query_signal_rt_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		List<Net_data_signal> ipc_data_sigs =  NetDataQuery.parse_query_signal_list_rt_ack(recv_buf);
		Net_data_signal ipc_data_sig = new Net_data_signal();
		
		int i = 0;
		for (i = 0; i < ipc_data_sigs.size(); ++i) {
			if (signalid == ((Net_data_signal)ipc_data_sigs.toArray()[i]).sigid) {
				ipc_data_sig = (Net_data_signal)ipc_data_sigs.toArray()[i];
				break;
			}
		}
		
		return ipc_data_sig;
	}
	
	//按照设备ID和信号ID获取告警等级
	// WARN: 低效慎用！！！ 
	public static int get_event_level(String ip_addr, int port, int equipid, int signalid)
	{
		byte[] send_buf = NetDataQuery.build_query_signal_rt_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		List<Net_data_signal> ipc_data_sigs =  NetDataQuery.parse_query_signal_list_rt_ack(recv_buf);
		Net_data_signal ipc_data_sig = new Net_data_signal();
		
		int i = 0;
		for (i = 0; i < ipc_data_sigs.size(); ++i) {
			if (signalid == ((Net_data_signal)ipc_data_sigs.toArray()[i]).sigid) {
				ipc_data_sig = (Net_data_signal)ipc_data_sigs.toArray()[i];
				break;
			}
		}
		
		return ipc_data_sig.severity;
	}
	
	
	
	//按照设备ID获取控制命令的配置列表
	public static List<Net_cfg_control> get_control_cfg_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_control_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_control_list_ack(recv_buf);
	}
	
	//按照设备ID获取控制命令参数含义的配置列表
	public static List<Net_cfg_ctrl_parameaning> get_control_parameaning_cfg_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_control_parameaning_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_control_parameaning_list_ack(recv_buf);
	}
	
	//按照设备ID获取控制命令数据
	public static List<Net_control_value_data> 
	get_control_value_data_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_control_value_data(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.build_query_control_value_data_ack(recv_buf);
	}
	
	//按照设备ID获取告警配置列表
	public static List<Net_cfg_event> get_event_cfg_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_event_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_event_list_ack(recv_buf);
	}
	
	//获取系统中所有活动告警
	public static List<Net_active_event> get_all_active_alarm_list(String ip_addr, int port) {
		byte[] send_buf = NetDataQuery.build_query_all_active_alarm_list();
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_all_active_alarm_list_ack(recv_buf);
	}
	
	//按设备ID取活动告警列表
	public static List<Net_active_event> get_equip_active_alarm_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_equip_active_alarm_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_equip_active_alarm_list_ack(recv_buf);
	}
	
	 //按照设备ID获取历史信号列表
	public static List<Net_history_signal> get_history_signal_list(String ip_addr, int port, int equipid) {
		byte[] send_buf = NetDataQuery.build_query_history_signal_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_history_signal_ack(recv_buf);
	}

	// 按要求获取历史信号列表
	public static List<Net_history_signal> get_his_sig_list(String ip_addr, int port, int equipid, int signalid,
			long startime, long span, long count, boolean order)
	{
		byte[] send_buf = NetDataQuery.build_query_his_sig(equipid, signalid, startime, span, count, order);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);

		return NetDataQuery.parse_query_his_sig_ack(recv_buf);
	}
	
	
	//返回系统状态 0:正常1：中断2：告警
	public static int get_MU_State()
	{
		List<Net_active_event> alarmList = get_all_active_alarm_list(NetHAL.IP,NetHAL.Port);
		int state = 0;
		
		if(alarmList.isEmpty())
		{
			return 0;
		}
		for (int i = 0; i < alarmList.size(); ++i) 
		{
			if (10001 == ((Net_active_event)alarmList.toArray()[i]).eventid) 
			{
				state = 1;
			}
		}
	   if(state==1 && alarmList.size()>0)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
	
	//界面用的，返回活动告警列表，其中包含了告警的所有属性
	public static List<Os_active_event> get_Active_Event(int equipID)
	{

		//List<ipc_active_event>  activeEventList = get_equip_active_alarm_list(IP,PORT,equipID);
		List<Net_active_event>  activeEventList = get_all_active_alarm_list(NetHAL.IP, NetHAL.Port);
		
		List<Os_active_event> eventList = new ArrayList<Os_active_event>();

		for(int i = 0; i < activeEventList.size(); ++i)
		{
			Os_active_event event = new Os_active_event();
			event.eventid = ((Net_active_event)activeEventList.toArray()[i]).eventid;
			event.starttime = ((Net_active_event)activeEventList.toArray()[i]).starttime;
			event.endtime = ((Net_active_event)activeEventList.toArray()[i]).endtime;
			event.grade = ((Net_active_event)activeEventList.toArray()[i]).grade;
			event.equipid = ((Net_active_event)activeEventList.toArray()[i]).equipid;
			event.meaning = ((Net_active_event)activeEventList.toArray()[i]).meaning;
			event.is_active = ((Net_active_event)activeEventList.toArray()[i]).is_active;
			// just for test {{{
			//event.name = get_event_name(event.equipid, event.eventid);
//待开启			event.name = DataGetter.getEventName(String.valueOf(event.equipid), String.valueOf(event.eventid));
//待开启			event.equipName = DataGetter.getEquipmentName(String.valueOf(event.equipid));
			// }}}
			eventList.add(event);
		}
		return eventList;
	}
	
	//界面用的，返回活动告警列表，其中包含了告警的所有属性
	public static List<Os_active_signal> get_Active_Signal(int equipID)
	{
	
		List<Net_data_signal>  activeSignalList = get_signal_data_list(NetHAL.IP, NetHAL.Port,equipID);
		List<Os_active_signal> signalList = new ArrayList<Os_active_signal>();
		for(int i = 0; i < activeSignalList.size(); ++i)
		{
			Os_active_signal signal = new Os_active_signal();
			
			signal.sigid = ((Net_data_signal)activeSignalList.toArray()[i]).sigid;
			signal.freshtime = ((Net_data_signal)activeSignalList.toArray()[i]).freshtime;
			signal.severity = ((Net_data_signal)activeSignalList.toArray()[i]).severity;
			signal.value = ((Net_data_signal)activeSignalList.toArray()[i]).value;
			signal.value_type = ((Net_data_signal)activeSignalList.toArray()[i]).value_type;
			signal.equipid = ((Net_data_signal)activeSignalList.toArray()[i]).equipid;
			
			Net_cfg_signal signalcfg = get_cfg_signal(equipID,signal.sigid);
			
			signal.name = (null == signalcfg ? "" : signalcfg.name);
			signal.unit = (null == signalcfg ? "" : signalcfg.unit);
			
			signalList.add(signal);
		}
		return signalList;
	}
	
	public static String get_event_name(int equID, int eventID)
	{
		List<Net_cfg_event>  cfgEventList =	 get_event_cfg_list(NetHAL.IP, NetHAL.Port,equID);
		String name = "" ;
		for(int i = 0; i < cfgEventList.size(); ++i)
		{
			if(((Net_cfg_event)cfgEventList.toArray()[i]).id == eventID)
				name =  ((Net_cfg_event)cfgEventList.toArray()[i]).name;
		}
		return name;
	}
	
	public static Net_cfg_signal get_cfg_signal(int equID, int signalID)
	{
		List<Net_cfg_signal>  cfgSignalList =	 get_signal_cfg_list(NetHAL.IP, NetHAL.Port,equID);
		Net_cfg_signal signal = null ;
		for(int i = 0; i < cfgSignalList.size(); ++i)
		{
			if(((Net_cfg_signal)cfgSignalList.toArray()[i]).id == signalID)
				signal =  ((Net_cfg_signal)cfgSignalList.toArray()[i]);
		}
		return signal;
	}
	
	//按设备ID返回状态 0:正常1：中断2：告警
	public static int get_Equipment_State(int equipID)
	{
		List<Net_active_event> alarmList = get_equip_active_alarm_list(NetHAL.IP, NetHAL.Port,equipID);
		int state = 0;
		
		if(alarmList.isEmpty())
		{
			return 0;
		}
		for (int i = 0; i < alarmList.size(); ++i) 
		{
			if (10001 == ((Net_active_event)alarmList.toArray()[i]).eventid) 
			{
				state = 1;
			}
		}
	   if(state==1 && alarmList.size()>0)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
	
	public static List<Net_cfg_trigger_value> get_cfg_trigger_value(String ip_addr, int port, int equipid)
	{
		byte[] send_buf = NetDataQuery.build_query_event_trigger_value_list(equipid);
		byte[] recv_buf = NetHAL.send_and_receive(send_buf, ip_addr, port);
		
		return NetDataQuery.parse_query_event_trigger_value_ack(recv_buf);
	}
	
}
