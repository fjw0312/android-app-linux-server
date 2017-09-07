package UIs;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import utils.BindExpression;
import utils.Expression;
import utils.RealTimeValue;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//�Զ���ؼ�ImageChange  ʹ��new TextView��ʽ   ʵ�ְ����� ��̬�仯ͼƬ
public class Ks_ImageChange extends ViewGroup implements VObject{

	public Ks_ImageChange(Context context) {
		super(context);		
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		//��Ԫ����ӵ���������

	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "ImageChange";           //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "";          //�ؼ��󶨱��ʽ
	int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�ؼ���������ɫ
	String v_strContent = "0";              //�ؼ��ַ�����
	String v_strFontFamily = "΢���ź�";      //�ؼ���������
	boolean v_bIsBold = false;               //�ؼ������Ƿ�Ӵ�
	String v_strHorizontalContentAlignment = "Center"; //�ؼ����ݵĺ���װ���䷽ʽ
	String v_strVerticalContentAlignment = "Center";  //�ؼ����ݵ�����װ���䷽ʽ
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //������ɫ�仯���ʽ
	String v_strCmdExpression = "";             //�ؼ�����������ʽ
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "��ҳ.xml";           //�ؼ�����¼���ת����
	String v_strImageExpression = "&gt;1[2.jpg]&gt;2[3.jpg]&gt;3[4.jpg]"; //ͼƬ�仯���ʽ
		                         //Binding{>1[2.jpg]>2[3.jpg]>3[4.jpg]}
	String v_strImgPath = "";
	String v_strImage = "fjw_logo.jpg";
	String v_strStartImage = "fjw_logo.jpg";
	String passWork = "pass";
	String usr = "fang";
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ 
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��

	//��������
	Bitmap bitmap;
	Hashtable<Float, String> htValue_ImageName = null; //<���ڵ��ڵ���ֵ�� ͼƬ����>����
	List<Float> htValue = null;  //<�Ƚ�ֵ>
	
	//�������� 
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���
	Expression expression = null; //���ʽ������
	int times = 0;

	
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //����viewgroup�ĵװ���ɫ 
		
		String path[] = v_strImgPath.split(".xml");	
		String imgPath = path[0]+".files/"+v_strImage;
//		Log.e("Ks_ImageChange->dispatchDraw>>v_strImage=", imgPath); 
		bitmap = BitmapFactory.decodeFile(imgPath);  
		if(bitmap==null) return; 
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // ���û��ʵľ��Ч��     
		
		Rect rect = new Rect();
		Rect rect2 = new Rect();
		rect.left = 0;
		rect.top = 0;
		rect.right = bitmap.getWidth();
		rect.bottom = bitmap.getHeight();
		
		rect2.left = 0;
		rect2.top = 0;
		rect2.right = v_iWidth;
		rect2.bottom = v_iHeight;
		
		canvas.rotate(v_fRotateAngle, v_iWidth/2, v_iHeight/2);
		canvas.drawBitmap(bitmap, rect, rect2, mPaint);

		
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_ImageChange-onLayout","into"); 		
		
	}

	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_ImageChange-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_ImageChange-doLayout","into");
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
		// �ɺ��� �ڴ˴� �жϰ󶨵�����  �ǰ��ź�value ���Ǹ澯�ȼ�EventSeverity
		RealTimeValue realTimeValue = new RealTimeValue();	
		String newValue = 	realTimeValue.getRealTimeValue(expression_lst);			
		if(v_strContent.equals(newValue) ) return false; //��ֵδ�ı� ������ 
		v_strContent = newValue;
		realTimeValue = null;
		Log.e("Ks_ImageChange>>updataValue>>","��ȡ���ݣ�"+v_strContent);
		
		try{
		//�����Ƚ���ֵ ��С 
		   if(htValue_ImageName != null){
			    boolean flag = false;
				float f_value = Float.parseFloat(v_strContent.trim());
				Log.e("Ks_ImageChange>>updataValue>>aaaa",String.valueOf(f_value));
				float f_state = f_value;
				for(int i=0; i<htValue.size();i++){		
					if(f_value >= htValue.get(i)){
						f_state = htValue.get(i);
						flag = true;
					}
				}
				
				if(flag){
					v_strImage = htValue_ImageName.get(f_state);
				}else{
					v_strImage = v_strStartImage;
				}
				
				Log.e("Ks_ImageChange>>updataValue>>rrrr",f_state+"---"+v_strImage);
				return true;    
			}
		}catch(Exception e){
			
			Log.e("Ks_ImageChange>>updataValue>>","�����Ƚ� ��ֵ��С �쳣�׳���");
		}	
			
		return false;  
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
		     else if ("ImgSrc".equals(strName)){
		    	 v_strImgPath = path;
		    	 v_strStartImage = strValue;
		    	 v_strImage = strValue;
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
		        else if ("ImageExpression".equals(strName)) 
		    		v_strImageExpression = strValue;    //ͼƬ�仯���ʽ
			 return true;
		}

	@Override
	public boolean parseExpression(String str_bindExpression) {
		// TODO Auto-generated method stub
		if("".equals(v_strExpression)) return false;
		bindExpression = new BindExpression();
		bindExpressionItem_num = bindExpression.getBindExpression_ItemLst(v_strExpression);
		if(bindExpressionItem_num == 0) return false;
		
		//���� ͼƬ�� ���ʽ >1[2.jpg]>2[3.jpg]>3[4.jpg] 
		try{
			if( (v_strImageExpression == null)||("".equals(v_strImageExpression)) ) return false;
			htValue_ImageName  = new Hashtable<Float, String>();
			htValue = new ArrayList<Float>();
	        String[] strItems = v_strImageExpression.split("\\]"); //>1[2.jpg
			for(int i=0; i<strItems.length; i++){
				String[] items = strItems[i].split("\\[|>");
				htValue_ImageName.put(Float.parseFloat(items[1]), items[2]);
				htValue.add(Float.parseFloat(items[1]));
				Log.e("Ks_ImageChange>>updataValue>>","��ֵ��"+items[1]+" ͼƬ��"+items[2]);
			}
			
			//����
			for(int i=0;i<htValue.size();i++){
				for(int j=i+1;j<htValue.size();j++){
					float tmp1 = htValue.get(i);
					float tmp2 = htValue.get(j);
					if(tmp1>tmp2){
						htValue.set(i, tmp2);
						htValue.set(j, tmp1);
					}
				}
			}
		//	for(int i=0;i<htValue.size();i++){
		//		Log.e("Ks_ImageChange>>parseExpression",">>>>>"+String.valueOf(htValue.get(i)));
		//	}
		}catch(Exception e){
			Log.e("Ks_ImageChange>>parseExpression","����ͼƬ���ʽ�쳣�׳���");
		}
		
		return true; 
	}	

}
