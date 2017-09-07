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
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//�Զ���ؼ�Rectangle  ʹ��new TextView��ʽ   Ŀǰ δ������ɫ  ��ֵ�� ��ɫ�ı书��
public class Ks_Rectangle extends ViewGroup implements VObject{

	public Ks_Rectangle(Context context) {
		super(context);
		//ʵ�����ÿؼ������Ԫ�ؿؼ�

		//��Ԫ����ӵ���������

	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Rectangle";           //�ؼ�����
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
	int v_iFillColor=0x00000000; //�����ڲ������ɫ
	int v_iBorderColor =0xFF000000; //����߿���ɫ
	int v_iBorderWidth = 1;  //�߿�������С
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��

	//��������
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���
	Expression expression = null; //���ʽ������
	
	//��������
	boolean v_bIsHGradient = true; // ˮƽ����
	int v_iSingleFillColor = 0x00000000;
	int v_iStartFillColor = 0x00000000;
	float[] v_fGradientColorPos = null;
	int[] v_iGradientFillColor = null;
	float v_fAlpha_2 = 1.0f;                 //�ؼ���λ

		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //����viewgroup�ĵװ���ɫ 
		//���Ʒ������
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // ���û��ʵľ��Ч��     
		RectF rectf = new RectF();
		rectf.left = 0+v_iBorderWidth/2;
		rectf.top = 0+v_iBorderWidth/2;
		rectf.right = v_iWidth-v_iBorderWidth/2;
		rectf.bottom = v_iHeight-v_iBorderWidth/2;   
        // 0,0.4,#FFC0C0C0,0,#FF585858,0.5,#FFC0C0C0,1
        // ������ɫ�ͽ����
        if (v_fGradientColorPos != null) {
		    LinearGradient lg = null;
		    if (v_bIsHGradient) {
		        lg = new LinearGradient(0, v_iHeight/2, v_iWidth, v_iHeight/2, v_iGradientFillColor, 
		        		v_fGradientColorPos, TileMode.MIRROR);
		    }
		    else {
		        lg = new LinearGradient(v_iWidth/2, 0, v_iWidth/2, v_iHeight, v_iGradientFillColor, 
		        		v_fGradientColorPos, TileMode.MIRROR);      	
		    }
		    mPaint.setShader(lg);
        }
        else
        	mPaint.setColor(v_iSingleFillColor); // ����䵥ɫ
        
		//���ƾ��������ɫ
		mPaint.setStrokeWidth(1);    //�����������
//		mPaint.setColor(v_iFillColor);
		mPaint.setStyle(Paint.Style.FILL); 
		canvas.drawRoundRect(rectf, v_iRadius,v_iRadius, mPaint);  
		//���ƾ��α߿�
		if(v_iBorderWidth != 0){
			mPaint.setStrokeWidth(v_iBorderWidth);    //�����������
			mPaint.setColor(v_iBorderColor);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRoundRect(rectf, v_iRadius,v_iRadius, mPaint);	
		}
		
//		Log.e("Ks_Rectangle>>dispatchDraw","into"); 
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Rectangle>>onLayout","into"); 		
		
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Rectangle-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Rectangle>>doLayout","into"); 
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
		parseFontcolor(newValue);    //����������ɫ
		if("".equals(realTimeValue.strResultMeaning)){  //�ж��� ģ���� 
			v_strContent = newValue;
		}else{                                          //�ж��� ������
			v_strContent = realTimeValue.strResultMeaning;
		}		
		realTimeValue = null;
		return true;
	}
	//��ɫ��������  �����������ʾֵ   fang
	public int parseFontcolor(String strValue)
	{
		v_iSingleFillColor = v_iStartFillColor;
		if( (v_strColorExpression == null)||("".equals(v_strColorExpression)) ) return -1;
		if( (strValue == null)||("".equals(strValue)) ) return -1;
		if("0.0".equals(strValue)) return -1;		
		if( (">".equals(v_strColorExpression.substring(0,1)))!=true ) return -1;
	
		String buf[] = v_strColorExpression.split(">"); //��ȡ���ʽ�е���������ɫ��Ԫ
		for(int i=1;i<buf.length;i++){
			String a[] = buf[i].split("\\[|\\]"); //����ָ���[ ]			
//			Log.e("Label-updataValue", "�Ƚ�ֵ"+a[0]+"+��ɫ��ֵ��"+a[1]);
			//�Ƚ���ֵ	
			float data = Float.parseFloat(a[0]); //��ñȽ�ֵ
			float value = Float.parseFloat(strValue); //����ֵ
			if(value > data){
				v_iSingleFillColor = Color.parseColor(a[1]);
			}
		}	
		return v_iSingleFillColor;
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
		    	 String[] arrStr = strValue.split(",");
		        	if (arrStr.length == 1) {      //�ж��Ǵ�ɫ���ǽ���ɫ
		        		v_iSingleFillColor = Color.parseColor(strValue);
		        		v_iStartFillColor = v_iSingleFillColor;
		        	}
		        	else {
		        		if (Integer.parseInt(arrStr[0]) == 0)
		        			v_bIsHGradient = false;
		        		else
		        			v_bIsHGradient = true;
		        		v_fAlpha = Float.parseFloat(arrStr[1]);
		        		
		        		int nCount = (arrStr.length - 2) / 2;
		        		v_fGradientColorPos = new float[nCount];
		        		v_iGradientFillColor = new int[nCount];
		        		int nIndex = 0;
		        		for (int i = 2; i < arrStr.length; i += 2) {	
		        			int color = Color.parseColor(arrStr[i]);
		        			v_iGradientFillColor[nIndex] = Color.argb((int)(Color.alpha(color)*v_fAlpha), Color.red(color), Color.green(color), Color.blue(color));
		        			v_fGradientColorPos[nIndex] = Float.parseFloat(arrStr[i+1]);
		        			nIndex++;
		        		}
		        	}
		     }
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
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		return true;
	}


}
