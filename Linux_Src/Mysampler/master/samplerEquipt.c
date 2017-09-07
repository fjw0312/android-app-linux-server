#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>
#include<unistd.h>
#include<pthread.h>
#include<time.h>

#include "samplerEquipt.h"
#include "dlEquipt.h"

//该文件 为单个设备(libEquipt.so)的采集 函数

//实时 数据上报 buf  数据池
extern char  RcmdBuff[MaxSignalNum+1];
extern float  SdataBuff[MaxSignalNum+1];

extern pthread_mutex_t  Rdata_mutex[MaxEquiptNum];    //数据采集 read 线程锁
extern pthread_mutex_t  Scmd_mutex[MaxEquiptNum];    //控制命令control 线程锁
extern pthread_mutex_t  Report_mutex;  //上报通信Report 线程锁



char scmdBuf[MaxSignalNum];  //定义中间变量
/*
//控制命令 起始标志
#define CONTROL_STAR　'&'
#define WRITE_STAR    '#'
#define NOSEND        '$'
//自定义一个信息结构体变量
struct message
{
	struct EquiptPortMessage *EquiptCfgMess;  //设备采集配置信息
	bool libEquipt_Loaded_Flag;  //动态库已加载 标志
	bool libPort_Open_Flag;      //端口已打开 标志

	float *readData_A;   //主机模式 read    的读取数据
	char *sCmdStr_A;    //主机模式 control 的发送数据
};
*/

//new 一个设备采集信息包 结构体节点
struct message *newSamplerMess(struct EquiptPortMessage *EquiptCfgMess, float *Data, char *Str)
{
	struct message *node = malloc(sizeof(struct message));
	node->EquiptCfgMess = EquiptCfgMess; //获取设备配置信息包
	node->libEquipt_Loaded_Flag = true;  //默认设备动态库加载
	node->libPort_Open_Flag = true;      //默认设备动态库端口 打开

	
	Data[0] = EquiptCfgMess->equiptId;   //RDataBuf 首个为 设备id
	Str[0] = EquiptCfgMess->equiptId;    //SCmdBuf 首个为 设备id
	node->readData_A = Data;                   //初始化 read 获取的数据
	node->sCmdStr_A = Str;                      //初始化 control 获取的数据
	
	
	return node;
}

void *sampler(void *arg)
{
	char spriBuf[400]; //log buf
	
	struct message *mess = (void *)arg;
	struct EquiptPortMessage *EquiptCfgMess = mess->EquiptCfgMess; //获取设备配置类
	//设备动态库 句柄变量
	int zero = 0;
	void *libHandle = (void *)&zero;
	//new 一个设备动态库 API结构体节点
	struct EquiptApiNode *E_ApiNode = NewEquiptApiNode(EquiptCfgMess->equiptLibName);
	
	//加载 动态库 dlopen
	DL_open(EquiptCfgMess->equiptLibName, &libHandle);
	sprintf(spriBuf,"DL_open %s has done !\n", EquiptCfgMess->equiptLibName);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	
	//加载 动态库所有API dlsym
	DL_sym_AllApi(libHandle, E_ApiNode);
	sprintf(spriBuf,"DL_sym_AllApi %s has done !\n", EquiptCfgMess->equiptLibName);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	
	//运行动态库API 采集数据
	while(mess->libEquipt_Loaded_Flag)
	{
		//端口打开 正常采集
		if(mess->libPort_Open_Flag){
			//打开采集端口
			void *porthandle;
			E_ApiNode->DriverInit(EquiptCfgMess->pMes, &porthandle);
			sprintf(spriBuf,"E_ApiNode->DriverInit %s done success!\n", EquiptCfgMess->equiptLibName);
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
			
			//循环采集数据
			while(mess->libPort_Open_Flag) //不断循环采集 数据
		    {
				if(EquiptCfgMess->equiptType==10) //采集模式  主动read/control  特点：收发数据需要覆盖
				{
					//发送 命令
					//判断是否有控制命令发送
					if((mess->sCmdStr_A != NULL)&&((mess->sCmdStr_A)[1] == CONTROL_STAR ) ){   //首起标志位为CONTROL_STAR 控制命令
						//有控制命令发送

						
						pthread_mutex_lock( &(Scmd_mutex[EquiptCfgMess->equiptId]));           //锁住	
						memset(scmdBuf, '\0', MaxSignalNum);
						strcpy(scmdBuf, (char *)(mess->sCmdStr_A+2)); //先把数据命令 赋予变量
						memset((mess->sCmdStr_A+1), '\0', MaxSignalNum-1); //控制命令清除
						(mess->sCmdStr_A)[1] = NOSEND;						
						pthread_mutex_unlock( &(Scmd_mutex[EquiptCfgMess->equiptId]));         //再开锁
						
						E_ApiNode->Control(porthandle, EquiptCfgMess->equiptAddr, scmdBuf);  //发送控制命令
						sprintf(spriBuf,"samplerEquipt>>>子线程进入 CONTROL 发送 内容： %s\n", scmdBuf);
						LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
						continue;
					}else if( (mess->sCmdStr_A != NULL)&&((mess->sCmdStr_A)[1] == WRITE_STAR ) )
					{
						//有控制命令发送						
						pthread_mutex_lock( &(Scmd_mutex[EquiptCfgMess->equiptId]));          //锁住
						memset(scmdBuf, '\0', MaxSignalNum);
						int n = 0;
						for(n=0;n<MaxSignalNum-2;n++)
						{
							scmdBuf[n] = (mess->sCmdStr_A)[n+2];
						}
						memset((mess->sCmdStr_A+1), '\0', MaxSignalNum-1);          //控制命令清除
						(mess->sCmdStr_A)[1] = NOSEND;
						pthread_mutex_unlock( &(Scmd_mutex[EquiptCfgMess->equiptId]));        //再开锁	
						
						E_ApiNode->Write(porthandle, scmdBuf, MaxSignalNum);  //发送控制命令						
						sprintf(spriBuf,"samplerEquipt>>>子线程进入 WRITE 发送 内容：%s\n", scmdBuf); //若为8bit 数据 打印为字符很难辨识
						LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数						
						continue;
					}
					//读取 数据					
					float rBuf[MaxSignalNum-2];
					int ret = E_ApiNode->Read(porthandle, EquiptCfgMess->equiptAddr, rBuf);	//为阻塞函数  采集数正常
					pthread_mutex_lock( &(Rdata_mutex[EquiptCfgMess->equiptId]) );//锁住
					if(ret == 0) //通信中断
					{
						mess->readData_A[1] = 0; //通信中断 
					}else{
						mess->readData_A[1] = 1; //通信正常 
					}
					int i = 0;
					for(i=0; i<MaxSignalNum-2; i++)
					{
						mess->readData_A[i+2] = rBuf[i];
					}
					pthread_mutex_unlock( &(Rdata_mutex[EquiptCfgMess->equiptId]));//再开锁
					sprintf(spriBuf,"samplerEquipt>>>>准备数据采集ret:%d 设备id=%d>>>>>\n", ret, EquiptCfgMess->equiptId);
					LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
					//system("date +%Y-%m-%d_%H:%M:%S");
					//printf("子线程进入 read <设备id=%d>采集内容： %f %f %f\n", EquiptCfgMess->equiptId,(mess->readData_A)[0],(mess->readData_A)[1],(mess->readData_A)[2]);										
					usleep(EquiptCfgMess->equiptReRunTime*1000); //采集周期
				}else if(EquiptCfgMess->equiptType==20)   //上报模式 主动Report  特点：收发数据不需要覆盖
				{	
			//		printf("准备上报通信id=%d>>>>>\n", mess->PortMess->portId);
			//		system("date +%H:%M:%S");				
					//与 上报设备 上报接口通信
					if(EquiptCfgMess->UpDataFlag==1){  //等待 前一个上报数据准备结束 再接收
						pthread_mutex_lock( &Report_mutex);//锁住
						E_ApiNode->Report(porthandle, SdataBuff, RcmdBuff);						
					//	sprintf(spriBuf,"samplerEquipt>>>>上报数据通信设备id=%d 命令内容%s接收！\n", EquiptCfgMess->equiptId,RcmdBuff);
					//	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
						EquiptCfgMess->UpDataFlag = 2;						
						pthread_mutex_unlock( &Report_mutex);//再开锁
					}
					//printf("结束上报通信   >>>>>\n");
					system("date +%Y-%m-%d_%H:%M:%S");
					//usleep(1000*400); //通信周期200ms
					usleep(EquiptCfgMess->equiptReRunTime*1000); //采集周期
			   }
			}
			//关闭采集端口
			E_ApiNode->DriverStop(NULL, porthandle);
			sprintf(spriBuf,"samplerEquipt>>>E_ApiNode->DriverStop %s done success!\n", mess->EquiptCfgMess->equiptLibName);	
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数			
		}
		usleep(EquiptCfgMess->equiptReRunTime*1000);
	}
	
	//卸载 动态库 dlclose
	DL_close(libHandle); 
	sprintf(spriBuf,"samplerEquipt>>>>DL_close has done !\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	
	return NULL;
}