package com.kuaishou.kcode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class Utils implements Serializable {
    int time=0;
    String data;
    int size_1=70;
    int size_2=5;
    int size_3=63;
    int max_size=2048;
    int[][] Util=new int[size_1*size_2*size_3][max_size];
    int[][] Util_size=new int[size_1*size_2*size_3][3];
    int[][] services=new int[size_1][2];
    int[] String_service;
    HashMap<Integer,Integer> find_services=null;
    HashMap<Long, Integer> find_Mainservices=null;
    ServicesIP[] String_IP;

    public Utils( int[][] util, int[][] util_size, int[][] services, int[] string_service, HashMap<Integer, Integer> find_services, HashMap<Long, Integer> find_Mainservices, ServicesIP[] string_IP, int string_IP_len, int services_len) {
        for(int i=0;i<size_1*size_2*size_3;i++){
            Util[i]= Arrays.copyOf(util[i], util_size[i][2]);
            Util_size[i]= Arrays.copyOf(util_size[i], 3);
        }
        for(int i=0;i<size_1;i++){
            this.services[i]=Arrays.copyOf(services[i], 2);
        }
        String_service = string_service;
        this.find_services = find_services;
        this.find_Mainservices = find_Mainservices;
        String_IP = string_IP;
        String_IP_len = string_IP_len;
        this.services_len = services_len;
    }

    int String_IP_len=0;
    int services_len=0;
    public Utils(){
        String_service=new int[size_1];
        this.find_services=new HashMap<>();
        this.find_Mainservices=new HashMap<>();
        String_IP=new ServicesIP[size_1*size_2*size_3];
        for(int i=0;i<size_1;i++){
            services[i][0]=0;
            services[i][1]=0;
        }
        for(int i=0;i<size_1*size_2*size_3;i++){
                Util_size[i][0]=0;
                Util_size[i][1]=0;
                Util_size[i][2]=max_size;
        }
    }
    public Utils(HashMap<Integer,Integer> find_services,int[] String_service,int services_len,int String_IP_len,HashMap<Long, Integer> find_Mainservices,ServicesIP[] String_IP){
        this.find_services=find_services;
        this.String_service=String_service;
        this.services_len=services_len;
        this.String_IP_len=String_IP_len;
        this.find_Mainservices=find_Mainservices;
        this.String_IP=String_IP;
        for(int i=0;i<size_1;i++){
            services[i][0]=0;
            services[i][1]=0;
        }
        for(int i=0;i<size_1*size_2*size_3;i++){
            Util_size[i][0]=0;
            Util_size[i][1]=0;
            Util_size[i][2]=max_size;
        }
    }
}
