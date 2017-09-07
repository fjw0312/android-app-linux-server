package SAM.NetHAL;

//���� �����Ӧ �� ������
public class NetRS {

	public NetRS() {
		// TODO Auto-generated constructor stub
	}
	
	//----------------------------���˶��� �ͻ���ʹ�õ� ���� �豸���������-------------------------------
	public static byte[] request_EquiptNum_Buf()
	{
		//�������ʽ�� 0 99 99 0 CRC mean
		byte equiptId = 0;
		byte msgType = 99;
		short signalAddr = 0;
		short signalPara = 0;
		String mean = "get EquiptNum=?";
		NetMsg msg = new NetMsg();		
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}
	//----------------------------���˶��� �ͻ���ʹ�õ� �豸�ź� �����-----------------------------------
	public static byte[] request_EquiptSig_Buf(byte equiptId,short signalAddr, short signalPara)
	{
		//�������ʽ�� equiptId 3 0 0 CRC mean
		byte msgType = 3;
		String mean = "request_E="+String.valueOf(equiptId);
		NetMsg msg = new NetMsg();	
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		
		return null;
	}
	//----------------------------���˶��� �ͻ���ʹ�õ� �豸�������� �����-------------------------------
	public static byte[] request_EquiptCmd_Buf(byte equiptId,short signalAddr,short signalPara)
	{
		//�������ʽ�� equiptId 4 x x  CRC mean
		byte msgType = 4;
		String mean = "request_Cmd_E="+String.valueOf(equiptId);
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		
		return null;
	}
	//----------------------------���˶��� �ͻ���ʹ�õ� �豸�ַ����� �����-------------------------------
	public static byte[] request_EquiptStrCmd_Buf(byte equiptId,String mean)
	{
		//�������ʽ�� equiptId 5 0 0 CRC mean

		byte msgType = 5;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}
	//----------------------------���˶��� �ͻ���ʹ�õ� ��չ�����ַ���Ϣ �����---------------------------
	public static byte[] request_myStr_Buf(byte equiptId,String mean)
	{
		//�������ʽ�� 0 88 0 0 CRC mean
		byte msgType = 88;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}
	
	//-------------------------------------------------------------------------------------
	//================================���˶��� �ذ�����=========================================
	//----------------------------���˶��� �����ʹ�õ� ���� �豸�����ذ�-----------------------------
	public static byte[] respond_EquiptNum_Buf(short equiptNum)
	{
		//�ذ���ʽ�� 0 99 99 0 CRC mean
		byte equiptId = 0;
		byte msgType = 99;
		short signalAddr = equiptNum;
		short signalPara = 0;
		String mean = "EquiptNum="+String.valueOf(equiptNum);
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}
	//----------------------------���˶��� �����ʹ�õ� �豸�ź� �ذ�-------------------------------------
	public static byte[] respond_EquiptSig_Buf(byte equiptId,short signalAddr,short signalPara)
	{
		//�ذ���ʽ�� equiptId 3 0 0 CRC mean   //signalAddr = 0  signalPara=0 ���������豸������
		byte msgType = 3;
		String mean = "respond_E="+String.valueOf(equiptId);
		if((signalAddr==0) &&(signalPara == 0 )) signalPara = (short)NetMsg.NetMsg_BodyLen;
		if(signalAddr+signalPara/4 > NetMsg.NetMsg_BodyLen) return null;
		int n = signalPara/4;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{		
			//���� ���ݳ�����
			byte[] buf = new byte[NetMsg.NetMsg_HeadLen+4*n];
			System.arraycopy(msg.msgBuf, 0, buf, 0, NetMsg.NetMsg_HeadLen);
			
			float dataBuff[] = new float[NetMsg.NetMsg_BodyLen/4];
			//getEquiptData(equiptId, dataBuff);                   //�����ݳ� ��ȡ�豸�ź�----master
			int i = 0;
			for(i=0;i<n;i++)
			{
				float f_data = dataBuff[signalAddr+i];
				buf[NetMsg.NetMsg_HeadLen+4*i+0] = (byte)(((int)f_data>>8*3)&0xFF);
				buf[NetMsg.NetMsg_HeadLen+4*i+1] = (byte)(((int)f_data>>8*2)&0xFF);
				buf[NetMsg.NetMsg_HeadLen+4*i+2] = (byte)(((int)f_data>>8*1)&0xFF);
				buf[NetMsg.NetMsg_HeadLen+4*i+3] = (byte)(((int)f_data>>8*0)&0xFF);
			}
			return buf; //���ذ��ĳ���
		}
		return null;
	}
	
	//----------------------------���˶��� �����ʹ�õ� �豸�������� �ذ�---------------------------------
	public static byte[] respond_EquiptCmd_Buf(byte equiptId,short signalAddr,short signalPara)
	{
		//�ذ���ʽ�� equiptId 4 x x  CRC mean
		byte msgType = 4;
		String mean = "EquiptNum="+String.valueOf(equiptId);
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}
	//----------------------------���˶��� �����ʹ�õ� �豸�ַ����� �ذ�---------------------------------
	public static byte[] respond_EquiptStrCmd_Buf(byte equiptId,String mean)
	{
		//�ذ���ʽ�� equiptId 5 0 0 CRC mean
		byte msgType = 5;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}
	//----------------------------���˶��� �����ʹ�õ� ��չ�����ַ���Ϣ �ذ�-----------------------------
	public static byte[] respond_myStr_Buf(byte equiptId,String mean)
	{
		//�ذ���ʽ�� 0 88 0 0 CRC mean
		byte msgType = 88;
		short signalAddr = 0;
		short signalPara = 0;
		NetMsg msg = new NetMsg();
		if( msg.fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean.getBytes()) == 0)
		{
			return msg.msgBuf; //���ذ��ĳ���
		}
		return null;
	}

}
