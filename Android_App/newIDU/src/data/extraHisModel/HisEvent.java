package data.extraHisModel;  

import android.util.Log;

// 历史 告警信号 操作函数类   
//表示一行的本地保存的历史告警数据 
public class HisEvent {
	 
	public String start_time="";   //告警时间        0 
	public String finish_time="";   //结束时间     1	
	public String equip_name=""; //设备名                2
	public String equipid="";    //设备id	  3
	public String event_name="";  //告警名	  4
//	public String sig_name="";    //信号名           
//	public String sigid="";      //信号id     
	public String event_id="";    //告警id    5
	public String severity="";   //关联告警等级  8
	public String value="";       //信号值         11	
	public String event_mean="";  //告警含义     12
	
	public String timelaterStr = ""; //结束时间后面的字符串
	
	//放入该字符串的设备名称
	public boolean set_equiptName(String name){
		equip_name = name;
		return true;
	}
	//放入该字符串的告警名
	public boolean set_eventName(String name){
		event_name = name;
		return true;
	}

		
	//将一个字符串转换为该类的成员 字符串数组
	public boolean read_string(String buf){
		
		String[] a  = new String[100];
		
		a = buf.split(",");
		timelaterStr = buf.substring(40);
//		Log.e("看数组长度2：", Integer.toString(a.length));
//		for(int i=0;i<a.length;i++){			
//			Log.e("读取历史告警的数据："+String.valueOf(i)+":", a[i]);
//		}
		if(a.length != 13){
			return false;
		}
		
		try{
		start_time = a[0];   //告警时间        0
		finish_time = a[1];  //结束时间     1	
		equip_name = a[2];//设备名              2  (中文错乱)
		equipid = a[3];    //设备id	  3
		event_name = a[4]; //告警名	  4   (中文错乱)
		event_id = a[5];    //告警id    5
		severity = a[8];   //关联告警等级  8
		value = a[11];      //信号值         11	
		event_mean = a[12];  //告警含义     12
		//sig_name = "";    //信号名         
	//	Log.e("read_string",equipid+equip_name+sigid+name+value+unit+value_type
	//			+is_invalid+severity+freshtime);
		
		
		return true;
		}catch(Exception e){
			Log.e("HisEvent>>read_string>>","解析有异常抛出！");	
		}
		return false;
	}
		
}
