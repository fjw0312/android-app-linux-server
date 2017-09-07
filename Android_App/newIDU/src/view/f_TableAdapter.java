package view;
import java.util.ArrayList;
import java.util.List;  
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class f_TableAdapter extends BaseAdapter {  
//************************************************Table��������д����
    public f_TableAdapter(Context context) {
        this.context = context;
        this.table = new ArrayList<TableRow>();
    }
    
    @Override  
    public int getCount() {
    	return m_nWaterMarker;
    }
    
    @Override  
    public long getItemId(int position) {
        return position;
    }
    
    public TableRow getItem(int position) {
        return table.get(position);
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	try{
		if (null == convertView)
		{
			TableRow tableRow = table.get(position);
			return new TableRowView(this.context, tableRow);
		}
		
		((TableRowView) convertView).updatavalue(table.get(position));
    	}catch(Exception e){
    		
    	}
		return convertView;
    }
  //************************************************Table�����
    public void addRow(TableRow row) {
    	table.add(row);
    }
    //************************************************Table�������
    public void cleanUp() {
    	table.clear();
    }
    
    /** 
     * TableRowView ʵ�ֱ���е���ʽ   �Ա���е�λ����ʽ��һ����Ҫ����
     * @author  
     */  
	class TableRowView extends LinearLayout
	{
		public TableRowView(Context context, TableRow tableRow)
		{
			super(context);
			this.setOrientation(LinearLayout.HORIZONTAL);
			
			mlstCellView = new ArrayList<TextView>();
			for (int i = 0; i < tableRow.getSize(); i++)  
			{
				// �����Ԫ��ӵ���   �����յ�Ԫ��Ա�ĳ�������뵥Ԫ���     �װ��λ�ò���
				TableCell tableCell = tableRow.getCellValue(i);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tableCell.width, tableCell.height);// ���ո�Ԫָ���Ĵ�С���ÿռ�
				layoutParams.setMargins(0, 0, 1, 1);// Ԥ����϶����߿�
				
				if (tableCell.type == TableCell.STRING)  
				{
					// �����Ԫ���ı�����  �ı���Ա��λ����ʽ������� 
					TextView textCell = new TextView(context);
					textCell.setTextColor(m_cTexColor);
					textCell.setGravity(Gravity.CENTER);
					textCell.setBackgroundColor(tableCell.cRowColor);
					textCell.setText(String.valueOf(tableCell.value));
					addView(textCell, layoutParams);
					mlstCellView.add(textCell);
				} else if (tableCell.type == TableCell.IMAGE)
				{
					// �����Ԫ��ͼ������
					ImageView imgCell = new ImageView(context);
					imgCell.setBackgroundColor(Color.GRAY);
					imgCell.setImageResource((Integer) tableCell.value);
					addView(imgCell, layoutParams);
				}
			}
			
			this.setBackgroundColor(Color.WHITE);// ������ɫ�����ÿ�϶��ʵ�ֱ߿�
		}
		
		//���±������������
		public void updatavalue(TableRow tableRow)
		{
			int count = tableRow.getSize();
			for (int i = 0; i < count; i++)
			{
				TableCell tableCell = tableRow.getCellValue(i);
				TextView cellview = mlstCellView.get(i);
				cellview.setBackgroundColor(tableCell.cRowColor);
				cellview.setText(String.valueOf(tableCell.value));
			}
		}
		
		private List<TextView> mlstCellView = null;
	}

    /** 
     * TableRow ʵ�ֱ�����    ��̬����� ������
     * @author  
     */  
	static public class TableRow
	{
		private TableCell[] cell;

		public TableRow(TableCell[] cell)
		{
			this.cell = cell;
		}

		public int getSize() {
			return cell.length;
		}

		public TableCell getCellValue(int index) {
			if (index >= cell.length)
				return null;
			return cell[index];
		}
	}
    /** 
     * TableCell ʵ�ֱ��ĸ�Ԫ   ��̬���Ԫ��Ա�Ķ�����
     * @author  
     */  
	static public class TableCell
	{
		static public final int STRING = 0;
		static public final int IMAGE = 1;
		public Object value;  
		public int width;
		public int height;
		public int type; 
		public int cRowColor;

		public TableCell(Object value, int width, int height, int type, int rowcolor)
		{
			this.value = value;
			this.width = width;
			this.height = height;
			this.type = type;
			this.cRowColor = rowcolor;
		}
	}
    
    private Context context;
    public int m_cTexColor = Color.GREEN;
    public float m_fTextSize = 20;  
    
    // �洢������ݡ� Attention: ��СӦֻ��������
    private List<TableRow> table;

    // ����ˮλ��������ʾ������
    public int m_nWaterMarker = 0;
} 