
//****************************************************************************
//*
//* ��;:
//*     �����ڴ�ģ���ʵ���ļ���ʵ�ֶ���̶Թ����ڴ�Ķ�д
//*
//* IO-LAN + SHMM �� V000 �� B000 �� D001
//*
//****************************************************************************

#include "ShareMem.h"

key_t g_keyShmm = -1;   // �����ڴ��key
int   g_iShmmId = -1;   // �����ڴ��ID
int   g_iSemid  = -1;   // �źŵƵ�ID



//************************************************************
// ����: SEM_LOCK
//
// ˵��:
//   �źŵƼ���
//    
// ������   
//   g_keyShmm  ipc key ֵ
//
// ����ֵ: 
//	 0:    �ɹ�
//   ����: ������
//	
//************************************************************
int SEM_LOCK(key_t g_keyShmm)
{
    int nsems = 1;   // How many semaphores to create 
	
	// �źŵƼ���
	struct sembuf sops;
	sops.sem_num = 0;             // A single semaphore 
    sops.sem_op  = -1;            // Increment the semaphore 
    sops.sem_flg = 0;             // block 

	if (semop(g_iSemid, &sops, 1) < 0)
	{
		//perror("semop() error:");
		return SHMM_ERR_SEMOP;
	}
	
	return SHMM_ERR_OK;
}

//************************************************************
// ����: SEM_UNLOCK
//
// ˵��:
//   �źŵƽ���
//    
// ������   
//   g_keyShmm  ipc key ֵ
//
// ����ֵ: 
//	 0:    �ɹ�
//   ����: ������
//	
//************************************************************
int SEM_UNLOCK(key_t g_keyShmm)
{
	// ��ȡ�źŵ�ID
    int nsems = 1;   // How many semaphores to create 
	
	// �źŵƽ���
	struct sembuf sops;
	sops.sem_num = 0;           // A single semaphore 
    sops.sem_op  = 1;           // Increment the semaphore 
    sops.sem_flg = 0;           // block 
	
	if (semop(g_iSemid,  &sops, 1) < 0)
	{
		//perror("semop() error:");
		return SHMM_ERR_SEMOP;
	}
	
	return SHMM_ERR_OK;
}

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
key_t SHMM_Open(int bCreate)
{
	g_keyShmm = ftok("/", 0);
	if (g_keyShmm < 0)
	{
		//perror("ftok() error:");
		return SHMM_ERR_FTOK;
	}

	/////////////////////////////////////////////////////////////

	// 1��������򿪹����ڴ�

	int iOpenFlag = 0600;
	if (bCreate == 1)
	{// ����Ǵ����ڴ�

		iOpenFlag |= IPC_CREAT;
	}
	
	// �򿪻򴴽������ڴ�
	g_iShmmId = shmget(g_keyShmm, SHMM_BUFSZ, iOpenFlag);
	if (g_iShmmId < 0)
	{
		//perror("shmget() error:");
		return SHMM_ERR_SHMGET;
	}
	
	////////////////////////////////////////////////////////////
	
	// 2����������źŵ�
    int           nsems = 1;   // How many semaphores to create 
    
    // Create the semaphore with world read-alter perms 
    g_iSemid = semget(g_keyShmm, nsems, iOpenFlag);
    if(g_iSemid < 0) 
	{
        //perror("semget():");
        return SHMM_ERR_SEMGET;
    }
	
	if (bCreate == 1)
	{// ����Ǵ����źŵ�
		
		struct sembuf buf;         // How semop should behave 

		// Set up the structure for semop 
		buf.sem_num = 0;           // A single semaphore 
		buf.sem_op  = 1;           // Increment the semaphore 
		buf.sem_flg = IPC_NOWAIT;  // Don't block 
		
		if((semop(g_iSemid, &buf, nsems)) < 0) 
		{
			//perror("semop() error:");
			return SHMM_ERR_SEMOP;
		}
	}
	//-- end of " if (bCreate == 1) " --
	
	////////////////////////////////////////////////////////////////

	// 3����ʼ�������ڴ���

	if (bCreate == 1)
	{// ����Ǵ��������ڴ棬���ʼ�������ڴ���
		
		char *shmbuf;		// Address in process 
		
		// Attach the segment 
		if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0)
		{
			// �źŵƽ���
			int iResult = SEM_UNLOCK(g_keyShmm);
			if (iResult < 0)
			{
				return iResult;
			}
			
			//perror("shmat() ERROR:");
			return SHMM_ERR_SHMAT;
		}
		
		// write data to shmm����ʼ��Ϊ0
		memset(shmbuf, 0, SHMM_BUFSZ);
		
		// Detach the segment 
		if (shmdt(shmbuf) < 0)
		{
			// �źŵƽ���
			int iResult = SEM_UNLOCK(g_keyShmm);
			if (iResult < 0)
			{
				return iResult;
			}
			
			//perror("shmdt() error:");
			return SHMM_ERR_SHMDT;
		}
	}
	//-- end of " if (bCreate == 1) " --

	return g_keyShmm;
}


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
int SHMM_Write(int nChannelNo, char* pBuf, int nLen)
{
	// ��鹲���ڴ��Ƿ���ڣ��Ƿ�ɶ�д
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	// ����ͨ���ż�����SM�е�λ��
	int nStartPos = 0; 
	if ((nChannelNo >= 0) && (nChannelNo <= 14))
	{// IO��ͨ����

		nStartPos = nChannelNo * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP1_CHANNEL_NO_END))
	{// ���1��1��bat��ͨ����

		nStartPos = (nChannelNo - 100 + BAT_GROUP_CHANNEL_NUM * 0 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP2_CHANNEL_NO_END))
	{// ���1��2��bat��ͨ����

		nStartPos = (nChannelNo - 130 + BAT_GROUP_CHANNEL_NUM * 1 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP1_CHANNEL_NO_END))
	{// ���2��1��bat��ͨ����

		nStartPos = (nChannelNo - 200 + BAT_GROUP_CHANNEL_NUM * 2 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP2_CHANNEL_NO_END))
	{// ���2��2��bat��ͨ����

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if (nChannelNo == SLOT_BOARD_SELFCHECK_INFO)
	{// ���1��2�ϵĲ忨�Լ���Ϣͨ����

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else
	{// �Ƿ�ͨ����

		return SHMM_ERR_CHANNEL;
	}
	//-- end of " if ((nChannelNo >= 0) && (nChannelNo <= 14)) " --

	// check nLen
	if (nChannelNo == 0)
	{// DI

		if (nLen > 2 * sizeof(char))
		{
			return SHMM_ERR_LEN_MAX;
		}
	}
	else 
	{// AI or bat

		if (nLen > SHMM_BUFSZ)
		{
			return SHMM_ERR_LEN_MAX;
		}
		
		if ((nStartPos + nLen) > SHMM_BUFSZ)
		{
			return SHMM_ERR_POINT;
		}
	}
	//-- end of " if (nChannelNo == 0) " --

	//printf("write -- �źŵƽ�������\n");

	// �źŵƼ���
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- �źŵ��Ѽ�����\n");
	
    char *shmbuf;		// Address in process 
    
	// Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0)
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmat() ERROR:");
		return SHMM_ERR_SHMAT;
    }
    
	// write data to shmm
	memcpy(shmbuf + nStartPos, pBuf, nLen);
	
	// Detach the segment 
    if (shmdt(shmbuf) < 0)
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmdt() error:");
		return SHMM_ERR_SHMDT;
    }
	
	// add for test --
	//sleep(10);

	// �źŵƽ���
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- �źŵ��ѽ�����\n");
	
	return nLen;
}

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
int SHMM_Read(int nChannelNo, char* pBuf, int nLen)
{
	// ��鹲���ڴ��Ƿ���ڣ��Ƿ�ɶ�д
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	// ����ͨ���ż�����SM�е�λ��
	int nStartPos = 0; 
	if ((nChannelNo >= 0) && (nChannelNo <= 14))
	{// IO��ͨ����

		nStartPos = nChannelNo * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP1_CHANNEL_NO_END))
	{// ���1��1��bat��ͨ����

		nStartPos = (nChannelNo - 100 + BAT_GROUP_CHANNEL_NUM * 0 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP2_CHANNEL_NO_END))
	{// ���1��2��bat��ͨ����

		nStartPos = (nChannelNo - 130 + BAT_GROUP_CHANNEL_NUM * 1 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP1_CHANNEL_NO_END))
	{// ���2��1��bat��ͨ����

		nStartPos = (nChannelNo - 200 + BAT_GROUP_CHANNEL_NUM * 2 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP2_CHANNEL_NO_END))
	{// ���2��2��bat��ͨ����

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if (nChannelNo == SLOT_BOARD_SELFCHECK_INFO)
	{// ���1��2�ϵĲ忨�Լ���Ϣͨ����

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else
	{// �Ƿ�ͨ����

		return SHMM_ERR_CHANNEL;
	}
	//-- end of " if ((nChannelNo >= 0) && (nChannelNo <= 14)) " --

	// check nLen
	if (nChannelNo == 0)
	{// DI 

		if (nLen > 2 * sizeof(char))
		{
			return SHMM_ERR_LEN_MAX;
		}
	}
	else 
	{// AI or bat

		if (nLen > SHMM_BUFSZ)
		{
			return SHMM_ERR_LEN_MAX;
		}
		
		if ((nStartPos + nLen) > SHMM_BUFSZ)
		{
			return SHMM_ERR_POINT;
		}
	}
	//-- end of " if (nChannelNo == 0) " --

	//printf("read -- �źŵƽ�������\n");

	// �źŵƼ���
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- �źŵ��Ѽ�����\n");
	
    char *shmbuf; // Address in process 
	
    // Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0) 
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
        //perror("shmat() error:");
        return SHMM_ERR_SHMAT;
    }
    
	// read data from shm
	memcpy(pBuf, shmbuf + nStartPos, nLen);
	
	// Detach the segment 
	if (shmdt(shmbuf) < 0)
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmdt() error:");
		return SHMM_ERR_SHMDT;
    }
	
	// �źŵƽ���
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- �źŵ��ѽ�����\n");
	
	return nLen;
}

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
int SHMM_Close()
{
	// ��鹲���ڴ��Ƿ���ڣ��Ƿ�ɶ�д
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	// �رչ����ڴ�
	int iResult = shmctl(g_iShmmId, IPC_RMID, NULL);
	if (iResult < 0)
	{
		//perror("shmctl() error: ");
		return SHMM_ERR_SHMCTL;
	}
	
	/////////////////////////////////////////////////////////////

	// �ͷ��źŵ�
    if((semctl(g_iSemid, 0, IPC_RMID)) < 0) 
	{
        //perror("semctl IPC_RMID error:");
        return SHMM_ERR_SEMCTL;
    } 
	
	return SHMM_ERR_OK;
}

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
int SHMM_SetReadedStatus()
{
	// ��鹲���ڴ��Ƿ���ڣ��Ƿ�ɶ�д
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	//printf("write -- �źŵƽ�������\n");

	// �źŵƼ���
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- �źŵ��Ѽ�����\n");
	
    char *shmbuf;		// Address in process 
    
	// Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0)
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmat() ERROR:");
		return SHMM_ERR_SHMAT;
    }
    
	// ���������Ѷ���ʶ�����ǽ�����DIͨ���澯״̬��ʶ��Ϊ0

	// ����DIͨ���ĸ澯״ֵ̬��1:�и澯��0:�޸澯
	char cDiWarnStatus = 0;       
	memcpy(shmbuf + sizeof(char), &cDiWarnStatus, sizeof(cDiWarnStatus));
	
	// Detach the segment 
    if (shmdt(shmbuf) < 0)
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmdt() error:");
		return SHMM_ERR_SHMDT;
    }
	
	// add for test --
	//sleep(10);

	// �źŵƽ���
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- �źŵ��ѽ�����\n");
	
	return SHMM_ERR_OK;
}

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
int SHMM_GetWarnStatus(char* pDiWarnStatus)
{
	// ��鹲���ڴ��Ƿ���ڣ��Ƿ�ɶ�д
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	//printf("read -- �źŵƽ�������\n");

	// �źŵƼ���
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- �źŵ��Ѽ�����\n");
	
    char *shmbuf; // Address in process 
	
    // Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0) 
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
        //perror("shmat() error:");
        return SHMM_ERR_SHMAT;
    }
    
	// ��ȡ����DIͨ���ĸ澯��ʶֵ
	memcpy(pDiWarnStatus, shmbuf + sizeof(char), sizeof(char));
	
	// Detach the segment 
	if (shmdt(shmbuf) < 0)
	{
		// �źŵƽ���
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmdt() error:");
		return SHMM_ERR_SHMDT;
    }
	
	// �źŵƽ���
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- �źŵ��ѽ�����\n");
	
	return SHMM_ERR_OK;
}

