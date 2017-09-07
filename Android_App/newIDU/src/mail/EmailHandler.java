package mail;

import utils.DealFile;
import android.util.Log;

import app.main.idu.MainActivity;

import data.extraHisModel.FileDeal;

//邮箱  发送执行 入口类    fang  Add
public class EmailHandler{

	public EmailHandler() {
		// TODO Auto-generated constructor stub

	}
	public static String inifile = "/mnt/sdcard/IDU.txt";
	public static boolean runFlag = false;   //该邮件告警功能 的使能标志 
	
	public static String ServerHost = "mail.kstar.com.cn";  //邮箱服务器 地址    smtp
	public static String ServerPort = "25";  //服务端口号             //大部分都用25  
	public static String UserName = "fang"; //发送邮箱名称
	public static String FromAddress = "fangjw@kstar.com.cn"; //发送邮箱 地址
	public static String Password = "kstar-6";   //发送邮箱 密码
	public static String ToAddress = "fjw0312@163.com";   //接收邮箱
	public static String Subject = "IDU告警";     //邮件主题
	public static String content = "初始测试！";     //邮件内容
	
	public static void EmailHandler_init(){ 
		try{
		DealFile fDeal = new DealFile(); 
		fDeal.has_file(inifile);
		String flag  = fDeal.read_str_line("runFlag").trim().split("=")[1];
		if("true".equals(flag)){ 
			runFlag = true;
		}else{
			runFlag = false;
		}
		ServerHost = fDeal.read_str_line("ServerHost").trim().split("=")[1];
		ServerPort = fDeal.read_str_line("ServerPort").trim().split("=")[1];
		UserName = fDeal.read_str_line("UserName").trim().split("=")[1];
		FromAddress = fDeal.read_str_line("FromAddress").trim().split("=")[1];
		Password = fDeal.read_str_line("Password").trim().split("=")[1];
		ToAddress = fDeal.read_str_line("ToAddress").trim().split("=")[1];
		Subject = fDeal.read_str_line("Subject").trim().split("=")[1];
		}catch(Exception e){
			Log.e("EmailHandler>>EmailHandler_init","读取邮件配置文件异常抛出！");
		}
		Log.e("EmailHandler>>", ServerHost+"  "+
								ServerPort+"  "+
								UserName+"  "+
								FromAddress+"  "+
								Password+"  "+
								ToAddress+"  "+
								Subject+"  ");
	}
	
	public void E_Handler() {
		// TODO Auto-generated method stub
		try{
			MailSenderInfo mailInfo = new MailSenderInfo();				
//			mailInfo.setMailServerHost("smtp.139.com"); //smtp 服务地址
			mailInfo.setMailServerHost(ServerHost); //smtp 服务地址 测试oK!
			mailInfo.setMailServerPort(ServerPort); //大部分都用25  
			mailInfo.setValidate(true);   
			mailInfo.setUserName(UserName);     //发送端邮箱 名称
            mailInfo.setPassword(Password);      //发送端邮箱 密码     
            mailInfo.setFromAddress(FromAddress);  //发送端邮箱 地址
            mailInfo.setToAddress(ToAddress);    //接收人地址 
            mailInfo.setSubject(Subject);        //邮件主题      
            mailInfo.setContent(content);      
            //这个类主要来发送邮件   
            SimpleMailSender sms = new SimpleMailSender();     
            Log.e("send-Mail","flag--2");
            sms.sendTextMail(mailInfo);//发送文体格式    
             //sms.sendHtmlMail(mailInfo);//发送html格式  
            Log.e("send-Mail","flag--3");
		}catch(Exception e){
			Log.e("send-Mail","发送邮件失败！");  
		}
	}


}
