
//****************************************************************************
//* 文件名: IOLAN.cpp
//*
//* 用途:
//*     主文件，提供应用程序的入口和出口
//*
//* Share Memory + IOLAN.cpp ＋ V000 ＋ B000 ＋ D001
//*
//****************************************************************************

#include "basetypes.h"

#include "ShareMem.h"


//************************************************************
// 函数: main
//
// 说明:
//   主程序入口
//    
// 参数：   
//	 argc     命令行参数个数
//   argv[]   命令行参数列表
//
// 返回值: 
//	 0:    正常退出
//   其它: 异常退出
//	
//************************************************************
int main(int argc, char* argv[])
{
	// 检查命令行参数
	if ((argc == 2) && (strcmp(argv[1], "-t") == 0))
	{// 显示软件的最新编译时间

		printf("本软件的最新编译时间为：2006.07.01 - 15:00 \n");
		return 0;
	}
	else if ((argc == 2) && (strcmp(argv[1], "-w") == 0))
	{// 写入数据

		// 显示系统当前的ipcs
		//system("ipcs  -m -s");

		// 创建共享内存
		key_t keyShmm = SHMM_Open(1); // Segment ID 
		
		if (keyShmm < 0)
		{
			printf("创建共享内存失败！错误码为：%d \n", keyShmm);
			return 1;
		}
		else
		{
			printf("创建共享内存 %d 成功！\n", keyShmm);
		}
		
		// 显示系统当前的ipcs
		//system("ipcs  -m -s");

		SHMM_RECORD_INFO shmmRecordInfo; 
		shmmRecordInfo.cDiWarnStatus = 0x1F;
		shmmRecordInfo.cValueDi      = 0x0F;

		// 写入数据
		int iErrCode = SHMM_Write(0, &shmmRecordInfo, 2 * sizeof(char));
		if (iErrCode < 0)
		{
			printf("向共享内存写入数据失败，错误码为 %d ！\n", iErrCode);
			return 1;
		}

		sleep(10);

		// 关闭共享内存
		if (SHMM_Close() != 0)
		{
			printf("关闭共享内存 %d 失败！\n", keyShmm);
			return 1;
		}
		
		// 显示系统当前的ipcs
		//system("ipcs  -m -s");
	}
	else if ((argc == 2) && (strcmp(argv[1], "-r") == 0))
	{// 读取数据

		// 打开共享内存
		key_t keyShmm = SHMM_Open(0); // Segment ID 
		
		if (keyShmm < 0)
		{
			printf("打开共享内存失败！错误码为：%d \n", keyShmm);
			return 1;
		}
		else
		{
			printf("打开共享内存 %d 成功！\n", keyShmm);
		}

		SHMM_RECORD_INFO shmmRecordInfo; 
		memset(&shmmRecordInfo, '\0', sizeof(SHMM_RECORD_INFO));

		int iErrCode = SHMM_Read(0, &shmmRecordInfo, 2 * sizeof(char));
		if (iErrCode < 0)
		{
			printf("从共享内存读取数据失败，错误码为 %d ！\n", iErrCode);
			return 1;
		}

		printf("读取到的DI数据为：0x%x 0x%x ",
			shmmRecordInfo.cDiWarnStatus, shmmRecordInfo.cValueDi);
	}
	else
	{
		printf("命令行参数不对！\n");
		printf(" -t 显示本软件的编译时间\n");
		printf(" -w 向共享内存写入数据的例程\n");
		printf(" -r 从共享内存读取数据的例程\n");

		return 1;
	}
	//-- end of " if ((argc == 2) && (strcmp(argv[1], "-t") == 0)) " --

 
	return 0;
}

