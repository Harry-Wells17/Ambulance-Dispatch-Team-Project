package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class ResourceBreaksTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceBreaks.class);
        ResourceBreaks resourceBreaks1 = new ResourceBreaks();
        resourceBreaks1.setId(1L);
        ResourceBreaks resourceBreaks2 = new ResourceBreaks();
        resourceBreaks2.setId(resourceBreaks1.getId());
        assertThat(resourceBreaks1).isEqualTo(resourceBreaks2);
        resourceBreaks2.setId(2L);
        assertThat(resourceBreaks1).isNotEqualTo(resourceBreaks2);
        resourceBreaks1.setId(null);
        assertThat(resourceBreaks1).isNotEqualTo(resourceBreaks2);
    }
}
