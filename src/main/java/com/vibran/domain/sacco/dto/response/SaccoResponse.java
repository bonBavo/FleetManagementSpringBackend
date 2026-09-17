package com.vibran.domain.sacco.dto.response;

import lombok.Data;
import java.time.Instant;

@Data
public class SaccoResponse {
    private Long   id;
    private String name;
    private String registrationNumber;
    private String routeDescription;
    private String contactEmail;
    private String contactPhone;
    private String county;
    private Boolean isActive;
    private Long    memberCount;
    private Instant createdAt;
}
