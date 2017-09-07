#/*==========================================================================*
# *
# *  FILENAME : Makefile
# *  VERSION  : V1.00
# *  PURPOSE  : The make file for the TCP/IP network communication port.
# *
# *  HISTORY  :
# *
# *==========================================================================*/

include ../../config_vars.mk

#the target output file
EXEC = comm_net_udpip.so

#the head files the object files depend on
INCS = commsock.h

#the object files to generate the target
OBJS = commsock.o

CFLAGS +=-I$(IDU_SOURCE_DIR)/include
CFLAGS +=-I$./include

#for self test
//TEST_EXEC = test_comm_tcpip
//TEST_OBJS = test_comm_tcpip.o
//TEST_LIBS = $(EXEC) -lpthread #and other required libs

#private shared libraries
LIBS = -lhal

all: $(EXEC)

$(EXEC): $(OBJS) $(INCS) 
	$(CC) -shared -fPIC $(CFLAGS) $(LDFLAGS) -o $@ $(OBJS) $(LIBS)
	ln -fs `pwd`/$(EXEC) $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

compidu:
	arm_v4t_le-gcc -c  -Wall -I./include -I. commsock.c
	arm_v4t_le-gcc -o comm_net_udpip.so -shared commsock.o -lpthread

clean:
	-rm -f $(EXEC) *.gdb *.elf *.o $(OBJS) core $(TEST_EXEC)
	-rm -f make_*.txt *.map *.so 
	-rm -f $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

