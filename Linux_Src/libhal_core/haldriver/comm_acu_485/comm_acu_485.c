/*==========================================================================*
 *
 *  FILENAME : comm_acu_485.c
 *  VERSION  : V1.00
 *  PURPOSE  : set the attr of the 485 port, the method to set the attr is
 *             special.
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#include <sys/ioctl.h>
#include "stdsys.h"		/* all standard system head files			*/
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "err_code.h"

#include "commserial.h"	/* The private head file, use the std serial.*/
#include "st16c554.h"

#ifdef _DEBUG
#define _DEBUG_485_PORT
#endif //_DEBUG

#define ACU485_CommRead		HAL_CommRead

#define INVALID_ACU485_SPEED		(-1)

/*==========================================================================*
 * FUNCTION : ACU485_GetSpeedAttr
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nBaudRate : 
 * RETURN   : static int : -1 for error
 * COMMENTS : copied from lixi, pstest.c
 * CREATOR  : Mao Fuhua(Frank)         DATE: 2004-11-27 18:00
 *==========================================================================*/
static int ACU485_GetSpeedAttr(IN int nBaudRate)
{
	// the special speed for ACU 485
	const int mtty_speed_arr[]= 
	{
		ST_B115200, ST_B57600, ST_B38400, ST_B19200, ST_B9600, ST_B4800,
		ST_B2400, ST_B1200, ST_B300
	};

	// the standard speed
	const int name_arr[] = 
	{
		115200, 57600, 38400,  19200,  9600,  4800,  
		2400,  1200,  300
	};

	int	i;

	ASSERT(ITEM_OF(mtty_speed_arr) == ITEM_OF(name_arr));

	for (i = 0; i < ITEM_OF(name_arr); i++)
	{
		if (name_arr[i] == nBaudRate)
		{
			return mtty_speed_arr[i];
		}
	}

	return INVALID_ACU485_SPEED;
}



/*==========================================================================*
 * FUNCTION : ACU485_SetCommAttributes
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int               fd     : 
 *            IN SERIAL_BAUD_ATTR  *pAttr : 
 * RETURN   : BOOL : 
 * COMMENTS : 
 * CREATOR  : Mao Fuhua(Frank)         DATE: 2004-11-27 17:53
 *==========================================================================*/
BOOL ACU485_SetCommAttributes(IN int fd, IN SERIAL_BAUD_ATTR *pAttr)
{
	int		nACU485Speed;

	nACU485Speed = ACU485_GetSpeedAttr(pAttr->nBaud);

	if (nACU485Speed == INVALID_ACU485_SPEED)
	{
		TRACEX("The speed %d is NOT support by the ACU 485 port.\n",
			pAttr->nBaud);
		return FALSE;
	}

	// set the baud rate
	if (ioctl(fd, (unsigned long)nACU485Speed, 0) != 0)
	{
		TRACEX("Set baudrate %d Error.\n", pAttr->nBaud);
		return FALSE;
	}

	TRACEX("The speed %d is set to the ACU 485 port OK.\n",
		pAttr->nBaud);

	return TRUE;
}



/* The "CommRead" proc for comm driver */
/*==========================================================================*
 * FUNCTION : ACU485_CommRead
 * PURPOSE  : Read data from a opened ACU 485 port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort        : the opened port
 *            OUT char   *pBuffer     : The buffer to save received data
 *            IN int     nBytesToRead :
 * RETURN   : int : the actual read bytes.
 * COMMENTS : The caller shall check the bytes to read is equal to the returned
 *            bytes to decide the read action is successful or not.
 * CREATOR  : Frank Mao                DATE: 2004-09-13 21:17
 *==========================================================================*/
int ACU485_CommRead(IN HANDLE hPort, OUT char *pBuffer, IN int nBytesToRead)
{
	SERIAL_PORT_DRV *pPort     = (SERIAL_PORT_DRV *)hPort;
	int				fd		   = pPort->fdSerial;
	int				nBytesRead = 0;
	int			    nTotalBytesRead = 0;	// total bytes of data being read
	int				nTimeout;

	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;

	// when data is NOT ready, the WaitFiledReadable() always return 1, 
	// but the read returns -1. so the WaitFiledReadable can NOT be used
	// in reading data from the ACU 485 Port.
	nTimeout = pPort->toTimeouts.nReadTimeout;

    while (nBytesToRead > 0)
    {
		nBytesRead = read(fd, pBuffer, (size_t)nBytesToRead);

		//TRACEX("To read %d bytes, this read %d bytes data, total %d.\n",
		//	nBytesToRead, nBytesRead, nTotalBytesRead);

		if (nBytesRead > 0)	/* we got some data */
        {
            // ok
            nBytesToRead	-= nBytesRead;
            pBuffer			+= nBytesRead;
			nTotalBytesRead += nBytesRead;

			if (nBytesToRead == 0)
			{
				//TRACEX("OK!!! To read %d bytes, this read %d bytes data, total %d.\n",
				//	nBytesToRead, nBytesRead, nTotalBytesRead);

				break;	// reading finished
			}

			// Need clear retry times? We don't clear it!
			// nRetryTimes = 0;
			nTimeout = pPort->toTimeouts.nIntervalTimeout;
		}

		else if (nBytesRead <= 0)
		{
			if (nTimeout <= 0)	// timeout
			{
				pPort->nLastErrorCode = ERR_COMM_TIMEOUT;

				//TRACEX("Read total %d bytes on timeout.\n",
				//	nTotalBytesRead);

				break;	// timeout
			}

			// sleep a while to read.
			Sleep((DWORD)pPort->toTimeouts.nIntervalTimeout);
			nTimeout -= pPort->toTimeouts.nIntervalTimeout;
		}
    }

    return nTotalBytesRead;
}
		
