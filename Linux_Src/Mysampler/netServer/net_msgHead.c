#include<stdio.h>
#include<stdlib.h>
#include<string.h>


#include "net_msgHead.h"

#define  OUT
#define  IN




/*
//网络包头结构体
struct netMsg{  //一个包30byte
	char startNode; //包的起始标识
	char equiptId;  //设备id
	char msgType;   //包的类型 类似功能码，03 请求数据 04 命令数据
	unsigned short signalAddr; //信号地址id
	unsigned short signalPara; //信号字节数 信号数*4（包体的字节大小）  也可为命令内容数据
	unsigned short CRC;        //CRC校验  从设备id开始的 6byte 的校验
	char mean[20];             //字符含义
	char endNode;     //包的借宿标识
};
*/
//定义临时使用msg buf
//char msg_head[NetMsg_HeadLen];
//char msg_body[MaxSignalNum*4];
//char msg[NetMsg_HeadLen+MaxSignalNum*4];

//CRC校验
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
	//由于java 处理crc无符号有困难 故重新设计校验
	unsigned short CRC = 0;
	int i = 0;
	for(i=0;i<len;i++)
	{
		CRC = CRC + Msg[i];
	}
	
    return(CRC);
}

//构建 网络包buf   返回 msgBuf
int fill_BufHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT char *msgBuf)
{
	if(msgBuf == NULL) return -1;
	//包头赋值
	msgBuf[0] = Start;
	msgBuf[1] = equiptId;
	msgBuf[2] = msgType;
	msgBuf[3] = (unsigned char)((signalAddr>>8)&0x00ff);
	msgBuf[4] = (unsigned char)(signalAddr&0x00ff);
	msgBuf[5] = (unsigned char)((signalPara>>8)&0x00ff);
	msgBuf[6] = (unsigned char)(signalPara&0x00ff);
	//需要CRC验证
	unsigned short	CRC = CRC16( (unsigned char *)(msgBuf+1), 6 );        // 校验码
    msgBuf[7] = (unsigned char)((CRC>>8) & 0x00FF);    // 高位在前
	msgBuf[8] = (unsigned char)(CRC & 0x00FF);         // 低位在后
	int i = 0;
	for(i=0;i<20;i++)
	{
		msgBuf[9+i] = mean[i];
	}
	msgBuf[29] = End;
	
	return 0;
}
//构建 网络包msgHead   返回 msgHead
int fill_MsgHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT struct netMsg *msg)
{
	if(msg == NULL) return -1;
	
	msg->startNode = Start;
	msg->equiptId = equiptId;  //设备id
	msg->msgType = msgType;   //包的类型 类似功能码，03 请求数据 04 命令数据
	msg->signalAddr = signalAddr; //信号地址id
	msg->signalPara = signalPara; //信号字节数 信号数*4  也可为命令内容数据
		char msgBuf[30];
		msgBuf[0] = Start;
		msgBuf[1] = equiptId;
		msgBuf[2] = msgType;
		msgBuf[3] = (unsigned char)((signalAddr>>8)&0x00ff);
		msgBuf[4] = (unsigned char)(signalAddr&0x00ff);
		msgBuf[5] = (unsigned char)((signalPara>>8)&0x00ff);
		msgBuf[6] = (unsigned char)(signalPara&0x00ff);
	msg->CRC  = CRC16( (unsigned char *)(msgBuf+1), 6 );        //CRC校验  从设备id开始的 6byte 的校验
	int i = 0;
	for(i=0;i<20;i++)
	{
		msg->mean[i] = mean[i];
	}
	msg->endNode = End;
	return 0;
}
//将 buf 转化为 msgHead
int BufToMsgHead(IN char *msgBuf, OUT struct netMsg *msg) 
{
	if( ( msg == NULL )||(msgBuf == NULL)) return -1;
	if( (msgBuf[0] != Start)||(msgBuf[29] != End) ) return -1;

	msg->startNode = msgBuf[0];
	msg->equiptId = msgBuf[1];  //设备id
	msg->msgType = msgBuf[2];   //包的类型 类似功能码，03 请求数据 04 命令数据
	msg->signalAddr = ((unsigned short)(msgBuf[3]&0x00FF)<<8)|((unsigned short)(msgBuf[4]&0x00FF)); //信号地址id
	msg->signalPara = ((unsigned short)(msgBuf[5]&0x00FF)<<8)|((unsigned short)(msgBuf[6]&0x00FF)); //信号字节数 信号数*4  也可为命令内容数据
	msg->CRC  = ((unsigned short)(msgBuf[7]&0x00FF)<<8)|((unsigned short)(msgBuf[8]&0x00FF));        //CRC校验  从设备id开始的 6byte 的校验
	int i = 0;
	for(i=0;i<20;i++)
	{
		msg->mean[i] = msgBuf[9+i];
	}
	msg->endNode = msgBuf[29];
	
	return 0;
}
//将 msgHead 转化为 buf
int MsgHeadToBuf(OUT char *msgBuf, IN struct netMsg *msg)
{
	if( ( msg == NULL )||(msgBuf == NULL)) return -1;
	if( (msg->startNode != Start)||(msg->endNode != End) ) return -1;
		//包头赋值
	msgBuf[0] = msg->startNode;
	msgBuf[1] = msg->equiptId;
	msgBuf[2] = msg->msgType;
	msgBuf[3] = (unsigned short)((msg->signalAddr>>8)&0x00ff);
	msgBuf[4] = (unsigned short)(msg->signalAddr&0x00ff);
	msgBuf[5] = (unsigned short)((msg->signalPara>>8)&0x00ff);
	msgBuf[6] = (unsigned short)(msg->signalPara&0x00ff);
	//unsigned short	CRC = CRC16( (unsigned char *)msgBuf, 6 );        // 校验码
    msgBuf[7] = (unsigned short)((msg->CRC>>8) & 0x00FF);    // 高位在前
	msgBuf[8] = (unsigned short)(msg->CRC & 0x00FF);         // 低位在后
	int i = 0;
	for(i=0;i<20;i++)
	{
		msgBuf[9+i] = msg->mean[i];
	}
	msgBuf[29] = msg->endNode;
	return 0;
}






//构建 网络buf
int fillNetBuf(IN struct netMsg *msg, IN char *bodyBuf,IN int bodyBufLenth, OUT char *NetBuf)
{
	//先构建bufHead
	if( MsgHeadToBuf(NetBuf,msg) == -1)  return -1;
	
//	bodyBufLenth = msg->signalPara; //获取包体长度
	
	//构建bufBody
	memcpy((char *)(NetBuf+NetMsg_HeadLen), bodyBuf, bodyBufLenth);
	
	return 0;
}

//解析 网络buf
int parseNetBuf(OUT struct netMsg *msg, OUT char *bodyBuf,OUT int bodyBufLenth, IN char *NetBuf)
{
	//解析 msgHead
	if( BufToMsgHead(NetBuf,msg) == -1)  return -1;
	
	bodyBufLenth = msg->signalPara; //获取包体长度
	
	//解析bufBody
	memcpy(bodyBuf, (char *)(NetBuf+NetMsg_HeadLen), bodyBufLenth);
	
	return 0;
}


//----------------------------个人定义 网络接收到包处理-------------------------------
//----------------------------个人定义 网络接收到包处理-------------------------------



