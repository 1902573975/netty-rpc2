package com.api.grp.comm;

/**
 * 雪花算法
 */
public class IdWorker {
    // 1 + 43(时间戳) + 4(机房id) + 4(机器id) + 12(随机值)

    private static long workerId =0;//机器ID
    private static long workerIdBits = 4L;//4位的机器id ---- 可改
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private static long dataCenterId = 0;//机房ID
    private static long dataCenterIdBits = 4L;//4位的机房id---- 可改
    private static long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    private static long sequence = 0L;//代表一毫秒内生成的多个id的最新序号
    private static long sequenceBits = 12L;//每毫秒内产生的id数 2 的 12次方---- 可改
    private static long sequenceMask = -1L ^ (-1L << sequenceBits); //2^12-1

    private static long twepoch = 1618296590390L;//是一个时间值,表示时间戳从这个时刻开始计数。//2021-4-13 14:30
    private static long lastTimestamp = -1L;//记录产生时间毫秒数，判断是否是同1毫秒

    //偏移量
    private static long workerIdShift = sequenceBits;
    private static long dataCenterIdShift = sequenceBits + workerIdBits;
    private static long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;


    public static void init(long workerIdParam, long dataCenterIdParam) throws IllegalArgumentException{
        if (workerIdParam > maxWorkerId || workerIdParam < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }
        if (dataCenterIdParam > maxDataCenterId || dataCenterIdParam < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",maxDataCenterId));
        }
        dataCenterId = dataCenterIdParam;
        workerId = workerIdParam;
    }


    public static long nextId() throws RuntimeException{
        long currentTimestamp;
        long tempSequence;
        synchronized (IdWorker.class) {
            currentTimestamp = currentTimestamp();
            tempSequence = sequence;

            if (currentTimestamp < lastTimestamp) {
                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - currentTimestamp));
            }
            if (currentTimestamp == lastTimestamp) {
                tempSequence = (tempSequence+1) & sequenceMask;//当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
                if (tempSequence == 0) {
                    currentTimestamp = tilNextMillis(currentTimestamp);
                }
            } else {
                tempSequence = 0;
            }
            lastTimestamp = currentTimestamp;
            sequence  = tempSequence;
        }
        long result =  ((currentTimestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | tempSequence;
        return result;
    }


    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }


    private static long currentTimestamp(){
        return System.currentTimeMillis();
    }
}