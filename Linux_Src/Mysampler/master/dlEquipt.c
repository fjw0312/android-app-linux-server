#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include<string.h>
#include<dlfcn.h>

#include "dlEquipt.h"

//该文件为 加载调用libEquipt.so 文件API


//new 一个设备动态库 函数APi 结构体节点
struct EquiptApiNode *NewEquiptApiNode(char *libPathName)
{
	struct EquiptApiNode *node = malloc(sizeof(struct EquiptApiNode));
	memset(node->libName, '\0', 100);
	strcpy(node->libName, libPathName);
	
	return node;
}

//打开 加载动态库
bool DL_open(IN char *libName, OUT void **libHandleAddr)
{
	//dlopen
	void *libHandle = dlopen(libName, RTLD_LAZY);
    if(libHandle==NULL)
	{
		printf("dlopen-%s\n", dlerror());
		printf("dlopen return NULL!\n");
		return false;
	}
	*libHandleAddr = libHandle; //获取打开动态库句柄的地址
	return true;
}
//关闭 加载动态库
bool DL_close(IN void *libHandle)
{
	//dlclose
	dlclose(libHandle);
	return true;
}

//连接 加载动态库 函数
bool DL_sym_AllApi(IN void *libHandle, OUT struct EquiptApiNode *ApiNode)
{
	//dlsym - DriverInit
	*(void **) (&(ApiNode->DriverInit)) = dlsym(libHandle, "DriverInit");
	if(dlerror()!=NULL)
	{
		printf("dlerror dlsym(DriverInit) - %s\n", dlerror());
		printf("dlerror dlsym(DriverInit)!= NULL\n");
		return false;
	}

	//dlsym - DriverStop
	*(void **) (&(ApiNode->DriverStop)) = dlsym(libHandle, "DriverStop");
	if(dlerror()!=NULL)
	{
		printf("dlerror dlsym(DriverStop) - %s\n", dlerror());
		printf("dlerror dlsym(DriverStop)!= NULL\n");
		return false;
	}
	//dlsym - Read
	*(void **) (&(ApiNode->Read)) = dlsym(libHandle, "Read");
	if(dlerror()!=NULL)
	{
		printf("dlerror dlsym(Read) - %s\n", dlerror());
		printf("dlerror dlsym(Read)!= NULL\n");
		return false;
	}
	//dlsym - Control
	*(void **) (&(ApiNode->Control)) = dlsym(libHandle, "Control");
	if(dlerror()!=NULL)
	{
		printf("dlerror dlsym(Control) - %s\n", dlerror());
		printf("dlerror dlsym(Control)!= NULL\n");
		return false;
	}
	//dlsym - Report
	*(void **) (&(ApiNode->Report)) = dlsym(libHandle, "Report");
	if(dlerror()!=NULL)
	{
		printf("dlerror dlsym(Report) - %s\n", dlerror());
		printf("dlerror dlsym(Report)!= NULL\n");
		return false;
	}
	//dlsym - Write
	*(void **) (&(ApiNode->Write)) = dlsym(libHandle, "Write");
	if(dlerror()!=NULL)
	{
		printf("dlerror dlsym(Write) - %s\n", dlerror());
		printf("dlerror dlsym(Write)!= NULL\n");
		return false;
	}
	
	return true;
}