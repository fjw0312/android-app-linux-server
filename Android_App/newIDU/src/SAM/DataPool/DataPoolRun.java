package SAM.DataPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import SAM.DataPoolModel.Event;
import SAM.DataPoolModel.Signal;
import SAM.NetHAL.ClientHAL;
import SAM.NetHAL.DataHAL;
import SAM.NetHAL.NetMsg;
import SAM.NetHAL.NetRS;
import android.util.Log;

public class DataPoolRun extends Thread{

	public DataPoolRun() {
		// TODO Auto-generated constructor stub
		
	}
	
	public NetDataModel DataModel = null; 
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		//�� ��ȡ�豸id����    NetDataModel.super���� ִ����XML cfg�ļ��Ľ���
		DataModel = new NetDataModel();
		//��ʼ�� �豸����      ������ģ�͵� �豸�ź���������   �豸�ź�������ĳ�ʼ��
		DataProcess.AllEquipt_initSignal();
		
		int k =0;
		while(true){
			//	k++;
			//	if(k==3) break;
			 Log.i("DataPoolRun>>run#TAG-RUN:", ">>>>>>>>>>");
				//���� �豸id����
			   if(NetDataModel.lst_poolEquipmentId==null) break;
			   Iterator<Integer> E_Id_lst = NetDataModel.lst_poolEquipmentId.keySet().iterator();
			   while(E_Id_lst.hasNext()){
				   int E_id =  E_Id_lst.next(); //��ȡ�豸id
				   Log.i("DataPoolRun>>�豸id:", String.valueOf(E_id));
				   //����ͨ��  ��ȡ �豸���ݲ� ����
				   float recvData[] = DataHAL.getEquiptSigLst(E_id); 
				     //������  �ź���ֵ
				   DataProcess.parseEquiptData(recvData);                 //����  ok
				     //������ �澯��Ϣ
				   DataProcess.parseEquiptEvent(recvData);                //����  ok 
				     //������  �ź��ำֵ
				   DataProcess.parseEquiptSig(E_id);                      //����  ok
				   //���Ϳ����ַ�				   
				   if(NetDataModel.lst_Pool_SCmdStr.get(E_id) !=null ){   //����  ok
					   DataHAL.setEquiptStrCmd(E_id, NetDataModel.lst_Pool_SCmdStr.get(E_id));
					   NetDataModel.lst_Pool_SCmdStr.clear();
				   }
				   //������չ�ַ�			   
				   if(NetDataModel.lst_pool_MyStr.get(E_id) !=null ){     //
					   DataHAL.setMyStr(E_id, NetDataModel.lst_pool_MyStr.get(E_id));
					   NetDataModel.lst_pool_MyStr.clear();
				   }	
			   //  Test_NetDataModel_printf(E_id);
			   }
			  
				try {
					Thread.sleep(2000);   //ͨ�� ���� 2s 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//	DataHAL.setEquiptStrCmd(1, "3,888");
			//	DataHAL.setEquiptBufCmd(1, (short)3, (short)999);
		}

	}
	
	//
	public void Test_NetDataModel_printf(int E_id){ //������� �豸id
		//�����鿴  �豸�ź�  ����
		Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++��ʼ��ӡ�豸�ź���+++++++++++++++++++");
		   Iterator<Integer> iter = NetDataModel.hm_Pool_htSignal.get(E_id).keySet().iterator();
		   while(iter.hasNext()){
			  int id = iter.next();
			  Signal signal = NetDataModel.hm_Pool_htSignal.get(E_id).get(id);
			   Log.e("DataPoolRun>>�豸�ź��ࣺ"+String.valueOf(signal.equiptId)+">>"+String.valueOf(signal.sigId), 
					   "����"+signal.name+"="+signal.value+"���壺"+signal.meaning+"���ͣ�"+String.valueOf(signal.type));
			}
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++������ӡ�豸�ź���+++++++++++++++++++");
	       //�����鿴  �豸�ź�  ����
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++��ʼ��ӡ�豸�ź���ֵ+++++++++++++++++++");
		   Iterator<Integer> iter2 = NetDataModel.hm_Pool_htSigValue.get(E_id).keySet().iterator();
		   while(iter2.hasNext()){
			  int id = iter.next();
			  float value = NetDataModel.hm_Pool_htSigValue.get(E_id).get(id);
			   Log.e("DataPoolRun>>�豸�źţ�", String.valueOf(E_id)+">>"+String.valueOf(id)+"="+String.valueOf(value));
		   }
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++������ӡ�豸�ź���ֵ+++++++++++++++++++");
		   //�����鿴  �豸�澯  ��Ϣ
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++��ʼ��ӡ�豸�澯��+++++++++++++++++++");
		   if(NetDataModel.lst_Pool_Event.get(E_id)==null) return;
		   for(int i=0;i<NetDataModel.lst_Pool_Event.get(E_id).size();i++)
		   {
			  Event event = NetDataModel.lst_Pool_Event.get(E_id).get(i);
			  
			   Log.e("DataPoolRun>>�豸�澯��", "�豸id:"+String.valueOf(event.equipId)
					                         +" �澯id:"+String.valueOf(event.eventId)
					                         +" �澯����:"+event.name
					                         +" �澯����:"+event.meaning
					                         +" �澯ֵ:"+String.valueOf(event.value)
					                         +" �澯�ȼ�:"+String.valueOf(event.grade));
		   }
		Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++������ӡ�豸�澯��+++++++++++++++++++");
	}

}
