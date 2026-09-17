package com.vibran.domain.user.repositiory;

import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findWithProfileById(Long id);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Page<User> findByIsDeletedFalse(Pageable pageable);

    Page<User> findByRoleAndIsDeletedFalse(UserRole role, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true, u.deletedAt = :now " +
            "WHERE u.id = :id")
    void softDelete(@Param("id") Long id, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE User u SET u.isActive = false, " +
            "u.suspendedAt = :now, u.suspendedReason = :reason " +
            "WHERE u.id = :id")
    void suspend(@Param("id") Long id,
                 @Param("reason") String reason,
                 @Param("now") Instant now);

    @Modifying
    @Query("UPDATE User u SET u.isActive = true, " +
            "u.suspendedAt = null, u.suspendedReason = null " +
            "WHERE u.id = :id")
    void unsuspend(@Param("id") Long id);

    List<User> findByRoleAndIsActiveTrueAndIsDeletedFalse(UserRole role);
    long countByRoleAndIsDeletedFalse(UserRole role);
    long countByIsActiveTrueAndIsDeletedFalse();

    Optional<User> findByEmailAndIsActiveTrueAndIsDeletedFalse(String email);
}
