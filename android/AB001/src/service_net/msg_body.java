package service_net;

import java.util.Hashtable;
import java.util.Iterator;

import DataModel.Signal;


//ͨ�Ű�����  ---���һ������type������ 
/****************
 * ������Ϣ
* nbyte id      eg: �ź�id/eventID/controlID/configID ĿǰֻĬ��ʹ��signal
* nbyte name
* nbyte value
* nbyte meaning
* 1byte #    ÿһ�н�������
* ˵����ÿһ�еĳ�Ա���ַ�����ʽ���� ����  ���ո� ����
*/
public class msg_body {

	//������������
	public byte[] msg_body_buf = new byte[1024*100];
	//�����ź�����
	public Hashtable<Integer,Signal> ht_siganl = new Hashtable<Integer,Signal>();

	public msg_body() {
		// TODO Auto-generated constructor stub
	}
	//fill ���ݰ���  ���ذ�����Ч���ݵĳ���
	public int fill_msg_body(Hashtable<Integer,Signal> HTsignal){
		if(HTsignal==null) return 0;
		int old_num = 0; //�����Ͱ���ÿ���ź� �̶�Ϊ200
		int i = 0;
		//�������� ����������
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
	//parse ���ݰ��� ���� �ź�������
	public void parse_msg_body(byte[] buf){
		if(buf==null) return;
		if(buf.length==0) return;
		
		
		ht_siganl.clear();
		byte[] bs = new byte[200];
		for(int i=0;i<(buf.length/200);i++){		
			for(int j=0;j<200;j++)  //�������
				bs[j] = '\0';
			System.arraycopy(buf, i*200, bs, 0, 200);
			
			Signal signal = new Signal();
			if(signal.parse_bytes(bs))
				ht_siganl.put(signal.signalID, signal);
		}
		
		return;
	}
	
}
