package com.kuaishou.kcode;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Collection;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author KCODE
 * Created on 2020-07-04
 */
public class KcodeAlertAnalysisImpl implements KcodeAlertAnalysis {
    Answer answer=new Answer();
    @Override
    public Collection<String> alarmMonitor(String path, Collection<String> alertRules) throws IOException {
        long startNs = nanoTime();
        /* 添加规则 */
        for(String e:alertRules){
            answer.addRules(e);
        }
        RandomAccessFile raf = new RandomAccessFile (path, "r");
        FileChannel channel = raf.getChannel();
        long fileSize = channel.size();
        long mapOffset = 0;
        MappedByteBuffer buff=null;
        int mapSize=0;
        int old=-1;
        int mintime=0;
        int maxtime=0;
        String caller;      /* 调用方 */
        String callerIP;    /* 调用方 IP */
        String responder;      /* 被调用目标方 */
        String responderIP;    /* 被调用目标方 IP */
        int success=0;      /* 调用成功？ */
        char[] caller_char=new char[50];
        int caller_char_size=0;
        char[] responder_char=new char[50];
        int responder_char_size=0;
        char[] callerIP_char=new char[30];
        int callerIP_char_size=0;
        char[] responderIP_char=new char[30];
        int responderIP_char_size=0;
        while(mapOffset<fileSize) {
            mapSize = fileSize - mapOffset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) (fileSize - mapOffset);
            buff = channel.map(FileChannel.MapMode.READ_ONLY, mapOffset, mapSize);
            mapSize--;
            while (buff.get(mapSize) != 10) {
                mapSize--;
            }
            mapSize++;
            int offset = 0;
            while (offset < mapSize) {
                /**
                 * 得到数据
                 */
                int elapsedTime = 0;    /* 调用耗时 */
                int startTime;      /* 调用开始时间 */
                long startTime_temp = 0;
                while (buff.get(offset) != ',') {
                    caller_char[caller_char_size++] = (char) buff.get(offset);
                    offset++;
                }
                caller = String.valueOf(caller_char).substring(0, caller_char_size);
                caller_char_size = 0;
                offset++;
                while (buff.get(offset) != ',') {
                    callerIP_char[callerIP_char_size++] = (char) buff.get(offset);
                    offset++;
                }
                callerIP = String.valueOf(callerIP_char).substring(0, callerIP_char_size);
                callerIP_char_size = 0;
                offset++;
                while (buff.get(offset) != ',') {
                    responder_char[responder_char_size++] = (char) buff.get(offset);
                    offset++;
                }
                responder = String.valueOf(responder_char).substring(0, responder_char_size);
                responder_char_size = 0;
                offset++;
                while (buff.get(offset) != ',') {
                    responderIP_char[responderIP_char_size++] = (char) buff.get(offset);
                    offset++;
                }
                responderIP = String.valueOf(responderIP_char).substring(0, responderIP_char_size);
                responderIP_char_size = 0;
                offset++;
                int flag = 1;
                while (buff.get(offset) != ',') {
                    if (flag == 1) {
                        if (buff.get(offset) == 't') {
                            success = 1;
                        } else {
                            success = 0;
                        }
                        flag = 0;
                    }
                    offset++;
                }
                offset++;
                while (buff.get(offset) != ',') {
                    elapsedTime = elapsedTime * 10 + buff.get(offset) - '0';
                    offset++;
                }
                offset++;
                while (buff.get(offset) != '\n') {
                    startTime_temp = startTime_temp * 10 + buff.get(offset) - '0';
                    offset++;
                }
                offset++;
                startTime = (int) (startTime_temp / (1000 * 60));
                if(old==-1){
                    answer.setMintime(startTime);
                    old=startTime;
                    mintime=old;
                }
                if(old!=startTime){
                    old=startTime;
                    if(old==mintime+2){
                        answer.getresult(mintime);
                        mintime++;
                    }
                }
                maxtime=maxtime<startTime?startTime:maxtime;
                answer.addPairData(startTime,caller,callerIP,responder,responderIP,elapsedTime,success);
            }
            mapOffset=mapOffset+offset;
        }
        while (mintime<=maxtime){
            answer.getresult(mintime);
            mintime++;
        }
        System.out.println("prepare 耗时(ms):" + NANOSECONDS.toMillis(nanoTime() - startNs));
        return answer.alarmMonitorres;
    }

    @Override
    public Collection<String> getLongestPath(String caller, String responder, String time, String type) {
        try {
            return answer.getLongestPathRes(caller,responder,time,type);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}