package UIs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import SAM.extraHisModel.HisDataDAO;
import SAM.extraHisModel.HisEvent;
import app.main.idu.MainActivity;
import app.main.idu.Page;
import app.main.idu.VObject;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemSelectedListener;


//�Զ���ؼ���ʷ�澯�б�  �ؼ�
//ע�⣬���ڱ����Ҫ�����ݱ��浽�������
@SuppressLint("HandlerLeak")
public class Ks_zHisEventList extends ViewGroup implements VObject{

	public Ks_zHisEventList(Context context) {
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
//		lstTitles.add("�ź�����");			
		lstTitles.add("�澯����");
		lstTitles.add("�澯����");
		lstTitles.add("�ź���ֵ");
		lstTitles.add("�澯�ȼ�");
		lstTitles.add("��ʼʱ��");
		lstTitles.add("����ʱ��");
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
		//�ź�����ʾtext	
			view_text = new TextView(context);
			view_text.setTextColor(Color.BLACK);
			view_text.setText("  �豸��   ");  //��Ϊ����
			view_text.setTextSize(16/MainActivity.densityPer);
			view_text.setGravity(Gravity.CENTER);
			view_text.setBackgroundColor(Color.argb(100, 100, 100, 100));
			this.addView(view_text);
			
			//����ѡ��button
			view_timeButton = new Button(context);
			view_timeButton.setText("��������");   // Set Time
			view_timeButton.setTextColor(Color.BLACK);
			view_timeButton.setTextSize(16/MainActivity.densityPer);
			view_timeButton.setPadding(2, 2, 2, 2);
			view_timeButton.setOnClickListener(l);//���øÿؼ��ļ���
			this.addView(view_timeButton);
			//ǰһ��button
			view_PerveDay = new Button(context);	
			view_PerveDay.setText("ǰһ��");  // PreveDay
			view_PerveDay.setTextColor(Color.BLACK);
			view_PerveDay.setTextSize(16/MainActivity.densityPer);
			view_PerveDay.setPadding(2, 2, 2, 2);		
			view_PerveDay.setOnClickListener(l);//���øÿؼ��ļ���	
			this.addView(view_PerveDay);
			//��һ��button
			view_NextDay = new Button(context);	
			view_NextDay.setText("��һ��");  // NextDay
			view_NextDay.setTextColor(Color.BLACK);
			view_NextDay.setTextSize(16/MainActivity.densityPer);	
			view_NextDay.setPadding(2, 2, 2, 2);
			view_NextDay.setOnClickListener(l);//���øÿؼ��ļ���
			this.addView(view_NextDay);
			//����receive
			view_Receive = new Button(context);		
			view_Receive.setText("  ��ȡ   ");
			view_Receive.setTextColor(Color.BLACK);
			view_Receive.setTextSize(16/MainActivity.densityPer);
			view_Receive.setPadding(2, 2, 2, 2);		
			view_Receive.setOnClickListener(l);	//���øÿؼ��ļ���	
			this.addView(view_Receive);
			//�������öԻ���
			calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			dialog = new DatePickerDialog(context, new OnDateSetListener() {			
				@Override
				public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub		
					num = 0;  //�����Ӽ���Ŧ����
//					String text = year + "-" + month +"-" + day;				
//					Log.i("LocalList-OnDateSetListener ѡ���������:", text);
				}
			}, year, month, day);
			//�ź���ѡ��spinner
			view_EquiptSpinner = new Spinner(context);//�ź������б�ؼ�
			adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item); 
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			view_EquiptSpinner.setAdapter(adapter);
			adapter_lst = new ArrayList<String>();
			adapter.add("  �豸��   ");	
			map_EquiptNameList = new HashMap<String, Integer>();
			view_EquiptSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {	
				 public void onItemSelected(			
		                    AdapterView<?> parent, View view, int position, long id) {
					 if(NetDataModel.hm_xmlEquiptCfgModel == null ) return;
					 if(NetDataModel.lst_poolEquipmentId == null ) return;
					 Iterator<Integer>id_lst = NetDataModel.lst_poolEquipmentId.keySet().iterator(); //���������б��Ա
			         while(id_lst.hasNext()){
			        	 int e_id = id_lst.next();
			        	 String e_name = NetDataModel.getEquipmentCfg(e_id).EquipTemplateName;
			        	 adapter_lst.add(e_name);
			        	 adapter.add(e_name);
			        	 if(e_name != null)
			        		 map_EquiptNameList.put(e_name, e_id);
			         }			            	
		            }

		            public void onNothingSelected(AdapterView<?> parent) {
		            	Log.e("HisEvent->view_SignalSpinner-OnClickListener","into onNothingSelected");
		            }
			});
			this.addView(view_EquiptSpinner);

		 
		//�����б�������  ��Ҫ���ã�����⵽�л���click1=false  ֹͣ������click1=true;
			this.table.setOnScrollListener(new OnScrollListener() {	
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					// TODO Auto-generated method stub
					switch(arg1){
					case OnScrollListener.SCROLL_STATE_IDLE:  //����Ļֹͣ����
						slideFlag=true;
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"��Ļֹͣ������");
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //��Ļ���� 
						slideFlag=false;
						Log.i("Ks_EventList->onScrollStateChanged>>", v_strID+"��Ļ����!");
						break;
					case OnScrollListener.SCROLL_STATE_FLING:   //��ָ�뿪����Ļ���Թ���
						slideFlag=false;
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
	String v_strType = "HisEventList";           //�ؼ�����
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
		TextView view_text;		            //�ź�����ʾtext		
		Spinner view_EquiptSpinner = null; 		//�豸��ѡ��spinner
		Button  view_timeButton;		        //����ѡ��button
		Button  view_PerveDay;		            //ǰһ��button
		Button  view_NextDay;		            //��һ��button
		Button  view_Receive;		            //����receive
		
		private  DatePickerDialog  dialog;  //���ڶԻ���ѡ��Ӧ��
		private int year,month,day;   //�Ի�����ʾ�������ձ���
		private Calendar calendar;
		private int flag = 0;
		private int num = 0; //�Ӽ���Ŧ�Ӽ���	
		public  String get_day="";   //��Ҫ��ȡ���ݵ�����
		
		private HashMap<String,Integer> map_EquiptNameList = null;  //<�豸��-�豸id>
		private  ArrayAdapter<String> adapter = null;
		private String closeEquiptName = "";
		public static int str_EquiptId = -1; //����Ҫ���豸-�ź�id�ַ���	
		List<String> adapter_lst = null;
		
		private boolean slideFlag = true; // ��� ���� ��־
		private boolean threadDie = true; //�߳� �Ƿ� ������־

	//��������
	List<String> lstTitles = null;  //�������Ա�ַ�����
	List<List<String>> lstContends = null; //����ַ�����Ա����
	BindExpression bindExpression = null;  //�󶨴�����
	int bindExpressionItem_num = 0;     //������ �ĸ���      �ź��б� ֻ�������
	Expression expression = null; //���ʽ������
	int equiptId = -1;


	boolean isCanUpdateViewFlag1 = true;
	boolean isCanUpdateViewFlag2 = true;
	long nowTime = 10000; //10s
	long oldTime = 0;
	
	boolean canVISIBLE = false;  //�жϿؼ��Ƿ�ɼ�  ��־����
	
	
	//������ view_Receive
	private OnClickListener l = new OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					Log.e("HisEvent-OnClickListener1", "into");   //��������
					// TODO Auto-generated method stub		
					
					//��ȡ���õ�����
					int set_year = dialog.getDatePicker().getYear();
					int set_month = dialog.getDatePicker().getMonth()+1;
					int set_day = dialog.getDatePicker().getDayOfMonth();
					
					if(slideFlag==false){
						Toast.makeText(Ks_zHisEventList.this.getContext(), "������Ļ�С����������Ժ�", Toast.LENGTH_SHORT).show();
						return;
					}
					//�ж���һ����������	
					if(arg0 == view_timeButton){
						dialog.show();  //�������ڶԻ���
						flag = 1;	
						num = 0;
						return;
					}else if(arg0 == view_Receive){
						num = 0;
					}else if(arg0 == view_NextDay){
						num++;	
						set_day = set_day + num; //������һ�죻 num������֮��
						//�жϲ�������������
						long now_time = java.lang.System.currentTimeMillis();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ʱ���ʽת��
						Date date = new Date(now_time);
						String sampletime = formatter.format(date);
						String now_year = sampletime.substring(0, 4);
						String now_month = sampletime.substring(5, 7);
						String now_day = sampletime.substring(8, 10);
						int int_now_year = Integer.valueOf(now_year);
						int int_now_month = Integer.valueOf(now_month);
						int int_now_day = Integer.valueOf(now_day);							 
						//��ĩ�ж�
						if(set_day > 31){
								set_day = set_day-31;
								set_month++;
								if(set_month>12){
									set_month = 1;
									set_year++;
								}
						}
						if(set_day < 1){
								set_day = set_day+31;
								set_month--;
								if(set_month<1){
									set_month = 12;
									set_year--;
								}
						}
						if((set_year==int_now_year)&&(set_month==int_now_month)&&(set_day > int_now_day)){								 
								 set_day = int_now_day;
								 num--;
						}					
				     }else if(arg0 == view_PerveDay){
					
							num--;  //num������֮��
							set_day = set_day + num; //������1��
							if(set_day < 1){
								set_day = 31+set_day;
								set_month--;
								if(set_month<1){
									set_month = 12;
									set_year--;
								}
							}if(set_day > 31){
								set_day = set_day-31;
								set_month++;
								if(set_month>12){
									set_month = 1;
									set_year++;
								}
							}
							
						}
						//�����·������ַ���ʽ
						String str_day;
						String str_nomth;
						if(set_day<10)
						{
							str_day = "0"+String.valueOf(set_day);
						}else{
							str_day = String.valueOf(set_day);
						}
						if(set_month<10)
						{
							str_nomth = "0"+String.valueOf(set_month);
						}else{
							str_nomth = String.valueOf(set_month);
						}
						get_day = String.valueOf(set_year)+"-"+str_nomth+"-"+str_day;
									
						if("".equals(get_day)) return;
										
						//��ʾ��ѡ����źŵ�����						
						closeEquiptName = (String) view_EquiptSpinner.getSelectedItem();
						view_text.setText(closeEquiptName);	
						if("  �豸��   ".equals(closeEquiptName))  return;
						str_EquiptId = map_EquiptNameList.get(closeEquiptName);
						Log.e("HisEvent-OnClickListener��ȡ���豸id:",str_EquiptId+"---"+closeEquiptName+"---");							
						
						if(threadDie){
							myThread thread = new myThread();
							thread.start(); 						
						}else{
							Toast.makeText(Ks_zHisEventList.this.getContext(), "���ڼ������ݣ����Ժ�", Toast.LENGTH_SHORT).show();
						}

		}
	};
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				doInvalidate();//���¿ؼ� ���
				Log.e("ks_HisEvent>>myhandler>>update():", "���ˢ��ִ�У�");
				break;
			default: break;
			}
		}
		
	};
	//���а�ť ����ʱ֪ͨuiˢ���߳�
	private class myThread extends Thread{	
		
		@SuppressWarnings("unused")
		@Override
		public void run() { 
			// TODO Auto-generated method stub
			try{
				threadDie = false;
				String fileName = "hisevent-"+String.valueOf(str_EquiptId);
				if(false){  //�°汾·��  �ļ���
					fileName = "hisevent-"+String.valueOf(str_EquiptId)+"-"+closeEquiptName;
				}
				
				if(HisDataDAO.getHisEquipEventList(fileName)){  //��ȡ  ��ʷ �豸 ����
					if( update() ) 
						myHandler.sendEmptyMessage(1);
					Thread.sleep(2000);  //2s ��ʱ ʹ���㹻��ʱ��ˢ�� ���
					Log.e("ks_HisEvent>>Thread>>fileName:",fileName);
				}
			}catch(Exception e){
				Log.e("ks_HisEvent->thread>>","�쳣�׳���");
			}
			threadDie = true;
		}
	}
	//������� ����ˢ��
	private boolean update(){
		lstContends.clear(); //���ҳ�����ǰ���� ���ź�  
		//��ȡ��ʷ�����豸�б�      ����豸����ʷ�����б� �б�<�豸��<��ʷ�źŽṹ��>>
		List<HisEvent> hisEvent_list = HisDataDAO.hisEvent_lst;
		//�������ݴ���  ȥ���ظ��ɼ��ĸ澯	
		List<String> key = new ArrayList();
		Hashtable<String,HisEvent> hast_his = new Hashtable<String,HisEvent>();
		for(int i=0; i<hisEvent_list.size(); i++){
			HisEvent his_event = hisEvent_list.get(i);
			if(his_event==null) return false;	
			boolean flag = true;
			//�ж��Ƿ��иø澯��ʼʱ���keyֵ���ź� �Ƿ��Ѿ����
			if(hast_his.containsKey(his_event.start_time+"#"+his_event.event_id)){
						flag = false;
						//���ж��Ƿ�Ϊͬһ���ź�
						if("1970-01-01".equals(his_event.finish_time.substring(0, 10)) )
							continue;
			}
			hast_his.put(his_event.start_time+"#"+his_event.event_id, his_event);	

			if(flag){
					key.add(his_event.start_time+"#"+his_event.event_id);
			}		
		}

		if(key == null||hast_his == null) return false;
			
		//��˳����ת
		List<String> key2 = null;
		key2 = new ArrayList<String>();
		key2.clear();
		for(int i=key.size()-1;i>=0; i--){
			key2.add(key.get(i));  
		} 
		
		//����
				lstContends.clear(); //���ҳ�����ǰ���� ���ź� 
				if(key2==null) return false; 
				Iterator<String> iterator_key = key2.iterator();
				while(iterator_key.hasNext()){ 
					String his_event_key = iterator_key.next();
					if(his_event_key==null||"".equals(his_event_key)) return false;
					HisEvent his_event = hast_his.get(his_event_key);
					if(his_event == null) return false;
				    List<String> lstRow_his = new ArrayList<String>();
				    lstRow_his.clear();
				    	//��ͨ���жϸ澯����ʱ�����ж�
				    	String finishTime = his_event.finish_time;
//				    	Log.e("HisEvent_updateValue->his_event_list����ȡʱ�䣺",finishTime);
				    	if(finishTime.length()<10) return false;
				    	//�ô�����debug����������
				    	if("1970-01-01".equals(finishTime.substring(0, 10))){  		    		  
				    		  finishTime = "null";	 
//				    		  continue;
				    	}
				    	//�����ȡ��ʱ�� ��ʷ�澯
				    	String startTime = his_event.start_time.substring(0, 10);//��ȡ������
				    	if(startTime.equals(get_day)==false){
				//    		Log.e("HisEvent_updateValue->his_event_list����ʼʱ�䣺",startTime);
				    		continue;
				    	}
				    	
//				      //�ظ���ǿ�ƴ���   	
//				    	if((lstContends!=null)||(lstContends.size()!=0)){
//					    	for(int i=0;i<lstContends.size();i++){
//						    	List<String> ls = lstContends.get(0);
//						    	String t_name = ls.get(0);
//						    	if(t_name.equals(closeEquiptName)==false)
//						    			lstContends.remove(i);
//					    	}
//				    	}
				    	String eventName = "";
				    	try{
				    	int equiptId = Integer.parseInt(his_event.equipid);
				    	int eventId = Integer.parseInt(his_event.event_id);
				    	his_event.event_name = his_event.event_name;
				    	}catch(Exception e){
				    		
				    	}
				    	
				    	lstRow_his.add(closeEquiptName);  //�豸����  			    
				    //	lstRow_his.add(name);    //�ź�����		    	
				    	lstRow_his.add(his_event.event_name);//�澯����
				    	lstRow_his.add(his_event.event_mean); //�澯����
				    	lstRow_his.add(his_event.value);     //�ź���ֵ
				    	lstRow_his.add(his_event.severity);    //�澯�ȼ� 
				    	lstRow_his.add(his_event.start_time); //��ʼʱ��
				    	lstRow_his.add(finishTime);//����ʱ��

				    	lstContends.add(lstRow_his);
				    
				 }
				table.updateContends(lstTitles, lstContends);
				lstContends.clear(); //���ҳ�����ǰ���� ���ź�
				hast_his.clear();	
				key.clear();
				key2.clear();
		return true;
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
				textView_titles[i].setTextSize(v_fFontSize/MainActivity.densityPer);
			}	
		}

		drawChild(canvas, view_Receive, getDrawingTime());
		drawChild(canvas, view_NextDay, getDrawingTime());
		drawChild(canvas, view_PerveDay, getDrawingTime());
		drawChild(canvas, view_timeButton, getDrawingTime());
		drawChild(canvas, view_EquiptSpinner, getDrawingTime());
		drawChild(canvas, view_text, getDrawingTime());
		
//		Log.e("Ks_EventList->dispatchDraw>>"," into��������"); 
//		new thread().run();
	}
	//��дonLayout() ����viewGroup�����е�view�װ�layout
	protected void onLayout(boolean bool, int l, int t, int r, int b) {
//		Log.e("Ks_EventList->onLayout","into"); 
		table.notifyTableLayoutChange(0, 48, v_iWidth, v_iHeight);
		
		if(table.m_bUseTitle==false){
			int title_Height=18;
			 for(int i=0;i<textView_titles.length;i++){
				 textView_titles[i].layout(i*v_iWidth/textView_titles.length, 30, 
						      (i+1)*v_iWidth/textView_titles.length, 30+title_Height);
			 }
		}
		
		int width_x = v_iWidth/5;
		view_EquiptSpinner.layout(0, 0, width_x*1-10, 30); 
		view_text.layout(0, 0, width_x*1-10, 30); 
		view_timeButton.layout(width_x*1, 0, width_x*2-10, 30);
		view_PerveDay.layout(width_x*2, 0, width_x*3-10, 30);
		view_NextDay.layout(width_x*3, 0, width_x*4-10, 30);
		view_Receive.layout(width_x*4, 0, width_x*5-10, 30);
		
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
		equiptId = expression.equip_ExId;
		
		return true;
	}

}
