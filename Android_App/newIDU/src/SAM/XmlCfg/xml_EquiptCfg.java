package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;


//设备模板文件的 告警配置类
@SuppressLint("UseSparseArrays")
public class xml_EquiptCfg {

	@SuppressLint("UseSparseArrays")
	public xml_EquiptCfg() {
		// TODO Auto-generated constructor stub
		xml_signalCfg_lst = new HashMap<String, xml_signalCfg>();
		xml_eventCfg_lst = new HashMap<String, xml_eventCfg>();
		xml_cmdCfg_lst = new HashMap<String, xml_cmdCfg>();
		sig_ChannelNo_id_lst = new HashMap<String,String>();
	}
	
	public String EquipTemplateId;        //设备模板id
	public String EquipTemplateName;   //设备模板名称
	public String EquipTemplateType;      //设备模板类型号
	public String LibName;             //设备模板引用的动态库名称
	
	public HashMap<String, xml_signalCfg> xml_signalCfg_lst = null; //信号配置链表<信号id, 信号配置类>
	public HashMap<String, xml_eventCfg> xml_eventCfg_lst = null;   //告警配置链表<告警id, 告警配置类>
	public HashMap<String, xml_cmdCfg> xml_cmdCfg_lst = null;       //控制配置链表<控制id, 控制配置类>
	
	//xml设备模板 文件名
	public String fileName;
	//信号通道   搜索 链表
	public HashMap<String,String> sig_ChannelNo_id_lst = null; //信号通道id  信号id

	
}
