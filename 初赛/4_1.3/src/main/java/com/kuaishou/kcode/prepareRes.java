package com.kuaishou.kcode;

import java.nio.MappedByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.kuaishou.kcode.KcodeRpcMonitorImpl.testFormat;

public class prepareRes extends Thread{
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
    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    MappedByteBuffer buff;
    int offset;
    int mapSize;
    public prepareRes(MappedByteBuffer buff,int offset,int mapSize){
        this.buff=buff;
        this.offset=offset;
        this.mapSize=mapSize;
    }
    @Override
    public void run() {
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
        while(offset<mapSize) {
            /**
             * 得到数据
             */
            int MainService_temp = 0;
            int TransService_temp = 0;
            int nums_temp = 0;
            long timestamp_temp = 0;
            while (buff.get(offset) != ',') {
                MainService_temp = MainService_temp * 31 + buff.get(offset);
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                MainIP_temp_char[MainIP_temp_char_size++] = (char) buff.get(offset);
                offset++;
            }
            MainIP_temp = String.valueOf(MainIP_temp_char).substring(0, MainIP_temp_char_size);
            MainIP_temp_char_size = 0;
            offset++;
            while (buff.get(offset) != ',') {
                TransService_temp = TransService_temp * 31 + buff.get(offset);
                MainService_temp = MainService_temp * 31 + buff.get(offset);
                offset++;
            }
            offset++;
            while (buff.get(offset) != ',') {
                TransIP_temp_char[TransIP_temp_char_size++] = (char) buff.get(offset);
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
                    data = simpleDateFormat.format(timestamp_temp);
                }
                if (old == -1) {
                    data = simpleDateFormat.format(timestamp_temp);
                    //throw new RuntimeException(data);
                }
                old = timestamp;
                find_services.clear();
                find_IP.clear();
                find_Mainservices.clear();
                services_len = 0;
                String_Mainservices_len = 0;
            }
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
            if (find_Mainservices.containsKey(MainService_temp)) {
                int index_1 = find_Mainservices.get(MainService_temp);
                if (find_IP.get(MainService_temp).containsKey(MainIP_temp + "," + TransIP_temp)) {
                    int index_2 = find_IP.get(MainService_temp).get(MainIP_temp + "," + TransIP_temp);
                    if (Util[index_1][index_2].size == Util[index_1][index_2].max_size) {
                        Util[index_1][index_2].max_size = Util[index_1][index_2].max_size * 2;
                        Util[index_1][index_2].nums = Arrays.copyOf(Util[index_1][index_2].nums, Util[index_1][index_2].max_size);
                    }
                    Util[index_1][index_2].nums[Util[index_1][index_2].size++] = nums_temp;
                    if (result == 1) Util[index_1][index_2].IsTrue++;
                } else {
                    String_IP[index_1][String_IP_len[index_1]] = MainIP_temp + "," + TransIP_temp;
                    Utils utils_temp = new Utils();
                    if (result == 1) utils_temp.IsTrue++;
                    utils_temp.nums[utils_temp.size++] = nums_temp;
                    Util[index_1][String_IP_len[index_1]] = utils_temp;
                    find_IP.get(MainService_temp).put(MainIP_temp + "," + TransIP_temp, String_IP_len[index_1]);
                    String_IP_len[index_1]++;
                }
            } else {
                String_Mainservices[String_Mainservices_len] = MainService_temp;
                String_IP_len[String_Mainservices_len] = 0;
                String_IP[String_Mainservices_len][String_IP_len[String_Mainservices_len]] = MainIP_temp + "," + TransIP_temp;
                Utils utils_temp = new Utils();
                if (result == 1) utils_temp.IsTrue++;
                utils_temp.nums[utils_temp.size++] = nums_temp;
                Util[String_Mainservices_len][String_IP_len[String_Mainservices_len]] = utils_temp;
                find_Mainservices.put(MainService_temp, String_Mainservices_len);
                HashMap<String, Integer> temp_hash = new HashMap<>();
                temp_hash.put(MainIP_temp + "," + TransIP_temp, String_IP_len[String_Mainservices_len]);
                find_IP.put(MainService_temp, temp_hash);
                String_IP_len[String_Mainservices_len]++;
                String_Mainservices_len++;
            }
        }
        prepareResult(data);
    }
    public void prepareResult(String currData){
        int time=testFormat(currData);
        for(int i=0;i<services_len;i++){
            KcodeRpcMonitorImpl.res_map_1.get(String_service[i]).put(time,services[i][0]*1.0/services[i][1]);
        }

        for(int j=0;j<String_Mainservices_len;j++){
            ArrayList<String> temp=new ArrayList<>();
            for(int k=0;k<String_IP_len[j];k++){
                temp.add(String_IP[j][k]+","+Util[j][k].res());
            }
            for(int i=0;i<currData.length();i++){
                String_Mainservices[j]=String_Mainservices[j]*31+currData.charAt(i);
            }
            KcodeRpcMonitorImpl.res_map.put(String_Mainservices[j],temp);
        }
    }
}
