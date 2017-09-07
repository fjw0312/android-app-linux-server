/*==========================================================================*
 *  FILENAME : run_queue.c
 *  VERSION  : V1.00
 *  PURPOSE  : source code for queue
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#include "stdsys.h"		/* all standard system head files			*/

#include "basetypes.h"
#include "new.h"
#include "err_code.h"
#include "run_queue.h"
#include "run_mutex.h"


#ifdef _DEBUG
//#define _DEBUG_RUN_QUEUE	1
#endif //_DEBUG


#define INIT_LOCK(pLock)	{pthread_mutex_t x = PTHREAD_MUTEX_INITIALIZER; *(pLock) = x;}
#define LOCK(pLock)			pthread_mutex_lock((pLock))
#define UNLOCK(pLock)		pthread_mutex_unlock((pLock))
#define DESTROY_LOCK(pLock) pthread_mutex_destroy((pLock))

struct _QUEUE	//	FIFO
{				
	int		nIncStep;	//	The nIncStep to indicate wthether the queue 
						//  capacity can be auto enlarged or not.
	int		nSize;		//	size of an element or a record of this queue
	int		nCapacity;	//	max elements of the queue
	char	*pBuffer;	//	the buffer to hold elements
	pthread_mutex_t	hSyncLock;	//	the mutex lock

	int		nCount;			//	the current elements in the queue

	int		nCharCapacity;	//	equal to (nCapacity-1)*nSize in char
	int		nHead;			//	the first char of the head position
	int		nTail;			//	the first char of the tail empty position

	HANDLE	hCountSem;		// the semaphore for the count.	
};				
typedef struct _QUEUE	QUEUE;


// NULL for error.
/*==========================================================================*
 * FUNCTION : Queue_Create
 * PURPOSE  : to create a queue with given args
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nMaxCapacity : the init maximum number of elements
 *            IN int  nElementSize : the size of an element in byte
 *            IN int  nIncStep     : the element num will be increased if
 *                                   the queue is full, 0 not increased.
 * RETURN   : HANDLE : NULL for fail, else for OK.
 * COMMENTS : 
 *==========================================================================*/
HANDLE Queue_Create( IN int nMaxCapacity, IN int nElementSize,
					 IN int nIncStep)
{
	QUEUE *q;

	if( (nMaxCapacity <= 0) || (nElementSize <= 0) )
	{
#ifdef _DEBUG_RUN_QUEUE
		TRACE("[Queue_Create] -- Invalid nMaxCapacity(%d) or nElementSize(%d).\n",
			nMaxCapacity, nElementSize);
#endif //_DEBUG_RUN_QUEUE

		return NULL;
	}

	q = NEW( QUEUE, 1 );
	if( q == NULL )
	{
#ifdef _DEBUG_RUN_QUEUE
		TRACE("[Queue_Create] -- Out of memory on new QUEUE.\n");
#endif //_DEBUG_RUN_QUEUE

		return NULL;
	}

	q->pBuffer = NEW( char, nMaxCapacity*nElementSize );
	if( q->pBuffer == NULL )
	{
#ifdef _DEBUG_RUN_QUEUE
		TRACE("[Queue_Create] -- Out of memory on new buffer of QUEUE.\n");
#endif //_DEBUG_RUN_QUEUE

		DELETE( q );
		return NULL;
	}

	// create the semaphore
	q->hCountSem = Sem_Create(0);// no share, start count is 0 
	if( q->hCountSem == NULL )
	{
#ifdef _DEBUG_RUN_QUEUE
		TRACE("[Queue_Create] -- Error on creating the semaphore of QUEUE.\n");
#endif //_DEBUG_RUN_QUEUE

		DELETE(q->pBuffer);
		DELETE( q );
		return NULL;
	}


	INIT_LOCK( &q->hSyncLock );

	q->nCapacity = nMaxCapacity;
	q->nSize     = nElementSize;
	q->nCount    = 0;
	q->nCharCapacity = (q->nCapacity-1) * q->nSize;
	q->nHead     = 0;
	q->nTail     = 0;
	q->nIncStep  = nIncStep;

	return (HANDLE)q;
}


/*==========================================================================*
 * FUNCTION : Queue_Empty
 * PURPOSE  : to empty a queue
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Queue_Empty(HANDLE hq)
{
	QUEUE *q = (QUEUE *)hq;

	LOCK( &q->hSyncLock );

	while (Sem_Wait(q->hCountSem, 0) == ERR_MUTEX_OK)
	{
		;
	}

	q->nCount    = 0;
	q->nHead     = 0;
	q->nTail     = 0;

	UNLOCK( &q->hSyncLock );
}

/*==========================================================================*
 * FUNCTION : Queue_Destroy
 * PURPOSE  : To destroy a initialized queue.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Queue_Destroy(HANDLE hq)
{
	QUEUE *q = (QUEUE *)hq;

	if( q == NULL )
	{
		return;
	}

#ifdef _DEBUG_RUN_QUEUE
	if (q->nCount != 0)
	{
		TRACE("[Queue_Destroy] -- A non-empty queue will be deleted.\n");
	}
#endif //_DEBUG

	DELETE( q->pBuffer );

	DESTROY_LOCK(&q->hSyncLock);
	Sem_Destroy(q->hCountSem);

	DELETE( q );
}


#define QUEUE_MOVE_AHEAD( q, x )	\
	( ((x) < (q)->nCharCapacity) ? ((x) + (q)->nSize) : 0 )

#define QUEUE_MOVE_BACK( q, x )	\
	( ((x) > 0) ? ((x) - (q)->nSize) : ( (q)->nCharCapacity) )

// 0: ok, -1: error, 1: full.
/*==========================================================================*
 * FUNCTION : Queue_Put
 * PURPOSE  : To put a element to a queue
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq        : 
 *            void    *pElement : 
 *            BOOL    bUrgent   : 
 * RETURN   : int : ERR_QUEUE_OK: OK, ERR_QUEUE_FULL: full, 
 *                  ERR_INVALID_ARGS: error arg,  ERR_NO_MEMORY: no memory
 * COMMENTS : 
 *==========================================================================*/
int Queue_Put( HANDLE hq, void *pElement, BOOL bUrgent )
{
	QUEUE *q = (QUEUE *)hq;
	int	nResult;

	if ((q == NULL) || (pElement == NULL))
	{
		return ERR_INVALID_ARGS;
	}

	LOCK( &q->hSyncLock );

	if (q->nCount >= q->nCapacity)	// queue is full
	{
		if (q->nIncStep > 0)	// auto increase the queue.
		{
			int	 nNewCharSize = (q->nCapacity+q->nIncStep)*q->nSize;
			char *pNewBuffer  = NEW(char, nNewCharSize);

			if (pNewBuffer != NULL)
			{
				size_t	nBytesFromHeadToEnd;

				// copy data to new buffer.
				// a full queue.
				// [6][7][8][9][0][1][2][3][4][5]
				//             TH
				// after enlarge 5 items
				// [6][7][8][9][][][][][][][0][1][2][3][4][5]
				//             T            H
				//1. copy 0~nTail bytes to new buffer, nTail is in byte
				memmove(&pNewBuffer[0], q->pBuffer, (size_t)q->nTail);
				
				//2. copy nHead~-End bytes to new buffer, and set new nHead
				nBytesFromHeadToEnd = 
					(size_t)(q->nCapacity*q->nSize - q->nHead);

				memmove(&pNewBuffer[nNewCharSize-nBytesFromHeadToEnd],
					&q->pBuffer[q->nHead], nBytesFromHeadToEnd);

				q->nHead += q->nIncStep*q->nSize;

				//memmove(pNewBuffer, q->pBuffer, 
				//	(size_t)(q->nCapacity*q->nSize));
				
				// delete old buffer
				DELETE(q->pBuffer);

				// set new buffer
				q->pBuffer	  = pNewBuffer;
				q->nCapacity += q->nIncStep;
				q->nCharCapacity = (q->nCapacity-1) * q->nSize;
			}
			else
			{
#ifdef _DEBUG_RUN_QUEUE
				TRACE("[Queue_Put] -- Out of memory on increasing buffer of QUEUE.\n");
#endif //_DEBUG_RUN_QUEUE

				nResult = ERR_NO_MEMORY;
			}
		}
		else
		{
#ifdef _DEBUG_RUN_QUEUE
//			TRACE( "[Queue_Put] -- Insert failure due to queue is full.\n" );
#endif //_DEBUG_RUN_QUEUE

			nResult = ERR_QUEUE_FULL;
		}
	}

	if (q->nCount < q->nCapacity)	// is not full?
	{
		if( bUrgent == FALSE )	// insert to tail
		{
			// copy data
			memmove( &q->pBuffer[q->nTail], pElement, 
				(size_t)q->nSize );

			// set tail position to the next empty.
			q->nTail = QUEUE_MOVE_AHEAD( q, q->nTail );
		}
		else	// insert to head
		{
			// make the head to back, which is an empty position
			q->nHead = QUEUE_MOVE_BACK( q, q->nHead );

			// copy data
			memmove( &q->pBuffer[q->nHead], pElement, 
				(size_t)q->nSize );
		}

		q->nCount++;

		nResult = ERR_QUEUE_OK;

		Sem_Post(q->hCountSem, 1);
	}

	UNLOCK( &q->hSyncLock );

	return nResult;
}


/*==========================================================================*
 * FUNCTION : Queue_GetCount
 * PURPOSE  : get the current item num in the queue
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hq            : 
 *            OUT int    *pMaxElements : the max items can be held. can be NULL
 * RETURN   : int : 0 for no item, > 0 the num of items
 * COMMENTS : 
 *==========================================================================*/
int Queue_GetCount(IN HANDLE hq, OUT int *pMaxElements)
{
	QUEUE	*q = (QUEUE *)hq;
	int		nCount;

	LOCK( &q->hSyncLock );

	if (pMaxElements != NULL)
	{
		*pMaxElements = q->nCapacity;
	}

	nCount = q->nCount;

	UNLOCK( &q->hSyncLock );

	return nCount;
}


/*==========================================================================*
 * FUNCTION : Queue_Get
 * PURPOSE  : get an element from the queue.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq        : 
 *            void    *pElement : 
 *            BOOL    bPeek     : TRUE: only get the element, the element
 *                                will not be deleted.
 *            DWORD dwTimeout   : wait for n ms if no data.
 * RETURN   : int : ERR_OK for OK, or ERR_QUEUE_EMPTY for empty queue
 *                  or ERR_INVALID_ARGS
 * COMMENTS : 
 *==========================================================================*/
int Queue_Get(HANDLE hq, void *pElement, BOOL bPeek, DWORD dwTimeout)
{
	QUEUE *q = (QUEUE *)hq;
	int	nResult;

	if ((q == NULL) || (pElement == NULL))
	{
		return ERR_INVALID_ARGS;
	}

	//1. to test there is any data or not.
	if (Sem_Wait(q->hCountSem, dwTimeout) != ERR_MUTEX_OK)
	{
#ifdef _DEBUG_RUN_QUEUE
		TRACE("[Queue_Get] -- The queue is empty.\n");
#endif //_DEBUG_RUN_QUEUE

		return ERR_QUEUE_EMPTY;
	}

	ASSERT(q->nCount > 0);

	// 2. to get the data.
	LOCK( &q->hSyncLock );
	if (q->nCount > 0)
	{
		// copy data
		memmove( pElement, &q->pBuffer[q->nHead], (size_t)q->nSize );

		if( !bPeek )
		{
			// move pos and descrease count
			q->nHead = QUEUE_MOVE_AHEAD( q, q->nHead );
			q->nCount --;
		}

		nResult = ERR_QUEUE_OK;
	}
	else	// Q is empty
	{
		nResult = ERR_QUEUE_EMPTY;
	}

	UNLOCK( &q->hSyncLock );

	if (bPeek)
	{
		// we only peek, increase the semaphore again.
		Sem_Post(q->hCountSem, 1);
	}

	return nResult;
}


/*==========================================================================*
 * FUNCTION : Queue_GetEx
 * PURPOSE  : get or peek an element from anywhere of the queue.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq        : 
 *            int     nWhich    : 0: get the head, -1: get the tail, 
 *                                other:the index of element to peek.
 *            void    *pElement : 
 *            BOOL    bPeek     : FALSE: the element will be gotten and
 *                                deleted from queue when nWhich is 0 or -1,
 *                                else the element will be kept in queue.
 * RETURN   : int : ERR_QUEUE_OK for OK, or ERR_QUEUE_EMPTY for empty queue
 *                  or ERR_INVALID_ARGS
 * COMMENTS : 
 *==========================================================================*/
int Queue_GetEx(HANDLE hq, int nWhich, void *pElement, BOOL bPeek)
{
	QUEUE *q = (QUEUE *)hq;
	int	nResult;

	if ((q == NULL) || (pElement == NULL))
	{
		return ERR_INVALID_ARGS;
	}

	//1. to test there is any data or not.
	if (Sem_Wait(q->hCountSem, 0) != ERR_MUTEX_OK)
	{
#ifdef _DEBUG_RUN_QUEUE
		TRACE("[Queue_Get] -- The queue is empty.\n");
#endif //_DEBUG_RUN_QUEUE

		return ERR_QUEUE_EMPTY;
	}

	LOCK( &q->hSyncLock );

	if( q->nCount > 0 )
	{
		nResult = ERR_QUEUE_OK;

		if (nWhich == 0)	// get the head
		{
			// copy data
			memmove( pElement, &q->pBuffer[q->nHead], (size_t)q->nSize );

			if( !bPeek )
			{
				// move pos and descrease count
				q->nHead = QUEUE_MOVE_AHEAD( q, q->nHead );
				q->nCount --;
			}
		}
		else if (nWhich == -1)	// get the tail
		{
			int nTempTail = QUEUE_MOVE_BACK( q, q->nTail );

			// copy data
			memmove( pElement, &q->pBuffer[nTempTail], (size_t)q->nSize );

			if( !bPeek )
			{
				// move pos and descrease count
				q->nTail = nTempTail;
				q->nCount --;
			}
		}

		else if (nWhich < q->nCount)
		{
			int nLocate = (q->nHead + nWhich*q->nSize) 
				% (q->nCharCapacity + q->nSize);

			// copy data
			memmove( pElement, &q->pBuffer[nLocate], (size_t)q->nSize );
			bPeek = TRUE;	// to make restore the semaphore
		}

		else
		{
			bPeek = TRUE;	// to make restore the semaphore
			nResult = ERR_INVALID_ARGS;
		}
	}

	else	// Q is empty
	{
		nResult = ERR_QUEUE_EMPTY;
	}

	UNLOCK( &q->hSyncLock );

	if (bPeek)
	{
		// we only peek, increase the semaphore again.
		Sem_Post(q->hCountSem, 1);
	}

	return nResult;
}
