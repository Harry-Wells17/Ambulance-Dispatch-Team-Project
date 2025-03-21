package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class UserExistTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserExist.class);
        UserExist userExist1 = new UserExist();
        userExist1.setId(1L);
        UserExist userExist2 = new UserExist();
        userExist2.setId(userExist1.getId());
        assertThat(userExist1).isEqualTo(userExist2);
        userExist2.setId(2L);
        assertThat(userExist1).isNotEqualTo(userExist2);
        userExist1.setId(null);
        assertThat(userExist1).isNotEqualTo(userExist2);
    }
}
