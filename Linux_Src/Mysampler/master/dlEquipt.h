#ifndef __DLEQUIPT_H
#define __DLEQUIPT_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#define IN
#define OUT
//设备动态库 函数APi 结构体节点
struct EquiptApiNode{
	char libName[100];     //设备动态库名称
	
	int (*DriverStop)(void *devstruct, void *PortHandle);     //打开驱动端口 API
	int (*DriverInit)(void *devstruct, void *PortHandleAddr); //关闭驱动端口 API
	int (*Read)(void *hComm, int nUnitNo, void* pvData);       //读取驱动端口数据 API
	int (*Control)(void *hComm,int nUnitNo, char *pCmdStr);    //发送驱动端口数据 API

	int (*Report)(void *hComm,  float *fdata,  char* sRecStr);                  //上报通信  API
	int (*Write)(void *hComm, char* pCmdStr, int nStrLen);                      //控制命令包 直接发送 API
};

struct EquiptApiNode *NewEquiptApiNode(char *libPathName);   //new 一个设备动态库 函数APi 结构体节点
bool DL_open(IN char *libName, OUT void **libHandleAddr);                //打开 加载动态库
bool DL_close(IN void *libHandle);                                      //关闭 加载动态库
bool DL_sym_AllApi(IN void *libHandle, OUT struct EquiptApiNode *ApiNode); //连接 加载动态库 函数

#endif