
//****************************************************************************
//*
//* �ļ���: ShareMem.h
//*
//* ��;:
//*     �����ڴ�ģ���ͷ�ļ����빲���ڴ��йصĶ���
//*
//* IO-LAN + SHMM �� V000 �� B000 �� D001
//*
//****************************************************************************

#ifndef SHARE_MEM_H
#define SHARE_MEM_H  0x01

#ifdef WIN32
#pragma pack (1)
#define PACKED
#else
#define PACKED __attribute__((packed, aligned(1)))
#endif

#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <ctype.h>

#include <unistd.h>
#include <sys/wait.h>
#include <limits.h>

#define SHMM_BUFSZ (1024 * 4)

// ���������
#define SHMM_ERR_OK             0x00  // û�д���
#define SHMM_ERR_FTOK          -0x01  // ִ��ftok()����ʧ��
#define SHMM_ERR_SHMGET        -0x02  // ִ��shmget()����ʧ��
#define SHMM_ERR_SHMCTL        -0x03  // ִ��shmctl()����ʧ��
#define SHMM_ERR_LEN_MAX       -0x04  // ���ݳ��ȳ�����������ֵ
#define SHMM_ERR_POINT         -0x05  // �����ڴ������ָ�뽫Խ�磬���������������ڴ洢������
#define SHMM_ERR_SHMAT         -0x06  // ִ��shmat()����ʧ��
#define SHMM_ERR_SHMDT         -0x07  // ִ��shmdt()����ʧ��
#define SHMM_ERR_SEMGET        -0x08  // ִ��semget()����ʧ��
#define SHMM_ERR_SEMOP         -0x09  // ִ��semop()����ʧ��
#define SHMM_ERR_SEMCTL        -0x0A  // ִ��semctl()����ʧ��
#define SHMM_ERR_NO_RW         -0x0B  // �����ڴ治�ܽ��ж�д����
#define SHMM_ERR_CHANNEL       -0x0C  // ͨ���Ŵ���


////////////////////////////////////////////////////////////////////

//***************************************************
//              �����ڴ��¼��Ϣ�ṹ��
//***************************************************
typedef struct _SHMM_RECORD_INFO
{  
	char         cValueDi;            // ����DIͨ���ĵ�ǰֵ
	char         cDiWarnStatus;       // ����DIͨ���ĸ澯״ֵ̬��1:�и澯��0:�޸澯

	char         cValueEmpty1;        // Ԥ���ַ�ֵ1
	char         cValueEmpty2;        // Ԥ���ַ�ֵ2

	float        fValueAi1;           // AIͨ��1�ĵ�ǰֵ
	float        fValueAi2;           // AIͨ��2�ĵ�ǰֵ
	float        fValueAi3;           // AIͨ��3�ĵ�ǰֵ
	float        fValueAi4;           // AIͨ��4�ĵ�ǰֵ
	float        fValueAi5;           // AIͨ��5�ĵ�ǰֵ
	float        fValueAi6;           // AIͨ��6�ĵ�ǰֵ
	float        fValueAi7;           // AIͨ��7�ĵ�ǰֵ
	float        fValueAi8;           // AIͨ��8�ĵ�ǰֵ
	float        fValueAi9;           // AIͨ��9�ĵ�ǰֵ
	float        fValueAi10;          // AIͨ��10�ĵ�ǰֵ
	float        fValueAi11;          // AIͨ��11�ĵ�ǰֵ
	float        fValueAi12;          // AIͨ��12�ĵ�ǰֵ
	float        fValueAi13;          // AIͨ��13�ĵ�ǰֵ
	float        fValueAi14;          // AIͨ��14�ĵ�ǰֵ

	float        fValueEmpty1;        // Ԥ������ֵ1
	float        fValueEmpty2;        // Ԥ������ֵ2
	
}SHMM_RECORD_INFO, *PSHMM_RECORD_INFO;

//-- start ����ӿں�����װ --

//************************************************************
// ����: SHMM_Open
//
// ˵��:
//  �򿪻򴴽������ڴ�
//    
// ������  
//   bCreate  1�����������ڴ棻0���򿪹����ڴ�
//
// ����ֵ: 
//	 -1:    ʧ��
//   ������ ipcs key ֵ
//	
//************************************************************
key_t SHMM_Open(int bCreate);

//************************************************************
// ����: SHMM_Close
//
// ˵��:
//   �رչ����ڴ�
//    
// ������   
//   g_keyShmm  ipc key ֵ
//
// ����ֵ: 
//	 0:    �ɹ�
//   ����: ������
//	
//************************************************************
int SHMM_Close();

//************************************************************
// ����: SHMM_Write
//
// ˵��:
//   �����ڴ�д������
//    
// ������   
//   nChannelNo  ͨ���ţ�0��DI��1~14��AI
//   pBuf        ����д�����ݵ��ڴ�ָ�롣
//   nLen        Ҫд������ݳ��ȡ�����DI��Ϊ2 char������AI��Ϊ1 float
//
// ����ֵ: 
//	 >= 0:    ʵ��д������ݳ���
//   <  0:    ������
//	
//************************************************************
int SHMM_Write(int nChannelNo, char* pBuf, int nLen);

//************************************************************
// ����: SHMM_Read
//
// ˵��:
//   ��ȡ�����ڴ������
//    
// ������   
//   nChannelNo  ͨ���ţ�0��DI��1~14��AI
//   pBuf        �����ȡ���ݵ��ڴ�ָ�롣
//   nLen        Ҫ��ȡ�����ݳ��ȡ�����DI��Ϊ2 char������AI��Ϊ1 float
//
// ����ֵ: 
//	 >= 0:    ʵ�ʶ�ȡ�����ݳ���
//   <  0:     ������
//	
//************************************************************
int SHMM_Read(int nChannelNo, char* pBuf, int nLen);

//************************************************************
// ����: SHMM_SetReadedStatus
//
// ˵��:
//   ��������DIͨ�������Ѷ��ı�ʶ
//    
// ������   
//
// ����ֵ: 
//	 = 0:      ������ȷ
//   <  0:     ������
//	
//************************************************************
int SHMM_SetReadedStatus();

//************************************************************
// ����: SHMM_GetWarnStatus
//
// ˵��:
//   ��ȡ����DIͨ���ĸ澯��ʶֵ
//    
// ������ 
//   pDiWarnStatus    ����DIͨ���ĸ澯��ʶֵ
//
// ����ֵ: 
//	 = 0:      ��ȷ
//   <  0:     ������
//	
//************************************************************
int SHMM_GetWarnStatus(char* pDiWarnStatus);

//-- end ����ӿں�����װ --

#ifdef WIN32
#pragma pack (4)
#else
#endif

#endif // SHARE_MEM_H
