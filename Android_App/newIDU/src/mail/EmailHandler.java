package mail;

import utils.DealFile;
import android.util.Log;

import app.main.idu.MainActivity;

import data.extraHisModel.FileDeal;

//����  ����ִ�� �����    fang  Add
public class EmailHandler{

	public EmailHandler() {
		// TODO Auto-generated constructor stub

	}
	public static String inifile = "/mnt/sdcard/IDU.txt";
	public static boolean runFlag = false;   //���ʼ��澯���� ��ʹ�ܱ�־ 
	
	public static String ServerHost = "mail.kstar.com.cn";  //��������� ��ַ    smtp
	public static String ServerPort = "25";  //����˿ں�             //�󲿷ֶ���25  
	public static String UserName = "fang"; //������������
	public static String FromAddress = "fangjw@kstar.com.cn"; //�������� ��ַ
	public static String Password = "kstar-6";   //�������� ����
	public static String ToAddress = "fjw0312@163.com";   //��������
	public static String Subject = "IDU�澯";     //�ʼ�����
	public static String content = "��ʼ���ԣ�";     //�ʼ�����
	
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
			Log.e("EmailHandler>>EmailHandler_init","��ȡ�ʼ������ļ��쳣�׳���");
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
//			mailInfo.setMailServerHost("smtp.139.com"); //smtp �����ַ
			mailInfo.setMailServerHost(ServerHost); //smtp �����ַ ����oK!
			mailInfo.setMailServerPort(ServerPort); //�󲿷ֶ���25  
			mailInfo.setValidate(true);   
			mailInfo.setUserName(UserName);     //���Ͷ����� ����
            mailInfo.setPassword(Password);      //���Ͷ����� ����     
            mailInfo.setFromAddress(FromAddress);  //���Ͷ����� ��ַ
            mailInfo.setToAddress(ToAddress);    //�����˵�ַ 
            mailInfo.setSubject(Subject);        //�ʼ�����      
            mailInfo.setContent(content);      
            //�������Ҫ�������ʼ�   
            SimpleMailSender sms = new SimpleMailSender();     
            Log.e("send-Mail","flag--2");
            sms.sendTextMail(mailInfo);//���������ʽ    
             //sms.sendHtmlMail(mailInfo);//����html��ʽ  
            Log.e("send-Mail","flag--3");
		}catch(Exception e){
			Log.e("send-Mail","�����ʼ�ʧ�ܣ�");  
		}
	}


}
