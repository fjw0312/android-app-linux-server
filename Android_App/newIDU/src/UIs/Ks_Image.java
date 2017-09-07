package UIs;

import mail.EmailSetDialog;
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
import app.main.idu.R;

//�Զ���ؼ�Image  ʹ��new TextView��ʽ    //���󶨵����ź���ʱ �󶨱��ʽӦ�ں����+0
public class Ks_Image extends ViewGroup implements VObject{

	public Ks_Image(Context context) {
		super(context);
		//���øÿؼ�����¼��ļ�
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub 
//				Log.e("f_Image->onClick>>into", "�������¼���"); 
				//�жϵ���¼���Ӧ������
				if("".equals(v_strClickEvent)==false){
					if("��ʾ����".equals(v_strClickEvent)){
						showWaiterAuthorizationDialog();	
					}else if("��������".equals(v_strClickEvent)){
						EmailSetDialog dialog = new EmailSetDialog(m_MainWindow.getContext());
						dialog.showPassDialog();
					}else{  //�����תҳ��
						Log.e("TAG_Image","into onClick");
						if("".equals(usr)){
							String[] arrStr = v_strClickEvent.split("\\(");
							if (m_MainWindow != null && "Show".equals(arrStr[0])) {
								String[] arrSplit = arrStr[1].split("\\)");
								m_MainWindow.changePage(arrSplit[0]+".xml"); 
							}
						}else{
							showPassDialog();  //��ʾȨ�޶Ի���
						}
					}
				}else if("".equals(v_strUrl)==false){  
					
				}else if("".equals(v_strCmdExpression)==false){
					
				}
			}
		});
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		//��Ԫ����ӵ���������
	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "Image";           //�ؼ�����
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
		
	String v_strImgPath = "";
	String v_strImage = "fjw_logo.jpg";
	String passWork = "pass";
	String usr = "fang";
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��

	//��������
	Bitmap bitmap;
	boolean touchFlag = false;
	
	//��� ҳ����ת ����
	private void onTouch_click(){
		Log.e("f_Image->onClick>>into", "�������¼���"); 
		//�жϵ���¼���Ӧ������
		if("".equals(v_strClickEvent)==false){
			if("��ʾ����".equals(v_strClickEvent)){
				showWaiterAuthorizationDialog();	
			}else if("��������".equals(v_strClickEvent)){
				EmailSetDialog dialog = new EmailSetDialog(m_MainWindow.getContext());
				dialog.showPassDialog();
			}else{  //�����תҳ��
				Log.e("TAG_Image","into onClick");
				if("".equals(usr)){
					String[] arrStr = v_strClickEvent.split("\\(");
					if (m_MainWindow != null && "Show".equals(arrStr[0])) {
						String[] arrSplit = arrStr[1].split("\\)");
						m_MainWindow.changePage(arrSplit[0]+".xml"); 
					}
				}else{
					showPassDialog();  //��ʾȨ�޶Ի���
				}
			}
		}else if("".equals(v_strUrl)==false){  
			
		}else if("".equals(v_strCmdExpression)==false){
			
		}
	}
	//��ʾ�û�Ȩ�޶Ի���
	public void showPassDialog(){
    	//LayoutInflater��������layout�ļ����µ�xml�����ļ�������ʵ����  
		LayoutInflater factory = LayoutInflater.from(m_MainWindow.getContext());
		//��activity_login�еĿؼ�������View��
		final View textEntryView = factory.inflate(R.layout.pass_dialog, null);		 
//		textEntryView.setBackgroundColor(Color.RED); //ֻ�� �����м䲿������
        //��LoginActivity�еĿؼ���ʾ�ڶԻ����� 
		
		AlertDialog.Builder dialog =new AlertDialog.Builder(m_MainWindow.getContext());
	//	dialog.getContext().setTheme(R.style.Theme_dialog); //�����Զ�����ʽ	
		dialog.setTitle("�û�Ȩ�޵�¼")  
		//�Ի���ı���  
//      .setTitle("�û�Ȩ�޵�¼")
       //�趨��ʾ��View
       .setView(textEntryView)
       //�Ի����еġ���½����ť�ĵ���¼�
       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

           public void onClick(DialogInterface dialog, int whichButton) { 
        	   
  			//��ȡ�û�����ġ��û������������롱
        	//ע�⣺textEntryView.findViewById����Ҫ����Ϊ����factory.inflate(R.layout.activity_login, null)��ҳ�沼�ָ�ֵ����textEntryView��
        	final EditText etUserName = (EditText)textEntryView.findViewById(R.id.etuserName);
            final EditText etPassword = (EditText)textEntryView.findViewById(R.id.etPWD);
            
          //��ҳ��������л�õġ��û������������롱תΪ�ַ���
   	        String userName = etUserName.getText().toString().trim();
   	    	String password = etPassword.getText().toString().trim();
   	    	if(userName.equals("fang") && password.equals("pass")){
				String[] arrStr = v_strClickEvent.split("\\(");
				if (m_MainWindow != null && "Show".equals(arrStr[0])) {
					String[] arrSplit = arrStr[1].split("\\)");
					m_MainWindow.changePage(arrSplit[0]+".xml");  
				}
   	    	}else if(userName.equals(usr) && password.equals(passWork)){
				String[] arrStr = v_strClickEvent.split("\\(");
				if (m_MainWindow != null && "Show".equals(arrStr[0])) {
					String[] arrSplit = arrStr[1].split("\\)");
					m_MainWindow.changePage(arrSplit[0]+".xml"); 
				}
   	    	}else{
   	    		Toast.makeText(m_MainWindow.getContext(), "������û�������", Toast.LENGTH_SHORT).show();
 //  	    		Toast.makeText(m_rRenderWindow.getContext(), "Incorrect username or password!", Toast.LENGTH_SHORT).show();
   	    	}
          }
           
        })
	    //�Ի���ġ��˳��������¼�
	   .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	   //LoginActivity.this.finish();
	         }
	   })
	      
	   //�Ի���Ĵ�������ʾ
		.create().show();

	}
	  //��ʾ�Ի���
    public void showWaiterAuthorizationDialog() { 
    	
    	//LayoutInflater��������layout�ļ����µ�xml�����ļ�������ʵ����
		LayoutInflater factory = LayoutInflater.from(m_MainWindow.getContext());
		//��activity_login�еĿؼ�������View��
		final View textEntryView = factory.inflate(R.layout.root_dialog, null);
         
        //��LoginActivity�еĿؼ���ʾ�ڶԻ�����
		new AlertDialog.Builder(m_MainWindow.getContext())
		//�Ի���ı���
       .setTitle("��̨ά��")
       //�趨��ʾ��View
       .setView(textEntryView)
       //�Ի����еġ���½����ť�ĵ���¼�
       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) { 
        	   
  			//��ȡ�û�����ġ��û������������롱
        	//ע�⣺textEntryView.findViewById����Ҫ����Ϊ����factory.inflate(R.layout.activity_login, null)��ҳ�沼�ָ�ֵ����textEntryView��
        	final EditText etUserName = (EditText)textEntryView.findViewById(R.id.etuserName);
            final EditText etPassword = (EditText)textEntryView.findViewById(R.id.etPWD);
            
          //��ҳ��������л�õġ��û������������롱תΪ�ַ���
   	        String userName = etUserName.getText().toString().trim();
   	    	String password = etPassword.getText().toString().trim();
   	    	
   	    	//����Ϊֹ�Ѿ�������ַ��͵��û����������ˣ����������Ǹ����Լ�����������д������
   	    	//������һ���򵥵Ĳ��ԣ��ٶ�������û��������붼��1�������������ҳ�棨OperationActivity��
   	    	if(   ( userName.equals("fang") && password.equals("pass") )
   	    	    ||( userName.equals("admin") && password.equals("kstar123") ) ){
				// ����Homeָ��
				Intent intent = new Intent();
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.HOME");  
				m_MainWindow.getContext().startActivity(intent);
				Intent intent2 = new Intent("android.intent.action.STATUSBAR_VISIBILITY"); //���ص����� ok
				m_MainWindow.getContext().sendBroadcast(intent2);

   	    	}else{
   	    		Toast.makeText(m_MainWindow.getContext(), "������û�������", Toast.LENGTH_SHORT).show();
   	    	}
           }
       })
       //�Ի���ġ��˳��������¼�
       .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
        	   //LoginActivity.this.finish();
           }
       })
       
        //�Ի���Ĵ�������ʾ
		.create().show();
	}
	
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		
//		canvas.drawColor(Color.GREEN);   //����viewgroup�ĵװ���ɫ 
		
		String path[] = v_strImgPath.split(".xml");	
		String imgPath = path[0]+".files/"+v_strImage;
//		Log.e("f_Image->dispatchDraw>>v_strImage=", imgPath); 
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
		
		if(("".equals(v_strClickEvent)==false) && (touchFlag)){
			mPaint.setColor(0x500000FF);
			mPaint.setStyle(Paint.Style.FILL); 
			canvas.drawRect(rect2, mPaint);
		}

	//	bitmap.recycle();
//		Log.e("Ks_Image>>onDraw","into");		
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_Image>>onLayout","into");		
		
	}

	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
//		Log.e("Ks_Image-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		 switch (event.getAction())
         {
	            case MotionEvent.ACTION_DOWN: 
	            	Log.e("TAG_Image","into onTouchEvent ACTION_DOWN");
	            	touchFlag = true;
	            	doInvalidate();
	            	break;
	            case MotionEvent.ACTION_UP:
	            	touchFlag = false; 
	            	doInvalidate();
	 //           	Log.e("TAG_Image","into onTouchEvent ACTION_UP");
	         //   	onTouch_click();
	            	break;
	            default: break;
         }
		return true;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_Image>>doLayout","into");
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
	
		return false;   //��ʱ������ �ؼ�
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
		    	 v_strImage = strValue;
		     }
		     else if ("passWork".equals(strName)) 
		        	passWork = strValue; 
		     else if ("user".equals(strName)) 
		    	 	usr = strValue; 
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
