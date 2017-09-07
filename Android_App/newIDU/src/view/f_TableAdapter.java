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
//************************************************Table适配器重写部分
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
  //************************************************Table添加行
    public void addRow(TableRow row) {
    	table.add(row);
    }
    //************************************************Table清除内容
    public void cleanUp() {
    	table.clear();
    }
    
    /** 
     * TableRowView 实现表格行的样式   对表格行的位置样式进一步按要求处理
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
				// 逐个格单元添加到行   将按照单元成员的长宽高填入单元格的     底板和位置参数
				TableCell tableCell = tableRow.getCellValue(i);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tableCell.width, tableCell.height);// 按照格单元指定的大小设置空间
				layoutParams.setMargins(0, 0, 1, 1);// 预留空隙制造边框
				
				if (tableCell.type == TableCell.STRING)  
				{
					// 如果格单元是文本内容  文本成员按位置样式参数添加 
					TextView textCell = new TextView(context);
					textCell.setTextColor(m_cTexColor);
					textCell.setGravity(Gravity.CENTER);
					textCell.setBackgroundColor(tableCell.cRowColor);
					textCell.setText(String.valueOf(tableCell.value));
					addView(textCell, layoutParams);
					mlstCellView.add(textCell);
				} else if (tableCell.type == TableCell.IMAGE)
				{
					// 如果格单元是图像内容
					ImageView imgCell = new ImageView(context);
					imgCell.setBackgroundColor(Color.GRAY);
					imgCell.setImageResource((Integer) tableCell.value);
					addView(imgCell, layoutParams);
				}
			}
			
			this.setBackgroundColor(Color.WHITE);// 背景白色，利用空隙来实现边框
		}
		
		//更新表格行内容数据
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
     * TableRow 实现表格的行    静态表格行 对象类
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
     * TableCell 实现表格的格单元   静态表格单元成员的对象类
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
    
    // 存储表格数据。 Attention: 大小应只增不减。
    private List<TableRow> table;

    // 设置水位概念，标记显示行数。
    public int m_nWaterMarker = 0;
} 