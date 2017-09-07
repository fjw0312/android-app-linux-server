
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>
#include <signal.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <termios.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <mntent.h>
#include <sys/vfs.h>
#include <sys/file.h>

#include "stdsys.h"
#include "basetypes.h"
#include "pubfunc.h"
#include "halcomm.h"
#include "new.h"
#include "err_code.h"
#include "ShareMem.h"

#include "commvirtual.h"

///////////////////////////////////
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <pthread.h>
#include <linux/rtc.h>
#include <sys/timeb.h>
///////////////////////////////////

// the static variable to store the info of canceling the warning
//extern static char status_of_warning;	
//in order to indicate the system had ever write buffer1 at least one time, user can read data from now on
//extern  int read_key_to_write_once(); 

//-- add by ylf 2007.01.09 --
// 门禁和红外告警联动参数

BOOL       m_bIsOpenLamp;     // 是否已开灯的标识
IN time_t  m_tOpenLampTime;   // 开灯的时间，s	
//-- end of add by ylf 2007.01.09 --

//-- add by ylf 2006.12.15 --
// 目的：增加so库的日志文件功能

#define MAXLINELEN 4096       // LogOut一行数据最大长度  
#define APP_LOG_MAX_SIZE  100*1024  // log文件最大长度为100K
#define APP_HEX_MAX_SIZE  40*1024   // 16进制文件最大长度为40K

// 文件名定义
//#define IDU_DOOR_LOG_FILE       NULL            // 不写日志文件，显示在屏幕上 
#define IDU_DOOR_LOG_FILE       "/home/idu/comm_virtual.log"  // 日志文件,
#define IDU_DOOR_BAK_FILE       "/home/idu/comm_virtual.bak"  // 日志文件备份,

//*****************************************************************
// 函数名称：Log_Prn
// 功能描述：打印日志函数：
// 输入参数：logfile      -  日志文件名，为NULL时，屏幕打印
//           fmt          -  打印的字符串
//           ap           -  字符串格式
// 输出参数：
// 返    回：
// 其他：
//*****************************************************************
void Log_Prn( const char *fmt, va_list ap, char *logfile )
{
    //int save_err;
    char buf[MAXLINELEN] = { 0 };
    FILE *plf;

    //save_err = errno;
    vsprintf( buf, fmt, ap );
    //sprintf( buf+strlen(buf), "; %s", strerror(save_err) );
    //strcat( buf, "\x0D\x0A" );
    //fflush( stdout );

    struct timeb timebuffer;   
    char *timeline;
    char szTimeLine[80] = { 0 };

    ftime( &timebuffer );
    timeline = ctime( & ( timebuffer.time ) );    
    sprintf( szTimeLine, "%.19s.%03hu－", timeline, timebuffer.millitm );

    if( logfile != NULL )
    {
        if( (plf=fopen(logfile,"a")) != NULL )
        {
            fputs( szTimeLine, plf );
            fputs( buf, plf );
            
            // 取当前日志文件的长度
            long loffset = 0;
            if( fseek( plf, 0, SEEK_END ) == 0 )
            {
                loffset = ftell( plf );
            }      
            
            fclose( plf );


            // 判断日志文件是否超长，如超长，将其命名为bak文件
            if( loffset > APP_LOG_MAX_SIZE )
            {
                if( remove( "Idu_Door.bak" ) == -1 )
                {
                    fputs( szTimeLine, stderr );
                    fputs( "删除备份日志文件失败!\x0D\x0A", stderr );
                }

                if( rename( logfile, "Idu_Door.bak" ) == -1 )
                {
                    fputs( szTimeLine, stderr );
                    fputs( "备份日志文件失败!\x0D\x0A", stderr );
                    if( remove( logfile ) == -1 )
                    {
                        fputs( szTimeLine, stderr );
                        fputs( "删除日志文件失败!\x0D\x0A", stderr );
                    }
                }
                else
                {
                    fputs( szTimeLine, stderr );
                    fputs( logfile, stderr );
                    fputs( "备份日志文件成功!\x0D\x0A", stderr );
                }
            }
        }
        else
        {
            fputs( "failed to open log file\x0D\x0A", stderr );
        }
    }
    else
    {
        fputs( szTimeLine, stderr );
        fputs( buf, stderr );
    }

    //fflush( NULL );
    return;
}


//*****************************************************************
// 函数名称：LogOut
// 功能描述：记录日志文件函数：
// 输入参数：fmt          -  打印的字符串
// 输出参数：
// 返    回：
// 其他：
//*****************************************************************
void LogOut( const char *fmt, ... )
{
    va_list ap;

    va_start( ap, fmt );
    Log_Prn( fmt, ap, IDU_DOOR_LOG_FILE );
    va_end( ap );

    return;
}
//-- end add by ylf 2006.12.15 --

int get_device_selftest_from_driver(void)
{
	int fd,self_status;
	char data_read[10];
	char selftest_info_low,selftest_info_high;

	fd = open("/dev/eeprom",O_RDWR | O_NOCTTY);
	if(fd<0)
	{
		printf("open eeprom error!\n");
		return 0;
	}
	lseek(fd,11,0);
	read(fd,data_read,1);
	selftest_info_low=data_read[0];
	lseek(fd,10,0);
	read(fd,data_read,1);
	selftest_info_high=data_read[0];
	close(fd);
	self_status=(selftest_info_high<<8)+selftest_info_low;
	return self_status;
	//add some code to combine the two info byte and return it
	
	//printf("get device selftest result\n");
}

//return value: self_test result, now only have bat and brg device , we should add it laterly
//return value=-1, it is unknown device, so error!

char get_slot_selftest_from_driver(int dev_id,int num)
{
	int fd;
	char read_value;
	char cResult_Bat[2];
	//printf("get selftest result from driver/e2prom\n");
	if((dev_id==1)||(dev_id==2))
	{
			int iErrCode = SHMM_Read(256, cResult_Bat, 2*sizeof(char));
			if (iErrCode < 0)
			{
						printf("read share memory failed in reading slot self_test result, err code is %d! \n", iErrCode);
						return -1;
			}		
			if(num==1)
			{
					read_value=cResult_Bat[0];
					//return read_value;
			}
			if(num==2)
			{
					read_value=cResult_Bat[1];
					//return read_value;
			}
			/*
			//bat card
			//should change to eeprom/share memory
			if(num==1)
			{
					fd=open("/dev/bat0",O_RDONLY);
					if(fd<0){ printf("open driver bat error!\n");}
			}
			if(num==2)
			{
					fd=open("/dev/bat1",O_RDONLY);
					if(fd<0){ printf("open driver bat error!\n");}
			}
			ioctl(fd,IOCTL_BAT_GROUP_ONE,0);
			ioctl(fd,IOCTL_BAT_2V,0);
			lseek(fd,26,0);
			read(fd,&read_value,1);
			close(fd);
			// return reference of 0v group one channel 26
			//*/
			
			return read_value;
	}

	//-- changed by ylf 2006.09.15--
	// 目的：增加读取BRG1自检信息的支持
	//if(dev_id==4)

	if((dev_id == 4) || (dev_id == 6))
	//-- end changed by ylf 2006.09.15--
	{
		//brg card
		fd=open("/dev/brg",O_RDONLY);
		read(fd,&read_value,1);
		close(fd);

		//-- add by ylf 2006.12.15 --
		// 目的：打印brg自检异常信息
		/*
		if (read_value != 0)
		{// 有错误

			// 将错误信息输出到文件

			 char strBrgSlfCheckInfo[8];
			 memset(&strBrgSlfCheckInfo, 0, sizeof(strBrgSlfCheckInfo));

			 int i = 0;
			 for(i = 0; i < 8; i++)
			 {
				 char cTempValue = read_value; 
				 cTempValue <<= (7 - i);
				 cTempValue >>= 7;
				 strBrgSlfCheckInfo[i] = cTempValue;
			 }
			 //-- end of " for(i = 0; i < 8; i++) " --

			 //if ((strBrgSlfCheckInfo[4] == 1) || (strBrgSlfCheckInfo[5] == 1))
			 {
				 LogOut( "brg自检信息0x%x异常！\x0D\x0A", read_value);
				 for(i = 0; i < 8; i++)
				 {
					 LogOut( "brg自检信息位 %d 的值为 %d ！\x0D\x0A",
						 i, strBrgSlfCheckInfo[i]);
				 }
				 //-- end of " for(i = 0; i < 8; i++) " --
			 }
			 //-- end of " if ((strBrgSlfCheckInfo[4] == 1) || (strBrgSlfCheckInfo[5] == 1)) " --
		}
		//-- end of " if (read_value != 0) " --
		//*/
		//-- end add by ylf 2006.12.15 --

		return read_value;
	}
	//printf("unknown device\n");
	//no card in slot
	if(dev_id==15)
	{
		return 0x3F;
	}
}

int get_slot_status_from_driver(int num)
{
	int fd,card1_type,card2_type;
	char data_read[10];
	
	fd = open("/dev/slot", O_RDWR | O_NOCTTY);
	//if fd return 0,it is ok,else it is wrong
	if(fd<0)
	{
		printf("open device error!\n");
		return 0;
	}
	read (fd,data_read,1);
	//printf("data_read=%x\n",data_read[0]);
	close(fd);
	//read the low 4 bit to be as card1_type
	card1_type=data_read[0]&0x0F;
	//read the high 4 bit to be as card2_type
	card2_type=data_read[0]>>4;
	if(num==1) return card1_type;
	else if (num==2) return card2_type;
	else return 0;
	//add code to analyse the data_read info, and return value to user
	
	//printf("get slot status from driver\n");
	
}

void DBGetCpuInfo(int *CpuOccupancy)
{
	unsigned long n_user;
	unsigned long n_nice;
	unsigned long n_system;
	unsigned long n_idle;
	FILE *fp;
	char buf[256];
	char name[]="cpu";
	int  nl = 0 ;
	float  tmp;
	
	fp=fopen(_PATH_CPUINFO,"rt");
	if(!fp)
	{
		printf ("Can't open file %s\n",_PATH_CPUINFO );
		n_user=0;
		n_nice=0;
		n_system=0;
		n_idle=0;
		return;
	}
	while (fgets(buf, sizeof(buf), fp)!= NULL)
	{
		if(nl>=0)
		{
			int ifl = 0;
			while(buf[ifl]!=':' && buf[ifl]!='\t' && buf[ifl]!='\0'&& buf[ifl]!=' ')
				ifl++;
			//printf ("buf=%s search=%s ifl=%d\n", buf,name,ifl );
			if( strstr(buf,name) )
			{
				//printf("seached! cpu info = %s\n", buf+ifl+1);
				if( sscanf(buf+ifl+1,"%ld%ld%ld%ld", &n_user, &n_nice, &n_system, &n_idle) != 4 )
				{
					n_user=0;
					n_nice=0;
					n_system=0;
					n_idle=0;
					fclose(fp);
					return;
				}
				fclose ( fp );
				//printf("user:%ld,nice:%ld,system:%ld,idle:%ld\n",n_user,n_nice,n_system,n_idle);
				tmp =(float)((n_user+n_nice+n_system)*100)/(float)(n_user+n_nice+n_system+n_idle);
				//printf("cpu occupancy is about %f\n", tmp);
				*CpuOccupancy = tmp;
				return;
			}
			ifl=0;
		}
		nl++;
	}
	fclose(fp);
}

void DBGetFlashInfo(int *FlashOccupancy)
{
	FILE *mount_table;
	struct mntent *mount_entry;
	struct statfs s;
	const char *device;
	const char *mount_point;
	long blocks_used;
	long blocks_percent_used;

	
	mount_table = NULL;
	if (!(mount_table = setmntent("/proc/mounts", "r"))) 
	{
		printf("set /proc/mounts error!\n");
		return;
	}
	
	if (mount_table) 
	{
		if (!(mount_entry = getmntent(mount_table))) 
		{
			endmntent(mount_table);
			printf("can't get mount table!\n");
			return;
		}
	}
	device = mount_entry->mnt_fsname;
	mount_point = mount_entry->mnt_dir;
	if (statfs(mount_point, &s) != 0) 
	{
		printf("can not get s struct!\n");
		return;
	}
	if ((s.f_blocks > 0) || !mount_table)
	{
		blocks_used = s.f_blocks - s.f_bfree;
		blocks_percent_used = 0;
		if (blocks_used + s.f_bavail) 
		{
			blocks_percent_used = (((long long) blocks_used) * 100
							   + (blocks_used + s.f_bavail)/2
							   ) / (blocks_used + s.f_bavail);
		}
		//printf("flash occupancy : %3ld\n", blocks_percent_used);
		*FlashOccupancy= blocks_percent_used;
	}
	//add it to close mount_table handle
	endmntent(mount_table);
}

void DBGetMemInfo(int *MemOccupancy)
{
	FILE *fp;
	int  nl = 0 ;
	float  tmp;
	char buff[256];
	char name1[]="MemTotal";
	char name2[]="MemFree";
	unsigned long mem_total;
	unsigned long mem_free;
	
	fp=fopen(_PATH_MEMINFO,"rt");
	if(!fp)
	{
		printf ("Can't open file %s\n",_PATH_MEMINFO );
		mem_total=0;
		mem_free=0;
		return;
	}
	while(fgets(buff, sizeof(buff), fp)!= NULL)
	{
		if(nl>1)
		{
			int ifl = 0;
			while(buff[ifl]!=':' && buff[ifl]!='\t' && buff[ifl]!='\0')
				ifl++;
		//	buff[ifl]=0;    /* interface */
		//	printf ("interface=%s search=%s\n", buff,interface );
		
			if ( strstr( buff, name1) )
			{
				//printf ("searched! mem_total=%s\n", buff+ifl+1 );
				if(sscanf(buff+ifl+1,"%ld",&mem_total)!=1)
				{
					mem_total=0;
					fclose(fp);
					return;
				}
			}
			if ( strstr( buff, name2) )
			{
			//	printf ("searched! mem_free=%s\n", buff+ifl+1 );
				if(sscanf(buff+ifl+1,"%ld",&mem_free)!=1)
				{
					mem_free=0;
					fclose(fp);
					return;
				}
				fclose(fp);
				//in order to enhance the efficence of program, only found the second data,the program
				//will return, so put the calculation here!
				tmp=((float)mem_free * 100)/(float)mem_total;
				//printf("tmp=%f\n", tmp);
				*MemOccupancy=100 - tmp;
				return;
			}
			ifl=0;					
		}
		nl++;
	}
	fclose(fp);
}

void DBGetPacketRTX(char *interface,unsigned long *rx_bytes,unsigned long *tx_bytes)
{
	FILE *fp;
	char buff[256];
	int  nl = 0 ;

	unsigned long r_bytes,r_packets,r_errs,r_drop,r_fifo, r_frame, r_compressed,r_multicast;
	unsigned long t_bytes,t_packets,t_errs,t_drop,t_fifo, t_frame, t_compressed,t_multicast;

	fp = fopen ( _PATH_PACKETRTX, "rt" );
	if (!fp)
	{
		printf ("Can't open file %s\n",_PATH_PACKETRTX );
		*rx_bytes = 0;
		*tx_bytes = 0;
		return;
	}

	while( fgets(buff, sizeof(buff), fp) != NULL )
	{
		if(nl>1)
		{
			int ifl = 0;
			while(buff[ifl]!=':' && buff[ifl]!='\t' && buff[ifl]!='\0')
				ifl++;
		//	buff[ifl]=0;    /* interface */
		//	printf ("interface=%s search=%s\n", buff,interface );

			if ( strstr( buff, interface) )
			{
		//		printf ("searched! S=%s\n", buff+ifl+1 );
				if(sscanf(buff+ifl+1, "%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld%ld",
			    		&r_bytes,&r_packets,&r_errs,&r_drop,&r_fifo, &r_frame, &r_compressed,&r_multicast,
			    		&t_bytes,&t_packets,&t_errs,&t_drop,&t_fifo, &t_frame, &t_compressed,&t_multicast  )!=16)
				{
					*rx_bytes = 0;
					*tx_bytes = 0;
					fclose(fp);
					return;
				}
				fclose(fp);
				*rx_bytes = r_bytes;
				*tx_bytes = t_bytes;
//				printf ( "Packet: t_bytes: %d", t_bytes );
				return;

			}

			ifl = 0;
        	}
		nl++;
	}

	*rx_bytes = 0;
	*tx_bytes = 0;
	fclose(fp);
}

int get_info_from_IDU(int channel)
{
	unsigned long ulRx_bytes1,ulRx_bytes2;
	unsigned long ulTx_bytes1,ulTx_bytes2,tmp;
	int mem_occup,cpu_occup,flash_occup,net_flow;
	char ifname[]="eth0";

	switch ( channel )
	{
		case self_test:
		{
			//add code here to check the result of self-test
		}
		break;

		case cpu_occupancy:
		{
			DBGetCpuInfo( &cpu_occup);
			return cpu_occup;		
		}
		break;
		
		case flash_occupancy:
		{
			DBGetFlashInfo( &flash_occup);
			return flash_occup;
		}
		break;
		
		case mem_occupancy:
		{
			DBGetMemInfo( &mem_occup );
			return mem_occup;
		}
		break;
		
		case network_flow:
		{
			DBGetPacketRTX( ifname,&ulRx_bytes1, &ulTx_bytes1 );
			//delay(1s) need to add code here
			sleep(1);
			DBGetPacketRTX( ifname,&ulRx_bytes2, &ulTx_bytes2 );
			//return KByte,so move ten bit to right = netflow/1024
			tmp = ((ulRx_bytes2+ulTx_bytes2)-(ulRx_bytes1+ulTx_bytes1));
			net_flow = tmp>>10;
			return net_flow;
			
		}
		break;
		
		default:
		{
			printf("error message from get_info_from_IDU\n");
			return -1;
		}
	}
}

#define TTYS_ONLY_NAME		"ttyS"
#define TTYS_FULL_PREFIX	"/dev/ttyS"
#define TTYS_ALIAS_NAME		"COM"
#define CONST_STRLEN(const_str)	(sizeof(const_str)-1)
/*==========================================================================*
 * FUNCTION : Virtual_GetDescriptor
 * PURPOSE  : Convert the non-standard serial device name such as ttyS0, ttyS1,
 *            to /dev/ttyS0, /dev/ttyS1..., and also convert COM1, COM2, ..., to
 *            /dev/ttyS0, /dev/ttyS1, and also convert "1", "2" to /dev/ttyS0, 
 *            /dev/ttyS1....
 * CALLS    : 
 * CALLED BY: Virtual_CommOpen
 * ARGUMENTS: IN char   *pPortDescriptor : 
 *            OUT char  *pStdDescriptor  : 
 * RETURN   : static int : the actual serial port start from 0, -1 for error. 
 * COMMENTS : 
 * CREATOR  : Wan Kun                DATE: 2006-05-05 17:26
 *==========================================================================*/
static int Virtual_GetDescriptor(IN char *pPortDescriptor,
				 OUT char *pStdDescriptor)
{
	char	*pComNum = NULL;
	int	nComNumStart, nComNum,tmp;

	// 1. Test if the name is full name or not. 
	//    this comparison is case sensitive
	if (strncmp(pPortDescriptor, TTYS_FULL_PREFIX,
		CONST_STRLEN(TTYS_FULL_PREFIX)) == 0)
	{
		// the full name is used.
		pComNum = pPortDescriptor + CONST_STRLEN(TTYS_FULL_PREFIX);
		nComNumStart = 0;	// /dev/ttySn, the n starts from 0.
	}

	// 2. to test if only the name, the /dev/ does not exist. 
	//    this comparison is case sensitive
	else if (strncmp(pPortDescriptor, TTYS_ONLY_NAME,
		CONST_STRLEN(TTYS_ONLY_NAME)) == 0)
	{
		// only the name is used.
		pComNum = pPortDescriptor + CONST_STRLEN(TTYS_ONLY_NAME);
		nComNumStart = 0;	// ttySn, the n starts from 0.
	}

	// 3. to the alias name COMx is used or not.
	//    this comparison is NOT case sensitive
	else if (strnicmp(pPortDescriptor, TTYS_ALIAS_NAME,
		CONST_STRLEN(TTYS_ALIAS_NAME)) == 0)
	{
		// the alias name is used.
		pComNum = pPortDescriptor + CONST_STRLEN(TTYS_ALIAS_NAME);
		nComNumStart = 1;	// COMn, the n starts from 1.
	}

	// 4. we consider only the port num such as 1, 2,... is passed in.
	else 
	{
		pComNum = pPortDescriptor;
		nComNumStart = 1;	// only the com num 'n', the n starts from 1.
	}

	//nComNum = (pComNum[0]-'0') - nComNumStart;	// the actual serial port num

	// 5. to test the comm number is correct or not.
	//    the comm num shall be a digital string.
	//    IDU virtual device only have:com10,com11,com21,com31
	//     modified by wankun
	if(sscanf(pComNum,"%d",&nComNum)!=1) printf("read desicriptor is error!\n");
	nComNum = nComNum - nComNumStart;	
	if ((nComNum < 0) || (nComNum > 49))
	{
		printf("[Serial_GetDescriptor] -- The port descriptor %s is invalid.\n",
			pPortDescriptor);

		return -1;
	}	
	if ( !strcmp(pComNum,"5")) return IDU_Virtual_Device;
	else if ( !strcmp(pComNum,"6")) return IO_Virtual_Device;
	else if ( !strcmp(pComNum,"7")) return DOOR_Open_Device;
	else if (!strcmp(pComNum,"11"))
		{
		   //printf("now make judgement from driver\n");
		   // add a proc/func here to make choice!
		   tmp=get_slot_status_from_driver(1);
		   switch(tmp)
		   {
		   	case 1: 
			case 2: return BAT_Virtual_Device_Slot1;
			break;
			case 3: return EXTEND_Com_Device;
			break;
			case 4: return BRIDGE_E1_Ether_Device;
			break;
			case 5: return DCM_Device;
			break;
		   }
		}
	else if (!strcmp(pComNum,"21"))
		{
		   tmp=get_slot_status_from_driver(2);
		   switch(tmp)
		   {
		   	case 1: 
			case 2: return BAT_Virtual_Device_Slot2;
			break;
			case 3: return EXTEND_Com_Device;
			break;
			case 4: return BRIDGE_E1_Ether_Device;
			break;
			case 5: return DCM_Device;
			break;
		   }	
		}
	else
	{
		TRACE("[Virtual_GetDescriptor] -- The port descriptor %s is invalid.\n",
			pPortDescriptor);

		return NO_Virtual_Device;
	}
}

/*==========================================================================*
 * FUNCTION : Virtual_CommOpen
 * PURPOSE  : To open a virtual communication port.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN char   *pPortDescriptor : The serial descriptor in Linux, 
 *				       "10"/"!!"/"21"/"31"
 *            IN char   *pOpenParams     : nothing
 *            IN DWORD  dwPortAttr       : B00=COMM_SERVER_MODE for server port
 *                                         B00=COMM_CLIENT_MODE for client port
 *            IN int    nTimeout         : Open timeout in ms
 *            OUT int   *pErrCode        : prt to save error code.
 * RETURN   : HANDLE : 
 * COMMENTS : 
 * CREATOR  : Wan Kun                DATE: 2006-05-07 17:55
 *==========================================================================*/
HANDLE Virtual_CommOpen(
	IN char		*pPortDescriptor,
	IN char		*pOpenParams,
	IN DWORD	dwPortAttr,
	IN int		nTimeout,
	OUT int		*pErrCode)
{
	//-- add by ylf 2007.01.09 --
	// 门禁和红外告警联动参数

	m_bIsOpenLamp    = FALSE;   // 是否已开灯的标识
	m_tOpenLampTime  = 0;       // 开灯的时间，s	
	//-- end of add by ylf 2007.01.09 --

	//printf("in virtual now!\n");----add by wankun
	VIRTUAL_PORT_DRV *pPort = NEW(VIRTUAL_PORT_DRV, 1);
	
	// full port device name, like: /dev/ttyS0
	char	szFullDescriptor[sizeof(TTYS_FULL_PREFIX)+2];
	
	/* 1. get mem */
	if (pPort == NULL)
	{
		*pErrCode = ERR_COMM_NO_MEMORY;
		return	NULL;
	}

	memset(pPort, 0, sizeof(VIRTUAL_PORT_DRV));

	/* 2. parse descriptor */
	pPort->VirtualDevice = Virtual_GetDescriptor(pPortDescriptor, szFullDescriptor);
	//printf("now virtual device is %d\n",pPort->VirtualDevice);
	if (pPort->VirtualDevice < 0)
	{
#ifdef _DEBUG_VIRTUAL_PORT
		TRACE("[Virtual_CommOpen] -- invalid port descriptor: \"%s\".\n",
			pPortDescriptor);
#endif //_DEBUG_VIRTUAL_PORT

		DELETE(pPort);
		*pErrCode = ERR_COMM_OPENING_PARAM;

		return	NULL;
	}

	//open share memory
	if(pPort->VirtualDevice ==IO_Virtual_Device)
	{
		key_t keyShmm = SHMM_Open(0); 
		if (keyShmm < 0)
		{
			printf("open share memory failed, err code is %d \n", keyShmm);
			return -1;
		}
		else
		{
			//printf("open share mem %d success \n", keyShmm);
		}
	}
	INIT_TIMEOUTS(pPort->toTimeouts, nTimeout, nTimeout);
	//to initialize the key to read once from up layer
	pPort->KeyToReadOnce=0;
	
	// open the serial port
	*pErrCode = ERR_COMM_OK;

	return (HANDLE)pPort;
}

HANDLE Virtual_CommAccept(IN HANDLE hPort)
{
	return hPort;
}

static void dump_buf( char *buf, int n)
{
	while (n > 0)
	{
		printf( "%c", *buf ++);
		n --;
	}

	printf( "\n" );
}

//extern void clear_warning_of_di();
//extern int sm_read_int(int channel, int *value);
//extern int sm_read_float(int channel,float *value );
extern int SHMM_Read(int nChannelNo, char* pBuf, int nLen);
char read_relay_data()
{
	char buf;
	int fd;
	//must add code here to gain relay_data from os driver
	fd = open("/dev/relay", O_RDWR| O_NOCTTY);
	if(fd <=0)
	{
		printf("failed to open relay\n");
		return 0;
	}
	read(fd, &buf,1);
	close(fd);
	
	return buf;
}

/* The "CommRead" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Virtual_CommRead
 * PURPOSE  : Read data from a opened serial port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort        : the opened port
 *            OUT char   *pBuffer     : The buffer to save received data
 *            IN int     nBytesToRead :
 * RETURN   : int : the actual read bytes.
 * COMMENTS : The caller shall check the bytes to read is equal to the returned
 *            bytes to decide the read action is successful or not.
 * CREATOR  : Wan Kun                DATE: 2006-05-07 21:17
 *==========================================================================*/
int Virtual_CommRead(IN HANDLE hPort, OUT char *pBuffer, IN int nBytesToRead)
{
	VIRTUAL_PORT_DRV *pPort     = (VIRTUAL_PORT_DRV *)hPort;
	//int value_from_IDU = 0;
	//float float_from_IO = 0.0;
	//float float_from_BAT=0.0;
	char buff[4];
	//IDU_RUN_TYPE *pRun=NEW(IDU_RUN_TYPE,1);
	//IDU_VER_TYPE *pVer=NEW(IDU_VER_TYPE,1);
	//IO_AI_TYPE *pIo=NEW(IO_AI_TYPE,1);
	//BAT_AI_TYPE *pBat=NEW(BAT_AI_TYPE,1);
	float out_array[68];
	//float ai_value_from_io[14];
	float tmp;
	int i,rc;
	//int digit_from_io = 0;
	//int relay_from_io = 0;
	//int key_to_write_once = 0;
	char buf,digit_to_char;
	
	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;
	//printf("in line 560\n");
#ifdef _test
	//printf("channel=%d\n",pPort->ChannelId);
	sprintf(buff,"%d", pPort->ChannelId);
	strcpy(pBuffer,buf);
	//dump_buf( pBuffer, (int)strlen(&pBuffer));
	//printf("length=%d\n",(int)strlen(&pBuffer));
	return strlen(pBuffer);//use (int)strlen(pBuffer) in gcc have problem!
#else
	//make judgement to the size of read
	if(nBytesToRead>max_wr_size)
	{
		printf("the size of you reading is large than the size of buffer!\n");
		return 0;
	}
	//read all ai data of io from share memory
	

	//put data to every device
	//printf("now the pPort->VirtualDevice is %d,pPort->CmdId is %d\n",pPort->VirtualDevice,pPort->CmdId);
	switch(pPort->VirtualDevice)
	{
		case IDU_Virtual_Device:
		{
			switch ( pPort->CmdId )
			{
				case cmd_idu_ver_info:	
				{
					//(IDU_VER_TYPE *)pBuffer= NEW(IDU_VER_TYPE,1);
					//IDU_VER_TYPE *pVer=NEW(IDU_VER_TYPE,1);
					IDU_VER_TYPE *pVer 		= (IDU_VER_TYPE *)pBuffer;
					
					//IDU_VER_TYPE *pVer=malloc(sizeof(IDU_VER_TYPE));
					pVer->version_id_all		= IDU_VER_ALL;
					pVer->version_id_single 	= IDU_VER_SINGLE;
					pVer->version_pcb 		= IDU_VER_PCB;
					pVer->version_cpld 		= IDU_VER_CPLD;
					pVer->compile_time_year 	= IDU_COMPILE_YEAR;
					pVer->compile_time_month 	= IDU_COMPILE_MONTH;
					pVer->compile_time_day 		= IDU_COMPILE_DAY;
					strcpy(pVer->version_description,IDU_VER_DESCRIPTION);
					//copy the content of struct of pver to pbuffer
					//(IDU_VER_TYPE *)pBuffer= malloc(sizeof(IDU_VER_TYPE));
					//memcpy((IDU_VER_TYPE *)pBuffer, pVer, sizeof(IDU_VER_TYPE));
					//printf("pbuffer->compile_time_day is %d,\n",((IDU_VER_TYPE *)pBuffer)->compile_time_day);
					//printf("address of pBuffer is %i,\n",pBuffer);
					//DELETE(pVer);
					//return the length of pBuffer
					nBytesToRead = sizeof(IDU_VER_TYPE);
					//printf("return value =%d\n",nBytesToRead);
				}
				break;
				case cmd_idu_run_info:
				{
					IDU_RUN_TYPE *pRun 		= (IDU_RUN_TYPE *)pBuffer;
					//IDU_RUN_TYPE *pRun=NEW(IDU_RUN_TYPE,1);
					pRun->slot1_status 		= (pPort->RunInfo).slot1_status;
					pRun->slot2_status 		= (pPort->RunInfo).slot2_status;
					pRun->slot1_selftest_info 	= (pPort->RunInfo).slot1_selftest_info;
					pRun->slot2_selftest_info 	= (pPort->RunInfo).slot2_selftest_info;
					pRun->device_selftest_info 	= (pPort->RunInfo).device_selftest_info;
					//printf("in read cpu_usage is %d \n",(pPort->RunInfo).cpu_usage);
					pRun->cpu_usage 		= (pPort->RunInfo).cpu_usage;
					pRun->flash_used 		= (pPort->RunInfo).flash_used;
					//printf("in read flash_usage is %d \n",(pPort->RunInfo).flash_used);
					pRun->sdram_used 		= (pPort->RunInfo).sdram_used;
					pRun->netflow_count 		= (pPort->RunInfo).netflow_count;
					//copy the content of struct of prun to pbuffer
					//memcpy((IDU_RUN_TYPE *)pBuffer, pRun, sizeof(IDU_RUN_TYPE));
					//DELETE(pRun);
					//return the length of pBuffer
					nBytesToRead = sizeof(IDU_RUN_TYPE);
				}
				break;
			}
		}
		break;
		
		case IO_Virtual_Device:
		{
			switch ( pPort->CmdId )
			{
				case cmd_io_ai:
				{

					int iErrCode = SHMM_Read(1, pBuffer, 14*sizeof(float));
					if (iErrCode < 0)
					{
						printf("read share memory failed, err code is  %d ! \n", iErrCode);
						return -1;
					}
					nBytesToRead = 14*sizeof(float);
					/*
					float shmmRecInfo[14];
					SHMM_Read(1, (char*)(&shmmRecInfo), sizeof(shmmRecInfo));
					for ( i = 0; i < 14; i++)
					{
					       //int nLenTest = i * sizeof(float);
					       printf("ch=%d %f\n", (i+1), shmmRecInfo[i]);
					}
					*/
				}
				break;
				case cmd_io_di:
				{
					//read the data of common area, add code here
					int iErrCode = SHMM_Read(0, pBuffer, sizeof(char));
					if (iErrCode < 0)
					{
						printf("read share memory failed, err code is %d! \n", iErrCode);
						return -1;
					}
					
					//-- add by ylf 2007.01.09 --
					// 门禁和红外告警联动参数					

					// 分拆所有DI通道的值到各自通道的结构体
					char cAllIoDiInfo[5];
					memset(&cAllIoDiInfo, 0, sizeof(cAllIoDiInfo));
					int i = 0;
					for (i = 0; i < 5; i++)
					{
						char cTempValue = *pBuffer;
						cTempValue <<= (7 - i); // 左移 7 - i 位
						cTempValue >>= 7; // 右移 7 位

						cAllIoDiInfo[i] = cTempValue;
					}
					//-- end of " for (i = 0; i < 5; i++) " --	
					
					if ((cAllIoDiInfo[2] == 0) || (cAllIoDiInfo[3] == 0))
					{// 有告警
						
						if (!m_bIsOpenLamp)
						{// 尚未开灯
							
							// 设置第4个do为恒高电平输出
							int rc = set_relay_value_to_io(4, 1 & 0x1);//cng change it
							if(!rc) 
							{
								printf("open or close relay failed\n");
								return 0;
							}

							//printf("设置第4个do为恒高电平输出成功！\n");
						}
						//-- end of " if (!m_bIsOpenLamp) " --
						
						// 更新开灯的时间
						m_tOpenLampTime = time(NULL);
						
						// 设置为已开灯
						m_bIsOpenLamp = TRUE; 						
					}
					else
					{// 没有告警
						
						if (m_bIsOpenLamp)
						{// 如果已开灯
							
							IN time_t  tCurrTime = time(NULL);
							if (tCurrTime - m_tOpenLampTime > 60)
							{// 关灯时间已到
								
								// 设置第4个do为恒低电平输出
								int rc = set_relay_value_to_io(4, 0 & 0x1);//cng change it
								if(!rc) 
								{
									printf("open or close relay failed\n");
									return 0;
								}

								//printf("设置第4个do为恒低电平输出成功！\n");
								
								// 设置为已关灯
								m_bIsOpenLamp = FALSE;     
							}
							//-- end of " if (tCurrTime - m_tOpenLampTime > 60) " --
						}
						//-- end of " if (m_bIsOpenLamp) " --
					}
					//-- end of " if ((cAllIoDiInfo[2] == 0) || (cAllIoDiInfo[3] == 0)) " --
					//-- end of add by ylf 2007.01.09 --

					//clear the status of waring keep
					iErrCode=SHMM_SetReadedStatus();
					if(iErrCode<0)
					{
						printf("clear readed status failed , the errcode is %d\n", iErrCode);
					}
					//return the length of pBuffer
					nBytesToRead = sizeof(char);
				};
				break;
				case cmd_io_relay_status:
				{
					//read the data of common area, add code here
					//relay_from_io = (long int)out_array[17];
				/*	if(key_to_write_once)
					{
						rc=sm_read_int(channel_relay_io,&relay_from_io);
						if(!rc)
						{
							printf("sm read relay data of io error\n");
							return -3;
						}
						printf("relay status = %d\n",relay_from_io);
					}
					else
					{
						printf("need wait for data ready!\n");
						return -1;
					}
					sprintf(pBuffer,"%d",relay_from_io);
					//return the length of pBuffer
					nBytesToRead = sizeof(long int);
				*/
					//use the relay driver to read the status of relay
					buf=read_relay_data();
					buf=buf&0x1F;
					//buf=buf&0xf8;//cng change it
					//because the sequence is opp,so change its sequence
					//buf=((buf<<7)&0x80)|((buf<<5)&0x40)|((buf<<3)&0x20)|((buf<<1)&0x10)|((buf>>1)&0x08)|((buf>>3)&0x04)|((buf>>5)&0x02)|((buf>>7)&0x01);
					//printf("the status of relay is %x\n",buf);
					memcpy(pBuffer,&buf,sizeof(char));
					nBytesToRead = sizeof(char);
				}
				break;
				case cmd_io_relay_set:
				{
				//need  delivery a feedback to user , then add code here!
				}
				break;
			}
		}
		break;
		
		case BAT_Virtual_Device_Slot1:
		{
			
			switch ( pPort->CmdId )
			{
				case cmd_bat_one:
				{
					//read the data of common area, add code here
					//we should change here in detail
					int iErrCode = SHMM_Read(101, pBuffer, 25*sizeof(float));
					if (iErrCode < 0)
					{
						printf("read share memory failed, err code is %d! \n", iErrCode);
						return -1;
					}
				}
				break;
				case cmd_bat_two:
				{
					//read the data of common area, add code here
					//we should change here in detail
					int iErrCode = SHMM_Read(131, pBuffer, 25*sizeof(float));
					if (iErrCode < 0)
					{
						printf("read share memory failed, err code is %d! \n", iErrCode);
						return -1;
					}
				}
				break;
			}
			//return the length of pBuffer
			nBytesToRead = 25*sizeof(float);	
		}
		break;

		case BAT_Virtual_Device_Slot2:
		{
			
			switch ( pPort->CmdId )
			{
				case cmd_bat_one:
				{
					//read the data of common area, add code here
					//we should change here in detail
					int iErrCode = SHMM_Read(201, pBuffer, 25*sizeof(float));
					if (iErrCode < 0)
					{
						printf("read share memory failed, err code is %d! \n", iErrCode);
						return -1;
					}
				}
				break;
				case cmd_bat_two:
				{
					//read the data of common area, add code here
					//we should change here in detail
					int iErrCode = SHMM_Read(231, pBuffer, 25*sizeof(float));
					if (iErrCode < 0)
					{
						printf("read share memory failed, err code is %d! \n", iErrCode);
						return -1;
					}
				}
				break;
			}
			//return the length of pBuffer
			nBytesToRead = 25*sizeof(float);	
		}
		break;		
		
		case DOOR_Open_Device:
		{
			//if need give a feedback to user, just add code here!
		}
		break;
		
		case EXTEND_Com_Device:
		{
			//add code here
		}
		break;

		case BRIDGE_E1_Ether_Device:
		{
			//add code here
		}
		break;
		
		case DCM_Device:
		{
			//add code here
		}
		break;
		
		case NO_Virtual_Device:
		{

		}
		break;
	}
	// to tell up layer you had already red it , return zero indicate no need to read again
	if(pPort->KeyToReadOnce)
	{
		pPort->KeyToReadOnce=0;//reset the key to read once
		return nBytesToRead;	
	}
	else
	{
		return 0;
	}
#endif //end of ifdef _test
}

//extern int set_relay_value_to_io(int channel, int value);
int set_relay_value_to_io(int channel, int value)
{
	char relay_out_value_orig;
	int fd;
	
	//read the original value of relay
	//relay_out_value_orig=read_relay_data();cng change it
	//printf("in set prog the status of relay is %x\n",relay_out_value_orig);
	//judge the status of relay , changed or not
	//if(((relay_out_value_orig << (channel-1)) & 0x80) != value )cng change it
	{
		/*
		//if changed, just modify the right bit of out data
		relay_out_value_new = relay_out_value_orig ^ (0x01 <<(channel-1));
		printf("relay_out_value_new=%d\n",0x01 <<(channel-1));
		//output its new value
		rn = write_relay_data(relay_out_value_new);
		if(!rn) 
		{
			printf("error in output value of relay!\n");
			return 0;
		}*/
		fd = open("/dev/relay", O_RDWR| O_NOCTTY);
		if(fd <=0)
		{
			printf("failed to open relay\n");
			return 0;
		}
		if(value)
		{
			//value=1,write relay on
			write(fd,cmd_buf1+(channel-1),1);
			//printf("relay on\n");
		}
		else
		{
			//value=0,write relay off
			write(fd,cmd_buf2+(channel-1),1);
			//printf("relay off\n");
		}
		close(fd);
		
	}
	return 1;
}
/* The "CommWrite" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Virtual_CommWrite
 * PURPOSE  : write a buffer to a port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort         : 
 *            IN char    *pBuffer      : 
 *            IN int     nBytesToWrite : 
 * RETURN   : int : =0 for error.  = nBytesToWrite ok
 * COMMENTS : 
 * CREATOR  : Wan Kun                DATE: 2006-05-07 21:36
 *==========================================================================*/
int Virtual_CommWrite(IN HANDLE hPort, IN char *pBuffer,IN int nBytesToWrite)
{
	VIRTUAL_PORT_DRV *pPort     = (VIRTUAL_PORT_DRV *)hPort;
	int				id_channel = 0;
	int rc,d_type,door_status,d_length,relay_value, i;
	int low,high;
	char d_out[24];

	/* clear last error */
	pPort->nLastErrorCode = ERR_COMM_OK;

//	rc = WaitFiledWritable(fd, pPort->toTimeouts.nReadTimeout);

#ifdef _test
	if(sscanf(pBuffer,"%d",&id_channel)!=1) return 0;
	//printf("id_channel=%d\n",id_channel);
	pPort->ChannelId = id_channel;
	return 1;
#else
	//add code to analyze the content of pbuffer, and then allocate the right value to the right variable
	if(sscanf(pBuffer,"%2d",&d_type)!=1) return 0;
	//printf("d_type = %d\n", d_type);
	//make judgement,and then allocate the right value to the virtual device
	switch(pPort->VirtualDevice)
	{
		case IDU_Virtual_Device:
		{
			switch ( d_type )
			{
				case cmd_idu_ver_info:	
				{
					pPort->CmdId=d_type;
    					// it means you can read the data only one time from now on 
    					pPort->KeyToReadOnce=1;
				}
				break;
				case cmd_idu_run_info:
				{
					pPort->CmdId=d_type;

					// put idu system info into pPort in advance
					(pPort->RunInfo).slot1_status		=get_slot_status_from_driver(1);
					(pPort->RunInfo).slot2_status		=get_slot_status_from_driver(2);
					(pPort->RunInfo).slot1_selftest_info	=get_slot_selftest_from_driver((pPort->RunInfo).slot1_status,1);
					(pPort->RunInfo).slot2_selftest_info	=get_slot_selftest_from_driver((pPort->RunInfo).slot2_status,2);
					(pPort->RunInfo).device_selftest_info	=get_device_selftest_from_driver();
					(pPort->RunInfo).cpu_usage		=get_info_from_IDU(cpu_occupancy);
					//printf("cpu_usage is %d\n",(pPort->RunInfo).cpu_usage);
					(pPort->RunInfo).flash_used		=get_info_from_IDU(flash_occupancy);
					//printf("flash_usage is %d\n",(pPort->RunInfo).flash_used);
					(pPort->RunInfo).sdram_used		=get_info_from_IDU( mem_occupancy);
					(pPort->RunInfo).netflow_count		=get_info_from_IDU(network_flow);
					//printf("network_flow is %ld\n",(pPort->RunInfo).netflow_count);
    					// it means you can read the data only one time from now on 
    					pPort->KeyToReadOnce=1;
				}
				break;
				default:
				{
					printf("unknown command type, pls send it again!\n");
					return 0;
				}
			}
		}
		break;
		
		case IO_Virtual_Device:
			{
				switch ( d_type )
				{
				case cmd_io_ai:
					{
						pPort->CmdId=d_type;
						// it means you can read the data only one time from now on 
						pPort->KeyToReadOnce=1;
					}
					break;
				case cmd_io_di:
					{
						pPort->CmdId=d_type;
						// it means you can read the data only one time from now on 
						pPort->KeyToReadOnce=1;
					};
					break;
				case cmd_io_relay_status:
					{
						pPort->CmdId=d_type;
						// it means you can read the data only one time from now on 
						pPort->KeyToReadOnce=1;
					}
					break;
				case cmd_io_relay_set:
					{// 设置继电器状态
						
						//-- changed by ylf 2006.10.27 --
						// 目的：修改DO的设置方法以配置修改后的协议
						
						/*
						//this is Digit Out,we also need to read the length and value
						WORD wLength=0;
						memcpy(&wLength,pBuffer+1,sizeof(WORD));
						d_length=wLength;
						//printf("d_length=%d\n",d_length);
						if(d_length==1)
						{
						char cRelayStatus;
						memcpy(&cRelayStatus,pBuffer+3,sizeof(char));
                        relay_value = cRelayStatus&0x1f;
						//printf("after chang,read value=0x%x\n",relay_value);		
						//because no need to use relay 5(smoke), so change seting number from 5 to 4 ,changed by wankun 2006.8.23			
						for(i=1;i<5;i++)
						{
						//Digit Out just depend on the info byte 
						rc=set_relay_value_to_io(i,(relay_value >> (i-1)) & 0x1);//cng change it
						if(!rc) 
						{
						printf("open or close relay failed\n");
						return 0;
						}
						}
						}
						//*/
						
						//this is Digit Out,we also need to read the length and value
						
						// 获取info的长度
						WORD wLength=0;
						memcpy(&wLength, pBuffer + 1, sizeof(WORD));
						d_length = wLength;
						//printf("d_length = %d \n", d_length);
						
						// 检查info长度
						if(d_length != sizeof(IDU_SET_DO_INFO))
						{// 错误的长度，输出错误信息
							
							printf(" the info len of set relay stauts %d is error! \n", d_length);
							return 0;
						}
						
						// 处理info内容
						IDU_SET_DO_INFO iduSetDoInfo;
						memcpy(&iduSetDoInfo, pBuffer + 3, d_length);
						
						/*
						printf("继电器编号 : 状态 : 脉冲延时 %d : %d : %d \n",
							iduSetDoInfo.cRelayNo, iduSetDoInfo.cStatus, iduSetDoInfo.cTimes);
						//*/

						// 检查继电器编号
						if((iduSetDoInfo.cRelayNo < 1) || (iduSetDoInfo.cRelayNo > 4))
						{// 错误的长继电器编号，输出错误信息
							
							printf(" the relay No. of set relay stauts %d is error! \n", iduSetDoInfo.cRelayNo);
							return 0;
						}
						
						// 检查继电器状态
						if((iduSetDoInfo.cStatus < 0) || (iduSetDoInfo.cStatus > 1))
						{// 错误的继电器状态，输出错误信息
							
							printf(" the status of set relay stauts %d is error! \n", iduSetDoInfo.cStatus);
							return 0;
						}
						
						// 处理
						if (iduSetDoInfo.cTimes == 0)
						{// 如果为0，表示为常得电/失电。
							
							//rc = set_relay_value_to_io(iduSetDoInfo.cRelayNo, iduSetDoInfo.cStatus);//cng change it
							rc = set_relay_value_to_io(iduSetDoInfo.cRelayNo, iduSetDoInfo.cStatus & 0x1);//cng change it
							if(!rc) 
							{
								printf("open or close relay failed\n");
								return 0;
							}

							//printf("设置继电器 %d 为恒量 %d 成功! \n", iduSetDoInfo.cRelayNo, iduSetDoInfo.cStatus);
						}
						else if (iduSetDoInfo.cTimes > 0)
						{// 如果大于0，用于脉冲控制。
							
							// 获取该继电器的当前状态
							char cRelayStatus = read_relay_data();
							cRelayStatus &= 0x1F;
							cRelayStatus &= (1 << (iduSetDoInfo.cRelayNo - 1));
							cRelayStatus >>= (iduSetDoInfo.cRelayNo - 1);

							//printf("继电器 %d 的当前状态为 %d \n", iduSetDoInfo.cRelayNo, cRelayStatus);

							int iCurrentStatus = cRelayStatus;
							if (iCurrentStatus == iduSetDoInfo.cStatus)
							{// 当前状态与脉冲状态相同，错误
								
								printf("继电器 %d 的当前状态与要设置的脉冲状态 %d 相同，将强制改变当前状态！\n", iduSetDoInfo.cRelayNo, iCurrentStatus);
								
								// 设置当前继电器状态与要设置的状态相反
								if (iCurrentStatus == 0)
								{
									iCurrentStatus = 1;
								}
								else 
								{
									iCurrentStatus = 0;
								}
								//-- end of " if (iCurrentStatus == 0) " --
								
								rc = set_relay_value_to_io(iduSetDoInfo.cRelayNo, iCurrentStatus & 0x1);//cng change it
								if(!rc) 
								{
									printf("open or close relay failed\n");
								}
								
								return 0;
							}
							//-- end of " if (iCurrentStatus == iduSetDoInfo.cStatus) " --
							
							// 设置继电器状态为脉冲状态
							rc = set_relay_value_to_io(iduSetDoInfo.cRelayNo, iduSetDoInfo.cStatus & 0x1);//cng change it
							if(!rc) 
							{
								printf("open or close relay failed\n");
								return 0;
							}
							
							// 延时
							usleep(iduSetDoInfo.cTimes * 10 * 1000);
							
							// 恢复继电器状态
							rc = set_relay_value_to_io(iduSetDoInfo.cRelayNo, iCurrentStatus & 0x1);//cng change it
							if(!rc) 
							{
								printf("open or close relay failed\n");
								return 0;
							}

							/*
							printf("设置继电器 %d 为脉冲状态 %d (%d) 成功! \n",
								iduSetDoInfo.cRelayNo, iduSetDoInfo.cStatus, iduSetDoInfo.cTimes);
							//*/
						}
						else
						{// 如果小于0，错误值
							
							printf(" the time of set relay stauts %d is error! \n", iduSetDoInfo.cTimes);
							return 0;
						}
						//-- end of " if (iduSetDoInfo.cTimes == 0) " --
				}
				break;
				//-- end of " case cmd_io_relay_set: " --
				default:
					{
						printf("unknown command in IO device!\n");
						return 0;
					}
			}
			//-- end of " switch ( d_type ) " --
		}
		break;
		//-- end of " case IO_Virtual_Device: " --
		
		case BAT_Virtual_Device_Slot1:
		{
			switch ( d_type )
			{
				case cmd_bat_one:
				{
					pPort->CmdId=d_type;
    					// it means you can read the data only one time from now on 
    					pPort->KeyToReadOnce=1;
				}
				break;
				case cmd_bat_two:
				{
					pPort->CmdId=d_type;
    					// it means you can read the data only one time from now on 
    					pPort->KeyToReadOnce=1;
				}
				break;
				default:
				{
					printf("unknown command  in bat device--slot1!\n");
					return 0;
				}
			}	
		}
		break;
		
		case BAT_Virtual_Device_Slot2:
		{
			switch ( d_type )
			{
				case cmd_bat_one:
				{
					pPort->CmdId=d_type;
    					// it means you can read the data only one time from now on 
    					pPort->KeyToReadOnce=1;
				}
				break;
				case cmd_bat_two:
				{
					pPort->CmdId=d_type;
    					// it means you can read the data only one time from now on 
    					pPort->KeyToReadOnce=1;
				}
				break;
				default:
				{
					printf("unknown command  in bat device--slot2!\n");
					return 0;
				}
			}	
		}
		break;
		
		case DOOR_Open_Device:
		{
			//add code here to open the door forbiden
			rc=set_relay_value_to_io(id_channel_door,door_status);
			if(!rc) printf("open or close door failed\n");
		}
		break;

		case EXTEND_Com_Device:
		{
			//add code here
		}
		break;

		case BRIDGE_E1_Ether_Device:
		{
			//add code here
		}
		break;
		
		case DCM_Device:
		{
			//add code here
		}
		break;

		case NO_Virtual_Device:
		{
			pPort->nLastErrorCode =  ERR_COMM_WRITE_DATA;
#ifdef _DEBUG_VIRTUAL_PORT
			TRACE("[Virtual_CommWrite] -- Write data error(%x)\n",
					pPort->nLastErrorCode);
#endif //_DEBUG_VIRTUAL_PORT
			printf("no virtual device at all , you should open virtual device again!\n");
			pPort->ChannelId = -1;
			return 0;	// wrong operation!
		}
		break;
	}
    return nBytesToWrite;
#endif	//end of ifdef _test


}

/* The "CommControl" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Virtual_CommControl
 * PURPOSE  : To control a opened port with command nCmd.
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE    hPort       : 
 *            IN int       nCmd        : 
 *            IN OUT void  *pBuffer    : 
 *            IN int       nDataLength : 
 * RETURN   : int :1 for OK, 0 is error code
 * COMMENTS : 
 * CREATOR  : Wan Kun                DATE: 2006-05-07 21:45
 *==========================================================================*/
int Virtual_CommControl( IN HANDLE hPort, IN int nCmd, IN OUT void *pBuffer,IN int nDataLength)
{
//add code here
/*
	VIRTUAL_PORT_DRV *pPort     = (VIRTUAL_PORT_DRV *)hPort;
	int ch_id,ch_value;
	switch ( nCmd )
	{
		case cancel_warning:
		{
			if ( sscanf((char *)pBuffer,"%d%d",&ch_id,&ch_value)!=2)
			{
				printf("read cancel warning message error!\n");
				return 0;
			}
			sprintf(warning_status,"%d%d",&ch_id,&ch_value);
			return 1;
		}
		break;
		default:
		{
			printf("unknown command!\n");
			return 0;
	}	}
	return 1;
	*/
}

/* The "CommClose" proc for comm driver */
/*==========================================================================*
 * FUNCTION : Virtual_CommClose
 * PURPOSE  : Close an opened port and release the memory of the port
 * CALLS    : 
 * CALLED BY: 
 * ARGUMENTS: IN HANDLE  hPort : 
 * RETURN   : int : ERR_COMM_OK or ERR_COMM_PORT_HANDLE
 * COMMENTS : 
 * CREATOR  : Wan Kun                DATE: 2006-05-07 20:32
 *==========================================================================*/
int Virtual_CommClose(IN HANDLE hPort)
{
//add code here 1---error  0---right
	VIRTUAL_PORT_DRV *pPort     = (VIRTUAL_PORT_DRV *)hPort;
	if(hPort == NULL) return 1;
	DELETE(pPort);
	return 0;
}
