package com.crsgroup.crs_service.controller;

import com.crsgroup.crs_service.model.Booking;
import com.crsgroup.crs_service.model.LockRequestDto;
import com.crsgroup.crs_service.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crs/slot")
public class SlotController {

    @Autowired
    private SlotService slotService;

    // ----------------------------
    // Lock Slot
    // ----------------------------
    @PostMapping("/lock")
    public Booking lockSlot(@RequestBody LockRequestDto request) {
        return slotService.lockSlot(request.centerId, request.slotId, request.serviceType);
    }

    // ----------------------------
    // Release Slot
    // ----------------------------
    @PostMapping("/release")
    public Booking releaseSlot(@RequestParam String paymentId) {
        return slotService.releaseSlot(paymentId);
    }

    // ----------------------------
    // Confirm Slot
    // ----------------------------
    @PostMapping("/confirm")
    public Booking confirmSlot(@RequestParam String paymentId) {
        return slotService.confirmSlot(paymentId);
    }
}