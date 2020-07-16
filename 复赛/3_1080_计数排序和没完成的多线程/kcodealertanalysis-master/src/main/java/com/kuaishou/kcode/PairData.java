package com.kuaishou.kcode;


public class PairData {
    int startTime;
    String caller;      /* 调用方 */
    String callerIP;    /* 调用方 IP */
    String responder;      /* 被调用目标方 */
    String responderIP;    /* 被调用目标方 IP */
    int maxsize=300;
    int[][] elapsedTimes=new int[maxsize][2]; /* 调用耗时列表 */
    int trueCount;              /* 调用成功次数 */
    int callTotal;              /* 调用总次数 */
    int midnums;
    PairData(int startTime,String caller,String callerIP,String responder,String responderIP) {
        this.startTime=startTime;
        this.caller=caller;
        this.callerIP=callerIP;
        this.responder=responder;
        this.responderIP=responderIP;
        this.trueCount=0;
        this.callTotal=0;
        this.midnums=0;
    }

    /**
     * 需要知道p99和RS
     */
    public TimeIPPair getresult(){
//        Arrays.sort(elapsedTimes,0,callTotal);
        int index=callTotal/100+1;
        int i=0;
        int sum=0;
        for(i=maxsize-1; i>=0;i--){
            sum+=elapsedTimes[i][1];
            if(sum>=index){
                break;
            }
        }
        int P99=elapsedTimes[i][0];
        double SR=trueCount*1.0/callTotal*100;
        TimeIPPair timeIPPair=new TimeIPPair(startTime,caller,callerIP,responder,responderIP,P99,SR);
        return timeIPPair;
    }
}
