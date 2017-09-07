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

/**网络数据请求解析 接口函数*/
public class NetDataQuery {

	public NetDataQuery() {
		// TODO Auto-generated constructor stub
	}
	
	public static byte[] build_query_station() {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_STATION;
		head.body_lenth = 1;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static byte[] build_query_equip_list() {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_EQUIP_LIST;
		head.body_lenth = 1;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_equipment> parse_query_equip_list_ack(byte[] recv_buf) {
		List<Net_equipment> equipments = new ArrayList<Net_equipment>();
		
		if (recv_buf == null) {
			return equipments;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_EQUIP_LIST_ACK) {
			return equipments;
		}
		
		int equip_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
	
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < equip_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_equipment equip = new Net_equipment();
				
			try {
				equip.id = Integer.parseInt(items[0]);
				equip.templateId = Integer.parseInt(items[1]);
				equip.category = Integer.parseInt(items[2]);
				equip.name = (null==items[3] ? "" : new String(items[3]));
				equip.xmlname = (null==items[4] ? "" : new String(items[4]));
			} catch (Exception e) {
				;
			}
				
			equipments.add(equip);
		}
		
		return equipments;
	}
	
	public static byte[] build_query_signal_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_SIG_LIST;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_cfg_signal>  parse_query_signal_list_ack(byte[] recv_buf) {
		List<Net_cfg_signal> cfg_signals = new ArrayList<Net_cfg_signal>();
		
		if (recv_buf == null) {
			return cfg_signals;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_SIG_LIST_ACK) {
			return cfg_signals;
		}
		
		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < sig_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_cfg_signal signal = new Net_cfg_signal();
				
			try {
				signal.equipid = Integer.parseInt(items[0]);
				signal.id = Integer.parseInt(items[1]);	
				signal.name = (null==items[2] ? "" : new String(items[2]));
				signal.unit = (null==items[3] ? "" : new String(items[3]));
				signal.precision = Integer.parseInt(items[4]);
				signal.description = (null==items[5] ? "" : new String(items[5]));
			} catch (Exception e) {
				;
			}
				
			cfg_signals.add(signal);
		}
		
		return cfg_signals;
	}
	
	public static byte[] build_query_signal_rt_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_SIG_LIST_RT;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_data_signal>  parse_query_signal_list_rt_ack(byte[] recv_buf) {
		List<Net_data_signal> data_signals = new ArrayList<Net_data_signal>();
		
		if (recv_buf == null) {
			return data_signals;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_SIG_LIST_RT_ACK) {
			return data_signals;
		}
		
		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < sig_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_data_signal signal = new Net_data_signal();
				
			try {
				signal.equipid = Integer.parseInt(items[0]);
				signal.sigid = Integer.parseInt(items[1]);
				signal.freshtime = Integer.parseInt(items[2]);
				signal.is_invalid = Integer.parseInt(items[3]);
				signal.value_type = Integer.parseInt(items[4]);
				signal.value = (null==items[5] ? "" : new String(items[5]));
				signal.severity = Integer.parseInt(items[6]);
				signal.meaning = (null==items[7] ? "" : new String(items[7]));
			} catch (Exception e) {
				;
			}
				
			data_signals.add(signal);
		}
		
		return data_signals;
	}
	
	public static byte[] build_query_control_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_CTRL_LIST;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static byte[] build_query_control_parameaning_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_CTRL_PARAM_LIST;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_cfg_control>  parse_query_control_list_ack(byte[] recv_buf) {
		List<Net_cfg_control> cfg_controls = new ArrayList<Net_cfg_control>();
		
		if (recv_buf == null) {
			return cfg_controls;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_CTRL_LIST_ACK) {
			return cfg_controls;
		}
		
		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < sig_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_cfg_control control = new Net_cfg_control();
				
			try {
				control.equipid = Integer.parseInt(items[0]);
				control.id = Integer.parseInt(items[1]);
				control.fMaxValue = Float.parseFloat(items[2]);
				control.fMinValue = Float.parseFloat(items[3]);
				control.name = (null==items[4] ? "" : new String(items[4]));
				control.unit = (null==items[5] ? "" : new String(items[5]));
			} catch (Exception e) {
				;
			}
				
			cfg_controls.add(control);
		}
		
		return cfg_controls;
	}
	
	
	public static List<Net_cfg_ctrl_parameaning>  parse_query_control_parameaning_list_ack(byte[] recv_buf) {
		List<Net_cfg_ctrl_parameaning> cfg_ctrlparam = new ArrayList<Net_cfg_ctrl_parameaning>();
		
		if (recv_buf == null) {
			return cfg_ctrlparam;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_CTRL_PARAM_LIST_ACK) {
			return cfg_ctrlparam;
		}
		
		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < sig_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_cfg_ctrl_parameaning ctrl_parameaning = new Net_cfg_ctrl_parameaning();
				
			try {
				ctrl_parameaning.ctrlid      = Integer.parseInt(items[0]);
				ctrl_parameaning.parameterid = Integer.parseInt(items[1]);
				ctrl_parameaning.paramvalue  = Integer.parseInt(items[2]);
				ctrl_parameaning.parameaning = (null==items[3] ? "" : new String(items[3]));
			} catch (Exception e) {
				;
			}
				
			cfg_ctrlparam.add(ctrl_parameaning);
		}
		
		return cfg_ctrlparam;
	}
	
	
	public static byte[] build_query_control_value_data(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_CTRL_VALUE;
		
		String body = String.format("%d`0`", equipid);

		byte[] body_buf = new byte[body.length()+1];
		System.arraycopy(body.getBytes(), 0, body_buf, 0, body.length());
		
		head.body_lenth = body_buf.length;
		head.para = 1;
		head.fill_msg_head(head_buf);

		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_control_value_data>  build_query_control_value_data_ack(byte[] recv_buf) {
		List<Net_control_value_data> control_values = new ArrayList<Net_control_value_data>();
		
		if (recv_buf == null) {
			return control_values;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_CTRL_VALUE_ACK) {
			return control_values;
		}
		
		int block_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
		
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < block_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_control_value_data control_value = new Net_control_value_data();
				
			try {
				control_value.equipid = Integer.parseInt(items[0]);
				control_value.ctrlid = Integer.parseInt(items[1]);
				control_value.relatedsigid = Integer.parseInt(items[2]);
				control_value.freshtime = Integer.parseInt(items[3]);
				control_value.is_invalid = Integer.parseInt(items[4]);
				control_value.value_type = Integer.parseInt(items[5]);
				control_value.value = new String(items[6]);
				if (control_value.relatedsigid == 0 || control_value.value.length() == 0) {
					control_value.value = "--";
				}
			} catch (Exception e) {
				;
			}
				
			control_values.add(control_value);
		}
		
		return control_values;
	}
	
	public static byte[] build_query_event_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_QUERY_EVENT_LIST;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_cfg_event>  parse_query_event_list_ack(byte[] recv_buf) {
		List<Net_cfg_event> cfg_events = new ArrayList<Net_cfg_event>();
		
		if (recv_buf == null) {
			return cfg_events;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_EVNET_LIST_ACK) {
			return cfg_events;
		}
		
		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < sig_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_cfg_event event = new Net_cfg_event();
				
			try {
				event.equipid = Integer.parseInt(items[0]);
				event.id = Integer.parseInt(items[1]);	
				event.grade = Integer.parseInt(items[2]);
				event.name = new String ((null==items[3] ? "" : new String(items[3])));
			} catch (Exception e) {
				;
			}
				
			cfg_events.add(event);
		}
		
		return cfg_events;
	}
	
	public static byte[] build_query_all_active_alarm_list() {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_ID_QUERY_ALL_ACTIVE_ALARM;
		head.body_lenth = 1;
		head.para = 0;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_active_event>  parse_query_all_active_alarm_list_ack(byte[] recv_buf) {
		List<Net_active_event> active_alarms = new ArrayList<Net_active_event>();
		
		if (recv_buf == null) {
			return active_alarms;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_ID_QUERY_ALL_ACTIVE_ALARM_ACK) {
			return active_alarms;
		}
		
		int block_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < block_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_active_event alarm = new Net_active_event();
				
			try {
				alarm.equipid = Integer.parseInt(items[0]);
				alarm.eventid = Integer.parseInt(items[1]);
				alarm.starttime = Integer.parseInt(items[2]);
				alarm.endtime = Integer.parseInt(items[3]);
				alarm.is_active = Integer.parseInt(items[4]);
				alarm.grade = Integer.parseInt(items[5]);
				alarm.meaning = (null==items[6] ? "" : new String(items[6]));
			} catch (Exception e) {
				;
			}
				
			active_alarms.add(alarm);
		}
		
		return active_alarms;
	}
	
	public static byte[] build_query_equip_active_alarm_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_ID_QUERY_EQUIP_ACTIVE_ALARM;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_active_event>  parse_query_equip_active_alarm_list_ack(byte[] recv_buf) {
		List<Net_active_event> active_alarms = new ArrayList<Net_active_event>();
		
		if (recv_buf == null) {
			return active_alarms;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_ID_QUERY_EQUIP_ACTIVE_ALARM_ACK) {
			return active_alarms;
		}
		
		int block_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < block_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_active_event alarm = new Net_active_event();
				
			try {
				alarm.equipid = Integer.parseInt(items[0]);
				alarm.eventid = Integer.parseInt(items[1]);
				alarm.starttime = Integer.parseInt(items[2]);
				alarm.endtime = Integer.parseInt(items[3]);
				alarm.is_active = Integer.parseInt(items[4]);
				alarm.grade = Integer.parseInt(items[5]);
				alarm.meaning = (null==items[6] ? "" : new String(items[6]));
			} catch (Exception e) {
				;
			}
				
			active_alarms.add(alarm);
		}
		
		return active_alarms;
	}
	
	public static byte[] build_control_cmd(List<Net_control> control_cmds) {
		int i = 0;
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_CTRL;
		
		String body = "";
		for (i = 0; i < control_cmds.size(); ++i) {
			Net_control item = (Net_control)control_cmds.toArray()[i];
			body += String.format("%d`%d`%d`%s", item.equipid, item.ctrlid, item.valuetype, item.value);
			if (i <  control_cmds.size()-1) {
				body += "|";
			}
		}
		
		byte[] body_buf = new byte[body.length()+1];
		System.arraycopy(body.getBytes(), 0, body_buf, 0, body.length());
		head.body_lenth = body_buf.length;
		head.para = control_cmds.size();
		head.fill_msg_head(head_buf);
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}

	public static int parse_control_cmd_ack(byte[] recv_buf) {	
		if (recv_buf == null) {
			return -1;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_CTRL_ACK) {
			return -1;
		}
		
		return head.ret_code;
	}
	
	public static byte[] build_query_history_signal_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = (short) MsgType.MSG_QUERY_HISTORY_SIGNAL;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_history_signal>  parse_query_history_signal_ack(byte[] recv_buf) {
		List<Net_history_signal> his_signals = new ArrayList<Net_history_signal>();
		
		if (recv_buf == null) {
			return his_signals;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_QUERY_HISTORY_SIGNAL_ACK) {
			return his_signals;
		}
		
		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
		
		String[] blocks = body.split("\\|");
		
		// TODO: 协议包错误需修正
		//if (sig_no > blocks.length) sig_no = blocks.length;
			
		for (i = 0; i < sig_no; ++i) {
			String[] items = blocks[i].split("`");
			Net_history_signal signal = new Net_history_signal();
				
			try {
				signal.equipid = Integer.parseInt(items[0]);
				signal.sigid = Integer.parseInt(items[1]);
				signal.name = (null==items[2] ? "" : new String(items[2]));
				signal.history_time = (null==items[3] ? "" : new String(items[3]));
				signal.sig_type = Integer.parseInt(items[4]);
				signal.value_type = Integer.parseInt(items[5]);
				signal.value = (null==items[6] ? "" : new String(items[6]));
				signal.severity = Integer.parseInt(items[7]);
				signal.unit = (null==items[8] ? "" : new String(items[8]));
			} catch (Exception e) {
				;
			}
				
			his_signals.add(signal);
		}
		
		return his_signals;
	}
	
	public static byte[] build_query_event_trigger_value_list(int equipid) {
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = (short) MsgType.MSG_ID_GET_TRIG_VALUE;
		head.body_lenth = 1;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] body_buf = new byte[1];
		body_buf[0] = '\0';
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}
	
	public static List<Net_cfg_trigger_value>  parse_query_event_trigger_value_ack(byte[] recv_buf) {
		List<Net_cfg_trigger_value> trig_values = new ArrayList<Net_cfg_trigger_value>();
		
		if (recv_buf == null) {
			return trig_values;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_ID_GET_TRIG_VALUE_ACK) {
			return trig_values;
		}
		
		int count = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1) {
			try {
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e) {
				;
			}
		}
			
		String body = new String(body_buf);
			
		String[] blocks = body.split("\\|");
		for (i = 0; i < count; ++i) {
			String[] items = blocks[i].split("`");
			Net_cfg_trigger_value trig_value = new Net_cfg_trigger_value();
				
			try {
				trig_value.equipid = Integer.parseInt(items[0]);
				trig_value.eventid = Integer.parseInt(items[1]);
				trig_value.enabled = Integer.parseInt(items[2]);
				trig_value.conditionid  = Integer.parseInt(items[3]);
				trig_value.startvalue = Float.parseFloat(items[4]);
				trig_value.stopvalue = Float.parseFloat(items[5]);
				trig_value.eventseverity = Integer.parseInt(items[6]);
				trig_value.mark = Integer.parseInt(items[7]);
			} catch (Exception e) {
				;
			}
				
			trig_values.add(trig_value);
		}
		
		return trig_values;
	}
	
	public static byte[] build_set_event_trigger_value(List<Net_cfg_trigger_value> trig_values) {
		int i = 0;
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_ID_SET_TRIG_VALUE;
		
		String body = "";
		for (i = 0; i < trig_values.size(); ++i) {
			Net_cfg_trigger_value item = (Net_cfg_trigger_value)trig_values.toArray()[i];
			body += String.format("%d`%d`%d`%d`%f`%f`%d`%d", 
					item.equipid, item.eventid, item.enabled, item.conditionid, item.startvalue,
					item.stopvalue, item.eventseverity, item.mark);
			if (i <  trig_values.size()-1) {
				body += "|";
			}
		}
		
		byte[] body_buf = new byte[body.length()+1];
		System.arraycopy(body.getBytes(), 0, body_buf, 0, body.length());
		head.body_lenth = body_buf.length;
		head.para = trig_values.size();
		head.fill_msg_head(head_buf);
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}

	public static int parse_set_event_trigger_value_ack(byte[] recv_buf) {	
		if (recv_buf == null) {
			return -1;
		}
		
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);
		
		if (head.cmd != MsgType.MSG_ID_SET_TRIG_VALUE_ACK) {
			return -1;
		}
		
		return head.ret_code;
	}
	
	public static byte[] build_query_his_sig(int equipid, int signalid, long startime, long span, long count, boolean order) {
		int i = 0;
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_ID_GET_HIS_SIG;
		
		int bodycount = 1;
		String body = "";
		for (i = 0; i < bodycount; ++i) {
			body += String.format("%d`%d`%d`%d", 
					startime, span, count, order?1:0);
			if (i <  bodycount-1) {
				body += "|";
			}
		}
		
		byte[] body_buf = new byte[body.length()+1];
		System.arraycopy(body.getBytes(), 0, body_buf, 0, body.length());
		head.body_lenth = body_buf.length;
		head.para = equipid;
		head.fill_msg_head(head_buf);
		
		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}

	public static List<Net_history_signal> parse_query_his_sig_ack(byte[] recv_buf)
	{
		List<Net_history_signal> his_signals = new ArrayList<Net_history_signal>();

		if (recv_buf == null)
		{
			return his_signals;
		}

		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);

		if (head.cmd != MsgType.MSG_ID_GET_HIS_SIG_ACK)
		{
			return his_signals;
		}

		int sig_no = head.para;
		int i = 0;

		byte[] body_buf = new byte[head.body_lenth];
		if (head.body_lenth > 1)
		{
			try
			{
				System.arraycopy(recv_buf, Msg.HEAD_LENTH, body_buf, 0, head.body_lenth);
			} catch (Exception e)
			{
				;
			}
		}

		String body = new String(body_buf);

		String[] blocks = body.split("\\|");

		// TODO: 协议包错误需修正
		// if (sig_no > blocks.length) sig_no = blocks.length;

		for (i = 0; i < sig_no; ++i)
		{
			String[] items = blocks[i].split("`");
			Net_history_signal signal = new Net_history_signal();

			try
			{
				signal.equipid = Integer.parseInt(items[0]);
				signal.sigid = Integer.parseInt(items[1]);
				signal.value = (null == items[2] ? "" : new String(items[2]));
				signal.history_time = (null == items[3] ? "" : new String(items[3]));
				signal.severity = Integer.parseInt(items[4]);
				//signal.name = (null == items[2] ? "" : new String(items[2]));
				//signal.sig_type = Integer.parseInt(items[4]);
				//signal.value_type = Integer.parseInt(items[5]);
				//signal.unit = (null == items[8] ? "" : new String(items[8]));
			} catch (Exception e)
			{
				;
			}

			his_signals.add(signal);
		}

		return his_signals;
	}

	public static byte[] build_set_signal_name(List<Net_cfg_signal_name> signal_name)
	{
		int i = 0;
		byte[] head_buf = new byte[Msg.HEAD_LENTH];
		MsgHead head = new MsgHead();
		head.cmd = MsgType.MSG_ID_SET_SIGNAL_NAME;

		String body = "";
		for (i = 0; i < signal_name.size(); ++i)
		{
			Net_cfg_signal_name item = (Net_cfg_signal_name) signal_name.toArray()[i];
			body += String.format("%d`%d`%d`%s", item.equipid, item.signalid, item.eventid, item.name);
			if (i < signal_name.size() - 1)
			{
				body += "|";
			}
		}

		byte[] body_buf = new byte[body.length() + 1];
		System.arraycopy(body.getBytes(), 0, body_buf, 0, body.length());
		head.body_lenth = body_buf.length;
		head.para = signal_name.size();
		head.fill_msg_head(head_buf);

		byte[] msg = Msg.merge_msg(head_buf, body_buf);
		return msg;
	}

	public static int parse_set_signal_name_ack(byte[] recv_buf)
	{
		if (recv_buf == null)
		{
			return -1;
		}

		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_buf);

		if (head.cmd != MsgType.MSG_ID_SET_SIGNAL_NAME_ACK)
		{
			return -1;
		}

		return head.ret_code;
	}

}


