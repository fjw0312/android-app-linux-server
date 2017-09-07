#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<unistd.h>
#include <pthread.h>

#include "dlEquipt.h"
#include "samplerEquipt.h"
#include "readIni.h"
#include "master.h"

//------------------------------------------------------------
//---        �ļ�˵�������ļ�Ϊ�ɼ���������ƹ����ļ�
//--- ���ܣ�����ģ���̳߳� ҵ��ɼ�����  ���ݶ���ӿ�
//--- ���ߣ�������   2017.01.20    Email��fjw0312@163.com
//------------------------------------------------------------
#define IN
#define OUT

//�ϱ���̬������ "libUpDataEquiptX.so"
#define UpDataEquiptLibName "libUpDataEquipt"

int portNum = 0;    //ʵ�ʶ˿� ����
int equiptNum = 0;  //ʵ���豸 ����


// ʵʱ�����շ� buf ���ݳ�RdataBuf[�豸id]  �豸id = 1.2.3��������
float RdataBuf[MaxEquiptNum][MaxSignalNum];      //ʵʱ����read ����                   buf[0]=�豸id   buf[1]=ͨ��״̬1/0 
char  ScmdBuf[MaxEquiptNum][MaxSignalNum];       //ʵʱ����control(send) ���� ָ������ buf[0]=�豸id   buf[0]=��������CONTROL_STAR/WRITE_STAR

//ʵʱ �����ϱ� buf  ���ݳ�
char   RcmdBuff[MaxSignalNum];
float  SdataBuff[MaxSignalNum];

//ʵʱ �ɼ��豸id����
int  EquiptIdBuf[MaxEquiptNum];


//�����̻߳��������� pthread_mutex_t  mt;
pthread_mutex_t  Rdata_mutex[MaxEquiptNum];    //���ݲɼ� read �߳���
pthread_mutex_t  Scmd_mutex[MaxEquiptNum];    //��������control/write �߳���
pthread_mutex_t  Report_mutex;  //�ϱ�ͨ��Report �߳���

/*****************************************************************/
// �������ƣ�master ��Ҫ����ִ�к�������--->samplerEquipt->dlEquipt->libEquipt.so
// �����������׶˲ɼ��� �ɼ���ں���
// ���������
// ���������
// ��    �أ�    
// ��    ����
/*****************************************************************/
void *master(void *arg)
{
	char spriBuf[200];

	//1. ��ȡ�����ļ�
	//����һ���˿���Ϣ�� һ���豸������Ϣ�� ����
	struct portMessage portMessS[MaxPortNum];  
	struct EquiptPortMessage equiptCfgMessS[MaxEquiptNum];
	
	portNum = ReadFile_PortCfg(portMessS);           //��ȡ��ʼ�����ļ� �˿�����		
	equiptNum = ReadFile_EquiptCfg(equiptCfgMessS);  //��ȡ��ʼ�����ļ� �˿�����
	if(equiptNum <= 0) pthread_exit(0); //return NULL;	
	int i=0,j=0;
	for(i=0;i<equiptNum;i++) //��ֵ �豸�������еĶ�Ӧ �豸�˿���Ϣ��
	{
		equiptCfgMessS[i].UpDataFlag = 0; //��ʼ�� �ϱ����̱�־
		int e_portId = equiptCfgMessS[i].portId;
		EquiptIdBuf[i] = e_portId;
		for(j=0; j<portNum; j++)
		{
			if(portMessS[j].portId == e_portId)
			{
				equiptCfgMessS[i].pMes = &portMessS[j];  //��ȡ �˿�������
				break;
			}
		}
	}
	sprintf(spriBuf,"========================�����ļ�������ϣ�========================\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����

	
	//2������ʼ�� ����������
	//��ʼ�� buf ���ݳ�
	int k = 0;
	for(k=0; k<MaxEquiptNum; k++){
		memset(RdataBuf[k], '\0', sizeof(RdataBuf[k])); //������� buf
		memset(ScmdBuf[k], '\0', sizeof(ScmdBuf[k])); //������� buf
		ScmdBuf[k][1] = NOSEND;  //��ʼ��ÿ���豸 ��������buf ��ΪNOSEND
	}
	//��ʼ���߳���
	for(k=0;k<MaxEquiptNum;k++)
	{
		pthread_mutex_init( &(Rdata_mutex[k]), NULL);
		pthread_mutex_init( &(Scmd_mutex[k]), NULL);		
	}
	pthread_mutex_init( &Report_mutex, NULL);

	
	//���� �豸������Ϣ��ָ������
	struct message *SamplerMess_Node[equiptNum];
	//�������ϱ��豸��ʶ
	int UpdataFlag = 0;
	
	//3.�����豸�ɼ� ���߳�
	//����ÿ���豸���߳� ����ȡ�ɼ����� 
	for(i=0; i<equiptNum; i++){
		
		if( strstr(equiptCfgMessS[i].equiptLibName, ".so")==NULL ) continue;  //�������豸�˿� �򷵻�
		if(equiptCfgMessS[i].equiptType ==  20)  UpdataFlag++;
		char *libName = equiptCfgMessS[i].equiptLibName;
		sprintf(spriBuf,"###master  ReadFile>>equiptCfgMessS[%d].equiptLibName=%s\n", equiptCfgMessS[i].equiptId, libName);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		
		//ʵ����һ���豸������Ϣ��
		SamplerMess_Node[i] = newSamplerMess((struct EquiptPortMessage *)&(equiptCfgMessS[i]), RdataBuf[equiptCfgMessS[i].equiptId], ScmdBuf[equiptCfgMessS[i].equiptId]);		

		//�½��߳�
		pthread_t pthread1;
		int ret = pthread_create(&pthread1, NULL, sampler, (void *)SamplerMess_Node[i]);
		if(ret == -1)
		{
			perror("master the pthread_create");
			sprintf(spriBuf, "master����pthread_create error!\n");
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		}
	}
	sleep(5);
	sprintf(spriBuf,"##################master�����ɼ��߳� ����������######################\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	

	
	//��ʼ��   �ϱ�����buf
	RcmdBuff[0] = NOSEND;
	SdataBuff[0] = -1;
	
	//����ʵʱ �ϱ�����
	while(UpdataFlag != 0)
	{			
		for(i=0; i<equiptNum; i++)//�����ɼ��豸 ��������
		{
			//�жϳ��ɼ����豸
			if(equiptCfgMessS[i].equiptType ==  10)
			{
				int EquiptId = equiptCfgMessS[i].equiptId;
				pthread_mutex_lock( &Rdata_mutex[EquiptId]);//��ס	�˴�һ��Ҫ���� �ɼ������߳�read ����дRdataBuf �������ݸ�ֵֵ����
				int ii;
				float f_Buff[MaxSignalNum];          //�м���� buf				
				for(ii=0;ii<MaxSignalNum;ii++)
				{
					f_Buff[ii] = RdataBuf[EquiptId][ii];  //��ȡĳ���豸����
				}
				pthread_mutex_unlock( &Rdata_mutex[EquiptId]);//�ٿ���
				
				//�����ϱ��豸  �����ϱ�
				for(j=0; j<equiptNum;j++)
				{
					if(equiptCfgMessS[j].equiptType ==  20)
					{
						pthread_mutex_lock( &Report_mutex);//��ס  �ϱ��Ĳɼ����� �� �������� ���� �� �ϱ��̻߳���
						for(ii=0;ii<MaxSignalNum;ii++)
						{
							SdataBuff[ii] = f_Buff[ii];  //��ȡĳ���豸����
						}
						equiptCfgMessS[j].UpDataFlag = 1;
						pthread_mutex_unlock( &Report_mutex);//�ٿ���
						
					
						usleep(equiptCfgMessS[j].equiptReRunTime*1000); //�ɼ�����
						//�ϱ�ͨ�� �����ϱ�����
						if(equiptCfgMessS[j].UpDataFlag == 2)
						{						
							if((RcmdBuff[0] != NOSEND) && (RcmdBuff[0] != 0))
							{
								pthread_mutex_lock( &Report_mutex);//��ס  �ϱ��Ĳɼ����� �� �������� ���� �� �ϱ��̻߳���
								setWriteCmd((int)RcmdBuff[0], RcmdBuff);
								memset(RcmdBuff, '\0', MaxSignalNum);          //�����������
								RcmdBuff[0] = NOSEND;
								pthread_mutex_unlock( &Report_mutex);//�ٿ���
							}
							equiptCfgMessS[j].UpDataFlag = 0;							
						}
					sprintf(spriBuf,"###master>>>>%s�ϱ�%s�豸�����ݣ�\n", equiptCfgMessS[j].equiptLibName, equiptCfgMessS[i].equiptLibName);
					LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
					}
				}
			}
		}		
		
	}//while(true) end

	while(true)
	{
		sleep(5); //5s
	}
	//return NULL;
	pthread_exit(0);
}


/*****************************************************************/
// ---------------------------------------------------------------
// ==================�������ݲ��������ӿ�(�����ϲ�)===============
// ---------------------------------------------------------------
/*****************************************************************/
//   ��ȡĳ�豸 ʵʱ����
void getEquiptData(IN int EquiptId, OUT float* fData)
{
	if(EquiptId>0 && EquiptId<MaxEquiptNum)
	{
		pthread_mutex_lock( &Rdata_mutex[EquiptId]);//��ס	
		int i = 0;
		for(i=0; i<MaxSignalNum; i++)
		{
			fData[i] = RdataBuf[EquiptId][i];
		}
		pthread_mutex_unlock( &Rdata_mutex[EquiptId] );//�ٿ���
		usleep(500); //0.5ms �� ��ֹ ���ϻ�ȡ ռ���˲ɼ��̵߳�ʱ��Ƭ
	//	printf("master>>>>getEquiptData--RdataBuf[%d]>>>%f  %f  %f\n", EquiptId-1, RdataBuf[EquiptId-1][0], RdataBuf[EquiptId-1][1], RdataBuf[EquiptId-1][2]);
	//	printf("master>>>>getEquiptData--data[%d]>>>%f  %f  %f\n", EquiptId-1, fData[0], fData[1], fData[2]);		
	}
}
//   ���ÿ������� control �� strCmd ��ʽ��"1,88"  ���豸EquiptId �ź�1������88����
void setControlCmd(IN int EquiptId, IN char* strCmd) //ע��� ��������ʱ ��ͬ���豸EquiptId���з����� ����Ӧ����2s (��Ϊ�ɼ�����ΪԼҪ2s)
{
	pthread_mutex_lock( &(Scmd_mutex[EquiptId]) );//��ס
	ScmdBuf[EquiptId][1] = CONTROL_STAR;
	strcpy((ScmdBuf[EquiptId]+2),strCmd);
	pthread_mutex_unlock( &(Scmd_mutex[EquiptId]) );//�ٿ���
	usleep(500); //0.5ms �� ��ֹ �������� ռ���˲ɼ��̵߳�ʱ��Ƭ	
}
//   ���÷������� write �� strCmd ��ʽ��01 03 00 00 00 08 44 0C   ��EquiptId�����͸������
void setWriteCmd(IN int EquiptId, IN char* strCmd)//ע��� ��������ʱ ��ͬ���豸EquiptId���з����� ����Ӧ����2s
{
	pthread_mutex_lock( &(Scmd_mutex[EquiptId]) );//��ס
	ScmdBuf[EquiptId][1] = WRITE_STAR;
	int n = 0;
	for(n=0;n<MaxSignalNum-2;n++)
	{
		ScmdBuf[EquiptId][n+2] = strCmd[n];
	}
	pthread_mutex_unlock( &(Scmd_mutex[EquiptId]) );//�ٿ���
	usleep(500); //0.5ms �� ��ֹ �������� ռ���˲ɼ��̵߳�ʱ��Ƭ	
}
//��ȡ  �ɼ���  �ɼ��豸id ����
void get_equiptIdBuf(OUT int *idBuf)
{
	int i = 0;
	for(i=0;i<equiptNum;i++)
	{
		idBuf[i] = EquiptIdBuf[i];
	}
}
//��ȡ �ɼ����� �ɼ��豸����
int get_equiptNum()
{
	return equiptNum;
}







