/*==========================================================================*
 *  FILENAME : test_comm_serial.c
 *  VERSION  : V1.00
 *  PURPOSE  : To test comm_std_serial.so
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#include "stdsys.h"		/* all standard system head files			*/
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"
#include "commserial.h"

#define TEST_DATA_LENGTH 1024

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
	HAL_CommControl( hPort, COMM_PURGE_TXCLEAR, (void*)1, 0);
	HAL_CommControl( hPort, COMM_PURGE_RXCLEAR, (void*)1, 0);
}

int TestClient( IN char *pszDevDescriptor, IN char *pszOpenParams,
			   IN int nTimeoutMS )
{
	HANDLE	hPort;
	int		nErrCode = ERR_COMM_OK;
	int     n, i;
	char	szBuf[TEST_DATA_LENGTH];
	char	szBufRead[TEST_DATA_LENGTH];
	int		nErrCount = 0;

	for (i = 0; i < (int)(sizeof(szBuf)-1); i ++)
	{
		szBuf[i] = (i%93) + 33;
	}

	printf( "0. Testing client port %s...\n", pszOpenParams );
	printf( "1. Opening port %s...\n", pszOpenParams );

	hPort = HAL_CommOpen( pszDevDescriptor, 
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

	for ( i = 1; i > 0; i++)
	{
		printf( "[%d,%d] 2. Testing write data to port...\n", i, nErrCount );

		n = HAL_CommWrite( hPort, szBuf, TEST_DATA_LENGTH );
		if (n != TEST_DATA_LENGTH)
		{
			n = HAL_CommControl( hPort, 
				COMM_GET_LAST_ERROR, &nErrCode,
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
			}

			ClearRxTx( hPort );
			nErrCount++;
		}

		else
		{
			printf( "  ===> Write data to port successful.\n" );
		}

		printf("3. Testing read data from port...\n");

		n = HAL_CommRead(hPort, szBufRead, TEST_DATA_LENGTH);
		if (n != TEST_DATA_LENGTH)
		{
			n = HAL_CommControl( hPort, 
				COMM_GET_LAST_ERROR, &nErrCode,
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
			}

			ClearRxTx( hPort );
			nErrCount++;
		}
		else if (memcmp(szBuf, szBufRead, TEST_DATA_LENGTH) != 0)
		{
			printf( "We send the following data:\n" ); dump_buf( szBuf, TEST_DATA_LENGTH-1);
			printf( "But we received the following data:\n" ); dump_buf( szBufRead, TEST_DATA_LENGTH-1);

			printf( "  ===> Read data returns OK, but the returned data error!!!.\n" );

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

	nErrCode = HAL_CommClose(hPort);
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

	for (i = 1; i > 0; i++)
	{
		printf("[%d,%d] 0. Testing incoming client port...\n", i,nErrCount);
		printf("1. Testing read data from port...\n");

		n = HAL_CommRead(hPort, szBufRead, TEST_DATA_LENGTH);
		if (n != TEST_DATA_LENGTH)
		{
			n = HAL_CommControl( hPort, 
				COMM_GET_LAST_ERROR, &nErrCode,
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
			}

			ClearRxTx( hPort );
			nErrCount++;
		}

		else
		{
			printf( "  ===> Read data from port successful.\n" );
			printf( "  ===> We received the following data:\n" ); dump_buf( szBufRead, TEST_DATA_LENGTH-1);
		}


		printf( "2. Testing write data to port...\n" );

		n = HAL_CommWrite( hPort, szBufRead, TEST_DATA_LENGTH );
		if (n != TEST_DATA_LENGTH)
		{
			n = HAL_CommControl( hPort, 
				COMM_GET_LAST_ERROR, &nErrCode,
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
			}

			ClearRxTx( hPort );
			nErrCount++;
		}
		else
		{
			printf( "  ===> Write data to port successful.\n" );
		}

		if (i > 10)
		{
			printf( "-------------> client exit now.\n");
			break;
		}
	}

	printf("4. Testing close incoming port...\n");
	printf( "Press Enter to close the port.\n" );
	getchar();

	nErrCode = HAL_CommClose(hPort);
	printf( "  ===> Close port %s, got error code %08x.\n",
		(nErrCode == ERR_COMM_OK) ? "OK" : "failure",
		nErrCode );

	return nErrCode;
}

int TestServer(IN char *pszDevDescriptor, IN char *pszOpenParams,
			   IN int nTimeoutMS )
{
	HANDLE	hPort;
	int		nErrCode = ERR_COMM_OK, nLastErr = 0;
	int     n;
    
	printf( "0. Testing server port %s...\n", pszOpenParams );
	printf( "1. Opening port %s...\n", pszOpenParams );

	hPort = HAL_CommOpen(pszDevDescriptor, 
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

	while (1)
	{
		HANDLE hClient = HAL_CommAccept( hPort );

		if (hClient != NULL) 
		{
			COMM_PEER_INFO cpi;
		    pthread_t hThread;

			n = HAL_CommControl(hClient, 
				COMM_GET_PEER_INFO, &cpi,
				sizeof(COMM_PEER_INFO) );
			if (n == ERR_COMM_OK)
			{
				printf( "The accepted serial port is %d\n",
					cpi.nPeerPort );
			}

			pthread_create( &hThread, 
				NULL,
				(PTHREAD_START_ROUTINE)TestIncomingClient,
				(void *)hClient );
		}

		else if ((n = HAL_CommControl(hPort, 
			COMM_GET_LAST_ERROR, &nErrCode,
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
		}
		else
		{
			printf( "  ===> Accept failure and error code is %08x.\n",
				nErrCode );

			goto TestFailure;
		}

		nLastErr = nErrCode;
	}	

TestFailure:
	printf("3. Testing close port...\n");
	printf( "Press Enter to close the port.\n" );
	getchar();

	nErrCode = HAL_CommClose(hPort);
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
 * CREATOR  : Frank Mao                DATE: 2004-09-15 17:31
 *==========================================================================*/
int main(IN int argc, IN char *argv[])
{
	if (argc == 5)
	{
		if(strcmp(argv[1], "-c") == 0)
		{
			TestClient( argv[2], argv[3], atoi(argv[4]) );
			return 0;
		}

		else if(strcmp(argv[1], "-s") == 0)
		{
			TestServer( argv[2], argv[3], atoi(argv[4]) );
			return 0;
		}
	}

	else
	{
		printf( "Usage: test_comm_serial <-c|-s> serial baud,n,d,s timeout\n" );
		return -1;
	}


	return 0;
}
