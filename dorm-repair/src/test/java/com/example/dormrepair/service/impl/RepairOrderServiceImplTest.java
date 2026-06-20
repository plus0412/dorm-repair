package com.example.dormrepair.service.impl;

import com.example.dormrepair.common.exception.BusinessException;
import com.example.dormrepair.entity.RepairOrder;
import com.example.dormrepair.entity.RepairRecord;
import com.example.dormrepair.mapper.RepairOrderImageMapper;
import com.example.dormrepair.mapper.RepairOrderMapper;
import com.example.dormrepair.mapper.RepairRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RepairOrderServiceImplTest {

    @Mock
    private RepairOrderMapper repairOrderMapper;

    @Mock
    private RepairOrderImageMapper repairOrderImageMapper;

    @Mock
    private RepairRecordMapper repairRecordMapper;

    @InjectMocks
    private RepairOrderServiceImpl repairOrderService;

    @Test
    void cancelOrder_shouldSuccess() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStudentId(100L);
        order.setStatus(0);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.cancelOrder(1L, 100L));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        assertEquals(7, captor.getValue().getStatus());
    }

    @Test
    void cancelOrder_shouldThrowWhenNotOwner() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStudentId(999L);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> repairOrderService.cancelOrder(1L, 100L));
    }

    @Test
    void cancelOrder_shouldThrowWhenStatusNotZero() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStudentId(100L);
        order.setStatus(2);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> repairOrderService.cancelOrder(1L, 100L));
    }

    @Test
    void resubmitOrder_shouldSuccess() {
        RepairOrder existingOrder = new RepairOrder();
        existingOrder.setId(1L);
        existingOrder.setStudentId(100L);
        existingOrder.setStatus(1);
        existingOrder.setImages("uploads/old.png");

        RepairOrder updatedOrder = new RepairOrder();
        updatedOrder.setId(1L);
        updatedOrder.setStudentId(100L);
        updatedOrder.setTitle("新标题");
        updatedOrder.setBuildingId(1L);
        updatedOrder.setRoomId(2L);
        updatedOrder.setRepairTypeId(3L);
        updatedOrder.setDescription("新描述");

        when(repairOrderMapper.selectById(1L)).thenReturn(existingOrder);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.resubmitOrder( updatedOrder, null));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        RepairOrder saved = captor.getValue();
        assertEquals(0, saved.getStatus());
        assertEquals("新标题", saved.getTitle());
        assertEquals("uploads/old.png", saved.getImages());
    }

    @Test
    void resubmitOrder_shouldThrowWhenStatusIsNotRejected() {
        RepairOrder existingOrder = new RepairOrder();
        existingOrder.setId(1L);
        existingOrder.setStudentId(100L);
        existingOrder.setStatus(0);

        RepairOrder updatedOrder = new RepairOrder();
        updatedOrder.setId(1L);
        updatedOrder.setStudentId(100L);

        when(repairOrderMapper.selectById(1L)).thenReturn(existingOrder);

        assertThrows(BusinessException.class, () -> repairOrderService.resubmitOrder(updatedOrder, null));
    }

    @Test
    void auditOrder_shouldApprove() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(0);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.auditOrder(1L, true, null, 999L));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        assertEquals(2, captor.getValue().getStatus());
    }

    @Test
    void auditOrder_shouldReject() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(0);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.auditOrder(1L, false, "材料不足", 999L));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        assertEquals(1, captor.getValue().getStatus());
    }

    @Test
    void auditOrder_shouldThrowWhenRejectWithoutRemark() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(0);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> repairOrderService.auditOrder(1L, false, null, 999L));
    }

    @Test
    void assignOrder_shouldSuccess() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(2);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.assignOrder(1L, 200L, 999L));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        RepairOrder saved = captor.getValue();
        assertEquals(3, saved.getStatus());
        assertEquals(200L, saved.getRepairUserId());
    }

    @Test
    void startRepair_shouldSuccess() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(3);
        order.setRepairUserId(200L);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.startRepair(1L, 200L));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        assertEquals(4, captor.getValue().getStatus());
    }

    @Test
    void startRepair_shouldThrowWhenNotAssignedToUser() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(3);
        order.setRepairUserId(999L);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> repairOrderService.startRepair(1L, 200L));
    }

    @Test
    void finishRepair_shouldSuccess() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setStatus(4);
        order.setRepairUserId(200L);

        when(repairOrderMapper.selectById(1L)).thenReturn(order);
        when(repairOrderMapper.updateById(any(RepairOrder.class))).thenReturn(1);
        when(repairRecordMapper.insert(any(RepairRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> repairOrderService.finishRepair(1L, 200L, "已更换灯泡"));

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderMapper).updateById(captor.capture());
        assertEquals(5, captor.getValue().getStatus());
    }
}
