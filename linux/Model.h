#ifndef _MODEL_H_
#define _MODEL_H_
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "kernel_list.h"

//信号类 结构体
struct signal{    //该信号固定最大字节为 400byte
	int signalId;
	char sig_name[99];
	float value;
	char sig_meaning[99];
	

	//辅助变量
	char data[200];
};

//设备类 gDat_equip结构体  flag = 0
struct gDat_equip{
	int flag;
	
	int equipId;
	int type;
	int signalId;
	int para_data;
	
	struct signal sig[500];  //暂时 设定每个设备最大有500条信号 
	
	char e_name[100];    //目前暂不使用
	char e_meaning[100]; //目前暂不使用

	//信号类的内核链表
	struct list_head list;
	//辅助变量	
	char buf[25];
};
//设备类 sDat_equip结构体  flag = 1
struct sDat_equip{
	int flag;
	
	int equipId;
	int type;
	int signalId;
	int para_data;
	
	struct signal sig[500];  //暂时 设定每个设备最大有500条信号
	
	char e_name[200];    //目前暂不使用
	char e_meaning[200]; //目前暂不使用
	
	//信号类的内核链表
	struct list_head list;
	//辅助变量	
	unsigned char buf[25];
};
//设备类 sQue_equip结构体  flag = 2
struct sQue_equip{
	int flag;
	
	int equipId;
	int type;
	int signalId;
	int para_data;
	
	struct signal sig[500];  //暂时 设定每个设备最大有500条信号
	
	char e_name[200];    //目前暂不使用
	char e_meaning[200]; //目前暂不使用

	//信号类的内核链表
	struct list_head list;	
	//辅助变量	
	unsigned char buf[25];
};
//设备类 sCmd_equip结构体  flag = 3
struct sCmd_equip{
	int flag;
	
	int equipId;
	int type;
	int signalId;
	int para_data;
	
	struct signal sig[500];  //暂时 设定每个设备最大有500条信号 
	
	char e_name[200];    //目前暂不使用
	char e_meaning[200]; //目前暂不使用

	//信号类的内核链表
	struct list_head list;	
	//辅助变量	
	unsigned char buf[25];
};

//msg_head 消息包头结构体
struct msg_head{
	char start;
	int lenth;
	unsigned char flag;
	
	int equipId;
	int type;
	int signalId;
	int para;
};

struct signal  *signal_init(void);
struct signal  *signal_Newnode(int id, char *name, float value,char *meaning);
struct gDat_equip *gDat_init(void);
struct gDat_equip *gDat_Newnode(int id, int type, int set_sigId, int paras, struct signal sig);
struct sDat_equip *sDat_init(void);
struct sDat_equip *sDat_Newnode(int id, int type, int set_sigId, int paras, struct signal sig);
struct sQue_equip *sQue_init(void);
struct sQue_equip *sQue_Newnode(int id, int type, int set_sigId, int paras, struct signal sig);
struct sCmd_equip *sCmd_init(void);
struct sCmd_equip *sCmd_Newnode(int id, int type, int set_sigId, int paras, struct signal sig);


#endif
