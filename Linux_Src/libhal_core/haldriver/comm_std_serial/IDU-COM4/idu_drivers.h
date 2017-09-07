/*********************************************************************************
 *	led driver                                                                   *
 *      version: 1.0.0                                                           *
 *   
 *********************************************************************************/                                                                           
#ifndef __IDU_DRIVERS_H__
#define __IDU_DRIVERS_H__

typedef unsigned char UCHAR;
typedef unsigned char uchar;


typedef struct _AT91S_PIO
{
	AT91_REG	 PIO_PER; 	/* PIO Enable Register */
	AT91_REG	 PIO_PDR; 	/* PIO Disable Register */
	AT91_REG	 PIO_PSR; 	/* PIO Status Register */
	AT91_REG	 Reserved0[1]; 	/*  */
	AT91_REG	 PIO_OER; 	/* Output Enable Register */
	AT91_REG	 PIO_ODR; 	/* Output Disable Registerr */
	AT91_REG	 PIO_OSR; 	/* Output Status Register */
	AT91_REG	 Reserved1[1]; 	/*  */
	AT91_REG	 PIO_IFER; 	/* Input Filter Enable Register */
	AT91_REG	 PIO_IFDR; 	/* Input Filter Disable Register */
	AT91_REG	 PIO_IFSR; 	/* Input Filter Status Register */
	AT91_REG	 Reserved2[1]; 	/*  */
	AT91_REG	 PIO_SODR; 	/* Set Output Data Register */
	AT91_REG	 PIO_CODR; 	/* Clear Output Data Register */
	AT91_REG	 PIO_ODSR; 	/* Output Data Status Register */
	AT91_REG	 PIO_PDSR; 	/* Pin Data Status Register */
	AT91_REG	 PIO_IER; 	/* Interrupt Enable Register */
	AT91_REG	 PIO_IDR; 	/* Interrupt Disable Register */
	AT91_REG	 PIO_IMR; 	/* Interrupt Mask Register */
	AT91_REG	 PIO_ISR; 	/* Interrupt Status Register */
	AT91_REG	 PIO_MDER; 	/* Multi-driver Enable Register */
	AT91_REG	 PIO_MDDR; 	/* Multi-driver Disable Register */
	AT91_REG	 PIO_MDSR; 	/* Multi-driver Status Register */
	AT91_REG	 Reserved3[1]; 	/*  */
	AT91_REG	 PIO_PPUDR; 	/* Pull-up Disable Register */
	AT91_REG	 PIO_PPUER; 	/* Pull-up Enable Register */
	AT91_REG	 PIO_PPUSR; 	/* Pad Pull-up Status Register */
	AT91_REG	 Reserved4[1]; 	/*  */
	AT91_REG	 PIO_ASR; 	/* Select A Register */
	AT91_REG	 PIO_BSR; 	/* Select B Register */
	AT91_REG	 PIO_ABSR; 	/* AB Select Status Register */
	AT91_REG	 Reserved5[9]; 	/*  */
	AT91_REG	 PIO_OWER; 	/* Output Write Enable Register */
	AT91_REG	 PIO_OWDR; 	/* Output Write Disable Register */
	AT91_REG	 PIO_OWSR; 	/* Output Write Status Register */
} AT91S_PIO, *AT91PS_PIO;

/*
 * Remap the peripherals from address 0xFFFA0000 .. 0xFFFFFFFF
 * to 0xFEFA0000 .. 0xFF000000.  (384Kb)
 */
#define AT91C_IO_PHYS_BASE	0xFFFA0000
#define AT91C_IO_SIZE		(0xFFFFFFFF - AT91C_IO_PHYS_BASE + 1)
#define AT91C_IO_VIRT_BASE	(0xFF000000 - AT91C_IO_SIZE)

 /* Convert a physical IO address to virtual IO address */
#define AT91_IO_P2V(x)	((x) - AT91C_IO_PHYS_BASE + AT91C_IO_VIRT_BASE)


#define AT91C_BASE_PIOC		((AT91PS_PIO)	AT91_IO_P2V(0xFFFFF800)) /* (PIOC) Base Address */
#define AT91C_BASE_PIOB		((AT91PS_PIO)	AT91_IO_P2V(0xFFFFF600)) /* (PIOB) Base Address */
#define AT91C_BASE_PIOA		((AT91PS_PIO)	AT91_IO_P2V(0xFFFFF400)) /* (PIOA) Base Address */

//#define AT91C_PIO_PA1		((unsigned int) 1 <<  1)	/* Pin Controlled by PA1 */
//#define AT91C_PIO_PA2		((unsigned int) 1 <<  2)	/* Pin Controlled by PA2 */
//#define AT91C_PIO_PA4		((unsigned int) 1 <<  4)	/* Pin Controlled by PA4 */
//#define AT91C_PIO_PA24		((unsigned int) 1 << 24)	/* Pin Controlled by PA23 */
//#define AT91C_PIO_PA22		((unsigned int) 1 << 22)	/* Pin Controlled by PA23 */
//#define AT91C_PIO_PA27		((unsigned int) 1 << 27)	/* Pin Controlled by PA27 */
//#define AT91C_PIO_PA28		((unsigned int) 1 << 28)	/* Pin Controlled by PA28 */
//#define AT91C_PIO_PA29		((unsigned int) 1 << 29)	/* Pin Controlled by PA29 */
//#define AT91C_PIO_PB11		((unsigned int) 1 << 11)	/* Pin Controlled by PB11 */
//#define AT91C_PIO_PB22		((unsigned int) 1 << 22)	/* Pin Controlled by PB22 */
//#define AT91C_PIO_PD23		((unsigned int) 1 << 23)	/* Pin Controlled by PD23 */
//#define AT91C_PIO_PD24		((unsigned int) 1 << 24)	/* Pin Controlled by PD24 */


#endif
