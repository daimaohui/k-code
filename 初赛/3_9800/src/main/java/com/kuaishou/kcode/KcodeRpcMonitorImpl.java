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
    int size_1=70;
    int size_2=5;
    int size_3=63;
    Utils[][] Util=new Utils[size_1*size_2][size_3];
    int[][] services=new int[size_1][2];
    int[] String_service=new int[size_1];
    HashMap<Integer,Integer> find_services=new HashMap<>();
    int[] String_Mainservices=new int[size_1*size_2];
    HashMap<Integer, Integer> find_Mainservices=new HashMap<>();
    int String_Mainservices_len=0;
    String[][] String_IP=new String[size_1*size_2][size_3];
    int[] String_IP_len=new int[size_1*size_2];
    HashMap<Integer, HashMap<String,Integer>> find_IP=new HashMap<>();
    int services_len=0;
    HashMap<Integer,ArrayList<String>> res_map=new HashMap<>();
    HashMap<Integer,HashMap<Integer,Double>> res_map_1=new HashMap<>();
    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    int mintime=0;
    int maxtime=0;
    HashMap<Integer,Double> sum=new HashMap<>();
    HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> res_map_2=new HashMap<>();
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
    }

    public void prepare(String path) throws IOException {
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
        char[] MainIP_temp_char=new char[30];
        int MainIP_temp_char_size=0;
        char[] TransIP_temp_char=new char[30];
        int TransIP_temp_char_size=0;
        int Isfirst=0;
        while(mapOffset<fileSize){
            mapSize=fileSize-mapOffset>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)(fileSize-mapOffset);
            buff= channel.map(FileChannel.MapMode.READ_ONLY,mapOffset,mapSize);
            mapSize--;
            while ( buff.get(mapSize) !=10 ){
                mapSize--;
            }
            mapSize++;
            int offset=0;
            while(offset<mapSize){
                /**
                 * 得到数据
                 */
                int MainService_temp=0;
                int TransService_temp=0;
                int nums_temp=0;
                long timestamp_temp=0;
                while(buff.get(offset)!=','){
                    MainService_temp=MainService_temp*31+buff.get(offset);
                    offset++;
                }
                offset++;
                while(buff.get(offset)!=','){
                    MainIP_temp_char[MainIP_temp_char_size++]=(char)buff.get(offset);
                    offset++;
                }
                MainIP_temp=String.valueOf(MainIP_temp_char).substring(0,MainIP_temp_char_size);
                MainIP_temp_char_size=0;
                offset++;
                while(buff.get(offset)!=','){
                    TransService_temp=TransService_temp*31+buff.get(offset);
                    MainService_temp=MainService_temp*31+buff.get(offset);
                    offset++;
                }
                offset++;
                while(buff.get(offset)!=','){
                    TransIP_temp_char[TransIP_temp_char_size++]=(char)buff.get(offset);
                    offset++;
                }
                TransIP_temp=String.valueOf(TransIP_temp_char).substring(0,TransIP_temp_char_size);
                TransIP_temp_char_size=0;
                offset++;
                int flag=1;
                while(buff.get(offset)!=','){
                    if(flag==1){
                        if(buff.get(offset)=='t'){
                            result=1;
                        }
                        else{
                            result=0;
                        }
                        flag=0;
                    }
                    offset++;
                }
                offset++;
                while(buff.get(offset)!=','){
                    nums_temp=nums_temp*10+buff.get(offset)-'0';
                    offset++;
                }
                offset++;
                while(buff.get(offset)!='\n'){
                    timestamp_temp=timestamp_temp*10+buff.get(offset)-'0';
                    offset++;
                }
                offset++;
                timestamp=(int)(timestamp_temp/(1000*60));
                if(timestamp!=old){
                    if(old!=-1){
                        prepareResult(data,Isfirst);
                        Isfirst=1;
                        data=simpleDateFormat.format(timestamp_temp);
                        System.out.println("111");
                        return;
                    }
                    if(old==-1){
                        data=simpleDateFormat.format(timestamp_temp);
                        mintime=testFormat(data);
                        //throw new RuntimeException(data);
                    }
                    old=timestamp;
                    find_services.clear();
                    find_IP.clear();
                    find_Mainservices.clear();
                    services_len=0;
                    String_Mainservices_len=0;
                }
                System.out.println(offset);
                if(find_services.containsKey(TransService_temp)){
                    int index=find_services.get(TransService_temp);
                    if(result==1) services[index][0]++;
                    services[index][1]++;
                }
                else{
                    String_service[services_len]=TransService_temp;
                    services[services_len][0]=0;
                    services[services_len][1]=0;
                    if(result==1) services[services_len][0]++;
                    services[services_len][1]++;
                    find_services.put(TransService_temp,services_len);
                    services_len++;
                }
                if(find_Mainservices.containsKey(MainService_temp)){
                    int index_1=find_Mainservices.get(MainService_temp);
                    if(find_IP.get(MainService_temp).containsKey(MainIP_temp+","+TransIP_temp)){
                        int index_2=find_IP.get(MainService_temp).get(MainIP_temp+","+TransIP_temp);
                        if(Util[index_1][index_2].size==Util[index_1][index_2].max_size){
                            Util[index_1][index_2].max_size=Util[index_1][index_2].max_size*2;
                            Util[index_1][index_2].nums= Arrays.copyOf(Util[index_1][index_2].nums, Util[index_1][index_2].max_size);
                        }
                        Util[index_1][index_2].nums[Util[index_1][index_2].size++]=nums_temp;
                        if(result==1) Util[index_1][index_2].IsTrue++;
                    }
                    else{
                        String_IP[index_1][String_IP_len[index_1]]=MainIP_temp+","+TransIP_temp;
                        Utils utils_temp=new Utils();
                        if(result==1) utils_temp.IsTrue++;
                        utils_temp.nums[utils_temp.size++]=nums_temp;
                        Util[index_1][String_IP_len[index_1]]=utils_temp;
                        find_IP.get(MainService_temp).put(MainIP_temp+","+TransIP_temp,String_IP_len[index_1]);
                        String_IP_len[index_1]++;
                    }
                }
                else{
                    String_Mainservices[String_Mainservices_len]=MainService_temp;
                    String_IP_len[String_Mainservices_len]=0;
                    String_IP[String_Mainservices_len][String_IP_len[String_Mainservices_len]]=MainIP_temp+","+TransIP_temp;
                    Utils utils_temp=new Utils();
                    if(result==1) utils_temp.IsTrue++;
                    utils_temp.nums[utils_temp.size++]=nums_temp;
                    Util[String_Mainservices_len][String_IP_len[String_Mainservices_len]]=utils_temp;
                    find_Mainservices.put(MainService_temp,String_Mainservices_len);
                    HashMap<String,Integer> temp_hash=new HashMap<>();
                    temp_hash.put(MainIP_temp+","+TransIP_temp,String_IP_len[String_Mainservices_len]);
                    find_IP.put(MainService_temp,temp_hash);
                    String_IP_len[String_Mainservices_len]++;
                    String_Mainservices_len++;
                }
            }
            mapOffset=mapOffset+offset;
        }
        prepareResult(data,Isfirst);
        maxtime=testFormat(data);
        System.out.println(data);
        prepareResponder();
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
                for(int k=j;k<=maxtime;k++){
                    double sum=res_map_1.get(String_service[i]).get(k)-res_map_1.get(String_service[i]).get(j-1);
                    int size=k-j+1;
                    double res_1=sum/size*100;
                    temp_2.put(k,String.format("%.2f", res_1)+"%");
                }
                temp_1.put(j,temp_2);
            }
            res_map_2.put(String_service[i],temp_1);
        }
    }
    public void prepareResult(String currData,int Isfirst){
        /**
         * 这是最小的时间
         */
        int time=testFormat(currData);
        if(Isfirst==0){
            for(int i=0;i<services_len;i++){
                HashMap<Integer,Double> res_map_1_temp=new HashMap<>();
                res_map_1_temp.put(time-1,0.0);
                res_map_1_temp.put(time,services[i][0]*1.0/services[i][1]);
                sum.put(String_service[i],services[i][0]*1.0/services[i][1]);
                res_map_1.put(String_service[i],res_map_1_temp);
            }
        }
        else{
            for(int i=0;i<services_len;i++){
                double temp=sum.get(String_service[i])+services[i][0]*1.0/services[i][1];
                res_map_1.get(String_service[i]).put(time,temp);
                sum.put(String_service[i],temp);
            }
        }
        for(int j=0;j<String_Mainservices_len;j++){
            ArrayList<String> temp=new ArrayList<>();
            for(int k=0;k<String_IP_len[j];k++){
                temp.add(String_IP[j][k]+","+Util[j][k].res());
            }
            for(int i=0;i<currData.length();i++){
                String_Mainservices[j]=String_Mainservices[j]*31+currData.charAt(i);
            }
            res_map.put(String_Mainservices[j],temp);
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
        double sum=0;
        int size=0;
        if(res_map_2.containsKey(responder_int)){
            return res_map_2.get(responder_int).get(st).get(et);
        }
        else{
            return "-1.00%";
        }
    }

}
