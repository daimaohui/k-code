package com.kuaishou.kcode;

public class PairDataV2 {
    int startTime;
    String caller;      /* 调用方 */
    String responder;      /* 被调用目标方 */
    int maxsize=600;
    int[][] elapsedTimes=new int[maxsize][2]; /* 调用耗时列表 */
    int trueCount;              /* 调用成功次数 */
    int callTotal;              /* 调用总次数 */
    int midnums;
    PairDataV2(int startTime,String caller,String responder) {
        this.startTime=startTime;
        this.caller=caller;
        this.responder=responder;
        this.trueCount=0;
        this.callTotal=0;
        this.midnums=0;
    }
    public String[] getresult(){
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
        double SR = Math.floor((double)trueCount / (double)callTotal * 10000) / 100;
        String[] temp={SR+"%",P99+"ms"};
        return temp;
    }
}
