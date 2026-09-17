package com.vibran.domain.vehicle.service.interfaces;

import com.vibran.domain.vehicle.dto.request.*;
import com.vibran.domain.vehicle.dto.response.*;
import com.vibran.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface VehicleService {

    VehicleResponse register(RegisterVehicleRequest request);

    VehicleResponse getById(Long id);
    VehicleResponse update(Long id, UpdateVehicleRequest request);
    void softDelete(Long id);

    List<VehicleResponse>         getByOwner(Long ownerId);
    List<VehicleSummaryResponse>  getMatatus();

    PagedResponse<VehicleResponse> getAll(Pageable pageable);

}
