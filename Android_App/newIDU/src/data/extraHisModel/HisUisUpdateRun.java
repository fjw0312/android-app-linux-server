package data.extraHisModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import app.main.idu.VObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;



//author: fang 
//date: 2016.11.17 
//���ǵ�  ���߳�������ÿһ��ҳ��  ����ʵ�� ����ϵͳ���������������ʷ�ؼ�ˢ��  ��������
/**��ʷ�ؼ�ui ���ݽ��� ˢ���߳�*/
public class HisUisUpdateRun extends Thread{
	public HisUisUpdateRun() {
		// TODO Auto-generated constructor stub
		mapUIs= new HashMap<String,VObject>();
	}
	
	//����
	public HashMap<String, VObject> mapUIs = null; //��Ҫˢ�� ����ʷ�ؼ�  <�ؼ�id�� �ؼ���>
	
	//����Handler
	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){  
			case 0:
				break;
			case 1:
				//��ȡhandler ��Ϣ ������Ӧuis-id ��ui
				String id = String.valueOf(msg.obj);
				
				if(id != null && mapUIs.get(id).getViews().isShown())					
					mapUIs.get(id).doInvalidate();  //���ÿؼ��ڲ� ui���º���
				Log.e("HisUisUpdateRun>>myHandler>>>֪ͨˢ�£�",  mapUIs.get(id).getViewsType());
				break;
			default:  
				break; 
			}

		}	
	};
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try{
			Thread.sleep(1000*20);
		}catch(Exception e){
			
		}
		while(true){
			try{
//				Log.e("HisUisUpdateRun>>run()","into��");
				Thread.sleep(5000);
				if(mapUIs==null) continue;
				//�����ؼ�
				Iterator<String> ids = mapUIs.keySet().iterator();
				while(ids.hasNext()){
					String id = ids.next();
					VObject obj = mapUIs.get(id);
					if(obj==null) continue;
					boolean up = obj.updataValue("����"); //���ÿؼ��Զ����ݸ��·���					
					if(up){  //�жϿؼ����ݸ��� �����Ƿ�ɹ�
						Message msg = new Message();
						msg.obj = id;
						msg.what = 1;
						myHandler.sendMessage(msg);   //����handler ��Ϣ 
						Log.e("HisUisUpdateRun>>run()>>>������Ϣ��", obj.getViewsType());
					}
				}
				
			}catch(Exception e){
				Log.e("HisUisUpdateRun>>run()","�쳣�׳���");
			}
		}
	}



}
