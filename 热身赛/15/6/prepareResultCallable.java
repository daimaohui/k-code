package com.kuaishou.kcode;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Callable;

class prepareResultCallable implements Callable<HashMap<String,String>> {
    HashMap<String,String> res_map=new HashMap<>();
    public KcodeQuestion.timestamp timestamp=null;

    public prepareResultCallable(KcodeQuestion.timestamp timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public HashMap<String,String> call() throws Exception {
        this.prepareResult();
        return this.res_map;
    }
    public void prepareResult(){
        for(int j=0;j<timestamp.methodNames.size();j++){
            KcodeQuestion.methodName a=timestamp.methodNames.get(j);
            Collections.sort(a.nums);
            int QFS=a.nums.size();
            int sum=0;
            int max=a.nums.get(QFS-1);
            int AVG=0;
            int P50=0;
            int  P99=0;
            for(int b:a.nums){
                sum+=b;
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
                P50=a.nums.get(i-1);
                i=QFS-QFS/100;
                P99=a.nums.get(i-1);
            }
            StringBuffer key=new StringBuffer();
            key.append(timestamp.timestamps);
            key.append(a._methodName);
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
            res_map.put(key.toString(),res.toString());
        }
    }

}
