/*==========================================================================*
 *  FILENAME : test_comm_serial.c
 *  VERSION  : V1.00
 *  PURPOSE  : To test comm_std_serial.so
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#include <signal.h>
#include "stdsys.h"		/* all standard system head files			*/
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"

#define TEST_DATA_LENGTH 512

//#warning "GetStandardPortDriverName need to be redefined..."

static char *GetStandardPortDriverName(IN int nStdPortType)
{
	const char *pszStdDriver[] =
	{
		"comm_std_serial.so",
		"comm_hayes_modem.so",
		"comm_net_tcpip.so",
		"comm_acu_485.so"
	};

	return (char *)pszStdDriver[nStdPortType];
}



static int s_nRunning = 1;
static void QuitSignalHandler( int a )
{
    if( s_nRunning > 0)
    {
        printf( "Receives a quit signal. Quiting ... \007\007\n" );
        s_nRunning = 0;
    }
    else
    {
        printf( "App is quiting, please wait ... \007\007\n" );
		s_nRunning--;
		if (s_nRunning < -5)
		{
			exit(-1);
		}
    }
}


static void dump_buf( char *buf, int n)
{
	while (n > 0)
	{
		printf( "%c", *buf ++);
		n --;
	}

	printf( "\n" );
}

static void ClearRxTx( HANDLE hPort)
{
	CommControl( hPort, COMM_PURGE_TXCLEAR, (void*)1, 0);
	CommControl( hPort, COMM_PURGE_RXCLEAR, (void*)1, 0);
}


static void ShowLastError( HANDLE hPort, char *pszMsg )
{
	int n;
	int		nErrCode;

	n = CommControl( hPort, 
		COMM_GET_LAST_ERROR, 
		(char *)&nErrCode,
		sizeof(nErrCode) );
	if (n != ERR_COMM_OK)
	{
		printf( "  ===>%s: error on getting error code got: %08x.\n",
			pszMsg, n );
	}
	else
	{
		printf( "  ===>%s: error code: %08x.\n",
			pszMsg, nErrCode );
	}
}


int TestClient(int nStdType, IN char *pszDevDescriptor, IN char *pszOpenParams,
			   IN int nTimeoutMS )
{
	HANDLE	hPort;
	int		nErrCode = ERR_COMM_OK;
	int     n, i;
	char	szBuf[TEST_DATA_LENGTH];
	char	szBufRead[TEST_DATA_LENGTH];
	int		nErrCount = 0;

	for (i = 0; i < sizeof(szBuf)-1; i ++)
	{
		szBuf[i] = (i%93) + 33;
	}

	printf( "0. Testing client port %s...\n", pszOpenParams );
	printf( "1. Opening port %s...\n", pszOpenParams );

	i = 1;

__OpenPort:
	hPort = CommOpen( GetStandardPortDriverName(nStdType),
		pszDevDescriptor, 
		pszOpenParams, 
		COMM_CLIENT_MODE,
		nTimeoutMS,
		&nErrCode);

	printf( "  ===> Port %s opened %s, error code: %08x.\n", 
		pszOpenParams, (hPort != NULL) ? "OK" : "failure",
		nErrCode );

	if (hPort == NULL)
	{
		return nErrCode;
	}

	ClearRxTx( hPort );

	for ( i = 1; s_nRunning; i++)
	{
		printf( "[%d,%d] 2. Testing write data to port...\n", i, nErrCount );

		n = CommWrite( hPort, szBuf, TEST_DATA_LENGTH );
		if (n != TEST_DATA_LENGTH)
		{
			n = CommControl( hPort, 
				COMM_GET_LAST_ERROR, 
				(char *)&nErrCode,
				sizeof(nErrCode) );
			if (n != ERR_COMM_OK)
			{
				printf( "  ===> Write data to port failure and "
					"trying to get error code got: %08x.\n",
					n );

			}
			else
			{
				printf( "  ===> Write data to port failure, error code: %08x.\n",
					nErrCode );
		
				nErrCode = CommGetLastError(hPort);
				printf( "  NEW ===> Write data to port failure, error code: %08x.\n",
					nErrCode );

			}

			ClearRxTx( hPort );
			nErrCount++;
		}

		else
		{
			printf( "  ===> Write data to port successful.\n" );
		}

		printf("3. Testing read data from port...\n");

		n = CommRead(hPort, szBufRead, TEST_DATA_LENGTH);
		if (n != TEST_DATA_LENGTH)
		{
			n = CommControl( hPort, 
				COMM_GET_LAST_ERROR,
				(char *)&nErrCode,
				sizeof(nErrCode) );
			if (n != ERR_COMM_OK)
			{
				printf( "  ===> Read data from port failure and "
					"trying to get error code got: %08x.\n",
					n );
			}
			else
			{
				printf( "  ===> Read data from port failure, error code: %08x.\n",
					nErrCode );
				nErrCode = CommGetLastError(hPort);
				printf( "  NEW ===> Write data to port failure, error code: %08x.\n",
					nErrCode );

#ifdef _IMPL_DIAL_SERIAL
				if (nErrCode == ERR_COMM_CONNECTION_BROKEN)
				{
					printf(" ===> Line is broken, close port and dial again...\n" );
					CommClose(hPort);
					goto __OpenPort;
				}
#endif //_IMPL_DIAL_SERIAL
			}

			ClearRxTx( hPort );
			nErrCount++;
		}
		else if (memcmp(szBuf, szBufRead, TEST_DATA_LENGTH) != 0)
		{
			printf( "We send the following data:\n" ); 
			dump_buf( szBuf, TEST_DATA_LENGTH-1);

			printf( "But we received the following data:\n" );
			dump_buf( szBufRead, TEST_DATA_LENGTH-1);

			printf( "  ===> Read data returns OK, but the returned data error!!!, sleep 5 seconds.\n" );
			Sleep( 5000 );

			ClearRxTx( hPort );
			nErrCount++;
		}
		else
		{
			printf( "  ===> Read data from port successful.\n" );
		}
		
	}

	printf("4. Testing close port...\n");
	printf( "Press Enter to close the port.\n" );
	getchar();

	nErrCode = CommClose(hPort);
	printf( "  ===> Close port %s, got error code %08x.\n",
		(nErrCode == ERR_COMM_OK) ? "OK" : "failure",
		nErrCode );

	return nErrCode;
}


int TestIncomingClient( HANDLE hPort )
{
	int		nErrCode = ERR_COMM_OK;
	int     n, i;
	char	szBufRead[TEST_DATA_LENGTH];
	int		nErrCount = 0;

	for (i = 1; s_nRunning; i++)
	{
		printf("[%d,%d] 0. Testing incoming client port...\n", i,nErrCount);
		printf("1. Testing read data from port...\n");

		n = CommRead(hPort, szBufRead, TEST_DATA_LENGTH);
		if (n != TEST_DATA_LENGTH)
		{
			n = CommControl( hPort, 
				COMM_GET_LAST_ERROR,
				(char *)&nErrCode,
				sizeof(nErrCode) );
			if (n != ERR_COMM_OK)
			{
				printf( "  ===> Read data from port failure and "
					"trying to get error code got: %08x.\n",
					n );

			}
			else
			{
				printf( "  ===> Read data from port failure, error code: %08x.\n",
					nErrCode );
				nErrCode = CommGetLastError(hPort);
				printf( "  NEW ===> Write data to port failure, error code: %08x.\n",
					nErrCode );
#ifdef _IMPL_DIAL_SERIAL
				if (nErrCode == ERR_COMM_CONNECTION_BROKEN)
				{
					printf(" ===> Line is broken, quiting...\n" );
					break;
				}
#endif //_IMPL_DIAL_SERIAL

			}

			ClearRxTx( hPort );
			nErrCount++;
		}

		else
		{
			printf( "  ===> Read data from port successful.\n" );
//			printf( "  ===> We received the following data:\n" ); 
//			dump_buf( szBufRead, TEST_DATA_LENGTH-1);
		}


		printf( "2. Testing write data to port...\n" );

		n = CommWrite( hPort, szBufRead, TEST_DATA_LENGTH );
		if (n != TEST_DATA_LENGTH)
		{
			n = CommControl( hPort, 
				COMM_GET_LAST_ERROR,
				(char *)&nErrCode,
				sizeof(nErrCode) );
			if (n != ERR_COMM_OK)
			{
				printf( "  ===> Write data to port failure and "
					"trying to get error code got: %08x.\n",
					n );

			}
			else
			{
				printf( "  ===> Write data to port failure, error code: %08x.\n",
					nErrCode );
				nErrCode = CommGetLastError(hPort);
				printf( "  NEW ===> Write data to port failure, error code: %08x.\n",
					nErrCode );
			}

			ClearRxTx( hPort );
			nErrCount++;
		}
		else
		{
			printf( "  ===> Write data to port successful.\n" );
		}

		if (i >= 10)
		{
			printf( "  ---------> Accepted client thread exit.\n" );
			break;
		}
	}

	//printf("4. Testing close incoming port...\n");
	//printf( "Press Enter to close the port.\n" );
	//getchar()	;

	printf( "----> client is closing port..\n");

	nErrCode = CommClose(hPort);
	printf( "  ===> Close port %s, got error code %08x.\n",
		(nErrCode == ERR_COMM_OK) ? "OK" : "failure",
		nErrCode );

	return nErrCode;
}

int TestServer(int nStdType,
			   IN char *pszDevDescriptor, IN char *pszOpenParams,
			   IN int nTimeoutMS )
{
	HANDLE	hPort;
	int		nErrCode = ERR_COMM_OK, nLastErr = 0;
	int     n;
    
	printf( "0. Testing server port %s...\n", pszOpenParams );
	printf( "1. Opening port %s...\n", pszOpenParams );

	hPort = CommOpen(GetStandardPortDriverName(nStdType),
		pszDevDescriptor, 
		pszOpenParams, 
		COMM_SERVER_MODE,
		nTimeoutMS,
		&nErrCode);

	printf( "  ===> Port %s opened %s, error code: %08x.\n", 
		pszOpenParams, (hPort != NULL) ? "OK" : "failure",
		nErrCode );

	if (hPort == NULL)
	{
		return nErrCode;
	}


	printf( "2. Testing accept...\n" );
	
	while (s_nRunning)
	{
		HANDLE hClient = CommAccept( hPort );

		if (hClient != NULL) 
		{
			COMM_PEER_INFO cpi;
		    pthread_t hThread;

			n = CommControl(hClient, 
				COMM_GET_PEER_INFO, 
				(char *)&cpi,
				sizeof(COMM_PEER_INFO) );
			if (n == ERR_COMM_OK)
			{
				printf( "\n\n============>The accepted serial port is %d, incoming phone is \"%s\".\n",
					cpi.nPeerPort, cpi.szAddr );
			}

			pthread_create( &hThread, 
				NULL,
				(PTHREAD_START_ROUTINE)TestIncomingClient,
				(void *)hClient );

			//Sleep(2000);
			//printf( "-------> close the accepted client port...\n");
			//CommClose(hClient);
		}

		else if ((n = CommControl(hPort, 
			COMM_GET_LAST_ERROR,
			(char *)&nErrCode,
			sizeof(nErrCode))) != ERR_COMM_OK)
		{
			printf( "  ===> Accept failure and also failed to"
				"get error code got: %08x.\n",
				n );
			goto TestFailure;
		}
		else if (nErrCode == ERR_COMM_TIMEOUT)
		{
			printf( "  ===> Accept timeout\n" );
		}
		else if (nErrCode == ERR_COMM_MANY_CLIENTS)
		{
			if (nLastErr != nErrCode)
			{
				printf( "  ===> Accept ERR_COMM_MANY_CLIENTS\n" );
			}

			Sleep(1000);
		}
		else
		{
			printf( "  ===> Accept failure and error code is %08x.\n",
				nErrCode );

			goto TestFailure;
		}

		nErrCode = CommGetLastError(hPort);
		printf( "  NEW ===> Accept error code: %08x.\n",
			nErrCode );
		Sleep(500);

		nLastErr = nErrCode;
	}	
	
TestFailure:
	printf("3. Testing close port...\n");
	printf( "Press Enter to close the port.\n" );
	getchar();

	nErrCode = CommClose(hPort);
	printf( "  ===> Close port %s, got error code %08x.\n",
		(nErrCode == ERR_COMM_OK) ? "OK" : "failure",
		nErrCode );

	return nErrCode;
}


/*==========================================================================*
 * FUNCTION : main
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int    argc    : 
 *            INT char  *argv[] : 
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int main(IN int argc, IN char *argv[])
{
	signal(SIGTERM, QuitSignalHandler);
	signal(SIGINT, QuitSignalHandler);

	if (argc == 6)
	{
		if(strcmp(argv[1], "-c") == 0)
		{
			TestClient( atoi(argv[2]), argv[3], argv[4], atoi(argv[5]) );
			return 0;
		}

		else if(strcmp(argv[1], "-s") == 0)
		{
			TestServer( atoi(argv[2]), argv[3], argv[4], atoi(argv[5]) );
			return 0;
		}
	}

	printf( 
		"Usage: test_hal_comm <-c|-s> std-port-id port-descr port-set timeout\n"
		" purpose           std-port-id  port-descriptor   port-setting\n"
		" test std serial         0        COM1 or COM2      b,p,d,s\n"
		" test dial serial        1        COM1 or COM2      b,p,d,s:tel\n"
		" test TCPIP port         2        eth0              n1.n2.n3.n4:port\n"
		" test ACU485 serial      3        COM4              b,p,d,s\n"
		);

	return -1;
}


//
// temporarily put the code here. maofuhua, 2005-2-13
//
#if xxx
#define PROT_TEST_ACK					0	// the data belongs to the prot
#define PROT_TEST_NAK					1	// the data does NOT belong to the prot
#define PROT_TEST_INSUFFICIENT_DATA		2	// need more data, fragment

/*==========================================================================*
 * FUNCTION : (*DATA_PROT_TEST_PROC)
 * PURPOSE  : The callback proc to test a frame belongs to the data protocol
 *            or not.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char	*pbFrame  : The frame to be tested
 *            IN int    nFrameLen : The bytes of data in the frame.
 *            IN void   *pvParam  : The param comes from register proc
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
typedef int (*DATA_PROT_TEST_PROC)(IN HANDLE	hProcessThread,
								   IN char		*pbFrame,
								   IN int		nFrameLen,
								   IN void		*pvParam);



/*==========================================================================*
 * FUNCTION : Comm_RegisterProtTester
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN const char           *pszProtName : 
 *            IN DATA_PROT_TEST_PROC  pfnTester    : 
 *            IN void                 *pvParam     : 
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int Comm_RegisterProtTester(IN const char			*pszProtName,
							IN HANDLE				hProcessThread,
							IN DATA_PROT_TEST_PROC	pfnTester,
							IN void					*pvParam)
{
}


//
#define COMM_EVENT_
int Comm_RegisterEventHandler(IN const char			*pszProtName,
							  IN HANDLE				hHandlerThread,
							  IN DWORD				dwEventMasks,
							  IN COMM_EVENT_HANDLER_PROC	pfnHandler,
							  IN void				pvParam)
{

}
#endif
