# app-linux-server
该工程为物联网设计框架模型

app-linux通信v0.2说明：
该工程为app与linux网络socket服务器的通信功能。
工程设计：
在android端和linux服务器端都建立一个Model的数据模型。
然后在该模型下放置4条全局内存的链表。
get链
set链
que链
cmd链
每个链表上放置的节点为一个设备类
设备类的成员有id type sigId para signal[500]；
signal为信号类-signal类成员有id name value meaning buf[200];
以上200和500为固定大小byte

通信的msg包格式为：
包头： 格式字节型
 * 1byte 包头起始 标识
 * 4byte 包长  包体的长度  
 * 4byte 包的数据类型   int 0-gDat 1-sDat 2-sQue 3-sCmd
 * 4byte 设备id
 * 4byte 设备类型/信息类型 (type编号)
 * 4byte 信号id
 * 4byte 信号数据/相关数据
包体： 格式字符型
* nbyte id `    
* nbyte name`
* nbyte value`
* nbyte meaning
* 1byte #    每一行结束符号


目前的该项目测试：
v0.1目前已测试android app 单独发送4中链数据任何一条链数据到linux，linux服务器端接收和解析都正确，且能放入到Model数据模型中，数据模型的数据获取出来查看也正确。
但通信的net_server，只做数据接收解析，为发送有效数据到app,只发送了无效数据！

v0.2 目前已测试 linux 只发送数据链上的设备数据给app.
app接收和解析正常，数据类放入数据模型正常！


v0.3 目前测试，可实现服务端与app客户端数据同时交互，只要发送接收的2端不是在同一个链表的设备类即可。


v0.4 该版本目前为初步可使用版本，目前格式为app建立2个请求数据设备类在que链上，然后发送数据给linux服务端，服务端事先已建立2个10条信号的gDat设备类在链表，当接收到网络数据请求就发送该设备数据给app端。
app端中的MainWindow页面类：
1.定义一个handler线程接收消息处理类，类中根据接收到的消息，调用该界面中的控件ui更新函数，实现ui控件的更新。
2：在该类定义一个内部线程数据获取类UI_thread，已每2s为周期的遍历界面的每一个控件的updateValue数据更新函数，当数据更新成功则发送handler消息，通知ui主线程上的handler处理函数，处理控件ui刷新。


2017.7.8 test git 9999

2017.7.8 测试多个提交
xxxxxxxxxxxxx
yyyyyyyyyyyyyy
222222222222222