该模块libHal_core
 功能：网络tcp/udp   串口  等协议 端口封装成lib.so   还封装统一接口 libHal.so  
与功能硬件接口comm_net_tcpip.so  comm_net_udpip.so  comm_std_serial.so


libHal.so 为 网口或串口的 接口APi  采用动态加载方式加载网络动态库或串口动态库，故运行时需要这些动态库。
libHal.so 为上层封装标准接口  故 采集时 可以直接静态调用该动态库即可。  接口函数见halcomm.h