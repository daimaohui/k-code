package com.kuaishou.kcode;

public class MyArratList {
    /* 本体 */
    String[] array;
    int[] len;

    /* 有多少元素？ */
    int length;

    /* 构造函数 */
    MyArratList() {
        this.array = new String[53];
        this.len=new int[53];
    }

    /* 重置 */
    void reset() {
        this.length =  0;
    }

    /* 获取元素 */
    String get(int idx) {
        return array[idx];
    }

    /* 添加元素 */
    void add(String elem,int len) {
        this.array[this.length] = elem;
        this.len[this.length]=len;
        this.length++;
    }
    void add(String elem) {
        this.array[this.length] = elem;
        this.length++;
    }
}
