#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<unistd.h>
#include <pthread.h>

#include "dlEquipt.h"
#include "samplerEquipt.h"
#include "readIni.h"
#include "master.h"

//------------------------------------------------------------
//---        文件说明：本文件为采集器整体控制管理文件
//--- 功能：数据模型线程池 业务采集管理  数据对外接口
//--- 作者：方炯文   2017.01.20    Email：fjw0312@163.com
//------------------------------------------------------------
#define IN
#define OUT

//上报动态库名称 "libUpDataEquiptX.so"
#define UpDataEquiptLibName "libUpDataEquipt"

int portNum = 0;    //实际端口 数量
int equiptNum = 0;  //实际设备 数量


// 实时数据收发 buf 数据池RdataBuf[设备id]  设备id = 1.2.3····
float RdataBuf[MaxEquiptNum][MaxSignalNum];      //实时数据read 接收                   buf[0]=设备id   buf[1]=通信状态1/0 
char  ScmdBuf[MaxEquiptNum][MaxSignalNum];       //实时数据control(send) 发送 指针数组 buf[0]=设备id   buf[0]=命令类型CONTROL_STAR/WRITE_STAR

//实时 数据上报 buf  数据池
char   RcmdBuff[MaxSignalNum];
float  SdataBuff[MaxSignalNum];

//实时 采集设备id数组
int  EquiptIdBuf[MaxEquiptNum];


//定义线程互斥锁变量 pthread_mutex_t  mt;
pthread_mutex_t  Rdata_mutex[MaxEquiptNum];    //数据采集 read 线程锁
pthread_mutex_t  Scmd_mutex[MaxEquiptNum];    //控制命令control/write 线程锁
pthread_mutex_t  Report_mutex;  //上报通信Report 线程锁

/*****************************************************************/
// 函数名称：master 主要功能执行函数函数--->samplerEquipt->dlEquipt->libEquipt.so
// 功能描述：底端采集器 采集入口函数
// 输入参数：
// 输出参数：
// 返    回：    
// 其    他：
/*****************************************************************/
void *master(void *arg)
{
	char spriBuf[200];

	//1. 读取配置文件
	//定义一个端口信息包 一个设备配置信息包 数组
	struct portMessage portMessS[MaxPortNum];  
	struct EquiptPortMessage equiptCfgMessS[MaxEquiptNum];
	
	portNum = ReadFile_PortCfg(portMessS);           //读取初始配置文件 端口配置		
	equiptNum = ReadFile_EquiptCfg(equiptCfgMessS);  //读取初始配置文件 端口配置
	if(equiptNum <= 0) pthread_exit(0); //return NULL;	
	int i=0,j=0;
	for(i=0;i<equiptNum;i++) //赋值 设备配置类中的对应 设备端口信息类
	{
		equiptCfgMessS[i].UpDataFlag = 0; //初始化 上报进程标志
		int e_portId = equiptCfgMessS[i].portId;
		EquiptIdBuf[i] = e_portId;
		for(j=0; j<portNum; j++)
		{
			if(portMessS[j].portId == e_portId)
			{
				equiptCfgMessS[i].pMes = &portMessS[j];  //获取 端口配置类
				break;
			}
		}
	}
	sprintf(spriBuf,"========================配置文件解析完毕！========================\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数

	
	//2变量初始化 及变量定义
	//初始化 buf 数据池
	int k = 0;
	for(k=0; k<MaxEquiptNum; k++){
		memset(RdataBuf[k], '\0', sizeof(RdataBuf[k])); //清零接收 buf
		memset(ScmdBuf[k], '\0', sizeof(ScmdBuf[k])); //清零接收 buf
		ScmdBuf[k][1] = NOSEND;  //初始化每个设备 发送命令buf 都为NOSEND
	}
	//初始化线程锁
	for(k=0;k<MaxEquiptNum;k++)
	{
		pthread_mutex_init( &(Rdata_mutex[k]), NULL);
		pthread_mutex_init( &(Scmd_mutex[k]), NULL);		
	}
	pthread_mutex_init( &Report_mutex, NULL);

	
	//定义 设备数据信息包指针数组
	struct message *SamplerMess_Node[equiptNum];
	//定义有上报设备标识
	int UpdataFlag = 0;
	
	//3.创建设备采集 子线程
	//创建每个设备子线程 并获取采集数据 
	for(i=0; i<equiptNum; i++){
		
		if( strstr(equiptCfgMessS[i].equiptLibName, ".so")==NULL ) continue;  //无运行设备端口 则返回
		if(equiptCfgMessS[i].equiptType ==  20)  UpdataFlag++;
		char *libName = equiptCfgMessS[i].equiptLibName;
		sprintf(spriBuf,"###master  ReadFile>>equiptCfgMessS[%d].equiptLibName=%s\n", equiptCfgMessS[i].equiptId, libName);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
		
		//实例化一个设备数据信息包
		SamplerMess_Node[i] = newSamplerMess((struct EquiptPortMessage *)&(equiptCfgMessS[i]), RdataBuf[equiptCfgMessS[i].equiptId], ScmdBuf[equiptCfgMessS[i].equiptId]);		

		//新建线程
		pthread_t pthread1;
		int ret = pthread_create(&pthread1, NULL, sampler, (void *)SamplerMess_Node[i]);
		if(ret == -1)
		{
			perror("master the pthread_create");
			sprintf(spriBuf, "master》》pthread_create error!\n");
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
		}
	}
	sleep(5);
	sprintf(spriBuf,"##################master创建采集线程 启动结束！######################\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	

	
	//初始化   上报数据buf
	RcmdBuff[0] = NOSEND;
	SdataBuff[0] = -1;
	
	//处理实时 上报数据
	while(UpdataFlag != 0)
	{			
		for(i=0; i<equiptNum; i++)//遍历采集设备 整体数据
		{
			//判断出采集的设备
			if(equiptCfgMessS[i].equiptType ==  10)
			{
				int EquiptId = equiptCfgMessS[i].equiptId;
				pthread_mutex_lock( &Rdata_mutex[EquiptId]);//锁住	此处一定要加锁 采集数据线程read 会重写RdataBuf 导致数据赋值值出错
				int ii;
				float f_Buff[MaxSignalNum];          //中间变量 buf				
				for(ii=0;ii<MaxSignalNum;ii++)
				{
					f_Buff[ii] = RdataBuf[EquiptId][ii];  //获取某个设备数据
				}
				pthread_mutex_unlock( &Rdata_mutex[EquiptId]);//再开锁
				
				//遍历上报设备  数据上报
				for(j=0; j<equiptNum;j++)
				{
					if(equiptCfgMessS[j].equiptType ==  20)
					{
						pthread_mutex_lock( &Report_mutex);//锁住  上报的采集数据 和 发送数据 操作 与 上报线程互斥
						for(ii=0;ii<MaxSignalNum;ii++)
						{
							SdataBuff[ii] = f_Buff[ii];  //获取某个设备数据
						}
						equiptCfgMessS[j].UpDataFlag = 1;
						pthread_mutex_unlock( &Report_mutex);//再开锁
						
					
						usleep(equiptCfgMessS[j].equiptReRunTime*1000); //采集周期
						//上报通信 处理上报命令
						if(equiptCfgMessS[j].UpDataFlag == 2)
						{						
							if((RcmdBuff[0] != NOSEND) && (RcmdBuff[0] != 0))
							{
								pthread_mutex_lock( &Report_mutex);//锁住  上报的采集数据 和 发送数据 操作 与 上报线程互斥
								setWriteCmd((int)RcmdBuff[0], RcmdBuff);
								memset(RcmdBuff, '\0', MaxSignalNum);          //控制命令清除
								RcmdBuff[0] = NOSEND;
								pthread_mutex_unlock( &Report_mutex);//再开锁
							}
							equiptCfgMessS[j].UpDataFlag = 0;							
						}
					sprintf(spriBuf,"###master>>>>%s上报%s设备的数据！\n", equiptCfgMessS[j].equiptLibName, equiptCfgMessS[i].equiptLibName);
					LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
					}
				}
			}
		}		
		
	}//while(true) end

	while(true)
	{
		sleep(5); //5s
	}
	//return NULL;
	pthread_exit(0);
}


/*****************************************************************/
// ---------------------------------------------------------------
// ==================对外数据操作函数接口(面向上层)===============
// ---------------------------------------------------------------
/*****************************************************************/
//   获取某设备 实时数据
void getEquiptData(IN int EquiptId, OUT float* fData)
{
	if(EquiptId>0 && EquiptId<MaxEquiptNum)
	{
		pthread_mutex_lock( &Rdata_mutex[EquiptId]);//锁住	
		int i = 0;
		for(i=0; i<MaxSignalNum; i++)
		{
			fData[i] = RdataBuf[EquiptId][i];
		}
		pthread_mutex_unlock( &Rdata_mutex[EquiptId] );//再开锁
		usleep(500); //0.5ms 的 防止 不断获取 占用了采集线程的时间片
	//	printf("master>>>>getEquiptData--RdataBuf[%d]>>>%f  %f  %f\n", EquiptId-1, RdataBuf[EquiptId-1][0], RdataBuf[EquiptId-1][1], RdataBuf[EquiptId-1][2]);
	//	printf("master>>>>getEquiptData--data[%d]>>>%f  %f  %f\n", EquiptId-1, fData[0], fData[1], fData[2]);		
	}
}
//   设置控制命令 control 包 strCmd 格式："1,88"  给设备EquiptId 信号1，发送88数据
void setControlCmd(IN int EquiptId, IN char* strCmd) //注意该 函数调用时 对同个设备EquiptId进行发命令 周期应大于2s (因为采集周期为约要2s)
{
	pthread_mutex_lock( &(Scmd_mutex[EquiptId]) );//锁住
	ScmdBuf[EquiptId][1] = CONTROL_STAR;
	strcpy((ScmdBuf[EquiptId]+2),strCmd);
	pthread_mutex_unlock( &(Scmd_mutex[EquiptId]) );//再开锁
	usleep(500); //0.5ms 的 防止 不断设置 占用了采集线程的时间片	
}
//   设置发送命令 write 包 strCmd 格式：01 03 00 00 00 08 44 0C   给EquiptId，发送该命令包
void setWriteCmd(IN int EquiptId, IN char* strCmd)//注意该 函数调用时 对同个设备EquiptId进行发命令 周期应大于2s
{
	pthread_mutex_lock( &(Scmd_mutex[EquiptId]) );//锁住
	ScmdBuf[EquiptId][1] = WRITE_STAR;
	int n = 0;
	for(n=0;n<MaxSignalNum-2;n++)
	{
		ScmdBuf[EquiptId][n+2] = strCmd[n];
	}
	pthread_mutex_unlock( &(Scmd_mutex[EquiptId]) );//再开锁
	usleep(500); //0.5ms 的 防止 不断设置 占用了采集线程的时间片	
}
//获取  采集器  采集设备id 数组
void get_equiptIdBuf(OUT int *idBuf)
{
	int i = 0;
	for(i=0;i<equiptNum;i++)
	{
		idBuf[i] = EquiptIdBuf[i];
	}
}
//获取 采集器的 采集设备个数
int get_equiptNum()
{
	return equiptNum;
}







