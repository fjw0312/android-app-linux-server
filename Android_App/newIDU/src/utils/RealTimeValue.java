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

/**获取实时 信号数值 告警等级   处理类 主要处理表达式运算及 数值量含义*/
public class RealTimeValue {
	
	public RealTimeValue() {
		// TODO Auto-generated constructor stub		
	}
	public String strResult = "";       //信号数据
	public String strResultMeaning = "";//信号含义
	public int iResult = 0;
	//获取绑定 信号的 实时数值    支持实时信号  or 实时信号的告警等级
	public String getRealTimeValue(List<Expression> expression_lst) {
		// TODO Auto-generated constructor stub
		if(expression_lst == null) return "";
		if(expression_lst.size()<=0) return "";
		Expression expression = null;
		for(int i=0;i<expression_lst.size();i++){
			expression = expression_lst.get(i);
			if("Para".equals(expression.type)){
				//参数运算单元
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
	//目前只 处理单项绑定的运算 结果   结果数值保留1位小数点   只能处理value类型的绑定 
	public String getRealTimeSignalData(List<Expression> expression_lst){
		String strSigValue = "";
		for(int j=0; j<expression_lst.size(); j++){
			Expression expression = expression_lst.get(j); 
			if("Value".equals(expression.type)){ 
	//			Signal signal = DataPoolModel.getSignal(expression.equip_ExId, expression.signal_ExId);
				Signal signal = NetDataModel.getSignal(expression.equip_ExId, expression.signal_ExId);
				if(signal == null) return "";
				//判断只有一个绑定 一个表达式 时   
				if(expression_lst.size()==1){  
					String str = signal.value;
					float f_value = Float.parseFloat(str);
		    		DecimalFormat decimalFloat = new DecimalFormat("0.0"); //float小数点精度处理		    		
		    		strResult= decimalFloat.format(f_value);
		    		strResultMeaning = "";
					if(signal.meaning != null){ //是否为数值量含义
						strResultMeaning = signal.meaning;
					}
					return strResult;
				}else{
					strSigValue = strSigValue + signal.value;
				}
			}else if("Para".equals(expression.type)){ //多字符运算
				strSigValue = strSigValue + expression.para;				
			}
		}
		Calculator calculator = new Calculator();
//		Log.e("Ks_Label>updataValue>>strSigValue", strSigValue);
		try{
			String strV = calculator.calculate(strSigValue) + "";
			float f_Value = Float.parseFloat(strV);
    		DecimalFormat decimalFloat = new DecimalFormat("0.0"); //float小数点精度处理
    		strResult= decimalFloat.format(f_Value);
		}catch(Exception e){				
		}
		calculator = null;

		return strResult;
	}
	
	//获取告警等级函数  单项绑定  单运算  
	public int getRealTimeEventSeverity(List<Expression> expression_lst){
/*	 信号类  暂无 告警等级信息	
		for(int j=0; j<expression_lst.size(); j++){
			Expression expression = expression_lst.get(j); 
			if("EventSeverity".equals(expression.type)){ 
				Signal signal = DataPoolModel.getSignal(expression.equip_ExId, expression.signal_ExId);
				if(signal == null) return 0;
				
				if(signal.invalid == 1){ //先判断该信号信息是否有效
					iResult = signal.severity;
				}else{
					iResult = 0; // 无效的信号信息 一般为中断状态  也可为 3 
				}
				//Log.e("RealTimeValue>getRealTimeEventSeverity>>", String.valueOf(signal.severity));
			}
		}
*/		return iResult;
	}
	//获取 某告警id 是否有告警  单项绑定  单运算  
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
				
				if(event.is_active == 1){ //先判断该实时告警是否有效

					iResult = 1;
				}
				//Log.e("RealTimeValue>getRealTimeEventSeverity>>", String.valueOf(signal.severity));
			}
		}
		return iResult; 
	}
		
	
}
