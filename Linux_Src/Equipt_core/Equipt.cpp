/*--------------------------------------------
文件名：Equipt.cpp 前2条为Fs102/XD100/XD300信号  2~10条信号为可设置信号 10~156为固定值信号
功能：开发sampler libhal 采集设备数据 程序
协议类型：modbus RTU ASCCII Q1 G1
date： 2016 12 6
made by: fjw0312
备注： 该文件使用于编译so文件与dll文件  可作为协议模板  
---------------------------------------------*/
//#define _LINUX_
#ifdef _LINUX_
	#include<stdlib.h>
	#include<string.h>
    #include "local_linux.h"
    bool bTestFlag = false;  //可修改 在运行中生成hex原始数据记录文件（需要生成时为true）
#else
    // 共用头文件的定义，适用于 VC++ 和 BC++部分
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

    #define _OCE_        // OCE驱动程序用

    #ifdef  _DLL_
        #undef  _OCE_    // DLL驱动程序用
    #endif
    #ifdef  _TSR_
        #undef  _OCE_    // TSR驱动程序用
    #endif
    #ifdef  _OCE_
        #define  _TSR_   // TSR驱动程序用
    #endif

    // 动态库使用部分
    #ifdef _DLL_
        #pragma comment(lib,"Ws2_32.lib")
        #include "local.h"
        #include "snuoci5.h"

        // 跟踪测试标志：TURE－跟踪测试运行，要调试信息；FALSE－普通运行。
        bool bTestFlag = false;
        extern int nExtOci5ID;
        // 跟踪测试用的数据记录文件名，请根据具体协议更改！
        // 如果记录告警发生时的采集原始数据请将bWriteErrLog置位。
        BOOL bWriteErrLog=FALSE;
        //BOOL bWriteErrLog=TRUE;
        #define HEXLOG_FILE  "YDC9310_made_FJW.HEX"    // 告警记录文件名称
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

 // 跟踪测试用的数据记录文件名，请根据具体协议更改！
char debugPath[]="/data/mgrid/";   //生成调试文件的路径
char HEX_FILE[]={"fjw_test.hex"};
char ASC_FILE[]={"fjw_test.asc"};
char ErrLog_FILE[]={"fjw_test.Log"};
char Report_FILE[]={"fjw_report.Log"};
char UPdata_FILE[]={"fjw_UPdata.Log"};




#define ReportMaxEquiptNum   20
#define ReportMaxSignalNum   801
float RdataBuf[ReportMaxEquiptNum][ReportMaxSignalNum];      //数据上报的  采集数据  设备最多20个  信号数据最多803条
char  SstrBuf[ReportMaxSignalNum] = {'$'};                           //数据上报的  采集数据

float buf[10]={0,1,2,3,4,5,6,7,8,9};

const int bySOI = 0x28;           // 头字符电总 "("
const int byEOI = 0x0D;           // 结束字符
const int nMaxChanelNo = 400;    // 最大采集信号个数
const int nMaxDllBufNum = 400;   // DLL最大接收缓冲
#define nMaxBufLen	512         //接收缓冲区的字节数量

#define nMaxControlNo   0      // 最大控制命令号
#define nMinControlNo   0

//选择是否是modbus  协议，宏定义
#define  MODBUS_RTU
//#define  MODBUS_ASCII
//#define  Q_G

// 数据结构表定义：
//      要求尽量按每个回收数据包的内容定义多个表；
//      对于结构相同的应避免重复定义，而放入程序循环，如电源模块参数。

// TODO: 请将数据处理部分的表结构、内容定义在此
//       要求结构、内容后均有注释
// 数据包解析结构
typedef struct
{
    int nType;          // 数据类型:0-结束标志,10-单字节开关量,11-单字节模拟量...
    int nOffset;        // 数据起始地址：在本数据包中的起始位置。
    int nScaleBit;      // 对于开关量,即位号；模拟量即比例关系,如10倍等。
    int nChannel;       // 系统分配的通道号，对于模块的参数，请用偏移表示，也可直接指定。

}DATASTRUCT;

//系统参数
// 
#define nR_DataStart0 0
DATASTRUCT arR_Data0[] =
{
    {21,  0,    100,  0},  // fs102 温度
    {21,  2,    100,  1},  //  fs102 湿度
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

//控制命令
//命令号    功能
// 11        清除最大最小值允许(0-禁止,1-允许)
// 12        最大最小值复位
// 13        电能累加值复位
// 14        最大电能需求量复位
// 15		 系统接线方式(0-3相4线,1-1相2线,2-3相3线,3-3相3线平衡,4-1相3线,5-3相4线平衡)
// 16		 最大电能需量取样时间(分钟)
// 17        最大电能需量滑窗时间(0－15分钟,1-30分钟)
// 18		 锁键盘(0-不锁,1-锁)
// 19		 语言(0-中文,1-英文)
// 20		 PT
// 21		 CT



//
// 当生成TSR或OCE时如果所需的接收缓冲区较大时请尽量用全局变量，因为它们的堆栈
// 较小。为支持多线程重入，动态库必须用局部变量，如确实需要用于写操作的全局变
// 量可用线程局部存储，在Local.H结构CGlobalInfo中增加一个元素。




// 版本信息，在作修改后应补充到此栏中，以便在应用程序可查阅。
// 供 TSR 和 DLL 使用。
//以下内容为用DLLTEST测试时显示的设备简要信息
char Info[] = {
    "☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆\n"
        "       KStarYDC9310C UPS \n"
        "			G1 G2 G3协议	   \n"
        "          版本：V1.0 测试版   \n"
        "  (通讯格式)：9600(可调),n,8,1\n"
        "    数据传输方式：串口 RS485 \n"
        " author: fjw0312 E-mail:fjw0312@163.com\n"
        "   \n"  
        "	\n"
        "    \n"
        "    \n"
        "	\n"
        " 编译时间：\n"               // 请勿在此后增加信息!
        "                                \n"    // 请保留此行(分配内存用)
};

//*****************************************************************
// 函数名称：DLLInfo();
// 功能描述：动态库版本中将信息包 Info 输出，以作版本信息等标志。
// 输入参数：Info--版本信息数组
// 输出参数：
// 返回：    版本信息数组
// 其他：
//*****************************************************************
char* DLLInfo( )
{
    int nStrLen = strlen( Info );
    sprintf( Info+nStrLen-30, "%s ", __DATE__ );
    strcat( Info, __TIME__ );
    strcat( Info, " \n" );

    return Info;
}

//CRC校验
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
//LRC校验
void LRC( unsigned char *Frame, int len )
{
    unsigned char v[2];
    unsigned char R = 0;

    for( int i = 1; i < len; i += 2 )  //i=1开始表示舍弃了Frame[0]
    {
        v[0] = Frame[i] < 0x3a ? Frame[i] - 0x30 : Frame[i] - 0x37;          //获取ASCII字符的数值 
        v[1] = Frame[i+1] < 0x3a ? Frame[i+1] - 0x30 : Frame[i+1] - 0x37;    //获取ASCII字符的数值 
        R += ( v[0] << 4 ) + v[1];   //把len个字符数据的和加起来
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
// 函数名称：ASSERT
// 功能描述：此函数是断言函数，调试使用。
// 输入参数：布尔型的条件值
// 输出参数：满足条件时，不做任何事，不满足条件，且处理于调试状态时，提
//           示出错信息，并选择继续运行，还是退出。
// 返回：    版本信息数组
// 其他：
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
// 函数名称：StrToK
// 功能描述：将字符串S以cSep为分隔符分段
// 输入参数：S-源字符串，D-第一段字符串，cSep-分隔符
// 输出参数：
// 返回：    第一段字符串的个数
// 其他：
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
// 函数名称：SendString
// 功能描述：向通讯口句柄hComm 发送字符串sSendStr的前 nStrLen个字符
// 输入参数：hComm - 通讯口句柄,sSendStr - 需要发送的字符串,
//           nStrLen - 需要发送的字符个数
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其他：
/*****************************************************************/
BOOL SendString( HANDLE hComm, BYTE* sSendStr, int nStrLen )
{
    DWORD lWritten=0;           // 实际向设备发送的字符个数
    
    ASSERT( hComm!=0 && sSendStr!=NULL && nStrLen>0 );
    
    // 为了减少干扰，发送前清发送/接收缓冲区
    PurgeComm( hComm, PURGE_TXCLEAR );
    PurgeComm( hComm, PURGE_RXCLEAR );
    
    // 对于部分设备，需在发送数据包前暂停片刻，请打开下面的注释，并确认时间（单位：ms）。
   // Sleep( 500 );
    
    // 向设备 hComm 发送字符串 sSendStr 的前nStrLen个字符，并将实际写入个数返回到 lWritten 中。
    WriteFile( hComm, (char*)sSendStr, nStrLen, &lWritten, NULL );

#ifdef _TSR_
    WriteString( sSendStr, 1, 23, 0x1f );
#endif           
    

    // 如果是跟踪测试，且非OCI－5方案(nExtOci5ID缺省为-1)，则记录调试信息。
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
// 函数名称：ReceiveString
// 功能描述：从设备 hComm 中向字符串 sRecStr 读入字符
// 输入参数：hComm - 通讯口句柄,sRecStr - 接收字符串指针,
//           nStrLen - 需要接收的字符个数
//           sSendStr- 发送的字符串指针
// 输出参数：实际接收的字符个数,接收到的字符串
// 返    回：    
// 其他：                   本函数仅支持标准modbus rtu协议  且严格校验
/*****************************************************************/
int ReceiveString(
                  HANDLE hComm,     // 通讯口句柄
                  BYTE* sSendStr,   // 发送的字符串指针
                  BYTE* sRecStr,    // 接收字符串指针
                  int nStrLen       // 需要接收的字符个数    nStrLen = -1;
                  )
{
    DWORD lRead=0;      //实际从设备读取的字符个数(正确部分)    
    int i = 0;          //总共从设备读取的字符个数(含误码)
    BYTE recvChar[262] = {0}; //总共从设备读取的字符(含误码)
    /*     
     * 前两个字节校验
     * ------------------------------------------------------------- 
     * 地址  功能码  长度  (数据.......校验) 
     * -------------------------------------------------------------
     */
    // 对于数据包有定长的情况：推荐使用一次性读完
    ReadFile( hComm, (char*)sRecStr, 1, &lRead, NULL );  //获取modbus设备地址
    memcpy(recvChar+i,sRecStr,1);
    i++;    
    do 
    {
        if ( lRead && sRecStr[0] == sSendStr[0] )//比较地址
        {//地址验证正确,验证下一个
            ReadFile( hComm, (char*)sRecStr+1, 1, &lRead, NULL );   //获取功能码
            memcpy(recvChar+i,sRecStr+1,1);
            i++;

            if ( sRecStr[1] == sSendStr[1] )  //功能码比较
            {
                //读取第三位:长度位
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
            {//功能码验证错误 
                sRecStr[0] = sRecStr[1];
            }    
        }
        else
        {//地址验证错误,如果只读了一个字节时则从设备中读下一个字节
            ReadFile( hComm, (char*)sRecStr, 1, &lRead, NULL );
            memcpy(recvChar+i,sRecStr,1);
            i++;
        }
    } while( lRead && i<261 );
    // 跟踪调试的接收信息处理：回应信息记录。
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
// 函数名称：ReceiveString
// 功能描述：从设备 hComm 中向字符串 sRecStr 读入字符
// 输入参数：hComm - 通讯口句柄,sRecStr - 接收字符串指针,
//           nStrLen - 需要接收的字符个数                  nStrLen = -1;
// 输出参数：实际接收的字符个数
// 返    回：TRUE－成功；FALSE－失败。    
// 其他：                               本函数仅支持标准modbus协议  可用于与支持数据长度占2byte的协议
/*****************************************************************/
int ReceiveString1(
                  HANDLE hComm,     // 通讯口句柄
                  BYTE* sRecStr,    // 接收字符串指针
                  int nStrLen       // 需要接收的字符个数
                  )
{
    DWORD lRead=0;  //实际从设备读取的字符个数
    
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
		//处理回包字节2byte
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
    // 请注意 TSR.CPP文件 ReadFile函数中的超时时间 lTimeOut和结束字符 byEOI
    //
    ////////////////////////////////////////////////////////////////////////

    // 跟踪调试的接收信息处理：回应信息记录。
 // 如果是跟踪测试，且非OCI－5方案(nExtOci5ID缺省为-1)，则记录调试信息。
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
// 函数名称：ReceiveString2
// 功能描述：从设备 hComm 中向字符串 sRecStr 读入字符
// 输入参数：hComm - 通讯口句柄,sRecStr - 接收字符串指针,
//           nStrLen - 需要接收的字符个数                       nStrLen = -1;
// 输出参数：实际接收的字符个数
// 返    回：成功采集的字符个数
// 其他：                   本函数主要适用于电总\Q1\G1   等 使用于回包是0X0D结尾的包 
//*****************************************************************
int ReceiveString2(
                  HANDLE hComm,     // 通讯口句柄
      //            int nUnitNo,      // 采集器单元地址
                  BYTE* sRecStr,    // 接收字符串指针
                  int nStrLen       // 需要接收的字符个数
                  )
{
    DWORD lRead=0;  //实际从设备读取的字符个数
    
    ASSERT( hComm!=0 && sRecStr!=NULL );

    // 对于数据包有定长的情况：推荐使用一次性读完
    if( nStrLen != -1 )
    {
        ReadFile( hComm, (char*)sRecStr, nStrLen, &lRead, NULL );
    }
    else
    {
        // 对于只能按特殊结束字符的情况：如回车0x0D
        int i=0;    // 计数器：接收字符个数
        do
        {
            ReadFile( hComm, (char*)sRecStr+i++, 1, &lRead, NULL );
        }while( lRead==1 && sRecStr[i-1]!=byEOI && i<nMaxDllBufNum-1 );
        lRead = i;  // 为了方便后面的处理，将接收的字符个数统一保留。
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
// 函数名称：ReceiveString3
// 功能描述：从设备 hComm 中向字符串 sRecStr 读入字符
// 输入参数：hComm - 通讯口句柄,sRecStr - 接收字符串指针,
//           nStrLen - 需要接收的字符个数             nStrLen = -1;
// 输出参数：实际接收的字符个数
// 返    回：成功采集的字符个数
// 其他：                   本函数主要适用于modbus_ascii  等
//*****************************************************************
int ReceiveString3( HANDLE hComm, BYTE* sRecStr, int nStrLen )
{
    DWORD lRead=0;
	int i = 0;
	int j = 0;
 // 对于数据包有定长的情况：推荐使用一次性读完
    if( nStrLen != -1 )
    {
        ReadFile( hComm, (char*)sRecStr, nStrLen, &lRead, NULL );
    }
    else
    {
        // 对于只能按特殊结束字符的情况：如回车0x0D      
        do
        {
            ReadFile( hComm, (char*)sRecStr+i, 1, &lRead, NULL );
			 i++;
			 j++;
        }while( (lRead==1) && (sRecStr[i-1]!=0x0A) && (i<nMaxDllBufNum-1) );

         lRead = i;  // 为了方便后面的处理，将接收的字符个数统一保留。
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
// 函数名称：GetResponeData
// 功能描述：查询数据包数据
// 输入参数：hComm - 通信句柄, nUnitNo - 采集器单元地址,
//           sSendStr - 要发送的命令串, Frame - 接收数据缓冲区
//           nSend - 发送命令串的长度, nRecv - 接收的数据长度
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
/*****************************************************************/

BOOL GetResponeData( HANDLE hComm,  BYTE *sSendStr, BYTE* Buf, int nSend, int nRecv)     
{
    int  nNum=0;    // 重试次数计数器：提高通讯成功率。
    int  nRet=0;    // 接收函数返回值：实际接收到的字符个数。

    ASSERT( hComm!=0 && sSendStr!=NULL && Buf!=NULL );
    
    do
    {
        if( !SendString( hComm, sSendStr, nSend ) )     // 发送命令串
        {
            return FALSE;
        }
        Sleep(200*nNum);

#ifdef MODBUS_RTU    
		nRet = ReceiveString( hComm, sSendStr, Buf, nRecv );     // 接收数据(严格验证 modbus_rtu)
	//  nRet = ReceiveString1( hComm, Buf, nRecv);               // 接收数据modbus_rtu 接收数据2字节格式
#endif
#ifdef Q_G  
		nRet = ReceiveString2( hComm, Buf, nRecv );              // 接收数据   用于G1或Q1等
		if( Buf[nRet-1] == byEOI )  
				return TRUE;
#endif

#ifdef MODBUS_ASCII
		nRet = ReceiveString3( hComm, Buf, nRecv );              // 接收数据   用于modbus_ascii
		if(( Buf[nRet-2] == 0x0d ) &&(Buf[nRet-1] == 0x0A))
				return TRUE;
#endif
      
        if( nRet == 0 )        // 未收到任何信息
        {
            if( bTestFlag )    // 如果是跟踪测试，则记录调试信息。
            {
                WriteAsc( ASC_FILE, "\r\n未收到任何信息\r\n" );
                WriteAsc( HEX_FILE, "\r\n未收到任何信息\r\n",18 );
            }           
        }
        else if( nRet< 6 )
        {

            if( bTestFlag )    // 如果是跟踪测试，则记录调试信息。
            {
                WriteAsc( ASC_FILE, "\r\n未收到足够信息\r\n" );
                WriteAsc( HEX_FILE, "\r\n未收到足够信息（回包字节小于6）\r\n" ,18);
            }
          
        }
        else if( sSendStr[0]!=Buf[0] || sSendStr[1]!=Buf[1] )
        {
            if( bTestFlag )    // 如果是跟踪测试，则记录调试信息。
            {
                WriteAsc( ASC_FILE, "\r\n关键字错误\r\n" );
                WriteAsc( HEX_FILE, "\r\n回包的地址或功能码错误\r\n" ,18);
            }

        }
        else
        {
            //数据校验
            unsigned short CRC = CRC16(Buf,nRet-2);
            if( Buf[nRet-2]==(BYTE)((CRC>>8)&0x00ff) && Buf[nRet-1]==(BYTE)(CRC&0x00ff) )
            {
                return TRUE;
            }
            else
            {
                if( bTestFlag )    // 如果是跟踪测试，则记录调试信息。
                {
                    WriteAsc( ASC_FILE, "\r\n校验错误\r\n" );
                    WriteAsc( HEX_FILE, "\r\n校验错误\r\n" ,18);
                }
                
            }
        }
		
    }while( ++nNum < 3 );
    
    return FALSE;
}



//*****************************************************************
// 函数名称：Fix_Data
// 功能描述：对已存放在全局变量数组Frame[]中的数据根据数据结构所
//           定义的对应关系进行解析
// 输入参数：Frame - 原始数据数组, fData - 解析后的数据
//           strData - 用于进行数据解析的数据结构
// 其    他：
//*****************************************************************
void Fix_Data(
              float* fData,             // 处理好的数据缓冲区
              DATASTRUCT Data_Struct[],     // 要处理的数据结构
              BYTE *Frame )             // 要处理的字符串
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
    
    while( Data_Struct[nLoop].nType )       // 循环直到结束：类型=0  开关量1位数  modbus数据量2位数
    {
		nData = 0;
        switch( Data_Struct[nLoop].nType )  // 根据数据类型作相应处理。  无符号为单数
        {
			
			case 1:   // 1字节开关量,每位表示一个开关量,符合标准modbus的1，2号指令
                fData[Data_Struct[nLoop].nChannel] = (float)
                    ( ( Frame[Data_Struct[nLoop].nOffset] 
                        >> Data_Struct[nLoop].nScaleBit ) & 0x01 );
                break;
				
			case 2:    // 2字节开关量
				for( i=0; i<2; i++ )
					for(int j=0;j<8;j++)
						fData[Data_Struct[nLoop].nChannel+i*8+j] = (float)
						((Frame[Data_Struct[nLoop].nOffset+1-i] >> j) & 0x01);
                break;
			
			
			case 10:    // 1字节模拟量有符号整型数
				fData[Data_Struct[nLoop].nChannel] = (float)
				(char)Frame[Data_Struct[nLoop].nOffset]/Data_Struct[nLoop].nScaleBit;
				break;
				
			case 11:   // 1字节模拟量无符号整型数
				fData[Data_Struct[nLoop].nChannel] = (float)Frame[Data_Struct[nLoop].nOffset];
                break;
			
			
            case 20:    // 2字节模拟量有符号整型数,高字节先进低字节后进, (低字在前高字在后)符合标准modbus的3,4号指令								
								    tmp =  Frame[Data_Struct[nLoop].nOffset];
									tmp = (tmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
								fData[Data_Struct[nLoop].nChannel] = (float)tmp/Data_Struct[nLoop].nScaleBit;
                break;
      		case 21:    // 2字节模拟量无符号整型数,先收高字节,再收低字节,(低字在前高字在后)符合标准modbus的3,4号指令
									temp =  Frame[Data_Struct[nLoop].nOffset];
									temp = (temp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
									fData[Data_Struct[nLoop].nChannel] = (float)temp/Data_Struct[nLoop].nScaleBit;
                break;
	        case 22:    // 2字节模拟量有符号整型数,先收低字节,再收高字节      
									tmp = (tmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
									tmp = (tmp << 8) + Frame[Data_Struct[nLoop].nOffset];
									fData[Data_Struct[nLoop].nChannel] = (float)tmp
									/ Data_Struct[nLoop].nScaleBit;
                break;
			case 23:    // 2字节模拟量无符号整型数,先收低字节,再收高字节      
									temp = (temp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
									temp = (temp << 8) + Frame[Data_Struct[nLoop].nOffset];
									fData[Data_Struct[nLoop].nChannel] = (float)temp
									/ Data_Struct[nLoop].nScaleBit;
                break;
			
            case 40:    // 4字节模拟量:浮点数
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
                    fData[Data_Struct[nLoop].nChannel] = *(float*)(&dwTmp) //--！！！！！！注意,变量dwTmp并不是ANSC标准格式，故这里用标准浮点指针指向这个变量，再取这个指针指向的值，从而把DWORD类型转换成标准float类型
						/ Data_Struct[nLoop].nScaleBit;
                break;
			case 41:    // 4字节模拟量:无符号整数(高字在前低字在后)
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTmp / Data_Struct[nLoop].nScaleBit;
                break;
			case 42:    // 4字节模拟量:有符号整数(高字在前低字在后)
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTemp / Data_Struct[nLoop].nScaleBit;
                break;		
			case 43:    // 4字节模拟量:无符号整数(低字在前高字在后)    **
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTmp = (dwTmp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTmp / Data_Struct[nLoop].nScaleBit;
                break;
			case 44:    // 4字节模拟量:有符号整数(低字在前高字在后)   **
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+2];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+3];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+0];
					dwTemp = (dwTemp << 8) + Frame[Data_Struct[nLoop].nOffset+1];
                    fData[Data_Struct[nLoop].nChannel] = (float)dwTemp / Data_Struct[nLoop].nScaleBit;
                break;
				
            case 50:    // 8字节模拟量:双精度浮点数
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

				
			case 200:   //最大处理8个字符（不支持含有非数字字符）       bcd   用于/Q1/G1
				fData[Data_Struct[nLoop].nChannel] = (float)atof((char*)Frame+Data_Struct[nLoop].nOffset);			
				break;
			case 100:   //1字节字符  的开关量  当开光亮字节要先-0x30时  0~9     ASCII   用于/Q1/G1
				 fData[Data_Struct[nLoop].nChannel] = (float)
				 ( Frame[Data_Struct[nLoop].nOffset] - 0x30 );
				break;	
			case 101: // 2字节字符 1byte模拟量解析开关量：'11'->17 ->bit0~bit7    ASCII 
				if(Frame[Data_Struct[nLoop].nOffset]==0x20 && Frame[Data_Struct[nLoop].nOffset+1]==0x20)   //处理 空格 报错
					fData[Data_Struct[nLoop].nChannel] = (float)9999;
				else
				{
					sscanf( (char *)Frame+Data_Struct[nLoop].nOffset, "%02x", &nData );
					fData[Data_Struct[nLoop].nChannel] = (float)((nData >> Data_Struct[nLoop].nScaleBit) & 0x01);
				}
				break;	
			case 110: // 2字节字符 1byte模拟量处理方式：  
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
			case 120: // 无符号4字节ASCII  2byte模拟量处理方式：'0010'->0016
				strncpy( sTemp, (char *)Frame+Data_Struct[nLoop].nOffset, 4 );
				sTemp[4] = 0;
				if( sTemp[0]==0x20&&sTemp[1]==0x20&&sTemp[2]==0x20&&sTemp[3]==0x20 )
					fData[Data_Struct[nLoop].nChannel] =  (float)9999;
				else
				{
					int h=0,l=0;
					sscanf( (char *)sTemp, "%02x", &h );      //先收高位
					sscanf( (char *)sTemp+2, "%02x", &l );
					fData[Data_Struct[nLoop].nChannel] = (float)(h*256+l)
						/ Data_Struct[nLoop].nScaleBit;
				}			
				break;	
			
			case 130: // 无符号6字节ASCII  2byte模拟量处理方式：'000013'->19
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
			
			case 140: // 无符号8字节ASCII  4byte模拟量处理方式：'00000013'->19
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
// 函数名称：GetData
// 功能描述：根据协议组织读取设备数据。
// 输入参数：hComm - 通信句柄, nUnitNo - 采集器单元地址,
//           pData - 上报数据缓冲区指针
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
/*****************************************************************/
BOOL GetData( HANDLE hComm, int nUnitNo, float* pData )
{    
    // 保证重入，动态库采用局部变量
	ASSERT( hComm!=0 && pData!=NULL );
    BYTE Frame[nMaxBufLen] = {0};  // 接收数据缓冲区
    BYTE sSendStr[20]={0};  // 发送数据缓冲区  
	unsigned short CRC;
	int i = 0;
    int nRecve = 0;
	
    int nSend = 8;  //modbus tcp nsend=12; modbus rtu nsend=8;	modbus ascii nsend=17
	int  frame_p = 3;  // modbus rtu 读取的有效数据区从第三字节开始
	
#ifdef MODBUS_RTU  
	unsigned char nSendBuf[][20] =
    {
		// 地址   功能码    寄存器地址   寄存器个数    校验（CRC）
		{ 0x00,    0x04,    0x00, 0x00,   0x00,  2,   0x00, 0x00 },   //[5]表示读取寄存器个数(一般一个寄存器2byte) 2字节的数据通道（通道数为该【5】值）
    //    { 0x00,0x03,0x00,0x00,0x00,33,0x00,0x00 },   //[5] 若数据为4byte的数据（通道数*2为该【5】值）
	//	{ 0x00,0x03,0x00,0x21,0x00,8,0x00,0x00 },
    //    { 0x00,0x02,0x00,0x00,0x00,8,0x00,0x00 }, 
	//	{ 0x00,0x03,0x02,0x00,0x00,75,0x00,0x00 },

    };
#endif	
#ifdef MODBUS_ASCII  
	unsigned char nSendBuf[][20] =
    {
		// 地址   功能码    寄存器地址   寄存器个数    校验（LRC）
		{ 0x00,    0x03,    0x00, 0x01,   0x00,  56,   0x00, 0x00 },   //[5]表示读取寄存器个数(一般一个寄存器2byte) 2字节的数据通道（通道数为该【5】值）
        { 0x00,    0x03,    0x00, 71,     0x00,  7,    0x00, 0x00 },   //[5] 若数据为4byte的数据（通道数*2为该【5】值）
		{ 0x00,    0x04,    0x00, 81,     0x00,  33,    0x00, 0x00 }, 
    //    { 0x00,0x02,0x00,0x00,0x00,8,0x00,0x00 }, 
	//	{ 0x00,0x03,0x02,0x00,0x00,75,0x00,0x00 },

    };
#endif
#ifdef Q_G		
		unsigned char nSendBuf[][20] = 
		{
			{ 'Q','1',0x0D },     //Q1<cr> 返回:(MMM.M NNN.N PPP.P QQQ RR.R SS.S TT.T b7b6b5b4b3b2b1b0<cr>
		
			{ 'R','E','A','D','0','1', 0x0d },		//	1输入电压、输入频率
			{ 'R','E','A','D','0','2', 0x0d },		//	2状态信息
			{ 'R','E','A','D','0','3', 0x0d },		//	3输出电压、输出频率
			{ 'R','E','A','D','0','4', 0x0d },		//	4输出电流、输出功因
			{ 'R','E','A','D','0','5', 0x0d },		//	5负载率、负载峰值比
			{ 'R','E','A','D','0','6', 0x0d },		//	6旁路电压、旁路频率
			{ 'R','E','A','D','0','7', 0x0d },		//	7BUS电压
			{ 'R','E','A','D','0','8', 0x0d },		//	8电池电压
			{ 'R','E','A','D','0','9', 0x0d },		//	9电池电流
			{ 'R','E','A','D','1','0', 0x0d },		//	10功率
			
		//	{'G', '1', 0x0D},  //G1
		//	{'G', '2', 0x0D},  //G2
		//	{'G', '3', 0x0D},  //G3
		};
#endif

	for( i=0; i<(sizeof(nSendBuf)/sizeof(nSendBuf[0])); i++ )
    {
#ifdef MODBUS_RTU		
		//设备地址
        nSendBuf[i][0] = (BYTE)nUnitNo;    //modbus-tcp 6位  modbus-rtu 0 位
		//需要CRC验证
		CRC = CRC16( nSendBuf[i], 6 );        // 校验码
        nSendBuf[i][6] = (BYTE)((CRC>>8) & 0x00FF);    // 高位在前
        nSendBuf[i][7] = (BYTE)(CRC & 0x00FF);         // 低位在后

		//接收nRecve的寄存器字节数+5
		if(nSendBuf[i][1]==0x02 || nSendBuf[i][1]==0x01)
		{
			nRecve=nSendBuf[i][5]%8?nSendBuf[i][5]/8+6:nSendBuf[i][5]/8+5;   //一个数据通道 数据接收1bye
		}
	    else
		{
			nRecve=nSendBuf[i][5]*2+5;    //一个寄存器 数据接收2byte
		}
		nSend = 8;
		frame_p = 3;		
#endif		
#ifdef MODBUS_ASCII                                           //地址      功能码         寄存器地址  
		sprintf((char *)nSendBuf[i], ":%02X%02X%02X%02X%02X%02X\0", nUnitNo, nSendBuf[i][1],nSendBuf[i][2],nSendBuf[i][3],
						nSendBuf[i][4],nSendBuf[i][5]); //寄存器个数
		//LRC校验		
		LRC( nSendBuf[i],13);
		nSend = 17;
		frame_p = 7;
		nRecve = -1;
		//nRecve=nSendBuf[i][5]*4+11;    //一个寄存器 数据接收2bye
		//WriteAsc( HEX_FILE, "\r\n printf send=:%s ", nSendBuf[i]);
		//WriteAsc( ASC_FILE, "\r\n printf send=:%s ", nSendBuf[i]);
#endif
#ifdef Q_G		
	    nSend = 7;           //此处注意手动修改
		if(i==0) nSend=3;
		frame_p = 0;
		nRecve = -1;
#endif

       //按地址接收数据       
		if(!GetResponeData( hComm, nSendBuf[i], Frame, nSend , nRecve ))
        {
			return FALSE;
		}
        else  //MODBUS RTU从第4位开始  MODBUS TCP从第十位开始
        {
            Fix_Data(pData+strChnlInfo[i].nMeasureStart, strChnlInfo[i].pMeasure,Frame+frame_p);
        }


		
       Sleep(500);
    }
	
//	*(pData+nR_DataStart0+10) = 100.00;   //强行赋予10通道值为100

	*(pData+nR_DataStart0+0) = *(pData+nR_DataStart0+0)-40;
	*(pData+nR_DataStart0+1) = *(pData+nR_DataStart0+1);
	//fjw test 200 模拟信号
	for(i=2; i<156; i++)
		{
			*(pData+nR_DataStart0+i) = i;
		}		
		//fjw_test 信号赋值
	for(i=2;i<10;i++)
		{
			*(pData+nR_DataStart0+i) = buf[i];
		}
   

	return TRUE;
}

/*****************************************************************/
// 函数名称：Read
// 功能描述：输出函数即PSMS与动态库/TSR等间的采集接口，采集并上报采
//           集的数据。采用本接口的有如下版本：PSMS4.0、TSR、
//           PowerStar、OCE等。
// 输入参数：hComm - 通信句柄, nUnitNo - 采集器单元地址,
//           pData - 上报数据缓冲区指针
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
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
// 函数名称：Query
// 功能描述：输出函数即PSMS与动态库的采集接口，采集并上报采集的数据。
//           采用本接口的有如下版本：V4.1以上4.x版/V5.0版 
// 输入参数：hComm - 通信句柄, nUnitNo - 采集器单元地址,
//           pData - 上报数据缓冲区指针
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
/*****************************************************************/
DLLExport BOOL Query(
                     HANDLE hComm,              // 通讯口句柄
                     int nUnitNo,                // 采集器单元地址
                     ENUMSIGNALPROC EnumProc,   // 枚举函数
                     LPVOID lpvoid              // 空类型指针
                     )
{
    float p[nMaxChanelNo+1]={ 0.0f };
    
    ASSERT( hComm!=0 );
    
    // 数据采集
    if ( !Read( hComm, nUnitNo, p ) )
    {
        return FALSE;
    }
    
    // 数据上报
    for( int i = 0; i<nMaxChanelNo; i++ )
    {
        EnumProc( i, p[i], lpvoid );
    }
    
    return TRUE;
}

// 跟踪测试入口函数
// 功能：在测试前，将全局标志 bTestFlag 置位；测试后，复位标志。
//*****************************************************************
// 函数名称：Test
// 功能描述：跟踪测试入口函数，在测试前，将全局标志 bTestFlag 置位；
//           测试后，复位标志。
// 输入参数：hComm - 通信句柄, 
//           nUnitNo - 采集器单元地址,
//           pData - 上报数据缓冲区指针
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
//*****************************************************************
DLLExport BOOL Test(HANDLE hComm,               // 通讯口句柄
                    int nUnitNo,                // 采集器单元地址
                    ENUMSIGNALPROC EnumProc,    // 枚举函数
                    LPVOID lpvoid)              // 空类型指针
{
    // 将调试标志置位
    bTestFlag = TRUE;
    
    // 调用采集函数采集数据，其中会因调试标志的置位而显示调试信息。
    BOOL bFlag = Query( hComm, nUnitNo, EnumProc, lpvoid );
    
    WriteAsc( ASC_FILE, "\r\n本次采集结束\r\n" );
    WriteAsc( HEX_FILE, "\r\n本次采集结束\r\n" );

    // 将调试标志复位
    bTestFlag = FALSE;
    
    return bFlag;
}
#endif


/*****************************************************************/
// 函数名称：SetDev
// 功能描述：控制命令的组织函数：根据协议的规定，组织命令
// 输入参数：hComm - 通信句柄, nUnitNo - 采集器单元地址,
//           nCmdNo - 命令号, fValue - 控制值/状态
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
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
		case 10:		//设定温度
			nReg= 0x0007;
			fValue *=10;
			break;
			
		case 11:	  //设定PID参数P  
			nReg= 0x000a;
			break;
		case 12:	   //设定PID参数I 
			nReg= 0x000b;
			break;
		case 13:	   //设定PID参数D  
			nReg= 0x000c;
			break;
			
		case 16:	   //设置控制方式
			nReg= 0x0010;
			break;
		case 17:	  //设置手动控制比例
			nReg= 0x0011;
			fValue *=10;
			break;
		case 18:	 //设置设备开关
			nReg= 0x0012;
			break;

		default:
			;
	
		}	
	
		sSendStr[2] = nReg>>8;			   //寄存器地址高位
		sSendStr[3] = nReg&0x00FF;		   //寄存器地址低位
					
		sSendStr[4] = (int)fValue>>8;		   //设定值高位
		sSendStr[5] = (int)fValue&0x00FF;	   //设定值低位 	
		
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
// 函数名称：Control
// 功能描述：PSMS V4.1及以上4.x版/V5.0版的控制函数。
// 输入参数：hComm - 通信句柄, nUnitNo - 采集器单元地址,
//           pCmdStr - 命令串
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：
/*****************************************************************/
bool Control( HANDLE hComm, int nUnitNo, char *pCmdStr )
{
	printf("libEquipt.so>>>Control into!\n");
    char sTarget[128];
    //float fValue = 0.0f;
    printf("libEquipt.so>>>Control 接收到的数据：%s\n", pCmdStr);
    
    ASSERT( hComm!=0 && pCmdStr!=NULL );
    
    //Get nCmdNo
    int nPoint = StrToK( pCmdStr,sTarget,',' );
    if( pCmdStr == NULL )
    {
        return FALSE;
    }
    int nCmdNo = (int)atoi( pCmdStr );    //为了安全，防止误送：命令号从11开始
    
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
// 函数名称：Write
// 功能描述：V4.0等版的控制函数。  目前  使用该接口  用于sampler 控制直接发送控制命令
// 输入参数：hComm - 通信句柄, 
//           pCmdStr - 命令串，  nStrLen - 命令字节长度。
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：pCmdStr 注意为8bit 的字节数据  非字符串
/*****************************************************************/
bool Write( HANDLE hComm, char* pCmdStr, int nStrLen)
{
	printf("libEquipt.so>>>Write 接收到的数据：");
	int i = 0;
	for(i=0;i<20; i++)
	{
		printf("%02X ", pCmdStr[i]);
	}
	printf("\n");
	printf("libEquipt.so>>>Write 接收到的数据结束\n");
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
// 函数名称：Report
// 功能描述：获取 第三方请求数据   API于sampler调用
// 输入参数：hComm - 通信句柄, sRecStr接收到的请求数据,
//           strlen请求数据的长度
//          EquiptId - 返回设备id号 
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：fdata: 地址 功能码 数据【800】 sRecStr：原生态 地址 功能码 地址···
//    float RdataBuf[20][803];      //数据上报的  采集数据
//    char  SstrBuf[803];           //数据上报的  控制数据
/*****************************************************************/
bool Report( HANDLE hComm,  float *fdata,  char* sRecStr) //控制命令#开头
{
	int i = 0;

	//赋值 控制数据
	if( (sRecStr!=NULL)&&(SstrBuf[0] != '$'))
	{
		//strcpy(sRecStr, SstrBuf);
		for(i=0; i< ReportMaxSignalNum; i++)
		{
			sRecStr[i] = SstrBuf[i];
		}
		printf("libUpdateEquipt.so  Report   SstrBuf=%s\n", SstrBuf);
		memset(SstrBuf, '\0', sizeof(SstrBuf));  //清空 发送内容
		SstrBuf[0] = '$'; 		
	}
	//赋值 采集数据
	
	if( (fdata != NULL) && (fdata[0] > -1) && (fdata[0] < ReportMaxEquiptNum) )
	{
		int portRunEquiptAddr = fdata[0];
	
		for(i=0; i< ReportMaxSignalNum; i++)
		{
			RdataBuf[portRunEquiptAddr][i] = fdata[i];
		//	printf("%f ", fdata[i]);
		}
		
		
		//打印 下报数据设备id
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
// 函数名称：requestSpone
// 功能描述对上报 通信请求的回包功能   内部接口UpDataServer 调用
// 输入参数：hComm - 通信句柄, 
//           RecvStr - 请求包命令串，  nStrLen - 请求包命令字节长度。
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：pCmdStr 注意为8bit 的字节数据  非字符串
/*****************************************************************/
bool requestSpone(HANDLE hComm, unsigned char *sRecStr, int nStrLen)
{


	//打印回包
	printf("UpDataEquipt.so>>>上报请求回包:\n");
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

	/***************设备地址1开始  信号地址0开始******************/
	 //定义相关变量
	int lWritten = 0;
	unsigned char SBuf[ReportMaxSignalNum*4+10];   
	char EAddr = -1,ModeFlag = -1;  //设备地址 功能码
	unsigned short SAddr = 0, ByteLenth = 0; //信号地址 字节长度
	unsigned short CRC = CRC16( sRecStr, 6 );		// 校验码
	unsigned char Crc1 = (BYTE)((CRC>>8) & 0x00FF);    // 高位在前
	unsigned char Crc2 = (BYTE)(CRC & 0x00FF);		   // 低位在后
				  
	if(sRecStr[6]==Crc1 && sRecStr[7]==Crc2)	 //1.验证CRC
	 {

//	   printf("UpDataEquipt.so>>>CRC校验正确!\n");
		EAddr = sRecStr[0];
		ModeFlag = sRecStr[1];	
		SAddr = sRecStr[2]&0x00FF;
		SAddr = (SAddr<<8)|(sRecStr[3]&0x00FF);
		ByteLenth = sRecStr[4];
		ByteLenth = (ByteLenth<<8)|(sRecStr[5]&0x00FF);
		if(EAddr>0 || EAddr<ReportMaxEquiptNum+1) //2.验证设备地址
		{
								 
			if(ModeFlag==0x03) //3.验证功能码  请求数据包
			{
				//printf("UpDataEquipt.so>>>打印解析的请求包:地址=%d 功能码=%d 信号地址=%d 信号字节数=%d \n", EAddr,ModeFlag,SAddr,ByteLenth);
				if(SAddr>-1 && ByteLenth>-1 && SAddr+ByteLenth/2<ReportMaxSignalNum) //4.验证信号地址 //5.验证信号长度
					{
						SBuf[0] = EAddr;
						SBuf[1] = ModeFlag;
						SBuf[2] = ByteLenth;  //此处目前有问题 最大赋值才255 ByteLenth>255会被转换
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
						CRC = CRC16( SBuf, 3+4*len ); 	   // 校验码
						Crc1 = (BYTE)((CRC>>8) & 0x00FF);	 // 高位在前
						Crc2 = (BYTE)(CRC & 0x00FF);		 // 低位在后
						SBuf[3+4*len] = Crc1;
						SBuf[3+4*len+1] = Crc2;					
						//发送 回包
						PurgeComm( hComm, PURGE_TXCLEAR );
						WriteFile( hComm, (char*)SBuf, 5+4*len, &lWritten, NULL );
						//打印回包
						printf("UpDataEquipt.so>>>上报回包:\n");
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
			else if(ModeFlag==0x04)		 //发送控制命令包
			{

					if(SAddr>-1 && ByteLenth>-1 && SAddr <ReportMaxSignalNum) //4.验证信号地址 //5.验证信号长度
					{
							//命令包保存 buf
							int n = 0 ;
							for(n=0;n<nStrLen;n++)
							{
								SstrBuf[n] = sRecStr[n];
								SBuf[n] = sRecStr[n];
							}	
							//发送 回包
							PurgeComm( hComm, PURGE_TXCLEAR );
							WriteFile( hComm, (char*)SBuf, nStrLen, &lWritten, NULL );
							printf("UpDataEquipt.so>>>上报回包:\n");
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
// 函数名称：UpDataServer
// 功能描述对第三方设备的上报服务函数。  目前  使用该接口  用于对第三方设备的 上报收发 服务
// 输入参数：hComm - 通信句柄, 
//           pCmdStr - 命令串，  nStrLen - 命令字节长度。
// 输出参数：
// 返    回：TRUE－成功；FALSE－失败。    
// 其    他：pCmdStr 注意为8bit 的字节数据  非字符串
/*****************************************************************/
void *UpDataServer(void *arg)
{
	void *hComm = arg;
	int nRecv = 8;  //modbus tcp nsend=12; modbus rtu nsend=8;
	int lRead = 0;
	int lWritten = 0;
	char RecvStr[20];
	unsigned char SBuf[ReportMaxSignalNum*4+10];   
	//死循环 服务
	while(true)
	{

		if( bTestFlag  )
   		{
				WriteAsc( UPdata_FILE, "\r\nUpDataServer 上报服务\r\n" );
    	}
		printf("UpDataEquipt.so 上报服务\n");
		//接收监听
		ReadFile(hComm, (char*)RecvStr, nRecv, &lRead, NULL ); //接收 请求包
		
/*		lRead = 8;
		int i = 0;
		for(i=0;i<lRead;i++)
		{

			RecvStr[i] = buff6[i];
		}
		unsigned char buff1[8] = {0x01,0x03, 0x00,0x00,0x00,0x06,0xc5,0xc8}; //测试OK
		unsigned char buff2[8] = {0x01,0x03, 0x00,0x00,0x00,0x64,0x44,0x21}; //测试OK
		unsigned char buff3[8] = {0x01,0x03, 0x00,0x32,0x00,0x64,0xe5,0xee}; //测试OK
		unsigned char buff4[8] = {0x01,0x03, 0x00,0x00,0x03,0xE8,0x45,0x74}; //测试 由于 modbus 回包最多长度255 故要考虑回包 长度位
		unsigned char buff5[8] = {0x01,0x04, 0x00,0x03,0x00,0x63,0x40,0x23}; //测试OK
		unsigned char buff6[8] = {0x02,0x03, 0x00,0x00,0x00,0x14,0x45,0xF6}; //测试OK
*/		if(lRead>=8)
		{


			if(requestSpone(hComm, (unsigned char *)RecvStr, lRead))
			{	printf("UpDataEquipt.so  上报请求 回包 成功!\n");
				if( bTestFlag  )
   				{
					WriteAsc( UPdata_FILE, "\r\n UpDataServer->requestSpone END!\r\n" );

    			}
			}
		}
		printf("\n");
		
		//清除 接收 缓存
		PurgeComm( hComm, PURGE_RXCLEAR );
		Sleep(200); //200ms  
				

	}
}


