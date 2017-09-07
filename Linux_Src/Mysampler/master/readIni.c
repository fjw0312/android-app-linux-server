#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<stdbool.h>
#include<unistd.h>

#include "readIni.h"

//----���ļ�Ϊ ��ȡ�����ļ� �����ļ�API

//��ȡ�������ļ�����·��
#define INIFINE  "./SAM.ini"
//��ӡ����־�ļ�����·��   LogFile_Flag �Ƿ��ӡ��־
#define LogFilePath "./LogFile.log"
#define LogFile_Flag


//��ȡÿһ���ַ�
char readBuf[400];
char *sub = "=#>";	//����ָ�������


/*
//�Զ���һ���˿���Ϣ�ṹ�� ����
struct portMessage
{
	int  portId;                 //�˿�id�� eg:1
	char portDiverLibName[100]; //�˿�������̬������·�� eg��"/data/ChaoYF/fjw/TestSampler/comm_std_serial.so"
	char portDiverName[50];     //�˿���������·��     eg: "/dev/ttyS3"
	char portPara[50];          //�˿��������ò���     eg: "9600,n,8,1"
	int  portAttr;              //�˿�����ģʽ         eg: 1  ����ģʽ
	int  portOutTime;           //�˿ڳ�ʱʱ��         eg: 20000  20s
	
}; 

//�Զ���һ���豸�ɼ�������Ϣ�ṹ��  ����
struct EquiptPortMessage
{ 
	int  equiptId;            //�豸id
    int  portId;              //���ж˿�id
	char equiptLibName[100]; //�˿����е��豸��̬������  "/data/ChaoYF/fjw/TestSampler/libEquipt1.so"  
	int  equiptType;          //�豸������ 10:�ɼ��豸  20:�ϱ��豸
	int  equiptAddr;          //�豸��ַ
	int  equiptReRunTime;     //�豸�ɼ����� ms
	
	//	void *porthandle;   //�˿ھ��  ��portId ->portMessage ->EquiptInit��ȡ��
	struct portMessage *pMes;   //�˿���Ϣ ��
};



*/

//new һ���˿���Ϣ�ṹ���
struct portMessage *newPortMessage()
{
	struct portMessage *node = malloc(sizeof(struct portMessage));
	memset(node->portDiverLibName, '\0', 100);
	memset(node->portDiverName, '\0', 50);
	memset(node->portPara, '\0', 50);
	
	
	return node;
};
//new һ���豸������Ϣ�ṹ���
struct EquiptPortMessage *newEquiptPortMessage()
{
	struct EquiptPortMessage *node = malloc(sizeof(struct EquiptPortMessage));
	memset(node->equiptLibName, '\0', 100);
	
	return node;
};


//���ļ� ��ȡ�ļ� ÿ���ַ�
int ReadFile_PortCfg(struct portMessage pMess[])  //IN OUT �ṹ�� �����飩ָ��
{
	char spriBuf[400];

	
	//���ļ�
	FILE *file = fopen(INIFINE, "r"); //ֻ���ļ�
	if(file==NULL)
	{
		sprintf(spriBuf, "���ļ�%sʧ�ܣ�\n", INIFINE);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		perror("fopen fail\n");
		return -1;
	}
	
	//�������
	int PortNumber = 0;        //�ɼ����˿�����	
	char libPortPath[100];     //�˿�������̬����·��
	int i = 0;


	while( fgets(readBuf, 400, file))
	{
		//�ж϶�ȡ���ַ�����
		if( strstr(readBuf, "PortNumber")) //��ȡ �ɼ����˿�����
		{
			char *buf1 = strtok(readBuf, sub); //��ȡ�Ⱥ��������
			char *buf2 = strtok(NULL, sub);    //��ȡ�Ⱥ��ұ�����
			if(buf1==NULL || buf2==NULL) continue;
			if(buf2){
				PortNumber = atoi(buf2);
			}
		}else if(strstr(readBuf, "libPortPath"))  //��ȡ �˿�������̬����·��
		{
			char *buf1 = strtok(readBuf, sub); //��ȡ�Ⱥ��������
			char *buf2 = strtok(NULL, sub);    //��ȡ�Ⱥ��ұ�����
			if(buf1==NULL || buf2==NULL) continue;
			if(buf2){
				strcpy(libPortPath, buf2);   //buf2 ��bugҪȥ�����ո�
			}
		}else if( strstr(readBuf, "portSetlibDiver") )     //��ȡ �ɼ����˿�����
		{
				char *buf1 = strtok(readBuf, sub); //��ȡ�Ⱥ��������
				char *buf2 = strtok(NULL, sub);    //��ȡ�Ⱥ��ұ����� �˿�id  1
				char *buf3 = strtok(NULL, sub);    //��ȡ�Ⱥ��ұ�����comm_std_serial.so
				char *buf4 = strtok(NULL, sub);    //��ȡ#��ȡ���� dev/ttyS3
				char *buf5 = strtok(NULL, sub);    //��ȡ#��ȡ���� 9600,n,8,1
				char *buf6 = strtok(NULL, sub);    //��ȡ#��ȡ���� 1
				char *buf7 = strtok(NULL, sub);    //��ȡ#��ȡ���� 20000
				if(buf1==NULL ||buf2==NULL || buf3==NULL) continue;			
				pMess[i].portId = atoi(buf2);
				strcpy(pMess[i].portDiverLibName, libPortPath);
				strcat(pMess[i].portDiverLibName, buf3);	
				strcpy(pMess[i].portPara, buf5);
				pMess[i].portAttr = atoi(buf6);
				pMess[i].portOutTime = atoi(buf7);
				if(strstr(buf4, " ")){
					memset(pMess[i].portDiverName, '\0', 50);
				}else{
					strcpy(pMess[i].portDiverName, buf4);
				}
				
				i++;
		}
	}
	fclose(file);

	sprintf(spriBuf,"PortNumber=%d libPortPath=%s\n", PortNumber,libPortPath);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	for(i=0; i<PortNumber; i++)
	{
		sprintf(spriBuf,"portMess[%d]>>portId=%d portDiverLibName=%s portDiverName=%s portPara=%s portAttr=%d portOutTime=%d\n",
		i,pMess[i].portId,pMess[i].portDiverLibName,pMess[i].portDiverName,pMess[i].portPara,pMess[i].portAttr,pMess[i].portOutTime);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	}
	
    return PortNumber;
}

//���ļ� ��ȡ�ļ� ÿ���ַ�
int ReadFile_EquiptCfg(struct EquiptPortMessage pMess[])  //IN OUT �ṹ�� �����飩ָ��
{
	char spriBuf[400];
			
	//���ļ�
	FILE *file = fopen(INIFINE, "r"); //ֻ���ļ�
	if(file==NULL)
	{
		sprintf(spriBuf,"���ļ�%sʧ�ܣ�\n", INIFINE);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
		perror("fopen fail\n");
		return -1;
	}
			
	//�������
	int EquiptNumber = 0; 	      //�ɼ��豸����
	char libEquiptPath[100];	  //�豸��̬����·��
	int i = 0;

	while( fgets(readBuf, 400, file))
	{
			//�ж϶�ȡ���ַ�����
			if( strstr(readBuf, "EquiptNumber")) //��ȡ �ɼ����˿�����
			{
				char *buf1 = strtok(readBuf, sub); //��ȡ�Ⱥ��������
				char *buf2 = strtok(NULL, sub);    //��ȡ�Ⱥ��ұ�����
				if(buf1==NULL || buf2==NULL) continue;
				if(buf2){
					EquiptNumber = atoi(buf2);
				}
			}else if(strstr(readBuf, "libEquiptPath"))  //��ȡ �˿�������̬����·��
			{
				char *buf1 = strtok(readBuf, sub); //��ȡ�Ⱥ��������
				char *buf2 = strtok(NULL, sub);    //��ȡ�Ⱥ��ұ�����
				if(buf1==NULL || buf2==NULL) continue;
				if(buf2){
					strcpy(libEquiptPath, buf2);	 //buf2 ��bugҪȥ�����ո�
				}
			}else if( strstr(readBuf, "portRunEquipt") )	   //��ȡ �ɼ����˿�����
			{
				char *buf1 = strtok(readBuf, sub); //��ȡ�Ⱥ��������
				char *buf2 = strtok(NULL, sub);    //�豸id 1
				char *buf3 = strtok(NULL, sub);    //�豸ģ��id 169
				char *buf4 = strtok(NULL, sub);    //�˿�id 1
				char *buf5 = strtok(NULL, sub);    //�豸��̬�� libEquipt.so
				char *buf6 = strtok(NULL, sub);    //�ɼ�����  10:�ɼ�  20:�ϱ�
				char *buf7 = strtok(NULL, sub);    //�豸��ַ
				char *buf8 = strtok(NULL, sub);    //�ɼ�����
				if(buf1==NULL ||buf2==NULL || buf3==NULL) continue;
				pMess[i].equiptId = atoi(buf2);
				pMess[i].EquipTemplateId = atoi(buf3);
				pMess[i].portId = atoi(buf4);
				strcpy(pMess[i].equiptLibName, libEquiptPath);
				strcat(pMess[i].equiptLibName, buf5);   //
				pMess[i].equiptType = atoi(buf6);
				pMess[i].equiptAddr = atoi(buf7);	
				pMess[i].equiptReRunTime = atoi(buf8);	
				i++;			
			}
	}
	fclose(file);


	sprintf(spriBuf,"EquiptNumber=%d libEquiptPath=%s\n", EquiptNumber,libEquiptPath);
	LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	for(i=0; i<EquiptNumber; i++)
	{
		sprintf(spriBuf,"equiptMess[%d]>>equiptId=%d portId=%d equiptLibName=%s equiptType=%d equiptAddr=%d equiptReRunTime=%d\n",
		i,pMess[i].equiptId,pMess[i].portId,pMess[i].equiptLibName,pMess[i].equiptType,pMess[i].equiptAddr,pMess[i].equiptReRunTime);
		LogFile_OutPrintf(spriBuf, sizeof(spriBuf));//log ��־��ӡ����
	}

		
	return EquiptNumber;
}


//log ��־��ӡ����
void LogFile_OutPrintf(char *str, int lenth)
{

	printf("%s", str);
	
#ifdef 	LogFile_Flag	
	system("date +%Y-%m-%d_%H:%M:%S >> ./LogFile.log");
	//����־�ļ�
	FILE *file = fopen(LogFilePath, "a+");
	if(file == NULL)
	{
		perror("fopen fail\n");
		return;
	}

	//д������
	fprintf(file, "%s", str);
	//�ر��ļ�
	fclose(file);	
#endif

	//���� �ַ����� ����
	memset(str, '\0', lenth);

}
