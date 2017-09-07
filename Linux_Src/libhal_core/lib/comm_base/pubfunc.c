/*==========================================================================*
 *  FILENAME : pubfunc.c
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

/* !!!!!!!!!!!!!!!!!=== NOTICE ===!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*
 ! There is a pubfunc.c in EMNET software, if you need something, you can  !
 ! copy it from that punfunc.c of EMNET.								   !
 !
 * !!!!!!!!!!!!!!!!!=== NOTICE ===!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/

#include "stdsys.h"
#include <dlfcn.h>		// for dl*()
#include "pubfunc.h"
#include "new.h"
#include "run_thread.h"

/*==========================================================================*
 * FUNCTION : strncpyz
 * PURPOSE  : copy nDstLen-1 chars from pSrc to pDst, and append '\0' to pDst
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: OUT char  *pDst   : The target string buffer
 *            IN char  *pSrc   : The source string ending with '\0'
 *            IN int   nDstLen : The size of target buffer.
 * RETURN   : char *: The target buffer pointer: pDst
 * COMMENTS : 
 *==========================================================================*/
char *strncpyz(OUT char *pDst, IN const char *pSrc, IN int nDstLen)
{
    char *p = pDst;

    if (p == NULL)
	{
        return NULL;
	}

	if (pSrc != NULL)
	{
		for( nDstLen -= 1; *pSrc && (nDstLen > 0); nDstLen -- )
		{
			*p ++ = *pSrc ++;
		}
	}

    *p = '\0';   /* append a string end flag '\0'    */

    return pDst;
}

/*==========================================================================*
 * FUNCTION : strncpyz_f
 * PURPOSE  : add filter funtion of strncpyz(replace the illegal character
 *			  with '?')
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: OUT char  *pDst   : The target string buffer
 *            IN char  *pSrc   : The source string ending with '\0'
 *            IN int   nDstLen : The size of target buffer.
 * RETURN   : char *: The target buffer pointer: pDst
 * COMMENTS : 
 *==========================================================================*/
char *strncpyz_f(OUT char *pDst, IN const char *pSrc, IN int nDstLen, 
				REPLACE_ILLEGAL_CHR_PROC pfn)
{
    char *p = pDst;

    if (p == NULL)
	{
        return NULL;
	}

	if (pSrc != NULL)
	{
		for( nDstLen -= 1; *pSrc && (nDstLen > 0); nDstLen -- )
		{
			//*p = *pSrc ++;
			*p ++ = pfn(*pSrc ++);
		}
	}

    *p = '\0';   /* append a string end flag '\0'    */

    return pDst;
}


/*=====================================================================*
 * Function name: GetFileLength
 * Description  : 
 * Argument     : FILE *fp	: uses std method to get the file length
 * Return type  : long
 *
 * Comment(s)   : 
 *--------------------------------------------------------------------*/
long GetFileLength( IN FILE *fp )
{
        long lCurPos = ftell( fp );     // get cur pos
        long lFileLen;

        fseek( fp, 0, SEEK_END );       // move pointer to end

        lFileLen = ftell( fp );         // get cur pos is the file length

        fseek( fp, lCurPos, SEEK_SET ); // restore the pos

        return lFileLen;
}


#define SELECT_MAX_WAIT_INTERVAL	MAX_WAIT_INTERVAL	// second

/*==========================================================================*
 * FUNCTION : Sleep
 * PURPOSE  : sleep with the interval dwMilliseconds
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN DWORD  dwMilliseconds : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
#define _USES_SELECT_SLEEP	1

#ifdef _USES_SELECT_SLEEP
void Sleep( IN DWORD dwMilliseconds )
{
    struct timeval tv;

	if (dwMilliseconds == 0)
	{
		sleep(0);
		return;
	}

	while (dwMilliseconds > 0)
	{
		//RUN_THREAD_HEARTBEAT();

		if (dwMilliseconds < (SELECT_MAX_WAIT_INTERVAL*1000))
		{
			tv.tv_usec = dwMilliseconds%1000*1000;		// usec     
			tv.tv_sec  = dwMilliseconds/1000;		// seconds  

			dwMilliseconds = 0;
		}
		else
		{
			tv.tv_usec = 0;				// usec     
			tv.tv_sec  = SELECT_MAX_WAIT_INTERVAL;  // seconds  

			dwMilliseconds -= (SELECT_MAX_WAIT_INTERVAL*1000);
		}
		
		select(0,NULL,NULL,NULL, &tv); 
	}
}

#else

void Sleep( IN DWORD dwMilliseconds )
{
	int	nSeconds		= (int)(dwMilliseconds/1000);
	int	nMicroSeconds  = (int)(dwMilliseconds%1000*1000);

	// if no us, don't sleep.
	if (nMicroSeconds > 0)
	{
		usleep( (unsigned int)nMicroSeconds );
	}

	// sometimes, we need to sleep(0) to give up the CPU. 
	while (nSeconds >= 0)
	{
		//RUN_THREAD_HEARTBEAT();

		if (nSeconds <= MAX_WAIT_INTERVAL)
		{
			sleep((unsigned int)nSeconds);
			break;
		}
		else
		{
			sleep(MAX_WAIT_INTERVAL);
			nSeconds -= MAX_WAIT_INTERVAL;
		}
	}
}
#endif


/*==========================================================================*
 * FUNCTION : NEW_strdup
 * PURPOSE  : dup a string which can be free used DELETE macro
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: const char  *szSrc : 
 * RETURN   : char *: the created string (end of '\0')
 * COMMENTS : 
 *==========================================================================*/
char * NEW_strdup(const char *szSrc)
{
	char *p;
	size_t  iLen = strlen(szSrc);

	if (szSrc == NULL)
	{
		return NULL;
	}
	
	p = NEW(char, iLen + 1);
	if (p == NULL)
	{
		return NULL;
	}

	strcpy(p, szSrc);
	//p[iLen] = '\0';

	return p;
}

/*==========================================================================*
 * FUNCTION : NEW_strdup_f
 * PURPOSE  : add filter funtion of NEW_strdup(replace the illegal character
 *			  with '?')
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: const char  *szSrc : 
 * RETURN   : char *: the created string (end of '\0')
 * COMMENTS : 
 *==========================================================================*/
char * NEW_strdup_f(const char *szSrc,REPLACE_ILLEGAL_CHR_PROC pfn)
{
	char *p;
	size_t  iLen = strlen(szSrc);

	if (szSrc == NULL)
	{
		return NULL;
	}
	
	p = NEW(char, iLen + 1);
	if (p == NULL)
	{
		return NULL;
	}

	strncpyz_f(p, szSrc, iLen + 1, pfn);
	//strcpy(p, szSrc);
	//p[iLen] = '\0';

	return p;
}

/*==========================================================================*
 * FUNCTION : NEW_straddz
 * PURPOSE  : add two string, use NEW macro to get memory
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: const char  *szSrc1 : 
 *            const char  *szSrc2 : 
 * RETURN   : char * : 
 * COMMENTS : the successfully created string will end of '\0'
 *==========================================================================*/
char * NEW_straddz(const char *szSrc1, const char *szSrc2)
{
	char *szDst, *p;
	size_t iLen1, iLen2;

	if (szSrc1 == NULL || szSrc2 == NULL)
	{
		return NULL;
	}

	iLen1 = strlen(szSrc1);
	iLen2 = strlen(szSrc2);

	szDst = NEW(char, iLen1 + iLen2 +1);
	if (szDst == NULL)
	{
		return NULL;
	}
	
	strcpy(szDst, szSrc1);
	p = szDst + iLen1;
	strcpy(p, szSrc2);
	szDst[iLen1 + iLen2] = '\0';

	return szDst;
}

/*==========================================================================*
 * FUNCTION : NEW_strcpy
 * PURPOSE  : safe strcpy (use NEW macro to get memory if memory is not enough)
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: char        **szDst : 
 *            const char  *szSrc  : 
 * RETURN   : char * : 
 * COMMENTS : szSrc need to be end with NULL
 *==========================================================================*/
char * NEW_strcpy(char **szDst, const char *szSrc)
{
	size_t iDstLen, iSrcLen;
	char *p;

	if (szDst == NULL || szSrc == NULL)
	{
		return NULL;
	}

	if (*szDst == NULL)
	{
		return NULL;
	}

	iDstLen = strlen(*szDst);
	iSrcLen = strlen(szSrc);

	if (iDstLen >= iSrcLen)
	{
		strcpy(*szDst, szSrc);

		return *szDst;
	}
	else
	{
		p = NEW(char, iSrcLen + 1);

		if (p == NULL)
		{
			return NULL;
		}

		strcpy(p, szSrc);

		DELETE(*szDst);
		*szDst = p;
	
		return p;
	}
}

/*=====================================================================*
 * Function name: WaitFiledReadable
 * Description  : 
 * Arguments    : int fd	: 
 *                int nmsTimeOut: milliseconds 
 * Return type  : int : 1: data ready, 0: timeout, -1: wait error.
 *
 *--------------------------------------------------------------------*/
int WaitFiledReadable(int fd, int nmsTimeOut/* milliseconds*/)
{
    fd_set fdset, fderr;
    struct timeval tv;
    int rv = WAIT_DATA_TIMEOUT;

	while (nmsTimeOut > 0)
	{
		// need add ClearWDT() here.

		if (nmsTimeOut < (SELECT_MAX_WAIT_INTERVAL*1000))
		{
			tv.tv_usec = nmsTimeOut%1000*1000;		/* usec     */
			tv.tv_sec  = nmsTimeOut/1000;			/* seconds  */

			nmsTimeOut = 0;
		}
		else
		{
			tv.tv_usec = 0;							/* usec     */
			tv.tv_sec  = SELECT_MAX_WAIT_INTERVAL;  /* seconds  */

			//RUN_THREAD_HEARTBEAT();

			nmsTimeOut -= (SELECT_MAX_WAIT_INTERVAL*1000);
		}

		FD_ZERO(&fdset);		/*  Initializes the set to the NULL set. */
		FD_SET(fd, &fdset);		/*  Adds descriptor s to set. */

		fderr = fdset;
		
		/* determines the status of one or more sockets, waiting if necessary, 
		 * to perform synchronous I/O.     */
#ifdef unix
		rv = select(fd+1, &fdset, NULL, &fderr, &tv);
#else
		rv = select(FD_SETSIZE, &fdset, NULL, &fderr, &tv);
#endif

		if (rv > 0)	// data ready or error.
		{
			rv = (FD_ISSET(fd, &fdset)) ? WAIT_DATA_READY	// OK
				: (FD_ISSET(fd, &fderr)) ? WAIT_DATA_EXCEPT	// except
				: WAIT_DATA_ERROR;							// error??
			break;
		}
		else if (rv < 0)
		{
			rv = WAIT_DATA_ERROR;							// error??
			break;
		}
	}

    return rv;  /* select error, or time out */
}



/*=====================================================================*
 * Function name: WaitFiledWritable
 * Description  : 
 * Arguments    : int fd	: 
 *                int nmsTimeOut: milliseconds
 * Return type  : int : 1: data ready, 0: timeout, -1: wait error.
 *
 *--------------------------------------------------------------------*/
int WaitFiledWritable(int fd, int nmsTimeOut/* milliseconds*/)
{
    fd_set fdset, fderr;
    struct timeval tv;
    int rv = WAIT_DATA_TIMEOUT;

	while (nmsTimeOut > 0)
	{
		// need add ClearWDT() here.
		if (nmsTimeOut < (SELECT_MAX_WAIT_INTERVAL*1000))
		{
			tv.tv_usec = nmsTimeOut%1000*1000;		/* usec     */
			tv.tv_sec  = nmsTimeOut/1000;			/* seconds  */

			nmsTimeOut = 0;
		}
		else
		{
			tv.tv_usec = 0;							/* usec     */
			tv.tv_sec  = SELECT_MAX_WAIT_INTERVAL;  /* seconds  */

			//RUN_THREAD_HEARTBEAT();

			nmsTimeOut -= (SELECT_MAX_WAIT_INTERVAL*1000);
		}

		FD_ZERO(&fdset);		/*  Initializes the set to the NULL set. */
		FD_SET(fd, &fdset);		/*  Adds descriptor s to set. */

		fderr = fdset;
		
		/* determines the status of one or more sockets, waiting if necessary, 
		* to perform synchronous I/O.     */
#ifdef unix
		rv = select(fd+1, NULL, &fdset, &fderr, &tv);
#else
		rv = select(FD_SETSIZE, NULL, &fdset, &fderr, &tv);
#endif

		if (rv > 0)	// data ready or error.
		{
			rv = (FD_ISSET(fd, &fdset)) ? WAIT_DATA_READY	// OK
				: (FD_ISSET(fd, &fderr)) ? WAIT_DATA_EXCEPT	// except
				: WAIT_DATA_ERROR;							// error??
			break;
		}
		else if (rv < 0)
		{
			rv = WAIT_DATA_ERROR;							// error??
			break;
		}
	}

    return rv;  /* select error, or time out */
}

#ifdef unix
/***
*char *_strupr(string) - map lower-case characters in a string to upper-case
*
*Purpose:
*   _strupr() converts lower-case characters in a null-terminated string
*   to their upper-case equivalents.  Conversion is done in place and
*   characters other than lower-case letters are not modified.
*
*   In the C locale, this function modifies only 7-bit ASCII characters
*   in the range 0x61 through 0x7A ('a' through 'z').
*
*   If the locale is not the 'C' locale, MapStringW() is used to do
*   the work.  Assumes enough space in the string to hold result.
*
*Entry:
*   char *string - string to change to upper case
*
*Exit:
*   input string address
*
*Exceptions:
*   The original string is returned unchanged on any error.
*
*******************************************************************************/

char * strupr(char * string)
{
    char * cp;

    for (cp=string; *cp; ++cp)
    {
        if ('a' <= *cp && *cp <= 'z')
            *cp += 'A' - 'a';
    }

    return(string);
}

#endif


/*==========================================================================*
 * FUNCTION : GetCurrentTime
 * PURPOSE  : unit is seconds.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS:   void : 
 * RETURN   : double : the current time in seconds.
 * COMMENTS : 
 *==========================================================================*/
double GetCurrentTime(void)
{
	struct timeval tv;

	gettimeofday(&tv, NULL);

	return (double)tv.tv_sec + (double)tv.tv_usec/(double)1000000;
}




/*==========================================================================*
 * FUNCTION : LoadDynamicLibrary
 * PURPOSE  : load a shared lib
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN const char  *pszLibName    : 
 *            IN int         nSym           : 
 *            IN const char  *ppszSymName[] : array of symbols
 *            OUT HANDLE     *pfnProc[]     : array to save symbol addr
 *            IN BOOL	 	 bNeedAllSym    : TRUE: all symbols shall be loaded
 *                                          FALSE: at least one symbol be loaded
 * RETURN   : HANDLE : non-NULL for OK, NULL for error.
 * COMMENTS : if bNeedAllSym is FALSE, some sym may be not found. 
 *==========================================================================*/
HANDLE LoadDynamicLibrary(IN const char *pszLibName,
					   IN int			nSym,
					   IN const char	*ppszSymName[],
					   OUT HANDLE		*pfnProc[],
					   IN BOOL			bNeedAllSym)
{
	int		i;
	HANDLE  hLib;
	//printf("in pubfunc 585 now!\n");//----add by wankun
	//1. open lib
	hLib = (HANDLE)dlopen(pszLibName, RTLD_LAZY);
	if (hLib == NULL)
	{
		printf("[LoadDynamicLibrary] -- dlopen %s fails on error %s.\n",
			pszLibName, dlerror() );

		return NULL;
	}
	//printf("in pubfunc 595 now!\n");----add by wankun
	//printf("pszlibname is %s, nsym=%d\n",pszLibName,nSym);//----add by wankun
	//2. load all symbols
	for (i = 0; i < nSym; i++)
	{
        *pfnProc[i] = (HANDLE)dlsym(hLib, ppszSymName[i]);
		if (*pfnProc[i] == NULL)
		{
			printf("[LoadDynamicLibrary] -- Fails on getting the "
				"address of \"%s\".%s\n",
				ppszSymName[i],dlerror());

			if (bNeedAllSym)	// error. due to all sym shall be found
			{				
				dlclose(hLib);
				return NULL;
			}
		}
	}
	//printf("in pubfunc 613 now!\n");----add by wankun
	return hLib;	// OK
}



/*==========================================================================*
 * FUNCTION : UnloadDynamicLibrary
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hLib : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void UnloadDynamicLibrary(IN HANDLE hLib)
{
	if (hLib != NULL)
	{
		dlclose(hLib);
	}
}


/*==========================================================================*
 * FUNCTION : AppendArrayItem
 * PURPOSE  : append a new item to ppArray, and increase the cur item num
 *            *pCurItems. The memory of *ppArray will be enlarged.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN OUT void  **ppArray  : The item will be appened to array,
 *                                      IN the old ptr, OUT the enlarged ptr 
 *            IN int       nItemSize  : an item size of array
 *            IN OUT int   *pCurItems : current item size in, +1 when out if OK
 *            IN void      *pNew      : the new item
 * RETURN   : void *: NULL for out of memory, the old array is DELETED.
 *                    else for OK.
 * COMMENTS : 
 *==========================================================================*/
void *AppendArrayItem(IN OUT void **ppArray, IN int nItemSize,
					  IN OUT int *pCurItems, IN void *pNew)
{
	char	*pNewArray;
	size_t	pOldSize = *pCurItems * nItemSize;

	pNewArray = RENEW( char, *ppArray, (pOldSize+nItemSize));
	if (pNewArray != NULL)
	{
		memmove(&pNewArray[pOldSize], pNew, (size_t)nItemSize);
		(*pCurItems)++;
	}
	else
	{
		TRACEX("out of memory, delete the old.\n");

		if (*ppArray != NULL)
		{
			DELETE(*ppArray);
		}
	}

	*ppArray = pNewArray;

	return *ppArray;
}



/*==========================================================================*
 * FUNCTION : MakeFullFileName
 * PURPOSE  : to make a full path as pszRootDir/pszSubDir/pszFileName
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: OUT char       *pFullName   : buffer to save full name, size must
 *                                          be greater than MAX_FILE_PATH(256)
 *            IN const char  *pszRootDir  : if NULL or "", treat it as "."
 *            IN const char  *pszSubDir   : if NULL or "", treat it as "."
 *            IN const char  *pszFileName : 
 * RETURN   : char *: return pFullName
 * COMMENTS : 
 *==========================================================================*/
char *MakeFullFileName(OUT char *pFullName,
							  IN const char *pszRootDir,
							  IN const char *pszSubDir,
							  IN const char *pszFileName)
{
	if ((pszRootDir == NULL) || (*pszRootDir == 0))
	{
		pszRootDir = ".";
	}

	if ((pszSubDir == NULL) || (*pszSubDir == 0))
	{
		pszSubDir = ".";
	}

	snprintf(pFullName, MAX_FILE_PATH, "%s/%s/%s",
		pszRootDir, pszSubDir, pszFileName);

	return pFullName;
}


/*==========================================================================*
 * FUNCTION : FindIntItemIndex
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nItemToFind : 
 *            IN int  *pIntArray  : 
 *            IN int  nItemNum    : 
 * RETURN   : int : -1: for not found, other is the index of the item
 *==========================================================================*/
int FindIntItemIndex(IN int nItemToFind, IN int *pIntArray, IN int nItemNum)
{
	int i;

	if (pIntArray != NULL)
	{
		for (i = 0; i < nItemNum; i++, pIntArray++)
		{
			if(nItemToFind == *pIntArray)
			{
				return i;
			}
		}
	}

	return -1;
}


/*==========================================================================*
 * FUNCTION : *TimeToString
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN time_t   tmTime      : 
 *            const char  *fmt        : see help of strftime
 *            OUT char    *strTime    : 
 *            IN int      nLenStrTime : 
 * RETURN   : char : 
 * COMMENTS : 
 *==========================================================================*/
char *TimeToString(IN time_t tmTime, const char *fmt, 
				   OUT char *strTime, IN int nLenStrTime)
{
	struct tm gmTime;

	// conver time, maofuhua, 04-11-12
	gmtime_r(&tmTime, &gmTime);

	// convert time to yyyy-mm-dd hh:mm:ss
	strftime(strTime, (size_t)nLenStrTime, fmt, &gmTime);

	return strTime;
}

