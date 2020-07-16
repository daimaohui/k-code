package com.kuaishou.kcode;

import com.jayway.jsonpath.internal.function.numeric.Max;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author kcode
 * Created on 2020-05-20
 */
public class KcodeQuestion {
    class methodName{
        String _methodName;
        ArrayList<Integer> nums=new ArrayList<>();
    }
    class timestamp {     //内部类
        long timestamps;
        methodName[] methodNames_index=new methodName[max_timestamps_size];
        String[]  methodNames=null;
        int methodNames_size=0;
        int max_methodNames_size=1024;

    }
    int max_timestamps_size=1024;
    timestamp[] timestamps=new timestamp[max_timestamps_size];
    long[] time=new long[max_timestamps_size];
    int timestamps_size=0;

    /**\
     * 用来查找是否有这种元素
     * @param res
     * @return
     */
    public int find(long res){
        for(int i=0;i<timestamps_size;i++){
            if(res==time[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * 重载
     * @param res
     * @param a
     * @return
     */
    public int find(String res,timestamp a){
        for(int i=0;i<a.methodNames_size;i++){
            if(res.equals(a.methodNames[i])){
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
    public void prepare(InputStream inputStream) {
        try {
            int max_size=1024*1024;
            int count=inputStream.available();
            System.out.println(count);
            long timestamp_temp=0;
            String methodNames_temp="";
            int nums_temp=0;
            int len=count/max_size;
            int len_1=count%max_size;
            byte[] buffer=new byte[max_size];
            int flag=0;
            int flag_1=0;
            int sum=0;
             for(int i=0;i<len;i++){
                inputStream.read(buffer);
                int j=0;
                while(j<max_size){
                       int k=j;
                       if(flag==1){
                           flag=0;
                       }
                       else{
                           while(j<max_size&&buffer[j]!=','){
                               timestamp_temp=timestamp_temp*10+(int)(buffer[j]-'0');
                               j++;
                           }
                           if(j>=max_size){
                               break;
                           }
                           timestamp_temp=timestamp_temp/1000;
                           j++;
                       }
                       if(flag_1==1){
                           flag_1=0;
                       }
                       else{
                           while(j<max_size&&buffer[j]!=','){
                               methodNames_temp=methodNames_temp+((char)buffer[j]);
                               j++;
                           }
                           if(j>=max_size){
                               flag=1;
                               break;
                           }
                           j++;
                       }
                        while(j<max_size&&buffer[j]!='\n') {
                            nums_temp = nums_temp * 10 + (int) (buffer[j] - '0');
                            j++;
                        }
                    if(j>=max_size){
                        flag=1;
                        flag_1=1;
                        break;
                    }
                    j++;
                    int index=find(timestamp_temp);
                    if(index!=-1){
                        timestamp temp=timestamps[index];
                        index=find(methodNames_temp,temp);
                        if(index==-1){
                            if(temp.max_methodNames_size==temp.methodNames_size){
                                temp.max_methodNames_size=temp.max_methodNames_size+temp.max_methodNames_size/2;
                                temp.methodNames=Arrays.copyOf(temp.methodNames,temp.max_methodNames_size);
                                temp.methodNames_index=Arrays.copyOf(temp.methodNames_index,temp.max_methodNames_size);
                            }
                            temp.methodNames[temp.methodNames_size]=methodNames_temp;
                            methodName temp_1=new methodName();
                            temp_1._methodName=methodNames_temp;
                            temp_1.nums.add(nums_temp);
                            temp.methodNames_index[temp.methodNames_size]=temp_1;
                            temp.methodNames_size++;
                        }
                        else{
                            methodName temp_1=temp.methodNames_index[index];
                            temp_1.nums.add(nums_temp);
                        }

                    }
                    else{
                        timestamp temp=new timestamp();
                        temp.timestamps=timestamp_temp;
                        temp.methodNames=new String[temp.max_methodNames_size];
                        temp.methodNames[temp.methodNames_size]=methodNames_temp;
                        methodName temp_1=new methodName();
                        temp_1._methodName=methodNames_temp;
                        temp_1.nums.add(nums_temp);
                        temp.methodNames_index[temp.methodNames_size]=temp_1;
                        temp.methodNames_size++;
                        if(this.max_timestamps_size==this.timestamps_size){
                            max_timestamps_size=max_timestamps_size+max_timestamps_size/2;
                            this.timestamps=Arrays.copyOf(timestamps,max_timestamps_size);
                            this.time=Arrays.copyOf(time,max_timestamps_size);
                        }
                        timestamps[timestamps_size]=temp;
                        time[timestamps_size]=timestamp_temp;
                        timestamps_size++;
                    }
                    timestamp_temp=0;
                    methodNames_temp="";
                    nums_temp=0;
                }
                sum+=j;
            }
             inputStream.read(buffer);
            int j=0;
            while(j<len_1){
                int k=j;
                if(flag==1){
                    flag=0;
                }
                else{
                    while(j<len_1&&buffer[j]!=','){
                        timestamp_temp=timestamp_temp*10+(int)(buffer[j]-'0');
                        j++;
                    }
                    if(j>=len_1){
                        break;
                    }
                    timestamp_temp=timestamp_temp/1000;
                    j++;
                }
                if(flag_1==1){
                    flag_1=0;
                }
                else{
                    while(j<len_1&&buffer[j]!=','){
                        methodNames_temp=methodNames_temp+((char)buffer[j]);
                        j++;
                    }
                    if(j>=len_1){
                        flag=1;
                        break;
                    }
                    j++;
                }
                while(j<len_1&&buffer[j]!='\n') {
                    nums_temp = nums_temp * 10 + (int) (buffer[j] - '0');
                    j++;
                }
                if(j>=len_1){
                    flag=1;
                    flag_1=1;
                    break;
                }
                j++;
                int index=find(timestamp_temp);
                if(index!=-1){
                    timestamp temp=timestamps[index];
                    index=find(methodNames_temp,temp);
                    if(index==-1){
                        if(temp.max_methodNames_size==temp.methodNames_size){
                            temp.max_methodNames_size=temp.max_methodNames_size+temp.max_methodNames_size/2;
                            temp.methodNames=Arrays.copyOf(temp.methodNames,temp.max_methodNames_size);
                            temp.methodNames_index=Arrays.copyOf(temp.methodNames_index,temp.max_methodNames_size);
                        }
                        temp.methodNames[temp.methodNames_size]=methodNames_temp;
                        methodName temp_1=new methodName();
                        temp_1._methodName=methodNames_temp;
                        temp_1.nums.add(nums_temp);
                        temp.methodNames_index[temp.methodNames_size]=temp_1;
                        temp.methodNames_size++;
                    }
                    else{
                        methodName temp_1=temp.methodNames_index[index];
                        temp_1.nums.add(nums_temp);
                    }

                }
                else{
                    timestamp temp=new timestamp();
                    temp.timestamps=timestamp_temp;
                    temp.methodNames=new String[temp.max_methodNames_size];
                    temp.methodNames[temp.methodNames_size]=methodNames_temp;
                    methodName temp_1=new methodName();
                    temp_1._methodName=methodNames_temp;
                    temp_1.nums.add(nums_temp);
                    temp.methodNames_index[temp.methodNames_size]=temp_1;
                    temp.methodNames_size++;
                    if(this.max_timestamps_size==this.timestamps_size){
                        max_timestamps_size=max_timestamps_size+max_timestamps_size/2;
                        this.timestamps=Arrays.copyOf(timestamps,max_timestamps_size);
                        this.time=Arrays.copyOf(time,max_timestamps_size);
                    }
                    timestamps[timestamps_size]=temp;
                    time[timestamps_size]=timestamp_temp;
                    timestamps_size++;
                }
                timestamp_temp=0;
                methodNames_temp="";
                nums_temp=0;
            }
            sum+=j;
            System.out.println(sum);
            System.out.println(timestamps_size);
            int index=find(1587990360);
            timestamp timestamp=timestamps[index];
            index=find("mockUser10",timestamp);
            System.out.println(timestamp.methodNames_index[index].nums.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        int index=find(timestamp);
        if(index==-1){
            System.out.println(timestamp);
            return "0,0,0,0,0";
        }
        timestamp temp=timestamps[index];
        index=find(methodName,temp);
        if (index==-1){
            return "0,0,0,0,0";
        }
        ArrayList<Integer> res=temp.methodNames_index[index].nums;

        Collections.sort(res);
        int QFS=res.size();
        int sum=0;
        int max=res.get(QFS-1);
        int AVG=0;
        int P50=0;
        int  P99=0;
        for(int a:res){
            sum+=a;
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
            if(0.01*QFS>QFS/100){
                i=QFS-QFS/100;
            }
            else {
                i=QFS-QFS/100;
            }
            P99=res.get(i-1);
        }
        String re=QFS+","+P99+","+P50+","+AVG+","+ max;
        return re;
    }
}
