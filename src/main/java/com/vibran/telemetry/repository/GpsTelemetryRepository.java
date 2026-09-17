package com.vibran.telemetry.repository;


import com.vibran.telemetry.document.GpsTelemetry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface GpsTelemetryRepository
        extends MongoRepository<GpsTelemetry, String> {

    // Latest position for a vehicle — Flutter live map marker
    Optional<GpsTelemetry> findTopByVehicleIdOrderByTimestampDesc(
            Long vehicleId);

    // Duplicate detection — reject already-seen sequence IDs
    boolean existsByVehicleIdAndSequenceId(Long vehicleId, Long sequenceId);

    // Trip replay — all points for a trip in order
    // Flutter draws polyline from these
    List<GpsTelemetry> findByTripIdOrderByTimestampAsc(Long tripId);

    // Vehicle route history between two timestamps
    List<GpsTelemetry> findByVehicleIdAndTimestampBetweenOrderByTimestampAsc(
            Long vehicleId, Instant from, Instant to);

    // Latest N points for a vehicle — Flutter trailing route line
    // Uses @Query to pass a limit
    @Query(value  = "{ 'vehicleId': ?0 }",
            sort   = "{ 'timestamp': -1 }",
            fields = "{ 'location': 1, 'speed': 1, 'timestamp': 1 }")
    List<GpsTelemetry> findLatestNByVehicleId(Long vehicleId);

    // Latest position for multiple vehicles — fleet dashboard map
    // Uses aggregation in TelemetryService — not Spring Data method
}