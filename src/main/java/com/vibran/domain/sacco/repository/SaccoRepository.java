package com.vibran.domain.sacco.repository;


import com.vibran.domain.sacco.entity.Sacco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SaccoRepository extends JpaRepository<Sacco, Long> {
    Optional<Sacco> findByIdAndIsActiveTrue(Long id);
    boolean existsByRegistrationNumber(String regNumber);
    Page<Sacco> findByIsActiveTrue(Pageable pageable);
    Page<Sacco> findByCountyAndIsActiveTrue(String county, Pageable pageable);
}
