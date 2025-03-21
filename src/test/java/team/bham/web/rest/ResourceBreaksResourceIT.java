package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team.bham.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import team.bham.domain.ResourceBreaks;
import team.bham.repository.ResourceBreaksRepository;
import team.bham.service.criteria.ResourceBreaksCriteria;

/**
 * Integration tests for the {@link ResourceBreaksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResourceBreaksResourceIT {

    private static final ZonedDateTime DEFAULT_LAST_BREAK = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_BREAK = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_BREAK = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_BREAK_REQUESTED = false;
    private static final Boolean UPDATED_BREAK_REQUESTED = true;

    private static final ZonedDateTime DEFAULT_STARTED_BREAK = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_STARTED_BREAK = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_STARTED_BREAK = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_ON_BREAK = false;
    private static final Boolean UPDATED_ON_BREAK = true;

    private static final String ENTITY_API_URL = "/api/resource-breaks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceBreaksRepository resourceBreaksRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResourceBreaksMockMvc;

    private ResourceBreaks resourceBreaks;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceBreaks createEntity(EntityManager em) {
        ResourceBreaks resourceBreaks = new ResourceBreaks()
            .lastBreak(DEFAULT_LAST_BREAK)
            .breakRequested(DEFAULT_BREAK_REQUESTED)
            .startedBreak(DEFAULT_STARTED_BREAK)
            .onBreak(DEFAULT_ON_BREAK);
        return resourceBreaks;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceBreaks createUpdatedEntity(EntityManager em) {
        ResourceBreaks resourceBreaks = new ResourceBreaks()
            .lastBreak(UPDATED_LAST_BREAK)
            .breakRequested(UPDATED_BREAK_REQUESTED)
            .startedBreak(UPDATED_STARTED_BREAK)
            .onBreak(UPDATED_ON_BREAK);
        return resourceBreaks;
    }

    @BeforeEach
    public void initTest() {
        resourceBreaks = createEntity(em);
    }

    @Test
    @Transactional
    void createResourceBreaks() throws Exception {
        int databaseSizeBeforeCreate = resourceBreaksRepository.findAll().size();
        // Create the ResourceBreaks
        restResourceBreaksMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isCreated());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeCreate + 1);
        ResourceBreaks testResourceBreaks = resourceBreaksList.get(resourceBreaksList.size() - 1);
        assertThat(testResourceBreaks.getLastBreak()).isEqualTo(DEFAULT_LAST_BREAK);
        assertThat(testResourceBreaks.getBreakRequested()).isEqualTo(DEFAULT_BREAK_REQUESTED);
        assertThat(testResourceBreaks.getStartedBreak()).isEqualTo(DEFAULT_STARTED_BREAK);
        assertThat(testResourceBreaks.getOnBreak()).isEqualTo(DEFAULT_ON_BREAK);
    }

    @Test
    @Transactional
    void createResourceBreaksWithExistingId() throws Exception {
        // Create the ResourceBreaks with an existing ID
        resourceBreaks.setId(1L);

        int databaseSizeBeforeCreate = resourceBreaksRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceBreaksMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLastBreakIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceBreaksRepository.findAll().size();
        // set the field null
        resourceBreaks.setLastBreak(null);

        // Create the ResourceBreaks, which fails.

        restResourceBreaksMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBreakRequestedIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceBreaksRepository.findAll().size();
        // set the field null
        resourceBreaks.setBreakRequested(null);

        // Create the ResourceBreaks, which fails.

        restResourceBreaksMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResourceBreaks() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList
        restResourceBreaksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceBreaks.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastBreak").value(hasItem(sameInstant(DEFAULT_LAST_BREAK))))
            .andExpect(jsonPath("$.[*].breakRequested").value(hasItem(DEFAULT_BREAK_REQUESTED.booleanValue())))
            .andExpect(jsonPath("$.[*].startedBreak").value(hasItem(sameInstant(DEFAULT_STARTED_BREAK))))
            .andExpect(jsonPath("$.[*].onBreak").value(hasItem(DEFAULT_ON_BREAK.booleanValue())));
    }

    @Test
    @Transactional
    void getResourceBreaks() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get the resourceBreaks
        restResourceBreaksMockMvc
            .perform(get(ENTITY_API_URL_ID, resourceBreaks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resourceBreaks.getId().intValue()))
            .andExpect(jsonPath("$.lastBreak").value(sameInstant(DEFAULT_LAST_BREAK)))
            .andExpect(jsonPath("$.breakRequested").value(DEFAULT_BREAK_REQUESTED.booleanValue()))
            .andExpect(jsonPath("$.startedBreak").value(sameInstant(DEFAULT_STARTED_BREAK)))
            .andExpect(jsonPath("$.onBreak").value(DEFAULT_ON_BREAK.booleanValue()));
    }

    @Test
    @Transactional
    void getResourceBreaksByIdFiltering() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        Long id = resourceBreaks.getId();

        defaultResourceBreaksShouldBeFound("id.equals=" + id);
        defaultResourceBreaksShouldNotBeFound("id.notEquals=" + id);

        defaultResourceBreaksShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultResourceBreaksShouldNotBeFound("id.greaterThan=" + id);

        defaultResourceBreaksShouldBeFound("id.lessThanOrEqual=" + id);
        defaultResourceBreaksShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak equals to DEFAULT_LAST_BREAK
        defaultResourceBreaksShouldBeFound("lastBreak.equals=" + DEFAULT_LAST_BREAK);

        // Get all the resourceBreaksList where lastBreak equals to UPDATED_LAST_BREAK
        defaultResourceBreaksShouldNotBeFound("lastBreak.equals=" + UPDATED_LAST_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsInShouldWork() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak in DEFAULT_LAST_BREAK or UPDATED_LAST_BREAK
        defaultResourceBreaksShouldBeFound("lastBreak.in=" + DEFAULT_LAST_BREAK + "," + UPDATED_LAST_BREAK);

        // Get all the resourceBreaksList where lastBreak equals to UPDATED_LAST_BREAK
        defaultResourceBreaksShouldNotBeFound("lastBreak.in=" + UPDATED_LAST_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak is not null
        defaultResourceBreaksShouldBeFound("lastBreak.specified=true");

        // Get all the resourceBreaksList where lastBreak is null
        defaultResourceBreaksShouldNotBeFound("lastBreak.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak is greater than or equal to DEFAULT_LAST_BREAK
        defaultResourceBreaksShouldBeFound("lastBreak.greaterThanOrEqual=" + DEFAULT_LAST_BREAK);

        // Get all the resourceBreaksList where lastBreak is greater than or equal to UPDATED_LAST_BREAK
        defaultResourceBreaksShouldNotBeFound("lastBreak.greaterThanOrEqual=" + UPDATED_LAST_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak is less than or equal to DEFAULT_LAST_BREAK
        defaultResourceBreaksShouldBeFound("lastBreak.lessThanOrEqual=" + DEFAULT_LAST_BREAK);

        // Get all the resourceBreaksList where lastBreak is less than or equal to SMALLER_LAST_BREAK
        defaultResourceBreaksShouldNotBeFound("lastBreak.lessThanOrEqual=" + SMALLER_LAST_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak is less than DEFAULT_LAST_BREAK
        defaultResourceBreaksShouldNotBeFound("lastBreak.lessThan=" + DEFAULT_LAST_BREAK);

        // Get all the resourceBreaksList where lastBreak is less than UPDATED_LAST_BREAK
        defaultResourceBreaksShouldBeFound("lastBreak.lessThan=" + UPDATED_LAST_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByLastBreakIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where lastBreak is greater than DEFAULT_LAST_BREAK
        defaultResourceBreaksShouldNotBeFound("lastBreak.greaterThan=" + DEFAULT_LAST_BREAK);

        // Get all the resourceBreaksList where lastBreak is greater than SMALLER_LAST_BREAK
        defaultResourceBreaksShouldBeFound("lastBreak.greaterThan=" + SMALLER_LAST_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByBreakRequestedIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where breakRequested equals to DEFAULT_BREAK_REQUESTED
        defaultResourceBreaksShouldBeFound("breakRequested.equals=" + DEFAULT_BREAK_REQUESTED);

        // Get all the resourceBreaksList where breakRequested equals to UPDATED_BREAK_REQUESTED
        defaultResourceBreaksShouldNotBeFound("breakRequested.equals=" + UPDATED_BREAK_REQUESTED);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByBreakRequestedIsInShouldWork() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where breakRequested in DEFAULT_BREAK_REQUESTED or UPDATED_BREAK_REQUESTED
        defaultResourceBreaksShouldBeFound("breakRequested.in=" + DEFAULT_BREAK_REQUESTED + "," + UPDATED_BREAK_REQUESTED);

        // Get all the resourceBreaksList where breakRequested equals to UPDATED_BREAK_REQUESTED
        defaultResourceBreaksShouldNotBeFound("breakRequested.in=" + UPDATED_BREAK_REQUESTED);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByBreakRequestedIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where breakRequested is not null
        defaultResourceBreaksShouldBeFound("breakRequested.specified=true");

        // Get all the resourceBreaksList where breakRequested is null
        defaultResourceBreaksShouldNotBeFound("breakRequested.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak equals to DEFAULT_STARTED_BREAK
        defaultResourceBreaksShouldBeFound("startedBreak.equals=" + DEFAULT_STARTED_BREAK);

        // Get all the resourceBreaksList where startedBreak equals to UPDATED_STARTED_BREAK
        defaultResourceBreaksShouldNotBeFound("startedBreak.equals=" + UPDATED_STARTED_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsInShouldWork() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak in DEFAULT_STARTED_BREAK or UPDATED_STARTED_BREAK
        defaultResourceBreaksShouldBeFound("startedBreak.in=" + DEFAULT_STARTED_BREAK + "," + UPDATED_STARTED_BREAK);

        // Get all the resourceBreaksList where startedBreak equals to UPDATED_STARTED_BREAK
        defaultResourceBreaksShouldNotBeFound("startedBreak.in=" + UPDATED_STARTED_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak is not null
        defaultResourceBreaksShouldBeFound("startedBreak.specified=true");

        // Get all the resourceBreaksList where startedBreak is null
        defaultResourceBreaksShouldNotBeFound("startedBreak.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak is greater than or equal to DEFAULT_STARTED_BREAK
        defaultResourceBreaksShouldBeFound("startedBreak.greaterThanOrEqual=" + DEFAULT_STARTED_BREAK);

        // Get all the resourceBreaksList where startedBreak is greater than or equal to UPDATED_STARTED_BREAK
        defaultResourceBreaksShouldNotBeFound("startedBreak.greaterThanOrEqual=" + UPDATED_STARTED_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak is less than or equal to DEFAULT_STARTED_BREAK
        defaultResourceBreaksShouldBeFound("startedBreak.lessThanOrEqual=" + DEFAULT_STARTED_BREAK);

        // Get all the resourceBreaksList where startedBreak is less than or equal to SMALLER_STARTED_BREAK
        defaultResourceBreaksShouldNotBeFound("startedBreak.lessThanOrEqual=" + SMALLER_STARTED_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak is less than DEFAULT_STARTED_BREAK
        defaultResourceBreaksShouldNotBeFound("startedBreak.lessThan=" + DEFAULT_STARTED_BREAK);

        // Get all the resourceBreaksList where startedBreak is less than UPDATED_STARTED_BREAK
        defaultResourceBreaksShouldBeFound("startedBreak.lessThan=" + UPDATED_STARTED_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByStartedBreakIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where startedBreak is greater than DEFAULT_STARTED_BREAK
        defaultResourceBreaksShouldNotBeFound("startedBreak.greaterThan=" + DEFAULT_STARTED_BREAK);

        // Get all the resourceBreaksList where startedBreak is greater than SMALLER_STARTED_BREAK
        defaultResourceBreaksShouldBeFound("startedBreak.greaterThan=" + SMALLER_STARTED_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByOnBreakIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where onBreak equals to DEFAULT_ON_BREAK
        defaultResourceBreaksShouldBeFound("onBreak.equals=" + DEFAULT_ON_BREAK);

        // Get all the resourceBreaksList where onBreak equals to UPDATED_ON_BREAK
        defaultResourceBreaksShouldNotBeFound("onBreak.equals=" + UPDATED_ON_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByOnBreakIsInShouldWork() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where onBreak in DEFAULT_ON_BREAK or UPDATED_ON_BREAK
        defaultResourceBreaksShouldBeFound("onBreak.in=" + DEFAULT_ON_BREAK + "," + UPDATED_ON_BREAK);

        // Get all the resourceBreaksList where onBreak equals to UPDATED_ON_BREAK
        defaultResourceBreaksShouldNotBeFound("onBreak.in=" + UPDATED_ON_BREAK);
    }

    @Test
    @Transactional
    void getAllResourceBreaksByOnBreakIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        // Get all the resourceBreaksList where onBreak is not null
        defaultResourceBreaksShouldBeFound("onBreak.specified=true");

        // Get all the resourceBreaksList where onBreak is null
        defaultResourceBreaksShouldNotBeFound("onBreak.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultResourceBreaksShouldBeFound(String filter) throws Exception {
        restResourceBreaksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceBreaks.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastBreak").value(hasItem(sameInstant(DEFAULT_LAST_BREAK))))
            .andExpect(jsonPath("$.[*].breakRequested").value(hasItem(DEFAULT_BREAK_REQUESTED.booleanValue())))
            .andExpect(jsonPath("$.[*].startedBreak").value(hasItem(sameInstant(DEFAULT_STARTED_BREAK))))
            .andExpect(jsonPath("$.[*].onBreak").value(hasItem(DEFAULT_ON_BREAK.booleanValue())));

        // Check, that the count call also returns 1
        restResourceBreaksMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultResourceBreaksShouldNotBeFound(String filter) throws Exception {
        restResourceBreaksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restResourceBreaksMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingResourceBreaks() throws Exception {
        // Get the resourceBreaks
        restResourceBreaksMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResourceBreaks() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();

        // Update the resourceBreaks
        ResourceBreaks updatedResourceBreaks = resourceBreaksRepository.findById(resourceBreaks.getId()).get();
        // Disconnect from session so that the updates on updatedResourceBreaks are not directly saved in db
        em.detach(updatedResourceBreaks);
        updatedResourceBreaks
            .lastBreak(UPDATED_LAST_BREAK)
            .breakRequested(UPDATED_BREAK_REQUESTED)
            .startedBreak(UPDATED_STARTED_BREAK)
            .onBreak(UPDATED_ON_BREAK);

        restResourceBreaksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedResourceBreaks.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedResourceBreaks))
            )
            .andExpect(status().isOk());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
        ResourceBreaks testResourceBreaks = resourceBreaksList.get(resourceBreaksList.size() - 1);
        assertThat(testResourceBreaks.getLastBreak()).isEqualTo(UPDATED_LAST_BREAK);
        assertThat(testResourceBreaks.getBreakRequested()).isEqualTo(UPDATED_BREAK_REQUESTED);
        assertThat(testResourceBreaks.getStartedBreak()).isEqualTo(UPDATED_STARTED_BREAK);
        assertThat(testResourceBreaks.getOnBreak()).isEqualTo(UPDATED_ON_BREAK);
    }

    @Test
    @Transactional
    void putNonExistingResourceBreaks() throws Exception {
        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();
        resourceBreaks.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceBreaksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceBreaks.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResourceBreaks() throws Exception {
        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();
        resourceBreaks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceBreaksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResourceBreaks() throws Exception {
        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();
        resourceBreaks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceBreaksMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceBreaks)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResourceBreaksWithPatch() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();

        // Update the resourceBreaks using partial update
        ResourceBreaks partialUpdatedResourceBreaks = new ResourceBreaks();
        partialUpdatedResourceBreaks.setId(resourceBreaks.getId());

        partialUpdatedResourceBreaks.lastBreak(UPDATED_LAST_BREAK).startedBreak(UPDATED_STARTED_BREAK);

        restResourceBreaksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceBreaks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceBreaks))
            )
            .andExpect(status().isOk());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
        ResourceBreaks testResourceBreaks = resourceBreaksList.get(resourceBreaksList.size() - 1);
        assertThat(testResourceBreaks.getLastBreak()).isEqualTo(UPDATED_LAST_BREAK);
        assertThat(testResourceBreaks.getBreakRequested()).isEqualTo(DEFAULT_BREAK_REQUESTED);
        assertThat(testResourceBreaks.getStartedBreak()).isEqualTo(UPDATED_STARTED_BREAK);
        assertThat(testResourceBreaks.getOnBreak()).isEqualTo(DEFAULT_ON_BREAK);
    }

    @Test
    @Transactional
    void fullUpdateResourceBreaksWithPatch() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();

        // Update the resourceBreaks using partial update
        ResourceBreaks partialUpdatedResourceBreaks = new ResourceBreaks();
        partialUpdatedResourceBreaks.setId(resourceBreaks.getId());

        partialUpdatedResourceBreaks
            .lastBreak(UPDATED_LAST_BREAK)
            .breakRequested(UPDATED_BREAK_REQUESTED)
            .startedBreak(UPDATED_STARTED_BREAK)
            .onBreak(UPDATED_ON_BREAK);

        restResourceBreaksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceBreaks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceBreaks))
            )
            .andExpect(status().isOk());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
        ResourceBreaks testResourceBreaks = resourceBreaksList.get(resourceBreaksList.size() - 1);
        assertThat(testResourceBreaks.getLastBreak()).isEqualTo(UPDATED_LAST_BREAK);
        assertThat(testResourceBreaks.getBreakRequested()).isEqualTo(UPDATED_BREAK_REQUESTED);
        assertThat(testResourceBreaks.getStartedBreak()).isEqualTo(UPDATED_STARTED_BREAK);
        assertThat(testResourceBreaks.getOnBreak()).isEqualTo(UPDATED_ON_BREAK);
    }

    @Test
    @Transactional
    void patchNonExistingResourceBreaks() throws Exception {
        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();
        resourceBreaks.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceBreaksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resourceBreaks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResourceBreaks() throws Exception {
        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();
        resourceBreaks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceBreaksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResourceBreaks() throws Exception {
        int databaseSizeBeforeUpdate = resourceBreaksRepository.findAll().size();
        resourceBreaks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceBreaksMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(resourceBreaks))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceBreaks in the database
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResourceBreaks() throws Exception {
        // Initialize the database
        resourceBreaksRepository.saveAndFlush(resourceBreaks);

        int databaseSizeBeforeDelete = resourceBreaksRepository.findAll().size();

        // Delete the resourceBreaks
        restResourceBreaksMockMvc
            .perform(delete(ENTITY_API_URL_ID, resourceBreaks.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ResourceBreaks> resourceBreaksList = resourceBreaksRepository.findAll();
        assertThat(resourceBreaksList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
