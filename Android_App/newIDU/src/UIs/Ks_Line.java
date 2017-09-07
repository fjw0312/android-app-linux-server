package UIs;



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

//ֱ��
//�Զ���ؼ�Line  ʹ�û��Ƶװ�drawText��ʽ
public class Ks_Line extends ViewGroup implements VObject{

	public Ks_Line(Context context) {
		super(context);
	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Line";           //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "";          //�ؼ��󶨱��ʽ
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
	//�ر����
	float m_fStartPointX = 0;
	float m_fStartPointY = 0;
	float m_fEndPointX = 400;
	float m_fEndPointY = 400;
	float width =1;
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��
	//��������
	float x_lenth,y_lenth;
	float x_start,y_start;
	float x_end,y_end;
		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //����viewgroup�ĵװ���ɫ 
		
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // ���û��ʵľ��Ч��     
		mPaint.setStrokeWidth(width);    //�����������
		//mPaint.setTextSize(v_fFontSize);
		mPaint.setColor(v_iFontColor);
		canvas.drawLine(x_start, y_start,x_end, y_end, mPaint); //дһ�������ַ�	����������� 30 50
//		Log.e("Ks_Line-dispatchDraw","into"); 	
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Line-onLayout","into"); 		
	}
/*	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		Log.e("Ks_Line-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return true;
	}
*/
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Line-doLayout","into");
		float g_x1 = 0,g_x2 = 0;  //layout �װ�rect ��2��
		float g_y1 = 0,g_y2 = 0;
	
		//��ȡ2���layout ����
		if( (m_fStartPointX<=m_fEndPointX)&&(m_fStartPointY<=m_fEndPointY) ){      //4�� ��
			x_lenth = m_fEndPointX-m_fStartPointX;
			y_lenth = m_fEndPointY - m_fStartPointY;
			g_x1 = m_fStartPointX;  g_y1 = m_fStartPointY;
			g_x2 = m_fEndPointX;  g_y2 = m_fEndPointY;			
			x_start = width; y_start = width;
			x_end = x_start + x_lenth; y_end = y_start + y_lenth;
			
		}else if( (m_fStartPointX<=m_fEndPointX)&&(m_fStartPointY>=m_fEndPointY) ){//2�� ��
			x_lenth = m_fEndPointX-m_fStartPointX;
			y_lenth = m_fStartPointY - m_fEndPointY; 
			g_x1 = m_fStartPointX ; g_y1 = m_fEndPointY;
			g_x2 = m_fEndPointX; g_y2 = m_fStartPointY;
			x_start = width; y_start = width + y_lenth;
			x_end = x_start + x_lenth; y_end = width;
			
		}else if((m_fStartPointX>=m_fEndPointX)&&(m_fStartPointY<=m_fEndPointY)){  //3�� ��
			x_lenth = m_fStartPointX-m_fEndPointX;
			y_lenth = m_fEndPointY - m_fStartPointY;
			g_x1 = m_fEndPointX ; g_y1 = m_fStartPointY;
			g_x2 = m_fStartPointX; g_y2 = m_fEndPointY;
			x_start = width + x_lenth;  y_start = width;
			x_end = width; y_end = width + y_lenth;
			
		}else if((m_fStartPointX>=m_fEndPointX)&&(m_fStartPointY>=m_fEndPointY)){  //1�� ��
			x_lenth = m_fStartPointX-m_fEndPointX;
			y_lenth = m_fStartPointY - m_fEndPointY; 
			g_x1 = m_fEndPointX ; g_y1 = m_fEndPointY;
			g_x2 = m_fStartPointX; g_y2 = m_fStartPointY;
			x_start = width + x_lenth;  y_start = width + y_lenth;
			x_end = width; y_end = width;
		}
		this.layout((int)(g_x1-width), (int)(g_y1-width), (int)(g_x2+width), (int)(g_y2+width));	
//		Log.e("Ks_Line-doLayout","into"); 	
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
		
   	  m_fStartPointX = m_fStartPointX * window.w_screenPer;
   	  m_fStartPointY = m_fStartPointY * window.h_screenPer;
 	  m_fEndPointX = m_fEndPointX * window.w_screenPer;
 	  m_fEndPointY = m_fEndPointY * window.h_screenPer;
		
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

	//	v_strContent = strValue;
	
		return false;  //����Ҫ ���� �ؼ� 
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
		      else if ("StartPoint".equals(strName)) {
		        	 String[] arrStr = strValue.split(",");
		        	 m_fStartPointX = Float.parseFloat(arrStr[0]);
		        	 m_fStartPointY = Float.parseFloat(arrStr[1]);
		       }
		      else if ("EndPoint".equals(strName)) {
		        	 String[] arrStr = strValue.split(",");
		        	 m_fEndPointX = Float.parseFloat(arrStr[0]);
		        	 m_fEndPointY = Float.parseFloat(arrStr[1]);
		      }
			 else if ("Width".equals(strName)) 
				 	width = Float.parseFloat(strValue);
		     else if ("Color".equals(strName)) 
		        	v_iFontColor = Color.parseColor(strValue); 
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
		return false;
	}
	
}
