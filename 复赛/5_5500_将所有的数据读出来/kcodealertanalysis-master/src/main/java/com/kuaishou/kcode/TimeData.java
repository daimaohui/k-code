package com.kuaishou.kcode;

import java.util.*;

/**
 * 这里包含了最小一分钟的所有数据
 */
public class TimeData {
    Map<String, PairData> pairDataMap = new HashMap<>(32);
    Map<String,PairDataV2> pairDataV2Map=new HashMap<>(32);
    HashSet<String> methodAndIpPairStringSet = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    String[] methodAndIpPairStringArray = null;
    HashSet<String> callers = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    HashSet<String> responders = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    HashSet<String> callerAndresponder = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    String[] callerAndresponderArray = null;
    Map<String,List<TimeIPPair>> timeIPPairs=new HashMap<>();
    public void addPairData( int startTime,String caller, String callerIP, String responder, String responderIP,
                             int elapsedTime,  int success,int flag) {
            String methodAndIpPairString=caller + responder + "|" + callerIP + "," + responderIP;
            methodAndIpPairStringSet.add(methodAndIpPairString);
            callers.add(caller);
            responders.add(responder);
            callerAndresponder.add(caller + responder);
            PairData pairData =
                    this.pairDataMap.computeIfAbsent(methodAndIpPairString, k -> new  PairData(startTime,caller,callerIP,responder,responderIP));
            if(pairData.callTotal==pairData.maxsize){
                pairData.maxsize=pairData.maxsize*2;
                pairData.elapsedTimes=Arrays.copyOf(pairData.elapsedTimes,pairData.maxsize);
            }
            pairData.elapsedTimes[pairData.callTotal++]=elapsedTime; /* 耗时 */ /* 调用总次数 */
            if(success==1) pairData.trueCount++;       /* 成功调用总次数 */
            PairDataV2 pairDataV2=this.pairDataV2Map.computeIfAbsent(caller+responder,k->new PairDataV2(startTime,caller,responder));
            if(pairDataV2.callTotal==pairDataV2.maxsize){
                pairDataV2.maxsize=pairDataV2.maxsize*2;
                pairDataV2.elapsedTimes=Arrays.copyOf(pairDataV2.elapsedTimes,pairDataV2.maxsize);
            }
            pairDataV2.elapsedTimes[pairDataV2.callTotal++]=elapsedTime;
            if(success==1) pairDataV2.trueCount++;

    }
    public void addPairData( int startTime,String caller, String callerIP, String responder, String responderIP,
                             int elapsedTime,  int success) {
        String methodAndIpPairString=caller + responder + "|" + callerIP + "," + responderIP;
        methodAndIpPairStringSet.add(methodAndIpPairString);
        callerAndresponder.add(caller + responder);
        PairData pairData =
                this.pairDataMap.computeIfAbsent(methodAndIpPairString, k -> new  PairData(startTime,caller,callerIP,responder,responderIP));
        if(pairData.callTotal==pairData.maxsize){
            pairData.maxsize=pairData.maxsize*2;
            pairData.elapsedTimes= Arrays.copyOf(pairData.elapsedTimes,pairData.maxsize);
        }
        pairData.elapsedTimes[pairData.callTotal++]=elapsedTime; /* 耗时 */ /* 调用总次数 */
        if(success==1) pairData.trueCount++;       /* 成功调用总次数 */
        PairDataV2 pairDataV2=this.pairDataV2Map.computeIfAbsent(caller+responder,k->new PairDataV2(startTime,caller,responder));
        if(pairDataV2.callTotal==pairDataV2.maxsize){
            pairDataV2.maxsize=pairDataV2.maxsize*2;
            pairDataV2.elapsedTimes=Arrays.copyOf(pairDataV2.elapsedTimes,pairDataV2.maxsize);
        }
        pairDataV2.elapsedTimes[pairDataV2.callTotal++]=elapsedTime;
        if(success==1) pairDataV2.trueCount++;
    }
    /**
     * 计算结果，保存在当前的一分钟里面
     */
    public String[][] getresult(Map<String,HashMap<String, HashMap<Integer,String[]>>> Node_f,Map<String,HashMap<String, HashMap<Integer,String[]>>> Node_s,String[] callersArray,String[] respondersArray){
        /* 将所有set转换为数组 */
        int methodAndIpPairSize = methodAndIpPairStringSet.size();
        methodAndIpPairStringArray
                = methodAndIpPairStringSet.toArray(new String[methodAndIpPairSize]);
        int cllersize=callers.size();
        callersArray=callers.toArray(new String[cllersize]);
        int respondersize=responders.size();
        respondersArray=responders.toArray(new String[respondersize]);
        int callerAndrespondersize=callerAndresponder.size();
        callerAndresponderArray=callerAndresponder.toArray(new String[callerAndrespondersize]);
        /* 计算 */
        for(String mp : methodAndIpPairStringArray) {
            PairData pairData=pairDataMap.get(mp);
            List<TimeIPPair> timeIPPair=this.timeIPPairs.computeIfAbsent(pairData.caller+pairData.responder,k->new ArrayList<>());
            timeIPPair.add(pairData.getresult());
        }
        for(String mp:callerAndresponderArray){
            PairDataV2 pairDataV2=pairDataV2Map.get(mp);
            String[] srAndP99=pairDataV2.getresult();
            /**
             * 开始构图，
             */
            /**
             * 向前图
             */
            HashMap<String, HashMap<Integer,String[]>> SRAndP99Map=Node_f.computeIfAbsent(pairDataV2.caller,k->new HashMap<>());
            HashMap<Integer,String[]> SRAndP99MapV1=SRAndP99Map.computeIfAbsent(pairDataV2.responder,k->new HashMap<>());
            SRAndP99MapV1.put(pairDataV2.startTime,srAndP99);
            /**
             * 向后图
             */
            HashMap<String, HashMap<Integer,String[]>> SRAndP99Map1=Node_s.computeIfAbsent(pairDataV2.responder,k->new HashMap<>());
            HashMap<Integer,String[]> SRAndP99Map1V1=SRAndP99Map1.computeIfAbsent(pairDataV2.caller,k->new HashMap<>());
            SRAndP99Map1V1.put(pairDataV2.startTime,srAndP99);
        }
        pairDataMap.clear();
        methodAndIpPairStringSet.clear();
        callerAndresponder.clear();
        callers.clear();
        responders.clear();
        pairDataV2Map.clear();
        String[][] res=new String[2][];
        res[0]=callersArray;
        res[1]=respondersArray;
        return res;
    }
    public void getresult(Map<String,HashMap<String, HashMap<Integer,String[]>>> Node_f,Map<String,HashMap<String, HashMap<Integer,String[]>>> Node_s){
        /* 将所有set转换为数组 */
        int methodAndIpPairSize = methodAndIpPairStringSet.size();
        methodAndIpPairStringArray
                = methodAndIpPairStringSet.toArray(new String[methodAndIpPairSize]);
        int callerAndrespondersize=callerAndresponder.size();
        callerAndresponderArray=callerAndresponder.toArray(new String[callerAndrespondersize]);
        /* 计算 */
        for(String mp : methodAndIpPairStringArray) {
            PairData pairData=pairDataMap.get(mp);
            List<TimeIPPair> timeIPPair=this.timeIPPairs.computeIfAbsent(pairData.caller+pairData.responder,k->new ArrayList<>());
            timeIPPair.add(pairData.getresult());
        }
        for(String mp:callerAndresponderArray){
            PairDataV2 pairDataV2=pairDataV2Map.get(mp);
            String[] srAndP99=pairDataV2.getresult();
            /**
             * 开始构图，
             */
            /**
             * 向前图
             */
            HashMap<String, HashMap<Integer,String[]>> SRAndP99Map=Node_f.computeIfAbsent(pairDataV2.caller,k->new HashMap<>());
            HashMap<Integer,String[]> SRAndP99MapV1=SRAndP99Map.computeIfAbsent(pairDataV2.responder,k->new HashMap<>());
            SRAndP99MapV1.put(pairDataV2.startTime,srAndP99);
            /**
             * 向后图
             */
            HashMap<String, HashMap<Integer,String[]>> SRAndP99Map1=Node_s.computeIfAbsent(pairDataV2.responder,k->new HashMap<>());
            HashMap<Integer,String[]> SRAndP99Map1V1=SRAndP99Map1.computeIfAbsent(pairDataV2.caller,k->new HashMap<>());
            SRAndP99Map1V1.put(pairDataV2.startTime,srAndP99);
        }
        pairDataMap.clear();
        methodAndIpPairStringSet.clear();
        callerAndresponder.clear();
        callers.clear();
        responders.clear();
        pairDataV2Map.clear();

    }
}
