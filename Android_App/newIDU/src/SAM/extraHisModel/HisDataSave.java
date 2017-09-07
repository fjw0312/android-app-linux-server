package SAM.extraHisModel;

import utils.DealFile;
import view.UtTable;
import android.util.Log;

public class HisDataSave {

	public HisDataSave() {
		// TODO Auto-generated constructor stub
	}	
	public static String inifile = "/mnt/sdcard/IDU.txt";
	
	public static long saveHisEquip_OldTime = 0;  //��¼ ���� ��ʷ�豸 ���ݵ� ʱ��
	public static long saveHisSignal_OldTime = 0;  //��¼ ���� ��ʷ�ź� ���ݵ� ʱ��
	public static long saveHisFormulaLine_OldTime = 0; //��¼ ���� pue����  ���ݵ� ʱ��
	
	public static long saveHisFormula_OldTime = 0;     //��¼ ���� �����ź� ���ݵ� ʱ��
	public static String saveHisFormulaDay_Old = "";  //��¼ ���� ������ʽ��  ���ݵ� ʱ��
	public static String saveHisFormulaMon_Old = "";  //��¼ ���� ������ʽ��  ���ݵ� ʱ��
	public static String saveHisFormulaYear_Old = ""; //��¼ ���� ������ʽ��  ���ݵ� ʱ��
	
	//��ʼ��  ��ʷ���ݵĲɼ�ʱ��
    public static void init_SaveDataTime(){
    	try{
		DealFile fDeal = new DealFile(); 
		fDeal.has_file(inifile);
		String Formula = fDeal.read_str_line("Formula").trim().split("=")[1];
		HisDataDAO.add_hisFormula_Id(Formula);
    	}catch(Exception e){
    		Log.e("HisDataSave>>init_SaveDataTime","��ȡ�õ��������ļ�ʧ���쳣�׳���");
    	}
    	
		//��ȡ pue�õ��� �ϴζϵ�ǰ ���ʱ��
		saveHisFormulaDay_Old = HisDataDAO.getBoforeDate(11);
		saveHisFormulaMon_Old = HisDataDAO.getBoforeDate(12);
		saveHisFormulaYear_Old = HisDataDAO.getBoforeDate(13);
		Log.e("HisDataSave>>init_SaveDataTime","Day_Old="+saveHisFormulaDay_Old+
				"   Mon_Old="+saveHisFormulaMon_Old+"   Year_Old="+saveHisFormulaYear_Old);
    }
    
    //�ɼ� ���� ��ʷ����
    public static void save_HisData(){
    	//���������κ�ҳ���� ���ݸ��µ��豸�ͺ���Ϣ
		try{
			long nowTime = java.lang.System.currentTimeMillis();
			//��ú�̨��Ҫʱʱ�����豸id������  ����ɼ�����ʱ����ʱ��ϵͳʱ����ܻ����  
			if( (nowTime-saveHisFormula_OldTime)>=5*60*1000 ) //5min
			{
				
				saveHisFormula_OldTime = nowTime;
				//ÿ��  ��¼1��
				String now_date = UtTable.getDate(saveHisFormula_OldTime, "yyyy.MM.dd HH:mm:ss");
				String n_day = now_date.substring(8,10); //��ȡ�������� 

				//�ж� ���ڵ����� �賿 ���00:05
				if(	n_day.equals(saveHisFormulaDay_Old) == false ){
					
					saveHisFormulaDay_Old = n_day;	 //����  �� ����					
					HisDataDAO.SaveHisFormula(11); // ���� ʽ�� ������ 
					Log.e("HisDataSave>>save_HisData","����-��ʽ��-������");
					
					//�ж� �Ƿ��³�  1���¹�ȥ
					String n_mon = now_date.substring(5,7);
					if(n_mon.equals(saveHisFormulaMon_Old) == false){
						saveHisFormulaMon_Old = n_mon;	 //����  �� ����					
						HisDataDAO.SaveHisFormula(12);   // ���� ʽ�� �� ����
						Log.e("HisDataSave>>save_HisData","����-��ʽ��-������");
						
						//�ж� �Ƿ��³�  1���ȥ
						String n_year = now_date.substring(0,4);
						if(n_year.equals(saveHisFormulaYear_Old) == false){
							saveHisFormulaYear_Old = n_year;	 //����  �� ����					
							HisDataDAO.SaveHisFormula(13);   // ���� ʽ�� �� ����
							Log.e("HisDataSave>>save_HisData","����-��ʽ��-������");
						}
					}
				}

			} 
			//pue ���� ʱ������1��  �ɼ�������2h 
			if( (nowTime-saveHisFormulaLine_OldTime)>=2*3600*1000 ) //2h
			{
				saveHisFormulaLine_OldTime = nowTime;
				//����  ��ʷ�ź�
				HisDataDAO.SaveHisFormula(10);
				Log.e("DataRun>>run","����-pue����-������");
			}
			//�ź�&�ź�����  20min ���� һ��   ����
			if( (nowTime-saveHisSignal_OldTime)>=20*60*1000 )
			{
				saveHisSignal_OldTime = nowTime;
				//����  ��ʷ�ź�
				HisDataDAO.saveHiSignal();
				Log.e("DataRun>>run","����-�ź�-������");
			}
			//��ú�̨��Ҫʱʱ�����豸id������  ����ɼ�����ʱ����ʱ��ϵͳʱ����ܻ����  4h ���� һ�� 
			if( (nowTime-saveHisEquip_OldTime)>=3600*4*1000 ) 
			{
				saveHisEquip_OldTime = nowTime;
				//���� ��ʷ�豸
				HisDataDAO.saveHisEquip(); 
				Log.e("DataRun>>run","����-�豸-������"); 
			}
			
		}catch(Exception e){
			Log.e("HisDataSave","��̨�豸ʵʱ�����쳣�׳�!");
		}
    }

}
