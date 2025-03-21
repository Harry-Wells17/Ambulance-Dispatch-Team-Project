package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class GeoLocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GeoLocation.class);
        GeoLocation geoLocation1 = new GeoLocation();
        geoLocation1.setId(1L);
        GeoLocation geoLocation2 = new GeoLocation();
        geoLocation2.setId(geoLocation1.getId());
        assertThat(geoLocation1).isEqualTo(geoLocation2);
        geoLocation2.setId(2L);
        assertThat(geoLocation1).isNotEqualTo(geoLocation2);
        geoLocation1.setId(null);
        assertThat(geoLocation1).isNotEqualTo(geoLocation2);
    }
}
