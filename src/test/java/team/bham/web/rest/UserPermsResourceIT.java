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
import team.bham.domain.enumeration.Perms;
import team.bham.repository.UserPermsRepository;
import team.bham.service.criteria.UserPermsCriteria;

/**
 * Integration tests for the {@link UserPermsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserPermsResourceIT {

    private static final Perms DEFAULT_PERMS = Perms.CREATE_EVENT;
    private static final Perms UPDATED_PERMS = Perms.CREATE_CALL;

    private static final String ENTITY_API_URL = "/api/user-perms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserPermsRepository userPermsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserPermsMockMvc;

    private UserPerms userPerms;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPerms createEntity(EntityManager em) {
        UserPerms userPerms = new UserPerms().perms(DEFAULT_PERMS);
        return userPerms;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPerms createUpdatedEntity(EntityManager em) {
        UserPerms userPerms = new UserPerms().perms(UPDATED_PERMS);
        return userPerms;
    }

    @BeforeEach
    public void initTest() {
        userPerms = createEntity(em);
    }

    @Test
    @Transactional
    void createUserPerms() throws Exception {
        int databaseSizeBeforeCreate = userPermsRepository.findAll().size();
        // Create the UserPerms
        restUserPermsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPerms)))
            .andExpect(status().isCreated());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeCreate + 1);
        UserPerms testUserPerms = userPermsList.get(userPermsList.size() - 1);
        assertThat(testUserPerms.getPerms()).isEqualTo(DEFAULT_PERMS);
    }

    @Test
    @Transactional
    void createUserPermsWithExistingId() throws Exception {
        // Create the UserPerms with an existing ID
        userPerms.setId(1L);

        int databaseSizeBeforeCreate = userPermsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserPermsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPerms)))
            .andExpect(status().isBadRequest());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPermsIsRequired() throws Exception {
        int databaseSizeBeforeTest = userPermsRepository.findAll().size();
        // set the field null
        userPerms.setPerms(null);

        // Create the UserPerms, which fails.

        restUserPermsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPerms)))
            .andExpect(status().isBadRequest());

        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserPerms() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        // Get all the userPermsList
        restUserPermsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userPerms.getId().intValue())))
            .andExpect(jsonPath("$.[*].perms").value(hasItem(DEFAULT_PERMS.toString())));
    }

    @Test
    @Transactional
    void getUserPerms() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        // Get the userPerms
        restUserPermsMockMvc
            .perform(get(ENTITY_API_URL_ID, userPerms.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userPerms.getId().intValue()))
            .andExpect(jsonPath("$.perms").value(DEFAULT_PERMS.toString()));
    }

    @Test
    @Transactional
    void getUserPermsByIdFiltering() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        Long id = userPerms.getId();

        defaultUserPermsShouldBeFound("id.equals=" + id);
        defaultUserPermsShouldNotBeFound("id.notEquals=" + id);

        defaultUserPermsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserPermsShouldNotBeFound("id.greaterThan=" + id);

        defaultUserPermsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserPermsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserPermsByPermsIsEqualToSomething() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        // Get all the userPermsList where perms equals to DEFAULT_PERMS
        defaultUserPermsShouldBeFound("perms.equals=" + DEFAULT_PERMS);

        // Get all the userPermsList where perms equals to UPDATED_PERMS
        defaultUserPermsShouldNotBeFound("perms.equals=" + UPDATED_PERMS);
    }

    @Test
    @Transactional
    void getAllUserPermsByPermsIsInShouldWork() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        // Get all the userPermsList where perms in DEFAULT_PERMS or UPDATED_PERMS
        defaultUserPermsShouldBeFound("perms.in=" + DEFAULT_PERMS + "," + UPDATED_PERMS);

        // Get all the userPermsList where perms equals to UPDATED_PERMS
        defaultUserPermsShouldNotBeFound("perms.in=" + UPDATED_PERMS);
    }

    @Test
    @Transactional
    void getAllUserPermsByPermsIsNullOrNotNull() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        // Get all the userPermsList where perms is not null
        defaultUserPermsShouldBeFound("perms.specified=true");

        // Get all the userPermsList where perms is null
        defaultUserPermsShouldNotBeFound("perms.specified=false");
    }

    @Test
    @Transactional
    void getAllUserPermsByUserManagementIsEqualToSomething() throws Exception {
        UserManagement userManagement;
        if (TestUtil.findAll(em, UserManagement.class).isEmpty()) {
            userPermsRepository.saveAndFlush(userPerms);
            userManagement = UserManagementResourceIT.createEntity(em);
        } else {
            userManagement = TestUtil.findAll(em, UserManagement.class).get(0);
        }
        em.persist(userManagement);
        em.flush();
        userPerms.addUserManagement(userManagement);
        userPermsRepository.saveAndFlush(userPerms);
        Long userManagementId = userManagement.getId();

        // Get all the userPermsList where userManagement equals to userManagementId
        defaultUserPermsShouldBeFound("userManagementId.equals=" + userManagementId);

        // Get all the userPermsList where userManagement equals to (userManagementId + 1)
        defaultUserPermsShouldNotBeFound("userManagementId.equals=" + (userManagementId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserPermsShouldBeFound(String filter) throws Exception {
        restUserPermsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userPerms.getId().intValue())))
            .andExpect(jsonPath("$.[*].perms").value(hasItem(DEFAULT_PERMS.toString())));

        // Check, that the count call also returns 1
        restUserPermsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserPermsShouldNotBeFound(String filter) throws Exception {
        restUserPermsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserPermsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserPerms() throws Exception {
        // Get the userPerms
        restUserPermsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserPerms() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();

        // Update the userPerms
        UserPerms updatedUserPerms = userPermsRepository.findById(userPerms.getId()).get();
        // Disconnect from session so that the updates on updatedUserPerms are not directly saved in db
        em.detach(updatedUserPerms);
        updatedUserPerms.perms(UPDATED_PERMS);

        restUserPermsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserPerms.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserPerms))
            )
            .andExpect(status().isOk());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
        UserPerms testUserPerms = userPermsList.get(userPermsList.size() - 1);
        assertThat(testUserPerms.getPerms()).isEqualTo(UPDATED_PERMS);
    }

    @Test
    @Transactional
    void putNonExistingUserPerms() throws Exception {
        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();
        userPerms.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPermsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userPerms.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPerms))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserPerms() throws Exception {
        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();
        userPerms.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPermsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPerms))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserPerms() throws Exception {
        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();
        userPerms.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPermsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPerms)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserPermsWithPatch() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();

        // Update the userPerms using partial update
        UserPerms partialUpdatedUserPerms = new UserPerms();
        partialUpdatedUserPerms.setId(userPerms.getId());

        restUserPermsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserPerms.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserPerms))
            )
            .andExpect(status().isOk());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
        UserPerms testUserPerms = userPermsList.get(userPermsList.size() - 1);
        assertThat(testUserPerms.getPerms()).isEqualTo(DEFAULT_PERMS);
    }

    @Test
    @Transactional
    void fullUpdateUserPermsWithPatch() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();

        // Update the userPerms using partial update
        UserPerms partialUpdatedUserPerms = new UserPerms();
        partialUpdatedUserPerms.setId(userPerms.getId());

        partialUpdatedUserPerms.perms(UPDATED_PERMS);

        restUserPermsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserPerms.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserPerms))
            )
            .andExpect(status().isOk());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
        UserPerms testUserPerms = userPermsList.get(userPermsList.size() - 1);
        assertThat(testUserPerms.getPerms()).isEqualTo(UPDATED_PERMS);
    }

    @Test
    @Transactional
    void patchNonExistingUserPerms() throws Exception {
        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();
        userPerms.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPermsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userPerms.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPerms))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserPerms() throws Exception {
        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();
        userPerms.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPermsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPerms))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserPerms() throws Exception {
        int databaseSizeBeforeUpdate = userPermsRepository.findAll().size();
        userPerms.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPermsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userPerms))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserPerms in the database
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserPerms() throws Exception {
        // Initialize the database
        userPermsRepository.saveAndFlush(userPerms);

        int databaseSizeBeforeDelete = userPermsRepository.findAll().size();

        // Delete the userPerms
        restUserPermsMockMvc
            .perform(delete(ENTITY_API_URL_ID, userPerms.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserPerms> userPermsList = userPermsRepository.findAll();
        assertThat(userPermsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
