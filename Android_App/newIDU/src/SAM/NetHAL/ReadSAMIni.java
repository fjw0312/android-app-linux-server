package SAM.NetHAL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import android.util.Log;

//读取  SAM.ini文件 
public class ReadSAMIni {

	public ReadSAMIni() {
		// TODO Auto-generated constructor stub
	}
	public static Hashtable<Integer,Integer> idlst = new Hashtable<Integer,Integer>(); //<设备id,设备模板id>
	
	public static String SamPath = "/data/fjw/SAM.ini";
	
	//配置文件的采集设备id 链表
	public static List<String> buflist2 = new ArrayList<String>();
	//读取含有某些字符的  所有行字符 如果含有某字符串-整个字符串添加进链表输出，否则输出空链表
	public static List<String> read_str_all_line(String buf){
		String buff = "";
		buflist2.clear();
		try{
			File file = new File(SamPath);
			if(!file.exists()){ //判断文件/目录是否存在 不存在新建
				Log.e("文件："+SamPath,"不存在！");
				return null;
			}
			//判断文件是否可读
			if(!file.canRead()){
				Log.e("文件:"+SamPath,"不可读");
				return null;
			}			
			BufferedReader bufread = new BufferedReader( new FileReader(file));
			while((buff = bufread.readLine())!=null){
				 if(buff.contains(buf)){
					 buflist2.add(buff);
				 }
			}				 
			bufread.close();
			}catch(Exception e){
				Log.e("文件:"+SamPath,"读出字符流失败！");
				return buflist2;
			}
		return buflist2;
	}
	//读取 配置文件 采集的设备id表
	public static Hashtable<Integer,Integer> readSAMIni(){
		List<String> strlst = new ArrayList<String>();
		strlst = read_str_all_line("portRunEquipt");
	    if(strlst == null )  return null;
		for(int i=0;i<strlst.size();i++){
			String str = strlst.get(i);
			String strs[] = str.split("=|#");
			Log.e("NetDataModel->readSAMIni>>>获取的文件设备id：",strs[1]);
			if(strs.length<2) continue;
			int equiptId = Integer.parseInt(strs[1]);
			int pleteTempId = Integer.parseInt(strs[2]); 
			idlst.put(equiptId, pleteTempId);
		}
		return idlst;
	}

}
