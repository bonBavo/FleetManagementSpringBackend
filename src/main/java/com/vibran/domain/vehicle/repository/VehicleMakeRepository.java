package com.vibran.domain.vehicle.repository;


import com.vibran.domain.vehicle.entity.VehicleMake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleMakeRepository extends JpaRepository<VehicleMake, Long> {
    Optional<VehicleMake> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
