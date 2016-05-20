#ifndef _SIGNAL_H_
#define _SIGNAL_H_

#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
#include "kernel_list.h"
#include "equip.h"


void dele_sig_nodeID(int flag, int equipID, int sigID); //ɾ���źŽڵ� ����ĳ���豸id ĳ���ź�id

struct signal get_signal_node(int flag, int equipId,int sigId); //��ȡ�豸�ڵ� �е��źŽڵ�

void add_signal_node(int flag, int equipId,int sigId,struct signal *sig); //����豸�ڵ� �е��źŽڵ�

void clear_signal_lst(int flag, int equipId);   //���ĳ�豸id�ϵ� signal ����

struct signal *get_signal_lst(int flag, int equipId);  //��ȡĳ���豸�ϵ� signal ����

#endif
