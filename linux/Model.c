#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
#include "kernel_list.h"


//新建一个节点 信号结构体节点
struct signal  *signal_Newnode(int id, char *name, float value,char *meaning)
{
	struct signal *new_node = malloc(sizeof(struct signal));
	new_node->signalId = id;
	strcpy(new_node->sig_name,name);
	new_node->value = value;
	strcpy(new_node->sig_meaning,meaning);
	//赋值 辅助变量

	sprintf((char *)new_node->data, "%d`%s`%f`%s#\n", id,name,value,meaning);
//	printf("signal_Newnode->data=%s\n", new_node->data);
	
	return new_node;
}
void fill_equip_buf(int flag, int id, int type, int set_sigId, int paras, unsigned char *buf)
{
//	sprintf(buf, "14096gDat%04d%04d%04d%04d#", id,type,set_sigId,paras);
	buf[0] = 0x01;
	int i=0;
	for(i=0;i<4;i++)
	{
		buf[4-i] = (unsigned char)((102400>>(8*i))&0xFF);
	}
	for(i=0;i<4;i++)
	{
		buf[8-i] = (unsigned char)((flag>>(8*i))&0xFF);
	}
	for(i=0;i<4;i++)
	{
		buf[12-i] = (unsigned char)((id>>(8*i))&0xFF);
	}
	for(i=0;i<4;i++)
	{
		buf[16-i] = (unsigned char)((type>>(8*i))&0xFF);
	}
		for(i=0;i<4;i++)
	{
		buf[20-i] = (unsigned char)((set_sigId>>(8*i))&0xFF);
	}
	for(i=0;i<4;i++)
	{
		buf[24-i] = (unsigned char)((paras>>(8*i))&0xFF);
	}
	
}

//初始化  gDat结构体
struct gDat_equip *gDat_init(void)
{
	struct gDat_equip *head = malloc(sizeof(struct gDat_equip));
	INIT_LIST_HEAD(&head->list);
	return head;	
}
//新建一个节点 
struct gDat_equip *gDat_Newnode(int id, int type, int set_sigId, int paras, struct signal sig)
{
	struct gDat_equip *new_node = malloc(sizeof(struct gDat_equip));
	new_node->flag = 0;
	new_node->equipId = id;
	new_node->type = type;
	new_node->signalId = set_sigId;
	new_node->para_data = paras;
	new_node->sig[0] = sig;
	//赋值 辅助变量
//	sprintf((char *)new_node->buf, "140960000%04d%04d%04d%04d#", id,type,set_sigId,paras);
	fill_equip_buf(0,id,type,set_sigId,paras,new_node->buf);
	INIT_LIST_HEAD(&new_node->list);
	return new_node;	
}
//初始化  sDat结构体
struct sDat_equip *sDat_init(void)
{
	struct sDat_equip *head = malloc(sizeof(struct sDat_equip));
	INIT_LIST_HEAD(&head->list);
	return head;	
}
//新建一个节点 
struct sDat_equip *sDat_Newnode(int id, int type, int set_sigId, int paras, struct signal sig)
{
	struct sDat_equip *new_node = malloc(sizeof(struct sDat_equip));
	new_node->flag = 1;
	new_node->equipId = id;
	new_node->type = type;
	new_node->signalId = set_sigId;
	new_node->para_data = paras;
	new_node->sig[0] = sig;
	//赋值 辅助变量
//	sprintf((char *)new_node->buf, "140960001%04d%04d%04d%04d#", id,type,set_sigId,paras);
	fill_equip_buf(1,id,type,set_sigId,paras,new_node->buf);
	INIT_LIST_HEAD(&new_node->list);
	return new_node;	
}
//初始化  sQue结构体
struct sQue_equip *sQue_init(void)
{
	struct sQue_equip *head = malloc(sizeof(struct sQue_equip));
	INIT_LIST_HEAD(&head->list);
	return head;	
}
//新建一个节点 
struct sQue_equip *sQue_Newnode(int id, int type, int set_sigId, int paras, struct signal sig)
{
	struct sQue_equip *new_node = malloc(sizeof(struct sQue_equip));
	new_node->flag = 2;
	new_node->equipId = id;
	new_node->type = type;
	new_node->signalId = set_sigId;
	new_node->para_data = paras;
	new_node->sig[0] = sig;
	//赋值 辅助变量
//	sprintf((char *)new_node->buf, "140960002%04d%04d%04d%04d#", id,type,set_sigId,paras);
	fill_equip_buf(2,id,type,set_sigId,paras,new_node->buf);
	INIT_LIST_HEAD(&new_node->list);
	return new_node;	
}
//初始化  sCmd结构体
struct sCmd_equip *sCmd_init(void)
{
	struct sCmd_equip *head = malloc(sizeof(struct sCmd_equip));
	INIT_LIST_HEAD(&head->list);
	return head;	
}
//新建一个节点 
struct sCmd_equip *sCmd_Newnode(int id, int type, int set_sigId, int paras, struct signal sig)
{
	struct sCmd_equip *new_node = malloc(sizeof(struct sCmd_equip));
	new_node->flag = 3;
	new_node->equipId = id;
	new_node->type = type;
	new_node->signalId = set_sigId;
	new_node->para_data = paras;
	new_node->sig[0] = sig;
	//赋值 辅助变量
//	sprintf((char *)new_node->buf, "140960003%04d%04d%04d%04d#", id,type,set_sigId,paras);
	fill_equip_buf(3,id,type,set_sigId,paras,new_node->buf);
	INIT_LIST_HEAD(&new_node->list);
	return new_node;	
}
