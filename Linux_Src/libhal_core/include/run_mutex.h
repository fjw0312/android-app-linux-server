/*==========================================================================*
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#ifndef __RUN_MUTEX_H__
#define __RUN_MUTEX_H__


/*==========================================================================*
 * FUNCTION : Mutex_Create
 * PURPOSE  : Create a mutex
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN BOOL  bOpenedLock : TRUE: the lock is opened, 
 *                                   FALSE: lock wiil be locked by the creator
 * RETURN   : HANDLE : NULL: failure, others: the handle of the mutex
 * COMMENTS : 
 *==========================================================================*/
HANDLE Mutex_Create(IN BOOL bOpenedLock);


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
int Mutex_Lock(IN HANDLE hm, IN DWORD dwWaitTimeout);


/*==========================================================================*
 * FUNCTION : Mutex_Unlock
 * PURPOSE  : Release the locked lock.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hm : 
 * RETURN   : void : 
 *==========================================================================*/
void Mutex_Unlock(HANDLE hm);


/*==========================================================================*
 * FUNCTION : Mutex_Destroy
 * PURPOSE  : To destroy a mutex, the mutext can not be used.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hm : The HANDLE of mutext will be destroyed
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Mutex_Destroy(IN HANDLE hm);




////////////////////// Semaphore ////////////////////////////

/*==========================================================================*
 * FUNCTION : Sem_Create
 * PURPOSE  : Create a semaphore
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nInitValue : the value of semaphore at init
 * RETURN   : HANDLE : NULL: failure, others: the handle of the semaphore
 * COMMENTS : 
 *==========================================================================*/
HANDLE Sem_Create(IN int  nInitValue);


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
int Sem_Wait(IN HANDLE hSem, IN DWORD dwWaitTimeout);


/*==========================================================================*
 * FUNCTION : Sem_Post
 * PURPOSE  : Release the semaphore.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hSem : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Sem_Post(HANDLE hSem, int nPostCount);


/*==========================================================================*
 * FUNCTION : Sem_Destroy
 * PURPOSE  : To destroy a semaphore, the semaphore can not be used.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hSem : The HANDLE of semaphore will be destroyed
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Sem_Destroy(IN HANDLE hSem);

#endif /*__RUN_MUTEX_H__*/

