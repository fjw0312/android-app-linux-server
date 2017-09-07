#ifndef __RUNTHREAD_H__
#define __RUNTHREAD_H__

// max wait time interval in sleep() or select().
#define MAX_WAIT_INTERVAL		5	//the maximum wait time in seconds.
#define LEN_RUN_THREAD_NAME		16	// the maximum length of the run thread name	


#define RUN_THREAD_INIT_HEARTBEATS		2	// the init heartbeats is 2
#define RUN_THREAD_MANAGER_HEARTBEAT_INTERVAL	500		// 500ms 
#define RUN_THREAD_HEARTBEAT_CHECK_INTERVAL		60000	// 60s.

#if (RUN_THREAD_HEARTBEAT_CHECK_INTERVAL < (2*MAX_WAIT_INTERVAL*1000))
#error The value "RUN_THREAD_HEARTBEAT_CHECK_INTERVAL" is too small!
#endif


/*==========================================================================*
 * FUNCTION : RUN_THREAD_START_PROC
 * PURPOSE  : // The run-thread entry route
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS:  void* pArgs: // The arg prt to run the thread
 * RETURN   : DWORD: exit code of the thread. 0 for OK in normal
 * COMMENTS : 
 *==========================================================================*/
typedef DWORD (*RUN_THREAD_START_PROC)
(
	void* pArgs			
);


/*==========================================================================*
 * FUNCTION : RUN_THREAD_EVENT_HANDLER
 * PURPOSE  : // the handler to process the event comes from thread manager.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: DWORD   dwThreadEvent : THREAD_EVENT_HEARTBEAT or 
 *									  THREAD_EVENT_NO_RESPONSE
 *            HANDLE  hThread       : The thread id
 * RETURN   : DWORD : THREAD_CONTINUE_RUN, 
 *                    THREAD_CANCEL_THIS or THREAD_CANCEL_ALL
 * COMMENTS : 
 *==========================================================================*/
typedef DWORD (*RUN_THREAD_EVENT_HANDLER)
(				
	DWORD	dwThreadEvent,		//	the thread event. below.
	HANDLE	hThread,			//	The thread id.
	const char	*pThreadName	//  the thread name.
);

// dwThreadEvent enumerate of RUN_THREAD_EVENT_HANDLER:
enum _THREAD_EVENT_ENUM
{
	THREAD_EVENT_HEARTBEAT			  =	1,// The heartbeat event from thread
	THREAD_EVENT_NO_RESPONSE		  = 2,// the thread is no response event.
	THREAD_EVENT_MANAGER_HAS_STARTED  = 3,
//	THREAD_EVENT_IS_STOPPED	          =	4,// the thread is stooped.
	THREAD_EVENT_CANNOT_BE_STOPPED	  = 5,// the thread can NOT be stooped.
	THREAD_EVENT_MANAGER_HAS_STOPPED  =	6
};

// The return result of RUN_THREAD_EVENT_HANDLER
#define THREAD_CONTINUE_RUN			0	// to continue run the thread
#define THREAD_CANCEL_THIS			1	// to cancel this running thread(hThread)
#define THREAD_CANCEL_ALL			2	// to stop all running thread(quit system)

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
 *                                                    0: no flags,see below
 * RETURN   : HANDLE : NULL for failure, else is the HANDLE of the thread.
 * COMMENTS : 
 *==========================================================================*/
HANDLE RunThread_Create(IN const char *pszThreadName, 
						IN RUN_THREAD_START_PROC pfnThreadProc,
						IN void *pThreadArg,
						OUT DWORD *pdwExitCode,
						IN DWORD dwCreateFlag);

// flags of creating a thread, two or more flags can use "or" operator.
#define RUN_THREAD_FLAG_HAS_MSG		0x0001	// a msg queue will be created
#define RUN_THREAD_FLAG_HAS(flags, f)	(((int)(flags)&(int)(f)) == (int)(f))


/*==========================================================================*
 * FUNCTION : RunThread_GetId
 * PURPOSE  : get the run thread id by name, if name is NULL will get the id
 *            of this caller thread
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: char  *pszThreadName : name of thread, NULL for the caller itself.
 * RETURN   : HANDLE : 
 * COMMENTS :
 *==========================================================================*/
HANDLE RunThread_GetId(IN const char *pszThreadName);

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
char *RunThread_GetName(IN HANDLE hThread, OUT char *pszName, IN int nNameLen);

//
// the thread message definition
//
struct _RUN_THREAD_MSG		//	The msg of thread
{				
	DWORD	dwMsg;		//	the msg type
	DWORD	dwParam1;	//	param1 of the msg
	DWORD	dwParam2;	//	param2 of the msg
	HANDLE	hSender;	//	the thread id of the sender
};

typedef struct _RUN_THREAD_MSG	RUN_THREAD_MSG;
//

#define RUN_THREAD_MAKE_MSG(pmsg, sender, msgType, par1, par2 ) \
		((pmsg)->dwMsg   = (DWORD)(msgType),	\
		(pmsg)-> dwParam1= (DWORD)(par1),		\
		(pmsg)->dwParam2 = (DWORD)(par2),		\
		(pmsg)->hSender  = (HANDLE)(sender) )

#define DEF_RUN_THREAD_MSG(sender, msgType, par1, par2 )	\
		{	(DWORD)(msgType),	\
			(DWORD)(par1),		\
			(DWORD)(par2),		\
			(HANDLE)(sender) }

enum RUN_THREAD_MSG_TYPE
{
	MSG_QUIT	= 0,		// to quit the thread
	MSG_TIMER	= 1,		// timer msg

// other system msg comes here

	MSG_MAX_SYS	= 1000,			// the maximum system msg

	MSG_USER	= MSG_MAX_SYS,
};

//
// to define user msg type, the msg type must be greater than MSG_USER.
//
//#define MSG_SET		MSG_USER+1
//

#define RUN_THREAD_MAX_MSG 50	//	the maximum msg can be hold in each thread


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
						 IN DWORD dwTimeout); // the unit is milliseconds

/*==========================================================================*
 * FUNCTION : RunThread_GetStatus
 * PURPOSE  : get the running state of a thread
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: HANDLE  hThread : 
 * RETURN   : int : RUN_THREAD_IS_RUNNING if running, else 
 *                  RUN_THREAD_IS_INVALID for invalid thread(or not running)
 *                  RUN_THREAD_TO_QUIT for the thread need to quit.
 * COMMENTS : status see below
 *==========================================================================*/
int RunThread_GetStatus(IN HANDLE hThread);

// status of run thread
#define RUN_THREAD_IS_RUNNING		0	// is running
#define RUN_THREAD_TO_QUIT			1	// thread need to quit.
#define RUN_THREAD_IS_INVALID		2	// the thread is invalid

#define THREAD_IS_RUNNING(hSelf)	\
			(RunThread_GetStatus((hSelf)) == RUN_THREAD_IS_RUNNING)

#define THREAD_IS_QUITED(hSelf)		\
			(RunThread_GetStatus((hSelf)) == RUN_THREAD_IS_INVALID)


/*==========================================================================*
 * FUNCTION : RunThread_GetThreadCount
 * PURPOSE  : get the count of the current running thread
 * CALLS    : 
 * CALLED BY: 
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int RunThread_GetThreadCount(void);

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
int RunThread_Heartbeat(IN HANDLE hThread);


//
// This macro can NOT be used in the function which is called frequently.
// If the calling period of a function is in few seconds level, the macro
// can be used in this function.
// OR, the calling will cause low efficient.
//
#define RUN_THREAD_HEARTBEAT()	RunThread_Heartbeat(RunThread_GetId(NULL))	

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
						  IN RUN_THREAD_MSG *pMsg, IN BOOL bUrgent);

/*==========================================================================*
 * FUNCTION : RunThread_PostQuitMessage
 * PURPOSE  : 
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hThread : -1 for all threads
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
int RunThread_PostQuitMessage(IN HANDLE hThread);

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
 * COMMENTS : 
 *==========================================================================*/
int RunThread_Stop(IN HANDLE hThread, IN int nTimeToWaitThreadQuit, 
				   IN BOOL bKillTimeoutedThread);


/*==========================================================================*
 * FUNCTION : RunThread_ManagerInit
 * PURPOSE  : init the thread manager thread.
 * CALLS    : 
 * CALLED BY: main
 * ARGUMENTS: RUN_THREAD_EVENT_HANDLER  *pfnEventHandler : 
 * RETURN   : int : ERR_OK if OK, else ERR_THREAD_INIT_MANAGER for error.
 * COMMENTS : 
 *==========================================================================*/
int RunThread_ManagerInit(IN RUN_THREAD_EVENT_HANDLER pfnEventHandler);

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
						  IN BOOL bKillTimeoutedThread);


#endif //__RUNTHREAD_H__

