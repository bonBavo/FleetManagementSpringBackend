package com.vibran.domain.sacco.dto.response;

import com.vibran.shared.enums.SaccoMemberRole;
import lombok.Data;
import java.time.Instant;

@Data
public class SaccoMembershipResponse {
    private Long   id;
    private Long   saccoId;
    private String saccoName;
    private Long   vehicleId;
    private String vehiclePlate;
    private String ownerName;
    private SaccoMemberRole role;
    private String routeCode;
    private Instant joinedAt;
    private Boolean isActive;
}
