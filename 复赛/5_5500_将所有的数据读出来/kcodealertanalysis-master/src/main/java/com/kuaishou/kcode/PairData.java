package com.kuaishou.kcode;


import java.util.Arrays;

public class PairData {
    int startTime;
    String caller;      /* 调用方 */
    String callerIP;    /* 调用方 IP */
    String responder;      /* 被调用目标方 */
    String responderIP;    /* 被调用目标方 IP */
    int maxsize=600;
    int[] elapsedTimes=new int[maxsize]; /* 调用耗时列表 */
    int trueCount;              /* 调用成功次数 */
    int callTotal;              /* 调用总次数 */
    PairData(int startTime,String caller,String callerIP,String responder,String responderIP) {
        this.startTime=startTime;
        this.caller=caller;
        this.callerIP=callerIP;
        this.responder=responder;
        this.responderIP=responderIP;
        this.trueCount=0;
        this.callTotal=0;
    }

    /**
     * 需要知道p99和RS
     */
    public TimeIPPair getresult(){
        Arrays.sort(elapsedTimes,0,callTotal);
        int i=callTotal-callTotal/100;
        int P99=elapsedTimes[i-1];
        double SR = Math.floor((double)trueCount / (double)callTotal * 10000) / 100;
        TimeIPPair timeIPPair=new TimeIPPair(startTime,caller,callerIP,responder,responderIP,P99,SR);
        return timeIPPair;
    }
}
