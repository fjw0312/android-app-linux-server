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

//socket net HAL����ӿ�   TCP �ͻ���
public class net_HAL {

	//Fields
	public static String IP="192.168.1.222";
	public static int Port = 6001;
	public byte[]  receviceData; //10k �����ݽ��տռ�
	public String   strRecv;  //���ַ���  �շ�Ӧ��ʽ
	public String   strSendHead="";//���ַ���  �շ�Ӧ��ʽ �ķ���ͷ
	public String   strRecvHead="";//���ַ���  �շ�Ӧ��ʽ �Ľ���ͷ
	
	//����
	public net_HAL(int mode) {
		// TODO Auto-generated constructor stub
		switch(mode){
		case 1:        //�ֽ��ڲ�Э��   Ӧ��ʽ
			receviceData = new byte[100*1024+25]; //100k �����ݽ��տռ�
			break;
		case 2:
			strRecv = "";  //200�� �ַ���
			strSendHead = "send:";
			strRecvHead = "recv:";
			break;
		case 3:
			break;
		default:
			break;
		}
	}
	//�շ�����  �ֽ��ڲ�Э��   Ӧ��ʽ  ���ؽ��յ�body �ֽ���
	public int send_later_receive(String ip_addr,int port, byte[] send_buf){
		byte[] recv_head = new byte[25];
		byte[] recv_body = new byte[100*1024]; //100k �����ݽ��տռ�
		
		
		//�½�socket
		Socket socket = new Socket(); 
		InputStream in = null;
		OutputStream out = null;
		try{
		//�������ӳ�ʱ 500ms
		socket.connect(new InetSocketAddress(IP, Port), 500);
		//����socket��ʱ 5s
		socket.setSoTimeout(5000);
		//��ȡ��� ������
		in = socket.getInputStream();
		out = socket.getOutputStream();
		//���� flush()
		out.write(send_buf, 0, send_buf.length);
		out.flush();
		
		//�ȴ��������Ļ�Ӧ
		int temp = 0;
		while(temp!=25){
			temp = in.read(recv_head,0,25);  //��ȡ���ص� ��������ͷ25byte 
		}  
		
		//�����жϻر��� ��ͷ��ʼ��ʶstart  		
		if(recv_head[0]==0x01){ //��ʼ��־��ȷ  
//			for(int i=0;i<25;i++){
//				Log.e("service->send_later_receive>>lenth["+ 
//						String.valueOf(i)+"]", String.valueOf(recv_head[i]));			
//			}
			//���ݰ�ͷ���� ������
			msg_head msgHead = new msg_head();
			if(!msgHead.parse_msg_head(recv_head)) return 0; 
//			Log.e("service->send_later_receive>>lenth=", String.valueOf(msgHead.lenth));
//			while( temp!=msgHead.lenth ){
				temp = in.read(recv_body,0,100*1024); //��ȡ����   ע������С��Ӧ�ó���4k-24byte
//				Log.e("service->send_later_receive>>body", String.valueOf(temp));
//				Log.e("service->send_later_receive>>body", String.valueOf(recv_body[0]));
//			}
			
			System.arraycopy(recv_head, 0, receviceData, 0, 25);
			System.arraycopy(recv_body, 0, receviceData, 25, 100*1024);
			
		}
		
		//�ر� ��  �ر�socket
		socket.close();
		out.close();
		in.close();
		recv_body = null;
		return temp;
		}catch(Exception e){
			Log.e("service-send_and_receive>>","Socket �쳣�׳���");
		}finally{
			try{
				//�ر� ��  �ر�socket
				socket.close();
				out.close();
				in.close();
				recv_body = null;
			}catch(Exception e){			
			}
		}

		
		return 0;
	}
	
	//���� �ַ��� ���ͻر��ַ��� ����   ����ok
	public String str_send_later_recv(String ip_addr,int port, String send_buf){
		//�½�socket
		Log.e("service->str_send_later_recv","into ��");
		Socket socket = new Socket(); 
		
		BufferedReader readrer = null;
		BufferedWriter pwriter = null;
		InputStream in = null;
		OutputStream out = null;
		try{
		//�������ӳ�ʱ 500ms
		socket.connect(new InetSocketAddress(IP, Port), 500);
		//����socket��ʱ 5s
		socket.setSoTimeout(5000);
		//��ȡ��� ������
		in = socket.getInputStream();
		out = socket.getOutputStream(); 
		readrer = new BufferedReader(new InputStreamReader(in));
		pwriter = new BufferedWriter(new OutputStreamWriter(out));
		//���� flush()	
		pwriter.write(send_buf);
		pwriter.flush();
//		out.write(send_buf.getBytes(), 0, send_buf.length());  
//		out.flush();
		Log.e("service->str_send_later_recv","1111111111 ��");
		//�ȴ��㱨
		int temp = 0;
//		for(int i=0;i<strRecv.length;i++){
//			strRecv[i] = readrer.readLine();
//			if(strRecv[i]==null) break;
//		}
		char c_buf[] = new char[256];
		int tmp= readrer.read(c_buf);
		String str_recv = new String(c_buf,0,tmp);  //ע��ֻ����Ч���ַ�ת��
		Log.e("service->str_send_later_recv",String.valueOf(tmp)+"--222222222222 ��  "+str_recv);
		strRecv = str_recv;
		//�ر� ��  �ر�socket
		socket.close();
		readrer.close();
		pwriter.close();
		out.close();
		in.close();
		}catch(Exception e){
			Log.e("service-send_and_receive>>","Socket �쳣�׳���");
		}finally{
			try{
				//�ر� ��  �ر�socket
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
  //���������ļ��Ĵ��� ����  ���������ļ�
  //���������ļ��Ĵ��� ����  �����ϴ��ļ�
	

}
