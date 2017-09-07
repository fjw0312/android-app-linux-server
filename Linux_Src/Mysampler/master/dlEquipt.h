#ifndef __DLEQUIPT_H
#define __DLEQUIPT_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#define IN
#define OUT
//�豸��̬�� ����APi �ṹ��ڵ�
struct EquiptApiNode{
	char libName[100];     //�豸��̬������
	
	int (*DriverStop)(void *devstruct, void *PortHandle);     //�������˿� API
	int (*DriverInit)(void *devstruct, void *PortHandleAddr); //�ر������˿� API
	int (*Read)(void *hComm, int nUnitNo, void* pvData);       //��ȡ�����˿����� API
	int (*Control)(void *hComm,int nUnitNo, char *pCmdStr);    //���������˿����� API

	int (*Report)(void *hComm,  float *fdata,  char* sRecStr);                  //�ϱ�ͨ��  API
	int (*Write)(void *hComm, char* pCmdStr, int nStrLen);                      //��������� ֱ�ӷ��� API
};

struct EquiptApiNode *NewEquiptApiNode(char *libPathName);   //new һ���豸��̬�� ����APi �ṹ��ڵ�
bool DL_open(IN char *libName, OUT void **libHandleAddr);                //�� ���ض�̬��
bool DL_close(IN void *libHandle);                                      //�ر� ���ض�̬��
bool DL_sym_AllApi(IN void *libHandle, OUT struct EquiptApiNode *ApiNode); //���� ���ض�̬�� ����

#endif