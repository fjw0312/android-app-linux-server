/*==========================================================================*
 *  FILENAME : commdial.c
 *  VERSION  : V1.00
 *  PURPOSE  :
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

#include "commserial.h"	/* The private head file for this module	*/

#ifdef _DEBUG
#define _DEBUG_MODEM_PORT  1
#endif //_DEBUG

#define MODEM_USES_DTR	1	// use DTR signal, undef to ignore. maofuhua. 2005-5-20

#define MODEM_RETRY_TIME	2		// retry 2 times if meet some error
#define MODEM_RETRY_DELAY	1000	// sleep 1000 ms before retry.
#define MODEM_DIAL_WAIT	    60000	// wait for 1 minutes
#define MODEM_HANGUP_WAIT   5000	// wait for 5s, old is 10
#define MODEM_RESPOND_WAIT	5000	// wait for normal AT command
#define MODEM_INTERVAL_TIMEOUT	1000// for normal AT command.
#define MODEM_INIT_INTERVAL (15*60*1000)// init modem per 15 minutes

#define MODEM_RESPONSE_LEN	128		// Modem response msg length

struct _AT_ANSWER
{
	const char	*pszAnswer;		// AT answer, must be defined in upper case
	int			nErrCode;		// the corresponding error code of the answer
};

typedef struct _AT_ANSWER	AT_ANSWER;

static int Modem_AtCommand(IN HANDLE hPort, IN const char *pszAT,
						 IN AT_ANSWER *pAnswers, IN int nAnswers,
						 IN int nTimeout, IN int nIntervalTimeout);


//#undef _RESET_MODEM	// do NOT reset modem.
#ifdef _RESET_MODEM
/*==========================================================================*
 * FUNCTION : Modem_Reset
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : static BOOL : 
 * COMMENTS : 
 *==========================================================================*/
static BOOL Modem_Reset(IN HANDLE hPort)
{
	char	  *pAtCmd = "ATZ &F\n\r"; /* reset and restore factory set*/ 
						
	AT_ANSWER answers[] = 
	{ 
		{"OK",		ERR_COMM_OK}
	};


#ifdef MODEM_USES_DTR
	// clear DTR to huangup
	Modem_Ioctl( hPort, MODEM_CLR_STATUS, TIOCM_DTR );
	Sleep(500);
	// set DTR
	Modem_Ioctl( hPort, MODEM_SET_STATUS, TIOCM_DTR );
#endif

	// wait time is 10 seconds ok?
	Modem_AtCommand(hPort, pAtCmd, answers, ITEM_OF(answers),
		MODEM_RESPOND_WAIT*2, MODEM_INTERVAL_TIMEOUT*2);

	return TRUE;
}
#endif

/*==========================================================================*
 * FUNCTION : Modem_Init
 * PURPOSE  : Init the modem with HAYES AT command, and make the modem 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : static BOOL : 
 * COMMENTS : 
 *==========================================================================*/
static BOOL Modem_Init(IN HANDLE hPort)
{
	SERIAL_PORT_DRV *pPort = (SERIAL_PORT_DRV *)hPort;
	time_t tmNow = time(NULL);

	if ( ((tmNow - pPort->tmLastInitModem) >= MODEM_INIT_INTERVAL)
		|| (pPort->tmLastInitModem > tmNow) )
	{
		int		i;
		const char	*pszAT[] = 
		{
			"AT&F "		// Set factory setting of mode V.43BIS 
			"E0 "		// Close text echo
			"V1 "		// use sentence to display answers
			"Q0 "		// allow return answer codes
			"L1 "		// Low speaker volume
			"X4 "		// Open result code 0-7 and 10, open detect busy and dial tone
#ifdef MODEM_USES_DTR
			"&D2 "		// Drop DTR to hangup, and return to AT cmd state
#else
			"&D0 "		// Ingnore DTR signal
#endif
			"&C1 "		// Trace CD status
			"S0=1\n\r",	// Auto respond: RING count is 1, no-zero be auto.
		};
		AT_ANSWER answers[] = 
		{ 
			{"OK",		ERR_COMM_OK}
		};


#ifdef _RESET_MODEM
		// reset the modem.
		Modem_Reset( hPort );
#endif

		for (i = 0; i < (int)ITEM_OF(pszAT); i++)
		{
			Modem_AtCommand(hPort, pszAT[i], answers, ITEM_OF(answers),
				MODEM_RESPOND_WAIT, MODEM_INTERVAL_TIMEOUT);
			Sleep( 100 );
		}

//	Do NOT check DSR for ACU comm drv has no this signal.	
//		if (Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_DSR) != 1)
//		{
//			pPort->tmLastInitModem = 0;	// last time need re-init.
//			TRACEX("Fails on getting TIOCM_DSR, re-init modem.\n");
//			return FALSE;
//		}

		pPort->tmLastInitModem = tmNow;
	}

#ifdef MODEM_USES_DTR
	// set DTR
	Modem_Ioctl( hPort, MODEM_SET_STATUS, TIOCM_DTR );
#endif

	return TRUE;
}

//refer to WIN32
#define MS_CTS_ON	TIOCM_CTS
#define MS_DSR_ON	TIOCM_DSR
#define MS_RING_ON	TIOCM_RI
#define MS_RLSD_ON	TIOCM_CD


#ifdef _HAS_ACU_SCC	// defined in config_vars.mk
// use the special IOCTL for ACU scc.
// the DTR can be set/cleared and only the CD status can be read.
// maofuhua, 2005-2-24

#define ACU_SCC_Ioctl	Modem_Ioctl

/*==========================================================================*
 * FUNCTION : ACU_SCC_Ioctl
 * PURPOSE  : Control or get the status of DTR/CD status of ACU.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: int  nAction  : get/set/clr.
 *            int  nCtrlSig : get support TIOCM_CD, set/clr support TIOCM_DTR
 * RETURN   : int : 1: the sig is set, 0 for not set, -1 for error
 * COMMENTS : 	// the signal can BE set, but can NOT be read out when there
 *             is NO CD. but the DTR must be set 0(ON) before answering the 
 *             income phone or dailing phone.
 *==========================================================================*/
int ACU_SCC_Ioctl(IN HANDLE hPort, int nAction, int nCtrlSig)
{
#define	sccdtr_fd		(((SERIAL_PORT_DRV *)hPort)->attr.fdSCCDTR)
#ifndef sccdtr_fd
	int				sccdtr_fd = -1;
#endif

	unsigned char	data = (char)-1;
	int				nResult = -1;
	// 0: Low level,  DCD is ON
	// 1: High level, DCD is OFF.

	ASSERT(hPort);

#ifndef sccdtr_fd
	sccdtr_fd = open("/dev/sccdtr", O_RDWR); /*O_WRONLY*/
	if (sccdtr_fd <= 0)
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("failed to open /dev/sccdtr! \n");
#endif
		return -1;
	}
#endif

	// get the CD status.
	if (nAction  == MODEM_GET_STATUS)
	{
		if (nCtrlSig == TIOCM_CD)
		{
			read(sccdtr_fd, &data, 1);
	
			//TRACEX("The modem carray status is %d(0 is ON).\n",
			//	(int)data);
			nResult = (((int)data) == 0) ? 1 : (((int)data) > 0) ? 0 : -1;
		}
	}

	else if (nAction  == MODEM_CLR_STATUS)
	{	
#ifdef MODEM_USES_DTR
		// drop DTR,
		if (nCtrlSig == TIOCM_DTR)
		{
			data = 1;		// drop the DTR, hangup.
			write(sccdtr_fd, &data, 1);
			nResult = 0;	// cleared.
		}
#endif
	}

	else if (nAction  == MODEM_SET_STATUS)
	{
#ifdef MODEM_USES_DTR
		// set DTR
		if (nCtrlSig == TIOCM_DTR)
		{
			data = 0;		// set DTR
			write(sccdtr_fd, &data, 1);
			nResult = 1;	// be set.
		}
#endif
	}

#ifndef sccdtr_fd
	close (sccdtr_fd);
#endif

	return nResult;
}

#else	// standard Modem Ioctl

/*==========================================================================*
 * FUNCTION : Modem_Ioctl
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort    : 
 *            int        nCtrlSig : 
 *            int        nAction  : 0: for get, 1: set, 2: clear status
 * RETURN   : int : 1: the sig is set, 0 for not set, -1 for error
 * COMMENTS : 
 *==========================================================================*/
int Modem_Ioctl(IN HANDLE hPort, int nAction, int nCtrlSig)
{
	int		nStatus;

	if (ioctl( ((SERIAL_PORT_DRV *)hPort)->fdSerial, TIOCMGET, &nStatus) < 0)
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("(ioctl( ((SERIAL_PORT_DRV *)hPort)->fdSerial, "
			"TIOCMGET, &nStatus) < 0) Modem_Ioctl return -1\n");
#endif
		return -1;
	}

	//TRACEX(">>>>>>Modem status is %x.\n", nStatus);

	if (nAction == MODEM_GET_STATUS)
	{
		//return ((nCtrlSig & nStatus) == nCtrlSig) ? 1 : 0;
		return ((nStatus & nCtrlSig) == nCtrlSig) ? 1 : 0; 
	}

	if (nAction == MODEM_SET_STATUS)
	{
		nStatus |= nCtrlSig;
	}

	else if (nAction == MODEM_CLR_STATUS)
	{
		nStatus &= ~nCtrlSig;
	}

	else
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("(nAction != MODEM_SET_STATUS)return -1\n");
#endif
		return -1;	// error.
	}

	if (ioctl( ((SERIAL_PORT_DRV *)hPort)->fdSerial, TIOCMSET, &nStatus) < 0)
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("(ioctl( ((SERIAL_PORT_DRV *)hPort)->fdSerial, TIOCMSET, &nStatus) < 0)\n");
#endif
		return -1;		// error.
	}

	return (nAction == MODEM_SET_STATUS) ? 1 : 0;
}
#endif


/*==========================================================================*
 * FUNCTION : Modem_AtCommand
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort       : 
 *            IN char    *pszAT: full at command with 'AT'
 *            IN AT_ANSWER *pAnswers : the answer lists in upper case
 *            IN int     nAnswers    : the number of answers
 *            IN int     nTimeout    : 
 * RETURN   : static int : return matched error code.
 * COMMENTS : 
 *==========================================================================*/
static int Modem_AtCommand(IN HANDLE hPort, IN const char *pszAT,
						 IN AT_ANSWER *pAnswers, IN int nAnswers,
						 IN int nTimeout, IN int nIntervalTimeout )
{
	SERIAL_PORT_DRV *pPort = (SERIAL_PORT_DRV *)hPort;
	COMM_TIMEOUTS	toOld  = pPort->toTimeouts;	// save timeout
	int		nLen, i;
	char	szResponse[MODEM_RESPONSE_LEN+1];
	int		nErrCode = ERR_COMM_CTRL_MODEM;
	BOOL	bToWaitResponse = TRUE;
	
	// set the new timeout before controlling
	INIT_TIMEOUTS_EX(pPort->toTimeouts, nTimeout, nTimeout, nIntervalTimeout );


#ifdef MODEM_USES_DTR
	// set DTR
	Modem_Ioctl( hPort, MODEM_SET_STATUS, TIOCM_DTR );
#endif

	// if pszAT != null, we need send AT command.
	// else we only to get the modem answers
	if ((pszAT != NULL) && (*pszAT != 0))
	{
		// clear buffer
		Serial_CommControl( hPort, COMM_PURGE_TXCLEAR, NULL, 0);
		Serial_CommControl( hPort, COMM_PURGE_RXCLEAR, NULL, 0);

		// send the AT command out.
		nLen = strlen(pszAT);

		// there is an \r\n in AT command
#ifdef _DEBUG_MODEM_PORT
		TRACE("[Modem_AtCommand] -- send AT command %s", pszAT);
#endif //_DEBUG_MODEM_PORT

		if (Serial_CommWrite(hPort, (char *)pszAT, nLen) != nLen)
		{
#ifdef _DEBUG_MODEM_PORT
			TRACE( "[Modem_AtCommand] -- Fails on sending AT command.\n" );
#endif //_DEBUG_MODEM_PORT
			bToWaitResponse = FALSE;
		}
	}

	if (bToWaitResponse)
	{
		// read the response from modem. 
		szResponse[0] = 0;
//		Sleep( 1000 );
		
		nLen = Serial_CommRead(hPort, szResponse, MODEM_RESPONSE_LEN);
		if (nLen > 0)
		{
			szResponse[nLen] = 0;		// end flag.
			(void)strupr(szResponse);	// to upper the string,

			// OK, let's check the response
			for (i = 0; i < nAnswers; i++)
			{
				// to the check the answer.
				if (strstr(szResponse, pAnswers[i].pszAnswer) != NULL)
				{
#ifdef _DEBUG_MODEM_PORT
					TRACE("====>got %s(((%s))).\n",
						pAnswers[i].pszAnswer, szResponse);
#endif //_DEBUG_MODEM_PORT

					nErrCode = pAnswers[i].nErrCode;
					break;
				}
			}

#ifdef _DEBUG_MODEM_PORT
			if (i == nAnswers)
			{
				TRACE( "====>got unkown response: %s.\n",
					szResponse );
			}
#endif //_DEBUG_MODEM_PORT
		}

#ifdef _DEBUG_MODEM_PORT
		else
		{
			TRACE( "====>timeout on getting response.\n" );
		}	
#endif //_DEBUG_MODEM_PORT
	}

	pPort->toTimeouts = toOld;	// restore timeouts


	return nErrCode;
}


/*==========================================================================*
 * FUNCTION : Modem_Dial
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort     : 
 *            IN char    *pszPhone : 
 * RETURN   : BOOL : TRUE for OK, false for error, 
 *                    errcode see pPort->nLastErrorCode
 * COMMENTS : 
 *==========================================================================*/
BOOL Modem_Dial(IN HANDLE hPort, IN char *pszPhone)
{
	char	szAT[MAX_LEN_PEER_ADDR+20];	// ATDTxxxxx\r
	int		nRetry = MODEM_RETRY_TIME;
	SERIAL_PORT_DRV *pPort = (SERIAL_PORT_DRV *)hPort;

	int		nErrCode;
	AT_ANSWER answers[] = 
	{ 
		{"CONNECT",		ERR_COMM_OK},
		{"BUSY",		ERR_COMM_PHONE_BUSY},
		{"NO CARRIER",	ERR_COMM_NO_CARRIER},
		{"NO DIALTONE", ERR_COMM_NO_DIALTONE}
	};

	if (Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) == 1)	// connected?
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("Modem is connecting, hangup it at first!\n");
#endif
		// hangup first!
		Modem_Hangup(hPort);
	}

	pPort->tmLastInitModem = 0;	// force to init modem.
	Modem_Init(hPort);

#ifdef MODEM_USES_DTR
	// clear DTR to huangup
	Modem_Ioctl( hPort, MODEM_CLR_STATUS, TIOCM_DTR );
	Sleep(500);
	// set DTR
	Modem_Ioctl( hPort, MODEM_SET_STATUS, TIOCM_DTR );
#endif

	// make the dial AT command.
	sprintf(szAT, "ATDT%s\r\n", pszPhone);

	while (nRetry-- > 0)
	{
		nErrCode = Modem_AtCommand(hPort, szAT, answers, ITEM_OF(answers),
			MODEM_DIAL_WAIT, MODEM_INTERVAL_TIMEOUT );

		if(nErrCode == ERR_COMM_OK)
		{
			break;
		}

		// retry.
		Sleep(MODEM_RETRY_DELAY);	// sleep a while to try again.

		// is connected?
		if (Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) == 1)
		{
			nErrCode = ERR_COMM_OK;
			break;
		}
	}

	pPort->nLastErrorCode = nErrCode;

	return (nErrCode == ERR_COMM_OK) ? TRUE : FALSE;
}


/*==========================================================================*
 * FUNCTION : Modem_CheckConnection
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : BOOL : 
 * COMMENTS : 
 *==========================================================================*/
BOOL Modem_CheckConnection( IN HANDLE hPort )
{
	BOOL bConnected = FALSE;

#ifdef MODEM_USES_DTR
	// set DTR
	Modem_Ioctl( hPort, MODEM_SET_STATUS, TIOCM_DTR );
#endif


#ifndef _HAS_ACU_SCC
	// is ringing?
	if (Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_RNG) == 1)
	{
		int nRetry = MODEM_RETRY_TIME;

		AT_ANSWER answers[] = 
		{ 
			{"RING",		ERR_COMM_OK-1},
			{"CONNECT",		ERR_COMM_OK  },
		};

#ifdef _DEBUG_MODEM_PORT
		TRACE( "[Modem_CheckConnection] -- Ringing...\n" );
#endif //_DEBUG_MODEM_PORT

		while (nRetry-- > 0)
		{
			if (Modem_AtCommand(hPort, NULL, answers, ITEM_OF(answers),
				MODEM_RESPOND_WAIT*6,			// wait for 30 seconds
				MODEM_INTERVAL_TIMEOUT*6) == ERR_COMM_OK)
			{
#ifdef _DEBUG_MODEM_PORT
				TRACEX("RING Detect OK...\n" );
#endif //_DEBUG_MODEM_PORT

				bConnected = TRUE;
			}
		}
	}
#endif

	// to check the connection status
	if (!bConnected && 
		(Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) == 1))
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("Carrier Detect OK.\n" );
#endif //_DEBUG_MODEM_PORT

		bConnected = TRUE;
	}
	else
	{
		// the modem is not connected, we init it.
		Modem_Init(hPort);

		Sleep(500);
	}

	return bConnected;
}


/*==========================================================================*
 * FUNCTION : Modem_Hangup
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : BOOL : TRUE for OK.
 * COMMENTS : 
 *==========================================================================*/
BOOL Modem_Hangup( IN HANDLE hPort )
{
	int		i, n;
	int		nErrCode  = ERR_COMM_CTRL_MODEM;
    
	const char	*pszAT[]= { "+++" "ATH0\r\n"};	// hangup command
	AT_ANSWER answers[] = 
	{ 
		{"OK",		ERR_COMM_OK}
	};

#ifdef _DEBUG_MODEM_PORT
	TRACEX("Hangup modem...\n");
#endif

#ifdef MODEM_USES_DTR
	// 1. drop DTR to hangup. wait about 2*5s.
	for (n = 0; (n < MODEM_RETRY_TIME) && (nErrCode != ERR_COMM_OK); n++)
	{
		Modem_Ioctl( hPort, MODEM_SET_STATUS, TIOCM_DTR);
		Sleep( MODEM_RETRY_DELAY );	// 1sec

		//1. drop DTR to hangup
		if (Modem_Ioctl( hPort, MODEM_CLR_STATUS, TIOCM_DTR) == 0)
		{	
			for (i = 0; i < MODEM_HANGUP_WAIT; i += MODEM_RETRY_DELAY)
			{
				Sleep( MODEM_RETRY_DELAY );	// 1sec

				// chk the connection status
				if (Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) == 0)
				{
					nErrCode = ERR_COMM_OK;
#ifdef _DEBUG_MODEM_PORT
					TRACEX("Hanguped by dropping DTR.\n");
#endif

					break;
				}
			}
		}
	}
#endif


	//2. if the phone does not hangup by dropping DTR, send AT command
	//   sleep 2*2*(1+1) = 8s
	for (n = 0; (n < MODEM_RETRY_TIME) && (nErrCode != ERR_COMM_OK); n++)
	{
		for (i = 0; i < (int)ITEM_OF(pszAT); i++)
		{
			nErrCode = Modem_AtCommand(hPort, pszAT[i], answers, ITEM_OF(answers),
				MODEM_HANGUP_WAIT, MODEM_INTERVAL_TIMEOUT);	// wait 1s

			if(nErrCode == ERR_COMM_OK)
			{
				break;
			}

			// retry.
			Sleep(MODEM_RETRY_DELAY);	// sleep a while to try again.

			// is not connected?
			if (Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) == 0)
			{
				nErrCode = ERR_COMM_OK;
				break;
			}
		}


#ifdef _DEBUG
		if(nErrCode == ERR_COMM_OK)
		{
			TRACEX("Hanguped by sending +++.\n");
		}
#endif //_DEBUG
	}

#ifdef _RESET_MODEM
	// reset the modem.
	Modem_Reset( hPort );
#endif

#ifdef MODEM_USES_DTR
	Modem_Ioctl( hPort, MODEM_CLR_STATUS, TIOCM_DTR);
#endif

	((SERIAL_PORT_DRV *)hPort)->nLastErrorCode = nErrCode;

	return (nErrCode == ERR_COMM_OK) ? TRUE : FALSE;
}
