#/*==========================================================================*
# *  FILENAME : Makefile
# *  VERSION  : V1.00
# *  PURPOSE  : The make file for making the public module 
# *
# *  HISTORY  :
# *
# *==========================================================================*/

# the driver directories will be maked
DIRS = comm_base

all install test:
	for i in $(DIRS); do make -C $$i $@ || exit $?; done
	
clean:
	for i in $(DIRS); do make -C $$i clean ; done
	-rm -f make_debug.txt make_release.txt
