package data.pool_model;

/**Tigger ��������*/
public class Tigger {
	public int equipId;        //�豸id
    public int tiggerId;
    public int enabled;        // 1 ���� 0 ͣ��
    public int conditionid;
    public float startvalue;  // ��Ӧģ���ļ��е� StartCompareValue
    public float stopvalue;   // ��Ӧģ���ļ��е� EndCompareValue
    public int eventseverity;  // ��Ӧģ���ļ��е� EventSeverity�� setʱ��Ϊ0��ʾ�����ø��ֶΡ�
    public int mark;    // ��ǣ�setʱ��ʾ����һ��< 0ֵ startvalue �� stopvalue ����Ҫ���ã� 1ֵ������startvalue�� 2ֵ������stopvalue, 3�澯ʹ�ܡ�>;
// get ʱ��ʶ startvalue��stopvalue ����Ч�� <ͬ�ϣ� 0 ����Ч(��ֵ)��1��startvalue��Ч�� 2��stopvalue��Ч��>
}
