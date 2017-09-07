/*==========================================================================*
 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#ifndef __RUN_TIMER_H__
#define __RUN_TIMER_H__

typedef int (*ON_TIMER_PROC)
(				
	HANDLE		hTimerOwner,//	The thread ID of timer owner
	int			idTimer,	//	The ID of the timer
	void		*pArgs		//	The args passed by timer owner
);				
//Return:	
//0=TIMER_CONTINUE_RUN:	To continue use this timer
//1=TIMER_KILL_THIS:	To kill this timer, this timer is no use
//2=TIMER_IS_KILLED:	(Reserved)The timer is deleted in the timer proc

#define TIMER_CONTINUE_RUN	0	//To continue use this timer
#define TIMER_KILL_THIS		1	//To kill this timer, this timer is no use
#define TIMER_IS_KILLED		2	//(Reserved)The timer is deleted in timer proc

typedef struct _TIMER_ENTRY 	TIMER_ENTRY;

struct _TIMER_ENTRY		//	The entry for a timer item
{				
	TIMER_ENTRY		*next;			//	Point to the next timer,
									//  must be the first field!
	HANDLE			hTimerOwner;	//	The ID of the thread that creates timer
	int				idTimer;		//	timer ID, shall be unique in a thread
	int				nInterval;		//  timer interval, in ms
	ON_TIMER_PROC	pfnAction;		//	the callback procedure on timer event
	DWORD			dwParam;		//	if pfnAction is NULL, it is the second
									//  msg param will be sent to owner. 
									//  or it is the param of calling pfnAction

	int				nElapsed;		//	The elapsed time of this timer
};

/*==========================================================================*
 * FUNCTION : Timer_Set
 * PURPOSE  : set a timer
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE         hTimerOwner : The run-thread ID to create tmr
 *            IN int            idTimer     : the unique timer id in this thrd
 *            IN int            nInterval   : the timer interval in ms
 *            IN ON_TIMER_PROC  pfnAction   : the callback on timer, if be null
 *                              the msg(NULL,MSG_TIMER,idTimer,dwParam) will be
 *                              sent to the owner thread.
 *            IN DWORD          dwParam     : the param for callback
 * RETURN   : int : ERR_OK for OK, ERR_TIMER_EXISTS: for timer exists,
 *                  ERR_TIMER_SET_FAIL for failure
 * COMMENTS : 
 *==========================================================================*/
int Timer_Set(IN HANDLE hTimerOwner, IN int idTimer, IN int nInterval, 
			  IN ON_TIMER_PROC pfnAction, IN DWORD dwParam);


/*==========================================================================*
 * FUNCTION : Timer_Kill
 * PURPOSE  : To kill a timer from the timer manager
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hTimerOwner : The timer owner
 *            IN int     idTimer     : 
 * RETURN   : int : ERR_OK for OK, ERR_TIMER_NOT_FOUND: no such timer
 * COMMENTS : 
 *==========================================================================*/
int Timer_Kill(IN HANDLE hTimerOwner, IN int idTimer);


/*==========================================================================*
 * FUNCTION : Timer_Reset
 * PURPOSE  : Reset a timer, the time will re-count from 0.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hTimerOwner : 
 *            IN int     idTimer     : 
 * RETURN   : int : ERR_OK for OK, ERR_TIMER_NOT_FOUND: no such timer
 * COMMENTS : 
 *==========================================================================*/
int Timer_Reset(IN HANDLE hTimerOwner, IN int idTimer);

#endif /*__RUN_TIMER_H__*/
