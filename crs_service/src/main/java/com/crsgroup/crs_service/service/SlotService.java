package com.crsgroup.crs_service.service;

import com.crsgroup.crs_service.model.Center;
import com.crsgroup.crs_service.model.Booking;
import com.crsgroup.crs_service.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlotService {

    @Autowired
    private CenterService centerService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // ----------------------------
    // Lock Slot
    // ----------------------------
    public Booking lockSlot(String centerId, Long slotId, String serviceType) {
        Center center = centerService.getActiveCentersFor(serviceType).stream()
                .filter(c -> c.getCentreId().equals(centerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Center not found or service not supported"));

        String url = center.getBaseUrl() + "/dcenter/lock?slotId=" + slotId;

        try {
            WebClient client = webClientBuilder.build();

            LockResponse response = client.post()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(LockResponse.class)
                    .block();

            if (response == null || response.lockId == null) {
                throw new RuntimeException("Failed to lock slot at center");
            }

            Booking booking = new Booking();
            booking.setBookingId(UUID.randomUUID().toString());
            booking.setLockId(response.lockId);
            booking.setCenterId(centerId);
            booking.setSlotTime(response.slotTime);
            booking.setStatus("LOCKED");
            booking.setPaymentId(UUID.randomUUID().toString());

            return bookingRepository.save(booking);

        } catch (Exception ex) {
            log.error("Error locking slot at {}: {}", center.getBaseUrl(), ex.getMessage());
            throw new RuntimeException("Slot locking failed");
        }
    }

    // ----------------------------
    // Release Slot
    // ----------------------------
    public Booking releaseSlot(String paymentId) {
        Booking booking = bookingRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Booking not found for paymentId " + paymentId));

        if (booking.getLockId() == null) {
            throw new RuntimeException("No lockId found for this booking");
        }

        Center center = centerService.getCenterById(booking.getCenterId())
                .orElseThrow(() -> new RuntimeException("Center not found"));

        String url = center.getBaseUrl() + "/dcenter/release?lockId=" + booking.getLockId();

        try {
            WebClient client = webClientBuilder.build();

            ReleaseResponse response = client.post()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ReleaseResponse.class)
                    .block();

            booking.setStatus("RELEASED");
            booking.setLockId(null);
            return bookingRepository.save(booking);

        } catch (Exception ex) {
            log.error("Error releasing slot at {}: {}", center.getBaseUrl(), ex.getMessage());
            throw new RuntimeException("Slot release failed");
        }
    }

    // ----------------------------
    // Confirm Slot
    // ----------------------------
    public Booking confirmSlot(String paymentId) {
        Booking booking = bookingRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Booking not found for paymentId " + paymentId));

        if (booking.getLockId() == null) {
            throw new RuntimeException("No lockId found for this booking");
        }

        Center center = centerService.getCenterById(booking.getCenterId())
                .orElseThrow(() -> new RuntimeException("Center not found"));

        String url = center.getBaseUrl() + "/dcenter/confirm?lockId=" + booking.getLockId();

        try {
            WebClient client = webClientBuilder.build();

            ConfirmResponse response = client.post()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ConfirmResponse.class)
                    .block();

            booking.setStatus("CONFIRMED");
            return bookingRepository.save(booking);

        } catch (Exception ex) {
            log.error("Error confirming slot at {}: {}", center.getBaseUrl(), ex.getMessage());
            throw new RuntimeException("Slot confirm failed");
        }
    }

    // ----------------------------
    // Internal Response DTOs
    // ----------------------------
    public static class LockResponse {
        public Long id;
        public String serviceType;
        public java.time.LocalDateTime slotTime;
        public String status;
        public String lockId;
    }

    public static class ReleaseResponse {
        public Long id;
        public String serviceType;
        public java.time.LocalDateTime slotTime;
        public String status;
        public String lockId;
    }

    public static class ConfirmResponse {
        public Long id;
        public String serviceType;
        public java.time.LocalDateTime slotTime;
        public String status;
        public String lockId;
    }
}