package DataModel;

import java.util.Hashtable;

/*****
 * 一个设备对象数据
 * 相当于一个数据表
 * 成员：ID name value meaning ht_signal
 * id name value meaning
 */

/**设备类-一个信号表*/
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
	
	public int siganlID=0;  //表示该设备类中额外 关注的信号
	public int para_data=0; //对注意信号的赋值参数
	
	public String equipName="";  //目前实际未使用
	public String meaning="";   //目前实际未使用
	
	public Hashtable<Integer,Signal> ht_signal;  //信号表

}
