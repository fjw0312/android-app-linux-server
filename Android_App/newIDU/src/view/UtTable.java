package view;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ListView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import view.f_TableAdapter.TableCell;
import view.f_TableAdapter.TableRow;

@SuppressLint("SimpleDateFormat")
public class UtTable extends ListView {
	public UtTable(Context context) {   
        super(context); 
        m_tableAdapter = new f_TableAdapter(this.getContext()); 
        this.setAdapter(m_tableAdapter); 
    }
	
	public void notifyTableLayoutChange(int l, int t, int r, int b) {
		m_nLeft = l;
		m_nTop = t;
		m_nRight = r; 
		m_nBottom = b; 

		m_tableAdapter.notifyDataSetChanged();
		this.layout(m_nLeft, m_nTop, m_nRight, m_nBottom);
	}
	//**********************update table��layout
	public void update()
	{
		m_tableAdapter.notifyDataSetChanged();
		this.layout(m_nLeft, m_nTop, m_nRight, m_nBottom - m_nLayoutBottomOffset);
		m_nLayoutBottomOffset = -m_nLayoutBottomOffset;
	}
	
	//********************* update table 
	public void updateContends(List<String> listTitles, List<List<String>> listContends)
	{
		//Log.e("UtTable��>updateContends>>","into����"); 
		if (listTitles == null || listContends == null) return;	
		
		//updatecount =  ����  
		int column = listTitles.size();                 //�ɱ�ͷ �ó� ���� 
		int width = (m_nRight - m_nLeft) / column;		
		int updatecount = m_bUseTitle ? listContends.size() + 1 : listContends.size();		
		int count = Math.min(m_tableAdapter.getCount(), updatecount);        //�ȽϺ�֮ǰ���ظ�����
		//Log.e("UtTable��>updateContends>>updatecount="+String.valueOf(updatecount),"count="+String.valueOf(count));
	
		//updatecount =  ����  (������ͷ��)
		//count =  ǰ��2���ظ�����  (������ͷ��) 
		int sta = 0;
		if(count == 0) sta = 1;
		if(listContends.size()==0){
			m_tableAdapter.cleanUp(); 
			sta = 1;
		}
		
		// �������
		if (m_bUseTitle && sta == 1)  //ֻ�г��α��ˢ�� �� �����ݱ�����ʱ
	//	if (m_bUseTitle)
		{
			// �б��������ݣ� ������⡣		
	//		m_tableAdapter.m_cTexColor = m_cFontColor; //��ͷ������ɫ
			m_tableAdapter.m_cTexColor = Color.RED; //��ͷ������ɫ   Ŀǰ�˴��޸���Ч
			int c_Title = m_cHeadBackgroundColor;   //��ͷ�װ���ɫ  
	
//			Log.e("UtTable��>updateContends>>","into------1111111����");  
			TableCell[] titles = new TableCell[listTitles.size()];
			for (int i = 0; i < column; i++)
			{				
				titles[i] = new TableCell(listTitles.get(i), width, LayoutParams.MATCH_PARENT, TableCell.STRING, c_Title);			
			}
			m_tableAdapter.addRow(new TableRow(titles));			
			titles = null;
		}
		
		// �����������������---�ظ��� ���ݸ���
		int k = m_bUseTitle?1:0 ;
		for (int i=k; i < count; i++)
		{		
				List<String> lst = listContends.get(i-k);
				TableRow contendRow = m_tableAdapter.getItem(i); 			
				for (int j = 0; j < contendRow.getSize(); ++j)
				{
					TableCell cell = contendRow.getCellValue(j);
					cell.value = lst.get(j);
					cell.width = width;
		//			Log.e("UtTable->updateContends>>1>>", "cell.value :"+cell.value);
				}
		}


		// ������������   // fjw ���������ݴ���
	//	if (m_tableAdapter.getCount() < updatecount)
		if (count < updatecount)
		{
			int i = m_bUseTitle?count-1:count ;
			int lstcontendsize = listContends.size();
			m_tableAdapter.m_cTexColor = m_cFontColor; 
			
			if (i < 0) i = 0; 
			for (; i < lstcontendsize; i++)
			{
				List<String> lst = listContends.get(i);
			//	Log.e("UtTable-updateContends->if", "listContends.get(i):"+String.valueOf(lst));
				TableCell[] cells = new TableCell[listTitles.size()];
				int cColor = i % 2 == 0 ? m_cEvenRowBackground : m_cOddRowBackground;
				for (int j = 0; j < lst.size(); ++j)
				{
					cells[j] = new TableCell(lst.get(j), width, LayoutParams.MATCH_PARENT, TableCell.STRING, cColor);
				}

				m_tableAdapter.addRow(new TableRow(cells));
				cells = null;
			}
		}

		// ����ˮλ
		m_tableAdapter.m_nWaterMarker = updatecount;
	}
	
	
	
	public void setFontColor(int cColor)
	{
		m_cFontColor = cColor;
	}
	
	public static String getDate(long milliSeconds, String dateFormat)
	{
		if (0 == milliSeconds)
			return ""; // 0 ��ʾδ��ȡ����Чʱ�� 

		DateFormat formatter = new SimpleDateFormat(dateFormat);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return new String(formatter.format(calendar.getTime()));
	}
	
	
	// params :
	protected f_TableAdapter m_tableAdapter = null;
	
	protected int m_nLeft = 0;
	protected int m_nTop = 0;
	protected int m_nRight = 0;
	protected int m_nBottom = 0;
	
	protected int m_nTableWidth = 0;
	protected int m_nTableHeight = 0;
	
	public int m_cFontColor = Color.GREEN;   //ǰ��ɫ ������ɫ
	public int m_fFontSize = 20;             //�����С
	public int m_cOddRowBackground = 0xFF000000; // ����
	public int m_cEvenRowBackground = 0xFF000000; // ż��
	public int m_cHeadBackgroundColor = 0x00000000; //��ͷ�װ���ɫ
	public boolean m_bUseTitle = true;
	int m_nLayoutBottomOffset = 1;	// ��̬����layout��С
}
