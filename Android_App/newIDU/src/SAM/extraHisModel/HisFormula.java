package SAM.extraHisModel;

import android.util.Log;

//用于保存eg: 总电能-空调耗能（其他耗能）用于计算PUE曲线  能耗分布 用电量 等
/** 历史 数据表达式 类 eg:8-2*/
public class HisFormula {

	public String getTime = ""; // 运算式子 获取时间
	public String strContent = ""; //运算式子 获取内容
	public String strTime = ""; //采集时间  "yyyy.MM.dd HH:mm:ss" 
	public String strEndContent = "";//当天结束 时 式子 内容
	
	public HisFormula() {
		// TODO Auto-generated constructor stub
	}
	
	// add signal value
	public void add_string(String str){ 
		if("".equals(str)) return;
		if("".equals(strContent)){
			strContent = str;
		}else{
			strContent = strContent + "&" + str;	
		}	
	}
	
	//将该类成员转换为一个字符串  不包含结束 内容
	public String to_string(){
		String buf = "";
		String a1 = strContent + "`";
		String a2 = getTime + "`";
		String a3 = strTime + "`";
		
		buf = a1+a2+a3;
		return buf;
	}
	//将一个字符串转换为该类的成员 字符串数组
	public boolean read_string(String buf){
		String[] a  = new String[100];
		
		a = buf.split("`");
//		Log.w("看数组长度2：", Integer.toString(a.length));
		if(a.length < 1){
			return false;
		}
		try{
		strContent = a[0];    //内容
		getTime = a[1];       //时间
		strTime = a[2];       //时间
		}catch(Exception e){
			Log.e("HisFormula>>read_string>>","解析有异常抛出！");  
			return false;
		}
		try{
			strEndContent = a[3]; 
		}catch(Exception e){
//			Log.e("HisFormula>>read_string>>","解析有异常抛出！");	
		}

		return true;
	}

}
