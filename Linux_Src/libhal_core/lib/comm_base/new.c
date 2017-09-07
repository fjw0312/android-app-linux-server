/*==========================================================================*
 *
 *  FILENAME : new.c

 *  VERSION  : V1.00
 *  PURPOSE  : To manage the allocated memory in DEBUG version
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
/* memory management utility */

#include <stdio.h>
#include <malloc.h>
#include <string.h>
#include <stdlib.h>
#include <signal.h>
#include <setjmp.h>

#include "new.h"

#ifdef _DEBUG
int _MEM_MGR_SHOW_NEW_MSG = 0;

//manage the allocated memory
#define _MANAGE_ALLOCATED_MEMORY	1

#define _MULTI_THREAD_SAFE_NEW		1

#if defined(_MANAGE_ALLOCATED_MEMORY) && !defined(_MULTI_THREAD_SAFE_NEW)
#define _MULTI_THREAD_SAFE_NEW		1
#endif

//#define MAX_FILE_LEN    80
typedef struct tagMemoryHead _MEMORY_NODE;

struct tagMemoryHead
{
    unsigned	long ulBeginFlag;   /* must be the first */

#ifdef	_MANAGE_ALLOCATED_MEMORY
	_MEMORY_NODE	*next;
	_MEMORY_NODE	*prev;
#endif

    int			nId;
    int			nLine;
    char		*pszFile;
	char		*pszFunction;
	size_t		ulSize;	
    unsigned	long ulFlag;   /* must locate before pBuffer */
    char		pBuffer[0];    /* must be last item    */
};

#define _MEMORY_FLAG        0x55aaaa55ul
#define _N_DBG_HEAD_OF_BUF  sizeof(_MEMORY_NODE)

//add end flag check, maofuhua, 2004-11-11. add end flag size
#define _N_NODE_EXTRA_SIZE	(_N_DBG_HEAD_OF_BUF + sizeof(unsigned long))

#define DEBUG_GET_NODE(p)	((_MEMORY_NODE *)((char *)(p)-_N_DBG_HEAD_OF_BUF))
#define DEBUG_IS_NODE(p)	(((p)->ulFlag == _MEMORY_FLAG) \
							&& ((p)->ulBeginFlag == _MEMORY_FLAG))

#define NODE_SET_END_FLAG(p)	\
	(*((unsigned long *)(&(p)->pBuffer[(p)->ulSize])) = _MEMORY_FLAG)

#define NODE_IS_OVERRIDED(p) \
	(*((unsigned long *)(&(p)->pBuffer[(p)->ulSize])) != _MEMORY_FLAG)

static int		g_nMemoryBlockCount  = 0;	// total unfreed mem in block
static int		g_nAllocatedMemoryId = 0;	// ID, always increased
static size_t	g_ulMemoryByteCount  = 0;	// total unfreed mem in byte

int MEM_GET_INFO(int *pnAllocatedMemoryId, size_t *pulMemoryByteCount)
{
	*pnAllocatedMemoryId = g_nAllocatedMemoryId;
	*pulMemoryByteCount  = g_ulMemoryByteCount;
	
	return g_nMemoryBlockCount;
}

#ifdef _MULTI_THREAD_SAFE_NEW
#include <pthread.h>

static pthread_mutex_t	hMemLock = PTHREAD_MUTEX_INITIALIZER;

#define LOCK_MEM_INIT()		  pthread_mutex_init(&hMemLock, NULL)
#define LOCK_MEM_SYNC()       pthread_mutex_lock(&hMemLock)
#define UNLOCK_MEM_SYNC()     pthread_mutex_unlock(&hMemLock)
#define DESTROY_MEM_SYNC()    pthread_mutex_destroy(&hMemLock)
#endif


static jmp_buf s_JumpBuf_SIGSEGV;
static void DEBUG_MEMORY_MANAGER_SIGSEGV(int n)
{
	n = n;
	longjmp(s_JumpBuf_SIGSEGV, 1);
}

// check the pointer field of mem node. maofuhua, 2004-11-14
static int CHECK_STRING_FIELD_VALID(char *pszStrToChk)
{
	char	szTemp[256];

	strncpy( szTemp, pszStrToChk, sizeof(szTemp));

	return szTemp[0];
}

static _MEMORY_NODE *DEBUG_MEMORY_MANAGER_CHECK_NODE(char *__caller__,
									_MEMORY_NODE *pNode, 
									char *__file__, int __line__, 
									char * __function__)
{
	__sighandler_t	pfnOldHandlder_SIGSEGV = NULL;
	_MEMORY_NODE	*pMemChecked;
	char			*pNodeName;

	if (pNode == NULL)
	{
		return NULL;
	}

	// avoid the node p is invalid to cause SEGMENT fault.
	/* set up to catch the SIGSEGV signal */
	pfnOldHandlder_SIGSEGV = signal(SIGSEGV, DEBUG_MEMORY_MANAGER_SIGSEGV);

	pNodeName = "this";
	pMemChecked = pNode;

	// set the on error return position.
	// setjmp returns non-0 if longjmp is called, error has happened.
	if(setjmp(s_JumpBuf_SIGSEGV) == 0)	// return 0 after init jmp buffer
	{
		// if cause segment fault, trigger SIGSEGV
		if (DEBUG_IS_NODE(pNode))	// check 
		{
			// check the node is written exceed end bound.
			if (NODE_IS_OVERRIDED(pNode))
			{
				printf("[MemMgr] -- Panic! %s: the memory %p(%s:%d:%s) is "
					"end overrided.\n",
					__caller__,
					((char *)pNode) + _N_DBG_HEAD_OF_BUF,
					pNode->pszFile, pNode->nLine, pNode->pszFunction);
			}

			// check the pszFile field pointer
			pNodeName = "this->pszFile";
			CHECK_STRING_FIELD_VALID(pNode->pszFile);

			// check the pszFunction field pointer
			pNodeName = "this->pszFunction";
			CHECK_STRING_FIELD_VALID(pNode->pszFunction);

			// check the first item next. This is a loop link.
#ifdef _MANAGE_ALLOCATED_MEMORY
			pNodeName = "next";
			pMemChecked = pNode->next;
			(void)DEBUG_IS_NODE(pNode->next);

			pNodeName = "prev";
			pMemChecked = pNode->prev;
			(void)DEBUG_IS_NODE(pNode->prev);
#endif
		}
		else 
		{
			printf("[MemMgr] -- Panic! %s: the memory ID:%d:%p(%s:%d:%s) is crashed.\n",
				__caller__,
				g_nAllocatedMemoryId,
				((char *)pNode) + _N_DBG_HEAD_OF_BUF,
				__file__, __line__, __function__);

			pNode = NULL;
		}
	}
	else
	{
		printf("[MemMgr] -- Panic! %s: the %s memory(%p) of ID:%d:%p(%s:%d:%s) causes a "
			"segment failt.\n",
			__caller__,
			pNodeName,
			((char *)pMemChecked) + _N_DBG_HEAD_OF_BUF,
			g_nAllocatedMemoryId,
			((char *)pNode) + _N_DBG_HEAD_OF_BUF,
			__file__, __line__, __function__);

		if (pNode == pMemChecked)	// this node is invalid.
		{
			pNode = NULL;
		}
	}

	// restore old handler.
	signal( SIGSEGV, pfnOldHandlder_SIGSEGV );

	return pNode;
}

#define DEBUG_MEMORY_MANAGER_GET_NODE(__caller__, p, \
	__file__, __line__, __function__)	\
	DEBUG_MEMORY_MANAGER_CHECK_NODE((__caller__), DEBUG_GET_NODE(p),\
		(__file__), (__line__),	(__function__))

/////////////// log malloced memory
#ifdef _MANAGE_ALLOCATED_MEMORY
static int g_nMemManagerInited = 0;

// the mem head. must be 2. the item[1] will be overwritten.
static _MEMORY_NODE	g_pMemNodeHead[2];
#define g_MemNodeHead	(g_pMemNodeHead[0])

static void DEBUG_DESTROY_MEMORY_MANAGER(void);

/*==========================================================================*
 * FUNCTION : DEBUG_INIT_MEMORY_MANAGER
 * PURPOSE  : to init the mem head to a bi-link.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS:   void : 
 * RETURN   : static void : 
 * COMMENTS : 
 *==========================================================================*/
static void DEBUG_INIT_MEMORY_MANAGER(void)
{
	_MEMORY_NODE	*pHead = &g_MemNodeHead;

#ifdef _MULTI_THREAD_SAFE_NEW
	LOCK_MEM_INIT();
#endif

	memset(&g_pMemNodeHead, 0, sizeof(g_pMemNodeHead)); 

	pHead->pszFunction = __FUNCTION__;
	pHead->ulFlag  = pHead->ulBeginFlag = _MEMORY_FLAG;
	pHead->pszFile = __FILE__;
	pHead->nLine   = __LINE__;
	pHead->nId     = 0;
	pHead->ulSize  = 0;

	// this call will overwrite the g_pMemNodeHead[1]
	NODE_SET_END_FLAG(pHead);

	// construct a loop dual-link.
	pHead->next = pHead->prev = pHead;

	atexit(DEBUG_DESTROY_MEMORY_MANAGER);
}


static void DEBUG_DESTROY_MEMORY_MANAGER(void)
{
	__sighandler_t	pfnOldHandlder_SIGSEGV = NULL;
	int				nCurLine = __LINE__;
	char			*pCurRunning = "Nothing";
#define TRACE_RUNNING_INFO(info)		(nCurLine = __LINE__, pCurRunning = (info))

	// show leaked memory
	if (g_nMemoryBlockCount == 0)
	{
		return;
	}

#ifdef _MULTI_THREAD_SAFE_NEW
	LOCK_MEM_SYNC();
#endif
	// avoid the node p is invalid to cause SEGMENT fault.
	/* set up to catch the SIGSEGV signal */
	pfnOldHandlder_SIGSEGV = signal(SIGSEGV, DEBUG_MEMORY_MANAGER_SIGSEGV);

	if(setjmp(s_JumpBuf_SIGSEGV) == 0)	// return 0 after init jmp buffer
	{
		int				i;
		_MEMORY_NODE	*pNode;
		_MEMORY_NODE	*p, *pEndNode;
		int				nCheckByNext = 1; 

		printf( "\n\n[MemMgr] -- PANIC! There are %d memory leaks (%u bytes) "
			"after exit.\n",
			g_nMemoryBlockCount, g_ulMemoryByteCount);

		pNode    = g_MemNodeHead.next;
		pEndNode = &g_MemNodeHead;

		for (i = 1; pNode != pEndNode; )
		{
			// to check the node.
			p = DEBUG_MEMORY_MANAGER_CHECK_NODE("MANAGER", pNode, __FILE__,
				__LINE__, __FUNCTION__);

			if ( p != NULL)
			{
				TRACE_RUNNING_INFO("displaying leaked node");

				printf("[MemMgr] -- Leak %-3d: Mem=%p ID=%-6d L=%-6d "
					"NEW at %s:%d:%s.\n",
					i, 
					p->pBuffer,
					p->nId, (int)p->ulSize, p->pszFile, p->nLine,
					p->pszFunction);

				TRACE_RUNNING_INFO("going to next node");

				// go to next/prev node.
				pNode = (nCheckByNext) ? pNode->next : pNode->prev;

				TRACE_RUNNING_INFO("freeing leaked node");

				// free leaked memory
				new_free((void*)p);

				i++;
			}
			else
			{
				TRACE_RUNNING_INFO("displaying creashed node");

				printf("[MemMgr] -- Leak %4d: Mem=%p is crashed.\n",
					i, ((char *)pNode)+_N_DBG_HEAD_OF_BUF);

				if (nCheckByNext)
				{
					TRACE_RUNNING_INFO("setting reverse mode");

					printf("[MemMgr] -- Memory leak check to try reverse "
						"direction.\n");

					nCheckByNext = 0;
					pEndNode     = pNode;
					pNode        = g_MemNodeHead.prev;
				}
				else
				{
					printf("[MemMgr] -- Memory leak check fails in reverse "
						"direction, exit.\n");
					break;
				}
			}
		}

		printf( "[MemMgr] -- End of showing %d memory leaks, "
			"there are %d crashed memory blocks.\n",
			g_nMemoryBlockCount,
			g_nMemoryBlockCount-(i-1));
	}
	else
	{
		// causes a segment fault.
		printf("[MemMgr] -- PANIC! Memory manager %s:%d causes a "
			"segment fault while executing %s.\n",
			__FILE__, nCurLine, pCurRunning);
			// restore old handler.
	}

	// restore old handler of the signal
	signal( SIGSEGV, pfnOldHandlder_SIGSEGV );		

#ifdef _MULTI_THREAD_SAFE_NEW
	UNLOCK_MEM_SYNC();

	DESTROY_MEM_SYNC();
#endif

#undef TRACE_RUNNING_INFO
}

static void DEBUG_ADD_NODE(_MEMORY_NODE *p, char *__file__, int __line__,
						   char *__function__)
{
	if (DEBUG_MEMORY_MANAGER_CHECK_NODE("NEW", g_MemNodeHead.next, 
		__file__, __line__, __function__) != NULL)
	{
		// insert at head
		p->next			   = g_MemNodeHead.next;
		g_MemNodeHead.next = p;

		p->prev            = &g_MemNodeHead;
		p->next->prev      = p;
	}
	else
	{
		printf( "[MemMgr] -- Memory allocation at %s:%d:%s OK, but fails on "
			"inserting it to memory list for the previous allocated memory error.\n",
			__file__, __line__, __function__);
	}
}

static void DEBUG_DEL_NODE(_MEMORY_NODE *p)
{
	p->prev->next = p->next;
	p->next->prev = p->prev;
}

#endif
///////////////

void *DEBUG_NEW( size_t dwSize, char *__file__, int __line__,
				char * __function__)
{
    _MEMORY_NODE *p;

#ifdef _MANAGE_ALLOCATED_MEMORY
	if (!g_nMemManagerInited)
	{
		g_nMemManagerInited = 1;
		DEBUG_INIT_MEMORY_MANAGER();
	}
#endif
   
    p = (_MEMORY_NODE *)new_malloc(sizeof(char)*(dwSize + _N_NODE_EXTRA_SIZE));
    if(p != NULL )
    {
		// OK, to init mem node.
#ifdef _MULTI_THREAD_SAFE_NEW
		LOCK_MEM_SYNC();
#endif

		p->nId    = ++g_nAllocatedMemoryId;
		p->ulFlag = _MEMORY_FLAG;
		p->ulBeginFlag = _MEMORY_FLAG;
		p->ulSize = dwSize;
		p->nLine  = __line__;
		p->pszFile= __file__;
		p->pszFunction = __function__;

		NODE_SET_END_FLAG(p);	// add end overrided check flag.

		g_nMemoryBlockCount += 1;
		g_ulMemoryByteCount += dwSize;

		if (_MEM_MGR_SHOW_NEW_MSG)
		{
			printf( "[MemMgr] -- NEW %d(%p),ID=%d: File %s:%d:%s L=%d.\n",
				g_nMemoryBlockCount, 
				p->pBuffer,
				p->nId, __file__, __line__, __function__, (int)dwSize );
			fflush(stdout);
		}

#ifdef _MANAGE_ALLOCATED_MEMORY
		DEBUG_ADD_NODE(p, __file__, __line__, __function__);
#endif

#ifdef _MULTI_THREAD_SAFE_NEW
		UNLOCK_MEM_SYNC();
#endif
	}

	else
	{
		printf( "[MemMgr] -- NEW failed at File %s:%d:%s L=%d.\n",
			__file__, __line__, __function__, (int)dwSize );
		fflush(stdout);
	}

    return (p != NULL) ? (void *)(p->pBuffer) : NULL;
}


void *DEBUG_RENEW(void *pOldPtr, size_t dwNewSize, 
				  char *__file__, int __line__,
				  char * __function__)
{
    _MEMORY_NODE *p, *pOldNode;

	if (dwNewSize == 0)
	{
		// same as free(), call DEBUG_DELETE()
		DEBUG_DELETE(pOldPtr, __file__, __line__, __function__);
		return NULL;	// freed
	}

	if (pOldPtr == NULL)
	{
		// same as malloc(), call DEBUG_NEW()
		return DEBUG_NEW(dwNewSize, __file__, __line__, __function__);
	}

#ifdef _MULTI_THREAD_SAFE_NEW
	LOCK_MEM_SYNC();
#endif

	// it's real realloc.get the real malloc addr
	pOldNode = DEBUG_MEMORY_MANAGER_GET_NODE("RENEW", pOldPtr, __file__, __line__,
		__function__);
	if (pOldNode != NULL)
	{
#ifdef _MANAGE_ALLOCATED_MEMORY
		// remove the old node at first, it may be changed after realloc
		DEBUG_DEL_NODE(pOldNode);
#endif

		p = (_MEMORY_NODE *)new_realloc(pOldNode, 
			sizeof(char)*(dwNewSize + _N_NODE_EXTRA_SIZE) );
		if (p != NULL)
		{
			// the head info is copied to the new returned addr,
			// that need not set any information
			// the num of mem block is not changed. 
			// But, we need record the file and line info.
			g_ulMemoryByteCount -= p->ulSize;
			g_ulMemoryByteCount += dwNewSize;

			p->nId			= ++g_nAllocatedMemoryId;
			p->nLine		= __line__;
			p->pszFile		= __file__;
			p->pszFunction  = __function__;
			p->ulSize		= dwNewSize;
			
			NODE_SET_END_FLAG(p);	// add end overrided check flag.

			if (_MEM_MGR_SHOW_NEW_MSG)
			{
				printf( "[MemMgr] -- RENEW %d(%p),ID=%d:" \
					" NEW: %s:%d:%s, L=%d, RENEW: %s:%d:%s L=%d.\n",
					g_nMemoryBlockCount, p->pBuffer, p->nId,
					p->pszFile, p->nLine, p->pszFunction,
					p->ulSize,
					__file__, __line__, __function__,
					dwNewSize);
				fflush(stdout);
			}

#ifdef _MANAGE_ALLOCATED_MEMORY
			DEBUG_ADD_NODE(p, __file__, __line__, __function__);
#endif
		}
		else
		{
			// if fails, we shall insert the old node to list again.
			// the old ptr is not changed when realloc fails
#ifdef _MANAGE_ALLOCATED_MEMORY
			DEBUG_ADD_NODE(pOldNode, pOldNode->pszFile, pOldNode->nLine,
				pOldNode->pszFunction);
#endif

			printf( "[MemMgr] -- RENEW %p failed at File %s:%d:%s L=%d.\n",
				pOldNode, __file__, __line__, __function__, (int)dwNewSize);
			fflush(stdout);
		}
    }
	else
	{
		p = NULL;

		printf( "[MemMgr] -- ERROR: RENEW unknown(NEW?) memory %p "
			"at %s:%d:%s.\n", 
			pOldPtr, __file__, __line__, __function__);
		fflush(stdout);
	}

#ifdef _MULTI_THREAD_SAFE_NEW
	UNLOCK_MEM_SYNC();
#endif

    return (p != NULL) ? (void *)(p->pBuffer) : NULL;
}


void DEBUG_DELETE( void *p1, char *__file__, int __line__,
				  char * __function__)
{
    if( p1 != NULL )
    {
        _MEMORY_NODE *p;
            
#ifdef _MULTI_THREAD_SAFE_NEW
		LOCK_MEM_SYNC();
#endif
		p = DEBUG_MEMORY_MANAGER_GET_NODE("DELETE", p1, __file__, __line__,
			__function__);
		if (p != NULL)
		{
			if (_MEM_MGR_SHOW_NEW_MSG)
			{
				printf("[MemMgr] -- DEL %d(%p),ID=%d:" \
					" NEW: %s:%d:%s, L=%d DEL: %s:%d:%s.\n",
					g_nMemoryBlockCount,
					p->pBuffer,	p->nId, 
					p->pszFile, p->nLine, p->pszFunction,
					(int)p->ulSize,
					__file__, __line__, __function__);
				fflush(stdout);
			}

			p->ulFlag      = 0;
			p->ulBeginFlag = 0;

			g_nMemoryBlockCount -= 1;
			g_ulMemoryByteCount -= p->ulSize;

#ifdef _MANAGE_ALLOCATED_MEMORY
			DEBUG_DEL_NODE(p);
#endif
			new_free( (void*)p );
        }
        else
        {
			printf("[MemMgr] -- WARNING: Unknown(NEW?) memory %p will be "
				"freed at %s:%d:%s.\n", 
                p1, __file__, __line__, __function__);
            fflush(stdout);

            new_free( p1 );
        }
    
#ifdef _MULTI_THREAD_SAFE_NEW
		UNLOCK_MEM_SYNC();
#endif	
	}
}

#endif	// ifdef _DEBUG

