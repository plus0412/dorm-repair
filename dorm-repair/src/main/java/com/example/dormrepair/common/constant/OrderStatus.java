package com.example.dormrepair.common.constant;

/**
 * 工单状态常量
 */
public class OrderStatus {
    /** 0 - 已提交，待分配 */
    public static final Integer SUBMITTED = 0;
    /** 1 - 已分配，待接单 */
    public static final Integer ASSIGNED = 1;
    /** 2 - 维修中 */
    public static final Integer REPAIRING = 2;
    /** 3 - 维修完成，待确认 */
    public static final Integer REPAIR_DONE = 3;
    /** 4 - 学生确认完成 */
    public static final Integer CONFIRMED = 4;
    /** 5 - 已取消 */
    public static final Integer CANCELLED = 5;
    /** 6 - 已超时 */
    public static final Integer TIMEOUT = 6;
    /** 7 - 已评价 */
    public static final Integer RATED = 7;
}
