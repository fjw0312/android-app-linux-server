#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
#include "equip.h"
#include "kernel_list.h"


//����ģ�͵Ľ��� �ļ�
//����4������
	//gDat_lst
    struct gDat_equip *gDat_head;
	//sDat_lst
	struct sDat_equip *sDat_head;
	//sQue_lst
	struct sQue_equip *sQue_head;
	//sCmd_lst
	struct sCmd_equip *sCmd_head;
/****************************ɾ���źŽڵ� ����ĳ���豸id ĳ���ź�id **********/
void dele_sig_nodeID(int flag, int equipID, int sigID)
{
	int i = 0;
	if(flag==0)
	{
		struct gDat_equip  *Equipment = get_Equiptment_gDatLst(equipID);
		if(Equipment==NULL) return;
		struct list_head *pos;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigID)
			{
				signals[i].signalId = 0;
				strcpy(signals[i].sig_name,"");
				signals[i].value = 0;
				strcpy(signals[i].sig_meaning,"");
				strcpy(signals[i].data,"");
			}
		}
	}else if(flag==1)
	{
		struct sDat_equip  *Equipment = get_Equiptment_sDatLst(equipID);
		if(Equipment==NULL) return;
		struct list_head *pos;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigID)
			{
				signals[i].signalId = 0;
				strcpy(signals[i].sig_name,"");
				signals[i].value = 0;
				strcpy(signals[i].sig_meaning,"");
				strcpy(signals[i].data,"");
			}
		}
	}else if(flag==2)
	{
		struct sQue_equip  *Equipment = get_Equiptment_sQueLst(equipID);
		if(Equipment==NULL) return;
		struct list_head *pos;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigID)
			{
				signals[i].signalId = 0;
				strcpy(signals[i].sig_name,"");
				signals[i].value = 0;
				strcpy(signals[i].sig_meaning,"");
				strcpy(signals[i].data,"");
			}
		}
	}else if(flag==3)
	{
		struct sCmd_equip  *Equipment = get_Equiptment_sCmdLst(equipID);
		if(Equipment==NULL) return;
		struct list_head *pos;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigID)
			{
				signals[i].signalId = 0;
				strcpy(signals[i].sig_name,"");
				signals[i].value = 0;
				strcpy(signals[i].sig_meaning,"");
				strcpy(signals[i].data,"");
			}
		}
	}
}
/*****************************************��ȡ�豸�ڵ� �е��źŽڵ�*************************/
struct signal get_signal_node(int flag, int equipId,int sigId)
{
	int i=0;
	if(flag==0)
	{
		struct gDat_equip  *Equipment = get_Equiptment_gDatLst(equipId);
		if(Equipment==NULL) return *signal_Newnode(0,"",0, "");
		struct signal *signals = Equipment->sig;
		//����
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigId)
			{
				return signals[i];
			}
		}
	}else if(flag==1)
	{
		struct sDat_equip  *Equipment = get_Equiptment_sDatLst(equipId);
		if(Equipment==NULL) return *signal_Newnode(0,"",0, "");
		struct signal *signals = Equipment->sig;
		//����
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigId)
			{
				return signals[i];
			}
		}
	}else if(flag==2)
	{
		struct sQue_equip  *Equipment = get_Equiptment_sQueLst(equipId);
		if(Equipment==NULL) return *signal_Newnode(0,"",0, "");
		struct signal *signals = Equipment->sig;
		//����
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigId)
			{
				return signals[i];
			}
		}
	}else if(flag==3)
	{
		struct sCmd_equip  *Equipment = get_Equiptment_sCmdLst(equipId);
		if(Equipment==NULL) return *signal_Newnode(0,"",0, "");
		struct signal *signals = Equipment->sig;
		//����
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == sigId)
			{
				return signals[i];
			}
		}
	}

	return *signal_Newnode(0,"",0, "");
}
/*****************************************����豸�ڵ� �е��źŽڵ�***********************/
void add_signal_node(int flag, int equipId,int sigId,struct signal *sig)
{
	int i=0;
	if(flag==0)
	{
		struct gDat_equip *Equipment = get_Equiptment_gDatLst(equipId);
		//���ж��豸�ڵ��Ƿ��Ѿ�����
		if(Equipment==NULL)
		{
	//		Equipment = equip_Newnode(equipId,100,0,0,sig);
			return;
		}
		//���ж��źŽڵ��Ƿ��Ѿ�����
		struct signal sig_p = get_signal_node(flag,equipId,sigId);
		if(sig_p.signalId==0)
		{	

		}else{
			dele_sig_nodeID(flag,equipId,sigId);		
		}
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == 0)
			{
				signals[i].signalId = sig->signalId;
				strcpy(signals[i].sig_name,sig->sig_name);
				signals[i].value = sig->value;
				strcpy(signals[i].sig_meaning,sig->sig_meaning);
				strcpy(signals[i].data,sig->data);
				break;
			}
		}
	}else if(flag==1)
	{
		struct sDat_equip *Equipment = get_Equiptment_sDatLst(equipId);
		//���ж��豸�ڵ��Ƿ��Ѿ�����
		if(Equipment==NULL)
		{
	//		Equipment = equip_Newnode(equipId,100,0,0,sig);
			return;
		}
		//���ж��źŽڵ��Ƿ��Ѿ�����
		struct signal sig_p = get_signal_node(flag,equipId,sigId);
		if(sig_p.signalId==0)
		{			
		}else{
			dele_sig_nodeID(flag,equipId,sigId);	
		}
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == 0)
			{
				signals[i].signalId = sig->signalId;
				strcpy(signals[i].sig_name,sig->sig_name);
				signals[i].value = sig->value;
				strcpy(signals[i].sig_meaning,sig->sig_meaning);
				strcpy(signals[i].data,sig->data);
				break;
			}
		}
	}else if(flag==2)
	{
		struct sQue_equip *Equipment = get_Equiptment_sQueLst(equipId);
		//���ж��豸�ڵ��Ƿ��Ѿ�����
		if(Equipment==NULL)
		{
	//		Equipment = equip_Newnode(equipId,100,0,0,sig);
			return;
		}
		//���ж��źŽڵ��Ƿ��Ѿ�����
		struct signal sig_p = get_signal_node(flag,equipId,sigId);
		if(sig_p.signalId==0)
		{			
		}else{
			dele_sig_nodeID(flag,equipId,sigId);	
		}
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == 0)
			{
				signals[i].signalId = sig->signalId;
				strcpy(signals[i].sig_name,sig->sig_name);
				signals[i].value = sig->value;
				strcpy(signals[i].sig_meaning,sig->sig_meaning);
				strcpy(signals[i].data,sig->data);
				break;
			}
		}
	}else if(flag==3)
	{
		struct sCmd_equip *Equipment = get_Equiptment_sCmdLst(equipId);
		//���ж��豸�ڵ��Ƿ��Ѿ�����
		if(Equipment==NULL)
		{
	//		Equipment = equip_Newnode(equipId,100,0,0,sig);
			return;
		}
		//���ж��źŽڵ��Ƿ��Ѿ�����
		struct signal sig_p = get_signal_node(flag,equipId,sigId);
		if(sig_p.signalId==0)
		{			
		}else{
			dele_sig_nodeID(flag,equipId,sigId);	
		}
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			if(signals[i].signalId == 0)
			{
				signals[i].signalId = sig->signalId;
				strcpy(signals[i].sig_name,sig->sig_name);
				signals[i].value = sig->value;
				strcpy(signals[i].sig_meaning,sig->sig_meaning);
				strcpy(signals[i].data,sig->data);
				break;
			}
		}
	}
}




/********************************************���ĳ�豸id�ϵ� signal ����************/
void clear_signal_lst(int flag, int equipId)
{
	int i=0;
	if(flag==0)
	{
		struct gDat_equip *Equipment = get_Equiptment_gDatLst(equipId);
		if(Equipment==NULL) return;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			dele_sig_nodeID(flag,equipId,signals[i].signalId);
		}
	}else if(flag==1)
	{
		struct sDat_equip *Equipment = get_Equiptment_sDatLst(equipId);
		if(Equipment==NULL) return;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			dele_sig_nodeID(flag,equipId,signals[i].signalId);
		}
	}else if(flag==2)
	{
		struct sQue_equip *Equipment = get_Equiptment_sQueLst(equipId);
		if(Equipment==NULL) return;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			dele_sig_nodeID(flag,equipId,signals[i].signalId);
		}
	}else if(flag==3)
	{
		struct sCmd_equip *Equipment = get_Equiptment_sCmdLst(equipId);
		if(Equipment==NULL) return;
		struct signal *signals = Equipment->sig;
		for(i=0;i<500;i++)
		{
			dele_sig_nodeID(flag,equipId,signals[i].signalId);
		}
	}
}
/********************************************��ȡĳ���豸�ϵ� signal ����************/
struct signal *get_signal_lst(int flag, int equipId)
{
	if(flag==0)
	{
		struct gDat_equip *Equipment = get_Equiptment_gDatLst(equipId);
		if(Equipment==NULL) return;
		return Equipment->sig;
	}else if(flag==1)
	{
		struct sDat_equip *Equipment = get_Equiptment_sDatLst(equipId);
		if(Equipment==NULL) return;
		return Equipment->sig;
	}else if(flag==2)
	{
		struct sQue_equip *Equipment = get_Equiptment_sQueLst(equipId);
		if(Equipment==NULL) return;
		return Equipment->sig;
	}else if(flag==3)
	{
		struct sCmd_equip *Equipment = get_Equiptment_sCmdLst(equipId);
		if(Equipment==NULL) return;
		return Equipment->sig;
	}
}
