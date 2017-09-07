#ifndef __NET_MSGHEAD_H
#define __NET_MSGHEAD_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>

#define  OUT
#define  IN
//�����ͷ����
#define NetMsg_HeadLen  30
#define Start  1
#define End    '`'

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

//���� �����buf   ���� msgBuf
int fill_BufHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT char *msgBuf);
//���� �����msgHead   ���� msgHead
int fill_MsgHead(char equiptId, char msgType, unsigned short signalAddr,unsigned short signalPara,char mean[20],OUT struct netMsg *msg);
//�� buf ת��Ϊ msgHead
int BufToMsgHead(IN char *msgBuf, OUT struct netMsg *msg);
//�� msgHead ת��Ϊ buf
int MsgHeadToBuf(OUT char *msgBuf, IN struct netMsg *msg);

//���� ����buf
int fillNetBuf(IN struct netMsg *msg, IN char *bodyBuf,IN int bodyBufLenth, OUT char *NetBuf);
//���� ����buf
int parseNetBuf(OUT struct netMsg *msg, OUT char *bodyBuf,OUT int bodyBufLenth, IN char *NetBuf);

#endif