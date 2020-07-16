package com.kuaishou.kcode;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author kcode
 * Created on 2020-05-20
 */
public class KcodeQuestion {
    public static int buff_size=8;
    public  static long[] timestamps=new long[buff_size];
    public  static int[][][]  nums=new int[buff_size][69][3120];
    public  static String[][] _methodName=new  String[buff_size][69];
    public  static int[][] nums_size=new int[buff_size][3120];
    public  static int[] methodNames_size=new int[buff_size];

    public  HashMap<String,Integer> map=new HashMap<>();
    public static HashMap<Long,HashMap<String,String>> res_map=new HashMap<>();
    public int index=0;
    /**
     * prepare() 方法用来接受输入数据集，数据集格式参考README.md
     *
     * @param inputStream
     */
    public void prepare(InputStream inputStream) throws IOException {
        //long startTime =  System.currentTimeMillis();
        int max_size=1024*256;
        byte[] buffer=new byte[max_size];
        int timestamp_temp_size=0;
        char cur=',';
        char[] methodNames_temp=new char[20];
        int methodNames_temp_size=0;
        String string_methodNames_temp="";
        int nums_temp=0;
        char old=',';
        int read_flag=0;
        int flag=0;
        int flag_1=0;
        int count=0;
        while ((read_flag=inputStream.read(buffer))!=-1){
            int j=0;
            while(j<read_flag) {
                if (flag == 1) {
                    flag = 0;
                } else {
                    while (j < read_flag && buffer[j] != ',') {
                        timestamp_temp_size++;
                        if(timestamp_temp_size==10){
                            cur=(char)buffer[j];
                        }
                        j++;
                    }
                    if (j >= read_flag) {
                        break;
                    }
                    j++;
                }
                if (flag_1 == 1) {
                    flag_1 = 0;
                } else {
                    while (j < read_flag && buffer[j] != ',') {
                        if( buffer[j] < 0)
                            methodNames_temp[methodNames_temp_size++]=(char)(buffer[j]+256);
                        else
                            methodNames_temp[methodNames_temp_size++]=(char)buffer[j];
                        j++;
                    }
                    if (j >= read_flag) {
                        flag = 1;
                        break;
                    }
                    j++;
                }
                while (j < read_flag && buffer[j] != '\n') {
                    nums_temp = nums_temp * 10 +((char)buffer[j] - '0');
                    j++;
                }
                if (j >= read_flag) {
                    flag = 1;
                    flag_1 = 1;
                    break;
                }
                j++;
                if(cur!=old){
                    if(old!=','){
//                        new prepareResultCallable(timestamps,nums,_methodName,nums_size,methodNames_size,index).start();
                        new prepareResultCallable(index).start();
                        map.clear();
                        index=(index+1)%buff_size;
                        methodNames_size[index]=0;
                    }
                    //timestamps[index]=1587987930+count;
                    timestamps[index]=1557987930+count;
                    old=cur;
                    count++;
                }
                string_methodNames_temp = String.valueOf(methodNames_temp).substring(0,methodNames_temp_size);
                if(map.containsKey(string_methodNames_temp)){
                    int i=map.get(string_methodNames_temp);
                    nums[index][i][nums_size[index][i]++]=nums_temp;
                }else{
                    nums_size[index][methodNames_size[index]]=0;
                    nums[index][methodNames_size[index]][nums_size[index][methodNames_size[index]]++]=nums_temp;
                    _methodName[index][methodNames_size[index]]=string_methodNames_temp;
                    map.put(string_methodNames_temp,methodNames_size[index]);
                    methodNames_size[index]++;
                }
                methodNames_temp_size=0;
                timestamp_temp_size=0;
                nums_temp=0;
            }
        }
//        System.gc();
//        System.runFinalization();
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

}
