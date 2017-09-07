#/*==========================================================================*
# *  FILENAME : Makefile
# *  VERSION  : V1.00
# *  PURPOSE  : The make file for the standard serial communication port.
# *
# *  HISTORY  :
# *
# *==========================================================================*/

include ../../config_vars.mk

#the target output file
EXEC = comm_std_serial.so

#the head files the object files depend on
INCS = commserial.h

#the object files to generate the target
OBJS = commserial.o

#for self test
TEST_EXEC = test_comm_serial
TEST_OBJS = test_comm_serial.o
TEST_LIBS = $(EXEC) -lpthread

#private shared libraries
LIBS = -lhal

all: $(EXEC)

$(EXEC): $(OBJS) $(INCS)
	$(CC) -shared -fPIC $(CFLAGS) $(LDFLAGS) -o $@ $(OBJS) $(LIBS)
	ln -fs `pwd`/$(EXEC) $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

install: $(EXEC)
	-mkdir -p $(IDU_TARGET_DIR)/haldriver/
	#-cp $(EXEC) $(IDU_TARGET_DIR)/haldriver/
	$(STRIP) -s -o $(IDU_TARGET_DIR)/haldriver/$(EXEC) $(EXEC)
	chmod +x $(IDU_TARGET_DIR)/haldriver/$(EXEC)
		
$(TEST_EXEC): $(EXEC) $(TEST_OBJS) $(INCS)
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(TEST_OBJS) $(TEST_LIBS)
	
test: $(TEST_EXEC)
	-echo Test making ...

clean:
	-rm -f $(EXEC) *.gdb *.elf *.o $(OBJS) core $(TEST_EXEC)
	-rm -f make_*.txt *.map *.so 
	-rm -f $(IDU_SOURCE_DIR)/haldriver/$(EXEC)

