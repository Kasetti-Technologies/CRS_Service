package com.crsgroup.crs_service.model;

import java.time.LocalDateTime;

public class LockRequestDto {
    public String centerId;
    public Long slotId;
    public String serviceType;
    public LocalDateTime slotTime;
}