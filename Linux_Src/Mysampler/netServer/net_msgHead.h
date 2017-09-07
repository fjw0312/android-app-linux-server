#ifndef __NET_MSGHEAD_H
#define __NET_MSGHEAD_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>

#define  OUT
#define  IN
//定义包头长度
#define NetMsg_HeadLen  30
#define Start  1
#define End    '`'

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

//构建 网络包buf   返回 msgBuf
int fill_BufHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT char *msgBuf);
//构建 网络包msgHead   返回 msgHead
int fill_MsgHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT struct netMsg *msg);
//将 buf 转化为 msgHead
int BufToMsgHead(IN char *msgBuf, OUT struct netMsg *msg);
//将 msgHead 转化为 buf
int MsgHeadToBuf(OUT char *msgBuf, IN struct netMsg *msg);

//构建 网络buf
int fillNetBuf(IN struct netMsg *msg, IN char *bodyBuf,IN int bodyBufLenth, OUT char *NetBuf);
//解析 网络buf
int parseNetBuf(OUT struct netMsg *msg, OUT char *bodyBuf,OUT int bodyBufLenth, IN char *NetBuf);

#endif