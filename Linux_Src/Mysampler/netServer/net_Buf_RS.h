#ifndef __NET_BUF_RS_H
#define __NET_BUF_RS_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>


#define IN
#define OUT

//定义 设备的最大信号个数
#define MaxSignalNum  800

//----------------------------个人定义 客户端使用的 初步 设备数量请求包-------------------------------
int request_EquiptNum_Buf(OUT char *requestBuf);
//----------------------------个人定义 服务端使用的 初步 设备数量回包---------------------------------
int respond_EquiptNum_Buf(OUT char *respondBuf,IN unsigned short equiptNum);

//----------------------------个人定义 客户端使用的 设备信号 请求包-----------------------------------
int request_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf);
//----------------------------个人定义 服务端使用的 设备信号 回包-------------------------------------
int respond_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf);

//----------------------------个人定义 客户端使用的 设备字符命令 请求包-------------------------------
int request_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *requestBuf);
//----------------------------个人定义 服务端使用的 设备字符命令 回包---------------------------------
int respond_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *respondBuf);

//----------------------------个人定义 客户端使用的 设备数据命令 请求包-------------------------------
int request_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf);
//----------------------------个人定义 服务端使用的 设备数据命令 回包---------------------------------
int respond_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf);

//----------------------------个人定义 客户端使用的 拓展备用字符信息 请求包---------------------------
int request_myStr_Buf(IN int equiptId,IN char *mean,OUT char *requestBuf);
//----------------------------个人定义 服务端使用的 拓展备用字符信息 回包-----------------------------
int respond_myStr_Buf(IN int equiptId,IN char *mean,OUT char *respondBuf);


//----------------------------个人定义 服务端使用的 自动 数据请求回应函数-----------------------------
int getBuf_and_setbuf(IN char *getBuf, OUT char *setBuf);
//----------------------------个人定义 客户端使用的 所有设备信号 请求包命令---------------------------
void getEquiptIdBuf(OUT int *idBuf);
int getBuf_EquiptSig(IN int equiptId, OUT char *setBuf);
int getBuf_and_setdone(IN char *getBuf);

#endif