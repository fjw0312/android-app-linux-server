package UIs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import utils.BindExpression;
import utils.Expression;
import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.SCmd;
import SAM.DataPoolModel.Tigger;
import SAM.XmlCfg.xml_eventCfg;
import SAM.XmlCfg.xml_eventCfg.EventCondition;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//�Ӽ� ���� ��ť    //֧�ְ󶨸澯��ֵ ��  ��������
public class Ks_ButtonUpDown extends ViewGroup implements VObject{

	public Ks_ButtonUpDown(Context context) {
		super(context);
		
		tigger = new Tigger();
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		//ʵ�����ÿؼ������Ԫ�ؿؼ�	
		imageButton1 = new ImageButton(context);
		imageButton2 = new ImageButton(context);
		// load image
				try {
					AssetManager assetManager = this.getContext().getResources().getAssets();
					InputStream is = null;
					InputStream is2 = null;
					
					if (null == s_bitUpSet1Image)
					{
						is = assetManager.open("ui/Button2-UP.png");
						s_bitUpSet1Image = BitmapFactory.decodeStream(is);
						imageButton1.setImageBitmap(s_bitUpSet1Image);
						is.close();
					}
					if (null == s_bitDownSet2Image)
					{
						is2 = assetManager.open("ui/Button2-DOWN.png");
						s_bitDownSet2Image = BitmapFactory.decodeStream(is2);
						imageButton2.setImageBitmap(s_bitDownSet2Image);
						is2.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("Ks_ButtonUpDown>>>super","�쳣�׳���");
				}
//				imageButton1.setImageBitmap(s_bitUpSet1Image);
//				imageButton2.setImageBitmap(s_bitDownSet2Image);
				
				imageButton1.setOnClickListener(l);
				imageButton2.setOnClickListener(l);
		        
		        m_oPaint = new Paint();
		addView(imageButton1);
		addView(imageButton2);
	}
	//ͼƬ ����¼� ʵ����
		private OnClickListener l = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(arg0 == imageButton1)   addd = v_iStepValueFirst;
				if(arg0 == imageButton2)   addd = 0 - v_iStepValueFirst;
				//Log.e("Ks_ButtonUpDown>>onClick",String.valueOf(addd));
				MyThread thread = new MyThread(); //�����޸� ���� �ļ��߳�
				thread.start();	
			}
		};
		//���� �߳�
		private class MyThread extends Thread{
			public void run() {
				try{
					if(addd > 0){
						myhandler.sendEmptyMessage(2);
					}else{
						myhandler.sendEmptyMessage(3);
					}
					//���Ϳ�������
					if(expression != null){	
						if(tiggerOrCmd_mode == 1){   //����  �澯ֵ  
							if( (tTiggerConditions_ht != null) &&(tTiggerConditions_ht.size()!=0) ){
								try{
									String str_conditionid = String.valueOf(tigger.conditionid);
									String startvalue = tTiggerConditions_ht.get(str_conditionid).StartCompareValue;
									tigger.startvalue = Float.parseFloat(startvalue) + (float)addd;
									Log.e("Ks_ButtonUpDown>>MyThread",String.valueOf(tigger.startvalue));
									//tigger.stopvalue = tTiggerConditions_ht.get(str_conditionid).endcompare;
									//tigger.eventseverity = tTiggerConditions_ht.get(str_conditionid).severity;
									tigger.mark = 1;
									NetDataModel.addTigger(tigger);	

								}catch(Exception e){
									Log.e("Ks_ButtonUpDown>>MyThread>>","�澯��ֵ ����ʧ��  �쳣�׳���");
								}
							}
						}else if(tiggerOrCmd_mode == 2){  //���ÿ�������
							SCmd scmd = new SCmd();
							scmd.equipId = expression.equip_ExId;
							scmd.cmdId = expression.command_ExId;
							int  val = cmdStartValue + addd;
							if(val > 80000) val = 10;  //��ֵ�÷ⶥ����
							cmdStartValue = val;							
							scmd.value = String.valueOf(val);
							scmd.valueType = 1;
							NetDataModel.addSCmd(scmd);
						}
						
					}
					Thread.sleep(400);//800ms
					myhandler.sendEmptyMessage(1);
				}catch(Exception e){ 
					Log.e("Ks_ButtonUpDown>>MyThread"," �쳣�׳���");
			    }
			}
		};
		private Handler myhandler = new Handler(){
			public void handleMessage(Message msg){
	    		switch (msg.what){
	    		case 1:   //��ť����
	    			imageButton1.setScaleX((float)1.0);
	    			imageButton1.setScaleY((float)1.0);
	    			imageButton2.setScaleX((float)1.0);
	    			imageButton2.setScaleY((float)1.0);
					Toast.makeText(m_MainWindow.getContext(), "���óɹ�", Toast.LENGTH_SHORT).show();
	    			break;
	    		case 2:   //�Ӱ�ť  ����
	    			imageButton1.setScaleX((float)1.3);
	    			imageButton1.setScaleY((float)1.3);
	    			break;
	    		case 3:  //����ť  ����
	    			imageButton2.setScaleX((float)1.3);
	    			imageButton2.setScaleY((float)1.3);
	    			break;
	    		default:
	    			break;
			    }
			}
		};
	
	//Fields
		String v_strID = "";                 //�ؼ�id
		String v_strType = "ButtonUpDown";           //�ؼ�����
		int v_iZIndex = 1;                    //�ؼ�ͼ��
		String v_strExpression = "Binding{[Trigger[Equip:2-Temp:175-Event:1-Condition:1]]}";          //�ؼ��󶨱��ʽ
								//Binding{[Cmd[Equip:2-Temp:175-Command:1-Parameter:1]]}   & ��Content
		int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
		int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
		int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
		float v_fAlpha = 1.0f;                 //�ؼ���λ 
		float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
		float v_fFontSize = 12.0f;              //���� ��С
		int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
		String v_strContent = "10";        //�ؼ��ַ�����
		String v_strFontFamily = "΢���ź�";      //�ؼ���������
		boolean v_bIsBold = false;               //�ؼ������Ƿ�Ӵ�
		String v_strHorizontalContentAlignment = "Center"; //�ؼ����ݵĺ���װ���䷽ʽ
		String v_strVerticalContentAlignment = "Center";  //�ؼ����ݵ�����װ���䷽ʽ
		String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //������ɫ�仯���ʽ
		String v_strCmdExpression = "";             //�ؼ�����������ʽ
		String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
		String v_strClickEvent = "��ҳ.xml";           //�ؼ�����¼���ת����
		int v_iStepValueFirst = 1;     //����ֵ
		
		float v_fButtonWidthPer = (float)0.4;
		
		boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
		Page m_MainWindow = null;         //��ҳ����
		
		//����ؼ�ʹ�õ�Ԫ��
		//��������
		private Bitmap s_bitUpSet1Image = null; 
		private Bitmap s_bitDownSet2Image = null; 
		private ImageButton imageButton1;
		private ImageButton imageButton2;
		Paint m_oPaint = null;
		int addd = 1; //ÿ�ε����ť�Ĳ���ֵ
		int cmdStartValue = 10; //�������ó�ʼֵ Ĭ��10
		int tiggerOrCmd_mode = 0;  //�澯����  ���ǿ�������  ģʽ     1:tigger   2:cmd

		BindExpression bindExpression = null;  //���ư󶨴�����
		int bindExpressionItem_num = 0;     //���ư����� �ĸ���      ֻ�������
		Expression expression = null;     //���Ʊ��ʽ������
		
		
		Tigger tigger = null;
		xml_eventCfg tiggerCfg = null;
		HashMap<String, EventCondition> tTiggerConditions_ht  = null;
		boolean getCfgFlag = true;

		
		//��дdispatchDraw() ����������view ��������drawChild()����	
		protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
		{		
			super.dispatchDraw(canvas);		
		//	canvas.drawColor(Color.WHITE);   //����viewgroup�ĵװ���ɫ 
			drawChild(canvas, imageButton1, getDrawingTime());
			drawChild(canvas, imageButton2, getDrawingTime());

		}
		//��дonLayout() ����viewGroup�����е�view�װ�layout 
		protected void onLayout(boolean bool, int l, int t, int r, int b) {
//			Log.e("Ks_ButtonUpDown>>onLayout","into");	
			imageButton1.layout(5, 0, v_iWidth/2-5, v_iHeight);
			imageButton2.layout(v_iWidth/2+5, 0, v_iWidth-5, v_iHeight);

		}
		//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
		public boolean onTouchEvent(MotionEvent event){
			super.onTouchEvent(event);
//			Log.e("Ks_ButtonUpDown-onTouchEvent","into");		 
			//invalidate();   //֪ͨ��ǰview �ػ����Լ�
			return true;
		}
		//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
		public void doLayout(boolean bool, int l, int t, int r, int b){
//			Log.e("Ks_ButtonUpDown>>doLayout","into");
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
		
			if(tiggerOrCmd_mode == 2) return false;  //��������ʱ���� �ⲿˢ��
			if(getCfgFlag == false) return false;
			try{
				tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);			
				if(tiggerCfg==null) return false;
				if("true".equals(tiggerCfg.Enable)){
					tigger.enabled = 1;
				}else{
					tigger.enabled = 0;
				}
				tTiggerConditions_ht = tiggerCfg.EventConditionlst;
				//TiggerConditionCfg tiggerConditionCfg = tTiggerConditions_ht.get(tigger.conditionid);
				getCfgFlag = false; //��ȡ�� ���� ����  ���� ��ȡ����־����  ʹ���ڸ��»�ȡ
				return true;
				
			}catch(Exception e){
			//	Log.i("Ks_ButtonUpDown>>updataValue>>","δ��ȡ�� SCmdCfg �쳣�׳���");		
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
			     else if ("Content".equals(strName)){ 
			        	v_strContent = strValue;
			        	if("".equals(v_strContent)==false){
			        		try{
			        		cmdStartValue = Integer.parseInt(v_strContent);
			        		}catch(Exception e){
			        			
			        		}
			        	}
			     }
			     else if ("StepValueFirst".equals(strName)){
			    	 if("".equals(strValue)==false)
			        	 v_iStepValueFirst = Integer.parseInt(strValue);
			     }
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
				bindExpression = new BindExpression();
				bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);	
				if(bindExpression.itemBindExpression_lst != null){
					String str_bindItem = bindExpression.itemBindExpression_lst.get(0); //����
					List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
					expression = expression_lst.get(0); //������
					//�ж� ʱ�ĸ����͵İ�
					if("Trigger".equals(expression.type)){
						//��ȡ �������� ���ݳ�Աitem		
						tiggerOrCmd_mode = 1;
						tigger.equipId = expression.equip_ExId;
						tigger.tiggerId = expression.event_ExId;
						tigger.conditionid = expression.condition_ExId;
					}else if("Cmd".equals(expression.type)){
						tiggerOrCmd_mode = 2;
					}
					
				//	Log.i("Ks_ButtonUpDown>>updataValue>>�ؼ�id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
					return true;
				}
				
			}
			return false;
		}
}
