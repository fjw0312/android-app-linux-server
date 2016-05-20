package UIs;

import com.example.ab001.MainWindow;

import common.VObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

//�Զ���ؼ�Button  ʹ��new Button��ʽ
public class f_Button extends ViewGroup implements VObject{

	public f_Button(Context context) {
		super(context);
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
				if("".equals(v_strClickEvent)==false){
					if("��ʾ����".equals(v_strClickEvent)){
						Intent intent = new Intent();
						intent.setAction("android.intent.action.MAIN");
						intent.addCategory("android.intent.category.HOME");
						arg0.getContext().startActivity(intent);
					}else{  //�����תҳ��
						String[] arrStr = v_strClickEvent.split("\\(");
						if (m_MainWindow != null && "Show".equals(arrStr[0])) {
							String[] arrSplit = arrStr[1].split("\\)");
							m_MainWindow.changePage(arrSplit[0]+".xml");
						}
					}
				}else if("".equals(v_strUrl)==false){
					
				}else if("".equals(v_strCmdExpression)==false){
					
				}
				i++;
		//		v_strContent = "�����ť��"+ String.valueOf(i);
				Log.e("button1-onClick","into");
				doInvalidate();
			}
		}
	};

	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Label";           //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "";          //�ؼ��󶨱��ʽ
	int v_iPosX = 0,v_iPosY = 0;       //�ؼ�����
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
	String v_strClickEvent = "show(��ҳ)";           //�ؼ�����¼���ת����
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	MainWindow m_MainWindow = null;         //��ҳ����
	
	//����ؼ�ʹ�õ�Ԫ��
	Button button;
	//��������
	int i = 0;
			
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{
//		Log.e("From-dispatchDraw","into");
		super.dispatchDraw(canvas);				    
//		canvas.drawColor(Color.YELLOW);   //����viewgroup�ĵװ���ɫ 
		button.setTextSize(v_fFontSize);
		button.setTextColor(v_iFontColor);
		button.setText(v_strContent);
		button.getPaint().setFakeBoldText(v_bIsBold);
		
		//������view
		drawChild(canvas, button, getDrawingTime());
			
	}
	//��дonLayout() ����viewGroup�����е���view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Label-onLayout","into"); 
		//������view��layout
		button.layout(0, 0, v_iWidth, v_iHeight);  //�����Ը����淶
						
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		Log.e("form-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return true;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Label-doLayout","into");
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
	public boolean doAddViewsToWindow(MainWindow window){
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
		if("�Ը���".equals(strValue)) return false;
		v_bNeedUpdateFlag = false;
		v_strContent = strValue;
		
		return true;
	}
	//�ؼ����ֲ���setGravity
	public boolean setGravity(){
		return true;
	}
	//�����ؼ�����ز���
	public boolean setProperties(String strName, String strValue){
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
