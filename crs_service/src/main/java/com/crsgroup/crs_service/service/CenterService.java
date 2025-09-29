package com.crsgroup.crs_service.service;

import com.crsgroup.crs_service.model.Center;
import com.crsgroup.crs_service.repository.CenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CenterService {

    @Autowired
    private CenterRepository repo;

    // ----------------------------
    // Existing: get active centers by service type
    // ----------------------------
    public List<Center> getActiveCentersFor(String serviceType) {
        return repo.findByStatus("ACTIVE").stream()
                   .filter(c -> serviceType == null || c.supports(serviceType))
                   .collect(Collectors.toList());
    }

    // ----------------------------
    // New helper method: get center by centerId (used in release/confirm)
    // ----------------------------
    public Optional<Center> getCenterById(String centerId) {
        return repo.findByStatus("ACTIVE").stream()
                   .filter(c -> c.getCentreId().equals(centerId))
                   .findFirst();
    }

    // ----------------------------
    // Existing: get all active centers
    // ----------------------------
    public List<Center> getAllActiveCenters() {
        return repo.findByStatus("ACTIVE");
    }
}