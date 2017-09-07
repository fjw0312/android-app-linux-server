#include<stdio.h>
#include<stdlib.h>
#include<string.h>


#include "net_msgHead.h"

#define  OUT
#define  IN




/*
//�����ͷ�ṹ��
struct netMsg{  //һ����30byte
	char startNode; //������ʼ��ʶ
	char equiptId;  //�豸id
	char msgType;   //�������� ���ƹ����룬03 �������� 04 ��������
	unsigned short signalAddr; //�źŵ�ַid
	unsigned short signalPara; //�ź��ֽ��� �ź���*4��������ֽڴ�С��  Ҳ��Ϊ������������
	unsigned short CRC;        //CRCУ��  ���豸id��ʼ�� 6byte ��У��
	char mean[20];             //�ַ�����
	char endNode;     //���Ľ��ޱ�ʶ
};
*/
//������ʱʹ��msg buf
//char msg_head[NetMsg_HeadLen];
//char msg_body[MaxSignalNum*4];
//char msg[NetMsg_HeadLen+MaxSignalNum*4];

//CRCУ��
unsigned short CRC16(unsigned char *Msg, unsigned short len)
{
	/*
    unsigned short CRC = 0xffff;
    unsigned short i,temp;
    while(len--)
    {
        CRC = CRC^(*Msg++);
        for(i=0;i<8;i++)
        {
            if(CRC&0x0001)
                CRC = (CRC>>1)^0xa001;
            else
                CRC>>=1;
        }
    }
    temp = CRC&0xff;
    CRC = ((CRC>>8)&0xff)+(temp<<8);   
*/
	//����java ����crc�޷��������� ���������У��
	unsigned short CRC = 0;
	int i = 0;
	for(i=0;i<len;i++)
	{
		CRC = CRC + Msg[i];
	}
	
    return(CRC);
}

//���� �����buf   ���� msgBuf
int fill_BufHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT char *msgBuf)
{
	if(msgBuf == NULL) return -1;
	//��ͷ��ֵ
	msgBuf[0] = Start;
	msgBuf[1] = equiptId;
	msgBuf[2] = msgType;
	msgBuf[3] = (unsigned char)((signalAddr>>8)&0x00ff);
	msgBuf[4] = (unsigned char)(signalAddr&0x00ff);
	msgBuf[5] = (unsigned char)((signalPara>>8)&0x00ff);
	msgBuf[6] = (unsigned char)(signalPara&0x00ff);
	//��ҪCRC��֤
	unsigned short	CRC = CRC16( (unsigned char *)(msgBuf+1), 6 );        // У����
    msgBuf[7] = (unsigned char)((CRC>>8) & 0x00FF);    // ��λ��ǰ
	msgBuf[8] = (unsigned char)(CRC & 0x00FF);         // ��λ�ں�
	int i = 0;
	for(i=0;i<20;i++)
	{
		msgBuf[9+i] = mean[i];
	}
	msgBuf[29] = End;
	
	return 0;
}
//���� �����msgHead   ���� msgHead
int fill_MsgHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT struct netMsg *msg)
{
	if(msg == NULL) return -1;
	
	msg->startNode = Start;
	msg->equiptId = equiptId;  //�豸id
	msg->msgType = msgType;   //�������� ���ƹ����룬03 �������� 04 ��������
	msg->signalAddr = signalAddr; //�źŵ�ַid
	msg->signalPara = signalPara; //�ź��ֽ��� �ź���*4  Ҳ��Ϊ������������
		char msgBuf[30];
		msgBuf[0] = Start;
		msgBuf[1] = equiptId;
		msgBuf[2] = msgType;
		msgBuf[3] = (unsigned char)((signalAddr>>8)&0x00ff);
		msgBuf[4] = (unsigned char)(signalAddr&0x00ff);
		msgBuf[5] = (unsigned char)((signalPara>>8)&0x00ff);
		msgBuf[6] = (unsigned char)(signalPara&0x00ff);
	msg->CRC  = CRC16( (unsigned char *)(msgBuf+1), 6 );        //CRCУ��  ���豸id��ʼ�� 6byte ��У��
	int i = 0;
	for(i=0;i<20;i++)
	{
		msg->mean[i] = mean[i];
	}
	msg->endNode = End;
	return 0;
}
//�� buf ת��Ϊ msgHead
int BufToMsgHead(IN char *msgBuf, OUT struct netMsg *msg) 
{
	if( ( msg == NULL )||(msgBuf == NULL)) return -1;
	if( (msgBuf[0] != Start)||(msgBuf[29] != End) ) return -1;

	msg->startNode = msgBuf[0];
	msg->equiptId = msgBuf[1];  //�豸id
	msg->msgType = msgBuf[2];   //�������� ���ƹ����룬03 �������� 04 ��������
	msg->signalAddr = ((unsigned short)(msgBuf[3]&0x00FF)<<8)|((unsigned short)(msgBuf[4]&0x00FF)); //�źŵ�ַid
	msg->signalPara = ((unsigned short)(msgBuf[5]&0x00FF)<<8)|((unsigned short)(msgBuf[6]&0x00FF)); //�ź��ֽ��� �ź���*4  Ҳ��Ϊ������������
	msg->CRC  = ((unsigned short)(msgBuf[7]&0x00FF)<<8)|((unsigned short)(msgBuf[8]&0x00FF));        //CRCУ��  ���豸id��ʼ�� 6byte ��У��
	int i = 0;
	for(i=0;i<20;i++)
	{
		msg->mean[i] = msgBuf[9+i];
	}
	msg->endNode = msgBuf[29];
	
	return 0;
}
//�� msgHead ת��Ϊ buf
int MsgHeadToBuf(OUT char *msgBuf, IN struct netMsg *msg)
{
	if( ( msg == NULL )||(msgBuf == NULL)) return -1;
	if( (msg->startNode != Start)||(msg->endNode != End) ) return -1;
		//��ͷ��ֵ
	msgBuf[0] = msg->startNode;
	msgBuf[1] = msg->equiptId;
	msgBuf[2] = msg->msgType;
	msgBuf[3] = (unsigned short)((msg->signalAddr>>8)&0x00ff);
	msgBuf[4] = (unsigned short)(msg->signalAddr&0x00ff);
	msgBuf[5] = (unsigned short)((msg->signalPara>>8)&0x00ff);
	msgBuf[6] = (unsigned short)(msg->signalPara&0x00ff);
	//unsigned short	CRC = CRC16( (unsigned char *)msgBuf, 6 );        // У����
    msgBuf[7] = (unsigned short)((msg->CRC>>8) & 0x00FF);    // ��λ��ǰ
	msgBuf[8] = (unsigned short)(msg->CRC & 0x00FF);         // ��λ�ں�
	int i = 0;
	for(i=0;i<20;i++)
	{
		msgBuf[9+i] = msg->mean[i];
	}
	msgBuf[29] = msg->endNode;
	return 0;
}






//���� ����buf
int fillNetBuf(IN struct netMsg *msg, IN char *bodyBuf,IN int bodyBufLenth, OUT char *NetBuf)
{
	//�ȹ���bufHead
	if( MsgHeadToBuf(NetBuf,msg) == -1)  return -1;
	
//	bodyBufLenth = msg->signalPara; //��ȡ���峤��
	
	//����bufBody
	memcpy((char *)(NetBuf+NetMsg_HeadLen), bodyBuf, bodyBufLenth);
	
	return 0;
}

//���� ����buf
int parseNetBuf(OUT struct netMsg *msg, OUT char *bodyBuf,OUT int bodyBufLenth, IN char *NetBuf)
{
	//���� msgHead
	if( BufToMsgHead(NetBuf,msg) == -1)  return -1;
	
	bodyBufLenth = msg->signalPara; //��ȡ���峤��
	
	//����bufBody
	memcpy(bodyBuf, (char *)(NetBuf+NetMsg_HeadLen), bodyBufLenth);
	
	return 0;
}


//----------------------------���˶��� ������յ�������-------------------------------
//----------------------------���˶��� ������յ�������-------------------------------



