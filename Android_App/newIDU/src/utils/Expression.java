package utils;

//kstar app 
//make by fang
//���ʽ  ��   ��������Value[Equip:127-Temp:179-Signal:8]
//������ ����ֻ���� ������ֵ�����ڵ�����
public class Expression {

	public Expression() {
		// TODO Auto-generated constructor stub
	}
	
	//Feilds
	public String type = "";   //��Ϊ���豸���ʽʱ ΪPara
	
	public int equip_ExId = -1;
	public int temp_ExId = -1;
	public int signal_ExId = -1;	
	public int event_ExId = -1;      //�澯����id ���� �澯���α��ʽ
	public int condition_ExId = -1;  //��������id ���� ����ֵ���ñ��ʽ
	public int command_ExId = -1;    //���� �������� ���ʽ
	public int parameter_ExId = -1;  //���� �������� ���ʽ
	public String value = "";     //ң�� ����Ҫ�ò���
	public String para = "";   // Ϊ���豸���ʽ ���ַ�����
	//�󶨱��ʽΪ�豸���͵�  ������
	//�󶨱��ʽΪ��ֵ���͵�  ������
	//�󶨱��ʽΪ�澯�ȼ����͵�  ������
	//�󶨱��ʽΪ�豸ͨ��״̬���͵�  ������
	//�󶨱��ʽΪ�澯ֵ�豸���͵�  ������
	//�󶨱��ʽΪ�����������ң�� ң�أ����͵�  ������

	
	//���������ʽ ������[Value[Equip:127-Temp:179-Signal:8]]  [Equip[Equip:1]] 
	public String parseExpression(String expression){
		if( "".equals(expression) ) return "";
		
       	String[] s_Express = expression.split("\\[");
       	if(s_Express.length<2){  //���� ���豸���ʽ
       		type = "Para";
       		para = expression;
       		return type;
       	}
       	type = s_Express[1];    	
    	String str_Exp = s_Express[2];
    	String[] s_Exp = str_Exp.split("\\]");
    	String str_value= s_Exp[0];
    	String[] para_Id = str_value.split("-");
    	for (int i = 0; i < para_Id.length; ++i) {
    		String[] s_Value = para_Id[i].split(":");
    		if (s_Value.length < 2)
    			continue;
    		if ("Equip".equals(s_Value[0])) 
    			equip_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Temp".equals(s_Value[0])) 
    			temp_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Signal".equals(s_Value[0])) 
    			signal_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Command".equals(s_Value[0])) 
    			command_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Parameter".equals(s_Value[0]))
    			parameter_ExId = Integer.parseInt(s_Value[1]);
    		else if ("Value".equals(s_Value[0])) 
    			value = s_Value[1];
    		else if ("Event".equals(s_Value[0])) 
    			event_ExId = Integer.parseInt(s_Value[1]); 
    		else if ("Condition".equals(s_Value[0])) {
    			condition_ExId = Integer.parseInt(s_Value[1]);
    		}
    	}
		
		return type;
	}

}
