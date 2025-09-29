package com.crsgroup.crs_service.repository;

import com.crsgroup.crs_service.model.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long> {
    List<Center> findByStatus(String status);
}