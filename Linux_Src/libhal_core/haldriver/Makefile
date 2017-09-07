#/*==========================================================================*
# *
# *  FILENAME : Makefile
# *  VERSION  : V1.00
# *  PURPOSE  : To make the all HAL drivers.
# *
# *  HISTORY  :
# *
# *==========================================================================*/


#The driver directories will be maked
DIRS = comm_std_serial 
DIRS += comm_acu_485
DIRS += comm_net_tcpip
DIRS += comm_net_udpip
DIRS += comm_hayes_modem
#DIRS += comm_virtual_dev
#DIRS += flash_storage ui_base


all install test:
	for i in $(DIRS) ; do make -C $$i $@ || exit $?; done

clean:
	for i in $(DIRS) ; do make -C $$i clean ; done
