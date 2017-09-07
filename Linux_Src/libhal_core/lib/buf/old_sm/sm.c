#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include "sm.h"

//this variable indicate the writing zone is buffer2 or not
static int key_to_buf2=0;
//in order to indicate the system had ever write buffer1 at least one time, user can read data from now on
static int key_to_write_once=0;
//to indicate the status of DI 
static int status_of_warning=0;

int read_key_to_write_once()
{
	return key_to_write_once;
}
void write_warning_to_so(int value)
{
	//strcpy(&status_of_warning,&value);
	status_of_warning = value;
	//printf("write the status of warning is %d\n",status_of_warning);
}
void read_warning_from_so(int *value)
{
	//strcpy(value,&status_of_warning);
	*value = status_of_warning;
	//printf("read status of warning is %d\n",*value);
}
void clear_warning_of_di()
{
	//strcpy(&status_of_warning,"0");
	status_of_warning=0;
}

void sm_buffer_init()
{
    int i;
    for(i=0;i<16;i++)
    {
    	
	buffer1[i].aidata=0.0;
	buffer2[i].aidata=0.0;
	//if(i==15) {buffer2[i-1].aidata=1.0;buffer2[i].aidata=2.0;}
    }
    //write_p=&buffer1[0];
    //printf("address write_p = %d \n",write_p);
    //read_p=&buffer2[0];
    //printf("address read_p = %d \n",read_p);
    begin_p1=&buffer1[0];
    //printf("address buff1 = %d end of buff1=%d buff2= %d end of buf2 %d\n",buffer1,&buffer1[15],buffer2,buffer2[15]);
    end_p1=&buffer1[13];
    begin_p2=&buffer2[0];
    end_p2=&buffer2[13];
}

int sm_buffer_read(union smdata *buf, int count)
{
  #ifdef PARANOID
     if(ring_debug>5) printf("das1600: ring_buffer_read(%08X,%08X,%d)\n",ring,buf,count);
     if(read_p < begin_p1) {
	printf("ring_buffer_read: tail1 corrupt\n");
	return(0);
     }
     if(read_p > end_p1) {
	printf("ring_buffer_read: tail1 corrupt\n");
	return(0);
     }
     if(read_p < begin_p2) {
	printf("ring_buffer_read: tail2 corrupt\n");
	return(0);
     }
     if(read_p > end_p2) {
	printf("ring_buffer_read: tail2 corrupt\n");
	return(0);
     }
     if(count != 1) {
	printf("ring_buffer_read: count must currently be 1\n");
	return(0);
     }
  #endif
  //printf("address read_p = %d \n",read_p);
  /*
  //if use the concept of queue , must add these codes
  if(read_p == end_p1) {
     read_p = begin_p1;
  }
  if(read_p == end_p2) {
     read_p = begin_p2;
  }
  */
  //*buf = *read_p++;
  if(!key_to_buf2) read_p = begin_p2+count-1;
  else read_p = begin_p1+count-1;
  *buf = *read_p;
  //printf("buf.didata=%ld\n",buf->didata);
  //printf("3)address read_p = %d \n",read_p);
  return(1);
}

int sm_buffer_write(union smdata *buf, int count)
{
  int tmp;


  #ifdef PARANOID
     if(write_p < begin_p1) {
	printf("ring_buffer_write: head corrupt\n");
	return(0);
     }
     if(write_p > end_p1) {
	printf("ring_buffer_write: head corrupt\n");
	return(0);
     }
     if(write_p < begin_p2) {
	printf("ring_buffer_write: head corrupt\n");
	return(0);
     }
     if(write_p > end_p2) {
	printf("ring_buffer_write: head corrupt\n");
	return(0);
     }
     if(count != 1) {
	printf("ring_buffer_write: count must currently be 1\n");
	return(0);
     }
  #endif


  //*write_p++ = *buf;

  //printf("key is %d\n",key_to_buf2);
  if(!key_to_buf2) write_p = begin_p1+count-1;
  else write_p= begin_p2+count-1;
  *write_p = *buf;
  //printf("1)address write_p = %d \n",*write_p);
  
  //write data first , and then judge change buffer area between 1 and 2
  if(write_p == end_p1 )
  {
     write_p = begin_p2;
     //this variable indicate the writing zone is buffer2 or not
     key_to_buf2=1;
     //in order to indicate the system had ever write buffer1 at least one time, user can read data from now on
     key_to_write_once=1;
     //in order to read back to the right place last time
     //tmp=read_p-begin_p2;
     //printf("tmp=%d,read_p=%d  begin_p2= %d\n",tmp,read_p,begin_p2);
     //read_p = begin_p1+tmp;
    // printf("write change to area2\n");
  }
  if(write_p == end_p2 )
  {
     write_p = begin_p1;
     //this variable indicate the writing zone is buffer2 or not
     key_to_buf2 = 0;
     //in order to read back to the right place
     //tmp=read_p-begin_p1;
     //read_p = begin_p2+tmp;
     //printf("write change to area1\n");
  }  
  return(1);
}

int sm_write_float(int channel,float value )
{
	SM_DATA_TYPE 	c;
	int rc;
	
	c.aidata=value;
	rc=sm_buffer_write(&c,channel);
	if(!rc) printf("write sm wrong!\n");
	return rc;	
}

int sm_write_int(int channel,int value )
{
	SM_DATA_TYPE 	c;
	int rc;
	
	c.didata=(long int)value;
	rc=sm_buffer_write(&c,channel);
	if(!rc) printf("write sm wrong!\n");
	return rc;	
}

int sm_read_float(int channel,float *value )
{
	SM_DATA_TYPE 	c2;
	int rc;
	
	rc=sm_buffer_read(&c2,channel);
	if(!rc)
	{
		printf("read error!\n");
	}
	*value=c2.aidata;
	return rc;
}

int sm_read_int(int channel,int *value)
{
	SM_DATA_TYPE 	c2;
	int rc;
	int tmp;
	
	rc=sm_buffer_read(&c2,channel);
	if(!rc)
	{
		printf("read error!\n");
	}
	
	tmp=c2.didata;
	//printf("tmp= %d\n",tmp);
	*value=tmp;
	return rc;
}
#ifdef _test
main()
{
   union smdata c;
   union smdata c2;
   int child;
   int rc;
   int i;
   int j;
   int reads;
   int writes;
   float tmp=0.0;
   float *imp;
   float read_back;
   char tt;
   
   //to test write and read warning status 
   write_warning_to_so(15);
   printf("status_warning is %d\n",status_of_warning);
   read_warning_from_so(&tt);
   printf("read value is %d\n",tt);
   clear_warning_of_di();
   printf("status_warning is %d\n",status_of_warning);
/* 
   //to check write data to sm correctly
   sm_buffer_init();
   //printf("sizeof sm is %d\n",sizeof(long));

   tmp=34.9;
   i=33;
   sm_write_float(4,tmp);
   printf("buf1[3]=%f\n",buffer1[3].aidata);
   sm_write_int(5,i);
   printf("buf1[4]=%ld\n",buffer1[4].didata);
   sm_write_float(16,tmp);
   if(key_to_write_once)
   {
   sm_read_float(4,&read_back);
   printf("return buf1[3]=%f\n",read_back);
   sm_read_int(5,&child);
   printf("return buf1[4]=%d\n",child);
   }
*/
   /*   
   i=23;
   sm_write_int(4,i);
   printf("buf1[3]=%ld\n",buffer1[3]);
   sm_write_int(16,i);
   sm_read_int(4,&child);
   printf("return buf1[3]=%d\n",child);
   */
   
  /* for(i=0;i<40;i++)
   {
	tmp=tmp+1;
	sm_write((long)tmp,i%16);
	
	if(i<16) printf("2)buf1[%d]=%f\n",i,buffer1[i].aidata);
	else if (i<32) printf("buf2[%d]=%f\n",(i-16),buffer2[i-16].aidata);
	else if (i<48) printf("buf1[%d]=%f\n",(i-32),buffer1[i-32].aidata);
	sm_read(imp,i%16);
	printf("4)read %d value is %f-----------------------------------------------------------\n",i,*imp);
   }*/
  /* for(i=0;i<168;i++)
   {
   	sm_read(imp);
	printf("read %d value is %f\n",i,*imp);
   }*/
/*
   c.aidata=0.0;
   c.didata=0;
   reads=0;
   writes=0;
   for(j=0; j<15; j++)
   {
      for(i=0; i<3; i++)
      {
	 rc=sm_buffer_write(&c, 1);
	 if(!rc) printf("write ring buffer error!\n");
         writes++;
	 if(ring_debug>2)
	 {
	  	if(j==1) printf("ring_buffer_write returned %d, was passed %d\n",rc,c.didata);
		else printf("ring_buffer_write returned %d, was passed %ld\n",rc,(long int)c.aidata);
	 }
	 if((rc==1)&&(j==1))
	 {
		c.didata=c.didata+1;
	 }
	 else if(rc==1) c.aidata=c.aidata+1.0;
      }

      for(i=0; i<3; i++)
      {
	 rc=sm_buffer_read(&c2, 1);
	 if(!rc) printf("read ring buffer error!\n");
         reads++;
	 if(ring_debug>2)
	 {
	  	if(j==1) printf("ring_buffer_read returned: rc=%d,c2=%d\n",rc,c2.didata);
		else printf("ring_buffer_read returned: rc=%d,c2=%f\n",rc,c2.aidata);
	 }
      }
   }
   printf("number of reads=%d\n",reads);
   printf("number of writes=%d\n",writes);
*/

}
#endif
