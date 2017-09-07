#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include "net_Buf_RS.h"
#include "net_msgHead.h"

#include "master.h"


#define  OUT
#define  IN




unsigned int equiptNum;  //�ɼ��豸 ����
char  MyStr[20];  //��չ�����ַ�   ���յ�������

/*******************************
*         ˵����
* author:fjw0312@163.com  date:2016.12.27
* ��������� ������ 99 03 04 05 88  100(������)
*******************************/


//----------------------------���˶��� �ͻ���ʹ�õ� ���� �豸���������-------------------------------
int request_EquiptNum_Buf(OUT char *requestBuf)
{
	//�������ʽ�� 0 99 99 0 CRC mean
	char equiptId = 0;
	char msgType = 99;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	char mean[20] = "get EquiptNum=?";
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptNum_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		          (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return -1;
}
//----------------------------���˶��� �����ʹ�õ� ���� �豸�����ذ�---------------------------------
int respond_EquiptNum_Buf(OUT char *respondBuf,IN unsigned short equiptNum)
{
	//�ذ���ʽ�� 0 99 99 0 CRC mean
	char equiptId = 0;
	char msgType = 99;
	unsigned short signalAddr = equiptNum;
	unsigned short signalPara = 0;
	char mean[20];	
	sprintf(mean, "get EquiptNum=%d", equiptNum);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_EquiptNum_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		         (unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return -1;
}

//----------------------------���˶��� �ͻ���ʹ�õ� �豸�ź� �����-----------------------------------
int request_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf)
{
	//�������ʽ�� equiptId 3 0 0 CRC mean

	char msgType = 3;
	char mean[20];
	sprintf(mean, "request_E=%d", equiptId);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptSig_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		          (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	
	return -1;
}
//----------------------------���˶��� �����ʹ�õ� �豸�ź� �ذ�-------------------------------------
int respond_EquiptSig_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf)
{
	//�ذ���ʽ�� equiptId 3 0 0 CRC mean   //signalAddr = 0  signalPara=0 ���������豸������
	char msgType = 3;
	char mean[20];
	sprintf(mean, "respond_E=%d", equiptId);
	
	if((signalAddr==0) &&(signalPara == 0 )) signalPara = MaxSignalNum*20;  //floatת��Ϊchar[20]�ַ�
	if(signalAddr+signalPara/20 > MaxSignalNum) return -1;
	int n = signalPara/20;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{		
		//���� ���ݳ�����
		float dataBuff[MaxSignalNum+1] = {10,20,3,5,7,9};
		getEquiptData(equiptId, dataBuff);                   //�����ݳ� ��ȡ�豸�ź�----master
		int i = 0;
		for(i=0;i<n;i++)
		{
			float f_data = dataBuff[signalAddr+i];
			char s_data[20]={0};
			sprintf(s_data, "%f", f_data);
			int k = 0;
			for(k=0;k<20;k++){
				respondBuf[NetMsg_HeadLen+20*i +k] = s_data[k];
			}
		//	respondBuf[NetMsg_HeadLen+4*i+0] = (unsigned char)(((int)f_data>>8*3)&0xFF);
		//	respondBuf[NetMsg_HeadLen+4*i+1] = (unsigned char)(((int)f_data>>8*2)&0xFF);
		//	respondBuf[NetMsg_HeadLen+4*i+2] = (unsigned char)(((int)f_data>>8*1)&0xFF);
		//	respondBuf[NetMsg_HeadLen+4*i+3] = (unsigned char)(((int)f_data>>8*0)&0xFF);
		}
		//printf("respond_EquiptSig_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n",  
		//      	(unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
		//		 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen+10*n; //���ذ��ĳ���
	}
	return -1;
}
//----------------------------���˶��� �ͻ���ʹ�õ� �豸�������� �����-------------------------------
int request_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *requestBuf)
{
	//�������ʽ�� equiptId 4 x x  CRC mean
	char msgType = 4;
	char mean[20];
	sprintf(mean, "request_Cmd_E=%d", equiptId);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		          (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return -1;
}
//----------------------------���˶��� �����ʹ�õ� �豸�������� �ذ�---------------------------------
int respond_EquiptCmd_Buf(IN int equiptId,IN unsigned short signalAddr, IN unsigned short signalPara,OUT char *respondBuf)
{
	//�ذ���ʽ�� equiptId 4 x x  CRC mean
	char msgType = 4;
	char mean[20];
	sprintf(mean, "respond_Cmd_E=%d", equiptId);
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_EquiptCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n",  
				 (unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return -1;
}
//----------------------------���˶��� �ͻ���ʹ�õ� �豸�ַ����� �����-------------------------------
int request_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *requestBuf)
{
	//�������ʽ�� equiptId 5 0 0 CRC mean

	char msgType = 5;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_EquiptStrCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n",  
				  (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return 0;
}
//----------------------------���˶��� �����ʹ�õ� �豸�ַ����� �ذ�---------------------------------
int respond_EquiptStrCmd_Buf(IN int equiptId, IN char *mean,OUT char *respondBuf)
{
	//�ذ���ʽ�� equiptId 5 0 0 CRC mean

	char msgType = 5;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_EquiptStrCmd_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		       	(unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return 0;
}

//----------------------------���˶��� �ͻ���ʹ�õ� ��չ�����ַ���Ϣ �����---------------------------
int request_myStr_Buf(IN int equiptId,IN char *mean,OUT char *requestBuf)
{
	//�������ʽ�� 0 88 0 0 CRC mean
	char msgType = 88;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, requestBuf) == 0)
	{
		printf("request_myStr_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		    	  (unsigned char)requestBuf[1],(unsigned char)requestBuf[2],(unsigned char)requestBuf[3],(unsigned char)requestBuf[4],
				  (unsigned char)requestBuf[5],(unsigned char)requestBuf[6],(unsigned char)requestBuf[7],(unsigned char)requestBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}
	return -1;
}
//----------------------------���˶��� �����ʹ�õ� ��չ�����ַ���Ϣ �ذ�-----------------------------
int respond_myStr_Buf(IN int equiptId,IN char *mean,OUT char *respondBuf)
{
	//�ذ���ʽ�� 0 88 0 0 CRC mean
	char msgType = 88;
	unsigned short signalAddr = 0;
	unsigned short signalPara = 0;
	if( fill_BufHead(equiptId, msgType, signalAddr, signalPara, mean, respondBuf) == 0)
	{
		printf("respond_myStr_Buf=%02X %02X %02X %02X %02X %02X %02X %02X \n", 
		     	(unsigned char)respondBuf[1],(unsigned char)respondBuf[2],(unsigned char)respondBuf[3],(unsigned char)respondBuf[4],
				 (unsigned char)respondBuf[5],(unsigned char)respondBuf[6],(unsigned char)respondBuf[7],(unsigned char)respondBuf[8]);
		return NetMsg_HeadLen; //���ذ��ĳ���
	}

	return -1;
}


//----------------------------���˶��� �����ʹ�õ� �Զ� ���������Ӧ����-----------------------------
int getBuf_and_setbuf(IN char *getBuf, OUT char *setBuf)
{
	if((getBuf == NULL)||(setBuf==NULL)) return 0;
	printf("net_Buf_RS>>getBuf_and_setbuf  into!\n");
	struct netMsg *Msg = malloc(sizeof(struct netMsg ));
	BufToMsgHead(getBuf, Msg);
	int ret = 0;
	//�ж� ���������
	if(Msg->msgType == 3)           //�����豸����
	{
		ret = respond_EquiptSig_Buf(Msg->equiptId,Msg->signalAddr, Msg->signalPara,setBuf); //��ֵ�ذ� ����
	}else if(Msg->msgType == 4)     //���������
	{
		ret = respond_EquiptCmd_Buf(Msg->equiptId,Msg->signalAddr, Msg->signalPara,setBuf);  //��ֵ�ذ� ����
		char strCmd[8] ={0};
		int i = 0;
		for(i=0; i<8; i++)       //���� ʹ respond_EquiptCmd_Bufֻ��ȡǰ8byte����� �����ݳ�
		{
			strCmd[i] = getBuf[i];
		}
		//memcpy(strCmd, (char *)(getBuf+1), 8);
		setWriteCmd(Msg->equiptId, strCmd);		            //�����ݳ� ��ӿ������� ----master
	}else if(Msg->msgType == 5)     //���������ַ�
	{
		ret = respond_EquiptStrCmd_Buf(Msg->equiptId, Msg->mean,setBuf);                      //��ֵ�ذ� ����
		char strCmd[20];
		strncpy(strCmd, Msg->mean, sizeof(strCmd));
		setControlCmd(Msg->equiptId, strCmd);		       //�����ݳ� ��ӿ������� ----master
	}else if(Msg->msgType == 99)   //�����豸����
	{
		unsigned short equiptNum = get_equiptNum();        //�����ݳ� ��ȡ�豸���� ----master
		//unsigned short equiptNum = 55;
		ret = respond_EquiptNum_Buf(setBuf, equiptNum);                                                //��ֵ�ذ� ����
	}else if(Msg->msgType == 88)   //������չ�����ַ�
	{
		memset(MyStr, '\0', 20);
		strncpy(MyStr, Msg->mean, sizeof(MyStr));
		ret = respond_myStr_Buf(Msg->equiptId, Msg->mean,setBuf);                         //��ֵ�ذ� ����
	}else{
		return 0;
		Msg = NULL;
		free(Msg);
	}
	
	Msg = NULL;
	free(Msg);
	if(ret == 0)  return 0;

	
	return ret;   //����setBuf ���ֽ���
}

//----------------------------���˶��� �ͻ���ʹ�õ� �����豸�ź� ���������---------------------------
void getEquiptIdBuf(OUT int *idBuf)
{
	get_equiptIdBuf(idBuf);
}
int getBuf_EquiptSig(IN int equiptId, OUT char *setBuf)
{
	int ret = -1;
	ret = respond_EquiptSig_Buf(equiptId,0, 0,setBuf); //��ֵ�ذ� ����
	return ret;
}
int getBuf_and_setdone(IN char *getBuf)
{
	if(getBuf == NULL) return 0;
	printf("net_Buf_RS>>getBuf_and_setbuf  into!\n");
	struct netMsg *Msg = malloc(sizeof(struct netMsg ));
	BufToMsgHead(getBuf, Msg);
	int ret = 0;
	//�ж� ���������
	if(Msg->msgType == 3)           //�����豸����
	{
		//�豸���������ϱ�ͨ��  ����ʹ�÷�������ͻ�����������
	}else if(Msg->msgType == 4)     //���������
	{
		ret = 1;
		char strCmd[8] ={0};
		int i = 0;
		for(i=0; i<8; i++)       //���� ʹ respond_EquiptCmd_Bufֻ��ȡǰ8byte����� �����ݳ�
		{
			strCmd[i] = getBuf[i];
		}
		//memcpy(strCmd, (char *)(getBuf+1), 8);
		setWriteCmd(Msg->equiptId, strCmd);		            //�����ݳ� ��ӿ������� ----master
	}else if(Msg->msgType == 5)     //���������ַ�
	{
		ret = 1;
		char strCmd[20];
		strncpy(strCmd, Msg->mean, sizeof(strCmd));
		setControlCmd(Msg->equiptId, strCmd);		       //�����ݳ� ��ӿ������� ----master
	}else if(Msg->msgType == 99)   //�����豸����
	{
		//unsigned short equiptNum = get_equiptNum();        //�����ݳ� ��ȡ�豸���� ----master
		//unsigned short equiptNum = 55;	
	}else if(Msg->msgType == 88)   //������չ�����ַ�
	{
		memset(MyStr, '\0', 20);
		strncpy(MyStr, Msg->mean, sizeof(MyStr));
	}else{
		return 0;
		Msg = NULL;
		free(Msg);
	}
	
	Msg = NULL;
	free(Msg);

	
	return ret;   //����setBuf ���ֽ���
}