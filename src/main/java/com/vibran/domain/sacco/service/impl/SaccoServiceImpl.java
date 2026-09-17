package com.vibran.domain.sacco.service.impl;


import com.vibran.domain.sacco.dto.request.*;
import com.vibran.domain.sacco.dto.response.*;
import com.vibran.domain.sacco.entity.*;
import com.vibran.domain.sacco.mapper.SaccoMapper;
import com.vibran.domain.sacco.repository.*;
import com.vibran.domain.sacco.service.interfaces.SaccoService;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.shared.exception.*;
import com.vibran.shared.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class SaccoServiceImpl implements SaccoService {

    private final SaccoRepository          saccoRepository;
    private final SaccoMembershipRepository membershipRepository;
    private final VehicleRepository        vehicleRepository;
    private final UserRepository           userRepository;
    private final SaccoMapper              mapper;

    @Override @Transactional
    public SaccoResponse create(CreateSaccoRequest request) {
        if (saccoRepository.existsByRegistrationNumber(
                request.getRegistrationNumber()))
            throw new DuplicateResourceException(
                    "SACCO reg number already exists: "
                            + request.getRegistrationNumber());

        Sacco saved = saccoRepository.save(mapper.toEntity(request));
        SaccoResponse resp = mapper.toResponse(saved);
        resp.setMemberCount(0L);
        return resp;
    }

    @Override
    public SaccoResponse getById(Long id) {
        Sacco sacco = saccoRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sacco", id));
        SaccoResponse resp = mapper.toResponse(sacco);
        resp.setMemberCount((long) sacco.getMemberships().size());
        return resp;
    }

    @Override
    public PagedResponse<SaccoResponse> getAll(Pageable pageable) {
        Page<Sacco> page = saccoRepository.findByIsActiveTrue(pageable);
        return toPagedResponse(page);
    }

    @Override
    public PagedResponse<SaccoResponse> getByCounty(String county,
                                                    Pageable pageable) {
        Page<Sacco> page = saccoRepository
                .findByCountyAndIsActiveTrue(county, pageable);
        return toPagedResponse(page);
    }

    @Override @Transactional
    public SaccoMembershipResponse joinSacco(Long saccoId,
                                             JoinSaccoRequest request) {
        Sacco sacco = saccoRepository.findByIdAndIsActiveTrue(saccoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sacco", saccoId));

        Vehicle vehicle = vehicleRepository
                .findByIdAndIsDeletedFalse(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", request.getVehicleId()));

        // Guard: already a member of another SACCO
        if (membershipRepository.existsByVehicleIdAndIsActiveTrue(vehicle.getId()))
            throw new BusinessRuleException(
                    "Vehicle " + vehicle.getPlateNumber() +
                            " is already a member of a SACCO. Leave first.");

        // Guard: must be matatu to join a SACCO
        if (!vehicle.isMatatu())
            throw new BusinessRuleException(
                    "Only matatus can join a SACCO");

        User owner = userRepository
                .findByIdAndIsDeletedFalse(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Owner user", request.getOwnerId()));

        SaccoMembership membership = SaccoMembership.builder()
                .sacco(sacco)
                .vehicle(vehicle)
                .owner(owner)
                .role(request.getRole() != null
                        ? request.getRole()
                        : com.vibran.shared.enums.SaccoMemberRole.MEMBER)
                .routeCode(request.getRouteCode())
                .build();

        return mapper.toMembershipResponse(
                membershipRepository.save(membership));
    }

    @Override @Transactional
    public void leaveSacco(Long vehicleId) {
        membershipRepository.deactivateForVehicle(vehicleId, Instant.now());
    }

    @Override
    public List<SaccoMembershipResponse> getSaccoFleet(Long saccoId) {
        return membershipRepository.findBySaccoIdAndIsActiveTrue(saccoId)
                .stream().map(mapper::toMembershipResponse).toList();
    }

    private PagedResponse<SaccoResponse> toPagedResponse(Page<Sacco> page) {
        return PagedResponse.<SaccoResponse>builder()
                .content(page.getContent().stream().map(s -> {
                    SaccoResponse r = mapper.toResponse(s);
                    r.setMemberCount((long) s.getMemberships().size());
                    return r;
                }).toList())
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages()).last(page.isLast())
                .build();
    }
}