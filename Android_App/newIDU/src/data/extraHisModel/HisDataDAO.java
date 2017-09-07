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

//��ʷ ���� ����  ����  ��ȡ Ŀǰ��֤ ���� ������
/*��ʷ ���� ģ�͵��ڲ� �Զ� ������ʷ���� ��**/
public class HisDataDAO {

	//����ע��  ����
	public static List<String> hisEquipId_lst = new ArrayList<String>(); //<�豸id> ��ʷ�豸id����
	public static List<String> hisSignalId_lst = new ArrayList<String>();//<�豸id-�ź�id>  ��ʷ�ź� �豸id�ź�id����
	public static List<String> hisFormulaId_lst = new ArrayList<String>();//<�豸id-�ź�id&�豸id-�ź�id>
	
	public static List<HisEvent> hisEvent_lst = new ArrayList<HisEvent>(); //��Ż�ȡ�� ĳ���豸��������ʷ�澯����
	public static List<HisSignal> oneDay_hisEquipSig_lst = new ArrayList<HisSignal>();  //��Ż�ȡ�� ĳ���豸ĳ��� ��ʷ�豸���� �ź�����
	public static List<HisSignal> oneDay_hisSignal_lst = new ArrayList<HisSignal>(); //��Ż�ȡ��ĳ�� �ź� ĳ��� ��ʷ�ź�����
	
	public static List<HisFormula> pueLine_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ pue���� ����
	
	public static List<HisFormula> day_RCBar_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ   ���õ���  ����
	public static List<HisFormula> day_pueBar_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ  ��pue  ����
	public static List<HisFormula> mon_RCBar_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ    ���õ���  ����
	public static List<HisFormula> mon_pueBar_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ  ��pue  ����
	public static List<HisFormula> year_RCBar_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ   ���õ���  ����
	public static List<HisFormula> year_pueBar_hisFormula_lst = new ArrayList<HisFormula>(); //���� ��ȡ�� ĳ��ʽ�� ��ʷ��Ϣ  ��pue  ����

	/**���Formula_Id*/ //�ú�����ֱ���ֶ����  ͦ�õ�
	public static void add_hisFormula_Id(String Formula_Id){ //������ʽ9-1&8-2
		if("".equals(Formula_Id)) return;
		if(hisFormulaId_lst.contains(Formula_Id)) return;
	//	hisFormulaId_lst.add("9-1&8-2");
		hisFormulaId_lst.add(Formula_Id);
		
	}

	//���� id ���� ����ʵʱ ���ݺ���---------------
	/** ���� ʵʱ�豸*/
	public static void saveHisEquip(){
		if(hisEquipId_lst==null) return;
		
		try{
		//��������
			for(int i=0; i<hisEquipId_lst.size(); i++){ 
				//��ȡ ʵʱ�豸��-�ź�����		
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
					date = UtTable.getDate(l_date, "yyyy.MM.dd HH:mm:ss"); //��ȡ����
					
					//�� ��ʷ�ź��� ת��Ϊ�ַ���
					buf[j] = hisSignal.to_string();
				}
				//�� ��ʷ�ź��ַ��� д���ļ�
				FileDeal file = new FileDeal();
				if(file.has_file(hisEquipId_lst.get(i)+"#"+date.substring(0,10), 1)){
					file.write_lines(buf);  //��һ���豸�� �ź��ַ� д���ļ�
				}
				buf = null;
			}
		}catch(Exception e){
			Log.e("HisDataRun>>saveHisEquip>>","�쳣�׳���");
		}
	}
	/**���� ʵʱ�ź�*/
	public static void saveHiSignal(){
		if(hisSignalId_lst==null) return;
		try{
		//��������
		for(int i=0; i<hisSignalId_lst.size(); i++){
			//��ȡ ʵʱ�ź�
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
			String strDate = UtTable.getDate(date, "yyyy.MM.dd HH:mm:ss"); //��ȡ����

			//Log.e("HisDataRun>>saveHiSignal~~~~>>", strDate);
			//�� ��ʷ�ź��� ת��Ϊ�ַ���
			String buf = hisSignal.to_string();
		
			//�� ��ʷ�ź��ַ��� д���ļ�
			FileDeal file = new FileDeal();
			if(file.has_file(hisSignalId_lst.get(i)+"#"+strDate.substring(0,10), 2)){
				file.write_line(buf);  //��һ���豸�� �ź��ַ� д���ļ�
			}
		}
		}catch(Exception e){
			Log.e("HisDataRun>>saveHiSignal>>","�쳣�׳���");
		}
	}
	/**���� ʵʱ����ʽ��*/  //�������PathMode = 10 11 12 13 14 ��pue����  ������  ������  ������
	public static void SaveHisFormula(int PathMode){
		if(hisFormulaId_lst==null) return;

		//��������
		for(int i=0; i<hisFormulaId_lst.size(); i++){
			//��ȡ ����ʽ��
			//��ȡ ʵʱ�ź�
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
				date = UtTable.getDate(l_date, "yyyy.MM.dd HH:mm:ss"); //��ȡ����
				hisFormula.strTime = date;
			} 
			if("".equals(date)) continue; 
			//�� ��ʷ�ź��� ת��Ϊ�ַ���
			String buf = hisFormula.to_string();
	//		Log.e("HisDataDAO>>SaveHisFormula:"+String.valueOf(PathMode), buf);
		
			//�� ��ʷ�ź��ַ��� д���ļ�
			FileDeal file = new FileDeal();   //һ���ļ��ı��浥λ ���� 365��Ϊ��λ
			String pathDate = date.substring(0,4);
			if(PathMode == 13) pathDate= ""; //�� ���ݶ���һ���ļ�
			if(file.has_file(hisFormulaId_lst.get(i)+"#"+pathDate, PathMode)){
				if((file.read_line() != null)&&(file.read_line().length()>1) ){ //�ж��ļ��Ƿ����ַ����� ȥ׷��
					file.write_line(hisFormula.strContent); 
				}
				file.write_str(buf);  //��һ���豸�� �ź��ַ� д���ļ�
			}
		}		
	}

	//--------------------------end--------
	
	public HisDataDAO() {  //���� ����
		// TODO Auto-generated constructor stub
	}
	
	//*****************�ϵ��ʼ ��ȡ �ϴζϵ�ǰ������***********************
	public static String getBoforeDate(int mode){ //mode ������ 11 12 13 ��Ӧ��ȡ��ʷ�ļ����д������
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
				String str = file.read_later_line(); //��ȡ�ļ����һ�� 
				if((str != null)&&(str.length()>1) ){ //�ж��ļ��Ƿ����ַ����� ȥ׷��
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
				String str = file.read_later_line(); //��ȡ�ļ����һ�� 
				if((str != null)&&(str.length()>1) ){ //�ж��ļ��Ƿ����ַ����� ȥ׷��
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
				String str = file.read_later_line(); //��ȡ�ļ����һ�� 
				if((str != null)&&(str.length()>1) ){ //�ж��ļ��Ƿ����ַ����� ȥ׷��
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
	
	//*************************��ȡ��ʷ ��� ���� ����***********************
	/**��ȡ �豸����ʷ�澯��Ϣ����*/
	public static boolean getHisEquipEventList(String fileName){
		//��ȡ ��ʷ�澯
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
	/**��ȡ ĳ���豸ĳ���豸����ʷ���� ����*/
	public static boolean getHisEquipSigList(String fileName){
		//��ȡ �豸��ʷ����
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
	/**��ȡ ĳ���ź�ĳ�����ʷ���� ����*/
	public static boolean getHisSignalList(String fileName){
		//��ȡ �豸��ʷ����
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
	/**��ȡ ĳ��ʽ��pue���� ����ʷ���� ����*/  //���������PathModeΪ�ļ�λ��    pueAndRcΪpue���õ���
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
