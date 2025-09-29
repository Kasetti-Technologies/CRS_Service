package com.crsgroup.crs_service.repository;

import com.crsgroup.crs_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentId(String paymentId);
}