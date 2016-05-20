#ifndef _SIGNAL_H_
#define _SIGNAL_H_

#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "Model.h"
#include "kernel_list.h"
#include "equip.h"


void dele_sig_nodeID(int flag, int equipID, int sigID); //删除信号节点 根据某个设备id 某个信号id

struct signal get_signal_node(int flag, int equipId,int sigId); //获取设备节点 中的信号节点

void add_signal_node(int flag, int equipId,int sigId,struct signal *sig); //添加设备节点 中的信号节点

void clear_signal_lst(int flag, int equipId);   //清除某设备id上的 signal 链表

struct signal *get_signal_lst(int flag, int equipId);  //获取某个设备上的 signal 链表

#endif
