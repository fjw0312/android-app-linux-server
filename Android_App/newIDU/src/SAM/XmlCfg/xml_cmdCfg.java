package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;


//设备模板文件的 告警配置类
public class xml_cmdCfg {

	@SuppressLint("UseSparseArrays")
	public xml_cmdCfg() {
		// TODO Auto-generated constructor stub
		CommandParameterlst = new HashMap<String, CommandParameter>();
	}
	
	public class CommandParameter{
		public CommandParameter(){
			CommandMeaninglst = new HashMap<String, String>();
		}
		public String ParameterId;          //控制子项id
		public String ParameterName;     //控制名称
		public String UIControlType;        //控制子项类型
		public String DataType;             //控制子项数据类型
		
		//控制子项 控制数据及含义链表
		public HashMap<String, String> CommandMeaninglst = null;
	}
	
	public String CommandId;              //控制id
	public String CommandName;         //控制名称
	public String CommandBaseId;          //控制基准id 默认-1
	public String CommandType;            //控制类型     
	public String CommandToken;        //控制标识号
	public String Retry;                  //控制发送次数
	public String Enable;              //控制使能
	
	//控制内容含义链表
	public HashMap<String, CommandParameter> CommandParameterlst = null; 

}
