package com.rkey.returnkeyapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rkey.returnkeyapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PendingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pending.class);
        Pending pending1 = new Pending();
        pending1.setId(1L);
        Pending pending2 = new Pending();
        pending2.setId(pending1.getId());
        assertThat(pending1).isEqualTo(pending2);
        pending2.setId(2L);
        assertThat(pending1).isNotEqualTo(pending2);
        pending1.setId(null);
        assertThat(pending1).isNotEqualTo(pending2);
    }
}
