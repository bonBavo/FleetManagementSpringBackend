package com.vibran.auth.repository;

import com.vibran.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true, " +
            "rt.revokedAt = :now, rt.revokeReason = :reason " +
            "WHERE rt.user.id = :userId AND rt.isRevoked = false")
    void revokeAllByUserId(@Param("userId") Long userId,
                           @Param("reason") String reason,
                           @Param("now") Instant now);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true, " +
            "rt.revokedAt = :now, rt.revokeReason = :reason " +
            "WHERE rt.tokenHash = :hash")
    void revokeByTokenHash(@Param("hash") String hash,
                           @Param("reason") String reason,
                           @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :threshold")
    void deleteExpiredBefore(@Param("threshold") Instant threshold);
}