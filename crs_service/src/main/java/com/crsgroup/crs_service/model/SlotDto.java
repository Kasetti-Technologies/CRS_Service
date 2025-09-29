package com.crsgroup.crs_service.model;

import java.time.LocalDateTime;

public class SlotDto {
    public Long id;
    public String serviceType;
    public LocalDateTime slotTime;
    public String status;
    public String centreId;    // added by CRS
    public String centreName;  // added by CRS
}
