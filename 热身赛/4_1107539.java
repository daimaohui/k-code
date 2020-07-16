package com.kuaishou.kcode;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        ArrayList<methodName> methodNames=new ArrayList<>();
        HashMap<String,Integer> map=new HashMap<>();
    }
    public HashMap<String,String> res_map=new HashMap<>();
    public timestamp timestamp=null;
    /**
     * prepare() 方法用来接受输入数据集，数据集格式参考README.md
     *
     * @param inputStream
     */
    public void prepare(InputStream inputStream) throws IOException{
        int max_size=1024*1024;
        byte[] buffer=new byte[max_size];
        int read_flag=0;
        long old=-1;
        long timestamp_temp=0;
        StringBuffer methodNames_temp=new StringBuffer();
        int nums_temp=0;
        int flag=0;
        int flag_1=0;
        long sum=0;
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
                        methodNames_temp.append((char) buffer[j]);
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
                        //long endTime_1 =  System.currentTimeMillis();
                        prepareResult();
                        //long endTime =  System.currentTimeMillis();
                        //sum+=endTime-endTime_1;
                        timestamp=null;
                    }
                    old=timestamp_temp;
                }
                if(timestamp==null)
                    timestamp=new timestamp();
                timestamp.timestamps=old;
                String string_methodNames_temp=methodNames_temp.toString();
                if(timestamp.map.containsKey(string_methodNames_temp)){
                    int i=timestamp.map.get(string_methodNames_temp);
                    methodName methodName=timestamp.methodNames.get(i);
                    methodName.nums.add(nums_temp);
                }else{
                    methodName methodName=new methodName();
                    methodName._methodName=string_methodNames_temp;
                    methodName.nums.add(nums_temp);
                    timestamp.methodNames.add(methodName);
                    timestamp.map.put(string_methodNames_temp,timestamp.methodNames.size()-1);
                }
                timestamp_temp=0;
                methodNames_temp=new StringBuffer();
                nums_temp=0;
            }
        }
        //long endTime_1 =  System.currentTimeMillis();
        prepareResult();
        //long endTime =  System.currentTimeMillis();
        //sum+=endTime-endTime_1;
        //System.out.println((sum)+"ms");
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
        StringBuffer key=new StringBuffer();
        key.append(timestamp);
        key.append(methodName);
        return res_map.get(key.toString());
    }
    public void prepareResult(){
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
            res_map.put(key.toString(),res.toString());
        }
    }
}
