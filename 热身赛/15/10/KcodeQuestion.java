package com.kuaishou.kcode;

import java.io.InputStream;

/**
 * @author kcode
 * Created on 2020-05-20
 */
public class KcodeQuestion {

    /**
     * prepare() 方法用来接受输入数据集，数据集格式参考README.md
     *
     * @param inputStream
     */
    public void prepare(InputStream inputStream) throws InterruptedException {
        /**
         * 调用线程进行处理数据和计算结果
         */
        prepareData preparedata=new prepareData(inputStream);
        Thread preparedata_Thread=new Thread(preparedata);
        prepareResult prepareResult=new prepareResult();
        Thread prepareResult_Thread=new Thread(prepareResult);
        preparedata_Thread.start();
        prepareResult_Thread.start();
        preparedata_Thread.join();
        prepareResult_Thread.join();
    }

    /**
     * getResult() 方法是由kcode评测系统调用，是评测程序正确性的一部分，请按照题目要求返回正确数据
     * 输入格式和输出格式参考 README.md
     *
     * @param timestamp 秒级时间戳
     * @param methodName 方法名称
     */
    public String getResult(Long timestamp, String methodName) {
        // do something
        return data.res_map.get(timestamp).get(methodName);
    }

}
