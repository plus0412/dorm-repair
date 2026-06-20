package com.example.dormrepair.common.result;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Result<T> 单元测试
 */
public class ResultTest {

    @Test
    void success_WithData_ShouldReturn200() {
        Result<String> result = Result.success("test-data");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("test-data", result.getData());
    }

    @Test
    void success_WithoutData_ShouldReturn200WithNullData() {
        Result<Void> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void error_WithCodeAndMessage_ShouldReturnSpecifiedValues() {
        Result<Void> result = Result.error(500, "自定义错误");
        assertEquals(500, result.getCode());
        assertEquals("自定义错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void error_WithMessageOnly_ShouldReturn500() {
        Result<Void> result = Result.error("系统错误");
        assertEquals(500, result.getCode());
        assertEquals("系统错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void setterAndGetter_ShouldWorkCorrectly() {
        Result<String> result = new Result<>();
        result.setCode(404);
        result.setMessage("Not Found");
        result.setData("test");

        assertEquals(404, result.getCode());
        assertEquals("Not Found", result.getMessage());
        assertEquals("test", result.getData());
    }
}
