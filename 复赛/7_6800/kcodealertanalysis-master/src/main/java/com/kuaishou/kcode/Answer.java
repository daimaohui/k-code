package com.kuaishou.kcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Answer {
    /* 数据映射 */
    Map<Integer, TimeData> timeDataMap = new HashMap<>(64);
    ArrayList<Rule> rules=new ArrayList<>();
    int mintime;
    int maxtime=0;
    /* 时间格式化对象 */
    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /*alarmMonitor中间变量*/
    Map<String,Integer> alarmMonitorrMap=new HashMap<>(64);
    /*alarmMonitorres*/
    Collection<String> alarmMonitorres=new ArrayList<>(2048);
    /*前向图*/
    Map<String,HashMap<String, HashMap<Integer,String[]>>> Node_f=new HashMap<>(64);
    /*后向图*/
    Map<String,HashMap<String, HashMap<Integer,String[]>>> Node_s=new HashMap<>(64);
    /*记忆化*/
    Collection<String>[][][] LongestPathMap;
    String[] callersArray=null;
    String[] respondersArray=null;
    public void setMintime(int mintime) {
        this.mintime = mintime;
    }
    public void setMaxtime(int maxtime) {
        this.maxtime = maxtime;
    }
    String mindate;
    int mindateH;
    int mindatem;
    public int find(String time){
        int H=(time.charAt(11)-'0')*10+(time.charAt(12)-'0');
        int m=(time.charAt(14)-'0')*10+(time.charAt(15)-'0');
        if(H>mindateH){
            return m-mindatem+60;
        }
        return m-mindatem;
    }
    int hashcode=7;
    int index_nums_1;
    int indexmod_1;
    public int find_index_1(String caller){
        int temp=0;
        for(int i=caller.length()-3;i<caller.length();i++){
            temp=temp*hashcode+caller.charAt(i)-'0';
        }
        return Math.abs(temp+indexmod_1)%index_nums_1;
    }
    int index_nums_2;
    int indexmod_2;
    public int find_index_2(String responder){
        int temp=0;
        for(int i=responder.length()-3;i<responder.length();i++){
            temp=temp*hashcode+responder.charAt(i)-'0';
        }
        return Math.abs(temp+indexmod_2)%index_nums_2;
    }
    public void addRules(Collection<String> alertRules){
        String[] split;
        int id;
        String caller;
        String responder;
        String ALERT_TYPE;
        int times;
        char Trigger;
        double threshold;
        for(String RuleStr:alertRules){
            split=RuleStr.split(",");
            id=Integer.valueOf(split[0]);
            caller=split[1];
            responder=split[2];
            ALERT_TYPE=split[3];
            times=Integer.valueOf(split[4].substring(0,split[4].length()-1));
            Trigger=split[4].charAt(split[4].length()-1);
            if(split[5].charAt(split[5].length()-1)=='%'){
                threshold=Double.valueOf(split[5].substring(0,split[5].length()-1));
            }
            else{
                threshold=Integer.valueOf(split[5].substring(0,split[5].length()-2));
            }
            Rule rule=new Rule(id,caller,responder,ALERT_TYPE,times,Trigger,threshold);
            rules.add(rule);
        }
    }
    /**
     *
     * @param startTime
     * @param caller
     * @param callerIP
     * @param responder
     * @param responderIP
     * @param elapsedTime
     * @param success
     */
    public void addPairData(int startTime, String caller, String callerIP, String responder, String responderIP,
                            int elapsedTime,  int success) {
        TimeData timeData =
                this.timeDataMap.computeIfAbsent(startTime, k -> new TimeData());
        if(startTime==mintime){
            timeData.addPairData(startTime,caller,callerIP,responder,responderIP,elapsedTime,success,1);
        }
        else{
            timeData.addPairData(startTime,caller,callerIP,responder,responderIP,elapsedTime,success);
        }

    }
    public void getresult(int startTime){
        TimeData timeData =timeDataMap.get(startTime);
        /**
         * 一遍计算第一问的结果，一遍构建第二问的图
         */
        if(startTime==mintime){
            String[][] res=timeData.getresult(Node_f,Node_s,callersArray,respondersArray); /* 调用内部方法进行计算结果 */
            callersArray=res[0];
            respondersArray=res[1];
        }
        else{
            timeData.getresult(Node_f,Node_s); /* 调用内部方法进行计算结果 */

        }
        /*
            调用一个方法来去判断规则
         */
        long data=(long)startTime*1000*60;
        getrule(timeData,simpleDateFormat.format(data));
        if(startTime==maxtime){
            getLongestPathRes();
        }
       timeData=null;
    }
    public void getrule(TimeData timeData,String date){
        for(Rule e:rules){
            String[] callers=null;
            String[] responders=null;
            if(e.caller.equals("ALL")){
                callers=this.callersArray;
            }else{
                callers=new String[1];
                callers[0]=e.caller;
            }
            if(e.responder.equals("ALL")){
                responders=this.respondersArray;
            }
            else{
                responders=new String[1];
                responders[0]=e.responder;
            }
            for(String caller:callers){
                for(String responder:responders){
                    List<TimeIPPair> timeIPPairs=timeData.timeIPPairs.get(caller+responder);
                    if(timeIPPairs!=null){
                        for(TimeIPPair timeIPPair:timeIPPairs){
                            String temp=e.id+caller+timeIPPair.callerIP+responder+timeIPPair.responderIP;
                            int value=0;
                            if(alarmMonitorrMap.containsKey(temp)){
                                value=alarmMonitorrMap.get(temp);
                            }
                            else{
                                alarmMonitorrMap.put(temp,0);
                            }
                            if(e.ALERT_TYPE.equals("SR")){
                                if(timeIPPair.RS<e.threshold){
                                    if((value+1)>=e.times){
                                        alarmMonitorres.add(e.id+","+date+","+timeIPPair.getRSString());
                                    }
                                    alarmMonitorrMap.put(temp,value+1);
                                }
                                else{
                                    alarmMonitorrMap.put(temp,0);
                                }
                            }
                            else{
                                if(timeIPPair.P99>e.threshold){
                                    if((value+1)>=e.times){
                                        alarmMonitorres.add(e.id+","+date+","+timeIPPair.getP99String());
                                    }
                                    alarmMonitorrMap.put(temp,value+1);
                                }
                                else{
                                    alarmMonitorrMap.put(temp,0);
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    public void getLongestPathRes(){
        /*放置路径*/
        /**
         * 先找好路径
         */
        index_nums_1=callersArray.length;
        indexmod_1=1;
        HashSet<Integer> hashSet=new HashSet<>();
        while(1==1){
            int flag=0;
            indexmod_1=1;
            while(indexmod_1<index_nums_1){
                for(String caller:callersArray){
                        int temp=0;
                        for(int i=caller.length()-3;i<caller.length();i++){
                            temp=temp*hashcode+caller.charAt(i)-'0';
                        }
                        int index=Math.abs(temp+indexmod_1)%index_nums_1;
                        if(hashSet.contains(index)){
                            flag=1;
                            break;
                        }
                        else{
                            hashSet.add(index);
                        }
                }
                if(flag==0){
                    break;
                }
                indexmod_1++;
                hashSet.clear();
            }
            if(flag==0){
                break;
            }
            ++index_nums_1;
        }
        index_nums_2=respondersArray.length;
        hashSet.clear();
        while(1==1){
            int flag=0;
            indexmod_2=1;
            while(indexmod_2<index_nums_2){
                for(String responder:respondersArray){
                    int temp=0;
                    for(int i=responder.length()-3;i<responder.length();i++){
                        temp=temp*hashcode+responder.charAt(i)-'0';
                    }
                    int index=Math.abs(temp+indexmod_2)%index_nums_2;
                    if(hashSet.contains(index)){
                        flag=1;
                        break;
                    }
                    else{
                        hashSet.add(index);
                    }
                }
                if(flag==0){
                    break;
                }
                indexmod_2++;
                hashSet.clear();
            }
            if(flag==0){
                break;
            }
            ++index_nums_2;
        }
        LongestPathMap=new Collection[index_nums_1][index_nums_2][(maxtime-mintime+1)*2];
        mindate=simpleDateFormat.format((long)mintime * 60 * 1000);
        mindateH=(mindate.charAt(11)-'0')*10+(mindate.charAt(12)-'0');
        mindatem=(mindate.charAt(14)-'0')*10+(mindate.charAt(15)-'0');
        String[] types={"P99","SR"};
        for(int caller_index=0;caller_index<callersArray.length;caller_index++){
            String caller=callersArray[caller_index];
            for(int responder_index=0;responder_index<respondersArray.length;responder_index++){
                String responder=respondersArray[responder_index];
                for(int startTime=mintime;startTime<=maxtime;startTime++){
                    for(int type_index=0;type_index<2;type_index++){
                        String type=types[type_index];
                        int hash=0;
                        for(int i=caller.length()-3;i<caller.length();i++){
                            hash=hash*hashcode+caller.charAt(i)-'0';
                        }
                        int index_1=Math.abs(hash+indexmod_1)%index_nums_1;
                        hash=0;
                        for(int i=responder.length()-3;i<responder.length();i++){
                            hash=hash*hashcode+responder.charAt(i)-'0';
                        }
                        int index_2=Math.abs(hash+indexmod_2)%index_nums_2;
                        LongestPathMap[index_1][index_2][(startTime-mintime)*2+type_index]=new ArrayList<>();
                        ArrayList<ArrayList<String>> path_f=new ArrayList<>();
                        HashSet<String> vis=new HashSet<>();
                        ArrayList<String> path_1=new ArrayList<>();
                        ArrayList<String> typepath_1=new ArrayList<>();
                        ArrayList<ArrayList<String>> typepath_f=new ArrayList<>();
                        String str;
                        HashMap<Integer,String[]> temp_map=Node_f.get(caller).get(responder);
                        if(temp_map!=null){
                            String[] srAndP99=temp_map.get(startTime);
                            if(srAndP99!=null){
                                if (type.equals("SR")) {
                                    str = srAndP99[0];

                                } else {
                                    str = srAndP99[1];
                                }
                            }
                            else{
                                continue;
                            }
                        }
                        else{
                            continue;
                        }
                        find_f(startTime,type,responder,path_f,vis,path_1,typepath_1,typepath_f);
                        vis.clear();
                        path_1.clear();
                        typepath_1.clear();
                        ArrayList<ArrayList<String>> path_s=new ArrayList<>();
                        ArrayList<ArrayList<String>> typepath_s=new ArrayList<>();
                        find_s(startTime,type,caller,path_s,vis,path_1,typepath_1,typepath_s);
                        for(int i=0;i<path_f.size();i++){
                            for(int j=0;j<path_s.size();j++){
                                ArrayList<String> e=path_f.get(i);
                                ArrayList<String> a=path_s.get(j);
                                ArrayList<String> type_e=typepath_f.get(i);
                                ArrayList<String> type_a=typepath_s.get(j);
                                String temp=caller+"->"+responder;
                                for(String s:e){
                                    temp=temp+"->"+s;
                                }
                                for(String s:a){
                                    temp=s+"->"+temp;
                                }
                                String temp_1=""+str;
                                for(String s:type_e){
                                    temp_1=temp_1+","+s;
                                }
                                for(String s:type_a){
                                    temp_1=s+","+temp_1;
                                }
                                LongestPathMap[index_1][index_2][(startTime-mintime)*2+type_index].add(temp+"|"+temp_1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void find_f(int startTime,String type,String responder,ArrayList<ArrayList<String>> path,HashSet<String> vis,ArrayList<String> path_1,ArrayList<String> typepath_1,ArrayList<ArrayList<String>> typepath){
        HashMap<String, HashMap<Integer,String[]>> SRStr=Node_f.get(responder);
        if(SRStr!=null){
            for (String key : SRStr.keySet()) {
                if(!vis.contains(key)){
                    path_1.add(key);
                    vis.add(key);
                    String[] Strs=SRStr.get(key).get(startTime);
                    if(Strs!=null){
                        if(type.equals("SR")){
                            typepath_1.add(Strs[0]);
                        }
                        else{
                            typepath_1.add(Strs[1]);
                        }
                    }
                    else{
                        if(type.equals("SR")){
                            typepath_1.add("-1%");
                        }
                        else{
                            typepath_1.add("-1ms");
                        }
                    }
                    find_f(startTime,type,key,path,vis,path_1,typepath_1,typepath);
                    typepath_1.remove(typepath_1.size()-1);
                    vis.remove(key);
                    path_1.remove(path_1.size()-1);
                }
            }
        }
        else{
            if(path.size()==0||path.get(0).size()<path_1.size()){
                path.clear();
                typepath.clear();
                ArrayList<String> tep=new ArrayList<>();
                for(String e:path_1){
                    tep.add(e);
                }
                path.add(tep);
                ArrayList<String> tep_1=new ArrayList<>();
                for(String e:typepath_1){
                    tep_1.add(e);
                }
                typepath.add(tep_1);
            }
            else if(path.get(0).size()==path_1.size()){
                ArrayList<String> tep=new ArrayList<>();
                for(String e:path_1){
                    tep.add(e);
                }
                path.add(tep);
                ArrayList<String> tep_1=new ArrayList<>();
                for(String e:typepath_1){
                    tep_1.add(e);
                }
                typepath.add(tep_1);
            }
        }

    }
    public void find_s(int startTime,String type,String caller,ArrayList<ArrayList<String>> path,HashSet<String> vis,ArrayList<String> path_1,ArrayList<String> typepath_1,ArrayList<ArrayList<String>> typepath){
        HashMap<String, HashMap<Integer,String[]>> SRStr=Node_s.get(caller);
        if(SRStr!=null){
            for (String key : SRStr.keySet()) {
                if(!vis.contains(key)){
                    path_1.add(key);
                    vis.add(key);
                    String[] Strs=SRStr.get(key).get(startTime);
                    if(Strs!=null){
                        if(type.equals("SR")){
                            typepath_1.add(Strs[0]);
                        }
                        else{
                            typepath_1.add(Strs[1]);
                        }
                    }
                    else{
                        if(type.equals("SR")){
                            typepath_1.add("-1%");
                        }
                        else{
                            typepath_1.add("-1ms");
                        }
                    }
                    find_s(startTime,type,key,path,vis,path_1,typepath_1,typepath);
                    typepath_1.remove(typepath_1.size()-1);
                    vis.remove(key);
                    path_1.remove(path_1.size()-1);
                }
            }
        }
        else{
            if(path.size()==0||path.get(0).size()<path_1.size()){
                path.clear();
                typepath.clear();
                ArrayList<String> tep=new ArrayList<>();
                for(String e:path_1){
                    tep.add(e);
                }
                path.add(tep);
                ArrayList<String> tep_1=new ArrayList<>();
                for(String e:typepath_1){
                    tep_1.add(e);
                }
                typepath.add(tep_1);
            }
            else if(path.get(0).size()==path_1.size()){
                ArrayList<String> tep=new ArrayList<>();
                for(String e:path_1){
                    tep.add(e);
                }
                path.add(tep);
                ArrayList<String> tep_1=new ArrayList<>();
                for(String e:typepath_1){
                    tep_1.add(e);
                }
                typepath.add(tep_1);
            }
        }
    }
}

