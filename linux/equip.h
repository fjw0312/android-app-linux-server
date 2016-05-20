#ifndef _EQUIP_H_
#define _EQUIP_H_
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
#include "kernel_list.h"

void Model_init(); ////数据模型的初始化
void dele_gDat_nodeID(int id); ////按设备id 删除节点
void dele_sDat_nodeID(int id);
void dele_sQue_nodeID(int id);
void dele_sCmd_nodeID(int id);

struct gDat_equip *get_Equiptment_gDatLst(int EquipID);   //获取设备节点信息
struct sDat_equip *get_Equiptment_sDatLst(int EquipID);   
struct sQue_equip *get_Equiptment_sQueLst(int EquipID);
struct sCmd_equip *get_Equiptment_sCmdLst(int EquipID);

void add_gDat_node(int id,struct gDat_equip *Equipment);  //链表添加节点
void add_sDat_node(int id,struct sDat_equip *Equipment);
void add_sQue_node(int id,struct sQue_equip *Equipment);
void add_sCmd_node(int id,struct sCmd_equip *Equipment);

void clear_equip_lst(int flag);  //清除链表
struct gDat_equip *get_gDat_lst(); //获取链表
struct sDat_equip *get_sDat_lst();
struct sQue_equip *get_sQue_lst();
struct sCmd_equip *get_sCmd_lst();

#endif
