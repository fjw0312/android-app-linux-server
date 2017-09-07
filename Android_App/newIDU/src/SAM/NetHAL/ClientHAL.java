package SAM.NetHAL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import android.util.Log;

//���� �ͻ��˽ӿ�
public class ClientHAL {

	public ClientHAL() {
		// TODO Auto-generated constructor stub
	}
	
	public static String IP = "127.0.0.1";
	public static int Port = 9090;	
	public static byte[] recv = new byte[NetMsg.NetMsg_HeadLen+NetMsg.NetMsg_BodyLen];
	
	//�����շ�����        �ֽ��ڲ�Э��   Ӧ��ʽ  ���ؽ��յ�body �ֽ���
	public static byte[] send_and_receive(byte[] send_buf) {
//public static byte[] send_and_receive(byte[] send_buf, String ip_addr, int port) {
			byte[] recv_head = new byte[NetMsg.NetMsg_HeadLen]; //��ͷ����30.
			byte[] recv_body = new byte[NetMsg.NetMsg_BodyLen];
			Arrays.fill(recv, (byte)0);
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
				if(in.read(recv_head, index, 1) == 1) {
					break;					
				}
		
				times ++;
				if (times > 100) { //��ν��� �����ر�socket
					socket.close();	
					Log.e("ClientHAL>>send_and_receive()","������Ӳ�����ͷ��");
					return null;
				}
			}
			
			if (recv_head[0] != NetMsg.Start) {
				socket.close();
				Log.e("ClientHAL>>send_and_receive()","�ذ�ʶ��ͷ����");
				return null;
			}
				
			index ++;
			//�ٽ��հ�ͷ            ��ͷ��ʼλ�Ƿ���֤��ȷ
			if (in.read(recv_head, index, NetMsg.NetMsg_HeadLen-1) != NetMsg.NetMsg_HeadLen-1) {
					socket.close();
					Log.e("ClientHAL>>send_and_receive()","��ͷ����������");
					return null;
			}
			
			//index += Msg.HEAD_LENTH-1;
			//������ͷ
			NetMsg msg = new NetMsg();
			System.arraycopy(recv_head, 0, msg.msgBuf, 0, NetMsg.NetMsg_HeadLen);
			msg.BufToMsgHead();
			
			
			//�жϽ��հ���
			int body_recved = 0;
			if( (msg.msgType == 3)&&(msg.signalPara != 0) ){
				body_recved = in.read(recv_body, 0, msg.signalPara);
			}
		
		//	recv = new byte[NetMsg.NetMsg_HeadLen+body_recved];
			
			System.arraycopy(recv_head, 0, recv, 0, NetMsg.NetMsg_HeadLen);
			System.arraycopy(recv_body, 0, recv, NetMsg.NetMsg_HeadLen, body_recved);
			
			
			
			} catch (IOException e)
			{
				Log.w("�������socket IOException err", "close socket");
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
					Log.w("�������socket err", mm);
				}			
			}
			
			return recv;
		}

}
