package app.main.idu;

import app.main.idu.Page;
import android.view.MotionEvent;
import android.view.View;

//自定义控件view 的接口类
public interface VObject {
	//重写绘制子视图
//	protected void dispatchDraw(Canvas canvas);
	//重写绘制子控件的底板位置
//	protected void onLayout(boolean bool, int l, int t, int r, int b);
	//重写点击触摸事件
	public boolean onTouchEvent(MotionEvent event);
	//绘制该控件底板位置
	public void doLayout(boolean bool, int l, int t, int r, int b);
	//更新视图触发重绘
	public void doInvalidate();
	//更新视图位置重绘
//	public void doRequestLayout();
	
	//将该views添加到父视图容器
	public boolean doAddViewsToWindow(Page window);
	
	//获取该视图views
	public View getViews();
	//获取该控件ID
	public String getViewsID();
	//获取该控件类型
	public String getViewsType();
	//获取该控件图层
	public int getViewsZIndex();
	//获取该控件绑定表达式
	public String getViewsExpression();
	//获取控件是否需要更新
	public boolean getNeedUpdateFlag();
	
	//设置控件ID
	public boolean setViewsID(String strID);
	//设置控件类型
	public boolean setViewsType(String strType);
	//设置控件图层
	public boolean setViewsZIndex(int n);
	//设置控件绑定表达式
	public boolean setViewsExpression(String strExpression);
	//设置控件是否需要更新
	public boolean setNeedUpdateFlag(boolean b_flag);
	
	//设置解析控件参数
	public boolean setProperties(String strName,String strValue, String path);
	//设置控件内部布局
	public boolean setGravity();
	
	//控件数据更新处理函数
	public boolean updataValue(String strValue);
	//绑定表达式的解析
	public boolean parseExpression(String str_bindExpression);
}
