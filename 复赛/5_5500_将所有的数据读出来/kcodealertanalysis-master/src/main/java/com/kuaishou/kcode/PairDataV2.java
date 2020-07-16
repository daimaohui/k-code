package com.kuaishou.kcode;

import java.util.Arrays;

public class PairDataV2 {
    int startTime;
    String caller;      /* 调用方 */
    String responder;      /* 被调用目标方 */
    int maxsize=2000;
    int[] elapsedTimes=new int[maxsize]; /* 调用耗时列表 */
    int trueCount;              /* 调用成功次数 */
    int callTotal;              /* 调用总次数 */
    PairDataV2(int startTime,String caller,String responder) {
        this.startTime=startTime;
        this.caller=caller;
        this.responder=responder;
        this.trueCount=0;
        this.callTotal=0;
    }
    public String[] getresult(){
        Arrays.sort(elapsedTimes,0,callTotal);
        int i=callTotal-callTotal/100;
        int P99=elapsedTimes[i-1];
        double SR = Math.floor((double)trueCount / (double) callTotal * 10000) / 100;
        String[] temp={SR+"%",P99+"ms"};
        return temp;
    }
}
