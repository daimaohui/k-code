package com.kuaishou.kcode;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kcode
 * Created on 2020-06-01
 * 实际提交时请维持包名和类名不变
 */

public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {
    public static int size_1=70;
    public static int size_2=3900;
    int max_size=300;
    int[][][] Util=new int[size_2][max_size][2];
    int[][] Util_size=new int[size_2][3];
    int[][] services=new int[size_1][2];
    public static int[] String_service=new int[size_1];
    public static HashMap<Integer,Integer> find_services=new HashMap<>();
    public static HashMap<Long, Integer> find_Mainservices=new HashMap<>();
    public static ServicesIP[] String_IP=new ServicesIP[size_2];
    public static int String_IP_len=0;
    public static int services_len=0;
    public static HashMap<Integer,ArrayList<String>> res_map=new HashMap<>(16,1);
    public static HashMap<Integer,HashMap<Integer,Double>> res_map_1=new HashMap<>(16,1);
    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    ArrayList<Thread> prepareRess=new ArrayList<>();
    int mintime=0;
    int maxtime=0;
    HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> res_map_2=new HashMap<>(16,1);
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
    }

    public void prepare(String path) throws IOException, InterruptedException {
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
                    prepareResult(data);
                    break;
                }
                if (old == -1) {
                    data = simpleDateFormat.format(timestamp_temp);
                    mintime = testFormat(data);
                }
                old=timestamp;
            }
            mapOffset=offset;
            if (find_services.containsKey(TransService_temp)) {
                int index = find_services.get(TransService_temp);
                if (result == 1) services[index][0]++;
                services[index][1]++;
            } else {
                String_service[services_len] = TransService_temp;
                services[services_len][0] = 0;
                services[services_len][1] = 0;
                if (result == 1) services[services_len][0]++;
                services[services_len][1]++;
                find_services.put(TransService_temp, services_len);
                services_len++;
            }
            if (find_Mainservices.containsKey(ServicesIP)) {
                int index_1=find_Mainservices.get(ServicesIP);
                if (result == 1) Util_size[index_1][0]++;
                int index_2=max_size/2+nums_temp-Util_size[index_1][2];
                Util[index_1][index_2][0] = nums_temp;
                Util[index_1][index_2][1]++;
                Util_size[index_1][1]++;
            } else {
                ServicesIP servicesIP=new ServicesIP(MainService_temp,MainIP_temp + "," + TransIP_temp);
                String_IP[String_IP_len]=servicesIP;
                Util_size[String_IP_len][0]=0;
                Util_size[String_IP_len][1]=0;
                Util_size[String_IP_len][2]=nums_temp;
                if (result == 1) Util_size[String_IP_len][0]++;
                Util[String_IP_len][max_size/2][0] = nums_temp;
                Util[String_IP_len][max_size/2][1] = 1;
                Util_size[String_IP_len][1]++;
                find_Mainservices.put(ServicesIP,String_IP_len);
                String_IP_len++;
            }
        }
        while(mapOffset<fileSize) {
            int max=1024*1024*1024/2*3;
            mapSize = fileSize - mapOffset > max ? max : (int) (fileSize - mapOffset);
            buff = channel.map(FileChannel.MapMode.READ_ONLY, mapOffset, mapSize);
            if(mapSize==max){
                /* 调整整个 buffer 的右边界 */
                /* 检查右边界::找到 2G 前的一个 '\n' */
                mapSize--;
                while ( (buff.get(mapSize)) != '\n' )
                    mapSize--;
                /* 再找第二个 '\n' 作为处理的起始右边界，第一个 '\n' 可能会有问题，
                 */
                mapSize--;
                while ( (buff.get(mapSize)) != ',' )
                    mapSize--;
                mapSize++;      /* 要在 ',' 之后 */
                long old_temp=0;
                while(buff.get(mapSize)!='\n'){
                    old_temp=old_temp*10+buff.get(mapSize)-'0';
                    mapSize++;
                }
                old_temp=old_temp/(1000*60);
                mapSize--;
                long temp=old_temp;
                /**
                 * 往回找，保证每个线程处理的是完整的时间
                 */
                while(temp==old_temp){
                    temp=0;
                    while(buff.get(mapSize)!='\n'){
                        mapSize--;
                    }
                    while ( (buff.get(mapSize)) != ',' )
                        mapSize--;
                    mapSize++;      /* 要在 ',' 之后 */
                    while(buff.get(mapSize)!='\n'){
                        temp=temp*10+buff.get(mapSize)-'0';
                        mapSize++;
                    }
                    mapSize--;
                    temp=temp/(1000*60);
                }
                mapSize++;
                Thread prepareRes_t=new prepareRes(buff,0,mapSize);
                prepareRes_t.start();
                prepareRess.add(prepareRes_t);
                mapOffset=mapOffset+mapSize+1;
            }
            else{  //最后的一个分块
                Thread prepareRes_t=new prepareRes(buff,0,mapSize);
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
        prepareResponder();
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
        for(int i=0;i<services_len;i++){
            HashMap<Integer,HashMap<Integer,String>> temp_1=new HashMap<>();
            for(int j=mintime;j<=maxtime;j++){
                HashMap<Integer,String> temp_2=new HashMap<>();
                double sum=0;
                for(int k=j;k<=maxtime;k++){
                    sum+=res_map_1.get(String_service[i]).get(k);
                    int size=k-j+1;
                    double res_1=sum/size*100;
                    temp_2.put(k,String.format("%.2f", res_1)+"%");
                }
                temp_1.put(j,temp_2);
            }
            res_map_2.put(String_service[i],temp_1);
        }
    }
    public void prepareResult(String currData){
        /**
         * 这是最小的时间
         */
        int time=testFormat(currData);
        for(int i=0;i<services_len;i++){
            HashMap<Integer,Double> res_map_1_temp=new HashMap<>();
            res_map_1_temp.put(time,services[i][0]*1.0/services[i][1]);
            res_map_1.put(String_service[i],res_map_1_temp);
        }
        for(int j=0;j<String_IP_len;j++){
            int key=String_IP[j].Mainservices;
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
            this.res_map.computeIfAbsent(key, k -> new ArrayList<>()).add(String_IP[j].IPs+","+String.format("%.2f", res_1)+"%,"+P99);
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
