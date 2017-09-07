package mail;
import utils.DealFile;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import app.main.idu.R;


//fang  made   ���� ����Ի� 
public class EmailSetDialog {

	public EmailSetDialog(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	private Context mContext;
	String FileName = "/mnt/sdcard/IDU.txt"; 
	
	//��ʾ�û�Ȩ�޽���Ի���
		@SuppressLint("InflateParams")
		public void showPassDialog(){
	    	//LayoutInflater��������layout�ļ����µ�xml�����ļ�������ʵ����  
			LayoutInflater factory = LayoutInflater.from(mContext);
			//��activity_login�еĿؼ�������View��
			final View mailView = factory.inflate(R.layout.mail_dialog, null);
			
        	final EditText SHostEdit = (EditText)mailView.findViewById(R.id.SHostEdit);
          	final EditText SPortEdit = (EditText)mailView.findViewById(R.id.SPortEdit);
          	final EditText FAddrEdit = (EditText)mailView.findViewById(R.id.FAddrEdit);
          	final EditText PWordEdit = (EditText)mailView.findViewById(R.id.PWordEdit);
          	final EditText NameEdit = (EditText)mailView.findViewById(R.id.NameEdit);
          	final EditText SubjectEdit = (EditText)mailView.findViewById(R.id.SubjectEdit);
          	SHostEdit.setHint(EmailHandler.ServerHost);
          	SPortEdit.setHint(EmailHandler.ServerPort);
          	FAddrEdit.setHint(EmailHandler.FromAddress);
          	PWordEdit.setHint(EmailHandler.Password);
          	NameEdit.setHint(EmailHandler.UserName);
          	SubjectEdit.setHint(EmailHandler.Subject);
          	
	        //��LoginActivity�еĿؼ���ʾ�ڶԻ�����
			new AlertDialog.Builder(mContext) 
			//�Ի���ı���
	      .setTitle("��������")
	       //�趨��ʾ��View
	       .setView(mailView)
	       //�Ի����еġ���½����ť�ĵ���¼�
	      .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

	           public void onClick(DialogInterface dialog, int whichButton) { 
	        	   
	  			//��ȡ�û�����ġ��û������������롱 
	        	//ע�⣺mailView.findViewById����Ҫ����Ϊ����factory.inflate(R.layout.activity_login, null)��ҳ�沼�ָ�ֵ����textEntryView��

	            
	        	   EmailHandler.ServerHost = SHostEdit.getText().toString().trim();
	        	   EmailHandler.ServerPort = SPortEdit.getText().toString().trim();
	        	   EmailHandler.FromAddress = FAddrEdit.getText().toString().trim();
	        	   EmailHandler.Password = PWordEdit.getText().toString().trim();
	        	   EmailHandler.UserName = NameEdit.getText().toString().trim();            
	        	   EmailHandler.Subject = SubjectEdit.getText().toString().trim();
	            
	            MyThread1 thread = new MyThread1();
	            thread.start();
	   	    	Toast.makeText(mContext, "�������óɹ���", Toast.LENGTH_SHORT).show();
	   	    	
	           }
	           
	        })
		    //�Ի���ġ��˳��������¼�
		   .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	   //LoginActivity.this.finish();
		         }
		   })
		       
		   //�Ի���Ĵ�������ʾ
			.create().show();
		}
		
		//���� �޸������ļ�  �������� �߳�
		private class MyThread1 extends Thread{
			
			final String ServerHost = "ServerHost";
			final String ServerPort = "ServerPort";
			final String FromAddress = "FromAddress";
			final String Password = "Password";
			final String UserName = "UserName"; 
			final String Subject = "Subject";
			
			public void run() {
				try{
					DealFile file = new DealFile();

					String NewStr1 = ServerHost+"="+EmailHandler.ServerHost;
					String NewStr2 = ServerPort+"="+EmailHandler.ServerPort;
					String NewStr3 = FromAddress+"="+EmailHandler.FromAddress;
					String NewStr4 = Password+"="+EmailHandler.Password;
					String NewStr5 = UserName+"="+EmailHandler.UserName;
					String NewStr6 = Subject+"="+EmailHandler.Subject;
					file.exchange_str_line(FileName, ServerHost, NewStr1);
					file.exchange_str_line(FileName, ServerPort, NewStr2);
					file.exchange_str_line(FileName, FromAddress, NewStr3);
					file.exchange_str_line(FileName, Password, NewStr4);
					file.exchange_str_line(FileName, UserName, NewStr5);
					file.exchange_str_line(FileName, Subject, NewStr6);
							
				}catch(Exception e){ 
					Log.e("Ks_ChangeEmailfile>>MyThread","�޸� �ʼ���ַ �߳� �쳣�׳���");  
			    }
			}
		};

}
