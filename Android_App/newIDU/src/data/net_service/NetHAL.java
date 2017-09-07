package data.net_service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;


/**socket net HAL����ӿ�   TCP �ͻ���*/
//���ļ�socket ������� ok
public class NetHAL {
	
	public static String IP = "127.0.0.1";
	public static int Port = 9630;	
	
	public NetHAL() {
		// TODO Auto-generated constructor stub
	}
	//�����շ�����        �ֽ��ڲ�Э��   Ӧ��ʽ  ���ؽ��յ�body �ֽ���
	public static byte[] send_and_receive(byte[] send_buf, String ip_addr, int port) {
		byte[] recv_head = new byte[Msg.HEAD_LENTH]; //��ͷ����27.
		byte[] recv_body = new byte[1024*1024];
		byte[] recv = null;
		// TODO: �ݴ��� NULL ���
		
		
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
		
		
		int index = 0;
		int times = 0;
		//ѭ��������������	//�Ƚ��հ�ͷ��ʶλ �Ƿ���֤��ȷ
		while (true) {
			if(in.read(recv_head, index, 1) == 0x01) {
				break;					
			}
	
			times ++;
			if (times > 100) { //��ν��� �����ر�socket
				socket.close();	
				Log.e("NetHAL>>send_and_receive()","������Ӳ�����ͷ��");
				return null;
			}
		}
		
		if (recv_head[0] != 0x01) {
			socket.close();
			Log.e("NetHAL>>send_and_receive()","�ذ�ʶ��ͷ����");
			return null;
		}
			
		index ++;
		//�ٽ��հ�ͷ            ��ͷ��ʼλ�Ƿ���֤��ȷ
		if (in.read(recv_head, index, Msg.HEAD_LENTH-1) != Msg.HEAD_LENTH-1) {
				socket.close();
				Log.e("NetHAL>>send_and_receive()","��ͷ����������");
				return null;
		}
		//index += Msg.HEAD_LENTH-1;
		//������ͷ
		MsgHead head = new MsgHead();
		head.parse_msg_head(recv_head);
		
		int body_lenth = head.body_lenth;
			
		if (body_lenth == 1) {              //��ͷ�����ݴ��ж�
			body_lenth = 0;	
		}
	
		if (body_lenth > recv_body.length) { //����С����ڰ�ͷ��С
			socket.close();  
			return null;
		}
		
		recv = new byte[Msg.HEAD_LENTH+head.body_lenth]; //�����źŰ������ڴ�
		System.arraycopy(recv_head, 0, recv, 0, Msg.HEAD_LENTH);		

		//�ٽ��հ���
		int body_recved = 0;
		times = 0;
		while (body_lenth > 0) {
			body_recved = in.read(recv_body, 0, body_lenth > 1024*1024 ? 1024 * 1024 : head.body_lenth);				
			// �������ȡ��ĩβ�ݴ�
			if (-1 == body_recved)
			{
				socket.close();
				Log.e("NetHAL>>send_and_receive()","������մ���");
				return null;
			}
				
			System.arraycopy(recv_body, 0, recv, Msg.HEAD_LENTH + head.body_lenth - body_lenth, body_recved);
			body_lenth -= body_recved;
			if (0 == body_lenth) break;  //�ж�ÿ��1M �����ݲɼ�����
				
			if (times > 5) {             //����5*1M Ĭ��Ϊ�ɼ�ʧ��
				socket.close();
				Log.e("NetHAL>>send_and_receive()","������ճ��� 5M ����");
				return null;
			}
				
	            times ++; 
		}
		return recv;
		
		} catch (IOException e)
		{
			Log.e("�������socket err", "close socket");
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
				Log.e("�������socket err", mm);
			}			
		}
		
		return null;
	}

}
