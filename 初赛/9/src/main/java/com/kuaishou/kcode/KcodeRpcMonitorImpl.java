package com.kuaishou.kcode;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.nanoTime;

/**
 * @author kcode
 * Created on 2020-06-01
 * 实际提交时请维持包名和类名不变
 */

public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {
    Utils util=new Utils();
    public static HashMap<Integer,ArrayList<String>> res_map=new HashMap<>(16,1);
    public static HashMap<Integer,HashMap<Integer,Double>> res_map_1=new HashMap<>(16,1);
    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    ArrayList<Thread> prepareRess=new ArrayList<>();
    int mintime=0;
    int maxtime=0;
    HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> res_map_2=new HashMap<>(16,1);
    public static Vector<Utils> utils=new Vector<>(15);
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
    }

    public void prepare(String path) throws IOException, InterruptedException {
        long startNs = nanoTime();
        RandomAccessFile raf = new RandomAccessFile (path, "r");
        FileChannel channel = raf.getChannel();
        long fileSize = channel.size();
        long mapOffset = 0;
        MappedByteBuffer buff=null;
        int mapSize=0;
        String data="";
        int timestamp=0;
        int old=-1;
        int result=0;
        String MainIP_temp="";
        String TransIP_temp="";
        char[] MainIP_temp_char=new char[20];
        int MainIP_temp_char_size=0;
        char[] TransIP_temp_char=new char[20];
        int TransIP_temp_char_size=0;
        mapSize=fileSize-mapOffset>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)(fileSize-mapOffset);
        buff= channel.map(FileChannel.MapMode.READ_ONLY,mapOffset,mapSize);
        int offset=0;
        while(offset<mapSize) {
            /**
             * 得到第一次的数据,构建所有的函数
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
                MainIP_temp_char[MainIP_temp_char_size++] = (char) buff.get(offset);
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            MainIP_temp = String.valueOf(MainIP_temp_char).substring(0, MainIP_temp_char_size);
            MainIP_temp_char_size = 0;
            offset++;
            while (buff.get(offset) != ',') {
                TransService_temp = TransService_temp * 31 + buff.get(offset);
                MainService_temp = MainService_temp * 31 + buff.get(offset);
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                TransIP_temp_char[TransIP_temp_char_size++] = (char) buff.get(offset);
                ServicesIP=ServicesIP*31+buff.get(offset);
                offset++;
            }
            TransIP_temp = String.valueOf(TransIP_temp_char).substring(0, TransIP_temp_char_size);
            TransIP_temp_char_size = 0;
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
                    break;
                }
                if (old == -1) {
                    data = simpleDateFormat.format(timestamp_temp);
                    mintime = testFormat(data);
                    //throw new RuntimeException(data);
                }
                old=timestamp;
            }
            mapOffset=offset;
            if (util.find_services.containsKey(TransService_temp)) {
                int index = util.find_services.get(TransService_temp);
                if (result == 1) util.services[index][0]++;
                util.services[index][1]++;
            } else {
                util.String_service[util.services_len] = TransService_temp;
                util.services[util.services_len][0] = 0;
                util.services[util.services_len][1] = 0;
                if (result == 1) util.services[util.services_len][0]++;
                util.services[util.services_len][1]++;
                util.find_services.put(TransService_temp, util.services_len);
                util.services_len++;
            }
            if (util.find_Mainservices.containsKey(ServicesIP)) {
                int index_1=util.find_Mainservices.get(ServicesIP);
                if (util.Util_size[index_1][1] == util.Util_size[index_1][2]) {
                    util.Util_size[index_1][2]= util.Util_size[index_1][2]* 2;
                    util.Util[index_1] = Arrays.copyOf(util.Util[index_1], util.Util_size[index_1][2]);
                }
                if (result == 1) util.Util_size[index_1][0]++;
                util.Util[index_1][util.Util_size[index_1][1]++] = nums_temp;
            } else {
                ServicesIP servicesIP=new ServicesIP(MainService_temp,MainIP_temp + "," + TransIP_temp);
                util.String_IP[util.String_IP_len]=servicesIP;
                util.Util_size[util.String_IP_len][0]=0;
                util.Util_size[util.String_IP_len][1]=0;
                if (result == 1) util.Util_size[util.String_IP_len][0]++;
                util.Util[util.String_IP_len][util.Util_size[util.String_IP_len][1]++] = nums_temp;
                util.find_Mainservices.put(ServicesIP,util.String_IP_len);
                util.String_IP_len++;
            }
        }
        int ismid=0;
        while(mapOffset<fileSize) {
            int max=Integer.MAX_VALUE;
            mapSize = fileSize - mapOffset > max ? max : (int) (fileSize - mapOffset);
            buff = channel.map(FileChannel.MapMode.READ_ONLY, mapOffset, mapSize);
            if(mapSize==max){
                /* 调整整个 buffer 的右边界 */
                /* 检查右边界::找到 2G 前的一个 '\n' */
                mapSize--;
                while ( (buff.get(mapSize)) != '\n' )
                    mapSize--;
                Thread prepareRes_t=new prepareRes(buff,0,mapSize,util,ismid);
                ismid=1;
                prepareRes_t.start();
                prepareRess.add(prepareRes_t);
                mapOffset=mapOffset+mapSize+1;
            }
            else{  //最后的一个分块
                Thread prepareRes_t=new prepareRes(buff,0,mapSize,util,2);
                prepareRes_t.start();
                prepareRess.add(prepareRes_t);
                mapSize--;
                while(buff.get(mapSize)!=','){
                    mapSize--;
                }
                mapSize++;
                long temp=0;
                while(buff.get(mapSize)!='\n'){
                    temp=temp*10+buff.get(mapSize)-'0';
                    mapSize++;
                }
                mapSize++;
                data=simpleDateFormat.format(temp);
                maxtime = testFormat(data);
                break;
            }
        }
        for (Thread e:prepareRess
             ) {
            e.join();
        }
        prepareRess.clear();
        /**
         * 等待线程完成，然后进行融合
         */
        int[] temp=new int[utils.size()];
        for(int i=0;i<utils.size();i++){
            if(temp[i]==0){
                for(int j=i+1;j<utils.size();j++){
                    //do something
                    Utils util_1=utils.get(i);
                    Utils util_2=utils.get(j);
                    if(util_1.data.equals(util_2.data)){
                        //System.out.println(util_1.data);
                        for(int k=0;k<util.services_len;k++){
                            util_1.services[k][0]=util_1.services[k][0]+util_2.services[k][0];
                            util_1.services[k][1]=util_1.services[k][1]+util_2.services[k][1];
                        }
                        for(int k=0;k<util.String_IP_len;k++){
                            util_1.Util_size[k][0]= util_1.Util_size[k][0]+util_2.Util_size[k][0];
                            util_1.Util[k] = Arrays.copyOf(util_1.Util[k], util_1.Util_size[k][1] + util_2.Util_size[k][1]);
                            System.arraycopy(util_2.Util[k], 0, util_1.Util[k], util_1.Util_size[k][1], util_2.Util_size[k][1]);
                            util_1.Util_size[k][1] = util_1.Util_size[k][1]+util_2.Util_size[k][1];
                        }
                        prepareResultv1(util_1);
                        temp[j]=1;
                        util_1=null;
                        util_2=null;
                        break;
                    }
                }
            }
        }
        prepareResponder();
        utils.clear();
        //throw new RuntimeException(data+"->"+(1+maxtime-mintime));
    }
    public static int testFormat(String str) {
        int initTime = 26514240;
        char arr1[] = new char[2];
        char arr2[] = new char[2];
        char arr3[] = new char[2];
        arr1[0] = str.charAt(11);
        arr1[1] = str.charAt(12);
        arr2[0] = str.charAt(14);
        arr2[1] = str.charAt(15);
        arr3[0] = str.charAt(8);
        arr3[1] = str.charAt(9);
        int time = ((int) arr3[0] - '0') * 10 * 24 * 60 + ((int) arr3[1] - '0') * 24 * 60 +
                ((int) arr1[0] - '0') * 10 * 60+ ((int) arr1[1] - '0') * 60 +
                ((int) arr2[0] - '0') * 10 + ((int) arr2[1] - '0');
        return time + initTime;
    }
    public void prepareResponder(){
        for(int i=0;i<util.services_len;i++){
            HashMap<Integer,HashMap<Integer,String>> temp_1=new HashMap<>();
            for(int j=mintime;j<=maxtime;j++){
                HashMap<Integer,String> temp_2=new HashMap<>();
                double sum=0;
                for(int k=j;k<=maxtime;k++){
                    sum+=res_map_1.get(util.String_service[i]).get(k);
                    int size=k-j+1;
                    double res_1=sum/size*100;
                    temp_2.put(k,String.format("%.2f", res_1)+"%");
                }
                temp_1.put(j,temp_2);
            }
            res_map_2.put(util.String_service[i],temp_1);
        }
    }
    public void prepareResultv1(Utils util){
        int time=testFormat(util.data);
        for(int i=0;i<util.size_1;i++){
            res_map_1.get(util.String_service[i]).put(time,util.services[i][0]*1.0/util.services[i][1]);
        }
        for(int j=0;j<util.String_IP_len;j++){
            if(util.Util_size[j][1]==0){
                continue;
            }
            int key=util.String_IP[j].Mainservices;
            for(int i=0;i<util.data.length();i++){
                key=key*31+util.data.charAt(i);
            }
            Arrays.sort(util.Util[j],0,util.Util_size[j][1]);
            int index=util.Util_size[j][1]-util.Util_size[j][1]/100;
            int P99=util.Util[j][index-1];
            double res_1=util.Util_size[j][0]*1.0/util.Util_size[j][1]*100;
            res_map.computeIfAbsent(key, k -> new ArrayList<>()).add(util.String_IP[j].IPs+","+String.format("%.2f", res_1)+"%,"+P99);
//            util.Util_size[j][1]=0;
//            util.Util_size[j][0]=0;
        }
    }
    public void prepareResult(Utils util){
        /**
         * 这是最小的时间
         */
        int time=testFormat(util.data);
        for(int i=0;i<util.services_len;i++){
            HashMap<Integer,Double> res_map_1_temp=new HashMap<>();
            res_map_1_temp.put(time,util.services[i][0]*1.0/util.services[i][1]);
            res_map_1.put(util.String_service[i],res_map_1_temp);
        }
        for(int j=0;j<util.String_IP_len;j++){
            if(util.Util_size[j][1]==0){
                continue;
            }
            int key=util.String_IP[j].Mainservices;
            for(int i=0;i<util.data.length();i++){
                key=key*31+util.data.charAt(i);
            }
            Arrays.sort(util.Util[j],0,util.Util_size[j][1]);
            int index=util.Util_size[j][1]-util.Util_size[j][1]/100;
            int P99=util.Util[j][index-1];
            double res_1=util.Util_size[j][0]*1.0/util.Util_size[j][1]*100;
            this.res_map.computeIfAbsent(key, k -> new ArrayList<>()).add(util.String_IP[j].IPs+","+String.format("%.2f", res_1)+"%,"+P99);
        }
    }
    public List<String> checkPair(String caller, String responder, String time) {
        int str_time=(caller+responder+time).hashCode();
        if(res_map.containsKey(str_time)){
            return res_map.get(str_time);
        }
        else{
            return new ArrayList<String>();
        }

    }
    public String checkResponder(String responder, String start, String end){
        int responder_int=responder.hashCode();
        int st=testFormat(start);
        int et=testFormat(end);
        if(st<mintime){
            st=mintime;
        }
        if(et>maxtime){
            et=maxtime;
        }
        if(res_map_2.containsKey(responder_int)){
            return res_map_2.get(responder_int).get(st).get(et);
        }
        else{
            return "-1.00%";
        }
    }

}
