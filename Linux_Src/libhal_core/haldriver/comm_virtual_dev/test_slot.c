
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
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
	printf("data_read=%x\n",data_read[0]);
	close(fd);
	//read the low 4 bit to be as card1_type
	card1_type=data_read[0]&0x0F;
	//read the high 4 bit to be as card2_type
	card2_type=data_read[0]>>4;
	if(num==1) return card1_type;
	else if (num==2) return card2_type;
	else return 0;
	//add code to analyse the data_read info, and return value to user
	
	printf("get slot status from driver\n");
	
}

int get_device_selftest_from_driver()
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


main()
{
	int re;
	
/*	re=get_slot_status_from_driver(1);
	if(!re) printf("get error!\n");
	printf("the slot status is %x",re); */
	re=get_device_selftest_from_driver();
	printf("the selftest result is %x",re);
}
