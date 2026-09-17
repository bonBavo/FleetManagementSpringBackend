package com.vibran.domain.geofence.repository;

import com.vibran.domain.geofence.entity.Geofence;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.entity.VehicleMake;
import com.vibran.domain.vehicle.entity.VehicleModel;
import com.vibran.domain.vehicle.repository.VehicleMakeRepository;
import com.vibran.domain.vehicle.repository.VehicleModelRepository;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_ACCESS_EXPIRY_MS=3600000",
    "JWT_REFRESH_EXPIRY_MS=86400000",
    "FIREBASE_CREDENTIALS_PATH=dummy.json",
    "spring.flyway.enabled=false"
})
class GeofenceRepositoryTest {

    @Autowired private GeofenceRepository geofenceRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private VehicleModelRepository vehicleModelRepository;
    @Autowired private VehicleMakeRepository vehicleMakeRepository;

    private User owner;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .fullName("Test Owner")
                .email("owner@test.com")
                .passwordHash("hash")
                .build();
        userRepository.save(owner);

        VehicleMake make = VehicleMake.builder()
                .name("Toyota")
                .build();
        vehicleMakeRepository.save(make);

        VehicleModel model = VehicleModel.builder()
                .name("Camry")
                .make(make)
                .build();
        vehicleModelRepository.save(model);

        vehicle = Vehicle.builder()
                .owner(owner)
                .model(model)
                .plateNumber("KCC 123X")
                .year(2022)
                .build();
        vehicleRepository.save(vehicle);
    }

    @Test
    @DisplayName("Should find active geofences for a specific vehicle and owner-wide ones")
    void shouldFindActiveGeofencesForVehicle() {
        // Given
        Geofence vehicleScoped = Geofence.builder()
                .owner(owner).vehicle(vehicle).name("Vehicle Scoped")
                .centerLatitude(new BigDecimal("1.23")).centerLongitude(new BigDecimal("36.82"))
                .radiusMeters(1000).isActive(true).alertOnEntry(true).alertOnExit(true)
                .build();

        Geofence ownerWide = Geofence.builder()
                .owner(owner).vehicle(null).name("Owner Wide")
                .centerLatitude(new BigDecimal("1.24")).centerLongitude(new BigDecimal("36.83"))
                .radiusMeters(2000).isActive(true).alertOnEntry(true).alertOnExit(true)
                .build();

        Geofence inactive = Geofence.builder()
                .owner(owner).vehicle(vehicle).name("Inactive")
                .centerLatitude(new BigDecimal("1.25")).centerLongitude(new BigDecimal("36.84"))
                .radiusMeters(500).isActive(false).alertOnEntry(true).alertOnExit(true)
                .build();

        geofenceRepository.saveAll(List.of(vehicleScoped, ownerWide, inactive));

        // When
        List<Geofence> results = geofenceRepository.findActiveGeofencesForVehicle(owner.getId(), vehicle.getId());

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(g -> g.getName().equals("Vehicle Scoped")));
        assertTrue(results.stream().anyMatch(g -> g.getName().equals("Owner Wide")));
        assertFalse(results.stream().anyMatch(g -> g.getName().equals("Inactive")));
    }

    @Test
    @DisplayName("existsByOwnerIdAndNameAndIsActiveTrue should return true for existing active geofence")
    void existsByNameTest() {
        Geofence g = Geofence.builder()
                .owner(owner).name("Nairobi Office")
                .centerLatitude(new BigDecimal("1.23")).centerLongitude(new BigDecimal("36.82"))
                .radiusMeters(1000).isActive(true).alertOnEntry(true).alertOnExit(true)
                .build();
        geofenceRepository.save(g);

        assertTrue(geofenceRepository.existsByOwnerIdAndNameAndIsActiveTrue(owner.getId(), "Nairobi Office"));
        assertFalse(geofenceRepository.existsByOwnerIdAndNameAndIsActiveTrue(owner.getId(), "Mombasa Office"));
    }
}
