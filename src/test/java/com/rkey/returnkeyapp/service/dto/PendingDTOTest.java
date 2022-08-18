package com.rkey.returnkeyapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.rkey.returnkeyapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PendingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PendingDTO.class);
        PendingDTO pendingDTO1 = new PendingDTO();
        pendingDTO1.setId(1L);
        PendingDTO pendingDTO2 = new PendingDTO();
        assertThat(pendingDTO1).isNotEqualTo(pendingDTO2);
        pendingDTO2.setId(pendingDTO1.getId());
        assertThat(pendingDTO1).isEqualTo(pendingDTO2);
        pendingDTO2.setId(2L);
        assertThat(pendingDTO1).isNotEqualTo(pendingDTO2);
        pendingDTO1.setId(null);
        assertThat(pendingDTO1).isNotEqualTo(pendingDTO2);
    }
}
