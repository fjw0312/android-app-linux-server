/*  BaseTypes.h
 * base data type defined as windows style 
 */

#ifndef __BASE_TYPES_H_2004_09_08
#define __BASE_TYPES_H_2004_09_08 

#include <endian.h>

#if __BYTE_ORDER == __LITTLE_ENDIAN		//x86
	#undef	_CPU_BIG_ENDIAN
	#define _CPU_LITTLE_ENDIAN	1
#elif __BYTE_ORDER == __BIG_ENDIAN		// ppc
	#undef  _CPU_LITTLE_ENDIAN
	#define _CPU_BIG_ENDIAN		1
#endif

#define __INLINE	inline

#ifndef WIN32
typedef char				CHAR;
typedef unsigned char       UCHAR;
typedef unsigned char       BYTE;      /* by : variable prefix */
typedef unsigned short      WORD;      /* n  : variable prefix */
typedef unsigned long       DWORD;     /* dw : variable prefix */
typedef unsigned long       ULONG;     /* ul : variable prefix */
typedef DWORD               *LPDWORD;
typedef WORD                *LPWORD;
typedef int                 BOOL;      /* b  : variable prefix */
typedef long                LONG;      /* l  : variable prefix */
typedef void                *HANDLE;
#define CALLBACK
typedef void                VOID;
typedef void				*PVOID;
typedef VOID                *LPVOID;

typedef unsigned int		UINT;
#endif

/* used by sig value */
typedef long				SIG_TIME;
typedef long				SIG_ENUM;

#ifndef _UTIME_T_DEFINED
typedef unsigned long utime_t;
#define _UTIME_T_DEFINED
#endif

#ifndef XBOOL   // unix boolean
#define XBOOL   int
#define OK      0
#define SUCCESS OK
#define FAIL    -1
#endif

#ifndef BOOL    
#define BOOL    int
#define TRUE    1
#define FALSE   0
#endif    

#define UNUSED(x)	(void)(x)

#ifdef _DEBUG
#define TRACE   printf
// trace with function and line number
#define TRACEX	printf("[%s:%d] -- ", __FUNCTION__, __LINE__), printf
#else
#define TRACE   1 ? (void)0 : (void)printf
#define TRACEX	1 ? (void)0 : (void)printf
#endif

#ifndef ASSERT		// Protocol defined in assert.h
#define ASSERT		assert
#endif

#ifndef IN
#define IN
#define OUT
#endif

#ifndef MIN
#define MIN(x,y)    ( ((x)<=(y)) ? (x) : (y) )
#endif

#ifndef MAX
#define MAX(x,y)    ( ((x)>=(y)) ? (x) : (y) )
#endif

#ifndef SWAP
#define SWAP(T, a, b)   do { T t = (a); (a) = (b); (b) = (t); } while(0)
#endif

#ifndef ABS
#define ABS(a)	(((a) < 0) ? (-(a)) : (a))
#endif

#define EPSILON					0.00001
#define FLOAT_EQUAL(f1, f2)		(ABS(((f1)-(f2)))<=EPSILON)
#define FLOAT_EQUAL0(f)			(FLOAT_EQUAL((f), 0))
#define FLOAT_NOT_EQUAL(f1, f2)		(ABS(((f1)-(f2)))>EPSILON)
#define FLOAT_NOT_EQUAL0(f)			(FLOAT_NOT_EQUAL((f), 0))



#ifndef DWORD_PTR
#define DWORD_PTR DWORD
#endif //DWORD_PTR

#ifndef MAKEWORD
#define MAKEWORD(a, b)      ((WORD)(((BYTE)((DWORD_PTR)(a) & 0xff)) | ((WORD)((BYTE)((DWORD_PTR)(b) & 0xff))) << 8))
#endif //MAKEWORD

#ifndef MAKELONG
#define MAKELONG(a, b)      ((LONG)(((WORD)((DWORD_PTR)(a) & 0xffff)) | ((DWORD)((WORD)((DWORD_PTR)(b) & 0xffff))) << 16))
#endif //MAKELONG


#ifndef LOWORD
#define LOWORD(l)           ((WORD)((DWORD_PTR)(l) & 0xffff))
#endif //LOWORD


#ifndef HIWORD
#define HIWORD(l)           ((WORD)((DWORD_PTR)(l) >> 16))
#endif //HIWORD


#ifndef LOBYTE
#define LOBYTE(w)           ((BYTE)((DWORD_PTR)(w) & 0xff))
#endif //LOBYTE


#ifndef HIBYTE
#define HIBYTE(w)           ((BYTE)((DWORD_PTR)(w) >> 8))
#endif //HIBYTE


#ifdef unix
typedef void *(*PTHREAD_START_ROUTINE)(void *);
#endif

// Get the item of an array, the array must be defined as:
// ITEM_TYPE	array[] = { item, item2, ...};
// This macro can NOT be used to get the item of a pointer, ITEM_TYPE *pArray!
#define ITEM_OF( array )		(int)(sizeof(array) / sizeof(array[0]))


// some basic const value for time
#define MS_PER_SEC		1000
#define US_PER_SEC		1000000
#define NS_PER_MS		1000000		//
#define NS_PER_SEC		1000000000

#define SEC_PER_HOUR	3600
#define SEC_PER_DAY		86400

#define MS_TO_NS(ms)	((ms)*NS_PER_MS)
#define MS_TO_SEC(ms)	((ms)/MS_PER_SEC)

#define WAIT_INFINITE	((unsigned long)-1)

#define ACU_MINFLOAT	-1000000


#endif // ifndef __BASE_TYPES_H
