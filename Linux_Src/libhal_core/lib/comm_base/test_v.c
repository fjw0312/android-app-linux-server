
#include <signal.h>
#include "stdsys.h"		/* all standard system head files			*/
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"
#include "commvirtual.h"


#define TEST_DATA_LENGTH 512

/*static char *GetStandardPortDriverName(IN int nStdPortType)
{
	const char *pszStdDriver[] =
	{
		"comm_std_serial.so",
		"comm_hayes_modem.so",
		"comm_net_tcpip.so",
		"comm_acu_485.so"
		"comm_virtual_dev.so"
	};

	return (char *)pszStdDriver[nStdPortType];
}*/
static void dump_buf( char *buf, int n)
{
	while (n > 0)
	{
		printf( "%c", *buf ++);
		n --;
	}

	printf( "\n" );
}

 main()
{
	HANDLE	hPort;
	char	szBuf[3]="31";
	char	port_name[10]="com6";
	char	szBufRead[160];
	int     n, i;
	int	nErrCode = ERR_COMM_OK;

	printf(" szbuf is:%s\n",szBuf);
	
	hPort = CommOpen( "comm_virtual_dev.so",
		port_name, 
		" ", 
		0x00,
		5000,
		&nErrCode);
	
	if (hPort == NULL)
	{
		printf("open error!\n");
		return 0;
	}
	else printf("open ok\n");
	
	//test write the digit number to virtual dev
	n = CommWrite( hPort, szBuf, TEST_DATA_LENGTH );
	if(!n) printf("write error!\n");
	else printf("write ok!\n");
	
	//test read the write number from virtual dev
	//(IDU_VER_TYPE *)szBufRead= malloc(sizeof(IDU_VER_TYPE));
	n = CommRead(hPort, szBufRead, 160);
	if(n<=0) printf("read error!\n");
	else printf("read ok!\n");
	if((!strcmp(szBuf,"30"))&&(!strcmp(port_name,"com5")))
	{
		printf("szBufRead->version_id_all is %d,\n",((IDU_VER_TYPE *)szBufRead)->version_id_all);
		printf("szBufRead->version_id_single is %d,\n",((IDU_VER_TYPE *)szBufRead)->version_id_single);
		printf("szBufRead->version_pcb is %d,\n",((IDU_VER_TYPE *)szBufRead)->version_pcb);
		printf("szBufRead->version_cpld is %d,\n",((IDU_VER_TYPE *)szBufRead)->version_cpld);
		printf("szBufRead->compile_time_year is %d,\n",((IDU_VER_TYPE *)szBufRead)->compile_time_year);
		printf("szBufRead->compile_time_month is %d,\n",((IDU_VER_TYPE *)szBufRead)->compile_time_month);
		printf("szBufRead->compile_time_day is %d,\n",((IDU_VER_TYPE *)szBufRead)->compile_time_day);
		printf("szBufRead->version_description is %s,\n",((IDU_VER_TYPE *)szBufRead)->version_description);
	}
	if((!strcmp(szBuf,"31"))&&(!strcmp(port_name,"com5")))
	{
		printf("pRun->cpu_usage is %d\n",((IDU_RUN_TYPE *)szBufRead)->cpu_usage);
		printf("pRun->flash_used is %d\n",((IDU_RUN_TYPE *)szBufRead)->flash_used);
		printf("pRun->sdram_used is %d\n",((IDU_RUN_TYPE *)szBufRead)->sdram_used);
		printf("pRun->netflow_count is %ld\n",((IDU_RUN_TYPE *)szBufRead)->netflow_count);
		
	}
	if((!strcmp(szBuf,"30"))&&(!strcmp(port_name,"com6")))
	{
		printf("pIo->ai1 is %f\n",((IO_AI_TYPE *)szBufRead)->ai1);
		printf("pIo->ai2 is %f\n",((IO_AI_TYPE *)szBufRead)->ai2);
		printf("pIo->ai3 is %f\n",((IO_AI_TYPE *)szBufRead)->ai3);
		printf("pIo->ai4 is %f\n",((IO_AI_TYPE *)szBufRead)->ai4);
		printf("pIo->ai5 is %f\n",((IO_AI_TYPE *)szBufRead)->ai5);
		printf("pIo->ai6 is %f\n",((IO_AI_TYPE *)szBufRead)->ai6);
		printf("pIo->ai7 is %f\n",((IO_AI_TYPE *)szBufRead)->ai7);
		printf("pIo->ai8 is %f\n",((IO_AI_TYPE *)szBufRead)->ai8);
		printf("pIo->ai9 is %f\n",((IO_AI_TYPE *)szBufRead)->ai9);
		printf("pIo->ai10 is %f\n",((IO_AI_TYPE *)szBufRead)->ai10);
		printf("pIo->ai11 is %f\n",((IO_AI_TYPE *)szBufRead)->ai11);
		printf("pIo->ai12 is %f\n",((IO_AI_TYPE *)szBufRead)->ai12);
		printf("pIo->ai13 is %f\n",((IO_AI_TYPE *)szBufRead)->ai13);
		printf("pIo->ai14 is %f\n",((IO_AI_TYPE *)szBufRead)->ai14);
	}
	if((!strcmp(szBuf,"31"))&&(!strcmp(port_name,"com6")))
	{
		printf("di is %s\n",szszBufRead);
	}
	//printf("address of szBufRead is %i,\n",(IDU_VER_TYPE *)szBufRead);
	//dump_buf( szBufRead, n);
	
	//test close the virtual dev
	n=CommClose(hPort);
	if(n) printf("close wrong\n");
	else printf("close ok\n");
	
	return 1;
}
