package com.vibran.domain.vehicle.repository;
//
//
//import com.vibran.domain.device.entity.DeviceRegistry;
//import com.vibran.shared.enums.DeviceStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface DeviceRegistryRepository
//        extends JpaRepository<DeviceRegistry, Long> {
//
//    // One-active-per-vehicle check (replaces DB generated column)
//    boolean existsByVehicleIdAndStatus(Long vehicleId, DeviceStatus status);
//
//    // Get active device for a vehicle
//    Optional<DeviceRegistry> findByVehicleIdAndStatus(
//            Long vehicleId, DeviceStatus status);
//
//    // Auth: ESP32 identifies itself by token on every MQTT message
//    Optional<DeviceRegistry> findByDeviceToken(String deviceToken);
//
//    Optional<DeviceRegistry> findByDeviceSerial(String serial);
//
//    // Offline detection — devices not seen in X minutes
//    List<DeviceRegistry> findByStatusAndLastSeenAtBefore(
//            DeviceStatus status, Instant threshold);
//}