
//****************************************************************************
//*
//* 用途:
//*     共享内存模块的实现文件，实现多进程对共享内存的读写
//*
//* IO-LAN + SHMM ＋ V000 ＋ B000 ＋ D001
//*
//****************************************************************************

#include "ShareMem.h"

key_t g_keyShmm = -1;   // 共享内存的key
int   g_iShmmId = -1;   // 共享内存的ID
int   g_iSemid  = -1;   // 信号灯的ID



//************************************************************
// 函数: SEM_LOCK
//
// 说明:
//   信号灯加锁
//    
// 参数：   
//   g_keyShmm  ipc key 值
//
// 返回值: 
//	 0:    成功
//   其它: 错误码
//	
//************************************************************
int SEM_LOCK(key_t g_keyShmm)
{
    int nsems = 1;   // How many semaphores to create 
	
	// 信号灯加锁
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
// 函数: SEM_UNLOCK
//
// 说明:
//   信号灯解锁
//    
// 参数：   
//   g_keyShmm  ipc key 值
//
// 返回值: 
//	 0:    成功
//   其它: 错误码
//	
//************************************************************
int SEM_UNLOCK(key_t g_keyShmm)
{
	// 获取信号灯ID
    int nsems = 1;   // How many semaphores to create 
	
	// 信号灯解锁
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
// 函数: SHMM_Open
//
// 说明:
//  打开或创建共享内存
//    
// 参数：  
//   bCreate  1：创建共享内存；0：打开共享内存
//
// 返回值: 
//	 -1:    失败
//   其它： ipcs key 值
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

	// 1、创建或打开共享内存

	int iOpenFlag = 0600;
	if (bCreate == 1)
	{// 如果是创建内存

		iOpenFlag |= IPC_CREAT;
	}
	
	// 打开或创建共享内存
	g_iShmmId = shmget(g_keyShmm, SHMM_BUFSZ, iOpenFlag);
	if (g_iShmmId < 0)
	{
		//perror("shmget() error:");
		return SHMM_ERR_SHMGET;
	}
	
	////////////////////////////////////////////////////////////
	
	// 2、创建或打开信号灯
    int           nsems = 1;   // How many semaphores to create 
    
    // Create the semaphore with world read-alter perms 
    g_iSemid = semget(g_keyShmm, nsems, iOpenFlag);
    if(g_iSemid < 0) 
	{
        //perror("semget():");
        return SHMM_ERR_SEMGET;
    }
	
	if (bCreate == 1)
	{// 如果是创建信号灯
		
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

	// 3、初始化共享内存区

	if (bCreate == 1)
	{// 如果是创建共享内存，则初始化共享内存区
		
		char *shmbuf;		// Address in process 
		
		// Attach the segment 
		if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0)
		{
			// 信号灯解锁
			int iResult = SEM_UNLOCK(g_keyShmm);
			if (iResult < 0)
			{
				return iResult;
			}
			
			//perror("shmat() ERROR:");
			return SHMM_ERR_SHMAT;
		}
		
		// write data to shmm，初始化为0
		memset(shmbuf, 0, SHMM_BUFSZ);
		
		// Detach the segment 
		if (shmdt(shmbuf) < 0)
		{
			// 信号灯解锁
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
// 函数: SHMM_Write
//
// 说明:
//   向共享内存写入数据
//    
// 参数：   
//   nChannelNo  通道号，0：DI，1~14：AI
//   pBuf        储存写的数据的内存指针。
//   nLen        要写入的数据长度。对于DI，为2 char；对于AI，为1 float
//
// 返回值: 
//	 >= 0:    实际写入的数据长度
//   <  0:    错误码
//	
//************************************************************
int SHMM_Write(int nChannelNo, char* pBuf, int nLen)
{
	// 检查共享内存是否存在，是否可读写
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	// 根据通道号计算在SM中的位置
	int nStartPos = 0; 
	if ((nChannelNo >= 0) && (nChannelNo <= 14))
	{// IO板通道号

		nStartPos = nChannelNo * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP1_CHANNEL_NO_END))
	{// 插槽1第1组bat板通道号

		nStartPos = (nChannelNo - 100 + BAT_GROUP_CHANNEL_NUM * 0 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP2_CHANNEL_NO_END))
	{// 插槽1第2组bat板通道号

		nStartPos = (nChannelNo - 130 + BAT_GROUP_CHANNEL_NUM * 1 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP1_CHANNEL_NO_END))
	{// 插槽2第1组bat板通道号

		nStartPos = (nChannelNo - 200 + BAT_GROUP_CHANNEL_NUM * 2 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP2_CHANNEL_NO_END))
	{// 插槽2第2组bat板通道号

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if (nChannelNo == SLOT_BOARD_SELFCHECK_INFO)
	{// 插槽1和2上的插卡自检信息通道号

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else
	{// 非法通道号

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

	//printf("write -- 信号灯将加锁！\n");

	// 信号灯加锁
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- 信号灯已加锁！\n");
	
    char *shmbuf;		// Address in process 
    
	// Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0)
	{
		// 信号灯解锁
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
		// 信号灯解锁
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

	// 信号灯解锁
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- 信号灯已解锁！\n");
	
	return nLen;
}

//************************************************************
// 函数: SHMM_Read
//
// 说明:
//   读取共享内存的数据
//    
// 参数：   
//   nChannelNo  通道号，0：DI，1~14：AI
//   pBuf        储存读取数据的内存指针。
//   nLen        要读取的数据长度。对于DI，为2 char；对于AI，为1 float
//
// 返回值: 
//	 >= 0:    实际读取的数据长度
//   <  0:     错误码
//	
//************************************************************
int SHMM_Read(int nChannelNo, char* pBuf, int nLen)
{
	// 检查共享内存是否存在，是否可读写
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	// 根据通道号计算在SM中的位置
	int nStartPos = 0; 
	if ((nChannelNo >= 0) && (nChannelNo <= 14))
	{// IO板通道号

		nStartPos = nChannelNo * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP1_CHANNEL_NO_END))
	{// 插槽1第1组bat板通道号

		nStartPos = (nChannelNo - 100 + BAT_GROUP_CHANNEL_NUM * 0 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT1_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT1_GROUP2_CHANNEL_NO_END))
	{// 插槽1第2组bat板通道号

		nStartPos = (nChannelNo - 130 + BAT_GROUP_CHANNEL_NUM * 1 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP1_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP1_CHANNEL_NO_END))
	{// 插槽2第1组bat板通道号

		nStartPos = (nChannelNo - 200 + BAT_GROUP_CHANNEL_NUM * 2 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if ((nChannelNo >= SLOT2_GROUP2_CHANNEL_NO_START) && (nChannelNo <= SLOT2_GROUP2_CHANNEL_NO_END))
	{// 插槽2第2组bat板通道号

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else if (nChannelNo == SLOT_BOARD_SELFCHECK_INFO)
	{// 插槽1和2上的插卡自检信息通道号

		nStartPos = (nChannelNo - 230 + BAT_GROUP_CHANNEL_NUM * 3 + (IO_CHANNEL_NUM - 1)) * sizeof(float);
	}
	else
	{// 非法通道号

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

	//printf("read -- 信号灯将加锁！\n");

	// 信号灯加锁
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- 信号灯已加锁！\n");
	
    char *shmbuf; // Address in process 
	
    // Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0) 
	{
		// 信号灯解锁
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
		// 信号灯解锁
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmdt() error:");
		return SHMM_ERR_SHMDT;
    }
	
	// 信号灯解锁
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- 信号灯已解锁！\n");
	
	return nLen;
}

//************************************************************
// 函数: SHMM_Close
//
// 说明:
//   关闭共享内存
//    
// 参数：   
//   g_keyShmm  ipc key 值
//
// 返回值: 
//	 0:    成功
//   其它: 错误码
//	
//************************************************************
int SHMM_Close()
{
	// 检查共享内存是否存在，是否可读写
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	// 关闭共享内存
	int iResult = shmctl(g_iShmmId, IPC_RMID, NULL);
	if (iResult < 0)
	{
		//perror("shmctl() error: ");
		return SHMM_ERR_SHMCTL;
	}
	
	/////////////////////////////////////////////////////////////

	// 释放信号灯
    if((semctl(g_iSemid, 0, IPC_RMID)) < 0) 
	{
        //perror("semctl IPC_RMID error:");
        return SHMM_ERR_SEMCTL;
    } 
	
	return SHMM_ERR_OK;
}

//************************************************************
// 函数: SHMM_SetReadedStatus
//
// 说明:
//   设置所有DI通道数据已读的标识
//    
// 参数：   
//
// 返回值: 
//	 = 0:      设置正确
//   <  0:     错误码
//	
//************************************************************
int SHMM_SetReadedStatus()
{
	// 检查共享内存是否存在，是否可读写
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	//printf("write -- 信号灯将加锁！\n");

	// 信号灯加锁
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- 信号灯已加锁！\n");
	
    char *shmbuf;		// Address in process 
    
	// Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0)
	{
		// 信号灯解锁
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmat() ERROR:");
		return SHMM_ERR_SHMAT;
    }
    
	// 设置数据已读标识，即是将所有DI通道告警状态标识置为0

	// 所有DI通道的告警状态值；1:有告警，0:无告警
	char cDiWarnStatus = 0;       
	memcpy(shmbuf + sizeof(char), &cDiWarnStatus, sizeof(cDiWarnStatus));
	
	// Detach the segment 
    if (shmdt(shmbuf) < 0)
	{
		// 信号灯解锁
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

	// 信号灯解锁
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("write -- 信号灯已解锁！\n");
	
	return SHMM_ERR_OK;
}

//************************************************************
// 函数: SHMM_GetWarnStatus
//
// 说明:
//   获取所有DI通道的告警标识值
//    
// 参数： 
//   pDiWarnStatus    所有DI通道的告警标识值
//
// 返回值: 
//	 = 0:      正确
//   <  0:     错误码
//	
//************************************************************
int SHMM_GetWarnStatus(char* pDiWarnStatus)
{
	// 检查共享内存是否存在，是否可读写
	if ((g_keyShmm < 0) || (g_iShmmId < 0) || (g_iSemid  < 0))
	{
		return SHMM_ERR_NO_RW;
	}

	//printf("read -- 信号灯将加锁！\n");

	// 信号灯加锁
	int iResult = SEM_LOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- 信号灯已加锁！\n");
	
    char *shmbuf; // Address in process 
	
    // Attach the segment 
    if((shmbuf = shmat(g_iShmmId, NULL, 0)) < (char *)0) 
	{
		// 信号灯解锁
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
        //perror("shmat() error:");
        return SHMM_ERR_SHMAT;
    }
    
	// 读取所有DI通道的告警标识值
	memcpy(pDiWarnStatus, shmbuf + sizeof(char), sizeof(char));
	
	// Detach the segment 
	if (shmdt(shmbuf) < 0)
	{
		// 信号灯解锁
		iResult = SEM_UNLOCK(g_keyShmm);
		if (iResult < 0)
		{
			return iResult;
		}
		
		//perror("shmdt() error:");
		return SHMM_ERR_SHMDT;
    }
	
	// 信号灯解锁
	iResult = SEM_UNLOCK(g_keyShmm);
	if (iResult < 0)
	{
		return iResult;
	}

	//printf("read -- 信号灯已解锁！\n");
	
	return SHMM_ERR_OK;
}

