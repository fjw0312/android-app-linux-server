package UIs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import utils.BindExpression;
import utils.Expression;

import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.SCmd;
import SAM.XmlCfg.xml_cmdCfg;
import SAM.XmlCfg.xml_cmdCfg.CommandParameter;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

//import data.pool.DataPoolModel;
//import data.pool_model.SCmd;
//import data.pool_model.equipment_cell.SCmdCfg;
//import data.pool_model.equipment_cell.SCmdCfg.CmdParameaningCfg;
import android.R.anim;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
//button.postInvalidate();  �ڷ�ui�߳�ˢ��view
//button.invalidate();       ������ui�߳�ˢ��view
//�ڷ�ui�߳�ˢ��uiһ����2�ַ�ʽ    postInvalidate   Handler
//ң�� ���� �ؼ�  
//made in kstar
//�Զ���ؼ�YkParameter  ʹ��SPinner Button   //Ŀǰ����bug adapter�����س�Ա��ʾ  ���õ�һ���̴߳���
public class Ks_YKParameter extends ViewGroup implements VObject{

	public Ks_YKParameter(Context context) {
		super(context);
		
		scmd = new SCmd();
		//ʵ�����ÿؼ������Ԫ�ؿؼ�		
		button = new Button(context);
		button.setText("����");
		button.setGravity(Gravity.CENTER); //Ŀǰ��������λ��ƫ�� debug
		button.setOnClickListener(l);
		spinner = new Spinner(context);
//		spinner.setBackgroundColor(Color.GRAY);
		adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		adapter.add("��ѡ���");
		textview = new TextView(context);
		textview.setText("��ѡ���");
		textview.setGravity(Gravity.CENTER);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			 public void onItemSelected(			
	                    AdapterView<?> parent, View view, int position, long id) {	
	            //	Log.e("YKParameter->onItemSelected","into onItemSelected");
	            }

	            public void onNothingSelected(AdapterView<?> parent) {
	            //	Log.e("YKParameter->onItemSelected","into onNothingSelected");
	            }
		});
		//����spinner�����item  ��Ч
		spinner.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			
			@Override
			public void onChildViewRemoved(View arg0, View arg1) {
				// TODO Auto-generated method stub
			//	Log.e("YKParameter->onChildViewRemoved>>","into onChildViewRemoved��");
			}
			
			@Override
			public void onChildViewAdded(View arg0, View arg1) {
				// TODO Auto-generated method stub
			//	Log.e("YKParameter->onChildViewAdded>>","into onChildViewAdded��");
			}
		});
		//�޷�������spinner item�Ĵ���
		spinner.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
//				Log.e("YKParameter->spinner.onTouch>>","into onTouch��");
				return false;
			}
		});
		//��Ԫ����ӵ���������
		addView(spinner);
		addView(button); 
		addView(textview); 
		
//		mythread.start();
	}
	//ʵ����button�ĵ���¼�����
	private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Log.e("YKParameter->onClick>>","into��"); 
			if(arg0==button){										
				String cloose = (String) spinner.getSelectedItem();
				//�Ƚ�ѡ���meaning  ��ȡ ��Ӧ��value
				Log.e("YKParameter->onClick>>cloose", cloose); 
				
				Iterator<String> lst = CommandMeaninglst.keySet().iterator();
				while(lst.hasNext()){
					String value = lst.next();
					if( cloose.equals(CommandMeaninglst.get(value)) ){
						scmd.value = value;
						NetDataModel.addSCmd(scmd);
						Toast.makeText(m_MainWindow.getContext(), "���ͳɹ���", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				Log.e("YKParameter->onClick>>","δ��ȡ�� ��Ӧ���� ���ͣ�"); 
			}		
		}
	};

	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 10:
				doInvalidate();
				break;
			default:
				break;
			}			
		}
	};
	private Thread mythread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub 
	//		while(true){ 
				try{
					Thread.sleep(500);
				}catch(Exception e){ 
					
				}
				String cloose = (String) spinner.getSelectedItem();
				if(cloose.equals(OldText)==false){
					OldText = cloose;
					
					myHandler.sendEmptyMessage(10);				
				}
	//		}
		}
	});
	
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "YkParameter";           //�ؼ�����
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
	String v_strCmdExpression = "Binding{[Cmd[Equip:1-Temp:173-Command:1-Parameter:1-Value:1]]}";             //�ؼ�����������ʽ
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "��ҳ.xml";           //�ؼ�����¼���ת����
	
	float v_fButtonWidthPer = (float)0.4;
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	//����ؼ�ʹ�õ�Ԫ��
	Spinner spinner;
	Button button;
	TextView textview;
	//��������
	int spinnerWidth;  //edittext�Ŀ��
	private ArrayAdapter<String> adapter;
	private ArrayList<String> strlst;
	String OldText="��ѡ���";
	BindExpression cmdbindExpression = null;  //���ư󶨴�����
	int cmdbindExpressionItem_num = 0;     //���ư����� �ĸ���      ֻ�������
	Expression cmdExpression = null;     //���Ʊ��ʽ������
	SCmd scmd = null; 
	int parameterId = 0;
//	List<CmdParameaningCfg> CmdParameaningCfg_lst = new ArrayList<CmdParameaningCfg>();
	HashMap<String, String> CommandMeaninglst = new HashMap<String, String>();
	boolean getParameanFlag = true;

		
	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{		
		super.dispatchDraw(canvas);		 
//		canvas.drawColor(Color.LTGRAY);   //����viewgroup�ĵװ���ɫ 
		float fontSize = v_iHeight*(float)0.35/MainActivity.densityPer;
		button.setTextSize(fontSize);
		button.setPadding(0, (int)v_iHeight/2-(int)(fontSize*MainActivity.densityPer), 0, 0); //��̬����button ���С̫С�ἷ������ ���޸�����Ԫ��λ�� 

		textview.setTextSize(v_iHeight*(float)0.25);
		textview.setText(OldText);

		drawChild(canvas, spinner, getDrawingTime());
		drawChild(canvas, button, getDrawingTime());
		drawChild(canvas, textview, getDrawingTime());
//		Log.e("YKParameter->dispatchDraw>>","into��"); 
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout 
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Label-onLayout","into"); 	
		spinnerWidth = (int)(v_iWidth*((float)1-v_fButtonWidthPer));
		spinner.layout(5, 5, spinnerWidth-5, v_iHeight-5);  //�����Ը����淶		
		textview.layout(5, 5, spinnerWidth-5, v_iHeight-5);  //�����Ը����淶	
		button.layout(spinnerWidth, 0, v_iWidth, v_iHeight);  //�����Ը����淶	
//		Log.e("YKParameter->onLayout>>","into��"); 
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){  
		super.onTouchEvent(event);
		Log.e("Ks_YKParameter->onTouchEvent","into");		 
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(mythread.isAlive()==false){
				mythread.start();
			}
		}
		
		return true;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���      
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("YKParameter->doLayout>>","into��"); 
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
	//���¿ؼ���ֵ����     �����ַ���  �����Ƿ���ֵд��ɹ�    ֻ��ȡһ��SCmdcfg ����
	public boolean  updataValue(String strValue) {
		if(getParameanFlag == false) return false;
		try{
//			SCmdCfg scmdCfg = DataPoolModel.getSCmdCfg(scmd.equipId, scmd.cmdId);
			xml_cmdCfg scmdCfg = NetDataModel.getSCmdCfg(scmd.equipId, scmd.cmdId);
			CommandMeaninglst = scmdCfg.CommandParameterlst.get(String.valueOf(parameterId)).CommandMeaninglst;
			if( (CommandMeaninglst != null) &&(CommandMeaninglst.size()!=0) ){	
				Iterator<String> vla_lst = CommandMeaninglst.keySet().iterator();
				while(vla_lst.hasNext()){
					String valueId = vla_lst.next();
					adapter.add(CommandMeaninglst.get(valueId));
					Log.i("Ks_YKParameter>>updataValue>>adapter.add", CommandMeaninglst.get(valueId));
				}
				getParameanFlag = false; //��ȡ�� ���� ����  ���� ��ȡ����־����  ʹ���ڸ��»�ȡ
				return true;
			}
		}catch(Exception e){
		//	Log.i("Ks_YKParameter>>updataValue>>","δ��ȡ�� SCmdCfg �쳣�׳���");		
		}			
		
		return false;  //�� �� ʵʱ ˢ������
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
			 else if ("ButtonWidthRate".equals(strName)) 
			        	v_fButtonWidthPer = Float.parseFloat(strValue);
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
	@Override  //���߳�Ϊ ui ���߳� ���û�ȡ �����̳߳� ����
	public boolean parseExpression(String str_bindExpression) {
		// TODO Auto-generated method stub
		if("".equals(v_strCmdExpression)==false){
			cmdbindExpression = new BindExpression();
			cmdbindExpressionItem_num = cmdbindExpression.getBindExpression_ItemLst(v_strCmdExpression);	
			if(cmdbindExpression != null){
				String str_bindItem = cmdbindExpression.itemBindExpression_lst.get(0); //����
				List<Expression> expression_lst = cmdbindExpression.itemExpression_ht.get(str_bindItem);
				cmdExpression = expression_lst.get(0); //������
				//��ȡ ���� ���ݳ�Աitem		
				scmd.equipId = cmdExpression.equip_ExId;
				scmd.cmdId = cmdExpression.command_ExId;
				scmd.valueType = 1;
				parameterId = cmdExpression.parameter_ExId;	
				Log.i("Ks_YKParameter>>updataValue>>�ؼ�id"+v_strID,scmd.equipId+"  "+scmd.cmdId);
			}	   
				
		}
		return false;
	}
}
