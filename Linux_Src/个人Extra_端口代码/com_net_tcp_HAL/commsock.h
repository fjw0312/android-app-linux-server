/* NetSock.h                */
/* by , 2001/03/4   */

#ifndef _COMMSOCK_H
#define _COMMSOCK_H


#if defined(linux) || defined(unix)
#include <netinet/in.h>			/* sockaddr_in, sockaddr */
#include <arpa/inet.h>			/* inet_ntoa */
#include <sys/resource.h>		/* setrlimit */
#include <sys/types.h>			/* socket, bind, accept */
#include <sys/socket.h>			/* socket, bind, accept, setsockopt, */
#include <syslog.h>
#include <netdb.h>
#endif



#include "stdsys.h"		
#include "basetypes.h"
#include "err_code.h"

//#include "pubfunc.h"
//#include "halcomm.h"
//#include "new.h"

/* 
 * If want to test the module with static mode,
 * please comment out the next line.
 * 定义 可以动态库共享 函数名
 */
#define _MAKE_SHARED_LIB
#ifdef _MAKE_SHARED_LIB
#define TCPIP_CommOpen			HAL_CommOpen
#define TCPIP_CommAccept		HAL_CommAccept
#define TCPIP_CommRead			HAL_CommRead
#define TCPIP_CommWrite			HAL_CommWrite
#define TCPIP_CommControl		HAL_CommControl
#define TCPIP_CommClose			HAL_CommClose
#endif //_MAKE_SHARED_LIB

/*
 * The new type to be used in all
 * instances which refer to sockets.
 */
#ifndef WIN32
typedef int           SOCKET;
#endif

#ifndef SOCKET_ERROR
#define SOCKET_ERROR    -1
#endif

#ifndef INVALID_SOCKET
#define INVALID_SOCKET  -1
#endif

#ifndef SD_BOTH
#define SD_BOTH         2
#endif

#if defined(WIN32)
#define CLOSE_SOCKET(sock)  closesocket(sock)
#define SC_SOCKADDR         sockaddr
#define TCP                 6
#define UDP                 17
#define E_INPROGRESS        WSAEWOULDBLOCK
#define SOCK_ERRNO()        WSAGetLastError()
#endif

#ifdef unix
#define SC_SOCKADDR         sockaddr
#define SOCK_ERRNO()        errno
#define E_INPROGRESS        EINPROGRESS
#define CLOSE_SOCKET(sock)  close(sock)
#endif

#if defined(_PSOS)  || defined(unix)
#define SOCK_ERRNO()        errno
#define E_INPROGRESS        EINPROGRESS
#define CLOSE_SOCKET(sock)  close(sock)
#endif


/**********************************/
#define TCPIP_MAX_CLIENTS_DEFAULT	5	/* The default TCP_IP connections	*/
#define TCPIP_MAX_CLIENT_LIMITAION	64	/* It's enough for IDU				*/

#define new_free(p)					free(p)
#define new_malloc(s)				malloc(s)
#define NEW(T, nItems)  (T *)new_malloc( sizeof(T)*(nItems) )
#define DELETE(p)	new_free(p)

/* the struct of timeout */
// 端口设置 超时结构体
struct SCommTimeouts
{				
	int	nReadTimeout;	/* The total timeout on reading. -1: wait for ever	*/
	int	nWriteTimeout;	/* The total timeout on writing. -1: wait for ever	*/

	/* timeout between 2 chars after first 1 bytes sent out or received		*/
#define TIMEOUT_INTERVAL_CHAR	50 /*ms, minimum BPS is 1000/(50/10)=20bps	*/
	int	nIntervalTimeout;	/* the timeout between 2 char.					*/
};
typedef struct SCommTimeouts		COMM_TIMEOUTS;
#define INIT_TIMEOUTS(to, rto, wto)	((to).nReadTimeout = (rto),		\
									 (to).nWriteTimeout = (wto),	\
									 (to).nIntervalTimeout = TIMEOUT_INTERVAL_CHAR)


#define INIT_TIMEOUTS_EX(to, rto, wto, ito)	((to).nReadTimeout = (rto), \
									 (to).nWriteTimeout = (wto),		\
									 (to).nIntervalTimeout = (ito))
//端口参数配置包  结构体
struct STcpipPortDriver					/*	TCP/IP Port HAL driver			*/
{				
	int					nLastErrorCode;	//	NOTE: MUST BE THE FIRST FIELD! 
										//  the last error code.

	int					nWorkMode;		/* COMM_LOCAL_SERVER,
										 * COMM_CONNECTED_CLIENT,
										 * or COMM_ACCEPTED_CLIENT
										 */
	int					nSockPort;
	DWORD				dwHostAddr;		/* local host addr, addr of myself	*/
	struct sockaddr_in	inPeerAddr;
	SOCKET	nSocket;
	COMM_TIMEOUTS		toTimeouts;
	int					nMaxClients;
	int					*pCurClients;
};				
typedef struct STcpipPortDriver		TCPIP_PORT_DRV;



BOOL TCPIP_ParseOpenParams(IN char	*pOpenParams,OUT DWORD *pdwIP, OUT int *piPort);
SOCKET CreateNetworkServer(IN int nWorkMode,IN DWORD dwServerAddr, IN short nServerPort );
SOCKET ConnectNetworkServer(IN int nWorkMode,IN DWORD dwServerAddr, IN short nServerPort,IN int nTimeout );
HANDLE TCPIP_CommOpen(
	IN char		*pPortDescriptor,   //端口描述："/dev/eth0"
	IN char		*pOpenParams,       //端口参数："127.0.0.1:8888"
	IN DWORD	dwPortAttr,         //端口Mode：模式  1:客户端  0:服务端
	IN int		nTimeout,           //端口超时： 应该是连接超时
	OUT int		*pErrCode );
HANDLE TCPIP_CommAccept( IN HANDLE hPort );  //传入端口参数配置包  结构体
int TCPIP_CommRead(IN HANDLE hPort, OUT char *pBuffer, IN int nBytesToRead);
int TCPIP_CommWrite(IN HANDLE hPort, IN char *pBuffer,	IN int nBytesToWrite);
int TCPIP_CommClose( IN HANDLE hPort );
int TCPIP_CommControl(	IN HANDLE hPort, IN int	nCmd, 
					  IN OUT void *pBuffer,	IN int	nDataLength);

#endif /* ifndef __NET_SOCK_H_2001_03_04_DEFINED    */

