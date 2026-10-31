package com.nak.demo.service;


import com.nak.demo.common.enums.OrderStatus;
import com.nak.demo.common.enums.PaymentStatus;
import com.nak.demo.dto.report.SalesReportDto;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.Product;
import com.nak.demo.exception.model.UnprocessableEntityException;
import com.nak.demo.repository.CashPaymentRepository;
import com.nak.demo.repository.OrderItemRepository;
import com.nak.demo.repository.OrderRepository;
import com.nak.demo.repository.PaymentRepository;
import com.nak.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final int MAX_RANGE_DAYS = 366;
    private static final int TOP_PRODUCTS = 5;
    private static final String PAID = OrderStatus.SUCCESS.name();

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CashPaymentRepository cashPaymentRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    /** Dates are in the server's time zone (Asia/Phnom_Penh on your PC). */
    private final ZoneId zone = ZoneId.systemDefault();

    public enum GroupBy { DAY, WEEK, MONTH }

    /**
     * Sales between two dates (both inclusive). Defaults: last 30 days, grouped by day.
     * Only paid orders (status SUCCESS) count as sales.
     */
    @Transactional(readOnly = true)
    public SalesReportDto salesReport(LocalDate from, LocalDate to, String groupByParam) {

        LocalDate end;
        if (to != null) {
            end = to;
        } else {
            end = LocalDate.now(zone);
        }

        LocalDate start;
        if (from != null) {
            start = from;
        } else {
            start = end.minusDays(29);
        }

        GroupBy groupBy = parseGroupBy(groupByParam);

        if (start.isAfter(end)) {
            throw new UnprocessableEntityException("'from' must be on or before 'to'");
        }
        if (ChronoUnit.DAYS.between(start, end) >= MAX_RANGE_DAYS) {
            throw new UnprocessableEntityException("date range can be at most " + MAX_RANGE_DAYS + " days");
        }

        LocalDateTime fromTime = start.atStartOfDay();
        LocalDateTime toTime = end.plusDays(1).atStartOfDay();

        List<Order> paidOrders = orderRepository
                .findByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(PAID, fromTime, toTime);

        BigDecimal revenue = paidOrders.stream().map(this::orderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long orderCount = paidOrders.size();

        BigDecimal average;
        if (orderCount == 0) {
            average = BigDecimal.ZERO;
        } else {
            average = revenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
        }

        Long itemsSoldResult = orderItemRepository.sumQuantity(PAID, fromTime, toTime);
        long itemsSold;
        if (itemsSoldResult == null) {
            itemsSold = 0;
        } else {
            itemsSold = itemsSoldResult;
        }

        long cancelled = orderRepository.countByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                OrderStatus.CANCELLED.name(), fromTime, toTime);

        SalesReportDto.Summary summary = new SalesReportDto.Summary(
                orderCount, money(revenue), money(average), itemsSold, cancelled);

        Map<String, List<Order>> byPeriod = paidOrders.stream()
                .collect(Collectors.groupingBy(o -> periodKey(o.getCreatedAt().toLocalDate(), groupBy)));
        List<SalesReportDto.Period> periods = new ArrayList<>();
        for (LocalDate d = periodStart(start, groupBy); !d.isAfter(end); d = nextPeriod(d, groupBy)) {
            String key = periodKey(d, groupBy);
            List<Order> orders = byPeriod.getOrDefault(key, List.of());
            BigDecimal periodRevenue = orders.stream().map(this::orderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            periods.add(new SalesReportDto.Period(key, orders.size(), money(periodRevenue)));
        }

        Instant fromInstant = fromTime.atZone(zone).toInstant();
        Instant toInstant = toTime.atZone(zone).toInstant();
        BigDecimal cash = cashPaymentRepository.sumTotalPaidBetween(fromInstant, toInstant);
        BigDecimal khqr = paymentRepository.sumAmountByStatusPaidBetween(PaymentStatus.PAID, fromInstant, toInstant);
        SalesReportDto.PaymentMethods methods = new SalesReportDto.PaymentMethods(money(cash), money(khqr));

        List<Object[]> rows = orderItemRepository.findTopProducts(PAID, fromTime, toTime, PageRequest.of(0, TOP_PRODUCTS));
        List<Long> ids = rows.stream().map(r -> (Long) r[0]).toList();
        Map<Long, String> names = productRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Product::getId, ReportService::productName));
        List<SalesReportDto.TopProduct> top = rows.stream()
                .map(r -> new SalesReportDto.TopProduct(
                        (Long) r[0],
                        names.getOrDefault((Long) r[0], "(deleted product)"),
                        ((Number) r[1]).longValue()))
                .toList();

        return new SalesReportDto(start, end, groupBy.name().toLowerCase(), summary, periods, methods, top);
    }

    private GroupBy parseGroupBy(String value) {
        if (value == null || value.isBlank()) {
            return GroupBy.DAY;
        }
        try {
            return GroupBy.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("group_by must be one of: day, week, month");
        }
    }

    /** The product's name, or "(no name)" if it is empty. */
    private static String productName(Product p) {
        if (p.getProductName() == null) {
            return "(no name)";
        }
        return p.getProductName();
    }

    /** The order's amount; older rows may only have one of the total columns filled. */
    private BigDecimal orderAmount(Order o) {
        if (o.getGrandTotal() != null && o.getGrandTotal().signum() > 0) {
            return o.getGrandTotal();
        }
        if (o.getTotalAmount() != null && o.getTotalAmount().signum() > 0) {
            return o.getTotalAmount();
        }
        if (o.getTotalPrice() != null && o.getTotalPrice().signum() > 0) {
            return o.getTotalPrice();
        }
        if (o.getTotal() == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(o.getTotal());
    }

    private static BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static LocalDate periodStart(LocalDate date, GroupBy groupBy) {
        return switch (groupBy) {
            case DAY -> date;
            case WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MONTH -> date.withDayOfMonth(1);
        };
    }

    private static LocalDate nextPeriod(LocalDate periodStart, GroupBy groupBy) {
        return switch (groupBy) {
            case DAY -> periodStart.plusDays(1);
            case WEEK -> periodStart.plusWeeks(1);
            case MONTH -> periodStart.plusMonths(1);
        };
    }

    /** day: 2026-10-28, week: 2026-10-26 (the Monday), month: 2026-10 */
    private static String periodKey(LocalDate date, GroupBy groupBy) {
        LocalDate startOfPeriod = periodStart(date, groupBy);
        if (groupBy == GroupBy.MONTH) {
            return YearMonth.from(startOfPeriod).toString();
        }
        return startOfPeriod.toString();
    }
}