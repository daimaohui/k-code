package com.kuaishou.kcode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Answer {
    /* 数据映射 */
    private Map<Integer, TimeData> timeDataMap = new HashMap<>();
    private ArrayList<Rule> rules=new ArrayList<>();
    /* 时间格式化对象 */
    private DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /*alarmMonitor中间变量*/
    Map<String,Integer> alarmMonitorrMap=new HashMap<>();
    /*alarmMonitorres*/
    Collection<String> alarmMonitorres=new ArrayList<>();
    /*前向图*/
    Map<String,HashMap<String, HashMap<Integer,SRAndP99>>> Node_f=new HashMap<>();
    /*后向图*/
    Map<String,HashMap<String, HashMap<Integer,SRAndP99>>> Node_s=new HashMap<>();
    public void addRules(String RuleStr){
        String[] split=RuleStr.split(",");
        int id=Integer.valueOf(split[0]);
        String caller=split[1];
        String responder=split[2];
        String ALERT_TYPE=split[3];
        int times=Integer.valueOf(split[4].substring(0,split[4].length()-1));
        char Trigger=split[4].charAt(split[4].length()-1);
        double threshold=0;
        if(split[5].charAt(split[5].length()-1)=='%'){
            threshold=Double.valueOf(split[5].substring(0,split[5].length()-1));
        }
        else{
            threshold=Integer.valueOf(split[5].substring(0,split[5].length()-2));
        }
        Rule rule=new Rule(id,caller,responder,ALERT_TYPE,times,Trigger,threshold);
        rules.add(rule);
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
        timeData.addPairData(startTime,caller,callerIP,responder,responderIP,elapsedTime,success);
    }
    public void getresult(int startTime){
        TimeData timeData =timeDataMap.get(startTime);
        /**
         * 一遍计算第一问的结果，一遍构建第二问的图
         */
        timeData.getresult(Node_f,Node_s); /* 调用内部方法进行计算结果 */
        /*
            调用一个方法来去判断规则
         */
        long data=(long)startTime*1000*60;
        getrule(timeData,simpleDateFormat.format(data));

    }
    public void getrule(TimeData timeData,String date){
        for(Rule e:rules){
            String[] callers=null;
            String[] responders=null;
            if(e.caller.equals("ALL")){
                callers=timeData.callersArray;
            }else{
                callers=new String[1];
                callers[0]=e.caller;
            }
            if(e.responder.equals("ALL")){
                responders=timeData.respondersArray;
            }
            else{
                responders=new String[1];
                responders[0]=e.responder;
            }
            for(String caller:callers){
                for(String responder:responders){
                    if(timeData.timeIPPairs.containsKey(caller+responder)){
                        List<TimeIPPair> timeIPPairs=timeData.timeIPPairs.get(caller+responder);
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
    public Collection<String> getLongestPathRes(String caller, String responder, String time, String type) throws ParseException {
        /*放置路径*/
        Collection<String> LongestPath=new ArrayList<>();
        /**
         * 先找好路径
         */
        int startTime = (int) (simpleDateFormat.parse(time).getTime() / (1000 * 60));
        int maxsize=0;
        ArrayList<ArrayList<String>> path_f=new ArrayList<>();
        HashSet<String> vis=new HashSet<>();
        ArrayList<String> path_1=new ArrayList<>();
        ArrayList<String> typepath_1=new ArrayList<>();
        ArrayList<ArrayList<String>> typepath_f=new ArrayList<>();
        String str;
        if (type.equals("SR")) {
            str = Node_f.get(caller).get(responder).get(startTime).SRStr;
        } else {
            str = Node_f.get(caller).get(responder).get(startTime).P99 + "ms";
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
                int num=e.size()+a.size()+2;
                if(num>maxsize){
                    LongestPath.clear();
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
                    LongestPath.add(temp+"|"+temp_1);
                    maxsize=num;
                }
                else if(num==maxsize){
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
                    LongestPath.add(temp+"|"+temp_1);
                }
            }
        }
        /**
         * 然后把结果写出来
         */
        return LongestPath;
    }
    public void find_f(int startTime,String type,String responder,ArrayList<ArrayList<String>> path,HashSet<String> vis,ArrayList<String> path_1,ArrayList<String> typepath_1,ArrayList<ArrayList<String>> typepath){
        if(Node_f.containsKey(responder)){
            HashMap<String, HashMap<Integer,SRAndP99>> SRStr=Node_f.get(responder);
            for (String key : SRStr.keySet()) {
                if(!vis.contains(key)){
                    path_1.add(key);
                    vis.add(key);
                    if(SRStr.get(key).containsKey(startTime)){
                        if(type.equals("SR")){
                            typepath_1.add(SRStr.get(key).get(startTime).SRStr);
                        }
                        else{
                            typepath_1.add(SRStr.get(key).get(startTime).P99+"ms");
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
        if(Node_s.containsKey(caller)){
            HashMap<String, HashMap<Integer,SRAndP99>> SRStr=Node_s.get(caller);
            for (String key : SRStr.keySet()) {
                if(!vis.contains(key)){
                    path_1.add(key);
                    vis.add(key);
                    if(SRStr.get(key).containsKey(startTime)){
                        if(type.equals("SR")){
                            typepath_1.add(SRStr.get(key).get(startTime).SRStr);
                        }
                        else{
                            typepath_1.add(SRStr.get(key).get(startTime).P99+"ms");
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
