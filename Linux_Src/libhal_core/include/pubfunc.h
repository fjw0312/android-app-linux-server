/*==========================================================================*
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#ifndef __PUBFUNC_H__
#define __PUBFUNC_H__

/* 
 * Functions for string operating	
 */
#include "basetypes.h"

typedef char (*REPLACE_ILLEGAL_CHR_PROC)(int c); 

char *strncpyz(OUT char *pDst, IN const char *pSrc, IN int nDstLen);
char *strncpyz_f(OUT char *pDst, IN const char *pSrc, IN int nDstLen, 
				REPLACE_ILLEGAL_CHR_PROC pfn);
char * NEW_strdup_f(const char *szSrc,REPLACE_ILLEGAL_CHR_PROC pfn);

char * NEW_strdup(IN const char *szSrc);
char * NEW_straddz(IN const char *szSrc1, IN const char *szSrc2);
char * NEW_strcpy(OUT char **szDst, IN const char *szSrc);

#ifdef unix
#define stricmp		strcasecmp
#define strnicmp	strncasecmp
char * strupr(char * string);
#endif

// init mem to 0. p: the ptr of struct objects, n: the num of struct items
#define ZERO_POBJS(p, n)	memset((p), 0, (n)*sizeof(*(p)))

/*
 * Functions for FILE operating
 */

long GetFileLength( IN FILE *fp );

void Sleep( IN DWORD dwMilliseconds );

#define WAIT_DATA_READY        1
#define WAIT_DATA_TIMEOUT     0
#define WAIT_DATA_ERROR       -1
#define WAIT_DATA_EXCEPT      -2

/*=====================================================================*
 * Function name: WaitFiledWritable
 * Description  : 
 * Arguments    : int fd	: 
 *                int nmsTimeOut: milliseconds
 * Return type  : int : 1: data ready, 0: timeout, -1: wait error.
 *
 *--------------------------------------------------------------------*/
int WaitFiledWritable(int fd, int nmsTimeOut/* milliseconds*/);

/*=====================================================================*
 * Function name: WaitFiledReadable
 * Description  : 
 * Arguments    : int fd	: 
 *                int nmsTimeOut: milliseconds 
 * Return type  : int : 1: data ready, 0: timeout, -1: wait error.
 *
 *--------------------------------------------------------------------*/
int WaitFiledReadable(int fd, int nmsTimeOut/* milliseconds*/);


double GetCurrentTime(void); // return the current time in seconds. 

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
 *==========================================================================*/
HANDLE LoadDynamicLibrary(IN const char *pszLibName,
					   IN int			nSym,
					   IN const char	*ppszSymName[],
					   OUT HANDLE		*pfnProc[],
					   IN BOOL			bNeedAllSym);

void UnloadDynamicLibrary(IN HANDLE hLib);


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
					  IN OUT int *pCurItems, IN void *pNew);



#define MAX_FILE_PATH			256

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
							  IN const char *pszFileName);


/*==========================================================================*
 * FUNCTION : FindIntItemIndex
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nItemToFind : 
 *            IN int  *pIntArray  : 
 *            IN int  nItemNum    : 
 * RETURN   : int : -1: for not found, other is the index of the item
 * COMMENTS : 
 *==========================================================================*/
int FindIntItemIndex(IN int nItemToFind, IN int *pIntArray, IN int nItemNum);



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
				   OUT char *strTime, IN int nLenStrTime);

#define TIME_CHN_FMT		"%Y-%m-%d %H:%M:%S"	// YYYYMMDD hhmmss
#define TIME_COMPACT_FMT	"%y%m%d %H%M%S"		// YYMMDDhhmmss

#endif /*__PUBFUNC_H__*/
