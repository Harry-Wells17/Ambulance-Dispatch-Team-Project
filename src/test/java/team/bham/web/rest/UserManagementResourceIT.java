package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import team.bham.IntegrationTest;
import team.bham.domain.UserManagement;
import team.bham.domain.UserPerms;
import team.bham.domain.UserRole;
import team.bham.repository.UserManagementRepository;
import team.bham.service.criteria.UserManagementCriteria;

/**
 * Integration tests for the {@link UserManagementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserManagementResourceIT {

    private static final String ENTITY_API_URL = "/api/user-managements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserManagementMockMvc;

    private UserManagement userManagement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserManagement createEntity(EntityManager em) {
        UserManagement userManagement = new UserManagement();
        return userManagement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserManagement createUpdatedEntity(EntityManager em) {
        UserManagement userManagement = new UserManagement();
        return userManagement;
    }

    @BeforeEach
    public void initTest() {
        userManagement = createEntity(em);
    }

    @Test
    @Transactional
    void createUserManagement() throws Exception {
        int databaseSizeBeforeCreate = userManagementRepository.findAll().size();
        // Create the UserManagement
        restUserManagementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isCreated());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeCreate + 1);
        UserManagement testUserManagement = userManagementList.get(userManagementList.size() - 1);
    }

    @Test
    @Transactional
    void createUserManagementWithExistingId() throws Exception {
        // Create the UserManagement with an existing ID
        userManagement.setId(1L);

        int databaseSizeBeforeCreate = userManagementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserManagementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserManagements() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        // Get all the userManagementList
        restUserManagementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userManagement.getId().intValue())));
    }

    @Test
    @Transactional
    void getUserManagement() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        // Get the userManagement
        restUserManagementMockMvc
            .perform(get(ENTITY_API_URL_ID, userManagement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userManagement.getId().intValue()));
    }

    @Test
    @Transactional
    void getUserManagementsByIdFiltering() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        Long id = userManagement.getId();

        defaultUserManagementShouldBeFound("id.equals=" + id);
        defaultUserManagementShouldNotBeFound("id.notEquals=" + id);

        defaultUserManagementShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserManagementShouldNotBeFound("id.greaterThan=" + id);

        defaultUserManagementShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserManagementShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserManagementsByUserRoleIsEqualToSomething() throws Exception {
        UserRole userRole;
        if (TestUtil.findAll(em, UserRole.class).isEmpty()) {
            userManagementRepository.saveAndFlush(userManagement);
            userRole = UserRoleResourceIT.createEntity(em);
        } else {
            userRole = TestUtil.findAll(em, UserRole.class).get(0);
        }
        em.persist(userRole);
        em.flush();
        userManagement.setUserRole(userRole);
        userManagementRepository.saveAndFlush(userManagement);
        Long userRoleId = userRole.getId();

        // Get all the userManagementList where userRole equals to userRoleId
        defaultUserManagementShouldBeFound("userRoleId.equals=" + userRoleId);

        // Get all the userManagementList where userRole equals to (userRoleId + 1)
        defaultUserManagementShouldNotBeFound("userRoleId.equals=" + (userRoleId + 1));
    }

    @Test
    @Transactional
    void getAllUserManagementsByUserPermsIsEqualToSomething() throws Exception {
        UserPerms userPerms;
        if (TestUtil.findAll(em, UserPerms.class).isEmpty()) {
            userManagementRepository.saveAndFlush(userManagement);
            userPerms = UserPermsResourceIT.createEntity(em);
        } else {
            userPerms = TestUtil.findAll(em, UserPerms.class).get(0);
        }
        em.persist(userPerms);
        em.flush();
        userManagement.setUserPerms(userPerms);
        userManagementRepository.saveAndFlush(userManagement);
        Long userPermsId = userPerms.getId();

        // Get all the userManagementList where userPerms equals to userPermsId
        defaultUserManagementShouldBeFound("userPermsId.equals=" + userPermsId);

        // Get all the userManagementList where userPerms equals to (userPermsId + 1)
        defaultUserManagementShouldNotBeFound("userPermsId.equals=" + (userPermsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserManagementShouldBeFound(String filter) throws Exception {
        restUserManagementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userManagement.getId().intValue())));

        // Check, that the count call also returns 1
        restUserManagementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserManagementShouldNotBeFound(String filter) throws Exception {
        restUserManagementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserManagementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserManagement() throws Exception {
        // Get the userManagement
        restUserManagementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserManagement() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();

        // Update the userManagement
        UserManagement updatedUserManagement = userManagementRepository.findById(userManagement.getId()).get();
        // Disconnect from session so that the updates on updatedUserManagement are not directly saved in db
        em.detach(updatedUserManagement);

        restUserManagementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserManagement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserManagement))
            )
            .andExpect(status().isOk());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
        UserManagement testUserManagement = userManagementList.get(userManagementList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingUserManagement() throws Exception {
        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();
        userManagement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserManagementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userManagement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserManagement() throws Exception {
        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();
        userManagement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserManagementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserManagement() throws Exception {
        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();
        userManagement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserManagementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userManagement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserManagementWithPatch() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();

        // Update the userManagement using partial update
        UserManagement partialUpdatedUserManagement = new UserManagement();
        partialUpdatedUserManagement.setId(userManagement.getId());

        restUserManagementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserManagement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserManagement))
            )
            .andExpect(status().isOk());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
        UserManagement testUserManagement = userManagementList.get(userManagementList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateUserManagementWithPatch() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();

        // Update the userManagement using partial update
        UserManagement partialUpdatedUserManagement = new UserManagement();
        partialUpdatedUserManagement.setId(userManagement.getId());

        restUserManagementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserManagement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserManagement))
            )
            .andExpect(status().isOk());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
        UserManagement testUserManagement = userManagementList.get(userManagementList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingUserManagement() throws Exception {
        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();
        userManagement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserManagementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userManagement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserManagement() throws Exception {
        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();
        userManagement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserManagementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserManagement() throws Exception {
        int databaseSizeBeforeUpdate = userManagementRepository.findAll().size();
        userManagement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserManagementMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userManagement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserManagement in the database
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserManagement() throws Exception {
        // Initialize the database
        userManagementRepository.saveAndFlush(userManagement);

        int databaseSizeBeforeDelete = userManagementRepository.findAll().size();

        // Delete the userManagement
        restUserManagementMockMvc
            .perform(delete(ENTITY_API_URL_ID, userManagement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserManagement> userManagementList = userManagementRepository.findAll();
        assertThat(userManagementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
