
#include "sample.h"
#include <string.h>
#include <stdio.h>

int FloatDataIsNULL(char *p)
{
    if((*p++==0xff)&&(*p++==0xff)&&(*p++==0xff)&&(*p==0xff))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int read_di_data()
{
	//must add code here to gain di_data from os driver
	return 33;
}
/*
int read_relay_data()
{
	//must add code here to gain relay_data from os driver
	return 33;
}
*/
int write_relay_data(int value)
{
	printf("output the value %d\n",value);
	return 1;
}
/*------------------------------------------------------------*/      
/*function:       select annolog input channel                         */
/* input parameter:     (BYTE)  channel to be select          */                         
/* output parameter:    no                                             */
/* access database:       ch_state                                     */                            
/* parent routine:    sample_ch()                                      */
/* subroutine:    no                                                   */

void  select_channel(int channel)
{	
    switch (channel)
    {
        case 0:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x80;
            break;     /* 温度 */
        case 1:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x81;
            break;     /* 湿度 */
        case 2:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x82;
            break;     /* 电源 */
        case 3:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x44;
            break;    /* 扩展0 */
        case 4:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x85;
            break;    /* 扩展1 */
        case 5:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x84;
            break;    /* 扩展2 */
        case 6:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x83;
            break;    /* 扩展3 */
        case 7:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x40;
            break;    /* 水浸 */
        case 8:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x41;
            break;    /* 烟感 */
        case 9:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x42;
            break;    /* 红外 */
        case 10:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x43;
            break;    /* 门碰 */
        case 11:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x86;
            break;    /* 片0正基准 */
        case 12:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x87;
            break;    /* 片0负基准 */
        case 13:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x46;
            break;    /* 片1正基准 */
        case 14:
            //XBYTE[CHANNEL_SELECT_ADDR] = 0x47;
            break;    /* 片1负基准 */
        defalult:
            break;
    }
}

void Close_channel()
{
	printf("now will close all the channel of AI!\n");
}

/*---------------------------------------------------------------------*/       
/*function:             sample default channel                         */
/* input parameter:         no                                         */                        
/* output parameter:    (unsigned int) count value of T0(channel!=12) 
			   or count value of T2(channel!=12)      				   */
/* access database:      none                                          */                           
/* parent routine:    sampling()                                       */
/* subroutine:    select_ch() ,read_string()                           */

int sample_ch(int channel)   
{  
        unsigned int out;

    select_channel(channel);/*0.03ms*/

    // add the code to get the exact value of analog sampling, out= exact sampling value
    
    Close_channel(); /*全关闭*/

    return (out);
}	

/*--------------------------------------------------------------*/
/*function:     sample anlog channel                          	*/
/* input parameter:     no 		                              	*/
/* output parameter:    no                                    	*/
/* access database:        ai.output,ai_state                 	*/                       
/* father subroutine:    ai_sampling()                         	*/
/* child subroutine:   sample_ch()				      			*/
float samp_ai(int channel)     	/* 要等于成浮点数的格式*/
{
    float temp1;

    temp1=(float)sample_ch(channel); 

	/*--------value convert start-----*/

	temp1 = temp1 * Vref;
	temp1 = temp1/4096;
/*  I will check the actually circuit to decide below code
    if(ch_i < 6)
    {
        if ( temp1 <= standard[1] )
        {
            temp1 = 0.0;
        }
        else
        {
            temp1 = temp1 - (float)standard[1];
        }

        temp1 = temp1 * Vref;

        if ( standard[1] == standard[0] )
        {
           temp1 = MAX_SAMPLE_VALUE;
        }
        else
        {
           temp1 = temp1/((float)((standard[0] - standard[1])));
        };
    }
    else
    {
        if ( temp1 <= standard[3] )
        {
            temp1 = 0.0;
        }
        else
        {
            temp1 = temp1 - (float)standard[3];
        }

        temp1 = temp1 * Vref;

        if ( standard[3] == standard[2] )
        {
           temp1 = MAX_SAMPLE_VALUE;
        }
        else
        {
           temp1 = temp1 / (float)((standard[2] - standard[3]));
        };

    }
*/

/* 输入电压检查*/
    if (temp1>MAX_SAMPLE_VALUE)
    {
        temp1 = MAX_SAMPLE_VALUE;
    }
    else if (temp1<MIN_SAMPLE_VALUE)
    {
        temp1 = MIN_SAMPLE_VALUE;
    };

/* there is some modification to the value of AI, double check it lately!    
// 直流电流输入,电流=电压/240 
    if(ch_type)
    {
        temp1 = temp1 / 240.0; //有个240欧姆电阻 
    }

//2005.2.1 增加对温湿度传感器断线的判断
    switch(ch_i)
    {
        case 2:
            temp1+=0.0060;
            break;
        case 0:
            if(temp1 < MIN_ANALOG_CURRENT)
            {
                bTempereIsWrong = TRUE;
            }
            else
            {
                bTempereIsWrong = FALSE;
            }
            break;
        case 1:
            if(temp1 < MIN_ANALOG_CURRENT)
            {
                bHumIsWrong = TRUE;
            }
            else
            {
                bHumIsWrong = FALSE;
            }
            break;
        default:
            break;
    }
*/
    return temp1;
}

int get_para_from_io(int channel)
{
	//add code from niexiaogen
	printf("now in proc --get channel parameters from config file\n");
}

/*---------------------------------------------------------------------*/
/*function:     sample a channel and sort                              */
/* input parameter:         int channel                                */                     
/* output parameter:    no                                             */
/* access database:        ai.output,ai_state                          */                       
/* father subroutine:    main()                                        */
/* child subroutine:   sample_ch()      */

float get_analog_value_from_io(int channel)
{
		
    float temp1,temp2,value;
    union 
    {
        char  chardata[4];
        float floatdata;
    }temp;
    
    if(channel >= AI_INPUT_MAX_IO)
    {
        return 0.0;
    }

    get_para_from_io(channel);
    
    temp1 = samp_ai(channel);
    
    value = ai.floatdata[channel];
    value = value - u_ch.channel_cfg.b0;
    value = value / u_ch.channel_cfg.k;  /*这里是变回实际采样值0－12V*/
    
	/* 取绝对值*/
    if (temp1 > value)
    {
        temp2 = temp1 - value;
    }
    else
    {
        temp2 = value - temp1;
    };

  	/* 判断是否需要滤波 */

    switch(channel)
    {
        case 0:
            temp2 = temp2 * u_ch.channel_cfg.k;
            temp2 = temp2 + u_ch.channel_cfg.b0;
            if (temp2 <= Temp_FILTER_VALVE)
            {
                SHOULD_RE_SAMPLE = 0;
            }
            else
            {
                SHOULD_RE_SAMPLE = 1;
            };
            break;
        case 1:
            temp2 = temp2 * u_ch.channel_cfg.k;
            temp2 = temp2 + u_ch.channel_cfg.b0;
            if (temp2 <= Humi_FILTER_VALVE)
            {
                SHOULD_RE_SAMPLE = 0;
            }
            else
            {
                SHOULD_RE_SAMPLE = 1;
            };
            break;
        default:
            if(ch_type == 0)
            {
                if (temp2 <= DC_FILTER_VALVE)
                {
                    SHOULD_RE_SAMPLE = 0;
                }
                else
                {
                    SHOULD_RE_SAMPLE = 1;
                };
            }
            else
            {
                if (temp2 <= DC_FILTER_VALVE/240)
                {
                    SHOULD_RE_SAMPLE = 0;
                }
                else
                {
                    SHOULD_RE_SAMPLE = 1;
                };
            }
            break;
    }
/*进行滤波处理*/
	printf("should_re_sample is %d\n",SHOULD_RE_SAMPLE);
    if (SHOULD_RE_SAMPLE)
    {
        if (temp1 < value)
        {
            temp2 = value;
            value = temp1;
            temp1 = temp2;
        };

        temp2 = samp_ai(channel);

        if (temp1 <= temp2)
        {
            temp1 = temp1 * u_ch.channel_cfg.k;
            value = temp1 + u_ch.channel_cfg.b0;
        }
        else if  (temp2 <= value)
        {
            value = value * u_ch.channel_cfg.k;
            value = value + u_ch.channel_cfg.b0;
        }
        else
        {
            temp2 = temp2 * u_ch.channel_cfg.k;
            value = temp2 + u_ch.channel_cfg.b0;
        };
    }
    else
    {
        value=(value+temp1)/2;
        value=value*u_ch.channel_cfg.k + u_ch.channel_cfg.b0;
    };

    temp.floatdata = value;
    if(FloatDataIsNULL(&temp.chardata[0]))
    {
    }
    else
    {/*合法值才赋值*/
        ai.floatdata[channel] = value;        			/* store the sample */
    }
}

int get_digital_value_from_io(int channel)
{
	int i,di_ch,di_data;
	int count[DI_INPUT_MAX];
	
	for ( i=0; i < DI_INPUT_MAX; i++ )
	{
		count[i] = 0;
	}
	
	for ( i=0; i < SAMPLE_TIME_MAX; i++ )
	{
		di_data = read_di_data();
		di_data=di_data & 0x1F;		//only use low five bit of this data
		for ( di_ch=0; di_ch<5; di_ch++ )
		{
			if ( ( ( di_data >> di_ch ) & 0x01 ) == 1 )
			{
				count[di_ch]++;
			}
		}
	}
	//for ( i=0; i < DI_INPUT_MAX; i++ ) printf("count %d is %d\n",i,count[i]);
	if( count[channel-1] >= 5 ) return 1;
	else return 0;
}

int get_relay_value_from_io(int channel)
{
	int relay_in_data;
	
	relay_in_data = read_relay_data();	
	relay_in_data = relay_in_data & 0x3F;		//only use low six bit of this data
	
	if ( ( ( relay_in_data >> (channel-1) ) & 0x01 ) == 1 ) return 1;
	else return 0;		
}

/*
// if get_relay_value_from_io need to make judgement, then use this function

int get_relay_value_from_io(int channel)
{
	int i, relay_ch, relay_in_data;
	int count[RELAY_INPUT_MAX];
	
	for ( i=0; i < RELAY_INPUT_MAX; i++ )
	{
		count[i] = 0;
	}
	for ( i=0; i < SAMPLE_TIME_MAX; i++ )
	{
		relay_in_data = read_relay_data();		
		relay_in_data = relay_in_data & 0x3F;		//only use low six bit of this data
		for ( relay_ch=0; relay_ch<6; relay_ch++ )
		{
			if ( ( ( relay_in_data >> relay_ch ) & 0x01 ) == 1 )
			{
				count[relay_ch]++;
			}
		}
	}
	//for ( i=0; i < DI_INPUT_MAX; i++ ) printf("count %d is %d\n",i,count[i]);
	if( count[channel-1] >= 5 ) return 1;
	else return 0;		
}
*/
/*
int set_relay_value_to_io(int channel, int value)
{
	int relay_out_value_orig,relay_out_value_new,rn;
	
	//read the original value of relay
	relay_out_value_orig=read_relay_data();
	//judge the status of relay , changed or not
	if(((relay_out_value_orig >> (channel-1)) & 0x01) != value )
	{
		//if changed, just modify the right bit of out data
		relay_out_value_new = relay_out_value_orig ^ (0x01 <<(channel-1));
		printf("relay_out_value_new=%d\n",0x01 <<(channel-1));
		//output its new value
		rn = write_relay_data(relay_out_value_new);
		if(!rn) 
		{
			printf("error in output value of relay!\n");
			return 0;
		}
	}
	return 1;
}
*/
int get_para_from_bat(int channel)
{
	//add code from niexiaogen
	
}

float get_value_from_bat(int channel)
{
	//add code here
}

#ifdef test

main()
{
	float tmp1;
	int tmp2;
	tmp1=get_analog_value_from_io(1);
	printf("get analog result is %f\n", tmp1);
	tmp2=get_digital_value_from_io(1);
	printf("get digital result is %d\n",tmp2);
	tmp2=get_relay_value_from_io(6);
	printf("get relay result is %d\n",tmp2);
	set_relay_value_to_io(6,0);
}

#endif
