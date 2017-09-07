package data.pool_model;

/**Tigger 报警包类*/
public class Tigger {
	public int equipId;        //设备id
    public int tiggerId;
    public int enabled;        // 1 启用 0 停用
    public int conditionid;
    public float startvalue;  // 对应模板文件中的 StartCompareValue
    public float stopvalue;   // 对应模板文件中的 EndCompareValue
    public int eventseverity;  // 对应模板文件中的 EventSeverity， set时若为0表示不设置该字段。
    public int mark;    // 标记，set时表示设哪一个< 0值 startvalue 和 stopvalue 都需要设置， 1值仅设置startvalue， 2值仅设置stopvalue, 3告警使能。>;
// get 时标识 startvalue、stopvalue 的有效性 <同上， 0 都有效(有值)，1仅startvalue有效， 2仅stopvalue有效。>
}
