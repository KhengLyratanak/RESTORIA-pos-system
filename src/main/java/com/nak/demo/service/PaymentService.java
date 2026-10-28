package com.nak.demo.service;

import com.nak.demo.common.config.BakongProperties;
import com.nak.demo.common.enums.PaymentStatus;
import com.nak.demo.dto.response.BakongTransactionResponse;
import com.nak.demo.dto.response.PaymentResponse;
import com.nak.demo.entity.Payment;
import com.nak.demo.exception.model.BakongException;
import com.nak.demo.exception.model.InvalidPaymentException;
import com.nak.demo.exception.model.PaymentNotFoundException;
import com.nak.demo.mapper.PaymentMapper;
import com.nak.demo.repository.PaymentRepository;
import com.nak.demo.util.KhqrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final Duration MIN_RECHECK = Duration.ofSeconds(2);          // do not hammer Bakong
    private static final Duration GIVE_UP_AFTER_EXPIRY = Duration.ofMinutes(5);

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private BakongService bakong;

    @Autowired
    private BakongProperties props;

    @Autowired
    private OrderGatewayService orders;

    @Autowired
    private PaymentMapper mapper;

    @Autowired
    private Clock clock;

    @Transactional
    public PaymentResponse createKhqr(Long orderId) {
        if (orderId == null) throw new InvalidPaymentException("orderId is required");

        // The amount comes from the server's own order data, never from the browser.
        var order = orders.getPayableOrder(orderId);
        Instant now = clock.instant();

        Optional<Payment> existing =
                repository.findFirstByOrderIdAndStatusAndExpiresAtAfter(orderId, PaymentStatus.PENDING, now);
        if (existing.isPresent()
                && existing.get().getAmount().compareTo(order.amount()) == 0
                && existing.get().getCurrency().equalsIgnoreCase(order.currency())) {
            return toResponse(existing.get());   // dialog reopened: same QR, no duplicates
        }

        Instant expires = now.plusSeconds(props.qrTtlSeconds());
        var built = KhqrBuilder.build(new KhqrBuilder.Params(
                props.accountId(), props.accountInformation(),
                props.merchantName(), props.merchantCity(),
                props.merchantAccount(), props.merchantId(), props.acquiringBank(),
                order.currency(), order.amount(), order.number(), props.terminalLabel(),
                now.toEpochMilli(), expires.toEpochMilli()));
        Payment saved = repository.save(new Payment(orderId, order.amount(), order.currency().toUpperCase(),
                built.qr(), built.md5(), now, expires));
        return toResponse(saved);
    }

    @Transactional
    public PaymentResponse getStatus(UUID paymentId) {
        return toResponse(verify(paymentId));
    }

    @Transactional
    public void refresh(UUID paymentId) {
        verify(paymentId);
    }

    @Transactional(readOnly = true)
    public List<UUID> findPendingIds() {
        return repository.findIdsByStatus(PaymentStatus.PENDING);
    }

    // ----- internals -----

    private Payment verify(UUID id) {
        Payment p = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        if (p.getStatus() != PaymentStatus.PENDING) return p;

        Instant now = clock.instant();
        boolean expired = !now.isBefore(p.getExpiresAt());

        if (!expired && p.getLastCheckedAt() != null
                && Duration.between(p.getLastCheckedAt(), now).compareTo(MIN_RECHECK) < 0) {
            return p;
        }
        p.setLastCheckedAt(now);

        try {
            Optional<BakongTransactionResponse> tx = bakong.findTransaction(p.getMd5());
            if (tx.isPresent()) {
                settle(p, tx.get(), now);
                return p;
            }
            if (expired) {
                p.setStatus(PaymentStatus.EXPIRED);   // checked one last time, still unpaid
                log.info("Payment {} EXPIRED: Bakong found no transaction for md5={}", id, p.getMd5());
            }
        } catch (BakongException e) {
            log.warn("Bakong check failed for payment {}: {}", id, e.getMessage());
            // Could not verify: stay PENDING (never assume paid), but do not hang forever.
            if (now.isAfter(p.getExpiresAt().plus(GIVE_UP_AFTER_EXPIRY))) {
                p.setStatus(PaymentStatus.EXPIRED);
                log.info("Payment {} EXPIRED: gave up after Bakong errors", id);
            }
        }
        return p;
    }

    /** Mark the order paid only if amount, currency, account and uniqueness all check out. */
    private void settle(Payment p, BakongTransactionResponse tx, Instant now) {
        boolean amountOk = p.getAmount().compareTo(tx.amount()) == 0;
        boolean currencyOk = p.getCurrency().equalsIgnoreCase(tx.currency());
        boolean accountOk =
                tx.toAccountId() != null
                        && !tx.toAccountId().isBlank()
                        && props.accountId() != null
                        && !props.accountId().isBlank()
                        && tx.toAccountId().equalsIgnoreCase(props.accountId());

        if (amountOk && currencyOk && accountOk && bakong.recordTransaction(p.getId(), p.getMd5(), tx)) {
            p.setStatus(PaymentStatus.PAID);
            p.setPaidAt(now);
            p.setBakongHash(tx.hash());
            orders.markPaid(p.getOrderId(), p.getId());
            log.info("Payment {} verified and settled for order {}", p.getId(), p.getOrderId());
        } else {
            p.setStatus(PaymentStatus.MISMATCH);
            log.error("Payment {} MISMATCH: expected {} {} to {}, got {} {} to {}", p.getId(),
                    p.getAmount(), p.getCurrency(), props.accountId(), tx.amount(), tx.currency(), tx.toAccountId());
        }
    }

    private PaymentResponse toResponse(Payment p) {
        long remaining = Math.max(0, Duration.between(clock.instant(), p.getExpiresAt()).getSeconds());
        return mapper.toResponse(p, remaining);
    }
}
 