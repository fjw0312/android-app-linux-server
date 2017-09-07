package data.net_service;


/**通信处理信息包*/
/***
 * 网络通信 信息处理包-处理单元一个设备类
 * @author jk
 * date 2016 10 1
 * E-mail:
 *
 */
public class Msg {
	
	public final static int HEAD_LENTH = 27;

	public Msg() {
		// TODO Auto-generated constructor stub
	}
	
	//获取信号包的数组    
	public static byte[] merge_msg(byte[] head, byte[] body) {
		byte[] msg_buf = new byte[HEAD_LENTH+body.length];
		System.arraycopy(head, 0, msg_buf, 0, HEAD_LENTH);
		System.arraycopy(body, 0, msg_buf, HEAD_LENTH, body.length);
		
		return msg_buf;
	}

}
