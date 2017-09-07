

#/////////////////////////////////////////////////////////////////////////////////////////////

# ±‡“Î∆˜∂®“Â
CPPFLAG = -D__cplusplus
LDFLAG= -L./
IDU_COMPILER = arm-linux-androideabi-g++
libUpDataEquipt.so: Equipt.cpp 
	$(IDU_COMPILER) $(LDFLAG) -D_LINUX_ -D_UPDATA_ -shared -fPIC Equipt.cpp IDUDRV.cpp  -I./ -lhal -o libUpDataEquipt.so

clean : 
	rm -rf *.o libUpDataEquipt.so

#/////////////////////////////////////////////////////////////////////////////////////////////
