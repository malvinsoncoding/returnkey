package com.rkey.returnkeyapp.repository;

import com.rkey.returnkeyapp.domain.Pending;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Pending entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PendingRepository extends JpaRepository<Pending, Long> {
    Pending findPendingByGeneratedToken(String generatedToken);
    Optional<Pending> findPendingByTokenReference(String tokenReference);
}
