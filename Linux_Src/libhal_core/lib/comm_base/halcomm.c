/*==========================================================================*
 *  FILENAME : idu_hal_comm.c
 *  VERSION  : V1.01
 *  PURPOSE  : The API's for access the HAL drivers of the communication ports
 *
 *
 *  HISTORY  : no modification at all
 *
 *==========================================================================*/
#include <dlfcn.h>		// for dl*()
#include "stdsys.h"		/* all standard system head files			*/
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"

#ifdef _DEBUG
//#define _DEBUG_HAL_COMM		1
#endif

struct SStdPortDriver	//	 the std port and instance definition
{
	//	the instance of actual port.
	HANDLE			hInstance;		//	NOTE: MUST BE THE FIRST FIELD! 

	
	// the std port info 
	char			*pszStdPortLib;		//  	The driver name of the std port 			
	HANDLE			hLib;			//	The opened library(*.so) handle.
							//  	an actual comm driver is implemented
							//  	as shared lib format.
	int			*pLibRef;		//	The reference count of this drv lib
				
	COMM_OPEN_PROC		pfnOpen;		//	function ptr to open port
	COMM_ACCEPT_PROC	pfnAccept;		//	function ptr to accept a client
	COMM_READ_PROC		pfnRead;		//	function ptr to read data
	COMM_WRITE_PROC		pfnWrite;		//	function ptr to write data to port
	COMM_CONTROL_PROC	pfnControl;		//	function ptr to control th port
	COMM_CLOSE_PROC		pfnClose;		//	close port				
};				
typedef struct SStdPortDriver 		STD_PORT_DRV;



/*==========================================================================*
 * FUNCTION : LoadStdPortDriverLib
 * PURPOSE  : open and load a std port driver named pszDrvName
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char           *pszDrvName : 
 *            OUT STD_PORT_DRV  *pPort      : 
 * RETURN   : static BOOL : TRUE for OK.
 * COMMENTS : 
 *==========================================================================*/
static BOOL LoadStdPortDriverLib(IN char *pszDrvName,
								  OUT STD_PORT_DRV *pPort)
{
	static const char *pszSymName[] =
	{
		SYM_HAL_COMMOPEN,		//"HAL_CommOpen"
		SYM_HAL_COMMACCEPT,		//"HAL_CommAccept"
		SYM_HAL_COMMREAD,		//"HAL_CommRead"
		SYM_HAL_COMMWRITE,		//"HAL_CommWrite"
		SYM_HAL_COMMCONTROL,		//"HAL_CommControl"
		SYM_HAL_COMMCLOSE		//"HAL_CommClose"
	};

	HANDLE	*pfnProc[] =
	{
		(HANDLE *)&pPort->pfnOpen,
		(HANDLE *)&pPort->pfnAccept,
		(HANDLE *)&pPort->pfnRead,
		(HANDLE *)&pPort->pfnWrite,
		(HANDLE *)&pPort->pfnControl,
		(HANDLE *)&pPort->pfnClose
	};

	pPort->hLib = LoadDynamicLibrary(pszDrvName,
		ITEM_OF(pszSymName),
		pszSymName, pfnProc,			// symbol names and addresses
		TRUE);					// all symbols shall be found.

	return (pPort->hLib != NULL) ? TRUE : FALSE;
}


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
 * HISTORY  : Modify the first arg from "int nStdPortType" to 
 *            "char	*pszStdPortLib". Maofuhua, 2004-11-13         
 *==========================================================================*/
HANDLE CommOpen(IN char	*pszStdPortLib,
	IN char *pPortDescriptor,
	IN char *pOpenParams,
	IN DWORD dwPortAttr,	
	IN int	nTimeout,	
	OUT int	*pErrCode)	
{
	//printf("in hal now!\n");//----add by wankun
	STD_PORT_DRV	*pPort;

	*pErrCode = ERR_COMM_OK;

	// allocate memory for drv
	pPort = NEW(STD_PORT_DRV, 1);
	
	if (pPort == NULL)
	{
#ifdef _DEBUG_HAL_COMM
		TRACEX("[CommOpen] -- No memory to new STD_PORT_DRV.\n");
#endif //_DEBUG_HAL_COMM

		*pErrCode = ERR_COMM_NO_MEMORY;

		return NULL;
	}
	
	ZERO_POBJS(pPort, 1);

	pPort->pszStdPortLib = pszStdPortLib;
	
	// load the dll.
	if (!LoadStdPortDriverLib(pszStdPortLib, pPort))
	{
#ifdef _DEBUG_HAL_COMM
		TRACEX("[CommOpen] -- Fails on loading standard port driver %s.\n",
			pszStdPortLib);
#endif //_DEBUG_HAL_COMM

		DELETE(pPort); 
		*pErrCode = ERR_COMM_LOADING_DRIVER;

		return NULL;
	}
	//printf("in hal 162 now!\n");----add by wankun
	pPort->pLibRef = NEW(int, 1);
	if (pPort->pLibRef == NULL)
	{
#ifdef _DEBUG_HAL_COMM
		TRACEX("[CommOpen] -- No memory to new STD_PORT_DRV->hRef.\n");
#endif //_DEBUG_HAL_COMM

		*pErrCode = ERR_COMM_NO_MEMORY;
		CommClose(pPort);

		return NULL;
	}

	*pPort->pLibRef = 0;

	// call open function of driver to open port
	pPort->hInstance = pPort->pfnOpen(
		pPortDescriptor,
		pOpenParams, 
		dwPortAttr, 
		nTimeout, 
		pErrCode);

	if (pPort->hInstance == NULL)
	{
		// error code has been set. do NOT change it.
		// *pErrCode = ERR_COMM_OPENING_PORT;
#ifdef _DEBUG_HAL_COMM
		TRACEX("[CommOpen] -- Fails on open port.\n");
#endif //_DEBUG_HAL_COMM

		CommClose(pPort);

		return NULL;
	}

	return (HANDLE)pPort;
}


/*==========================================================================*
 * FUNCTION : CommAccept
 * PURPOSE  : To accept a client from a server port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : The opened server type port
 * RETURN   : HANDLE : NON-NULL: The accepted client handle. NULL: failure
 * COMMENTS : 
 *==========================================================================*/
 HANDLE CommAccept(IN HANDLE hPort)
{
	STD_PORT_DRV *pPort = (STD_PORT_DRV *)hPort;
	STD_PORT_DRV *pClient;
	HANDLE		hClient;

	pClient = NEW(STD_PORT_DRV, 1);
	if (pClient == NULL)
	{
#ifdef _DEBUG_HAL_COMM
		TRACEX("[CommAccept] -- No memory to new STD_PORT_DRV for client.\n");
#endif //_DEBUG_HAL_COMM
		return NULL;
	}

	hClient = pPort->pfnAccept( pPort->hInstance );
	if (hClient == NULL)
	{
#ifdef _DEBUG_HAL_COMM
		TRACEX("[CommAccept] --  Fails on accepting client.\n");
#endif //_DEBUG_HAL_COMM

		DELETE(pClient);

		return NULL;
	}

	*pClient = *pPort;				// get all properties of parent port.
	pClient->hInstance = hClient;	// new instance.
	(*pPort->pLibRef)++;			// increase ref.

	return (HANDLE)pClient;
}


/* To read data from an opened port */
/*==========================================================================*
 * FUNCTION : CommRead
 * PURPOSE  : Read data from an opened client mode port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort        : an opened/accepted client port handle
 *            OUT char   *pBuffer     : buffer to save data
 *            IN int     nBytesToRead : maximum bytes of data to be read
 * RETURN   : int : >=0, The actual bytes of data read. <0: error.
 * COMMENTS : when timeout, returned byte will less than nBytesToRead
 *==========================================================================*/
int CommRead ( IN HANDLE hPort, 
			  OUT char	*pBuffer, 
			  IN int nBytesToRead )
{
	return ((STD_PORT_DRV *)hPort)->pfnRead(
		((STD_PORT_DRV *)hPort)->hInstance,
		pBuffer,
		nBytesToRead);
}


/* To write data out to an opened port */
/*==========================================================================*
 * FUNCTION : CommWrite
 * PURPOSE  : Send the data with given length to the client port.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort         : an opened/accepted client port handle
 *            IN char    *pBuffer      : 
 *            IN int     nBytesToWrite : 
 * RETURN   : int : -1 for error, else the actual bytes sent.
 * COMMENTS : 
 *==========================================================================*/
int CommWrite ( IN HANDLE	hPort, 
			   IN char *pBuffer, 
			   IN int nBytesToWrite )
{
	return ((STD_PORT_DRV *)hPort)->pfnWrite(
		((STD_PORT_DRV *)hPort)->hInstance,
		pBuffer,
		nBytesToWrite);
}


/* To control an opened port */
/*==========================================================================*
 * FUNCTION : CommControl
 * PURPOSE  : send an control command to the port to get info from the port or
 *            change setting to the port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE    hPort         : the port handle
 *            IN int       nCmd          : cmd
 *            IN OUT char  *pBuffer      : data to be sent or retrieved
 *            IN int       nBufferLength : buff length
 * RETURN   : int : ERR_OK for OK, else the error code.
 * COMMENTS : 
 *==========================================================================*/
int CommControl ( IN HANDLE hPort,
				 IN int nCmd,
				 IN OUT char *pBuffer, 
				 IN int nBufferLength )
{
	return ((STD_PORT_DRV *)hPort)->pfnControl(
		((STD_PORT_DRV *)hPort)->hInstance,
		nCmd,
		pBuffer,
		nBufferLength);
}

/*==========================================================================*
 * FUNCTION : CommClose
 * PURPOSE  : to close an port which is no use anymore
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int CommClose ( IN HANDLE	hPort )
{
	STD_PORT_DRV *pPort = (STD_PORT_DRV *)hPort;
	int			nErrCode;

	if (pPort == NULL)
	{
		return ERR_COMM_OK;		// nothins need to be closed.
	}
	//printf("nErrcode is %d\n", nErrCode);
	nErrCode = (pPort->hInstance != NULL) 
		?	pPort->pfnClose(pPort->hInstance) : ERR_COMM_OK;
	//printf("nErrcode is %d\n", nErrCode);
    	if (pPort->pLibRef != NULL)
	{
		(*pPort->pLibRef)--;
		if (*pPort->pLibRef < 0)	// the lib is no use.
		{
			//unload the shared lib
			UnloadDynamicLibrary(pPort->hLib);
	
			DELETE( pPort->pLibRef );
		}
	}

	DELETE(pPort);
	//printf("nErrcode is %d\n", nErrCode);
	return nErrCode;
}
