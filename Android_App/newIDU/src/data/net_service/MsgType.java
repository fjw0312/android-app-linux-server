package data.net_service;

//网络包类型 应答 码表
public class MsgType {
	public final static int MSG_QUERY_STATION = 0x10;
	public final static int MSG_QUERY_STATION_ACK = 0x11;
	
	public final static int MSG_QUERY_EQUIP_LIST = 0x20;
	public final static int MSG_QUERY_EQUIP_LIST_ACK = 0x21;
	
	public final static int MSG_QUERY_EQUIP_LIST_RT = 0x22;
	public final static int MSG_QUERY_EQUIP_LIST_RT_ACK = 0x23;
	
	public final static int MSG_QUERY_SIG_LIST = 0x30;
	public final static int MSG_QUERY_SIG_LIST_ACK = 0x31;
	
	public final static int MSG_QUERY_CTRL_LIST = 0x32;
	public final static int MSG_QUERY_CTRL_LIST_ACK = 0x33;
	
	public final static int MSG_QUERY_EVENT_LIST = 0x34;
	public final static int MSG_QUERY_EVNET_LIST_ACK = 0x35;
	
	public final static int MSG_QUERY_CTRL_PARAM_LIST = 0x36;
	public final static int MSG_QUERY_CTRL_PARAM_LIST_ACK = 0x37;
	
	public final static int MSG_QUERY_SIG_LIST_RT = 0x42;
	public final static int MSG_QUERY_SIG_LIST_RT_ACK = 0x43;
	
	public final static int MSG_QUERY_SIG_LIST_RT_EX = 0x40;
	public final static int MSG_QUERY_SIG_LIST_RT_EX_ACK = 0x41;
	
	public final static int MSG_INFO_ALARM = 0x50;
	public final static int MSG_INFO_ALARM_ACK = 0x51;
	
	public final static int MSG_ID_QUERY_ALL_ACTIVE_ALARM = 0x60;
	public final static int MSG_ID_QUERY_ALL_ACTIVE_ALARM_ACK = 0x61;
	
	public final static int MSG_ID_QUERY_EQUIP_ACTIVE_ALARM = 0x62;
	public final static int MSG_ID_QUERY_EQUIP_ACTIVE_ALARM_ACK = 0x63;
	
	public final static int MSG_CTRL = 0x70;
	public final static int MSG_CTRL_ACK = 0x71;
	
	public final static int MSG_CTRL_VALUE = 0x72;
	public final static int MSG_CTRL_VALUE_ACK = 0x73;
	
	public final static int MSG_IP_INFORM = 0x84;
	public final static int MSG_IP_INFORM_ACK = 0x85;
	
	public final static int MSG_INFO_ALARM_HEARTBEAT = 0x86;
	public final static int MSG_INFO_ALARM_HEARTBEAT_ACK = 0x87;
	
	public final static int MSG_QUERY_HISTORY_SIGNAL = 0x90;
	public final static int MSG_QUERY_HISTORY_SIGNAL_ACK = 0x91;
	
	public final static int MSG_ID_SET_TRIG_VALUE = 0xA0;
	public final static int MSG_ID_SET_TRIG_VALUE_ACK = 0xA1;
	
	public final static int MSG_ID_GET_TRIG_VALUE = 0xA2;
	public final static int MSG_ID_GET_TRIG_VALUE_ACK = 0xA3;
	
	public final static int MSG_ID_GET_HIS_SIG = 0xA4;
	public final static int MSG_ID_GET_HIS_SIG_ACK = 0xA5;
	
	public final static int MSG_ID_SET_SIGNAL_NAME = 0xA6;
	public final static int MSG_ID_SET_SIGNAL_NAME_ACK = 0xA7;
}
