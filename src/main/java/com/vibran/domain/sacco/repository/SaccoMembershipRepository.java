package com.vibran.domain.sacco.repository;

import com.vibran.domain.sacco.entity.SaccoMembership;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaccoMembershipRepository
        extends JpaRepository<SaccoMembership, Long> {

    // Check active membership exists before adding
    boolean existsByVehicleIdAndIsActiveTrue(Long vehicleId);

    // Current membership for a vehicle
    Optional<SaccoMembership> findByVehicleIdAndIsActiveTrue(Long vehicleId);

    // All vehicles in a SACCO — SACCO dashboard
    @EntityGraph(attributePaths = {"vehicle","vehicle.model",
            "vehicle.model.make"})
    List<SaccoMembership> findBySaccoIdAndIsActiveTrue(Long saccoId);

    // Vehicles on a specific route
    List<SaccoMembership> findBySaccoIdAndRouteCodeAndIsActiveTrue(
            Long saccoId, String routeCode);

    // Leave SACCO
    @Modifying
    @Query("UPDATE SaccoMembership m SET m.isActive = false, m.leftAt = :now " +
            "WHERE m.vehicle.id = :vehicleId AND m.isActive = true")
    void deactivateForVehicle(@Param("vehicleId") Long vehicleId,
                              @Param("now") Instant now);
}

