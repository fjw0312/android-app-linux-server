package SAM.NetHAL;

public class NetMsg {

	public NetMsg() {
		// TODO Auto-generated constructor stub
	}
    public static int NetMsg_HeadLen = 30;    //包头长度
    public static int NetMsg_BodyLen = 800*20; //包体最大长度
    public static byte Start = 1;   //包的起始位
    public static char End   =  '`'; //包的结束符
    public byte[] msgBuf = new byte[NetMsg_HeadLen];
	
	
	public byte startNode; //包的启始标识位
	public byte equiptId;  //设备id
	public byte msgType;   //设备类型
	public short signalAddr; //信号地址id
	public short signalPara; //信号字节数 信号数*4（包体的字节大小）  也可为命令内容数据
	public short CRC;     //CRC校验  从设备id开始的 6byte 的校验
    public byte[] mean = new byte[20];  //字符含义
    public byte endNode;  //包的结束标识
    
    
  //CRC校验
   public short CRC16(byte[] Msg, int len)
    {
        short CRC = 0;
        for(int i=0;i<len;i++)
        {
        	CRC = (short)(CRC + (short)Msg[i]);
        }
  
        return(CRC);
    }
 //构建 网络包buf   返回 msgBuf
  public int fill_BufHead(byte equiptId, byte msgType, short signalAddr,short signalPara,byte[] in_mean)
   {
   	if(msgBuf == null) return -1;
   	//包头赋值
   	msgBuf[0] = Start;
   	msgBuf[1] = equiptId;
   	msgBuf[2] = msgType;
   	msgBuf[3] = (byte)((signalAddr>>8)&0x00ff);
   	msgBuf[4] = (byte)(signalAddr&0x00ff);
   	msgBuf[5] = (byte)((signalPara>>8)&0x00ff);
   	msgBuf[6] = (byte)(signalPara&0x00ff);
   	//需要CRC验证
   	byte buf[] = new byte[6];
   	System.arraycopy(msgBuf, 1, buf, 0, 6);
   	short	CRC = CRC16( buf, 6 );            // 校验码
    msgBuf[7] = (byte)((CRC>>8) & 0x00FF);    // 高位在前
   	msgBuf[8] = (byte)(CRC & 0x00FF);         // 低位在后
   	byte[] mean_buf = new byte[20];
   	System.arraycopy(in_mean, 0, mean_buf, 0, in_mean.length);
   	System.arraycopy(mean_buf, 0, msgBuf, 9, 20);
   	msgBuf[29] = (byte) End;
   	
   	return 0;
   }
//构建 网络包msgHead   返回 msgHead
	int fill_MsgHead(byte in_equiptId, byte in_msgType, short in_signalAddr,short in_signalPara,byte in_mean[])
	{		
		startNode = Start;
		equiptId = in_equiptId;  //设备id
		msgType = in_msgType;     //包的类型 类似功能码，03 请求数据 04 命令数据
		signalAddr = in_signalAddr; //信号地址id
		signalPara = in_signalPara; //信号字节数 信号数*4  也可为命令内容数据
		byte Buf[] = new byte[30];
			Buf[0] = equiptId;
			Buf[1] = msgType;
			Buf[2] = (byte)((signalAddr>>8)&0x00ff);
			Buf[3] = (byte)(signalAddr&0x00ff);
			Buf[4] = (byte)((signalPara>>8)&0x00ff);
			Buf[5] = (byte)(signalPara&0x00ff);
		CRC  = CRC16( (Buf), 6 );        //CRC校验  从设备id开始的 6byte 的校验
	   	byte[] mean_buf = new byte[20];
	   	System.arraycopy(in_mean, 0, mean_buf, 0, in_mean.length);
		System.arraycopy(mean_buf, 0, mean, 0, 20);
		endNode = (byte)End;
		return 0;
	}
	
	//将 buf 转化为 msgHead
	public int BufToMsgHead() 
	{
		if( (msgBuf[0]!=Start)||(msgBuf[29] != End) ){
			return -1;
		}
		startNode = msgBuf[0];
		equiptId = msgBuf[1];  //设备id
		msgType = msgBuf[2];   //包的类型 类似功能码，03 请求数据 04 命令数据
		signalAddr = (short) (((short)(msgBuf[3]&0x00FF)<<8)|((short)(msgBuf[4]&0x00FF))); //信号地址id
		signalPara =  (short) (((short)(msgBuf[5]&0x00FF)<<8)|((short)(msgBuf[6]&0x00FF))); //信号字节数 信号数*4  也可为命令内容数据
		CRC  =  (short) (((short)(msgBuf[7]&0x00FF)<<8)|((short)(msgBuf[8]&0x00FF)));     //CRC校验  从设备id开始的 6byte 的校验
		System.arraycopy(msgBuf, 9, mean, 0, 20);
		endNode = msgBuf[29];		
		return 0;
	}
	//将 msgHead 转化为 buf
	public int MsgHeadToBuf()
	{
		if( (startNode != Start)||(endNode != End) ) return -1;
			//包头赋值
		msgBuf[0] = startNode;
		msgBuf[1] = equiptId;
		msgBuf[2] = msgType;
		msgBuf[3] = (byte)((signalAddr>>8)&0x00ff);
		msgBuf[4] = (byte)(signalAddr&0x00ff);
		msgBuf[5] = (byte)((signalPara>>8)&0x00ff);
		msgBuf[6] = (byte)(signalPara&0x00ff);
		//unsigned short	CRC = CRC16( (unsigned char *)msgBuf, 6 );        // 校验码
	    msgBuf[7] = (byte)((CRC>>8) & 0x00FF);    // 高位在前
		msgBuf[8] = (byte)(CRC & 0x00FF);         // 低位在后
		System.arraycopy(mean, 0, msgBuf, 9, 20);
		msgBuf[29] = endNode;
		return 0;
	}
}
