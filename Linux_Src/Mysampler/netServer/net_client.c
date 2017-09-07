#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<pthread.h>

#include "net_msgHead.h"
#include "net_Buf_RS.h"

#define ServerIP "127.0.0.1"
#define Port      9090


//客户端  by to 1 server
//log 文件打印
void LogFile_Client(char *recv, int recvLenth, char *send, int sendLenth)
{
			//打开日志文件
		FILE *file = fopen("./netLog.log", "a+");
			//写入内容
			char str[20];
			sprintf(str, "recv:");
			fprintf(file, str);
			int k = 0;
			for(k=0;k<recvLenth;k++)
			{
				memset(str,'\0', 20);;
				sprintf(str, "%02X ", (unsigned char)recv[k]);
				fprintf(file, str);
			}
			fprintf(file, "\n");	

			sprintf(str, "send:");
			fprintf(file, str);
			for(k=0;k<sendLenth;k++)
			{
				memset(str,'\0', 20);;
				sprintf(str, "%02X ", (unsigned char)send[k]);
				fprintf(file, str);
			}
			fprintf(file, "\n");			
			//关闭文件
		fclose(file);
}
//send data
void *fuct_send_Client(void *arg,char *send_buf,int send_lenth)
{
	int *new_sock = (void *)arg;
	printf("net_client->fuct_send>>into\n");
	ssize_t ret3 = send(*new_sock, send_buf, send_lenth, 0); //发送数据
	if(ret3 == -1)
	{
		perror("the send");
		return NULL;
	}
	printf("net_client->fuct_send>>end\n");
	
	return NULL;
}

//recv date  //此函数为：接收应答式
void *Client_rcv_and_send(void *arg)
{
	int *new_sock = (void *)arg;
	
	char msg_get[NetMsg_HeadLen];
	char msgBuf[NetMsg_HeadLen+MaxSignalNum*20];
	//int msg_lenth = 0;
	
	  //打印输out提示
	printf("net_client->fuct_rcv into!\n");

	while(1)
	{
		memset(msg_get, '\0', NetMsg_HeadLen);
		memset(msgBuf, '\0', NetMsg_HeadLen+MaxSignalNum*20);

		
		printf("will recv \n");
		//接收包头
		ssize_t ret1 = recv(*new_sock, msg_get, NetMsg_HeadLen, 0); //先获取包头		
		if(ret1 == -1)
		{
			perror("net_client->fuct_rcv>>recv");
			return NULL;
		}else if(ret1==0)    //这里一定得处理recv 返回值为0 即通信端中断 然后不在读写该accept到的socket 否则会进程退出！
		{
			printf("net_client->fuct_rcv>>the other socket break\n");
         	return NULL;
		}
		if( (msg_get[0]!= Start)||(msg_get[29]!= End) )
		{
			printf("net_client->fuct_rcv>>net msg start!=1\n");
            return NULL;
		}else if(msg_get[2] == 100){
			
		/*	//心跳包 数据不做处理
			ssize_t ret4 = send(*new_sock, msg_get, NetMsg_HeadLen, 0); //发送心跳数据
			if(ret4 == -1)
			{
				perror("the send heart");
				return NULL;
			}
		*/
			continue;
			
		}
		printf("recv buf=%02X %02X %02X %02X %02X %02X %02X %02X %02X ...%02X\n", (unsigned char)msg_get[0],
		          (unsigned char)msg_get[1],(unsigned char)msg_get[2],(unsigned char)msg_get[3],(unsigned char)msg_get[4],
				  (unsigned char)msg_get[5],(unsigned char)msg_get[6],(unsigned char)msg_get[7],(unsigned char)msg_get[8],
				  (unsigned char)msg_get[29]);
		/*    不接收 包体
		//接收包体的信息
		if(msg_lenth>0)
		{
		        ssize_t ret2 = recv(*new_sock, msg_body, msg_lenth, 0); //先获取msg_body
		     	if(ret2 == -1)
			   {
					perror("net_client->fuct_rcv>>recv");
					pthread_exit(0);
			    }else if(ret2==0)    //这里一定得处理recv 返回值为0 即通信端中断 然后不在读写该accept到的socket 否则会进程退出！
			   {
					printf("net_client->fuct_rcv>>the other socket break\n");
         			pthread_exit(0);
			   }
			printf("net_client->fuct_rcv>>into recv body >>buff_body=%s\n",buff_body);
		}
		*/
		//解析并 发送回包
		int len = getBuf_and_setbuf(msg_get, msgBuf);
		ssize_t ret3 = send(*new_sock, msgBuf, len, 0); //发送数据
		if(ret3 == -1)
		{
			perror("the send");
			return NULL;
		}
		printf("net_client->fuct_send>>end\n");
		//fuct_send_Client(new_sock,msgBuf,len);
		printf("fuct_send buf=%02X %02X %02X %02X %02X %02X %02X %02X %02X ...%02X\n", (unsigned char)msgBuf[0],
		          (unsigned char)msgBuf[1],(unsigned char)msgBuf[2],(unsigned char)msgBuf[3],(unsigned char)msgBuf[4],
				  (unsigned char)msgBuf[5],(unsigned char)msgBuf[6],(unsigned char)msgBuf[7],(unsigned char)msgBuf[8],
				  (unsigned char)msgBuf[29]);
				  
		//打印收发包log 文件
	//	LogFile_Client(msg_get, NetMsg_HeadLen, msgBuf, len);

	}
	return NULL;
}

//主线程发送数据
void *net_client(void *arg)
{
	printf("into net_client!\n");
	while(1)
	{
		//创建套接字文件
		int sockfd = socket(AF_INET,SOCK_STREAM,0);
		if(sockfd == -1)
		{
			perror("the socket");
			return NULL;
		}
			//设置网络超时
		struct timeval timeOut;
		timeOut.tv_sec  = 10;        //超时10s
		timeOut.tv_usec = 0;         //+超时30us
		setsockopt(sockfd, SOL_SOCKET, SO_SNDTIMEO, &timeOut, sizeof(timeOut));
		setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &timeOut, sizeof(timeOut));
	//	setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &timeOut, sizeof(timeOut));
		
		//本机地址ip和端口设置
			//定义internet协议地址结构体 变量
		struct sockaddr_in to_addr;
			//清空协议地址变量
		memset(&to_addr, 0, sizeof(to_addr));
			//填充地址信息
		to_addr.sin_family = AF_INET;  //地址族
		to_addr.sin_port = htons(Port);		//端口
		to_addr.sin_addr.s_addr = inet_addr(ServerIP);	//ipv4地址
		//等待连接服务端端成功

		printf("will connect... \n");
				//将该变量强制转换为struct sockaddr类型在函数中使用
		int ret1 = connect(sockfd, (struct sockaddr *)(&to_addr), sizeof(to_addr));
		if(ret1 == -1)
		{
			perror("the connect");
			sleep(1);
			continue;
		}
		
		//新建子线程 -- 网络收发通信
			//新建子线程名变量
		pthread_t thread1;
		int pth1 = pthread_create(&thread1, NULL, Client_rcv_and_send, &sockfd);
		if(pth1 != 0)
		{
			perror("create thread1");
			continue;
		}
	
		pthread_join(thread1, NULL);  //如果在这里等待子线程退出，那么该端就只能同时处理一个socket的对话
		printf("pthread end!\n");
		close(sockfd);//关闭socket
	}
	
	printf("into net_client end!\n");
	
}
