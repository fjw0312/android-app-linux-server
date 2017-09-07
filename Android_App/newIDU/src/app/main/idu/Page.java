package app.main.idu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import utils.Expression;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

//app ҳ���� ��������ҳ�� 
public class Page extends ViewGroup{
	//��Ĺ���
	public Page(final MainActivity context) {
		super(context);
		m_MainActivity = context;
//		this.setFocusableInTouchMode(true);
	      
		//��ʼ�� myHandler		
		myHandler = new Handler(){ //���յ���Ϣ����
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				switch(msg.what){  
				case 0:
					break;
				case 1:
					//��ȡhandler ��Ϣ ������Ӧuis-id ��ui
					uiID = String.valueOf(msg.obj);
				//	Log.e("MainWindow->Handler>>ID=",uiID);  
					if(p_mapUis.get(uiID)!=null)
						p_mapUis.get(uiID).doInvalidate();  //���ÿؼ��ڲ� ui���º��� 
					break;
				default: 
					break; 
				}
			}
		};
		imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	UI_thread uiThread;
	@Override 
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
		if(visibility == View.VISIBLE){     //�ؼ��ɼ�
			Log.e("TAG_page  "+strPage,"into View.VISIBLE");
			pageRunFlag = true;
			if(uiThread == null ){	
				uiThread = new UI_thread();
				uiThread.start();
			}else if(uiThread.isAlive()==false){
				uiThread = new UI_thread();
				uiThread.start();
			}

		}else if(visibility == View.GONE){  //�ؼ����ɼ�
			Log.e("TAG_page "+strPage,"into View.GONE");
			if(pageRunFlag){
				if(uiThread.isAlive()){
					uiThread.interrupt();
				}
			}
			pageRunFlag = false;
		}	
	}
	public InputMethodManager imm;  //������̹���������
	
	//Fields
	public MainActivity m_MainActivity; //�������ڻ
	String strPage="";//ҳ������
	String uiID = ""; //��Ҫˢ�µĿؼ�
	Canvas m_canvas;
	HashMap<String, VObject> p_mapUis = new HashMap<String, VObject>();//����һ��ҳ��<�ؼ���id���ؼ���>����
//	List<VObject> p_listUis = new ArrayList<VObject>();//����һ��ҳ��<�ؼ���>����
	List<String> p_listUisId = new ArrayList<String>();//����һ��ҳ��<�ؼ���id>����
	List<String> pl_listUisId = new ArrayList<String>();//����һ��ҳ��<�ؼ���id>����
	
	public int w_screen = 0;     //page��Ļ�Ŀ��
	public int h_screen = 0;     //page��Ļ�ĸ߶�
	public float w_screenPer = 1;//page��Ļ�Ŀ�ȱ���
	public float h_screenPer = 1;//page��Ļ�ĸ߶ȱ���
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
	
	public Handler myHandler =null;
	boolean pageRunFlag = true;

	//��дViewGroup ��onLayout  ���Ƹ�����view�ĵװ�λ��  һ�������ڵ���view.onLayout()�ͻ��е���
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
//	    Log.e("Page>>onLayout", strPage+"  into!");
		//�����ؼ����� ���»�����Views�ĵװ�λ��	
		if(arg0){
			Iterator<String> itstr = p_listUisId.iterator(); 
			while(itstr.hasNext()){
				String id = itstr.next();
				VObject views = p_mapUis.get(id);  
				views.doLayout(arg0, arg1, arg2, arg3, arg4);
			}
			Log.e("Page>>onLayout", strPage+"  true-onLayout �ӿؼ�����!!!!!!!");
		}
//		Log.e("Page>>onLayout", strPage+"  onLayout �ӿؼ�����!!!!!!!");
	}
	
	//��дviewGroup ��dispatchDraw()  ���Ƹ�����view  һ�������ڵ���view.onDraw()�ͻ��е���     
	protected void dispatchDraw(Canvas canvas){
		super.dispatchDraw(canvas);
//		Log.e("Page>>dispatchDraw", strPage+"  into!");
		//�����ؼ����� �ػ���views
/*		Iterator<String> itstr = p_listUisId.iterator();
		while(itstr.hasNext()){
			String id = itstr.next();
			VObject views = p_mapUis.get(id);
			drawChild(canvas, views.getViews(), getDrawingTime()); //������view 
		//	Log.e("Page>>dispatchDraw", strPage+"���ƵĿؼ�����"+id);
		}
		Log.e("Page>>dispatchDraw", strPage+" dispatchDraw �ӿؼ�Draw����!!!!!!!");
*/	}
	//��дviewGroup onTouchEvent() 
	public boolean onTouchEvent(MotionEvent event){
		Log.e("Page>>onTouchEvent>>>","into--");
		super.onTouchEvent(event);
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(getWindowToken(), 0);	
		}
		
		//event.getAction() Ϊ��8λ�������ͣ���8λ+1λ��������� 
		//�ú���&0xff ��&MotionEvent.ACTION_MASK = event.getActionMasked()
		switch(event.getActionMasked()){ //�жϴ���������getActionMasked() 
			case MotionEvent.ACTION_DOWN : //����ָ����
				touchPonitNum = 1;				
				start_x = event.getX(0);
				start_y = event.getY(0);
				//���ؼ���
//				InputMethodManager imm = (InputMethodManager)m_MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
				break;
			case MotionEvent.ACTION_UP : //����ָ����
				touchPonitNum = 0;
				
				end_x = event.getX(0);
				end_y = event.getY(0);
				if((oneOpintFlag==true) && (scalePer==1)){    //�ж�����
					if((start_y-end_y <200)||(end_y-start_y <200)){
						if(start_x-end_x >30 ){  //�һ���
							changPage(1);
						}else if(end_x-start_x >30){  //����
							changPage(2);
						}
					}
				}
				Log.e("Page>>onTouchEvent>>>","into ACTION_UP��");
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
					Log.e("page-onTouchEvent","ACTION_MOVE");
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
	@SuppressLint({ "UseSparseArrays", "FloatMath" })
	public void loadPage(String strXmlFile){
		strPage = strXmlFile;
		//����xmlҳ��
		ParseXml parsexml = new ParseXml(m_MainActivity);
		try{
		p_mapUis = parsexml.getXmlStream(strPage);
		}catch(Exception e){
			Log.e("MainWindow-loadPage","����xml�ļ��쳣�׳�");
		}
		parsexml = null;
		
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
//				p_listUis.add(views);
				p_listUisId.add(views.getViewsID());
			}
		}
	//	p_mapIuis = null;
		
		//��������ؼ�ÿ������ҳ��
		Iterator<String> itstr = p_listUisId.iterator();
		while(itstr.hasNext()){
			String id = itstr.next();
			VObject views = p_mapUis.get(id);
			views.doAddViewsToWindow(this); //���ؼ������ҳ��
		}
		
		
		//���������ؼ��İ󶨱��ʽ
		Iterator<String> itId = p_mapUis.keySet().iterator();
		while(itId.hasNext()){
			String id = itId.next();
			VObject obj = p_mapUis.get(id);
			if(obj==null) continue;
			obj.parseExpression(""); //��������ؼ��İ󶨱��ʽ			
		}			 
		System.gc();
		//����UI����ˢ���߳�
		uiThread = new UI_thread();
		uiThread.start();
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
	private class  UI_thread extends Thread {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			List<VObject> Uis = p_listUis;	//<�ؼ���>���� //��ȡ��ǰҳ��Ŀؼ�����
						
			while(pageRunFlag){				
				try{  //sleep 2s  
					Thread.sleep(500); //2000ms   			
					if(Page.this.isShown()==false) return;  //�ж��Ƿ�ҳ��ɼ�
					Log.i("MainWindow->UI_thread>>ҳ������",strPage+"   into!");
			
					//�����ؼ����� ���¿ؼ�����
					Iterator<String> itId_str = p_mapUis.keySet().iterator();
					while(itId_str.hasNext()){
						String id = itId_str.next();
						VObject obj = p_mapUis.get(id);
						if(obj==null) continue;
						//jk mark notice:���ռ��������û������������ݸı䣬�ú����޷���true ��֪ͨhandlerˢ��ui
						boolean up = obj.updataValue("�Ը���"); //���ÿؼ��Զ����ݸ��·���					
						String str_uiId = obj.getViewsID();
						if("".equals(str_uiId)) continue;
						if(up){  //�жϿؼ����ݸ��� �����Ƿ�ɹ�
							Message msg = new Message();
							msg.obj = str_uiId;
							msg.what = 1;
							myHandler.sendMessage(msg);   //����handler ��Ϣ  
						}		
					}
					Thread.sleep(1500); //1000ms  			
					if(Page.this.isShown()==false) return;  //�ж��Ƿ�ҳ��ɼ�
				}catch(InterruptedException e){ 
					Log.i("page->UI_thread>>","�߳��ж�  �׳��� "); 
					return;
				}catch(Exception e){ 
					Log.w("page->UI_thread>>","sleep �����׳��� "); 
				}
		//		Log.e("MainWindow->UI_thread>>ҳ������"+strPage+"--","end! ");
			}
		}
	}

}
