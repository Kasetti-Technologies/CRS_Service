package com.crsgroup.crs_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="bookings", schema="centerschema")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="booking_id", unique = true)
    private String bookingId;

    @Column(name="lock_id")
    private String lockId;

    @Column(name="center_id")
    private String centerId;

    @Column(name="slot_time")
    private LocalDateTime slotTime;

    @Column(name="status")
    private String status;

    @Column(name="payment_id")
    private String paymentId;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}