package com.example.dormrepair.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 工单编号生成工具类
 * 格式：BX + yyyyMMddHHmmss + 3位随机数，总长度 18 位
 */
public class OrderNoUtil {

    private static final String PREFIX = "BX";
    private static final Random RANDOM = new Random();

    /**
     * 生成工单编号
     * 格式：BX + yyyyMMddHHmmss + 3位随机数
     */
    public static String generate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        int randomNum = RANDOM.nextInt(900) + 100; // 100~999
        return PREFIX + timestamp + randomNum;
    }
}
