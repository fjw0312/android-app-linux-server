package data.extraHisModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import view.UtTable;
import data.net_model.Net_data_signal;
import data.net_service.DataHAL;
import data.net_service.NetHAL;
import data.pool.DataPoolModel;
import android.util.Log;

//历史 方面 数据  保存  读取 目前验证 基本 正常！
/*历史 数据 模型的内部 自动 保存历史数据 类**/
public class HisDataDAO {

	//对外注册  链表
	public static List<String> hisEquipId_lst = new ArrayList<String>(); //<设备id> 历史设备id链表
	public static List<String> hisSignalId_lst = new ArrayList<String>();//<设备id-信号id>  历史信号 设备id信号id链表
	public static List<String> hisFormulaId_lst = new ArrayList<String>();//<设备id-信号id&设备id-信号id>
	
	public static List<HisEvent> hisEvent_lst = new ArrayList<HisEvent>(); //存放获取的 某个设备的所有历史告警链表
	public static List<HisSignal> oneDay_hisEquipSig_lst = new ArrayList<HisSignal>();  //存放获取的 某个设备某天的 历史设备数据 信号链表
	public static List<HisSignal> oneDay_hisSignal_lst = new ArrayList<HisSignal>(); //存放获取的某个 信号 某天的 历史信号链表
	
	public static List<HisFormula> pueLine_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息 pue曲线 链表
	
	public static List<HisFormula> day_RCBar_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息   天用电量  链表
	public static List<HisFormula> day_pueBar_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息  天pue  链表
	public static List<HisFormula> mon_RCBar_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息    月用电量  链表
	public static List<HisFormula> mon_pueBar_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息  月pue  链表
	public static List<HisFormula> year_RCBar_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息   年用电量  链表
	public static List<HisFormula> year_pueBar_hisFormula_lst = new ArrayList<HisFormula>(); //存在 获取的 某个式子 历史信息  年pue  链表

	/**添加Formula_Id*/ //该函数可直接手动添加  挺好的
	public static void add_hisFormula_Id(String Formula_Id){ //参数格式9-1&8-2
		if("".equals(Formula_Id)) return;
		if(hisFormulaId_lst.contains(Formula_Id)) return;
	//	hisFormulaId_lst.add("9-1&8-2");
		hisFormulaId_lst.add(Formula_Id);
		
	}

	//根据 id 链表 保存实时 数据函数---------------
	/** 保存 实时设备*/
	public static void saveHisEquip(){
		if(hisEquipId_lst==null) return;
		
		try{
		//遍历链表
			for(int i=0; i<hisEquipId_lst.size(); i++){ 
				//获取 实时设备类-信号链表		
				int id = Integer.parseInt(hisEquipId_lst.get(i));
				List<Net_data_signal> net_signal_lst = DataHAL.get_signal_data_list(NetHAL.IP, NetHAL.Port, id);
				if(net_signal_lst==null) continue;
				String buf[] = new String[net_signal_lst.size()];
				String date = "";
				for(int j=0; j<net_signal_lst.size(); j++){
					Net_data_signal net_signal = net_signal_lst.get(j);
					HisSignal hisSignal = new HisSignal();
					hisSignal.equipid = hisEquipId_lst.get(i);
					int EquiptId = Integer.parseInt(hisSignal.equipid);
					hisSignal.equip_name = DataPoolModel.getEquipment(EquiptId).equipName; //*
					hisSignal.sigid = String.valueOf(net_signal.sigid);
					int SignalId = Integer.parseInt(hisSignal.sigid);
					hisSignal.name = DataPoolModel.getSignal(EquiptId, SignalId).name; //*
					hisSignal.value = net_signal.value;
					hisSignal.mean = net_signal.meaning;
					hisSignal.value_type = String.valueOf(net_signal.value_type);
					hisSignal.is_invalid = String.valueOf(net_signal.is_invalid);
					hisSignal.severity = String.valueOf(net_signal.severity);
					hisSignal.freshtime = String.valueOf(net_signal.freshtime);
					long l_date = Long.parseLong(hisSignal.freshtime) * 1000;
					date = UtTable.getDate(l_date, "yyyy.MM.dd HH:mm:ss"); //获取日期
					
					//将 历史信号类 转换为字符串
					buf[j] = hisSignal.to_string();
				}
				//将 历史信号字符串 写入文件
				FileDeal file = new FileDeal();
				if(file.has_file(hisEquipId_lst.get(i)+"#"+date.substring(0,10), 1)){
					file.write_lines(buf);  //将一个设备的 信号字符 写入文件
				}
				buf = null;
			}
		}catch(Exception e){
			Log.e("HisDataRun>>saveHisEquip>>","异常抛出！");
		}
	}
	/**保存 实时信号*/
	public static void saveHiSignal(){
		if(hisSignalId_lst==null) return;
		try{
		//遍历链表
		for(int i=0; i<hisSignalId_lst.size(); i++){
			//获取 实时信号
			String ids[] = hisSignalId_lst.get(i).split("-");
			if(ids.length<1) continue;
			int equipId = Integer.parseInt(ids[0]);
			int signalId = Integer.parseInt(ids[1]);
			Net_data_signal net_signal = DataHAL.get_signal_data(NetHAL.IP, NetHAL.Port,equipId,signalId);
			HisSignal hisSignal = new HisSignal();
			hisSignal.equipid = ids[0];
			int EquiptId = Integer.parseInt(hisSignal.equipid);
			hisSignal.equip_name = DataPoolModel.getEquipment(EquiptId).equipName; //*
			hisSignal.sigid = ids[1];
			int SignalId = Integer.parseInt(hisSignal.sigid);
			hisSignal.name = DataPoolModel.getSignal(EquiptId, SignalId).name; //*
			hisSignal.value = net_signal.value;
			hisSignal.mean = net_signal.meaning;
			hisSignal.value_type = String.valueOf(net_signal.value_type);
			hisSignal.is_invalid = String.valueOf(net_signal.is_invalid);
			hisSignal.severity = String.valueOf(net_signal.severity);
			hisSignal.freshtime = String.valueOf(net_signal.freshtime);			
			long date = Long.parseLong(hisSignal.freshtime) * 1000;
			String strDate = UtTable.getDate(date, "yyyy.MM.dd HH:mm:ss"); //获取日期

			//Log.e("HisDataRun>>saveHiSignal~~~~>>", strDate);
			//将 历史信号类 转换为字符串
			String buf = hisSignal.to_string();
		
			//将 历史信号字符串 写入文件
			FileDeal file = new FileDeal();
			if(file.has_file(hisSignalId_lst.get(i)+"#"+strDate.substring(0,10), 2)){
				file.write_line(buf);  //将一个设备的 信号字符 写入文件
			}
		}
		}catch(Exception e){
			Log.e("HisDataRun>>saveHiSignal>>","异常抛出！");
		}
	}
	/**保存 实时运算式子*/  //输入参数PathMode = 10 11 12 13 14 ：pue曲线  天数据  月数据  年数据
	public static void SaveHisFormula(int PathMode){
		if(hisFormulaId_lst==null) return;

		//遍历链表
		for(int i=0; i<hisFormulaId_lst.size(); i++){
			//获取 运算式子
			//获取 实时信号
			String str[] = hisFormulaId_lst.get(i).split("&");
			if(str.length<1) continue;
			HisFormula hisFormula = new HisFormula();
			String date = "";
			for(int j=0; j<str.length; j++){
				String ids[] = str[j].split("-");
				if(ids.length<1) continue;
				int equipId = Integer.parseInt(ids[0]);
				int signalId = Integer.parseInt(ids[1]);
				Net_data_signal net_signal = DataHAL.get_signal_data(NetHAL.IP, NetHAL.Port,equipId,signalId);
				if(net_signal==null) continue;
				hisFormula.add_string( net_signal.value );
				hisFormula.getTime = String.valueOf(net_signal.freshtime);
				long l_date = Long.parseLong(hisFormula.getTime) * 1000;
				date = UtTable.getDate(l_date, "yyyy.MM.dd HH:mm:ss"); //获取日期
				hisFormula.strTime = date;
			} 
			if("".equals(date)) continue; 
			//将 历史信号类 转换为字符串
			String buf = hisFormula.to_string();
	//		Log.e("HisDataDAO>>SaveHisFormula:"+String.valueOf(PathMode), buf);
		
			//将 历史信号字符串 写入文件
			FileDeal file = new FileDeal();   //一个文件的保存单位 以年 365天为单位
			String pathDate = date.substring(0,4);
			if(PathMode == 13) pathDate= ""; //年 数据都放一个文件
			if(file.has_file(hisFormulaId_lst.get(i)+"#"+pathDate, PathMode)){
				if((file.read_line() != null)&&(file.read_line().length()>1) ){ //判断文件是否有字符存在 去追加
					file.write_line(hisFormula.strContent); 
				}
				file.write_str(buf);  //将一个设备的 信号字符 写入文件
			}
		}		
	}

	//--------------------------end--------
	
	public HisDataDAO() {  //构造 函数
		// TODO Auto-generated constructor stub
	}
	
	//*****************上点初始 读取 上次断电前的日期***********************
	public static String getBoforeDate(int mode){ //mode 可输入 11 12 13 对应获取历史文件最后写入日期
		String num = "";
		FileDeal file = new FileDeal(); 
		if( (hisFormulaId_lst==null)||(hisFormulaId_lst.size()==0) )  return num;
		String id = hisFormulaId_lst.get(0);
		if("".equals(id)) return num;
		long nowTime = java.lang.System.currentTimeMillis();
		String now_date = UtTable.getDate(nowTime, "yyyy.MM.dd HH:mm:ss");
		String pathDate = now_date.substring(0,4);
		switch(mode){
		case 11:
			if(file.has_file(id+"#"+pathDate, mode)){
				String str = file.read_later_line(); //读取文件最后一行 
				if((str != null)&&(str.length()>1) ){ //判断文件是否有字符存在 去追加
					HisFormula hisFormula = new HisFormula();
					if( hisFormula.read_string(str) ){
						String date = hisFormula.strTime;
						num = date.substring(8,10);
					}
				}
			}
			break;
		case 12:
			if(file.has_file(id+"#"+pathDate, mode)){
				String str = file.read_later_line(); //读取文件最后一行 
				if((str != null)&&(str.length()>1) ){ //判断文件是否有字符存在 去追加
					HisFormula hisFormula = new HisFormula();
					if( hisFormula.read_string(str) ){
						String date = hisFormula.strTime;
						num = date.substring(5,7);
					}
				}
			}
			break;
		case 13:
			if(file.has_file(id+"#"+"", mode)){
				String str = file.read_later_line(); //读取文件最后一行 
				if((str != null)&&(str.length()>1) ){ //判断文件是否有字符存在 去追加
					HisFormula hisFormula = new HisFormula();
					if( hisFormula.read_string(str) ){
						String date = hisFormula.strTime;
						num = date.substring(0,4);
					}
				}
			}
			break;
			default: break;
		}

		return num;
	}
	
	//*************************获取历史 相关 数据 函数***********************
	/**读取 设备的历史告警信息链表*/
	public static boolean getHisEquipEventList(String fileName){
		//读取 历史告警
		hisEvent_lst.clear();
		FileDeal file = new FileDeal();
		if(file.has_file(fileName, 3)){
			 if(file.read_all_line()==false) return false;
			 List<String> eventList = file.buflist1;
			 if(eventList==null) return false;
			 for(int i=0; i<eventList.size(); i++){
				 HisEvent hisEvent = new HisEvent();
				 if( hisEvent.read_string(eventList.get(i)) )
					 hisEvent_lst.add(hisEvent);
			 }
			 return true;
		}		
		return false;
	}
	/**读取 某个设备某天设备的历史数据 链表*/
	public static boolean getHisEquipSigList(String fileName){
		//读取 设备历史数据
		oneDay_hisEquipSig_lst.clear();
		FileDeal file = new FileDeal();
		if(file.has_file(fileName, 1)){
			if(file.read_all_line() == false) return false;
			List<String> signalList = file.buflist1;
			 if(signalList==null) return false;
			 for(int i=0; i<signalList.size(); i++){
				 HisSignal hisSignal = new HisSignal();
				 if( hisSignal.read_string(signalList.get(i)) ) 
					 oneDay_hisEquipSig_lst.add(hisSignal);
			 }			
			 return true;
		}		
		return false;
	}
	/**读取 某个信号某天的历史数据 链表*/
	public static boolean getHisSignalList(String fileName){
		//读取 设备历史数据
		oneDay_hisSignal_lst.clear();
		FileDeal file = new FileDeal();
		if(file.has_file(fileName, 2)){
			if(file.read_all_line() == false) return false;
			List<String> signalList = file.buflist1;
			 if(signalList==null) return false;
			 for(int i=0; i<signalList.size(); i++){
				 HisSignal hisSignal = new HisSignal();
				 if( hisSignal.read_string(signalList.get(i)) )
					 oneDay_hisSignal_lst.add(hisSignal);
			 }
			 return true;
		}		
		return false;
	}
	/**读取 某个式子pue曲线 的历史数据 链表*/  //输入参数：PathMode为文件位置    pueAndRc为pue或用电量
	public static boolean getPueLine_HisFormulaList(String fileName, int PathMode, int pueAndRc){

		if(PathMode==10) pueLine_hisFormula_lst.clear();
		else if(PathMode==11){
			if(pueAndRc == 1){
				day_RCBar_hisFormula_lst.clear();
			}else{
				day_pueBar_hisFormula_lst.clear();
			}
		}else if(PathMode==12){
			if(pueAndRc == 1){
				mon_RCBar_hisFormula_lst.clear();
			}else{
				mon_pueBar_hisFormula_lst.clear();
			}
		}else if(PathMode==13){
			if(pueAndRc == 1){
				year_RCBar_hisFormula_lst.clear();
			}else{
				year_pueBar_hisFormula_lst.clear();
			}
		}

		FileDeal file = new FileDeal();
		if(file.has_file(fileName, PathMode)){
			if(file.read_all_line() == false) return false;
			List<String> formulaList = file.buflist1;
			 if(formulaList==null) return false;
			 for(int i=0; i<formulaList.size(); i++){ 
				 HisFormula hisFormula = new HisFormula();
				 if( hisFormula.read_string(formulaList.get(i)) ){
						if(PathMode==10) pueLine_hisFormula_lst.add(hisFormula); 
						else if(PathMode==11){
							if(pueAndRc == 1){
								day_RCBar_hisFormula_lst.add(hisFormula); 
							}else{
								day_pueBar_hisFormula_lst.add(hisFormula); 
							}
						}else if(PathMode==12){
							if(pueAndRc == 1){
								mon_RCBar_hisFormula_lst.add(hisFormula); 
							}else{
								mon_pueBar_hisFormula_lst.add(hisFormula); 
							}
						}else if(PathMode==13){
							if(pueAndRc == 1){
								year_RCBar_hisFormula_lst.add(hisFormula); 
							}else{
								year_pueBar_hisFormula_lst.add(hisFormula); 
							}
						} 
				 }		
			 }
			 return true;
		}		
		return false;
	}
	//******************************end******************************
}
