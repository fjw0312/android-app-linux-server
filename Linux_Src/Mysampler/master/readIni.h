#ifndef __READINI_H
#define __READINI_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#define IN
#define OUT


//自定义一个端口信息结构体 变量
struct portMessage
{
	int  portId;                 //端口id号 eg:1
	char portDiverLibName[100]; //端口驱动动态库名称路劲 eg："/data/mgrid/TestSampler/comm_std_serial.so"
	char portDiverName[50];     //端口驱动名称路劲     eg: "/dev/ttyS3"
	char portPara[50];          //端口驱动配置参数     eg: "9600,n,8,1"
	int  portAttr;              //端口主从模式         eg: 1  主机模式
	int  portOutTime;           //端口超时时间         eg: 20000  20s
	
//	char portRunEquiptLibName[100]; //端口运行的设备动态库名称（之后考虑兼容多个设备）
//	int  portRunEquiptAddr;        //端口运行的设备地址（之后考虑兼容多个设备）
}; 
//自定义一个设备采集配置信息结构体  变量
struct EquiptPortMessage
{ 
	int  equiptId;            //设备id
	int  EquipTemplateId;     //设备模板id
    int  portId;              //运行端口id
	char equiptLibName[100]; //端口运行的设备动态库名称
	int  equiptType;          //设备的类型 10:采集设备  20:上报设备
	int  equiptAddr;          //设备地址
	int  equiptReRunTime;     //设备采集周期 ms

//	void *porthandle;   //端口句柄  由portId ->portMessage ->EquiptInit获取到
	struct portMessage *pMes;   //端口信息 类
	int  UpDataFlag;   //上报的过程标志  0：未上报状态 1：上报数据赋值 2：上报通信中 3：上报通信结束处理返回命令 4：上报周期结束
	
};


//new 一个端口信息结构体包
struct portMessage *newPortMessage();
//new 一个设备配置信息结构体包
struct EquiptPortMessage *newEquiptPortMessage();
//打开文件 读取文件 每行字符
int ReadFile_PortCfg(struct portMessage *pMess);
//打开文件 读取文件 每行字符
int ReadFile_EquiptCfg(struct EquiptPortMessage *pMess);

void LogFile_OutPrintf(char *str, int lenth);                      //log 日志打印函数

#endif