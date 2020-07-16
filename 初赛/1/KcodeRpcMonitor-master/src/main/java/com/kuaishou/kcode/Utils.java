package com.kuaishou.kcode;

import java.util.Arrays;

public class Utils {
    public int IsTrue=0;
    public int size=0;
    public  int max_size=1024;
    public int[] nums=new int[max_size];
    public String res(){
        Arrays.sort(nums,0,size);
        int i=size-size/100;
        int P99=nums[i-1];
        double res_1=IsTrue*1.0/size*100;
        return String.format("%.2f", res_1)+"%,"+P99;
    }
}
