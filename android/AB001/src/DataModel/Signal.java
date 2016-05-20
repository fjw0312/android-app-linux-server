package DataModel;

import android.util.Log;

/*****
 * 基础数据类
 * 一个单元数据
 * 成员：
 * id name value meaning
 */
/**信号类*/
public class Signal {

	public Signal() {
		// TODO Auto-generated constructor stub
	}
	public Signal(int id,String name,float s_value,String mean) {
		// TODO Auto-generated constructor stub
		signalID=id;
		SignalName=name;
		value=s_value;
		meaning=mean;
	}
	public int signalID=1;
	public String SignalName="";
	public float value=0;
	public String meaning="";
	
	public byte[] bytes;  //辅助变量
	public String[] a;    //辅助变量
	
/************************************函数接口方法****************************************/	
	//由于要将信号类打包数据发送 故 设定信心类的数据打包格式 为
	//String id +`+ Sting name +`+ String value+`+ String meaning +#
	public String to_string(){
		String strbuf = "";
		
		String a1 = String.valueOf(signalID)+"`";
		String a2 = SignalName+"`";
		String a3 = String.valueOf(value)+"`";
		String a4 = meaning+"#";
		
		strbuf = a1+a2+a3+a4;
		return strbuf;
	}
	//将字符串解析赋值类的成员
	public boolean parse_string(String strbuf){
		if("".equals(strbuf)) return false;
		
		a = new String[100];  //暂设一个信号类100个字符
		a = strbuf.split("`");
		
		signalID = Integer.parseInt(a[0]);
		SignalName = a[1];
		value =	Float.parseFloat(a[2]);
		meaning = a[3];
		
//		Log.e("Signal->parse_string>>","signalID="
//		+String.valueOf(signalID)+" SignalName="+SignalName+" value="
//			+String.valueOf(value)	+" meaning="+meaning);
		return true;
	}
	public int to_bytes(){
		bytes = new byte[400]; //暂设一个信号类不超过400byte
		
		String strbuf = to_string();
		byte[] bs = strbuf.getBytes();
		System.arraycopy(bs, 0, bytes, 0, bs.length);
		
		return bs.length;
	}
	public boolean parse_bytes(byte[] bs){
		if(bs==null) return false;
		if(bs[0]=='\0') return false;
		if(bs[0]==0) return false;
		String str = new String(bs);
		String str1[] =  str.split("#");
		if( parse_string(str1[0]) )
			return true;
		return false;
	}

}
