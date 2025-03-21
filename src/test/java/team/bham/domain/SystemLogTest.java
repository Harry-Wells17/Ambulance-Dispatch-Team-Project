package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class SystemLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemLog.class);
        SystemLog systemLog1 = new SystemLog();
        systemLog1.setId(1L);
        SystemLog systemLog2 = new SystemLog();
        systemLog2.setId(systemLog1.getId());
        assertThat(systemLog1).isEqualTo(systemLog2);
        systemLog2.setId(2L);
        assertThat(systemLog1).isNotEqualTo(systemLog2);
        systemLog1.setId(null);
        assertThat(systemLog1).isNotEqualTo(systemLog2);
    }
}
