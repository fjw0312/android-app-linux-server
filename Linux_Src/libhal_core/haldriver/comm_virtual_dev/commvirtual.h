/*==========================================================================*
 *  FILENAME : commserial.h

 *  VERSION  : V1.00
 *  PURPOSE  :
 *
 *
 *  HISTORY  :
 *
 *==========================================================================*/

#define PACKED __attribute__((packed, aligned(1)))
 
typedef struct idu_version
{
	char version_id_all;
	char version_id_single;
	char version_pcb;
	char version_cpld;
	unsigned short compile_time_year;//if want 2 byte ,use unsigned short type
	char compile_time_month;
	char compile_time_day;
	char version_description[32];
}PACKED;
typedef struct idu_version		IDU_VER_TYPE;

#define IDU_VER_ALL			0x1
#define IDU_VER_SINGLE			0x1
#define IDU_VER_PCB			0x1
#define IDU_VER_CPLD			0x1
#define IDU_COMPILE_YEAR		0x2006
#define IDU_COMPILE_MONTH		0x5
#define IDU_COMPILE_DAY			0x27
#define IDU_VER_DESCRIPTION		"EMERSON NETWORK POWER!          "

struct idu_run
{
	char slot1_status;
	char slot2_status;
	char slot1_selftest_info;
	char slot2_selftest_info;
	unsigned short device_selftest_info;
	char cpu_usage;
	char flash_used;
	char sdram_used;
	unsigned short netflow_count;
}PACKED;
typedef struct idu_run			IDU_RUN_TYPE;


struct io_ai
{
	float 	ai1;
	float	ai2;
	float	ai3;
	float	ai4;
	float 	ai5;
	float 	ai6;
	float	ai7;
	float 	ai8;
	float	ai9;
	float	ai10;
	float 	v_sum_bat1;
	float	v_sum_bat2;
	float	temperature_i2c;
	float 	humidity_i2c;
	//float	reserve1;
	//float	reserve2;
};
typedef struct io_ai			IO_AI_TYPE;

//-- add by ylf 2006.10.27 --
// 目的：设置IO板继电器状态的info结构体
struct SET_DO_INFO
{
	char cRelayNo;         // 继电器编号：1~4
	char cStatus;          // 状态控制命令 0代表失电，1代表得电
	char cTimes;           // 脉冲延时，* 10 ms。 如果为0，表示为常得电/失电。

}PACKED;
typedef struct SET_DO_INFO      IDU_SET_DO_INFO;
//-- end add by ylf 2006.10.27 --

struct bat_ai
{
	float	bat1;
	float	bat2;
	float	bat3;
	float	bat4;
	float	bat5;
	float	bat6;
	float	bat7;
	float	bat8;
	float	bat9;
	float	bat10;
	float	bat11;
	float	bat12;
	float	bat13;
	float	bat14;
	float	bat15;
	float	bat16;
	float	bat17;
	float	bat18;
	float	bat19;
	float	bat20;
	float	bat21;
	float	bat22;
	float	bat23;
	float	bat24;
	float	bat_current;
};
typedef struct bat_ai			BAT_AI_TYPE;

struct SVirtualPortDriver	//	The HAL driver for direct serial port
{
	int				nLastErrorCode;	//NOTE: MUST BE THE FIRST FIELD! 
							//  the last error code.

//	int				fdvirtual;	//	the handle from open()
	COMM_TIMEOUTS			toTimeouts;	//	read and write timeout
//	int				nWorkMode;	//	see enum COMM_WORK_MODE_ENUM in "halcomm.h"
//	int				nMaxClients;	//	always 1
//	int				*pCurClients;	//	0 or 1.
	int				VirtualDevice;	//	default=0/IDU=1/IO=2/BAT=3
	int				ChannelId;	//	actual channel id,if = -1,then error
	int				CmdId;		//	actual command id
	int				KeyToReadOnce;	// 	to indicate the data readed once from up layer
	IDU_RUN_TYPE			RunInfo;
};
typedef struct SVirtualPortDriver		VIRTUAL_PORT_DRV;

  
#define Virtual_CommOpen		HAL_CommOpen
#define Virtual_CommAccept		HAL_CommAccept
#define Virtual_CommRead		HAL_CommRead
#define Virtual_CommWrite		HAL_CommWrite
#define Virtual_CommControl		HAL_CommControl
#define Virtual_CommClose		HAL_CommClose

#define IDU_Virtual_Device			1
#define IO_Virtual_Device			2
#define BAT_Virtual_Device_Slot1	3
#define BAT_Virtual_Device_Slot2	4
#define DOOR_Open_Device			5
#define EXTEND_Com_Device			6
#define BRIDGE_E1_Ether_Device		7
#define DCM_Device					8
#define NO_Virtual_Device			-1

#define self_test			0
#define cpu_occupancy			1
#define flash_occupancy			2
#define mem_occupancy			3
#define network_flow			4

//#define only_channel			1
//#define channel_and_outdata		2
//#define door_open_or_close		3
#define cmd_idu_ver_info		0
#define cmd_idu_run_info		1

#define cmd_io_ai			0
#define cmd_io_di			1
#define cmd_io_relay_status		2
#define cmd_io_relay_set		3

#define cmd_bat_one			0
#define cmd_bat_two			1

#define id_channel_door			6

#define cancel_warning			1
//indicate the maxiam size of buffer when read / write procedure excuting
#define max_wr_size			4096
//indicate the number of di channel of io module
#define channel_di_io			0
//#define channel_relay_io		2


#define _PATH_PACKETRTX  		"/proc/net/dev"
#define _PATH_MEMINFO			"/proc/meminfo"
#define _PATH_CPUINFO			"/proc/stat"

// the bat part
#define IOCTL_BAT_GROUP_ONE 	0x7941   /*select bat group one*/
#define IOCTL_BAT_GROUP_TWO 	0x7942   /*select bat group two*/
#define IOCTL_BAT_2V			0x7943    /*range select 0~2v*/
#define IOCTL_BAT_6V			0x7944    /*range select 0~6v*/
#define IOCTL_BAT_12V		0x7945   /*range select 0~12v*/

//the relay part
#define IOCTL_RELAY0_HIGH_PULSE 	0x7401  
#define IOCTL_RELAY0_LOW_PULSE 		0x7402
#define IOCTL_RELAY1_HIGH_PULSE 	0x7411  
#define IOCTL_RELAY1_LOW_PULSE 		0x7412
#define IOCTL_RELAY2_HIGH_PULSE 	0x7421  
#define IOCTL_RELAY2_LOW_PULSE 		0x7422
#define IOCTL_RELAY3_HIGH_PULSE 	0x7431  
#define IOCTL_RELAY3_LOW_PULSE 		0x7432
#define IOCTL_RELAY4_HIGH_PULSE 	0x7441  
#define IOCTL_RELAY4_LOW_PULSE 		0x7442
#define IOCTL_RELAY5_HIGH_PULSE 	0x7451  
#define IOCTL_RELAY5_LOW_PULSE 		0x7452

#define IOCTL_RELAY0_HIGH_LEVEL 	0x7901
#define IOCTL_RELAY0_LOW_LEVEL 		0x7902
#define IOCTL_RELAY1_HIGH_LEVEL 	0x7911
#define IOCTL_RELAY1_LOW_LEVEL 		0x7912
#define IOCTL_RELAY2_HIGH_LEVEL 	0x7921
#define IOCTL_RELAY2_LOW_LEVEL 		0x7922
#define IOCTL_RELAY3_HIGH_LEVEL 	0x7931
#define IOCTL_RELAY3_LOW_LEVEL 		0x7932
#define IOCTL_RELAY4_HIGH_LEVEL 	0x7941
#define IOCTL_RELAY4_LOW_LEVEL 		0x7942
#define IOCTL_RELAY5_HIGH_LEVEL 	0x7951
#define IOCTL_RELAY5_LOW_LEVEL 		0x7952

#define WRITE_RELAY0_ON 	0x01
#define WRITE_RELAY0_OFF 	0x02
#define WRITE_RELAY1_ON 	0x11
#define WRITE_RELAY1_OFF 	0x12
#define WRITE_RELAY2_ON 	0x21
#define WRITE_RELAY2_OFF 	0x22
#define WRITE_RELAY3_ON 	0x31
#define WRITE_RELAY3_OFF 	0x32
#define WRITE_RELAY4_ON 	0x41
#define WRITE_RELAY4_OFF 	0x42
#define WRITE_RELAY5_ON 	0x51
#define WRITE_RELAY5_OFF 	0x52

#define RELAY_IS_ON	1
#define RELAY_IS_OFF	0

#define IDU_RELAY0_BIT 	((unsigned char) 1 << 0)
#define IDU_RELAY1_BIT ((unsigned char) 1 << 1)
#define IDU_RELAY2_BIT ((unsigned char) 1 << 2)
#define IDU_RELAY3_BIT ((unsigned char) 1 << 3)
#define IDU_RELAY4_BIT ((unsigned char) 1 << 4)
#define IDU_RELAY5_BIT ((unsigned char) 1 << 5)



char cmd_buf1[]={WRITE_RELAY0_ON,WRITE_RELAY1_ON,WRITE_RELAY2_ON,WRITE_RELAY3_ON,WRITE_RELAY4_ON,WRITE_RELAY5_ON};
char cmd_buf2[]={WRITE_RELAY0_OFF,WRITE_RELAY1_OFF,WRITE_RELAY2_OFF,WRITE_RELAY3_OFF,WRITE_RELAY4_OFF,WRITE_RELAY5_OFF};




