package com.kuaishou.kcode;

import static com.kuaishou.kcode.KcodeUtils.createCheckPairMap;
import static com.kuaishou.kcode.KcodeUtils.createCheckResponderMap;
import static java.lang.System.nanoTime;
import static java.lang.System.setOut;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kcode
 * Created on 2020-06-01
 * 该评测程序主要便于选手在本地优化和调试自己的程序
 */
public class KcodeRpcMonitorTest {
    public static void main(String[] args) throws Exception {
        KcodeRpcMonitor kcodeRpcMonitor = new KcodeRpcMonitorImpl();
        long startNs = nanoTime();
        kcodeRpcMonitor.prepare("C:\\Users\\daimaohui\\Downloads\\kcodeRpcMonitor.data");
        System.out.println("prepare 耗时(ms):" + NANOSECONDS.toMillis(nanoTime() - startNs));

        // 读取checkPair.result文件
        Map<CheckPairKey, Set<CheckPairResult>> checkPairMap = createCheckPairMap("C:\\Users\\daimaohui\\Downloads\\checkPair.result");

        // 读取checkResponder.result文件
        Map<CheckResponderKey, CheckResponderResult> checkResponderMap = createCheckResponderMap("C:\\Users\\daimaohui\\Downloads\\checkResponder.result");

        // 评测checkPair
        checkPair(kcodeRpcMonitor, checkPairMap);

        // 评测checkResponder
        checkResponder(kcodeRpcMonitor, checkResponderMap);
//        int size=1000000*306;
//        System.out.println(size);
//        long startNs = nanoTime();
//        while(size>=0){
//            int a=KcodeRpcMonitorImpl.testFormat("2020-06-15 12:06")-KcodeRpcMonitorImpl.testFormat("2020-06-15 11:06");
//            String temp=String.format("%.2f", a*1.0/100)+"%";
//            size--;
//        }
//        System.out.println(size);
//        System.out.println("prepare 耗时(ms):" + NANOSECONDS.toMillis(nanoTime() - startNs));
    }

    public static void checkPair(KcodeRpcMonitor kcodeRpcMonitor,
                                 Map<CheckPairKey, Set<CheckPairResult>> checkPairMap) {
        int checkPairTime = 1000000; // 可以自己修改次数
        long cast = 0L;
        while (true) {
            for (Map.Entry<CheckPairKey, Set<CheckPairResult>> entry : checkPairMap.entrySet()) {
                CheckPairKey key = entry.getKey();
                long startNs = nanoTime();
                List<String> result = kcodeRpcMonitor.checkPair(key.getCaller(), key.getResponder(), key.getTime());
                cast += (nanoTime() - startNs);
                Set<CheckPairResult> checkResult = entry.getValue();
                if (Objects.isNull(result) || checkResult.size() != result.size()) {
                    System.out.println("key:" + key + ", result:" + result + ", checkResult:" + checkResult);
                    throw new RuntimeException("评测结果错误");
                }
                if (result.size() != 0) {
                    Set<CheckPairResult> checkPairResSet = result.stream().map(CheckPairResult::new).collect(toSet());
                    if (!checkResult.containsAll(checkPairResSet)) {
                        System.out.println("key:" + key + ", result:" + result + ", checkResult:" + checkResult);
                        throw new RuntimeException("评测结果错误");
                    }
                }
                if (checkPairTime-- <= 0) {
                    System.out.println("checkPair 结束, cast(ms):" + NANOSECONDS.toMillis(cast));
                    return;
                }
            }
        }
    }

    public static void checkResponder(KcodeRpcMonitor kcodeRpcMonitor,
                                      Map<CheckResponderKey, CheckResponderResult> checkResponderMap) throws ParseException {
        int checkResponderTime = 1000000; // 可以自己修改次数
        long cast = 0L;
        while (true) {
            for (Map.Entry<CheckResponderKey, CheckResponderResult> entry : checkResponderMap.entrySet()) {
                CheckResponderKey key = entry.getKey();
                long startNs = nanoTime();
                String result = kcodeRpcMonitor.checkResponder(key.getName(), key.getStartTime(), key.getEndTime());
                cast += (nanoTime() - startNs);
                CheckResponderResult checkResponderResult = entry.getValue();
                if (Objects.isNull(result) || !checkResponderResult.equals(new CheckResponderResult(result))) {
                    System.out.println("key:" + key + ", result:" + result + ", checkResult:" + checkResponderResult);
                    throw new RuntimeException("评测结果错误");
                }
                if (checkResponderTime-- <= 0) {
                    System.out.println("checkResponder 结束, cast(ms):" + NANOSECONDS.toMillis(cast));
                    return;
                }
            }
        }
    }
}
