package com.kuaishou.kcode;

import java.util.Arrays;
import java.util.HashMap;

class prepareResultCallable  extends Thread{
//    public   long[] timestamps;
//    public   int[][][]  nums;
//    public   String[][] _methodName;
//    public   int[][] nums_size;
//    public   int[] methodNames_size;
//long[] timestamps,int[][][]  nums,String[][] _methodName,int[][] nums_size,int[] methodNames_size,
    public int index;
    public prepareResultCallable(int index){
//        this.timestamps=timestamps;
//        this.nums=nums;
//        this._methodName=_methodName;
//        this.nums_size=nums_size;
//        this.methodNames_size=methodNames_size;
        this.index=index;
    }

    @Override
    public void run() {
            HashMap<String,String> res_Hashmap=new HashMap<>();
            for(int j=0;j<KcodeQuestion.methodNames_size[index];j++){
                Arrays.sort(KcodeQuestion.nums[index][j],0,KcodeQuestion.nums_size[index][j]);
                int QFS=KcodeQuestion.nums_size[index][j];
                int sum=0;
                int max=KcodeQuestion.nums[index][j][QFS-1];
                int AVG=0;
                int P50=0;
                int  P99=0;
                for(int i=0;i<QFS;i++){
                    sum+=KcodeQuestion.nums[index][j][i];
                }
                if(QFS!=0){
                    if(sum/(QFS*1.0)>sum/QFS){
                        AVG=sum/QFS+1;
                    }
                    else {
                        AVG=sum/QFS;
                    }
                    int i=0;
                    if(0.5*QFS>QFS/2){
                        i=QFS/2+1;
                    }
                    else {
                        i=QFS/2;
                    }
                    P50=KcodeQuestion.nums[index][j][i-1];
                    i=QFS-QFS/100;
                    P99=KcodeQuestion.nums[index][j][i-1];
                }
                StringBuffer res=new StringBuffer();
                res.append(QFS);
                res.append(",");
                res.append(P99);
                res.append(",");
                res.append(P50);
                res.append(",");
                res.append(AVG);
                res.append(",");
                res.append(max);
                res_Hashmap.put(KcodeQuestion._methodName[index][j],res.toString());
                res.delete(0,res.length());
            }
            KcodeQuestion.res_map.put(KcodeQuestion.timestamps[index],res_Hashmap);
        //System.out.println("-------");
        //System.out.println(KcodeQuestion.son_index);
    }
}
