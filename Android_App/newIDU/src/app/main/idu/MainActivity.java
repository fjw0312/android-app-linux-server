package app.main.idu;
/**********
 * ��Ȩ��˾��ChaoYF   
 * ���ʱ�䣺2016-10-1  designed TO 2017-2-21
 * ����ˣ�fang     E-mail:fjw0312@163.com
 * �汾�ţ�����ư汾    ��Ȩ������ fjw0312��������
 * ********/
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import utils.DealFile;
import data.pool.DataPoolThreadRun;
import SAM.DataPool.DataPoolRun;
import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_EquiptCfg;
import SAM.XmlCfg.xml_cmdCfg;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_signalCfg;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

//���� �ӿڷ���  ����SAM
public class MainActivity extends Activity {

	//�������  
	RelativeLayout layout_page = null; //���岼��ҳ�沼������
	Page myPage = null; //������ʾҳ����
	String strPage="";      //��ǰҳ������� 
	
	List<String> list_strPageName = new ArrayList<String>();//����ҳ������������
	HashMap<String, Page> m_page = new HashMap<String, Page>(); //����<ҳ����,ҳ����>���� 
	
	String pagePath ="";//ҳ���ļ���·�� 
	String pageDir = "/IDU_Page/"; //ҳ���ļ����ļ�������
	
	public static int screen_width = 1024; //��Ļ�ֱ��� ���
	public static int screen_height = 768; //��Ļ�ֱ��� �߶�
	public static float densityPer = 1;
	boolean loadPageFlag = false; //ҳ����ؽ�����־λ 
	public static String EventCmd = "";   //���ڸ澯����
	public static int DataCaseNum = 2;   //���õ����ݷ������   //1:dataNet  2:SAMNet

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //���س�app������ ���setContentView֮ǰ
		setContentView(R.layout.activity_main);

		//���ô���ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //����ϵͳ������     
		
		Intent intent = new Intent("android.intent.action.STATUSBAR_INVISIBILITY"); //���ص����� ok
		this.sendBroadcast(intent);
				
	//	  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//���ص����� 
	//    getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);        //���ص�����ͼ��  
	//    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE); //���ص�����ͼ�� 
	
		//��ȡ��Ļ�ֱ���
  //     Display display = getWindowManager().getDefaultDisplay();
  //     screen_width = display.getWidth();  
  //     screen_height = display.getHeight(); 
  //     Log.e("MainAcitivity-onCreate>>--f1--��ȡ��Ļ�ܶȴ�С:", "screen_width="+String.valueOf(screen_width)+" screen_height="+String.valueOf(screen_height));
		
	    screen_width = this.getResources().getDisplayMetrics().widthPixels;
		screen_height = this.getResources().getDisplayMetrics().heightPixels+50; 
		densityPer = this.getResources().getDisplayMetrics().density;
//		Toast.makeText(this, "screen_width="+String.valueOf(screen_width)+"screen_height="+String.valueOf(screen_height), Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "densityPer="+String.valueOf(this.getResources().getDisplayMetrics().density), Toast.LENGTH_LONG).show();
		
		//��ȡ�澯����  ����  
		try{
			DealFile file = new DealFile();
			String str= file.read_File_str_line("/mnt/sdcard/ECMD.txt", "EventCmd"); 
			String[] a = str.split("=");
			EventCmd = a[1];
			Log.e("MainActivity>>EventCmd",  EventCmd); 
		}catch(Exception e){
			EventCmd = "";  
		}
		
		//��ȡ���������  
		layout_page = (RelativeLayout)findViewById(R.id.layout_id); 
		
		//��ȡҳ���ļ�·��
		pagePath = Environment.getExternalStorageDirectory().getPath()+pageDir;
		Log.e("MainAcitivity-onCreate>>pagePath:", pagePath);
		
		//��ҳ������߳�
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub  
				//��ȡpagelistʵ����ҳ��    �˴��ܺ�ʱ 5s  �������߳� ִ��
				if(!readPage()){
					Log.e("MainAcitivity-onCreate","��ȡ����ҳ��ʧ��");
					myhandler.sendEmptyMessage(2);			
				}
				myhandler.sendEmptyMessage(3);	
				loadPageFlag = true;  //ҳ����ؽ�����־ 
			}
		}).start();
		
		if(DataCaseNum == 1){       // ִ�������߳�  ������1
			DataPoolThreadRun dataThread = new DataPoolThreadRun(); 
			dataThread.start();
		}else if(DataCaseNum == 2){  // ִ�������߳�  ������2
			DataPoolRun dataRun = new DataPoolRun();
			dataRun.start();
		}
		
	}

	private Handler myhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub  1
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				String strPageName = (String)msg.obj;
				String strPagePath = pagePath+strPageName; //��ȡҳ�����ļ�����·��		
				Page page = new Page(MainActivity.this); //����ҳ�������
				page.loadPage(strPagePath);            //����ҳ��ʵ���� 
				m_page.put(strPageName, page);             //ҳ�� ����<ҳ�����ƣ�ҳ����>����    
	//			Log.e("MainActivity>>myhandler",strPageName+"   "+strPagePath);
				//����ҳ�浽����
				if(strPageName.equals(list_strPageName.get(0))){
					page.setVisibility(View.VISIBLE);
					strPage = strPageName;	
					myPage = page; 
				}else{
					page.setVisibility(View.GONE);
				}				
				layout_page.addView(page);	//�ڲ���ͼȫ������  ��ʱ1s
				break;	
			case 2:
				Toast.makeText(MainActivity.this, "readPage ʧ�ܣ�", Toast.LENGTH_LONG).show();
				break;
			case 3:				
				Toast.makeText(MainActivity.this, "ҳ����ؽ�����", Toast.LENGTH_LONG).show();
				break;

			default: break;
			}
		}
		
	};
	
	//��ȡpagelist�ļ���ҳ���ļ�����ʵ�������ظ���ҳ�溯��
	private boolean readPage() {
		//��ȡҳ�������ļ� 
		String pagelistPath = pagePath+"pagelist";
		BufferedReader reader = null; //�����ȡ�ļ������ַ��� 
		try{ 
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(pagelistPath),"gb2312"));  //���ļ��ֽ�����ȡ ת���ɶ�ȡ�ַ��� ��װ���ɻ����ַ���
		}catch(Exception e){
			Log.e("MainAcitivity-readPage","��ȡpagelist�ļ�ʧ�ܣ�");
			return false;
		}		
		//��ȡҳ����������
		try{
			for(int i=0;i<100;i++){  //���100��ҳ������
				String strpage = reader.readLine();			
				if("".equals(strpage) || strpage ==null){
					break;		  //��ҳ���ȡ�������ѭ��		
				}else{
					strpage = strpage.trim(); //ȥ���ַ������ұ߿ո�
					list_strPageName.add(strpage); //����ȡ��ҳ�����Ʒ��� ҳ��������������
				}
			}
			reader.close();
		}catch(Exception e){	
			reader = null;
			Log.e("MainAcitivity-readPage","��ȡpagelistҳ������ʧ�ܣ�");
		}
		
		//�ȼ�����ҳ   Ŀ�ģ�����ҳ���������� �� �������ã� 
		try{
			Message msg = new Message();
			msg.obj = list_strPageName.get(0);
			msg.what = 1;
			myhandler.sendMessage(msg);
			Thread.sleep(4*1000);
		
			//����ҳ��������ظ���ҳ�� ʵ���� ����<ҳ�����ƣ�ҳ����>����
			for(int i=1;i<list_strPageName.size();i++){
				String strPageName = list_strPageName.get(i); //��ȡҳ������
				Message msg2 = new Message();
				msg2.obj = strPageName;
				msg2.what = 1;
				myhandler.sendMessage(msg2); 
			
			}
		}catch(Exception e){
			Log.e("MainActivity>>readPage-1","�쳣�׳���");	
		}
	
		return true;
	}
	
	
	//activity�л�ҳ�� ��ת��ĳһҳ 
	public void onPageChange(String new_pageName){
		if(loadPageFlag==false){
			Toast.makeText(MainActivity.this, "ҳ������У����Ժ�", Toast.LENGTH_LONG).show();
			return;
		}
		Page oldPage = m_page.get(strPage);         //��ȡ��ǰҳ����  
		Page newPage = m_page.get(new_pageName);    //��ȡ��Ҫ�л���ҳ���� 
	//	layout_page.addView(newPage);   //�ü��صķ�ʽ  ��Ӧ�ٶ�̫���ˣ�
	//	layout_page.removeView(oldPage); 
	//	newPage.bringToFront();         //��ͼ����ʾ ��ӦҲ��̫����		
		oldPage.setVisibility(View.GONE);
		newPage.setVisibility(View.VISIBLE);
		
		oldPage.clearFocus();
//		newPage.requestFocus();
		
		Log.e("MainActivity>>onPageChange>>>","�����л�ҳ�棡");
		myPage = newPage;
		strPage = new_pageName;
	} //��һҳ 
	public void nextPageChange(){
		int num = list_strPageName.indexOf(strPage);
		num++;
		if(list_strPageName.size()<=num) return;
		String new_pageName = list_strPageName.get(num);
		onPageChange(new_pageName);
	}//��һҳ
	public void prePageChange(){
		int num = list_strPageName.indexOf(strPage);
		num--;
		if(num<0) return;
		String new_pageName = list_strPageName.get(num);
		onPageChange(new_pageName);		
	}
}
