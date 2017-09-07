package SAM.extraHisModel;

import utils.DealFile;
import view.UtTable;
import android.util.Log;

public class HisDataSave {

	public HisDataSave() {
		// TODO Auto-generated constructor stub
	}	
	public static String inifile = "/mnt/sdcard/IDU.txt";
	
	public static long saveHisEquip_OldTime = 0;  //记录 保存 历史设备 数据的 时间
	public static long saveHisSignal_OldTime = 0;  //记录 保存 历史信号 数据的 时间
	public static long saveHisFormulaLine_OldTime = 0; //记录 保存 pue曲线  数据的 时间
	
	public static long saveHisFormula_OldTime = 0;     //记录 保存 运算信号 数据的 时间
	public static String saveHisFormulaDay_Old = "";  //记录 保存 天运算式子  数据的 时间
	public static String saveHisFormulaMon_Old = "";  //记录 保存 月运算式子  数据的 时间
	public static String saveHisFormulaYear_Old = ""; //记录 保存 年运算式子  数据的 时间
	
	//初始化  历史数据的采集时间
    public static void init_SaveDataTime(){
    	try{
		DealFile fDeal = new DealFile(); 
		fDeal.has_file(inifile);
		String Formula = fDeal.read_str_line("Formula").trim().split("=")[1];
		HisDataDAO.add_hisFormula_Id(Formula);
    	}catch(Exception e){
    		Log.e("HisDataSave>>init_SaveDataTime","读取用电量配置文件失败异常抛出！");
    	}
    	
		//获取 pue用电量 上次断电前 最后时间
		saveHisFormulaDay_Old = HisDataDAO.getBoforeDate(11);
		saveHisFormulaMon_Old = HisDataDAO.getBoforeDate(12);
		saveHisFormulaYear_Old = HisDataDAO.getBoforeDate(13);
		Log.e("HisDataSave>>init_SaveDataTime","Day_Old="+saveHisFormulaDay_Old+
				"   Mon_Old="+saveHisFormulaMon_Old+"   Year_Old="+saveHisFormulaYear_Old);
    }
    
    //采集 保存 历史数据
    public static void save_HisData(){
    	//遍历请求任何页面下 数据更新的设备型号信息
		try{
			long nowTime = java.lang.System.currentTimeMillis();
			//获得后台需要时时更新设备id的链表  如果采集到的时间延时于系统时间可能会误差  
			if( (nowTime-saveHisFormula_OldTime)>=5*60*1000 ) //5min
			{
				
				saveHisFormula_OldTime = nowTime;
				//每天  记录1次
				String now_date = UtTable.getDate(saveHisFormula_OldTime, "yyyy.MM.dd HH:mm:ss");
				String n_day = now_date.substring(8,10); //获取当天日期 

				//判断 日期到隔天 凌晨 误差00:05
				if(	n_day.equals(saveHisFormulaDay_Old) == false ){
					
					saveHisFormulaDay_Old = n_day;	 //保存  天 数据					
					HisDataDAO.SaveHisFormula(11); // 保存 式子 天数据 
					Log.e("HisDataSave>>save_HisData","保存-天式子-结束！");
					
					//判断 是否月初  1个月过去
					String n_mon = now_date.substring(5,7);
					if(n_mon.equals(saveHisFormulaMon_Old) == false){
						saveHisFormulaMon_Old = n_mon;	 //保存  月 数据					
						HisDataDAO.SaveHisFormula(12);   // 保存 式子 月 数据
						Log.e("HisDataSave>>save_HisData","保存-月式子-结束！");
						
						//判断 是否月初  1年过去
						String n_year = now_date.substring(0,4);
						if(n_year.equals(saveHisFormulaYear_Old) == false){
							saveHisFormulaYear_Old = n_year;	 //保存  年 数据					
							HisDataDAO.SaveHisFormula(13);   // 保存 式子 年 数据
							Log.e("HisDataSave>>save_HisData","保存-年式子-结束！");
						}
					}
				}

			} 
			//pue 曲线 时间轴跨度1月  采集点周期2h 
			if( (nowTime-saveHisFormulaLine_OldTime)>=2*3600*1000 ) //2h
			{
				saveHisFormulaLine_OldTime = nowTime;
				//保存  历史信号
				HisDataDAO.SaveHisFormula(10);
				Log.e("DataRun>>run","保存-pue曲线-结束！");
			}
			//信号&信号曲线  20min 保存 一次   保存
			if( (nowTime-saveHisSignal_OldTime)>=20*60*1000 )
			{
				saveHisSignal_OldTime = nowTime;
				//保存  历史信号
				HisDataDAO.saveHiSignal();
				Log.e("DataRun>>run","保存-信号-结束！");
			}
			//获得后台需要时时更新设备id的链表  如果采集到的时间延时于系统时间可能会误差  4h 保存 一次 
			if( (nowTime-saveHisEquip_OldTime)>=3600*4*1000 ) 
			{
				saveHisEquip_OldTime = nowTime;
				//保存 历史设备
				HisDataDAO.saveHisEquip(); 
				Log.e("DataRun>>run","保存-设备-结束！"); 
			}
			
		}catch(Exception e){
			Log.e("HisDataSave","后台设备实时更新异常抛出!");
		}
    }

}
