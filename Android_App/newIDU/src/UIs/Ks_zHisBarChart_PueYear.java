package UIs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import utils.BindExpression;
import utils.Calculator;
import utils.Expression;
import utils.RealTimeValue;
import view.Axis;
import view.UtTable;
import SAM.extraHisModel.HisDataDAO;
import SAM.extraHisModel.HisFormula;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

//�Զ���ؼ�  ���õ���pue  ��״ͼ
public class Ks_zHisBarChart_PueYear extends ViewGroup implements VObject{

	public Ks_zHisBarChart_PueYear(Context context) {
		super(context); 
		point_lst = new ArrayList<String>();
			
		m_oPaint = new Paint();   //���軭����ռ�
		m_oPaint.setTextSize(v_fFontSize); //���û���������С
		m_oPaint.setColor(v_iFontColor);   //���û�����ɫ
		m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
		m_oPaint.setStyle(Paint.Style.STROKE); //���û��ʷ��

		//���� һ��������
		myAxis = new Axis(context);
		myAxis.enable_y_label = false;  //����ʾ ���� �Լ���y��̶�
		myAxis.y_density = 5;  //����y��̶��ܶ�				

		//��Ԫ����ӵ���������
		addView(myAxis);
	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Label";           //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "";//�ؼ��󶨱��ʽ     �ؼ������
	int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
	int  v_iStartFontColor = 0xFF008000;    //�ؼ���������ɫ
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
	Axis myAxis;
	//��������
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���
	Expression expression = null; //���ʽ������
	int times = 0;
	
	
	Paint m_oPaint = null; //�½����� �����
	String str_prevData = "";
	String str_nowData = "";
	String str_nextData = "";
	
	List<String> point_lst = null;  //��ŵ��ַ�����
	
	long x_MaxTimeValue = 0;
	long x_MinTimeValue = 0;
	float y_MaxValue = 0;
	
	float prevDay_value = 0;  //��¼ǰһ������
	long nowTime = 0;         //��ǰʱ��
	long prev_num_Mon = 0;    //num ��ǰʱ�� ʱ��
	int  numDay = 4;          //��ȡ ������ �õ���
	
	boolean canVISIBLE = false;  //�жϿؼ��Ƿ�ɼ�  ��־����
		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�    
	{		
		super.dispatchDraw(canvas);	
		m_oPaint.setStyle(Style.FILL); 	//���û���Ϊʵ��
		
		//���� ��Ϣ��
		if((point_lst==null)||point_lst.size()==0) return;
		try{
					for(int i=0; i<point_lst.size(); i++){
//						Log.e("Ks_HisBarChart_PueChart>>onDraw>>str", point_lst_mon.get(i));
						if(i > numDay-1) break;
						int ii = i;
						if(point_lst.size() > numDay){
							ii = point_lst.size() - numDay + i;
						}
						//��ȡ �ɼ�ʱ�� �� ��ֵ
						String str[] = point_lst.get(ii).split("-");
						if(str.length != 2) continue;
						
						float f_value = Float.parseFloat(str[0]);
						if(f_value==0) continue;
						//float f_time = Float.parseFloat(str[1]);
				
						//����Բ��
						float node_x = myAxis.x_start+myAxis.x_unit*(i+1);
						float node_y = myAxis.y_start-myAxis.y_per_unit*f_value;
						RectF rectf = new RectF();
						rectf.left =  node_x - myAxis.x_unit/4;
						rectf.top =  node_y;
						rectf.right =  node_x + myAxis.x_unit/4;
						rectf.bottom =  myAxis.y_start;
						m_oPaint.setColor(v_iFontColor);   //���û�����ɫ
						canvas.drawRect(rectf, m_oPaint);
						
						m_oPaint.setTextSize(18);
			    		DecimalFormat decimalFloat = new DecimalFormat("0.00"); //floatС���㾫�ȴ���
			    		String strValue= decimalFloat.format(f_value);
						canvas.drawText(strValue, node_x-30, node_y-2, m_oPaint);  // ������ֵ
						m_oPaint.setTextSize(15);
						m_oPaint.setColor(Color.BLACK);   //���û�����ɫ
						canvas.drawText(str[1], node_x-40, myAxis.y_start+20, m_oPaint); // ����  y�� ��ǩ ����
					}

		}catch(Exception e){
			Log.e("Ks_HisBarChart_PueYear>>onDraw>>","�������� �쳣�׳���");
		}
		//Log.e("Ks_HisBarChart_PueYear>>dispatchDraw>>","into!!!");
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) { 
//		Log.e("Ks_zHisBarChart_RcYear-onLayout","into"); 
		myAxis.layout(0, 0, v_iWidth, v_iHeight);  //�����Ը����淶	
		myAxis.upDataValue(v_iWidth,v_iHeight,numDay,25, numDay,y_MaxValue*(float)1.2); //������ʱ������2��
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
	//	Log.e("Ks_zHisBarChart_RcYear->onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;  //����false  ��page �ܲ���onTouch();
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_zHisBarChart_RcYear-doLayout","into");
		this.layout(v_iPosX, v_iPosY, v_iPosX+v_iWidth, v_iPosY+v_iHeight); //���Ƹ�view�װ�layout		

	}
	
	//����invalidate() �ؼ�����->onDraw()���ú���
	public void doInvalidate(){
			this.invalidate();
			myAxis.invalidate();
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
		v_bNeedUpdateFlag = false;
		if("".equals(HisDataDAO.hisFormulaId_lst.get(0))) return false;	
				
		point_lst.clear();

		//��ȡ�ź� ���� ��ʷʽ������
		nowTime = java.lang.System.currentTimeMillis();
		prev_num_Mon = nowTime - (long)numDay*365*24*3600*1000; //��ȡnumDay����ǰ�� ʱ��			
		String strnowTime = UtTable.getDate(nowTime, "yyyy.MM.dd HH:mm:ss");
		String nowfileName = HisDataDAO.hisFormulaId_lst.get(0)+"#";
		
		if(HisDataDAO.getPueLine_HisFormulaList(nowfileName, 13, 2)){
			List<HisFormula> hisFormula_lst = HisDataDAO.year_pueBar_hisFormula_lst;
			if(hisFormula_lst==null) return false; 			
				//���� ��ʷʽ�� 
				for(int i=0; i<hisFormula_lst.size();i++){
					HisFormula hisFormula = hisFormula_lst.get(i);
					long  value_l_time = Long.parseLong(hisFormula.getTime) *1000;					
					if(value_l_time > prev_num_Mon){	//���4 ��
						String start_value = hisFormula.strContent;					
						String start_str[] = start_value.split("&"); 
						String end_value = hisFormula.strEndContent;						
						if("".equals(end_value)|| end_value==null)  continue;  //δ�н������ݣ�
						//��ȡ ʽ�ӵ� ��������
						float  f_start_value_T = Float.parseFloat(start_str[0]);
						float  f_start_value_H = Float.parseFloat(start_str[1]);
						float  f_end_value_T = f_start_value_T;
						float  f_end_value_H = f_start_value_H;
						if("".equals(end_value)==false){
							String end_str[] = end_value.split("&");
							f_end_value_T = Float.parseFloat(end_str[0]);
							f_end_value_H = Float.parseFloat(end_str[1]);
						}
						
						float f_T_d = f_end_value_T - f_start_value_T;
						float f_H_d = f_end_value_H - f_start_value_H;
						float  f_value = f_T_d/(f_T_d - f_H_d);
//						Log.e("Ks_HisBarChart_PueYear>>updateValue>>strPoint��", String.valueOf(f_value));

						String readTime_day = hisFormula.strTime.substring(0,4); //��ȡ ��  						
						String v = String.valueOf( f_value );
						String strPoint = v+"-"+readTime_day; //�� ��ֵ��ɼ�ʱ��ϲ�Ϊһ���ɼ���
						point_lst.add(strPoint);
						
						if(y_MaxValue < f_value) y_MaxValue = f_value;
//						Log.e("Ks_HisBarChart_PueYear>>updateValue>>strPoint��", hisFormula_lst.get(i).strContent+"---"+hisFormula_lst.get(i).strEndContent+">>>"+strPoint);
					}
				}//end for
		}//end if

		myAxis.upDataValue(v_iWidth,v_iHeight,numDay,25, numDay,y_MaxValue*(float)1.2); //������ʱ������2��
        return true;  //�пؼ������仯��Ҫ�仯����view��������text����ͼ�� ��Ҫ����true;
	}

	//����󶨱��ʽ
	public boolean parseExpression(String str_bindExpression){
		if("".equals(v_strExpression)) return false;
		
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
		     else if ("FontColor".equals(strName)){
		    	 v_iStartFontColor = Color.parseColor(strValue); 
		    	 v_iFontColor = v_iStartFontColor;
		     }		        	
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
