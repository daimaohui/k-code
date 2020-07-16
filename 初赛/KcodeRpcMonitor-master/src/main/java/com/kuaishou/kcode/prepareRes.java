package com.kuaishou.kcode;

import java.nio.MappedByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.kuaishou.kcode.KcodeRpcMonitorImpl.testFormat;

public class prepareRes extends Thread{
    Utils util=null;
    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    MappedByteBuffer buff;
    int offset;
    int mapSize;
    int ismid;
    public prepareRes(MappedByteBuffer buff,int offset,int mapSize,Utils util,int ismid){
        this.buff=buff;
        this.offset=offset;
        this.mapSize=mapSize;
        this.util=new Utils(util.services_len,util.String_IP_len);
        this.ismid=ismid;
    }
    @Override
    public void run() {
        if(ismid==0){
            String data="";
            int timestamp=0;
            int old=-1;
            int result=0;
            /**
             * 第一阶段，最后的一个是多余的
             */
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
                        util.data=data;
                        prepareResult(util);
                        data = simpleDateFormat.format(timestamp_temp);
                    }
                    if (old == -1) {
                        data = simpleDateFormat.format(timestamp_temp);
                        //throw new RuntimeException(data);
                    }
                    old = timestamp;
                    for(int i=0;i<util.size_1;i++){
                        util.services[i][0]=0;
                        util.services[i][1]=0;
                    }
                    for(int j=0;j<util.String_IP_len;j++){
                        util.Util_size[j][1]=0;
                        util.Util_size[j][0]=0;
                        util.Util_size[j][2]=0;
                        for(int i=0; i<util.max_size;i++){
                            util.Util[j][i][1]=0;
                            util.Util[j][i][0]=0;
                        }
                    }
                }
                int index = KcodeRpcMonitorImpl.find_services.get(TransService_temp);
                if (result == 1) util.services[index][0]++;
                util.services[index][1]++;
                int index_1=KcodeRpcMonitorImpl.find_Mainservices.get(ServicesIP);
                if(util.Util_size[index_1][2]==0){
                    if (result == 1) util.Util_size[index_1][0]++;
                    util.Util_size[index_1][2]=nums_temp;
                    int index_2=util.max_size/2+nums_temp-util.Util_size[index_1][2];
                    util.Util[index_1][index_2][0] = nums_temp;
                    util.Util[index_1][index_2][1]++;
                    util.Util_size[index_1][1]++;
                }
                else{
                    if (result == 1) util.Util_size[index_1][0]++;
                    int index_2=util.max_size/2+nums_temp-util.Util_size[index_1][2];
                    util.Util[index_1][index_2][0] = nums_temp;
                    util.Util[index_1][index_2][1]++;
                    util.Util_size[index_1][1]++;
                }
            }
            Utils util_temp=new Utils(util.Util, util.Util_size, util.services,util.String_IP_len, util.services_len);
            util_temp.data=data;
            KcodeRpcMonitorImpl.utils[0]=util_temp;

        }
        else if(ismid==-1){
            /**
             * 最后部分，就是前面一端不满足
             */
            int isfirst=1;
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
                        if(isfirst==1){
                            Utils util_temp=new Utils(util.Util, util.Util_size, util.services,util.String_IP_len, util.services_len);

                            util_temp.data=data;
                            KcodeRpcMonitorImpl.utils[11]=util_temp;
                        }
                        else{
                            util.data=data;
                            prepareResult(util);
                        }
                        data = simpleDateFormat.format(timestamp_temp);
                    }
                    if (old == -1) {
                        data = simpleDateFormat.format(timestamp_temp);
                        //throw new RuntimeException(data);
                    }
                    old = timestamp;
                    for(int i=0;i<util.size_1;i++){
                        util.services[i][0]=0;
                        util.services[i][1]=0;
                    }
                    for(int j=0;j<util.String_IP_len;j++){
                        util.Util_size[j][1]=0;
                        util.Util_size[j][0]=0;
                        util.Util_size[j][2]=0;
                        for(int i=0; i<util.max_size;i++){
                            util.Util[j][i][1]=0;
                            util.Util[j][i][0]=0;
                        }
                    }
                }
                int index = KcodeRpcMonitorImpl.find_services.get(TransService_temp);
                if (result == 1) util.services[index][0]++;
                util.services[index][1]++;
                int index_1=KcodeRpcMonitorImpl.find_Mainservices.get(ServicesIP);
                if(util.Util_size[index_1][2]==0){
                    if (result == 1) util.Util_size[index_1][0]++;
                    util.Util_size[index_1][2]=nums_temp;
                    int index_2=util.max_size/2+nums_temp-util.Util_size[index_1][2];
                    util.Util[index_1][index_2][0] = nums_temp;
                    util.Util[index_1][index_2][1]++;
                    util.Util_size[index_1][1]++;
                }
                else{
                    if (result == 1) util.Util_size[index_1][0]++;
                    int index_2=util.max_size/2+nums_temp-util.Util_size[index_1][2];
                    util.Util[index_1][index_2][0] = nums_temp;
                    util.Util[index_1][index_2][1]++;
                    util.Util_size[index_1][1]++;
                }
            }
            if(isfirst==1){
                Utils util_temp=new Utils(util.Util, util.Util_size, util.services,util.String_IP_len, util.services_len);
                util_temp.data=data;
                KcodeRpcMonitorImpl.utils[11]=util_temp;
            }
            else{
                util.data=data;
                prepareResult(util);
            }
        }
        else{
            /**
             * 中间部分，就是两端都是不满足的
             */
            int isfirst=1;
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
                        if(isfirst==1){
                            Utils util_temp=new Utils(util.Util, util.Util_size, util.services,util.String_IP_len, util.services_len);
                            util_temp.data=data;
                            KcodeRpcMonitorImpl.utils[2*ismid-1]=util_temp;
                            isfirst=0;
                        }
                        else{
                            util.data=data;
                            prepareResult(util);
                        }
                        data = simpleDateFormat.format(timestamp_temp);
                    }
                    if (old == -1) {
                        data = simpleDateFormat.format(timestamp_temp);
                        //throw new RuntimeException(data);
                    }
                    old = timestamp;
                    for(int i=0;i<util.size_1;i++){
                        util.services[i][0]=0;
                        util.services[i][1]=0;
                    }
                    for(int j=0;j<util.String_IP_len;j++){
                        util.Util_size[j][1]=0;
                        util.Util_size[j][0]=0;
                        util.Util_size[j][2]=0;
                        for(int i=0; i<util.max_size;i++){
                            util.Util[j][i][1]=0;
                            util.Util[j][i][0]=0;
                        }
                    }
                }
                int index = KcodeRpcMonitorImpl.find_services.get(TransService_temp);
                if (result == 1) util.services[index][0]++;
                util.services[index][1]++;
                int index_1=KcodeRpcMonitorImpl.find_Mainservices.get(ServicesIP);
                if(util.Util_size[index_1][2]==0){
                    if (result == 1) util.Util_size[index_1][0]++;
                    util.Util_size[index_1][2]=nums_temp;
                    int index_2=util.max_size/2+nums_temp-util.Util_size[index_1][2];
                    util.Util[index_1][index_2][0] = nums_temp;
                    util.Util[index_1][index_2][1]++;
                    util.Util_size[index_1][1]++;
                }
                else{
                    if (result == 1) util.Util_size[index_1][0]++;
                    int index_2=util.max_size/2+nums_temp-util.Util_size[index_1][2];
                    util.Util[index_1][index_2][0] = nums_temp;
                    util.Util[index_1][index_2][1]++;
                    util.Util_size[index_1][1]++;
                }
            }
            Utils util_temp1=new Utils(util.Util, util.Util_size, util.services, util.String_IP_len, util.services_len);
            util_temp1.data=data;
            KcodeRpcMonitorImpl.utils[2*ismid]=util_temp1;
        }
        util=null;
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
