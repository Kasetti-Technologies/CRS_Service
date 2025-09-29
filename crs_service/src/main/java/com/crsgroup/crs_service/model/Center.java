package com.crsgroup.crs_service.model;

import jakarta.persistence.*;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="centers", schema="centerschema")
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="centre_id")
    private String centreId;

    @Column(name="name")
    private String name;//--

    @Column(name="base_url")
    private String baseUrl;

    @Column(name="supported_services")
    private String supportedServices;

    @Column(name="status")
    private String status;//--

    // getters/setters

    public boolean supports(String svc) {
        return Arrays.stream((supportedServices==null?"":supportedServices).split(","))
                     .map(String::trim).map(String::toUpperCase)
                     .anyMatch(s -> s.equals(svc.toUpperCase()));
    }
}