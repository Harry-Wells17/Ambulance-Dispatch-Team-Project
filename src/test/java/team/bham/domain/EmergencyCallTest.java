package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class EmergencyCallTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmergencyCall.class);
        EmergencyCall emergencyCall1 = new EmergencyCall();
        emergencyCall1.setId(1L);
        EmergencyCall emergencyCall2 = new EmergencyCall();
        emergencyCall2.setId(emergencyCall1.getId());
        assertThat(emergencyCall1).isEqualTo(emergencyCall2);
        emergencyCall2.setId(2L);
        assertThat(emergencyCall1).isNotEqualTo(emergencyCall2);
        emergencyCall1.setId(null);
        assertThat(emergencyCall1).isNotEqualTo(emergencyCall2);
    }
}
