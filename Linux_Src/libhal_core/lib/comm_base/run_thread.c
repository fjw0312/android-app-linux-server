/*==========================================================================*
 *  FILENAME : run_thread.c
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#define _GNU_SOURCE	1

#include <signal.h>
#include <setjmp.h>

#ifdef MGRID_ANDROID
#include <pthread.h>
#endif 
#include <pthread.h>

#include "stdsys.h"
#include "basetypes.h"
#include "new.h"
#include "err_code.h"
#include "pubfunc.h"
#include "run_queue.h"
#include "run_thread.h"

#ifdef MGRID_ANDROID
struct sigaction g_sigactions;

void thread_exit_handler (int signo)
{
	TRACE("MGRIDINF: thread exit [%d]\n", pthread_self ());
  pthread_exit(0);
}
#endif


#ifdef _DEBUG
//#define _DEBUG_RUN_THREAD	1
#endif //_DEBUG

// the thread entry of mamanger can be enlarged dynamically
#define _RUN_THREAD_DYNAMIC_ENTRY		1
#define NAME_THREAD_MANAGER		"Thread Manager"

// Define it to treat the manager as special thread, not a run thread.
#define THREAD_MANAGER_AS_SPECIAL	1

// total created threads to be used as a magic code in making thread id
static int s_nTotalCreatedThreads = 0;

// (x|1)<<16 to make sure NULL(0) will not be returned.
#define MAKE_THREAD_ID(idx)		((((++s_nTotalCreatedThreads)|1)<<16) \
									+(short)(idx))
#define GET_THREAD_INDEX(id)	((int)(short)((int)id))

struct _RUN_THREAD_ENTRY	//	The thread info
{				
	DWORD		dwThreadId;		// hiword is a magic word, 
								// low word is the index in thread manager

	char		szThreadName [LEN_RUN_THREAD_NAME];

	pthread_t	hSystemThread;	//	The ID of phread in Linux OS

	HANDLE	hMsgQueue;			//	the msg queue of the thread
	DWORD	volatile dwThreadHeartbeat;	//	current thread heartbeat count

	int	volatile nStatus;			// The running status.
};

typedef struct _RUN_THREAD_ENTRY	RUN_THREAD_ENTRY;

#define MAX_THREAD_ENTRY		256	// The maximum thread can be created.
#define THREAD_ENTRY_INC_STEP	32	// The step to increase the pThreadEntries

#ifdef _RUN_THREAD_DYNAMIC_ENTRY
#define THREAD_INIT_ENTRY		THREAD_ENTRY_INC_STEP
#else
#define THREAD_INIT_ENTRY		MAX_THREAD_ENTRY
#endif


struct _RUN_THREAD_MANAGER	//	The run-thread manager
{				
	int	  	 	            nThreadEntries;		//max threads can be created
	RUN_THREAD_ENTRY		*pThreadEntries;	//entries to save created thread
	int	volatile		    nRunningThreads;	//the num of running threads

	RUN_THREAD_EVENT_HANDLER pfnEventHandler;	//handler to process thread event

	pthread_mutex_t			hSyncLock;			//lock to protect the entry
	int						nLastEmptyEntry;	//the last release entry idx

#ifdef THREAD_MANAGER_AS_SPECIAL
	pthread_t				hManagerThread;		//thread id of the manager
	int volatile			nStatus;			//status of the manager.
#else
	HANDLE					hManagerThread;		//thread id of the manager
#endif
};

typedef struct _RUN_THREAD_MANAGER	RUN_THREAD_MANAGER;



static RUN_THREAD_MANAGER	s_runMgr =
{
	0,							//nThreadEntries
	NULL,						//pThreadEntries
	0,							//nRunningThreads
	NULL,						//pfnEventHandler
//	PTHREAD_MUTEX_INITIALIZER,  //mutex
#ifndef MGRID_ANDROID
	PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP,// can be locked recursively
#else
	PTHREAD_RECURSIVE_MUTEX_INITIALIZER,// can be locked recursively
#endif // MGRID_ANDROID
	0,							//nLastEmptyEntry
	0,							//hManagerThread
#ifdef THREAD_MANAGER_AS_SPECIAL
	RUN_THREAD_IS_INVALID		//nStatus:
#endif
};

#define PMGR_SYNC_LOCK			&s_runMgr.hSyncLock
//#ifdef _DEBUG_RUN_THREAD
//#define LOCK_THREAD_MANAGER()   TRACE("%p: [%s] try to lock.\n", (HANDLE)pthread_self(), __FUNCTION__), pthread_mutex_lock(PMGR_SYNC_LOCK),TRACE("%p: [%s] has gotten lock.\n", (HANDLE)pthread_self(), __FUNCTION__)
//#define UNLOCK_THREAD_MANAGER() TRACE("%p: [%s] try to unlock.\n",(HANDLE)pthread_self(), __FUNCTION__), pthread_mutex_unlock(PMGR_SYNC_LOCK)
//#else
#define LOCK_THREAD_MANAGER()   pthread_mutex_lock(PMGR_SYNC_LOCK)
#define UNLOCK_THREAD_MANAGER() pthread_mutex_unlock(PMGR_SYNC_LOCK)
//#endif

#define DESTROY_THREAD_MANAGER() pthread_mutex_destroy(PMGR_SYNC_LOCK)

struct _RUN_THREAD_START_ARG
{
#ifdef _DEBUG_RUN_THREAD
	RUN_THREAD_ENTRY	*pEntry;		// thread entry in manager
#endif //_DEBUG_RUN_THREAD
	HANDLE				hThread;		// thread handle

	RUN_THREAD_START_PROC pfnThreadProc;// start proc
	void				*pThreadArg;	// start arg

	DWORD				*pdwExitCode;	// exit code ptr.

	pthread_mutex_t		hSyncLock;		// sync lock for creating thread
};
typedef struct _RUN_THREAD_START_ARG	RUN_THREAD_START_ARG;

#define LOCK_THREAD_SYNC(p)       pthread_mutex_lock(&((p)->hSyncLock))
#define UNLOCK_THREAD_SYNC(p)     pthread_mutex_unlock(&((p)->hSyncLock))
#define DESTROY_THREAD_SYNC(p)    pthread_mutex_destroy(&((p)->hSyncLock))


#define RUN_THREAD_IS_VALID(pEntry)	((pEntry)->dwThreadId != 0)


static RUN_THREAD_ENTRY *RunThread_GetEntryById(IN HANDLE hThread);
static int RunThread_CancelThreads(IN OUT RUN_THREAD_ENTRY *pEntry,
							IN int nEntry);

/*==========================================================================*
 * FUNCTION : RunThread_EnlargeEntries
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nEntryToAdd : 
 * RETURN   : static BOOL : 
 * COMMENTS : The thread manager must be locked before calling this
 *==========================================================================*/
static BOOL RunThread_EnlargeEntries(IN int nEntryToAdd)
{
	RUN_THREAD_ENTRY	*pNewEntries;
	int					nNewEntries;

	nNewEntries = s_runMgr.nThreadEntries+nEntryToAdd;

	// to enlarge the entry buffer
	pNewEntries = RENEW(RUN_THREAD_ENTRY, 
		s_runMgr.pThreadEntries, nNewEntries);
	if (pNewEntries == NULL)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_EnlargeEntries] -- "
			"out of memory on enlarging thread entries to %d.\n",
			nNewEntries);
#endif //_DEBUG_RUN_THREAD

		return FALSE;
	}

	ZERO_POBJS(&pNewEntries[s_runMgr.nThreadEntries], 
		nNewEntries);	// clean the new allocated buffer

	s_runMgr.pThreadEntries = pNewEntries;	// set new entry ptr.
	s_runMgr.nThreadEntries = nNewEntries;

	return TRUE;
}

/*==========================================================================*
 * FUNCTION : RunThread_GetEmptyEntry
 * PURPOSE  : get an empty thread entry from manager
 * CALLS    : 
 * CALLED BY: RunThread_Create
 * RETURN   : static int : -1: for fail. other is the index of the entry.
 * COMMENTS : The thread manager must be locked before calling this function
 *==========================================================================*/
static int RunThread_GetEmptyEntry(void)
{
	RUN_THREAD_ENTRY *pEntry;
	int i;

	// test the entries is large enough or not,
	// if not enough, to enlarge the thread entry buffer.
	if (s_runMgr.nRunningThreads >= s_runMgr.nThreadEntries)
	{
#ifdef _RUN_THREAD_DYNAMIC_ENTRY
		if (!RunThread_EnlargeEntries(THREAD_ENTRY_INC_STEP))
		{
			return -1;
		}
#else
		if ((s_runMgr.nThreadEntries != 0) ||				// alread init'd
			!RunThread_EnlargeEntries(THREAD_INIT_ENTRY))	// or init fail.
		{
			return -1;
		}
#endif //_RUN_THREAD_DYNAMIC_ENTRY	
	}

	// to search a empty entry
	if (s_runMgr.nLastEmptyEntry >=  s_runMgr.nThreadEntries)
	{
		s_runMgr.nLastEmptyEntry = 0;
	}

	i = s_runMgr.nLastEmptyEntry;
	pEntry = &s_runMgr.pThreadEntries[i];

	// we always can find a empty entry.
	while (RUN_THREAD_IS_VALID(pEntry))
	{
		i++;	// move to the next entry
		pEntry++;

		if (i >= s_runMgr.nThreadEntries)
		{
			// reach end of the last entry, to the first entry. 
			i = 0;
			pEntry = &s_runMgr.pThreadEntries[i];
		}
	}

	// we simply consider the next entry is free.
	s_runMgr.nLastEmptyEntry = i+1;
	s_runMgr.nRunningThreads++;	// increase the running thread num

#ifdef _DEBUG_RUN_THREAD
	TRACEX("Current running threa num is %d.\n", s_runMgr.nRunningThreads);
#endif //_DEBUG_RUN_THREAD

	return i;
}


/*==========================================================================*
 * FUNCTION : RunThread_ReleaseEntry
 * PURPOSE  : Release a unused entry, to destroy the msg queue and set dwThreadId
 *            to 0.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: RUN_THREAD_ENTRY  *pEntry : 
 * RETURN   : static void : 
 * COMMENTS : must lock the manager before calling this function.
 *==========================================================================*/
static void RunThread_ReleaseEntry(IN OUT RUN_THREAD_ENTRY *pEntry)
{
	if (pEntry == NULL)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_ReleaseEntry] -- Try to release a NULL entry.\n");
#endif //_DEBUG_RUN_THREAD

		return;
	}

	// to release the queue.
	if (pEntry->hMsgQueue != NULL)
	{
		Queue_Destroy(pEntry->hMsgQueue);
		pEntry->hMsgQueue = NULL;
	}

	pEntry->nStatus = RUN_THREAD_IS_INVALID;

	s_runMgr.nLastEmptyEntry = GET_THREAD_INDEX(pEntry->dwThreadId);
	s_runMgr.nRunningThreads--;		// the running thread num

#ifdef _DEBUG_RUN_THREAD
	TRACEX("Current running threa num is %d.\n", s_runMgr.nRunningThreads);
#endif //_DEBUG_RUN_THREAD

	pEntry->dwThreadId = 0;			// release it
}


/*==========================================================================*
 * FUNCTION : RunThread_InitThread
 * PURPOSE  : Init the thread arg
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: const char            *pszThreadName :
 *            RUN_THREAD_START_PROC  pfnThreadProc  : 
 *            void                   *pThreadArg    : 
 *            DWORD                  *pdwExitCode   : 
 *            DWORD			  		 dwCreateFlag   : create flags
 * RETURN   : static RUN_THREAD_START_ARG *: NULL for failure
 * COMMENTS : 
 *==========================================================================*/
static RUN_THREAD_START_ARG *RunThread_InitThread( 
						IN const char *pszThreadName, 
						IN RUN_THREAD_START_PROC pfnThreadProc,
						IN void *pThreadArg,
						OUT DWORD *pdwExitCode,
						IN DWORD dwCreateFlag)
{
	int					 nEntryId;
	RUN_THREAD_START_ARG *pArg;
	RUN_THREAD_ENTRY	 *pEntry;
	pthread_mutex_t		 hSyncLockInit = PTHREAD_MUTEX_INITIALIZER;

	if (pfnThreadProc == NULL)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_InitThread] -- The thread proc can NOT be NULL!.\n");
#endif //_DEBUG_RUN_THREAD

		return NULL;
	}

	//1. to get a empty thread entry
	nEntryId = RunThread_GetEmptyEntry();
	if (nEntryId < 0)	// -1 for error.
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_InitThread] -- No empty entry to create thread %s.\n",
			pszThreadName);
#endif //_DEBUG_RUN_THREAD

		return NULL;
	}

	// init thread startup arg
	pArg = NEW(RUN_THREAD_START_ARG, 1);
	if (pArg == NULL)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_InitThread] -- Out of memory on creating"
			"thread %s.\n",	pszThreadName);
#endif //_DEBUG_RUN_THREAD

		return NULL;
	}

	// set the parameters of the run thread entry
	pEntry = &s_runMgr.pThreadEntries[nEntryId];

	// create msg queue
	if (RUN_THREAD_FLAG_HAS(dwCreateFlag, RUN_THREAD_FLAG_HAS_MSG))
	{
		pEntry->hMsgQueue = Queue_Create(
			RUN_THREAD_MAX_MSG,
			sizeof(RUN_THREAD_MSG),
			0);	// don't increase it.
		if (pEntry->hMsgQueue == NULL)
		{
#ifdef _DEBUG_RUN_THREAD
			TRACE("[RunThread_InitThread] -- Out of memory on creating"
				"thread %s.\n",	pszThreadName);
#endif //_DEBUG_RUN_THREAD

			DELETE(pArg);	// delete the allocated memory
			return NULL;
		}
	}
	else
	{
		pEntry->hMsgQueue = NULL;
	}

	if ((pszThreadName == NULL) || (*pszThreadName == '\0'))
	{
		pszThreadName = "__NoName__";

		TRACEX("Warning! Create thread without a name!\n");
	}

	strncpyz(pEntry->szThreadName, pszThreadName, 
		sizeof(pEntry->szThreadName));

	// give 2 heartbeat.
	pEntry->dwThreadHeartbeat = RUN_THREAD_INIT_HEARTBEATS;
	pEntry->dwThreadId = MAKE_THREAD_ID(nEntryId);
	pEntry->nStatus    = RUN_THREAD_IS_RUNNING;

	// init the startup arg
	pArg->hSyncLock     = hSyncLockInit;

#ifdef _DEBUG_RUN_THREAD
	pArg->pEntry		= pEntry;
#endif //_DEBUG_RUN_THREAD
	pArg->hThread       = (HANDLE)pEntry->dwThreadId;
	pArg->pdwExitCode   = pdwExitCode;
	pArg->pfnThreadProc = pfnThreadProc;
	pArg->pThreadArg    = pThreadArg;

	return pArg;
}


#define _RUN_THREAD_SAFE_SET_EXIT_CODE	1

#ifdef _RUN_THREAD_SAFE_SET_EXIT_CODE
static jmp_buf s_jumpBufSIGSEGV;

static void ExitCodeHandler_SIGSEGV(int n)
{
	n = n;
	longjmp(s_jumpBufSIGSEGV, 1);
}

// to set the exit code. must lock the manager at first!
static void RunThread_SetExitCode(IN HANDLE hSelf, DWORD dwExitCode,
								  DWORD *pdwExitCode)
{
	__sighandler_t s_pfnOldSIGSEGV = NULL;

	// avoid the pdwExitCode is invalid to cause SEGMENT fault.
	/* set up to catch the SIGSEGV signal */
	s_pfnOldSIGSEGV = signal( SIGSEGV, ExitCodeHandler_SIGSEGV );

	// set the on error return position.
	if(setjmp(s_jumpBufSIGSEGV) == 0)	// return 0 after init jmp buffer
	{
		*pdwExitCode = dwExitCode;	// if cause segment fault, trigger SIGSEGV
	}
	else	// setjmp returns non-0 if longjmp is called, error has happened.
	{
		TRACEX("Panic! the exit code addr %p of %p is invalid!\n",
			pdwExitCode, hSelf);
	}

	// restore old handler.
	signal( SIGSEGV, s_pfnOldSIGSEGV );
}

#else
#define RunThread_SetExitCode(hSelf, dwExitCode, pdwExitCode) \
			*(pdwExitCode) = (dwExitCode)
#endif

/*==========================================================================*
 * FUNCTION : RunThread_HookEntry
 * PURPOSE  : The run thread hook proc.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: RUN_THREAD_START_ARG  *pArg : 
 * RETURN   : static void *: 
 * COMMENTS : 
 *==========================================================================*/
static void *RunThread_HookEntry(IN RUN_THREAD_START_ARG *pArg)
{
	RUN_THREAD_START_PROC pfnThreadProc = pArg->pfnThreadProc;// start proc
	void	  *pThreadArg   = pArg->pThreadArg;	// start arg
	DWORD	  *pdwExitCode  = pArg->pdwExitCode;// exit code ptr.
	HANDLE	  hSelf			= pArg->hThread;
	DWORD	  dwExitCode;

#ifdef _DEBUG_RUN_THREAD
	char		szThreadName [LEN_RUN_THREAD_NAME];

	strncpyz(szThreadName, pArg->pEntry->szThreadName, 
		sizeof(szThreadName));

	TRACEX("Thread %s(ThreadID=%p,ProcessID=%d) started. Now total threads is %d\n",
		szThreadName, hSelf, getpid(), s_runMgr.nRunningThreads);
#endif //_DEBUG_RUN_THREAD

#ifndef MGRID_ANDROID
	// set the thread is cancelable
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
	
	//set the thread can be canceled immediately
	pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL);
#endif

    // Sync. do NOT continue, till creator work done.
    LOCK_THREAD_SYNC(pArg);

	DESTROY_THREAD_SYNC(pArg);  // remove lock, it's no use.
	DELETE(pArg);				// delete the memory.

	// to run the thread routine now.
	dwExitCode = pfnThreadProc(pThreadArg);

	// the thread run done, release the entry to free.
	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	if (pdwExitCode != NULL)	// save the exit code.
	{
		RunThread_SetExitCode(hSelf, dwExitCode, pdwExitCode);
	}

	RunThread_ReleaseEntry(&s_runMgr.pThreadEntries[GET_THREAD_INDEX(hSelf)]);

	UNLOCK_THREAD_MANAGER();

#ifdef _DEBUG_RUN_THREAD
	TRACEX("Thread %s(%p) stopped. Now total threads is %d\n",
		szThreadName, hSelf, s_runMgr.nRunningThreads);
#endif //_DEBUG_RUN_THREAD

	return (void *)dwExitCode;
}


/*==========================================================================*
 * FUNCTION : RunThread_Create
 * PURPOSE  : Create a detached thread.
 * CALLS    : pthread API
 * CALLED BY: 
 * ARGUMENTS: const char             *pszThreadName : the thread name.
 *            RUN_THREAD_START_PROC  pfnThreadProc  : the main proc of thread
 *            void                   *pThreadArg    : arg of the thread
 *            DWORD                  *pdwExitCode   : ptr to save exit code.
 *                                                    can be null. if not null,
 *                                                    the addr must be keep 
 *                                                    valid when the thread is
 *                                                    running.
 *            DWORD					dwCreateFlag    : create flags
 * RETURN   : HANDLE : NULL for failure, else is the HANDLE of the thread.
 * COMMENTS : 
 *==========================================================================*/
HANDLE RunThread_Create(IN const char *pszThreadName, 
						IN RUN_THREAD_START_PROC pfnThreadProc,
						IN void *pThreadArg,
						IN DWORD *pdwExitCode,
						IN DWORD dwCreateFlag)
{
	HANDLE					hThread = NULL;
	RUN_THREAD_START_ARG	*pArg;

#ifdef _DEBUG_RUN_THREAD
	TRACE("[RunThread_Create] -- Creating thread %s...\n",
		pszThreadName);
#endif //_DEBUG_RUN_THREAD

	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	//1. get a empty thread entry and init the thread startup argument
	pArg = RunThread_InitThread(pszThreadName, 
		pfnThreadProc,
		pThreadArg,
		pdwExitCode,
		dwCreateFlag);

	if (pArg != NULL)
	{
//		RUN_THREAD_ENTRY *pEntry = pArg->pEntry;
		RUN_THREAD_ENTRY *pEntry;
		
		pEntry = &s_runMgr.pThreadEntries[GET_THREAD_INDEX(pArg->hThread)];

		// lock the sync lock at first.
		LOCK_THREAD_SYNC(pArg);

		// to create the thread
		if (pthread_create( &pEntry->hSystemThread, 
            NULL,//&attr
			(PTHREAD_START_ROUTINE)RunThread_HookEntry,
			(void *)pArg) == 0)
		{
			hThread = (HANDLE)pEntry->dwThreadId;

			// create the thread OK. detach the thread
			pthread_detach(pEntry->hSystemThread);

			UNLOCK_THREAD_SYNC(pArg); // sync. let thread to go.

			// the pArg will be deleted in the thread just created,
			// do NOT use pArg any more!
		}
		else
		{
#ifdef _DEBUG_RUN_THREAD
			TRACE("[RunThread_Create] -- Fails on creating thread %s.\n",
				pszThreadName);
#endif //_DEBUG_RUN_THREAD

			// release the allocated entry to free
			RunThread_ReleaseEntry(pEntry);

			DESTROY_THREAD_SYNC(pArg);  // remove lock, it's no use.
			DELETE(pArg);
        }
	}

	UNLOCK_THREAD_MANAGER();

	return hThread;
}


/*==========================================================================*
 * FUNCTION : RunThread_GetEntryById
 * PURPOSE  : Get the thread entry according to the thread handle.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hThread : 
 * RETURN   : static RUN_THREAD_ENTRY *: 
 * COMMENTS : 
 *==========================================================================*/
static RUN_THREAD_ENTRY *RunThread_GetEntryById(IN HANDLE hThread)
{
	RUN_THREAD_ENTRY *pEntry;
	int				 nEntryIndex;

	if (hThread == NULL)
	{
		return NULL;
	}

	nEntryIndex = GET_THREAD_INDEX(hThread);
	if ((nEntryIndex < 0) || (nEntryIndex >= s_runMgr.nThreadEntries))
	{
		return NULL;
	}

	pEntry = &s_runMgr.pThreadEntries[nEntryIndex];

	if (pEntry->dwThreadId != (DWORD)hThread)
	{
		//it is an invalid thread id.
		return NULL;
	}

	return pEntry;
}


/*==========================================================================*
 * FUNCTION : RunThread_InternalPostMessage
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN OUT RUN_THREAD_ENTRY  *pEntry : 
 *            IN                       nEntry  : 
 *            RUN_THREAD_MSG           *pMsg   : 
 *            BOOL                     bUrgent : 
 * RETURN   : static int : 
 * COMMENTS : 
 *==========================================================================*/
static int RunThread_InternalPostMessage(IN OUT RUN_THREAD_ENTRY *pEntry,
										IN int nEntry,
										RUN_THREAD_MSG *pMsg,
										BOOL bUrgent)
{
	int nResult = ERR_THREAD_OK;

	// send quit message.
	for (; nEntry > 0; nEntry--, pEntry++)
	{
		if (RUN_THREAD_IS_VALID(pEntry))
		{
			if (pMsg->dwMsg == MSG_QUIT)
			{
#ifdef _DEBUG_RUN_THREAD
				TRACEX("Thread %s(%08x) is recved a quit msg...\n",
					pEntry->szThreadName, (int)pEntry->dwThreadId);
#endif
				pEntry->nStatus = RUN_THREAD_TO_QUIT;
			}

			if ((pEntry->hMsgQueue == NULL)
				|| (Queue_Put(pEntry->hMsgQueue, (void *)pMsg, bUrgent) 
						!= ERR_QUEUE_OK))
			{
				nResult = ERR_THREAD_MSG_FULL;
			}
		}
	}

	return nResult;
}

/*==========================================================================*
 * FUNCTION : RunThread_ProcessThreadEvent
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: DWORD              dwEvent  : 
 *            RUN_THREAD_ENTRY  *pEntry : 
 * RETURN   : static int : 
 * COMMENTS : 
 *==========================================================================*/
static int RunThread_ProcessThreadEvent(IN DWORD dwEvent, 
										IN OUT RUN_THREAD_ENTRY *pEntry)
{
	int rc;
	int nEntry;
//	RUN_THREAD_MSG msg;

	rc = s_runMgr.pfnEventHandler(dwEvent, 
		(HANDLE)pEntry->dwThreadId,	pEntry->szThreadName);

	switch (rc)
	{
	case THREAD_CONTINUE_RUN:
		return rc;

	case THREAD_CANCEL_THIS:
		{
			nEntry = 1;
		}
		break;

	case THREAD_CANCEL_ALL:
		{
			pEntry = s_runMgr.pThreadEntries;
			nEntry = s_runMgr.nThreadEntries;
		}
		break;

	default:
		return rc;
	}

	//to KILL the threads
	RunThread_CancelThreads(pEntry, nEntry);

	return rc;
}


/*==========================================================================*
 * FUNCTION : RunThread_Heartbeat
 * PURPOSE  : To trigger a time of heartbeat. a thread shall call this func
 *            periodly to indicate that is running.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hThread : 
 * RETURN   : int : the current heartbeat count
 * COMMENTS : 
 *==========================================================================*/
int RunThread_Heartbeat(IN HANDLE hThread)
{
	int					nBeatCount = 0;
	RUN_THREAD_ENTRY	*pEntry;
	int					nCurStatus;

	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	// test the thread id is valid or not
	pEntry = RunThread_GetEntryById(hThread);

	if (pEntry != NULL)
	{
#ifdef _DEBUG_RUN_THREAD
//		TRACE("[RunThread_Heartbeat] -- Thread %s(%p) is heartbeating.\n",
//			pEntry->szThreadName, (HANDLE)pEntry->dwThreadId);
#endif //_DEBUG_RUN_THREAD

		// increase the thread heartbeat count
		pEntry->dwThreadHeartbeat++;
	
		nBeatCount = pEntry->dwThreadHeartbeat;

//		not need call thread event handler.
//		RunThread_ProcessThreadEvent(THREAD_EVENT_HEARTBEAT, pEntry);
		nCurStatus = pEntry->nStatus;
	}
	else
	{
		nCurStatus = RUN_THREAD_IS_RUNNING;
	}

	UNLOCK_THREAD_MANAGER();

#ifndef MGRID_ANDROID
	if (nCurStatus == RUN_THREAD_TO_QUIT)
	{
		pthread_testcancel();	// test the cancel state....
	}
#endif

	return nBeatCount;
}


/*==========================================================================*
 * FUNCTION : RunThread_GetId
 * PURPOSE  : get the run thread id by name, if name is NULL will get the id
 *            of this caller thread
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: const char  *pszThreadName : name of thread, 
 *                                         NULL for the caller itself.
 * RETURN   : HANDLE : 
 * COMMENTS : 
 *==========================================================================*/
HANDLE RunThread_GetId(IN const char *pszThreadName)
{
	int				 i;
	RUN_THREAD_ENTRY *p;
	HANDLE			 hThread = NULL;
    
	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	p = s_runMgr.pThreadEntries;

	if (pszThreadName == NULL)	// get the ID of the caller.
	{
		pthread_t hSelf = pthread_self();

		for (i = 0; i < s_runMgr.nThreadEntries; i++, p++)
		{
			if (RUN_THREAD_IS_VALID(p) &&
				(pthread_equal(hSelf, p->hSystemThread) != 0)) // equal
			{
				hThread = (HANDLE)p->dwThreadId;
				break;
			}
		}
	}

	else
	{
		for (i = 0; i < s_runMgr.nThreadEntries; i++, p++)
		{
			if (strcmp(p->szThreadName, pszThreadName) == 0)
			{
				hThread = (HANDLE)p->dwThreadId;
				break;
			}
		}
	}

	UNLOCK_THREAD_MANAGER();

	return hThread;
}


/*==========================================================================*
 * FUNCTION : RunThread_GetName
 * PURPOSE  : get the thread name by ID, if ID is NULL, to get the self-name.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hThread : NULL to get self name.
 *            OUT char   *pszName : the buffer to save name, the size must be
 *                                  greater than LEN_RUN_THREAD_NAME(16).
 *                                  NOTE: 
 *									if pszName is NULL, this is unsafe unsage.
 *                                  the thread name will be returned. 
 *            IN  int    nNameLen : the  buffer len in byte of pszName.
 * RETURN   : char *: return pszName. empty string("", not NULL) on failure.
 * COMMENTS : 
 *==========================================================================*/
char *RunThread_GetName(IN HANDLE hThread, OUT char *pszName, IN int nNameLen)
{
	RUN_THREAD_ENTRY *pEntry;

	if (hThread == NULL)
	{
		// get self-name, this call must be located outsize LOCK/UNLOCK.
		hThread = RunThread_GetId(NULL);
	}

	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	pEntry = RunThread_GetEntryById(hThread);
	if (pEntry != NULL)
	{
		if (pszName != NULL)
		{
			strncpyz(pszName, (const char *)pEntry->szThreadName, nNameLen);
		}
		else
		{
			pszName = pEntry->szThreadName;
		}
	}
	else	// not found the thread.
	{
		if (pszName != NULL)
		{
			pszName[0] = 0;
		}
		else
		{
			pszName = "";
		}
	}

	UNLOCK_THREAD_MANAGER();

	return pszName;
}


/*==========================================================================*
 * FUNCTION : RunThread_GetMessage
 * PURPOSE  : get a msg from run thread
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE          hThread : 
 *            RUN_THREAD_MSG  *pMsg   : 
 *            BOOL            bPeek   : TRUE is peek only
 *			  DWORD			  dwTimeout: if no msg, the time in ms will wait. 
 * RETURN   : int :ERR_OK for get a message, 
 *                 ERR_THREAD_INVALID_HANDLE for an invalid hThread passed in.
 *                 ERR_THREAD_MSG_EMPTY for no msg.
 * COMMENTS : 
 *==========================================================================*/
int RunThread_GetMessage(IN HANDLE hThread,
						 OUT RUN_THREAD_MSG *pMsg, 
						 IN BOOL bPeek,
						 IN DWORD dwTimeout)
{
	int	nResult = ERR_THREAD_MSG_EMPTY;
	RUN_THREAD_ENTRY *pEntry;
	HANDLE	hMsgQueue;

#ifdef _DEBUG_RUN_THREAD
	//TRACE("[RunThread_GetMessage] -- %p is getting msg.\n", hThread);
#endif //_DEBUG_RUN_THREAD

	// It will cause a temp deadlock if a thread try to wait and get msg from
	// its msg queue after the manager has been locked by it.
	// In this case, other thread can NOT access the manager because the mgr
	// is already locked by this thread, but the other thread has to wait...

	// 1. get the msg queue.
	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	// test the thread id is valid or not
	pEntry = RunThread_GetEntryById(hThread);
	if (pEntry != NULL)
	{
		hMsgQueue = pEntry->hMsgQueue;
	}
	else
	{
		hMsgQueue = NULL;
		nResult   = ERR_THREAD_INVALID_HANDLE;
	}

	// release the manager at once.
	UNLOCK_THREAD_MANAGER();

	//2. try to get the msg.
	if ((hMsgQueue != NULL) 
		&& (Queue_Get(hMsgQueue, (void *)pMsg, bPeek, dwTimeout)
				== ERR_QUEUE_OK))
	{
		nResult = ERR_THREAD_OK;
	}

	return nResult;
}


/*==========================================================================*
 * FUNCTION : RunThread_PostMessage
 * PURPOSE  : put a message to the given thread.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE          hThread : -1: send to all thread.
 *            RUN_THREAD_MSG  *pMsg   : 
 *            BOOL            bUrgent : TRUE if the msg is urgent.
 * RETURN   : int :ERR_OK for putting a message ok, 
 *                 ERR_THREAD_INVALID_HANDLE for an invalid hThread passed in.
 *                 ERR_THREAD_MSG_FULL for the msg queue is full.
 * COMMENTS : 
 *==========================================================================*/
int RunThread_PostMessage(IN HANDLE hThread,
						  IN RUN_THREAD_MSG *pMsg, IN BOOL bUrgent)
{
	int	nResult;
	int nEntry;
	RUN_THREAD_ENTRY *pEntry;

#ifdef _DEBUG_RUN_THREAD
	//TRACE("[RunThread_PostMessage] -- post msg(%d,%d,%d) to %p\n",
	//	(int)pMsg->dwMsg, (int)pMsg->dwParam1, (int)pMsg->dwParam2, hThread);
#endif //_DEBUG_RUN_THREAD

	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	// test the thread id is valid or not
	if (hThread != (HANDLE)-1)
	{
		pEntry = RunThread_GetEntryById(hThread);
		nEntry = 1;
	}
	else	// broadcast msg to all thread.
	{
		pEntry = s_runMgr.pThreadEntries;
		nEntry = s_runMgr.nThreadEntries;
	}

	if (pEntry != NULL)
	{
		nResult = RunThread_InternalPostMessage(pEntry, nEntry,
			pMsg, bUrgent);
	}
	else
	{
		nResult = ERR_THREAD_INVALID_HANDLE;
	}

	UNLOCK_THREAD_MANAGER();

	return nResult;
}


/*==========================================================================*
 * FUNCTION : RunThread_PostQuitMessage
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hThread : -1 for all threads
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int RunThread_PostQuitMessage(IN HANDLE hThread)
{
	int nResult;
	RUN_THREAD_MSG	msg;

	// make quit msg.
	RUN_THREAD_MAKE_MSG(&msg, 0, MSG_QUIT, 0, 0);

	// send quit msg to this thread
	nResult = RunThread_PostMessage((HANDLE)hThread, &msg, FALSE);

	return nResult;
}


/*==========================================================================*
 * FUNCTION : RunThread_GetStatus
 * PURPOSE  : get the running state of a thread
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hThread : 
 * RETURN   : int : RUN_THREAD_IS_RUNNING if running, else 
 *                  RUN_THREAD_IS_INVALID for invalid thread(or not running)
 *                  RUN_THREAD_TO_QUIT for the thread need to quit.
 * COMMENTS : 
 *==========================================================================*/
int RunThread_GetStatus(IN HANDLE hThread)
{
	RUN_THREAD_ENTRY *pEntry;

	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	// test the thread id is valid or not
	pEntry = RunThread_GetEntryById(hThread);

	UNLOCK_THREAD_MANAGER();
	return (pEntry != NULL) ? pEntry->nStatus : RUN_THREAD_IS_INVALID;
}


/*==========================================================================*
 * FUNCTION : RunThread_GetThreadCount
 * PURPOSE  : get the count of the current running thread
 * CALLS    : 
 * CALLED BY: 
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int RunThread_GetThreadCount(void)
{
	int n;

	LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

	n = s_runMgr.nRunningThreads;

	UNLOCK_THREAD_MANAGER();

	return n;
}


/*==========================================================================*
 * FUNCTION : RunThread_CancelThreads
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN OUT RUN_THREAD_ENTRY  *pEntry : 
 *            IN int                   nEntry  : 
 * RETURN   : static int :
 * COMMENTS :  the manager shall be locked at first!
 *==========================================================================*/
static int RunThread_CancelThreads(IN OUT RUN_THREAD_ENTRY *pEntry,
							IN int nEntry)
{
	int nStatus;

	if (pEntry == NULL)
	{
		return ERR_THREAD_KILLED;
	}

#ifdef _DEBUG_RUN_THREAD
	TRACEX("Trying to cancel %d threads...\n", nEntry);
#endif //_DEBUG_RUN_THREAD

	for (; nEntry > 0; nEntry--, pEntry++)
	{
		if (RUN_THREAD_IS_VALID(pEntry))
		{
#ifdef MGRID_ANDROID
      switch( pthread_kill( pEntry->hSystemThread, SIGUSR1))
#else
			switch(pthread_cancel(pEntry->hSystemThread))
#endif
			{
			case 0:		// ok
#ifdef _DEBUG_RUN_THREAD
				TRACE( "[RunThread_CancelThreads] -- Thread %s(%p) is canceled.\n",
					pEntry->szThreadName, (HANDLE)pEntry->dwThreadId);
#endif //_DEBUG_RUN_THREAD
				//break;	//! No break here

			case ESRCH:	// it has stopped
 				RunThread_ReleaseEntry(pEntry);
				nStatus = ERR_THREAD_KILLED;
				break;

			default:
				nStatus = ERR_THREAD_STILL_RUNNING;
			}
		}
	}

	return nStatus;
}

/*==========================================================================*
 * FUNCTION : RunThread_Stop
 * PURPOSE  : To stop  thread.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hThread               : 
 *            int     nTimeToWaitThreadQuit : wait n ms on thread quiting
 *            BOOL    bKillTimeoutedThread  : if timeout, kill the thread
 * RETURN   : int : ERR_THREAD_OK for OK, 
 *                  ERR_THREAD_STILL_RUNNING for thread does not stopped,
 *                  ERR_THREAD_KILLED for thread is terminated by force.
 *==========================================================================*/
int RunThread_Stop(IN HANDLE hThread, IN int nTimeToWaitThreadQuit, 
				   IN BOOL bKillTimeoutedThread)
{
	int				nStatus;
	int				nTimeWaited;
	HANDLE			hKiller = NULL;

	nStatus = RunThread_GetStatus(hThread);

	// the thread is not running. OK.
	if (nStatus == RUN_THREAD_IS_INVALID)
	{
		return ERR_THREAD_OK;
	}
    
	// the thread is running, send it a quit msg
	if (nStatus == RUN_THREAD_IS_RUNNING)
	{
		// send a quit msg to the thread.
		RunThread_PostQuitMessage(hThread);
	}

	if (nTimeToWaitThreadQuit >= MAX_WAIT_INTERVAL*1000)
	{
		hKiller = RunThread_GetId(NULL);
	}

	// to test the thread status again. 
	// if the thread quit done, the RUN_THREAD_IS_INVALID will be returned.
	for (nTimeWaited = 0; ; )
	{
		nStatus = RunThread_GetStatus(hThread);
		if (nStatus == RUN_THREAD_IS_INVALID)
		{
			// thread quit done now.
			return ERR_THREAD_OK;
		}

		if (nTimeWaited >= nTimeToWaitThreadQuit)
		{	
			// do NOT move this condition to for(;;), 
			// or will lose one time to test the Status after Sleep().
			break;	// timeout
		}

		// wait a while.
#define RUN_THREAD_CHECK_STATUS_INTERVAL		50 /* ms */
		Sleep(RUN_THREAD_CHECK_STATUS_INTERVAL);
		nTimeWaited += RUN_THREAD_CHECK_STATUS_INTERVAL;

		// touch heartbeat
		if ((hKiller != NULL) && (nTimeWaited >= MAX_WAIT_INTERVAL*1000))
		{
			RunThread_Heartbeat(hKiller);
		}
	}
				
	// the thread is still running. to kill it!
	nStatus = ERR_THREAD_STILL_RUNNING;
	if (bKillTimeoutedThread)
	{
		LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

		nStatus = RunThread_CancelThreads(RunThread_GetEntryById(hThread), 1);

		UNLOCK_THREAD_MANAGER();
	}

	return nStatus;
}


/*==========================================================================*
 * FUNCTION : RunThread_Manager
 * PURPOSE  : the main proc of run thread manager.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN void  *pArgNoUse : 
 * RETURN   : static DWORD : 
 * COMMENTS : 
 *==========================================================================*/
static DWORD RunThread_Manager(IN void *pArgNoUse )
{
	int t;
	RUN_THREAD_ENTRY *pEntry;
#ifndef THREAD_MANAGER_AS_SPECIAL
	HANDLE			 hMgr;		// the manager itself
#endif

	UNUSED(pArgNoUse);

#ifdef _DEBUG_RUN_THREAD
	TRACE("[RunThread_Manager] -- thread manager started.\n");
#endif //_DEBUG_RUN_THREAD
	
#ifndef MGRID_ANDROID
	// set the thread is cancelable
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
	
	//set the thread can be canceled immediately
	pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL);
#endif

#ifndef THREAD_MANAGER_AS_SPECIAL
	// get the manager self entry.
	hMgr = RunThread_GetId(NULL);
	ASSERT(hMgr);
#endif

#ifdef THREAD_MANAGER_AS_SPECIAL
#define RUN_THREAD_MANAGER_IS_RUNNING() \
			(s_runMgr.nStatus == RUN_THREAD_IS_RUNNING)
#else
#define RUN_THREAD_MANAGER_IS_RUNNING() \
			(RunThread_GetStatus(hMgr) == RUN_THREAD_IS_RUNNING)
#endif

	while (RUN_THREAD_MANAGER_IS_RUNNING())
	{
		t = RUN_THREAD_HEARTBEAT_CHECK_INTERVAL;
		
		// wait for a period before to check heartbeat.
		while ((t > 0) && RUN_THREAD_MANAGER_IS_RUNNING())
		{
#ifndef THREAD_MANAGER_AS_SPECIAL	// shall heartbeat!
			RunThread_Heartbeat(hMgr);	// increase the thread heartbeat
#endif
			s_runMgr.pfnEventHandler(THREAD_EVENT_HEARTBEAT, NULL,
				NAME_THREAD_MANAGER);

			Sleep(RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL);
			t -= RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL;
		}

		// now to check the heartbeat of the threads
		LOCK_THREAD_MANAGER();	// lock the thread manager data structure.

		pEntry = s_runMgr.pThreadEntries;
		for (t = 0; t < s_runMgr.nThreadEntries; t++, pEntry++)
		{
			if (RUN_THREAD_IS_VALID(pEntry) && 
				(pEntry->dwThreadHeartbeat == 0)) // no heartbeat!
			{
#ifdef _DEBUG_RUN_THREAD
				TRACE("[RunThread_Manager] -- Thread %s(%p) is no response.\n",
					pEntry->szThreadName, (HANDLE)pEntry->dwThreadId);
#endif //_DEBUG_RUN_THREAD

				RunThread_ProcessThreadEvent(THREAD_EVENT_NO_RESPONSE, 
					pEntry);
			}

			pEntry->dwThreadHeartbeat = 0;
		}

		UNLOCK_THREAD_MANAGER();
	}

#ifdef _DEBUG_RUN_THREAD
	TRACE("[RunThread_Manager] -- thread manager exited.\n");
#endif //_DEBUG_RUN_THREAD

#ifdef THREAD_MANAGER_AS_SPECIAL
	s_runMgr.nStatus = RUN_THREAD_IS_INVALID;	// quit done.
#endif

	return 0;
}


/*==========================================================================*
 * FUNCTION : RunThread_ManagerInit
 * PURPOSE  : init the thread manager thread.
 * CALLS    : 
 * CALLED BY: main
 * ARGUMENTS: RUN_THREAD_EVENT_HANDLER  *pfnEventHandler : 
 * RETURN   : int : ERR_OK if OK, else ERR_THREAD_INIT_MANAGER for error.
 * COMMENTS : 
 *==========================================================================*/
int RunThread_ManagerInit(IN RUN_THREAD_EVENT_HANDLER pfnEventHandler)
{
#ifdef MGRID_ANDROID
  int rc;
  memset(&g_sigactions, 0, sizeof(g_sigactions));   
  sigemptyset(&g_sigactions.sa_mask);  
  g_sigactions.sa_flags = 0;   
  g_sigactions.sa_handler = thread_exit_handler;  
  if ( (rc=sigaction(SIGUSR1,&g_sigactions,NULL)) != 0)
  {
    TRACE("MGRIDERR: set sigaction failed! threadid:[%d], errno:[%d]\n", 
        pthread_self (), rc);
  }
#endif

#ifdef _DEBUG_RUN_THREAD
	TRACE("[RunThread_ManagerInit] -- Run thread manager is starting...\n");
#endif //_DEBUG_RUN_THREAD

	if (pfnEventHandler == NULL)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_ManagerInit] -- pfnEventHandler is NULL error.\n");
#endif //_DEBUG_RUN_THREAD

		return ERR_THREAD_INIT_MANAGER;
	}

	s_runMgr.pfnEventHandler = pfnEventHandler;

	if (!RunThread_EnlargeEntries(THREAD_INIT_ENTRY))
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_ManagerInit] -- Fails on init %d thread entries.\n",
			THREAD_INIT_ENTRY);
#endif //_DEBUG_RUN_THREAD

		return ERR_THREAD_INIT_MANAGER;
	}

#ifdef THREAD_MANAGER_AS_SPECIAL
	s_runMgr.nStatus = RUN_THREAD_IS_RUNNING;

	// use pthread_create instead of RunThread_Create.
	if (pthread_create( &s_runMgr.hManagerThread, 
		NULL,//&attr
		(PTHREAD_START_ROUTINE)RunThread_Manager,
		(void *)&s_runMgr) != 0)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_ManagerInit] -- Fails on creating thread manager"
			" thread.\n");
#endif //_DEBUG_RUN_THREAD

		s_runMgr.nStatus = RUN_THREAD_IS_INVALID;
		return ERR_THREAD_INIT_MANAGER;
	}

	// create the thread OK. detach the thread
	pthread_detach(s_runMgr.hManagerThread);

#else
	s_runMgr.hManagerThread  = RunThread_Create("THREAD_MGR",
		RunThread_Manager,
		NULL,
		NULL,
		0);		// no flag, no msg queue.
	if (s_runMgr.hManagerThread == NULL)
	{
#ifdef _DEBUG_RUN_THREAD
		TRACE("[RunThread_ManagerInit] -- Fails on creating thread manager"
			" thread.\n");
#endif //_DEBUG_RUN_THREAD

		return ERR_THREAD_INIT_MANAGER;
	}
#endif

	return ERR_THREAD_OK;
}


/*==========================================================================*
 * FUNCTION : RunThread_LogPanicThreads
 * PURPOSE  : display the panic(it's still running)threads info.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: BOOL  bNotify : TRUE to notify event handler
 * RETURN   : static void : 
 * COMMENTS : 
 *==========================================================================*/
static void RunThread_LogPanicThreads(BOOL bNotify)
{
	RUN_THREAD_ENTRY *pEntry;
	int i;

	TRACEX("Panic! There are %d threads are still running before "
		"thread manager exiting...\n",
		s_runMgr.nRunningThreads);

	// this shall never happen!
	pEntry = s_runMgr.pThreadEntries;
	for (i = 0; i < s_runMgr.nThreadEntries; i++, pEntry++)
	{
		if (RUN_THREAD_IS_VALID(pEntry))
		{	
			TRACEX("Panic! The thread %s(%p) is still running.\n",
				pEntry->szThreadName, (HANDLE)pEntry->dwThreadId);

			//	RunThread_ReleaseEntry(pEntry);	// do NOT release!

			if (bNotify)
			{
				s_runMgr.pfnEventHandler(
					THREAD_EVENT_CANNOT_BE_STOPPED,
					(HANDLE)pEntry->dwThreadId,
					pEntry->szThreadName);
			}
		}
	}

	TRACEX("End of showing PANIC threads.\n");
}


/*==========================================================================*
 * FUNCTION : RunThread_WaitThreadsQuit
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: int   nTimeToWaitThreadQuit : 
 * RETURN   : static int : ERR_THREAD_OK for OK, 
 *                  ERR_THREAD_STILL_RUNNING for thread does not stopped,
 *                  ERR_THREAD_OK for all threads have quited.
 * COMMENTS : 
 *==========================================================================*/
static int RunThread_WaitThreadsQuit(IN int nTimeToWaitThreadQuit)
{
	int nTimeWaited = 0;

	// check the running thread in given time.
	while ((s_runMgr.nRunningThreads > 0) 
		&& (nTimeWaited < nTimeToWaitThreadQuit))
	{
		// send manager heartbeat event.
		s_runMgr.pfnEventHandler(THREAD_EVENT_HEARTBEAT, NULL,
			NAME_THREAD_MANAGER);

		Sleep(RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL);

		// increase the waited time.
		nTimeWaited += RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL;
	}

	if (s_runMgr.nRunningThreads > 0)
	{
		return ERR_THREAD_STILL_RUNNING;
	}

	// all threads quit done
	return ERR_THREAD_OK;
}

/*==========================================================================*
 * FUNCTION : RunThread_ManagerExit
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: main
 * ARGUMENTS: int   nTimeToWaitThreadQuit : 
 *            BOOL  bKillTimeoutedThread  : 
 * RETURN   : int : ERR_THREAD_OK for OK, 
 *                  ERR_THREAD_STILL_RUNNING for thread does not stopped,
 *                  ERR_THREAD_KILLED for thread is terminated by force.
 * COMMENTS : 
 *==========================================================================*/
int RunThread_ManagerExit(IN int nTimeToWaitThreadQuit,
						  IN BOOL bKillTimeoutedThread)
{
	int nResult;

#ifdef _DEBUG_RUN_THREAD
	TRACE("[RunThread_ManagerExit] -- Run thread manager is exiting...\n");
#endif //_DEBUG_RUN_THREAD

#ifdef THREAD_MANAGER_AS_SPECIAL
	s_runMgr.nStatus = RUN_THREAD_TO_QUIT;	// to quit manager thread.
#endif

	// stop all running threads
	// -- NOT need, the threads shall quit done before calling RunThread_ManagerExit().
	//	RunThread_PostQuitMessage((HANDLE)-1);

	s_runMgr.pfnEventHandler(THREAD_EVENT_HEARTBEAT, NULL,
		NAME_THREAD_MANAGER);

	// wait threads quit.
	nResult = RunThread_WaitThreadsQuit(nTimeToWaitThreadQuit);
	if (nResult == ERR_THREAD_STILL_RUNNING)
	{
		// to kill the still running threads
		if (bKillTimeoutedThread)
		{
			// before canceling threads, post quit message.
			RunThread_PostQuitMessage((HANDLE)-1);

			// wait 5 seconds again
			nResult = RunThread_WaitThreadsQuit(nTimeToWaitThreadQuit);
			if (nResult == ERR_THREAD_STILL_RUNNING)
			{
				// to kill the still running threads.
				// we don't hope this happens, 
				// it may cause memory leak and other problems
				LOCK_THREAD_MANAGER();
				RunThread_LogPanicThreads(TRUE);

				RunThread_CancelThreads(s_runMgr.pThreadEntries,
					s_runMgr.nThreadEntries);
		
				UNLOCK_THREAD_MANAGER();
			}
		}

		nResult = ERR_THREAD_KILLED;
	}

#ifdef THREAD_MANAGER_AS_SPECIAL
	TRACEX("Waiting for manager to quit...\n");

	// wait for the manager to quit.
	while ((s_runMgr.nStatus != RUN_THREAD_IS_INVALID) &&
		(nTimeToWaitThreadQuit >= 0))	// wait a period at least.
	{
		s_runMgr.pfnEventHandler(THREAD_EVENT_HEARTBEAT, NULL,
			NAME_THREAD_MANAGER);

		Sleep(RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL);
		nTimeToWaitThreadQuit -= RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL;
	}

	// error? cancel it.
	if (s_runMgr.nStatus != RUN_THREAD_IS_INVALID)
	{
		TRACEX("Thread manager is no response. canceled.\n");

#ifdef MGRID_ANDROID
    if ( pthread_kill(s_runMgr.hManagerThread, SIGUSR1) != 0)
    {   
      TRACEX("MGRIDERR: Error cancelling thread [%d], thread not found", 
          s_runMgr.hManagerThread);
    }
#else
		pthread_cancel(s_runMgr.hManagerThread);
#endif
		nResult = ERR_THREAD_EXIT_MANAGER;
	}

	TRACEX("Thread manager quited.\n");
#endif

	// cleanup the data of the thread manager
	DELETE(s_runMgr.pThreadEntries);
	DESTROY_THREAD_MANAGER();

	s_runMgr.nThreadEntries = 0;
	s_runMgr.pThreadEntries = NULL;
	s_runMgr.nRunningThreads= 0;
	s_runMgr.nLastEmptyEntry= 0;
	s_runMgr.hManagerThread = 0;

#ifdef _DEBUG_RUN_THREAD
	TRACE("[RunThread_ManagerExit] -- Run thread manager is exited.\n");
#endif //_DEBUG_RUN_THREAD
    
	return nResult;
}
