package com.kuaishou.kcode;

import java.util.Arrays;
import java.util.HashMap;

class prepareResult implements Runnable {
    public void run() {
        int count=0;
        while(data.flag==0||count<data.timestamps.size()){
            //System.out.println(count);
            if(count==data.timestamps.size()){
                //System.out.println(count);
                synchronized (data.lock) {
                    try {
                        data.lock.wait();//阻塞当前
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
                timestamp timestamp= data.timestamps.get(count);
                count++;
            HashMap<String,String> res_Hashmap=new HashMap<>();
            for(int j=0;j<timestamp.methodNames.size();j++){
                methodName a=timestamp.methodNames.get(j);
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
            data.res_map.put(timestamp.timestamps,res_Hashmap);
            timestamp=null;
        }
    }
}

