package service_net;

import java.util.Iterator;

import DataModel.Equipment;
import DataModel.Model;
import DataModel.Signal;
import android.util.Log;

/**����˿ڷ������-�����շ��߳�*/

public class net_service_thread extends Thread{ 

	public net_service_thread() {
		// TODO Auto-generated constructor stub
	}
	public void run(){
		net_HAL myClient = new net_HAL(1); //�����ֽ�ͨ�� ģʽ
		msg net_msg = new msg();
		//ѭ����������
		while(true){
			//��������ģ�� ���������� 
			int flag = 2;  //����������������		
//			Iterator<Integer> iter = Model.ht_get_equip.keySet().iterator();
//			Iterator<Integer> iter = Model.ht_set_equip.keySet().iterator();
			Iterator<Integer> iter = Model.ht_que_equip.keySet().iterator();
//			Iterator<Integer> iter = Model.ht_cmd_equip.keySet().iterator();
			while(iter.hasNext()){
				int equipID = iter.next();
				
				Equipment equip = Model.get_Equipment_getHt(equipID);
				if(equip!=null){				
					Iterator<Integer> it = equip.ht_signal.keySet().iterator(); 
					while(it.hasNext()){
						int id = it.next();
						Signal sig = equip.ht_signal.get(id);
						Log.e("��ѯ����ģ�͸��豸�ź�>>�豸id="+String.valueOf(equipID),"signalID="
								+String.valueOf(sig.signalID)+" SignalName="+sig.SignalName+" value="
									+String.valueOf(sig.value)	+" meaning="+sig.meaning);
						
					}
				}
			
				int msg_lenth = net_msg.fill_msg(flag, equipID);
				byte[] send_buf = new byte[msg_lenth];
				System.arraycopy(net_msg.buf, 0, send_buf, 0, msg_lenth);  
//				msg msg2 = new msg();
//				msg2.parse_msg(send_buf);
						
				int recv_num = myClient.send_later_receive("192.168.1.222", 6001, send_buf); 
				net_msg.parse_msg(myClient.receviceData);
//				Log.e("net_server->run>>recv_num=", String.valueOf(recv_num));
				
				//�������ģ����  �������������� �Ƿ��п�������Ҫ����
				if(Model.ht_cmd_equip.size()>0){
//					Log.e("net_service_thread->run>>", "into if(Model.ht_cmd_equip)");
//					Iterator<Integer> it = Model.ht_cmd_equip.keySet().iterator();
//					while(it.hasNext()){
//						net_msg.fill_msg(3, equipID);
//						myClient.send_later_receive("192.168.1.222", 6001, net_msg.buf); 
//						net_msg.parse_msg(myClient.receviceData);
//					}
	//				Model model = new Model();
	//				model.clear_Model_ht(3);  //���ƽ��� ���������
	//				model = null;
				}
				
				try{
				Thread.sleep(2000);
				}catch(Exception e){
					
				}
				
				
			}
		
		}
		
	}

}
