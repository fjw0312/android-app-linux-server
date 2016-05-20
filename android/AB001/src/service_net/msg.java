package service_net;

import android.util.Log;
import DataModel.Equipment;
import DataModel.Model;

/**通信处理信息包*/
/***
 * 网络通信 信息处理包-处理单元一个设备类
 * @author fjw0312
 * date 2016 4 22
 * E-mail fjw0312@163.com
 *
 */
public  class msg {

	public msg() {
		// TODO Auto-generated constructor stub
	}
	//Model 上报 网络的 byte[] buf
	public byte[] buf = new byte[100*1024+25];
	//网络 下载获取 到Model 的 设备类
	public Equipment equip = new Equipment();
	
	//fill 填充网络通信信号包  返回包的字节大小
	public int fill_msg(int flag, int equiptID){        //目前 主要 实现set 发送数据包的填充  
		Equipment equip = new Equipment();
		if(flag==0){
			equip = Model.get_Equipment_getHt(equiptID);//得保证 该设备存在数据模型中
		}else if(flag==1){
			equip = Model.get_Equipment_setHt(equiptID);//得保证 该设备存在数据模型中
		}else if(flag==2){			
			equip = Model.get_Equipment_queHt(equiptID);//得保证 该设备存在数据模型中
		}else if(flag ==3){
			equip = Model.get_Equipment_cmdHt(equiptID);//得保证 该设备存在数据模型中
		}
		
		msg_body msgBody = new msg_body();
		int body_lenth = msgBody.fill_msg_body(equip.ht_signal); 						
		msg_head msgHead = new msg_head();
		int head_lenth = msgHead.fill_msg_head(body_lenth, flag, equiptID, equip.type, equip.siganlID, equip.para_data);
		
		System.arraycopy(msgHead.msg_head_buf, 0, buf, 0, head_lenth);
		System.arraycopy(msgBody.msg_body_buf, 0, buf, head_lenth, body_lenth);
		
		return head_lenth+body_lenth;
	}
	//parse 解析网络通信信号包 -赋值与数据模型
	public void parse_msg(byte[] buf){ //目前 主要 实现get 发送数据包的解析 
//		Log.e("msg->parse_msg>>buf.lenth:",String.valueOf(buf.length));
		Equipment equip = new Equipment();
		msg_head msgHead = new msg_head();
		msgHead.parse_msg_head(buf);
		msg_body msgBody = new msg_body();
		if(msgHead.lenth==0){
			msgBody.parse_msg_body(null);
		}else{
			Log.e("msg->parse_msg>>msgHead.lenth:",String.valueOf(msgHead.lenth));
			byte[] buf1 = new byte[msgHead.lenth];
			int temp = msgHead.lenth-25;
			if( msgHead.lenth>(buf.length-25) ){
				temp = buf.length-25;
			}
			System.arraycopy(buf, 25, buf1, 0, temp);
			msgBody.parse_msg_body(buf1);
		}

		equip.equipID = msgHead.equipID;
		equip.type = msgHead.type;
		equip.siganlID = msgHead.siganlID;
		equip.para_data = msgHead.para_data;
		equip.ht_signal = msgBody.ht_siganl;
		if(msgHead.flag==0){
			Model.add_Equipment_getHt(equip.equipID, equip);
		}else if(msgHead.flag==1){
				Model.add_Equipment_setHt(equip.equipID, equip);
		}else if(msgHead.flag==2){
				Model.add_Equipment_queHt(equip.equipID, equip);
		}else if(msgHead.flag==3){
				Model.add_Equipment_cmdHt(equip.equipID, equip);
		}
		
	}
}
