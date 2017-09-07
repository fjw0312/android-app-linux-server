#/*==========================================================================*
# *
# *  FILENAME : Makefile
# *  VERSION  : V1.00
# *  PURPOSE  : The make file for the non-standard 485 serial port at ACU
# *
# *  HISTORY  :
# *
# *==========================================================================*/

include ../../config_vars.mk

#the target output file
EXEC = comm_acu_485.so

COMM_STD_SERAIL_PATH = ../comm_std_serial

#the head files the object files depend on
INCS = $(COMM_STD_SERAIL_PATH)/commserial.h
INCS += $(IDU_SOURCE_DIR)/include

#the object files to generate the target
OBJS = commserial.o comm_acu_485.o

#for self test
TEST_EXEC = test_comm_485
TEST_OBJS = test_comm_485.o
TEST_LIBS = $(EXEC) -lpthread

#private shared libraries
#LIBS = -lpub

CFLAGS += -D_IMPL_ACU_485=1	#implement the acu 485 special serial
CFLAGS += -I$(COMM_STD_SERAIL_PATH)
CFLAGS += -I$(IDU_SOURCE_DIR)/include

all: $(EXEC)

commserial.o: $(INCS)
	$(CC) -c $(CFLAGS) -o $@ $(COMM_STD_SERAIL_PATH)/commserial.c
	ln -fs `pwd`/$(EXEC) $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

$(EXEC): $(OBJS) $(INCS) 
	$(CC) -shared -fPIC $(CFLAGS) $(LDFLAGS) -o $@ $(OBJS) $(LIBS)

install: $(EXEC)
	-mkdir -p $(ACU_TARGET_DIR)/haldriver/
	#-cp $(EXEC) $(ACU_TARGET_DIR)/haldriver/
	$(STRIP) -s -o $(ACU_TARGET_DIR)/haldriver/$(EXEC) $(EXEC)
	chmod +x $(ACU_TARGET_DIR)/haldriver/$(EXEC)	
		
$(TEST_EXEC): $(EXEC) $(TEST_OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(TEST_OBJS) $(TEST_LIBS)
	
test: $(TEST_EXEC)
	-echo Test making ...

clean:
	-rm -f $(EXEC) *.gdb *.elf *.o $(OBJS) core $(TEST_EXEC)
	-rm -f make_*.txt *.map *.so 
	-rm -f $(IDU_SOURCE_DIR)/haldriver/$(EXEC)



