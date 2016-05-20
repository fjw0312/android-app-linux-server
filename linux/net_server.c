#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<pthread.h>

#include "common.h"
#include "Model.h"
#include "kernel_list.h"
#include "equip.h"
#include "signal.h"

//服务端  by to 1 server
void *fuct_send(void *arg,char *send_buf,int send_lenth);

//deal net data send and recv   先收后发！
void deal(void *arg,char *head, char *body)
{
	//解析设备id
	int equipId=0,flag=0;
	int i=0;
	for(i=0;i<4;i++)
		flag |= (int)(head[8-i]&0xff)<<(8*i);
	for(i=0;i<4;i++)
		equipId |= (int)(head[12-i]&0xff)<<(8*i);

	printf("net_server->deal>>equipId=%d   flag=%d\n",equipId,flag);
	if(flag==0)   //该数据类型为服务器发出数据，故获取到该数据，不保存，不回应
	{
//		parse_msg(head,body); //解析保存设备数据
//		char buf[25]={0};  //建立无效包头
//		fuct_send(arg, buf, 25);  //发送无效包
		char *buf = fill_msg(flag,equipId);
		fuct_send(arg,buf,1024*100+25);  //发送有效回包数据
	}else if(flag==1) //该数据类型是客户端往服务端发送，只保存，不回应
	{
		parse_msg(head,body); //解析保存设备数据
//		char buf[25]={0};  //建立无效包头
//		fuct_send(arg, buf, 25);  //发送无效包
		char *buf = fill_msg(flag,equipId);
		fuct_send(arg,buf,1024*100+25);  //发送有效回包数据
	}else if(flag==2) //该数据类型是客户端的数据请求，  不保存，只回应
	{
//		parse_msg(head,body); //解析保存设备数据
//		char buf[25]={0};  //建立无效包头
//		fuct_send(arg, buf, 25);  //发送无效包
		//获取设备数据包
		char *buf = fill_msg(0,equipId);
		fuct_send(arg,buf,1024*100+25);  //发送有效回包数据
		
	}else if(flag==3) //该数据类型是客户端的控制请求，  只保存，不回应
	{
//		parse_msg(head,body); //解析保存设备数据
//		char buf[25]={0};  //建立无效包头
//		fuct_send(arg, buf, 25);  //发送无效包
		char *buf = fill_msg(flag,equipId);
		fuct_send(arg,buf,1024*100+25);  //发送有效回包数据
	}
	printf("net_server->deal>>end\n");
}



//send data
void *fuct_send(void *arg,char *send_buf,int send_lenth)
{
	int *new_sock = (void *)arg;
	printf("net_server->fuct_send>>into\n");
	ssize_t ret3 = send(*new_sock, send_buf, send_lenth, 0); //发送数据
	if(ret3 == -1)
	{
		perror("the send");
		pthread_exit(0);
	}
	printf("net_server->fuct_send>>end\n");
}

//recv date
void *fuct_rcv(void *arg)
{
	int *new_sock = (void *)arg;
	char buff_head[25];
	char buff_body[1024*100];
//	char buff[1024*100+25];
	int  body_lenth = 0;
	  //打印输out提示
	printf("net_server->fuct_rcv into!\n");
	unsigned int k=0;
		int i;
	while(1)
	{
		k++;
		memset(buff_head, '\0', 25);
		memset(buff_body, '\0', 1024*100);

		//接收包头
		ssize_t ret1 = recv(*new_sock, buff_head, 25, 0); //先获取包头
		if(ret1 == -1)
		{
			perror("net_server->fuct_rcv>>recv");
			pthread_exit(0);
		}else if(ret1==0)    //这里一定得处理recv 返回值为0 即通信端中断 然后不在读写该accept到的socket 否则会进程退出！
		{
			printf("net_server->fuct_rcv>>the other socket break\n");
         		 pthread_exit(0);
		}
		if(buff_head[0]!=1)
		{
			printf("net_server->fuct_rcv>>net msg start!=1\n");
            		pthread_exit(0);
		}
		//解析包体的长度
		body_lenth = 0;
		for(i=0;i<4;i++)
			body_lenth |= (unsigned int)(buff_head[4-i]&0xff)<<(8*i);
		printf("net_server->fuct_rcv>>body_lenth=%d\n", body_lenth);
		//接收包体的信息
		if(body_lenth>0)
		{
		        ssize_t ret2 = recv(*new_sock, buff_body, body_lenth, 0); //先获取msg_body
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

		//处理通信包并回包处理
		deal(arg,buff_head,buff_body);
	}
	pthread_exit(0);
}

//主线程发送数据
void *net_server(void *arg)
{
	//创建套接字文件
	int sockfd = socket(AF_INET,SOCK_STREAM,0);
	if(sockfd == -1)
	{
		perror("the socket");
		pthread_exit(0);
	}
	
	//本机地址ip和端口设置
		//定义internet协议地址结构体 变量
	struct sockaddr_in my_addr;
		//清空协议地址变量
	memset(&my_addr, 0, sizeof(my_addr));
		//填充地址信息
	my_addr.sin_family = AF_INET;  //地址族
	my_addr.sin_port = htons(6001);		//端口
	my_addr.sin_addr.s_addr = inet_addr("192.168.1.222");	//ipv4地址
		//将该变量强制转换为struct sockaddr类型在函数中使用
	int ret1 = bind(sockfd, (struct sockaddr *)(&my_addr), sizeof(my_addr));
	if(ret1 == -1)
	{
		perror("the bind");
		pthread_exit(0);
	}
	
	//设置监听端口
	int ret2 = listen(sockfd, 5);  //允许5个socket管道的同时连接
	if(ret2 == -1)
	{
		perror("the listen");
		pthread_exit(0);
	}
	
       //接受tcp连接
	while(1)   //服务器 一值处于监听状态 连接个数不限 但受限于listen个数的最大值
	{             // 如果下面for(i:10){下面内容} pthread_join 就可以限定同时最多与10个socket对话且 服务的一直运行着
		printf("while(1)  into!\n");
			//定义internet协议地址结构体 变量
			struct sockaddr_in caddr;
			int size = sizeof(struct sockaddr);  //定义struct sockaddr大小的变量（不可省略才可给一下函数取地址）
		int newsockfd = accept(sockfd, (struct sockaddr *)(&caddr), &size); //wait accept
		if(newsockfd == -1)
		{
			perror("the accept");
			pthread_exit(0);
		}
	
		//新建子线程
			//新建子线程名变量
		pthread_t thread1,thread2;
		int pth1 = pthread_create(&thread1, NULL, fuct_rcv, &newsockfd);
		if(pth1 != 0)
		{
			perror("create thread1");
			pthread_exit(0);
		}
	//	int pth2 = pthread_create(&thread2, NULL, fuct_send, &newsockfd);
	//	if(pth2 != 0)
	//	{
	//		perror("create thread2");
	//		exit(-1);
	//	}
	
		pthread_join(thread1, NULL);  //如果在这里等待子线程退出，那么该服务器端就只能同时处理一个socket的对话
	//	pthread_join(thread2, NULL);
		printf("pthread end!\n");
	}

	//关闭套接字
	close(sockfd);
	printf("has close socket!\n");
	
}
