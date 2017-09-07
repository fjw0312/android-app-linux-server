package utils;

import java.util.Collections;
import java.util.Stack;


//**made by kstar
public class Calculator {
    private Stack<String> postfixStack  = new Stack<String>();		// ��׺ʽջ
    private Stack<Character> opStack  = new Stack<Character>();		// �����ջ
    private int [] operatPriority  = new int[] {0,3,2,1,-1,1,0,2};	// ���������ASCII��-40����������������ȼ�
   
    /*public static void main(String[] args) {
        System.out.println(5+12*(3+5)/7.0);
        Calculator cal  = new Calculator();
        String s = "5+12*(3+5)/7";
        double result  = cal.calculate(s);
        System.out.println(result);
    }*/

    /**
     * ���ո����ı��ʽ����
     * @param expression Ҫ����ı��ʽ����:5+12*(3+5)/7
     * @return
     */
    public double calculate(String expression) {
    	postfixStack.clear();
    	opStack.clear();
        Stack<String> resultStack  = new Stack<String>();
        prepare(expression);
        Collections.reverse(postfixStack);				// ����׺ʽջ��ת
        String firstValue  ,secondValue,currentValue;	// �������ĵ�һ��ֵ���ڶ���ֵ�����������
        while(!postfixStack.isEmpty()) {
            currentValue  = postfixStack.pop();
            if(!isOperator(currentValue.charAt(0))) {	// ����������������������ջ��
                resultStack.push(currentValue);
            } else {	// ������������Ӳ�����ջ��ȡ����ֵ�͸���ֵһ���������
                 secondValue  = resultStack.pop();
                 firstValue  = resultStack.pop();
                 String tempResult  = calculate(firstValue, secondValue, currentValue.charAt(0));
                 resultStack.push(tempResult);
            }
        }
        return Double.valueOf(resultStack.pop());
    }
    
    /**
     * ����׼���׶ν����ʽת����Ϊ��׺ʽջ
     * @param expression
     */
    private void prepare(String expression) {
        opStack.push(',');		// ���������ջ��Ԫ�ض��ţ��˷������ȼ����
        char[] arr  = expression.toCharArray();
        int currentIndex  = 0;	// ��ǰ�ַ���λ��
        int count = 0;			// �ϴ����������������������������ַ��ĳ��ȱ��ڻ���֮�����ֵ
        char currentOp  ,peekOp;// ��ǰ��������ջ��������
        for(int i=0;i<arr.length;i++) {
            currentOp = arr[i];
            if(isOperator(currentOp)) {	// �����ǰ�ַ��������
                if(count > 0) {
                    postfixStack.push(new String(arr,currentIndex,count));	// ȡ���������֮�������
                }
                peekOp = opStack.peek();
                if(currentOp == ')') {	// �����������������ջ�е�Ԫ���Ƴ�����׺ʽջ��ֱ������������
                    while(opStack.peek() != '(') {
                        postfixStack.push(String.valueOf(opStack.pop()));
                    }
                    opStack.pop();
                } else {
                    while(currentOp != '(' && peekOp != ',' && compare(currentOp,peekOp) ) {
                        postfixStack.push(String.valueOf(opStack.pop()));
                        peekOp = opStack.peek();
                    }
                    opStack.push(currentOp);
                }
                count = 0;
                currentIndex = i+1;
            } else {
                count++;
            }
        }
        if(count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {	// ���һ���ַ��������Ż��������������������׺ʽջ��
            postfixStack.push(new String(arr,currentIndex,count));
        } 
        
        while(opStack.peek() != ',') {
            postfixStack.push(String.valueOf( opStack.pop()));	// ��������ջ�е�ʣ���Ԫ����ӵ���׺ʽջ��
        }
    }
    
    /**
     * �ж��Ƿ�Ϊ��������
     * @param c
     * @return
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' ||c == ')';
    }
    
    /**
     * ����ASCII��-40���±�ȥ�����������ȼ�
     * @param cur
     * @param peek
     * @return
     */
    public  boolean compare(char cur,char peek) {	// �����peek���ȼ�����cur������true��Ĭ�϶���peek���ȼ�Ҫ��
        boolean result  = false;
        if(operatPriority[(peek)-40] >= operatPriority[(cur) - 40]) {
           result = true;
        }
        return result;
    }
    
    /**
     * ���ո��������������������
     * @param firstValue
     * @param secondValue
     * @param currentOp
     * @return
     */
    private String calculate(String firstValue,String secondValue,char currentOp) {
        String result  = "";
        switch(currentOp) {
            case '+':
                result = String.valueOf(ArithHelper.add(firstValue, secondValue));
                break;
            case '-':
                result = String.valueOf(ArithHelper.sub(firstValue, secondValue));
                break;
            case '*':
                result = String.valueOf(ArithHelper.mul(firstValue, secondValue));
                break;
            case '/':
                result = String.valueOf(ArithHelper.div(firstValue, secondValue));
                break;
        }
        return result;
    }
}