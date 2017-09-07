package utils;

//kstar app 
//make by fang
//表达式  类   解析功能Value[Equip:127-Temp:179-Signal:8]
//解析的 内容只解析 单项数值类型内的数据
public class Expression {

	public Expression() {
		// TODO Auto-generated constructor stub
	}
	
	//Feilds
	public String type = "";   //当为非设备表达式时 为Para
	
	public int equip_ExId = -1;
	public int temp_ExId = -1;
	public int signal_ExId = -1;	
	public int event_ExId = -1;      //告警参数id 用于 告警屏蔽表达式
	public int condition_ExId = -1;  //报警子项id 用于 报警值设置表达式
	public int command_ExId = -1;    //用于 控制命令 表达式
	public int parameter_ExId = -1;  //用于 控制命令 表达式
	public String value = "";     //遥调 不需要该参数
	public String para = "";   // 为非设备表达式 的字符内容
	//绑定表达式为设备类型的  参数类
	//绑定表达式为数值类型的  参数类
	//绑定表达式为告警等级类型的  参数类
	//绑定表达式为设备通信状态类型的  参数类
	//绑定表达式为告警值设备类型的  参数类
	//绑定表达式为控制命令（包含遥调 遥控）类型的  参数类

	
	//解析出表达式 的类型[Value[Equip:127-Temp:179-Signal:8]]  [Equip[Equip:1]] 
	public String parseExpression(String expression){
		if( "".equals(expression) ) return "";
		
       	String[] s_Express = expression.split("\\[");
       	if(s_Express.length<2){  //出现 非设备表达式
       		type = "Para";
       		para = expression;
       		return type;
       	}
       	type = s_Express[1];    	
    	String str_Exp = s_Express[2];
    	String[] s_Exp = str_Exp.split("\\]");
    	String str_value= s_Exp[0];
    	String[] para_Id = str_value.split("-");
    	for (int i = 0; i < para_Id.length; ++i) {
    		String[] s_Value = para_Id[i].split(":");
    		if (s_Value.length < 2)
    			continue;
    		if ("Equip".equals(s_Value[0])) 
    			equip_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Temp".equals(s_Value[0])) 
    			temp_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Signal".equals(s_Value[0])) 
    			signal_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Command".equals(s_Value[0])) 
    			command_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Parameter".equals(s_Value[0]))
    			parameter_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Value".equals(s_Value[0])) 
    			value = s_Value[1];
    		else if ("Event".equals(s_Value[0])) 
    			event_ExId = Integer.parseInt(s_Value[1]); 
    		else if ("Condition".equals(s_Value[0])) {
    			condition_ExId = Integer.parseInt(s_Value[1]);
    		}
    	}
		
		return type;
	}

}
