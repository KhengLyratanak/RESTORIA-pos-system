package com.nak.demo.service;

import com.nak.demo.common.enums.OrderStatus;
import com.nak.demo.common.enums.TableType;
import com.nak.demo.dto.table.TableResponseDto;
import com.nak.demo.entity.DiningTable;
import com.nak.demo.entity.Order;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.exception.model.UnprocessableEntityException;
import com.nak.demo.mapper.DiningTableMapper;
import com.nak.demo.repository.DiningTableRepository;
import com.nak.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tables and take-away numbers. A table is BUSY while it has an unpaid (PENDING) order,
 * and becomes FREE by itself when that order is paid or cancelled.
 */
@Service
public class DiningTableService {

    private static final int TABLE_COUNT = 10;
    private static final int TAKE_AWAY_COUNT = 10;

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiningTableMapper diningTableMapper;

    @Transactional(readOnly = true)
    public List<TableResponseDto> listTables() {
        Map<Long, Order> openOrderByTable = new HashMap<>();
        for (Order order : orderRepository.findByStatusAndTableIdIsNotNull(OrderStatus.PENDING.name())) {
            openOrderByTable.put(order.getTableId(), order);
        }

        List<TableResponseDto> result = new ArrayList<>();
        for (DiningTable table : diningTableRepository.findAllByOrderBySortOrderAsc()) {
            result.add(diningTableMapper.toResponseDto(table, openOrderByTable.get(table.getId())));
        }
        return result;
    }

    /**
     * The table, if it is free. A table can't take a second order while it has an unpaid one:
     * add the new items to that order instead. currentOrderId lets an order keep its own table.
     */
    public DiningTable getFreeTable(Long tableId, Long currentOrderId) {
        DiningTable table = diningTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("table not found with id :" + tableId));

        orderRepository.findFirstByStatusAndTableId(OrderStatus.PENDING.name(), tableId)
                .filter(openOrder -> !openOrder.getId().equals(currentOrderId))
                .ifPresent(openOrder -> {
                    throw new UnprocessableEntityException(table.getName() + " already has open order #"
                            + openOrder.getId() + ", add the items to that order instead");
                });
        return table;
    }

    /** Creates Table 1–10 and Take Away 01–10 the first time the app starts. */
    @Transactional
    public void createDefaultTables() {
        if (diningTableRepository.count() > 0) {
            return;
        }
        for (int i = 1; i <= TABLE_COUNT; i++) {
            diningTableRepository.save(newTable("T" + i, "Table " + i, TableType.DINE_IN, i));
        }
        for (int i = 1; i <= TAKE_AWAY_COUNT; i++) {
            String number = String.format("%02d", i);
            diningTableRepository.save(newTable("TA" + i, "Take Away " + number, TableType.TAKE_AWAY, 100 + i));
        }
    }

    private DiningTable newTable(String code, String name, TableType type, int sortOrder) {
        DiningTable table = new DiningTable();
        table.setCode(code);
        table.setName(name);
        table.setType(type.name());
        table.setSortOrder(sortOrder);
        return table;
    }
}
