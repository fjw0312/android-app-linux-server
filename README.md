# android_App_Sam
android app控制智能设备 控制小系统 （适用于机房监控、环境监控、微模块监控等智能控制系统）

功能介绍：

 android app  与  linux 采集控制应用程序
 
 app:可以支持通过修改配置文件XML（不需要开发代码）从而改变页面内容，包括页面组件、页面数据绑定、页面图片、页面删减等等。
 
 linux应用程序：可以通过添加配置文件和动态协议库.so 从而支持新智能设备的接入。
 
 功能总概述： 本系统平台支持不限定界面定制app、不限定的智能通信设备进入控制、不限定的设备数据内容。
 
 app功能详述：app 采用碎片化的方式，只用一个activity加载不同碎片化/viewGroup 的页面，每个页面由一个可修改的xml文件交给app解析，解析出页面上的所有 组件，每个组件都由自定义View而来。每个组件xml参数可以绑定一条数据表达式，向数据模型池不断以2s为周期请求数据，并将新的数据刷新到组件上。期中数据池模型
 也实时向网络上的linux层应用程序通信获取实时数据，并将实时控制命令发送下去。
 
 linux应用程序：采用串口或者网口 采集智能设备的数据，将采集数据排序后又通过网络发送给android app.
 
 app 和 linux应用程序可以在统一机子上  或同个局域网内。
 
  所有到的技术：  
  
  网络socket  串口通信  so动态库加载  文件读写  android ui布局  自定义组件（控件）  xml解析 等等。
  
  
 注：app 中有2个 数据接口  
 
 data.pool      为公司项目数据接口（由于属于公司故没用）（但接口为本人私下个人设计的也不属于公司）
 
 SAM.dataPool   为个人本项目的数据接口 （与Linux_Src的接口）
 
 本项目工程，只是参考公司的项目，全新自己设计的一个小demo.
 
 app是全新设计的性能更好，当然功能上还不够。linux程序是为测试app自己写的一个小demo与公司项目20w行代码就没可比性了。
 但，这个项目的设计思路还是挺新颖的，适用性也特别强，特别适用智能硬件等控制采集。
 
 最后声明：本工程项目为本人个人开发，不属于任何公司或其他个人所有！

   

 接下来将写 使用步骤：   待续！

