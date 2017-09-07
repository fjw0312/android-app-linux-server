
#include "local_linux.h"
const int nMaxChanelNo =300;
extern char ASC_FILE[];
extern char HEX_FILE[];
extern char debugPath[];   //生成调试文件的路径


extern bool bTestFlag;


/*---------------------------端口配置--------------------------*/
// DiverLibPath --设备驱动动态库存放路径
// DiverLibName --设备驱动动态库名称 串口:"comm_std_serial.so" 网口:"comm_net_tcpip.so"
// DiverDevName --设备驱动节点名称   串口:"/dev/ttySn"         网口: ""
// DiverPara    --设备驱动端口参数   串口:"9600,n,8,1"         网口: "192.168.1.167:8001"
// DiverPortAttr--设备驱动端口主从模式   0x01:主机模式    0x00:从机模式 
// DiverTimeOut --设备驱动端口超时时间  
/***************************************************************/
#define DiverLibPath  "/data/mgrid/TestSampler/"       
#define DiverLibName  "comm_std_serial.so"
#define DiverDevName  "/dev/ttyS3"
#define DiverPara     "9600,n,8,1"
#define DiverPortAttr 0x01
#define DiverTimeOut  2000
/*---------------------------------------------------------*/
/*  //已放置于local_linux.h
//自定义一个端口信息结构体 变量
struct portMessage
{
	int  portId;                 //端口id号 eg:1
	char portDiverLibName[100]; //端口驱动动态库名称路劲 eg："/data/mgrid/TestSampler/comm_std_serial.so"
	char portDiverName[50];     //端口驱动名称路劲     eg: "/dev/ttyS3"
	char portPara[50];          //端口驱动配置参数     eg: "9600,n,8,1"
	int  portAttr;              //端口主从模式         eg: 1  主机模式
	int  portOutTime;           //端口超时时间         eg: 20000  20s
	char portRunEquiptLibName[100]; //端口运行的设备动态库名称（之后考虑兼容多个设备）
	int  portRunEquiptAddr;        //端口运行的设备地址（之后考虑兼容多个设备）
}; 
*/


enum DataType
{
	Data_Type_Int		= 0,
	Data_Type_Float		= 1,
	Data_Type_Char		= 2,
	Data_Type_Array		= 3,	//char *,i.	First byte inside indicates the length of the array EXCLUDING the first byte. 
								//In other words it indicates the length of the actual data.
	Data_Type_Struct	= 4,	//First byte indicates the number of elements inside the structure
								//The fields inside the structure follow the pattern described above

	Data_Type_Time		= 5,
};

enum SigEventType
{
	SigEvent_Type_Signal	= 0,	//It is only a signal,not an event
	SigEvent_Type_Common	= 1,	//the driver sends active/inactive information for each event
	SigEvent_Type_Card		= 2,	//It does not have start-stop time or active/inactive state,example card swipe. 
	SigEvent_Type_Fire		= 3,	//While the event existing, the driver will keep sending the event with active flag, 
									//but the extra information of the event may be different. Eg, fire alarm,when the 
									//alarm generated, an active event will be sent, and while the event existing, 
									//driver will keep sending the event(active), but in these events, the smoke density 
									//is different.when the alarm decease,an inactive event will be sent. 
};




bool Test(HANDLE hComm, int nUnitNo, ENUMSIGNALPROC EnumProc , LPVOID lpvoid)
{
    bTestFlag = TRUE;
    
    // 调用采集函数采集数据，其中会因调试标志的置位而显示调试信息。
    BOOL bFlag = Query( hComm, nUnitNo, EnumProc, lpvoid );
    
    WriteAsc( ASC_FILE, "\r\n本次采集结束\r\n", 16 );
    WriteAsc( HEX_FILE, "\r\n本次采集结束\r\n", 16 );

    // 将调试标志复位
    bTestFlag = FALSE;
    
    return bFlag;
}




bool Query( HANDLE hComm, int nUnitNo, ENUMSIGNALPROC EnumProc, LPVOID lpvoid ) 
{
	
	float fData[nMaxChanelNo];
	//SignalEvent sigevent;
	SignalEvent *sigevent;
	sigevent = new SignalEvent;
	sigevent->EventInfo = new char[30];
	
	if( Read( hComm, nUnitNo, fData) )
	{
		//sigevent->EventInfo = new char[30];
		WriteAsc(HEX_FILE,"\r\n采集成功!\r\n",20);

		sigevent->EventTime = 0;
		sigevent->Debug = 0;
		for(int i=0;i<nMaxChanelNo;i++)
		{
			sigevent->EventType = SigEvent_Type_Signal;
			sigevent->EventInfo[0] = (char)Data_Type_Float;
			memcpy( &(sigevent->EventInfo[1]),(char*)&fData[i],4 );	
			EnumProc( i,sigevent, lpvoid );
			//bTestFlag = TRUE;
			//WriteAsc( HEX_FILE, "\r\ndata[%d]:%f", i,fData[i]);
			//bTestFlag = FALSE;
		}



		delete [ ]sigevent->EventInfo;
		delete sigevent;
		return TRUE;
	}
	WriteAsc(HEX_FILE,"\r\n采集失败\r\n");
	delete [ ]sigevent->EventInfo;
	delete sigevent;

	return FALSE;
}



//when D.C exit the sampling process,D.C will call this function to notify the driver.
//driver may do some clean up here.
bool DriverStop(DeviceStruct *devstruct, HANDLE PortHandle)
{
	CommClose(PortHandle);
	return TRUE;
}
/*
//this interface is for driver debugging.
//DebugCommand=1,query data,2-send command
//when DebugCommand is 1, the 2nd paramter "Command" is meaningless.if DebugCommand is 2,the 
//content of "Command" is the command string for control() function
//ValuePair is channel number and channel value pair.where the channel value follows the same 
//encoding format
//DebugStrings is the output message to show the driver's running information.It is written by
//the driver's developer,it is something like prtintf information.


bool Debug(int DebugCommand, char * Command, DeviceStruct *devstruct, HANDLE PortHandle, char *ValuePair, char * DebugStrings)
{
	return TRUE;
}
*/


//when D.C is initializing the sampler thread,It call this function. Sometimes the communication port
//should be opened by driver,then in this function,the port should be opened and return the handle of 
//the potr to D.C. when this function returns,D.C will check if the PortHandle is NULL or not,if it is
//still NULL,that means the port should be opened by D.C itself.then D.C will open it. if the 
//PortHandle is not NULL then it means the port has been opened by driver,then D.C needn't to open port,
//it just needs to use this hanlde to communicate with driver.
//And,some of the devices will send infomation proactively,in this case,we can create a thread in this 
//function to create a thread to deal with the information sent by device proactively.


bool DriverInit(struct portMessage *portMess, HANDLE &PortHandle)
{
	int iError = 0;
	if(portMess==NULL)
	{		
		char libName[200];
		memset(libName,'0', 200);
		strcpy(libName, DiverLibPath);
		strcat(libName, DiverLibName);
 		PortHandle = CommOpen(libName,   DiverDevName,	DiverPara,  DiverPortAttr,	 DiverTimeOut,				
			                &iError);
		printf("打印libName=%s ----++++++----\n", libName);
	}else{
		PortHandle = CommOpen(portMess->portDiverLibName, portMess->portDiverName,	portMess->portPara, 
									portMess->portAttr,	 portMess->portOutTime,	 &iError);
		printf("打印libName=%s ----++++++----\n", portMess->portDiverLibName);
	}
	//增加 上报 启动上报服务 函数。
#ifdef _UPDATA_
	//创建 上报服务线程
		pthread_t pthread1;	int ret = pthread_create(&pthread1, NULL, UpDataServer, PortHandle);
		if(ret == -1)
		{		
			perror("IDUDRV 创建上报服务线程失败!  the pthread");		
			return -1;
		}
#endif

	return TRUE;
}


//the following are not export function,they are used internally.they needn't to be modified.
time_t ChangeTime( int year, int month, int date, int hour, int min, int sec )
{
	struct tm tmTime;
	time_t ltime;
	tmTime.tm_year= year-1900;
	tmTime.tm_mon = month-1;
	tmTime.tm_mday= date;
	tmTime.tm_hour=hour;
	tmTime.tm_min=min;
	tmTime.tm_sec=sec;

	ltime = mktime(&tmTime);

	return ltime;
}

// 设置串口通信参数
// lpParam:9600,N,8,1...
int SetPortAttr(HANDLE hFile,char* lpParam)
{
	int nRet = CommControl( (hFile), COMM_SET_COMM_ATTRIBUTES, (void*)lpParam, 0);
	return nRet;

}
BOOL ChangeMode( HANDLE hComm, long baud, char* mode )
{
	BOOL ret = FALSE;
	char FormatString[128] = {0,};
	sprintf(FormatString, "%d,%s", (int)baud, mode);
	ret = SetPortAttr(hComm, (char*)FormatString);
	ret = !ret; //ERROR_OK = 0; OTHERS = ~0
	return ret;
}

#define MAX_DEBUG_FILE_SIZE		(64*1024)	// 64K
#define MAX_FILE_PATH	100

/*==========================================================================*
 * FUNCTION : MakeFullFileName
 * PURPOSE  : to make a full path as pszRootDir/pszSubDir/pszFileName
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: OUT char       *pFullName   : buffer to save full name, size must
 *                                          be greater than MAX_FILE_PATH(256)
 *            IN const char  *pszRootDir  : if NULL or "", treat it as "."
 *            IN const char  *pszSubDir   : if NULL or "", treat it as "."
 *            IN const char  *pszFileName : 
 * RETURN   : char *: return pFullName
 * COMMENTS : 
 * CREATOR  : 
 *==========================================================================*/
char *MakeFullFileName(OUT char *pFullName,
							  IN const char *pszRootDir,
							  IN const char *pszSubDir,
							  IN const char *pszFileName)
{
	if ((pszRootDir == NULL) || (*pszRootDir == 0))
	{
		pszRootDir = ".";
	}

	if ((pszSubDir == NULL) || (*pszSubDir == 0))
	{
		pszSubDir = ".";
	}

	snprintf(pFullName, MAX_FILE_PATH, "%s/%s/%s",
		pszRootDir, pszSubDir, pszFileName);

	return pFullName;
}


static FILE *OpenDebugFile(char *pszFileName, long nMaxAllowedSize)
{
	FILE	*fp;
	char	szFullName[MAX_FILE_PATH]={0x00};
    long    nLogSize;

    /*fp = fopen( 
		MakeFullFileName(szFullName,"/var/", NULL, pszFileName),
		"a+b");*/
	strcpy(szFullName,debugPath);
	strcat(szFullName,pszFileName);
	fp = fopen( szFullName,"a+b");
    if( fp == NULL )
    {
        fprintf( stderr, "Open log file %s fail.\n", szFullName );
	    return NULL;
    }

	fseek(fp, 0l, SEEK_END);
    nLogSize = ftell(fp); // at the end of file, we use a+ open
    
	if(nLogSize >= nMaxAllowedSize )
    {
		int ret = ftruncate(fileno(fp), 0L);	// cut to 0
		if(ret)
		{
		
		}
		rewind(fp);
	}

	return fp;
}

//写ASCII码记录文件
BOOL WriteAsc(char *szFileName, LPCTSTR pszFormat, ...)
{
	va_list     arglist;
    char        szBuf[512];
    FILE        *fp;
	int			nLen;

	if(!bTestFlag)
		return FALSE;

    //ClearWDT(); /* clear watch dog */
	fp = OpenDebugFile(szFileName, MAX_DEBUG_FILE_SIZE);
    if( fp == NULL )
    {
        return FALSE;
    }

	va_start(arglist, pszFormat);
    nLen = vsnprintf(szBuf, sizeof(szBuf), pszFormat, arglist);
	va_end(arglist);

	if(szBuf[nLen-1] == '\n')	
	{
		szBuf[nLen-1] = 0; 
	}

    fprintf(fp, "%s\n", szBuf);

    fflush( fp );
    fclose( fp );

	return TRUE;
}



//写二进制记录文件
BOOL WriteHex(char *szFileName, char *pBuf, int nLen)
{
    FILE	*fp;
	int		k;

	if(!bTestFlag)
		return FALSE;

	fp = OpenDebugFile(szFileName, MAX_DEBUG_FILE_SIZE);
    if( fp == NULL )
    {
        return FALSE;
    }

    for( k=0; k<nLen; k++ )
    {
        fprintf( fp, "%02X ", (unsigned char)pBuf[k] );
    }

	//fprintf(fp, "\n");

	fflush( fp );
    fclose( fp );

	return TRUE;
}

//************************************************************
// 函数: TimeToString
//
// 说明:
//   时间转换为字符串
//    
// 参数：   
//   tmTime      时间
//   fmt         字符串格式
//   strTime     输出字符串
//   nLenStrTime 字符串长度
//
// 返回值: 
//   字符串的时间
//
//************************************************************
// 定义日期时间格式
#define TIME_CHN_FMT		       "%Y-%m-%d %H:%M:%S"	// YYYYMMDD hhmmss

char* TimeToString(IN time_t tmTime, const char *fmt, 
				   OUT char *strTime, IN int nLenStrTime)
{
	struct tm gmTime;

	gmtime_r(&tmTime, &gmTime);

	// convert time to yyyy-mm-dd hh:mm:ss
	strftime(strTime, (size_t)nLenStrTime, fmt, &gmTime);

	return strTime;
}
/*
 char szTimeLine[80];

// get current time and convert to YYYY-MM-DD hh:mm:ss.
TimeToString(time(NULL), TIME_CHN_FMT, szTimeLine, sizeof(szTimeLine));
*/
