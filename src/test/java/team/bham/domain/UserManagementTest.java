package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class UserManagementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserManagement.class);
        UserManagement userManagement1 = new UserManagement();
        userManagement1.setId(1L);
        UserManagement userManagement2 = new UserManagement();
        userManagement2.setId(userManagement1.getId());
        assertThat(userManagement1).isEqualTo(userManagement2);
        userManagement2.setId(2L);
        assertThat(userManagement1).isNotEqualTo(userManagement2);
        userManagement1.setId(null);
        assertThat(userManagement1).isNotEqualTo(userManagement2);
    }
}
