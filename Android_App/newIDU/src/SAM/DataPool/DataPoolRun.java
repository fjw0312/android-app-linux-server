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
		//先 获取设备id链表    NetDataModel.super加载 执行了XML cfg文件的解析
		DataModel = new NetDataModel();
		//初始化 设备数据      对数据模型的 设备信号数据链表   设备信号类链表的初始化
		DataProcess.AllEquipt_initSignal();
		
		int k =0;
		while(true){
			//	k++;
			//	if(k==3) break;
			 Log.i("DataPoolRun>>run#TAG-RUN:", ">>>>>>>>>>");
				//遍历 设备id链表
			   if(NetDataModel.lst_poolEquipmentId==null) break;
			   Iterator<Integer> E_Id_lst = NetDataModel.lst_poolEquipmentId.keySet().iterator();
			   while(E_Id_lst.hasNext()){
				   int E_id =  E_Id_lst.next(); //获取设备id
				   Log.i("DataPoolRun>>设备id:", String.valueOf(E_id));
				   //网络通信  获取 设备数据并 解析
				   float recvData[] = DataHAL.getEquiptSigLst(E_id); 
				     //解析出  信号数值
				   DataProcess.parseEquiptData(recvData);                 //测试  ok
				     //解析出 告警信息
				   DataProcess.parseEquiptEvent(recvData);                //测试  ok 
				     //解析出  信号类赋值
				   DataProcess.parseEquiptSig(E_id);                      //测试  ok
				   //发送控制字符				   
				   if(NetDataModel.lst_Pool_SCmdStr.get(E_id) !=null ){   //测试  ok
					   DataHAL.setEquiptStrCmd(E_id, NetDataModel.lst_Pool_SCmdStr.get(E_id));
					   NetDataModel.lst_Pool_SCmdStr.clear();
				   }
				   //发送拓展字符			   
				   if(NetDataModel.lst_pool_MyStr.get(E_id) !=null ){     //
					   DataHAL.setMyStr(E_id, NetDataModel.lst_pool_MyStr.get(E_id));
					   NetDataModel.lst_pool_MyStr.clear();
				   }	
			   //  Test_NetDataModel_printf(E_id);
			   }
			  
				try {
					Thread.sleep(2000);   //通信 周期 2s 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//	DataHAL.setEquiptStrCmd(1, "3,888");
			//	DataHAL.setEquiptBufCmd(1, (short)3, (short)999);
		}

	}
	
	//
	public void Test_NetDataModel_printf(int E_id){ //传入参数 设备id
		//遍历查看  设备信号  数据
		Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++开始打印设备信号类+++++++++++++++++++");
		   Iterator<Integer> iter = NetDataModel.hm_Pool_htSignal.get(E_id).keySet().iterator();
		   while(iter.hasNext()){
			  int id = iter.next();
			  Signal signal = NetDataModel.hm_Pool_htSignal.get(E_id).get(id);
			   Log.e("DataPoolRun>>设备信号类："+String.valueOf(signal.equiptId)+">>"+String.valueOf(signal.sigId), 
					   "》》"+signal.name+"="+signal.value+"含义："+signal.meaning+"类型："+String.valueOf(signal.type));
			}
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++结束打印设备信号类+++++++++++++++++++");
	       //遍历查看  设备信号  数据
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++开始打印设备信号数值+++++++++++++++++++");
		   Iterator<Integer> iter2 = NetDataModel.hm_Pool_htSigValue.get(E_id).keySet().iterator();
		   while(iter2.hasNext()){
			  int id = iter.next();
			  float value = NetDataModel.hm_Pool_htSigValue.get(E_id).get(id);
			   Log.e("DataPoolRun>>设备信号：", String.valueOf(E_id)+">>"+String.valueOf(id)+"="+String.valueOf(value));
		   }
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++结束打印设备信号数值+++++++++++++++++++");
		   //遍历查看  设备告警  信息
		 Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++开始打印设备告警类+++++++++++++++++++");
		   if(NetDataModel.lst_Pool_Event.get(E_id)==null) return;
		   for(int i=0;i<NetDataModel.lst_Pool_Event.get(E_id).size();i++)
		   {
			  Event event = NetDataModel.lst_Pool_Event.get(E_id).get(i);
			  
			   Log.e("DataPoolRun>>设备告警：", "设备id:"+String.valueOf(event.equipId)
					                         +" 告警id:"+String.valueOf(event.eventId)
					                         +" 告警名称:"+event.name
					                         +" 告警含义:"+event.meaning
					                         +" 告警值:"+String.valueOf(event.value)
					                         +" 告警等级:"+String.valueOf(event.grade));
		   }
		Log.e("DataPoolRun>>Test_NetDataModel_printf","++++++++++++++++++结束打印设备告警类+++++++++++++++++++");
	}

}
