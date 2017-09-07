
//****************************************************************************
//* �ļ���: IOLAN.cpp
//*
//* ��;:
//*     ���ļ����ṩӦ�ó������ںͳ���
//*
//* Share Memory + IOLAN.cpp �� V000 �� B000 �� D001
//*
//****************************************************************************

#include "basetypes.h"

#include "ShareMem.h"


//************************************************************
// ����: main
//
// ˵��:
//   ���������
//    
// ������   
//	 argc     �����в�������
//   argv[]   �����в����б�
//
// ����ֵ: 
//	 0:    �����˳�
//   ����: �쳣�˳�
//	
//************************************************************
int main(int argc, char* argv[])
{
	// ��������в���
	if ((argc == 2) && (strcmp(argv[1], "-t") == 0))
	{// ��ʾ��������±���ʱ��

		printf("����������±���ʱ��Ϊ��2006.07.01 - 15:00 \n");
		return 0;
	}
	else if ((argc == 2) && (strcmp(argv[1], "-w") == 0))
	{// д������

		// ��ʾϵͳ��ǰ��ipcs
		//system("ipcs  -m -s");

		// ���������ڴ�
		key_t keyShmm = SHMM_Open(1); // Segment ID 
		
		if (keyShmm < 0)
		{
			printf("���������ڴ�ʧ�ܣ�������Ϊ��%d \n", keyShmm);
			return 1;
		}
		else
		{
			printf("���������ڴ� %d �ɹ���\n", keyShmm);
		}
		
		// ��ʾϵͳ��ǰ��ipcs
		//system("ipcs  -m -s");

		SHMM_RECORD_INFO shmmRecordInfo; 
		shmmRecordInfo.cDiWarnStatus = 0x1F;
		shmmRecordInfo.cValueDi      = 0x0F;

		// д������
		int iErrCode = SHMM_Write(0, &shmmRecordInfo, 2 * sizeof(char));
		if (iErrCode < 0)
		{
			printf("�����ڴ�д������ʧ�ܣ�������Ϊ %d ��\n", iErrCode);
			return 1;
		}

		sleep(10);

		// �رչ����ڴ�
		if (SHMM_Close() != 0)
		{
			printf("�رչ����ڴ� %d ʧ�ܣ�\n", keyShmm);
			return 1;
		}
		
		// ��ʾϵͳ��ǰ��ipcs
		//system("ipcs  -m -s");
	}
	else if ((argc == 2) && (strcmp(argv[1], "-r") == 0))
	{// ��ȡ����

		// �򿪹����ڴ�
		key_t keyShmm = SHMM_Open(0); // Segment ID 
		
		if (keyShmm < 0)
		{
			printf("�򿪹����ڴ�ʧ�ܣ�������Ϊ��%d \n", keyShmm);
			return 1;
		}
		else
		{
			printf("�򿪹����ڴ� %d �ɹ���\n", keyShmm);
		}

		SHMM_RECORD_INFO shmmRecordInfo; 
		memset(&shmmRecordInfo, '\0', sizeof(SHMM_RECORD_INFO));

		int iErrCode = SHMM_Read(0, &shmmRecordInfo, 2 * sizeof(char));
		if (iErrCode < 0)
		{
			printf("�ӹ����ڴ��ȡ����ʧ�ܣ�������Ϊ %d ��\n", iErrCode);
			return 1;
		}

		printf("��ȡ����DI����Ϊ��0x%x 0x%x ",
			shmmRecordInfo.cDiWarnStatus, shmmRecordInfo.cValueDi);
	}
	else
	{
		printf("�����в������ԣ�\n");
		printf(" -t ��ʾ������ı���ʱ��\n");
		printf(" -w �����ڴ�д�����ݵ�����\n");
		printf(" -r �ӹ����ڴ��ȡ���ݵ�����\n");

		return 1;
	}
	//-- end of " if ((argc == 2) && (strcmp(argv[1], "-t") == 0)) " --

 
	return 0;
}

