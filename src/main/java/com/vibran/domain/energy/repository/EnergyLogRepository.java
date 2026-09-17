package com.vibran.domain.energy.repository;


import com.vibran.domain.energy.entity.EnergyLog;
import com.vibran.shared.enums.EnergyEventType;
import com.vibran.shared.enums.EnergyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyLogRepository extends JpaRepository<EnergyLog, Long> {

    // Latest reading — Flutter dashboard gauge (fuel % or battery %)
    Optional<EnergyLog> findTopByVehicleIdOrderByRecordedAtDesc(Long vehicleId);

    // Latest by energy type — useful when vehicle is hybrid
    Optional<EnergyLog> findTopByVehicleIdAndEnergyTypeOrderByRecordedAtDesc(
            Long vehicleId, EnergyType type);

    // Chart data — Flutter history graph
    List<EnergyLog> findByVehicleIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long vehicleId, Instant from, Instant to);

    // Fuel theft or sudden charge drop events
    List<EnergyLog> findByVehicleIdAndEventTypeOrderByRecordedAtDesc(
            Long vehicleId, EnergyEventType eventType);

    // EV: all charge sessions
    List<EnergyLog> findByVehicleIdAndEnergyTypeAndEventTypeIn(
            Long vehicleId, EnergyType type, List<EnergyEventType> events);
}