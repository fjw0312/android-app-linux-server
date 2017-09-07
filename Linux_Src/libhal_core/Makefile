#/*==========================================================================*
# *  FILENAME : Makefile
# *  CREATOR  : fjw0312              
# *  VERSION  : V1.00
# *  PURPOSE  : The make project to make the whole  hal target files.
# *
# *  HISTORY  :
# *
# *==========================================================================*/

include ./config_vars.mk

#The directories will be maked
DIRS = lib haldriver

all test:
	for i in $(DIRS) ; do make -C $$i $@ || exit $?; done
	
install:
	#erase all file at target dir.
	-rm -fr $(IDU_TARGET_DIR)/*
	#
	-mkdir -p $(IDU_TARGET_DIR)/log/
	
	##config
	-cp -fr $(IDU_SOURCE_DIR)/documents/ $(IDU_TARGET_DIR)/
	
	#only keep 1 default language--zh.
	##-for x in en es fr it pt sv; do \
	##		rm -fr $(IDU_TARGET_DIR)/config_default/lang/$$x \
	##	done;
	
	for i in $(DIRS) ; do make -C $$i $@ || exit $?; done
	
	#make the check sum of all files
	$(IDU_TARGET_DIR)/bin/appsum make $(IDU_TARGET_DIR)
	
	#make pkg
	#mkpkg type target_dir pkg_dir lang_name suffix_name
	$(IDU_TARGET_DIR)/bin/mkpkg batch $(IDU_TARGET_DIR) $(IDU_PKG_DIR) $(IDU_LOCAL_LANGUAGE) $(IDU_SOURCE_VER)
	
clean:
	for i in $(DIRS) ; do make -C $$i clean ; done
	-rm -f make_*.txt *.map
