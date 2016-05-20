package service_net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

//socket net HAL网络接口   TCP 客户端
public class net_HAL {

	//Fields
	public static String IP="192.168.1.222";
	public static int Port = 6001;
	public byte[]  receviceData; //10k 的数据接收空间
	public String   strRecv;  //简单字符串  收发应答方式
	public String   strSendHead="";//简单字符串  收发应答方式 的发送头
	public String   strRecvHead="";//简单字符串  收发应答方式 的接受头
	
	//构造
	public net_HAL(int mode) {
		// TODO Auto-generated constructor stub
		switch(mode){
		case 1:        //字节内部协议   应答方式
			receviceData = new byte[100*1024+25]; //100k 的数据接收空间
			break;
		case 2:
			strRecv = "";  //200行 字符串
			strSendHead = "send:";
			strRecvHead = "recv:";
			break;
		case 3:
			break;
		default:
			break;
		}
	}
	//收发函数  字节内部协议   应答方式  返回接收的body 字节数
	public int send_later_receive(String ip_addr,int port, byte[] send_buf){
		byte[] recv_head = new byte[25];
		byte[] recv_body = new byte[100*1024]; //100k 的数据接收空间
		
		
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
		
		//等待输入流的回应
		int temp = 0;
		while(temp!=25){
			temp = in.read(recv_head,0,25);  //读取返回的 输入流包头25byte 
		}  
		
		//基本判断回报流 包头起始标识start  		
		if(recv_head[0]==0x01){ //启始标志正确  
//			for(int i=0;i<25;i++){
//				Log.e("service->send_later_receive>>lenth["+ 
//						String.valueOf(i)+"]", String.valueOf(recv_head[i]));			
//			}
			//根据包头解析 包长度
			msg_head msgHead = new msg_head();
			if(!msgHead.parse_msg_head(recv_head)) return 0; 
//			Log.e("service->send_later_receive>>lenth=", String.valueOf(msgHead.lenth));
//			while( temp!=msgHead.lenth ){
				temp = in.read(recv_body,0,100*1024); //获取包体   注意包体大小不应该超过4k-24byte
//				Log.e("service->send_later_receive>>body", String.valueOf(temp));
//				Log.e("service->send_later_receive>>body", String.valueOf(recv_body[0]));
//			}
			
			System.arraycopy(recv_head, 0, receviceData, 0, 25);
			System.arraycopy(recv_body, 0, receviceData, 25, 100*1024);
			
		}
		
		//关闭 流  关闭socket
		socket.close();
		out.close();
		in.close();
		recv_body = null;
		return temp;
		}catch(Exception e){
			Log.e("service-send_and_receive>>","Socket 异常抛出！");
		}finally{
			try{
				//关闭 流  关闭socket
				socket.close();
				out.close();
				in.close();
				recv_body = null;
			}catch(Exception e){			
			}
		}

		
		return 0;
	}
	
	//定义 字符串 发送回报字符串 函数   测试ok
	public String str_send_later_recv(String ip_addr,int port, String send_buf){
		//新建socket
		Log.e("service->str_send_later_recv","into ！");
		Socket socket = new Socket(); 
		
		BufferedReader readrer = null;
		BufferedWriter pwriter = null;
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
		readrer = new BufferedReader(new InputStreamReader(in));
		pwriter = new BufferedWriter(new OutputStreamWriter(out));
		//输入 flush()	
		pwriter.write(send_buf);
		pwriter.flush();
//		out.write(send_buf.getBytes(), 0, send_buf.length());  
//		out.flush();
		Log.e("service->str_send_later_recv","1111111111 ！");
		//等待汇报
		int temp = 0;
//		for(int i=0;i<strRecv.length;i++){
//			strRecv[i] = readrer.readLine();
//			if(strRecv[i]==null) break;
//		}
		char c_buf[] = new char[256];
		int tmp= readrer.read(c_buf);
		String str_recv = new String(c_buf,0,tmp);  //注意只将有效的字符转换
		Log.e("service->str_send_later_recv",String.valueOf(tmp)+"--222222222222 ！  "+str_recv);
		strRecv = str_recv;
		//关闭 流  关闭socket
		socket.close();
		readrer.close();
		pwriter.close();
		out.close();
		in.close();
		}catch(Exception e){
			Log.e("service-send_and_receive>>","Socket 异常抛出！");
		}finally{
			try{
				//关闭 流  关闭socket
				socket.close();
				readrer.close();
				pwriter.close();
				out.close();
				in.close();			
			}catch(Exception e){			
			}
		}
		
		return strRecv;
	}
  //定义网络文件的传送 函数  请求下载文件
  //定义网络文件的传送 函数  请求上传文件
	

}
