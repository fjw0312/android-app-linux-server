#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>

#include "commsock.h"

//#define TEST_READ
#define TEST_WRITE

//char *ipPortParams = "10.2.10.146:8888";
char *ipPortParams = "127.0.0.1:8888";
unsigned long dIp;
int port;

void  TestServer()
{
	int pErrCode;
	HANDLE handle = TCPIP_CommOpen("/dev/eth0", ipPortParams, 0, 4000, &pErrCode);
	if(pErrCode != 0)
		printf("打印 端口打开 返回pErrCode=%x\n", pErrCode);	
	if(handle == NULL) return;
	char *pBuffer = (char *)malloc(800);
	while(1){
		
		HANDLE acceptHandle = TCPIP_CommAccept( handle );  //传入端口参数配置包  结构体	
		if(acceptHandle != NULL){
			printf("获取到网络客户端连接 XD\n");
			//sleep(2);
			int readSize = TCPIP_CommRead(acceptHandle, pBuffer, 800);
			printf("1-读取的数据大小readSize =%d\n pBuffer=%s\n", readSize,pBuffer);

			memset(pBuffer, 0, 800);
			stpcpy(pBuffer,"fangServer write data!");
			int writeSize = TCPIP_CommWrite(acceptHandle, pBuffer,	strlen(pBuffer));
			printf("1-写入的数据大小writeSize =%d\n  \n", writeSize);
			
			TCPIP_CommClose( acceptHandle );  //服务器 模式 最好还是逐个关闭连接client sockt避免内存泄漏
		}
		
	}

	
	TCPIP_CommClose( handle );
}

void TestClient()
{
	int pErrCode;
	HANDLE handle = TCPIP_CommOpen("/dev/eth0", ipPortParams, 1, 4000, &pErrCode);
	if(pErrCode != 0)
		printf("打印 端口打开 返回pErrCode=%x\n", pErrCode);
	if(handle == NULL) return;
	printf("已连接到服务端 XD\n");
	
	char *pBuffer = (char *)malloc(800);
	stpcpy(pBuffer,"fangClient Write data!");
	int writeSize = TCPIP_CommWrite(handle, pBuffer,	strlen(pBuffer));
	printf("1-写入的数据大小writeSize =%d\n", writeSize);

	memset(pBuffer, 0, 800);
	int readSize = TCPIP_CommRead(handle, pBuffer, 800);
	printf("1-读取的数据大小readSize =%d\n  pBuffer=%s\n  \n", readSize,pBuffer);

	sleep(5);
	TCPIP_CommClose( handle );
}

int main(int argc,char **argv)
{
	printf("into main ！\n");
	//TCPIP_ParseOpenParams(ipPortParams, &dIp, &port);
	//int sockfd = CreateNetworkServer(SOCK_STREAM, dIp, port );
	//int sockfd2 = ConnectNetworkServer(SOCK_STREAM, dIp, port, 2000 );

	if(argc<2){
		printf("输入参数 缺失！-- '-T' or '-S'\n");
		return -1;
	}else{
		if(strstr(argv[1], "-T")){
			TestClient();
		}else if(strstr(argv[1], "-S")){
			TestServer();
		}else{
			printf("请输入正确的参数！-- '-T' or '-S'\n");
			return -1;
		}
	}
	
	sleep(2);
	printf("into main end ! \n");
	
	return 0;
}