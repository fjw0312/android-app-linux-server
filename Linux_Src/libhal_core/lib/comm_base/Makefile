#/*==========================================================================*
# *  VERSION  : V1.00
# *  PURPOSE  : The make file for the public lib
# *
# *  HISTORY  :
# *
# *==========================================================================*/

include ../../config_vars.mk

#the target output file
EXEC =libhal.so

#the head files the object files depend on
INCS =halcomm.h
INCS +=$(IDU_SOURCE_DIR)/include/run_mutex.h
INCS +=$(IDU_SOURCE_DIR)/include/run_timer.h
INCS +=$(IDU_SOURCE_DIR)/include/run_queue.h
INCS +=$(IDU_SOURCE_DIR)/include/run_thread.h

#the object files to generate the target
OBJS =pubfunc.o halcomm.o
OBJS +=new.o run_mutex.o run_timer.o run_queue.o run_thread.o 

CFLAGS +=-D_DEBUG_HAL_COMM

#DIR1=../idu_samp
#OBJS +=$(DIR1)/sample.o

# commented by CharlesChen
#DIR2=../buf
#OBJS +=$(DIR2)/ShareMem.o

#for self test
#TEST_EXEC = test_hal_comm
#TEST_OBJS = test_hal_comm.o #$(OBJS)
#TEST_EXEC = test_v
#TEST_OBJS = test_v.o
#TEST_LIBS = -lhal #libpub.so #$(EXEC)
#test_v.o : test_v.cpp
	#$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(TEST_LIBS)

#private shared libraries
LIBS = -ldl
 
all: $(EXEC)

$(EXEC): $(OBJS) $(INCS)
	$(CC) -shared $(CFLAGS) $(LDFLAGS) -o $@ $(OBJS) $(LIBS)
	ln -fs `pwd`/$(EXEC) $(IDU_SOURCE_DIR)/lib/$(EXEC)

install: $(EXEC)
	-mkdir $(IDU_TARGET_DIR)/lib/
	#-cp $(EXEC) $(IDU_TARGET_DIR)/lib/
	$(STRIP) -s -o $(IDU_TARGET_DIR)/lib/$(EXEC) $(EXEC)
	chmod +x $(IDU_TARGET_DIR)/lib/$(EXEC)	
		
$(TEST_EXEC): $(EXEC) $(TEST_OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(TEST_OBJS) $(TEST_LIBS) $(LIBS)

test: $(TEST_EXEC)
	-echo Test making ...

clean:
	-rm -f $(EXEC) *.gdb *.elf *.o $(OBJS) core $(TEST_EXEC)
	-rm -f make_*.txt *.map *.so
	-rm -f $(IDU_SOURCE_DIR)/lib/$(EXEC)
