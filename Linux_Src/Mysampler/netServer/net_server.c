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


//服务端  by to 1 server
//log 文件打印
void LogFile_Server(char *recv, int recvLenth, char *send, int sendLenth)
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
void *fuct_send_Server(void *arg,char *send_buf,int send_lenth)
{
	int *new_sock = (void *)arg;
	printf("net_server->fuct_send>>into\n");
	ssize_t ret3 = send(*new_sock, send_buf, send_lenth, 0); //发送数据
	if(ret3 == -1)
	{
		perror("the send");
		return NULL;
	}
	printf("net_server->fuct_send>>end\n");
	
	return NULL;
}

//recv date  //此函数为：接收应答式
void *Server_rcv_and_send(void *arg)
{
	int *new_sock = (void *)arg;
	
	char msg_get[NetMsg_HeadLen];
	char msgBuf[NetMsg_HeadLen+MaxSignalNum*20];
	//int msg_lenth = 0;
	
	  //打印输out提示
	printf("net_server->fuct_rcv into!\n");

	while(1)
	{
		memset(msg_get, '\0', NetMsg_HeadLen);
		memset(msgBuf, '\0', NetMsg_HeadLen+MaxSignalNum*20);

		
		printf("will recv \n");
		//接收包头
		ssize_t ret1 = recv(*new_sock, msg_get, NetMsg_HeadLen, 0); //先获取包头		
		if(ret1 == -1)
		{
			perror("net_server->fuct_rcv>>recv");
			return NULL;
		}else if(ret1==0)    //这里一定得处理recv 返回值为0 即通信端中断 然后不在读写该accept到的socket 否则会进程退出！
		{
			printf("net_server->fuct_rcv>>the other socket break\n");
         	return NULL;
		}
		if( (msg_get[0]!= Start)||(msg_get[29]!= End) )
		{
			printf("net_server->fuct_rcv>>net msg start!=1\n");
            return NULL;
		}
		printf("recv buf=%02X %02X %02X %02X %02X %02X %02X %02X %02X ...%02X\n", (unsigned char)msg_get[0],
		          (unsigned char)msg_get[1],(unsigned char)msg_get[2],(unsigned char)msg_get[3],(unsigned char)msg_get[4],
				  (unsigned char)msg_get[5],(unsigned char)msg_get[6],(unsigned char)msg_get[7],(unsigned char)msg_get[8],
				  (unsigned char)msg_get[29]);
		/*   服务端  不接收 包体
		//接收包体的信息
		if(msg_lenth>0)
		{
		        ssize_t ret2 = recv(*new_sock, msg_body, msg_lenth, 0); //先获取msg_body
		     	if(ret2 == -1)
			   {
					perror("net_server->fuct_rcv>>recv");
					pthread_exit(0);
			    }else if(ret2==0)    //这里一定得处理recv 返回值为0 即通信端中断 然后不在读写该accept到的socket 否则会进程退出！
			   {
					printf("net_server->fuct_rcv>>the other socket break\n");
         			pthread_exit(0);
			   }
			printf("net_server->fuct_rcv>>into recv body >>buff_body=%s\n",buff_body);
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
		printf("net_server->fuct_send>>end\n");
		//fuct_send_Server(new_sock,msgBuf,len);
		printf("fuct_send buf=%02X %02X %02X %02X %02X %02X %02X %02X %02X ...%02X\n", (unsigned char)msgBuf[0],
		          (unsigned char)msgBuf[1],(unsigned char)msgBuf[2],(unsigned char)msgBuf[3],(unsigned char)msgBuf[4],
				  (unsigned char)msgBuf[5],(unsigned char)msgBuf[6],(unsigned char)msgBuf[7],(unsigned char)msgBuf[8],
				  (unsigned char)msgBuf[29]);
				  
		//打印收发包log 文件
	//	LogFile_Server(msg_get, NetMsg_HeadLen, msgBuf, len);

	}
	return NULL;
}

//主线程发送数据
void *net_server(void *arg)
{
	//创建套接字文件
	int sockfd = socket(AF_INET,SOCK_STREAM,0);
	if(sockfd == -1)
	{
		perror("the socket");
		return NULL;
	}
	
	//设置ip 端口可重用
	int enable = 1;
	setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR,  &enable, sizeof(int));
	//设置 收发超时
//	struct timeval timeOut;
//	timeOut.tv_sec  = 60;        //超时60s
//	timeOut.tv_usec = 0;         //+超时30us
//	setsockopt(sockfd, SOL_SOCKET, SO_SNDTIMEO, &timeOut, sizeof(timeOut));
//	setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &timeOut, sizeof(timeOut));
	
	//本机地址ip和端口设置
		//定义internet协议地址结构体 变量
	struct sockaddr_in my_addr;
		//清空协议地址变量
	memset(&my_addr, 0, sizeof(my_addr));
		//填充地址信息
	my_addr.sin_family = AF_INET;  //地址族
	my_addr.sin_port = htons(Port);		//端口
	my_addr.sin_addr.s_addr = inet_addr(ServerIP);	//ipv4地址
		//将该变量强制转换为struct sockaddr类型在函数中使用
	int ret1 = bind(sockfd, (struct sockaddr *)(&my_addr), sizeof(my_addr));
	if(ret1 == -1)
	{
		perror("the bind");
		close(sockfd);
		return NULL;
	}
	
	//设置监听端口
	int ret2 = listen(sockfd, 5);  //允许5个socket管道的同时连接
	if(ret2 == -1)
	{
		perror("the listen");
		close(sockfd);
		return NULL;
	}
	
       //接受tcp连接
	while(1)   //服务器 一值处于监听状态 连接个数不限 但受限于listen个数的最大值
	{             // 如果下面for(i:10){下面内容} pthread_join 就可以限定同时最多与10个socket对话且 服务的一直运行着
		printf("will accept... \n");
			//定义internet协议地址结构体 变量
			struct sockaddr_in caddr;
			int size = sizeof(struct sockaddr);  //定义struct sockaddr大小的变量（不可省略才可给一下函数取地址）
		int newsockfd = accept(sockfd, (struct sockaddr *)(&caddr), &size); //wait accept
		if(newsockfd == -1)
		{
			perror("the accept");
			close(sockfd);
			return NULL;
		}
	
		//新建子线程
			//新建子线程名变量
		pthread_t thread1;
		int pth1 = pthread_create(&thread1, NULL, Server_rcv_and_send, &newsockfd);
		if(pth1 != 0)
		{
			perror("create thread1");
			close(sockfd);
			return NULL;
		}
	
		pthread_join(thread1, NULL);  //如果在这里等待子线程退出，那么该服务器端就只能同时处理一个socket的对话
		close(newsockfd);
		printf("pthread end!\n");
	}

	//关闭套接字
	close(sockfd);
	printf("has close socket!\n");
	
}
