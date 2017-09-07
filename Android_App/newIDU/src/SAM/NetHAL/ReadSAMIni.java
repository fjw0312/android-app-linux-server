package SAM.NetHAL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import android.util.Log;

//��ȡ  SAM.ini�ļ� 
public class ReadSAMIni {

	public ReadSAMIni() {
		// TODO Auto-generated constructor stub
	}
	public static Hashtable<Integer,Integer> idlst = new Hashtable<Integer,Integer>(); //<�豸id,�豸ģ��id>
	
	public static String SamPath = "/data/fjw/SAM.ini";
	
	//�����ļ��Ĳɼ��豸id ����
	public static List<String> buflist2 = new ArrayList<String>();
	//��ȡ����ĳЩ�ַ���  �������ַ� �������ĳ�ַ���-�����ַ�����ӽ�����������������������
	public static List<String> read_str_all_line(String buf){
		String buff = "";
		buflist2.clear();
		try{
			File file = new File(SamPath);
			if(!file.exists()){ //�ж��ļ�/Ŀ¼�Ƿ���� �������½�
				Log.e("�ļ���"+SamPath,"�����ڣ�");
				return null;
			}
			//�ж��ļ��Ƿ�ɶ�
			if(!file.canRead()){
				Log.e("�ļ�:"+SamPath,"���ɶ�");
				return null;
			}			
			BufferedReader bufread = new BufferedReader( new FileReader(file));
			while((buff = bufread.readLine())!=null){
				 if(buff.contains(buf)){
					 buflist2.add(buff);
				 }
			}				 
			bufread.close();
			}catch(Exception e){
				Log.e("�ļ�:"+SamPath,"�����ַ���ʧ�ܣ�");
				return buflist2;
			}
		return buflist2;
	}
	//��ȡ �����ļ� �ɼ����豸id��
	public static Hashtable<Integer,Integer> readSAMIni(){
		List<String> strlst = new ArrayList<String>();
		strlst = read_str_all_line("portRunEquipt");
	    if(strlst == null )  return null;
		for(int i=0;i<strlst.size();i++){
			String str = strlst.get(i);
			String strs[] = str.split("=|#");
			Log.e("NetDataModel->readSAMIni>>>��ȡ���ļ��豸id��",strs[1]);
			if(strs.length<2) continue;
			int equiptId = Integer.parseInt(strs[1]);
			int pleteTempId = Integer.parseInt(strs[2]); 
			idlst.put(equiptId, pleteTempId);
		}
		return idlst;
	}

}
