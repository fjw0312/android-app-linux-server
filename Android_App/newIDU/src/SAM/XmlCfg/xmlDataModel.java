package SAM.XmlCfg;

import android.annotation.SuppressLint;
import java.util.HashMap;



/**配置文件的 配置数据模型*/
//将所有的设备 配置文件的信息 都储存在该类里
public class xmlDataModel {

	@SuppressLint("UseSparseArrays")
	public xmlDataModel() {
		// TODO Auto-generated constructor stub
	}
	
	//系统 设备类链表 <设备配置EquipTemplateId, 设备配置类>
	public static HashMap<Integer,  xml_EquiptCfg> hm_xmlDataModel =  new HashMap<Integer,  xml_EquiptCfg>();; //<设备配置EquipTemplateId, 设备配置类>

}
