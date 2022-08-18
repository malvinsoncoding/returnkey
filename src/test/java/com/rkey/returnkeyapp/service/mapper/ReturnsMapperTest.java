package com.rkey.returnkeyapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnsMapperTest {

    private ReturnsMapper returnsMapper;

    @BeforeEach
    public void setUp() {
        returnsMapper = new ReturnsMapperImpl();
    }
}
