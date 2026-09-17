package com.vibran.domain.geofence.repository;

import com.vibran.domain.geofence.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    // All active geofences for an owner — management screen
    List<Geofence> findByOwnerIdAndIsActiveTrue(Long ownerId);

    // All active geofences scoped to a specific vehicle
    List<Geofence> findByVehicleIdAndIsActiveTrue(Long vehicleId);

    // ── Used by AlertRulesEngine on every GPS packet ──────
    // Returns geofences that apply to this vehicle:
    //   1. Geofences specifically assigned to this vehicle
    //   2. Owner-wide geofences (vehicle_id IS NULL)
    @Query("SELECT g FROM Geofence g " +
           "WHERE g.isActive = true " +
           "AND g.owner.id = :ownerId " +
           "AND (g.vehicle.id = :vehicleId OR g.vehicle IS NULL)")
    List<Geofence> findActiveGeofencesForVehicle(
        @Param("ownerId")   Long ownerId,
        @Param("vehicleId") Long vehicleId);

    boolean existsByOwnerIdAndNameAndIsActiveTrue(Long ownerId, String name);
}
