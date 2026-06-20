package com.example.dormrepair.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BusinessException 单元测试
 */
public class BusinessExceptionTest {

    @Test
    void constructor_WithCodeAndMessage_ShouldSetFieldsCorrectly() {
        BusinessException ex = new BusinessException(500, "业务错误");
        assertEquals(500, ex.getCode());
        assertEquals("业务错误", ex.getMessage());
    }

    @Test
    void constructor_WithMessageOnly_ShouldDefaultCodeTo500() {
        BusinessException ex = new BusinessException("业务错误");
        assertEquals(500, ex.getCode());
        assertEquals("业务错误", ex.getMessage());
    }

    @Test
    void setter_ShouldWorkCorrectly() {
        BusinessException ex = new BusinessException(400, "测试");
        ex.setCode(404);
        assertEquals(404, ex.getCode());
    }

    @Test
    void instanceof_RuntimeException_ShouldBeTrue() {
        BusinessException ex = new BusinessException(500, "测试");
        assertTrue(ex instanceof RuntimeException);
    }
}
