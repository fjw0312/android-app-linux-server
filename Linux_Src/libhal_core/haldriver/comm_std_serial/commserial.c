/*==========================================================================*
 *  FILENAME : comm_std_serial.c
 *  VERSION  : V1.00
 *  PURPOSE  : The HAL communication driver for standard serial port
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#include "stdsys.h"		/* all standard system head files			*/
#include <sys/file.h>	// for flock()
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"

#include "commserial.h"	/* The private head file for this module	*/

#include<linux/fs.h>//arm_linux_kernel_fs.h" 
#include<linux/types.h>
//#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

 //


#define ST_STOP1        0x0001
#define ST_STOP15       0x0003   //1.5 bit
#define ST_STOP2        0x0002
#define ST_LOOPBACK     0x0020
#define ST_CS5          0x0060
#define ST_CS6          0x0080
#define ST_CS7          0x00a0
#define ST_CS8          0x00c0
#define ST_PARNONE      0x0004
#define ST_PARODD       0x0008
#define ST_PAREVEN      0x000c
#define ST_PARSPACE    0x0010
#define ST_PARMASK    0x0014
#define ST_B300         0xb700
#define ST_B600         0xb800
#define ST_B1200        0xb900
#define ST_B1800        0xba00
#define ST_B2400        0xbb00
#define ST_B4800        0xbc00
#define ST_B7200        0xbd00
#define ST_B9600        0xbe00 
#define ST_B19200       0xbf00
#define ST_B38400       0xc000
#define ST_B57600       0xc100
#define ST_B115200      0xc200  

#define CMSPAR    010000000000 
#define  B8000  0010016
#define  B10416 0010017


#ifdef _DEBUG
//#define _DEBUG_SERIAL_PORT
#endif //_DEBUG

//the flag will be passed by compiler.
//#define _IMPL_DIAL_SERIAL	1// to implement the dialling serial

// the linux supported baud rates of serial
struct SUnixBaudList
{
    unsigned long ulUnixBaud;
    int     nNormalBaud;
};

//-- add by ylf 2006.08.25 --
// 目的：增加8000和10416波特率的支持

// 把原来的LINUX中的定义
//#define  B3500000 0010016
//#define  B4000000 0010017

// 改为：
#define  B8000  0010016
#define  B10416 0010017
//-- end add by ylf 2006.08.25 --

/*
//add two baud rate 8000 and 10416, added by wankun 2006.8.23
//use B3500000 replace B8000
//use B4000000 replace B10416
//*/

static int controlword = 0;

static struct SUnixBaudList s_UnixBaudList[] =
{
    { B50	  ,  50 },
    { B75	  ,  75 },
    { B110	  ,  110 },
    { B134	  ,  134 },
    { B150	  ,  150 },
    { B200	  ,  200 },
    { B300	  ,  300 },
    { B600	  ,  600 },
    { B1200	  ,  1200 },
    { B1800	  ,  1800 },
    { B2400	  ,  2400 },
    { B4800	  ,  4800 },
    //{ B3500000	  ,  8000 },//use B3500000 replace B8000
    { B8000	  ,  8000 },// add by ylf 2006.08.25
    { B9600	  ,  9600 },
    //{ B4000000  ,  10416 },//use B4000000 replace B10416
    { B10416  ,  10416 },// add by ylf 2006.08.25
    { B19200  ,  19200 },
    { B38400  ,  38400 },
    { B57600  ,  57600 },
    { B115200 ,  115200 },
    { B230400 ,  230400 },
    { B460800 ,  460800 },
    { B500000 ,  500000 },
    { B576000 ,  576000 },
    { B921600 ,  921600 },
    { B1000000,  1000000 },
    { B1152000,  1152000 },
    { B1500000,  1500000 },
    { B2000000,  2000000 },
    { B2500000,  2500000 },
    { B3000000,  3000000 },
    //{ B3500000,  3500000 }, // del by ylf 2006.08.25
    //{ B4000000,  4000000 }, // del by ylf 2006.08.25
    { 0,         0       } /* end flag */
};

#define MIN_SERIAL_BAUD		(s_UnixBaudList[0].nNormalBaud)
#define	MAX_SERIAL_BAUD		(s_UnixBaudList[ITEM_OF(s_UnixBaudList)-2].nNormalBaud)

static unsigned long ConvertBaudrateToUnix(int baud)
{
    struct SUnixBaudList    *p = s_UnixBaudList;

    while((p->nNormalBaud != baud) && (p->nNormalBaud != 0))
        p ++;

//#ifdef _DEBUG
//    printf("[ConvertBaudrateToUnix] -- convert normal baud %d to unix got %lu\n",
//        baud, p->ulUnixBaud);
//#endif 	/*_DEBUG	

    return p->ulUnixBaud;
}

#ifndef	SPACE 
#define SPACE  0x20
#endif

#define COMMA_SEPARATOR		','
#define COLON_SERARATOR		':'

static char *SplitText(IN char *S, OUT char *D, IN int cSep);

/*
void mdelay()
{
	int time,time2;
	for(time=0;time<10;time++)          
	{                                    
		for(time2=0;time2<10000;time2++)
		{				
		}
	}
}*/

//LinYG,2007-08-18,修改,增加显示版本号
//w91221,2008-04-16,修改,modified to support clear rx buff

char Info[] = {
    "    串口驱动库\n"
    " \n"
    " 版本号：V2.00\n"   // 修改后，请一定修改版本号。

    " 本程序最后编译时间：\n"               // 请勿在此后增加信息!
    "                                \n"    // 请保留此行(分配内存用)
};

//*****************************************************************
// 函数名称：DLLInfo();
// 功能描述：将信息包 Info 输出，以作版本信息等标志。
// 输入参数：Info--版本信息数组
// 输出参数：
// 返回：    版本信息数组
// 其他：
//*****************************************************************
char* DLLInfo( )
{
    int nStrLen = strlen( Info );
    sprintf( Info+nStrLen-30, "%s ", __DATE__ );
    strcat( Info, __TIME__ );
    strcat( Info, " \n" );

    return Info;
}
//end by LinYG


/*==========================================================================*
 * FUNCTION : SplitText
 * PURPOSE  : split text S with separator cSep(,), and save the splitted text
 *            to D.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char   *S   : the source string to be splitted
 *            OUT char  *D   : to save the splitted string
 *            IN int   cSep : the separator
 * RETURN   : static char *: the string pointer is not splitted.
 * COMMENTS : 
 * CREATOR  : Mao Fuhua                DATE: 2004-09-16 20:19
 *==========================================================================*/
static char *SplitText(IN char *S, OUT char *D, IN int cSep)
{
	int i = 0;
	
    // 1. get rid of the front space
    while(*S == SPACE)
	{
        S ++;
	}

    // 2. copy the non-space char
	while( *S && *S != (char)cSep)
	{
		D[i ++] = *S ++;
	}

    if (*S == (char)cSep)    // skip COMMA_SEPARATOR
	{
        S ++;
	}

    // 3. get rid of the rear space
    i --;
    while((i >= 0) && (D[i] == SPACE))
	{
        i --;
	}

    i ++;

    D[i]=0;

	return S;
}


/*==========================================================================*
 * FUNCTION : Serial_ParseOpenParams
 * PURPOSE  : Parse the baud rate, data bits and other attributes of the serial
 *            from opening parameters
 *            open comm with b,p,d,s format, for dialling port is b,p,d,s:phone
 *            b=baud, e.g., 9600,
 *            p=parity, n, o, e is allowed.
 *            d=data bits, 5-8 is valid
 *            s=stop bits, 1-2 is valid
 *            for phone number, the '-','+',' ' are allowed in the phone number,
 *            but they will be ignored.
 * CALLS    : 
 * CALLED BY: Serial_CommOpen
 * ARGUMENTS: IN char               *pSettings : 
 *            OUT SERIAL_BAUD_ATTR  *pAttr       : 
 * RETURN   : static BOOL : TRUE for success, FALSE for error params
 * COMMENTS : 
 * CREATOR  : Mao Fuhua                DATE: 2004-09-16 20:14
 *==========================================================================*/
static BOOL Serial_ParseOpenParams(IN char	*pSettings, 
								   OUT SERIAL_BAUD_ATTR *pAttr)
{
#ifdef _IMPL_DIAL_SERIAL
	int		i;
#endif //_IMPL_DIAL_SERIAL

	// the settings like: 1000000,n,8,1:00867559876543211234567
#define MAX_SETTING_LEN		64	// 64 is enough!!
    char p[MAX_SETTING_LEN], *s = pSettings;
	
    if ((pSettings == NULL) || (*pSettings == 0) 
		|| strlen(pSettings) >= MAX_SETTING_LEN)	// too long.
    {
        return FALSE;
    }

    // get the baud rate set
    s = SplitText(s, p, COMMA_SEPARATOR);

    pAttr->nBaud = atoi(p);

	pAttr->ulUnixBaud = ConvertBaudrateToUnix(pAttr->nBaud);

    if (pAttr->ulUnixBaud == 0)
    {
#ifdef _DEBUG_SERIAL_PORT
        TRACE("[Serial_ParseOpenParams] -- System does not support"
			" the baud rate %d.", pAttr->nBaud);
#endif 	/*_DEBUG	*/
        return FALSE;
    }

    // odd or even check
    s = SplitText(s, p, COMMA_SEPARATOR);
    if (p[1] != 0) // too long
    {
        return FALSE;
    }

    if ((p[0] == 'N') || (p[0] == 'n'))
	{
        pAttr->nOdd = 0;
	}
    else if ((p[0] == 'O') || (p[0] == 'o'))
	{
        pAttr->nOdd = 1;
	}
    else if ((p[0] == 'E') || (p[0] == 'e'))
	{
        pAttr->nOdd = 2;
	}
	//-- add by ylf 2006.08.02 --
	// add parity: s (space)、m (mask)

    else if ((p[0] == 'S') || (p[0] == 's'))
	{// space

        pAttr->nOdd = 3;
	}
    else if ((p[0] == 'M') || (p[0] == 'm'))
	{// mask

        pAttr->nOdd = 4;
	}
	//-- end add by ylf 2006.08.02 --
    else
	{
        return FALSE;
	}
    
    // data bit - only one byte 5-8
    s = SplitText(s, p, COMMA_SEPARATOR);
    if ((p[0] < '5') || (p[0] > '8') || (p[1] != 0))
    {
        TRACE("[Serial_ParseOpenParams] -- error on parsing data bit.\n");
        return FALSE;
    }

    pAttr->nData = p[0] - '0';

    // stop bit
#ifdef _IMPL_DIAL_SERIAL
    s = SplitText(s, p, COLON_SERARATOR);
#else
    s = SplitText(s, p, COMMA_SEPARATOR);
#endif
    if (((p[0] == '1') || (p[0] == '2')) && (p[1] == 0))
    {
        pAttr->nStop = p[0] - '0';  // 1 or 2 stop bits
    }
    // 1.5 stop bits
    else if ((p[0] == '1') && (p[1] == '.') && (p[2] == '5') && (p[3] == 0))
    {
        pAttr->nStop = 3;      // 1.5 stop bits
    }
    else
    {
        TRACE("[Serial_ParseOpenParams] -- error on parsing stop bit.\n");
        return FALSE;
    }

#ifdef _IMPL_DIAL_SERIAL
	// to parse the dialling phone number, the left s is phone number
    SplitText(s, p, COLON_SERARATOR);
	s = p;	// use s temporarily.
	for (i = 0; (i < (int)(sizeof(pAttr->szPhone)-1)) && (*s != 0); s++)
	{
		// copy the digital number of the phone. ',' is a delay flag
		if ((('0' <= *s) && (*s <= '9'))
			|| (*s == ',') || (*s == 'W') || (*s == 'w'))
		{
			pAttr->szPhone[i++] = *s;
		}

		// ignore the '+', '-' and ' ' in the phone number string.
		// if other character is included, return error.
		else if ((*s != '+') && (*s != '-') && (*s != ' '))
		{
	        TRACE("[Serial_ParseOpenParams] -- error on parsing "
				"phone number: %s.\n", s);
			return FALSE;
		}
	}

	pAttr->szPhone[i] = 0;	// append an end flag	
#endif //_IMPL_DIAL_SERIAL

#ifdef _DEBUG_SERIAL_PORT
#ifdef _IMPL_DIAL_SERIAL
	TRACE("[Serial_ParseOpenParams] -- Parse serail settings and"
		" got %d,%d,%d,%d:%s\n",
		pAttr->nBaud, pAttr->nOdd, pAttr->nData, pAttr->nStop, 
		pAttr->szPhone);
#else
	TRACE("[Serial_ParseOpenParams] -- Parse serail settings and"
		" got %d,%d,%d,%d\n",
		pAttr->nBaud, pAttr->nOdd, pAttr->nData, pAttr->nStop);
#endif //_IMPL_DIAL_SERIAL
#endif //_DEBUG_SERIAL_PORT

	return TRUE;
}



#define TTYS_ONLY_NAME			"ttyS"
#define TTYS_FULL_PREFIX		"/dev/ttyS"
#define TTYS_ALIAS_NAME			"COM"
#define CONST_STRLEN(const_str)		(sizeof(const_str)-1)

/*==========================================================================*
 * FUNCTION : Serial_GetDescriptor
 * PURPOSE  : Convert the non-standard serial device name such as ttyS0, ttyS1,
 *            to /dev/ttyS0, /dev/ttyS1..., and also convert COM1, COM2, ..., to
 *            /dev/ttyS0, /dev/ttyS1, and also convert "1", "2" to /dev/ttyS0, 
 *            /dev/ttyS1....
 * CALLS    : 
 * CALLED BY: Serial_CommOpen
 * ARGUMENTS: IN char   *pPortDescriptor : 
 *            OUT char  *pStdDescriptor  : 
 * RETURN   : static int : the actual serial port start from 0, -1 for error. 
 * COMMENTS : 
 * CREATOR  : Mao Fuhua                DATE: 2004-09-16 17:26
 *==========================================================================*/
static int Serial_GetDescriptor(IN char *pPortDescriptor,
				 OUT char *pStdDescriptor)
{
	char	*pComNum = NULL;
	int	nComNumStart, nComNum;

	// 1. Test if the name is full name or not. 
	//    this comparison is case sensitive
	if (strncmp(pPortDescriptor, TTYS_FULL_PREFIX,
		CONST_STRLEN(TTYS_FULL_PREFIX)) == 0)
	{
		// the full name is used.
		pComNum = pPortDescriptor + CONST_STRLEN(TTYS_FULL_PREFIX);
		nComNumStart = 0;	// /dev/ttySn, the n starts from 0.
	}

	// 2. to test if only the name, the /dev/ does not exist. 
	//    this comparison is case sensitive
	else if (strncmp(pPortDescriptor, TTYS_ONLY_NAME,
		CONST_STRLEN(TTYS_ONLY_NAME)) == 0)
	{
		// only the name is used.
		pComNum = pPortDescriptor + CONST_STRLEN(TTYS_ONLY_NAME);
		nComNumStart = 0;	// ttySn, the n starts from 0.
	}

	// 3. to the alias name COMx is used or not.
	//    this comparison is NOT case sensitive
	else if (strnicmp(pPortDescriptor, TTYS_ALIAS_NAME,
		CONST_STRLEN(TTYS_ALIAS_NAME)) == 0)
	{
		// the alias name is used.
		pComNum = pPortDescriptor + CONST_STRLEN(TTYS_ALIAS_NAME);
		nComNumStart = 1;	// COMn, the n starts from 1.
	}

	// 4. we consider only the port num such as 1, 2,... is passed in.
	else 
	{
		pComNum = pPortDescriptor;
		nComNumStart = 1;	// only the com num 'n', the n starts from 1.
	}

	// 5. to test the comm number is correct or not.
	//    the comm num shall be a digital string.
	//    ACU only support 0-9 serial port at tatol
	//nComNum = (pComNum[0]-'0') - nComNumStart;	// the actual serial port num

	/*if ((nComNum < 0) || (pComNum[0] < '0') || (pComNum[0] > '9')|| (pComNum[1] != '\0'))
	{
		TRACE("[Serial_GetDescriptor] -- The port descriptor %s is invalid.\n",
			pPortDescriptor);

		return -1;
	}*/
	
	//modified by wankun , idu can identity the dev number from 0~50
	if(sscanf(pComNum,"%d",&nComNum)!=1) printf("read desicriptor is error!\n");

       //-- changed by ylf 2006.07.22 --
       // the COM start No changed from 0 to 1
	//nComNum = nComNum - nComNumStart;	
	//if ((nComNum < 0) || (nComNum > 49))

        nComNum = nComNum - nComNumStart + 1;
        if ((nComNum < 1) || (nComNum > 50))
        //-- end changed by ylf 2006.07.22 --
	{
		printf("[Serial_GetDescriptor] -- The port descriptor %s is invalid.\n",
			pPortDescriptor);

		return -1;
	}
    // 6. merge the full port name. The buffer is big enough.
	sprintf(pStdDescriptor, "%s%d",
		TTYS_FULL_PREFIX,	// /dev/ttyS
		nComNum);			// actual comm num from 0


#ifdef _DEBUG_SERIAL_PORT
	TRACE("[Serial_GetDescriptor] -- convert %s got %s\n",
		pPortDescriptor, pStdDescriptor);
#endif //_DEBUG_SERIAL_PORT

	
	return nComNum;
}


#ifndef _IMPL_ACU_485	// for standard serial port

/*==========================================================================*
 * FUNCTION : cfsetdatabits
 * PURPOSE  : set the data bits
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: struct termios  *options  : 
 *            int             nDataBits : 
 * RETURN   : static int : 
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-17 11:09
 *==========================================================================*/
static int cfsetdatabits(struct termios *options, int nDataBits)
{
    int n;
    int nUnixDataBits[] = { CS5, CS6, CS7, CS8 };

    n = ((nDataBits >= 5) && (nDataBits <= 8)) ? nUnixDataBits[nDataBits-5] : CS8;
    
    options->c_cflag &= ~CSIZE;
    options->c_cflag |= n;

    return n;
}


/*==========================================================================*
 * FUNCTION : cfsetparity
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: struct termios  *options : 
 *            int             nParity  : 
 * RETURN   : static int : 
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-17 11:10
 *==========================================================================*/
static int cfsetparity(struct termios *options, int nParity)
{
    options->c_cflag &= ~PARENB; /* default, no parity */

    if (nParity == 1) // odd
    {
        options->c_cflag |= PARENB;
        options->c_cflag |= PARODD;

		//-- add by ylf 2006.09.16 --
		//目的：屏蔽s和m校验位的影响
		options->c_cflag &= ~CMSPAR;
		//-- end add by ylf 2006.09.16 --
    }

    else if (nParity == 2) // even
    {
        options->c_cflag |= PARENB;
        options->c_cflag &= ~PARODD;

		//-- add by ylf 2006.09.16 --
		//目的：屏蔽s和m校验位的影响
		options->c_cflag &= ~CMSPAR;
		//-- end add by ylf 2006.09.16 --
   }

	//-- add by ylf 2006.08.02 --
	// add parity: s (space)、m (mask)
	
    else if (nParity == 3)
	{// space
		
		options->c_cflag &= ~PARODD;
		options->c_cflag |= PARENB|CMSPAR;
	}
    else if (nParity == 4)
	{// mask
		
		options->c_cflag |= PARENB | CMSPAR;
		options->c_cflag |= PARODD;
	}
	//-- end add by ylf 2006.08.02 --

	//-- changed by ylf 2007.05.15 --
	// 目的：支持 M 和 S 校验位
	/*
	if (nParity != 0)	// enable parity, 2004-10-8
	{
		options->c_iflag |= INPCK;
	}
	else	// disable
	{
		options->c_iflag &= ~INPCK;
	}
	//*/

	if (nParity == 0)	// enable parity, 2004-10-8
	{
		options->c_iflag &= ~INPCK;
	}
	else if ((nParity == 1)	|| (nParity == 2))
	{
		options->c_iflag |= INPCK;
	}
	/*
	else if ((nParity == 3)	|| (nParity == 4))
	{
		options->c_iflag  = 0;                // 直接设置为0最方便；否则以前的设置会影响接收
	}
	//*/
	//-- end changed by ylf 2007.05.15 --

    return nParity;
}


/*==========================================================================*
 * FUNCTION : cfsetstopbits
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: struct termios  *options : 
 *            int             nStop    : 
 * RETURN   : static int : 
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-17 11:10
 *==========================================================================*/
static int cfsetstopbits(struct termios *options, int nStop)
{
    if (nStop == 1)
        options->c_cflag &= ~CSTOPB;    // default is 1 stop bits
    else if (nStop == 2)
    {
        options->c_cflag |= CSTOPB;
    }
    
    return nStop;
}


/*==========================================================================*
 * FUNCTION : Serial_SetCommAttributes
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * Arguments: IN int fd	: the opened serial port descriptor
 *            IN SERIAL_BAUD_ATTR  *pAttr : 
 * RETURN   : BOOL : 
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-17 11:12
 *==========================================================================*/
BOOL Serial_SetCommAttributes(IN int fd, IN SERIAL_BAUD_ATTR *pAttr)
{
    struct termios options;
	
#if 0
    struct stat statbuf;  //
// if (ioctl(fd,0xbfc5,0)!= 0)
//   {
 //   printf("enter tcsetattr\n");

    if(fstat(fd, &statbuf) < 0)      //
    	printf("fstat error\n");
    //int devmaj = MAJOR(statbuf->st_ino. i_rdev);//fd->f_dentry.d_inode);//
    //printf("major is %d\n",devmaj);

    //char *pfn = statbuf.st_ino.i_dentry.d_name;
    dev_t dev_fd = statbuf.st_dev;
    int devmaj = 0;
    devmaj = MINOR(dev_fd);
    if(devmaj!=0)
    	printf("minor is %d\n",devmaj);

#endif	

//-- added by zzw 2007.03.14 to support idu-com4 --
	if(ioctl(fd,0xffff,0)!= 0)  // Is com4?
		
    /* Get the current options for the port */
    {
//-- end added by zzw 2007.03.14 to support idu-com4 --    
    tcgetattr(fd, &options);

    cfsetispeed(&options, pAttr->ulUnixBaud);	// Set the input baud rate
    cfsetospeed(&options, pAttr->ulUnixBaud);	// set the output baud rate

    /* Enable the receiver and set local mode */
    options.c_cflag |= (CLOCAL | CREAD);

    // data bits
	cfsetdatabits(&options, pAttr->nData);

    // stop bits
	cfsetstopbits(&options, pAttr->nStop);

    // parity
	cfsetparity(&options, pAttr->nOdd);

    options.c_cflag &= ~CRTSCTS;	/* Disable hardware flow control */  

    /* Enable data to be processed as raw input */
    options.c_lflag &= ~(ICANON | ECHO | ISIG);

	//add for the test result of YANGYUNLIANG. 
	//for can not send out the 0x11,0xD,0xA,0x13 chars.
	options.c_iflag  = 0;
	options.c_oflag &= ~OPOST;   /*Output*/	
	// end add for the test result of YANGYUNLIANG

    /* Set the new options for the port */
    if (tcsetattr(fd, TCSANOW, &options) != 0)
    {

        TRACE("[Serial_SetCommAttributes] -- set baud rate fail on %d.\n\r", errno);
        return FALSE;
    	 
    }  
     return TRUE;
    }

//-- add by zzw 2007.03.14 to support idu-com4 --	
    else
    	{
	//printf("nodd = %d\n",pAttr->nOdd);
       controlword = 0;
	
    	switch(pAttr->nBaud)
    	{
		
    		case 300:
    		           controlword  |=  ST_B300;
    		            break;
    		case 600:
    		            controlword  |=  ST_B600;
    		            break;
    		case 1200:
    		            controlword  |=  ST_B1200;
    		            break;
    		case 2400:
    		           controlword  |=  ST_B2400;
    		            break;		
    		case 4800:
    		           controlword  |=  ST_B4800;
    		            break;
    		case 9600:
    		            controlword  |=  ST_B9600;
    		            break;
    		case 19200:
    		            controlword  |=  ST_B19200;
    		            break;
    		case 38400:
    		           controlword  |=  ST_B38400;
    		            break;
    		            
    		default : 
    		             controlword  |=  ST_B9600;
    		            break;   
    	}

    	switch(pAttr->nOdd)
    	{ 
    		case 0:

    		           controlword  |=  ST_PARNONE;
    		            break;
    		case 1:   
	
    		            controlword  |=  ST_PARODD;
    		            break;
    		case 2:   
				
    		            controlword  |=  ST_PAREVEN;
    		            break;
    		case 3:   
			
    		            controlword  |=  ST_PARSPACE;
    		            break;
    		case 4:   
			
    		            controlword  |=  ST_PARMASK;
    		            break;
    		default :
    		             controlword  |=  ST_PARNONE;
    		            break;   
						
    	}

    	switch(pAttr->nStop)
    	{
    		
    		case 1: 
    		           controlword  |=  ST_STOP1;
    		            break;
    		case 2: 
    		            controlword  |=  ST_STOP2;
    		            break;
    		case 3:
    		            controlword  |=  ST_STOP15;
    		            break;
       		default :
    		             controlword  |=  ST_STOP1;
    		            break;     		
    	}

    	switch(pAttr->nData)
    	{

    		case 5:
    		           controlword  |=  ST_CS5;
    		            break;
    		
    		case 6:
    		           controlword  |=  ST_CS6;
    		            break;
    		case 7:
    		            controlword  |=  ST_CS7;
    		            break;
    		case 8: 
    		            controlword  |=  ST_CS8;
    		            break;
       		default :
    		             controlword  |=  ST_CS8;
    		            break;   
    		
    	}		
		
    	ioctl(fd,controlword,0);
		return TRUE;
    	}
   //-- add by zzw 2007.03.14 to support idu-com4 -- 
}



#else	// for the ACU 485 serial port

extern	BOOL ACU485_SetCommAttributes(IN int fd, IN SERIAL_BAUD_ATTR *pAttr);
#define Serial_SetCommAttributes	ACU485_SetCommAttributes

#endif	// _IMPL_ACU_485


/*==========================================================================*
 * FUNCTION : Serial_OpenPort
 * PURPOSE  : To open the serial port(pszComName), and set its 
 *            attributes(buad rate, data bits,and so on)
 * CALLS    : 
 * CALLED BY: Serial_CommOpen
 * ARGUMENTS: IN char              *pszComName : 
 *            IN SERIAL_BAUD_ATTR  *pAttr             : 
 * RETURN   : static int : the handle of the opened port
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-17 10:55
 *==========================================================================*/
static int Serial_OpenPort(IN char *pszComName,
			   IN SERIAL_BAUD_ATTR *pAttr)
{
    int	fd;	// the file descriptor

    fd = open(pszComName, O_RDWR | O_NOCTTY | O_NONBLOCK | O_EXCL); 
    if (fd < 0)
    {
#ifdef _DEBUG
	perror("[Serial_OpenPort] -- open serial");
        TRACE("[Serial_OpenPort] -- open %s fail, got error %d, %s.\n\r", 
            pszComName, errno, strerror(errno));
#endif 	/*_DEBUG	*/
	return -1;
    }
    if(ioctl(fd,0xffff,0)!= 0)
    {
	// lock the com to avoid other process open again
#ifndef MGRID_ANDROID
	if( flock( fd, LOCK_EX|LOCK_NB ) < 0 )
	{
#ifdef _DEBUG
		TRACE("[Serial_OpenPort] -- try to lock %s fail, got %d, %s.\n",
			pszComName, errno, strerror(errno));
#endif 	/*_DEBUG	*/

		close( fd );
		return -1;
	}
#endif /* MGRID_ANDROID */

#ifdef _DEBUG
	TRACE("[Serial_OpenPort] ------- open&lock [%s] -------------\n", pszComName);
#endif 	/*_DEBUG	*/

    }
    if (!Serial_SetCommAttributes(fd, pAttr))
    {
#ifdef _DEBUG
	      perror("[Serial_OpenPort] -- set serial");
	      TRACE("[Serial_OpenPort] ------- set [%s] failed! -------------\n", 
            pszComName);
#endif 	/*_DEBUG	*/
        close(fd);
        return -1;
    }


#ifdef _IMPL_DIAL_SERIAL
#ifdef _HAS_ACU_SCC
	pAttr->fdSCCDTR = open("/dev/sccdtr", O_RDWR); /*O_WRONLY*/
	if (pAttr->fdSCCDTR <= 0)
	{
#ifdef _DEBUG_MODEM_PORT
		TRACEX("failed to open /dev/sccdtr! \n");
#endif
		close(fd);
        return -1;
	}
#endif
#endif

    return fd;
}

/*==========================================================================*
 * FUNCTION : Serial_CommOpen
 * PURPOSE  : To open a standard serial communication port.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char   *pPortDescriptor : The serial descriptor in Linux, 
 *				       /dev/ttyS0, /dev/ttyS1,...
 *            IN char   *pOpenParams     : the port settings as format:
 *					   "buad,parity,data,stop", 
 *                                         such as "9600,n,8,1". for detail 
 *                                         please refer to the function
 *                                         Serial_ParseOpenParams
 *            IN DWORD  dwPortAttr       : B00=COMM_SERVER_MODE for server port
 *                                         B00=COMM_CLIENT_MODE for client port
 *            IN int    nTimeout         : Open timeout in ms
 *            OUT int   *pErrCode        : prt to save error code.
 * RETURN   : HANDLE : 
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-11 17:55
 *==========================================================================*/
HANDLE Serial_CommOpen(
	IN char		*pPortDescriptor,
	IN char		*pOpenParams,
	IN DWORD	dwPortAttr,
	IN int		nTimeout,
	OUT int		*pErrCode)
{
	SERIAL_PORT_DRV *pPort = NEW(SERIAL_PORT_DRV, 1);
	
	// full port device name, like: /dev/ttyS0
	char	szFullDescriptor[sizeof(TTYS_FULL_PREFIX)+2];
	
	/* 1. get mem */
	if (pPort == NULL)
	{
		*pErrCode = ERR_COMM_NO_MEMORY;
		return	NULL;
	}

	memset(pPort, 0, sizeof(SERIAL_PORT_DRV));

	/* 2. parse descriptor */
	pPort->nSerialPort = Serial_GetDescriptor(pPortDescriptor, 
		szFullDescriptor);
	if (pPort->nSerialPort < 0)
	{
#ifdef _DEBUG_SERIAL_PORT
		TRACE("[Serial_CommOpen] -- invalid port descriptor: \"%s\".\n",
			pPortDescriptor);
#endif //_DEBUG_SERIAL_PORT

		DELETE(pPort);
		*pErrCode = ERR_COMM_OPENING_PARAM;

		return	NULL;
	}

	/* 2.1. parse comm settings */
	if (!Serial_ParseOpenParams(pOpenParams, &pPort->attr))
	{
#ifdef _DEBUG_SERIAL_PORT
		TRACE("[Serial_CommOpen] -- invalid parameters: \"%s\".\n",
			pOpenParams);
#endif //_DEBUG_SERIAL_PORT

		DELETE(pPort);
		*pErrCode = ERR_COMM_OPENING_PARAM;

		return	NULL;
	}

	INIT_TIMEOUTS(pPort->toTimeouts, nTimeout, nTimeout);
	
	pPort->pCurClients = NEW(int, 1);	
	if (pPort->pCurClients == NULL)
	{
		DELETE(pPort);
		*pErrCode = ERR_COMM_NO_MEMORY;

		return NULL;
	}

	*pPort->pCurClients = 0;	/*	no linkages now		*/

	// open the serial port.
	pPort->fdSerial = Serial_OpenPort(szFullDescriptor, &pPort->attr);
	if (pPort->fdSerial < 0)
	{
#ifdef _DEBUG_SERIAL_PORT
		TRACE("[Serial_CommOpen] -- Fails on opening port \"%s\" with %s.\n",
			szFullDescriptor, pOpenParams);
#endif //_DEBUG_SERIAL_PORT

		DELETE(pPort->pCurClients);	
		DELETE(pPort);
		*pErrCode = ERR_COMM_OPENING_PORT;

		return	NULL;
	}
	if (ioctl(pPort->fdSerial,0xffff,0)== 0)
		ioctl(pPort->fdSerial,0xff00,pPort->toTimeouts.nReadTimeout);  //-- add by zzw 2007.06.07
	
	/* to connect to a remote server, create a client port	*/
	if ((dwPortAttr & COMM_CLIENT_MODE) == COMM_CLIENT_MODE)
	{
		pPort->nWorkMode = COMM_OUTGOING_CLIENT;

#ifdef _IMPL_DIAL_SERIAL
		// to dial the remote dialling server		
		if (!Modem_Dial((HANDLE)pPort, pPort->attr.szPhone))
		{
			TRACE("[Serial_CommOpen] -- Fails on dialling number %s.\n",
				pPort->attr.szPhone);

			close(pPort->fdSerial);
#ifdef _IMPL_DIAL_SERIAL
#ifdef _HAS_ACU_SCC
			close(pPort->attr.fdSCCDTR);
#endif
#endif
			*pErrCode = pPort->nLastErrorCode;

			DELETE(pPort->pCurClients);	
			DELETE(pPort);

			return NULL;
		}

		// set the peer phone info
		strncpyz(pPort->szPhone, pPort->attr.szPhone, sizeof(pPort->szPhone));
		pPort->bCommunicating = TRUE;
#endif //_IMPL_DIAL_SERIAL
	}

	/* to create a server port	*/
	else
	{
		pPort->nWorkMode = COMM_LOCAL_SERVER;
		pPort->nMaxClients = SERIAL_MAX_CLIENTS_DEFAULT;
	}

	*pErrCode = ERR_COMM_OK;

	return (HANDLE)pPort;
}


#ifdef _IMPL_DIAL_SERIAL	// for dialling serial
#define CLIENT_IS_READY(pPort)		Modem_CheckConnection((HANDLE)pPort)
#else						// for direct serial
#define CLIENT_IS_READY(pPort)		1
#endif //_IMPL_DIAL_SERIAL


/* The "CommAccept" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Serial_CommAccept
 * PURPOSE  : To accept a client from serial port with server type.
 *            Actually, the serial is no server or client mode,
 *            it always works as end-end mode. the accept function is purely
 *            to keep the accordance with the work mode of the TCP/IP port.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : The server port
 * RETURN   : HANDLE : non-zero for successful, NULL for failure
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-13 20:43
 *==========================================================================*/

HANDLE Serial_CommAccept(IN HANDLE hPort)
{
	SERIAL_PORT_DRV *pPort   = (SERIAL_PORT_DRV *)hPort;
	SERIAL_PORT_DRV *pClient = NULL;

	// clear last error 
	pPort->nLastErrorCode = ERR_COMM_OK;

	Serial_SetCommAttributes(pPort->fdSerial, &pPort->attr);

	// check the socket whether a server port or not,
 	// only server port is allowed to call accept
	
	if (pPort->nWorkMode != COMM_LOCAL_SERVER)
	{
		pPort->nLastErrorCode = ERR_COMM_SUPPORT_ACCEPT;
		return NULL;
	}

	// check the current client num exceeds the maximum connections or not 
	if (*pPort->pCurClients >= pPort->nMaxClients)
	{
		pPort->nLastErrorCode = ERR_COMM_MANY_CLIENTS;
		return NULL;
	}

	// For serial, the accept calling always return the handle directly	
	if (CLIENT_IS_READY(pPort))
	{
		// Ok, create a client object 
		pClient = NEW(SERIAL_PORT_DRV,1);
		if (pClient == NULL)	// out of memory error	
		{
			pPort->nLastErrorCode = ERR_COMM_NO_MEMORY;
			return NULL;
		}

		*pClient			= *pPort;	// Copy all info from server port	
		pClient->nWorkMode	= COMM_INCOMING_CLIENT;
		(*pPort->pCurClients)++;		// increase the current linkage		

#ifdef _IMPL_DIAL_SERIAL
		pClient->bCommunicating = TRUE;
#endif
	}
#ifdef _IMPL_DIAL_SERIAL
	else
	{
		pPort->nLastErrorCode = ERR_COMM_TIMEOUT;
		return NULL;
	}
#endif

	return (HANDLE)pClient;
}


#ifndef _IMPL_ACU_485	// for standard serial port

/* The "CommRead" proc for comm driver */
/*==========================================================================*
 * FUNCTION : CommRead
 * PURPOSE  : Read data from a opened serial port
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
int Serial_CommRead(IN HANDLE hPort, OUT char *pBuffer, IN int nBytesToRead)
{
	SERIAL_PORT_DRV *pPort     = (SERIAL_PORT_DRV *)hPort;
	int				fd		   = pPort->fdSerial;
	int				nBytesRead = 0;
	int			    nTotalBytesRead = 0;	// total bytes of data being read
	int				nRetryTimes = 0;
	int				rc;

	/* clear last error */
	//printf("commserisal nBytestoRead = %d\n",nBytesToRead);
    pPort->nLastErrorCode = ERR_COMM_OK;
//-- add by zzw 2007.03.14 to support idu-com4 --
    int judge = ioctl(fd,0xffff,0);    // Does this port belong com4?
    if(judge)
    {
//-- end add by zzw 2007.03.14 to support idu-com4 --	    
	rc = WaitFiledReadable(fd, pPort->toTimeouts.nReadTimeout);

#ifdef _IMPL_DIAL_SERIAL
	// alway test the modem connection status, due to sometimes the
	// CD is lost, but the rc is > 0. maofuhua, 2005-2-24
	if (pPort->bCommunicating && // this is client.
		(Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) != 1))
	{
#ifdef _DEBUG_SERIAL_PORT
		TRACEX("Modem Line is broken.\n");
#endif //_DEBUG_SERIAL_PORT
		
		pPort->nLastErrorCode =	ERR_COMM_CONNECTION_BROKEN;
		return 0;
	}
#endif //_IMPL_DIAL_SERIAL

	if( rc <= 0 )
	{
		/* 0: wait time out, -1: fd error on waiting    */
		pPort->nLastErrorCode = 
			(rc == 0) ? ERR_COMM_TIMEOUT  : ERR_COMM_READ_DATA;

#ifdef _DEBUG_SERIAL_PORT
			TRACE("[Serial_CommRead] -- Read data error(%x)\n",
				pPort->nLastErrorCode);
#endif //_DEBUG_SERIAL_PORT
		//printf("read return2\n");
		return 0;	// nothing has been read
	}    	
	
    while (nBytesToRead > 0)
    {
    		
		nBytesRead = read(fd, pBuffer, (size_t)nBytesToRead); //

		//printf("commserisal nBytesRead = %d\n",nBytesRead);
		
		if (nBytesRead > 0)	/* we got some data */
        {
            // ok
            nBytesToRead	-= nBytesRead;
            pBuffer			+= nBytesRead;
			nTotalBytesRead += nBytesRead;

			if (nBytesToRead == 0)
			{
				
				break;	// reading finished
			}

			// Need clear retry times? We don't clear it!
			nRetryTimes = 0;
		}
				
		else if ((nBytesRead < 0) && (errno != EAGAIN))		// read error?
		{
			printf("nBytesRead < 0\n");
			nRetryTimes++;		// let's retry again
			if (nRetryTimes >= COM_RETRY_TIME)
			{
				pPort->nLastErrorCode = ERR_COMM_READ_DATA;

#ifdef _DEBUG_SERIAL_PORT
				TRACE("[Serial_CommRead] -- Read data error(%d), "
					"has retried %d times.\n", errno, nRetryTimes);
#endif //_DEBUG_SERIAL_PORT
				
					break;	// error, quit now.
			}
		}
		// nothing received, data not ready? wait a while
		// if error, we also sleep a while and retry again.
		rc = WaitFiledReadable(fd, pPort->toTimeouts.nIntervalTimeout);
		if( rc <= 0 )
		{
			/* 0: wait time out, -1: fd error on waiting    */
			pPort->nLastErrorCode = (rc == 0) ? ERR_COMM_TIMEOUT
				: ERR_COMM_READ_DATA;			
			break;
		}
    	} 
	return nTotalBytesRead;
   }

//-- add by zzw 2007.03.14 to support idu-com4 --
	else
		{		   
		   nBytesRead = read(fd, pBuffer, (size_t)nBytesToRead);
		   //printf("commserisal nBytesRead = %d\n",nBytesRead);
		   return nBytesRead;
		}	
//-- end added by zzw 2007.03.14 to support idu-com4 --
    
}

#endif


/* The "CommWrite" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Serial_CommWrite
 * PURPOSE  : write a buffer to a port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort         : 
 *            IN char    *pBuffer      : 
 *            IN int     nBytesToWrite : 
 * RETURN   : int : < 0 for error. <nBytesToWrite timeout, = nBytesToWrite ok
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-13 21:36
 *==========================================================================*/
int Serial_CommWrite(IN HANDLE hPort, IN char *pBuffer,	IN int nBytesToWrite)
{
	SERIAL_PORT_DRV *pPort     = (SERIAL_PORT_DRV *)hPort;
	int				fd		   = pPort->fdSerial;
	int				nBytesWritten = 0;
	int			    nTotalBytesWritten = 0;	// total bytes of data being read
	int				nRetryTimes = 0;
	int				rc;

	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;

	//printf("commseril nBytesToWrite = %n",nBytesToWrite);
	int judge = ioctl(fd,0xffff,0);
    if(judge)
    {
	rc = WaitFiledWritable(fd, pPort->toTimeouts.nReadTimeout);
    	
#ifdef _IMPL_DIAL_SERIAL
	// alway test the modem connection status, due to sometimes the
	// CD is lost, but the rc is > 0. maofuhua, 2005-2-24
	if (pPort->bCommunicating && // this is client.
		(Modem_Ioctl(hPort, MODEM_GET_STATUS, TIOCM_CD) != 1))
	{
#ifdef _DEBUG_SERIAL_PORT
		TRACEX("Modem Line is broken.\n");
#endif //_DEBUG_SERIAL_PORT

		pPort->nLastErrorCode =	ERR_COMM_CONNECTION_BROKEN;
		return 0;
	}
#endif //_IMPL_DIAL_SERIAL
	
	if( rc <= 0 )
	{
		/* 0: wait time out, -1: fd error on waiting    */
		pPort->nLastErrorCode = 
			(rc == 0) ? ERR_COMM_TIMEOUT  : ERR_COMM_WRITE_DATA;

#ifdef _DEBUG_SERIAL_PORT
			TRACE("[Serial_CommWrite] -- Write data error(%x)\n",
				pPort->nLastErrorCode);
#endif //_DEBUG_SERIAL_PORT

		return 0;	// nothing has been read
	}
    }
    while (nBytesToWrite > 0)
    {

        nBytesWritten = write(fd, pBuffer, (size_t)nBytesToWrite);//
        //printf("commseril nBytesWritten = %n",nBytesWritten);
        if (nBytesWritten > 0)	/* we got some data */
        {
            // ok
            nBytesToWrite	   -= nBytesWritten;
            pBuffer			   += nBytesWritten;
			nTotalBytesWritten += nBytesWritten;

			if (nBytesToWrite == 0)
			{
				break;	// writing finished
			}

			// Need clear retry times? We don't clear it!
			// nRetryTimes = 0;
		}

		else if ((nBytesWritten < 0) && (errno != EAGAIN))		// read error?
		{
			nRetryTimes++;		// let's retry again
			if (nRetryTimes >= COM_RETRY_TIME)
			{
				pPort->nLastErrorCode = ERR_COMM_WRITE_DATA;

#ifdef _DEBUG_SERIAL_PORT
				TRACE("[Serial_CommWrite] -- Write data error(%d), "
					"has retried %d times.\n", errno, nRetryTimes);
#endif //_DEBUG_SERIAL_PORT

				break;	// error, quit now.
			}
		}
    if(judge!= 0)
    {
		// nothing received, data not ready? wait a while
		// if error, we also sleep a while and retry again.
		rc = WaitFiledWritable(fd, pPort->toTimeouts.nIntervalTimeout);
		if( rc <= 0 )
		{
			/* 0: wait time out, -1: fd error on waiting    */
			pPort->nLastErrorCode = (rc == 0) ? ERR_COMM_TIMEOUT
				: ERR_COMM_READ_DATA;
			break;
		}
		
    }
/*	else
		{
		 	int tdel = pPort->toTimeouts.nIntervalTimeout;
			tdel *= 100;
			//printf("tdel = %d\n",tdel);
			Sleep(tdel);
			//mdelay();
		}*/
    }

    return nTotalBytesWritten;
}


/* The "CommControl" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Serial_CommControl
 * PURPOSE  : To control a opened port with command nCmd.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE    hPort       : 
 *            IN int       nCmd        : 
 *            IN OUT void  *pBuffer    : 
 *            IN int       nDataLength : 
 * RETURN   : int :ERR_OK for OK, else is error code
 * COMMENTS : 
 * CREATOR  : Frank Mao                DATE: 2004-09-13 21:45
 * LinYG,2007-08-18,修改,1.增加设置通信参数,2.增加显示版本号
 *==========================================================================*/
int Serial_CommControl(	IN HANDLE hPort, IN int	nCmd, 
					  IN OUT void *pBuffer,	IN int	nDataLength)
{
	/* Now we use switch to test the command, 
	 * we can optimize it if there are too many commands 
	 * which maybe make lower efficiency
	 */
	SERIAL_PORT_DRV *pPort = (SERIAL_PORT_DRV *)hPort;
	int nErrCode = ERR_COMM_OK;

	char *pOpenParams = NULL;

	if (hPort == NULL)
	{
		return ERR_COMM_PORT_HANDLE;
	}

	switch (nCmd)
	{
	case COMM_GET_LAST_ERROR:	/* get the last error code	*/
		{
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(int));

			*(int *)pBuffer = pPort->nLastErrorCode;
		}

		break;

	case COMM_GET_PEER_INFO:	/* get the sender info		*/
		{
			COMM_PEER_INFO *pPeerInfo = (COMM_PEER_INFO *)pBuffer;

			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(COMM_PEER_INFO));

			if (pPort->nWorkMode != COMM_LOCAL_SERVER)
			{
				pPeerInfo->nPeerPort = pPort->nSerialPort;
				pPeerInfo->nPeerType = pPort->nWorkMode;
#ifdef _IMPL_DIAL_SERIAL
				memmove(pPeerInfo->szAddr, pPort->szPhone, sizeof(pPort->szPhone));
#else
				pPeerInfo->szAddr[0] = 0;
#endif //_IMPL_DIAL_SERIAL
			}
			else
			{
				/* The server port does not support		*/
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

	case COMM_SET_TIMEOUTS:		/* set the comm timeout in ms. */
		{	
			/* bBuffer=COMM_TIMEOUTS, nDataLength=sizeof(COMM_TIMEOUTS)		*/
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(COMM_TIMEOUTS));

			pPort->toTimeouts = *(COMM_TIMEOUTS *)pBuffer;
		}

		break;

	case COMM_GET_TIMEOUTS:		/* get the comm timeout in ms	*/
		{	
			/* bBuffer=COMM_TIMEOUTS, nDataLength=sizeof(COMM_TIMEOUTS)		*/
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(COMM_TIMEOUTS));

			*(COMM_TIMEOUTS *)pBuffer = pPort->toTimeouts;
		}
		break;

	case COMM_PURGE_TXCLEAR:	/* clear the transmit buffer	*/
		{
			if (pPort->nWorkMode != COMM_LOCAL_SERVER)
			{
//				tcflush(pPort->fdSerial,TCOFLUSH);
				Sleep(50);	// do NOTHING.
			}
			else
			{
				/* The server port does not support		*/
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

	case COMM_PURGE_RXCLEAR:	/* clear the receive buffer		*/
		{

			if (pPort->nWorkMode != COMM_LOCAL_SERVER)
			{
				char		c;

//				tcflush(pPort->fdSerial,TCIFLUSH);
				/////////////////modified by w91221////////////////////////
				if (ioctl(pPort->fdSerial,0xffff,0)== 0) 
				{					
					ioctl(pPort->fdSerial,0xff01,0);   ////clear rx buff
				}
				else

				/////////////////modified by w91221////////////////////////
				while (read(pPort->fdSerial, &c, 1) > 0)
				{
					;
				}
			}
			else
			{
				/* The server port does not support		*/
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}
		break;

	case COMM_SET_MAX_CLIENTS:	/* set the maximum allowed clients	*/
		{
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(int));

			if (pPort->nWorkMode == COMM_LOCAL_SERVER)	/* Must be server	*/
			{	
				if ((*(int *)pBuffer > 0)				 /* client must > 0	*/
					&& (*(int *)pBuffer <= SERIAL_MAX_CLIENT_LIMITAION))
				{
					pPort->nMaxClients = *(int *)pBuffer;
				}
				else
				{
					nErrCode = ERR_COMM_CTRL_PARAMS;
				}
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

	case COMM_GET_MAX_CLIENTS:		/* get the maximum allowed clients */
		{
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(int));

			if (pPort->nWorkMode == COMM_LOCAL_SERVER) /* Must be server	*/
			{
				*(int *)pBuffer = pPort->nMaxClients;
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

		/* get the number of the current connected clients */
	case COMM_GET_CUR_CLIENTS:
		{
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(int));

			if (pPort->nWorkMode == COMM_LOCAL_SERVER) /* Must be server	*/
			{
				*(int *)pBuffer = *pPort->pCurClients;
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

	case COMM_GET_CLIENT_LIMITATION:	/* get the maximum client limitation */
		{
			ASSERT(pBuffer);
			ASSERT(nDataLength == sizeof(int));

			if (pPort->nWorkMode == COMM_LOCAL_SERVER) /* Must be server	*/
			{
				*(int *)pBuffer = SERIAL_MAX_CLIENT_LIMITAION;
			}
			else
			{
				nErrCode = ERR_COMM_CTRL_COMMAND;
			}
		}

		break;

	//2007-08-19,added by LinYG
	case COMM_SET_COMM_ATTRIBUTES:

		ASSERT(pBuffer);	
		pOpenParams = (char*)pBuffer;

		//printf("pOpenParams=%s\n",pOpenParams);

		if (!Serial_ParseOpenParams(pOpenParams, &pPort->attr))
		{

			nErrCode = ERR_COMM_CTRL_COMMAND;
			//printf("ParseOpenParams error\n");

		}

		//printf("Baud=%d,oDD=%d,Data=%d,Stop=%d\n",pPort->attr.nBaud,pPort->attr.nOdd,pPort->attr.nData,
		//	pPort->attr.nStop);

		if (!Serial_SetCommAttributes(pPort->fdSerial, &pPort->attr))
		{
			nErrCode = ERR_COMM_CTRL_COMMAND;
			//printf("SetCommAttributes error\n");

		}


		break;
		//end

	default:
		{
			nErrCode = ERR_COMM_CTRL_COMMAND;
		}
	}

	return nErrCode;
}

/* The "CommClose" proc for comm driver */
/*==========================================================================*
 * FUNCTION : CommClose
 * PURPOSE  : Close an opened port and release the memory of the port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : int : ERR_COMM_OK or ERR_COMM_PORT_HANDLE
 * COMMENTS : 
 * CREATOR  : Frank Mao                 DATE: 2004-09-14 20:32
 *==========================================================================*/
int Serial_CommClose(IN HANDLE hPort)
{
	SERIAL_PORT_DRV *pPort = (SERIAL_PORT_DRV *)hPort;

	if (hPort == NULL)
	{
		return ERR_COMM_PORT_HANDLE;
	}

#ifdef _IMPL_DIAL_SERIAL
	// hangup the modem.
	if (pPort->nWorkMode != COMM_LOCAL_SERVER)
	{
		Modem_Hangup(pPort);
		pPort->bCommunicating = FALSE;
	}
#endif

	/* descrease the linkage, the pCurClients shall be deleted if no
	 * any clients. But the server and clients share the same pCurClients,
	 * and the initialing value of *pCurClients is 0, so the condition to
	 * delete pCurClients is *pCurClients < 0.
	 */
	(*pPort->pCurClients)--;
	if (*pPort->pCurClients < 0)
	{
		close(pPort->fdSerial);	// to close fd if it is not used yet.
		DELETE(pPort->pCurClients);

#ifdef _IMPL_DIAL_SERIAL
#ifdef _HAS_ACU_SCC
		close(pPort->attr.fdSCCDTR);
#endif
#endif
	}

	DELETE(pPort);

	return ERR_COMM_OK;
}

