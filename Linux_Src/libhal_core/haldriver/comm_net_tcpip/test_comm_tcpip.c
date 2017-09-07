/*==========================================================================*
 *  FILENAME : test_comm_tcpip.c
 *  VERSION  : V1.00
 *  PURPOSE  : To test comm_net_tcpip.so
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#include "stdsys.h"		/* all standard system head files			*/
#include <netinet/in.h>			/* sockaddr_in, sockaddr */
#include <arpa/inet.h>			/* inet_ntoa */
#include <sys/socket.h>			/* socket, bind, accept, setsockopt, */
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"

#define TEST_DATA_LENGTH 1024

int TestClient( IN char *pszDevDescriptor, IN char *pszOpenParams,
			   IN int nTimeoutMS )
{
	HANDLE	hPort;
	int		nErrCode = ERR_COMM_OK;
	int     n;
	char	szBuf[TEST_DATA_LENGTH] = "Test TCP_IP port.1234567890.";
	char	szBufRead[TEST_DATA_LENGTH];

	printf( "0. Testing client port %s...\n", pszOpenParams );
	printf( "1. Opening port %s...\n", pszOpenParams );

	hPort = HAL_CommOpen(pszDevDescriptor, 
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


	printf( "2. Testing write data to port...\n" );

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

		goto TestFailure;
	}

	printf( "  ===> Write data to port successful.\n" );

	printf("3. Testing read data from port...\n");

	n = HAL_CommRead(hPort, szBufRead, TEST_DATA_LENGTH);
	if (n != TEST_DATA_LENGTH)
	{
		n = HAL_CommControl( hPort, 
			COMM_GET_LAST_ERROR, &nErrCode,
			sizeof(nErrCode) );
		if (n != ERR_COMM_OK)
		{
			printf( "  ===> Read data to port failure and "
				"trying to get error code got: %08x.\n",
				n );

		}
		else
		{
			printf( "  ===> Read data to port failure, error code: %08x.\n",
				nErrCode );
		}

		goto TestFailure;
	}
	else if (memcmp(szBuf, szBufRead, TEST_DATA_LENGTH) != 0)
	{
		printf( "  ===> Read data returns OK, but the returned data error!!!.\n" );
		goto TestFailure;
	}

	printf( "  ===> Read data to port successful.\n" );
	

TestFailure:
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
	int     n;
	char	szBufRead[TEST_DATA_LENGTH];

	printf( "0. Testing incoming client port...\n" );
	printf("1. Testing read data from port...\n");

	n = HAL_CommRead(hPort, szBufRead, TEST_DATA_LENGTH);
	if (n != TEST_DATA_LENGTH)
	{
		n = HAL_CommControl( hPort, 
			COMM_GET_LAST_ERROR, &nErrCode,
			sizeof(nErrCode) );
		if (n != ERR_COMM_OK)
		{
			printf( "  ===> Read data to port failure and "
				"trying to get error code got: %08x.\n",
				n );

		}
		else
		{
			printf( "  ===> Read data to port failure, error code: %08x.\n",
				nErrCode );
		}

		goto TestFailure;
	}

	printf( "  ===> Read data to port successful.\n" );
	
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

		goto TestFailure;
	}

	printf( "  ===> Write data to port successful.\n" );


TestFailure:
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
	int		nErrCode = ERR_COMM_OK;
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
			DWORD dwPeerIP = 0;
		    pthread_t hThread;


			n = HAL_CommControl(hClient, 
				COMM_GET_PEER_INFO, &cpi,
				sizeof(COMM_PEER_INFO) );
			if (n == ERR_COMM_OK)
			{
				// szAddr is in network format.
				dwPeerIP = *(DWORD *)cpi.szAddr;
			}


			printf("A client %s is accepted.\n",
				inet_ntoa(*(struct in_addr *)&dwPeerIP));

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
			printf( "  ===> Accept ERR_COMM_MANY_CLIENTS\n" );
		}
		else
		{
			printf( "  ===> Accept failure and error code is %08x.\n",
				nErrCode );

			goto TestFailure;
		}
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
	if (argc == 4)
	{
		if(strcmp(argv[1], "-c") == 0)
		{
			TestClient( "/dev/eth0",/* ignored */
				argv[2], atoi(argv[3]) );
			return 0;
		}

		else if(strcmp(argv[1], "-s") == 0)
		{
			TestServer( "/dev/eth0",/* ignored */
				argv[2], atoi(argv[3]) );
			return 0;
		}
	}

	else
	{
		printf( "Usage: test_comm_tcpip <-c|-s> ip:port timeout\n" );
		return -1;
	}


	return 0;
}
