package com.kuaishou.kcode;

import java.util.*;

/**
 * 这里包含了一分钟的所有数据
 */
public class TimeData {
    Map<String, PairData> pairDataMap = new HashMap<>();
    Map<String,PairDataV2> pairDataV2Map=new HashMap<>();
    HashSet<String> methodAndIpPairStringSet = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    String[] methodAndIpPairStringArray = null;
    HashSet<String> callers = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    String[] callersArray = null;
    HashSet<String> responders = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    String[] respondersArray = null;
    HashSet<String> callerAndresponder = new HashSet<>();/*都是为了访问方便，其实可以使用数组*/
    String[] callerAndresponderArray = null;
    Map<String,List<TimeIPPair>> timeIPPairs=new HashMap<>();
    public void addPairData( int startTime,String caller, String callerIP, String responder, String responderIP,
                             int elapsedTime,  int success) {
        String methodAndIpPairString=caller + responder + "|" + callerIP + "," + responderIP;
        methodAndIpPairStringSet.add(methodAndIpPairString);
        callers.add(caller);
        responders.add(responder);
        callerAndresponder.add(caller + responder);
        PairData pairData =
                this.pairDataMap.computeIfAbsent(methodAndIpPairString, k -> new  PairData(startTime,caller,callerIP,responder,responderIP));
        pairData.elapsedTimes.add(elapsedTime); /* 耗时 */
        pairData.callTotal++;                   /* 调用总次数 */
        if(success==1) pairData.trueCount++;       /* 成功调用总次数 */
        PairDataV2 pairDataV2=this.pairDataV2Map.computeIfAbsent(caller+responder,k->new PairDataV2(startTime,caller,responder));
        pairDataV2.elapsedTimes.add(elapsedTime);
        pairDataV2.callTotal++;
        if(success==1) pairDataV2.trueCount++;
    }

    /**
     * 计算结果，保存在当前的一分钟里面
     */
    public void getresult( Map<String,ArrayList<Weight>> Node_f,Map<String,ArrayList<Weight>> Node_s){
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
            SRAndP99 srAndP99=pairDataV2.getresult();
            /**
             * 开始构图，
             */
            /**
             * 向前图
             */
            if(Node_f.containsKey(pairDataV2.caller)){
                ArrayList<Weight> weights=Node_f.get(pairDataV2.caller);
                weights.add(new Weight(pairDataV2.responder,srAndP99,pairDataV2.startTime));

            }
            else{
                ArrayList<Weight> weights=new ArrayList<>();
                weights.add(new Weight(pairDataV2.responder,srAndP99,pairDataV2.startTime));
                Node_f.put(pairDataV2.caller,weights);
            }
            String s1="MUU1MacServerDryrun2";
            String s2="N12lcFlexDBServer";
            if((pairDataV2.caller+pairDataV2.responder).equals(s1+s2)&&pairDataV2.startTime==26569526){
                ArrayList<Weight> weights=Node_f.get(pairDataV2.caller);
                for(Weight w:weights){
                    if(w.serviceName.equals(pairDataV2.responder)){
                        System.out.println(w.SRAndP99Map.get(pairDataV2.startTime).P99);
                    }
                }

            }

//            Node_f.computeIfAbsent(pairDataV2.caller,k->new ArrayList<Weight>()).add(new Weight(pairDataV2.responder,srAndP99,pairDataV2.startTime));
            /**
             * 向后图
             */
//            if(Node_s.containsKey(timeIPPair.responder)){
//                ArrayList<Weight> weights=Node_s.get(timeIPPair.responder);
//                weights.add(new Weight(timeIPPair.caller,new SRAndP99(timeIPPair.RSStr,timeIPPair.P99),timeIPPair.startTime));
//            }else{
//                ArrayList<Weight> weights=new ArrayList<>();
//                weights.add(new Weight(timeIPPair.caller,new SRAndP99(timeIPPair.RSStr,timeIPPair.P99),timeIPPair.startTime));
//                Node_s.put(timeIPPair.responder,weights);
//            }
            Node_s.computeIfAbsent(pairDataV2.responder,k->new ArrayList<Weight>()).add(new Weight(pairDataV2.caller,srAndP99,pairDataV2.startTime));
        }
        pairDataMap.clear();
        methodAndIpPairStringSet.clear();
        callerAndresponder.clear();
        callers.clear();
        responders.clear();
    }
}
