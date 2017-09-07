/*The driver supports 16c550/2552/554 single/dual/quard-uart chips supplied *
 *by TI,NS,EXAR and PHILLIPS.                                               *
 *        version: 1.00                                                     *
 *                                 2003/08/28                               *
 *                                                                          *                  
 ****************************************************************************/
 
 
 #define DEVICE_MAJOR	  	225
 #define DEVICE_NAME 		"mttyS" /* used as mttyS0-mttyS7*/
 
 #define CHIP_CLK               11059200 /*clock frequency in Hz*/  
 #define IMMR_BASE              0xff000000 //0x10000000
 #define CS3_BASE               0xa0000000 /*base address of CS3*/

 #define CHANNEL_NUM            8  /*how many uart channels?*/
 
 
 #define SIUMCR                 0x000
 #define SIEL                   0x018   /*interrupt edge/level register*/
 #define SIMASK			0x014   /*interrupt mask register*/
 
 #define SIEL_EDGE      	0xFFFF0000
                                /*IRQ0-IRQ7 are falling edge sensitive*/
 #define SIUMCR_MASK            0x00002000  
				/*set DPC = 1---DP[0-3] as IRQ[3-6] */

 
 #define EXT_IRQ1_ENABLE        0x20000000
 #define EXT_IRQ2_ENABLE        0x08000000
 #define EXT_IRQ3_ENABLE        0x02000000
 #define EXT_IRQ4_ENABLE        0x00800000
 #define EXT_IRQ5_ENABLE        0x00200000
 #define EXT_IRQ6_ENABLE        0x00080000
 #define EXT_IRQ1_DISABLE       0xdfffffff
 #define EXT_IRQ2_DISABLE       0xf7ffffff
 #define EXT_IRQ3_DISABLE       0xfdffffff
 #define EXT_IRQ4_DISABLE       0xff7fffff
 #define EXT_IRQ5_DISABLE       0xffdfffff
 #define EXT_IRQ6_DISABLE       0xfff7ffff

 /*above lines should be modified according to the processor!*/
 
 /*define irq num for each uart channel,modify them*/
 #define IRQ3			6
 #define IRQ4			8
 #define IRQ_NUM                IRQ4
 #define UART0_IRQ              IRQ_NUM
 #define UART1_IRQ      	IRQ_NUM
 #define UART2_IRQ      	IRQ_NUM
 #define UART3_IRQ      	IRQ_NUM
 #define UART4_IRQ      	IRQ_NUM         
 #define UART5_IRQ      	IRQ_NUM 
 #define UART6_IRQ      	IRQ_NUM
 #define UART7_IRQ      	IRQ_NUM       
 


 
 /*define each uart channel base address,modify them*/
 #define UART0_BASE_ADDR        CS3_BASE + 0x00000
 #define UART1_BASE_ADDR        CS3_BASE + 0x00008
 #define UART2_BASE_ADDR        CS3_BASE + 0x00010
 #define UART3_BASE_ADDR        CS3_BASE + 0x00018
 #define UART4_BASE_ADDR        CS3_BASE + 0x80000
 #define UART5_BASE_ADDR        CS3_BASE + 0x80008
 #define UART6_BASE_ADDR        CS3_BASE + 0x80010
 #define UART7_BASE_ADDR        CS3_BASE + 0x80018

 
 /*define registers*/ 
 #define  UART_RHR             0x00   /*receive data holding register-RHR*/
 #define  UART_THR             0x00   /*actually, THR*/
 #define  UART_LCR             0x03   /*line control register*/
 #define  UART_LSR             0x05   /*LSR,line status register*/ 
 #define  UART_IER             0x01   /*interrupt enable register,IER*/
 #define  UART_ISR             0x02   /*interrupt status register*/

 #define  UART_FCR             0x02  /*FIFO control register*/
 #define  UART_MCR             0x04  /*modem control register*/
 #define  UART_MSR             0x06  /*modem status register*/
 #define  UART_SPR             0x07  /*a temporary data register to store 8 bits of user information*/
 #define  UART_DLL             0x00  /*special registers,DLL,baud rate value LSB*/
 #define  UART_DLM             0x01  /*DLM,MSB*/
 #define  UART_AFR             0x02 
 
 
 #define  UART_LCR_BIT5        0x00
 #define  UART_LCR_BIT6        0x01
 #define  UART_LCR_BIT7        0x02
 #define  UART_LCR_BIT8        0x03
 #define  UART_LCR_PARITY      0x08
 #define  UART_LCR_NONPARITY   0x00
 #define  UART_LCR_EVENPARITY  0x10
 #define  UART_LCR_ODDPARITY   0x00
 
 #define  UART_LCR_STOP1       0x00    /*1 stop bit*/
 #define  UART_LCR_STOP15      0x04
 #define  UART_LCR_STOP2       0x04
 #define  UART_LCR_TXBREAK     0x40 
 #define  UART_LCR_DIVISOR     0x80             /*bit7, divisor latch enabled*/  
 
 #define  UART_LSR_RXREADY     0x01
 #define  UART_LSR_RXFFULL     0x01
 #define  UART_LSR_TXREADY     0x20
 #define  UART_LSR_TXEMPTY     0x40
 #define  UART_LSR_RXBREAK     0x10		/* Received BREAK */
 #define  UART_LSR_RXFRAMING   0x08		/* Received framing error */
 #define  UART_LSR_RXPARITY    0x04		/* Received parity error */
 #define  UART_LSR_RXOVERRUN   0x02		/* Received overrun error */
 
 
 #define  UART_IER_RXRDY       0x01 /*enable rxrdy interrupt*/
 #define  UART_FCR_FIFOEN      0x01 /*enable t/r FIFO.when above two set,RXRDY interrupt triggered if receive FIFO trigger level reached*/
 #define  UART_IER_TXRDY       0x02  
 #define  UART_IER_RXFIFO      0x01 
 #define  UART_IER_TXFIFO      0x02  
 #define  UART_IER_RXFTO       0x08
 
/* #define  UART_UIMR_LSRIRQENABLE 0x004*/
 
 #define  UART_IER_MODEMEN    0x08 /*enable modem status register interrupt*/
/*time out int enabled*/
 #define  UART_FCR_RESET      0x06 /*FIFO r/t reset*/
 
 /*set as DMA mode1 so that interrupt issued when trigger level reached*/ 
 #define  UART_FCR_DMA        0x08
 #define  UART_FCR_TRIGGER    0x80 /*trigger level as 4/8 bytes*/
 #define  UART_MCR_INTAB      0x08 
 #define  UART_MCR_LOOP       0x10;/*enable loopback mode*/      

 #define  UART_ISR_LSR        0x06 /*receive lsr interrupt pending*/
 #define  UART_ISR_RXRDY      0x04 /*receive data ready interrupt*/
 #define  UART_ISR_TXRDY      0x02
 #define  UART_ISR_RXFIFO     0x04 /*receive data ready interrupt*/
 #define  UART_ISR_TXFIFO     0x02
 #define  UART_ISR_RXFTO      0x0c /*received data time out*/
 #define  UART_ISR_MSR        0x00 /*modem status register interrupt*/
 
 
 /*中断线有限，因此，最好在open时才注册中断，close时释放中断，这样可以共享中断线；
 如果在初始化时注册中断，则不能共享中断线*/
 
 
#define ST_STOP1        0x0001
#define ST_STOP15       0x0003/*1.5 bit*/
#define ST_STOP2        0x0002
#define ST_LOOPBACK     0x0020
#define ST_DEBUG        0x0040      
#define ST_CS5          0x0060
#define ST_CS6          0x0080
#define ST_CS7          0x00a0
#define ST_CS8          0x00c0
#define ST_PARNONE      0x0004
#define ST_PARODD       0x0008
#define ST_PAREVEN      0x000c
#define ST_PARSPACE     0x0010
#define ST_PARMASK      0x0014
#define ST_B50          0xb100
#define ST_B75          0xb200
#define ST_B110         0xb300
#define ST_B134         0xb400
#define ST_B150         0xb500
#define ST_B200         0xb600
#define ST_B300         0xb700
#define ST_B600         0xb800
#define ST_B1200        0xb900
#define ST_B1800        0xba00
#define ST_B2400        0xbb00
#define ST_B4800        0xbc00
#define ST_B7200        0xbd00
#define ST_B9600        0xbe00 
#define ST_B19200       0xbf00
#define ST_B38400       0xc000
#define ST_B57600       0xc100
#define ST_B115200      0xc200
 
 
 
 #define BUFFER_SIZE  512     /*circular buffer size*/                            
 struct st_dev{
           char rbuffer[BUFFER_SIZE];/*buffer for in-coming characters*/
           char tbuffer[BUFFER_SIZE];/*for out-going chars*/
           volatile unsigned char *uartp;/*point to uart channel base addr*/
           unsigned int irq;
           unsigned int minor;
           unsigned int head;   
           unsigned int tail;      
           
        };/*creat a ring queue(buffer) to store in-coming characters*/  
        
                                   
