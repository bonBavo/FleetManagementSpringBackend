package com.vibran.domain.vehicle.repository;


import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.PowertrainType;
import com.vibran.shared.enums.VehicleCategory;
import com.vibran.shared.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Single lookups
    Optional<Vehicle> findByIdAndIsDeletedFalse(Long id);

    Optional<Vehicle> findByPlateNumberAndIsDeletedFalse(String plateNumber);

    // With relations — avoids N+1 on list screens
    @EntityGraph(attributePaths = {"model", "model.make", "owner"})
    Optional<Vehicle> findWithDetailsById(Long id);

    // Owner's fleet — Flutter home screen
    @EntityGraph(attributePaths = {"model", "model.make"})
    List<Vehicle> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    // Filter by status
    List<Vehicle> findByOwnerIdAndStatusAndIsDeletedFalse(
            Long ownerId, VehicleStatus status);

    // All EVs — charge monitoring dashboard
    List<Vehicle> findByPowertrainTypeAndIsDeletedFalse(PowertrainType type);

    // All matatus — SACCO management screen
    List<Vehicle> findByVehicleCategoryInAndIsDeletedFalse(
            List<VehicleCategory> categories);

    // Admin: full list paginated
    Page<Vehicle> findByIsDeletedFalse(Pageable pageable);

    // Existence checks
    boolean existsByPlateNumber(String plateNumber);

    boolean existsByVin(String vin);

    boolean existsByPlateNumberAndIdNot(String plateNumber, Long id);

    // Counts
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);

    long countByPowertrainTypeAndIsDeletedFalse(PowertrainType type);

    // Soft delete
    @Modifying
    @Query("UPDATE Vehicle v SET v.isDeleted = true, v.deletedAt = :now " +
            "WHERE v.id = :id")
    void softDelete(@Param("id") Long id, @Param("now") Instant now);
}
