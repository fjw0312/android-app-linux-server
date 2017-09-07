package utils;

import java.text.DecimalFormat;
import java.util.List;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Event;
import SAM.DataPoolModel.Signal;
import android.util.Log;
import app.main.idu.MainActivity;
//import data.pool.DataPoolModel;
//import data.pool_model.Event;
//import data.pool_model.Signal;

/**��ȡʵʱ �ź���ֵ �澯�ȼ�   ������ ��Ҫ������ʽ���㼰 ��ֵ������*/
public class RealTimeValue {
	
	public RealTimeValue() {
		// TODO Auto-generated constructor stub		
	}
	public String strResult = "";       //�ź�����
	public String strResultMeaning = "";//�źź���
	public int iResult = 0;
	//��ȡ�� �źŵ� ʵʱ��ֵ    ֧��ʵʱ�ź�  or ʵʱ�źŵĸ澯�ȼ�
	public String getRealTimeValue(List<Expression> expression_lst) {
		// TODO Auto-generated constructor stub
		if(expression_lst == null) return "";
		if(expression_lst.size()<=0) return "";
		Expression expression = null;
		for(int i=0;i<expression_lst.size();i++){
			expression = expression_lst.get(i);
			if("Para".equals(expression.type)){
				//�������㵥Ԫ
			}else if("Value".equals(expression.type)){
				getRealTimeSignalData(expression_lst); 
				break;
			}else if("EventSeverity".equals(expression.type)){
				//Log.e("RealTimeValue>>getRealTimeValue","into EventSeverity!"); 
				int num =getRealTimeEventSeverity(expression_lst);
				strResult = String.valueOf(iResult);
				break;
			}else if("Event".equals(expression.type)){
				//Log.e("RealTimeValue>>getRealTimeValue","into Event!");
				int num = getReadTimeEvent(expression_lst);
				strResult = String.valueOf(iResult);
				break;
			}
		}
		
		return strResult;
	}
	//Ŀǰֻ ������󶨵����� ���   �����ֵ����1λС����   ֻ�ܴ���value���͵İ� 
	public String getRealTimeSignalData(List<Expression> expression_lst){
		String strSigValue = "";
		for(int j=0; j<expression_lst.size(); j++){
			Expression expression = expression_lst.get(j); 
			if("Value".equals(expression.type)){ 
	//			Signal signal = DataPoolModel.getSignal(expression.equip_ExId, expression.signal_ExId);
				Signal signal = NetDataModel.getSignal(expression.equip_ExId, expression.signal_ExId);
				if(signal == null) return "";
				//�ж�ֻ��һ���� һ�����ʽ ʱ   
				if(expression_lst.size()==1){  
					String str = signal.value;
					float f_value = Float.parseFloat(str);
		    		DecimalFormat decimalFloat = new DecimalFormat("0.0"); //floatС���㾫�ȴ���		    		
		    		strResult= decimalFloat.format(f_value);
		    		strResultMeaning = "";
					if(signal.meaning != null){ //�Ƿ�Ϊ��ֵ������
						strResultMeaning = signal.meaning;
					}
					return strResult;
				}else{
					strSigValue = strSigValue + signal.value;
				}
			}else if("Para".equals(expression.type)){ //���ַ�����
				strSigValue = strSigValue + expression.para;				
			}
		}
		Calculator calculator = new Calculator();
//		Log.e("Ks_Label>updataValue>>strSigValue", strSigValue);
		try{
			String strV = calculator.calculate(strSigValue) + "";
			float f_Value = Float.parseFloat(strV);
    		DecimalFormat decimalFloat = new DecimalFormat("0.0"); //floatС���㾫�ȴ���
    		strResult= decimalFloat.format(f_Value);
		}catch(Exception e){				
		}
		calculator = null;

		return strResult;
	}
	
	//��ȡ�澯�ȼ�����  �����  ������  
	public int getRealTimeEventSeverity(List<Expression> expression_lst){
/*	 �ź���  ���� �澯�ȼ���Ϣ	
		for(int j=0; j<expression_lst.size(); j++){
			Expression expression = expression_lst.get(j); 
			if("EventSeverity".equals(expression.type)){ 
				Signal signal = DataPoolModel.getSignal(expression.equip_ExId, expression.signal_ExId);
				if(signal == null) return 0;
				
				if(signal.invalid == 1){ //���жϸ��ź���Ϣ�Ƿ���Ч
					iResult = signal.severity;
				}else{
					iResult = 0; // ��Ч���ź���Ϣ һ��Ϊ�ж�״̬  Ҳ��Ϊ 3 
				}
				//Log.e("RealTimeValue>getRealTimeEventSeverity>>", String.valueOf(signal.severity));
			}
		}
*/		return iResult;
	}
	//��ȡ ĳ�澯id �Ƿ��и澯  �����  ������  
	public int getReadTimeEvent(List<Expression> expression_lst){
		for(int j=0; j<expression_lst.size(); j++){
			Expression expression = expression_lst.get(j); 
			if("Event".equals(expression.type)){ 
			//	Event event = DataPoolModel.getEvent(expression.equip_ExId, expression.event_ExId);
				Event event = NetDataModel.getEvent(expression.equip_ExId, expression.event_ExId);
				if(event == null){
					iResult = 0;
					 return iResult;
				}
				
				if(event.is_active == 1){ //���жϸ�ʵʱ�澯�Ƿ���Ч

					iResult = 1;
				}
				//Log.e("RealTimeValue>getRealTimeEventSeverity>>", String.valueOf(signal.severity));
			}
		}
		return iResult; 
	}
		
	
}
