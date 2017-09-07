
#define AI_INPUT_MAX_IO		12
#define Vref			2.5    //²Î¿¼µçÑ¹ÊÇ2.5·ü
#define MAX_SAMPLE_VALUE	2.5    //need confirm lately!
#define MIN_SAMPLE_VALUE	0.0
#define Temp_FILTER_VALVE	0.01
#define Humi_FILTER_VALVE	0.1
#define DC_FILTER_VALVE		0.1
#define DI_INPUT_MAX		5
#define SAMPLE_TIME_MAX		9
#define RELAY_INPUT_MAX		6

struct
{
    char cfg[16];
    struct
    {
        float up_alarm;	 //upper alarm value for default channel
        float down_alarm;//down alarm value for default channel
        float k;         //'k' in y=kx+b0 equalite for defult channel
        float b0;	 //'b0' in y=kx+b0 equalite for defult channel
    }channel_cfg;
}u_ch;

struct
{	  
    char chardata[AI_INPUT_MAX_IO*4];
    float floatdata[AI_INPUT_MAX_IO];        /* output value for each channel*/
}ai;

int SHOULD_RE_SAMPLE=0;
int ch_type=0;