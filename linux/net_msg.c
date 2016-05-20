#include<stdio.h>
#include<stdlib.h>
#include<string.h>


#include "Model.h"
#include "equip.h"
#include "signal.h"
#include "common.h"


char msg_head[25];
char msg_body[100*1024];
char msg[100*1024+25];

char *fill_msg(int flag, int equipId)
{
	
	char *msg_head_buf;
	struct signal *sig;
	memset(msg_head,0, 25);
	memset(msg_body,0, 100*1024);

	if(flag==0)
	{
		struct gDat_equip *equip = get_Equiptment_gDatLst(equipId);
		if(equip==NULL) return NULL;
		msg_head_buf = equip->buf;
		sig = equip->sig;
	}else if(flag==1)
	{
		
		struct sDat_equip *equip = get_Equiptment_sDatLst(equipId);
		if(equip==NULL) return NULL;		
		msg_head_buf = equip->buf;
		sig = equip->sig;
	}else if(flag==2)
	{
		struct sQue_equip *equip = get_Equiptment_sQueLst(equipId);
		if(equip==NULL) return NULL;
		msg_head_buf = equip->buf;
		sig = equip->sig;
	}else if(flag==3)
	{
		struct sCmd_equip *equip = get_Equiptment_sCmdLst(equipId);
		if(equip==NULL) return NULL;
		msg_head_buf = equip->buf;
		sig = equip->sig;
	}

	int i = 0,j=0;
//	printf("net_msg->fill_msg>>msg_head_buf[8]=%d\n", msg_head_buf[8]);
//	printf("net_msg->fill_msg:");
	for(i=0;i<25;i++)
	{
		msg_head[i] = msg_head_buf[i];	
		msg[i] = msg_head_buf[i];
//		printf("%d", msg_head[i]);
	}
//	printf("\n");

	for(i=0;i<500;i++)
	{
//		for(j=0;j<200;j++)
//		{
//			printf("i=%d   sig[%d].data=%s\n",i, i, sig[i].data);  //打印结果正常
			strncpy((msg_body+i*200), sig[i].data, 200);
			strncpy((msg+i*200+25), sig[i].data, 200);
//			printf("i=%d   msg_body+%d *200=%s\n",i, i, (msg_body+i*200));  //打印结果正常
//			msg_body[i*500+j] = sig[i].data[j];	
//		}
	}

//	strncpy((msg+25), msg_body, 1024*100);  //此函数会 copy到空时后面全填0000 所以会有问题
	
	return msg;
}
void parse_msg(char *head, char *body)
{
	if(head==NULL) return;


	int lenth =0;
	int flag = 0;
	int equipId = 0;
	int type = 0;
	int signalID =0;
	int paras =0;
	int i=0;
	for(i=0;i<4;i++)
		lenth |= (int)(head[4-i]&0xff)<<(8*i);
	for(i=0;i<4;i++)
		flag |= (int)(head[8-i]&0xff)<<(8*i);
	for(i=0;i<4;i++)
		equipId |= (int)(head[12-i]&0xff)<<(8*i);
	for(i=0;i<4;i++)
		type |= (int)(head[16-i]&0xff)<<(8*i);
	for(i=0;i<4;i++)
		signalID |= (int)(head[20-i]&0xff)<<(8*i);
	for(i=0;i<4;i++)
		paras |= (int)(head[24-i]&0xff)<<(8*i);

	struct signal *sig = signal_Newnode(1,"name",1, "fjw_test");
	if(flag==0)
	{
		struct gDat_equip *new_equip  = gDat_Newnode(equipId, type, signalID, paras, *sig);
		add_gDat_node(new_equip->equipId,new_equip);   //add equip node
	}else if(flag==1)
	{
		struct sDat_equip *new_equip  = sDat_Newnode(equipId, type, signalID, paras, *sig);
		add_sDat_node(new_equip->equipId,new_equip);   //add equip node
	}else if(flag==2)
	{
		struct sQue_equip *new_equip  = sQue_Newnode(equipId, type, signalID, paras, *sig);
		add_sQue_node(new_equip->equipId,new_equip);   //add equip node
	}else if(flag==3)
	{		
		struct sCmd_equip *new_equip  = sCmd_Newnode(equipId, type, signalID, paras, *sig);
		add_sCmd_node(new_equip->equipId,new_equip);   //add equip node
	}
	 
	printf("parse_msg->equipId=%d type=%d signalID=%d paras=%d flag=%d\n", equipId,type,signalID,paras,flag);//测试正确
	parse_msg_body(flag, equipId,lenth, body);
}

void parse_msg_body(int flag,int equipID, int lenth, char *buf)
{
	if(buf==NULL) return;
	if(lenth==0) return;
	if(strlen(buf)==0) return;
	int i=0,j=0;
	char bs[200];
	for(i=0;i<(lenth/200);i++)
	{
		memset(bs,0, 200);
		strncpy(bs, buf+200*i, 200);
//		printf("parse_msg_body--->bs=%s\n", bs); //测试正确
		if(strlen(bs)>1)
		{
			char *tok = "`";
			int sigId = atoi(strtok(bs,tok));		
			char *name = strtok(NULL,tok);
			float value = atof(strtok(NULL,tok));
			char *end = strtok(NULL,tok);
			char *meaning = strtok(end,"#");			
			struct signal *sig_bs= signal_Newnode(sigId,name,value,meaning); //test we can value*4 see lst
			printf("parse_msg_body->sigId=%d name=%s value=%f meaning=%s\n", sigId,name,value,meaning);//测试正确
			add_signal_node(flag,equipID,sigId,sig_bs);    //add signal node
		}

	}
	
}









