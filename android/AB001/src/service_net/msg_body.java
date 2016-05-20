package service_net;

import java.util.Hashtable;
import java.util.Iterator;

import DataModel.Signal;


//通信包体类  ---存放一个类型type的链表 
/****************
 * 多条信息
* nbyte id      eg: 信号id/eventID/controlID/configID 目前只默认使用signal
* nbyte name
* nbyte value
* nbyte meaning
* 1byte #    每一行结束符号
* 说明：每一行的成员以字符串格式发送 再以  【空格】 隔开
*/
public class msg_body {

	//包体数据数组
	public byte[] msg_body_buf = new byte[1024*100];
	//包体信号链表
	public Hashtable<Integer,Signal> ht_siganl = new Hashtable<Integer,Signal>();

	public msg_body() {
		// TODO Auto-generated constructor stub
	}
	//fill 数据包体  返回包体有效数据的长度
	public int fill_msg_body(Hashtable<Integer,Signal> HTsignal){
		if(HTsignal==null) return 0;
		int old_num = 0; //将发送包的每条信号 固定为200
		int i = 0;
		//遍历链表 并赋予数组
		Iterator<Integer> iter = HTsignal.keySet().iterator();
		while(iter.hasNext()){
			int signalID = iter.next();
			Signal signal = HTsignal.get(signalID);
			int num = signal.to_bytes();
			System.arraycopy(signal.bytes, 0, msg_body_buf, old_num, num);
	//		old_num = old_num +num;
			i++;
			old_num = i *  200;
		}
		return old_num;
	}
	//parse 数据包体 返回 信号类链表
	public void parse_msg_body(byte[] buf){
		if(buf==null) return;
		if(buf.length==0) return;
		
		
		ht_siganl.clear();
		byte[] bs = new byte[200];
		for(int i=0;i<(buf.length/200);i++){		
			for(int j=0;j<200;j++)  //清空数组
				bs[j] = '\0';
			System.arraycopy(buf, i*200, bs, 0, 200);
			
			Signal signal = new Signal();
			if(signal.parse_bytes(bs))
				ht_siganl.put(signal.signalID, signal);
		}
		
		return;
	}
	
}
