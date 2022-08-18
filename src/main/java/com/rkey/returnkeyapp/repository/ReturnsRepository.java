package com.rkey.returnkeyapp.repository;

import com.rkey.returnkeyapp.domain.Returns;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Returns entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReturnsRepository extends JpaRepository<Returns, Long> {}
