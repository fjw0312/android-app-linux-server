package UIs;


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

//�Զ���ؼ�Table  ʹ��new draw canvas��ʽ
public class Ks_Table extends ViewGroup implements VObject{

	public Ks_Table(Context context) {
		super(context);
		//ʵ�����ÿؼ������Ԫ�ؿؼ�

		//��Ԫ����ӵ���������

	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Table";           //�ؼ�����
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
	
	int v_iRadius = 0;   //Բ�ǰ뾶
	int v_iRowNum = 1;  //����
	int v_iColNum = 1;  //���� 
	boolean v_bIsHasHead = false;   //�Ƿ��б�ͷ
	float v_fHeadPer = (float)0.3;   //���ͷ�ı�����С
	float v_fFirstRowPer = (float)0.3;  //��һ�еı�����С
	float v_fFirstColPer = (float)0.3;  //��һ�еı�����С
	int v_iHeadBackgroundColor = 0x00000000;    //��ͷ��ɫ
	int v_iFirstRowBackgroundColor = 0x00000000; //��һ����ɫ
	int v_iFirstColBackgroundColor = 0x00000000; //��һ����ɫ
	int v_iTableBackgroundColor = 0x00000000; //����м�������ɫ
	int v_iLineThickness =3; //������С
	int v_iLineColor = 0xFF000000;  //������ɫ
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��

	//��������

		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //����viewgroup�ĵװ���ɫ 
		
		if(v_bIsHasHead==false){
			v_iFirstRowBackgroundColor=v_iHeadBackgroundColor;
		}else{
			v_iFirstRowBackgroundColor = v_iTableBackgroundColor;
		}
		
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // ���û��ʵľ��Ч��     
		mPaint.setStrokeWidth(v_iLineThickness);    //�����������
		RectF rectf = new RectF();
		rectf.left = 0+v_iLineThickness/2;
		rectf.top = 0+v_iLineThickness/2;
		rectf.right = v_iWidth-v_iLineThickness/2;
		rectf.bottom = v_iHeight-v_iLineThickness/2;
		
		//�����Ƿ��б�ͷ ��ͷ�������һ�б���
		if(v_bIsHasHead){
			v_fHeadPer = v_fFirstRowPer;
			v_fFirstRowPer = 0;
			v_iRowNum++;
		}
		
		//���Ʊ�ͷ
		RectF rectf1 = new RectF();
		rectf1.left = rectf.left;
		rectf1.top = rectf.top;
		rectf1.right = rectf.left;
		rectf1.bottom = rectf.top;
		if(v_bIsHasHead){			
			rectf1.left = rectf.left;
			rectf1.top = rectf.top;
			rectf1.right = rectf.right;
			rectf1.bottom = rectf.left+v_iHeight*v_fHeadPer;
			mPaint.setColor(v_iHeadBackgroundColor);
			mPaint.setStyle(Paint.Style.FILL);  
			canvas.drawRoundRect(rectf1, v_iRadius, v_iRadius, mPaint);
			mPaint.setColor(v_iLineColor);
			mPaint.setStyle(Paint.Style.STROKE);  
			canvas.drawRoundRect(rectf1, v_iRadius, v_iRadius, mPaint);
		}
		//���Ʊ�ĵ�һ�� �װ���ɫ
		RectF rectf2 = new RectF();
		rectf2.left = rectf.left;
		rectf2.top = rectf1.bottom;
		rectf2.right = rectf.right; 
		rectf2.bottom = rectf1.bottom + v_iHeight*v_fFirstRowPer;
		mPaint.setColor(v_iFirstRowBackgroundColor);
		mPaint.setStyle(Paint.Style.FILL);  
		canvas.drawRoundRect(rectf2, v_iRadius, v_iRadius, mPaint);
 
		
		//���Ʊ�ĵ�һ�� �װ���ɫ
		RectF rectf3 = new RectF();
		rectf3.left = rectf.left;
		rectf3.top = rectf1.bottom;
		rectf3.right = rectf.left + v_iWidth * v_fFirstColPer;
		rectf3.bottom = rectf.bottom ;
		mPaint.setColor(v_iFirstColBackgroundColor);
		mPaint.setStyle(Paint.Style.FILL);  
		canvas.drawRoundRect(rectf3, v_iRadius, v_iRadius, mPaint);

		//���Ʊ���м� �װ���ɫ
		RectF rectf4 = new RectF();
		rectf4.left = rectf3.right;
		rectf4.top = rectf2.bottom;
		rectf4.right = rectf.right;
		rectf4.bottom = rectf.bottom;
		mPaint.setColor(v_iTableBackgroundColor);
//		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL);  
		canvas.drawRoundRect(rectf4, v_iRadius, v_iRadius, mPaint); 
		
		//���Ʊ���м�����  ����
		mPaint.setColor(v_iLineColor);
		float y_per = (rectf.bottom-rectf2.bottom)/(v_iRowNum-1);  
		for(int i=0;i<v_iRowNum-1;i++){
			canvas.drawLine(rectf.left, rectf2.bottom+y_per*i, rectf.right, rectf2.bottom+y_per*i, mPaint);
		}
		//���Ʊ���м�����  ����
		float x_per = (rectf.right-rectf3.right)/(v_iColNum-1); 
		for(int i=0;i<v_iColNum-1;i++){
			canvas.drawLine(rectf3.right+x_per*i, rectf2.top, rectf3.right+x_per*i, rectf.bottom, mPaint);
		}
   
		//���� ��� �߿�
		mPaint.setColor(v_iLineColor);
		mPaint.setStyle(Paint.Style.STROKE); 
		canvas.drawRoundRect(rectf2, v_iRadius, v_iRadius, mPaint);//���Ƶ�һ�б߿�
		canvas.drawRoundRect(rectf3, v_iRadius, v_iRadius, mPaint);//���Ƶ�һ�б߿�
		canvas.drawRoundRect(rectf, v_iRadius, v_iRadius, mPaint);//������߿�
//		Log.e("ks_Table>>onDraw","into"); 
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("ks_Table>>onLayout","into"); 		
		
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_Table-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("ks_Table>>doLayout","into"); 
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

	//	v_strContent = strValue;
	
		return false;   //��ˢ�� �ؼ�
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
			 
			  else if ("RowNum".equals(strName)) 
			       	 	v_iRowNum = Integer.parseInt(strValue);
			  else if ("ColNum".equals(strName)) 
		       	 		v_iColNum = Integer.parseInt(strValue);
			  else if ("IsHasHead".equals(strName)){
				  if("True".equals(strValue)){
					  v_bIsHasHead = true; 
				  }
				  else  v_bIsHasHead = false;
			  }
			  else if ("HeadPer".equals(strName)) 
	       	 			v_fHeadPer = Float.parseFloat(strValue);
			  else if ("FirstRowRatio".equals(strName)) 
	       	 			v_fFirstRowPer = Float.parseFloat(strValue);
			  else if ("FirstColRatio".equals(strName)) 
				  		v_fFirstColPer = Float.parseFloat(strValue);
			  else if ("HeadBackgroundColor".equals(strName)) 
				  		v_iHeadBackgroundColor = Color.parseColor(strValue); 
			  else if ("FirstRowBackgroundColor".equals(strName)) 
				  		v_iFirstRowBackgroundColor = Color.parseColor(strValue); 
			  else if ("FirstColBackgroundColor".equals(strName)) 
			  		v_iFirstColBackgroundColor = Color.parseColor(strValue); 
			  else if ("TableBackgroundColor".equals(strName)) 
			  		v_iTableBackgroundColor = Color.parseColor(strValue); 
			  else if ("LineThickness".equals(strName)) 
				  v_iLineThickness = Integer.parseInt(strValue);
			  else if ("LineColor".equals(strName)) 
			  		v_iLineColor = Color.parseColor(strValue); 
			 
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
