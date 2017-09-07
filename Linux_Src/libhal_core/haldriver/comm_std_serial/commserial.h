/*==========================================================================*
 *  FILENAME : commserial.h

 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#ifndef __COMMSERIAL_H__
#define __COMMSERIAL_H__

#include <termios.h>

/* 
 * If want to test the module with static mode,
 * please comment out the next line.
 */
#define _MAKE_SHARED_LIB

#ifdef _MAKE_SHARED_LIB

#define Serial_CommOpen			HAL_CommOpen
#define Serial_CommAccept		HAL_CommAccept
#define Serial_CommRead			HAL_CommRead
#define Serial_CommWrite		HAL_CommWrite
#define Serial_CommControl		HAL_CommControl
#define Serial_CommClose		HAL_CommClose

#endif //_MAKE_SHARED_LIB

/**********************************/
#define SERIAL_MAX_CLIENTS_DEFAULT	1	/* The default serial connections	*/
#define SERIAL_MAX_CLIENT_LIMITAION	1	/* only 1 connection is allowed		*/

struct _SERIAL_BAUD_ATTR
{
	int				nBaud;		// the baud rate
	int				nOdd;		// 0: none, 1: odd, 2: even
	int				nData;		// the data bits, 5-8
	int				nStop;		// the stop bits, 1-2
	unsigned long	ulUnixBaud;	// the unix defined baud rates.

#ifdef _IMPL_DIAL_SERIAL
//the phone num dialling in, empty if no
	char			szPhone[MAX_LEN_PEER_ADDR];
#ifdef _HAS_ACU_SCC
	int				fdSCCDTR;
#endif
#endif
};
typedef struct _SERIAL_BAUD_ATTR	SERIAL_BAUD_ATTR;


struct SSerialPortDriver	//	The HAL driver for direct serial port
{				
	int				nLastErrorCode;	//NOTE: MUST BE THE FIRST FIELD! 
							//  the last error code.

	int				fdSerial;	//	the handle from open()
	COMM_TIMEOUTS			toTimeouts;	//	read and write timeout
	int				nWorkMode;	//	see enum COMM_WORK_MODE_ENUM in "halcomm.h"
	int				nMaxClients;	//	always 1
	int				*pCurClients;	//	0 or 1.

	int				nSerialPort;	// the serial port start from 0.

#ifdef _IMPL_DIAL_SERIAL
//the phone num dialling in, empty if no
	char			szPhone[MAX_LEN_PEER_ADDR];
	time_t			tmLastInitModem;// The last time of modem initialized
	BOOL			bCommunicating;	// TRUE for in communication.
#endif

	SERIAL_BAUD_ATTR	attr;		// the attr of the port.
};				
typedef struct SSerialPortDriver		SERIAL_PORT_DRV;

#define COM_RETRY_TIME		3		// the maximum retry time on error.

#ifdef _IMPL_DIAL_SERIAL

#define MODEM_GET_STATUS	0
#define MODEM_SET_STATUS	1
#define MODEM_CLR_STATUS	2

extern int  Modem_Ioctl(IN HANDLE hPort, int nAction, int nCtrlSig);
extern BOOL Modem_Dial(IN HANDLE hPort, IN char *pszPhone);
extern BOOL Modem_CheckConnection( IN HANDLE hPort);
extern BOOL Modem_Hangup(IN HANDLE hPort);
extern BOOL Serial_SetCommAttributes(IN int fd, IN SERIAL_BAUD_ATTR *pAttr);
#endif


#endif /*__COMMSERIAL_H__*/
