package com.kuaishou.kcode;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author kcode
 * Created on 2020-05-20
 */
public class KcodeQuestion {
    public class methodName{
        String _methodName;
        int max_nums_size=2000;
        int[] nums=new int[max_nums_size];
        int nums_size=0;
        int sum=0;
    }
    public class timestamp {     //内部类
        long timestamps;
        methodName[] methodNames=new methodName[256];
        int methodNames_size=0;
    }
    public HashMap<Long,HashMap<String,String>> res_map=new HashMap<>();
    public timestamp timestamp=null;
    int find(String _methodName){
        for(int i=timestamp.methodNames_size-1;i>=0;i--){
            if(timestamp.methodNames[i]._methodName.equals(_methodName)){
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
        int max_size=1024*256;
        byte[] buffer=new byte[max_size];
        int read_flag=0;
        long old=-1;
        long timestamp_temp=0;
        StringBuffer methodNames_temp=new StringBuffer();
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
                        prepareResult();
                        timestamp=null;
                    }
                    old=timestamp_temp;
                }
                if(timestamp==null){
                    timestamp=new timestamp();
                    timestamp.timestamps=old;
                }
                String string_methodNames_temp=methodNames_temp.toString();
                int index=find(string_methodNames_temp);
                if(index!=-1){
                    methodName methodName=timestamp.methodNames[index];
                    if(methodName.max_nums_size==methodName.nums_size){
                        methodName.max_nums_size=methodName.max_nums_size*2;
                        methodName.nums= Arrays.copyOf(methodName.nums,methodName.max_nums_size);
                    }
                    methodName.nums[methodName.nums_size++]=nums_temp;
                    methodName.sum+=nums_temp;
                }else{
                    methodName methodName=new methodName();
                    methodName._methodName=string_methodNames_temp;
                    methodName.nums[methodName.nums_size++]=nums_temp;
                    methodName.sum+=nums_temp;
                    timestamp.methodNames[timestamp.methodNames_size++]=methodName;
                }
                timestamp_temp=0;
                methodNames_temp=new StringBuffer();
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

        return res_map.get(timestamp).get(methodName);
    }
    public void prepareResult(){
        HashMap<String,String> res_Hashmap=new HashMap<>();
        for(int j=0;j<timestamp.methodNames_size;j++){
            methodName a=timestamp.methodNames[j];
            Arrays.sort(a.nums,0,a.nums_size);
            int QFS=a.nums_size;
            int sum=a.sum;
            int max=a.nums[QFS-1];
            int AVG=0;
            int P50=0;
            int  P99=0;
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
                P50=a.nums[i-1];
                i=QFS-QFS/100;
                P99=a.nums[i-1];
            }

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
            res_Hashmap.put(a._methodName,res.toString());
        }
        res_map.put(timestamp.timestamps,res_Hashmap);
    }
}
