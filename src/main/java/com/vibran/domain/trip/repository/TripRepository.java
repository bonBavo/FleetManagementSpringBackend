package com.vibran.domain.trip.repository;


import com.vibran.domain.trip.entity.Trip;
import com.vibran.shared.enums.TripStatus;
import com.vibran.shared.enums.TripType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Prevent overlapping active trips
    boolean existsByVehicleIdAndStatus(Long vehicleId, TripStatus status);

    // Current active trip
    Optional<Trip> findByVehicleIdAndStatus(Long vehicleId, TripStatus status);

    // Flutter: vehicle trip history
    Page<Trip> findByVehicleIdOrderByStartTimeDesc(Long vehicleId, Pageable p);

    // Flutter: trip history filtered by date
    Page<Trip> findByVehicleIdAndStartTimeBetweenOrderByStartTimeDesc(
            Long vehicleId,
            Instant from,
            Instant to,
            Pageable pageable
    );

    // Driver history
    Page<Trip> findByDriverIdOrderByStartTimeDesc(Long driverId, Pageable p);

    // Matatu SACCO: trips for a route

    List<Trip> findByRouteCodeAndTripTypeAndStartTimeBetween(
            String routeCode,
            TripType tripType,
            Instant startTime,
            Instant endTime
    );
    // With full details — Flutter trip detail screen
    @EntityGraph(attributePaths = {"vehicle","vehicle.model",
            "vehicle.model.make","driver","device"})
    Optional<Trip> findWithDetailsById(Long id);

    // Analytics: total distance per vehicle
    @Query("SELECT COALESCE(SUM(t.distanceKm), 0) FROM Trip t " +
            "WHERE t.vehicle.id = :vehicleId AND t.status = 'COMPLETED'")
    Double sumDistanceByVehicleId(@Param("vehicleId") Long vehicleId);

    // Analytics: SACCO daily revenue
    @Query("SELECT COALESCE(SUM(t.fareCollectedKes), 0) FROM Trip t " +
            "WHERE t.routeCode = :route " +
            "AND t.startTime BETWEEN :from AND :to")
    Double sumFareByRouteAndPeriod(@Param("route")  String route,
                                   @Param("from")   Instant from,
                                   @Param("to")     Instant to);
}
