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
import SAM.extraHisModel.HisSignal;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

//�Զ���ؼ���ʷ�ź�����
public class Ks_zHisSigLine extends ViewGroup implements VObject{

	public Ks_zHisSigLine(Context context) {
		super(context); 
		
		point_lst_24h = new ArrayList<String>();
		
			
		m_oPaint = new Paint();   //���軭����ռ�
		m_oPaint.setTextSize(v_fFontSize); //���û���������С
		m_oPaint.setColor(v_iFontColor);   //���û�����ɫ
		m_oPaint.setAntiAlias(true); // ���û��ʵľ��Ч��
		m_oPaint.setStyle(Paint.Style.STROKE); //���û��ʷ��
		//����ѡ��Ŧ�� ��ť��3��
				ridobuttons = new RadioButton[3];
				ridobuttons[0] = new RadioButton(context);
				ridobuttons[0].setText("24Сʱ");
				ridobuttons[0].setChecked(true);
				ridobuttons[1] = new RadioButton(context);
				ridobuttons[1].setText("24 ��");
				ridobuttons[2] = new RadioButton(context);
				ridobuttons[2].setText("30 ��");
				for(int i=0;i<3;i++){
					ridobuttons[i].setTextColor(Color.BLACK);
			//		ridobuttons[i].setOnClickListener(l);	
				}
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
	String v_strExpression = "Binding{[Value[Equip:114-Temp:173-Signal:1]]}";//�ؼ��󶨱��ʽ
	                      //��֧�ְ��źŸ澯�ȼ�Binding{[EventSeverity[Equip:2-Temp:175-Signal:1]]}
	int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
	int  v_iStartFontColor = 0xFF008000;         //�ؼ���������ɫ
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
	RadioButton[] ridobuttons;
	Axis myAxis;
	//��������
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���
	Expression expression = null; //���ʽ������
	int times = 0;
	
	Paint m_oPaint = null; //�½����� �����
	String equipSignalId = "";
	long x_MaxTimeValue = 0;
	long x_MinTimeValue = 0;
	float y_MaxValue = 0;
	List<String> point_lst_24h = null;  //���24Сʱ�� ���ַ�����
	int a_Width = 150;    //�ؼ���С ���Ϳ� w h 
	int a_Height = 137;
	String str_prevData = "";
	String str_nowData = "";
	String str_nextData = "";
	
	boolean canVISIBLE = false;  //�жϿؼ��Ƿ�ɼ�  ��־����
		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�    
	{		
		super.dispatchDraw(canvas);	
		m_oPaint.setColor(v_iFontColor);   //���û�����ɫ
		m_oPaint.setStyle(Style.STROKE); 	//���û���Ϊʵ��
		float pre_x = 0;
		float pre_y = 0;
		//���� ��Ϣ��
		
		if((point_lst_24h==null)||(point_lst_24h.size()==0)) return;
		try{
			for(int i=0; i<point_lst_24h.size(); i++){
				//��ȡ �ɼ�ʱ�� �� ��ֵ
				String str[] = point_lst_24h.get(i).split("-");
				if(str.length != 2) continue;
				float f_value = Float.parseFloat(str[0]);
				long l_time = Long.parseLong(str[1]);
				
				//����Բ��
				float node_x = myAxis.x_start+myAxis.x_per_unit*(l_time-x_MinTimeValue);
				float node_y = myAxis.y_start-myAxis.y_per_unit*f_value;
	//			Log.e("Ks_SigLine>>onDraw>>ֵ��", String.valueOf(f_value));
   //     		Log.e("Ks_SigLine>>onDraw>>ʱ��", String.valueOf(l_time-x_MinTimeValue));
	//			canvas.drawCircle(node_x, node_y,6, m_oPaint); // ������ֵ��

				//���� ����
				if(i!=0){
					canvas.drawLine(pre_x,pre_y,node_x,node_y,m_oPaint);
				}
				pre_x = node_x;
				pre_y = node_y;
			}
			m_oPaint.setTextSize(14); //���û���������С
			m_oPaint.setColor(Color.BLUE);   //���û�����ɫ
			int x_pad = 35, y_pad = 20;  //λ�� ����
			
			canvas.drawText(str_prevData, myAxis.x_start-x_pad, myAxis.y_start+y_pad, m_oPaint);
			canvas.drawText(str_nowData, myAxis.x_lenth/2, myAxis.y_start+y_pad, m_oPaint);
			canvas.drawText(str_nextData, myAxis.x_lenth-x_pad-10, myAxis.y_start+y_pad, m_oPaint);
		}catch(Exception e){
			Log.e("Ks_SigLine>>onDraw>>","�������� �쳣�׳���");
		}
	
		//������view
	//	drawChild(canvas, myAxis, getDrawingTime());
//		Log.e("Label-dispatchDraw","into"); 
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) { 
//		Log.e("Label-onLayout","into"); 
	//	if(bool)
		myAxis.layout(0, 0, v_iWidth, v_iHeight);  //�����Ը����淶	
		myAxis.upDataValue(v_iWidth,v_iHeight,9,10, 3600*48, 100);//������ʱ������2��
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
	//	Log.e("Ks_Label->onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;  //����false  ��page �ܲ���onTouch();
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
		if(bindExpression==null) return false;
		
		//2��1���ڵ�����ˢ��
		times++;
		if(times>1) times=0;
		
	    x_MinTimeValue = 2000000000;
		
		point_lst_24h.clear();
		//��ȡ�ź�ǰһ����ʷ����
		long preDayTime = java.lang.System.currentTimeMillis() - 3600*24*1000;
		String strpreDayTime = UtTable.getDate(preDayTime, "yyyy.MM.dd HH:mm:ss");		
		String preDayfileName = equipSignalId+"#"+strpreDayTime.substring(0,10);
		str_prevData = strpreDayTime.substring(0,10);
		//Log.e("Ks_SigLine>>updateValue>>��ȡǰһ���ļ�", preDayfileName);
		List<HisSignal> hisSig_lst = new ArrayList<HisSignal>();
		synchronized (HisDataDAO.oneDay_hisSignal_lst) {
			if(HisDataDAO.getHisSignalList(preDayfileName) == false) ;
			else{
				hisSig_lst = HisDataDAO.oneDay_hisSignal_lst;
				if(hisSig_lst==null) return false;	

				//���� ��ʷ�ź�
				for(int i=0; i<hisSig_lst.size();i++){
					HisSignal hisSig = hisSig_lst.get(i);
					if(hisSig==null) continue;
					String value = hisSig.value;
					String readTime = hisSig.freshtime;
					long l_rTime = Long.parseLong(readTime);
					float f_value = Float.parseFloat(value);

					String strPoint = value+"-"+readTime; //�� ��ֵ��ɼ�ʱ��ϲ�Ϊһ���ɼ���
					point_lst_24h.add(strPoint);
					
					if(y_MaxValue < f_value) y_MaxValue = f_value;
//					if(x_MaxTimeValue < l_rTime) x_MaxTimeValue = l_rTime;
//					if(x_MinTimeValue > l_rTime) x_MinTimeValue = l_rTime;
				}
			}
		}

		//��ȡ�źŵ�����ʷ����
		long nowTime = java.lang.System.currentTimeMillis(); 
		String strnowTime = UtTable.getDate(nowTime, "yyyy.MM.dd HH:mm:ss");
		String nowfileName = equipSignalId+"#"+strnowTime.substring(0,10);
		str_nowData = strnowTime.substring(0,10);
		//Log.e("Ks_SigLine>>updateValue>>��ȡ�����ļ�", nowfileName);
		synchronized (HisDataDAO.oneDay_hisSignal_lst) {
			if(HisDataDAO.getHisSignalList(nowfileName) == false) ;
			else{
				hisSig_lst = HisDataDAO.oneDay_hisSignal_lst;
				if(hisSig_lst==null) return false;
				//���� ��ʷ�ź�
				for(int i=0; i<hisSig_lst.size();i++){
					HisSignal hisSig = hisSig_lst.get(i);
					if(hisSig==null) continue;
					String value = hisSig.value;
					String readTime = hisSig.freshtime;
					long l_rTime = Long.parseLong(readTime);
					Float f_value = Float.parseFloat(value);
					
					String strPoint = value+"-"+readTime; //�� ��ֵ��ɼ�ʱ��ϲ�Ϊһ���ɼ���
					point_lst_24h.add(strPoint);
					
					if(y_MaxValue < f_value) y_MaxValue = f_value;
//					if(x_MaxTimeValue < l_rTime) x_MaxTimeValue = l_rTime;
//					if(x_MinTimeValue > l_rTime) x_MinTimeValue = l_rTime;
				}
			}

	
		}
		//��ȡ ����00:00:00��ʱ������	  �������賿 ��Ϊ��ʼʱ��ʱ�䡣		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");//ʱ���ʽת��
			String yy = str_prevData + " 00:00:00";
			long l = formatter.parse(yy).getTime();
			x_MinTimeValue = l/1000;  //���ɵ�λ s

			Log.i("Ks_SigLine>>updateValue>>> ��ʼʱ��ʱ�䣺", yy+"   "+String.valueOf(x_MinTimeValue));
		} catch (Exception e) {
			// TODO Auto-generated catch block 
			Log.e("Ks_SigLine>>updateValue>>> ��ʼʱ��ʱ�䣺", str_prevData+"�쳣�׳���");
			e.printStackTrace();
		}
		
		long nextTime = java.lang.System.currentTimeMillis() + 3600*24*1000;
		String strnextTime = UtTable.getDate(nextTime, "yyyy.MM.dd HH:mm:ss");
		str_nextData = strnextTime.substring(0,10);
		myAxis.upDataValue(v_iWidth,v_iHeight,9,10, 3600*48,100); //������ʱ������2��
			
        return true;  //�пؼ������仯��Ҫ�仯����view��������text����ͼ�� ��Ҫ����true;
	}
	//��ɫ��������  �����������ʾֵ   fang
	public int parseFontcolor(String strValue)
	{
		v_iFontColor = v_iStartFontColor;
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
				v_iFontColor = Color.parseColor(a[1]);
			}
		}	
		return v_iFontColor;
	}
	//����󶨱��ʽ
	public boolean parseExpression(String str_bindExpression){
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		//�ź��б�  ֻ�ᵥ��  ��������
		if(bindExpression==null) return false;
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
		expression = expression_lst.get(0);
		
		int equipt_id = expression.equip_ExId;
		int signal_id = expression.signal_ExId;
		equipSignalId = String.valueOf(equipt_id)+"-"+String.valueOf(signal_id);
		//ע�� ��ʷ�ź�id
		if(HisDataDAO.hisSignalId_lst.contains( equipSignalId ) ==false){
			HisDataDAO.hisSignalId_lst.add(equipSignalId);
//			Log.e("Ks_SigLine>parse_expression>equipSignalId:", equipSignalId);
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
