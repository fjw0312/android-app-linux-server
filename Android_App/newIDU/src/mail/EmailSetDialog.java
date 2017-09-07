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


//fang  made   设置 邮箱对话 
public class EmailSetDialog {

	public EmailSetDialog(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	private Context mContext;
	String FileName = "/mnt/sdcard/IDU.txt"; 
	
	//显示用户权限进入对话框
		@SuppressLint("InflateParams")
		public void showPassDialog(){
	    	//LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化  
			LayoutInflater factory = LayoutInflater.from(mContext);
			//把activity_login中的控件定义在View中
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
          	
	        //将LoginActivity中的控件显示在对话框中
			new AlertDialog.Builder(mContext) 
			//对话框的标题
	      .setTitle("邮箱设置")
	       //设定显示的View
	       .setView(mailView)
	       //对话框中的“登陆”按钮的点击事件
	      .setPositiveButton("确定", new DialogInterface.OnClickListener() {

	           public void onClick(DialogInterface dialog, int whichButton) { 
	        	   
	  			//获取用户输入的“用户名”，“密码” 
	        	//注意：mailView.findViewById很重要，因为上面factory.inflate(R.layout.activity_login, null)将页面布局赋值给了textEntryView了

	            
	        	   EmailHandler.ServerHost = SHostEdit.getText().toString().trim();
	        	   EmailHandler.ServerPort = SPortEdit.getText().toString().trim();
	        	   EmailHandler.FromAddress = FAddrEdit.getText().toString().trim();
	        	   EmailHandler.Password = PWordEdit.getText().toString().trim();
	        	   EmailHandler.UserName = NameEdit.getText().toString().trim();            
	        	   EmailHandler.Subject = SubjectEdit.getText().toString().trim();
	            
	            MyThread1 thread = new MyThread1();
	            thread.start();
	   	    	Toast.makeText(mContext, "邮箱设置成功！", Toast.LENGTH_SHORT).show();
	   	    	
	           }
	           
	        })
		    //对话框的“退出”单击事件
		   .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	   //LoginActivity.this.finish();
		         }
		   })
		       
		   //对话框的创建、显示
			.create().show();
		}
		
		//创建 修改配置文件  邮箱配置 线程
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
					Log.e("Ks_ChangeEmailfile>>MyThread","修改 邮件地址 线程 异常抛出！");  
			    }
			}
		};

}
