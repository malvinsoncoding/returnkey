package com.rkey.returnkeyapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PendingMapperTest {

    private PendingMapper pendingMapper;

    @BeforeEach
    public void setUp() {
        pendingMapper = new PendingMapperImpl();
    }
}
