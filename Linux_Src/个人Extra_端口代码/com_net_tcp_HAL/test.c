#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>

#include "commsock.h"

//#define TEST_READ
#define TEST_WRITE

char *ipPortParams = "10.2.10.146:8888";
unsigned long dIp;
int port;

void  TestServer()
{
	int pErrCode;
	HANDLE handle = TCPIP_CommOpen("/dev/eth0", ipPortParams, 0, 4000, &pErrCode);
	if(pErrCode != 0)
		printf("打印 端口打开 返回pErrCode=%x\n", pErrCode);	
	if(handle == NULL) return;
	HANDLE acceptHandle = TCPIP_CommAccept( handle );  //传入端口参数配置包  结构体
	char *pBuffer = (char *)malloc(800);
	if(acceptHandle != NULL){
#ifdef  TEST_READ
		int readSize = TCPIP_CommRead(acceptHandle, pBuffer, 800);
		printf("1-读取的数据大小readSize =%d\n", readSize);
#endif	
#ifdef  TEST_WRITE	
		pBuffer = "fang write data!";
		int writeSize = TCPIP_CommWrite(acceptHandle, pBuffer,	strlen(pBuffer));
		printf("1-写入的数据大小readSize =%d\n", writeSize);
#endif		
	}
	printf("main>> 进入sleep-1 !\n");
	sleep(5);
	printf("main>> 进入sleep-1 结束!\n");
	if(acceptHandle != NULL){
#ifdef  TEST_READ		
		int readSize = TCPIP_CommRead(acceptHandle, pBuffer, 800);
		printf("2-读取的数据大小readSize =%d\n", readSize);
#endif	
#ifdef  TEST_WRITE			
		pBuffer = "fang write data -2 !";
		int writeSize = TCPIP_CommWrite(acceptHandle, pBuffer,	strlen(pBuffer));
		printf("2-写入的数据大小readSize =%d\n", writeSize);
#endif		
	}
	TCPIP_CommClose( acceptHandle );  //服务器 模式 最好还是逐个关闭连接client sockt避免内存泄漏
	TCPIP_CommClose( handle );
}

void TestClient()
{
	int pErrCode;
	HANDLE handle = TCPIP_CommOpen("/dev/eth0", ipPortParams, 1, 4000, &pErrCode);
	if(pErrCode != 0)
		printf("打印 端口打开 返回pErrCode=%x\n", pErrCode);
	if(handle == NULL) return;
	char *pBuffer = (char *)malloc(800);
#ifdef  TEST_READ	
	int readSize = TCPIP_CommRead(handle, pBuffer, 800);
	printf("1-读取的数据大小readSize =%d\n", readSize);
	printf("main>>TestClient 进入sleep-1 !\n");
	sleep(5);
	printf("main>>TestClient 进入sleep-1 结束!\n");
	readSize = TCPIP_CommRead(handle, pBuffer, 800);
	printf("2-读取的数据大小readSize =%d\n", readSize);
#endif	
#ifdef  TEST_WRITE	
	pBuffer = "fang write data!";
	int writeSize = TCPIP_CommWrite(handle, pBuffer,	strlen(pBuffer));
	printf("1-写入的数据大小readSize =%d\n", writeSize);
	printf("main>>TestClient 进入sleep-1 !\n");
	sleep(5);
	printf("main>>TestClient 进入sleep-1 结束!\n");
	pBuffer = "fang write data -2 !";
	writeSize = TCPIP_CommWrite(handle, pBuffer,	strlen(pBuffer));
	printf("2-写入的数据大小readSize =%d\n", writeSize);
#endif		
	TCPIP_CommClose( handle );
}

int main(int argc,char **argv)
{
	printf("into main ！\n");
	

	TCPIP_ParseOpenParams(ipPortParams, &dIp, &port);
	
	//int sockfd = CreateNetworkServer(SOCK_STREAM, dIp, port );
	//int sockfd2 = ConnectNetworkServer(SOCK_STREAM, dIp, port, 2000 );

	//TestServer();
	TestClient();
    
	printf("main>> 进入sleep-2 !\n");
	sleep(8);
	printf("into main end ! \n");
	
	return 0;
}