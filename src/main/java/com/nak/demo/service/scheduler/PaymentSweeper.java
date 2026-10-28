package com.nak.demo.service.scheduler;

import com.nak.demo.common.enums.PaymentStatus;
import com.nak.demo.repository.PaymentRepository;
import com.nak.demo.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class PaymentSweeper {

    private static final Logger log = LoggerFactory.getLogger(PaymentSweeper.class);

    @Autowired
    private PaymentService service;

    @Scheduled(fixedDelay = 60000)
    public void sweep() {
        for (UUID id : service.findPendingIds()) {
            try {
                service.refresh(id);
            } catch (Exception e) {
                log.warn("Sweep failed for {}: {}", id, e.getMessage());
            }
        }
    }
}