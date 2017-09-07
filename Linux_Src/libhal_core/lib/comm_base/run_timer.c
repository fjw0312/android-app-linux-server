/*==========================================================================*
 *  FILENAME : run_timer.c

 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/
#include <stdlib.h>
#include "stdsys.h"		/* all standard system head files			*/
#include <signal.h>

#include "basetypes.h"
#include "new.h"
#include "pubfunc.h"

#include "err_code.h"
#include "run_thread.h"
#include "run_timer.h"

#ifdef __USE_XOPEN2K
//#error "__USE_XOPEN2K" shall NOT be defined in timer!
#endif

#define TIMER_RESOLUTION		20	// ms.

#ifdef _DEBUG
//#define _DEBUG_RUN_TIMER	1
#endif //_DEBUG


// time init method. must define this macro!
//#define _TIMER_INIT_POSIX_TIMER_METHOD			1
//#define _TIMER_INIT_BSD_ALARM_METHOD			1
#define _TIMER_INIT_THREAD_DELAY_METHOD			1

// The timer must be init with the following method,
// because if using the alarm signal method to init timer,
// the sleep call always be interrupted by the alarm signal!
#ifdef _TIMER_INIT_POSIX_TIMER_METHOD
	#warning "_TIMER_INIT_POSIX_TIMER_METHOD method will error if time changed!"
	#define Timer_ManagerInitTimer		Timer_ManagerInitPosixTimer
#elif defined  _TIMER_INIT_BSD_ALARM_METHOD
	#define Timer_ManagerInitTimer		Timer_ManagerInitBsdAlarmTimer
#elif defined _TIMER_INIT_THREAD_DELAY_METHOD
	#define Timer_ManagerInitTimer		Timer_ManagerInitThreadTimer
#else
	#error "Timer method not defined."
#endif


struct _TIMER_MANAGER	//	The timer manager
{				
	TIMER_ENTRY 	*pTimerList;	//	The timer list, must be the first field!

	int				nTimerCount;	//	The total timer count

	int				nTimerResolution;//	The resolution of the timer,
									//  default is 20ms

	pthread_mutex_t	hTimerSyncLock;	//	The internal lock.
#ifdef _TIMER_INIT_POSIX_TIMER_METHOD
	timer_t			hTimer;
#endif
#ifdef _TIMER_INIT_BSD_ALARM_METHOD
	__sighandler_t	pOldHandler;	//	To save the old event hanler of SIGALRM
#endif

#ifdef _TIMER_INIT_THREAD_DELAY_METHOD
	pthread_t		hTimer;
	int				nRunningCount;
#endif
};				
typedef struct _TIMER_MANAGER	TIMER_MANAGER;


//define the unique timer manager.
static TIMER_MANAGER	s_TimerManager =
{	
	NULL,	// not any timer
	-1,		// -1: indicate the manager is not initialized yet.
	0,		
	PTHREAD_MUTEX_INITIALIZER,  // mutex	
#ifdef	_TIMER_INIT_POSIX_TIMER_METHOD
	0
#endif
#ifdef _TIMER_INIT_BSD_ALARM_METHOD
	NULL
#endif
#ifdef _TIMER_INIT_THREAD_DELAY_METHOD
	0,
	0,
#endif
};


#define LOCK_TIMER_SYNC()       pthread_mutex_lock(&s_TimerManager.hTimerSyncLock)
#define UNLOCK_TIMER_SYNC()     pthread_mutex_unlock(&s_TimerManager.hTimerSyncLock)
#define DESTROY_TIMER_SYNC()    pthread_mutex_destroy(&s_TimerManager.hTimerSyncLock)

#define TIMER_LIST_INITIALIZED()	(s_TimerManager.nTimerCount >= 0)


static void Timer_ManagerExit(void);

/*==========================================================================*
 * FUNCTION : Timer_AlarmHander
 * PURPOSE  : The handler for SIGALRM to process timers.
 * CALLS    : 
 * CALLED BY: Linux OS
 * ARGUMENTS: IN int  nSigNum : shall be SIGALRM.
 * RETURN   : static void : 
 * COMMENTS : 
 *==========================================================================*/
static void Timer_AlarmHander(IN int nSigNum, siginfo_t *extra, void *cruft)
{
	TIMER_ENTRY	*p;				// The current timer being processed
	TIMER_ENTRY	*pPrevTimer;	// the previuos timer of the current.
	RUN_THREAD_MSG msg;

	static double tmLastTime = 0;
	double		  tmNow = GetCurrentTime();
	int			  nElapsedTime;


	UNUSED(nSigNum);
	UNUSED(extra);
	UNUSED(cruft);

	nElapsedTime = (int)((tmNow - tmLastTime)*1000.0);
	tmLastTime   = tmNow; 

	if ((nElapsedTime >= 50*TIMER_RESOLUTION) || (nElapsedTime <= 0))
	{
		nElapsedTime = s_TimerManager.nTimerResolution;
	}

//	TRACEX("timer...\n");

	LOCK_TIMER_SYNC();	// lock the timer list avoid accessing confliction

	pPrevTimer = (TIMER_ENTRY *)&s_TimerManager;

	while (pPrevTimer->next != NULL)
	{
		p = pPrevTimer->next;

		p->nElapsed	+= nElapsedTime;	// the elapsed time.

		// Check this timer is timeout or not
		if (p->nElapsed >= p->nInterval)				// it's time to act!
		{
#ifdef _DEBUG_RUN_TIMER
//			TRACEX("Timer #%d is triggering...\n", p->idTimer);
#endif //_DEBUG_RUN_TIMER

			// decrease the used timer interval
			p->nElapsed -= p->nInterval;

			if (p->pfnAction != NULL)
			{
				if (p->pfnAction(p->hTimerOwner, p->idTimer,
					(void *)p->dwParam) == TIMER_KILL_THIS)
				{
					// remove the timer, it's no use.
					pPrevTimer->next  = p->next;	// delete p for link.
					DELETE(p);

					continue;
				}
			}

			else	// send timer message to thread
			{
				RUN_THREAD_MAKE_MSG(&msg, NULL,	MSG_TIMER, 
					p->idTimer, p->dwParam);

				RunThread_PostMessage(p->hTimerOwner, &msg, FALSE);
			}
		}

		// to process the next timer.
		pPrevTimer = pPrevTimer->next;
	}

	UNLOCK_TIMER_SYNC();	
}


#ifdef _TIMER_INIT_THREAD_DELAY_METHOD
static void *Timer_ThreadProc(IN void *lpArg)
{
    struct timeval tv, tvSaved;

	UNUSED(lpArg);

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Timer thread is starting...\n");
#endif //_DEBUG_RUN_TIMER

	tvSaved.tv_usec = s_TimerManager.nTimerResolution%1000*1000;/* usec     */
	tvSaved.tv_sec  = s_TimerManager.nTimerResolution/1000;		/* seconds  */
	
	while(TIMER_LIST_INITIALIZED())
	{
		tv = tvSaved;
		select(0,NULL,NULL,NULL, &tv);	// sleep

		// on timer.
		Timer_AlarmHander(0, NULL, NULL);

		s_TimerManager.nRunningCount++;
	}

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Timer thread exited.\n");
#endif //_DEBUG_RUN_TIMER

	s_TimerManager.nTimerCount = -1;// flag of quiting done

	return NULL;
}


static int Timer_ManagerInitThreadTimer(IN int nTimerResolution)
{
	if (pthread_create( &s_TimerManager.hTimer, 
		NULL,//&attr
		(PTHREAD_START_ROUTINE)Timer_ThreadProc,
		(void *)nTimerResolution) == 0)
	{
		pthread_detach(s_TimerManager.hTimer);
		return ERR_OK;
	}

	return -1;
}
#endif

#ifdef _TIMER_INIT_POSIX_TIMER_METHOD
/*==========================================================================*
 * FUNCTION : Timer_ManagerFunctionMethod
 * PURPOSE  : To init the POSIX timer.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nTimerResolution : resolution in ms.
 * RETURN   : static int : 
 * COMMENTS : 
 *==========================================================================*/
static int Timer_ManagerInitPosixTimer(IN int nTimerResolution) 
{
	struct sigevent sig_spec;
	struct itimerspec tmr_setting;

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Timer manager is initializting...\n");
#endif //_DEBUG_RUN_TIMER

	memset(&sig_spec, 0, sizeof(sig_spec));

// begin of alarm method. reserve it
//	struct sigaction sa;

	/* setup signal to respond to timer */
//	sigemptyset(&sa.sa_mask);

//	sa.sa_flags     = SA_SIGINFO;
//	(__sighandler_t)sa.sa_sigaction = (__sighandler_t)p;

//	if (sigaction(SIGRTMIN, &sa, NULL) < 0)
//	{
//		TRACEX("Fails(%d) on sigaction\n", errno);
//		return -1;
//	}
//
//	sig_spec.sigev_notify = SIGEV_SIGNAL;
//	sig_spec.sigev_signo  = SIGALRM;//SIGRTMIN;
// End of alarm method

	sig_spec.sigev_notify			= SIGEV_THREAD;
	sig_spec.sigev_signo			= 0;
	sig_spec.sigev_notify_function  = (void (*)(sigval_t))Timer_AlarmHander;
	sig_spec.sigev_value.sival_int  = SIGALRM;	// for compliant
	
	/* create timer, which uses the REALTIME clock */
	if (timer_create(CLOCK_REALTIME, &sig_spec,	&s_TimerManager.hTimer) < 0)
	{
		TRACEX("Fails(%d) on calling timer_create.\n", errno);
		return -1;
	}

	/* set the initial expiration and frequency of timer */
	tmr_setting.it_value.tv_sec     = 0;	// can NOT both be 0.
	tmr_setting.it_value.tv_nsec    = 1;	// 

	tmr_setting.it_interval.tv_sec  = nTimerResolution/1000;
	tmr_setting.it_interval.tv_nsec = nTimerResolution%1000*1000000;

	if (timer_settime(s_TimerManager.hTimer, 0, &tmr_setting, NULL) < 0)
	{
		TRACEX("Fails(%d) on calling timer_settime.\n", errno);
		return -1;
	}

	return ERR_OK;
}

#endif


#ifdef _TIMER_INIT_BSD_ALARM_METHOD

/*==========================================================================*
 * FUNCTION : Timer_ManagerInitBsdAlarmTimer
 * PURPOSE  : init the timer using BSD alarm method.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN int  nTimerResolution : 
 * RETURN   : static int : 
 * COMMENTS : 
 *==========================================================================*/
static int Timer_ManagerInitBsdAlarmTimer_Proc(IN int nTimerResolution)
{
	struct itimerval itv;
	int    nResult;

//	TRACEX("Timer_ManagerInitBsdAlarmTimer_Proc\n");

	// set timer alarm handler, and save the old one
	s_TimerManager.pOldHandler = (__sighandler_t)signal(SIGALRM,
		(__sighandler_t)Timer_AlarmHander);

	// start the timer
	/* Value to put into `it_value' when the timer expires.  */
	itv.it_interval.tv_sec	= nTimerResolution / 1000;
	itv.it_interval.tv_usec = nTimerResolution % 1000 * 1000;

	/* Time to the next timer expiration.  */
	itv.it_value.tv_sec		= itv.it_interval.tv_sec;
	itv.it_value.tv_usec	= itv.it_interval.tv_usec;

	nResult = setitimer( ITIMER_REAL, &itv, NULL );
	if( nResult != 0 )
	{
		perror( "setitimer( ITIMER_REAL, ...)" );

		// restore the old timer handler
		signal( SIGALRM, (__sighandler_t)s_TimerManager.pOldHandler );
	}

	while(TIMER_LIST_INITIALIZED()) 
	{
		sleep(1);
	}

	return ERR_OK;
}



static int Timer_ManagerInitBsdAlarmTimer(IN int nTimerResolution)
{
	pthread_t	hThread;

//	TRACEX("Init timer\n");

	if (pthread_create( &hThread, 
		NULL,//&attr
		(PTHREAD_START_ROUTINE)Timer_ManagerInitBsdAlarmTimer_Proc,
		(void *)nTimerResolution) == 0)
	{
		pthread_detach(hThread);

//		TRACEX("Init timer OK\n");

		return ERR_OK;
	}

//	TRACEX("Init timer failure\n");

	return -1;
}

#endif


/*==========================================================================*
 * FUNCTION : Timer_ManagerInit
 * PURPOSE  : To init the timer manager with given resolution
 * CALLS    : 
 * CALLED BY: Timer_Set
 * ARGUMENTS: IN int  nTimerResolution : in ms. by default the timer res
 *                                       is 20ms.
 * RETURN   : int : 
 * COMMENTS : 
 *==========================================================================*/
static int Timer_ManagerInit(IN int nTimerResolution)
{
	int		nResult = -1;

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Timer manager is starting...\n");
#endif //_DEBUG_RUN_TIMER

	if( nTimerResolution <= 0 )
	{
		return -1;
	}

	LOCK_TIMER_SYNC();

	if(!TIMER_LIST_INITIALIZED()) 
	{
		s_TimerManager.nTimerResolution = nTimerResolution;
		s_TimerManager.nTimerCount	    = 0;

		if (Timer_ManagerInitTimer(nTimerResolution) == 0)
		{
			nResult = 0;

			// init OK, register the atexit to destroy the timerHandler.
			atexit(Timer_ManagerExit);
		}
		else
		{
			s_TimerManager.nTimerCount	    = -1;	// failure.
		}
	}

	UNLOCK_TIMER_SYNC();

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Timer manager started OK.\n");
#endif //_DEBUG_RUN_TIMER

	return nResult;
}


/*==========================================================================*
 * FUNCTION : Timer_FindPreviousTimer
 * PURPOSE  : Find a timer
 * CALLS    : 
 * CALLED BY: internal use
 * ARGUMENTS: IN HANDLE  hTimerOwner : 
 *            IN int     idTimer     : 
 * RETURN   : static TIMER_ENTRY *: the previous timer of this timer.
 * COMMENTS : must lock the manager at first!
 *==========================================================================*/
static TIMER_ENTRY *Timer_FindPreviousTimer(IN HANDLE hTimerOwner,
											IN int idTimer)
{
	TIMER_ENTRY	*p;				// The current timer being processed
	TIMER_ENTRY	*pPrevTimer;	// the previuos timer of the current.

	pPrevTimer = (TIMER_ENTRY *)&s_TimerManager;
	p = pPrevTimer->next;
	
	while (p != NULL)
	{
		if ((p->hTimerOwner == hTimerOwner) && (p->idTimer == idTimer))
		{
			return pPrevTimer;
		}

		// to process the next timer.
		pPrevTimer = p;
		p		   = p->next;
	}

	return NULL;
}

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
			  IN ON_TIMER_PROC pfnAction, IN DWORD dwParam)
{
	int	nResult;

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Thread %p is setting timer #%d.\n", hTimerOwner, idTimer);
#endif //_DEBUG_RUN_TIMER

	// add the init code in timer_set. the user needn't call Timer_ManagerInit()
	// before calling Timer_Set().
	if (!TIMER_LIST_INITIALIZED())
	{
		Timer_ManagerInit(TIMER_RESOLUTION);
	}

	LOCK_TIMER_SYNC();

	// to check the timer has been added or not
	if (Timer_FindPreviousTimer(hTimerOwner, idTimer) == NULL)
	{
		TIMER_ENTRY	*p = NEW(TIMER_ENTRY, 1);
		
		if( p != NULL )
		{
			// set the timer params
			p->hTimerOwner	= hTimerOwner;
			p->idTimer		= idTimer;		// timer ID
			p->nInterval	= (nInterval >= s_TimerManager.nTimerResolution) ? nInterval 
				:s_TimerManager.nTimerResolution;	// timer interval, in ms
			p->pfnAction	= pfnAction;
			p->dwParam		= dwParam;

			p->nElapsed		= 0;		// the elapsed time.
//			p->next			= NULL;

			// add the timer to the head of the manager
			p->next						= s_TimerManager.pTimerList;
			s_TimerManager.pTimerList	= p;

			s_TimerManager.nTimerCount++;

			nResult = ERR_TIMER_OK;
		}
		else
		{
			TRACE("[Timer_Set] -- Out of memory on adding timer(%d).\n", idTimer);
			nResult = ERR_TIMER_SET_FAIL;
		}
	}

	else	// a timer can NOT be allowed to add twice.
	{
		TRACE("[Timer_Set] -- The timer(#%d) is already added.\n", idTimer);
		nResult = ERR_TIMER_EXISTS;
	}

	UNLOCK_TIMER_SYNC();	

	return nResult;
}


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
int Timer_Kill(IN HANDLE hTimerOwner, IN int idTimer)
{
	TIMER_ENTRY	*p;				// The current timer being processed
	TIMER_ENTRY	*pPrevTimer;	// the previuos timer of the current.

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Killing timer #%d.\n", idTimer);
#endif //_DEBUG_RUN_TIMER

	LOCK_TIMER_SYNC();

	// to check the timer has been added or not
	pPrevTimer = Timer_FindPreviousTimer(hTimerOwner, idTimer);
	if (pPrevTimer != NULL)
	{
		p                = pPrevTimer->next;	// remove from link
		pPrevTimer->next = p->next;

		DELETE(p);	// this timer will be deleted.

		s_TimerManager.nTimerCount--;
	}

	UNLOCK_TIMER_SYNC();	

	return (pPrevTimer != NULL) ? ERR_TIMER_OK: ERR_TIMER_NOT_FOUND;
}


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
int Timer_Reset(IN HANDLE hTimerOwner, IN int idTimer)
{
	TIMER_ENTRY	*pPrevTimer;	// the previuos timer of the current.

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Reseting timer #%d.\n", idTimer);
#endif //_DEBUG_RUN_TIMER

	LOCK_TIMER_SYNC();

	// to check the timer has been added or not
	pPrevTimer = Timer_FindPreviousTimer(hTimerOwner, idTimer);
	if (pPrevTimer != NULL)
	{
		pPrevTimer->next->nElapsed = 0;	// the elapsed time is cleaned
	}

	UNLOCK_TIMER_SYNC();	

	return (pPrevTimer != NULL) ? ERR_TIMER_OK: ERR_TIMER_NOT_FOUND;
}

/*==========================================================================*
 * FUNCTION : Timer_ManagerExit
 * PURPOSE  : Cleanup the timer manager.
 * CALLS    : 
 * CALLED BY: main
 * RETURN   : void : 
 * COMMENTS : 
 *==========================================================================*/
static void Timer_ManagerExit(void)
{
	TIMER_ENTRY	*p, *t;
#ifdef _TIMER_INIT_THREAD_DELAY_METHOD
	int		nLastCount;
#endif

#ifdef _DEBUG_RUN_TIMER
	TRACEX("Timer manager is exiting...\n");
#endif //_DEBUG_RUN_TIMER
	
	LOCK_TIMER_SYNC();

	if(TIMER_LIST_INITIALIZED()) 
	{
		s_TimerManager.nTimerCount = -1000;	// force the timer thread to quit.

		// stop timer now
#ifdef _TIMER_INIT_POSIX_TIMER_METHOD
		timer_delete(s_TimerManager.hTimer);
#endif

#ifdef _TIMER_INIT_BSD_ALARM_METHOD

		setitimer( ITIMER_REAL, NULL, NULL ); // OK ?

		signal( SIGALRM, (__sighandler_t)s_TimerManager.pOldHandler );
#endif

#ifdef _TIMER_INIT_THREAD_DELAY_METHOD
//		TRACEX("Wait for timer thread quit.\n");

		nLastCount = s_TimerManager.nRunningCount;

		while (s_TimerManager.nTimerCount != -1)
		{
			// sleep 20 times resolution of time. if the count do NOT change,
			// the thread has already quited.
			usleep((unsigned int)(20*1000*s_TimerManager.nTimerResolution));
			if (nLastCount == s_TimerManager.nRunningCount)
			{
//				TRACEX("The timer thread has already quited.\n");
				break;
			}
		}

//		TRACEX("The timer thread quited.\n");
#endif

		p = s_TimerManager.pTimerList;

		while( p != NULL )
		{
			t = p;

			p = p->next;

			DELETE( t );
		}

		s_TimerManager.pTimerList  = NULL;
		s_TimerManager.nTimerCount = -1;	// not initialized flag
	}

	UNLOCK_TIMER_SYNC();
}


