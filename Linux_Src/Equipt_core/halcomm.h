/*==========================================================================*
 *  FILENAME : halcomm.h
 *  CREATOR  : fjw0312
 *  VERSION  : V1.00
 *  PURPOSE  : Defines the API protocols for the communication port drivers of
 *             HAL( Hardware Abstract Layer).
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#ifndef __HALCOMM_H__
#define __HALCOMM_H__

#ifdef __cplusplus
extern "C" {
#endif

/* 
 *  The following are the API to access communication ports:
 */

#define COMM_SERVER_MODE	0x00
#define COMM_CLIENT_MODE	0x01

/* the work mode of the comm port */
enum COMM_WORK_MODE_ENUM
{
	COMM_LOCAL_SERVER		= 0,// The local server port
	COMM_OUTGOING_CLIENT	= 1,// Client port to connect to remote server
	COMM_INCOMING_CLIENT	= 2	// Client port has connected to local server
};


/* To open the communication port */
/*==========================================================================*
 * FUNCTION : CommOpen
 * PURPOSE  : To open the given type communication port.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char	*pszStdPortLib   : the std port accessing driver name,
 *										   a shared library.
 *            IN char   *pPortDescriptor : the physical port device defined in
 *                                         /dev/, such as /dev/ttyS0, ...
 *            IN char   *pOpenParams     : the open params depend on the port
 *                                         type. for std serial as "b,p,d,s",
 *                                         for dial serial as "b,p,d,s:tel", 
 *                                         for TCP/IP as "n1.n2.n3.n4:port"
 *            IN DWORD  dwPortAttr       : B00=COMM_SERVER_MODE: for server port
 *                                         B00=COMM_CLIENT_MODE: for client port
 *                                         Others are reserved.
 *            IN int    nTimeout         : the timeout to open the port in ms
 *            OUT int   *pErrCode        : ptr to receive error code
 * RETURN   : HANDLE : NON-NULL: Handle of opened port, NULL: for error. 
 * COMMENTS : 
 * CREATOR  : Mao Fuhua                DATE: 2004-09-25 19:30
 * HISTORY  : Modify the first arg from "int nStdPortType" to 
 *            "char	*pszStdPortLib". Maofuhua, 2004-11-13         
 *==========================================================================*/

HANDLE CommOpen(IN char	*pszStdPortLib,
	IN char *pPortDescriptor,/* the physical port device defined in
							 *  /dev/, such as /dev/ttyS0, ... 
							 */
	IN char *pOpenParams,	/* the open params depend on the port type		*/
	IN DWORD dwPortAttr,	/* B00=COMM_SERVER_MODE: for server port
							 * B00=COMM_CLIENT_MODE: for client port
							 */
	IN int	nTimeout,		/* the timeout to open the port in milliseconds */
	OUT int	*pErrCode );	/* the ptr to receieve error code on opening	*/

/* To accept a client from a server port */
HANDLE CommAccept ( IN HANDLE hPort );

/* To read data from an opened port */
int CommRead ( IN HANDLE hPort, 
			  OUT char	*pBuffer, 
			  IN int nBytesToRead );

/* To write data out to an opened port */
int CommWrite ( IN OUT HANDLE	hPort, 
			   IN char *pBuffer, 
			   IN int nBytesToWrite );

/* To control an opened port */
int CommControl ( IN HANDLE hPort,
				 IN int nCmd,
				 IN OUT void *pBuffer, 
				 IN int nBufferLength );
/* To close an opened port */			 
int CommClose ( IN HANDLE	hPort );
// get the last error of the port. 
// in the internal, nErr = hPort->hInstance->nLastErrorCode;
#define CommGetLastError(hPort)		(**(int **)(hPort))


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
	COMM_GET_CLIENT_LIMITATION  = 10,  /* get the capacity maximum of the client connections */
	COMM_SET_COMM_ATTRIBUTES	= 11,  // set series port communication parameters									
	COMM_PRIVATE_COMMAND		= 255  /* >255:defined by the HAL comm drv*/
};

/* the comm peer info */
#define MAX_LEN_PEER_ADDR	32
struct SCommPeerInfo
{
	int		nPeerType;
	int		nPeerPort;
	char	szAddr[MAX_LEN_PEER_ADDR];	/* the addr of peer:
						  1)IP: for TCP/IP and UDP. IP=*(DWORD *)szAddr
						  2)Phone Number:for dialling port(depends on modem)*/
};
typedef struct SCommPeerInfo		COMM_PEER_INFO;

/* the struct of timeout */
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

/* To close an opened port */
int CommClose ( IN HANDLE	hPort );


/* do NOT directly use the following interfaces */
/* The open proc named "CommOpen" shall be provided by each HAL comm driver */
typedef HANDLE (*COMM_OPEN_PROC)
(				
	IN char		*pPortDescriptor,
	IN char		*pOpenParams,
	IN DWORD	dwPortAttr,
	IN int		nTimeout,
	OUT int		*pErrCode
);

/* The "CommAccept" proc for comm driver */
typedef HANDLE (*COMM_ACCEPT_PROC)
(				
	IN HANDLE	hPort
);

/* The "CommRead" proc for comm driver */
typedef int (*COMM_READ_PROC)
(				
	IN HANDLE	hPort,		
	OUT char	*pBuffer,
	IN int		nBytesToRead
);				

/* The "CommWrite" proc for comm driver */
typedef int (*COMM_WRITE_PROC)
(				
	IN HANDLE	hPort,
	IN char		*pBuffer,
	IN int		nBytesToWrite
);				

/* The "CommControl" proc for comm driver */
typedef int (*COMM_CONTROL_PROC)
(				
	IN HANDLE	hPort,
	IN int		nCmd,
	IN OUT void	*pBuffer,
	IN int	nDataLength
);
//Return:	0: OK


/* The "CommClose" proc for comm driver */
typedef int (*COMM_CLOSE_PROC)
(
	IN HANDLE	hPort
);

/* 
 * The following protocols are used to develop HAL communication port drivers,
 * The application service shall NOT call them directly
 */
HANDLE HAL_CommOpen( IN char *pPortDescriptor, IN char *pOpenParams,
	IN DWORD dwPortAttr, IN int nTimeout, OUT int *pErrCode );
HANDLE HAL_CommAccept( IN HANDLE hPort );
int HAL_CommRead(IN HANDLE hPort, OUT char *pBuffer, IN int nBytesToRead);
int HAL_CommWrite(IN HANDLE hPort, IN char *pBuffer,	IN int nBytesToWrite);
int HAL_CommControl(	IN HANDLE hPort, IN int	nCmd, 
					  IN OUT void *pBuffer,	IN int	nDataLength);
int HAL_CommClose( IN HANDLE hPort );

/* 
 * Define the export function names(symbole name) for 
 * all HAL communication drivers
 */
#define SYM_HAL_COMMOPEN		"HAL_CommOpen"
#define SYM_HAL_COMMACCEPT		"HAL_CommAccept"
#define SYM_HAL_COMMREAD		"HAL_CommRead"
#define SYM_HAL_COMMWRITE		"HAL_CommWrite"
#define SYM_HAL_COMMCONTROL		"HAL_CommControl"
#define SYM_HAL_COMMCLOSE		"HAL_CommClose"


#ifdef __cplusplus
}
#endif

#endif /*__HALCOMM_H__*/
