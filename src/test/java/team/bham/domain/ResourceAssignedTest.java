package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class ResourceAssignedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceAssigned.class);
        ResourceAssigned resourceAssigned1 = new ResourceAssigned();
        resourceAssigned1.setId(1L);
        ResourceAssigned resourceAssigned2 = new ResourceAssigned();
        resourceAssigned2.setId(resourceAssigned1.getId());
        assertThat(resourceAssigned1).isEqualTo(resourceAssigned2);
        resourceAssigned2.setId(2L);
        assertThat(resourceAssigned1).isNotEqualTo(resourceAssigned2);
        resourceAssigned1.setId(null);
        assertThat(resourceAssigned1).isNotEqualTo(resourceAssigned2);
    }
}
