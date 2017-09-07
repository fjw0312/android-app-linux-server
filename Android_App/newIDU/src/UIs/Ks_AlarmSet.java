package UIs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import utils.BindExpression;
import utils.Expression;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//�澯��ֵ ���� �ؼ�
//made by fjw0312
public class Ks_AlarmSet extends ViewGroup implements VObject{

	public Ks_AlarmSet(Context context) {
		super(context);
		
		tigger = new Tigger();
		//ʵ�����ÿؼ������Ԫ�ؿؼ�	
		//ʵ�����ÿؼ������Ԫ�ؿؼ�	
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		button = new Button(context);
		button.setText("����");
		button.setGravity(Gravity.CENTER); //Ŀǰ��������λ��ƫ�� debug
		button.setOnClickListener(l); 
		textview = new TextView(context);
		editview = new EditText(context);
		editview.setBackground(null); //ʹedittext���»�����ʧ   
		editview.setSingleLine();  //����Ϊ���������ʽ
		editview.setGravity(Gravity.CENTER); //Ŀǰ��������λ��ƫ�� debug
//		editview.setCursorVisible(false);
//		editview.setOnClickListener(l);
		editview.setInputType(EditorInfo.TYPE_CLASS_PHONE); 
		imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	
		editview.setOnFocusChangeListener(new OnFocusChangeListener() {			
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						if(arg1){
							f_color = f_color2;
							is_ant = true;
							b_runThread = true;
							editview.setText("");
							doInvalidate();
							imm.showSoftInput(editview, InputMethodManager.SHOW_FORCED);
							editview.setFocusable(true);
							editview.setFocusableInTouchMode(true);

							myThread thread = new myThread();
							thread.start(); 
						
							Log.e("Ks_AlarmSet>>onFocusChange-oooo>>>","�۽��ı���롷����");
						}else{
							f_color = f_color1;
							is_ant = false;
							b_runThread = false;
							
							doInvalidate();
							Log.e("Ks_AlarmSet>>onFocusChange-oooo>>>","�۽��ı��˳�������");
						}
					}
		});
		
		addView(editview);
		addView(button);
		addView(textview);
	}
	private Handler myHandler= new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1: 
				textview.setText(editview.getText().toString());
		//		Log.e("Ks_AlarmSet>>myHandler>>>editview.getText()",editview.getText().toString());
				break;
			default: break;
			}
		}
		
	};
	private class myThread extends Thread{	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try{
//					Log.e("Ks_AlarmSet>>myThread>>>","thread-run");
					Thread.sleep(400);
					myHandler.sendEmptyMessage(1);
					if(b_runThread == false) break;				
				}catch(Exception e){				
				}
			}
		}
	}
   private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if(arg0==editview){	
//						editview.setFocusableInTouchMode(true); //ʹ�ܹ�� ���Զ��������
//						imm.showSoftInput(editview, InputMethodManager.SHOW_FORCED);//�������
			}else if(arg0==button){					
				imm.hideSoftInputFromWindow(m_MainWindow.getWindowToken(), 0);				
				editview.clearFocus();								
				textview.setText(editview.getText().toString());
				//���Ϳ�������
				if(TriggerExpression != null){					
					if( (tTiggerConditions_ht != null) &&(tTiggerConditions_ht.size()!=0) ){
						try{
							tigger.startvalue = Float.parseFloat( editview.getText().toString() );
				//			tigger.eventseverity = Integer.parseInt(tTiggerConditions_ht.get(String.value(tigger.conditionid).EventSeverity));						
							tigger.mark = 1;
							AddThread thread = new AddThread();
							thread.start();
					//		NetDataModel.addTigger(tigger);
							Toast.makeText(m_MainWindow.getContext(), "�澯��ֵ�ɹ���", Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							Log.e("Ks_AlarmSet>>onClick>>","�澯��ֵ ����ʧ��  �쳣�׳���");
						}
					}
				}
				
			}	
			doInvalidate();
		}
	};
	
	private class AddThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			NetDataModel.addTigger(tigger);
		}
		
	}
	
	//Fields
		String v_strID = "";                 //�ؼ�id
		String v_strType = "AlarmSet";           //�ؼ�����
		int v_iZIndex = 1;                    //�ؼ�ͼ��
		String v_strExpression = "Binding{[Trigger[Equip:2-Temp:175-Event:1-Condition:1]]}";          //�ؼ��󶨱��ʽ
		int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
		int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
		int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
		float v_fAlpha = 1.0f;                 //�ؼ���λ
		float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
		float v_fFontSize = 12.0f;              //���� ��С
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
		
		float v_fButtonWidthPer = (float)0.4;
		
		boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
		Page m_MainWindow = null;         //��ҳ����
		//����ؼ�ʹ�õ�Ԫ��
		EditText editview;
		Button button;
		TextView textview;
		//��������
		InputMethodManager imm;  //������̹���������
		int editWidth;  //edittext�Ŀ��
		BindExpression TriggerbindExpression = null;  //���ư󶨴�����
		int TriggerbindExpressionItem_num = 0;     //���ư����� �ĸ���      ֻ�������
		Expression TriggerExpression = null;     //���Ʊ��ʽ������
		Tigger tigger = null;
		xml_eventCfg tiggerCfg = null;
		HashMap<String, EventCondition> tTiggerConditions_ht = null;
		boolean getConditionFlag = true;

		int f_color1 = 0xDDDCDCDC;
		int f_color2 = 0xFFE1A222;
		int f_color = 0xDDDCDCDC;
		boolean is_ant = false;
		boolean b_runThread = false; //û�취ˢ�� �������� ֻ�ܲ����̲߳���ˢ��ʾ��
		
		//��дdispatchDraw() ����������view ��������drawChild()����	
		protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
		{		
			super.dispatchDraw(canvas);		
		//	canvas.drawColor(Color.WHITE);   //����viewgroup�ĵװ���ɫ 
//			Log.e("Ks_AlarmSet>>doLayout","into");
			float fontSize = v_iHeight*(float)0.4/MainActivity.densityPer;
			textview.setTextSize(fontSize);
			textview.setTextColor(Color.RED);
			textview.setGravity(Gravity.CENTER);			
			textview.setPadding(0, (int)fontSize/2, 0, 0);
			editview.setTextSize(fontSize);
			editview.setPadding(0, 0, 0, 0);
			
			button.setTextSize(fontSize);
			button.setPadding(0, (int)v_iHeight/2-(int)(fontSize*MainActivity.densityPer), 0, 0); //��̬����button ���С̫С�ἷ������ ���޸�����Ԫ��λ�� 
			
			Paint mPaint = new Paint();
			mPaint.setStrokeWidth(1);    //�����������
			mPaint.setStyle(Paint.Style.FILL); 
			mPaint.setColor(Color.WHITE);
			canvas.drawRect(6, 6, editWidth-6, v_iHeight-6, mPaint); 
			mPaint.setStrokeWidth(1);    //�����������
			mPaint.setStyle(Paint.Style.STROKE); 
			mPaint.setColor(f_color);
			mPaint.setAntiAlias(is_ant);

			canvas.drawRect(3, 3, editWidth-3, v_iHeight-3, mPaint);
			//shader ֻ�ܵõ�����ɫ��Ч��		
//			Shader mShader = new LinearGradient(0, 0, 2, 4, 0xDD000000, 0x00000000, TileMode.CLAMP);
//			mPaint.setShader(mShader);
//			mPaint.setShadowLayer(5, 2, 2, Color.RED); //ʵ�ʸ���ӰЧ����Ч
			drawChild(canvas, button, getDrawingTime());
			drawChild(canvas, editview, getDrawingTime());
			drawChild(canvas, textview, getDrawingTime());
		}
		//��дonLayout() ����viewGroup�����е�view�װ�layout 
		protected void onLayout(boolean bool, int l, int t, int r, int b) {
//			Log.e("Ks_AlarmSet>>onLayout","into");	
			editWidth = (int)(v_iWidth*((float)1-v_fButtonWidthPer));
			editview.layout(0, 0, editWidth, v_iHeight);  //�����Ը����淶		
			button.layout(editWidth, 0, v_iWidth, v_iHeight-2);  //�����Ը����淶	
			textview.layout(0, 0, editWidth, v_iHeight);	
		}
		//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
		public boolean onTouchEvent(MotionEvent event){
			super.onTouchEvent(event);
//			Log.e("Ks_AlarmSet-onTouchEvent","into");		 
			//invalidate();   //֪ͨ��ǰview �ػ����Լ�
			return true;
		}
		//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
		public void doLayout(boolean bool, int l, int t, int r, int b){
//			Log.e("Ks_AlarmSet>>doLayout","into");
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
			
			m_MainWindow = window;
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
		
			if(getConditionFlag == false) return false;
			try{
				tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);
				if("true".equals(tiggerCfg.Enable)){
					tigger.enabled = 1;
				}else{
					tigger.enabled = 0;
				}
				
				tigger.enabled = 1;
				tTiggerConditions_ht = tiggerCfg.EventConditionlst;

				getConditionFlag = false; //��ȡ�� ���� ����  ���� ��ȡ����־����  ʹ���ڸ��»�ȡ
				return true;
				
			}catch(Exception e){
			//	Log.i("Ks_AlarmSet>>updataValue>>","δ��ȡ�� SCmdCfg �쳣�׳���");		
			}	
			return false;
		}
		//�ؼ����ֲ���setGravity
		public boolean setGravity(){
			return true;
		}
		//�����ؼ�����ز���
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
				 else if ("ButtonWidthRate".equals(strName)) 
				        	v_fButtonWidthPer = Float.parseFloat(strValue);
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
			     else if ("BackgroundColor".equals(strName)) 
			        	v_iBackgroundColor = Color.parseColor(strValue); 
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
			if("".equals(v_strExpression)==false){
				TriggerbindExpression = new BindExpression();
				TriggerbindExpressionItem_num = TriggerbindExpression.getBindExpression_ItemLst(v_strExpression);	
				if(TriggerbindExpression.itemBindExpression_lst != null){
					String str_bindItem = TriggerbindExpression.itemBindExpression_lst.get(0); //����
					List<Expression> expression_lst = TriggerbindExpression.itemExpression_ht.get(str_bindItem);
					TriggerExpression = expression_lst.get(0); //������
					//��ȡ �������� ���ݳ�Աitem							
					tigger.equipId = TriggerExpression.equip_ExId;
					tigger.tiggerId = TriggerExpression.event_ExId;
					tigger.conditionid = TriggerExpression.condition_ExId;

					//Log.i("Ks_AlarmSet>>parseExpression>>�ؼ�id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
					return true;
				}
				
			}
			return false;
		}
}
