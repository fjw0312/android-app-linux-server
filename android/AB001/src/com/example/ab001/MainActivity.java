package com.example.ab001;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import service_net.net_service_thread;

import DataModel.Model;
import DataModel.Signal;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	//�������
	RelativeLayout layout_page = null; //���岼��ҳ�沼������
	MainWindow page = null; //������ʾҳ����
	String strPage="";      //��ǰҳ�������
	
	List<String> list_strPageName = new ArrayList();//����ҳ������������
	HashMap<String, MainWindow> m_page = new HashMap<String, MainWindow>(); //����<ҳ����,ҳ����>����
	
	String pagePath ="";//ҳ���ļ���·��
	String pageDir = "/page_path/"; //ҳ���ļ����ļ�������
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //���س�app������ ���setContentView֮ǰ		 
		setContentView(R.layout.activity_main);
		
		//���ô���ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //����ϵͳ������     //���ص�����
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		//��ȡ���������
		layout_page = (RelativeLayout)findViewById(R.id.layout_id);
		//��ȡҳ���ļ�·��
		pagePath = Environment.getExternalStorageDirectory().getPath()+pageDir;
		Log.e("MainAcitivity-onCreate>>pagePath:", pagePath);
		
		//��ȡpagelistʵ����ҳ��
		if(!readPage()){
			Log.e("MainAcitivity-onCreate","��ȡ����ҳ��ʧ��");
			Toast.makeText(this, "readPage ʧ�ܣ�", Toast.LENGTH_LONG).show();
		}

		//����ҳ���뻭������
		if(list_strPageName.isEmpty()){
			Toast.makeText(this, "pagelist ��ȡ���Ϊ�գ�", Toast.LENGTH_LONG).show();
			Log.e("MainAcitivity-onCreate>>list_strPageName:", "isEmpty");
		}else{
		
			strPage = list_strPageName.get(0);	
			page = m_page.get(strPage);
			layout_page.addView(page, 1024, 768);   //vtu ��ȥ���������±���ʣ��Լ1024*635
		}
		
		//��ʼ����������������
		que_model_node_init();
		
		//��������ͨ���߳� ����
		net_service_thread net_thread = new net_service_thread();
		net_thread.start();
		 
	}
	
	
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
			for(int i=0;i<120;i++){  //���120��ҳ������
				String strpage = reader.readLine();
				strpage = strpage.trim(); //ȥ���ַ������ұ߿ո�
				if("".equals(strpage)){
					break;		  //��ҳ���ȡ�������ѭ��		
				}else{
					list_strPageName.add(strpage); //����ȡ��ҳ�����Ʒ��� ҳ��������������
				}
			}
			reader.close();
		}catch(Exception e){			
	//		Log.e("MainAcitivity-readPage","��ȡpagelistҳ������ʧ�ܣ�");
		}
		//����ҳ��������ظ���ҳ�� ʵ���� ����<ҳ�����ƣ�ҳ����>����
		Iterator<String> itr = list_strPageName.iterator();
		while(itr.hasNext()){
			String strPage = itr.next(); //��ȡҳ������
			String strPagePath = pagePath+strPage; //��ȡҳ�����ļ�����·��
			MainWindow page = new MainWindow(this); //����ҳ�������
			page.loadPage(strPagePath);            //����ҳ��ʵ����
			m_page.put(strPage, page);            //ҳ�� ����<ҳ�����ƣ�ҳ����>����       
			
		}
		return true;
	}
	
	
	//activity�л�ҳ�� ��ת��ĳһҳ
	public void onPageChange(String new_pageName){
		MainWindow oldPage = m_page.get(strPage);         //��ȡ��ǰҳ����
		MainWindow newPage = m_page.get(new_pageName);    //��ȡ��Ҫ�л���ҳ����
		layout_page.removeView(oldPage);   
		layout_page.addView(newPage);
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

	
	//��ʼ����������ģ��
	private void que_model_node_init(){
		
		//��ʼ�� ����ģ��
		Model model = new Model();
/*	//��ʽһ	
		//��ʼ�� ����ģ�͵�����������
		List<Integer>  lst = new ArrayList<Integer>();
		for(int i=1;i<4;i++){
			lst.add(i);
		}
		//��ʼ�� ����ģ�ͻ�����������
		Model.Model_init_lst(lst); 
*/	
		
	//��ʽ��
	//	Model.add_get_setHt(1, 10, 0, 0); //��ʼ�� ���������
	//	Model.add_set_setHt(1, 20, 0, 0); //��ʼ�� ���������
		Model.add_que_setHt(1, 30, 0, 0); //��ʼ�� ���������
		Model.add_que_setHt(2, 30, 0, 0); //��ʼ�� ���������
	//	Model.add_cmd_setHt(1, 40, 0, 0); //��ʼ�� ���������
/*		//���get�����豸id 2 һ���ź���  
		Signal sig1g = new Signal(1,"get-2-�ź�",200,"�źź���1");    
		Model.add_Signal_getHt(1,sig1g.signalID,sig1g);
		Signal sig2g = new Signal(2,"get-2-�ź�",400,"�źź���2");
		Model.add_Signal_getHt(1,sig2g.signalID,sig2g); 
		//���set�����豸id 2 һ���ź���
		Signal sig1s = new Signal(1,"set-2-�ź�",200,"�źź���1");
		Model.add_Signal_setHt(1,sig1s.signalID,sig1s); 
		Signal sig2s = new Signal(2,"set-2-�ź�",400,"�źź���2");
		Model.add_Signal_setHt(1,sig2s.signalID,sig2s);
		//���que�����豸id 2 һ���ź���
		Signal sig1 = new Signal(2,"que-2-�ź�",200,"�źź���2"); 
		Model.add_Signal_queHt(2,sig1.signalID,sig1);
		Signal sig2 = new Signal(3,"que-2-�ź�",400,"�źź���3"); 
		Model.add_Signal_queHt(2,sig2.signalID,sig2);
		Signal sig3 = new Signal(4,"que-2-�ź�",400,"�źź���4");
		Model.add_Signal_queHt(2,sig3.signalID,sig3);
		//���cmd�����豸id 2 һ���ź���
		Signal sig1c = new Signal(1,"cmd-2-�ź�",200,"�źź���1");
		Model.add_Signal_cmdHt(1,sig1c.signalID,sig1c);
		Signal sig2c = new Signal(2,"cmd-2-�ź�",400,"�źź���2");
		Model.add_Signal_cmdHt(1,sig2c.signalID,sig2c);
*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
