package com.kuaishou.kcode;

import java.util.ArrayList;
import java.util.HashMap;

public class data {
    public static  ArrayList<timestamp> timestamps=new ArrayList<>(); //用来存放中间变量，就是所谓的仓库
    public static  HashMap<Long,HashMap<String,String>> res_map=new HashMap<>(); //静态，用来存放最终结果，可以改进
    public static Object lock=new Object();
    public static int flag=0;
}
