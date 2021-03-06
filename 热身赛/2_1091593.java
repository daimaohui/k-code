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
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        long old=-1;
        long current = 0;
        while ((line = reader.readLine()) != null){
            String[] strs=line.split(",");
            current=Long.valueOf(strs[0]);
            if(current/1000!=old){
                if(old!=-1){
                    prepareResult();
                    timestamp=null;
                }
                old=current/1000;
            }
            if(timestamp==null)
            timestamp=new timestamp();
            timestamp.timestamps=old;
            int index=find(strs[1]);
            if(index!=-1){
                timestamp.methodNames[index].nums.add(Integer.valueOf(strs[2]));
            }else{
                methodName methodName=new methodName();
                methodName._methodName=strs[1];
                methodName.nums.add(Integer.valueOf(strs[2]));
                if(timestamp.methodNames_size==timestamp.max_methodNames_size){
                    timestamp.max_methodNames_size=timestamp.max_methodNames_size+timestamp.max_methodNames_size/2;
                    timestamp.methodNames= Arrays.copyOf(timestamp.methodNames,timestamp.max_methodNames_size);
                }
                timestamp.methodNames[timestamp.methodNames_size++]=methodName;
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
