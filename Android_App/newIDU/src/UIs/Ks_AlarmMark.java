package UIs;

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
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

//���θ澯 �ؼ�
//made in kstar   Ŀǰ����bug �������澯����ʱ ����  ��ʹ�澯һֱ��ס���� ����������α仯��
public class Ks_AlarmMark extends ViewGroup implements VObject{

	public Ks_AlarmMark(Context context) { 
		super(context);
		tigger = new Tigger();
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		button = new Button(context);
		button.setOnClickListener(l); //Ϊbutton���ü�����
		//�����Ԫ�ؿؼ������ÿؼ�
		addView(button);
	}
	//����������
	private OnClickListener l = new OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 == button){ //�ж�Ϊ��button�ĵ���¼�
				//�жϵ���¼���Ӧ������
				String text = "";
				if(("".equals(v_strMaskExpression)==false)&&(TriggerExpression != null)){

					   if(markFlag){      //��һ�� �������
							tigger.enabled = 0;
							String startvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).StartCompareValue;
							tigger.startvalue = Float.parseFloat(startvalue);
							//tigger.eventseverity = 0;
							markFlag = false;
							text = "�澯����";
							litwith = 3;
							litColor = 0xB9DF0000;
							Log.e("button1-onClick",  text);
					   }else{            //��2�� ����澯
							tigger.enabled = 1;					
							String startvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).StartCompareValue;
							tigger.startvalue = Float.parseFloat(startvalue);							
							markFlag = true;
							text = "�澯ʹ��";
							litwith = 6;
							litColor = 0xB9009500;
							Log.e("button1-onClick",  text);
					   }
						AddThread thread = new AddThread();
						thread.start();
					 //  DataPoolModel.addTigger(tigger); 
					   Toast.makeText(m_Page.getContext(), "�澯�������ã�", Toast.LENGTH_SHORT).show();

					   v_strContent = text; 
				 
				}
				
				doInvalidate();
			}	
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
	String v_strType = "MarkAlarm";     //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "";          //�ؼ��󶨱��ʽ
	int v_iPosX = 0,v_iPosY = 0;       //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
	String v_strContent = "�澯ʹ��";        //�ؼ��ַ�����
	String v_strFontFamily = "΢���ź�";      //�ؼ���������
	boolean v_bIsBold = false;               //�ؼ������Ƿ�Ӵ�
	String v_strHorizontalContentAlignment = "Center"; //�ؼ����ݵĺ���װ���䷽ʽ
	String v_strVerticalContentAlignment = "Center";  //�ؼ����ݵ�����װ���䷽ʽ
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //������ɫ�仯���ʽ
	String v_strCmdExpression = "";             //�ؼ�����������ʽ
	String v_strYTCmdExpression = "";          //�ؼ�ң��������ʽ 
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "show(��ҳ)";           //�ؼ�����¼���ת����
	String v_strMaskExpression = "Binding{[Mask[Room:1-Equip:2-Event:1]]}"; //�¼����α��ʽ
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_Page = null;         //��ҳ����
	
	//����ؼ�ʹ�õ�Ԫ��
	Button button;
	
	//��������
	int litwith = 6;
	int litColor = 0xB9009500;
	int i = 0;
	BindExpression TriggerbindExpression = null;  //���ư󶨴�����
	int TriggerbindExpressionItem_num = 0;     //���ư����� �ĸ���      ֻ�������
	Expression TriggerExpression = null;     //���Ʊ��ʽ������
	Tigger tigger = null;
	HashMap<String, EventCondition> tTiggerConditions_ht  = new HashMap<String, EventCondition>();
	boolean getConditionFlag = true;
	boolean markFlag = true;
			
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{
//		Log.e("Ks_AlarmMark>>dispatchDraw","into");
		super.dispatchDraw(canvas);				    
//		canvas.drawColor(Color.YELLOW);   //����viewgroup�ĵװ���ɫ 
		button.setTextSize(v_fFontSize/MainActivity.densityPer);
		button.setTextColor(v_iFontColor);
		button.setText(v_strContent);
		button.getPaint().setFakeBoldText(v_bIsBold);
		
		//������view
		drawChild(canvas, button, getDrawingTime());
		Paint mPaint = new Paint();
		mPaint.setStrokeWidth(1);    //�����������
		mPaint.setStyle(Paint.Style.STROKE); 
		mPaint.setColor(litColor);
		canvas.drawRect(litwith, litwith, v_iWidth-litwith, v_iHeight-litwith, mPaint);
			
	}
	//��дonLayout() ����viewGroup�����е���view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_AlarmMark>>onLayout","into");
		//������view��layout
		button.layout(0, 0, v_iWidth, v_iHeight);  //�����Ը����淶
						
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("form-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_AlarmMark>>doLayout","into");
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
		
		m_Page = window;
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
			xml_eventCfg tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);			
			if(tiggerCfg==null) return false;
			tTiggerConditions_ht = tiggerCfg.EventConditionlst;		
			if( (tTiggerConditions_ht != null) &&(tTiggerConditions_ht.size()!=0) ){ 
					tigger.conditionid = 1;
					String startvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).StartCompareValue;
					String stopvalue = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).EndCompareValue;
					String eventseverity = tTiggerConditions_ht.get(String.valueOf(tigger.conditionid)).EventSeverity;
					if("".equals(startvalue)==false){
						tigger.startvalue = Float.parseFloat(startvalue);
					}
					if("".equals(stopvalue)==false){
						tigger.stopvalue = Float.parseFloat(stopvalue);
					}
					if("".equals(eventseverity)==false){
						tigger.eventseverity = Integer.parseInt(eventseverity);
					}
					tigger.mark = 3;
					getConditionFlag = false; //��ȡ�� ���� ����  ���� ��ȡ����־����  ʹ���ڸ��»�ȡ
					return true;
			}
		}catch(Exception e){
				Log.e("Ks_YKParameter>>updataValue>>","δ��ȡ�� tiggerCfg �쳣�׳���");
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
		     else if ("MaskExpression".equals(strName))   
		       	 	v_strMaskExpression = strValue;          //���α��ʽ
		     else if ("CmdExpression".equals(strName)) 
		        	v_strCmdExpression = strValue;      //����������ʽ
		     else if ("YTCmdExpression".equals(strName)) 
		        	v_strYTCmdExpression = strValue;      //ң��������ʽ
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
		if("".equals(v_strMaskExpression)==false){
			TriggerbindExpression = new BindExpression(); 
			TriggerbindExpressionItem_num = TriggerbindExpression.getBindExpression_ItemLst(v_strMaskExpression);			
			if( (TriggerbindExpression != null)&&(TriggerbindExpression.itemBindExpression_lst != null) ){
				String str_bindItem = TriggerbindExpression.itemBindExpression_lst.get(0); //����
				List<Expression> expression_lst = TriggerbindExpression.itemExpression_ht.get(str_bindItem);
				TriggerExpression = expression_lst.get(0); //������
				//��ȡ �������� ���ݳ�Աitem							
				tigger.equipId = TriggerExpression.equip_ExId;
				tigger.tiggerId = TriggerExpression.event_ExId;

				Log.e("Ks_AlarmMark>>updataValue>>�ؼ�id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
			}
			
		}	
		return false;
	}
}
