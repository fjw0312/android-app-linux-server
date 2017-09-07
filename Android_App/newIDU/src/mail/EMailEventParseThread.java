package mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import android.util.Log;
import app.main.idu.MainActivity;
import data.net_model.Net_control;
import data.net_service.DataHAL;
import data.net_service.NetHAL;
import data.pool.DataPoolModel;
import data.pool_model.Event;



//�ʼ� �澯��Ϣ ���� �߳�       fang  Add
public class EMailEventParseThread extends Thread{

	public EMailEventParseThread() {
		// TODO Auto-generated constructor stub
	}
	
	//����澯��Ϣ���� ���ڸ澯�̴߳���
	public static List<String> eventLst = new ArrayList<String>(); 
	public static List<String> old_eventLst = new ArrayList<String>();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		EmailHandler.EmailHandler_init();   //��ʼ�� ��ȡ �ʼ������ļ� 
		try {			
			Thread.sleep(5000);  //��ʱ����
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true){
			if(EmailHandler.runFlag==false){ 
				try{
					Thread.sleep(2000); //1s�������߳�
				}catch(Exception e){
					
				}
				if("".equals(MainActivity.EventCmd)){
					continue;
				}				
			}
			eventLst.clear();
			if(DataPoolModel.getAllEvent() !=null){				
				//�����澯��
				List<Event> lst = DataPoolModel.getAllEvent();
				for(int i=0;i<lst.size();i++){
					Event event = lst.get(i);  
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ʱ���ʽת��
					Date date = new Date(event.starttime*1000);
					String sampletime = formatter.format(date);	
					//����澯�ȼ�
					String grade = "һ��澯";
					switch(event.grade){ 
							case 1:  grade = "֪ͨ";
								break;
							case 2:  grade = "һ��澯";
								break; 
							case 3:  grade = "���ظ澯";
								break;
							case 4:  grade = "�����澯";
								break;
							default:
								break;
					}
							String str = "�豸��"+DataPoolModel.getEquipment(event.equipId).equipName.trim()+" \n"
									+"�澯���ƣ�"+event.name.trim()+" \n"
									+"�澯�ȼ���"+grade+" \n"
							//		+event.value+" "
									+"�澯���壺"+event.meaning.trim()+" \n"
									+"��ʼʱ�䣺"+sampletime+" \n ";
							eventLst.add(str);	
				}											
			} 
			//--------------------
			//�жϸ澯��Ϣ�Ƿ�Ϊ�²��� 
			for(int i=0;i<eventLst.size();i++){
					if( old_eventLst.contains(eventLst.get(i)) ){//�澯֮ǰ�Ѳ���
					//	Log.e("TAG1", eventLst.get(i));
					}else{//�澯�²��� 
						Log.i("TAG2-�¸澯����", eventLst.get(i));//�¸澯����	
						//�澯����
						if(eventLst.get(i).contains(MainActivity.EventCmd.trim())){
							Log.e("TAG-EventCmd>>�澯���� �澯����", MainActivity.EventCmd);//�¸澯����
							EventCmdThread eventCmdThread = new EventCmdThread();
							eventCmdThread.value = "1";
							eventCmdThread.start();
						}
						//���ʼ�----------------
						if(EmailHandler.runFlag){
							EmailHandler email = new EmailHandler();
							email.content = "==�����澯==\n"+eventLst.get(i);
							email.E_Handler();
						}
					} 
				}
				//�жϸ澯��ʧ
				if(old_eventLst.size()>eventLst.size()){ 
					for(int i=0;i<old_eventLst.size();i++){ 
						if( eventLst.contains(old_eventLst.get(i))==false ){//�澯��ʧ�ĸ澯��Ϣ
							long time = java.lang.System.currentTimeMillis();//���²ɼ�ʱ��	
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ʱ���ʽת��
							Date date = new Date(time);
							String sampletime = formatter.format(date);
							String str = old_eventLst.get(i).substring(0, old_eventLst.get(i).length()-1)
									+"����ʱ�䣺"+sampletime; 
							Log.i("TAG3-�澯����", str); //�澯��������Ϣ 
							//�澯����
							if(old_eventLst.get(i).contains(MainActivity.EventCmd.trim())){
								Log.e("TAG-EventCmd>>�澯���� �澯��ʧ", MainActivity.EventCmd);//�¸澯����
								EventCmdThread eventCmdThread = new EventCmdThread();
								eventCmdThread.value = "0";
								eventCmdThread.start();
							}
							//���ʼ� ----------------
							if(EmailHandler.runFlag){
								EmailHandler email = new EmailHandler();
								email.content = "==�澯����==\n"+str; 
								email.E_Handler();
							}
						} 
					}
				}
				
				old_eventLst.clear();
				for(int i=0;i<eventLst.size();i++){
					old_eventLst.add(eventLst.get(i));
				}
				eventLst.clear();

				try{
					Thread.sleep(1000); //1s�������߳�
				}catch(Exception e){
					
				}
		}
	}
	//�澯����
	private class EventCmdThread extends Thread{
		String value = "";
		@Override	
		public void run() {
			// TODO Auto-generated method stub
			super.run();
	    	List<Net_control> lstCtrl = new ArrayList<Net_control>();
	    	Net_control ipcC = new Net_control();
			ipcC.equipid = 1;  //���������豸id
			ipcC.ctrlid = 1;   //���������豸cmdId 
			ipcC.valuetype = 1;  //Parameter
			ipcC.value = value;
			lstCtrl.add(ipcC);
			DataHAL.send_control_cmd(NetHAL.IP, NetHAL.Port,lstCtrl);
			Log.e("Do_EventCmd>�澯����", "���ƣ�");//�¸澯����   
			lstCtrl = null;
			ipcC = null;
		}

	}

}
