package com.vibran.domain.sacco.dto.request;

import com.vibran.shared.enums.SaccoMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinSaccoRequest {
    @NotNull private Long   vehicleId;
    @NotNull private Long   ownerId;
    private SaccoMemberRole role;
    private String          routeCode;
}
