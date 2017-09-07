package UIs;

import java.util.List;

import utils.BindExpression;
import utils.Expression;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.SCmd;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

//import data.pool.DataPoolModel;
//import data.pool_model.SCmd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

//�Զ���ؼ�Button  ʹ��new Button��ʽ  
public class Ks_Button extends ViewGroup implements VObject{

	public Ks_Button(Context context) {
		super(context);
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		button = new Button(context);
		button.setGravity(Gravity.CENTER);
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
						Intent intent2 = new Intent("android.intent.action.STATUSBAR_VISIBILITY"); //���ص����� ok
						m_Page.getContext().sendBroadcast(intent2);
					}else{  //�����תҳ��
						String[] arrStr = v_strClickEvent.split("\\(");
						if (m_Page != null && "Show".equals(arrStr[0])) {
							String[] arrSplit = arrStr[1].split("\\)");
							m_Page.changePage(arrSplit[0]+".xml");  
							return;
						}
					}
				}else if("".equals(v_strUrl)==false){
					
				}else if("".equals(v_strCmdExpression)==false){
					if(cmdExpression != null){
						SCmd scmd = new SCmd();
						scmd.equipId = cmdExpression.equip_ExId;
						scmd.cmdId = cmdExpression.command_ExId;
						scmd.value = cmdExpression.value;
						scmd.valueType = 1;
						NetDataModel.addSCmd(scmd);
						Toast.makeText(m_Page.getContext(), "���ͳɹ���", Toast.LENGTH_SHORT).show();
						Log.i("button1-onClick"," v_strCmdExpression  ���Ϳ������");
					}
				}
				i++;
		//		v_strContent = "�����ť��"+ String.valueOf(i);
				Log.i("button1-onClick","into");
				doInvalidate();
			}
		}
	};

	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Button";           //�ؼ�����
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
	String v_strCmdExpression = "Binding{[Cmd[Equip:1-Temp:173-Command:1-Parameter:1-Value:1]]}";             //�ؼ�����������ʽ
	String v_strYTCmdExpression = "";          //�ؼ�ң��������ʽ
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "show(��ҳ)";           //�ؼ�����¼���ת����
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_Page = null;         //��ҳ����
	
	//����ؼ�ʹ�õ�Ԫ��
	Button button;
	//��������
	int i = 0;
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���      �ź��б� ֻ�������
	Expression expression = null; //���ʽ������
	BindExpression cmdbindExpression = null;  //���ư󶨴�����
	int cmdbindExpressionItem_num = 0;     //���ư����� �ĸ���      ֻ�������
	Expression cmdExpression = null;     //���Ʊ��ʽ������
			
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{
//		Log.e("Ks_Button>>dispatchDraw","into");
		super.dispatchDraw(canvas);	
		
//		canvas.drawColor(Color.YELLOW);   //����viewgroup�ĵװ���ɫ 
		float fontSize = v_iHeight*(float)0.4/MainActivity.densityPer;
		button.setTextSize(v_iHeight*(float)0.4/MainActivity.densityPer);
		button.setTextColor(v_iFontColor);
		button.setPadding(0, (int)v_iHeight/2-(int)(fontSize*MainActivity.densityPer), 0, 0); //��̬����button ���С̫С�ἷ������ ���޸�����Ԫ��λ�� 
		button.setText(v_strContent);
		button.getPaint().setFakeBoldText(v_bIsBold); 
		
		//������view
		drawChild(canvas, button, getDrawingTime());
			
	}
	//��дonLayout() ����viewGroup�����е���view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Button>>onLayout","into");
		//������view��layout
		button.layout(0, 0, v_iWidth, v_iHeight);  //�����Ը����淶
						
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_Button-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return true;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Button>>doLayout","into");
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
	
		return false;  //�ݲ����� �ؼ�
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
		if("".equals(v_strExpression)==false){
			bindExpression = new BindExpression();
			bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);			
		}
		if("".equals(v_strCmdExpression)==false){
			cmdbindExpression = new BindExpression();
			cmdbindExpressionItem_num = cmdbindExpression.getBindExpression_ItemLst(v_strCmdExpression);	
			if(cmdbindExpression != null){
				String str_bindItem = cmdbindExpression.itemBindExpression_lst.get(0); //����
				List<Expression> expression_lst = cmdbindExpression.itemExpression_ht.get(str_bindItem);
				cmdExpression = expression_lst.get(0); //������
			}

		}
	
		return false;
	}
}
