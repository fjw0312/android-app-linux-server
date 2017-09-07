#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "net_Buf_RS.h"
#include "net_msgHead.h"

#include "master.h"


#define  OUT
#define  IN




unsigned int equiptNum;  //采集设备 数量
char  MyStr[20];  //拓展备用字符   接收到的内容

/*******************************
*         说明：
* author:fjw0312@163.com  date:2016.12.27
* 网络包定义 功能码 99 03 04 05 88  100(心跳包)
*******************************/


//----------------------------个人定义 客户端使用的 初步 设备数量请求包-------------------------------
int request_EquiptNum_Buf(OUT char *requestBuf)
{
	//请求包格式： 0 99 99 0 CRC mean
	char equiptId = 0;
	char msgType = 99;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	char mean[20] = "get EquiptNum=?";
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptNum_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		          (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return -1;
}
//----------------------------个人定义 服务端使用的 初步 设备数量回包---------------------------------
int respond_EquiptNum_Buf(OUT char *respondBuf,IN unsigned short equiptNum)
{
	//回包格式： 0 99 99 0 CRC mean
	char equiptId = 0;
	char msgType = 99;
	unsigned short signalAddr = equiptNum;
	unsigned short signalPara = 0;
	char mean[20];	
	sprintf(mean, "get EquiptNum=%d", equiptNum);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_EquiptNum_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		         (unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return -1;
}

//----------------------------个人定义 客户端使用的 设备信号 请求包-----------------------------------
int request_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf)
{
	//请求包格式： equiptId 3 0 0 CRC mean

	char msgType = 3;
	char mean[20];
	sprintf(mean, "request_E=%d", equiptId);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptSig_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		          (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	
	return -1;
}
//----------------------------个人定义 服务端使用的 设备信号 回包-------------------------------------
int respond_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf)
{
	//回包格式： equiptId 3 0 0 CRC mean   //signalAddr = 0  signalPara=0 请求整个设备的数据
	char msgType = 3;
	char mean[20];
	sprintf(mean, "respond_E=%d", equiptId);
	
	if((signalAddr==0) &&(signalPara == 0 )) signalPara = MaxSignalNum*20;  //float转化为char[20]字符
	if(signalAddr+signalPara/20 > MaxSignalNum) return -1;
	int n = signalPara/20;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{		
		//请求 数据池数据
		float dataBuff[MaxSignalNum+1] = {10,20,3,5,7,9};
		getEquiptData(equiptId, dataBuff);                   //向数据池 获取设备信号----master
		int i = 0;
		for(i=0;i<n;i++)
		{
			float f_data = dataBuff[signalAddr+i];
			char s_data[20]={0};
			sprintf(s_data, "%f", f_data);
			int k = 0;
			for(k=0;k<20;k++){
				respondBuf[NetMsg_HeadLen+20*i +k] = s_data[k];
			}
		//	respondBuf[NetMsg_HeadLen+4*i+0] = (unsigned char)(((int)f_data>>8*3)&0xFF);
		//	respondBuf[NetMsg_HeadLen+4*i+1] = (unsigned char)(((int)f_data>>8*2)&0xFF);
		//	respondBuf[NetMsg_HeadLen+4*i+2] = (unsigned char)(((int)f_data>>8*1)&0xFF);
		//	respondBuf[NetMsg_HeadLen+4*i+3] = (unsigned char)(((int)f_data>>8*0)&0xFF);
		}
		//printf("respond_EquiptSig_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n",  
		//      	(unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
		//		 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen+10*n; //返回包的长度
	}
	return -1;
}
//----------------------------个人定义 客户端使用的 设备数据命令 请求包-------------------------------
int request_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf)
{
	//请求包格式： equiptId 4 x x  CRC mean
	char msgType = 4;
	char mean[20];
	sprintf(mean, "request_Cmd_E=%d", equiptId);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		          (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return -1;
}
//----------------------------个人定义 服务端使用的 设备数据命令 回包---------------------------------
int respond_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf)
{
	//回包格式： equiptId 4 x x  CRC mean
	char msgType = 4;
	char mean[20];
	sprintf(mean, "respond_Cmd_E=%d", equiptId);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_EquiptCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n",  
				 (unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return -1;
}
//----------------------------个人定义 客户端使用的 设备字符命令 请求包-------------------------------
int request_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *requestBuf)
{
	//请求包格式： equiptId 5 0 0 CRC mean

	char msgType = 5;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptStrCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n",  
				  (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return 0;
}
//----------------------------个人定义 服务端使用的 设备字符命令 回包---------------------------------
int respond_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *respondBuf)
{
	//回包格式： equiptId 5 0 0 CRC mean

	char msgType = 5;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_EquiptStrCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		       	(unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return 0;
}

//----------------------------个人定义 客户端使用的 拓展备用字符信息 请求包---------------------------
int request_myStr_Buf(IN int equiptId,IN char *mean,OUT char *requestBuf)
{
	//请求包格式： 0 88 0 0 CRC mean
	char msgType = 88;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_myStr_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		    	  (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}
	return -1;
}
//----------------------------个人定义 服务端使用的 拓展备用字符信息 回包-----------------------------
int respond_myStr_Buf(IN int equiptId,IN char *mean,OUT char *respondBuf)
{
	//回包格式： 0 88 0 0 CRC mean
	char msgType = 88;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_myStr_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		     	(unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //返回包的长度
	}

	return -1;
}


//----------------------------个人定义 服务端使用的 自动 数据请求回应函数-----------------------------
int getBuf_and_setbuf(IN char *getBuf, OUT char *setBuf)
{
	if((getBuf == NULL)||(setBuf==NULL)) return 0;
	printf("net_Buf_RS>>getBuf_and_setbuf  into!\n");
	struct netMsg *Msg = malloc(sizeof(struct netMsg ));
	BufToMsgHead(getBuf, Msg);
	int ret = 0;
	//判断 请求包类型
	if(Msg->msgType == 3)           //请求设备数据
	{
		ret = respond_EquiptSig_Buf(Msg->equiptId,Msg->signalAddr, Msg->signalPara,setBuf); //赋值回包 内容
	}else if(Msg->msgType == 4)     //控制命令包
	{
		ret = respond_EquiptCmd_Buf(Msg->equiptId,Msg->signalAddr, Msg->signalPara,setBuf);  //赋值回包 内容
		char strCmd[8] ={0};
		int i = 0;
		for(i=0; i<8; i++)       //处理 使 respond_EquiptCmd_Buf只获取前8byte命令包 给数据池
		{
			strCmd[i] = getBuf[i];
		}
		//memcpy(strCmd, (char *)(getBuf+1), 8);
		setWriteCmd(Msg->equiptId, strCmd);		            //向数据池 添加控制命令 ----master
	}else if(Msg->msgType == 5)     //控制命令字符
	{
		ret = respond_EquiptStrCmd_Buf(Msg->equiptId, Msg->mean,setBuf);                      //赋值回包 内容
		char strCmd[20];
		strncpy(strCmd, Msg->mean, sizeof(strCmd));
		setControlCmd(Msg->equiptId, strCmd);		       //向数据池 添加控制命令 ----master
	}else if(Msg->msgType == 99)   //请求设备数量
	{
		unsigned short equiptNum = get_equiptNum();        //向数据池 获取设备数量 ----master
		//unsigned short equiptNum = 55;
		ret = respond_EquiptNum_Buf(setBuf, equiptNum);                                                //赋值回包 内容
	}else if(Msg->msgType == 88)   //请求拓展备用字符
	{
		memset(MyStr, '\0', 20);
		strncpy(MyStr, Msg->mean, sizeof(MyStr));
		ret = respond_myStr_Buf(Msg->equiptId, Msg->mean,setBuf);                         //赋值回包 内容
	}else{
		return 0;
		Msg = NULL;
		free(Msg);
	}
	
	Msg = NULL;
	free(Msg);
	if(ret == 0)  return 0;

	
	return ret;   //返回setBuf 的字节数
}

//----------------------------个人定义 客户端使用的 所有设备信号 请求包命令---------------------------
void getEquiptIdBuf(OUT int *idBuf)
{
	get_equiptIdBuf(idBuf);
}
int getBuf_EquiptSig(IN int equiptId, OUT char *setBuf)
{
	int ret = -1;
	ret = respond_EquiptSig_Buf(equiptId,0, 0,setBuf); //赋值回包 内容
	return ret;
}
int getBuf_and_setdone(IN char *getBuf)
{
	if(getBuf == NULL) return 0;
	printf("net_Buf_RS>>getBuf_and_setbuf  into!\n");
	struct netMsg *Msg = malloc(sizeof(struct netMsg ));
	BufToMsgHead(getBuf, Msg);
	int ret = 0;
	//判断 请求包类型
	if(Msg->msgType == 3)           //请求设备数据
	{
		//设备数据主动上报通信  不会使用服务端来客户端请求数据
	}else if(Msg->msgType == 4)     //控制命令包
	{
		ret = 1;
		char strCmd[8] ={0};
		int i = 0;
		for(i=0; i<8; i++)       //处理 使 respond_EquiptCmd_Buf只获取前8byte命令包 给数据池
		{
			strCmd[i] = getBuf[i];
		}
		//memcpy(strCmd, (char *)(getBuf+1), 8);
		setWriteCmd(Msg->equiptId, strCmd);		            //向数据池 添加控制命令 ----master
	}else if(Msg->msgType == 5)     //控制命令字符
	{
		ret = 1;
		char strCmd[20];
		strncpy(strCmd, Msg->mean, sizeof(strCmd));
		setControlCmd(Msg->equiptId, strCmd);		       //向数据池 添加控制命令 ----master
	}else if(Msg->msgType == 99)   //请求设备数量
	{
		//unsigned short equiptNum = get_equiptNum();        //向数据池 获取设备数量 ----master
		//unsigned short equiptNum = 55;	
	}else if(Msg->msgType == 88)   //请求拓展备用字符
	{
		memset(MyStr, '\0', 20);
		strncpy(MyStr, Msg->mean, sizeof(MyStr));
	}else{
		return 0;
		Msg = NULL;
		free(Msg);
	}
	
	Msg = NULL;
	free(Msg);

	
	return ret;   //返回setBuf 的字节数
}