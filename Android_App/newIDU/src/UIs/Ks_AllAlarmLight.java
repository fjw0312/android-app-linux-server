package UIs;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Event;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//�Զ���    ϵͳ����澯��  
public class Ks_AllAlarmLight extends ViewGroup implements VObject{
	public Ks_AllAlarmLight(Context context) {
		super(context);
		// load image 
		try {
					AssetManager assetManager = this.getContext().getResources().getAssets();
					InputStream is = null;
					
					if (null == s_bitAlarmLevel1Image)
					{
						is = assetManager.open("ui/Alarm_green.png");
						s_bitAlarmLevel1Image = BitmapFactory.decodeStream(is);
						is.close();
					}

					if (null == s_bitAlarmLevel2Image)
					{
						is = assetManager.open("ui/Alarm_red.png");
						s_bitAlarmLevel2Image = BitmapFactory.decodeStream(is);
						is.close();
					}

					if (null == s_bitAlarmLevel3Image)
					{
						is = assetManager.open("ui/Alarm_redRun.png");
						s_bitAlarmLevel3Image = BitmapFactory.decodeStream(is);
						is.close();
					}

					m_bitCurrentAlarmImage = s_bitAlarmLevel1Image;
			} catch (IOException e) {
					e.printStackTrace();
			}
		m_oPaint = new Paint();
		m_rSrcRect = new Rect();
		m_rDestRect = new Rect();
		myThread thread = new myThread();
		thread.start();
	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "AllAlarmLight";           //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "";          //�ؼ��󶨱��ʽ
	int v_iPosX = 0,v_iPosY = 0;           //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
	String v_strContent = "��������";        //�ؼ��ַ�����
	String v_strFontFamily = "΢���ź�";      //�ؼ���������
	boolean v_bIsBold = false;               //�ؼ������Ƿ�Ӵ�
	String v_strHorizontalContentAlignment = "Center"; //�ؼ����ݵĺ���װ���䷽ʽ
	String v_strVerticalContentAlignment = "Center";  //�ؼ����ݵ�����װ���䷽ʽ
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //������ɫ�仯���ʽ
	String v_strCmdExpression = "";             //�ؼ�����������ʽ
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "��ҳ.xml";           //�ؼ�����¼���ת����
	
	String v_strImgPath = "";
	String v_strImage = "fjw_logo.jpg";
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	//����ؼ�ʹ�õ�Ԫ��
	//��������
	private static Bitmap s_bitAlarmLevel1Image = null; 
	private static Bitmap s_bitAlarmLevel2Image = null; 
	private static Bitmap s_bitAlarmLevel3Image = null; 
	Bitmap m_bitCurrentAlarmImage = null; 
	Paint m_oPaint = null;
	Rect m_rSrcRect = null;
	Rect m_rDestRect = null;
	int EventNum = 0;  //�澯����
	int Flag = 0;   //��¼Ŀǰ�澯״̬
	
	boolean canVISIBLE = false;  //�жϿؼ��Ƿ�ɼ�  ��־����
	myThread thread;	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
		//�ÿؼ���Visibility���Ա仯����
		if(visibility == View.VISIBLE){     //�ؼ��ɼ�
			canVISIBLE = true;
//			thread = new myThread();
//			thread.start();
		}else if(visibility == View.GONE){  //�ؼ����ɼ�
			if(canVISIBLE){
//				if(thread.isAlive()){
//					thread.interrupt();
//				}
			}
			canVISIBLE = false;
		}
	}	
			
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{
//		Log.e("Ks_AllAlarmLight-dispatchDraw","into");
		super.dispatchDraw(canvas);				    
		canvas.drawColor(v_iBackgroundColor);   //����viewgroup�ĵװ���ɫ 
		
		m_rSrcRect.left = 0;
		m_rSrcRect.top = 0;
		m_rSrcRect.right = m_bitCurrentAlarmImage.getWidth();
		m_rSrcRect.bottom = m_bitCurrentAlarmImage.getHeight();
		
		m_rDestRect.left = 0;
		m_rDestRect.top = 0;
		m_rDestRect.right = v_iWidth;
		m_rDestRect.bottom = v_iHeight;
		canvas.drawBitmap(m_bitCurrentAlarmImage, m_rSrcRect, m_rDestRect, m_oPaint);		
	}
	//��дonLayout() ����viewGroup�����е���view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_AllAlarmLight-onLayout","into"); 		
						
	}
	//��д�����¼�onTouchEvent()  ������Form �����¼����ô��ͰѴ����¼�������
/*	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		Log.e("Ks_AllAlarmLight-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return true;
	}
*/	
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_AllAlarmLight-doLayout","into");
		this.layout(v_iPosX, v_iPosY, v_iPosX+v_iWidth, v_iPosY+v_iHeight); //���Ƹ�view�װ�layout		
	}
			
	//����invalidate() �ؼ�����->onDraw()���ú���
	public void doInvalidate(){
		this.invalidate();
	}
	//����requestLayout() �װ����->onLayout()���ú���
	public void doRequestLayout(){
		this.requestLayout();
	}	
	//����addView()���� ������ͼ��ӽ��븸��ͼ
	public boolean doAddViewsToWindow(Page window){
		//��Ļ���䴦��
		v_iPosX = (int)((float)v_iPosX * window.w_screenPer);
		v_iPosY = (int)((float)v_iPosY * window.h_screenPer);
		v_iWidth = (int)((float)v_iWidth * window.w_screenPer);
		v_iHeight = (int)((float)v_iHeight * window.h_screenPer);
		
		window.addView(this);
		return true;
	}
	
		
	//��ȡ�ÿؼ���
	public View getViews(){
		return this;
	}
	//��ȡ�ؼ�ID
	public String getViewsID() {
		return v_strID;
	}
	//��ȡ�ؼ�����
	public String getViewsType() {
		return v_strType;
	}
	//��ȡ�ؼ���ͼ�����
	public int getViewsZIndex(){
		return v_iZIndex;
	}
	//��ȡ�ؼ��󶨱��ʽ
	public String getViewsExpression() {
		return v_strExpression;
	}
	//��ȡ�Ƿ����view��ʶ
	public boolean getNeedUpdateFlag(){
		return v_bNeedUpdateFlag;
	}

	
	//���ÿؼ���id
	public boolean setViewsID(String id){
		v_strID = id;
		return true;
	}
	//���ÿؼ���type
	public boolean setViewsType(String type){
		v_strType = type;
		return true;
	}
	//��ȡ�ؼ���ͼ�����
	public boolean setViewsZIndex(int n){
		v_iZIndex = n;
		return true;
	}
	//��ȡ�ؼ��󶨱��ʽ
	public boolean setViewsExpression(String strExpression) {
		v_strExpression = strExpression;
		return true;
	}
	//�����Ƿ����view��ʶ
	public boolean setNeedUpdateFlag(boolean b_flag){
		v_bNeedUpdateFlag = b_flag;
		return true;
	}
	//���¿ؼ���ֵ����     �����ַ���  �����Ƿ���ֵд��ɹ�
	public boolean  updataValue(String strValue) {			
	//	v_strContent = strValue;
		int num = 0;
		HashMap<Integer,List<Event>> allEvent_ht = NetDataModel.getAllEvent(); 	
		if(allEvent_ht == null){ 
			num = 0;
		}else{
			Iterator<Integer> equiptId_lst = allEvent_ht.keySet().iterator();
			while(equiptId_lst.hasNext()){
				int e_id = equiptId_lst.next();
				List<Event> itemEvent_lst = allEvent_ht.get(e_id);
				if(itemEvent_lst == null){ 
					num = num+0;
				}else{
					num = num+itemEvent_lst.size();
				}
			}
		}
		if(num==0){  //�޸澯״̬
			Flag = 0;
		}else{       //�и澯״̬
			Flag = 1;
		}		
		return false;       //�ؼ��ڲ��Լ�ˢ��
	}
	//handler
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1: 
				if(Ks_AllAlarmLight.this.isShown())
					doInvalidate();
				break;
			default: break;
			}
		}
		
	};
	//����ʵʱˢ���߳�
	private class myThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int i = 0;
			while(true){ //ʵʱˢ��
				try {
					if(Flag == 0){  //�޸澯״̬
						if(m_bitCurrentAlarmImage!=s_bitAlarmLevel1Image){
							m_bitCurrentAlarmImage = s_bitAlarmLevel1Image;					
							myHandler.sendEmptyMessage(1);
						}
						Thread.sleep(500);
					}else if(Flag==1){ //�и澯
						m_bitCurrentAlarmImage = s_bitAlarmLevel2Image;	
						myHandler.sendEmptyMessage(1);
						Thread.sleep(500);					
						m_bitCurrentAlarmImage = s_bitAlarmLevel3Image;
						myHandler.sendEmptyMessage(1);
						Thread.sleep(500);
					}
					i++;
					if(i>2*10){  //20��ѭ�� ��ˢ��һ��
						myHandler.sendEmptyMessage(1);
						i = 0;
					}
				}catch(InterruptedException e){					
					return;	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}		
	}
	//�ؼ����ֲ���setGravity
	public boolean setGravity(){
		return true;
	}
	//�����ؼ�����ز���   //Form �ؼ������������ؼ�������Щ��ͬ
	public boolean setProperties(String strName, String strValue, String path){
			 if ("ZIndex".equals(strName))
		       	 	v_iZIndex = Integer.parseInt(strValue);	       	  
		     else if ("Location".equals(strName)) {
			       	String[] arrStr = strValue.split(",");
			        v_iPosX = Integer.parseInt(arrStr[0]);
			       	v_iPosY = Integer.parseInt(arrStr[1]);
		      }
		      else if ("Size".equals(strName)) {
			       	String[] arrSize = strValue.split(",");
			        v_iWidth = Integer.parseInt(arrSize[0]);
			        v_iHeight = Integer.parseInt(arrSize[1]);
		      }
		     else if ("Alpha".equals(strName)) 
		       	 	v_fAlpha = Float.parseFloat(strValue);
		     else if ("RotateAngle".equals(strName)) 
		        	v_fRotateAngle = Float.parseFloat(strValue);
		     else if ("Content".equals(strName)) 
		        	v_strContent = strValue;
		     else if ("FontFamily".equals(strName))
		        	v_strFontFamily = strValue;
		     else if ("FontSize".equals(strName)) 	   
		        	v_fFontSize = Float.parseFloat(strValue);	    	
		     else if ("IsBold".equals(strName))
		       	 	v_bIsBold = Boolean.parseBoolean(strValue);
		     else if ("FontColor".equals(strName)) 
		        	v_iFontColor = Color.parseColor(strValue); 
		     else if ("BackColor".equals(strName)) 
		        	v_iBackgroundColor = Color.parseColor(strValue); 
		     else if ("BackImage".equals(strName)){
		    	 v_strImgPath = path;
		    	 v_strImage = strValue;
		     }
		     else if ("HorizontalContentAlignment".equals(strName))
		       	 	v_strHorizontalContentAlignment = strValue;
		     else if ("VerticalContentAlignment".equals(strName))
		       	 	v_strVerticalContentAlignment = strValue;
		     else if ("Expression".equals(strName))   
		       	 	v_strExpression = strValue;          //�������ݱ��ʽ
		     else if ("CmdExpression".equals(strName)) 
		        	v_strCmdExpression = strValue;      //����������ʽ
		     else if("ColorExpression".equals(strName))
		        	v_strColorExpression = strValue;    //������ɫ�仯���ʽ	
		     else if("ClickEvent".equals(strName))
		        	v_strClickEvent = strValue;         //����¼����ʽ
		     else if("Url".equals(strName))
		        	v_strUrl = strValue;                //��ҳ���ӱ��ʽ��ַ

			 return true;
		}
	@Override
	public boolean parseExpression(String str_bindExpression) {
		// TODO Auto-generated method stub
		return false;
	}	
}
