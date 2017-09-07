package SAM.NetHAL;

import java.util.Hashtable;
import java.util.List;

import android.util.Log;

//����  ���� ����  ����ӿ�  ��
public class DataHAL {

	public DataHAL() {
		// TODO Auto-generated constructor stub
	}
	
	public static float rvData[] = new float[NetMsg.NetMsg_BodyLen/20];
	
	//��ȡ �ɼ��豸id ����
	public static Hashtable<Integer,Integer> getEquiptIdLst(){
		ReadSAMIni readSAM = new ReadSAMIni();
		readSAM.readSAMIni();
		return readSAM.idlst;
	}
	//��������ת�� ����
	public static float[] ByteToFloat(byte[] rvBuf){
		try{ 
		for(int i=0;i<rvBuf.length/20;i++){  
			 String str = new String(rvBuf,i*20,20);  
			 if(str==null || "".equals(str)) continue;
			 String str2 = str.replace(" ", "");
			 String str3 = str2.trim();
			 if(str3==null || "".equals(str3)) continue;
		//	 Log.e("DataHAL>>>ByteToFloat",str3);
			 float f_data = Float.parseFloat(str3);
			 rvData[i] = f_data;
			 str = null;
		//	 Log.e("DataHAL>>>ByteToFloat=",String.valueOf(f_data));			 
		}
		}catch(Exception e){
			Log.e("DataHAL>>ByteToFloat", "����ת�� �쳣�׳���");
			return null;
		}
		return rvData;
	}
	
	//��ȡ  ĳid�豸��  ��������
	public static float[] getEquiptSigLst(int equiptId){
		byte sendbuf[] = NetRS.request_EquiptSig_Buf((byte)equiptId, (short)0, (short)0); //��ȡ���Ͱ�
		byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);  
		byte recvbody[] = new byte[NetMsg.NetMsg_BodyLen];
		//�ж�������Ч
		if(recvbuf != null){
			System.arraycopy(recvbuf, NetMsg.NetMsg_HeadLen, recvbody, 0, NetMsg.NetMsg_BodyLen);
			return ByteToFloat(recvbody);
		}
		return null;
	}
	//����  ĳid�豸��  �����������ݰ�
	public static boolean setEquiptBufCmd(int equiptId,short signalAddr,short signalPara){
			byte sendbuf[] = NetRS.request_EquiptCmd_Buf((byte)equiptId, signalAddr,signalPara);
			byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);
			if(recvbuf !=null){
				return true;
			}
			return false;
	}
	//����  ĳid�豸��  ���������ַ�
	public static boolean setEquiptStrCmd(int equiptId,String mean){
		byte sendbuf[] = NetRS.request_EquiptStrCmd_Buf((byte)equiptId, mean);
		byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);
		if(recvbuf !=null){
			return true;
		}
		return false;
	}
	//����  ĳid�豸��  ���������ַ�
	public static byte[] setMyStr(int equiptId,String mean){
		byte sendbuf[] = NetRS.request_EquiptStrCmd_Buf((byte)equiptId, mean);
		byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);
		
		return recvbuf;
	}

}
