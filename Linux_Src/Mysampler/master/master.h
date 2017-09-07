#ifndef __MASTER_H
#define __MASTER_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>
#include <pthread.h>



void *master(void *arg);
void getEquiptData(IN int EquiptId, OUT float* fData);
void setControlCmd(IN int EquiptId, IN char* strCmd);
void setWriteCmd(IN int EquiptId, IN char* strCmd);

void get_equiptIdBuf(OUT int *idBuf); //获取  采集器  采集设备id 数组
int get_equiptNum();//获取 采集器的 采集设备个数


#endif