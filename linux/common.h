#ifndef _COMMON_H_
#define _COMMON_H_


#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "kernel_list.h"
#include "Model.h"
#include "equip.h"
#include "signal.h"

//make_data.c   file
void make_data();

//net_msg.c     file
char *fill_msg(int flag, int equipId);
void parse_msg(char *head, char *body);
void parse_msg_body(int flag,int equipID,int lenth, char *buf);


//net_server.c   file
void *net_server(void *arg);


#endif
