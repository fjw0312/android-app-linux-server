#/*==========================================================================*
# *  VERSION  : V1.00
# *  PURPOSE  : The make file for the standard serial communication port.
# *
# *  HISTORY  :
# *
# *==========================================================================*/

include ../../config_vars.mk

#the target output file
EXEC =

#the head files the object files depend on
INCS = sample.h

#the object files to generate the target
OBJS =

#for self test
TEST_EXEC = test_hal_comm
TEST_OBJS = test_hal_comm.o
TEST_LIBS = -lpthread -ldl

#private shared libraries
LIBS = -lpub

all: $(EXEC)

$(EXEC): $(OBJS) $(INCS) $(PUBINCS)
	$(CC) -shared -fPIC $(CFLAGS) $(LDFLAGS) -o $@ $(OBJS) $(LIBS)

install: $(EXEC)
	#-cp $(EXEC) $(ACU_TARGET_DIR)/haldriver/
		
$(TEST_EXEC): $(EXEC) $(TEST_OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(TEST_OBJS) $(TEST_LIBS) $(LIBS)
	
test: $(TEST_EXEC)
	-echo Test making ...

clean:
	-rm -f $(EXEC) *.gdb *.elf *.o $(OBJS) core $(TEST_EXEC)
	-rm -f make_*.txt *.map *.so
	


