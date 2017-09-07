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
EXEC = comm_net_tcpip.so

#the head files the object files depend on
INCS = commsock.h

#the object files to generate the target
OBJS = commsock.o

CFLAGS +=-I$(IDU_SOURCE_DIR)/include

#for self test
TEST_EXEC = test_comm_tcpip
TEST_OBJS = test_comm_tcpip.o
TEST_LIBS = $(EXEC) -lpthread #and other required libs

#private shared libraries
LIBS = -lhal

all: $(EXEC)

$(EXEC): $(OBJS) $(INCS) 
	$(CC) -shared -fPIC $(CFLAGS) $(LDFLAGS) -o $@ $(OBJS) $(LIBS)
	ln -fs `pwd`/$(EXEC) $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

install: $(EXEC)
	-mkdir -p $(ACU_TARGET_DIR)/haldriver/
	#-cp $(EXEC) $(ACU_TARGET_DIR)/haldriver/
	$(STRIP) -s -o $(ACU_TARGET_DIR)/haldriver/$(EXEC) $(EXEC)
	chmod +x $(ACU_TARGET_DIR)/haldriver/$(EXEC)	
	
$(TEST_EXEC): $(EXEC) $(TEST_OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(TEST_OBJS) $(TEST_LIBS) $(LIBS)
	
test: $(TEST_EXEC)
	-echo Test making ...

clean:
	-rm -f $(EXEC) *.gdb *.elf *.o $(OBJS) core $(TEST_EXEC)
	-rm -f make_*.txt *.map *.so 
	-rm -f $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

