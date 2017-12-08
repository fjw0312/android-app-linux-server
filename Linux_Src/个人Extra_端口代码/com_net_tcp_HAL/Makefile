# fjw0312
#//获取工作目录下所有的.c 文件
src = $(wildcard *.c )
#//获取工作目录下所有的.c 文件对应的.o文件
obj = $(patsubst %.c, %.o, $(src))
target = main

CC = gcc
#CC = arm-linux-androideabi-gcc

CFLAGS = -Wall -g -c -I./  -I./include/
#LDFLAGS = -L./ -ldl -pthread
LDFLAGS = -L./  -pthread

all: $(target)

$(target): $(obj)
	$(CC) -o $@ $^ $(LDFLAGS)
$(DIR)%.o:%.c
	$(CC) -o $@ $(CFLAGS) $< 

clean:
	-rm -f $(target)
	-rm -f $(obj)
	-rm -f *~

.PHONY: clean
test:
	@echo $(src)
	@echo $(obj)
