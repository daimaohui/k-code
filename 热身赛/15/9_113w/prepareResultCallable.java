package com.kuaishou.kcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;

class prepareResultCallable implements Callable<HashMap<Long,HashMap<String,String>>> {
    public HashMap<Long,HashMap<String,String>> res_map=new HashMap<>();
    public KcodeQuestion.timestamp timestamp=null;

    public prepareResultCallable(KcodeQuestion.timestamp timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public HashMap<Long,HashMap<String,String>> call() throws Exception {
        this.prepareResult();
        return this.res_map;
    }
    public void prepareResult(){
        HashMap<String,String> res_Hashmap=new HashMap<>();
        for(int j=0;j<timestamp.methodNames.size();j++){
            KcodeQuestion.methodName a=timestamp.methodNames.get(j);
            Arrays.sort(a.nums,0,a.nums_size);
            int QFS=a.nums_size;
            int sum=0;
            int max=a.nums[QFS-1];
            int AVG=0;
            int P50=0;
            int  P99=0;
            for(int i=0;i<QFS;i++){
                sum+=a.nums[i];
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
                P50=a.nums[i-1];
                i=QFS-QFS/100;
                P99=a.nums[i-1];
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
            res_Hashmap.put(a._methodName,res.toString());
        }
        timestamp.methodNames.clear();
        timestamp.map.clear();
        res_map.put(timestamp.timestamps,res_Hashmap);
    }

}
