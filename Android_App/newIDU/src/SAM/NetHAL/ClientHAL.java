package SAM.NetHAL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import android.util.Log;

//网络 客户端接口
public class ClientHAL {

	public ClientHAL() {
		// TODO Auto-generated constructor stub
	}
	
	public static String IP = "127.0.0.1";
	public static int Port = 9090;	
	public static byte[] recv = new byte[NetMsg.NetMsg_HeadLen+NetMsg.NetMsg_BodyLen];
	
	//网络收发函数        字节内部协议   应答方式  返回接收的body 字节数
	public static byte[] send_and_receive(byte[] send_buf) {
//public static byte[] send_and_receive(byte[] send_buf, String ip_addr, int port) {
			byte[] recv_head = new byte[NetMsg.NetMsg_HeadLen]; //包头长度30.
			byte[] recv_body = new byte[NetMsg.NetMsg_BodyLen];
			Arrays.fill(recv, (byte)0);
			// TODO: 容错返回 NULL 情况
			
			
			//新建socket
			Socket socket = new Socket(); 
			InputStream in = null;
			OutputStream out = null;

			try{
			//设置连接超时 500ms
			socket.connect(new InetSocketAddress(IP, Port), 500);
			//设置socket超时 5s
			socket.setSoTimeout(5000);
			//获取输出 输入流
			in = socket.getInputStream();
			out = socket.getOutputStream();
			//输入 flush()
			out.write(send_buf, 0, send_buf.length);
			out.flush();
			
			
			int index = 0;
			int times = 0;
			//循环接收网络数据	//先接收包头标识位 是否验证正确
			while (true) {
				if(in.read(recv_head, index, 1) == 1) {
					break;					
				}
		
				times ++;
				if (times > 100) { //多次接收 不到关闭socket
					socket.close();	
					Log.e("ClientHAL>>send_and_receive()","多次连接不到包头！");
					return null;
				}
			}
			
			if (recv_head[0] != NetMsg.Start) {
				socket.close();
				Log.e("ClientHAL>>send_and_receive()","回包识别头错误！");
				return null;
			}
				
			index ++;
			//再接收包头            包头起始位是否验证正确
			if (in.read(recv_head, index, NetMsg.NetMsg_HeadLen-1) != NetMsg.NetMsg_HeadLen-1) {
					socket.close();
					Log.e("ClientHAL>>send_and_receive()","包头不完整错误！");
					return null;
			}
			
			//index += Msg.HEAD_LENTH-1;
			//解析包头
			NetMsg msg = new NetMsg();
			System.arraycopy(recv_head, 0, msg.msgBuf, 0, NetMsg.NetMsg_HeadLen);
			msg.BufToMsgHead();
			
			
			//判断接收包体
			int body_recved = 0;
			if( (msg.msgType == 3)&&(msg.signalPara != 0) ){
				body_recved = in.read(recv_body, 0, msg.signalPara);
			}
		
		//	recv = new byte[NetMsg.NetMsg_HeadLen+body_recved];
			
			System.arraycopy(recv_head, 0, recv, 0, NetMsg.NetMsg_HeadLen);
			System.arraycopy(recv_body, 0, recv, NetMsg.NetMsg_HeadLen, body_recved);
			
			
			
			} catch (IOException e)
			{
				Log.w("网络出错！socket IOException err", "close socket");
				e.printStackTrace();
			}finally
			{
				try
				{
					in.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				
				try
				{
					out.close();
				} catch (Throwable e)
				{
					e.printStackTrace();
				}
				
				try
				{
					socket.close();
				} catch (Throwable e)
				{
					String mm = e.getMessage().toString();
					Log.w("网络出错！socket err", mm);
				}			
			}
			
			return recv;
		}

}
