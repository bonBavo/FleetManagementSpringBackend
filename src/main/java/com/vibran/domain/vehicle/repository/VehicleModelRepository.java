package com.vibran.domain.vehicle.repository;


import com.vibran.domain.vehicle.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
    List<VehicleModel> findByMakeId(Long makeId);
    boolean existsByMakeIdAndNameIgnoreCase(Long makeId, String name);
}
