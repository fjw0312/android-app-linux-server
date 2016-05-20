#ifndef _EQUIP_H_
#define _EQUIP_H_
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
#include "kernel_list.h"

void Model_init(); ////����ģ�͵ĳ�ʼ��
void dele_gDat_nodeID(int id); ////���豸id ɾ���ڵ�
void dele_sDat_nodeID(int id);
void dele_sQue_nodeID(int id);
void dele_sCmd_nodeID(int id);

struct gDat_equip *get_Equiptment_gDatLst(int EquipID);   //��ȡ�豸�ڵ���Ϣ
struct sDat_equip *get_Equiptment_sDatLst(int EquipID);   
struct sQue_equip *get_Equiptment_sQueLst(int EquipID);
struct sCmd_equip *get_Equiptment_sCmdLst(int EquipID);

void add_gDat_node(int id,struct gDat_equip *Equipment);  //������ӽڵ�
void add_sDat_node(int id,struct sDat_equip *Equipment);
void add_sQue_node(int id,struct sQue_equip *Equipment);
void add_sCmd_node(int id,struct sCmd_equip *Equipment);

void clear_equip_lst(int flag);  //�������
struct gDat_equip *get_gDat_lst(); //��ȡ����
struct sDat_equip *get_sDat_lst();
struct sQue_equip *get_sQue_lst();
struct sCmd_equip *get_sCmd_lst();

#endif
