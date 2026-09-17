package com.vibran.domain.geofence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibran.auth.service.JwtService;
import com.vibran.domain.geofence.dto.request.CreateGeofenceRequest;
import com.vibran.domain.geofence.dto.response.GeofenceResponse;
import com.vibran.domain.geofence.service.GeofenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GeofenceController.class)
@AutoConfigureMockMvc(addFilters = false)
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
class GeofenceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private GeofenceService geofenceService;
    @MockBean private JwtService jwtService;
    @MockBean private com.vibran.auth.service.UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("POST /api/v1/geofences should return 201 when valid")
    @WithMockUser
    void createGeofenceTest() throws Exception {
        // Given
        CreateGeofenceRequest request = new CreateGeofenceRequest();
        request.setOwnerId(1L);
        request.setName("Office");
        request.setCenterLatitude(new BigDecimal("1.23"));
        request.setCenterLongitude(new BigDecimal("36.82"));
        request.setRadiusMeters(500);

        GeofenceResponse response = new GeofenceResponse();
        response.setId(1L);
        response.setName("Office");

        when(geofenceService.create(any(CreateGeofenceRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/geofences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Office"));
    }

    @Test
    @DisplayName("POST /api/v1/geofences should return 400 when validation fails")
    @WithMockUser
    void createGeofenceValidationFailTest() throws Exception {
        // Given: Missing required fields
        CreateGeofenceRequest request = new CreateGeofenceRequest();
        request.setName(""); // Blank

        // When & Then
        mockMvc.perform(post("/api/v1/geofences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
