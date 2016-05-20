

#include<stdio.h>
#include<stdlib.h>
#include<string.h>


#include "kernel_list.h"
#include "Model.h"
#include "equip.h"
#include "signal.h"
#include "common.h"


   extern struct gDat_equip *gDat_head;
	//sDat_lst
   extern  struct sDat_equip *sDat_head;
	//sQue_lst
   extern  struct sQue_equip *sQue_head;
	//sCmd_lst
   extern  struct sCmd_equip *sCmd_head;

int main(int argc,char **argv)
{

	printf("into main\n");
	int i=0;		
	int mode = 1;
	if(argc > 1)
		mode = atoi(argv[1]);

//创建模型 及 模型数据
	make_data();
	
//查看模型设备链数据	
	struct gDat_equip *gDat_lst;
	struct sDat_equip *sDat_lst;
	struct sQue_equip *sQue_lst;
	struct sCmd_equip *sCmd_lst;
	gDat_lst = get_gDat_lst();  //get gDat_lst
	struct gDat_equip *p_equip;
	list_for_each_entry(p_equip,&gDat_lst->list,list)
	{
		printf("<Model>the equip ID=%d  ", p_equip->equipId);
		printf("type=%d  ", p_equip->type);
		printf("signalId=%d  ", p_equip->signalId);
		printf("para_data=%d\n", p_equip->para_data);
		//查看信号
		struct signal *p = get_signal_lst(0,p_equip->equipId);
		if(p!=NULL)
		{
			for(i=0;i<500;i++)
			{
				if(p[i].signalId==0) break;
				printf("<Model>-equip-signal:i=:%d   ", i);
				printf("signalID:%d  ", p[i].signalId);
				printf("name:%s  ", p[i].sig_name);
				printf("value:%f  ", p[i].value);
				printf("meaning:%s\n", p[i].sig_meaning);	
				printf("data:%s\n", p[i].data);
			}
		}		
	}
	
//创建数据模型的自动应答处理通信线程
	pthread_t net_thread;
	int pth1 = pthread_create(&net_thread, NULL, net_server, NULL);
	if(pth1 != 0)
	{
			perror("create net_thread");
			exit(-1);
	}



		
	while(1)
	{
		printf("SEEEEE  !!!!!!!!!!!!! \n");
		sleep(2);
		//再次查看数据模型数据
		struct signal *p3;
		struct gDat_equip *equipA;
		struct sDat_equip *equipB;
		struct sQue_equip *equipC;
		struct sCmd_equip *equipD;
		switch(mode)
		{
			case 0:
				equipA= get_Equiptment_gDatLst(1);
				if(equipA==NULL) continue;
				p3 = get_signal_lst(0,equipA->equipId);
				break;
			case 1:
				equipB= get_Equiptment_sDatLst(1);
				if(equipB==NULL) continue;
				p3 = get_signal_lst(1,equipB->equipId);				
				break;
			case 2:
				equipC = get_Equiptment_sQueLst(1);
				if(equipC==NULL) continue;
				p3 = get_signal_lst(2,equipC->equipId);				
				break;
			case 3:
				equipD= get_Equiptment_sCmdLst(1);
				if(equipD==NULL) continue;
				p3 = get_signal_lst(3,equipD->equipId);				
				break;
			default: break;
		}
		 

		if(p3==NULL)  continue;
		for(i=0;i<500;i++)
		{
				if(p3[i].signalId==0) break;
				printf("i=:%d   ", i);
				printf("signalID:%d  ", p3[i].signalId);
				printf("name:%s  ", p3[i].sig_name);
				printf("value:%f  ", p3[i].value);
				printf("meaning:%s\n", p3[i].sig_meaning);	
				printf("data:%s\n", p3[i].data);
		}
	
	}

		printf("end  !!!!!!!!!!!!! \n");


//等待网络服务的线程退出
	pthread_join(net_thread, NULL); 

	return 0;
}
