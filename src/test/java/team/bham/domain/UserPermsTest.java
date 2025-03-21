package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class UserPermsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPerms.class);
        UserPerms userPerms1 = new UserPerms();
        userPerms1.setId(1L);
        UserPerms userPerms2 = new UserPerms();
        userPerms2.setId(userPerms1.getId());
        assertThat(userPerms1).isEqualTo(userPerms2);
        userPerms2.setId(2L);
        assertThat(userPerms1).isNotEqualTo(userPerms2);
        userPerms1.setId(null);
        assertThat(userPerms1).isNotEqualTo(userPerms2);
    }
}
