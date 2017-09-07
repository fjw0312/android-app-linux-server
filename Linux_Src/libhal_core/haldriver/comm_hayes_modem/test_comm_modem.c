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
#include "commserial.h"

#define TEST_DATA_LENGTH 512


static int s_nRunning = 1;
static void QuitSignalHandler( int a )
{
	UNUSED(a);

    if( s_nRunning )
    {
        printf( "Receives a quit signal. Quiting ... \007\007\n" );
        s_nRunning = 0;
    }
    else
    {
        printf( "App is quiting, please wait ... \007\007\n" );
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
	HAL_CommControl( hPort, COMM_PURGE_TXCLEAR, (void*)1, 0);
	HAL_CommControl( hPort, COMM_PURGE_RXCLEAR, (void*)1, 0);
}


#ifdef __TEST_MODEM
static int OpenModem( char *pszSerialPort )
{
	int fd;
	struct termios options;
	/* open the port */

	fd = open(pszSerialPort, O_RDWR | O_NOCTTY | O_NDELAY);
	fcntl(fd, F_SETFL, 0);
	/* get the current options */
	tcgetattr(fd, &options);
	/* set raw input, 1 second timeout */
	options.c_cflag |= (CLOCAL | CREAD);
	options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
	options.c_oflag &= ~OPOST;
	options.c_cc[VMIN] = 0;
	options.c_cc[VTIME] = 10;
	/* set the options */
	tcsetattr(fd, TCSANOW, &options);

	return fd;
}

/* O - 0 = MODEM ok, -1 = MODEM bad */
static int init_modem(int fd) /* I - Serial port file */
{
	char buffer[255]; /* Input buffer */
	char *bufptr; /* Current char in buffer */
	int nbytes; /* Number of bytes read */
	int tries; /* Number of tries so far */
	for (tries = 0; tries < 3; tries ++)
	{
		/* send an AT command followed by a CR */
		if (write(fd, "AT\r", 3) < 3)
			continue;
		sleep(1);
		/* read characters into our string buffer until we get a CR or NL */
		bufptr = buffer;
		while ((nbytes = read(fd, bufptr, buffer + sizeof(buffer) - bufptr-1)) > 0)
		{
			bufptr += nbytes;
			if (bufptr[-1] == '\n' || bufptr[-1] == '\r')
				break;
		}

		/* nul terminate the string and see if we got an OK response */
		*bufptr = '\0';

		printf( "recv:%s\n", buffer );

		if (strncmp(buffer, "OK", 2) == 0)
			return (0);
	}
	return (-1);
}
#endif

static void ShowLastError( HANDLE hPort, char *pszMsg )
{
	int n;
	int		nErrCode;

	n = HAL_CommControl( hPort, 
		COMM_GET_LAST_ERROR, &nErrCode,
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


static BOOL bClientIsRunning = FALSE;
static void *TestInteractClientreadThread(HANDLE hPort)
{
	int		nErrCode = ERR_COMM_OK;
	int     n, i;
	char	szBufRead[2];

	while(s_nRunning)
	{
		//printf("reading...\n");
		n = HAL_CommRead(hPort, szBufRead, sizeof(szBufRead)-1);
		if (n > 0)
		{
			szBufRead[n] = 0;	// end flag
			printf( "%s", szBufRead );
			fflush(stdout);
		}
	}

	bClientIsRunning = FALSE;
	return 0;
}


int TestInteractClient(HANDLE hPort)
{
	int		nErrCode = ERR_COMM_OK;
	int     n, i;
	char	szBuf[TEST_DATA_LENGTH];
	pthread_t hThread;

	ClearRxTx( hPort );

	bClientIsRunning = TRUE;

	pthread_create( &hThread, 
		NULL,
		(PTHREAD_START_ROUTINE)TestInteractClientreadThread,
		(void *)hPort );

	printf( "Please input an AT command('quit' to quit):\n" );


	while(bClientIsRunning)
	{
		printf( ">>>" ); 
		fflush( stdout );

//		n = scanf( "%s", szBuf );
		fgets(szBuf, sizeof(szBuf), stdin);
//		if( n != 0 )
		{
			strcat( szBuf, "\r" );
			n = 1+strlen( szBuf );
			//dump_buf( szBuf, n );

			if( strnicmp(szBuf, "quit", 4) == 0 )
			{
				bClientIsRunning = FALSE;
				break;
			}

			i = HAL_CommWrite( hPort, szBuf, n );
			if (n != i)
			{
				ShowLastError( hPort, (char *)"Writing Data" );
				ClearRxTx( hPort );
			}

  			fflush( stdout );
		}
	}
	
	Sleep(1000);

	printf("4. Testing close port...\n");

	nErrCode = HAL_CommClose(hPort);
	printf( "  ===> Close port %s, got error code %08x.\n",
		(nErrCode == ERR_COMM_OK) ? "OK" : "failure",
		nErrCode );

	bClientIsRunning = FALSE;

	return nErrCode;
}



int TestModem( IN char *pszDevDescriptor, IN char *pszOpenParams,
			   IN int nTimeoutMS )
{
	HANDLE	hPort;
	int		nErrCode = ERR_COMM_OK;
	int     n, i;
	char	szBuf[TEST_DATA_LENGTH];
	char	szBufRead[TEST_DATA_LENGTH];

	printf( "0. Testing modem port %s...\n", pszOpenParams );
	printf( "1. Opening port %s...\n", pszOpenParams );
#ifdef __TEST_MODEM
	int fd = OpenModem( pszDevDescriptor );
	if( fd == -1 )
	{
		printf( "Open port %s failure\n", pszDevDescriptor );
		return 0;
	}

	nErrCode = init_modem( fd );
	if( nErrCode != 0 )
	{
		printf( "Init modem failure\n" );
	}
	else
	{
		printf( "Init modem OK\n" );
	}


	return 1;
#endif


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


	TestInteractClient(hPort);


	return nErrCode;
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

	i = 1;

__OpenPort:
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

	for ( i = 1; s_nRunning; i++)
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
#ifdef _IMPL_DIAL_SERIAL
				if (nErrCode == ERR_COMM_CONNECTION_BROKEN)
				{
					printf(" ===> Line is broken, close port and dial again...\n" );
					HAL_CommClose(hPort);
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

	for (i = 1; s_nRunning; i++)
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
			//printf( "  ===> We received the following data:\n" ); 
			//dump_buf( szBufRead, TEST_DATA_LENGTH-1);
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
	}

	printf("4. Testing close incoming port...\n");
	printf( "Press Enter to close the client port.\n" );
	getchar();

	nErrCode = HAL_CommClose(hPort);
	printf( "  ===> Close port %s, got error code %08x.\n",
		(nErrCode == ERR_COMM_OK) ? "OK" : "failure",
		nErrCode );

	bClientIsRunning = FALSE;
	s_nRunning = 1;// to continue run server.

	return nErrCode;
}

static void *pTestClient = TestIncomingClient;
static int bAutoTest = TRUE;

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

	while (s_nRunning || bClientIsRunning)
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
				printf( "The accepted serial port is %d, incoming phone is \"%s\".\n",
					cpi.nPeerPort, cpi.szAddr );
			}

			pthread_create( &hThread, 
				NULL,
				(PTHREAD_START_ROUTINE)pTestClient,
				(void *)hClient );

			bClientIsRunning = TRUE;
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
	printf("3. Testing close server port...\n");
	printf( "Press Enter to close the server port.\n" );
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
	signal(SIGTERM, QuitSignalHandler);
	signal(SIGINT, QuitSignalHandler);

	if (argc == 5)
	{
		if(strcmp(argv[1], "-m") == 0)
		{
			TestModem( argv[2], argv[3], atoi(argv[4]) );
		}
		else if(strcmp(argv[1], "-c") == 0)
		{
			TestClient( argv[2], argv[3], atoi(argv[4]) );
			return 0;
		}

		else if(strncmp(argv[1], "-s", 2) == 0)
		{
			if (argv[1][2] == 'i')
			{
				printf("Enter interact test\n");
				pTestClient = (void *)TestInteractClient;
			}

			TestServer( argv[2], argv[3], atoi(argv[4]) );
			return 0;
		}
	}

	else
	{
		printf( "Usage: test_comm_serial <-c|-m> serial baud,n,d,s:phone timeout\n"
			"   or: test_comm_serial -<s|si> serial baud,n,d,s timeout\n"
			"       si and m are interactive test with Terminal.\n");
		return -1;
	}


	return 0;
}
