
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


char read_relay_data()
{
	char buf;
	int fd;
	//must add code here to gain relay_data from os driver
	fd = open("/dev/relay1", O_RDWR| O_NOCTTY);
	if(fd <=0)
	{
		printf("failed to open relay\n");
		return 0;
	}
	read(fd, &buf,1);
	close(fd);
	
	return buf;
}

int set_relay_value_to_io(int channel, int value)
{
	char relay_out_value_orig=0x0;
	int fd;
	
	//read the original value of relay
	relay_out_value_orig=read_relay_data();
	printf("in set prog the status of relay is %x\n",relay_out_value_orig);
	//judge the status of relay , changed or not
	if(((relay_out_value_orig << (channel-1)) & 0x80) != value )
	//if(((relay_out_value_orig >> (channel-1)) & 0x01) != value )	
	{
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
			printf("relay on\n");
		}
		else
		{
			//value=0,write relay off
			write(fd,cmd_buf2+(channel-1),1);
			printf("relay off\n");
		}
		close(fd);
		
	}
	return 1;
}

int main(int argc,char *argv[])
{
			char buf,i;
			int rc,j,relay_value;
					printf("------test read proc!----------------\n");
					buf=read_relay_data();
					//buf=buf&0x1F;
					printf("the status of relay is %x\n",buf);
					
					getchar();
					printf("--------test write relay----------------\n");
					relay_value=atoi(argv[1]);
					printf("before change the value is %x\n",relay_value);
					i=(char)relay_value;
					relay_value=((i<<7)&0x80)|((i<<5)&0x40)|((i<<3)&0x20)|((i<<1)&0x10)|((i>>1)&0x08)|((i>>3)&0x04)|((i>>5)&0x02)|((i>>7)&0x01);
					
					printf("after change the value is %x\n",relay_value);
						for(j=1;j<6;j++)
						{
							//Digit Out just depend on the info byte 
							rc=set_relay_value_to_io(j,((relay_value << (j-1)) & 0x80));
							//rc=set_relay_value_to_io(j,((relay_value >> (j-1)) & 0x01));
							if(!rc) 
							{
								printf("open or close relay failed\n");
							}
						}	
					printf("--------after write relay read status----------------\n");
					buf=read_relay_data();
					//buf=buf&0x1F;
					printf("the status of relay is %x\n",buf);				
}

