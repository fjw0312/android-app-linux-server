package com.example.ab001;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import common.ParseXml;
import common.VObject;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

//app ҳ���� ��������ҳ��
public class MainWindow extends ViewGroup {
	//��Ĺ���
	public MainWindow(final MainActivity context){
		super(context);
		m_MainActivity = context;
		//��ȡ��Ļ�ĳߴ�
		w_screen = getResources().getDisplayMetrics().widthPixels;
		h_screen = getResources().getDisplayMetrics().heightPixels;
		//��ʼ�� myHandler
		myHandler = new Handler(){ //���յ���Ϣ����
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				switch(msg.what){  
				case 0:
					break;
				case 1:
					//��ȡhandler ��Ϣ ������Ӧuis-id ��ui
					String uiID = String.valueOf(msg.obj);
					Log.e("MainWindow->Handler>>ID=",uiID);
					if(p_mapUis.get(uiID)!=null)
						p_mapUis.get(uiID).doInvalidate();  //���ÿؼ��ڲ� ui���º���
					break;
				default:
					break;
				}

			}
		};
		//����UI����ˢ���߳�
		UI_thread.start();
	}
	
	//Fields
	MainActivity m_MainActivity; //�������ڻ
	String strPage="";//ҳ������
	HashMap<String, VObject> p_mapUis = new HashMap<String, VObject>();//����һ��ҳ��<�ؼ������ؼ���>����
	List<VObject> p_listUis = new ArrayList();//����һ��ҳ��<�ؼ���>����
	
	int w_screen = 0;     //��Ļ�Ŀ��
	int h_screen = 0;     //��Ļ�ĸ߶�
	int touchPonitNum = 0; //����Ĵ�������
	float newDistance = 0;   //�����Ŵ��2��֮����� �ڶ���մ���ʱ
	float oldDistance = 0;   //�����Ŵ��2��֮����� �ڶ����ƶ�ʱ
	float scalePer = 1;  //����ķŴ����
	
	boolean oneOpintFlag =true; //�Ƿ񵥵㻬����ʶ���� ���㻬�� true;
	float start_x =0;
	float start_y =0;
	float end_x =0;
	float end_y =0;
	float tranlate_x =0; //�ƶ�������x����
	float tranlate_y =0; //�ƶ�������y����
	float oldTranlate_x =0; //�ƶ�������x����old
	float oldTranlate_y =0; //�ƶ�������y����old
	
	Handler myHandler =null;

	//��дViewGroup ��onLayout  ���Ƹ�����view�ĵװ�λ��
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		//�����ؼ����� ���»�����Views�ĵװ�λ��
		Iterator<VObject> iter = p_listUis.iterator();
		while(iter.hasNext()){
			VObject obj = iter.next();
			obj.doLayout(arg0, arg1, arg2, arg3, arg4);			
		}		
	}
	
	//��дviewGroup ��dispatchDraw()  ���Ƹ�����view
	protected void dispatchDraw(Canvas canvas){
		super.dispatchDraw(canvas);
		//�����ؼ����� �ػ���views
		Iterator<VObject> iter = p_listUis.iterator();
		while(iter.hasNext()){
			VObject obj = iter.next();
			drawChild(canvas, obj.getViews(), getDrawingTime()); //������view
		}
	}
	//��дviewGroup onTouchEvent()
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("MainWindow-onTouchEvent��ӡ�ߴ磺","��ȣ�"+String.valueOf(w_screen)+" �߶ȣ�"+String.valueOf(h_screen));
		//event.getAction() Ϊ��8λ�������ͣ���8λ+1λ��������� 
		//�ú���&0xff ��&MotionEvent.ACTION_MASK = event.getActionMasked()
		switch(event.getActionMasked()){ //�жϴ���������getActionMasked() 
			case MotionEvent.ACTION_DOWN : //����ָ����
				touchPonitNum = 1;				
				start_x = event.getX(0);
				start_y = event.getY(0);
				break;
			case MotionEvent.ACTION_UP : //����ָ����
				touchPonitNum = 0;
				
				end_x = event.getX(0);
				end_y = event.getY(0);
				if((oneOpintFlag==true) && (scalePer==1)){    //�ж�����
					if((start_y-end_y <20)||(end_y-start_y <20)){
						if(start_x-end_x >40 ){  //�һ���
							changPage(1);
						}else if(end_x-start_x >40){  //����
							changPage(2);
						}
					}
				}
				oneOpintFlag = true;	
				break;
			case MotionEvent.ACTION_POINTER_DOWN: //��һ������Ҫ����ָ����
				touchPonitNum += 1;
				oldDistance = distance(event); //��ȡ��㴥������ʱ����
				oneOpintFlag = false;  //��㻬��ϵ�ж���
//				Log.e("MainWindow-onTouchEvent","ACTION_POINTER_DOWN");
				break;
			case MotionEvent.ACTION_POINTER_UP: //��һ������Ҫ����ָ����
				touchPonitNum -= 1;
//				Log.e("MainWindow-onTouchEvent","ACTION_POINTER_UP");
				break;
			case MotionEvent.ACTION_MOVE : //����ָ�ƶ�
				if(touchPonitNum>=2){  //�ж�㴥��
					newDistance = distance(event); //��ȡ��㴥���ƶ�ʱ����
					float per = (newDistance-oldDistance)/100;					
					scalePer = per + scalePer;
					if(scalePer<1) scalePer = 1;
					if(scalePer>10) scalePer = 10;
//					Log.e("MainWindow-onTouchEvent->>distance:","����"+String.valueOf(scalePer));
					if(newDistance-oldDistance>5){ //�Ŵ�
						
						this.setScaleX(scalePer); //��������
						this.setScaleY(scalePer);

						
					}else if(oldDistance-newDistance>5){ //��С
						
						this.setScaleX(scalePer); //��������
						this.setScaleY(scalePer);
					
				    }

				}else{    //�����ƶ�
					if(scalePer>1){     //�Ŵ���϶� ��Ļλ��       notices:δ�����϶����հ״�
						end_x = event.getX(0);
						end_y = event.getY(0);
						tranlate_x = end_x-start_x;
						tranlate_y = end_y-start_y;
						this.setTranslationX(oldTranlate_x+tranlate_x);//�ƶ�view�ĵװ�λ��
						this.setTranslationY(oldTranlate_y+tranlate_y);	
						oldTranlate_x = oldTranlate_x+tranlate_x;
						oldTranlate_y = oldTranlate_y+tranlate_y;
						Log.e("MainWindow-onTouchEvent>>oldTranlate_x",String.valueOf(oldTranlate_x));
						Log.e("MainWindow-onTouchEvent>>oldTranlate_y",String.valueOf(oldTranlate_y));
					}else{
						this.setTranslationX(0);//�ƶ�view�ĵװ�λ��
						this.setTranslationY(0);
						oldTranlate_x = 0;
						oldTranlate_y = 0;
					}
				}
				break;
				
		}
		
		return true;
		
	}
	//����2�㴥���ľ���  ���ڷŴ����
	private float distance(MotionEvent event){
		float eX = event.getX(1) - event.getX(0); // ����ĵ����� - ǰ��������
		float eY = event.getY(1) - event.getY(0);
		return FloatMath.sqrt(eX * eX + eY * eY);
	}

	
	//ҳ�����
	public void loadPage(String strXmlFile){
		strPage = strXmlFile;
		//����xmlҳ��
		ParseXml parsexml = new ParseXml(m_MainActivity);
		try{
		p_mapUis = parsexml.getXmlStream(strPage);
		}catch(Exception e){
			Log.e("MainWindow-loadPage","����xml�ļ��쳣�׳�");
		}
		
		//��ȡ����ͼ����ֵ-����<�ؼ�ͼ�㣬�ؼ���>�������ؼ�������
		HashMap<Integer,VObject> p_mapIuis = new HashMap<Integer,VObject>();
		int maxZIndex = 0;
		Iterator<String> iter = p_mapUis.keySet().iterator();
		while(iter.hasNext()){
			String strViews = iter.next();
			VObject views = p_mapUis.get(strViews);
			p_mapIuis.put(views.getViewsZIndex(), views);
			if(views.getViewsZIndex()>maxZIndex){
				maxZIndex = views.getViewsZIndex();
			}		
		}
		//���ؼ���ͼ�������������
		for(int i=0;i<maxZIndex+1;i++){
			if(p_mapIuis.containsKey(i)){   //�жϸ�ͼ���Ƿ��пؼ�
				VObject views = p_mapIuis.get(i);
				p_listUis.add(views);
			}
		}
		//��������ؼ�ÿ������ҳ��
		Iterator<VObject> it = p_listUis.iterator();
		while(it.hasNext()){
			VObject views = it.next();
			views.doAddViewsToWindow(this); //���ؼ������ҳ��
		}
	}
	
	//ҳ���л�  //�������eg:page2.xml
	public void changePage(String strNewPage){
		m_MainActivity.onPageChange(strNewPage);
	}
	//ҳ���л�  //�������arg 0, 1:��һҳ 2��ǰһҳ
	public void changPage(int arg){
		if(arg==1)
			m_MainActivity.nextPageChange();
		if(arg==2)
			m_MainActivity.prePageChange();
	}
	
	
	//ʵ���� һ��UI����ˢ�µ��߳�
	Thread UI_thread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
//				Log.e("MainWindow->UI_thread>>ҳ������"+strPage+"--","into! ");
				//��ȡ��ǰҳ��Ŀؼ�����
				List<VObject> Uis = p_listUis;//<�ؼ���>����
				//�����ؼ����� ���¿ؼ�����
				Iterator<VObject> iter = Uis.iterator();
				while(iter.hasNext()){
					VObject obj = iter.next();
					if(obj==null) continue;
					
					boolean up = obj.updataValue("�Ը���"); //���ÿؼ��Զ����ݸ��·���
					Message msg = new Message();
					String str_uiId = obj.getViewsID();
					if("".equals(str_uiId)) continue;
					msg.obj = str_uiId;
					msg.what = 1;
					if(up)  //�жϿؼ����ݸ��� �����Ƿ�ɹ�
						myHandler.sendMessage(msg);   //����handler ��Ϣ
				}
				//sleep 2s
				try{
					Thread.sleep(2000); //2000ms
				}catch(Exception e){
					
				}
			}
		}
	});

}
