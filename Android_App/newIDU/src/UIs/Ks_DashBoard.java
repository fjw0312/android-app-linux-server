package UIs;

import java.text.DecimalFormat;
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
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//�Ǳ���        //Ŀǰ�ÿؼ������ ֻ֧�ֵ����
public class Ks_DashBoard extends ViewGroup implements VObject{

	public Ks_DashBoard(Context context) {
		super(context);
		//ʵ�����ÿؼ������Ԫ�ؿؼ�

		//��Ԫ����ӵ���������
		
        m_oPaint = new Paint();
        m_rRectF1 = new RectF();
        m_rRectF2 = new RectF();
        m_rRectF3 = new RectF();

	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "DashBoard";        //�ؼ�����
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
	float maxValue = 100;  //���̵����ֵ
	float minValue = 0;  //���̵���Сֵ
	int scale = 24;      //���̵Ŀ̶�
	int mode = 1;        //������ʽ
	float data_value = 10; //Ŀǰ�ı�����ֵ
	int m_nBorderWidth = 2;  //�������
	int m_nfillWidth = 30;  //����Բ�����
	String str_value="";
	float warnPer1 = (float)0.333;  //�澯Բ����ֵ��ʼ����
	float warnPer2 = (float)0.334;  //�澯Բ����ֵ��ʼ����
	int warnPerColor1 = 0xE54ACA4F;   //�澯Բ����ɫ
	int warnPerColor2 = 0xDFE9E852;   //�澯Բ����ɫ
	int warnPerColor3 = 0xE1E8373A;   //�澯Բ����ɫ
	Paint m_oPaint = null;  
	RectF m_rRectF1 = null;
	RectF m_rRectF2 = null;
	RectF m_rRectF3 = null;
	int m_cFillColor = 0xFFF2C0FF;
	int m_cLineColor = 0xFF5EC5EE;

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
		drawPanl(canvas);
//		Log.e("Ks_DashBoard>>dispatchDraw","into"); 
	}
	//���� ����
	private void drawPanl(Canvas canvas){
		float angle = 270/(maxValue-minValue) * (data_value-minValue);
		int pad = m_nBorderWidth/2+4;  //��Բ�߾�
		//���� ��������
				m_rRectF1.left = pad+m_nfillWidth/2;
				m_rRectF1.top = pad+m_nfillWidth/2;
				if(v_iWidth<v_iHeight){    //��С��һ�߳���   ��֤ΪԲ������
					m_rRectF1.right = v_iWidth-pad-m_nfillWidth/2;;
					m_rRectF1.bottom = v_iWidth-pad-m_nfillWidth/2;
				}else{
					m_rRectF1.right = v_iHeight-pad-m_nfillWidth/2;
					m_rRectF1.bottom = v_iHeight-pad-m_nfillWidth/2;
				}
				float bb = m_rRectF1.right;
				 
					//������Բ��
					m_rRectF2.left = m_rRectF1.left-m_nfillWidth/2;
					m_rRectF2.top = m_rRectF1.top-m_nfillWidth/2;
					m_rRectF2.right = m_rRectF1.right+m_nfillWidth/2;
					m_rRectF2.bottom = m_rRectF1.bottom+m_nfillWidth/2;
					m_oPaint.setColor(0x88000000);
					m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
					m_oPaint.setStrokeWidth(m_nBorderWidth);
					m_oPaint.setStyle(Paint.Style.STROKE);
			        canvas.drawOval(m_rRectF2, m_oPaint);  
			        
					//�����ڶ�Բ��   ��� ��m_rRectF1.right/8 ����Բ����
					m_rRectF2.left = m_rRectF2.left + bb/(float)16;
					m_rRectF2.top = m_rRectF2.top + bb/(float)16;
					m_rRectF2.right = m_rRectF2.right - bb/(float)16;
					m_rRectF2.bottom = m_rRectF2.bottom - bb/(float)16;
					m_oPaint.setColor(m_nBorderWidth);
					m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
					m_oPaint.setStrokeWidth(m_nBorderWidth);
					m_oPaint.setStyle(Paint.Style.STROKE);
			        canvas.drawOval(m_rRectF2, m_oPaint); 
			        
					//������Բ��   ��� ��m_rRectF1.right/8 ����Բ����
			        RectF m_rRectF_b = new RectF();
			        m_rRectF_b.left = m_rRectF2.left - bb/(float)32;
			        m_rRectF_b.top = m_rRectF2.top - bb/(float)32;
			        m_rRectF_b.right = m_rRectF2.right + bb/(float)32;
			        m_rRectF_b.bottom = m_rRectF2.bottom + bb/(float)32;
					m_oPaint.setColor(0xCEE5E2E2);
					m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
					m_oPaint.setStrokeWidth(m_rRectF1.right/(float)16);
					m_oPaint.setStyle(Paint.Style.STROKE);
			        canvas.drawOval(m_rRectF_b, m_oPaint);
			        
					//�����м� ���� Բ
			        float bfbf = 4;
					m_rRectF3.left = m_rRectF2.left+m_nBorderWidth/2 +bfbf;
					m_rRectF3.top = m_rRectF2.top+m_nBorderWidth/2 +bfbf;
					m_rRectF3.right = m_rRectF2.right-m_nBorderWidth/2 -bfbf;
					m_rRectF3.bottom = m_rRectF2.bottom-m_nBorderWidth/2 -bfbf;
					m_oPaint.setColor(0xFFFBFBFB);
			//		m_oPaint.setColor(m_cBorderColor); // ����䱳��ɫ
					m_oPaint.setStyle(Paint.Style.FILL);   
					canvas.drawOval(m_rRectF3, m_oPaint);	
			      			
						 //�����澯Բ��
						int hh = 10;  //Բ��������� 
						RectF m_rRectF_h = new RectF();
						m_rRectF_h.left = m_rRectF3.left+hh/2; 
						m_rRectF_h.top = m_rRectF3.top+hh/2; 
						m_rRectF_h.right = m_rRectF3.right-hh/2;
						m_rRectF_h.bottom = m_rRectF3.bottom-hh/2;
						
						
						m_oPaint.setStrokeWidth(hh);
						m_oPaint.setStyle(Paint.Style.STROKE); 
						
						m_oPaint.setColor(warnPerColor1); // ����䵥ɫ 
//						canvas.drawOval(m_rRectF3, m_oPaint); 			
						float angle_x1 = 270*warnPer1;
						 canvas.drawArc(m_rRectF_h, //������ʹ�õľ��������С   
								 	135,  //��ʼ�Ƕ�   
								 	angle_x1, //ɨ���ĽǶ�   
						            false, //�Ƿ�ʹ������     
						            m_oPaint); 		
						 
						m_oPaint.setColor(warnPerColor2); // ����䵥ɫ 
//						canvas.drawOval(m_rRectF3, m_oPaint); 			
						float angle_x2 = 270*warnPer2;
						canvas.drawArc(m_rRectF_h, //������ʹ�õľ��������С   
										angle_x1+135,  //��ʼ�Ƕ�   
										angle_x2, //ɨ���ĽǶ�   
							            false, //�Ƿ�ʹ������     
							            m_oPaint); 
						m_oPaint.setColor(warnPerColor3); // ����䵥ɫ 
						float angle_x3 = 270-angle_x1-angle_x2;
//						canvas.drawOval(m_rRectF3, m_oPaint); 							
						canvas.drawArc(m_rRectF_h, //������ʹ�õľ��������С   
										135+angle_x1+angle_x2,  //��ʼ�Ƕ�   
										angle_x3, //ɨ���ĽǶ�   
							            false, //�Ƿ�ʹ������     
							            m_oPaint); 


					float x_origin = (m_rRectF3.left+m_rRectF3.right)/(float)2.0;
					float y_origin = (m_rRectF3.top+m_rRectF3.bottom)/(float)2.0;
					float x_p = m_rRectF3.right - x_origin;
					float y_p = m_rRectF3.bottom - y_origin;
					
				//������Բ�̶�  �̶ֿ� 
					m_oPaint.setColor(m_cLineColor);		
					m_oPaint.setStrokeWidth(2);				
					canvas.save();
					canvas.translate(x_origin, y_origin);	
					canvas.rotate(45);
//					canvas.rotate(-270/scale);
					for(int i=0;i<=scale;i++){						 
						 
						 canvas.drawLine(0, y_p, 0, y_p-16, m_oPaint);	
						 canvas.rotate((float)270/(float)scale);
					}
					canvas.restore();
					//������Բ�̶�  ϸ�̶�
//					m_oPaint.setColor(Color.RED);			
					m_oPaint.setStrokeWidth(1);				
					canvas.save();
					canvas.translate(x_origin, y_origin);
					canvas.rotate(45);
					float count2 = scale*5;
					for(int i=0;i<=count2;i++){						 				 
						 canvas.drawLine(0, y_p, 0, y_p-10, m_oPaint);	
						 canvas.rotate((float)270/(float)count2);
					}
					canvas.restore();
						
				//�ٸ���һ����Բ	
					m_oPaint.setColor(v_iBorderColor); // ����䵥ɫ	
					m_oPaint.setStrokeWidth(m_nBorderWidth);
					m_oPaint.setStyle(Paint.Style.STROKE);
					m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��   
					if(m_rRectF2.bottom-m_rRectF2.top!=m_rRectF2.right-m_rRectF2.left){
						m_rRectF2.left = m_rRectF2.right+m_rRectF2.bottom-m_rRectF2.top;
					}
			        canvas.drawOval(m_rRectF2, m_oPaint);  
			        
				//������ǩ
					canvas.save();	
					canvas.translate(x_origin, y_origin);			
					m_oPaint.setColor(m_cLineColor);
					m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
					m_oPaint.setTextSize(16);
					m_oPaint.setStyle(Paint.Style.FILL);
					Path path = new Path();
					path.addArc(new RectF(-x_p, -y_p, x_p, y_p), 0, 360);		
				
					canvas.rotate(135);
					canvas.rotate(-270/scale);
					for(int i=0;i<=scale;i++){						 
						canvas.rotate(270/scale); 
//						 if(i%2==0){ //2���̶���ʾһ����ǩ
							 float label_value = minValue+( (maxValue-minValue)/scale *(float)i);
							 DecimalFormat decimalFloat = new DecimalFormat("0"); //floatС���㾫�ȴ���
							 String str = decimalFloat.format(label_value); 
							 
							canvas.drawTextOnPath(str, path, -5, 30, m_oPaint);
							
//						 }
						 
					}
					canvas.restore(); 
					
				//��������ָ��
					canvas.save();	
					canvas.translate(x_origin, y_origin);
					m_oPaint.setColor(0xE500263E);
					m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��			
					m_oPaint.setStyle(Paint.Style.FILL);
					canvas.rotate(45);
					canvas.rotate(angle);
					Path path2 = new Path();
					path2.moveTo(8, 0);
					path2.lineTo(0, y_p*(float)0.8);
					path2.lineTo(-8, 0);
					path2.lineTo(8, 0);
					canvas.drawPath(path2, m_oPaint);

					canvas.restore();
					
					//�������ĵ�	
					m_oPaint.setStyle(Paint.Style.FILL); 
					m_oPaint.setColor(0xFFFFFFFF); // ����䵥ɫ					  
					canvas.drawCircle(x_origin, y_origin, 12,  m_oPaint);
					m_oPaint.setColor(0xCEE5E2E2); // ����䵥ɫ					  
					canvas.drawCircle(x_origin, y_origin, 12,  m_oPaint);
					m_oPaint.setStrokeWidth(1);
					m_oPaint.setColor(0x66000000); // ����䵥ɫ	
					m_oPaint.setStyle(Paint.Style.STROKE); 
					canvas.drawCircle(x_origin, y_origin, 12,  m_oPaint);
					
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_DashBoard>>onLayout","into"); 		
		
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_DashBoard-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_DashBoard>>doLayout","into"); 	
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
		data_value = Float.parseFloat(newValue);
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
		     else if ("LineColor".equals(strName)) {
		        	m_cLineColor = Color.parseColor(strValue);
		        }
		        else if ("WarmPer1".equals(strName)){
		           	warnPer1 = Float.parseFloat(strValue);
		        }
		        else if ("WarmPer2".equals(strName)){
		           	warnPer2 = Float.parseFloat(strValue);
		        }
		        else if ("WarmPerColor1".equals(strName)){
		        	warnPerColor1 = Color.parseColor(strValue);
		        }
		        else if ("WarmPerColor2".equals(strName)){
		        	warnPerColor2 = Color.parseColor(strValue);
		        }
		        else if ("WarmPerColor3".equals(strName)){
		        	warnPerColor3 = Color.parseColor(strValue);
		        }
		        else if ("MaxValue".equals(strName))
		        	maxValue = Integer.parseInt(strValue);
		        else if ("scale".equals(strName))
		        	scale = Integer.parseInt(strValue);
		        else if ("mode".equals(strName))
		        	if("".equals(strValue)){ 
		        		
		        	}else{
		        		mode = Integer.parseInt(strValue);
		        	}
		     else if ("ForeColor".equals(strName)) 
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
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		return true;
	}
	
}
