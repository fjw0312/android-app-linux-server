/*==========================================================================*
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#define _XOPEN_SOURCE	600

#include "stdsys.h"		/* all standard system head files			*/

#ifndef __USE_XOPEN2K
//#error "__USE_XOPEN2K" shall be defined at first!
#endif

#include <semaphore.h>

#include "basetypes.h"
#include "new.h"
#include "err_code.h"
#include "pubfunc.h"
#include "run_thread.h"
#include "run_mutex.h"

#ifdef _DEBUG
//#define _DEBUG_MUTEX	1
#endif

struct _MUTEX	//	internal structure for mutex
{				
	pthread_mutex_t	hInternalMutex;	//	the mutex lock
//	pthread_cond_t	hInternalCond;	//	the waitable condition
};				

typedef struct _MUTEX	MUTEX;

/*==========================================================================*
 * FUNCTION : Mutex_Create
 * PURPOSE  : Create a mutex
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN BOOL  bOpenedLock : TRUE: the lock is opened, 
 *                                   FALSE: lock will be locked by the creator
 * RETURN   : HANDLE : NULL: failure, others: the handle of the mutex
 * COMMENTS : 
 *==========================================================================*/
HANDLE Mutex_Create(IN BOOL bOpenedLock)
{
	MUTEX			*pMutex = NEW( MUTEX, 1 );
	pthread_mutex_t mut		= PTHREAD_MUTEX_INITIALIZER;

	if (pMutex == NULL)
	{
#ifdef _DEBUG_MUTEX
	TRACE("[Mutex_Create] -- out of memory on new a MUTEX.\n");
#endif //_DEBUG_MUTEX

	return NULL;
	}

	pMutex->hInternalMutex = mut;
	
	// To lock the mutext if want to created a locked mutext
	if (!bOpenedLock)
	{
		Mutex_Lock((HANDLE)pMutex, 0);
	}
	
	return (HANDLE)pMutex;
}

/*==========================================================================*
 * FUNCTION : Mutex_Lock
 * PURPOSE  : try to get a lock
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hm            : 
 *            IN DWORD   dwWaitTimeout : time for waiting lock in ms
 * RETURN   : int : ERR_OK, for OK, ERR_MUTEX_TIMEOUT for timeout.
 * COMMENTS : 
 *==========================================================================*/
#ifdef __USE_XOPEN2K
int Mutex_Lock(IN HANDLE hm, IN DWORD dwWaitTimeout)
{
	MUTEX	*pMutex = (MUTEX *)hm;
	struct	timespec ts;
	long	nWaitSeconds, nWaitNonaseconds;
	time_t	tmEndTime;
	
	if (hm == NULL)
	{
		return ERR_INVALID_ARGS;
	}

	nWaitSeconds		= (long)MS_TO_SEC(dwWaitTimeout);			// sec
	nWaitNonaseconds	= (long)MS_TO_NS(dwWaitTimeout%MS_PER_SEC);	// nonasec

	clock_gettime(CLOCK_REALTIME, &ts);	// get the start time.
	
	ts.tv_nsec += nWaitNonaseconds;
	if (ts.tv_nsec > NS_PER_SEC)
	{
		ts.tv_sec  += 1;
		ts.tv_nsec -= NS_PER_SEC;
	}

	tmEndTime = ts.tv_sec;

#ifdef _DEBUG_MUTEX
	TRACE("[Mutex_Lock] -- lock and wait %d ms at %d\n", 
		(int)dwWaitTimeout, (int)tmEndTime);
#endif //_DEBUG_MUTEX

	while (nWaitSeconds >= 0)
	{
		// get the end of the real time.
		tmEndTime += ((nWaitSeconds < MAX_WAIT_INTERVAL) ? nWaitSeconds
			: MAX_WAIT_INTERVAL);

		ts.tv_sec = tmEndTime;
		// do NOT change ts.tv_nsec.

		if (pthread_mutex_timedlock(&pMutex->hInternalMutex, &ts) == 0)
		{
			return ERR_MUTEX_OK;
		}

		nWaitSeconds -= MAX_WAIT_INTERVAL;
		if (nWaitSeconds >= 0)
		{
			RUN_THREAD_HEARTBEAT();
		}
	}

#ifdef _DEBUG_MUTEX
	TRACE("[Mutex_Lock] -- lock timeouted at %d\n", 
		(int)time(NULL));
#endif //_DEBUG_MUTEX

	// if goes here, timeout on trying to lock.
	return ERR_MUTEX_TIMEOUT;
}

#else


int Mutex_Lock(IN HANDLE hm, IN DWORD dwWaitTimeout)
{
	MUTEX	*pMutex = (MUTEX *)hm;
	double	tEnd, tLast, tNow;

#define WAIT_LOCK_INTERVAL	20000	// 20 ms
	
	if (hm == NULL)
	{
		return ERR_INVALID_ARGS;
	}

#ifdef _DEBUG_MUTEX
	TRACE("[Mutex_Lock] -- lock and wait %d ms at %d\n", 
		(int)dwWaitTimeout, (int)time(NULL));
#endif //_DEBUG_MUTEX

	tEnd = GetCurrentTime() + (double)dwWaitTimeout/1000.0;

	for (;;)
	{
		if (pthread_mutex_trylock(&pMutex->hInternalMutex) == 0)
		{
			return ERR_MUTEX_OK;
		}

		// to test timeout.
		tNow = GetCurrentTime();
		if (tNow >= tEnd)
		{
			break;
		}

		// trigger heartbeart
		if (((int)tNow - (int)tLast) >= MAX_WAIT_INTERVAL)
		{
			RUN_THREAD_HEARTBEAT();
			tLast = tNow;
		}
			
		// sleep a while to try, always use the interval 20 ms
		usleep(WAIT_LOCK_INTERVAL);	// unit is us
	}

#ifdef _DEBUG_MUTEX
	TRACE("[Mutex_Lock] -- lock timeouted at %d\n", 
		(int)time(NULL));
#endif //_DEBUG_MUTEX

	return ERR_MUTEX_TIMEOUT;
}
#endif


/*==========================================================================*
 * FUNCTION : Mutex_Unlock
 * PURPOSE  : Release the locked lock.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hm : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Mutex_Unlock(HANDLE hm)
{
	if (hm != NULL)
	{
		pthread_mutex_unlock(&((MUTEX *)hm)->hInternalMutex);
	}
}


/*==========================================================================*
 * FUNCTION : Mutex_Destroy
 * PURPOSE  : To destroy a mutex, the mutext can not be used.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hm : The HANDLE of mutext will be destroyed
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Mutex_Destroy(IN HANDLE hm)
{
	if (hm != NULL)
	{
		pthread_mutex_destroy(&((MUTEX *)hm)->hInternalMutex);
		DELETE( hm );
	}
}



/*==========================================================================*
 *==========================================================================*
 *
 *  FILENAME : run_sem.c
 *  VERSION  : V1.00
 *  PURPOSE  :source code for semaphore.
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*
 *==========================================================================*/


struct _SEMAPHORE	//	internal structure for semaphore
{				
	sem_t	hInternalSem;	//	the semaphore lock
};				

typedef struct _SEMAPHORE	SEMAPHORE;

/*==========================================================================*
 * FUNCTION : Sem_Create
 * PURPOSE  : Create a semaphore
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nInitValue : the value of semaphore at init
 * RETURN   : HANDLE : NULL: failure, others: the handle of the semaphore
 * COMMENTS : 
 *==========================================================================*/
HANDLE Sem_Create(IN int  nInitValue)
{
	SEMAPHORE	*pSem = NEW(SEMAPHORE, 1);

	if (pSem == NULL)
	{
#ifdef _DEBUG_MUTEX
		TRACE("[Sem_Create] -- out of memory on new a SEMAPHORE.\n");
#endif //_DEBUG_MUTEX

		return NULL;
	}

	// no share, start count is 0 
	sem_init(&pSem->hInternalSem, 0, (unsigned int)nInitValue);
	
	return (HANDLE)pSem;
}


#ifdef __USE_XOPEN2K
/*==========================================================================*
 * FUNCTION : Sem_Wait
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hSem          : 
 *            IN DWORD   dwWaitTimeout : 
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int Sem_Wait(IN HANDLE hSem, IN DWORD dwWaitTimeout)
{
	SEMAPHORE	*pSem = (SEMAPHORE *)hSem;
	struct	timespec ts;
	long	nWaitSeconds, nWaitNonaseconds;
	time_t	tmEndTime;
	
	if (hSem == NULL)
	{
		return ERR_INVALID_ARGS;
	}

	nWaitSeconds		= (long)MS_TO_SEC(dwWaitTimeout);			// sec
	nWaitNonaseconds	= (long)MS_TO_NS(dwWaitTimeout%MS_PER_SEC);	// nonasec

	clock_gettime(CLOCK_REALTIME, &ts);	// get the start time.
	
	ts.tv_nsec += nWaitNonaseconds;
	if (ts.tv_nsec > NS_PER_SEC)
	{
		ts.tv_sec  += 1;
		ts.tv_nsec -= NS_PER_SEC;
	}

	tmEndTime = ts.tv_sec;

#ifdef _DEBUG_MUTEX
	TRACE("[Sem_Wait] -- %p wait %d ms at %d\n", 
		RunThread_GetId(NULL), (int)dwWaitTimeout, (int)tmEndTime);
#endif //_DEBUG_MUTEX

	while (nWaitSeconds >= 0)
	{
		// get the end of the real time.
		tmEndTime += ((nWaitSeconds < MAX_WAIT_INTERVAL) ? nWaitSeconds
			: MAX_WAIT_INTERVAL);

		ts.tv_sec = tmEndTime;
		// do NOT change ts.tv_nsec.

		if (sem_timedwait(&pSem->hInternalSem, &ts) == 0)
		{
			return ERR_MUTEX_OK;//wait ok
		}

		//continue to try to wait.
		nWaitSeconds -= MAX_WAIT_INTERVAL;
		if (nWaitSeconds >= 0)
		{
			RUN_THREAD_HEARTBEAT();
		}
	}

#ifdef _DEBUG_MUTEX
	TRACE("[Sem_Wait] -- wait timeouted at %d\n", 
		(int)time(NULL));
#endif //_DEBUG_MUTEX

	// if goes here, timeout on trying to lock.
	return ERR_MUTEX_TIMEOUT;
}

#else

int Sem_Wait(IN HANDLE hSem, IN DWORD dwWaitTimeout)
{
	SEMAPHORE	*pSem = (SEMAPHORE *)hSem;
	double	tEnd, tLast, tNow;
	HANDLE	hSelf;

#define WAIT_LOCK_INTERVAL	20000	// 20 ms
	
	if (hSem == NULL)
	{
		return ERR_INVALID_ARGS;
	}

#ifdef _DEBUG_MUTEX
	TRACE("[Sem_Wait] -- %p wait %d ms at %d\n", 
		RunThread_GetId(NULL), (int)dwWaitTimeout, (int)time(NULL));
#endif //_DEBUG_MUTEX

	tEnd = GetCurrentTime() + (double)dwWaitTimeout/1000.0;

	for (;;)
	{
		RunThread_Heartbeat(hSelf);

		if (sem_trywait(&pSem->hInternalSem) == 0)
		{
			return ERR_MUTEX_OK;//lock ok
		}

		//continue to try to lock.

		// to test timeout.
		tNow = GetCurrentTime();
		if (tNow >= tEnd)
		{
			break;
		}

		// trigger heartbeart
		if (((int)tNow - (int)tLast) >= MAX_WAIT_INTERVAL)
		{
			RUN_THREAD_HEARTBEAT();
			tLast = tNow;
		}
			
		// sleep a while to try, always use the interval 20 ms
		usleep(WAIT_LOCK_INTERVAL);	// unit is us
	}

#ifdef _DEBUG_MUTEX
	TRACE("[Sem_Wait] -- wait timeouted at %d\n", 
		(int)time(NULL));
#endif //_DEBUG_MUTEX

	return ERR_MUTEX_TIMEOUT;
}
#endif


/*==========================================================================*
 * FUNCTION : Sem_Post
 * PURPOSE  : Release the semaphore.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hSem : 
 * RETURN   : void : 
 *==========================================================================*/
void Sem_Post(HANDLE hSem, int nPostCount)
{
	if (hSem != NULL)
	{
		while (nPostCount-- > 0)
		{
			if (sem_post(&((SEMAPHORE *)hSem)->hInternalSem) != 0)
			{
				break;
			}
		}
	}
}


/*==========================================================================*
 * FUNCTION : Sem_Destroy
 * PURPOSE  : To destroy a semaphore, the semaphore can not be used.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hSem : The HANDLE of semaphore will be destroyed
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Sem_Destroy(IN HANDLE hSem)
{
	if (hSem != NULL)
	{
		sem_destroy(&((SEMAPHORE *)hSem)->hInternalSem);
		DELETE( hSem );
	}
}

