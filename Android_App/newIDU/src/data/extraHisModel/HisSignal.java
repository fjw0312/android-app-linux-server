package data.extraHisModel;  

import android.util.Log;

//历史信号 类 历史信号 操作函数 类
//表示一行的本地保存的历史信号数据 
public class HisSignal {
	
	public String equipid;    //设备id
	public String equip_name; //设备名
	public String sigid;      //信号id
	public String name;        //信号名
	public String value;       //信号值
	public String mean;        //含义
	public String value_type;   //信号类型
	public String is_invalid;   //0 --> valid, 1 --> invalid
	public String severity;   //关联告警等级
	public String freshtime = "0";   //历史时间
	

	//将该类成员转换为一个字符串
	public String to_string(){
		
		String buf = null;  
		String a1 = equipid + "`";
		String a2 = equip_name + "`";
		String a3 = sigid + "`";
		String a4 = name + "`";
		String a5 = value + "`";
		String a6 = mean + "`";
		String a7 = value_type + "`";
		String a8 = is_invalid + "`";
		String a9 = severity + "`";
		String a10 = freshtime;
		
		buf = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10;
		
		return buf;
	}
	//将一个字符串转换为该类的成员 字符串数组
	public boolean read_string(String buf){
		
		String[] a  = new String[100];
		
		a = buf.split("`");
//		Log.w("看数组长度2：", Integer.toString(a.length));
		if(a.length != 10){
			return false;
		}
		
		try{
		equipid = a[0];    //设备id
		equip_name = a[1]; //设备名
		sigid = a[2];      //信号id
		name = a[3];        //信号名
		value = a[4];       //信号值
		mean = a[5];        //单位
		value_type = a[6];   //信号类型
		is_invalid = a[7];   //0 --> valid, 1 --> invalid
		severity = a[8];   //关联告警等级
		freshtime = a[9];   //历史时间
	//	Log.e("read_string",equipid+equip_name+sigid+name+value+unit+value_type
	//			+is_invalid+severity+freshtime);
			
		return true;
		}catch(Exception e){
			Log.e("HisSignal>>read_string>>","解析有异常抛出！");	
		}
		return false;
	}
		
}
