package com.rkey.returnkeyapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rkey.returnkeyapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Returns.class);
        Returns returns1 = new Returns();
        returns1.setId(1L);
        Returns returns2 = new Returns();
        returns2.setId(returns1.getId());
        assertThat(returns1).isEqualTo(returns2);
        returns2.setId(2L);
        assertThat(returns1).isNotEqualTo(returns2);
        returns1.setId(null);
        assertThat(returns1).isNotEqualTo(returns2);
    }
}
