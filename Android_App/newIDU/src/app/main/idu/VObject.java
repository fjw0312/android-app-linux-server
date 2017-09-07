package app.main.idu;

import app.main.idu.Page;
import android.view.MotionEvent;
import android.view.View;

//�Զ���ؼ�view �Ľӿ���
public interface VObject {
	//��д��������ͼ
//	protected void dispatchDraw(Canvas canvas);
	//��д�����ӿؼ��ĵװ�λ��
//	protected void onLayout(boolean bool, int l, int t, int r, int b);
	//��д��������¼�
	public boolean onTouchEvent(MotionEvent event);
	//���Ƹÿؼ��װ�λ��
	public void doLayout(boolean bool, int l, int t, int r, int b);
	//������ͼ�����ػ�
	public void doInvalidate();
	//������ͼλ���ػ�
//	public void doRequestLayout();
	
	//����views��ӵ�����ͼ����
	public boolean doAddViewsToWindow(Page window);
	
	//��ȡ����ͼviews
	public View getViews();
	//��ȡ�ÿؼ�ID
	public String getViewsID();
	//��ȡ�ÿؼ�����
	public String getViewsType();
	//��ȡ�ÿؼ�ͼ��
	public int getViewsZIndex();
	//��ȡ�ÿؼ��󶨱��ʽ
	public String getViewsExpression();
	//��ȡ�ؼ��Ƿ���Ҫ����
	public boolean getNeedUpdateFlag();
	
	//���ÿؼ�ID
	public boolean setViewsID(String strID);
	//���ÿؼ�����
	public boolean setViewsType(String strType);
	//���ÿؼ�ͼ��
	public boolean setViewsZIndex(int n);
	//���ÿؼ��󶨱��ʽ
	public boolean setViewsExpression(String strExpression);
	//���ÿؼ��Ƿ���Ҫ����
	public boolean setNeedUpdateFlag(boolean b_flag);
	
	//���ý����ؼ�����
	public boolean setProperties(String strName,String strValue, String path);
	//���ÿؼ��ڲ�����
	public boolean setGravity();
	
	//�ؼ����ݸ��´�����
	public boolean updataValue(String strValue);
	//�󶨱��ʽ�Ľ���
	public boolean parseExpression(String str_bindExpression);
}
