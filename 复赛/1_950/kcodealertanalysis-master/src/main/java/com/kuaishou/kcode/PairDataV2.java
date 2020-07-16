package com.kuaishou.kcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PairDataV2 {
    int startTime;
    String caller;      /* 调用方 */
    String responder;      /* 被调用目标方 */
    List<Integer> elapsedTimes; /* 调用耗时列表 */
    int trueCount;              /* 调用成功次数 */
    int callTotal;              /* 调用总次数 */
    PairDataV2(int startTime,String caller,String responder) {
        this.startTime=startTime;
        this.elapsedTimes = new ArrayList<>();
        this.caller=caller;
        this.responder=responder;
        this.trueCount=0;
        this.callTotal=0;
    }
    public SRAndP99 getresult(){
        int len=elapsedTimes.size();
        Integer [] nums = elapsedTimes.toArray(new Integer[len]);
        Arrays.sort(nums,0,len);
        int i=callTotal-callTotal/100;
        int P99=nums[i-1];
        double SR=trueCount*1.0/callTotal*100;
        SRAndP99 srAndP99=new SRAndP99(String.format("%.2f", SR)+"%",P99);
        elapsedTimes.clear();
        return srAndP99;
    }
}
