package SAM.NetHAL;

public class NetMsg {

	public NetMsg() {
		// TODO Auto-generated constructor stub
	}
    public static int NetMsg_HeadLen = 30;    //��ͷ����
    public static int NetMsg_BodyLen = 800*20; //������󳤶�
    public static byte Start = 1;   //������ʼλ
    public static char End   =  '`'; //���Ľ�����
    public byte[] msgBuf = new byte[NetMsg_HeadLen];
	
	
	public byte startNode; //������ʼ��ʶλ
	public byte equiptId;  //�豸id
	public byte msgType;   //�豸����
	public short signalAddr; //�źŵ�ַid
	public short signalPara; //�ź��ֽ��� �ź���*4��������ֽڴ�С��  Ҳ��Ϊ������������
	public short CRC;     //CRCУ��  ���豸id��ʼ�� 6byte ��У��
    public byte[] mean = new byte[20];  //�ַ�����
    public byte endNode;  //���Ľ�����ʶ
    
    
  //CRCУ��
   public short CRC16(byte[] Msg, int len)
    {
        short CRC = 0;
        for(int i=0;i<len;i++)
        {
        	CRC = (short)(CRC + (short)Msg[i]);
        }
  
        return(CRC);
    }
 //���� �����buf   ���� msgBuf
  public int fill_BufHead(byte equiptId, byte msgType, short signalAddr,short signalPara,byte[] in_mean)
   {
   	if(msgBuf == null) return -1;
   	//��ͷ��ֵ
   	msgBuf[0] = Start;
   	msgBuf[1] = equiptId;
   	msgBuf[2] = msgType;
   	msgBuf[3] = (byte)((signalAddr>>8)&0x00ff);
   	msgBuf[4] = (byte)(signalAddr&0x00ff);
   	msgBuf[5] = (byte)((signalPara>>8)&0x00ff);
   	msgBuf[6] = (byte)(signalPara&0x00ff);
   	//��ҪCRC��֤
   	byte buf[] = new byte[6];
   	System.arraycopy(msgBuf, 1, buf, 0, 6);
   	short	CRC = CRC16( buf, 6 );            // У����
    msgBuf[7] = (byte)((CRC>>8) & 0x00FF);    // ��λ��ǰ
   	msgBuf[8] = (byte)(CRC & 0x00FF);         // ��λ�ں�
   	byte[] mean_buf = new byte[20];
   	System.arraycopy(in_mean, 0, mean_buf, 0, in_mean.length);
   	System.arraycopy(mean_buf, 0, msgBuf, 9, 20);
   	msgBuf[29] = (byte) End;
   	
   	return 0;
   }
//���� �����msgHead   ���� msgHead
	int fill_MsgHead(byte in_equiptId, byte in_msgType, short in_signalAddr,short in_signalPara,byte in_mean[])
	{		
		startNode = Start;
		equiptId = in_equiptId;  //�豸id
		msgType = in_msgType;     //�������� ���ƹ����룬03 �������� 04 ��������
		signalAddr = in_signalAddr; //�źŵ�ַid
		signalPara = in_signalPara; //�ź��ֽ��� �ź���*4  Ҳ��Ϊ������������
		byte Buf[] = new byte[30];
			Buf[0] = equiptId;
			Buf[1] = msgType;
			Buf[2] = (byte)((signalAddr>>8)&0x00ff);
			Buf[3] = (byte)(signalAddr&0x00ff);
			Buf[4] = (byte)((signalPara>>8)&0x00ff);
			Buf[5] = (byte)(signalPara&0x00ff);
		CRC  = CRC16( (Buf), 6 );        //CRCУ��  ���豸id��ʼ�� 6byte ��У��
	   	byte[] mean_buf = new byte[20];
	   	System.arraycopy(in_mean, 0, mean_buf, 0, in_mean.length);
		System.arraycopy(mean_buf, 0, mean, 0, 20);
		endNode = (byte)End;
		return 0;
	}
	
	//�� buf ת��Ϊ msgHead
	public int BufToMsgHead() 
	{
		if( (msgBuf[0]!=Start)||(msgBuf[29] != End) ){
			return -1;
		}
		startNode = msgBuf[0];
		equiptId = msgBuf[1];  //�豸id
		msgType = msgBuf[2];   //�������� ���ƹ����룬03 �������� 04 ��������
		signalAddr = (short) (((short)(msgBuf[3]&0x00FF)<<8)|((short)(msgBuf[4]&0x00FF))); //�źŵ�ַid
		signalPara =  (short) (((short)(msgBuf[5]&0x00FF)<<8)|((short)(msgBuf[6]&0x00FF))); //�ź��ֽ��� �ź���*4  Ҳ��Ϊ������������
		CRC  =  (short) (((short)(msgBuf[7]&0x00FF)<<8)|((short)(msgBuf[8]&0x00FF)));     //CRCУ��  ���豸id��ʼ�� 6byte ��У��
		System.arraycopy(msgBuf, 9, mean, 0, 20);
		endNode = msgBuf[29];		
		return 0;
	}
	//�� msgHead ת��Ϊ buf
	public int MsgHeadToBuf()
	{
		if( (startNode != Start)||(endNode != End) ) return -1;
			//��ͷ��ֵ
		msgBuf[0] = startNode;
		msgBuf[1] = equiptId;
		msgBuf[2] = msgType;
		msgBuf[3] = (byte)((signalAddr>>8)&0x00ff);
		msgBuf[4] = (byte)(signalAddr&0x00ff);
		msgBuf[5] = (byte)((signalPara>>8)&0x00ff);
		msgBuf[6] = (byte)(signalPara&0x00ff);
		//unsigned short	CRC = CRC16( (unsigned char *)msgBuf, 6 );        // У����
	    msgBuf[7] = (byte)((CRC>>8) & 0x00FF);    // ��λ��ǰ
		msgBuf[8] = (byte)(CRC & 0x00FF);         // ��λ�ں�
		System.arraycopy(mean, 0, msgBuf, 9, 20);
		msgBuf[29] = endNode;
		return 0;
	}
}
