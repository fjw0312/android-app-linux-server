#ifndef __SAMPLEREQUIPT_H
#define __SAMPLEREQUIPT_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#include "readIni.h"

#define IN
#define OUT

//定义 最大端口数量 20 最大采集设备数量 20  最大信号数800
#define  MaxPortNum    20
#define  MaxEquiptNum  20
#define  MaxSignalNum  800

//控制命令 起始标志
#define CONTROL_STAR   '&'
#define WRITE_STAR     '#'
#define NOSEND         '$'

//自定义一个信息结构体变量
struct message
{
	struct EquiptPortMessage *EquiptCfgMess;  //设备采集配置信息
	bool libEquipt_Loaded_Flag;  //动态库已加载 标志
	bool libPort_Open_Flag;      //端口已打开 标志

	float *readData_A;   //主机模式 read    的读取数据
	char *sCmdStr_A;    //主机模式 control 的发送数据	
	
//	char *recStr_B;     //上报模式  RecvStr      获取请求数据
//	float *sendData_B;  //上报模式  sendData  应答请求数据
//	int recStr_B_EquiptId;   //上报模式  RecvStr      获取请求数据设备的id
//	int sendData_B_EquiptId; //上报模式  sendData     应答请求数据设备的id
};

struct message *newSamplerMess(struct EquiptPortMessage *EquiptCfgMess, float *Data, char *Str);
void *sampler(void *arg);

#endif