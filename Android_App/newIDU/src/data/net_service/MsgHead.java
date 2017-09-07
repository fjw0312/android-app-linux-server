package data.net_service;

//网络包头信息  15byte
public class MsgHead {
	
	public byte start = 0x00;   // 0x01 固定
	public byte version;
	public short sn;
	public short cmd;
	public int body_lenth = 0;
	public byte ret_code;
	public int para;
	
	//将网络包类 转换为  数组       用于获得网络包类
	public void fill_msg_head(byte[] buf) {
		buf[0] = 0x01;
		
		String str = String.format("%02x", version);
		buf[1] = str.getBytes()[0];
		buf[2] = str.getBytes()[1];
		
		int i = 0;
		str = String.format("%04x", sn);
		for (i = 0; i < 4; ++i) {
			buf[3+i] = str.getBytes()[i];
		}
		
		str = String.format("%02x", cmd);
		buf[7] = str.getBytes()[0];
		buf[8] = str.getBytes()[1];
		
		str = String.format("%08x", body_lenth);
		for (i = 0; i < 8; ++i) {
			buf[9+i] = str.getBytes()[i];
		}
		
		str = String.format("%02x", ret_code);
		buf[17] = str.getBytes()[0];
		buf[18] = str.getBytes()[1];
		
		str = String.format("%08x", para);
		for (i = 0; i < 8; ++i) {
			buf[19+i] = str.getBytes()[i];
		}
	}
	
	//将数组 转化为  网络包类       用于解析网络包
	public  void parse_msg_head(byte[] buf) {
		start = buf[0];		
		try {
			String str_version = String.format("%c%c", buf[1], buf[2]);
			version = (byte)Integer.parseInt(str_version, 16);
		
			String str_sn = String.format("%c%c%c%c", buf[3], buf[4], buf[5], buf[6]);
			sn = (short)Integer.parseInt(str_sn, 16);
		
			String str_cmd = String.format("%c%c", buf[7], buf[8]);
			cmd = (short)Integer.parseInt(str_cmd, 16);
		
			String str_length = String.format("%c%c%c%c%c%c%c%c", 
				buf[9], buf[10], buf[11], buf[12], buf[13], buf[14], buf[15], buf[16]);
			body_lenth = (int)Integer.parseInt(str_length, 16);
		
			String str_ret_code = String.format("%c%c", buf[17], buf[18]);
			ret_code = (byte)Integer.parseInt(str_ret_code, 16);
		
			String str_para = String.format("%c%c%c%c%c%c%c%c", 
				buf[19], buf[20], buf[21], buf[22], buf[23], buf[24], buf[25], buf[26]);
			para = (int)Integer.parseInt(str_para, 16);
		} catch (Exception e) {   
			return;//	throw new Exception(e.getMessage().toString());	
		}
	}
}
