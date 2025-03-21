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
import team.bham.domain.EmergencyCall;
import team.bham.domain.Resource;
import team.bham.domain.ResourceAssigned;
import team.bham.repository.ResourceAssignedRepository;
import team.bham.service.criteria.ResourceAssignedCriteria;

/**
 * Integration tests for the {@link ResourceAssignedResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResourceAssignedResourceIT {

    private static final ZonedDateTime DEFAULT_CALL_RECIEVED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CALL_RECIEVED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CALL_RECIEVED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_ON_SCENE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ON_SCENE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ON_SCENE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LEFT_SCENE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LEFT_SCENE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LEFT_SCENE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_ARRIVED_HOSPITAL_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ARRIVED_HOSPITAL_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ARRIVED_HOSPITAL_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_CLEAR_HOSPITAL_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CLEAR_HOSPITAL_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CLEAR_HOSPITAL_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_GREEN_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_GREEN_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_GREEN_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UN_ASSIGNED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UN_ASSIGNED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UN_ASSIGNED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/resource-assigneds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceAssignedRepository resourceAssignedRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResourceAssignedMockMvc;

    private ResourceAssigned resourceAssigned;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceAssigned createEntity(EntityManager em) {
        ResourceAssigned resourceAssigned = new ResourceAssigned()
            .callRecievedTime(DEFAULT_CALL_RECIEVED_TIME)
            .onSceneTime(DEFAULT_ON_SCENE_TIME)
            .leftSceneTime(DEFAULT_LEFT_SCENE_TIME)
            .arrivedHospitalTime(DEFAULT_ARRIVED_HOSPITAL_TIME)
            .clearHospitalTime(DEFAULT_CLEAR_HOSPITAL_TIME)
            .greenTime(DEFAULT_GREEN_TIME)
            .unAssignedTime(DEFAULT_UN_ASSIGNED_TIME);
        return resourceAssigned;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceAssigned createUpdatedEntity(EntityManager em) {
        ResourceAssigned resourceAssigned = new ResourceAssigned()
            .callRecievedTime(UPDATED_CALL_RECIEVED_TIME)
            .onSceneTime(UPDATED_ON_SCENE_TIME)
            .leftSceneTime(UPDATED_LEFT_SCENE_TIME)
            .arrivedHospitalTime(UPDATED_ARRIVED_HOSPITAL_TIME)
            .clearHospitalTime(UPDATED_CLEAR_HOSPITAL_TIME)
            .greenTime(UPDATED_GREEN_TIME)
            .unAssignedTime(UPDATED_UN_ASSIGNED_TIME);
        return resourceAssigned;
    }

    @BeforeEach
    public void initTest() {
        resourceAssigned = createEntity(em);
    }

    @Test
    @Transactional
    void createResourceAssigned() throws Exception {
        int databaseSizeBeforeCreate = resourceAssignedRepository.findAll().size();
        // Create the ResourceAssigned
        restResourceAssignedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isCreated());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeCreate + 1);
        ResourceAssigned testResourceAssigned = resourceAssignedList.get(resourceAssignedList.size() - 1);
        assertThat(testResourceAssigned.getCallRecievedTime()).isEqualTo(DEFAULT_CALL_RECIEVED_TIME);
        assertThat(testResourceAssigned.getOnSceneTime()).isEqualTo(DEFAULT_ON_SCENE_TIME);
        assertThat(testResourceAssigned.getLeftSceneTime()).isEqualTo(DEFAULT_LEFT_SCENE_TIME);
        assertThat(testResourceAssigned.getArrivedHospitalTime()).isEqualTo(DEFAULT_ARRIVED_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getClearHospitalTime()).isEqualTo(DEFAULT_CLEAR_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getGreenTime()).isEqualTo(DEFAULT_GREEN_TIME);
        assertThat(testResourceAssigned.getUnAssignedTime()).isEqualTo(DEFAULT_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void createResourceAssignedWithExistingId() throws Exception {
        // Create the ResourceAssigned with an existing ID
        resourceAssigned.setId(1L);

        int databaseSizeBeforeCreate = resourceAssignedRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceAssignedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCallRecievedTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceAssignedRepository.findAll().size();
        // set the field null
        resourceAssigned.setCallRecievedTime(null);

        // Create the ResourceAssigned, which fails.

        restResourceAssignedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isBadRequest());

        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResourceAssigneds() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList
        restResourceAssignedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceAssigned.getId().intValue())))
            .andExpect(jsonPath("$.[*].callRecievedTime").value(hasItem(sameInstant(DEFAULT_CALL_RECIEVED_TIME))))
            .andExpect(jsonPath("$.[*].onSceneTime").value(hasItem(sameInstant(DEFAULT_ON_SCENE_TIME))))
            .andExpect(jsonPath("$.[*].leftSceneTime").value(hasItem(sameInstant(DEFAULT_LEFT_SCENE_TIME))))
            .andExpect(jsonPath("$.[*].arrivedHospitalTime").value(hasItem(sameInstant(DEFAULT_ARRIVED_HOSPITAL_TIME))))
            .andExpect(jsonPath("$.[*].clearHospitalTime").value(hasItem(sameInstant(DEFAULT_CLEAR_HOSPITAL_TIME))))
            .andExpect(jsonPath("$.[*].greenTime").value(hasItem(sameInstant(DEFAULT_GREEN_TIME))))
            .andExpect(jsonPath("$.[*].unAssignedTime").value(hasItem(sameInstant(DEFAULT_UN_ASSIGNED_TIME))));
    }

    @Test
    @Transactional
    void getResourceAssigned() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get the resourceAssigned
        restResourceAssignedMockMvc
            .perform(get(ENTITY_API_URL_ID, resourceAssigned.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resourceAssigned.getId().intValue()))
            .andExpect(jsonPath("$.callRecievedTime").value(sameInstant(DEFAULT_CALL_RECIEVED_TIME)))
            .andExpect(jsonPath("$.onSceneTime").value(sameInstant(DEFAULT_ON_SCENE_TIME)))
            .andExpect(jsonPath("$.leftSceneTime").value(sameInstant(DEFAULT_LEFT_SCENE_TIME)))
            .andExpect(jsonPath("$.arrivedHospitalTime").value(sameInstant(DEFAULT_ARRIVED_HOSPITAL_TIME)))
            .andExpect(jsonPath("$.clearHospitalTime").value(sameInstant(DEFAULT_CLEAR_HOSPITAL_TIME)))
            .andExpect(jsonPath("$.greenTime").value(sameInstant(DEFAULT_GREEN_TIME)))
            .andExpect(jsonPath("$.unAssignedTime").value(sameInstant(DEFAULT_UN_ASSIGNED_TIME)));
    }

    @Test
    @Transactional
    void getResourceAssignedsByIdFiltering() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        Long id = resourceAssigned.getId();

        defaultResourceAssignedShouldBeFound("id.equals=" + id);
        defaultResourceAssignedShouldNotBeFound("id.notEquals=" + id);

        defaultResourceAssignedShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultResourceAssignedShouldNotBeFound("id.greaterThan=" + id);

        defaultResourceAssignedShouldBeFound("id.lessThanOrEqual=" + id);
        defaultResourceAssignedShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime equals to DEFAULT_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldBeFound("callRecievedTime.equals=" + DEFAULT_CALL_RECIEVED_TIME);

        // Get all the resourceAssignedList where callRecievedTime equals to UPDATED_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.equals=" + UPDATED_CALL_RECIEVED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime in DEFAULT_CALL_RECIEVED_TIME or UPDATED_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldBeFound("callRecievedTime.in=" + DEFAULT_CALL_RECIEVED_TIME + "," + UPDATED_CALL_RECIEVED_TIME);

        // Get all the resourceAssignedList where callRecievedTime equals to UPDATED_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.in=" + UPDATED_CALL_RECIEVED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime is not null
        defaultResourceAssignedShouldBeFound("callRecievedTime.specified=true");

        // Get all the resourceAssignedList where callRecievedTime is null
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime is greater than or equal to DEFAULT_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldBeFound("callRecievedTime.greaterThanOrEqual=" + DEFAULT_CALL_RECIEVED_TIME);

        // Get all the resourceAssignedList where callRecievedTime is greater than or equal to UPDATED_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.greaterThanOrEqual=" + UPDATED_CALL_RECIEVED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime is less than or equal to DEFAULT_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldBeFound("callRecievedTime.lessThanOrEqual=" + DEFAULT_CALL_RECIEVED_TIME);

        // Get all the resourceAssignedList where callRecievedTime is less than or equal to SMALLER_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.lessThanOrEqual=" + SMALLER_CALL_RECIEVED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime is less than DEFAULT_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.lessThan=" + DEFAULT_CALL_RECIEVED_TIME);

        // Get all the resourceAssignedList where callRecievedTime is less than UPDATED_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldBeFound("callRecievedTime.lessThan=" + UPDATED_CALL_RECIEVED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByCallRecievedTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where callRecievedTime is greater than DEFAULT_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldNotBeFound("callRecievedTime.greaterThan=" + DEFAULT_CALL_RECIEVED_TIME);

        // Get all the resourceAssignedList where callRecievedTime is greater than SMALLER_CALL_RECIEVED_TIME
        defaultResourceAssignedShouldBeFound("callRecievedTime.greaterThan=" + SMALLER_CALL_RECIEVED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime equals to DEFAULT_ON_SCENE_TIME
        defaultResourceAssignedShouldBeFound("onSceneTime.equals=" + DEFAULT_ON_SCENE_TIME);

        // Get all the resourceAssignedList where onSceneTime equals to UPDATED_ON_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("onSceneTime.equals=" + UPDATED_ON_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime in DEFAULT_ON_SCENE_TIME or UPDATED_ON_SCENE_TIME
        defaultResourceAssignedShouldBeFound("onSceneTime.in=" + DEFAULT_ON_SCENE_TIME + "," + UPDATED_ON_SCENE_TIME);

        // Get all the resourceAssignedList where onSceneTime equals to UPDATED_ON_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("onSceneTime.in=" + UPDATED_ON_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime is not null
        defaultResourceAssignedShouldBeFound("onSceneTime.specified=true");

        // Get all the resourceAssignedList where onSceneTime is null
        defaultResourceAssignedShouldNotBeFound("onSceneTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime is greater than or equal to DEFAULT_ON_SCENE_TIME
        defaultResourceAssignedShouldBeFound("onSceneTime.greaterThanOrEqual=" + DEFAULT_ON_SCENE_TIME);

        // Get all the resourceAssignedList where onSceneTime is greater than or equal to UPDATED_ON_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("onSceneTime.greaterThanOrEqual=" + UPDATED_ON_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime is less than or equal to DEFAULT_ON_SCENE_TIME
        defaultResourceAssignedShouldBeFound("onSceneTime.lessThanOrEqual=" + DEFAULT_ON_SCENE_TIME);

        // Get all the resourceAssignedList where onSceneTime is less than or equal to SMALLER_ON_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("onSceneTime.lessThanOrEqual=" + SMALLER_ON_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime is less than DEFAULT_ON_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("onSceneTime.lessThan=" + DEFAULT_ON_SCENE_TIME);

        // Get all the resourceAssignedList where onSceneTime is less than UPDATED_ON_SCENE_TIME
        defaultResourceAssignedShouldBeFound("onSceneTime.lessThan=" + UPDATED_ON_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByOnSceneTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where onSceneTime is greater than DEFAULT_ON_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("onSceneTime.greaterThan=" + DEFAULT_ON_SCENE_TIME);

        // Get all the resourceAssignedList where onSceneTime is greater than SMALLER_ON_SCENE_TIME
        defaultResourceAssignedShouldBeFound("onSceneTime.greaterThan=" + SMALLER_ON_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime equals to DEFAULT_LEFT_SCENE_TIME
        defaultResourceAssignedShouldBeFound("leftSceneTime.equals=" + DEFAULT_LEFT_SCENE_TIME);

        // Get all the resourceAssignedList where leftSceneTime equals to UPDATED_LEFT_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.equals=" + UPDATED_LEFT_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime in DEFAULT_LEFT_SCENE_TIME or UPDATED_LEFT_SCENE_TIME
        defaultResourceAssignedShouldBeFound("leftSceneTime.in=" + DEFAULT_LEFT_SCENE_TIME + "," + UPDATED_LEFT_SCENE_TIME);

        // Get all the resourceAssignedList where leftSceneTime equals to UPDATED_LEFT_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.in=" + UPDATED_LEFT_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime is not null
        defaultResourceAssignedShouldBeFound("leftSceneTime.specified=true");

        // Get all the resourceAssignedList where leftSceneTime is null
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime is greater than or equal to DEFAULT_LEFT_SCENE_TIME
        defaultResourceAssignedShouldBeFound("leftSceneTime.greaterThanOrEqual=" + DEFAULT_LEFT_SCENE_TIME);

        // Get all the resourceAssignedList where leftSceneTime is greater than or equal to UPDATED_LEFT_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.greaterThanOrEqual=" + UPDATED_LEFT_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime is less than or equal to DEFAULT_LEFT_SCENE_TIME
        defaultResourceAssignedShouldBeFound("leftSceneTime.lessThanOrEqual=" + DEFAULT_LEFT_SCENE_TIME);

        // Get all the resourceAssignedList where leftSceneTime is less than or equal to SMALLER_LEFT_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.lessThanOrEqual=" + SMALLER_LEFT_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime is less than DEFAULT_LEFT_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.lessThan=" + DEFAULT_LEFT_SCENE_TIME);

        // Get all the resourceAssignedList where leftSceneTime is less than UPDATED_LEFT_SCENE_TIME
        defaultResourceAssignedShouldBeFound("leftSceneTime.lessThan=" + UPDATED_LEFT_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByLeftSceneTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where leftSceneTime is greater than DEFAULT_LEFT_SCENE_TIME
        defaultResourceAssignedShouldNotBeFound("leftSceneTime.greaterThan=" + DEFAULT_LEFT_SCENE_TIME);

        // Get all the resourceAssignedList where leftSceneTime is greater than SMALLER_LEFT_SCENE_TIME
        defaultResourceAssignedShouldBeFound("leftSceneTime.greaterThan=" + SMALLER_LEFT_SCENE_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime equals to DEFAULT_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("arrivedHospitalTime.equals=" + DEFAULT_ARRIVED_HOSPITAL_TIME);

        // Get all the resourceAssignedList where arrivedHospitalTime equals to UPDATED_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.equals=" + UPDATED_ARRIVED_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime in DEFAULT_ARRIVED_HOSPITAL_TIME or UPDATED_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound(
            "arrivedHospitalTime.in=" + DEFAULT_ARRIVED_HOSPITAL_TIME + "," + UPDATED_ARRIVED_HOSPITAL_TIME
        );

        // Get all the resourceAssignedList where arrivedHospitalTime equals to UPDATED_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.in=" + UPDATED_ARRIVED_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime is not null
        defaultResourceAssignedShouldBeFound("arrivedHospitalTime.specified=true");

        // Get all the resourceAssignedList where arrivedHospitalTime is null
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime is greater than or equal to DEFAULT_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("arrivedHospitalTime.greaterThanOrEqual=" + DEFAULT_ARRIVED_HOSPITAL_TIME);

        // Get all the resourceAssignedList where arrivedHospitalTime is greater than or equal to UPDATED_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.greaterThanOrEqual=" + UPDATED_ARRIVED_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime is less than or equal to DEFAULT_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("arrivedHospitalTime.lessThanOrEqual=" + DEFAULT_ARRIVED_HOSPITAL_TIME);

        // Get all the resourceAssignedList where arrivedHospitalTime is less than or equal to SMALLER_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.lessThanOrEqual=" + SMALLER_ARRIVED_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime is less than DEFAULT_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.lessThan=" + DEFAULT_ARRIVED_HOSPITAL_TIME);

        // Get all the resourceAssignedList where arrivedHospitalTime is less than UPDATED_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("arrivedHospitalTime.lessThan=" + UPDATED_ARRIVED_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByArrivedHospitalTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where arrivedHospitalTime is greater than DEFAULT_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("arrivedHospitalTime.greaterThan=" + DEFAULT_ARRIVED_HOSPITAL_TIME);

        // Get all the resourceAssignedList where arrivedHospitalTime is greater than SMALLER_ARRIVED_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("arrivedHospitalTime.greaterThan=" + SMALLER_ARRIVED_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime equals to DEFAULT_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("clearHospitalTime.equals=" + DEFAULT_CLEAR_HOSPITAL_TIME);

        // Get all the resourceAssignedList where clearHospitalTime equals to UPDATED_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.equals=" + UPDATED_CLEAR_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime in DEFAULT_CLEAR_HOSPITAL_TIME or UPDATED_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("clearHospitalTime.in=" + DEFAULT_CLEAR_HOSPITAL_TIME + "," + UPDATED_CLEAR_HOSPITAL_TIME);

        // Get all the resourceAssignedList where clearHospitalTime equals to UPDATED_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.in=" + UPDATED_CLEAR_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime is not null
        defaultResourceAssignedShouldBeFound("clearHospitalTime.specified=true");

        // Get all the resourceAssignedList where clearHospitalTime is null
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime is greater than or equal to DEFAULT_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("clearHospitalTime.greaterThanOrEqual=" + DEFAULT_CLEAR_HOSPITAL_TIME);

        // Get all the resourceAssignedList where clearHospitalTime is greater than or equal to UPDATED_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.greaterThanOrEqual=" + UPDATED_CLEAR_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime is less than or equal to DEFAULT_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("clearHospitalTime.lessThanOrEqual=" + DEFAULT_CLEAR_HOSPITAL_TIME);

        // Get all the resourceAssignedList where clearHospitalTime is less than or equal to SMALLER_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.lessThanOrEqual=" + SMALLER_CLEAR_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime is less than DEFAULT_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.lessThan=" + DEFAULT_CLEAR_HOSPITAL_TIME);

        // Get all the resourceAssignedList where clearHospitalTime is less than UPDATED_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("clearHospitalTime.lessThan=" + UPDATED_CLEAR_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByClearHospitalTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where clearHospitalTime is greater than DEFAULT_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldNotBeFound("clearHospitalTime.greaterThan=" + DEFAULT_CLEAR_HOSPITAL_TIME);

        // Get all the resourceAssignedList where clearHospitalTime is greater than SMALLER_CLEAR_HOSPITAL_TIME
        defaultResourceAssignedShouldBeFound("clearHospitalTime.greaterThan=" + SMALLER_CLEAR_HOSPITAL_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime equals to DEFAULT_GREEN_TIME
        defaultResourceAssignedShouldBeFound("greenTime.equals=" + DEFAULT_GREEN_TIME);

        // Get all the resourceAssignedList where greenTime equals to UPDATED_GREEN_TIME
        defaultResourceAssignedShouldNotBeFound("greenTime.equals=" + UPDATED_GREEN_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime in DEFAULT_GREEN_TIME or UPDATED_GREEN_TIME
        defaultResourceAssignedShouldBeFound("greenTime.in=" + DEFAULT_GREEN_TIME + "," + UPDATED_GREEN_TIME);

        // Get all the resourceAssignedList where greenTime equals to UPDATED_GREEN_TIME
        defaultResourceAssignedShouldNotBeFound("greenTime.in=" + UPDATED_GREEN_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime is not null
        defaultResourceAssignedShouldBeFound("greenTime.specified=true");

        // Get all the resourceAssignedList where greenTime is null
        defaultResourceAssignedShouldNotBeFound("greenTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime is greater than or equal to DEFAULT_GREEN_TIME
        defaultResourceAssignedShouldBeFound("greenTime.greaterThanOrEqual=" + DEFAULT_GREEN_TIME);

        // Get all the resourceAssignedList where greenTime is greater than or equal to UPDATED_GREEN_TIME
        defaultResourceAssignedShouldNotBeFound("greenTime.greaterThanOrEqual=" + UPDATED_GREEN_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime is less than or equal to DEFAULT_GREEN_TIME
        defaultResourceAssignedShouldBeFound("greenTime.lessThanOrEqual=" + DEFAULT_GREEN_TIME);

        // Get all the resourceAssignedList where greenTime is less than or equal to SMALLER_GREEN_TIME
        defaultResourceAssignedShouldNotBeFound("greenTime.lessThanOrEqual=" + SMALLER_GREEN_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime is less than DEFAULT_GREEN_TIME
        defaultResourceAssignedShouldNotBeFound("greenTime.lessThan=" + DEFAULT_GREEN_TIME);

        // Get all the resourceAssignedList where greenTime is less than UPDATED_GREEN_TIME
        defaultResourceAssignedShouldBeFound("greenTime.lessThan=" + UPDATED_GREEN_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByGreenTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where greenTime is greater than DEFAULT_GREEN_TIME
        defaultResourceAssignedShouldNotBeFound("greenTime.greaterThan=" + DEFAULT_GREEN_TIME);

        // Get all the resourceAssignedList where greenTime is greater than SMALLER_GREEN_TIME
        defaultResourceAssignedShouldBeFound("greenTime.greaterThan=" + SMALLER_GREEN_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime equals to DEFAULT_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldBeFound("unAssignedTime.equals=" + DEFAULT_UN_ASSIGNED_TIME);

        // Get all the resourceAssignedList where unAssignedTime equals to UPDATED_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.equals=" + UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime in DEFAULT_UN_ASSIGNED_TIME or UPDATED_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldBeFound("unAssignedTime.in=" + DEFAULT_UN_ASSIGNED_TIME + "," + UPDATED_UN_ASSIGNED_TIME);

        // Get all the resourceAssignedList where unAssignedTime equals to UPDATED_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.in=" + UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime is not null
        defaultResourceAssignedShouldBeFound("unAssignedTime.specified=true");

        // Get all the resourceAssignedList where unAssignedTime is null
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.specified=false");
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime is greater than or equal to DEFAULT_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldBeFound("unAssignedTime.greaterThanOrEqual=" + DEFAULT_UN_ASSIGNED_TIME);

        // Get all the resourceAssignedList where unAssignedTime is greater than or equal to UPDATED_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.greaterThanOrEqual=" + UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime is less than or equal to DEFAULT_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldBeFound("unAssignedTime.lessThanOrEqual=" + DEFAULT_UN_ASSIGNED_TIME);

        // Get all the resourceAssignedList where unAssignedTime is less than or equal to SMALLER_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.lessThanOrEqual=" + SMALLER_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime is less than DEFAULT_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.lessThan=" + DEFAULT_UN_ASSIGNED_TIME);

        // Get all the resourceAssignedList where unAssignedTime is less than UPDATED_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldBeFound("unAssignedTime.lessThan=" + UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByUnAssignedTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        // Get all the resourceAssignedList where unAssignedTime is greater than DEFAULT_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldNotBeFound("unAssignedTime.greaterThan=" + DEFAULT_UN_ASSIGNED_TIME);

        // Get all the resourceAssignedList where unAssignedTime is greater than SMALLER_UN_ASSIGNED_TIME
        defaultResourceAssignedShouldBeFound("unAssignedTime.greaterThan=" + SMALLER_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByResourceIsEqualToSomething() throws Exception {
        Resource resource;
        if (TestUtil.findAll(em, Resource.class).isEmpty()) {
            resourceAssignedRepository.saveAndFlush(resourceAssigned);
            resource = ResourceResourceIT.createEntity(em);
        } else {
            resource = TestUtil.findAll(em, Resource.class).get(0);
        }
        em.persist(resource);
        em.flush();
        resourceAssigned.setResource(resource);
        resourceAssignedRepository.saveAndFlush(resourceAssigned);
        Long resourceId = resource.getId();

        // Get all the resourceAssignedList where resource equals to resourceId
        defaultResourceAssignedShouldBeFound("resourceId.equals=" + resourceId);

        // Get all the resourceAssignedList where resource equals to (resourceId + 1)
        defaultResourceAssignedShouldNotBeFound("resourceId.equals=" + (resourceId + 1));
    }

    @Test
    @Transactional
    void getAllResourceAssignedsByEmergencyCallIsEqualToSomething() throws Exception {
        EmergencyCall emergencyCall;
        if (TestUtil.findAll(em, EmergencyCall.class).isEmpty()) {
            resourceAssignedRepository.saveAndFlush(resourceAssigned);
            emergencyCall = EmergencyCallResourceIT.createEntity(em);
        } else {
            emergencyCall = TestUtil.findAll(em, EmergencyCall.class).get(0);
        }
        em.persist(emergencyCall);
        em.flush();
        resourceAssigned.setEmergencyCall(emergencyCall);
        resourceAssignedRepository.saveAndFlush(resourceAssigned);
        Long emergencyCallId = emergencyCall.getId();

        // Get all the resourceAssignedList where emergencyCall equals to emergencyCallId
        defaultResourceAssignedShouldBeFound("emergencyCallId.equals=" + emergencyCallId);

        // Get all the resourceAssignedList where emergencyCall equals to (emergencyCallId + 1)
        defaultResourceAssignedShouldNotBeFound("emergencyCallId.equals=" + (emergencyCallId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultResourceAssignedShouldBeFound(String filter) throws Exception {
        restResourceAssignedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceAssigned.getId().intValue())))
            .andExpect(jsonPath("$.[*].callRecievedTime").value(hasItem(sameInstant(DEFAULT_CALL_RECIEVED_TIME))))
            .andExpect(jsonPath("$.[*].onSceneTime").value(hasItem(sameInstant(DEFAULT_ON_SCENE_TIME))))
            .andExpect(jsonPath("$.[*].leftSceneTime").value(hasItem(sameInstant(DEFAULT_LEFT_SCENE_TIME))))
            .andExpect(jsonPath("$.[*].arrivedHospitalTime").value(hasItem(sameInstant(DEFAULT_ARRIVED_HOSPITAL_TIME))))
            .andExpect(jsonPath("$.[*].clearHospitalTime").value(hasItem(sameInstant(DEFAULT_CLEAR_HOSPITAL_TIME))))
            .andExpect(jsonPath("$.[*].greenTime").value(hasItem(sameInstant(DEFAULT_GREEN_TIME))))
            .andExpect(jsonPath("$.[*].unAssignedTime").value(hasItem(sameInstant(DEFAULT_UN_ASSIGNED_TIME))));

        // Check, that the count call also returns 1
        restResourceAssignedMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultResourceAssignedShouldNotBeFound(String filter) throws Exception {
        restResourceAssignedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restResourceAssignedMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingResourceAssigned() throws Exception {
        // Get the resourceAssigned
        restResourceAssignedMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResourceAssigned() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();

        // Update the resourceAssigned
        ResourceAssigned updatedResourceAssigned = resourceAssignedRepository.findById(resourceAssigned.getId()).get();
        // Disconnect from session so that the updates on updatedResourceAssigned are not directly saved in db
        em.detach(updatedResourceAssigned);
        updatedResourceAssigned
            .callRecievedTime(UPDATED_CALL_RECIEVED_TIME)
            .onSceneTime(UPDATED_ON_SCENE_TIME)
            .leftSceneTime(UPDATED_LEFT_SCENE_TIME)
            .arrivedHospitalTime(UPDATED_ARRIVED_HOSPITAL_TIME)
            .clearHospitalTime(UPDATED_CLEAR_HOSPITAL_TIME)
            .greenTime(UPDATED_GREEN_TIME)
            .unAssignedTime(UPDATED_UN_ASSIGNED_TIME);

        restResourceAssignedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedResourceAssigned.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedResourceAssigned))
            )
            .andExpect(status().isOk());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
        ResourceAssigned testResourceAssigned = resourceAssignedList.get(resourceAssignedList.size() - 1);
        assertThat(testResourceAssigned.getCallRecievedTime()).isEqualTo(UPDATED_CALL_RECIEVED_TIME);
        assertThat(testResourceAssigned.getOnSceneTime()).isEqualTo(UPDATED_ON_SCENE_TIME);
        assertThat(testResourceAssigned.getLeftSceneTime()).isEqualTo(UPDATED_LEFT_SCENE_TIME);
        assertThat(testResourceAssigned.getArrivedHospitalTime()).isEqualTo(UPDATED_ARRIVED_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getClearHospitalTime()).isEqualTo(UPDATED_CLEAR_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getGreenTime()).isEqualTo(UPDATED_GREEN_TIME);
        assertThat(testResourceAssigned.getUnAssignedTime()).isEqualTo(UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void putNonExistingResourceAssigned() throws Exception {
        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();
        resourceAssigned.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceAssignedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceAssigned.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResourceAssigned() throws Exception {
        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();
        resourceAssigned.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAssignedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResourceAssigned() throws Exception {
        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();
        resourceAssigned.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAssignedMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResourceAssignedWithPatch() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();

        // Update the resourceAssigned using partial update
        ResourceAssigned partialUpdatedResourceAssigned = new ResourceAssigned();
        partialUpdatedResourceAssigned.setId(resourceAssigned.getId());

        partialUpdatedResourceAssigned
            .onSceneTime(UPDATED_ON_SCENE_TIME)
            .greenTime(UPDATED_GREEN_TIME)
            .unAssignedTime(UPDATED_UN_ASSIGNED_TIME);

        restResourceAssignedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceAssigned.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceAssigned))
            )
            .andExpect(status().isOk());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
        ResourceAssigned testResourceAssigned = resourceAssignedList.get(resourceAssignedList.size() - 1);
        assertThat(testResourceAssigned.getCallRecievedTime()).isEqualTo(DEFAULT_CALL_RECIEVED_TIME);
        assertThat(testResourceAssigned.getOnSceneTime()).isEqualTo(UPDATED_ON_SCENE_TIME);
        assertThat(testResourceAssigned.getLeftSceneTime()).isEqualTo(DEFAULT_LEFT_SCENE_TIME);
        assertThat(testResourceAssigned.getArrivedHospitalTime()).isEqualTo(DEFAULT_ARRIVED_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getClearHospitalTime()).isEqualTo(DEFAULT_CLEAR_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getGreenTime()).isEqualTo(UPDATED_GREEN_TIME);
        assertThat(testResourceAssigned.getUnAssignedTime()).isEqualTo(UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void fullUpdateResourceAssignedWithPatch() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();

        // Update the resourceAssigned using partial update
        ResourceAssigned partialUpdatedResourceAssigned = new ResourceAssigned();
        partialUpdatedResourceAssigned.setId(resourceAssigned.getId());

        partialUpdatedResourceAssigned
            .callRecievedTime(UPDATED_CALL_RECIEVED_TIME)
            .onSceneTime(UPDATED_ON_SCENE_TIME)
            .leftSceneTime(UPDATED_LEFT_SCENE_TIME)
            .arrivedHospitalTime(UPDATED_ARRIVED_HOSPITAL_TIME)
            .clearHospitalTime(UPDATED_CLEAR_HOSPITAL_TIME)
            .greenTime(UPDATED_GREEN_TIME)
            .unAssignedTime(UPDATED_UN_ASSIGNED_TIME);

        restResourceAssignedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceAssigned.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceAssigned))
            )
            .andExpect(status().isOk());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
        ResourceAssigned testResourceAssigned = resourceAssignedList.get(resourceAssignedList.size() - 1);
        assertThat(testResourceAssigned.getCallRecievedTime()).isEqualTo(UPDATED_CALL_RECIEVED_TIME);
        assertThat(testResourceAssigned.getOnSceneTime()).isEqualTo(UPDATED_ON_SCENE_TIME);
        assertThat(testResourceAssigned.getLeftSceneTime()).isEqualTo(UPDATED_LEFT_SCENE_TIME);
        assertThat(testResourceAssigned.getArrivedHospitalTime()).isEqualTo(UPDATED_ARRIVED_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getClearHospitalTime()).isEqualTo(UPDATED_CLEAR_HOSPITAL_TIME);
        assertThat(testResourceAssigned.getGreenTime()).isEqualTo(UPDATED_GREEN_TIME);
        assertThat(testResourceAssigned.getUnAssignedTime()).isEqualTo(UPDATED_UN_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingResourceAssigned() throws Exception {
        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();
        resourceAssigned.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceAssignedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resourceAssigned.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResourceAssigned() throws Exception {
        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();
        resourceAssigned.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAssignedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResourceAssigned() throws Exception {
        int databaseSizeBeforeUpdate = resourceAssignedRepository.findAll().size();
        resourceAssigned.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceAssignedMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceAssigned))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceAssigned in the database
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResourceAssigned() throws Exception {
        // Initialize the database
        resourceAssignedRepository.saveAndFlush(resourceAssigned);

        int databaseSizeBeforeDelete = resourceAssignedRepository.findAll().size();

        // Delete the resourceAssigned
        restResourceAssignedMockMvc
            .perform(delete(ENTITY_API_URL_ID, resourceAssigned.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ResourceAssigned> resourceAssignedList = resourceAssignedRepository.findAll();
        assertThat(resourceAssignedList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
