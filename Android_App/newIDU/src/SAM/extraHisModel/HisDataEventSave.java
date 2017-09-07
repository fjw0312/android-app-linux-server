package SAM.extraHisModel;

import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import android.util.Log;
import view.UtTable;


//*********���� ��ʷ�澯  ִ�е��߳���
public class HisDataEventSave extends Thread{
	
	private String strLine = "";
	public HisEvent hisEvent = null;	
	
	public String strEquiptId = "";
	public String strEquiptName = "";
	public long startTime = 0;
	public xml_eventCfg xmlEventCfg = null;
	public EventCondition eventcondition = null;
	public String value = "";


	//���� ��ʷ�澯��
	public void fullEvent1(){
		try{
		    long sTime = startTime*1000;
			String startTime_str = UtTable.getDate(sTime, "yyyy-MM-dd HH:mm:ss");
			long  stopTime = System.currentTimeMillis();
			String stopTime_str = UtTable.getDate(stopTime, "yyyy-MM-dd HH:mm:ss");
			hisEvent = new HisEvent();
			hisEvent.start_time = startTime_str;
			hisEvent.finish_time = stopTime_str;
			hisEvent.equip_name = strEquiptName;
			hisEvent.equipid = strEquiptId;
			hisEvent.event_name = xmlEventCfg.EventName;
			hisEvent.event_id = xmlEventCfg.EventId;
			hisEvent.severity = eventcondition.EventSeverity;
			hisEvent.value = value;
			hisEvent.event_mean = eventcondition.Meaning;
		}catch(Exception e){
			Log.e("HisDataEventSave->fullEvent1","�쳣�׳���");
		}
	}
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		fullEvent1();
		if(hisEvent == null) return;
		strLine = hisEvent.to_string();
		strEquiptId = hisEvent.equipid;
		//�� ��ʷ�ź��ַ��� д���ļ�
		FileDeal file = new FileDeal();
		if(file.has_file("hisevent-"+strEquiptId, 3)){
			file.write_line(strLine);  //��һ���豸�� �ź��ַ� д���ļ�
		}
		
	}

}
