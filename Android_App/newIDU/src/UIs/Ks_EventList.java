package UIs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import utils.BindExpression;
import utils.Expression;
import view.UtTable;
import SAM.DataPool.NetDataModel;
import SAM.DataPoolModel.Event;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;

//import data.pool.DataPoolModel;
//import data.pool_model.Event;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


//�Զ���ؼ�Ks_EventList �ܸ澯�б� ʹ��new draw canvas��ʽ  
//ע�⣬���ڱ����Ҫ�����ݱ��浽����������� ����ˢ�������������ڴ�  �쳣��
public class Ks_EventList extends ViewGroup implements VObject{

	public Ks_EventList(Context context) {
		super(context); 
//		Log.e("Ks_EventList->","into"); 
		//ʵ�����ÿؼ������Ԫ�ؿؼ�
		 lstTitles = new ArrayList<String>();         //�������Ա�ַ�����
		 lstContends = new ArrayList<List<String>>(); //����ַ�����Ա���� 
				 
		 //������view
		 table = new UtTable(context);
		 table.m_bUseTitle = false;  //��ͷ �Ƿ���ʾ
		 this.addView(table);
		 
		 //������  //��Ԫ����ӵ���������
		 lstTitles.add("�豸����");
		 lstTitles.add("�澯����");
		 lstTitles.add("�澯����");
		 lstTitles.add("�澯�ȼ�");
		 lstTitles.add("��ʼʱ��");
		 textView_titles = new TextView[lstTitles.size()];
		 if(table.m_bUseTitle==false){
			 for(int i=0;i<lstTitles.size();i++){
				 textView_titles[i] = new TextView(context);
				 textView_titles[i].setText(lstTitles.get(i));
				 textView_titles[i].setTextColor(Color.BLACK); //������ɫ�� ��������һ��v_iFontColor
				 textView_titles[i].setGravity(Gravity.CENTER);
				 this.addView(textView_titles[i]);
			 }
		 }

		 
		//�����б�������  ��Ҫ���ã�����⵽�л���click1=false  ֹͣ������click1=true;
			this.table.setOnScrollListener(new OnScrollListener() {	
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					// TODO Auto-generated method stub
					switch(arg1){
					case OnScrollListener.SCROLL_STATE_IDLE:  //����Ļֹͣ����
						
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"��Ļֹͣ������");
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //��Ļ���� 
						
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"��Ļ����!");
						break;
					case OnScrollListener.SCROLL_STATE_FLING:   //��ָ�뿪����Ļ���Թ���
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"��Ļ���Թ���!");
						break;
					}
				}
				@Override  //�������ˢ�� ���й������������ø÷���
				public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub 
					//Log.e("Ks_EventList->onScroll>>","listview��������--2��");  
				}
			});
		 
	}
	//Fields
	String v_strID = "";                 //�ؼ�id
	String v_strType = "EventList";           //�ؼ�����
	int v_iZIndex = 1;                    //�ؼ�ͼ��
	String v_strExpression = "Binding{[Equip[Equip:3]]}";          //�ؼ��󶨱��ʽ
	int v_iPosX = 100,v_iPosY = 100;       //�ؼ�����
	int v_iWidth = 50,v_iHeight = 50;       //�ؼ���С
	int v_iBackgroundColor = 0x00000000;    //�ؼ��װ���ɫ
	float v_fAlpha = 1.0f;                 //�ؼ���λ
	float v_fRotateAngle = 0.0f;           //�ؼ���ת�Ƕ�
	float v_fFontSize = 12.0f;              //�ؼ�������С
	int  v_iFontColor = 0xFF008000;         //�������ɫ
	String v_strContent = "��������";        //�ؼ��ַ�����
	String v_strFontFamily = "΢���ź�";      //�ؼ���������
	boolean v_bIsBold = false;               //�ؼ������Ƿ�Ӵ�
	String v_strHorizontalContentAlignment = "Center"; //�ؼ����ݵĺ���װ���䷽ʽ
	String v_strVerticalContentAlignment = "Center";  //�ؼ����ݵ�����װ���䷽ʽ
	String v_strColorExpression = ">20[#FF009090]>30[#FF0000FF]>50[#FFFF0000]"; //������ɫ�仯���ʽ
	String v_strCmdExpression = "";             //�ؼ�����������ʽ
	String v_strUrl = "www.hao123.com";          //�ؼ���ҳ��ַ������ʽ
	String v_strClickEvent = "��ҳ.xml";           //�ؼ�����¼���ת����
	

	int v_iLineThickness =3; //������С
	int v_iLineColor = 0xFF000000;  //������ɫ
//	public int m_cOddRowBackground = 0xFF000000; // ����
//	public int m_cEvenRowBackground = 0xFF000000; // ż��
	
	boolean v_bNeedUpdateFlag = false;            //�ؼ������ֵ���±�ʶ
	Page m_MainWindow = null;         //��ҳ����
	
	//����ؼ�ʹ�õ�Ԫ��
	 UtTable table;
	 TextView[] textView_titles;

	//��������
	List<String> lstTitles = null;  //�������Ա�ַ�����
	List<List<String>> lstContends = null; //����ַ�����Ա����
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���      �ź��б� ֻ�������
	Expression expression = null; //���ʽ������


	boolean isCanUpdateViewFlag1 = true;
	boolean isCanUpdateViewFlag2 = true;
	long nowTime = 10000; //10s
	long oldTime = 0;
	
	
	// ��ʱ 2s ˢ�������߳�   �ȴ� view ��ˢ�½�����
	private class thread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try{
			Thread.sleep(1800);
			}catch(Exception e){				
			}
			isCanUpdateViewFlag1 = true;
		//	Log.e("Ks_EventList->thread>>","  Thread end !");
		}	
	}

	//��дdispatchDraw() ����������view ��������drawChild()����	
	protected void dispatchDraw(Canvas canvas)  //����viewGroup�б�������ӿؼ�   
	{	
		super.dispatchDraw(canvas);		
		canvas.drawColor(v_iBackgroundColor);   //����viewgroup�ĵװ���ɫ  
		
		drawChild(canvas, table, getDrawingTime());
		
		if(table.m_bUseTitle==false){
			for(int i=0;i<textView_titles.length;i++){
				drawChild(canvas, textView_titles[i], getDrawingTime());
				textView_titles[i].setTextColor(Color.BLACK);
				textView_titles[i].setTextSize(v_fFontSize/MainActivity.densityPer*1.2f);
			}	
		}
		
//		Log.e("Ks_EventList->dispatchDraw>>"," into��������");  
//		new thread().run();
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_EventList->onLayout","into"); 
		table.notifyTableLayoutChange(0, 20, v_iWidth, v_iHeight);
		
		if(table.m_bUseTitle==false){
			int title_Height=18;
			 for(int i=0;i<textView_titles.length;i++){
				 textView_titles[i].layout(i*v_iWidth/textView_titles.length, 0, 
						      (i+1)*v_iWidth/textView_titles.length, v_iPosY+title_Height);
			 }
		}
		 
		
	}
	//��д�����¼�onTouchEvent()  ��Ҫ����view add�������ϲ���ʹ�ã�����Ҫ��ͼ���ϲ�
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);         //�ñ��ؼ���Ӱ�� ���� �����˴�����Ӧ
//		Log.e("Ks_EventList-onTouchEvent","into");
		//invalidate();   //֪ͨ��ǰview �ػ����Լ�
		return false;
	}
	//����Layout() ����ؼ��װ�Layout��Сλ�û��ƺ���     
	public void doLayout(boolean bool, int l, int t, int r, int b){
//		Log.e("Ks_EventList->doLayout","into");
		
		this.layout(v_iPosX, v_iPosY, v_iPosX+v_iWidth, v_iPosY+v_iHeight); //���Ƹ�view�װ�layout
		 
	}
	
	//����invalidate() �ؼ�����->onDraw()���ú���
	public void doInvalidate(){

//			Log.e("Ks_EventList-doInvalidate","table.update");
			table.update();  //���±������  
		    //this.invalidate();
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
		
		if(expression==null) return false;
//		if(isCanUpdateViewFlag1==false)  return false;

		List<Event> event_lst = new ArrayList<Event>();
		event_lst.clear();
		//�ж��ǻ�ȡ�����豸�澯�б� �������и澯��Ϣ�б�
		if(expression.equip_ExId == 0){ //��ȡ���и澯�����б�		
			HashMap<Integer,List<Event>> allEvent_ht = NetDataModel.getAllEvent(); 	
			if(allEvent_ht == null) return false;
			Iterator<Integer> equiptId_lst = allEvent_ht.keySet().iterator();
			while(equiptId_lst.hasNext()){
				int e_id = equiptId_lst.next();
				List<Event> itemEvent_lst = allEvent_ht.get(e_id);
				if(itemEvent_lst == null) continue;
				for(int i=0;i<itemEvent_lst.size();i++){
					event_lst.add(itemEvent_lst.get(i));
				}
			}
		}else{  //��ȡĳ���豸�澯�б�
			event_lst = NetDataModel.getHtEvent(expression.equip_ExId); 
		}
		nowTime = java.lang.System.currentTimeMillis(); 
		if(event_lst==null || event_lst.size()==0){

			if(nowTime-oldTime > 3800){ //3s���� ~~4s��ˢ��һ�� 
				oldTime = nowTime;
				lstContends.clear();
				table.updateContends(lstTitles,lstContends);  //�����ͷ ��������� ������  �˴����ڴ����������
				return true;
			}else{
				return false;
			}
		}
		
		isCanUpdateViewFlag1 = false;

		lstContends.clear(); 
		for(int i=0; i<event_lst.size(); i++){
			List<String> lstRow = new ArrayList<String>();
			lstRow.clear();
			Event event = event_lst.get(i);
			if(event==null) continue;
			lstRow.add(event.equipName);  //�豸����
			lstRow.add(event.name);       //�澯����
			lstRow.add(event.meaning);    //�澯����
			lstRow.add( String.valueOf(event.grade) );      //�澯�ȼ�
			lstRow.add(UtTable.getDate(event.starttime * 1000, "yyyy.MM.dd HH:mm:ss"));  //��ʼʱ�� 
		//	Log.i("Ks_EventList>>updataValue>>",event.equipName+
		//						"  "+event.name+"  "+event.meaning);
			lstContends.add(lstRow);  //��ӱ���� 
		}
		table.updateContends(lstTitles,lstContends);  //�����ͷ ��������� ������  �˴����ڴ����������		

		if(nowTime-oldTime > 3800){ //3s���� ~~4s��ˢ��һ�� 
			oldTime = nowTime;
			return true;
		}else{
			return false;
		}
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

			  else if ("OddRowBackground".equals(strName)) 
				 table.m_cOddRowBackground = Color.parseColor(strValue); 
			  else if ("EvenRowBackground".equals(strName)) 
				  table.m_cEvenRowBackground = Color.parseColor(strValue); 
			 
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
		     else if ("ForeColor".equals(strName)){  //ǰ��ɫ �� ������ɫ
		    	 	table.m_cFontColor = Color.parseColor(strValue); 
		    	 	v_iFontColor = Color.parseColor(strValue); 
		     }
		  //   else if ("FontColor".equals(strName)) 
		  //      	v_iFontColor = Color.parseColor(strValue); 
		     else if ("BackgroundColor".equals(strName)) //����ɫ
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
		
		//�澯�б�   ֻ�ᵥ��  ��������
		if(bindExpression==null) return false;
		String str_bindItem = bindExpression.itemBindExpression_lst.get(0);
		List<Expression> expression_lst = bindExpression.itemExpression_ht.get(str_bindItem);
		expression = expression_lst.get(0);
		
		return true;
	}

}
