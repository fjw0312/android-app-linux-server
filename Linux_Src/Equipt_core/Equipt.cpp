/*--------------------------------------------
�ļ�����Equipt.cpp ǰ2��ΪFs102/XD100/XD300�ź�  2~10���ź�Ϊ�������ź� 10~156Ϊ�̶�ֵ�ź�
���ܣ�����sampler libhal �ɼ��豸���� ����
Э�����ͣ�modbus RTU ASCCII Q1 G1
date�� 2016 12 6
made by: fjw0312
��ע�� ���ļ�ʹ���ڱ���so�ļ���dll�ļ�  ����ΪЭ��ģ��  
---------------------------------------------*/
//#define _LINUX_
#ifdef _LINUX_
	#include<stdlib.h>
	#include<string.h>
    #include "local_linux.h"
    bool bTestFlag = false;  //���޸� ������������hexԭʼ���ݼ�¼�ļ�����Ҫ����ʱΪtrue��
#else
    // ����ͷ�ļ��Ķ��壬������ VC++ �� BC++����
    #include <stdlib.h>
    #include <string.h>
    #include <stdio.h>
    #include <conio.h>
    #include <math.h>

    #ifdef _WINDOWS
        #define _DLL_
    #endif

    #ifdef _CONSOLE
        #define _DLL_
        #define _DEBUG_
    #endif

    #define _OCE_        // OCE����������

    #ifdef  _DLL_
        #undef  _OCE_    // DLL����������
    #endif
    #ifdef  _TSR_
        #undef  _OCE_    // TSR����������
    #endif
    #ifdef  _OCE_
        #define  _TSR_   // TSR����������
    #endif

    // ��̬��ʹ�ò���
    #ifdef _DLL_
        #pragma comment(lib,"Ws2_32.lib")
        #include "local.h"
        #include "snuoci5.h"

        // ���ٲ��Ա�־��TURE�����ٲ������У�Ҫ������Ϣ��FALSE����ͨ���С�
        bool bTestFlag = false;
        extern int nExtOci5ID;
        // ���ٲ����õ����ݼ�¼�ļ���������ݾ���Э����ģ�
        // �����¼�澯����ʱ�Ĳɼ�ԭʼ�����뽫bWriteErrLog��λ��
        BOOL bWriteErrLog=FALSE;
        //BOOL bWriteErrLog=TRUE;
        #define HEXLOG_FILE  "YDC9310_made_FJW.HEX"    // �澯��¼�ļ�����
    #endif  //end #ifdef _DLL_

    #ifndef _OCE_
        #ifdef _TSR_
        #include "struct.h"
        #endif
    #endif

    #ifdef _OCE_
        #include "Comset.h"
        #ifndef _TSR_
            #define _TSR_
        #endif
    #endif
#endif

 // ���ٲ����õ����ݼ�¼�ļ���������ݾ���Э����ģ�
char debugPath[]="/data/mgrid/";   //���ɵ����ļ���·��
char HEX_FILE[]={"fjw_test.hex"};
char ASC_FILE[]={"fjw_test.asc"};
char ErrLog_FILE[]={"fjw_test.Log"};
char Report_FILE[]={"fjw_report.Log"};
char UPdata_FILE[]={"fjw_UPdata.Log"};




#define ReportMaxEquiptNum   20
#define ReportMaxSignalNum   801
float RdataBuf[ReportMaxEquiptNum][ReportMaxSignalNum];      //�����ϱ���  �ɼ�����  �豸���20��  �ź��������803��
char  SstrBuf[ReportMaxSignalNum] = {'$'};                           //�����ϱ���  �ɼ�����

float buf[10]={0,1,2,3,4,5,6,7,8,9};

const int bySOI = 0x28;           // ͷ�ַ����� "("
const int byEOI = 0x0D;           // �����ַ�
const int nMaxChanelNo = 400;    // ���ɼ��źŸ���
const int nMaxDllBufNum = 400;   // DLL�����ջ���
#define nMaxBufLen	512         //���ջ��������ֽ�����

#define nMaxControlNo   0      // �����������
#define nMinControlNo   0

//ѡ���Ƿ���modbus  Э�飬�궨��
#define  MODBUS_RTU
//#define  MODBUS_ASCII
//#define  Q_G

// ���ݽṹ���壺
//      Ҫ������ÿ���������ݰ������ݶ�������
//      ���ڽṹ��ͬ��Ӧ�����ظ����壬���������ѭ�������Դģ�������

// TODO: �뽫���ݴ����ֵı�ṹ�����ݶ����ڴ�
//       Ҫ��ṹ�����ݺ����ע��
// ���ݰ������ṹ
typedef struct
{
    int nType;          // ��������:0-������־,10-���ֽڿ�����,11-���ֽ�ģ����...
    int nOffset;        // ������ʼ��ַ���ڱ����ݰ��е���ʼλ�á�
    int nScaleBit;      // ���ڿ�����,��λ�ţ�ģ������������ϵ,��10���ȡ�
    int nChannel;       // ϵͳ�����ͨ���ţ�����ģ��Ĳ���������ƫ�Ʊ�ʾ��Ҳ��ֱ��ָ����

}DATASTRUCT;

//ϵͳ����
// 
#define nR_DataStart0 0
DATASTRUCT arR_Data0[] =
{
    {21,  0,    100,  0},  // fs102 �¶�
    {21,  2,    100,  1},  //  fs102 ʪ��
    {21,  4,    1,  2},  // 
    {21,  6,    1,  3},  // 
    {21,  8,    1,  4},  //
    {21,  10,    1,  5},  // 
    {21,  12,    1,  6},  // 
    {21,  14,    1,  7},  // 
    {21,  16,    1,  8},  //  
    {21,  18,    1,  9},  // 
};


typedef struct strChannelInfo {
    unsigned int nMeasureStart;
    DATASTRUCT *pMeasure;
}CHANNELINFO;

CHANNELINFO strChnlInfo[] = {
	{nR_DataStart0, arR_Data0},
 //   {nR_DataStart1, arR_Data1},
//    {nR_DataStart2, arR_Data2},
 //   {nR_DataStart3, arR_Data3},

};

//��������
//�����    ����
// 11        ��������Сֵ����(0-��ֹ,1-����)
// 12        �����Сֵ��λ
// 13        �����ۼ�ֵ��λ
// 14        ��������������λ
// 15		 ϵͳ���߷�ʽ(0-3��4��,1-1��2��,2-3��3��,3-3��3��ƽ��,4-1��3��,5-3��4��ƽ��)
// 16		 ����������ȡ��ʱ��(����)
// 17        ��������������ʱ��(0��15����,1-30����)
// 18		 ������(0-����,1-��)
// 19		 ����(0-����,1-Ӣ��)
// 20		 PT
// 21		 CT



//
// ������TSR��OCEʱ�������Ľ��ջ������ϴ�ʱ�뾡����ȫ�ֱ�������Ϊ���ǵĶ�ջ
// ��С��Ϊ֧�ֶ��߳����룬��̬������þֲ���������ȷʵ��Ҫ����д������ȫ�ֱ�
// �������ֲ߳̾��洢����Local.H�ṹCGlobalInfo������һ��Ԫ�ء�




// �汾��Ϣ�������޸ĺ�Ӧ���䵽�����У��Ա���Ӧ�ó���ɲ��ġ�
// �� TSR �� DLL ʹ�á�
//��������Ϊ��DLLTEST����ʱ��ʾ���豸��Ҫ��Ϣ
char Info[] = {
    "������������������\n"
        "       KStarYDC9310C UPS \n"
        "			G1 G2 G3Э��	   \n"
        "          �汾��V1.0 ���԰�   \n"
        "  (ͨѶ��ʽ)��9600(�ɵ�),n,8,1\n"
        "    ���ݴ��䷽ʽ������ RS485 \n"
        " author: fjw0312 E-mail:fjw0312@163.com\n"
        "   \n"  
        "	\n"
        "    \n"
        "    \n"
        "	\n"
        " ����ʱ�䣺\n"               // �����ڴ˺�������Ϣ!
        "                                \n"    // �뱣������(�����ڴ���)
};

//*****************************************************************
// �������ƣ�DLLInfo();
// ������������̬��汾�н���Ϣ�� Info ����������汾��Ϣ�ȱ�־��
// ���������Info--�汾��Ϣ����
// ���������
// ���أ�    �汾��Ϣ����
// ������
//*****************************************************************
char* DLLInfo( )
{
    int nStrLen = strlen( Info );
    sprintf( Info+nStrLen-30, "%s ", __DATE__ );
    strcat( Info, __TIME__ );
    strcat( Info, " \n" );

    return Info;
}

//CRCУ��
unsigned short CRC16(unsigned char *Msg, unsigned short len)
{
    unsigned short CRC = 0xffff;
    unsigned short i,temp;
    while(len--)
    {
        CRC = CRC^(*Msg++);
        for(i=0;i<8;i++)
        {
            if(CRC&0x0001)
                CRC = (CRC>>1)^0xa001;
            else
                CRC>>=1;
        }
    }
    temp = CRC&0xff;
    CRC = ((CRC>>8)&0xff)+(temp<<8);     
    return(CRC);
}
//LRCУ��
void LRC( unsigned char *Frame, int len )
{
    unsigned char v[2];
    unsigned char R = 0;

    for( int i = 1; i < len; i += 2 )  //i=1��ʼ��ʾ������Frame[0]
    {
        v[0] = Frame[i] < 0x3a ? Frame[i] - 0x30 : Frame[i] - 0x37;          //��ȡASCII�ַ�����ֵ 
        v[1] = Frame[i+1] < 0x3a ? Frame[i+1] - 0x30 : Frame[i+1] - 0x37;    //��ȡASCII�ַ�����ֵ 
        R += ( v[0] << 4 ) + v[1];   //��len���ַ����ݵĺͼ�����
    }
    
    char H = ( ( -R ) & 0xF0 ) >> 4;  
    char L =  ( -R ) & 0x0F;
    Frame[len  ] = H < 10 ? H + 0x30 : H + 0x37;
    Frame[len+1] = L < 10 ? L + 0x30 : L + 0x37;
    Frame[len+2] = '\r';    //0x0D
    Frame[len+3] = '\n';    //0x0A
    Frame[len+4] = 0x00;
}
/*****************************************************************/
// �������ƣ�ASSERT
// �����������˺����Ƕ��Ժ���������ʹ�á�
// ��������������͵�����ֵ
// �����������������ʱ�������κ��£��������������Ҵ����ڵ���״̬ʱ����
//           ʾ������Ϣ����ѡ��������У������˳���
// ���أ�    �汾��Ϣ����
// ������
/*****************************************************************/
void ASSERT(BOOL bFlag)
{
#ifdef _DEBUG_
    if (!bFlag)
    {
        printf("Assert Error!\n");
        printf("Press Enter to ignore, other keys to exit.\n");
        
        int iRet = getch();
        if (iRet == 13)
            return;
        exit(1);
    }
#endif
    return;
}

/*****************************************************************/
// �������ƣ�StrToK
// �������������ַ���S��cSepΪ�ָ����ֶ�
// ���������S-Դ�ַ�����D-��һ���ַ�����cSep-�ָ���
// ���������
// ���أ�    ��һ���ַ����ĸ���
// ������
/*****************************************************************/
int StrToK( char *S, char *D, char cSep )
{
    int i=0;
    
    ASSERT( S!=NULL && D!=NULL );
 
    while( S[i] && S[i] != cSep )
    {
        D[i] = S[i];
	    i++;
    }

    D[i] = 0;
    //return S[i] ? i+1 : 0;
    if (S[i])
    {
        return i + 1;
    }
    return 0;
}



/*****************************************************************/
// �������ƣ�SendString
// ������������ͨѶ�ھ��hComm �����ַ���sSendStr��ǰ nStrLen���ַ�
// ���������hComm - ͨѶ�ھ��,sSendStr - ��Ҫ���͵��ַ���,
//           nStrLen - ��Ҫ���͵��ַ�����
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ������
/*****************************************************************/
BOOL SendString( HANDLE hComm, BYTE* sSendStr, int nStrLen )
{
    DWORD lWritten=0;           // ʵ�����豸���͵��ַ�����
    
    ASSERT( hComm!=0 && sSendStr!=NULL && nStrLen>0 );
    
    // Ϊ�˼��ٸ��ţ�����ǰ�巢��/���ջ�����
    PurgeComm( hComm, PURGE_TXCLEAR );
    PurgeComm( hComm, PURGE_RXCLEAR );
    
    // ���ڲ����豸�����ڷ������ݰ�ǰ��ͣƬ�̣���������ע�ͣ���ȷ��ʱ�䣨��λ��ms����
   // Sleep( 500 );
    
    // ���豸 hComm �����ַ��� sSendStr ��ǰnStrLen���ַ�������ʵ��д��������ص� lWritten �С�
    WriteFile( hComm, (char*)sSendStr, nStrLen, &lWritten, NULL );

#ifdef _TSR_
    WriteString( sSendStr, 1, 23, 0x1f );
#endif           
    

    // ����Ǹ��ٲ��ԣ��ҷ�OCI��5����(nExtOci5IDȱʡΪ-1)�����¼������Ϣ��
    if( bTestFlag  )
    {

        WriteAsc( ASC_FILE, "\r\nSend:%d char", nStrLen );
        WriteAsc( HEX_FILE, "\r\nSend:%d char", nStrLen );
        WriteAsc( ASC_FILE, (char*)sSendStr, nStrLen );
        WriteHex( HEX_FILE, (char*)sSendStr, nStrLen );
    }
    
    return lWritten;
}


/*****************************************************************/
// �������ƣ�ReceiveString
// �������������豸 hComm �����ַ��� sRecStr �����ַ�
// ���������hComm - ͨѶ�ھ��,sRecStr - �����ַ���ָ��,
//           nStrLen - ��Ҫ���յ��ַ�����
//           sSendStr- ���͵��ַ���ָ��
// ���������ʵ�ʽ��յ��ַ�����,���յ����ַ���
// ��    �أ�    
// ������                   ��������֧�ֱ�׼modbus rtuЭ��  ���ϸ�У��
/*****************************************************************/
int ReceiveString(
                  HANDLE hComm,     // ͨѶ�ھ��
                  BYTE* sSendStr,   // ���͵��ַ���ָ��
                  BYTE* sRecStr,    // �����ַ���ָ��
                  int nStrLen       // ��Ҫ���յ��ַ�����    nStrLen = -1;
                  )
{
    DWORD lRead=0;      //ʵ�ʴ��豸��ȡ���ַ�����(��ȷ����)    
    int i = 0;          //�ܹ����豸��ȡ���ַ�����(������)
    BYTE recvChar[262] = {0}; //�ܹ����豸��ȡ���ַ�(������)
    /*     
     * ǰ�����ֽ�У��
     * ------------------------------------------------------------- 
     * ��ַ  ������  ����  (����.......У��) 
     * -------------------------------------------------------------
     */
    // �������ݰ��ж�����������Ƽ�ʹ��һ���Զ���
    ReadFile( hComm, (char*)sRecStr, 1, &lRead, NULL );  //��ȡmodbus�豸��ַ
    memcpy(recvChar+i,sRecStr,1);
    i++;    
    do 
    {
        if ( lRead && sRecStr[0] == sSendStr[0] )//�Ƚϵ�ַ
        {//��ַ��֤��ȷ,��֤��һ��
            ReadFile( hComm, (char*)sRecStr+1, 1, &lRead, NULL );   //��ȡ������
            memcpy(recvChar+i,sRecStr+1,1);
            i++;

            if ( sRecStr[1] == sSendStr[1] )  //������Ƚ�
            {
                //��ȡ����λ:����λ
                ReadFile( hComm, (char*)sRecStr+2, 1, &lRead, NULL );
                memcpy(recvChar+i,sRecStr+2,1);
                i++;
                ReadFile( hComm, (char*)sRecStr+3, sRecStr[2]+2, &lRead, NULL );
                memcpy(recvChar+i,sRecStr+3,lRead);
                i=i+lRead;
                lRead+=3;
                break;
            }
            else
            {//��������֤���� 
                sRecStr[0] = sRecStr[1];
            }    
        }
        else
        {//��ַ��֤����,���ֻ����һ���ֽ�ʱ����豸�ж���һ���ֽ�
            ReadFile( hComm, (char*)sRecStr, 1, &lRead, NULL );
            memcpy(recvChar+i,sRecStr,1);
            i++;
        }
    } while( lRead && i<261 );
    // ���ٵ��ԵĽ�����Ϣ������Ӧ��Ϣ��¼��
#ifndef _TSR_
	if( bTestFlag  )
    {
	    WriteAsc( ASC_FILE, "\r\nRecv:%d char", (int)lRead );
	    WriteAsc( HEX_FILE, "\r\nRecv:%d char", (int)lRead );
	    WriteAsc( ASC_FILE, "%s", (char*)recvChar );
		WriteHex( HEX_FILE, (char*)recvChar, i );
	}
#endif    // end of #ifndef _TSR_    
 
    return (int)lRead;
}

/*****************************************************************/
// �������ƣ�ReceiveString
// �������������豸 hComm �����ַ��� sRecStr �����ַ�
// ���������hComm - ͨѶ�ھ��,sRecStr - �����ַ���ָ��,
//           nStrLen - ��Ҫ���յ��ַ�����                  nStrLen = -1;
// ���������ʵ�ʽ��յ��ַ�����
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ������                               ��������֧�ֱ�׼modbusЭ��  ��������֧�����ݳ���ռ2byte��Э��
/*****************************************************************/
int ReceiveString1(
                  HANDLE hComm,     // ͨѶ�ھ��
                  BYTE* sRecStr,    // �����ַ���ָ��
                  int nStrLen       // ��Ҫ���յ��ַ�����
                  )
{
    DWORD lRead=0;  //ʵ�ʴ��豸��ȡ���ַ�����
    
	if( nStrLen != -1 )
    {
        ReadFile( hComm, (char*)sRecStr, nStrLen, &lRead, NULL );
    }
    else
    {
        ReadFile( hComm, (char*)sRecStr, 3, &lRead, NULL );
        if(lRead<3)
            return FALSE;
        int len = (int)sRecStr[2];
		//����ذ��ֽ�2byte
		if(len==0)
		{
			ReadFile( hComm, (char*)sRecStr+3, 1, &lRead, NULL );
			len = (int)sRecStr[3];
			ReadFile( hComm, (char*)sRecStr+4, len+2, &lRead, NULL );
			lRead +=4;
		}
		else{
        ReadFile( hComm, (char*)sRecStr+3, len+2, &lRead, NULL );
        lRead += 3;
		}
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // ��ע�� TSR.CPP�ļ� ReadFile�����еĳ�ʱʱ�� lTimeOut�ͽ����ַ� byEOI
    //
    ////////////////////////////////////////////////////////////////////////

    // ���ٵ��ԵĽ�����Ϣ������Ӧ��Ϣ��¼��
 // ����Ǹ��ٲ��ԣ��ҷ�OCI��5����(nExtOci5IDȱʡΪ-1)�����¼������Ϣ��
    if( bTestFlag  )
    {
      WriteAsc( ASC_FILE, "\r\nRecv:%d char", (int)lRead );
      WriteAsc( HEX_FILE, "\r\nRecv:%d char", (int)lRead );
      WriteAsc( ASC_FILE, (char*)sRecStr );
      WriteHex( HEX_FILE, (char*)sRecStr, (int)lRead );
    }
   
    
    return (int)lRead;
}

//*****************************************************************
// �������ƣ�ReceiveString2
// �������������豸 hComm �����ַ��� sRecStr �����ַ�
// ���������hComm - ͨѶ�ھ��,sRecStr - �����ַ���ָ��,
//           nStrLen - ��Ҫ���յ��ַ�����                       nStrLen = -1;
// ���������ʵ�ʽ��յ��ַ�����
// ��    �أ��ɹ��ɼ����ַ�����
// ������                   ��������Ҫ�����ڵ���\Q1\G1   �� ʹ���ڻذ���0X0D��β�İ� 
//*****************************************************************
int ReceiveString2(
                  HANDLE hComm,     // ͨѶ�ھ��
      //            int nUnitNo,      // �ɼ�����Ԫ��ַ
                  BYTE* sRecStr,    // �����ַ���ָ��
                  int nStrLen       // ��Ҫ���յ��ַ�����
                  )
{
    DWORD lRead=0;  //ʵ�ʴ��豸��ȡ���ַ�����
    
    ASSERT( hComm!=0 && sRecStr!=NULL );

    // �������ݰ��ж�����������Ƽ�ʹ��һ���Զ���
    if( nStrLen != -1 )
    {
        ReadFile( hComm, (char*)sRecStr, nStrLen, &lRead, NULL );
    }
    else
    {
        // ����ֻ�ܰ���������ַ����������س�0x0D
        int i=0;    // �������������ַ�����
        do
        {
            ReadFile( hComm, (char*)sRecStr+i++, 1, &lRead, NULL );
        }while( lRead==1 && sRecStr[i-1]!=byEOI && i<nMaxDllBufNum-1 );
        lRead = i;  // Ϊ�˷������Ĵ��������յ��ַ�����ͳһ������
        sRecStr[lRead] = 0;
    }
	if( bTestFlag  )
    {
      WriteAsc( ASC_FILE, "\r\nRecv:%d char", (int)lRead );
      WriteAsc( HEX_FILE, "\r\nRecv:%d char", (int)lRead );
      WriteAsc( ASC_FILE, (char*)sRecStr );
      WriteHex( HEX_FILE, (char*)sRecStr, (int)lRead );
    }
    return (int)lRead;
}
//*****************************************************************
// �������ƣ�ReceiveString3
// �������������豸 hComm �����ַ��� sRecStr �����ַ�
// ���������hComm - ͨѶ�ھ��,sRecStr - �����ַ���ָ��,
//           nStrLen - ��Ҫ���յ��ַ�����             nStrLen = -1;
// ���������ʵ�ʽ��յ��ַ�����
// ��    �أ��ɹ��ɼ����ַ�����
// ������                   ��������Ҫ������modbus_ascii  ��
//*****************************************************************
int ReceiveString3( HANDLE hComm, BYTE* sRecStr, int nStrLen )
{
    DWORD lRead=0;
	int i = 0;
	int j = 0;
 // �������ݰ��ж�����������Ƽ�ʹ��һ���Զ���
    if( nStrLen != -1 )
    {
        ReadFile( hComm, (char*)sRecStr, nStrLen, &lRead, NULL );
    }
    else
    {
        // ����ֻ�ܰ���������ַ����������س�0x0D      
        do
        {
            ReadFile( hComm, (char*)sRecStr+i, 1, &lRead, NULL );
			 i++;
			 j++;
        }while( (lRead==1) && (sRecStr[i-1]!=0x0A) && (i<nMaxDllBufNum-1) );

         lRead = i;  // Ϊ�˷������Ĵ��������յ��ַ�����ͳһ������
         sRecStr[lRead] = 0;
    }
    
    
    if( bTestFlag  )
    {
      WriteAsc( ASC_FILE, "\r\nRecv:%d char", (int)lRead );
      WriteAsc( HEX_FILE, "\r\nRecv:%d char", (int)lRead );
      WriteAsc( ASC_FILE, (char*)sRecStr );
      WriteHex( HEX_FILE, (char*)sRecStr, (int)lRead );
    }
 
    return (int)lRead;
}

/*****************************************************************/
// �������ƣ�GetResponeData
// ������������ѯ���ݰ�����
// ���������hComm - ͨ�ž��, nUnitNo - �ɼ�����Ԫ��ַ,
//           sSendStr - Ҫ���͵����, Frame - �������ݻ�����
//           nSend - ��������ĳ���, nRecv - ���յ����ݳ���
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
/*****************************************************************/

BOOL GetResponeData( HANDLE hComm,  BYTE *sSendStr, BYTE* Buf, int nSend, int nRecv)     
{
    int  nNum=0;    // ���Դ��������������ͨѶ�ɹ��ʡ�
    int  nRet=0;    // ���պ�������ֵ��ʵ�ʽ��յ����ַ�������

    ASSERT( hComm!=0 && sSendStr!=NULL && Buf!=NULL );
    
    do
    {
        if( !SendString( hComm, sSendStr, nSend ) )     // �������
        {
            return FALSE;
        }
        Sleep(200*nNum);

#ifdef MODBUS_RTU    
		nRet = ReceiveString( hComm, sSendStr, Buf, nRecv );     // ��������(�ϸ���֤ modbus_rtu)
	//  nRet = ReceiveString1( hComm, Buf, nRecv);               // ��������modbus_rtu ��������2�ֽڸ�ʽ
#endif
#ifdef Q_G  
		nRet = ReceiveString2( hComm, Buf, nRecv );              // ��������   ����G1��Q1��
		if( Buf[nRet-1] == byEOI )  
				return TRUE;
#endif

#ifdef MODBUS_ASCII
		nRet = ReceiveString3( hComm, Buf, nRecv );              // ��������   ����modbus_ascii
		if(( Buf[nRet-2] == 0x0d ) &&(Buf[nRet-1] == 0x0A))
				return TRUE;
#endif
      
        if( nRet == 0 )        // δ�յ��κ���Ϣ
        {
            if( bTestFlag )    // ����Ǹ��ٲ��ԣ����¼������Ϣ��
            {
                WriteAsc( ASC_FILE, "\r\nδ�յ��κ���Ϣ\r\n" );
                WriteAsc( HEX_FILE, "\r\nδ�յ��κ���Ϣ\r\n",18 );
            }           
        }
        else if( nRet< 6 )
        {

            if( bTestFlag )    // ����Ǹ��ٲ��ԣ����¼������Ϣ��
            {
                WriteAsc( ASC_FILE, "\r\nδ�յ��㹻��Ϣ\r\n" );
                WriteAsc( HEX_FILE, "\r\nδ�յ��㹻��Ϣ���ذ��ֽ�С��6��\r\n" ,18);
            }
          
        }
        else if( sSendStr[0]!=Buf[0] || sSendStr[1]!=Buf[1] )
        {
            if( bTestFlag )    // ����Ǹ��ٲ��ԣ����¼������Ϣ��
            {
                WriteAsc( ASC_FILE, "\r\n�ؼ��ִ���\r\n" );
                WriteAsc( HEX_FILE, "\r\n�ذ��ĵ�ַ���������\r\n" ,18);
            }

        }
        else
        {
            //����У��
            unsigned short CRC = CRC16(Buf,nRet-2);
            if( Buf[nRet-2]==(BYTE)((CRC>>8)&0x00ff) && Buf[nRet-1]==(BYTE)(CRC&0x00ff) )
            {
                return TRUE;
            }
            else
            {
                if( bTestFlag )    // ����Ǹ��ٲ��ԣ����¼������Ϣ��
                {
                    WriteAsc( ASC_FILE, "\r\nУ�����\r\n" );
                    WriteAsc( HEX_FILE, "\r\nУ�����\r\n" ,18);
                }
                
            }
        }
		
    }while( ++nNum < 3 );
    
    return FALSE;
}



//*****************************************************************
// �������ƣ�Fix_Data
// �������������Ѵ����ȫ�ֱ�������Frame[]�е����ݸ������ݽṹ��
//           ����Ķ�Ӧ��ϵ���н���
// ���������Frame - ԭʼ��������, fData - �����������
//           strData - ���ڽ������ݽ��������ݽṹ
// ��    ����
//*****************************************************************
void Fix_Data(
              float* fData,             // ����õ����ݻ�����
              DATASTRUCT Data_Struct[],     // Ҫ��������ݽṹ
              BYTE *Frame )             // Ҫ������ַ���
{
    int nLoop = 0;
    int i=0 ;
	short tmp=0;
	unsigned short temp=0;
	DWORD  dwTmp = 0;
	unsigned long dwTemp = 0 ;
	char sTemp[8]={0};
	int nData=0;  
    
    ASSERT( fData!=NULL );
    
    while( Data_Struct[nLoop].nType )       // ѭ��ֱ������������=0  ������1λ��  modbus������2λ��
    {
		nData = 0;
        switch( Data_Struct[nLoop].nType )  // ����������������Ӧ����  �޷���Ϊ����
        {
			
			case 1:   // 1�ֽڿ�����,ÿλ��ʾһ��������,���ϱ�׼modbus��1��2��ָ��
                fData[Data_Struct[nLoop].nChannel] = (float)
                    ( ( Frame[Data_Struct[nLoop].nOffset] 
                        >> Data_Struct[nLoop].nScaleBit ) & 0x01 );
                break;
				
			case 2:    // 2�ֽڿ�����
				for( i=0; i<2; i++ )
					for(int j=0;j<8;j++)
						fData[Data_Struct[nLoop].nChannel+i*8+j] = (float)
						((Frame[Data_Struct[nLoop].nOffset+1-i] >> j) & 0x01);
                break;
			
			
			case 10:    // 1�ֽ�ģ�����з���������
				fData[Data_Struct[nLoop].nChannel] = (float)
				(char)Frame[Data_Struct[nLoop].nOffset]/Data_Struct[nLoop].nScaleBit;
				break;
				
			case 11:   // 1�ֽ�ģ�����޷���������
				fData[Data_Struct[nLoop].nChannel] = (float)Frame[Data_Struct[nLoop].nOffset];
                break;
			
			
            case 20:    // 2�ֽ�ģ�����з���������,���ֽ��Ƚ����ֽں��, (������ǰ�����ں�)���ϱ�׼modbus��3,4��ָ��								
								    tmp =  Frame[Data_Struct[nLoop].nOffset];
									tmp = (tmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
								fData[Data_Struct[nLoop].nChannel] = (float)tmp/Data_Struct[nLoop].nScaleBit;
                break;
      		case 21:    // 2�ֽ�ģ�����޷���������,���ո��ֽ�,���յ��ֽ�,(������ǰ�����ں�)���ϱ�׼modbus��3,4��ָ��
									temp =  Frame[Data_Struct[nLoop].nOffset];
									temp = (temp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
									fData[Data_Struct[nLoop].nChannel] = (float)temp/Data_Struct[nLoop].nScaleBit;
                break;
	        case 22:    // 2�ֽ�ģ�����з���������,���յ��ֽ�,���ո��ֽ�      
									tmp = (tmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
									tmp = (tmp << 8) + Frame[Data_Struct[nLoop].nOffset];
									fData[Data_Struct[nLoop].nChannel] = (float)tmp
									/ Data_Struct[nLoop].nScaleBit;
                break;
			case 23:    // 2�ֽ�ģ�����޷���������,���յ��ֽ�,���ո��ֽ�      
									temp = (temp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
									temp = (temp << 8) + Frame[Data_Struct[nLoop].nOffset];
									fData[Data_Struct[nLoop].nChannel] = (float)temp
									/ Data_Struct[nLoop].nScaleBit;
                break;
			
            case 40:    // 4�ֽ�ģ����:������
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
                    fData[Data_Struct[nLoop].nChannel] = *(float*)(&dwTmp) //--������������ע��,����dwTmp������ANSC��׼��ʽ���������ñ�׼����ָ��ָ�������������ȡ���ָ��ָ���ֵ���Ӷ���DWORD����ת���ɱ�׼float����
						/ Data_Struct[nLoop].nScaleBit;
                break;
			case 41:    // 4�ֽ�ģ����:�޷�������(������ǰ�����ں�)
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTmp / Data_Struct[nLoop].nScaleBit;
                break;
			case 42:    // 4�ֽ�ģ����:�з�������(������ǰ�����ں�)
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTemp / Data_Struct[nLoop].nScaleBit;
                break;		
			case 43:    // 4�ֽ�ģ����:�޷�������(������ǰ�����ں�)    **
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTmp / Data_Struct[nLoop].nScaleBit;
                break;
			case 44:    // 4�ֽ�ģ����:�з�������(������ǰ�����ں�)   **
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTemp / Data_Struct[nLoop].nScaleBit;
                break;
				
            case 50:    // 8�ֽ�ģ����:˫���ȸ�����
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
					fData[Data_Struct[nLoop].nChannel] = (float)dwTmp;
				
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+4];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+5];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+6];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+7];		
					fData[Data_Struct[nLoop].nChannel+1] = (float)dwTmp;							
				break;            	
            	
      
			case 80:		// time
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];		//year
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];		//year
      		        dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];		//month
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];	  //month		
      		        fData[Data_Struct[nLoop].nChannel] = (float)dwTmp;  // 
      		
      		        dwTmp = 0;
      		        dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+4];		//day
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+5];		//day
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+6];		//reserved
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+7];		//reserved
					fData[Data_Struct[nLoop].nChannel+1] = (float)dwTmp;  // 

      		        dwTmp = 0;
      		        dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+8];		//hour
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+9];		//hour
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+10];		//min
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+11];		//min
					fData[Data_Struct[nLoop].nChannel+2] = (float)dwTmp;  // 

      		        dwTmp = 0;
      		        dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+12];		//sec
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+13];		//sec
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+14];		//msec
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+15];		//msec
					fData[Data_Struct[nLoop].nChannel+3] = (float)dwTmp;  //   
				break;  

				
			case 200:   //�����8���ַ�����֧�ֺ��з������ַ���       bcd   ����/Q1/G1
				fData[Data_Struct[nLoop].nChannel] = (float)atof((char*)Frame+Data_Struct[nLoop].nOffset);			
				break;
			case 100:   //1�ֽ��ַ�  �Ŀ�����  ���������ֽ�Ҫ��-0x30ʱ  0~9     ASCII   ����/Q1/G1
				 fData[Data_Struct[nLoop].nChannel] = (float)
				 ( Frame[Data_Struct[nLoop].nOffset] - 0x30 );
				break;	
			case 101: // 2�ֽ��ַ� 1byteģ����������������'11'->17 ->bit0~bit7    ASCII 
				if(Frame[Data_Struct[nLoop].nOffset]==0x20 && Frame[Data_Struct[nLoop].nOffset+1]==0x20)   //���� �ո� ����
					fData[Data_Struct[nLoop].nChannel] = (float)9999;
				else
				{
					sscanf( (char *)Frame+Data_Struct[nLoop].nOffset, "%02x", &nData );
					fData[Data_Struct[nLoop].nChannel] = (float)((nData >> Data_Struct[nLoop].nScaleBit) & 0x01);
				}
				break;	
			case 110: // 2�ֽ��ַ� 1byteģ��������ʽ��  
				strncpy( sTemp, (char *)Frame+Data_Struct[nLoop].nOffset, 2 );
				sTemp[2] = 0;
				if(sTemp[0]==0x20 && sTemp[1]==0x20)
					fData[Data_Struct[nLoop].nChannel] = (float)9999;
				else
				{
					sscanf( (char *)sTemp, "%02x", &nData );   //
					fData[Data_Struct[nLoop].nChannel] = (float)nData;
				}
				break;	
			case 120: // �޷���4�ֽ�ASCII  2byteģ��������ʽ��'0010'->0016
				strncpy( sTemp, (char *)Frame+Data_Struct[nLoop].nOffset, 4 );
				sTemp[4] = 0;
				if( sTemp[0]==0x20&&sTemp[1]==0x20&&sTemp[2]==0x20&&sTemp[3]==0x20 )
					fData[Data_Struct[nLoop].nChannel] =  (float)9999;
				else
				{
					int h=0,l=0;
					sscanf( (char *)sTemp, "%02x", &h );      //���ո�λ
					sscanf( (char *)sTemp+2, "%02x", &l );
					fData[Data_Struct[nLoop].nChannel] = (float)(h*256+l)
						/ Data_Struct[nLoop].nScaleBit;
				}			
				break;	
			
			case 130: // �޷���6�ֽ�ASCII  2byteģ��������ʽ��'000013'->19
				strncpy( sTemp, (char *)Frame+Data_Struct[nLoop].nOffset, 6 );
				sTemp[6] = 0;
				if( sTemp[0]==0x20&&sTemp[1]==0x20&&sTemp[2]==0x20&&sTemp[3]==0x20&&sTemp[4]==0x20&&sTemp[5]==0x20  )
					fData[Data_Struct[nLoop].nChannel] =  (float)9999;
				else
				{
					int hl1=0,h1=0,l1=0;
					sscanf( (char *)sTemp, "%02x", &hl1 );  
					sscanf( (char *)sTemp+2, "%02x", &h1 );
					sscanf( (char *)sTemp+4, "%02x", &l1 );
					fData[Data_Struct[nLoop].nChannel] = (float)(hl1*65536+h1*256+l1)
						/ Data_Struct[nLoop].nScaleBit;
				}
				break;	
			
			case 140: // �޷���8�ֽ�ASCII  4byteģ��������ʽ��'00000013'->19
				strncpy( sTemp, (char *)Frame+Data_Struct[nLoop].nOffset, 8 );
				sTemp[8] = 0;
				if( sTemp[0]==0x20&&sTemp[1]==0x20&&sTemp[2]==0x20&&sTemp[3]==0x20&&sTemp[4]==0x20&&sTemp[5]==0x20  )
					fData[Data_Struct[nLoop].nChannel] =  (float)9999;
				else
				{
					int hh2=0,hl2=0,h2=0,l2=0;
					sscanf( (char *)sTemp, "%02x", &hh2 );  
					sscanf( (char *)sTemp+2, "%02x", &hl2 );
					sscanf( (char *)sTemp+4, "%02x", &h2 );
					sscanf( (char *)sTemp+6, "%02x", &l2 );
					fData[Data_Struct[nLoop].nChannel] = (float)(hh2*65536*256+hl2*65536+h2*256+l2)
						/ Data_Struct[nLoop].nScaleBit;
				}
				break;
					
			default:
				break;
        }   //End Switch
        nLoop++;
    }   //End While
	
}

/*****************************************************************/
// �������ƣ�GetData
// ��������������Э����֯��ȡ�豸���ݡ�
// ���������hComm - ͨ�ž��, nUnitNo - �ɼ�����Ԫ��ַ,
//           pData - �ϱ����ݻ�����ָ��
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
/*****************************************************************/
BOOL GetData( HANDLE hComm, int nUnitNo, float* pData )
{    
    // ��֤���룬��̬����þֲ�����
	ASSERT( hComm!=0 && pData!=NULL );
    BYTE Frame[nMaxBufLen] = {0};  // �������ݻ�����
    BYTE sSendStr[20]={0};  // �������ݻ�����  
	unsigned short CRC;
	int i = 0;
    int nRecve = 0;
	
    int nSend = 8;  //modbus tcp nsend=12; modbus rtu nsend=8;	modbus ascii nsend=17
	int  frame_p = 3;  // modbus rtu ��ȡ����Ч�������ӵ����ֽڿ�ʼ
	
#ifdef MODBUS_RTU  
	unsigned char nSendBuf[][20] =
    {
		// ��ַ   ������    �Ĵ�����ַ   �Ĵ�������    У�飨CRC��
		{ 0x00,    0x04,    0x00, 0x00,   0x00,  2,   0x00, 0x00 },   //[5]��ʾ��ȡ�Ĵ�������(һ��һ���Ĵ���2byte) 2�ֽڵ�����ͨ����ͨ����Ϊ�á�5��ֵ��
    //    { 0x00,0x03,0x00,0x00,0x00,33,0x00,0x00 },   //[5] ������Ϊ4byte�����ݣ�ͨ����*2Ϊ�á�5��ֵ��
	//	{ 0x00,0x03,0x00,0x21,0x00,8,0x00,0x00 },
    //    { 0x00,0x02,0x00,0x00,0x00,8,0x00,0x00 }, 
	//	{ 0x00,0x03,0x02,0x00,0x00,75,0x00,0x00 },

    };
#endif	
#ifdef MODBUS_ASCII  
	unsigned char nSendBuf[][20] =
    {
		// ��ַ   ������    �Ĵ�����ַ   �Ĵ�������    У�飨LRC��
		{ 0x00,    0x03,    0x00, 0x01,   0x00,  56,   0x00, 0x00 },   //[5]��ʾ��ȡ�Ĵ�������(һ��һ���Ĵ���2byte) 2�ֽڵ�����ͨ����ͨ����Ϊ�á�5��ֵ��
        { 0x00,    0x03,    0x00, 71,     0x00,  7,    0x00, 0x00 },   //[5] ������Ϊ4byte�����ݣ�ͨ����*2Ϊ�á�5��ֵ��
		{ 0x00,    0x04,    0x00, 81,     0x00,  33,    0x00, 0x00 }, 
    //    { 0x00,0x02,0x00,0x00,0x00,8,0x00,0x00 }, 
	//	{ 0x00,0x03,0x02,0x00,0x00,75,0x00,0x00 },

    };
#endif
#ifdef Q_G		
		unsigned char nSendBuf[][20] = 
		{
			{ 'Q','1',0x0D },     //Q1<cr> ����:(MMM.M NNN.N PPP.P QQQ RR.R SS.S TT.T b7b6b5b4b3b2b1b0<cr>
		
			{ 'R','E','A','D','0','1', 0x0d },		//	1�����ѹ������Ƶ��
			{ 'R','E','A','D','0','2', 0x0d },		//	2״̬��Ϣ
			{ 'R','E','A','D','0','3', 0x0d },		//	3�����ѹ�����Ƶ��
			{ 'R','E','A','D','0','4', 0x0d },		//	4����������������
			{ 'R','E','A','D','0','5', 0x0d },		//	5�����ʡ����ط�ֵ��
			{ 'R','E','A','D','0','6', 0x0d },		//	6��·��ѹ����·Ƶ��
			{ 'R','E','A','D','0','7', 0x0d },		//	7BUS��ѹ
			{ 'R','E','A','D','0','8', 0x0d },		//	8��ص�ѹ
			{ 'R','E','A','D','0','9', 0x0d },		//	9��ص���
			{ 'R','E','A','D','1','0', 0x0d },		//	10����
			
		//	{'G', '1', 0x0D},  //G1
		//	{'G', '2', 0x0D},  //G2
		//	{'G', '3', 0x0D},  //G3
		};
#endif

	for( i=0; i<(sizeof(nSendBuf)/sizeof(nSendBuf[0])); i++ )
    {
#ifdef MODBUS_RTU		
		//�豸��ַ
        nSendBuf[i][0] = (BYTE)nUnitNo;    //modbus-tcp 6λ  modbus-rtu 0 λ
		//��ҪCRC��֤
		CRC = CRC16( nSendBuf[i], 6 );        // У����
        nSendBuf[i][6] = (BYTE)((CRC>>8) & 0x00FF);    // ��λ��ǰ
        nSendBuf[i][7] = (BYTE)(CRC & 0x00FF);         // ��λ�ں�

		//����nRecve�ļĴ����ֽ���+5
		if(nSendBuf[i][1]==0x02 || nSendBuf[i][1]==0x01)
		{
			nRecve=nSendBuf[i][5]%8?nSendBuf[i][5]/8+6:nSendBuf[i][5]/8+5;   //һ������ͨ�� ���ݽ���1bye
		}
	    else
		{
			nRecve=nSendBuf[i][5]*2+5;    //һ���Ĵ��� ���ݽ���2byte
		}
		nSend = 8;
		frame_p = 3;		
#endif		
#ifdef MODBUS_ASCII                                           //��ַ      ������         �Ĵ�����ַ  
		sprintf((char *)nSendBuf[i], ":%02X%02X%02X%02X%02X%02X\0", nUnitNo, nSendBuf[i][1],nSendBuf[i][2],nSendBuf[i][3],
						nSendBuf[i][4],nSendBuf[i][5]); //�Ĵ�������
		//LRCУ��		
		LRC( nSendBuf[i],13);
		nSend = 17;
		frame_p = 7;
		nRecve = -1;
		//nRecve=nSendBuf[i][5]*4+11;    //һ���Ĵ��� ���ݽ���2bye
		//WriteAsc( HEX_FILE, "\r\n printf send=:%s ", nSendBuf[i]);
		//WriteAsc( ASC_FILE, "\r\n printf send=:%s ", nSendBuf[i]);
#endif
#ifdef Q_G		
	    nSend = 7;           //�˴�ע���ֶ��޸�
		if(i==0) nSend=3;
		frame_p = 0;
		nRecve = -1;
#endif

       //����ַ��������       
		if(!GetResponeData( hComm, nSendBuf[i], Frame, nSend , nRecve ))
        {
			return FALSE;
		}
        else  //MODBUS RTU�ӵ�4λ��ʼ  MODBUS TCP�ӵ�ʮλ��ʼ
        {
            Fix_Data(pData+strChnlInfo[i].nMeasureStart, strChnlInfo[i].pMeasure,Frame+frame_p);
        }


		
       Sleep(500);
    }
	
//	*(pData+nR_DataStart0+10) = 100.00;   //ǿ�и���10ͨ��ֵΪ100

	*(pData+nR_DataStart0+0) = *(pData+nR_DataStart0+0)-40;
	*(pData+nR_DataStart0+1) = *(pData+nR_DataStart0+1);
	//fjw test 200 ģ���ź�
	for(i=2; i<156; i++)
		{
			*(pData+nR_DataStart0+i) = i;
		}		
		//fjw_test �źŸ�ֵ
	for(i=2;i<10;i++)
		{
			*(pData+nR_DataStart0+i) = buf[i];
		}
   

	return TRUE;
}

/*****************************************************************/
// �������ƣ�Read
// �������������������PSMS�붯̬��/TSR�ȼ�Ĳɼ��ӿڣ��ɼ����ϱ���
//           �������ݡ����ñ��ӿڵ������°汾��PSMS4.0��TSR��
//           PowerStar��OCE�ȡ�
// ���������hComm - ͨ�ž��, nUnitNo - �ɼ�����Ԫ��ַ,
//           pData - �ϱ����ݻ�����ָ��
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
/*****************************************************************/
bool Read( HANDLE hComm, int nUnitNo, void* pData )
{
    ASSERT( hComm!=0 && pData!=NULL );
    
    
    if( !GetData( hComm, nUnitNo, (float*)pData ) )
    {

        return FALSE;
    }
    
    
    return TRUE;
}




#ifdef _DLL_
/*****************************************************************/
// �������ƣ�Query
// �������������������PSMS�붯̬��Ĳɼ��ӿڣ��ɼ����ϱ��ɼ������ݡ�
//           ���ñ��ӿڵ������°汾��V4.1����4.x��/V5.0�� 
// ���������hComm - ͨ�ž��, nUnitNo - �ɼ�����Ԫ��ַ,
//           pData - �ϱ����ݻ�����ָ��
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
/*****************************************************************/
DLLExport BOOL Query(
                     HANDLE hComm,              // ͨѶ�ھ��
                     int nUnitNo,                // �ɼ�����Ԫ��ַ
                     ENUMSIGNALPROC EnumProc,   // ö�ٺ���
                     LPVOID lpvoid              // ������ָ��
                     )
{
    float p[nMaxChanelNo+1]={ 0.0f };
    
    ASSERT( hComm!=0 );
    
    // ���ݲɼ�
    if ( !Read( hComm, nUnitNo, p ) )
    {
        return FALSE;
    }
    
    // �����ϱ�
    for( int i = 0; i<nMaxChanelNo; i++ )
    {
        EnumProc( i, p[i], lpvoid );
    }
    
    return TRUE;
}

// ���ٲ�����ں���
// ���ܣ��ڲ���ǰ����ȫ�ֱ�־ bTestFlag ��λ�����Ժ󣬸�λ��־��
//*****************************************************************
// �������ƣ�Test
// �������������ٲ�����ں������ڲ���ǰ����ȫ�ֱ�־ bTestFlag ��λ��
//           ���Ժ󣬸�λ��־��
// ���������hComm - ͨ�ž��, 
//           nUnitNo - �ɼ�����Ԫ��ַ,
//           pData - �ϱ����ݻ�����ָ��
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
//*****************************************************************
DLLExport BOOL Test(HANDLE hComm,               // ͨѶ�ھ��
                    int nUnitNo,                // �ɼ�����Ԫ��ַ
                    ENUMSIGNALPROC EnumProc,    // ö�ٺ���
                    LPVOID lpvoid)              // ������ָ��
{
    // �����Ա�־��λ
    bTestFlag = TRUE;
    
    // ���òɼ������ɼ����ݣ����л�����Ա�־����λ����ʾ������Ϣ��
    BOOL bFlag = Query( hComm, nUnitNo, EnumProc, lpvoid );
    
    WriteAsc( ASC_FILE, "\r\n���βɼ�����\r\n" );
    WriteAsc( HEX_FILE, "\r\n���βɼ�����\r\n" );

    // �����Ա�־��λ
    bTestFlag = FALSE;
    
    return bFlag;
}
#endif


/*****************************************************************/
// �������ƣ�SetDev
// ���������������������֯����������Э��Ĺ涨����֯����
// ���������hComm - ͨ�ž��, nUnitNo - �ɼ�����Ԫ��ַ,
//           nCmdNo - �����, fValue - ����ֵ/״̬
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
/*****************************************************************/
bool SetDev( HANDLE hComm, int nUnitNo, int nCmdNo, char *szValue )
{
		BYTE sSendStr[50]={0};
		BYTE Frame[nMaxDllBufNum]={0};
		unsigned short CRC;
		unsigned short nReg;
	
		float fValue = (float)atof( szValue );
		sSendStr[0] = (BYTE)nUnitNo;
		sSendStr[1] = 0x06;
	/*	switch (nCmdNo)
		{
		case 10:		//�趨�¶�
			nReg= 0x0007;
			fValue *=10;
			break;
			
		case 11:	  //�趨PID����P  
			nReg= 0x000a;
			break;
		case 12:	   //�趨PID����I 
			nReg= 0x000b;
			break;
		case 13:	   //�趨PID����D  
			nReg= 0x000c;
			break;
			
		case 16:	   //���ÿ��Ʒ�ʽ
			nReg= 0x0010;
			break;
		case 17:	  //�����ֶ����Ʊ���
			nReg= 0x0011;
			fValue *=10;
			break;
		case 18:	 //�����豸����
			nReg= 0x0012;
			break;

		default:
			;
	
		}	
	
		sSendStr[2] = nReg>>8;			   //�Ĵ�����ַ��λ
		sSendStr[3] = nReg&0x00FF;		   //�Ĵ�����ַ��λ
					
		sSendStr[4] = (int)fValue>>8;		   //�趨ֵ��λ
		sSendStr[5] = (int)fValue&0x00FF;	   //�趨ֵ��λ 	
		
		CRC = CRC16(sSendStr,6);
		sSendStr[6] = (BYTE)((CRC>>8)&0x00ff);
		sSendStr[7] = (BYTE)(CRC&0x00ff);
		if(!GetResponeData( hComm, sSendStr, Frame, 8,TRUE ))
			return FALSE;
	*/
		buf[nCmdNo-11] = fValue;
		return TRUE;
}

/*****************************************************************/
// �������ƣ�Control
// ����������PSMS V4.1������4.x��/V5.0��Ŀ��ƺ�����
// ���������hComm - ͨ�ž��, nUnitNo - �ɼ�����Ԫ��ַ,
//           pCmdStr - ���
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����
/*****************************************************************/
bool Control( HANDLE hComm, int nUnitNo, char *pCmdStr )
{
	printf("libEquipt.so>>>Control into!\n");
    char sTarget[128];
    //float fValue = 0.0f;
    printf("libEquipt.so>>>Control ���յ������ݣ�%s\n", pCmdStr);
    
    ASSERT( hComm!=0 && pCmdStr!=NULL );
    
    //Get nCmdNo
    int nPoint = StrToK( pCmdStr,sTarget,',' );
    if( pCmdStr == NULL )
    {
        return FALSE;
    }
    int nCmdNo = (int)atoi( pCmdStr );    //Ϊ�˰�ȫ����ֹ���ͣ�����Ŵ�11��ʼ
    
    //Get Value
    if( nPoint>0 )
    {
        nPoint = StrToK( pCmdStr+nPoint,sTarget,',' );
        if( sTarget==NULL )
        {
            return FALSE;
        }
    }

    if( SetDev( hComm, nUnitNo, nCmdNo, sTarget ) == FALSE )
    {
        return FALSE;
    }
    return TRUE;


}

/*****************************************************************/
// �������ƣ�Write
// ����������V4.0�Ȱ�Ŀ��ƺ�����  Ŀǰ  ʹ�øýӿ�  ����sampler ����ֱ�ӷ��Ϳ�������
// ���������hComm - ͨ�ž��, 
//           pCmdStr - �����  nStrLen - �����ֽڳ��ȡ�
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����pCmdStr ע��Ϊ8bit ���ֽ�����  ���ַ���
/*****************************************************************/
bool Write( HANDLE hComm, char* pCmdStr, int nStrLen)
{
	printf("libEquipt.so>>>Write ���յ������ݣ�");
	int i = 0;
	for(i=0;i<20; i++)
	{
		printf("%02X ", pCmdStr[i]);
	}
	printf("\n");
	printf("libEquipt.so>>>Write ���յ������ݽ���\n");
	 if( bTestFlag  )
	 {
          WriteAsc( ASC_FILE, "\r\n Write :\r\n" );
          WriteAsc( HEX_FILE, pCmdStr ,nStrLen);
     }
	if(SendString(hComm, (BYTE *)pCmdStr, nStrLen ) )
		return TRUE;
	return false;
}


/*****************************************************************/
// �������ƣ�Report
// ������������ȡ ��������������   API��sampler����
// ���������hComm - ͨ�ž��, sRecStr���յ�����������,
//           strlen�������ݵĳ���
//          EquiptId - �����豸id�� 
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����fdata: ��ַ ������ ���ݡ�800�� sRecStr��ԭ��̬ ��ַ ������ ��ַ������
//    float RdataBuf[20][803];      //�����ϱ���  �ɼ�����
//    char  SstrBuf[803];           //�����ϱ���  ��������
/*****************************************************************/
bool Report( HANDLE hComm,  float *fdata,  char* sRecStr) //��������#��ͷ
{
	int i = 0;

	//��ֵ ��������
	if( (sRecStr!=NULL)&&(SstrBuf[0] != '$'))
	{
		//strcpy(sRecStr, SstrBuf);
		for(i=0; i< ReportMaxSignalNum; i++)
		{
			sRecStr[i] = SstrBuf[i];
		}
		printf("libUpdateEquipt.so  Report   SstrBuf=%s\n", SstrBuf);
		memset(SstrBuf, '\0', sizeof(SstrBuf));  //��� ��������
		SstrBuf[0] = '$'; 		
	}
	//��ֵ �ɼ�����
	
	if( (fdata != NULL) && (fdata[0] > -1) && (fdata[0] < ReportMaxEquiptNum) )
	{
		int portRunEquiptAddr = fdata[0];
	
		for(i=0; i< ReportMaxSignalNum; i++)
		{
			RdataBuf[portRunEquiptAddr][i] = fdata[i];
		//	printf("%f ", fdata[i]);
		}
		
		
		//��ӡ �±������豸id
		if( bTestFlag  )
   		{
       		 WriteAsc( Report_FILE, "\r\nReport: EquiptId=%d\n", fdata[0] );
    	}
	}else{
		return FALSE;
	}
	
	int iAddr = fdata[0];


	return TRUE;
}

/*****************************************************************/
// �������ƣ�requestSpone
// ���������s���ϱ� ͨ������Ļذ�����   �ڲ��ӿ�UpDataServer ����
// ���������hComm - ͨ�ž��, 
//           RecvStr - ����������  nStrLen - ����������ֽڳ��ȡ�
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����pCmdStr ע��Ϊ8bit ���ֽ�����  ���ַ���
/*****************************************************************/
bool requestSpone(HANDLE hComm, unsigned char *sRecStr, int nStrLen)
{


	//��ӡ�ذ�
	printf("UpDataEquipt.so>>>�ϱ�����ذ�:\n");
	int u;
	for(u=0;u<nStrLen;u++)
	{
		printf("%02X ", sRecStr[u]);
	}
	printf("\n");
	if( bTestFlag  )
   	{
		WriteAsc( UPdata_FILE, "\r\n UpDataServer->requestSpone content :\r\n" );
		WriteHex( UPdata_FILE, (char*)sRecStr, nStrLen);
    }

	/***************�豸��ַ1��ʼ  �źŵ�ַ0��ʼ******************/
	 //������ر���
	int lWritten = 0;
	unsigned char SBuf[ReportMaxSignalNum*4+10];   
	char EAddr = -1,ModeFlag = -1;  //�豸��ַ ������
	unsigned short SAddr = 0, ByteLenth = 0; //�źŵ�ַ �ֽڳ���
	unsigned short CRC = CRC16( sRecStr, 6 );		// У����
	unsigned char Crc1 = (BYTE)((CRC>>8) & 0x00FF);    // ��λ��ǰ
	unsigned char Crc2 = (BYTE)(CRC & 0x00FF);		   // ��λ�ں�
				  
	if(sRecStr[6]==Crc1 && sRecStr[7]==Crc2)	 //1.��֤CRC
	 {

//	   printf("UpDataEquipt.so>>>CRCУ����ȷ!\n");
		EAddr = sRecStr[0];
		ModeFlag = sRecStr[1];	
		SAddr = sRecStr[2]&0x00FF;
		SAddr = (SAddr<<8)|(sRecStr[3]&0x00FF);
		ByteLenth = sRecStr[4];
		ByteLenth = (ByteLenth<<8)|(sRecStr[5]&0x00FF);
		if(EAddr>0 || EAddr<ReportMaxEquiptNum+1) //2.��֤�豸��ַ
		{
								 
			if(ModeFlag==0x03) //3.��֤������  �������ݰ�
			{
				//printf("UpDataEquipt.so>>>��ӡ�����������:��ַ=%d ������=%d �źŵ�ַ=%d �ź��ֽ���=%d \n", EAddr,ModeFlag,SAddr,ByteLenth);
				if(SAddr>-1 && ByteLenth>-1 && SAddr+ByteLenth/2<ReportMaxSignalNum) //4.��֤�źŵ�ַ //5.��֤�źų���
					{
						SBuf[0] = EAddr;
						SBuf[1] = ModeFlag;
						SBuf[2] = ByteLenth;  //�˴�Ŀǰ������ ���ֵ��255 ByteLenth>255�ᱻת��
						if(ByteLenth == 0) ByteLenth = ReportMaxSignalNum;
						int len = ByteLenth/2;
						SBuf[2] = 4*len;
						int n = 0 ;
						for(n=0;n<len;n++)
						{

									float f_data = RdataBuf[EAddr][SAddr+n];
									SBuf[3+4*n+0] = (unsigned char)(((int)f_data>>8*3)&0xFF);
									SBuf[3+4*n+1] = (unsigned char)(((int)f_data>>8*2)&0xFF);
									SBuf[3+4*n+2] = (unsigned char)(((int)f_data>>8*1)&0xFF);
									SBuf[3+4*n+3] = (unsigned char)(((int)f_data>>8*0)&0xFF);
						}
						CRC = CRC16( SBuf, 3+4*len ); 	   // У����
						Crc1 = (BYTE)((CRC>>8) & 0x00FF);	 // ��λ��ǰ
						Crc2 = (BYTE)(CRC & 0x00FF);		 // ��λ�ں�
						SBuf[3+4*len] = Crc1;
						SBuf[3+4*len+1] = Crc2;					
						//���� �ذ�
						PurgeComm( hComm, PURGE_TXCLEAR );
						WriteFile( hComm, (char*)SBuf, 5+4*len, &lWritten, NULL );
						//��ӡ�ذ�
						printf("UpDataEquipt.so>>>�ϱ��ذ�:\n");
						for(n=0;n<5+4*len;n++)
							{
								printf("%02X ", SBuf[n]);
							}
						printf("\n");
						if( bTestFlag  )
   						{
      						WriteAsc( UPdata_FILE, "\r\n UpDataServer->requestSpone->to ask:\r\n" );
							WriteHex( UPdata_FILE, (char *)SBuf, 5+4*len);
   					    }
						return true;
					}
			}
			else if(ModeFlag==0x04)		 //���Ϳ��������
			{

					if(SAddr>-1 && ByteLenth>-1 && SAddr <ReportMaxSignalNum) //4.��֤�źŵ�ַ //5.��֤�źų���
					{
							//��������� buf
							int n = 0 ;
							for(n=0;n<nStrLen;n++)
							{
								SstrBuf[n] = sRecStr[n];
								SBuf[n] = sRecStr[n];
							}	
							//���� �ذ�
							PurgeComm( hComm, PURGE_TXCLEAR );
							WriteFile( hComm, (char*)SBuf, nStrLen, &lWritten, NULL );
							printf("UpDataEquipt.so>>>�ϱ��ذ�:\n");
							for(n=0;n<nStrLen;n++)
								{
									printf("%02X ", SBuf[n]);
								}
							printf("\n");
							if( bTestFlag  )
   							{
								WriteAsc( UPdata_FILE, "\r\n UpDataServer->requestSpone->to ask:\r\n" );
								WriteHex( UPdata_FILE, (char *)SBuf, nStrLen);
   					   		}
							return true;
					}
			}
		}	
				  
	}

	return false;
}

/*****************************************************************/
// �������ƣ�UpDataServer
// ���������s�Ե������豸���ϱ���������  Ŀǰ  ʹ�øýӿ�  ���ڶԵ������豸�� �ϱ��շ� ����
// ���������hComm - ͨ�ž��, 
//           pCmdStr - �����  nStrLen - �����ֽڳ��ȡ�
// ���������
// ��    �أ�TRUE���ɹ���FALSE��ʧ�ܡ�    
// ��    ����pCmdStr ע��Ϊ8bit ���ֽ�����  ���ַ���
/*****************************************************************/
void *UpDataServer(void *arg)
{
	void *hComm = arg;
	int nRecv = 8;  //modbus tcp nsend=12; modbus rtu nsend=8;
	int lRead = 0;
	int lWritten = 0;
	char RecvStr[20];
	unsigned char SBuf[ReportMaxSignalNum*4+10];   
	//��ѭ�� ����
	while(true)
	{

		if( bTestFlag  )
   		{
				WriteAsc( UPdata_FILE, "\r\nUpDataServer �ϱ�����\r\n" );
    	}
		printf("UpDataEquipt.so �ϱ�����\n");
		//���ռ���
		ReadFile(hComm, (char*)RecvStr, nRecv, &lRead, NULL ); //���� �����
		
/*		lRead = 8;
		int i = 0;
		for(i=0;i<lRead;i++)
		{

			RecvStr[i] = buff6[i];
		}
		unsigned char buff1[8] = {0x01,0x03, 0x00,0x00,0x00,0x06,0xc5,0xc8}; //����OK
		unsigned char buff2[8] = {0x01,0x03, 0x00,0x00,0x00,0x64,0x44,0x21}; //����OK
		unsigned char buff3[8] = {0x01,0x03, 0x00,0x32,0x00,0x64,0xe5,0xee}; //����OK
		unsigned char buff4[8] = {0x01,0x03, 0x00,0x00,0x03,0xE8,0x45,0x74}; //���� ���� modbus �ذ���೤��255 ��Ҫ���ǻذ� ����λ
		unsigned char buff5[8] = {0x01,0x04, 0x00,0x03,0x00,0x63,0x40,0x23}; //����OK
		unsigned char buff6[8] = {0x02,0x03, 0x00,0x00,0x00,0x14,0x45,0xF6}; //����OK
*/		if(lRead>=8)
		{


			if(requestSpone(hComm, (unsigned char *)RecvStr, lRead))
			{	printf("UpDataEquipt.so  �ϱ����� �ذ� �ɹ�!\n");
				if( bTestFlag  )
   				{
					WriteAsc( UPdata_FILE, "\r\n UpDataServer->requestSpone END!\r\n" );

    			}
			}
		}
		printf("\n");
		
		//��� ���� ����
		PurgeComm( hComm, PURGE_RXCLEAR );
		Sleep(200); //200ms  
				

	}
}


