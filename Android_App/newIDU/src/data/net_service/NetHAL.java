package data.net_service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;


/**socket net HAL网络接口   TCP 客户端*/
//该文件socket 网络测试 ok
public class NetHAL {
	
	public static String IP = "127.0.0.1";
	public static int Port = 9630;	
	
	public NetHAL() {
		// TODO Auto-generated constructor stub
	}
	//网络收发函数        字节内部协议   应答方式  返回接收的body 字节数
	public static byte[] send_and_receive(byte[] send_buf, String ip_addr, int port) {
		byte[] recv_head = new byte[Msg.HEAD_LENTH]; //包头长度27.
		byte[] recv_body = new byte[1024*1024];
		byte[] recv = null;
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
			if(in.read(recv_head, index, 1) == 0x01) {
				break;					
			}
	
			times ++;
			if (times > 100) { //多次接收 不到关闭socket
				socket.close();	
				Log.e("NetHAL>>send_and_receive()","多次连接不到包头！");
				return null;
			}
		}
		
		if (recv_head[0] != 0x01) {
			socket.close();
			Log.e("NetHAL>>send_and_receive()","回包识别头错误！");
			return null;
		}
			
		index ++;
		//再接收包头            包头起始位是否验证正确
		if (in.read(recv_head, index, Msg.HEAD_LENTH-1) != Msg.HEAD_LENTH-1) {
				socket.close();
				Log.e("NetHAL>>send_and_receive()","包头不完整错误！");
				return null;
		}
		//index += Msg.HEAD_LENTH-1;
		//解析包头
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_head);
		
		int body_lenth = head.body_lenth;
			
		if (body_lenth == 1) {              //包头长度容错判断
			body_lenth = 0;	
		}
	
		if (body_lenth > recv_body.length) { //包大小需大于包头大小
			socket.close();  
			return null;
		}
		
		recv = new byte[Msg.HEAD_LENTH+head.body_lenth]; //定义信号包变量内存
		System.arraycopy(recv_head, 0, recv, 0, Msg.HEAD_LENTH);		

		//再接收包体
		int body_recved = 0;
		times = 0;
		while (body_lenth > 0) {
			body_recved = in.read(recv_body, 0, body_lenth > 1024*1024 ? 1024 * 1024 : head.body_lenth);				
			// 对意外读取到末尾容错
			if (-1 == body_recved)
			{
				socket.close();
				Log.e("NetHAL>>send_and_receive()","包体接收错误！");
				return null;
			}
				
			System.arraycopy(recv_body, 0, recv, Msg.HEAD_LENTH + head.body_lenth - body_lenth, body_recved);
			body_lenth -= body_recved;
			if (0 == body_lenth) break;  //判断每次1M 总数据采集结束
				
			if (times > 5) {             //超过5*1M 默认为采集失败
				socket.close();
				Log.e("NetHAL>>send_and_receive()","包体接收超过 5M 出错！");
				return null;
			}
				
	            times ++; 
		}
		return recv;
		
		} catch (IOException e)
		{
			Log.e("网络出错！socket err", "close socket");
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
				Log.e("网络出错！socket err", mm);
			}			
		}
		
		return null;
	}

}
