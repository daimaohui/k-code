package com.kuaishou.kcode;

import java.util.ArrayList;

import static com.kuaishou.kcode.KcodeRpcMonitorImpl.testFormat;

public class mergeRes extends Thread {
    Utils util_1=null;
    Utils util_2=null;
    public mergeRes(Utils util_1,Utils util_2){
        this.util_1=util_1;
        this.util_2=util_2;
    }
    @Override
    public void run() {
        for(int k=0;k<util_1.services_len;k++){
            util_1.services[k][0]=util_1.services[k][0]+util_2.services[k][0];
            util_1.services[k][1]=util_1.services[k][1]+util_2.services[k][1];
        }
        for(int k=0;k<util_1.String_IP_len;k++){
            for(int e=0;e<util_1.max_size;e++){
                if(util_2.Util[k][e][0]!=0){
                    int index_2=util_1.max_size/2+util_2.Util[k][e][0]-util_1.Util_size[k][2];
                    util_1.Util[k][index_2][1]+=util_2.Util[k][e][1];
                    util_1.Util[k][index_2][0]=util_2.Util[k][e][0];
                }
            }
            util_1.Util_size[k][0]= util_1.Util_size[k][0]+util_2.Util_size[k][0];
            util_1.Util_size[k][1] = util_1.Util_size[k][1]+util_2.Util_size[k][1];
        }
        prepareResult(util_1);
    }
    public void prepareResult(Utils util){
        int time=testFormat(util.data);
        for(int i=0;i<util.size_1;i++){
            KcodeRpcMonitorImpl.res_map_1.get(KcodeRpcMonitorImpl.String_service[i]).put(time,util.services[i][0]*1.0/util.services[i][1]);
        }
        for(int j=0;j<util.String_IP_len;j++){
            if(util.Util_size[j][1]==0){
                continue;
            }
            int key=KcodeRpcMonitorImpl.String_IP[j].Mainservices;
            for(int i=0;i<util.data.length();i++){
                key=key*31+util.data.charAt(i);
            }
            int index=util.Util_size[j][1]/100+1;
            int i=0;
            int sum=0;
            for(i=util.max_size-1; i>=0;i--){
                sum+=util.Util[j][i][1];
                if(sum>=index){
                    break;
                }
            }
            int P99=util.Util[j][i][0];
            double res_1=util.Util_size[j][0]*1.0/util.Util_size[j][1]*100;
            KcodeRpcMonitorImpl.res_map.computeIfAbsent(key, k -> new ArrayList<>()).add(KcodeRpcMonitorImpl.String_IP[j].IPs+","+String.format("%.2f", res_1)+"%,"+P99);
        }
    }
}
