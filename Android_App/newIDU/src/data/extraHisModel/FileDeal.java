package data.extraHisModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

//FileDeal: 本地储存 文件的 文件 操作 函数处理类
//author: fang
//date:  2016 10 18 
//function:保存实时数据到本地文件夹，并实时调用文件夹的历史数据 
public class FileDeal {

//		String path = "/mnt/sdcard/tmp/";
/*		String his_equip_path = "/mgrid/log/HisEquip/";     //type:1
		String his_signal_path = "/mgrid/log/HisSignal/";   //type:2
		String his_event_path = "/mgrid/log/event/";        //type:3 
		//String RC_signal = "/mgrid/log/HisFormula/";		
		String his_formulaLine_path = "/mgrid/log/HisFormula/Line/"; //pue运算式子曲线 保存文件路径                             //type:10
		String his_formulaDay_path = "/mgrid/log/HisFormula/Day/";  //天用电量/pue分布  运算式子曲线 保存文件路径  //type:11
		String his_formulaMon_path = "/mgrid/log/HisFormula/Mon/";  //月用电量/pue分布 运算式子曲线 保存文件路径   //type:12
		String his_formulaYear_path = "/mgrid/log/HisFormula/Year/"; //年用电量/pue分布运算式子曲线 保存文件路径    //type:13
*/		
		String his_equip_path = "/data/ChaoYF/App_Data/HisEquipt/";     //type:1
		String his_signal_path = "/data/ChaoYF/App_Data/HisSignal/";   //type:2
		String his_event_path = "/data/ChaoYF/App_Data/history/";        //type:3 	
		String his_formulaLine_path = "/data/ChaoYF/App_Data/HisFormula/Line/"; //pue运算式子曲线 保存文件路径                             //type:10
		String his_formulaDay_path = "/data/ChaoYF/App_Data/HisFormula/Day/";  //天用电量/pue分布  运算式子曲线 保存文件路径  //type:11
		String his_formulaMon_path = "/data/ChaoYF/App_Data/HisFormula/Mon/";  //月用电量/pue分布 运算式子曲线 保存文件路径   //type:12
		String his_formulaYear_path = "/data/ChaoYF/App_Data/HisFormula/Year/"; //年用电量/pue分布运算式子曲线 保存文件路径    //type:13
	
		File file = null;   //文件类变量
		Writer fws = null;   //写入字符流
		Reader frs = null;   //读出字符流   
		InputStream fis = null;  //读取字节流
		OutputStream fos = null; //写入字节流
		BufferedReader bufread = null; //高级读取文本流
		PrintWriter  priwrite = null; //高级写入文本流
		public static int num_signal = 0;  //标识写入信号个数
//		public static int w_line_num = 0;  //文件写入的信号行数
//		public static int r_line_num = 0;  //文件已经读取的信号行数
		
		

	//判断文件是否为空等
	public boolean has_file(String filename , int who){
		if(who == 1) file = new File(his_equip_path+filename+".dat");
		else if(who == 2) file = new File(his_signal_path+filename+".dat");
		else if(who == 3) file = new File(his_event_path+filename+".dat");
//		else if(who == 4) file = new File(RC_signal+filename+".dat");
		
		else if(who == 10) file = new File(his_formulaLine_path+filename+".dat");
		else if(who == 11) file = new File(his_formulaDay_path+filename+".dat");
		else if(who == 12) file = new File(his_formulaMon_path+filename+".dat");
		else if(who == 13) file = new File(his_formulaYear_path+filename+".dat");
		if(!file.exists()){ //判断文件/目录是否存在 不存在新建
			try{
			file.createNewFile(); //新建目录/文件
			}catch(IOException e){
				return false;
			}
		}
		//判断文件是否可读
		if(!file.canRead()){
			Log.e("文件","不可读");
			return false;
		}	
				
		//判断文件是否可写
		if(!file.canWrite()){
			Log.e("文件","不可写");
			return false;
		}
		
/*		try{
			fis = new FileInputStream(file);
			fos = new FileOutputStream(file);
		}catch(Exception e){
			Log.e("文件","流读取失败！");
		}
*/		
		return true;
	}
	
	//写入字符串
	public boolean write_buf(String buf){
		
		try{
		fws = new FileWriter(file);
		fws.write(buf);
		fws.close();
		}catch(Exception e){
			Log.e("文件","写入字符流失败！");
		}
		
		return true;
	}
	//读出字符串
	public String read_buf(String buf){
		
		try{
			char c[] = new char[100];
			frs = new FileReader(file);
			for(int i=0; (frs.read(c))!=-1;i++);
			frs.close();
			buf = c.toString();
			}catch(Exception e){
				Log.e("文件","读出字符流失败！");
			}
		
		return buf;
	}
	//写入字字节
	public boolean write_byte(int buf){
			
		try{
			fos = new FileOutputStream(file);
			fos.write(buf);
			fos.close();
			}catch(Exception e){
				Log.e("文件","写入字节流失败！");
			}
		
			return true;
	}
	//读出字节
	public int[] read_byte(int[] buf){
			
		try{		
			fis = new FileInputStream(file);
			for(int i=0; (buf[i]=fis.read())!=-1;i++);
			fis.close();
			}catch(Exception e){
				Log.e("文件","读出字节流失败！");
			}
				
			return buf;
	}
	//写入 传入字符串
	public boolean write_str(String buf){

		
		try{
			priwrite = new PrintWriter(new FileWriter(file,true),true); //true尾部写入行字符串
			priwrite.print(buf);  //尾部写入行字符串
			priwrite.close();
			}catch(Exception e){
				Log.e("文件","写入字符流失败！");
			}
		
			return true;
	}	
	//写入行 传入字符串
	public boolean write_line(String buf){

		
		try{
			priwrite = new PrintWriter(new FileWriter(file,true),true); //true尾部写入行字符串
			priwrite.println(buf);  //尾部写入行字符串
			priwrite.close();
			}catch(Exception e){
				Log.e("文件","写入字符流失败！");
			}
		
			return true;
	}
	
	//读出行  头一行字符 返回字符串
	public String read_line(){				
		
		try{
			bufread = new BufferedReader( new FileReader(file));
			if((buf_line = bufread.readLine())==null){
				bufread.close();
				return null;
				}
			bufread.close();				
			}catch(Exception e){
				Log.e("文件","读出字符流失败！");
				return null;
			}
						
			return buf_line;
	}
	//写入多行 传入字符串
	public boolean write_lines(String[] bufs){
		if(bufs==null) return false;
		
		try{
			priwrite = new PrintWriter(new FileWriter(file,true),true); //true尾部写入行字符串
			for(int i=0;i<bufs.length;i++){
				priwrite.println(bufs[i]);  //尾部写入行字符串
			}
			priwrite.close();
			}catch(Exception e){
				Log.e("文件","写入字符流失败！");
			}
		
			return true;
	}
	//读文件  文件最后一行字符
		public String read_later_line(){							
			
			try{
				 String line = "";
				 bufread = new BufferedReader( new FileReader(file));
				 while((line = bufread.readLine())!=null){	
					 buf_later_line = line;
				  }
				// Log.i("local_file->read_later_line读取到的最后一行字符：",buf_later_line);
				  bufread.close();
				}catch(Exception e){
					Log.e("文件","读出字符流失败！");
					return null;
				}
							
				return buf_later_line;
		}
	
	//读出行  所有行字符 返回字符串数组
		public boolean read_all_line(){
				
			String buf = new String();
			
			buflist1.clear();	
//			buflist1 = new ArrayList<String>();
			try{
				bufread = new BufferedReader( new FileReader(file));
				while((buf = bufread.readLine())!=null){
					buflist1.add(buf);
				}
				bufread.close();				
				}catch(Exception e){
					Log.e("文件","读出字符流失败！");
					return false;
				}
					
				return true;
		}
		 
	//读取含有某些字符的  行字符 如果含有某字符串-整个字符串输出，否则输出空字符
		public String read_str_line(String buf){
			buf_str_line = "";
			String buff = "";
			try{
				 bufread = new BufferedReader( new FileReader(file));
				 while((buff = bufread.readLine())!=null){
			//		 if(buf_str_line.indexOf(buf)!= -1)  break;	 //包含某字符串就退出while
					 if(buff.contains(buf)){
						 buf_str_line = buff;
						 break;
					 }
				  }				 
				  bufread.close();
				}catch(Exception e){
					Log.e("文件","读出字符流失败！");
					return buf_str_line;
				}
			return buf_str_line;
		}
	//读取含有某些字符的  所有行字符 如果含有某字符串-整个字符串添加进链表输出，否则输出空链表
		public List<String> read_str_all_line(String buf){
			String buff = "";
			buflist2.clear();
			try{
				 bufread = new BufferedReader( new FileReader(file));
				 while((buff = bufread.readLine())!=null){
			//		 if(buf_str_line.indexOf(buf)!= -1)  break;	 //包含某字符串就退出while
					 if(buff.contains(buf)){
						 buflist2.add(buff);
					 }
				  }				 
				  bufread.close();
				}catch(Exception e){
					Log.e("文件","读出字符流失败！");
					return buflist2;
				}
			return buflist2;
		}	
	
		public List<String> buflist1 = new ArrayList<String>();
		public List<String> buflist2 = new ArrayList<String>();
		String buf_line = new String();
		String buf_later_line = new String();
		String buf_str_line = new String();
}
