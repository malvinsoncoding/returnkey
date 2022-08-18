package com.rkey.returnkeyapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.rkey.returnkeyapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnsDTO.class);
        ReturnsDTO returnsDTO1 = new ReturnsDTO();
        returnsDTO1.setId(1L);
        ReturnsDTO returnsDTO2 = new ReturnsDTO();
        assertThat(returnsDTO1).isNotEqualTo(returnsDTO2);
        returnsDTO2.setId(returnsDTO1.getId());
        assertThat(returnsDTO1).isEqualTo(returnsDTO2);
        returnsDTO2.setId(2L);
        assertThat(returnsDTO1).isNotEqualTo(returnsDTO2);
        returnsDTO1.setId(null);
        assertThat(returnsDTO1).isNotEqualTo(returnsDTO2);
    }
}
