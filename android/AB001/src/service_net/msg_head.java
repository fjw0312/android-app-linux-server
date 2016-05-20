package service_net;

import android.util.Log;


//通信包头类   25byte
/****************
 * 1byte 包头起始 标识
 * 4byte 包长  包体的长度  
 * 4byte 包的数据类型   int 0-gDat 1-sDat 2-sQue 3-sCmd
 * 4byte 设备id
 * 4byte 设备类型/信息类型 (type编号)
 * 4byte 信号id
 * 4byte 信号数据/相关数据
 * **************
 */
public class msg_head {

	//Fields
	//包头成员
	public byte start=0x01;  //包头起始 标识
	public int lenth=0;
	public int flag = 0;  //0-gDat 1-sDat 2-sQue 3-sCmd
	
	public int equipID;
	public int type;
	public int siganlID;
	public int para_data;
	
	//包头数据数组
	public byte[] msg_head_buf = new byte[25];
	
	public msg_head() { 
		// TODO Auto-generated constructor stub
	}
	//实例化包头参数 并返回数组大小
	public int fill_msg_head(int set_lenth,int s_flag,int set_equipID,int set_type,int set_signalID,int set_para){
				
		lenth = set_lenth;
		flag = s_flag;
		equipID = set_equipID;
		type = set_type;
		siganlID = set_signalID;
		para_data = set_para;
		
		msg_head_buf[0] = start;
		for(int i=0;i<4;i++)
			msg_head_buf[4-i] = (byte)( (lenth>>(8*i)) & 0xFF );
		for(int i=0;i<4;i++)
			msg_head_buf[8-i] = (byte)( (flag>>(8*i)) & 0xFF );
		for(int i=0;i<4;i++)
			msg_head_buf[12-i] = (byte)( (equipID>>(8*i)) & 0xFF );
		for(int i=0;i<4;i++)
			msg_head_buf[16-i] = (byte)( (type>>(8*i)) & 0xFF );
		for(int i=0;i<4;i++)
			msg_head_buf[20-i] = (byte)( (siganlID>>(8*i)) & 0xFF );
		for(int i=0;i<4;i++)
			msg_head_buf[24-i] = (byte)( (para_data>>(8*i)) & 0xFF );
	
		Log.e("msg_head->fill_msg_head>>lenth",String.valueOf(lenth));
//		for(int i=0;i<25;i++){
//			Log.e("msg_head->fill_msg_head>>buf[i]", "buf["+String.valueOf(i)+"]"+
//					"="+String.valueOf(msg_head_buf[i]));
//		}
		
		return msg_head_buf.length;
	}
	//解析实例化包头
	public boolean parse_msg_head(byte[] buf){
		if(buf==null) return false;
		if(buf.length<25) return false;
		
		start = buf[0];
		lenth = 0;
		flag = 0;
		equipID = 0;
		type = 0;
		siganlID = 0;
		para_data = 0;
		
		for(int i=0;i<4;i++)
			lenth |= (int)(buf[4-i] & 0xFF)<<(8*i);
		for(int i=0;i<4;i++)
			flag |= (int)(buf[8-i] & 0xFF)<<(8*i);
		for(int i=0;i<4;i++)
			equipID |= (int)(buf[12-i] & 0xFF)<<(8*i);
		for(int i=0;i<4;i++)
			type |= (int)(buf[16-i] & 0xFF)<<(8*i);
		for(int i=0;i<4;i++)
			siganlID |= (int)(buf[20-i] & 0xFF)<<(8*i);
		for(int i=0;i<4;i++)
			para_data |= (int)(buf[24-i] & 0xFF)<<(8*i);
		

//		Log.e("msg_head->parse_msg_head>>buf[4]",String.valueOf(buf[4]));
		Log.e("msg_head->parse_msg_head>>lenth",String.valueOf(lenth));
		Log.e("msg_head->parse_msg_head>>equipID",String.valueOf(equipID));
		return true;
	}

}
