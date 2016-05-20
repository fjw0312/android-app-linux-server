#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
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
	//����ģ�͵ĳ�ʼ��
void Model_init()
{
	//gDat_lst
	struct gDat_equip *gDat_lst= gDat_init();
	//sDat_lst
	struct sDat_equip *sDat_lst = sDat_init();
	//sQue_lst
	struct sQue_equip *sQue_lst = sQue_init();
	//sCmd_lst
	struct sCmd_equip *sCmd_lst = sCmd_init();
	
	gDat_head = gDat_lst;
	sDat_head = sDat_lst;
	sQue_head = sQue_lst;
	sCmd_head = sCmd_lst;
}

/*********************************************ɾ��һ���ڵ�********************/
//���豸id ɾ���ڵ�
void dele_gDat_nodeID(int id)
{
	struct list_head *pos;
	struct gDat_equip *p;
	list_for_each(pos,&gDat_head->list)
	{
		p = list_entry(pos,struct gDat_equip,list);	
		if(p->equipId==id)
		{				
			list_del(pos);				
			free(p);				
			break;
		}		
	}
		
}
void dele_sDat_nodeID(int id)
{
	struct list_head *pos;
	struct sDat_equip *p;
	list_for_each(pos,&sDat_head->list)
	{
		p = list_entry(pos,struct sDat_equip,list);		
		if(p->equipId==id)
		{
			list_del(pos);
			free(p);
			break;
		}
	}
}
void dele_sQue_nodeID(int id)
{
	struct list_head *pos;
	struct sQue_equip *p;
	list_for_each(pos,&sQue_head->list)
	{
		p = list_entry(pos,struct sQue_equip,list);	
		if(p->equipId==id)
		{
			list_del(pos);
			free(p);
			break;
		}
	}
}
void dele_sCmd_nodeID(int id)
{
	struct list_head *pos;
	struct sCmd_equip *p;
	list_for_each(pos,&sCmd_head->list)
	{
		p = list_entry(pos,struct sCmd_equip,list);	
		if(p->equipId==id)
		{
			list_del(pos);
			free(p);
			break;
		}

	}
}

/*********************************************��ȡ�豸�ڵ���Ϣ*********************/
struct gDat_equip *get_Equiptment_gDatLst(int EquipID)
{
	struct gDat_equip *p = NULL;
	//����
	list_for_each_entry(p,&gDat_head->list,list)
	{
		if(p->equipId==EquipID){
			return p;
		}
	}
	return NULL;
}
struct sDat_equip *get_Equiptment_sDatLst(int EquipID)
{
	struct sDat_equip *p = NULL;
	//����
	list_for_each_entry(p,&sDat_head->list,list)
	{
		if(p->equipId==EquipID){
			return p;
		}
	}
	return NULL;
}
struct sQue_equip *get_Equiptment_sQueLst(int EquipID)
{
	struct sQue_equip *p = NULL;
	//����
	list_for_each_entry(p,&sQue_head->list,list)
	{
		if(p->equipId==EquipID){
			return p;
		}
	}
	return NULL;
}
struct sCmd_equip *get_Equiptment_sCmdLst(int EquipID)
{
	struct sCmd_equip *p = NULL;
	//����
	list_for_each_entry(p,&sCmd_head->list,list)
	{
		if(p->equipId==EquipID){
			return p;
		}
	}
	return NULL;
}

/********************************************������ӽڵ�*********************/
void add_gDat_node(int id,struct gDat_equip *Equipment)
{
	//���ж��豸�ڵ��Ƿ��Ѿ�����
	if(get_Equiptment_gDatLst(id)==NULL){
	}else{
		dele_gDat_nodeID(id);
	}
	list_add_tail(&Equipment->list, &gDat_head->list);

}
void add_sDat_node(int id,struct sDat_equip *Equipment)
{
	//���ж��豸�ڵ��Ƿ��Ѿ�����
	if(get_Equiptment_sDatLst(id)==NULL)
	{			
	}else{
		dele_sDat_nodeID(id);
	}
	list_add_tail(&Equipment->list, &sDat_head->list);

}
void add_sQue_node(int id,struct sQue_equip *Equipment)
{
	//���ж��豸�ڵ��Ƿ��Ѿ�����
	if(get_Equiptment_sQueLst(id)==NULL)
	{
	}else{
		dele_sQue_nodeID(id);
	}
	list_add_tail(&Equipment->list, &sQue_head->list);	
}
void add_sCmd_node(int id,struct sCmd_equip *Equipment)
{
	//���ж��豸�ڵ��Ƿ��Ѿ�����
	if(get_Equiptment_sCmdLst(id)==NULL)
	{
	}else{
		dele_sCmd_nodeID(id);
	}
	list_add_tail(&Equipment->list, &sCmd_head->list);	

}


/******************************************�������*************************/
void clear_equip_lst(int flag)
{
	
	struct gDat_equip *p1;
	struct sDat_equip *p2;
	struct sQue_equip *p3;
	struct sCmd_equip *p4;
	switch(flag)
	{
		
		case 0:
			//���������ͷ���Դ
			
			list_for_each_entry(p1,&gDat_head->list,list)
			{
				dele_gDat_nodeID(p1->equipId);
			}
			break;
		case 1:
			
			list_for_each_entry(p2,&sDat_head->list,list)
			{
				dele_sDat_nodeID(p1->equipId);
			}
			break;
		case 2:
			
			list_for_each_entry(p3,&sQue_head->list,list)
			{
				dele_sQue_nodeID(p1->equipId);
			}
			break;
		case 3:
			
			list_for_each_entry(p4,&sCmd_head->list,list)
			{
				dele_sCmd_nodeID(p1->equipId);
			}
			break;
		default:
		     break;
	}
}
/***********************************************��ȡ����***************************/
struct gDat_equip *get_gDat_lst()
{
	return gDat_head;
}
struct sDat_equip *get_sDat_lst()
{
	return sDat_head;
}
struct sQue_equip *get_sQue_lst()
{
	return sQue_head;
}
struct sCmd_equip *get_sCmd_lst()
{
	return sCmd_head;
}
