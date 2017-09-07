

#/////////////////////////////////////////////////////////////////////////////////////////////

# ±‡“Î∆˜∂®“Â
CPPFLAG = -D__cplusplus
LDFLAG= -L./
IDU_COMPILER = arm-linux-androideabi-g++
libEquipt_ReadSampler.so: Equipt.cpp 
	$(IDU_COMPILER) $(LDFLAG) -D_LINUX_ -shared -fPIC Equipt.cpp IDUDRV.cpp  -I./ -lhal -o libEquipt_ReadSampler.so

clean : 
	rm -rf *.o libEquipt_ReadSampler.so

#/////////////////////////////////////////////////////////////////////////////////////////////
