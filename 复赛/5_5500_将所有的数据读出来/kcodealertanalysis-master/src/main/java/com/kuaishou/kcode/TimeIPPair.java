package com.kuaishou.kcode;

/**
 * 存放准备好的结果
 */
public class TimeIPPair {
    int startTime;
    String caller;      /* 调用方 */
    String callerIP;    /* 调用方 IP */
    String responder;      /* 被调用目标方 */
    String responderIP;    /* 被调用目标方 IP */
    int P99;
    double RS;
    public TimeIPPair(int startTime,String caller, String callerIP, String responder, String responderIP, int p99, double RS) {
        this.startTime=startTime;
        this.caller = caller;
        this.callerIP = callerIP;
        this.responder = responder;
        this.responderIP = responderIP;
        P99 = p99;
        this.RS = RS;
    }
    public String getRSString(){
        return caller+","+callerIP+","+responder+","+responderIP+","+RS+"%";
    }
    public String getP99String(){
        return caller+","+callerIP+","+responder+","+responderIP+","+P99+"ms";
    }
}
