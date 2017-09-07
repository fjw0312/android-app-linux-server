package SAM.NetHAL;

import java.util.Hashtable;
import java.util.List;

import android.util.Log;

//网络  数据 控制  对外接口  类
public class DataHAL {

	public DataHAL() {
		// TODO Auto-generated constructor stub
	}
	
	public static float rvData[] = new float[NetMsg.NetMsg_BodyLen/20];
	
	//获取 采集设备id 链表
	public static Hashtable<Integer,Integer> getEquiptIdLst(){
		ReadSAMIni readSAM = new ReadSAMIni();
		readSAM.readSAMIni();
		return readSAM.idlst;
	}
	//数据类型转换 函数
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
			Log.e("DataHAL>>ByteToFloat", "数据转换 异常抛出！");
			return null;
		}
		return rvData;
	}
	
	//获取  某id设备的  网络数据
	public static float[] getEquiptSigLst(int equiptId){
		byte sendbuf[] = NetRS.request_EquiptSig_Buf((byte)equiptId, (short)0, (short)0); //获取发送包
		byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);  
		byte recvbody[] = new byte[NetMsg.NetMsg_BodyLen];
		//判断数据有效
		if(recvbuf != null){
			System.arraycopy(recvbuf, NetMsg.NetMsg_HeadLen, recvbody, 0, NetMsg.NetMsg_BodyLen);
			return ByteToFloat(recvbody);
		}
		return null;
	}
	//发送  某id设备的  控制命令数据包
	public static boolean setEquiptBufCmd(int equiptId,short signalAddr,short signalPara){
			byte sendbuf[] = NetRS.request_EquiptCmd_Buf((byte)equiptId, signalAddr,signalPara);
			byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);
			if(recvbuf !=null){
				return true;
			}
			return false;
	}
	//发送  某id设备的  控制命令字符
	public static boolean setEquiptStrCmd(int equiptId,String mean){
		byte sendbuf[] = NetRS.request_EquiptStrCmd_Buf((byte)equiptId, mean);
		byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);
		if(recvbuf !=null){
			return true;
		}
		return false;
	}
	//发送  某id设备的  控制命令字符
	public static byte[] setMyStr(int equiptId,String mean){
		byte sendbuf[] = NetRS.request_EquiptStrCmd_Buf((byte)equiptId, mean);
		byte recvbuf[] = ClientHAL.send_and_receive(sendbuf);
		
		return recvbuf;
	}

}
