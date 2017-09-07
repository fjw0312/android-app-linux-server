#ifndef __NET_BUF_RS_H
#define __NET_BUF_RS_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>


#define IN
#define OUT

//���� �豸������źŸ���
#define MaxSignalNum  800

//----------------------------���˶��� �ͻ���ʹ�õ� ���� �豸���������-------------------------------
int request_EquiptNum_Buf(OUT char *requestBuf);
//----------------------------���˶��� �����ʹ�õ� ���� �豸�����ذ�---------------------------------
int respond_EquiptNum_Buf(OUT char *respondBuf,IN unsigned short equiptNum);

//----------------------------���˶��� �ͻ���ʹ�õ� �豸�ź� �����-----------------------------------
int request_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf);
//----------------------------���˶��� �����ʹ�õ� �豸�ź� �ذ�-------------------------------------
int respond_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf);

//----------------------------���˶��� �ͻ���ʹ�õ� �豸�ַ����� �����-------------------------------
int request_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *requestBuf);
//----------------------------���˶��� �����ʹ�õ� �豸�ַ����� �ذ�---------------------------------
int respond_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *respondBuf);

//----------------------------���˶��� �ͻ���ʹ�õ� �豸�������� �����-------------------------------
int request_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf);
//----------------------------���˶��� �����ʹ�õ� �豸�������� �ذ�---------------------------------
int respond_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf);

//----------------------------���˶��� �ͻ���ʹ�õ� ��չ�����ַ���Ϣ �����---------------------------
int request_myStr_Buf(IN int equiptId,IN char *mean,OUT char *requestBuf);
//----------------------------���˶��� �����ʹ�õ� ��չ�����ַ���Ϣ �ذ�-----------------------------
int respond_myStr_Buf(IN int equiptId,IN char *mean,OUT char *respondBuf);


//----------------------------���˶��� �����ʹ�õ� �Զ� ���������Ӧ����-----------------------------
int getBuf_and_setbuf(IN char *getBuf, OUT char *setBuf);
//----------------------------���˶��� �ͻ���ʹ�õ� �����豸�ź� ���������---------------------------
void getEquiptIdBuf(OUT int *idBuf);
int getBuf_EquiptSig(IN int equiptId, OUT char *setBuf);
int getBuf_and_setdone(IN char *getBuf);

#endif