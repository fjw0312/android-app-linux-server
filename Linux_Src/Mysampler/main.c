#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<dlfcn.h>
#include <pthread.h>
#include <unistd.h>

#include "dlEquipt.h"
#include "samplerEquipt.h"
#include "readIni.h"
#include "master.h"
#include "net_server.h"


/*****************************************************************/
// �������ƣ�Main  ����ں���
// �����������׶˲ɼ��� �ɼ���ں���
// ���������
// ���������
// ��    �أ�    
// ��    ����
/*****************************************************************/
int main(int argc,char **argv)
{
	char spriBuf[50];
	sprintf(spriBuf, "into main !\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����

	//�����ɼ��� ���߳�
	pthread_t pthread1;
	int ret1 = pthread_create(&pthread1, NULL, master, NULL);
	if(ret1 == -1)
	{
		perror("Main  the pthread-master");
		sprintf(spriBuf, "Main����pthread_create1 error!\n");
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		return -1;
	}
	sleep(2);
	//�½�����������߳�
	pthread_t pthread2;
	int ret2 = pthread_create(&pthread2, NULL, net_server, NULL);	
	if(ret2 == -1)
	{
		perror("Main  the pthread-netServer");
		sprintf(spriBuf, "Main����pthread_create2 error!\n");
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		return -1;
	}
	
	sleep(10);
	
	//��ȡ ���� �鿴
	int a = 0;
	while(a<50)
	{
//		int EquiptId = 1;
		int i = 0;
		for(i=1;i<3;i++)
		{
			
	/*		float data[MaxSignalNum];
			getEquiptData(i, data);
			printf("main>>�鿴��id=%d�ɼ����ݣ�\n", i);
			for(i=0; i<50; i++)
			{
				printf("%f ", data[i]);
			}
			printf("\n\n");
	*/	
	//		printf("main>>>>�ɼ���������data[%d]>>>%f  %f  %f\n", EquiptId+i, data[0], data[1], data[2]);
		}

		sleep(1);
	/*	//����control
		if(a==10)
		{
			char *strCmd = "3,998";
			setControlCmd(1, strCmd);
			sprintf(spriBuf, "######main>>>>control ����strcmd-1>>>%s\n", strCmd);
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		}
		if(a==12)
		{
			char *strCmd = "3,88";
			setControlCmd(1, strCmd);
			sprintf(spriBuf, "######main>>>>control ����strcmd-2>>>%s\n", strCmd);
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		}
		if(a==15)
		{
			char *strCmd = "2,555";
			setControlCmd(1, strCmd);
			sprintf(spriBuf, "######main>>>>control ����strcmd-3>>>%s\n", strCmd);
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		}
		if(a==17)
		{
			char *strCmd = "2,666";
			setControlCmd(1, strCmd);
			sprintf(spriBuf, "######main>>>>control ����strcmd-4>>>%s\n", strCmd);
			LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		}
		
		//����write
		if(a==30)
		{
			char strbuf[20] = {0x01, 0x03, 0x04, 0x05, 0x06, 0x08, 0x44, 0x0C};
			setWriteCmd(1, strbuf);
			printf("######main>>>>control ����strbuf-1>>>%s\n", strbuf);
		}
		if(a==40)
		{
			char strbuf[20] = {0x01, 0x04, 0x02, 0x01, 0x03, 0x09, 0x61, 0xCC};
			setWriteCmd(1, strbuf);
			printf("######main>>>>control ����strbuf-2>>>%s\n", strbuf);
		}
		if(a==45)
		{
			char strbuf[20] = {0x01, 0x04, 0x00, 0x00, 0x00, 0x05, 0x30, 0x09};
			setWriteCmd(1, strbuf);
			printf("######main>>>>control ����strbuf-3>>>%s\n", strbuf);
		}
	*/	
		a++;
	}

	
	//�ȴ����߳� �˳�
	pthread_join(pthread1, NULL);
	pthread_join(pthread2, NULL);
	sprintf(spriBuf, "into main  end!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	return 0;
}
