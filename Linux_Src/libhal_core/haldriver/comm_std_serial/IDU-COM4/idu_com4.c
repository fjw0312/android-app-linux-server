
#ifndef LINUX_VERSION_CODE
#include <linux/version.h>
#endif
#if LINUX_VERSION_CODE > KERNEL_VERSION(2,2,0)
#include <asm/uaccess.h> /* for put_user */
#endif



#ifdef MODULE
#include <linux/module.h>  
#else
#define MOD_INC_USE_COUNT
#define MOD_DEC_USE_COUNT
#endif
#include <linux/config.h>
#include <linux/version.h>
#include <linux/types.h>
#include <linux/sched.h>
#include <linux/timer.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/fs.h>
#include <linux/mm.h>
#include <linux/delay.h>
#include <linux/time.h>
#include <asm/param.h>
//#include <linux/malloc.h>
#include <asm/uaccess.h>
#include <linux/errno.h>
#include <linux/vmalloc.h>

#include <linux/signal.h>
#include <asm/bitops.h>
#include <linux/ctype.h>

#include <asm/irq.h> /*enable_irq()*/
#include <asm/ptrace.h>
#include <linux/fs.h>
#include <linux/wrapper.h>
#include <linux/wait.h>
#include <linux/interrupt.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/spinlock.h>

#include "idu_ths.h"
#include "idu_com4.h"
 
#define SUCCESS 0
/* Used to prevent concurent access to the same device */
static int Device_Open = 0;
static char * st_device_driver="Emerson IDU-COM4 driver,version: A01\n";
  
#ifndef CONSOLE_BAUD_RATE
#define	CONSOLE_BAUD_RATE 19200
#endif

#define DoubleClock(a) (int)(((double)CHIP_CLK)/16/(a))

static unsigned char UART_LCR_STA[CHANNEL_NUM] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
static unsigned char UART_MCR_STA[CHANNEL_NUM] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
static unsigned long UART_TIMEOUT[CHANNEL_NUM] = {0,0,0,0,0,0,0,0}; 

static unsigned long UART_BASE0;
static unsigned long UART_BASE1;
static unsigned long UART_BASE2;
static unsigned long UART_BASE3;
static unsigned long AIC_VBASE;
static struct semaphore rd_sem,com1_sem,com2_sem,com3_sem,com4_sem,com5_sem,com6_sem,com7_sem,com8_sem;

//static spinlock_t lockirq;
	
static int baud_table[] = { 50,75,110,134,150,200,300,600,1200,1800,2400,4800,7200,9600,19200,38400,57600,115200,};
		
/*I did not import tasklet to implement bottom halves and did not use semphore lock*/
/*I will induce tasklet and bottom half later*/
static wait_queue_head_t rqueue;/*the queue is used to implement flow control*/

static volatile int whichslot = 1;	
//static struct st_dev * Apdev[8];
//static int gn,gi;


MODULE_PARM( whichslot, "i");
MODULE_PARM_DESC(whichslot, "An integer");

//unsigned char reverseA(unsigned char val);

//void receive_char(struct st_dev *dev);
//void st_put_char(char ch,struct st_dev *dev);

static void st_interrupt(int irq,void *dev_id,struct pt_regs *regs);
int init_port(struct st_dev *dev);
int __init init_st(void);
void __exit exit_st(void);


//#include <asm/processor.h>
//#include <linux/ctype.h>
/*
void enable_ext_irqs()
{
 asm volatile ( "stmdb sp!, {r0}" );
 asm volatile ( "mrs r0, cpsr" );
 asm volatile ( "bic r0,r0,#0xC0" );
 asm volatile ( "msr cpsr_cxsf,r0" );
 asm volatile ( "ldmia sp!,{r0}" );

}

void disable_ext_irqs()
{
 asm volatile ( "stmdb sp!, {r0}" );
 asm volatile ( "mrs r0, cpsr" );
 asm volatile ( "orr r0,r0,#0xC0" );
 asm volatile ( "msr cpsr_cxsf,r0" );
 asm volatile ( "ldmia sp!,{r0}" );


}
*/
int enable_ext_irq(int irq_vector )
{
 	volatile unsigned long *irq_ctr_reg; 
 	//disable_ext_irqs(); 
 	if(irq_vector<0 || irq_vector >32)
  	{
    		return(1);
  	}
  
 	/*setting all ext-irqs are falling-edge triggered*/
 	irq_ctr_reg = (volatile unsigned long *)(AIC_VBASE + SIEL5); 
 	*irq_ctr_reg = (*irq_ctr_reg) | SIEL_EDGE; 

 	/*setting multiplexing pins as IRQ3-6*/
 //	irq_ctr_reg = (volatile unsigned long *)(AIC_VBASE + SIUMCR);   
 //	*irq_ctr_reg = (*irq_ctr_reg) & SIUMCR_MASK;   

 	/*pointing to interrupt mask register*/
 	irq_ctr_reg = (volatile unsigned long *)(AIC_VBASE + AIC_IECR);
     
 	switch(irq_vector)
 	{
   	case IRQ0:      	*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ0_ENABLE;
        		break;
  	case IRQ1:      	*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ1_ENABLE;
                	break;
   	case IRQ2:      	*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ2_ENABLE;
	        	break;
   	case IRQ3:     	*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ3_ENABLE;
                	break;
   	case IRQ4:    	*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ4_ENABLE;
   			break;
   	case IRQ5:    	//printk("enable IRQ5\n");	
					*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ5_ENABLE;
   			break;
   	case IRQ6:    	*irq_ctr_reg=(*irq_ctr_reg)|EXT_IRQ6_ENABLE;
   			break;			
  
   	default:     	break;
     
 	}
 	//enable_ext_irqs();
 	return(0);
}

/*
void enable_sttx_irq()
{
}

void disable_sttx_irq()
{
}
*/
int disable_ext_irq(int irq_vector )
{
 	volatile unsigned long *irq_ctr_reg;
 	if(irq_vector<0 || irq_vector >32)
 	{
    		return(1);
  	}
 	irq_ctr_reg=(volatile unsigned long *)(AIC_VBASE + AIC_IDCR );   
 	switch(irq_vector)
 	{
   	case IRQ0:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ0_DISABLE;
               		break;
   	case IRQ1:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ1_DISABLE;
               		break;
   	case IRQ2:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ2_DISABLE;
	       
              		 break;
   	case IRQ3:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ3_DISABLE;
	       		break;
   	case IRQ4:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ4_DISABLE;
	       		break;
   	case IRQ5:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ5_DISABLE;
	       		break;  
   	case IRQ6:    	*irq_ctr_reg=(*irq_ctr_reg)&EXT_IRQ6_DISABLE;
	       		break;  
				
   	default:    	break;

 	}
 	return 0;
}

int at91_set_ExIRQ(AT91PS_PIO pio_base, uint pin, int use_pullup, int use_filter,int peripheral_sel)
{
    pio_base->PIO_PDR |= pin;
	
    if(use_pullup)
    {
        pio_base->PIO_PPUER |= pin;        
    }
    else
    {
        pio_base->PIO_PPUDR |= pin;
    }
	
    if(use_filter)
    {
        pio_base->PIO_IFER |= pin;        
    }
    else
    {
        pio_base->PIO_IFDR |= pin;        
    }
    if(peripheral_sel)
    {
        pio_base->PIO_BSR |= pin;
    }
    else
    {
        pio_base->PIO_ASR |= pin;   
    }
    pio_base->PIO_IER |= pin;
    
    return 0;
}


static int st_open(struct inode *inode,struct file *filp)
{
	int minor;
	struct st_dev *dev;

	minor=MINOR(inode->i_rdev);	

	switch(whichslot)
	{
	case 0:
       	if ((dev = kmalloc(sizeof(struct st_dev), GFP_KERNEL)) == NULL) 
		{
			MOD_DEC_USE_COUNT;
			printk("failed to malloc mem...\n");
			return -ENOMEM;
		}		
		if(minor < 4)		
			dev->irq = IRQ_NUM0;			
		else
			dev->irq = IRQ_NUM5;		
		break;		
	case 1:		
		if(minor > 3)	
		{
			printk("the device not exists!\n");
			return -EINVAL;
		}
		
       	if ((dev = kmalloc(sizeof(struct st_dev), GFP_KERNEL)) == NULL) 
		{
			MOD_DEC_USE_COUNT;
			printk("failed to malloc mem...\n");
			return -ENOMEM;
		}
	
		dev->irq = IRQ_NUM0;
		break;
	case 2:		
		if(minor < 4)	
		{
			printk("the device not exists!\n");
			return -EINVAL;
		}
       	if ((dev = kmalloc(sizeof(struct st_dev), GFP_KERNEL)) == NULL) 
		{
			MOD_DEC_USE_COUNT;
			printk("failed to malloc mem...\n");
			return -ENOMEM;
		}		
		dev->irq = IRQ_NUM5;
		break;
	}

	MOD_INC_USE_COUNT;
	
//	printk("enter st_open and minor = %d\n",minor); 
  	/*malloc memory for dev in kernel space*/	

	/*Now,initialise dev*/  
	dev->rbuffer[0] = 0;
	dev->tbuffer[0] = 0;

	
	switch(minor)
	{
	case 0:		
			if(down_trylock(&com1_sem))
			{
				printk("com1 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}
			dev->uartp = (volatile unsigned char *)(UART_BASE1 +  UARTB_OFFSET_ADDR);			
			
	        	break;
	case 1: 
			if(down_trylock(&com2_sem))
			{
				printk("com2 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}		
			dev->uartp = (volatile unsigned char *)(UART_BASE1 + \
	                             UARTA_OFFSET_ADDR);
	        	break;   
	case 2:	

			if(down_trylock(&com3_sem))
			{
				printk("com3 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}		
			dev->uartp = (volatile unsigned char *)(UART_BASE0 + \
	                             UARTB_OFFSET_ADDR);
			break;
	case 3:
			if(down_trylock(&com4_sem))
			{
				printk("com4 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}		
	        	dev->uartp = (volatile unsigned char *)(UART_BASE0 + \
	                             UARTA_OFFSET_ADDR);
	        	break;
	case 4:		
			if(down_trylock(&com5_sem))
			{
				printk("com5 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}
			dev->uartp = (volatile unsigned char *)(UART_BASE3 +  UARTB_OFFSET_ADDR);			
			
	        	break;
	case 5: 
			if(down_trylock(&com6_sem))
			{
				printk("com6 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}		
			dev->uartp = (volatile unsigned char *)(UART_BASE3 + \
	                             UARTA_OFFSET_ADDR);
	        	break;   
	case 6:	

			if(down_trylock(&com7_sem))
			{
				printk("com7 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}		
			dev->uartp = (volatile unsigned char *)(UART_BASE2 + \
	                             UARTB_OFFSET_ADDR);
			break;
	case 7:
			if(down_trylock(&com8_sem))
			{
				printk("com8 is already opened!\n");
				MOD_DEC_USE_COUNT;
				 kfree(dev);
				return -EINVAL;
			}		
	        	dev->uartp = (volatile unsigned char *)(UART_BASE2 + \
	                             UARTA_OFFSET_ADDR);
	        	break;		
	
	}


	/*rhead is the producer position*/
	/*rtail is the consumer position*/
	dev->rhead=0;
	dev->rtail=0; 
	dev->thead=0;
	dev->ttail=0; 	
	dev->rhead=0;
	dev->bw=0; 	
	dev->minor=minor;
	//printk("minor%d rhead = %d",dev->minor,dev->rhead);
	filp->private_data = dev;
	
#if 1


	//AT91PS_PIO ppio = AT91C_BASE_PIOA;
	//uint testpio = ppio -> PIOA_PSR;
	//printk("before set, PIOA_PSR = %x\n",testpio);
	//testpio = ppio -> PIO_ABSR;
	//printk("before set, PIOA_ABSR = %x\n",testpio);
	
	
	//testpio = ppio -> PIO_PSR;
	//printk("after set, PIOA_PSR = %x\n",testpio);
	//testpio = ppio -> PIO_ABSR;
			if (request_irq(dev->irq, st_interrupt, SA_SHIRQ, "st16c2552/554 uarts", dev))   //SA_SHIRQ  SA_INTERRUPT
			{ 
		  	 	printk("st16c2552/554: Unable to attach st16c2552/554 uarts interrupt\n ");
		  		MOD_DEC_USE_COUNT;   
		  	 	kfree(filp->private_data);
		   		return -EINVAL;
			}

//       printk("attach st16c2552/554 uarts interrupt\n ");

#endif
	init_port(dev);
	Device_Open++;

	enable_ext_irq(dev->irq); /**/
	return SUCCESS;
}
 

static ssize_t st_read(struct file *filp,
    char *buffer,    /* The buffer to fill with data */
    size_t length,   /* The length of the buffer */
    loff_t *offset)  
{
	int count =0;
	int temp;
	unsigned long t;
	struct st_dev *dev =filp->private_data;	

	int j = dev->minor;

	if (UART_TIMEOUT[j])
	{
		//printk("new start = %d\n",jiffies);
		for (t=0; t < UART_TIMEOUT[j]; t++)
		{
			if (dev->rhead >= dev->rtail)						
				count = dev->rhead - dev->rtail;
			else
				count = BUFFER_SIZE - dev->rtail + dev->rhead ;
			if (count >= length)				
				break;
			set_current_state(TASK_INTERRUPTIBLE);
                   schedule_timeout(10);					
		}
		//printk("new end = %d\n",jiffies);
				//return -EAGAIN;
	}		

	//down_interruptible(&rd_sem);
	while (dev->rhead ==dev->rtail)
	{
		/*up(&sem);*//*release the lock */			
			
		if (filp->f_flags &O_NONBLOCK)
		{
			//printk("!filp->f_flags &O_NONBLOCK return\n");
			//up(&rd_sem);			
			return 0;// -EAGAIN;  fixed 3.14
		}
		
	
		if (wait_event_interruptible(rqueue,dev->rhead !=dev->rtail))
			{
				printk("!wait_event_interruptible return!\n");
				//up(&rd_sem);
				return -ERESTARTSYS;/*signal:tell the fs layer to handle it */
		/*otherwise loop,but first reacquire the lock */
		/* printk("process %i (%s) awoken \n",current->pid,current->comm);*/ 
			}
	}

	//printk("start read,jiffies=%d\n",jiffies);
	if (dev->rhead >dev->rtail)/*duacess happens here, you must lock or optimisie the codes*/
	{   
		count = dev->rhead - dev->rtail;
		
	 	if (count > length )
			count = length; 
			
		if (copy_to_user(buffer,(dev->rbuffer)+(dev->rtail),count))
			{
			printk("!copy_to_user1 return!\n");
			//up(&rd_sem);
			return -EFAULT;
			}
		dev->rtail +=count;
		if (dev->rtail >=BUFFER_SIZE)
			dev->rtail =0;/*wrapped */
	}
	else   
	{  
		count = BUFFER_SIZE - dev->rtail + dev->rhead ;
	   	if (count > length)
	   		count=length;
	   	temp=BUFFER_SIZE - dev->rtail;
		if (count> temp)
		{
			if (copy_to_user(buffer,(dev->rbuffer)+(dev->rtail),temp))
				{
				printk("!copy_to_user2 return!\n");
				//up(&rd_sem);
				return -EFAULT;
				}
			if (copy_to_user(buffer+temp,(dev->rbuffer),count-temp))
				{
				printk("!copy_to_user3 return!\n");
				//up(&rd_sem);
				return -EFAULT;
				}
			dev->rtail =count-temp;
			if (dev->rtail >=BUFFER_SIZE)
		   		dev->rtail =0;/*wrapped */
		
		}
		else
		{
			if (copy_to_user(buffer,(dev->rbuffer)+(dev->rtail),count))
				{
				printk("!copy_to_user4 return!\n");
				//up(&rd_sem);
				return -EFAULT;
				}
			dev->rtail +=count;
			if (dev->rtail >=BUFFER_SIZE)
		   		dev->rtail =0;/*wrapped */
		}
	}
	//up(&rd_sem);
	//printk("end st_read!\n");
	//printk("end read,jiffies=%d\n",jiffies);
	return count;
}


static ssize_t st_write(struct file *filp,const char *buf,size_t count,loff_t *f_pos)
{
	int j;
	struct st_dev *dev=filp->private_data;
	volatile unsigned char	*uartp = dev->uartp;
	//printk("start write,jiffies=%d\n",jiffies);
	//volatile unsigned char tempw;
/*	while(dev->bw)
		{			
			schedule();
		}*/	
       //struct inode *inode = filp->f_dentry->d_inode;
	// read minor of device   
	j = dev->minor;	
	//printk("st_write!\n");
	if(copy_from_user(dev->tbuffer,buf,count))
		{
			return -EFAULT;
		}
		
		UART_MCR_STA[j] = UART_MCR_STA[j]  & 0xfd;
		writeb(UART_MCR_STA[j],(uartp+UART_MCR));	
		
		dev->bw = 1;
		dev->thead=0;
		dev->ttail = count;
		dev->uartp[UART_IER] = 0x03;	//enable_sttx_irq();
		//printk("end write,jiffies=%d\n",jiffies);
/*		
	while(dev->bw)
		{
			//printk("there is a package being written so schedule!\n");
			schedule();
		}
	while(!(dev->uartp[UART_LSR] & UART_LSR_TXEMPTY))
		{
			schedule();
		}
	*/

	
	return count;

#if 0
             UART_MCR_STA[j] = UART_MCR_STA[j]  & 0xfd;

		writeb(UART_MCR_STA[j],(uartp+UART_MCR));
		

		for(i=0;i<count;i++)
		{		
			//if (!(readb(uartp+UART_LSR) & UART_LSR_TXREADY))
			//	usleep(1);
				while(!(uartp[UART_LSR] & UART_LSR_TXREADY))
				{
					//printk("!schedule1\n");
					schedule();/*	
					set_current_state(TASK_UNINTERRUPTIBLE);
           				schedule_timeout((10 * HZ) / 1000);*/
				}
			//}
			//else
				writeb(dev->tbuffer[i],(uartp+UART_THR));
			schedule();
				//mb();
		}

		while(!( readb(uartp+UART_LSR) & UART_LSR_TXEMPTY))
		{	
			//printk("!schedule2\n");
			schedule();/*
			set_current_state(TASK_UNINTERRUPTIBLE);
           		schedule_timeout((10 * HZ) / 1000);	*/	
		}	

		UART_MCR_STA[j]  = UART_MCR_STA[j] |UART_MCR_RTS;

		writeb(UART_MCR_STA[j],(uartp+UART_MCR));		
#endif
	
}


static void st_interrupt(int irq,void *dev_id,struct pt_regs *regs)
{
	struct st_dev *dev=dev_id;
	unsigned char temp,tempd;

	disable_ext_irq(dev->irq);
	//disable_ext_irqs();
	//spin_lock(&lockirq);	
	temp=dev->uartp[UART_ISR];	
	//printk("!st_interrupt\n");
	temp=temp &0x0f;
	if((temp==UART_ISR_RXFTO)||(temp==UART_ISR_RXFIFO))//fifo ready or  time out
	{
		while((dev->uartp[UART_LSR])&(UART_LSR_RXREADY))
		{

			tempd = dev->uartp[UART_RHR];
			dev->rbuffer[dev->rhead]= tempd; //dev->uartp[UART_RHR];		

			dev->rhead++;

			if(dev->rhead>=BUFFER_SIZE)
				dev->rhead=0;
		
			if(dev->rhead == dev->rtail )
			{
				dev->rtail++; //the buffer full
				if(dev->rtail>=BUFFER_SIZE)
					dev->rtail=0;
			}
	
		}	
		wake_up_interruptible(&rqueue);//wake any reading process 
	}

/*write*/

//	if(dev->bw)
//	{		
		//printk("enter interrupt dev->bw temp = %x\n",temp);
		if(temp==UART_ISR_TXFIFO)
			{				
					int j = dev->minor;					
					writeb(dev->tbuffer[dev->thead],(dev->uartp+UART_THR));
					dev->thead++;
					if(dev->thead == dev->ttail)
						{
							dev->bw=0;
							dev->uartp[UART_IER] = 0x01;
							while(!(dev->uartp[UART_LSR] & UART_LSR_TXEMPTY))
							{
							}
							UART_MCR_STA[j]  = UART_MCR_STA[j] |UART_MCR_RTS;
							dev->uartp[UART_MCR] = UART_MCR_STA[j];
						}	
			}
//		}
	//spin_unlock(&lockirq);	
	//enable_ext_irqs();
	enable_ext_irq(dev->irq);
}


/*void receive_char(struct st_dev *dev)
{
	unsigned char temp;

	while((dev->uartp[UART_LSR])&(UART_LSR_RXREADY))
	{

		temp = dev->uartp[UART_RHR];
		dev->rbuffer[dev->rhead]= temp; //dev->uartp[UART_RHR];
		

		dev->rhead++;

		if(dev->rhead>=BUFFER_SIZE)
			dev->rhead=0;
		
		if(dev->rhead == dev->rtail )
		{
			dev->rtail++; //the buffer full
			if(dev->rtail>=BUFFER_SIZE)
				dev->rtail=0;
		}
	
	}		

	wake_up_interruptible(&rqueue);//wake any reading process 
}*/



int init_port(struct st_dev *dev)/*initial uart port*/
{

	int temp,clock=0;    
      int exttyS_minor = dev->minor;      
	int j = exttyS_minor;
	volatile unsigned char tempi;	  

			writeb(0x04,((dev->uartp)+UART_AFR));//MF fuction = RXRDY
		      writeb(0x0f,((dev->uartp)+UART_FCR)); /* 0xcf;*/ /*trigger =14,reset fifos*///0x0f
			mb();
			UART_LCR_STA[j] = UART_LCR_DIVISOR;
			//dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);
			writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
			mb();
			clock=DoubleClock(CONSOLE_BAUD_RATE);
			temp = (clock)&0xff00;

       		 //dev->uartp[UART_DLM] = reverseA((temp>>8)&0xff);  /* set msb baud */
			 writeb(((temp>>8)&0xff),((dev->uartp)+UART_DLM));
			 mb();
       		 tempi = (clock)&0xff;

        		//dev->uartp[UART_DLL] = reverseA(tempi);
			 writeb((tempi),((dev->uartp)+UART_DLL));
			mb();
 	    
			//dev->uartp[UART_LCR] =reverseA( (UART_LCR_NONPARITY | UART_LCR_BIT8 |UART_LCR_STOP1));
			UART_LCR_STA[j] = UART_LCR_NONPARITY | UART_LCR_BIT8 |UART_LCR_STOP1;  //this line shoud not be fixed
			 writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
			mb();
	
			/*now,set imr and fifo,etc*/
        		//dev->uartp[UART_FCR] = reverseA(UART_FCR_FIFOEN);
			writeb(UART_FCR_FIFOEN,((dev->uartp)+UART_FCR));
			mb();
			//dev->uartp[UART_FCR] = reverseA(UART_FCR_FIFOEN | UART_FCR_DMA | UART_FCR_TRIGGER);
			writeb((UART_FCR_FIFOEN | UART_FCR_DMA | UART_FCR_TRIGGER),((dev->uartp)+UART_FCR));
			mb();
        		//dev->uartp[UART_MCR] = reverseA(0x08); /*INTA-D not hig-Z in 16 mode*/
        		writeb(UART_MCR_STA[j],((dev->uartp)+UART_MCR));
			mb();
       		 //dev->uartp[UART_IER] = 0x00; 
       		 writeb(0x00,((dev->uartp)+UART_IER));
			 mb();
       		 /*clear status bits*/
        		/*temp=dev->uartp[UART_LSR];
			 mb();
        		temp=dev->uartp[UART_RHR];
			mb();
        		temp=dev->uartp[UART_ISR];
			mb();	
        		temp=dev->uartp[UART_MSR];
			mb();*/
			tempi = readb((dev->uartp)+UART_LSR);			
       		 while(tempi&0x01)
       		 	{
       		 		//temp=dev->uartp[UART_RHR];   /*temp not be used?*/
					//mb();
       		 		tempi = readb((dev->uartp)+UART_LSR);
					mb();            				
       		 	}
        		//dev->uartp[UART_IER] = 0x00;//  reverseA(UART_IER_RXFIFO);  /*To do*/
        		writeb(0x01,((dev->uartp)+UART_IER));
			mb();
	return 0;
}


static int st_ioctl(struct inode *inode, struct file *filp,
				unsigned int cmd, unsigned long arg)
{

     if(cmd == 0xffff)
	   return 0;
      
	struct st_dev *dev=filp->private_data;
	int rc,clock=0;
	int tb;
	unsigned short temp;
	volatile unsigned char tempc;     
	
	int j = dev->minor;
	//use for testing
	//unsigned char st_regval = 0xff;

	rc = -EINVAL;
	
	if(cmd ==0 || cmd >=0xff02)       ///////modified by w91221 to support clear rx buff
		return rc;
/////////////////////added by w91221////////////////////////////
       if(cmd == IOCTL_CLEAR_RX_BUFF)
		{
		 dev->rtail = dev->rhead ;
		 //printk("clear rx buff finished\n");
		 return 0;
		}
/////////////////////added by w91221////////////////////////////

	if(cmd == IOCTL_SET_TIMEOUT)
		{	
			//printk("arg = %d\n",arg);
			UART_TIMEOUT[j] = arg / 100;
			//printk("UART_TIMEOUT = %d\n",UART_TIMEOUT[j]);
		}
	
        if( cmd & 0x001f)/* cmd magic contains stopbit/parity info*/
        {
        	
		if(cmd&0x0003)
		{ 
	
			temp=cmd & 0x0003;
			switch(temp)
			{
			case ST_STOP1:  /*set stopbit as 1*/				  	
				  tempc = UART_LCR_STA[j];
				  UART_LCR_STA[j] = tempc&0xfb;			  
			        // dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);/*reset lcr bit2*/ 
				  writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
				   mb();

			         /*dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])|UART_LCR_STOP1;*/				  
				 rc = 0;
			         break;
			case ST_STOP2:  
				  tempc = UART_LCR_STA[j];
				  UART_LCR_STA[j] = tempc&0xfb;				
			         //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xfb;/*reset lcr bit2*/
				  UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_STOP2;
				  writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));					 
			        // dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])|UART_LCR_STOP2;
				 rc = 0;
			         break;
			case ST_STOP15:
				  tempc = UART_LCR_STA[j];
				  UART_LCR_STA[j] = tempc&0xfb;	
				  UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_STOP15;
				  writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));		
				  
			        // dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xfb;/*reset lcr bit2*/
			         //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])|UART_LCR_STOP15;
				 rc = 0;
			         break;
			default:
				rc = -EINVAL;
			    	break;
			
			}
			if(rc == -EINVAL )
				return rc;
		}
				
		if(cmd&0x001c)
		{
			temp=cmd&0x001c;
			switch(temp)
			{
			    
			    case ST_PARNONE:					
				  	tempc = UART_LCR_STA[j];
				  	UART_LCR_STA[j] = tempc&0xc7;					
			            //dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);/*reset lcr bit5-4-3*/
					writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
					mb();
					UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_NONPARITY;
			            //dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);
					writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
					mb();
				    rc = 0;
			            break;
			    case ST_PARODD:  
				  	tempc = UART_LCR_STA[j];
				  	UART_LCR_STA[j] = tempc&0xc7;					
			            //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xc7;/*reset lcr bit5-4-3*/
			            UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_PARITY| UART_LCR_ODDPARITY;
			            writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
			            //dev->uartp[UART_LCR] = UART0ALCR_STA|UART_LCR_PARITY| UART_LCR_ODDPARITY;
				    rc = 0;
			 	    break;
			    case ST_PAREVEN:
				  	tempc = UART_LCR_STA[j];
				  	UART_LCR_STA[j] = tempc&0xc7;					
			            //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xc7;/*reset lcr bit5-4-3*/
			            UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_PARITY| UART_LCR_EVENPARITY;			            
			            writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
				    rc = 0;
			            break;

			    case ST_PARSPACE:
				  	tempc = UART_LCR_STA[j];
				  	UART_LCR_STA[j] = tempc&0xc7;					
			            //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xc7;/*reset lcr bit5-4-3*/
			            UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_SETPARITY|UART_LCR_PARITY| UART_LCR_EVENPARITY;		            
			            writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
				    rc = 0;
			            break;

			    case ST_PARMASK:
				  	tempc = UART_LCR_STA[j];
				  	UART_LCR_STA[j] = tempc&0xc7;					
			            //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xc7;/*reset lcr bit5-4-3*/
			            UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_SETPARITY|UART_LCR_PARITY;	            
			            writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
				    rc = 0;
			            break;						

			    default:  rc = -EINVAL;
			    break;
		  	}
		  	if(rc == -EINVAL )
				return rc;
		}
		if(rc == -EINVAL )
			return rc;	
           }

	   
           if((cmd & 0x00e0))
           {
	
	            temp = (cmd & 0x00e0);
	            switch(temp)
	            {
#if 0
	              case ST_LOOPBACK:  printk("internal loopback mode enabled!\n");
			       dev->uartp[UART_MCR] =UART_MCR_LOOP; /*考虑不自环和MCR[3]`*/
			
				rc = 0;
		       		break;
#endif

	              case ST_CS5:  /*set word length 5*/
				    UART_LCR_STA[j] = UART_LCR_STA[j]&0xfc;
				    UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_BIT5;
	                       writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));				  
	                       //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xfc;/*reset lcr bit1-0*/
	                      // dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])|UART_LCR_BIT5;
			       rc = 0;	
	                       break;
	              case ST_CS6:
				    UART_LCR_STA[j] = UART_LCR_STA[j]&0xfc;
				    UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_BIT6;
	                       writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));						  	
			       //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xfc;/*reset lcr bit1-0*/
	                   //    dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])|UART_LCR_BIT6;
				rc =0;	
	                       break;
	              case ST_CS7:
				    UART_LCR_STA[j] = UART_LCR_STA[j]&0xfc;
				    UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_BIT7;
	                       writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));					  	
	                   //    dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])&0xfc;/*reset lcr bit1-0*/
			       //dev->uartp[UART_LCR] = (dev->uartp[UART_LCR])|UART_LCR_BIT7;
				rc = 0;
	                       break;
	              case ST_CS8:
				   UART_LCR_STA[j] = UART_LCR_STA[j]&0xfc;
	                      // dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);/*reset lcr bit1-0*/
				    writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
				    mb();
				    UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_BIT8;
				    
	                       //dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);
	                       writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
				    mb();  
				   
				rc =0;
	                       break;
	              default: rc = -EINVAL;
	           
	           }
                   if(rc == -EINVAL )
			return rc; 
          }

	if((cmd & 0xff00))/*set baudrate*/
	{	
		rc = 0;
		if (((cmd&0xff00) < 0xb100) || ((cmd&0xff00) >0xc200))
		{
	
			rc= -EINVAL;
			return rc;
		}

		temp=((cmd & 0xff00)>>8 ) - 0xb1;		
		tb=baud_table[temp];

		//clock = DoubleClock(tb);
		clock=(int)((CHIP_CLK)/(16*tb));
		UART_LCR_STA[j] = UART_LCR_STA[j]|UART_LCR_DIVISOR;
		//dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);
		writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
		mb();
		temp = (clock)&0xff00;
		//dev->uartp[UART_DLM] = reverseA((temp>>8)&0xff);  /* set msb baud */
		writeb((temp>>8)&0xff,((dev->uartp)+UART_DLM));
		mb();
		tempc = (clock)&0xff;
		//dev->uartp[UART_DLL] = reverseA(tempc);  /* set lsb baud */
		writeb(tempc,((dev->uartp)+UART_DLL));
		mb();
		UART_LCR_STA[j] = UART_LCR_STA[j] & 0x7f;
		//dev->uartp[UART_LCR] = reverseA(UART0ALCR_STA);
		writeb(UART_LCR_STA[j],((dev->uartp)+UART_LCR));
		mb();
	}		

	 return rc ;
     
}


static int st_release(struct inode *inode, 
			  struct file *filp)
{
	
      struct st_dev *dev =filp->private_data;
	char temp;
	int exttyS_minor = dev->minor; 
	
  	while((dev->uartp[UART_LSR])&(UART_LSR_RXREADY))
        {
                temp =  dev->uartp[UART_RHR];
	
        }

        /*down_interruptible(&sem);*//*等到ISR结束之后才可释放中断*/        
        disable_ext_irq(dev->irq);  	//to do? judge Device_Open == 0?
        dev->uartp[UART_IER] = 0x00;	

	  MOD_DEC_USE_COUNT;	
	//iounmap((unsigned char *)UART_BASE0);
	/*'Cause all device share one interrupt line,you should not disable
	 * the interrupt line when other devices are busy
	 */
	//printk("minor%d To release irq\n",exttyS_minor);	 
	free_irq(dev->irq,dev); 

       kfree(filp->private_data);

	switch(exttyS_minor)
	{
	case 0:	
			up(&com1_sem);
	        	break;
	case 1: 	
			up(&com2_sem);
	        	break;
	case 2: 	
			up(&com3_sem);
	        	break;
	case 3:	
			up(&com4_sem);
	        	break;	
	case 4:	
			up(&com5_sem);
	        	break;
	case 5: 	
			up(&com6_sem);
	        	break;
	case 6: 	
			up(&com7_sem);
	        	break;
	case 7:	
			up(&com8_sem);
	        	break;					
	}

        Device_Open--; 
      
        return 0;
}

static struct file_operations Fops_st = 
{	
      /*  owner:	        THIS_MODULE,*/
        read:		st_read,	
	write:		st_write,	
	ioctl:		st_ioctl,	
	open:		st_open,	
	release:	st_release,
};
 

int __init init_st(void)
{
	int ret,i,n;
	char dummy;
	volatile unsigned char	*uartp;
	//volatile unsigned char tempi;
	volatile unsigned long *irq_ctr_reg; 
	//unsigned char st_regval = 0xff; //use for debug

	init_MUTEX(&rd_sem);
	init_MUTEX(&com1_sem);	
	init_MUTEX(&com2_sem);
	init_MUTEX(&com3_sem);
	init_MUTEX(&com4_sem);
	init_MUTEX(&com5_sem);	
	init_MUTEX(&com6_sem);
	init_MUTEX(&com7_sem);
	init_MUTEX(&com8_sem);

	//spin_lock_init(&lockirq);
	
	unsigned long flags;
	local_irq_save(flags);    /* interrupts are now disabled */

	if(whichslot == 2)
		{
			i = 4;
			n = 8;
			UART_BASE2 = (unsigned long)ioremap_nocache(0x70000000,0x900000);// use cs6
			UART_BASE3 = (unsigned long)ioremap_nocache(0x80a00030,0x900000);// use cs7_4
		}
	else if(whichslot == 1)
		{
			i = 0;
			n = 4;		
			UART_BASE0 = (unsigned long)ioremap_nocache(0x50000000,0x900000);// use cs4	
			UART_BASE1 = (unsigned long)ioremap_nocache(0x60000000,0x900000);// use cs5	 
		}
	else if(whichslot == 0)
		{
			i = 0;
			n = 8;			
			UART_BASE0 = (unsigned long)ioremap_nocache(0x50000000,0x900000);// use cs4	
			UART_BASE1 = (unsigned long)ioremap_nocache(0x60000000,0x900000);// use cs5
			UART_BASE2 = (unsigned long)ioremap_nocache(0x70000000,0x900000);// use cs6
			UART_BASE3 = (unsigned long)ioremap_nocache(0x80a00030,0x900000);// use cs7_4		
		}
	else
		{
			printk("the parmeter whichslot is error!\n");
			return -EINVAL;
		}

	AIC_VBASE  = (unsigned long)ioremap_nocache(AIC_BASE,0x200);
	
 	irq_ctr_reg = (volatile unsigned long *)(AIC_VBASE + SIEL5);
	//printk("AIC SIEL =%x\n",*irq_ctr_reg);
	*irq_ctr_reg = *(irq_ctr_reg) & (0xffffff9f);
	*irq_ctr_reg = *(irq_ctr_reg) | SIEL_EDGE; 
	//printk("AIC SIEL =%x\n",*irq_ctr_reg);

 	irq_ctr_reg = (volatile unsigned long *)(AIC_VBASE + SIEL0);
	//printk("AIC SIEL =%x\n",*irq_ctr_reg);	
	*irq_ctr_reg = *(irq_ctr_reg) & (0xffffff9f);
	*irq_ctr_reg = *(irq_ctr_reg) | SIEL_EDGE; 
	//printk("AIC SIEL =%x\n",*irq_ctr_reg);	
 	//*irq_ctr_reg = (*irq_ctr_reg) | SIEL_EDGE; 
	
	/* TO fix the bug of hardware. for line cs3 and cs4 connected together in hardware lixi */

	for(;i<n;i++)
 	{

    		switch(i)
		{
        	case 0:				
					uartp = (volatile unsigned char *)(UART_BASE1 + \
				UARTB_OFFSET_ADDR);	
					
                        	break;
        	case 1:         uartp = (volatile unsigned char *)(UART_BASE1 + \
				UARTA_OFFSET_ADDR);
					
                        	break;
       	case 2:		uartp = (volatile unsigned char *)(UART_BASE0 + \
				UARTB_OFFSET_ADDR);
					
				break;
        	case 3:		uartp = (volatile unsigned char *)(UART_BASE0 + \
                        	UARTA_OFFSET_ADDR);
					
				break;

        	case 4:				
					uartp = (volatile unsigned char *)(UART_BASE3 + \
				UARTB_OFFSET_ADDR);	
					
                        	break;
        	case 5:         uartp = (volatile unsigned char *)(UART_BASE3 + \
				UARTA_OFFSET_ADDR);
					
                        	break;
       	case 6:		uartp = (volatile unsigned char *)(UART_BASE2 + \
				UARTB_OFFSET_ADDR);
					
				break;
        	case 7:		uartp = (volatile unsigned char *)(UART_BASE2 + \
                        	UARTA_OFFSET_ADDR);
					
				break;


        	}
   

			UART_MCR_STA[i] = 0x0a;
   			writeb(UART_MCR_STA[i],uartp + UART_MCR); /*set intA-D continuous in 16 mode!*/
			mb();
		
   			writeb(0x00,uartp+UART_IER); /*disable all uart interrupts*/
			mb();
			UART_LCR_STA[i] = 0x7f  & UART_LCR_STA[i];
			//UART_LCR_STA[i] = UART_LCR_STA[i] | UART_LCR_DIVISOR;
			
			writeb(UART_LCR_STA[i],uartp+UART_LCR);   /**/
			mb();

   			dummy =readb(uartp+UART_ISR);/*clear ISR/RHR*/
			mb();
   			//printk("ISR=%x\n",dummy);
    			while((readb(uartp+UART_LSR))&0x01)
    				{
						mb();
						dummy = readb(uartp+UART_RHR);
					
    				} 	
	
		}	
	
	init_waitqueue_head(&rqueue);
	
	if ((ret = register_chrdev(DEVICE_MAJOR, DEVICE_NAME, &Fops_st) < 0))
 	{
 		printk ("device failed with %d\n", ret);
 		return ret;
 	}
	printk(st_device_driver);
	
	 if(whichslot == 2)
       {
       	printk("COM4 is in slot2,use IRQ5\n");
		at91_set_ExIRQ(AT91C_BASE_PIOA, AT91C_PIO_PA3, 1, 0,1);   /*set PA3 as IRQ5*/	 /*this should close when release and consider race conditions*/	
	 }
	 else if(whichslot == 1)
	 {
	 	printk("COM4 is in slot1,use IRQ0\n");
	 	at91_set_ExIRQ(AT91C_BASE_PIOB, AT91C_PIO_PB29, 1, 0,0); /*set PB29 as IRQ0*/
	 }
	 else if(whichslot == 0)
	 {
	 	printk("COM4 is in slot1 and slot 2 use IRQ0 and IRQ5\n");
	 	at91_set_ExIRQ(AT91C_BASE_PIOB, AT91C_PIO_PB29, 1, 0,0); /*set PB29 as IRQ0*/
		at91_set_ExIRQ(AT91C_BASE_PIOA, AT91C_PIO_PA3, 1, 0,1);   /*set PA3 as IRQ5*/	 /*this should close when release and consider race conditions*/			
	 }	 
	local_irq_restore(flags); /* interrupts are restored to their previous state */

	return 0;
}


void __exit exit_st(void)
{
 	int unreg_result;


 	/* Unregister the device */
	//printk("to exit_st\n");
	iounmap((unsigned char *)UART_BASE0);
	iounmap((unsigned char *)UART_BASE1);
	iounmap((unsigned char *)UART_BASE2);
	iounmap((unsigned char *)UART_BASE3);	
	iounmap((unsigned char *)AIC_VBASE);
	
 	unreg_result = unregister_chrdev(DEVICE_MAJOR, DEVICE_NAME);

 	/* If there's an error, report it */
 	if (unreg_result < 0) {
 		printk("Error in unregister_chrdev: %d\n", unreg_result);
		
 	}	
 	//printk("st16c2552 driver succesfully uninstalled\n");
}


/*
void st_put_char(char ch,struct st_dev *dev)
{	

	volatile unsigned char	*uartp = dev->uartp;
	//volatile unsigned char tempp;
	while (!(uartp[UART_LSR]  & UART_LSR_TXREADY)){		
  		schedule();}
	//writeb(ch,(uartp+UART_THR));	
	uartp[UART_THR] = ch;
}       */          

module_init(init_st);   
module_exit(exit_st);   

MODULE_LICENSE("GPL");
          
