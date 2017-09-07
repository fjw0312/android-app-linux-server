#/*==========================================================================*
# *  FILENAME : config_vars.mk
# *  CREATOR  : fjw0312
# *  VERSION  : V1.01
# *  PURPOSE  : Defines the variables for the makefiles.
# *
# *  HISTORY  :
# *
# *==========================================================================*/

#complile flags
CFLAGS =

#link flags
LDFLAGS =

IDU_MAKE_VERSION = debug

IDU_CROSS_PLATFORM=montavista
COMPILER_DIR=/home/charles/v4t_le/bin
IDU_SOURCE_DIR=../..

ifeq ($(IDU_CROSS_PLATFORM), montavista)
	IDU_CROSS_PLATFORM = arm_v4t_le-
endif

ifeq ($(IDU_CROSS_PLATFORM), ppc)
	IDU_CROSS_PLATFORM = ppc_8xx-
endif

ifeq ($(IDU_CROSS_PLATFORM), arm)
	IDU_CROSS_PLATFORM = arm-linux-
endif

ifeq ($(IDU_CROSS_PLATFORM), x86)
	IDU_CROSS_PLATFORM = 
endif

#CC = $(IDU_CROSS_PLATFORM)gcc
#LD = $(IDU_CROSS_PLATFORM)ld
#AR = $(IDU_CROSS_PLATFORM)ar
#RANLIB=$(IDU_CROSS_PLATFORM)ranlib
#STRIP=$(IDU_CROSS_PLATFORM)strip

CC = arm-linux-androideabi-gcc
CPP = arm-linux-androideabi-g++
AR = arm-linux-androideabi-ar
RANLIB = arm-linux-androideabi-ranlib
STRIP = arm-linux-androideabi-strip


#link flags
#LDFLAGS += -lpthread -lrt -lm -L$(IDU_SOURCE_DIR)/lib
# no rt, pthread for Android, CharlesChen.
LDFLAGS += -lm -L$(IDU_SOURCE_DIR)/lib
LDFLAGS += -L$(COMPILER_DIR)/lib -funwind-tables

#Compile flags: Add the debug flag
ifeq ($(IDU_MAKE_VERSION), debug)
	CFLAGS  += -g -ggdb -D_DEBUG=1 -fnon-call-exceptions
	LDFLAGS += -Wl,-Map,$@.map -fnon-call-exceptions
else
	CFLAGS  += -DNDEBUG -fnon-call-exceptions
	LDFLAGS += -Wl,-s -fnon-call-exceptions
endif


ifeq ($(IDU_CROSS_PLATFORM), ppc_8xx-)
	CFLAGS += -D_HAS_WATCH_DOG=1 -D_HAS_LCD_UI=1 -D_HAS_FLASH_MEM=1 -D_HAS_IDU_SCC
endif

CFLAGS += -W -Wall -Wstrict-prototypes -Wundef -Wunknown-pragmas -Wunreachable-code 
CFLAGS += -Wfloat-equal -W -Wimplicit -Wconversion -DMGRID_ANDROID -funwind-tables
#CFLAGS += -Werror #treat warning as error
CFLAGS += -I$(IDU_SOURCE_DIR)/include -I.
CFLAGS += -I$(COMPILER_DIR)/include

#public dependent head files.
PUBINCS = $(IDU_SOURCE_DIR)/include/public.h

#public librarys
PUBLIBS = -lhal
