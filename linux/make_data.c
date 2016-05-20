
#include<stdio.h>
#include<stdlib.h>
#include<string.h>


#include "kernel_list.h"
#include "Model.h"
#include "equip.h"
#include "signal.h"


	//gDat_lst
    struct gDat_equip *gDat_head;
	//sDat_lst
	struct sDat_equip *sDat_head;
	//sQue_lst
	struct sQue_equip *sQue_head;
	//sCmd_lst
	struct sCmd_equip *sCmd_head;


void make_data()   //make 3 equip
{
	printf("into make_data\n");
	int i=0,j=0;
	

	Model_init();
	//建立gDat链 设备节点
	for(i=1;i<3;i++)  //2 个设备
	{
		struct signal *sig = signal_Newnode(0,"name",1, "fjw_test");
		struct signal sig3 = *sig;
		struct gDat_equip *new_equip  = gDat_Newnode(i, 100+i, 0, 88, sig3);
		add_gDat_node(new_equip->equipId,new_equip);   //add equip node
		for(j=1;j<11;j++)  //10条信号
		{		
			struct signal *new_signal = signal_Newnode(j,"name",i*10+2*j, "fjw_test");
			add_signal_node(0,i,j,new_signal);    //add signal node
		}
	}
/*	//建立sDat链 设备节点
	for(i=1;i<2;i++)
	{
		struct signal *sig = signal_Newnode(0,"name",1, "fjw_test");
		struct signal sig3 = *sig;
		struct sDat_equip *new_equip  = sDat_Newnode(i, 100+i, 0, 88, sig3);
		add_sDat_node(new_equip->equipId,new_equip);   //add equip node
		for(j=1;j<3;j++)
		{		
			struct signal *new_signal = signal_Newnode(j,"name",i*2000+2*j, "fjw_test");
			add_signal_node(1,i,j,new_signal);    //add signal node
		}
	}
	//建立sQue链 设备节点
	for(i=1;i<2;i++)
	{
		struct signal *sig = signal_Newnode(0,"name",1, "fjw_test");
		struct signal sig3 = *sig;
		struct sQue_equip *new_equip  = sQue_Newnode(i, 100+i, 0, 88, sig3);
		add_sQue_node(new_equip->equipId,new_equip);   //add equip node
		for(j=1;j<3;j++)
		{		
			struct signal *new_signal = signal_Newnode(j,"name",i*3000+2*j, "fjw_test");
			add_signal_node(2,i,j,new_signal);    //add signal node
		}
	}
	//建立sCmd链 设备节点
	for(i=1;i<2;i++)
	{
		struct signal *sig = signal_Newnode(0,"name",1, "fjw_test");
		struct signal sig3 = *sig;
		struct sCmd_equip *new_equip  = sCmd_Newnode(i, 100+i, 0, 88, sig3);
		add_sCmd_node(new_equip->equipId,new_equip);   //add equip node
		for(j=1;j<3;j++)
		{		
			struct signal *new_signal = signal_Newnode(j,"name",i*4000+2*j, "fjw_test");
			add_signal_node(3,i,j,new_signal);    //add signal node
		}
	}
*/	
}
