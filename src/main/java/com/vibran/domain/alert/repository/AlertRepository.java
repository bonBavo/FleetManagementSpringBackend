package com.vibran.domain.alert.repository;

import com.vibran.domain.alert.entity.Alert;
import com.vibran.shared.enums.AlertSeverity;
import com.vibran.shared.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Throttle check — prevent duplicate spam alerts
    boolean existsByVehicleIdAndAlertTypeAndCooldownUntilAfter(
            Long vehicleId, AlertType type, Instant now);

    // Unacknowledged alerts per vehicle — Flutter badge count
    long countByVehicleIdAndIsAcknowledgedFalse(Long vehicleId);

    // Flutter: unacknowledged alert list
    List<Alert> findByVehicleIdAndIsAcknowledgedFalseOrderByTriggeredAtDesc(
            Long vehicleId);

    // Critical unacknowledged — dashboard warning banner
    List<Alert> findByVehicleIdAndSeverityAndIsAcknowledgedFalse(
            Long vehicleId, AlertSeverity severity);

    // Paginated history — Flutter alert history screen
    @EntityGraph(attributePaths = {"vehicle", "device", "trip", "acknowledgedBy"})
    Page<Alert> findByVehicleIdOrderByTriggeredAtDesc(
            Long vehicleId, Pageable pageable);

    // All unacknowledged alerts for an owner's fleet
    @Query("SELECT a FROM Alert a WHERE a.vehicle.owner.id = :ownerId " +
            "AND a.isAcknowledged = false " +
            "ORDER BY a.triggeredAt DESC")
    List<Alert> findUnacknowledgedByOwner(@Param("ownerId") Long ownerId);

    // Admin: all alerts paginated
    @EntityGraph(attributePaths = {"vehicle", "acknowledgedBy"})
    Page<Alert> findAllByOrderByTriggeredAtDesc(Pageable pageable);
}

