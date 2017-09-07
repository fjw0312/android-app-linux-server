#ifndef __SAMPLEREQUIPT_H
#define __SAMPLEREQUIPT_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#include "readIni.h"

#define IN
#define OUT

//���� ���˿����� 20 ���ɼ��豸���� 20  ����ź���800
#define  MaxPortNum    20
#define  MaxEquiptNum  20
#define  MaxSignalNum  800

//�������� ��ʼ��־
#define CONTROL_STAR   '&'
#define WRITE_STAR     '#'
#define NOSEND         '$'

//�Զ���һ����Ϣ�ṹ�����
struct message
{
	struct EquiptPortMessage *EquiptCfgMess;  //�豸�ɼ�������Ϣ
	bool libEquipt_Loaded_Flag;  //��̬���Ѽ��� ��־
	bool libPort_Open_Flag;      //�˿��Ѵ� ��־

	float *readData_A;   //����ģʽ read    �Ķ�ȡ����
	char *sCmdStr_A;    //����ģʽ control �ķ�������	
	
//	char *recStr_B;     //�ϱ�ģʽ  RecvStr      ��ȡ��������
//	float *sendData_B;  //�ϱ�ģʽ  sendData  Ӧ����������
//	int recStr_B_EquiptId;   //�ϱ�ģʽ  RecvStr      ��ȡ���������豸��id
//	int sendData_B_EquiptId; //�ϱ�ģʽ  sendData     Ӧ�����������豸��id
};

struct message *newSamplerMess(struct EquiptPortMessage *EquiptCfgMess, float *Data, char *Str);
void *sampler(void *arg);

#endif