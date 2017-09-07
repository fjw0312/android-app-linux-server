/*==========================================================================*
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#ifndef __RUN_QUEUE_H__
#define __RUN_QUEUE_H__

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
					 IN int nIncStep);


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
int Queue_Put( HANDLE hq, void *pElement, BOOL bUrgent );


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
int Queue_GetCount(IN HANDLE hq, OUT int *pMaxElements);


/*==========================================================================*
 * FUNCTION : Queue_Get
 * PURPOSE  : get an element from the queue.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq        : 
 *            void    *pElement : 
 *            BOOL    bPeek     : TRUE: only get the element, the element
 *                                will not be deleted.
 *            DWORD  dwTimeout  : wait for n ms if no data.
 * RETURN   : int : ERR_OK for OK, or ERR_QUEUE_EMPTY for empty queue
 *                  or ERR_INVALID_ARGS
 * COMMENTS : 
 *==========================================================================*/
int Queue_Get(HANDLE hq, void *pElement, BOOL bPeek, DWORD dwTimeout);


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
int Queue_GetEx(HANDLE hq, int nWhich, void *pElement, BOOL bPeek);


/*==========================================================================*
 * FUNCTION : Queue_Empty
 * PURPOSE  : to empty a queue
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Queue_Empty(HANDLE hq);


/*==========================================================================*
 * FUNCTION : Queue_Destroy
 * PURPOSE  : To destroy a initialized queue.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hq : 
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
void Queue_Destroy( HANDLE hq);



#endif /*__RUN_QUEUE_H__*/
