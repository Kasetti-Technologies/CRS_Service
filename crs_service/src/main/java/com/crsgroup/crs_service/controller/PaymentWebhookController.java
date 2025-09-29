package com.crsgroup.crs_service.controller;

import com.crsgroup.crs_service.model.PaymentWebhookDto;
import com.crsgroup.crs_service.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/crs/payment")
@Slf4j
public class PaymentWebhookController {

    @Autowired
    private SlotService slotService;

    @PostMapping("/webhook")
    public String handlePaymentWebhook(@RequestBody PaymentWebhookDto dto) {
        log.info("Received payment webhook: paymentId={}, status={}", dto.paymentId, dto.status);

        try {
            if ("SUCCESS".equalsIgnoreCase(dto.status)) {
                slotService.confirmSlot(dto.paymentId);
                log.info("Slot confirmed for paymentId={}", dto.paymentId);
                return "Booking confirmed";
            } else {
                slotService.releaseSlot(dto.paymentId);
                log.info("Slot released for paymentId={}", dto.paymentId);
                return "Booking released";
            }
        } catch (Exception ex) {
            log.error("Error processing payment webhook for paymentId={}: {}", dto.paymentId, ex.getMessage());
            return "Error processing webhook";
        }
    }
}