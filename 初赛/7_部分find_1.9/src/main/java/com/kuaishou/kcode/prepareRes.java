package com.kuaishou.kcode;

import java.nio.MappedByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.kuaishou.kcode.KcodeRpcMonitorImpl.testFormat;

public class prepareRes extends Thread{
    int size_1=70;
    int size_2=3900;
    int max_size=300;
    int[][][] Util=new int[size_2][max_size][2];
    int[][] Util_size=new int[size_2][3];
    int[][] services=new int[size_1][2];
    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    MappedByteBuffer buff;
    int offset;
    int mapSize;
    public prepareRes(MappedByteBuffer buff,int offset,int mapSize){
        this.buff=buff;
        this.offset=offset;
        this.mapSize=mapSize;
    }
    @Override
    public void run() {
        String data="";
        int timestamp=0;
        int old=-1;
        int result=0;
        while(offset<mapSize) {
            /**
             * 得到数据
             */
            int MainService_temp = 0;
            int TransService_temp = 0;
            int nums_temp = 0;
            long timestamp_temp = 0;
            long ServicesIP=0;
            while (buff.get(offset) != ',') {
                MainService_temp = MainService_temp * 31 + buff.get(offset);
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                TransService_temp = TransService_temp * 31 + buff.get(offset);
                MainService_temp = MainService_temp * 31 + buff.get(offset);
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            offset++;
            int flag = 1;
            while (buff.get(offset) != ',') {
                if (flag == 1) {
                    if (buff.get(offset) == 't') {
                        result = 1;
                    } else {
                        result = 0;
                    }
                    flag = 0;
                }
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                nums_temp = nums_temp * 10 + buff.get(offset) - '0';
                offset++;
            }
            offset++;
            while (buff.get(offset) != '\n') {
                timestamp_temp = timestamp_temp * 10 + buff.get(offset) - '0';
                offset++;
            }
            offset++;
            timestamp = (int) (timestamp_temp / (1000 * 60));
            if (timestamp != old) {
                if (old != -1) {
                    prepareResult(data);
                    data = simpleDateFormat.format(timestamp_temp);
                }
                if (old == -1) {
                    data = simpleDateFormat.format(timestamp_temp);
                }
                old = timestamp;
            }

            int index = KcodeRpcMonitorImpl.find_services.get(TransService_temp);
            if (result == 1) services[index][0]++;
            services[index][1]++;
            int index_1=KcodeRpcMonitorImpl.find_Mainservices.get(ServicesIP);
            if(Util_size[index_1][2]==0){
                if (result == 1) Util_size[index_1][0]++;
                Util_size[index_1][2]=nums_temp;
                int index_2=max_size/2+nums_temp-Util_size[index_1][2];
                Util[index_1][index_2][0] = nums_temp;
                Util[index_1][index_2][1]++;
                Util_size[index_1][1]++;
            }
            else{
                if (result == 1) Util_size[index_1][0]++;
                int index_2=max_size/2+nums_temp-Util_size[index_1][2];
                if(index_2<0){
                    System.out.println(nums_temp+","+Util_size[index_1][2]);
                }
                Util[index_1][index_2][0] = nums_temp;
                Util[index_1][index_2][1]++;
                Util_size[index_1][1]++;
            }
        }
        prepareResult(data);
    }
    public void prepareResult(String currData){
        int time=testFormat(currData);
        for(int i=0;i<size_1;i++){
            KcodeRpcMonitorImpl.res_map_1.get(KcodeRpcMonitorImpl.String_service[i]).put(time,services[i][0]*1.0/services[i][1]);
            services[i][0]=0;
            services[i][1]=0;
        }
        for(int j=0;j<KcodeRpcMonitorImpl.String_IP_len;j++){
            if(Util_size[j][1]==0){
                continue;
            }
            int key=KcodeRpcMonitorImpl.String_IP[j].Mainservices;
            for(int i=0;i<currData.length();i++){
                key=key*31+currData.charAt(i);
            }
            int index=Util_size[j][1]/100+1;
            int i=0;
            int sum=0;
            for(i=max_size-1; i>=0;i--){
                sum+=Util[j][i][1];
                if(sum>=index){
                    break;
                }
            }
            int P99=Util[j][i][0];
            double res_1=Util_size[j][0]*1.0/Util_size[j][1]*100;
            KcodeRpcMonitorImpl.res_map.computeIfAbsent(key, k -> new ArrayList<>()).add(KcodeRpcMonitorImpl.String_IP[j].IPs+","+String.format("%.2f", res_1)+"%,"+P99);
            Util_size[j][1]=0;
            Util_size[j][0]=0;
            Util_size[j][2]=0;
            for(i=0; i<max_size;i++){
                Util[j][i][1]=0;
                Util[j][i][0]=0;
            }
        }
    }
}
