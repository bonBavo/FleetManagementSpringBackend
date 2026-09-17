-- =============================================================
-- V20__seed_initial_data.sql
-- FleetSense Kenya — Initial seed data
-- Realistic Nairobi fleet, matatu SACCOs, drivers, devices
-- Password for all users: Admin@1234 (BCrypt hash below)
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Clean existing data to avoid conflicts if needed
TRUNCATE TABLE audit_logs;
TRUNCATE TABLE notifications;
TRUNCATE TABLE alerts;
TRUNCATE TABLE energy_logs;
TRUNCATE TABLE trips;
TRUNCATE TABLE geofences;
TRUNCATE TABLE sacco_memberships;
TRUNCATE TABLE device_registry;
TRUNCATE TABLE vehicles;
TRUNCATE TABLE vehicle_models;
TRUNCATE TABLE vehicle_makes;
TRUNCATE TABLE user_profiles;
TRUNCATE TABLE saccos;
TRUNCATE TABLE users;

-- =============================================================
-- USERS
-- Password hash = BCrypt of "Admin@1234"
-- =============================================================
INSERT INTO users (id, full_name, email, phone, password_hash, role, is_active, is_deleted, is_email_verified, created_at, updated_at)
VALUES
  (1, 'Victor Wachira',       'victor@fleetsense.co.ke',  '+254712000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'SUPER_ADMIN', 1, 0, 1, NOW(), NOW()),
  (2, 'Braven Andrew Amuli',  'braven@fleetsense.co.ke',  '+254712000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'ADMIN',       1, 0, 1, NOW(), NOW()),
  (3, 'Urban Sumba',          'urban@fleetsense.co.ke',   '+254712000003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'MANAGER',     1, 0, 1, NOW(), NOW()),
  (4, 'James Otieno',         'james.otieno@gmail.com',   '+254722111001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'DRIVER',      1, 0, 1, NOW(), NOW()),
  (5, 'Mary Njoki',           'mary.njoki@gmail.com',     '+254733222002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'DRIVER',      1, 0, 1, NOW(), NOW()),
  (6, 'Peter Kamau',          'peter.kamau@gmail.com',    '+254744333003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'DRIVER',      1, 0, 1, NOW(), NOW()),
  (7, 'Grace Akinyi',         'grace.akinyi@gmail.com',   '+254755444004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'DRIVER',      1, 0, 1, NOW(), NOW()),
  (8, 'John Mwangi',          'john.mwangi@gmail.com',    '+254766555005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'DRIVER',      1, 0, 1, NOW(), NOW()),
  (9, 'Alice Wanjiru',        'alice.wanjiru@gmail.com',  '+254777666006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'MANAGER',     1, 0, 1, NOW(), NOW()),
  (10,'Samuel Kipchoge',      'samuel.kip@gmail.com',     '+254788777007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8yQnPcR3y1234hBcnS', 'DRIVER',      1, 0, 1, NOW(), NOW());

-- =============================================================
-- USER PROFILES
-- =============================================================
INSERT INTO user_profiles (user_id, city, country, license_number, emergency_contact, emergency_phone, created_at, updated_at)
VALUES
  (1,  'Nairobi', 'Kenya', NULL,              'Janet Wachira',   '+254720000001', NOW(), NOW()),
  (2,  'Nairobi', 'Kenya', NULL,              'Rose Amuli',      '+254720000002', NOW(), NOW()),
  (3,  'Nairobi', 'Kenya', NULL,              'Tom Sumba',       '+254720000003', NOW(), NOW()),
  (4,  'Nairobi', 'Kenya', 'DL-KE-2021-0041', 'Agnes Otieno',   '+254720000041', NOW(), NOW()),
  (5,  'Nairobi', 'Kenya', 'DL-KE-2020-0052', 'Paul Njoki',     '+254720000052', NOW(), NOW()),
  (6,  'Nairobi', 'Kenya', 'DL-KE-2019-0063', 'Susan Kamau',    '+254720000063', NOW(), NOW()),
  (7,  'Kisumu',  'Kenya', 'DL-KE-2022-0074', 'Brian Akinyi',   '+254720000074', NOW(), NOW()),
  (8,  'Nairobi', 'Kenya', 'DL-KE-2018-0085', 'Mercy Mwangi',   '+254720000085', NOW(), NOW()),
  (9,  'Nairobi', 'Kenya', NULL,              'Ken Wanjiru',     '+254720000096', NOW(), NOW()),
  (10, 'Eldoret', 'Kenya', 'DL-KE-2023-0107', 'Faith Kipchoge', '+254720000107', NOW(), NOW());

-- =============================================================
-- VEHICLE MAKES
-- =============================================================
INSERT INTO vehicle_makes (id, name, country_of_origin, created_at)
VALUES
  (1,  'Toyota',     'Japan',  NOW()),
  (2,  'Isuzu',      'Japan',  NOW()),
  (3,  'Nissan',     'Japan',  NOW()),
  (4,  'Mitsubishi', 'Japan',  NOW()),
  (5,  'Mercedes',   'Germany',NOW()),
  (6,  'Bajaj',      'India',  NOW()),
  (7,  'KIBO',       'Kenya',  NOW());

-- =============================================================
-- VEHICLE MODELS
-- =============================================================
INSERT INTO vehicle_models (id, make_id, name, fuel_capacity_l, created_at)
VALUES
  (1,  1, 'Hiace',        65,   NOW()),
  (2,  1, 'Land Cruiser', 93,   NOW()),
  (3,  1, 'Hilux',        80,   NOW()),
  (4,  2, 'NPR',          100,  NOW()),
  (5,  2, 'NQR',          120,  NOW()),
  (6,  3, 'Civilian',     60,   NOW()),
  (7,  4, 'Rosa',         100,  NOW()),
  (8,  5, 'Sprinter',     75,   NOW()),
  (9,  6, 'RE',           6,    NOW()),
  (10, 7, 'Nili',         NULL, NOW());

-- =============================================================
-- VEHICLES
-- =============================================================
INSERT INTO vehicles (id, owner_id, model_id, plate_number, year, color,
                      powertrain_type, vehicle_category, seating_capacity,
                      status, is_deleted, created_at, updated_at)
VALUES
  (1,  2, 1,  'KDA 123A', 2020, 'White',   'ICE',      'MATATU_14', 14, 'ACTIVE', 0, NOW(), NOW()),
  (2,  2, 1,  'KBX 456B', 2021, 'Yellow',  'ICE',      'MATATU_14', 14, 'ACTIVE', 0, NOW(), NOW()),
  (3,  2, 6,  'KCC 789C', 2019, 'White',   'ICE',      'MATATU_33', 33, 'ACTIVE', 0, NOW(), NOW()),
  (4,  3, 4,  'KDD 012D', 2022, 'Blue',    'ICE',      'TRUCK',     NULL,'ACTIVE', 0, NOW(), NOW()),
  (5,  3, 3,  'KDE 345E', 2023, 'Silver',  'ICE',      'PICKUP',    NULL,'ACTIVE', 0, NOW(), NOW()),
  (6,  9, 10, 'KNBI 001', 2024, 'Green',   'ELECTRIC', 'PERSONAL_CAR',5, 'ACTIVE', 0, NOW(), NOW()),
  (7,  9, 10, 'KNBI 002', 2024, 'Blue',    'ELECTRIC', 'PERSONAL_CAR',5, 'ACTIVE', 0, NOW(), NOW()),
  (8,  2, 7,  'KFF 678F', 2018, 'White',   'ICE',      'MATATU_33', 33, 'ACTIVE', 0, NOW(), NOW()),
  (9,  3, 2,  'KGG 901G', 2021, 'Black',   'ICE',      'PERSONAL_CAR',5,'INACTIVE',0,NOW(), NOW()),
  (10, 2, 1,  'KHH 234H', 2022, 'White',   'HYBRID',   'MATATU_14', 14, 'ACTIVE', 0, NOW(), NOW());

-- =============================================================
-- DEVICE REGISTRY
-- =============================================================
INSERT INTO device_registry (id, vehicle_id, device_serial, sim_iccid, sim_phone_number,
                              device_token, firmware_version, status,
                              assigned_at, last_seen_at, created_at, updated_at)
VALUES
  (1,  1,  'ESP32-AA001122', '8925301000000000001', '+254700001001', 'token-fleet-secure-001', '1.0.0', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (2,  2,  'ESP32-BB003344', '8925301000000000002', '+254700001002', 'token-fleet-secure-002', '1.0.0', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (3,  3,  'ESP32-CC005566', '8925301000000000003', '+254700001003', 'token-fleet-secure-003', '1.0.0', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (4,  4,  'ESP32-DD007788', '8925301000000000004', '+254700001004', 'token-fleet-secure-004', '1.0.1', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (5,  5,  'ESP32-EE009900', '8925301000000000005', '+254700001005', 'token-fleet-secure-005', '1.0.1', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (6,  6,  'ESP32-FF001111', '8925301000000000006', '+254700001006', 'token-fleet-secure-006', '1.0.2', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (7,  7,  'ESP32-GG002222', '8925301000000000007', '+254700001007', 'token-fleet-secure-007', '1.0.2', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (8,  8,  'ESP32-HH003333', '8925301000000000008', '+254700001008', 'token-fleet-secure-008', '1.0.0', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (9,  10, 'ESP32-JJ005555', '8925301000000000010', '+254700001010', 'token-fleet-secure-010', '1.0.1', 'ACTIVE',   NOW(), NOW(), NOW(), NOW()),
  (10, NULL,'ESP32-KK006666', NULL,                 NULL,            'token-fleet-secure-011', '1.0.0', 'UNASSIGNED',NULL, NULL,  NOW(), NOW());

-- =============================================================
-- SACCOs
-- =============================================================
INSERT INTO saccos (id, name, registration_number, route_description,
                    contact_email, contact_phone, county, is_active, created_at, updated_at)
VALUES
  (1, 'Githurai Matatu Owners Association', 'SACCO-NRB-2019-001',
   'Nairobi CBD — Githurai 45',
   'info@githurai45.co.ke', '+254700100001', 'Nairobi', 1, NOW(), NOW()),

  (2, 'Westlands Express SACCO', 'SACCO-NRB-2018-002',
   'Nairobi CBD — Westlands — Parklands',
   'info@westlandsexpress.co.ke', '+254700100002', 'Nairobi', 1, NOW(), NOW()),

  (3, 'Rongai Travelers SACCO', 'SACCO-NRB-2020-003',
   'Nairobi CBD — Rongai — Ngong',
   'info@rongaitravelers.co.ke', '+254700100003', 'Kajiado', 1, NOW(), NOW()),

  (4, 'Thika Road Shuttle SACCO', 'SACCO-NRB-2017-004',
   'Nairobi CBD — Kasarani — Thika',
   'info@thikaroad.co.ke', '+254700100004', 'Nairobi', 1, NOW(), NOW());

-- =============================================================
-- SACCO MEMBERSHIPS
-- =============================================================
INSERT INTO sacco_memberships (id, sacco_id, vehicle_id, owner_id, role,
                                route_code, is_active, joined_at, created_at, updated_at)
VALUES
  (1, 1, 1,  2, 'MEMBER', 'CBD-GITHURAI-45',  1, NOW(), NOW(), NOW()),
  (2, 1, 2,  2, 'MEMBER', 'CBD-GITHURAI-45',  1, NOW(), NOW(), NOW()),
  (3, 2, 3,  2, 'OWNER',  'CBD-WESTLANDS-46', 1, NOW(), NOW(), NOW()),
  (4, 1, 8,  2, 'MEMBER', 'CBD-GITHURAI-45',  1, NOW(), NOW(), NOW()),
  (5, 4, 10, 2, 'MEMBER', 'CBD-KASARANI-145', 1, NOW(), NOW(), NOW());

-- =============================================================
-- GEOFENCES
-- =============================================================
INSERT INTO geofences (id, owner_id, vehicle_id, name, description,
                       center_latitude, center_longitude, radius_meters,
                       alert_on_entry, alert_on_exit, is_active, created_at, updated_at)
VALUES
  (1, 2, NULL, 'Nairobi CBD Depot',
   'Main operating and parking zone — all matatus',
   -1.2840, 36.8219, 1500, 1, 1, 1, NOW(), NOW()),

  (2, 2, 1, 'Githurai Terminal',
   'End terminus for CBD-Githurai-45 route',
   -1.1833, 36.9000, 500,  1, 1, 1, NOW(), NOW()),

  (3, 2, 3, 'Westlands Stage',
   'Westlands matatu stage and pick-up zone',
   -1.2676, 36.8030, 300,  0, 1, 1, NOW(), NOW()),

  (4, 3, 4, 'Mombasa Road Checkpoint',
   'Truck route monitoring — Mombasa Road',
   -1.3200, 36.8400, 2000, 0, 1, 1, NOW(), NOW()),

  (5, 9, 6, 'Kilimani Charging Hub',
   'EV charging station — Kilimani',
   -1.2900, 36.7800, 200,  1, 1, 1, NOW(), NOW());

-- =============================================================
-- TRIPS (recent completed trips)
-- =============================================================
INSERT INTO trips (id, vehicle_id, driver_id, device_id,
                   start_time, end_time,
                   start_latitude, start_longitude, start_address,
                   end_latitude,   end_longitude,   end_address,
                   distance_km, duration_minutes, max_speed_kmh, avg_speed_kmh,
                   fuel_consumed_l, status,
                   route_code, passenger_count, fare_collected_kes, trip_type,
                   created_at, updated_at)
VALUES
  (1, 1, 4, 1,
   DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR),
   -1.2840, 36.8219, 'Kencom House, CBD',
   -1.1833, 36.9000, 'Githurai 45 Stage',
   18.5, 55, 78.0, 32.0, 2.8, 'COMPLETED',
   'CBD-GITHURAI-45', 12, 648.00, 'MATATU_ROUTE',
   NOW(), NOW()),

  (2, 2, 5, 2,
   DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR),
   -1.2840, 36.8219, 'Kencom House, CBD',
   -1.1833, 36.9000, 'Githurai 45 Stage',
   18.5, 60, 82.0, 29.0, 3.1, 'COMPLETED',
   'CBD-GITHURAI-45', 14, 756.00, 'MATATU_ROUTE',
   NOW(), NOW()),

  (3, 3, 6, 3,
   DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR),
   -1.2840, 36.8219, 'Archive Road, CBD',
   -1.2676, 36.8030, 'Westlands Stage',
   5.2, 38, 65.0, 22.0, 1.2, 'COMPLETED',
   'CBD-WESTLANDS-46', 28, 840.00, 'MATATU_ROUTE',
   NOW(), NOW()),

  (4, 4, NULL, 4,
   DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR),
   -1.2921, 36.8219, 'Industrial Area, Nairobi',
   -4.0435, 39.6682, 'Mombasa Port',
   490.0, 360, 110.0, 78.0, 68.0, 'COMPLETED',
   NULL, NULL, NULL, 'DELIVERY',
   NOW(), NOW()),

  (5, 1, 4, 1,
   DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL,
   -1.1833, 36.9000, 'Githurai 45 Stage',
   NULL, NULL, NULL,
   NULL, NULL, NULL, NULL, NULL, 'ACTIVE',
   'CBD-GITHURAI-45', NULL, NULL, 'MATATU_ROUTE',
   NOW(), NOW());

-- =============================================================
-- ENERGY LOGS (fuel and EV readings)
-- =============================================================
INSERT INTO energy_logs (id, vehicle_id, trip_id, fuel_level_pct, fuel_volume_l,
                         energy_type, event_type,
                         latitude, longitude, recorded_at, created_at)
VALUES
  -- Matatu 1 readings
  (1,  1, 1,  78.0, NULL, 'FUEL', 'TRIP_START_SNAPSHOT', -1.2840, 36.8219, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW()),
  (2,  1, 1,  72.5, NULL, 'FUEL', 'TRIP_END_SNAPSHOT',   -1.1833, 36.9000, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
  (3,  1, 5,  71.0, NULL, 'FUEL', 'TRIP_START_SNAPSHOT', -1.1833, 36.9000, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
  (4,  1, NULL,70.0, NULL, 'FUEL', 'PERIODIC_SNAPSHOT',   -1.2100, 36.8500, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),

  -- Matatu 2 readings
  (5,  2, 2,  65.0, NULL, 'FUEL', 'TRIP_START_SNAPSHOT', -1.2840, 36.8219, DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
  (6,  2, 2,  58.0, NULL, 'FUEL', 'TRIP_END_SNAPSHOT',   -1.1833, 36.9000, DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW()),
  (7,  2, NULL,57.5, NULL, 'FUEL', 'PERIODIC_SNAPSHOT',   -1.1833, 36.9000, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),

  -- Truck readings (low fuel warning)
  (8,  4, 4,  45.0, NULL, 'FUEL', 'TRIP_START_SNAPSHOT', -1.2921, 36.8219, DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW()),
  (9,  4, 4,  12.5, NULL, 'FUEL', 'TRIP_END_SNAPSHOT',   -4.0435, 39.6682, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW()),
  (10, 4, NULL,12.0, NULL, 'FUEL', 'LOW_CHARGE_WARNING',  -4.0435, 39.6682, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW()),

  -- EV readings (vehicle 6)
  (11, 6, NULL, 85.0, NULL, 'ELECTRIC_CHARGE', 'PERIODIC_SNAPSHOT',    -1.2900, 36.7800, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
  (12, 6, NULL, 82.0, NULL, 'ELECTRIC_CHARGE', 'PERIODIC_SNAPSHOT',    -1.2921, 36.8219, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
  (13, 7, NULL, 45.0, NULL, 'ELECTRIC_CHARGE', 'PERIODIC_SNAPSHOT',    -1.3000, 36.8100, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
  (14, 7, NULL, 18.0, NULL, 'ELECTRIC_CHARGE', 'LOW_CHARGE_WARNING',   -1.3100, 36.8000, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),

  -- Fuel theft simulation on vehicle 3
  (15, 3, NULL, 60.0, NULL, 'FUEL', 'PERIODIC_SNAPSHOT', -1.2676, 36.8030, DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW()),
  (16, 3, NULL, 47.0, NULL, 'FUEL', 'FUEL_DROP_DETECTED',-1.2676, 36.8030, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW());

-- =============================================================
-- ALERTS
-- =============================================================
INSERT INTO alerts (id, vehicle_id, device_id, trip_id,
                    alert_type, severity, title, message,
                    latitude, longitude, speed_at_alert,
                    threshold_value, actual_value,
                    is_acknowledged, acknowledged_at, is_resolved, resolved_at,
                    cooldown_until, triggered_at, created_at, updated_at)
VALUES
  -- Overspeed on matatu 2
  (1, 2, 2, 2,
   'OVERSPEED', 'HIGH',
   'Overspeed detected — KBX 456B',
   'Vehicle travelling at 82.0 km/h (limit: 80 km/h)',
   -1.2400, 36.8600, 82.0, 80.0, 82.0,
   0, NULL, 0, NULL,
   DATE_ADD(NOW(), INTERVAL 5 MINUTE),
   DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW(), NOW()),

  -- Fuel theft on matatu 3
  (2, 3, 3, NULL,
   'FUEL_THEFT', 'CRITICAL',
   'Fuel theft suspected — KCC 789C',
   'Fuel dropped 60.0% → 47.0% suddenly',
   -1.2676, 36.8030, NULL, 10.0, 13.0,
   0, NULL, 0, NULL,
   DATE_ADD(NOW(), INTERVAL 60 MINUTE),
   DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW()),

  -- Low fuel on truck
  (3, 4, 4, 4,
   'LOW_FUEL', 'CRITICAL',
   'Low fuel — KDD 012D',
   'Fuel tank at 12.0% (threshold: 15%)',
   -4.0435, 39.6682, NULL, 15.0, 12.0,
   1, DATE_SUB(NOW(), INTERVAL 3 HOUR), 0, NULL,
   DATE_ADD(NOW(), INTERVAL 30 MINUTE),
   DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW()),

  -- Low battery EV
  (4, 7, 7, NULL,
   'SYSTEM_ERROR', 'HIGH',
   'Low battery — KNBI 002',
   'Battery at 18.0% (threshold: 20%)',
   -1.3100, 36.8000, NULL, 20.0, 18.0,
   0, NULL, 0, NULL,
   DATE_ADD(NOW(), INTERVAL 30 MINUTE),
   DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()),

  -- Geofence exit on matatu 1
  (5, 1, 1, 5,
   'GEOFENCE_EXIT', 'MEDIUM',
   'Geofence exit — KDA 123A',
   'Vehicle left zone: Nairobi CBD Depot',
   -1.2100, 36.8500, NULL, NULL, NULL,
   1, DATE_SUB(NOW(), INTERVAL 1 HOUR), 1, DATE_SUB(NOW(), INTERVAL 1 HOUR),
   DATE_ADD(NOW(), INTERVAL 5 MINUTE),
   DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW());

-- =============================================================
-- NOTIFICATIONS
-- =============================================================
INSERT INTO notifications (id, user_id, alert_id, title, body,
                           channel, delivery_status, is_read,
                           sent_at, created_at, updated_at)
VALUES
  (1, 2, 1, 'Overspeed detected — KBX 456B',
   'Vehicle travelling at 82.0 km/h on CBD-GITHURAI-45',
   'PUSH', 'SENT', 0, NOW(), NOW(), NOW()),

  (2, 2, 2, 'Fuel theft suspected — KCC 789C',
   'Fuel dropped 13% suddenly. Immediate action required.',
   'PUSH', 'SENT', 0, NOW(), NOW(), NOW()),

  (3, 3, 3, 'Low fuel — KDD 012D',
   'Truck fuel at 12%. Refuel before next trip.',
   'PUSH', 'SENT', 1, NOW(), NOW(), NOW()),

  (4, 9, 4, 'Low battery — KNBI 002',
   'EV battery at 18%. Head to nearest charging point.',
   'PUSH', 'SENT', 0, NOW(), NOW(), NOW()),

  (5, 2, 5, 'Geofence exit — KDA 123A',
   'Vehicle has left the Nairobi CBD Depot zone.',
   'PUSH', 'SENT', 1, NOW(), NOW(), NOW());

-- =============================================================
-- AUDIT LOGS
-- =============================================================
INSERT INTO audit_logs (actor_id, actor_role, action, entity_type,
                        entity_id, description, created_at)
VALUES
  (1, 'SUPER_ADMIN', 'USER_CREATED', 'users', 2, 'Super admin created backend engineer account', DATE_SUB(NOW(), INTERVAL 30 DAY)),
  (1, 'SUPER_ADMIN', 'USER_CREATED', 'users', 3, 'Super admin created manager account', DATE_SUB(NOW(), INTERVAL 30 DAY)),
  (2, 'ADMIN', 'VEHICLE_REGISTERED', 'vehicles', 1, 'Admin registered matatu KDA 123A', DATE_SUB(NOW(), INTERVAL 20 DAY)),
  (2, 'ADMIN', 'VEHICLE_REGISTERED', 'vehicles', 2, 'Admin registered matatu KBX 456B', DATE_SUB(NOW(), INTERVAL 20 DAY)),
  (2, 'ADMIN', 'VEHICLE_REGISTERED', 'vehicles', 3, 'Admin registered matatu KCC 789C', DATE_SUB(NOW(), INTERVAL 20 DAY)),
  (2, 'ADMIN', 'DEVICE_ASSIGNED',    'devices',  1, 'Device ESP32-AA001122 assigned to KDA 123A', DATE_SUB(NOW(), INTERVAL 15 DAY)),
  (2, 'ADMIN', 'DEVICE_ASSIGNED',    'devices',  2, 'Device ESP32-BB003344 assigned to KBX 456B', DATE_SUB(NOW(), INTERVAL 15 DAY)),
  (2, 'ADMIN', 'SACCO_JOINED',       'sacco_memberships', 1, 'KDA 123A joined Githurai SACCO', DATE_SUB(NOW(), INTERVAL 10 DAY)),
  (2, 'ADMIN', 'SACCO_JOINED',       'sacco_memberships', 2, 'KBX 456B joined Githurai SACCO', DATE_SUB(NOW(), INTERVAL 10 DAY)),
  (1, 'SUPER_ADMIN', 'USER_CREATED', 'users', 4, 'Driver James Otieno registered', DATE_SUB(NOW(), INTERVAL 5 DAY));

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
-- VERIFY SEED
-- =============================================================
SELECT 'users'            AS tbl, COUNT(*) AS `count` FROM users
UNION ALL SELECT 'vehicles',          COUNT(*) FROM vehicles
UNION ALL SELECT 'device_registry',   COUNT(*) FROM device_registry
UNION ALL SELECT 'saccos',            COUNT(*) FROM saccos
UNION ALL SELECT 'sacco_memberships', COUNT(*) FROM sacco_memberships
UNION ALL SELECT 'geofences',         COUNT(*) FROM geofences
UNION ALL SELECT 'trips',             COUNT(*) FROM trips
UNION ALL SELECT 'energy_logs',       COUNT(*) FROM energy_logs
UNION ALL SELECT 'alerts',            COUNT(*) FROM alerts
UNION ALL SELECT 'notifications',     COUNT(*) FROM notifications
UNION ALL SELECT 'audit_logs',        COUNT(*) FROM audit_logs;
