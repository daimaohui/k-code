package com.kuaishou.kcode;

import java.util.Arrays;

public class Utils {
    String data;
    int size_1=70;
    int size_2=3900;
    int max_size=600;
    int[][][] Util=new int[size_2][max_size][2];
    int[][] Util_size=new int[size_2][3];
    int[][] services=new int[size_1][2];
    int String_IP_len=0;
    int services_len=0;
    public Utils(int[][][] util, int[][] util_size, int[][] services, int string_IP_len, int services_len) {
//        for(int i=0;i<size_1;i++){
//            this.services[i][0]=services[i][0];
//            this.services[i][1]=services[i][1];
//        }
//        for(int j=0;j<size_2;j++){
//            this.Util_size[j][0]=util_size[j][0];
//            this.Util_size[j][1]=util_size[j][1];
//            this.Util_size[j][2]=util_size[j][2];
//            for(int i=0; i<max_size;i++){
//                this.Util[j][i][0]=util[j][i][0];
//                this.Util[j][i][1]=util[j][i][1];
//            }
//        }
//        Util= Arrays.copyOf(util, size_2);
//        Util_size= Arrays.copyOf(util_size, size_2);
//        this.services=Arrays.copyOf(services, size_1);
        for(int i=0;i<size_1;i++){
            this.services[i]=services[i].clone();
        }
        for(int j=0;j<size_2;j++){
            Util_size[j]= util_size[j].clone();
            for(int i=0; i<max_size;i++){
                Util[j][i]= util[j][i].clone();
            }
        }
        String_IP_len = string_IP_len;
        this.services_len = services_len;
    }
    public Utils(){
        for(int i=0;i<size_1;i++){
            services[i][0]=0;
            services[i][1]=0;
        }
        for(int i=0;i<size_2;i++){
                Util_size[i][0]=0;
                Util_size[i][1]=0;
                Util_size[i][2]=0;
        }
    }
    public Utils(int services_len,int String_IP_len){
        this.services_len=services_len;
        this.String_IP_len=String_IP_len;
        for(int i=0;i<size_1;i++){
            services[i][0]=0;
            services[i][1]=0;
        }
        for(int i=0;i<size_2;i++){
            Util_size[i][0]=0;
            Util_size[i][1]=0;
            Util_size[i][2]=0;
        }
    }
}
