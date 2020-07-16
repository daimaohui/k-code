package com.kuaishou.kcode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PairData {
    int startTime;
    String caller;      /* 调用方 */
    String callerIP;    /* 调用方 IP */
    String responder;      /* 被调用目标方 */
    String responderIP;    /* 被调用目标方 IP */
    List<Integer> elapsedTimes; /* 调用耗时列表 */
    int trueCount;              /* 调用成功次数 */
    int callTotal;              /* 调用总次数 */

    PairData(int startTime,String caller,String callerIP,String responder,String responderIP) {
        this.startTime=startTime;
        this.elapsedTimes = new ArrayList<>();
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
        int len=elapsedTimes.size();
        Integer [] nums = elapsedTimes.toArray(new Integer[len]);
        Arrays.sort(nums,0,len);
        int i=callTotal-callTotal/100;
        int P99=nums[i-1];
        double SR=trueCount*1.0/callTotal*100;
        TimeIPPair timeIPPair=new TimeIPPair(startTime,caller,callerIP,responder,responderIP,P99,SR);
        elapsedTimes.clear();
        return timeIPPair;
    }
}
