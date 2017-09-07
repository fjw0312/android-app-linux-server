package UIs;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import utils.BindExpression;
import utils.Calculator;
import utils.Expression;
import utils.RealTimeValue;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//�澯��ֵ����  �ؼ�
//made by fang
public class Ks_AlarmEdgeLabel extends ViewGroup implements VObject{

	public Ks_AlarmEdgeLabel(Context context) {
		super(context);
		tigger = new Tigger();
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		textview = new TextView(context); 
		//��Ԫ����ӵ���������
		addView(textview);
	}
	//Fields
	String v_strID = "";                    //�ؼ�id
	String v_strType = "AlarmEdgeLabel";      //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "Binding{[Trigger[Equip:2-Temp:175-Event:1-Condition:1]]}";//�澯��ֵ���ʽ
	int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
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
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_Page = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��
	TextView textview;
	//��������
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���
	Expression expression = null; //���ʽ������
	int times = 0;
	BindExpression TriggerbindExpression = null;  //���ư󶨴�����
	int TriggerbindExpressionItem_num = 0;     //���ư����� �ĸ���      ֻ�������
	Expression TriggerExpression = null;     //���Ʊ��ʽ������
	Tigger tigger = null;
	HashMap<String, EventCondition> tTiggerConditions_ht = null;
	boolean getConditionFlag = true;
		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);	
//		Log.e("Ks_AlarmEdgeLabel>>dispatchDraw","into");  
//		canvas.drawColor(Color.LTGRAY);   //����viewgroup�ĵװ���ɫ 
		//�����ӿؼ�Ԫ�ز���
//		textview.getPaint().setAntiAlias(true);
		textview.setTextSize(v_fFontSize/MainActivity.densityPer);
		textview.setTextColor(v_iFontColor);
		textview.setText(v_strContent);
		textview.getPaint().setStrokeWidth(1);
		textview.getPaint().setFakeBoldText(v_bIsBold);	
//		textview.getPaint().setAntiAlias(true);
		//������view
		drawChild(canvas, textview, getDrawingTime());
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_AlarmEdgeLabel>>onLayout","into"); 		
		textview.layout(0, 0, v_iWidth, v_iHeight);  //�����Ը����淶 	
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_AlarmEdgeLabel>>onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_AlarmEdgeLabel>>doLayout","into");
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
	//���ÿؼ���ͼ�����
	public boolean setViewsZIndex(int n){
		v_iZIndex = n;
		return true;
	}
	//���ÿؼ��󶨱��ʽ
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
		if("".equals(v_strExpression)) return false;
		if(tigger == null) return false;
		
		try{
			xml_eventCfg tiggerCfg = NetDataModel.getEventCfg(tigger.equipId, tigger.tiggerId);	
			//TiggerCfg tiggerCfg = DataPoolModel.getTiggerCfg(tigger.equipId, tigger.tiggerId);
			//Log.e("Ks_AlarmEdgeLabel>>updataValue>>--f1",tiggerCfg.EventName);	
			if(tiggerCfg == null)  return false;
			int enabled = 1;
			if("true".equals(tiggerCfg.Enable)){
				tigger.enabled = 1;
			}else{
				tigger.enabled = 0;
			}
			tigger.enabled = enabled;
			tTiggerConditions_ht = tiggerCfg.EventConditionlst;
			String str_conditionid = String.valueOf(tigger.conditionid);
			if(tTiggerConditions_ht == null || tTiggerConditions_ht.get(str_conditionid)==null)  return false;
			String startvalue = tTiggerConditions_ht.get(str_conditionid).StartCompareValue;
			//String stopvalue = tTiggerConditions_ht.get(str_conditionid).EndCompareValue;
			//String eventseverity = tTiggerConditions_ht.get(str_conditionid).EventSeverity;
			
			tigger.startvalue = Float.parseFloat(startvalue);
			//tigger.stopvalue = Float.parseFloat(stopvalue);
			//tigger.eventseverity = Integer.parseInt(eventseverity); 
			
			
			//��ȡ �澯������ ֵ  
			String value = String.valueOf(tigger.startvalue);
			//Log.e("Ks_AlarmEdgeLabel>>updataValue>>--f4",startvalue);	
			if(v_strContent.equals(value)) return false;
			v_strContent = value;
			return true;
			
		}catch(Exception e){
			Log.e("Ks_AlarmEdgeLabel>>updataValue>>","δ��ȡ�� �澯��ֵ �쳣�׳���");		
		}	
		return false;
	}
	//����󶨱��ʽ
	public boolean parseExpression(String str_bindExpression){
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

				Log.i("Ks_YKParameter>>updataValue>>�ؼ�id"+v_strID, tigger.equipId+"  "+tigger.tiggerId);
				return true;
			}

		}
		
		return true;
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

}
