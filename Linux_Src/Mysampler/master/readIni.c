#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<stdbool.h>
#include<unistd.h>

#include "readIni.h"

//----该文件为 读取配置文件 功能文件API

//读取的配置文件名称路径
#define INIFINE  "./SAM.ini"
//打印的日志文件名称路径   LogFile_Flag 是否打印日志
#define LogFilePath "./LogFile.log"
#define LogFile_Flag


//读取每一行字符
char readBuf[400];
char *sub = "=#>";	//定义分隔符变量


/*
//自定义一个端口信息结构体 变量
struct portMessage
{
	int  portId;                 //端口id号 eg:1
	char portDiverLibName[100]; //端口驱动动态库名称路劲 eg："/data/ChaoYF/fjw/TestSampler/comm_std_serial.so"
	char portDiverName[50];     //端口驱动名称路劲     eg: "/dev/ttyS3"
	char portPara[50];          //端口驱动配置参数     eg: "9600,n,8,1"
	int  portAttr;              //端口主从模式         eg: 1  主机模式
	int  portOutTime;           //端口超时时间         eg: 20000  20s
	
}; 

//自定义一个设备采集配置信息结构体  变量
struct EquiptPortMessage
{ 
	int  equiptId;            //设备id
    int  portId;              //运行端口id
	char equiptLibName[100]; //端口运行的设备动态库名称  "/data/ChaoYF/fjw/TestSampler/libEquipt1.so"  
	int  equiptType;          //设备的类型 10:采集设备  20:上报设备
	int  equiptAddr;          //设备地址
	int  equiptReRunTime;     //设备采集周期 ms
	
	//	void *porthandle;   //端口句柄  由portId ->portMessage ->EquiptInit获取到
	struct portMessage *pMes;   //端口信息 类
};



*/

//new 一个端口信息结构体包
struct portMessage *newPortMessage()
{
	struct portMessage *node = malloc(sizeof(struct portMessage));
	memset(node->portDiverLibName, '\0', 100);
	memset(node->portDiverName, '\0', 50);
	memset(node->portPara, '\0', 50);
	
	
	return node;
};
//new 一个设备配置信息结构体包
struct EquiptPortMessage *newEquiptPortMessage()
{
	struct EquiptPortMessage *node = malloc(sizeof(struct EquiptPortMessage));
	memset(node->equiptLibName, '\0', 100);
	
	return node;
};


//打开文件 读取文件 每行字符
int ReadFile_PortCfg(struct portMessage pMess[])  //IN OUT 结构体 （数组）指针
{
	char spriBuf[400];

	
	//打开文件
	FILE *file = fopen(INIFINE, "r"); //只读文件
	if(file==NULL)
	{
		sprintf(spriBuf, "打开文件%s失败！\n", INIFINE);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
		perror("fopen fail\n");
		return -1;
	}
	
	//定义变量
	int PortNumber = 0;        //采集器端口数量	
	char libPortPath[100];     //端口驱动动态库存放路径
	int i = 0;


	while( fgets(readBuf, 400, file))
	{
		//判断读取的字符内容
		if( strstr(readBuf, "PortNumber")) //获取 采集器端口数量
		{
			char *buf1 = strtok(readBuf, sub); //获取等号左边内容
			char *buf2 = strtok(NULL, sub);    //获取等号右边内容
			if(buf1==NULL || buf2==NULL) continue;
			if(buf2){
				PortNumber = atoi(buf2);
			}
		}else if(strstr(readBuf, "libPortPath"))  //获取 端口驱动动态库存放路径
		{
			char *buf1 = strtok(readBuf, sub); //获取等号左边内容
			char *buf2 = strtok(NULL, sub);    //获取等号右边内容
			if(buf1==NULL || buf2==NULL) continue;
			if(buf2){
				strcpy(libPortPath, buf2);   //buf2 有bug要去除掉空格
			}
		}else if( strstr(readBuf, "portSetlibDiver") )     //获取 采集器端口配置
		{
				char *buf1 = strtok(readBuf, sub); //获取等号左边内容
				char *buf2 = strtok(NULL, sub);    //获取等号右边内容 端口id  1
				char *buf3 = strtok(NULL, sub);    //获取等号右边内容comm_std_serial.so
				char *buf4 = strtok(NULL, sub);    //获取#截取内容 dev/ttyS3
				char *buf5 = strtok(NULL, sub);    //获取#截取内容 9600,n,8,1
				char *buf6 = strtok(NULL, sub);    //获取#截取内容 1
				char *buf7 = strtok(NULL, sub);    //获取#截取内容 20000
				if(buf1==NULL ||buf2==NULL || buf3==NULL) continue;			
				pMess[i].portId = atoi(buf2);
				strcpy(pMess[i].portDiverLibName, libPortPath);
				strcat(pMess[i].portDiverLibName, buf3);	
				strcpy(pMess[i].portPara, buf5);
				pMess[i].portAttr = atoi(buf6);
				pMess[i].portOutTime = atoi(buf7);
				if(strstr(buf4, " ")){
					memset(pMess[i].portDiverName, '\0', 50);
				}else{
					strcpy(pMess[i].portDiverName, buf4);
				}
				
				i++;
		}
	}
	fclose(file);

	sprintf(spriBuf,"PortNumber=%d libPortPath=%s\n", PortNumber,libPortPath);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	for(i=0; i<PortNumber; i++)
	{
		sprintf(spriBuf,"portMess[%d]>>portId=%d portDiverLibName=%s portDiverName=%s portPara=%s portAttr=%d portOutTime=%d\n",
		i,pMess[i].portId,pMess[i].portDiverLibName,pMess[i].portDiverName,pMess[i].portPara,pMess[i].portAttr,pMess[i].portOutTime);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	}
	
    return PortNumber;
}

//打开文件 读取文件 每行字符
int ReadFile_EquiptCfg(struct EquiptPortMessage pMess[])  //IN OUT 结构体 （数组）指针
{
	char spriBuf[400];
			
	//打开文件
	FILE *file = fopen(INIFINE, "r"); //只读文件
	if(file==NULL)
	{
		sprintf(spriBuf,"打开文件%s失败！\n", INIFINE);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
		perror("fopen fail\n");
		return -1;
	}
			
	//定义变量
	int EquiptNumber = 0; 	      //采集设备数量
	char libEquiptPath[100];	  //设备动态库存放路径
	int i = 0;

	while( fgets(readBuf, 400, file))
	{
			//判断读取的字符内容
			if( strstr(readBuf, "EquiptNumber")) //获取 采集器端口数量
			{
				char *buf1 = strtok(readBuf, sub); //获取等号左边内容
				char *buf2 = strtok(NULL, sub);    //获取等号右边内容
				if(buf1==NULL || buf2==NULL) continue;
				if(buf2){
					EquiptNumber = atoi(buf2);
				}
			}else if(strstr(readBuf, "libEquiptPath"))  //获取 端口驱动动态库存放路径
			{
				char *buf1 = strtok(readBuf, sub); //获取等号左边内容
				char *buf2 = strtok(NULL, sub);    //获取等号右边内容
				if(buf1==NULL || buf2==NULL) continue;
				if(buf2){
					strcpy(libEquiptPath, buf2);	 //buf2 有bug要去除掉空格
				}
			}else if( strstr(readBuf, "portRunEquipt") )	   //获取 采集器端口配置
			{
				char *buf1 = strtok(readBuf, sub); //获取等号左边内容
				char *buf2 = strtok(NULL, sub);    //设备id 1
				char *buf3 = strtok(NULL, sub);    //设备模板id 169
				char *buf4 = strtok(NULL, sub);    //端口id 1
				char *buf5 = strtok(NULL, sub);    //设备动态库 libEquipt.so
				char *buf6 = strtok(NULL, sub);    //采集类型  10:采集  20:上报
				char *buf7 = strtok(NULL, sub);    //设备地址
				char *buf8 = strtok(NULL, sub);    //采集周期
				if(buf1==NULL ||buf2==NULL || buf3==NULL) continue;
				pMess[i].equiptId = atoi(buf2);
				pMess[i].EquipTemplateId = atoi(buf3);
				pMess[i].portId = atoi(buf4);
				strcpy(pMess[i].equiptLibName, libEquiptPath);
				strcat(pMess[i].equiptLibName, buf5);   //
				pMess[i].equiptType = atoi(buf6);
				pMess[i].equiptAddr = atoi(buf7);	
				pMess[i].equiptReRunTime = atoi(buf8);	
				i++;			
			}
	}
	fclose(file);


	sprintf(spriBuf,"EquiptNumber=%d libEquiptPath=%s\n", EquiptNumber,libEquiptPath);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	for(i=0; i<EquiptNumber; i++)
	{
		sprintf(spriBuf,"equiptMess[%d]>>equiptId=%d portId=%d equiptLibName=%s equiptType=%d equiptAddr=%d equiptReRunTime=%d\n",
		i,pMess[i].equiptId,pMess[i].portId,pMess[i].equiptLibName,pMess[i].equiptType,pMess[i].equiptAddr,pMess[i].equiptReRunTime);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log 日志打印函数
	}

		
	return EquiptNumber;
}


//log 日志打印函数
void LogFile_OutPrintf(char *str, int lenth)
{

	printf("%s", str);
	
#ifdef 	LogFile_Flag	
	system("date +%Y-%m-%d_%H:%M:%S >> ./LogFile.log");
	//打开日志文件
	FILE *file = fopen(LogFilePath, "a+");
	if(file == NULL)
	{
		perror("fopen fail\n");
		return;
	}

	//写入内容
	fprintf(file, "%s", str);
	//关闭文件
	fclose(file);	
#endif

	//清零 字符变量 内容
	memset(str, '\0', lenth);

}
