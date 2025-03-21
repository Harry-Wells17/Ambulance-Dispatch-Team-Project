package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import team.bham.IntegrationTest;
import team.bham.domain.User;
import team.bham.domain.UserExist;
import team.bham.repository.UserExistRepository;
import team.bham.service.UserExistService;
import team.bham.service.criteria.UserExistCriteria;

/**
 * Integration tests for the {@link UserExistResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserExistResourceIT {

    private static final Boolean DEFAULT_EXIST = false;
    private static final Boolean UPDATED_EXIST = true;

    private static final String ENTITY_API_URL = "/api/user-exists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserExistRepository userExistRepository;

    @Mock
    private UserExistRepository userExistRepositoryMock;

    @Mock
    private UserExistService userExistServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserExistMockMvc;

    private UserExist userExist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExist createEntity(EntityManager em) {
        UserExist userExist = new UserExist().exist(DEFAULT_EXIST);
        return userExist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExist createUpdatedEntity(EntityManager em) {
        UserExist userExist = new UserExist().exist(UPDATED_EXIST);
        return userExist;
    }

    @BeforeEach
    public void initTest() {
        userExist = createEntity(em);
    }

    @Test
    @Transactional
    void createUserExist() throws Exception {
        int databaseSizeBeforeCreate = userExistRepository.findAll().size();
        // Create the UserExist
        restUserExistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userExist)))
            .andExpect(status().isCreated());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeCreate + 1);
        UserExist testUserExist = userExistList.get(userExistList.size() - 1);
        assertThat(testUserExist.getExist()).isEqualTo(DEFAULT_EXIST);
    }

    @Test
    @Transactional
    void createUserExistWithExistingId() throws Exception {
        // Create the UserExist with an existing ID
        userExist.setId(1L);

        int databaseSizeBeforeCreate = userExistRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserExistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userExist)))
            .andExpect(status().isBadRequest());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserExists() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        // Get all the userExistList
        restUserExistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExist.getId().intValue())))
            .andExpect(jsonPath("$.[*].exist").value(hasItem(DEFAULT_EXIST.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserExistsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userExistServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserExistMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userExistServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserExistsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userExistServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserExistMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userExistRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserExist() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        // Get the userExist
        restUserExistMockMvc
            .perform(get(ENTITY_API_URL_ID, userExist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userExist.getId().intValue()))
            .andExpect(jsonPath("$.exist").value(DEFAULT_EXIST.booleanValue()));
    }

    @Test
    @Transactional
    void getUserExistsByIdFiltering() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        Long id = userExist.getId();

        defaultUserExistShouldBeFound("id.equals=" + id);
        defaultUserExistShouldNotBeFound("id.notEquals=" + id);

        defaultUserExistShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserExistShouldNotBeFound("id.greaterThan=" + id);

        defaultUserExistShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserExistShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserExistsByExistIsEqualToSomething() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        // Get all the userExistList where exist equals to DEFAULT_EXIST
        defaultUserExistShouldBeFound("exist.equals=" + DEFAULT_EXIST);

        // Get all the userExistList where exist equals to UPDATED_EXIST
        defaultUserExistShouldNotBeFound("exist.equals=" + UPDATED_EXIST);
    }

    @Test
    @Transactional
    void getAllUserExistsByExistIsInShouldWork() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        // Get all the userExistList where exist in DEFAULT_EXIST or UPDATED_EXIST
        defaultUserExistShouldBeFound("exist.in=" + DEFAULT_EXIST + "," + UPDATED_EXIST);

        // Get all the userExistList where exist equals to UPDATED_EXIST
        defaultUserExistShouldNotBeFound("exist.in=" + UPDATED_EXIST);
    }

    @Test
    @Transactional
    void getAllUserExistsByExistIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        // Get all the userExistList where exist is not null
        defaultUserExistShouldBeFound("exist.specified=true");

        // Get all the userExistList where exist is null
        defaultUserExistShouldNotBeFound("exist.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExistsByCreatedByIsEqualToSomething() throws Exception {
        User createdBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userExistRepository.saveAndFlush(userExist);
            createdBy = UserResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        userExist.setCreatedBy(createdBy);
        userExistRepository.saveAndFlush(userExist);
        Long createdById = createdBy.getId();

        // Get all the userExistList where createdBy equals to createdById
        defaultUserExistShouldBeFound("createdById.equals=" + createdById);

        // Get all the userExistList where createdBy equals to (createdById + 1)
        defaultUserExistShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserExistShouldBeFound(String filter) throws Exception {
        restUserExistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExist.getId().intValue())))
            .andExpect(jsonPath("$.[*].exist").value(hasItem(DEFAULT_EXIST.booleanValue())));

        // Check, that the count call also returns 1
        restUserExistMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserExistShouldNotBeFound(String filter) throws Exception {
        restUserExistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserExistMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserExist() throws Exception {
        // Get the userExist
        restUserExistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserExist() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();

        // Update the userExist
        UserExist updatedUserExist = userExistRepository.findById(userExist.getId()).get();
        // Disconnect from session so that the updates on updatedUserExist are not directly saved in db
        em.detach(updatedUserExist);
        updatedUserExist.exist(UPDATED_EXIST);

        restUserExistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserExist.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserExist))
            )
            .andExpect(status().isOk());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
        UserExist testUserExist = userExistList.get(userExistList.size() - 1);
        assertThat(testUserExist.getExist()).isEqualTo(UPDATED_EXIST);
    }

    @Test
    @Transactional
    void putNonExistingUserExist() throws Exception {
        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();
        userExist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userExist.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExist))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserExist() throws Exception {
        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();
        userExist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExist))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserExist() throws Exception {
        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();
        userExist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExistMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userExist)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserExistWithPatch() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();

        // Update the userExist using partial update
        UserExist partialUpdatedUserExist = new UserExist();
        partialUpdatedUserExist.setId(userExist.getId());

        partialUpdatedUserExist.exist(UPDATED_EXIST);

        restUserExistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExist))
            )
            .andExpect(status().isOk());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
        UserExist testUserExist = userExistList.get(userExistList.size() - 1);
        assertThat(testUserExist.getExist()).isEqualTo(UPDATED_EXIST);
    }

    @Test
    @Transactional
    void fullUpdateUserExistWithPatch() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();

        // Update the userExist using partial update
        UserExist partialUpdatedUserExist = new UserExist();
        partialUpdatedUserExist.setId(userExist.getId());

        partialUpdatedUserExist.exist(UPDATED_EXIST);

        restUserExistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExist))
            )
            .andExpect(status().isOk());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
        UserExist testUserExist = userExistList.get(userExistList.size() - 1);
        assertThat(testUserExist.getExist()).isEqualTo(UPDATED_EXIST);
    }

    @Test
    @Transactional
    void patchNonExistingUserExist() throws Exception {
        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();
        userExist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userExist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExist))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserExist() throws Exception {
        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();
        userExist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExist))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserExist() throws Exception {
        int databaseSizeBeforeUpdate = userExistRepository.findAll().size();
        userExist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExistMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userExist))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExist in the database
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserExist() throws Exception {
        // Initialize the database
        userExistRepository.saveAndFlush(userExist);

        int databaseSizeBeforeDelete = userExistRepository.findAll().size();

        // Delete the userExist
        restUserExistMockMvc
            .perform(delete(ENTITY_API_URL_ID, userExist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserExist> userExistList = userExistRepository.findAll();
        assertThat(userExistList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
