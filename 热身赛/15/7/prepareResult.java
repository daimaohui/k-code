package com.kuaishou.kcode;

import java.util.Collections;
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
                for(int j=0;j<timestamp.methodNames.size();j++){
                    methodName a=timestamp.methodNames.get(j);
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
                    data.res_map.put(key.toString(),res.toString());
                }
        }
        //System.out.println(count);
    }
}

