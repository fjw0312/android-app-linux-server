/*==========================================================================*
 *    ChaoYF--fjw0312@163.com
 *    设备网络tcp 标准通信结构函数
 *
 *  PRODUCT  :
 *
 *  FILENAME : commsock.c
 *  CREATOR  : fjw0312               DATE: 
 *  VERSION  : V1.00
 *  PURPOSE  : Defines the APIs for TCP/IP communication port.
 *
 *
 *  HISTORY  : 
 *
 *==========================================================================*/

/*
#include "stdsys.h"		
#include "basetypes.h"
#include "err_code.h"

//#include "pubfunc.h"
//#include "halcomm.h"
//#include "new.h"
*/
#include "commsock.h"	/* The private head file for this module	*/

#define _DEBUG_TCPIP_PORT




#define WAIT_DATA_READY        1
#define WAIT_DATA_TIMEOUT     0
#define WAIT_DATA_ERROR       -1
#define WAIT_DATA_EXCEPT      -2

#define MAX_WAIT_INTERVAL		5	//the maximum wait time in seconds.
//#define LEN_RUN_THREAD_NAME		16	// the maximum length of the run thread name	
#define SELECT_MAX_WAIT_INTERVAL	MAX_WAIT_INTERVAL	// second
/*=====================================================================*
 * Function name: WaitFiledReadable  复用等待网络可读发送
 * Description  : 
 * Arguments    : int fd	:   文件描述符（可传入网络文件描述符）
 *                int nmsTimeOut: milliseconds 
 * Return type  : int : 1: data ready, 0: timeout, -1: wait error.
 *
 * Create       :     2000-10-30 17:06:54
 * Comment(s)   : Modified by , 2004-09-22
 *--------------------------------------------------------------------*/
int WaitFiledReadable(int fd, int nmsTimeOut/* milliseconds*/)
{
    fd_set fdset, fderr;
    struct timeval tv;
    int rv = WAIT_DATA_TIMEOUT;   //初始化 0

	while (nmsTimeOut > 0)
	{
		// need add ClearWDT() here.  //判断 每SELECT_MAX_WAIT_INTERVAL=5s一个周期处理等待

		if (nmsTimeOut < (SELECT_MAX_WAIT_INTERVAL*1000))  //设置的超时小于5s  时
		{
			tv.tv_usec = nmsTimeOut%1000*1000;		/* usec     */
			tv.tv_sec  = nmsTimeOut/1000;			/* seconds  */

			nmsTimeOut = 0;
		}
		else           //设置的超时大于5s  时
		{
			tv.tv_usec = 0;							/* usec     */
			tv.tv_sec  = SELECT_MAX_WAIT_INTERVAL;  /* seconds  */

			//RUN_THREAD_HEARTBEAT();

			nmsTimeOut -= (SELECT_MAX_WAIT_INTERVAL*1000);   //每次减去5s
		}
	//============使用网络复用select  --设置响应超时==========
	//1.定义文件描述符集并清空添加描述符
		FD_ZERO(&fdset);		//清空复用文件描述符/*  Initializes the set to the NULL set. */
		FD_SET(fd, &fdset);		// 添加文件描述符到集/*  Adds descriptor s to set. */

		fderr = fdset;  //FD_SET(fd, &fderr);
		
		/* determines the status of one or more sockets, waiting if necessary, 
		 * to perform synchronous I/O.     */
#ifdef unix
	//2.启动超时多路复用select()
		rv = select(fd+1, &fdset, NULL, &fderr, &tv);//返回值 无文件响应0   有>0  监视读文件描述符集合  异常处理文件描述符集合
#else
		rv = select(FD_SETSIZE, &fdset, NULL, &fderr, &tv);
#endif
	//3.void FD_ISSET(int fd, fd_set *fdset)； 查看集中响应的文件描述符（是否有改文件描述符响应）
		if (rv > 0)	// data ready or error.
		{
			rv = (FD_ISSET(fd, &fdset)) ? WAIT_DATA_READY	// 读文件描述符集合OK
				: (FD_ISSET(fd, &fderr)) ? WAIT_DATA_EXCEPT	// 异常处理文件描述符集合except
				: WAIT_DATA_ERROR;							// error??
			break;
		}
		else if (rv < 0)
		{
			rv = WAIT_DATA_ERROR;							// error??
			break;
		}
	}

    return rv;  /* select error, or time out *///超过传入设置超时时间 return 0; 有响应正常 return 1,响应异常-2；出错-1；
}


/*=====================================================================*
 * Function name: WaitFiledWritable   复用等待网络可写发送
 * Description  : 
 * Arguments    : int fd	:   文件描述符（可传入网络文件描述符）
 *                int nmsTimeOut: milliseconds
 * Return type  : int : 1: data ready, 0: timeout, -1: wait error.
 *
 * Create       :             2000-10-30 17:08:10
 * Comment(s)   : Modified by   , 2004-09-22
 *--------------------------------------------------------------------*/
int WaitFiledWritable(int fd, int nmsTimeOut/* milliseconds*/)
{
    fd_set fdset, fderr;
    struct timeval tv;
    int rv = WAIT_DATA_TIMEOUT;  //初始化 0

	while (nmsTimeOut > 0)
	{
		//判断 每SELECT_MAX_WAIT_INTERVAL=5s一个周期处理等待
		// need add ClearWDT() here.
		if (nmsTimeOut < (SELECT_MAX_WAIT_INTERVAL*1000))  //设置的超时小于5s  时
		{
			tv.tv_usec = nmsTimeOut%1000*1000;		/* usec     */
			tv.tv_sec  = nmsTimeOut/1000;			/* seconds  */

			nmsTimeOut = 0;
		}
		else    //设置的超时大于5s  时
		{
			tv.tv_usec = 0;							/* usec     */
			tv.tv_sec  = SELECT_MAX_WAIT_INTERVAL;  /* seconds  */

			//RUN_THREAD_HEARTBEAT();

			nmsTimeOut -= (SELECT_MAX_WAIT_INTERVAL*1000);   //每次减去5s
		}
	//============使用网络复用select  --设置响应超时==========
	//1.定义文件描述符集并清空添加描述符
		FD_ZERO(&fdset);		//清空复用文件描述符/*  Initializes the set to the NULL set. */
		FD_SET(fd, &fdset);		// 添加文件描述符到集/*  Adds descriptor s to set. */

		fderr = fdset;
		
		/* determines the status of one or more sockets, waiting if necessary, 
		* to perform synchronous I/O.     */
#ifdef unix
	//2.启动超时多路复用select()
		rv = select(fd+1, NULL, &fdset, &fderr, &tv);//返回值 无文件响应0   有>0  监视写文件描述符集合  异常处理文件描述符集合
#else
		rv = select(FD_SETSIZE, NULL, &fdset, &fderr, &tv);
#endif
	//3.void FD_ISSET(int fd, fd_set *fdset)； 查看集中响应的文件描述符（是否有改文件描述符响应）
		if (rv > 0)	// data ready or error.
		{
			rv = (FD_ISSET(fd, &fdset)) ? WAIT_DATA_READY	// OK
				: (FD_ISSET(fd, &fderr)) ? WAIT_DATA_EXCEPT	// except
				: WAIT_DATA_ERROR;							// error??
			break;
		}
		else if (rv < 0)
		{
			rv = WAIT_DATA_ERROR;							// error??
			break;
		}
	}

    return rv;  /* select error, or time out *///超过传入设置超时时间 return 0; 有响应正常 return 1,响应异常-2；出错-1；
}



/*==========================================================================*
 * FUNCTION : TCPIP_ParseOpenParams
 * PURPOSE  : Parse IP address and port from opening param
              解析端口参数
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char   *pOpenParams : The params will be parsed   eg：127.0.0.1:68888
 *            OUT DWORD *pdwIP       : dev IP 
 *            OUT int   *piPort      : dev Port
 * RETURN   : static BOOL : TRUE for success, FALSE for error params
 * COMMENTS : 
 * CREATOR  :                DATE: 2004-09-11 19:47
 *==========================================================================*/
BOOL TCPIP_ParseOpenParams(IN char	*pOpenParams, 
								  OUT DWORD *pdwIP, OUT int *piPort)
{
	char	*pszPort = NULL;
	char	*p = pOpenParams;
	char	szIP[64];	/* to save ascii IP: 255.255.255.255:65536  */
	int		i;
    //解析出 IP  端口
	for (i = 0; (i < (int)(sizeof(szIP)-1)) && (*p != 0); i++)
	{
		/* only 0-9, '.' and ':' are allowed	*/
		if (((*p >= '0') && (*p <= '9')) || (*p == '.'))
		{
			szIP[i] = *p;		
		}

		else if (*p == ':')
		{
			szIP[i] = 0;	/* replace the colon with '\0', IP end	*/
			pszPort = &szIP[i+1];		/* the port string start	*/
		}

		else		/* error character			*/
		{
			return	FALSE;
		}

		p++;
	}

	szIP[i] = 0;		/* end flag				*/

	if (pszPort == NULL)
	{
		return FALSE;	/* error parameters		*/
	}


#define STR_BROAD_IP	"255.255.255.255"
#define MAX_IP_PORT		65535

	// convert IP addr. inet_addr returns network format.  inet_addr:功能是将一个点分十进制的IP转换成一个长整数型数
	*pdwIP = ntohl((DWORD)inet_addr(szIP));// we need host format!!!  //

	if ((*pdwIP == (DWORD)-1) &&	// got a -1 addr, but not a broadcast
		(strncmp(pOpenParams, STR_BROAD_IP, sizeof(STR_BROAD_IP)) != 0))
	{
		TRACE( "[TCPIP_ParseOpenParams] -- invalid IP address %s.\n", 
			szIP );
		return FALSE;
	}

	/* convert port		*/
	*piPort = atoi( pszPort );	// host format.

	// test port is too big or not, greater than 65335
	if (*piPort > MAX_IP_PORT)
	{
		TRACE( "[TCPIP_ParseOpenParams] -- invalid IP port %s.\n",
			pszPort );
		return FALSE;
	}
	
#ifdef _DEBUG_TCPIP_PORT
	i = htonl(*pdwIP);	// convert IP to network format
	TRACE( "[TCPIP_ParseOpenParams] -- parse %s got ip: %s(%08x), port %d.\n",
		pOpenParams, 
		inet_ntoa(*(struct in_addr *)&i),
		(int)*pdwIP,
		(int)*piPort );
#endif //_DEBUG_TCPIP_PORT

	return TRUE;
}

/*==========================================================================*
 * FUNCTION : SetNonBlockSocket   设置网络非阻塞(或阻塞)
 * PURPOSE  : Set the block/non-block state of a socket
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: SOCKET  s         : 
 *            int     nNonBlock : non-zero to set the socket to non-block.
 * RETURN   : static int : 
 * COMMENTS : 
 * CREATOR  :                 DATE: 2004-09-13 20:03
 *==========================================================================*/
static int SetNonBlockSocket( SOCKET s, int nNonBlock )
{
    int rc;

    if ( nNonBlock != 0 )
    {
        nNonBlock = 1;
    }

#ifdef WIN32
    rc = ioctlsocket(s, FIONBIO, &nNonBlock );
#endif

#ifdef _PSOS
    rc = ioctl(s, FIONBIO, &nNonBlock );
#endif

#ifdef unix
    rc = fcntl( s, F_GETFL );    //读取文件描述词标志

    if ( nNonBlock )
    {
        rc |= O_NONBLOCK;     //NOBLOCK
    }
    else
    {
        rc &= ~O_NONBLOCK;
    }

    rc = fcntl(s, F_SETFL, rc);   //设置文件描述词标志
#endif

	/* server socket is nonblocking */
	return rc;
}

/*==========================================================================*
 * FUNCTION : CreateNetworkServer   创建网络服务端
 * PURPOSE  : Create a non-blocked TCP/IP or UDP server
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int    nWorkMode    : SOCK_DGRAM or SOCK_STREAM
 *            IN DWORD  dwServerAddr : the server addr, now is ignored
 *            IN short  nServerPort  : port in host format
 * RETURN   : static SOCKET : The server socket, INVALID_SOCKET for error.
 * COMMENTS : 
 * CREATOR  :                DATE: 2004-09-13 19:52
 *==========================================================================*/
SOCKET CreateNetworkServer(IN int nWorkMode,
								  IN DWORD dwServerAddr, IN short nServerPort )
{
	struct sockaddr_in sAddr;
	long   rc ;
    SOCKET  sServer;

    /*--------------------------------------------------------------------*/
	/* Create socket                                                      */
	/*--------------------------------------------------------------------*/
    if ( nWorkMode != SOCK_DGRAM )
    {
        nWorkMode = SOCK_STREAM;   //TCP传输     SOCK_DGRAM:UDP传输 
    }

	//1.创建socket
	//IPPROTO_TCP 、 IPPROTO_IP 、IPPROTO_UDP代表三种不同的协议，分别代表IP协议族里面的TCP协议、IP协议和UDP协议
    sServer = socket ( AF_INET, nWorkMode, IPPROTO_IP );  //IPPROTO_IP=0
	if ( sServer < 0 )
	{
#ifdef _DEBUG_TCPIP_PORT
        printf( "[CreateNetworkServer] -- call socket() fail: %s.\n",
            strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/
		return INVALID_SOCKET;
	}

	// 2.let the address can be reused.//2.设置ip 端口可重用
	rc = 1;
	setsockopt(sServer, SOL_SOCKET, SO_REUSEADDR, 
		(const char *)&rc, sizeof(rc));
		
	//3.本机地址ip和端口设置
	memset( ( void * )&sAddr, 0, sizeof(sAddr) );
    sAddr.sin_family      = AF_INET ;
	sAddr.sin_port        = htons((u_short)nServerPort);
	sAddr.sin_addr.s_addr = htonl(dwServerAddr);
	
	//4.将本机地址绑定 ip
    rc = bind ( sServer, (struct SC_SOCKADDR *)&sAddr, sizeof( sAddr ) );
	if ( rc != 0 )
    {
#ifdef _DEBUG_TCPIP_PORT
        printf( "[CreateNetworkServer] -- bind() server fail: %s.\n",
            strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/

        CLOSE_SOCKET( sServer );
		 
        return INVALID_SOCKET;
	}

	//5.设置 读写 是否阻塞
	/* make sure the socket is synchorous and non-blocking	*/
	/* if err = 1 ,set sServer to non-blocking mode. */
	/* if err = 0 ,set sServer to blocking mode. */
    /* under debug , to see sync or async */
    if (SetNonBlockSocket( sServer, 1 ) != 0 )  //设置为非阻塞模式
    {
#ifdef _DEBUG_TCPIP_PORT
        printf( "[CreateNetworkServer] -- set non-block socket error: %s.\n",
            strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/
        
        CLOSE_SOCKET( sServer );
        return INVALID_SOCKET;
    }

	//6.开始监听端口
    /* start to listen */
    if (nWorkMode == SOCK_STREAM)
    {
		/* accoring to experience, 5 is enough	*/
        if ( listen( sServer, 5 ) < 0 )
        {
#ifdef _DEBUG_TCPIP_PORT
            printf( "[CreateNetworkServer] -- listen() error: %s.\n",
                strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/

            CLOSE_SOCKET( sServer );
            return INVALID_SOCKET;
        }
    }

	return sServer;  //返回服务端socket句柄
}


/*==========================================================================*
 * FUNCTION : ConnectNetworkServer  客户端创建连接
 * PURPOSE  : Connect to server with non-blocked mode.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int    nWorkMode    : SOCK_DGRAM or SOCK_STREAM
 *            IN DWORD  dwServerAddr : server addr in host format
 *            IN short  nServerPort  : server port in host format
 *            IN int    nTimeout     : time out in ms    //这里传入的是连接超时
 * RETURN   : SOCKET : INVALID_SOCKET(-1) for failure, others for successful
 * COMMENTS : 
 * CREATOR  :                DATE: 2004-09-13 20:36
 *==========================================================================*/
SOCKET ConnectNetworkServer(IN int nWorkMode, 
					  IN DWORD dwServerAddr, IN short nServerPort,
					  IN int nTimeout )
{
	SOCKET s; 		        /*socket descriptor and socket type */
    int rc = 0;
	
    if ( nWorkMode != SOCK_DGRAM )
    {
        nWorkMode = SOCK_STREAM;  //TCP传输     SOCK_DGRAM:UDP传输 
    }

	/*Allocate a socket*/
	//1.创建socket
	//IPPROTO_TCP 、 IPPROTO_IP 、IPPROTO_UDP代表三种不同的协议，分别代表IP协议族里面的TCP协议、IP协议和UDP协议
    s = socket( AF_INET, nWorkMode, IPPROTO_IP );
	if ( s == INVALID_SOCKET )  //返回-1 创建失败
    {
#ifdef _DEBUG_TCPIP_PORT
        printf( "[ConnectNetworkServer] -- call socket() error: : %s.\n",
            strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/
        return INVALID_SOCKET;
    }

    /* set the sock to non-blocking     */
	//2.设置 读写 是否阻塞
    rc = SetNonBlockSocket( s, 1 /*bNonBlock*/ );  //设置为非阻塞模式
    if ( rc != 0 )
    {
#ifdef _DEBUG_TCPIP_PORT
        printf( "[ConnectNetworkServer] -- set non-block socket error: %s.\n",
            strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/
    }

    // do connect
    if ( rc == 0 ) // init ok
    {
		//设置地址ip和端口设置
        struct sockaddr_in sin; /*an internet endpoint address      */

       	memset ( ( void * )&sin, 0, sizeof(sin) );

        sin.sin_family = AF_INET ;
        sin.sin_port = htons( (u_short)nServerPort );
        sin.sin_addr.s_addr = htonl(dwServerAddr); 

	    /* Connect the socket.  the connect() will be return immediately 
         * because we set socket to non-blocking mode. 
         *
         * if connected ok, the select(FD_WRITE) will return write fds
         */
		 //连接服务端端成功
	    if ( (connect(s, (struct SC_SOCKADDR *)&sin, sizeof(sin)) == 0) ||
            (SOCK_ERRNO() == E_INPROGRESS) )    /* non-blocking */
        {
            if ((nTimeout <= 0) || /* do not wait connect return */  //不等待连接  时间
                (WaitFiledWritable( s, nTimeout ) > 0))              //连接等待超时 时间内响应成功 返回
            {
				int	nSockError = 0;
				int nLen = sizeof(int);

				// if the sock is writable, to get the error code,
				// if SO_ERROR is 0, OK. //判断是否　获得套接字错误　 无错误才返回s
				if ((getsockopt(s, SOL_SOCKET, SO_ERROR, 
					(char *)&nSockError, (socklen_t *)&nLen) == 0) &&
					(nSockError == 0))
				{
	                /* connect ok ! */
		            return s;   /* teturn the socket descriptor */
				}

				TRACE( "[ConnectNetworkServer] -- SO_ERROR: %d(%s)\n",
					nSockError, strerror(nSockError));
            }
        }
    }

#ifdef _DEBUG_TCPIP_PORT
    printf( "[ConnectNetworkServer] -- failed on error: %s.\n",
        strerror(SOCK_ERRNO()) ); 
#endif 	/*_DEBUG_TCPIP_PORT	*/

    /* error occurred   */
    CLOSE_SOCKET(s);

    return INVALID_SOCKET;
}

#define COMM_SERVER_MODE	0x00
#define COMM_CLIENT_MODE	0x01
/* the work mode of the comm port */
enum COMM_WORK_MODE_ENUM
{
	COMM_LOCAL_SERVER		= 0,// The local server port
	COMM_OUTGOING_CLIENT	= 1,// Client port to connect to remote server
	COMM_INCOMING_CLIENT	= 2	// Client port has connected to local server本端服务端的已连接过来的客户端
};

/*==========================================================================*
 * FUNCTION : TCPIP_CommOpen   初始化网络端口 并打开端口
 * PURPOSE  : To open a TCP/IP communication port.
 * CALLS    : CreateNetworkServer, ConnectNetworkServer
 * CALLED BY: 
 * ARGUMENTS: IN char   *pPortDescriptor : The network descriptor in Linux, 
 *									       eth0, eth1.If NULL or empty string,
 *										   use the default descriptor eth0.
 *                                         (now is ignored)
 *            IN char   *pOpenParams     : the IP and port in string as format:
 *										   "IP:port", such as "10.63.2.90:5080". 
 *                                         For server port, the IP is ignored.
 *            IN DWORD  dwPortAttr       : B00=COMM_SERVER_MODE for server port
 *                                         B00=COMM_CLIENT_MODE for client port
 *            IN int    nTimeout         : Open timeout in ms
 *            OUT int   *pErrCode        : prt to save error code.
 * RETURN   : HANDLE : 
 * COMMENTS : 
 * CREATOR  :                DATE: 2004-09-11 17:55
 *==========================================================================*/
HANDLE TCPIP_CommOpen(
	IN char		*pPortDescriptor,   //端口描述："/dev/eth0"
	IN char		*pOpenParams,       //端口参数："127.0.0.1:8888"
	IN DWORD	dwPortAttr,         //端口Mode：模式  1:客户端  0:服务端
	IN int		nTimeout,           //端口超时： 应该是连接超时
	OUT int		*pErrCode )         //返回错误码
{
	TCPIP_PORT_DRV *pPort = NEW(TCPIP_PORT_DRV, 1); //实例化  申请端口结构体空间
	//之后对 结构体成员 赋值

	UNUSED(pPortDescriptor);    //(void)char*  没使用

	/* 1. 申请不到内存空间 则退出 */
	if (pPort == NULL)
	{
		*pErrCode = ERR_COMM_NO_MEMORY;
		return	NULL;
	}
	memset(pPort, 0, sizeof(TCPIP_PORT_DRV));  //清空内存数据

	/* 2. 根据端口参数解析 端口IP port parse IP and socket port info */
	if (!TCPIP_ParseOpenParams(pOpenParams, &pPort->dwHostAddr, 
		&pPort->nSockPort)) //获得端口ip  端口号
	{//如果解析失败则返回 退出处理
#ifdef _DEBUG_TCPIP_PORT
		TRACE("[TCPIP_CommOpen] -- invalid parameters: \"%s\".\n",
			pOpenParams);

#endif //_DEBUG_TCPIP_PORT

		DELETE( pPort );
		*pErrCode = ERR_COMM_OPENING_PARAM;

		return	NULL;
	}
	//3.初始化 超时结构体
	INIT_TIMEOUTS( pPort->toTimeouts, nTimeout, nTimeout); //用传入超时参数nTimeout  赋值pPort->toTimeouts
	
	pPort->pCurClients = NEW( int, 1 );	//实例化 端口结构体.pCurClients
	if (pPort->pCurClients == NULL)//实例化不了 就释放 退出
	{
		DELETE( pPort );
		*pErrCode = ERR_COMM_NO_MEMORY;

		return NULL;
	}

	*pPort->pCurClients = 0;//直接赋值	/*	第几个客户端连接no linkages now		*/

	/* to connect to a remote server, create a client port	*/
	//端口 模式1：为客户端模式
	if ((dwPortAttr & COMM_CLIENT_MODE) == COMM_CLIENT_MODE)
	{
		//创建 连接服务端的客户端 socket  并赋值获得socket 字符文件引用
		pPort->nSocket = ConnectNetworkServer(
			SOCK_STREAM,
			pPort->dwHostAddr,	/* the server address	*/
			pPort->nSockPort,	
			nTimeout );

		if (pPort->nSocket == INVALID_SOCKET) //客户端socket 创建失败
		{
			DELETE( pPort->pCurClients );
			DELETE( pPort );
			*pErrCode = ERR_COMM_CONNECT_SERVER;

			return NULL;
		}

		pPort->nWorkMode = COMM_OUTGOING_CLIENT;  //端口结构体.工作模式  =直接赋值1

		/* set the peer info */
		//端口 结构体.端口ip地址  = 直接赋值
        pPort->inPeerAddr.sin_family      = AF_INET ;
        pPort->inPeerAddr.sin_port        = htons(pPort->nSockPort);
        pPort->inPeerAddr.sin_addr.s_addr = htonl(pPort->dwHostAddr); 
	}
	else //端口 模式2：为服务端模式
	{
		pPort->dwHostAddr = INADDR_ANY;		// 指定地址为0.0.0.0的地址，代表了所有本地的地址

		//创建  服务端 socket   并赋值获得socket 字符文件引用
		pPort->nSocket = CreateNetworkServer(
			SOCK_STREAM,
			pPort->dwHostAddr,				// 0.0.0.0
			pPort->nSockPort);

		if (pPort->nSocket == INVALID_SOCKET) //服务端socket 创建失败
		{
			DELETE( pPort->pCurClients );
			DELETE( pPort );
			*pErrCode = ERR_COMM_CREATE_SERVER;

			return NULL;
		}

		pPort->nWorkMode   = COMM_LOCAL_SERVER;   //端口结构体.工作模式  =直接赋值0
		pPort->nMaxClients = TCPIP_MAX_CLIENTS_DEFAULT;   //服务端 最多监听数量
	}

	*pErrCode = ERR_COMM_OK;

	return (HANDLE)pPort;
}

#define COM_RETRY_TIME	3
/*==========================================================================*
 * FUNCTION : TCPIP_CommAccept   获取 客户端连接  （若本端为客户端会马上退出）
 * PURPOSE  : To accept a client from tcpip server port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : The server port
 * RETURN   : HANDLE : non-zero for successful, NULL for failure
 * COMMENTS : 
 * CREATOR  :                 DATE: 2004-09-13 20:43
 *==========================================================================*/
HANDLE TCPIP_CommAccept( IN HANDLE hPort )  //传入端口参数配置包  结构体
{
	TCPIP_PORT_DRV *pPort   = (TCPIP_PORT_DRV *)hPort;
	TCPIP_PORT_DRV *pClient = NULL;

	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;

	/* check the socket whether a server port or not,
 	 * only server port is allowed to call accept
	 */
	 //非 本地服务端  模式
	if (pPort->nWorkMode != COMM_LOCAL_SERVER)
	{
		pPort->nLastErrorCode = ERR_COMM_SUPPORT_ACCEPT;
		return NULL;
	}
	//只有 本地服务端 模式才向下执行

	/* 超过最大客户端连接数 则错误返回check the current client num exceeds the maximum connections or not */
	if ( *pPort->pCurClients >= pPort->nMaxClients)  //超过最大客户端连接数 则错误返回
	{
		pPort->nLastErrorCode = ERR_COMM_MANY_CLIENTS;
		return NULL;
	}
//	printf("commSock.c>>进入TCPIP_CommAccept>> 等待复用-1>0\n");
	//等待超时 时间内 读取响应
	/* If the socket is readable, means there is a client is pending	*/
	if (WaitFiledReadable( pPort->nSocket, pPort->toTimeouts.nReadTimeout ) > 0) //(如果客户端连接成功会有读取响应)等待超时 时间内 读取响应
	{
//		printf("commSock.c>>进入TCPIP_CommAccept>> 等待复用-2>0\n");
	    struct sockaddr_in	saAddr;
		int					n = sizeof(saAddr);
		SOCKET				sClient;
		//接受tcp连接
		sClient = accept( pPort->nSocket, (struct SC_SOCKADDR *)&saAddr, (socklen_t *)&n );
		if (sClient== INVALID_SOCKET)
		{
			pPort->nLastErrorCode = ERR_COMM_ACCEPT_FAILURE;
			return NULL;
		}

		/* 创建一个 连接过来的客户端口 配置结构体 Ok, create a client object */
		pClient = NEW(TCPIP_PORT_DRV,1);  //创建一个新的客户端端口配置  结构体
		if (pClient == NULL)	/* out of memory error	*/
		{
			pPort->nLastErrorCode = ERR_COMM_NO_MEMORY;
			CLOSE_SOCKET( sClient );

			return NULL;
		}
        //远程客户端 端口配置 赋值
		*pClient			= *pPort;	/* Copy all info from server port	*/
		pClient->nSocket	= sClient;
		pClient->nWorkMode	= COMM_INCOMING_CLIENT;
		pClient->inPeerAddr = saAddr;
		(*pPort->pCurClients)++;		/* increase the current linkage		*/
	}
	else
	{
//		printf("commSock.c>>进入TCPIP_CommAccept>> 等待复用 超时或异常-3>0\n");
		pPort->nLastErrorCode = ERR_COMM_TIMEOUT;
		return NULL;
	}

	return (HANDLE)pClient;
}


/*==========================================================================*
 * FUNCTION : CommRead   读取数据     ---接收不到数据会接收超时，
 * PURPOSE  : Read data from a opened TCP/IP port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort        : the opened port
 *            OUT char   *pBuffer     : The buffer to save received data
 *            IN int     nBytesToRead :
 * RETURN   : int : the actual read bytes.
 * COMMENTS : 
 * CREATOR  :                DATE: 2004-09-13 21:17
 *==========================================================================*/
int TCPIP_CommRead(IN HANDLE hPort, OUT char *pBuffer, IN int nBytesToRead)
{
	TCPIP_PORT_DRV	*pPort = (TCPIP_PORT_DRV *)hPort;  //获取端口结构体
	int				rc;
	int				fd   = pPort->nSocket;
	int			    nTotalBytesRead = 0;	// total bytes of data being read
	int				nRetryTimes		= 0;

	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;

	//1.监视 读取响应
	rc = WaitFiledReadable(fd, pPort->toTimeouts.nReadTimeout); /* 0: wait time out, -1: fd error on waiting 	*/
	if( rc <= 0 ) //没有读到 有效数据  或超时  就退出
	{
		pPort->nLastErrorCode = (rc == 0) ? ERR_COMM_TIMEOUT  : ERR_COMM_READ_DATA;
#ifdef _DEBUG_TCPIP_PORT
		TRACE("[TCPIP_CommRead] -- Read data error(%x)\n", pPort->nLastErrorCode);
#endif 
		return 0;	// nothing has been read
	}

	//开始 读取  字节数据 ---能读取到数据了
    while (nBytesToRead > 0)
    {
		//读取 网络字节数据
		rc = recv(fd, pBuffer, (size_t)nBytesToRead, 0);  
		//printf("commSock>>TCPIP_CommRead>>>读取网络数据recv rc=%d\n", rc);
        if (rc > 0)	/* we got some data */  // 读取到数据ok
        {         
            nBytesToRead	-= rc;  //统计还需接收的字节数
            pBuffer			+= rc;  //接收buf指针偏移
			nTotalBytesRead += rc;  //统计已接收大小

			if (nBytesToRead == 0) //接收完毕 退出
			{
				break;	// reading finished
			}

			// Need clear retry times? We don't clear it!
			// nRetryTimes = 0;
		}
		//接收 出错
		else if ((rc < 0) && (errno != EAGAIN))		// read error?
		{
			nRetryTimes++;		// let's retry again
			if (nRetryTimes >= COM_RETRY_TIME)
			{
				pPort->nLastErrorCode = ERR_COMM_READ_DATA;
#ifdef _DEBUG_TCPIP_PORT
				TRACE("[TCPIP_CommRead] -- Read data error(%d), "
					"has retried %d times.\n", errno, nRetryTimes);
#endif //_DEBUG_TCPIP_PORT
				break;	// error, quit now.
			}
		}
		//已接收不到数据  socket closed
		else if (rc == 0)	// the socket is shutdown, gracefully closed.  已接收不到数据，socket closed
		{
			pPort->nLastErrorCode = ERR_COMM_CONNECTION_BROKEN;
#ifdef _DEBUG_TCPIP_PORT
			TRACE("[TCPIP_CommRead] -- The socket is shutdown.\n");
#endif //_DEBUG_TCPIP_PORT
			break;
		}
		
		//----------------  未读取够数据---------------
		//每次recv前  等待重新 判断监视复用是否 有读取变化 if error, we also sleep a while and retry again.
		rc = WaitFiledReadable(fd, pPort->toTimeouts.nIntervalTimeout);
		if( rc <= 0 )  //没有读到 有效数据  或超时  就退出
		{
			/* 0: wait time out, -1: fd error on waiting    */
			pPort->nLastErrorCode = (rc == 0) ? ERR_COMM_TIMEOUT
				: ERR_COMM_READ_DATA;
#ifdef _DEBUG_TCPIP_PORT
			TRACE("[TCPIP_CommRead] -- The socket failed %s after read %d "
				"bytes.\n", 
				rc == 0 ? "timeout" : "select" , nTotalBytesRead);
#endif //_DEBUG_TCPIP_PORT
			break;
		}
    }

#ifdef _DEBUG_TCPIP_PORT
	TRACE("[TCPIP_CommRead] -- End The socket read %d bytes(err code: %d).\n",
		nTotalBytesRead, pPort->nLastErrorCode);
#endif //_DEBUG_TCPIP_PORT

	return nTotalBytesRead;
}
				
/*==========================================================================*
 * FUNCTION : TCPIP_CommWrite    写入数据     ---写入不到数据会写入超时，
 * PURPOSE  : write a buffer to a port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort         : 
 *            IN char    *pBuffer      : 
 *            IN int     nBytesToWrite : 
 * RETURN   : int : < 0 for error. <nBytesToWrite timeout, = nBytesToWrite ok
 * COMMENTS : 
 * CREATOR  :                DATE: 2004-09-13 21:36
 *==========================================================================*/
int TCPIP_CommWrite(IN HANDLE hPort, IN char *pBuffer,	IN int nBytesToWrite)
{
	TCPIP_PORT_DRV	*pPort = (TCPIP_PORT_DRV *)hPort;
	int				rc;
	int				fd   = pPort->nSocket;
	int			    nTotalBytesWritten = 0;	// total bytes of data being written
	int				nRetryTimes		= 0;

	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;
	//1.监视 读取响应
	//printf("CommSock>>TCPIP_CommWrite>>准备进入WaitFiledWritable --flag1\n");
	rc = WaitFiledWritable(fd, pPort->toTimeouts.nReadTimeout);	
	if( rc <= 0 )
	{
		/* 0: wait time out, -1: fd error on waiting    */
		pPort->nLastErrorCode = (rc == 0) ? ERR_COMM_TIMEOUT  : ERR_COMM_WRITE_DATA;
#ifdef _DEBUG_TCPIP_PORT
		TRACE("[TCPIP_CommWrite] -- Write data error(%x)\n",
			pPort->nLastErrorCode);
#endif //_DEBUG_TCPIP_PORT
		return 0;	// nothing has been read
	}

	//开始 写入  字节数据
    while (nBytesToWrite > 0)
    {
		rc = send(fd, pBuffer, (size_t)nBytesToWrite, 0);
		//printf("CommSock>>TCPIP_CommWrite>>进入sendto返回rc=%d\n", rc);
        if (rc > 0)	/* we wrote out some data */
        {
            // ok
            nBytesToWrite	   -= rc;
            pBuffer			   += rc;
			nTotalBytesWritten += rc;

			if (nBytesToWrite == 0)  //写入完毕 退出
			{
				break;	// writing finished
			}
		}
		else if ((rc < 0) && (errno != EAGAIN))		//写入 出错 //write error?
		{
			nRetryTimes++;		// let's retry again
			if (nRetryTimes >= COM_RETRY_TIME)
			{
				pPort->nLastErrorCode = ERR_COMM_WRITE_DATA;
#ifdef _DEBUG_TCPIP_PORT
				TRACE("[TCPIP_CommWrite] -- Write data error(%d), "
					"has retried %d times.\n", errno, nRetryTimes);
#endif //_DEBUG_TCPIP_PORT
				break;	// error, quit now.
			}
		}
		//----------------  未读写入 数据---------------
		//每次sendto前  等待重新 判断监视复用是否 有写入变化
		// if error, we also sleep a while and retry again.
		rc = WaitFiledWritable(fd, pPort->toTimeouts.nIntervalTimeout);
		if( rc <= 0 )
		{
			/* 0: wait time out, -1: fd error on waiting    */
			pPort->nLastErrorCode = (rc == 0) ? ERR_COMM_TIMEOUT
				: ERR_COMM_READ_DATA;
			break;
		}
    }

	return nTotalBytesWritten;
}


/*==========================================================================*
 * FUNCTION : CommClose
 * PURPOSE  : Close an opened port and release the memory of the port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : int : ERR_COMM_OK or ERR_COMM_PORT_HANDLE
 * COMMENTS : 
 * CREATOR  :                  DATE: 2004-09-14 20:32
 *==========================================================================*/
int TCPIP_CommClose( IN HANDLE hPort )
{
	TCPIP_PORT_DRV *pPort = (TCPIP_PORT_DRV *)hPort;

	if (hPort == NULL)
	{
		return ERR_COMM_PORT_HANDLE;
	}

	if (pPort->nWorkMode != COMM_LOCAL_SERVER)
	{
		/* */
		shutdown(pPort->nSocket, SHUT_RDWR);  // 连接的读写都关闭  如果仅close sockt有其他的进程共享着这个套接字，那么它仍然是打开的
	}
	CLOSE_SOCKET(pPort->nSocket);		/* close the socket handle	*/

	(*pPort->pCurClients)--;
	if (*pPort->pCurClients < 0)
	{
		DELETE(pPort->pCurClients);
	}

	DELETE(pPort);
	return ERR_COMM_OK;
}

/* The control commands: nCmd */
enum COMM_CONTROL_COMMAND_ENUM
{
	COMM_GET_LAST_ERROR         = 1,   /* get the last error code */
	COMM_GET_PEER_INFO          = 2,   /* get the sender info, 
											bBuffer=COMM_PEER_INFO, 
											nDataLength=sizeof(COMM_PEER_INFO)*/
	COMM_SET_TIMEOUTS           = 3,   /* set the comm timeout in ms. 
											bBuffer=COMM_TIMEOUTS, 
											nDataLength=sizeof(COMM_TIMEOUTS)*/
	COMM_GET_TIMEOUTS           = 4,   /* get the comm timeout in ms
											bBuffer=COMM_TIMEOUTS, 
											nDataLength=sizeof(COMM_TIMEOUTS)*/
	COMM_PURGE_TXCLEAR          = 5,	  /* clear the transmit buffer */
	COMM_PURGE_RXCLEAR          = 6,   /* clear the receive buffer */
	COMM_SET_MAX_CLIENTS        = 7,   /* set the maximum allowed clients */
	COMM_GET_MAX_CLIENTS        = 8,   /* get the maximum allowed clients */
	COMM_GET_CUR_CLIENTS        = 9,   /* get the number of the current 
											connected clients */
	COMM_GET_CLIENT_LIMITATION  = 10,  /* get the capacity maximum of 
										* the client connections */
	COMM_SET_COMM_ATTRIBUTES = 11,		//lyg,璁剧疆涓插彛鍙傛暟
	COMM_PRIVATE_COMMAND		= 255  /* >255:defined by the HAL comm drv*/
};

/*==========================================================================*
 * FUNCTION : TCPIP_CommControl    控制  设置参数  获取设置参数
 * PURPOSE  : To control a opened port with command nCmd.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE    hPort       : 
 *            IN int       nCmd        : 
 *            IN OUT void  *pBuffer    : 
 *            IN int       nDataLength : 
 * RETURN   : int :ERR_OK for OK, else is error code
 * COMMENTS : 
 * CREATOR  :                 DATE: 2004-09-13 21:45
 *==========================================================================*/
int TCPIP_CommControl(	IN HANDLE hPort, IN int	nCmd, 
					  IN OUT void *pBuffer,	IN int	nDataLength)
{
	/* Now we use switch to test the command, 
	 * we can optimize it if there are too many commands 
	 * which makes lower efficiency
	 */
	TCPIP_PORT_DRV *pPort = (TCPIP_PORT_DRV *)hPort;
	int nErrCode = ERR_COMM_OK;

	if (hPort == NULL)
	{
		return ERR_COMM_PORT_HANDLE;
	}
	assert(pBuffer);  //断言 判断是否为假 为假打印信息并退出程序

	switch (nCmd)
	{
	case COMM_GET_LAST_ERROR:	/* get the last error code	*/
		{
			assert(nDataLength == sizeof(int));
			*(int *)pBuffer = pPort->nLastErrorCode;
		}

		break;

	case COMM_GET_PEER_INFO:	/* get the sender info		*/
		{
			//个人  暂不实现
		}

		break;

	case COMM_SET_TIMEOUTS:		/* set the comm timeout in ms. */
		{	
			/* bBuffer=COMM_TIMEOUTS, nDataLength=sizeof(COMM_TIMEOUTS)		*/
			assert(nDataLength == sizeof(COMM_TIMEOUTS));
			pPort->toTimeouts = *(COMM_TIMEOUTS *)pBuffer;
		}

		break;

	case COMM_GET_TIMEOUTS:		/* get the comm timeout in ms	*/
		{	
			/* bBuffer=COMM_TIMEOUTS, nDataLength=sizeof(COMM_TIMEOUTS)		*/
			assert(nDataLength == sizeof(COMM_TIMEOUTS));
			*(COMM_TIMEOUTS *)pBuffer = pPort->toTimeouts;
		}
		break;

	case COMM_PURGE_TXCLEAR:	/* clear the transmit buffer	*/
		{
			/* no action	*/
			if (pPort->nWorkMode == COMM_LOCAL_SERVER)
			{
				/* The server port does not support		*/
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}
		break;

	case COMM_PURGE_RXCLEAR:	/* clear the receive buffer		*/
		{
			if (pPort->nWorkMode != COMM_LOCAL_SERVER)
			{
				COMM_TIMEOUTS	toOld  = pPort->toTimeouts;	// save timeout
				char			szTemp[256];	
				INIT_TIMEOUTS_EX(pPort->toTimeouts, 100, 100, 10 );// set the new timeout before controlling
				while (TCPIP_CommRead(hPort, szTemp, sizeof(szTemp)) > 0);
				pPort->toTimeouts = toOld;	// restore timeouts
			}
			else /* if (pPort->nWorkMode == COMM_LOCAL_SERVER) */ /* The server port does not support		*/
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}
		break;

	case COMM_SET_MAX_CLIENTS:	/* set the maximum allowed clients	*/
		{
			assert(nDataLength == sizeof(int));
			if (pPort->nWorkMode == COMM_LOCAL_SERVER)	/* Must be server	*/
			{	
				if ((*(int *)pBuffer > 0)				 /* client must > 0	*/
					&& (*(int *)pBuffer <= TCPIP_MAX_CLIENT_LIMITAION))
				{
					pPort->nMaxClients = *(int *)pBuffer;
				}
				else
				{
					nErrCode = ERR_COMM_CTRL_PARAMS;
				}
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

	case COMM_GET_MAX_CLIENTS:		/* get the maximum allowed clients */
		{
			assert(nDataLength == sizeof(int));

			if (pPort->nWorkMode == COMM_LOCAL_SERVER) /* Must be server	*/
			{
				*(int *)pBuffer = pPort->nMaxClients;
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

		/* get the number of the current connected clients */
	case COMM_GET_CUR_CLIENTS:
		{
			assert(nDataLength == sizeof(int));
			if (pPort->nWorkMode == COMM_LOCAL_SERVER) /* Must be server	*/
			{
				*(int *)pBuffer = *pPort->pCurClients;
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}
		break;

	case COMM_GET_CLIENT_LIMITATION:	/* get the maximum client limitation */
		{
			assert(nDataLength == sizeof(int));

			if (pPort->nWorkMode == COMM_LOCAL_SERVER) /* Must be server	*/
			{
				*(int *)pBuffer = TCPIP_MAX_CLIENT_LIMITAION;
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}
		break;

	default:
		{
			nErrCode = ERR_COMM_CTRL_COMMAND;
		}
	}

	return nErrCode;
}

