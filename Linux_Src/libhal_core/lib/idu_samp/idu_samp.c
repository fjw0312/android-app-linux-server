
/*function:     delay subroutine                                        */
/* input parameter:      number of delay microsecond                    */
/* output parameter:    no                                              */
/* access database:             no                                      */
/* parent routine:   main()	                                            */
/* subroutine:    no                                                    */
void delay_ms(int m_second)
{
    int ll;
    for(;m_second>0;m_second--)
    {
        for (ll=0;ll<70;ll++);
    };
}    

void CloseWP(void)
{
    printf(" now in CloseWp ! \n");
    delay_ms(5);
}

void InitialAll()
{
	printf("now in initializing procedure! \n");
}

main()
{
	CloseWp();
	InitialAll(); /* ³õÊ¼»¯ */
	
	while(1)
	{
	
	}
	
}