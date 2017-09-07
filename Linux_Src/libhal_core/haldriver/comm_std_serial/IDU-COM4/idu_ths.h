/*********************************************************************************
 *	idu-ths driver                                                                   *
 *      version: 1.0.0                                                           *                     
 *   
 *********************************************************************************/                                                                           
#ifndef __IDU_THS_H__
#define __IDU_THS_H__

#include "idu_drivers.h"

#define THS_BASE 0x80800010
#define DI_BASE    0x80800000

#define THS_SDA_DELAY	udelay(6)	/* 1/4 I2C clock duration */
#define THS_SCL_DELAY udelay(3)
#define THS_HIGH      1
#define THS_LOW       0
#define THS_MEASURE_TEMPERATURE 0x3
#define THS_MEASURE_HUMIDITY 0x5
#define THS_READ_REG 0x7
#define THS_WRITE_REG 0x6
#define THS_SOFT_RESET 0x1e

#define AD7893_CONVST_DELAY	udelay(1)	/* */
#define AD7893_SCLK_DELAY	udelay(1)	/* */
#define AD7893_CONVERT_DELAY	udelay(50)	/* */
#define AD7893_SCLK_HIGH      1
#define AD7893_SCLK_LOW       0
#define AD7893_CONVST_HIGH 0 /*PA4 is reversed to CONVST*/
#define AD7893_CONVST_LOW  1 /*PA4 is reversed to CONVST*/
#define AD7893_FILTER_TIME  128

extern unsigned char magic(volatile unsigned char val);
extern int at91_set_gpio_input(AT91S_PIO *pio_base, uint pin, int use_pullup, int use_filter);
extern int at91_set_gpio_output(AT91S_PIO *pio_base, uint pin, int use_pullup);
extern int at91_set_gpio_value(AT91S_PIO *pio_base, uint pin, int value);
extern int at91_set_A_periph(AT91S_PIO *pio_base, uint pin, int use_pullup);
extern int at91_set_B_periph(AT91S_PIO *pio_base, uint pin, int use_pullup);
extern unsigned char at91_get_gpio_bit_value(AT91S_PIO *pio_base, unsigned int pin);

#endif

