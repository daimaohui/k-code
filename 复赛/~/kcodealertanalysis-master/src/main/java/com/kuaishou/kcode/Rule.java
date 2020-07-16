package com.kuaishou.kcode;

public class Rule {
    /**
     * 规则编号,主调服务名,被调服务名,数据类型,触发条件,阈值
     */
    int id;
    String caller;      /* 调用方 */
    String responder;      /* 被调用目标方 */
    String ALERT_TYPE;
    int times;  /* 次数限制*/
    char Trigger;
    double threshold;

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", caller='" + caller + '\'' +
                ", responder='" + responder + '\'' +
                ", ALERT_TYPE='" + ALERT_TYPE + '\'' +
                ", times=" + times +
                ", Trigger=" + Trigger +
                ", threshold=" + threshold +
                '}';
    }

    public Rule(int id, String caller, String responder, String ALERT_TYPE, int times, char trigger, double threshold) {
        this.id = id;
        this.caller = caller;
        this.responder = responder;
        this.ALERT_TYPE = ALERT_TYPE;
        this.times = times;
        Trigger = trigger;
        this.threshold = threshold;
    }
}
