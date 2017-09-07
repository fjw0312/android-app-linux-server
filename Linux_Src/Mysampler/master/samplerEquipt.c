#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>
#include<unistd.h>
#include<pthread.h>
#include<time.h>

#include "samplerEquipt.h"
#include "dlEquipt.h"

//���ļ� Ϊ�����豸(libEquipt.so)�Ĳɼ� ����

//ʵʱ �����ϱ� buf  ���ݳ�
extern char  RcmdBuff[MaxSignalNum+1];
extern float  SdataBuff[MaxSignalNum+1];

extern pthread_mutex_t  Rdata_mutex[MaxEquiptNum];    //���ݲɼ� read �߳���
extern pthread_mutex_t  Scmd_mutex[MaxEquiptNum];    //��������control �߳���
extern pthread_mutex_t  Report_mutex;  //�ϱ�ͨ��Report �߳���



char scmdBuf[MaxSignalNum];  //�����м����
/*
//�������� ��ʼ��־
#define CONTROL_STAR��'&'
#define WRITE_STAR    '#'
#define NOSEND        '$'
//�Զ���һ����Ϣ�ṹ�����
struct message
{
	struct EquiptPortMessage *EquiptCfgMess;  //�豸�ɼ�������Ϣ
	bool libEquipt_Loaded_Flag;  //��̬���Ѽ��� ��־
	bool libPort_Open_Flag;      //�˿��Ѵ� ��־

	float *readData_A;   //����ģʽ read    �Ķ�ȡ����
	char *sCmdStr_A;    //����ģʽ control �ķ�������
};
*/

//new һ���豸�ɼ���Ϣ�� �ṹ��ڵ�
struct message *newSamplerMess(struct EquiptPortMessage *EquiptCfgMess, float *Data, char *Str)
{
	struct message *node = malloc(sizeof(struct message));
	node->EquiptCfgMess = EquiptCfgMess; //��ȡ�豸������Ϣ��
	node->libEquipt_Loaded_Flag = true;  //Ĭ���豸��̬�����
	node->libPort_Open_Flag = true;      //Ĭ���豸��̬��˿� ��

	
	Data[0] = EquiptCfgMess->equiptId;   //RDataBuf �׸�Ϊ �豸id
	Str[0] = EquiptCfgMess->equiptId;    //SCmdBuf �׸�Ϊ �豸id
	node->readData_A = Data;                   //��ʼ�� read ��ȡ������
	node->sCmdStr_A = Str;                      //��ʼ�� control ��ȡ������
	
	
	return node;
}

void *sampler(void *arg)
{
	char spriBuf[400]; //log buf
	
	struct message *mess = (void *)arg;
	struct EquiptPortMessage *EquiptCfgMess = mess->EquiptCfgMess; //��ȡ�豸������
	//�豸��̬�� �������
	int zero = 0;
	void *libHandle = (void *)&zero;
	//new һ���豸��̬�� API�ṹ��ڵ�
	struct EquiptApiNode *E_ApiNode = NewEquiptApiNode(EquiptCfgMess->equiptLibName);
	
	//���� ��̬�� dlopen
	DL_open(EquiptCfgMess->equiptLibName, &libHandle);
	sprintf(spriBuf,"DL_open %s has done !\n", EquiptCfgMess->equiptLibName);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	
	//���� ��̬������API dlsym
	DL_sym_AllApi(libHandle, E_ApiNode);
	sprintf(spriBuf,"DL_sym_AllApi %s has done !\n", EquiptCfgMess->equiptLibName);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	
	//���ж�̬��API �ɼ�����
	while(mess->libEquipt_Loaded_Flag)
	{
		//�˿ڴ� �����ɼ�
		if(mess->libPort_Open_Flag){
			//�򿪲ɼ��˿�
			void *porthandle;
			E_ApiNode->DriverInit(EquiptCfgMess->pMes, &porthandle);
			sprintf(spriBuf,"E_ApiNode->DriverInit %s done success!\n", EquiptCfgMess->equiptLibName);
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
			
			//ѭ���ɼ�����
			while(mess->libPort_Open_Flag) //����ѭ���ɼ� ����
		    {
				if(EquiptCfgMess->equiptType==10) //�ɼ�ģʽ  ����read/control  �ص㣺�շ�������Ҫ����
				{
					//���� ����
					//�ж��Ƿ��п��������
					if((mess->sCmdStr_A != NULL)&&((mess->sCmdStr_A)[1] == CONTROL_STAR ) ){   //�����־λΪCONTROL_STAR ��������
						//�п��������

						
						pthread_mutex_lock( &(Scmd_mutex[EquiptCfgMess->equiptId]));           //��ס	
						memset(scmdBuf, '\0', MaxSignalNum);
						strcpy(scmdBuf, (char *)(mess->sCmdStr_A+2)); //�Ȱ��������� �������
						memset((mess->sCmdStr_A+1), '\0', MaxSignalNum-1); //�����������
						(mess->sCmdStr_A)[1] = NOSEND;						
						pthread_mutex_unlock( &(Scmd_mutex[EquiptCfgMess->equiptId]));         //�ٿ���
						
						E_ApiNode->Control(porthandle, EquiptCfgMess->equiptAddr, scmdBuf);  //���Ϳ�������
						sprintf(spriBuf,"samplerEquipt>>>���߳̽��� CONTROL ���� ���ݣ� %s\n", scmdBuf);
						LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
						continue;
					}else if( (mess->sCmdStr_A != NULL)&&((mess->sCmdStr_A)[1] == WRITE_STAR ) )
					{
						//�п��������						
						pthread_mutex_lock( &(Scmd_mutex[EquiptCfgMess->equiptId]));          //��ס
						memset(scmdBuf, '\0', MaxSignalNum);
						int n = 0;
						for(n=0;n<MaxSignalNum-2;n++)
						{
							scmdBuf[n] = (mess->sCmdStr_A)[n+2];
						}
						memset((mess->sCmdStr_A+1), '\0', MaxSignalNum-1);          //�����������
						(mess->sCmdStr_A)[1] = NOSEND;
						pthread_mutex_unlock( &(Scmd_mutex[EquiptCfgMess->equiptId]));        //�ٿ���	
						
						E_ApiNode->Write(porthandle, scmdBuf, MaxSignalNum);  //���Ϳ�������						
						sprintf(spriBuf,"samplerEquipt>>>���߳̽��� WRITE ���� ���ݣ�%s\n", scmdBuf); //��Ϊ8bit ���� ��ӡΪ�ַ����ѱ�ʶ
						LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����						
						continue;
					}
					//��ȡ ����					
					float rBuf[MaxSignalNum-2];
					int ret = E_ApiNode->Read(porthandle, EquiptCfgMess->equiptAddr, rBuf);	//Ϊ��������  �ɼ�������
					pthread_mutex_lock( &(Rdata_mutex[EquiptCfgMess->equiptId]) );//��ס
					if(ret == 0) //ͨ���ж�
					{
						mess->readData_A[1] = 0; //ͨ���ж� 
					}else{
						mess->readData_A[1] = 1; //ͨ������ 
					}
					int i = 0;
					for(i=0; i<MaxSignalNum-2; i++)
					{
						mess->readData_A[i+2] = rBuf[i];
					}
					pthread_mutex_unlock( &(Rdata_mutex[EquiptCfgMess->equiptId]));//�ٿ���
					sprintf(spriBuf,"samplerEquipt>>>>׼�����ݲɼ�ret:%d �豸id=%d>>>>>\n", ret, EquiptCfgMess->equiptId);
					LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
					//system("date +%Y-%m-%d_%H:%M:%S");
					//printf("���߳̽��� read <�豸id=%d>�ɼ����ݣ� %f %f %f\n", EquiptCfgMess->equiptId,(mess->readData_A)[0],(mess->readData_A)[1],(mess->readData_A)[2]);										
					usleep(EquiptCfgMess->equiptReRunTime*1000); //�ɼ�����
				}else if(EquiptCfgMess->equiptType==20)   //�ϱ�ģʽ ����Report  �ص㣺�շ����ݲ���Ҫ����
				{	
			//		printf("׼���ϱ�ͨ��id=%d>>>>>\n", mess->PortMess->portId);
			//		system("date +%H:%M:%S");				
					//�� �ϱ��豸 �ϱ��ӿ�ͨ��
					if(EquiptCfgMess->UpDataFlag==1){  //�ȴ� ǰһ���ϱ�����׼������ �ٽ���
						pthread_mutex_lock( &Report_mutex);//��ס
						E_ApiNode->Report(porthandle, SdataBuff, RcmdBuff);						
					//	sprintf(spriBuf,"samplerEquipt>>>>�ϱ�����ͨ���豸id=%d ��������%s���գ�\n", EquiptCfgMess->equiptId,RcmdBuff);
					//	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
						EquiptCfgMess->UpDataFlag = 2;						
						pthread_mutex_unlock( &Report_mutex);//�ٿ���
					}
					//printf("�����ϱ�ͨ��   >>>>>\n");
					system("date +%Y-%m-%d_%H:%M:%S");
					//usleep(1000*400); //ͨ������200ms
					usleep(EquiptCfgMess->equiptReRunTime*1000); //�ɼ�����
			   }
			}
			//�رղɼ��˿�
			E_ApiNode->DriverStop(NULL, porthandle);
			sprintf(spriBuf,"samplerEquipt>>>E_ApiNode->DriverStop %s done success!\n", mess->EquiptCfgMess->equiptLibName);	
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����			
		}
		usleep(EquiptCfgMess->equiptReRunTime*1000);
	}
	
	//ж�� ��̬�� dlclose
	DL_close(libHandle); 
	sprintf(spriBuf,"samplerEquipt>>>>DL_close has done !\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	
	return NULL;
}