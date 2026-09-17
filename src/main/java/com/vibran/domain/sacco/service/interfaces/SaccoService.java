package com.vibran.domain.sacco.service.interfaces;

import com.vibran.domain.sacco.dto.request.*;
import com.vibran.domain.sacco.dto.response.*;
import com.vibran.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SaccoService {
    SaccoResponse              create(CreateSaccoRequest request);
    SaccoResponse              getById(Long id);
    PagedResponse<SaccoResponse> getAll(Pageable pageable);
    PagedResponse<SaccoResponse> getByCounty(String county, Pageable pageable);
    SaccoMembershipResponse    joinSacco(Long saccoId, JoinSaccoRequest request);
    void                       leaveSacco(Long vehicleId);
    List<SaccoMembershipResponse> getSaccoFleet(Long saccoId);
}