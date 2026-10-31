package com.nak.demo.service;

import com.nak.demo.common.enums.DiscountType;
import com.nak.demo.common.enums.OrderStatus;
import com.nak.demo.common.enums.PaymentStatus;
import com.nak.demo.dto.order.OrderCreateDto;
import com.nak.demo.dto.order.OrderDiscountDto;
import com.nak.demo.dto.order.OrderResponseDto;
import com.nak.demo.dto.order.OrderUpdateDto;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.OrderItem;
import com.nak.demo.entity.Product;
import com.nak.demo.entity.ShopSetting;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.exception.model.UnprocessableEntityException;
import com.nak.demo.mapper.OrderMapper;
import com.nak.demo.repository.OrderRepository;
import com.nak.demo.repository.PaymentRepository;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockManagementService stockManagementService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderPricingService orderPricingService;

    @Autowired
    private ShopSettingService shopSettingService;

    public List<OrderResponseDto> listsOrder() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseDtoList(orders);
    }

    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id :" + orderId));

        String status = String.valueOf(order.getStatus()).toUpperCase();
        if (status.equals(OrderStatus.CANCELLED.name())) {
            throw new UnprocessableEntityException("order " + orderId + " is already cancelled");
        }
        if (status.equals(OrderStatus.SUCCESS.name()) || status.equals("PAID")) {
            throw new UnprocessableEntityException("order " + orderId + " is already paid and cannot be cancelled");
        }
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.PENDING)) {
            throw new UnprocessableEntityException(" a KHQR payment for order " + orderId + " is still in progress, try again after it expires");
        }
        stockManagementService.returnStockForOrder(order.getItems());

        order.setStatus(OrderStatus.CANCELLED.name());
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(reason == null || reason.isBlank() ? null : reason.trim());
        orderRepository.save(order);

        return orderMapper.toResponseDto(order);
    }

    @Transactional
    public void createOrder(OrderCreateDto payload) {
        stockManagementService.reserveStockforOrder(payload.getOrderItems());
        Order order = orderMapper.toEntity(payload);

        saveCurrentPrices(order);
        setDiscount(order, payload.getDiscountType(), payload.getDiscountValue());

        //VAT and service rates are copied onto the order, so changing settings later never changes old bills
        ShopSetting setting = shopSettingService.getCurrent();
        order.setVatRate(setting.getVatRate());
        order.setServiceRate(setting.getServiceRate());

        orderPricingService.calculate(order);
        orderRepository.save(order);
    }

    /** Gives (or removes, with value 0) a discount on an order that is not paid yet. */
    @Transactional
    public OrderResponseDto applyDiscount(Long orderId, OrderDiscountDto payload) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id :" + orderId));

        if (!OrderStatus.PENDING.name().equalsIgnoreCase(order.getStatus())) {
            throw new UnprocessableEntityException("only an unpaid order can get a discount, order " + orderId + " is " + order.getStatus());
        }
        //a QR already on screen has the old amount, so the customer must not pay it after the price changes
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.PENDING)) {
            throw new UnprocessableEntityException("a KHQR payment for order " + orderId + " is still in progress, try again after it expires");
        }
        if (order.getSubtotal() == null) {
            throw new UnprocessableEntityException("order " + orderId + " was made before discounts existed");
        }

        setDiscount(order, payload.getDiscountType(), payload.getDiscountValue());
        orderPricingService.calculate(order);
        orderRepository.save(order);
        return orderMapper.toResponseDto(order);
    }

    public OrderResponseDto updateOrderStatus(Long orderId, OrderUpdateDto payload) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id :" + orderId));
        orderMapper.updateEntityDto(existingOrder, payload);
        orderRepository.save(existingOrder);

        return orderMapper.toResponseDto(existingOrder);
    }

    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("order not found with id :" + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    /** Copies each product's current price onto its order line. */
    private void saveCurrentPrices(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id :" + item.getProductId()));
            item.setUnitPrice(toBigDecimal(product.getPrice()));
        }
    }

    /** Checks the discount and puts it on the order. No type or value 0 means no discount. */
    private void setDiscount(Order order, String type, BigDecimal value) {
        if (type == null || type.isBlank() || value == null || value.signum() == 0) {
            order.setDiscountType(null);
            order.setDiscountValue(null);
            return;
        }
        String discountType = type.trim().toUpperCase();
        if (!discountType.equals(DiscountType.PERCENT.name()) && !discountType.equals(DiscountType.AMOUNT.name())) {
            throw new UnprocessableEntityException("discount type must be PERCENT or AMOUNT");
        }
        if (discountType.equals(DiscountType.PERCENT.name()) && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new UnprocessableEntityException("percent discount must be at most 100");
        }
        order.setDiscountType(discountType);
        order.setDiscountValue(value);
    }

    private BigDecimal toBigDecimal(Number n) {
        if (n == null) {
            throw new IllegalStateException("Product price is missing");
        }
        return (n instanceof BigDecimal) ? (BigDecimal) n : BigDecimal.valueOf(n.doubleValue());
    }
}
