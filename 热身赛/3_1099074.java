package com.kuaishou.kcode;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author kcode
 * Created on 2020-05-20
 */
public class KcodeQuestion {
    public class methodName{
        String _methodName;
        ArrayList<Integer> nums=new ArrayList<>();
    }
    public class timestamp {     //内部类
        long timestamps;
        int max_methodNames_size=300;
        methodName[]  methodNames=new methodName[max_methodNames_size];
        int methodNames_size=0;
    }
    public HashMap<String,String> res_map=new HashMap<>();
    public timestamp timestamp=null;

    /**
     * 用来进行查找
     * @param res
     * @return
     */
    public int find(String res){
        for(int i=0;i<timestamp.methodNames_size;i++){
            if(res.equals(timestamp.methodNames[i]._methodName)){
                return i;
            }
        }
        return -1;
    }
    /**
     * prepare() 方法用来接受输入数据集，数据集格式参考README.md
     *
     * @param inputStream
     */
    public void prepare(InputStream inputStream) throws IOException{
        int max_size=1024*1024*100;
        byte[] buffer=new byte[max_size];
        int read_flag=0;
        long old=-1;
        long timestamp_temp=0;
        String methodNames_temp="";
        int nums_temp=0;
        int flag=0;
        int flag_1=0;
        while ((read_flag=inputStream.read(buffer))!=-1){
            int j=0;
            while(j<read_flag) {
                if (flag == 1) {
                    flag = 0;
                } else {
                    while (j < read_flag && buffer[j] != ',') {
                        timestamp_temp = timestamp_temp * 10 + (int) ((char)buffer[j] - '0');
                        j++;
                    }
                    if (j >= read_flag) {
                        break;
                    }
                    timestamp_temp = timestamp_temp / 1000;
                    j++;
                }
                if (flag_1 == 1) {
                    flag_1 = 0;
                } else {
                    while (j < read_flag && buffer[j] != ',') {
                        methodNames_temp = methodNames_temp + ((char) buffer[j]);
                        j++;
                    }
                    if (j >= read_flag) {
                        flag = 1;
                        break;
                    }
                    j++;
                }
                while (j < read_flag && buffer[j] != '\n') {
                    nums_temp = nums_temp * 10 + (int) ((char)buffer[j] - '0');
                    j++;
                }
                if (j >= read_flag) {
                    flag = 1;
                    flag_1 = 1;
                    break;
                }
                j++;
                if(timestamp_temp!=old){
                    if(old!=-1){
                        prepareResult();
                        timestamp=null;
                    }
                    old=timestamp_temp;
                }
                if(timestamp==null)
                    timestamp=new timestamp();
                timestamp.timestamps=old;
                int index=find(methodNames_temp);
                if(index!=-1){
                    timestamp.methodNames[index].nums.add(nums_temp);
                }else{
                    methodName methodName=new methodName();
                    methodName._methodName=methodNames_temp;
                    methodName.nums.add(nums_temp);
                    if(timestamp.methodNames_size==timestamp.max_methodNames_size){
                        timestamp.max_methodNames_size=timestamp.max_methodNames_size+timestamp.max_methodNames_size/2;
                        timestamp.methodNames= Arrays.copyOf(timestamp.methodNames,timestamp.max_methodNames_size);
                    }
                    timestamp.methodNames[timestamp.methodNames_size++]=methodName;
                }
                timestamp_temp=0;
                methodNames_temp="";
                nums_temp=0;
            }
        }
        prepareResult();
    }

    /**
     * getResult() 方法是由kcode评测系统调用，是评测程序正确性的一部分，请按照题目要求返回正确数据
     * 输入格式和输出格式参考 README.md
     *
     * @param timestamp 秒级时间戳
     * @param methodName 方法名称
     */
    public String getResult(Long timestamp, String methodName) {
        // do something

        return res_map.get(timestamp.toString()+methodName);
    }
    public void prepareResult(){
        for(int j=0;j<timestamp.methodNames_size;j++){
            methodName a=timestamp.methodNames[j];
            ArrayList<Integer> res=a.nums;
            Collections.sort(res);
            int QFS=res.size();
            int sum=0;
            int max=res.get(QFS-1);
            int AVG=0;
            int P50=0;
            int  P99=0;
            for(int b:res){
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
                P50=res.get(i-1);
                i=QFS-QFS/100;
                P99=res.get(i-1);
            }
            String key=timestamp.timestamps+a._methodName;
            res_map.put(key,QFS+","+P99+","+P50+","+AVG+","+ max);
        }
    }
}
