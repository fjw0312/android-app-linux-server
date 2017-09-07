package SAM.NetHAL;

//网络 请求回应 包 处理类
public class NetRS {

	public NetRS() {
		// TODO Auto-generated constructor stub
	}
	
	//----------------------------个人定义 客户端使用的 初步 设备数量请求包-------------------------------
	public static byte[] request_EquiptNum_Buf()
	{
		//请求包格式： 0 99 99 0 CRC mean
		byte equiptId = 0;
		byte msgType = 99;
		short signalAddr = 0;
		short signalPara = 0;
		String mean = "get EquiptNum=?";
		NetMsg msg = new NetMsg();		
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}
	//----------------------------个人定义 客户端使用的 设备信号 请求包-----------------------------------
	public static byte[] request_EquiptSig_Buf(byte equiptId,short signalAddr, short signalPara)
	{
		//请求包格式： equiptId 3 0 0 CRC mean
		byte msgType = 3;
		String mean = "request_E="+String.valueOf(equiptId);
		NetMsg msg = new NetMsg();	
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		
		return null;
	}
	//----------------------------个人定义 客户端使用的 设备数据命令 请求包-------------------------------
	public static byte[] request_EquiptCmd_Buf(byte equiptId,short signalAddr,short signalPara)
	{
		//请求包格式： equiptId 4 x x  CRC mean
		byte msgType = 4;
		String mean = "request_Cmd_E="+String.valueOf(equiptId);
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		
		return null;
	}
	//----------------------------个人定义 客户端使用的 设备字符命令 请求包-------------------------------
	public static byte[] request_EquiptStrCmd_Buf(byte equiptId,String mean)
	{
		//请求包格式： equiptId 5 0 0 CRC mean

		byte msgType = 5;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}
	//----------------------------个人定义 客户端使用的 拓展备用字符信息 请求包---------------------------
	public static byte[] request_myStr_Buf(byte equiptId,String mean)
	{
		//请求包格式： 0 88 0 0 CRC mean
		byte msgType = 88;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}
	
	//-------------------------------------------------------------------------------------
	//================================个人定义 回包部分=========================================
	//----------------------------个人定义 服务端使用的 初步 设备数量回包-----------------------------
	public static byte[] respond_EquiptNum_Buf(short equiptNum)
	{
		//回包格式： 0 99 99 0 CRC mean
		byte equiptId = 0;
		byte msgType = 99;
		short signalAddr = equiptNum;
		short signalPara = 0;
		String mean = "EquiptNum="+String.valueOf(equiptNum);
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}
	//----------------------------个人定义 服务端使用的 设备信号 回包-------------------------------------
	public static byte[] respond_EquiptSig_Buf(byte equiptId,short signalAddr,short signalPara)
	{
		//回包格式： equiptId 3 0 0 CRC mean   //signalAddr = 0  signalPara=0 请求整个设备的数据
		byte msgType = 3;
		String mean = "respond_E="+String.valueOf(equiptId);
		if((signalAddr==0) &&(signalPara == 0 )) signalPara = (short)NetMsg.NetMsg_BodyLen;
		if(signalAddr+signalPara/4 > NetMsg.NetMsg_BodyLen) return null;
		int n = signalPara/4;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{		
			//请求 数据池数据
			byte[] buf = new byte[NetMsg.NetMsg_HeadLen+4*n];
			System.arraycopy(msg.msgBuf, 0, buf, 0, NetMsg.NetMsg_HeadLen);
			
			float dataBuff[] = new float[NetMsg.NetMsg_BodyLen/4];
			//getEquiptData(equiptId, dataBuff);                   //向数据池 获取设备信号----master
			int i = 0;
			for(i=0;i<n;i++)
			{
				float f_data = dataBuff[signalAddr+i];
				buf[NetMsg.NetMsg_HeadLen+4*i+0] = (byte)(((int)f_data>>8*3)&0xFF);
				buf[NetMsg.NetMsg_HeadLen+4*i+1] = (byte)(((int)f_data>>8*2)&0xFF);
				buf[NetMsg.NetMsg_HeadLen+4*i+2] = (byte)(((int)f_data>>8*1)&0xFF);
				buf[NetMsg.NetMsg_HeadLen+4*i+3] = (byte)(((int)f_data>>8*0)&0xFF);
			}
			return buf; //返回包的长度
		}
		return null;
	}
	
	//----------------------------个人定义 服务端使用的 设备数据命令 回包---------------------------------
	public static byte[] respond_EquiptCmd_Buf(byte equiptId,short signalAddr,short signalPara)
	{
		//回包格式： equiptId 4 x x  CRC mean
		byte msgType = 4;
		String mean = "EquiptNum="+String.valueOf(equiptId);
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}
	//----------------------------个人定义 服务端使用的 设备字符命令 回包---------------------------------
	public static byte[] respond_EquiptStrCmd_Buf(byte equiptId,String mean)
	{
		//回包格式： equiptId 5 0 0 CRC mean
		byte msgType = 5;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}
	//----------------------------个人定义 服务端使用的 拓展备用字符信息 回包-----------------------------
	public static byte[] respond_myStr_Buf(byte equiptId,String mean)
	{
		//回包格式： 0 88 0 0 CRC mean
		byte msgType = 88;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //返回包的长度
		}
		return null;
	}

}
