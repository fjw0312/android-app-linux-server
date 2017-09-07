package utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


//绑定表达式类 Binding{[Value[Equip:127-Temp:179-Signal:8]]}
public class BindExpression {

	public BindExpression() {
		// TODO Auto-generated constructor stub
		itemBindExpression_lst = new ArrayList<String>();
		itemExpression_ht = new Hashtable<String, List<Expression>>();
	}
	
	public String bindExpressionContext = "";
	public List<String> itemBindExpression_lst = null;
	public Hashtable<String, List<Expression>> itemExpression_ht = null;
	
	//获取绑定 表达式的 子项 表达式数组[Value[Equip:127-Temp:179-Signal:8]] or
	//[Value[Equip:127-Temp:179-Signal:8]]+[Value[Equip:127-Temp:179-Signal:9]] or
	//[Value[Equip:127-Temp:179-Signal:8]]+(4) 
    public int getBindExpression_ItemLst(String str_bindExpression){	
    	 if("".equals(str_bindExpression)) return 0;
    	 removeBind(str_bindExpression); //去除绑定表达式的Binding{ }
    	 if("".equals(bindExpressionContext)) return 0;
    	
    	 String[] s_bindExpressionItem = bindExpressionContext.split("\\|");
    	 for(int i=0; i<s_bindExpressionItem.length; i++){    		
    		 itemBindExpression_lst.add(s_bindExpressionItem[i]);    		
    		 getItemItemExpression(s_bindExpressionItem[i]); //将子项([value[***]]+40) 在解析 出表达式的 子子项
    	 }
    	
    	return itemBindExpression_lst.size();
    }
    //获取 子项表达式中 运算子项 链表  如果单项表达式 返回true  [value[***]] +40 + +(60+30)
    public boolean getItemItemExpression(String itemExpression){
    	if("".equals(itemExpression)) return true;
    	List<String> str_lst = new ArrayList<String>();
    	List<Expression> expression_lst = new ArrayList<Expression>();
    	
    	//将每个设备表达式 非设备表达式 提取为子项
    	if( itemExpression.contains("Value") ){ //判断是否为数值类型  可能存在 数值运算   		
    		int index_star = 0;
    		int index_end = 0;
    		for(int i=0;i<itemExpression.length()-1;i++){
    			char c1 = itemExpression.charAt(i);
    			char c2 = itemExpression.charAt(i+1);
    			if( (c1 == '[')&&(c2 == 'V') ){    				
    				index_star = i;
    				String item = itemExpression.substring(index_end, index_star);    				
    				if("".equals(item) == false){
    					str_lst.add(item);
    		//			Log.i("BindExpression>getItemItemExpression>>1 add-item", item);
    				}
    			}else if((c1==']') && (c2==']') ){
    				index_end = i+2;
    				String item = itemExpression.substring(index_star, index_end);				
    				if("".equals(item) == false){
    					str_lst.add(item);
    		//			Log.i("BindExpression>getItemItemExpression>>2 add-item", item);	
    				}
    			}
    		}
			String item = itemExpression.substring(index_end, itemExpression.length());	
			if("".equals(item) == false){
				str_lst.add(item);
			//	Log.i("BindExpression>getItemItemExpression>>3 add-item", item); 
			}
    	}else{
    		str_lst.add(itemExpression);
    	}
    	
    	//遍历 子项绑定  的子项表达式  字符与类的转换
    	for(int i=0; i<str_lst.size(); i++){
    		Expression expression = new Expression();
    		expression.parseExpression(str_lst.get(i));
    		expression_lst.add(expression);
    	}
    	str_lst.clear();
    	str_lst = null;
    		
    	itemExpression_ht.put(itemExpression, expression_lst);
    	
    	return true;
    }

	//去除绑定表达式的Binding{ }
	public String removeBind(String str_bindExpression){
		if("".equals(str_bindExpression)) return "";
		
		String[] s_Str = str_bindExpression.split("\\{");
		if (s_Str.length <= 1)
			return "";
		String Str = s_Str[1];
		s_Str = Str.split("\\}");
		if (s_Str.length < 1)
			return "";
		bindExpressionContext = s_Str[0];
		
		return bindExpressionContext;
	}

}
