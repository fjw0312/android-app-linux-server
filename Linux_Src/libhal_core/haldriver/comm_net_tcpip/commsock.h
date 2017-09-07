/* NetSock.h                */
/* by , 2001/03/4   */

#ifndef __NET_SOCK_H_2001_03_04_DEFINED
#define __NET_SOCK_H_2001_03_04_DEFINED

#if defined(linux) || defined(unix)

#include <netinet/in.h>			/* sockaddr_in, sockaddr */
#include <arpa/inet.h>			/* inet_ntoa */
#include <sys/resource.h>		/* setrlimit */
#include <sys/types.h>			/* socket, bind, accept */
#include <sys/socket.h>			/* socket, bind, accept, setsockopt, */
#include <syslog.h>
#include <netdb.h>

#endif

/* 
 * If want to test the module with static mode,
 * please comment out the next line.
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


#ifndef WIN32
/*
 * The new type to be used in all
 * instances which refer to sockets.
 */
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
#define TCPIP_MAX_CLIENT_LIMITAION	64	/* It's enough for ACU				*/

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


#endif /* ifndef __NET_SOCK_H_2001_03_04_DEFINED    */

