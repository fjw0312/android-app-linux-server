package UIs;

import java.util.List;

import utils.BindExpression;
import utils.Expression;
import utils.RealTimeValue;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//��ͼ�ؼ�        //Ŀǰ�ÿؼ������ ֻ֧�ֵ����
public class Ks_PieChart extends ViewGroup implements VObject{

	public Ks_PieChart(Context context) {
		super(context);
		//ʵ�����ÿؼ������Ԫ�ؿؼ�

		//��Ԫ����ӵ���������

	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "PieChart";        //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "Binding{[Value[Equip:114-Temp:173-Signal:1]]}"; //�ؼ��󶨱��ʽ
    //��֧�ְ��źŸ澯�ȼ�Binding{[EventSeverity[Equip:2-Temp:175-Signal:1]]}
	int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
	String v_strContent = "";        //�ؼ��ַ�����
	String v_strFontFamily = "΢���ź�";      //�ؼ���������
	boolean v_bIsBold = false;               //�ؼ������Ƿ�Ӵ�
	String v_strHorizontalContentAlignment = "Center"; //�ؼ����ݵĺ���װ���䷽ʽ
	String v_strVerticalContentAlignment = "Center";  //�ؼ����ݵ�����װ���䷽ʽ
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //������ɫ�仯���ʽ
	String v_strCmdExpression = "";             //�ؼ�����������ʽ
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "��ҳ.xml";           //�ؼ�����¼���ת����
	
	int v_iRadius = 0;   //Բ�ǰ뾶
	int v_iFillColor=0x00000000; //�����ڲ������ɫ
	int v_iBorderColor =0xFF000000; //����߿���ɫ
	int v_iBorderWidth = 1;  //�߿�������С
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��
	
	//��������
	String old_string = "";
	String str_value = "";
	int[]  m_DataColor= new int[50];

	//��������
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���
	Expression expression = null; //���ʽ������
	
	int  v_iStartFontColor = 0xFF008000;         //�ؼ���������ɫ

		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //����viewgroup�ĵװ���ɫ 
		
		//��������Բ
		Paint mPaint = new Paint();
		mPaint.setColor(v_iBackgroundColor); 
		mPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
		mPaint.setStrokeWidth(1);
		mPaint.setStyle(Paint.Style.FILL);
		RectF rect = new RectF(5, 5, v_iWidth-5, v_iHeight-5); //�������ʾԲ��
		canvas.drawArc(rect, //������ʹ�õľ��������С   
				            0,  //��ʼ�Ƕ�   
				            360, //ɨ���ĽǶ�   
				            true, //�Ƿ�ʹ������   
				            mPaint); 	
		
		  //��������������
		   int i = 0;
		   float angle = 0;
		   String value = v_strContent; //�������
		   if("".equals(value)) return;
		   float data = Float.parseFloat(value); 
		   Log.e("Ks_PieChart>>dispatchDraw", String.valueOf(data));
		   float data1 = data/100*360;
		   mPaint.setColor(v_iFontColor); 	//���û�����ɫ
		   mPaint.setStyle(Paint.Style.FILL);
		  canvas.drawArc(rect, //������ʹ�õľ��������С   
		    			angle,  //��ʼ�Ƕ�   
		    			data1, //ɨ���ĽǶ�   
		 		        true, //�Ƿ�ʹ������   
		 		       mPaint);  
		  mPaint.setStrokeWidth(5);
		  mPaint.setStyle(Paint.Style.STROKE);
		  mPaint.setColor(v_iFontColor); 	//���û�����ɫ
		  canvas.drawArc(rect, //������ʹ�õľ��������С   
		    			angle,  //��ʼ�Ƕ�   
		    			data1, //ɨ���ĽǶ�   
		 		        false, //�Ƿ�ʹ������   
		 		       mPaint); 
		
//		Log.e("Ks_PieChart>>dispatchDraw","into"); 
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_PieChart>>onLayout","into"); 		
		
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_PieChart-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_PieChart>>doLayout","into"); 	
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
		v_bNeedUpdateFlag = false;
		if(bindExpression==null) return false; 
		//Label�ؼ� ֻ���ǵ� �󶨱��ʽ  ��ֵ  �� ֱ�ӻ�ȡget(0);
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
		RealTimeValue realTimeValue = new RealTimeValue();	
		String newValue = realTimeValue.getRealTimeValue(expression_lst);
		if("".equals(newValue)) newValue = "0.0"; //��ʾδ��ȡ����Ч����
		if(v_strContent.equals(newValue) ) return false; //��ֵδ�ı� ������	
		v_strContent = newValue;	
		realTimeValue = null;
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
		     else if ("Radius".equals(strName))
		    	 v_iRadius = Integer.parseInt(strValue);
		     else if ("BorderColor".equals(strName)) 
		        	v_iBorderColor = Color.parseColor(strValue); 
		     else if ("BorderWidth".equals(strName)) 
		        	v_iBorderWidth = Integer.parseInt(strValue); 
		     else if ("FillColor".equals(strName)){
		    	 v_iStartFontColor = Color.parseColor(strValue); 
		    	 v_iFillColor = v_iStartFontColor;
		     }
		     else if ("ForeColor".equals(strName)) 
		        	v_iFontColor = Color.parseColor(strValue); 
		     else if ("BackgroundColor".equals(strName)) 
		        	v_iBackgroundColor = Color.parseColor(strValue); 
			 else if ("DataColor".equals(strName)) {
			        String strData[] = strValue.split("\\+|\\|");  
			        for(int i=0;i<strData.length;i++){
			        	m_DataColor[i] = Color.parseColor(strData[i]);
			        }
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
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		return true;
	}
	
}
