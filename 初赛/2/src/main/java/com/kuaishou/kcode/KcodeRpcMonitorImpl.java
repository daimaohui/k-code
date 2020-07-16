package com.kuaishou.kcode;

import org.omg.PortableInterceptor.INACTIVE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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
    HashMap<String,Service> sum=new HashMap<>();
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
    }
    public void prepare(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        int max_size=1024*1024*512;
        byte[] buffer=new byte[max_size];
        String MainIP_temp="";
        String TransIP_temp="";
        int result=0;
        String data="";
        int old=-1;
        int read_flag=0;
        char[] line=new char[100];
        int line_len=0;
        String string_line="";
        int[] spilt=new int[6];
        int spilt_len=0;
        int timestamp=0;
        while ((read_flag=inputStream.read(buffer))!=-1){
            for(int i=0;i<read_flag;i++){
                if(buffer[i]!='\n'){
                    if (buffer[i] == ',') {
                        spilt[spilt_len++]=line_len;
                    }
                    line[line_len++]=(char)buffer[i];
                }
                else{
                    string_line=String.valueOf(line);
                    if(line[spilt[3]+1]=='t'){
                        result=1;
                    }
                    else{
                        result=0;
                    }
                    int String_MainService_temp=0;
                    int TransService_temp=0;
                    int nums_temp=0;
                    long timestamp_temp=0;
                    for(int j=spilt[1]+1;j<spilt[2];j++){
                        TransService_temp=TransService_temp*31+line[j];
                    }
                    String_MainService_temp=TransService_temp;
                    for(int j=0;j<spilt[0];j++){
                        String_MainService_temp=String_MainService_temp*31+line[j];
                    }
                    MainIP_temp=string_line.substring(spilt[0]+1,spilt[1]);
                    TransIP_temp=string_line.substring(spilt[2]+1,spilt[3]);
                    for(int j=spilt[4]+1;j<spilt[5];j++){
                        nums_temp=nums_temp*10+line[j]-'0';
                    }
                    for(int j=spilt[5]+1;j<line_len;j++){
                        timestamp_temp=timestamp_temp*10+line[j]-'0';
                    }
                    timestamp=(int)(timestamp_temp/(1000*60));
                    if(timestamp!=old){
                        if(old!=-1){
//                           System.out.println(services_len);
//                           System.out.println(data);
                            prepareResult(data,old);
                            data=simpleDateFormat.format(timestamp_temp);
                        }
                        if(old==-1){
                            mintime=timestamp;
                            data=simpleDateFormat.format(timestamp_temp);

                        }
                        old=timestamp;
                        find_services.clear();
                        find_IP.clear();
                        find_Mainservices.clear();
                        services_len=0;
                        String_Mainservices_len=0;
                    }
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
                    if(find_Mainservices.containsKey(String_MainService_temp)){
                        int index_1=find_Mainservices.get(String_MainService_temp);
                        if(find_IP.get(String_MainService_temp).containsKey(MainIP_temp+","+TransIP_temp)){
                            int index_2=find_IP.get(String_MainService_temp).get(MainIP_temp+","+TransIP_temp);
                            if(Util[index_1][index_2].size==Util[index_1][index_2].max_size){
                                Util[index_1][index_2].max_size=Util[index_1][index_2].max_size*2;
                                Util[index_1][index_2].nums= Arrays.copyOf(Util[index_1][index_2].nums, Util[index_1][index_2].max_size);
//                                    sum++;
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
                            find_IP.get(String_MainService_temp).put(MainIP_temp+","+TransIP_temp,String_IP_len[index_1]);
                            String_IP_len[index_1]++;
                        }
                    }
                    else{
                        String_Mainservices[String_Mainservices_len]=String_MainService_temp;
                        String_IP_len[String_Mainservices_len]=0;
                        String_IP[String_Mainservices_len][String_IP_len[String_Mainservices_len]]=MainIP_temp+","+TransIP_temp;
                        Utils utils_temp=new Utils();
                        if(result==1) utils_temp.IsTrue++;
                        utils_temp.nums[utils_temp.size++]=nums_temp;
                        Util[String_Mainservices_len][String_IP_len[String_Mainservices_len]]=utils_temp;
                        find_Mainservices.put(String_MainService_temp,String_Mainservices_len);
                        HashMap<String,Integer> temp_hash=new HashMap<>();
                        temp_hash.put(MainIP_temp+","+TransIP_temp,String_IP_len[String_Mainservices_len]);
                        find_IP.put(String_MainService_temp,temp_hash);
                        String_IP_len[String_Mainservices_len]++;
                        String_Mainservices_len++;
                    }
                    spilt_len=0;
                    line_len=0;
                }
            }

        }
//        System.out.println(services_len);
//        System.out.println(data);
        prepareResult(data,timestamp);
        maxtime=timestamp;

//        System.gc();
//        System.runFinalization();
    }
    public void prepareResult(String currData,int time){
        HashMap<Integer,Double> res_map_1_temp=new HashMap<>();
        for(int i=0;i<services_len;i++){
            res_map_1_temp.put(String_service[i],services[i][0]*1.0/services[i][1]);
        }
        res_map_1.put(time,res_map_1_temp);
        //System.out.println(currData);
        for(int j=0;j<String_Mainservices_len;j++){
            ArrayList<String> temp=new ArrayList<>();
            for(int k=0;k<String_IP_len[j];k++){
//                    System.out.println(String_IP[i][j][k]+","+Util[i][j][k].res());
                temp.add(String_IP[j][k]+","+Util[j][k].res());
            }
            for(int i=0;i<currData.length();i++){
                String_Mainservices[j]=String_Mainservices[j]*31+currData.charAt(i);
            }
            res_map.put(String_Mainservices[j],temp);
        }
    }
    public List<String> checkPair(String caller, String responder, String time) {
        int str_time=(responder+caller+time).hashCode();
        if(res_map.containsKey(str_time)){
            return res_map.get(str_time);
        }
        else{
            return new ArrayList<String>();
        }

    }

    public String checkResponder(String responder, String start, String end){
        int responder_int=responder.hashCode();
        Date starttime = null;
        Date endtime=null;
        try {
            starttime = simpleDateFormat.parse(start);
            endtime=simpleDateFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int st =(int)(starttime.getTime()/(1000*60));
        int et=(int)(endtime.getTime()/(1000*60));
        if(st<mintime){
            st=mintime;
        }
        if(et>maxtime){
            et=maxtime;
        }
        double sum=0;
        int size=0;
        while(st<=et){
            Object a=res_map_1.get(st).get(responder_int);
            if(a!=null){
                sum+=(double)a;
                size++;
            }
            st++;
        }
        if(size==0){
            return "-1.00%";
        }
        else{
            double res_1=sum/size*100;
            return String.format("%.2f", res_1)+"%";
        }
    }

}
