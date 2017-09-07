#ifndef __READINI_H
#define __READINI_H

#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#define IN
#define OUT


//�Զ���һ���˿���Ϣ�ṹ�� ����
struct portMessage
{
	int  portId;                 //�˿�id�� eg:1
	char portDiverLibName[100]; //�˿�������̬������·�� eg��"/data/mgrid/TestSampler/comm_std_serial.so"
	char portDiverName[50];     //�˿���������·��     eg: "/dev/ttyS3"
	char portPara[50];          //�˿��������ò���     eg: "9600,n,8,1"
	int  portAttr;              //�˿�����ģʽ         eg: 1  ����ģʽ
	int  portOutTime;           //�˿ڳ�ʱʱ��         eg: 20000  20s
	
//	char portRunEquiptLibName[100]; //�˿����е��豸��̬�����ƣ�֮���Ǽ��ݶ���豸��
//	int  portRunEquiptAddr;        //�˿����е��豸��ַ��֮���Ǽ��ݶ���豸��
}; 
//�Զ���һ���豸�ɼ�������Ϣ�ṹ��  ����
struct EquiptPortMessage
{ 
	int  equiptId;            //�豸id
	int  EquipTemplateId;     //�豸ģ��id
    int  portId;              //���ж˿�id
	char equiptLibName[100]; //�˿����е��豸��̬������
	int  equiptType;          //�豸������ 10:�ɼ��豸  20:�ϱ��豸
	int  equiptAddr;          //�豸��ַ
	int  equiptReRunTime;     //�豸�ɼ����� ms

//	void *porthandle;   //�˿ھ��  ��portId ->portMessage ->EquiptInit��ȡ��
	struct portMessage *pMes;   //�˿���Ϣ ��
	int  UpDataFlag;   //�ϱ��Ĺ��̱�־  0��δ�ϱ�״̬ 1���ϱ����ݸ�ֵ 2���ϱ�ͨ���� 3���ϱ�ͨ�Ž������������� 4���ϱ����ڽ���
	
};


//new һ���˿���Ϣ�ṹ���
struct portMessage *newPortMessage();
//new һ���豸������Ϣ�ṹ���
struct EquiptPortMessage *newEquiptPortMessage();
//���ļ� ��ȡ�ļ� ÿ���ַ�
int ReadFile_PortCfg(struct portMessage *pMess);
//���ļ� ��ȡ�ļ� ÿ���ַ�
int ReadFile_EquiptCfg(struct EquiptPortMessage *pMess);

void LogFile_OutPrintf(char *str, int lenth);                      //log ��־��ӡ����

#endif