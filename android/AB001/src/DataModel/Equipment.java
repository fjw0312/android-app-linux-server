package DataModel;

import java.util.Hashtable;

/*****
 * һ���豸��������
 * �൱��һ�����ݱ�
 * ��Ա��ID name value meaning ht_signal
 * id name value meaning
 */

/**�豸��-һ���źű�*/
public class Equipment {

	public Equipment() {
		// TODO Auto-generated constructor stub
		ht_signal = new Hashtable<Integer,Signal>();
	}
	public Equipment(int id,int e_type,int signalId,int paras) {
		// TODO Auto-generated constructor stub
		equipID=id;		
		type=e_type;
		siganlID=signalId;
		para_data=paras;
		
		ht_signal = new Hashtable<Integer,Signal>();
	}
	public Equipment(int id,int e_type,int signalId,int paras,Hashtable<Integer,Signal> HTsignal) {
		// TODO Auto-generated constructor stub
		equipID=id;	
		type=e_type;
		siganlID=signalId;
		para_data=paras;
		
		ht_signal = new Hashtable<Integer,Signal>();
		ht_signal = HTsignal;
	}
	public int equipID=1;	
	public int type=100;
	
	public int siganlID=0;  //��ʾ���豸���ж��� ��ע���ź�
	public int para_data=0; //��ע���źŵĸ�ֵ����
	
	public String equipName="";  //Ŀǰʵ��δʹ��
	public String meaning="";   //Ŀǰʵ��δʹ��
	
	public Hashtable<Integer,Signal> ht_signal;  //�źű�

}
