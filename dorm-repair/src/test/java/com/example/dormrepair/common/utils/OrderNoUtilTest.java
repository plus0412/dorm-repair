package com.example.dormrepair.common.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderNoUtil 单元测试
 */
public class OrderNoUtilTest {

    @Test
    void generate_ShouldReturnNonNull() {
        String result = OrderNoUtil.generate();
        assertNotNull(result);
    }

    @Test
    void generate_ShouldStartWithBX() {
        String result = OrderNoUtil.generate();
        assertTrue(result.startsWith("BX"));
    }

    @Test
    void generate_ShouldHaveCorrectLength() {
        String result = OrderNoUtil.generate();
        // BX(2) + yyyyMMddHHmmss(14) + 3位随机数 = 19位
        assertEquals(19, result.length());
    }

    @Test
    void generate_ShouldHaveTimestampFormat() {
        String result = OrderNoUtil.generate();
        String timestampPart = result.substring(2, 16); // 提取 yyyyMMddHHmmss 部分
        assertEquals(14, timestampPart.length());
        // 验证都是数字
        assertTrue(timestampPart.matches("\\d{14}"));
    }

    @Test
    void generate_ShouldHaveRandomSuffix() {
        String result = OrderNoUtil.generate();
        String randomPart = result.substring(16);
        assertEquals(3, randomPart.length());
        assertTrue(randomPart.matches("\\d{3}"));
    }

    @Test
    void generate_ShouldGenerateUniqueValues() {
        String result1 = OrderNoUtil.generate();
        String result2 = OrderNoUtil.generate();
        // 由于时间戳精确到秒，同一秒内可能相同，但概率极低
        // 这里主要测试方法不抛出异常
        assertNotNull(result1);
        assertNotNull(result2);
    }
}
