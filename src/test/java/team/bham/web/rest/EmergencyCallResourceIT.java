package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team.bham.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import team.bham.domain.EmergencyCall;
import team.bham.domain.Event;
import team.bham.domain.ResourceAssigned;
import team.bham.domain.SystemLog;
import team.bham.domain.User;
import team.bham.domain.enumeration.CallCategory;
import team.bham.domain.enumeration.Sex;
import team.bham.repository.EmergencyCallRepository;
import team.bham.service.EmergencyCallService;
import team.bham.service.criteria.EmergencyCallCriteria;

/**
 * Integration tests for the {@link EmergencyCallResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EmergencyCallResourceIT {

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_OPEN = false;
    private static final Boolean UPDATED_OPEN = true;

    private static final CallCategory DEFAULT_TYPE = CallCategory.CAT1;
    private static final CallCategory UPDATED_TYPE = CallCategory.CAT2;

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;
    private static final Integer SMALLER_AGE = 1 - 1;

    private static final Sex DEFAULT_SEX_ASSIGNED_AT_BIRTH = Sex.MALE;
    private static final Sex UPDATED_SEX_ASSIGNED_AT_BIRTH = Sex.FEMALE;

    private static final String DEFAULT_HISTORY = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY = "BBBBBBBBBB";

    private static final String DEFAULT_INJURIES = "AAAAAAAAAA";
    private static final String UPDATED_INJURIES = "BBBBBBBBBB";

    private static final String DEFAULT_CONDITION = "AAAAAAAAAA";
    private static final String UPDATED_CONDITION = "BBBBBBBBBB";

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;
    private static final Float SMALLER_LATITUDE = 1F - 1F;

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;
    private static final Float SMALLER_LONGITUDE = 1F - 1F;

    private static final Boolean DEFAULT_CLOSED = false;
    private static final Boolean UPDATED_CLOSED = true;

    private static final String ENTITY_API_URL = "/api/emergency-calls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmergencyCallRepository emergencyCallRepository;

    @Mock
    private EmergencyCallRepository emergencyCallRepositoryMock;

    @Mock
    private EmergencyCallService emergencyCallServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmergencyCallMockMvc;

    private EmergencyCall emergencyCall;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmergencyCall createEntity(EntityManager em) {
        EmergencyCall emergencyCall = new EmergencyCall()
            .created(DEFAULT_CREATED)
            .open(DEFAULT_OPEN)
            .type(DEFAULT_TYPE)
            .age(DEFAULT_AGE)
            .sexAssignedAtBirth(DEFAULT_SEX_ASSIGNED_AT_BIRTH)
            .history(DEFAULT_HISTORY)
            .injuries(DEFAULT_INJURIES)
            .condition(DEFAULT_CONDITION)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .closed(DEFAULT_CLOSED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        emergencyCall.setCreatedBy(user);
        return emergencyCall;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmergencyCall createUpdatedEntity(EntityManager em) {
        EmergencyCall emergencyCall = new EmergencyCall()
            .created(UPDATED_CREATED)
            .open(UPDATED_OPEN)
            .type(UPDATED_TYPE)
            .age(UPDATED_AGE)
            .sexAssignedAtBirth(UPDATED_SEX_ASSIGNED_AT_BIRTH)
            .history(UPDATED_HISTORY)
            .injuries(UPDATED_INJURIES)
            .condition(UPDATED_CONDITION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .closed(UPDATED_CLOSED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        emergencyCall.setCreatedBy(user);
        return emergencyCall;
    }

    @BeforeEach
    public void initTest() {
        emergencyCall = createEntity(em);
    }

    @Test
    @Transactional
    void createEmergencyCall() throws Exception {
        int databaseSizeBeforeCreate = emergencyCallRepository.findAll().size();
        // Create the EmergencyCall
        restEmergencyCallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emergencyCall)))
            .andExpect(status().isCreated());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeCreate + 1);
        EmergencyCall testEmergencyCall = emergencyCallList.get(emergencyCallList.size() - 1);
        assertThat(testEmergencyCall.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testEmergencyCall.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testEmergencyCall.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testEmergencyCall.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testEmergencyCall.getSexAssignedAtBirth()).isEqualTo(DEFAULT_SEX_ASSIGNED_AT_BIRTH);
        assertThat(testEmergencyCall.getHistory()).isEqualTo(DEFAULT_HISTORY);
        assertThat(testEmergencyCall.getInjuries()).isEqualTo(DEFAULT_INJURIES);
        assertThat(testEmergencyCall.getCondition()).isEqualTo(DEFAULT_CONDITION);
        assertThat(testEmergencyCall.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testEmergencyCall.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testEmergencyCall.getClosed()).isEqualTo(DEFAULT_CLOSED);
    }

    @Test
    @Transactional
    void createEmergencyCallWithExistingId() throws Exception {
        // Create the EmergencyCall with an existing ID
        emergencyCall.setId(1L);

        int databaseSizeBeforeCreate = emergencyCallRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmergencyCallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emergencyCall)))
            .andExpect(status().isBadRequest());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = emergencyCallRepository.findAll().size();
        // set the field null
        emergencyCall.setCreated(null);

        // Create the EmergencyCall, which fails.

        restEmergencyCallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emergencyCall)))
            .andExpect(status().isBadRequest());

        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = emergencyCallRepository.findAll().size();
        // set the field null
        emergencyCall.setLatitude(null);

        // Create the EmergencyCall, which fails.

        restEmergencyCallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emergencyCall)))
            .andExpect(status().isBadRequest());

        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = emergencyCallRepository.findAll().size();
        // set the field null
        emergencyCall.setLongitude(null);

        // Create the EmergencyCall, which fails.

        restEmergencyCallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emergencyCall)))
            .andExpect(status().isBadRequest());

        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmergencyCalls() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList
        restEmergencyCallMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emergencyCall.getId().intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.booleanValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].sexAssignedAtBirth").value(hasItem(DEFAULT_SEX_ASSIGNED_AT_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].history").value(hasItem(DEFAULT_HISTORY)))
            .andExpect(jsonPath("$.[*].injuries").value(hasItem(DEFAULT_INJURIES)))
            .andExpect(jsonPath("$.[*].condition").value(hasItem(DEFAULT_CONDITION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].closed").value(hasItem(DEFAULT_CLOSED.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmergencyCallsWithEagerRelationshipsIsEnabled() throws Exception {
        when(emergencyCallServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmergencyCallMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(emergencyCallServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmergencyCallsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(emergencyCallServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmergencyCallMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(emergencyCallRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEmergencyCall() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get the emergencyCall
        restEmergencyCallMockMvc
            .perform(get(ENTITY_API_URL_ID, emergencyCall.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(emergencyCall.getId().intValue()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.open").value(DEFAULT_OPEN.booleanValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.sexAssignedAtBirth").value(DEFAULT_SEX_ASSIGNED_AT_BIRTH.toString()))
            .andExpect(jsonPath("$.history").value(DEFAULT_HISTORY))
            .andExpect(jsonPath("$.injuries").value(DEFAULT_INJURIES))
            .andExpect(jsonPath("$.condition").value(DEFAULT_CONDITION))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.closed").value(DEFAULT_CLOSED.booleanValue()));
    }

    @Test
    @Transactional
    void getEmergencyCallsByIdFiltering() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        Long id = emergencyCall.getId();

        defaultEmergencyCallShouldBeFound("id.equals=" + id);
        defaultEmergencyCallShouldNotBeFound("id.notEquals=" + id);

        defaultEmergencyCallShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEmergencyCallShouldNotBeFound("id.greaterThan=" + id);

        defaultEmergencyCallShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEmergencyCallShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created equals to DEFAULT_CREATED
        defaultEmergencyCallShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the emergencyCallList where created equals to UPDATED_CREATED
        defaultEmergencyCallShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultEmergencyCallShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the emergencyCallList where created equals to UPDATED_CREATED
        defaultEmergencyCallShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created is not null
        defaultEmergencyCallShouldBeFound("created.specified=true");

        // Get all the emergencyCallList where created is null
        defaultEmergencyCallShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created is greater than or equal to DEFAULT_CREATED
        defaultEmergencyCallShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the emergencyCallList where created is greater than or equal to UPDATED_CREATED
        defaultEmergencyCallShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created is less than or equal to DEFAULT_CREATED
        defaultEmergencyCallShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the emergencyCallList where created is less than or equal to SMALLER_CREATED
        defaultEmergencyCallShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created is less than DEFAULT_CREATED
        defaultEmergencyCallShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the emergencyCallList where created is less than UPDATED_CREATED
        defaultEmergencyCallShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where created is greater than DEFAULT_CREATED
        defaultEmergencyCallShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the emergencyCallList where created is greater than SMALLER_CREATED
        defaultEmergencyCallShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByOpenIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where open equals to DEFAULT_OPEN
        defaultEmergencyCallShouldBeFound("open.equals=" + DEFAULT_OPEN);

        // Get all the emergencyCallList where open equals to UPDATED_OPEN
        defaultEmergencyCallShouldNotBeFound("open.equals=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByOpenIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where open in DEFAULT_OPEN or UPDATED_OPEN
        defaultEmergencyCallShouldBeFound("open.in=" + DEFAULT_OPEN + "," + UPDATED_OPEN);

        // Get all the emergencyCallList where open equals to UPDATED_OPEN
        defaultEmergencyCallShouldNotBeFound("open.in=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByOpenIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where open is not null
        defaultEmergencyCallShouldBeFound("open.specified=true");

        // Get all the emergencyCallList where open is null
        defaultEmergencyCallShouldNotBeFound("open.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where type equals to DEFAULT_TYPE
        defaultEmergencyCallShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the emergencyCallList where type equals to UPDATED_TYPE
        defaultEmergencyCallShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultEmergencyCallShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the emergencyCallList where type equals to UPDATED_TYPE
        defaultEmergencyCallShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where type is not null
        defaultEmergencyCallShouldBeFound("type.specified=true");

        // Get all the emergencyCallList where type is null
        defaultEmergencyCallShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age equals to DEFAULT_AGE
        defaultEmergencyCallShouldBeFound("age.equals=" + DEFAULT_AGE);

        // Get all the emergencyCallList where age equals to UPDATED_AGE
        defaultEmergencyCallShouldNotBeFound("age.equals=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age in DEFAULT_AGE or UPDATED_AGE
        defaultEmergencyCallShouldBeFound("age.in=" + DEFAULT_AGE + "," + UPDATED_AGE);

        // Get all the emergencyCallList where age equals to UPDATED_AGE
        defaultEmergencyCallShouldNotBeFound("age.in=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age is not null
        defaultEmergencyCallShouldBeFound("age.specified=true");

        // Get all the emergencyCallList where age is null
        defaultEmergencyCallShouldNotBeFound("age.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age is greater than or equal to DEFAULT_AGE
        defaultEmergencyCallShouldBeFound("age.greaterThanOrEqual=" + DEFAULT_AGE);

        // Get all the emergencyCallList where age is greater than or equal to UPDATED_AGE
        defaultEmergencyCallShouldNotBeFound("age.greaterThanOrEqual=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age is less than or equal to DEFAULT_AGE
        defaultEmergencyCallShouldBeFound("age.lessThanOrEqual=" + DEFAULT_AGE);

        // Get all the emergencyCallList where age is less than or equal to SMALLER_AGE
        defaultEmergencyCallShouldNotBeFound("age.lessThanOrEqual=" + SMALLER_AGE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsLessThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age is less than DEFAULT_AGE
        defaultEmergencyCallShouldNotBeFound("age.lessThan=" + DEFAULT_AGE);

        // Get all the emergencyCallList where age is less than UPDATED_AGE
        defaultEmergencyCallShouldBeFound("age.lessThan=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByAgeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where age is greater than DEFAULT_AGE
        defaultEmergencyCallShouldNotBeFound("age.greaterThan=" + DEFAULT_AGE);

        // Get all the emergencyCallList where age is greater than SMALLER_AGE
        defaultEmergencyCallShouldBeFound("age.greaterThan=" + SMALLER_AGE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsBySexAssignedAtBirthIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where sexAssignedAtBirth equals to DEFAULT_SEX_ASSIGNED_AT_BIRTH
        defaultEmergencyCallShouldBeFound("sexAssignedAtBirth.equals=" + DEFAULT_SEX_ASSIGNED_AT_BIRTH);

        // Get all the emergencyCallList where sexAssignedAtBirth equals to UPDATED_SEX_ASSIGNED_AT_BIRTH
        defaultEmergencyCallShouldNotBeFound("sexAssignedAtBirth.equals=" + UPDATED_SEX_ASSIGNED_AT_BIRTH);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsBySexAssignedAtBirthIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where sexAssignedAtBirth in DEFAULT_SEX_ASSIGNED_AT_BIRTH or UPDATED_SEX_ASSIGNED_AT_BIRTH
        defaultEmergencyCallShouldBeFound("sexAssignedAtBirth.in=" + DEFAULT_SEX_ASSIGNED_AT_BIRTH + "," + UPDATED_SEX_ASSIGNED_AT_BIRTH);

        // Get all the emergencyCallList where sexAssignedAtBirth equals to UPDATED_SEX_ASSIGNED_AT_BIRTH
        defaultEmergencyCallShouldNotBeFound("sexAssignedAtBirth.in=" + UPDATED_SEX_ASSIGNED_AT_BIRTH);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsBySexAssignedAtBirthIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where sexAssignedAtBirth is not null
        defaultEmergencyCallShouldBeFound("sexAssignedAtBirth.specified=true");

        // Get all the emergencyCallList where sexAssignedAtBirth is null
        defaultEmergencyCallShouldNotBeFound("sexAssignedAtBirth.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByHistoryIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where history equals to DEFAULT_HISTORY
        defaultEmergencyCallShouldBeFound("history.equals=" + DEFAULT_HISTORY);

        // Get all the emergencyCallList where history equals to UPDATED_HISTORY
        defaultEmergencyCallShouldNotBeFound("history.equals=" + UPDATED_HISTORY);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByHistoryIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where history in DEFAULT_HISTORY or UPDATED_HISTORY
        defaultEmergencyCallShouldBeFound("history.in=" + DEFAULT_HISTORY + "," + UPDATED_HISTORY);

        // Get all the emergencyCallList where history equals to UPDATED_HISTORY
        defaultEmergencyCallShouldNotBeFound("history.in=" + UPDATED_HISTORY);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByHistoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where history is not null
        defaultEmergencyCallShouldBeFound("history.specified=true");

        // Get all the emergencyCallList where history is null
        defaultEmergencyCallShouldNotBeFound("history.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByHistoryContainsSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where history contains DEFAULT_HISTORY
        defaultEmergencyCallShouldBeFound("history.contains=" + DEFAULT_HISTORY);

        // Get all the emergencyCallList where history contains UPDATED_HISTORY
        defaultEmergencyCallShouldNotBeFound("history.contains=" + UPDATED_HISTORY);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByHistoryNotContainsSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where history does not contain DEFAULT_HISTORY
        defaultEmergencyCallShouldNotBeFound("history.doesNotContain=" + DEFAULT_HISTORY);

        // Get all the emergencyCallList where history does not contain UPDATED_HISTORY
        defaultEmergencyCallShouldBeFound("history.doesNotContain=" + UPDATED_HISTORY);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByInjuriesIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where injuries equals to DEFAULT_INJURIES
        defaultEmergencyCallShouldBeFound("injuries.equals=" + DEFAULT_INJURIES);

        // Get all the emergencyCallList where injuries equals to UPDATED_INJURIES
        defaultEmergencyCallShouldNotBeFound("injuries.equals=" + UPDATED_INJURIES);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByInjuriesIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where injuries in DEFAULT_INJURIES or UPDATED_INJURIES
        defaultEmergencyCallShouldBeFound("injuries.in=" + DEFAULT_INJURIES + "," + UPDATED_INJURIES);

        // Get all the emergencyCallList where injuries equals to UPDATED_INJURIES
        defaultEmergencyCallShouldNotBeFound("injuries.in=" + UPDATED_INJURIES);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByInjuriesIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where injuries is not null
        defaultEmergencyCallShouldBeFound("injuries.specified=true");

        // Get all the emergencyCallList where injuries is null
        defaultEmergencyCallShouldNotBeFound("injuries.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByInjuriesContainsSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where injuries contains DEFAULT_INJURIES
        defaultEmergencyCallShouldBeFound("injuries.contains=" + DEFAULT_INJURIES);

        // Get all the emergencyCallList where injuries contains UPDATED_INJURIES
        defaultEmergencyCallShouldNotBeFound("injuries.contains=" + UPDATED_INJURIES);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByInjuriesNotContainsSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where injuries does not contain DEFAULT_INJURIES
        defaultEmergencyCallShouldNotBeFound("injuries.doesNotContain=" + DEFAULT_INJURIES);

        // Get all the emergencyCallList where injuries does not contain UPDATED_INJURIES
        defaultEmergencyCallShouldBeFound("injuries.doesNotContain=" + UPDATED_INJURIES);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByConditionIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where condition equals to DEFAULT_CONDITION
        defaultEmergencyCallShouldBeFound("condition.equals=" + DEFAULT_CONDITION);

        // Get all the emergencyCallList where condition equals to UPDATED_CONDITION
        defaultEmergencyCallShouldNotBeFound("condition.equals=" + UPDATED_CONDITION);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByConditionIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where condition in DEFAULT_CONDITION or UPDATED_CONDITION
        defaultEmergencyCallShouldBeFound("condition.in=" + DEFAULT_CONDITION + "," + UPDATED_CONDITION);

        // Get all the emergencyCallList where condition equals to UPDATED_CONDITION
        defaultEmergencyCallShouldNotBeFound("condition.in=" + UPDATED_CONDITION);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByConditionIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where condition is not null
        defaultEmergencyCallShouldBeFound("condition.specified=true");

        // Get all the emergencyCallList where condition is null
        defaultEmergencyCallShouldNotBeFound("condition.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByConditionContainsSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where condition contains DEFAULT_CONDITION
        defaultEmergencyCallShouldBeFound("condition.contains=" + DEFAULT_CONDITION);

        // Get all the emergencyCallList where condition contains UPDATED_CONDITION
        defaultEmergencyCallShouldNotBeFound("condition.contains=" + UPDATED_CONDITION);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByConditionNotContainsSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where condition does not contain DEFAULT_CONDITION
        defaultEmergencyCallShouldNotBeFound("condition.doesNotContain=" + DEFAULT_CONDITION);

        // Get all the emergencyCallList where condition does not contain UPDATED_CONDITION
        defaultEmergencyCallShouldBeFound("condition.doesNotContain=" + UPDATED_CONDITION);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude equals to DEFAULT_LATITUDE
        defaultEmergencyCallShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the emergencyCallList where latitude equals to UPDATED_LATITUDE
        defaultEmergencyCallShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultEmergencyCallShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the emergencyCallList where latitude equals to UPDATED_LATITUDE
        defaultEmergencyCallShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude is not null
        defaultEmergencyCallShouldBeFound("latitude.specified=true");

        // Get all the emergencyCallList where latitude is null
        defaultEmergencyCallShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultEmergencyCallShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the emergencyCallList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultEmergencyCallShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultEmergencyCallShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the emergencyCallList where latitude is less than or equal to SMALLER_LATITUDE
        defaultEmergencyCallShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude is less than DEFAULT_LATITUDE
        defaultEmergencyCallShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the emergencyCallList where latitude is less than UPDATED_LATITUDE
        defaultEmergencyCallShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where latitude is greater than DEFAULT_LATITUDE
        defaultEmergencyCallShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the emergencyCallList where latitude is greater than SMALLER_LATITUDE
        defaultEmergencyCallShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude equals to DEFAULT_LONGITUDE
        defaultEmergencyCallShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the emergencyCallList where longitude equals to UPDATED_LONGITUDE
        defaultEmergencyCallShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultEmergencyCallShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the emergencyCallList where longitude equals to UPDATED_LONGITUDE
        defaultEmergencyCallShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude is not null
        defaultEmergencyCallShouldBeFound("longitude.specified=true");

        // Get all the emergencyCallList where longitude is null
        defaultEmergencyCallShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultEmergencyCallShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the emergencyCallList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultEmergencyCallShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultEmergencyCallShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the emergencyCallList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultEmergencyCallShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude is less than DEFAULT_LONGITUDE
        defaultEmergencyCallShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the emergencyCallList where longitude is less than UPDATED_LONGITUDE
        defaultEmergencyCallShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where longitude is greater than DEFAULT_LONGITUDE
        defaultEmergencyCallShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the emergencyCallList where longitude is greater than SMALLER_LONGITUDE
        defaultEmergencyCallShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByClosedIsEqualToSomething() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where closed equals to DEFAULT_CLOSED
        defaultEmergencyCallShouldBeFound("closed.equals=" + DEFAULT_CLOSED);

        // Get all the emergencyCallList where closed equals to UPDATED_CLOSED
        defaultEmergencyCallShouldNotBeFound("closed.equals=" + UPDATED_CLOSED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByClosedIsInShouldWork() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where closed in DEFAULT_CLOSED or UPDATED_CLOSED
        defaultEmergencyCallShouldBeFound("closed.in=" + DEFAULT_CLOSED + "," + UPDATED_CLOSED);

        // Get all the emergencyCallList where closed equals to UPDATED_CLOSED
        defaultEmergencyCallShouldNotBeFound("closed.in=" + UPDATED_CLOSED);
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByClosedIsNullOrNotNull() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        // Get all the emergencyCallList where closed is not null
        defaultEmergencyCallShouldBeFound("closed.specified=true");

        // Get all the emergencyCallList where closed is null
        defaultEmergencyCallShouldNotBeFound("closed.specified=false");
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByResourceAssignedIsEqualToSomething() throws Exception {
        ResourceAssigned resourceAssigned;
        if (TestUtil.findAll(em, ResourceAssigned.class).isEmpty()) {
            emergencyCallRepository.saveAndFlush(emergencyCall);
            resourceAssigned = ResourceAssignedResourceIT.createEntity(em);
        } else {
            resourceAssigned = TestUtil.findAll(em, ResourceAssigned.class).get(0);
        }
        em.persist(resourceAssigned);
        em.flush();
        emergencyCall.addResourceAssigned(resourceAssigned);
        emergencyCallRepository.saveAndFlush(emergencyCall);
        Long resourceAssignedId = resourceAssigned.getId();

        // Get all the emergencyCallList where resourceAssigned equals to resourceAssignedId
        defaultEmergencyCallShouldBeFound("resourceAssignedId.equals=" + resourceAssignedId);

        // Get all the emergencyCallList where resourceAssigned equals to (resourceAssignedId + 1)
        defaultEmergencyCallShouldNotBeFound("resourceAssignedId.equals=" + (resourceAssignedId + 1));
    }

    @Test
    @Transactional
    void getAllEmergencyCallsBySystemLogIsEqualToSomething() throws Exception {
        SystemLog systemLog;
        if (TestUtil.findAll(em, SystemLog.class).isEmpty()) {
            emergencyCallRepository.saveAndFlush(emergencyCall);
            systemLog = SystemLogResourceIT.createEntity(em);
        } else {
            systemLog = TestUtil.findAll(em, SystemLog.class).get(0);
        }
        em.persist(systemLog);
        em.flush();
        emergencyCall.addSystemLog(systemLog);
        emergencyCallRepository.saveAndFlush(emergencyCall);
        Long systemLogId = systemLog.getId();

        // Get all the emergencyCallList where systemLog equals to systemLogId
        defaultEmergencyCallShouldBeFound("systemLogId.equals=" + systemLogId);

        // Get all the emergencyCallList where systemLog equals to (systemLogId + 1)
        defaultEmergencyCallShouldNotBeFound("systemLogId.equals=" + (systemLogId + 1));
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByCreatedByIsEqualToSomething() throws Exception {
        User createdBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            emergencyCallRepository.saveAndFlush(emergencyCall);
            createdBy = UserResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        emergencyCall.setCreatedBy(createdBy);
        emergencyCallRepository.saveAndFlush(emergencyCall);
        Long createdById = createdBy.getId();

        // Get all the emergencyCallList where createdBy equals to createdById
        defaultEmergencyCallShouldBeFound("createdById.equals=" + createdById);

        // Get all the emergencyCallList where createdBy equals to (createdById + 1)
        defaultEmergencyCallShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllEmergencyCallsByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            emergencyCallRepository.saveAndFlush(emergencyCall);
            event = EventResourceIT.createEntity(em);
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        emergencyCall.setEvent(event);
        emergencyCallRepository.saveAndFlush(emergencyCall);
        Long eventId = event.getId();

        // Get all the emergencyCallList where event equals to eventId
        defaultEmergencyCallShouldBeFound("eventId.equals=" + eventId);

        // Get all the emergencyCallList where event equals to (eventId + 1)
        defaultEmergencyCallShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmergencyCallShouldBeFound(String filter) throws Exception {
        restEmergencyCallMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emergencyCall.getId().intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.booleanValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].sexAssignedAtBirth").value(hasItem(DEFAULT_SEX_ASSIGNED_AT_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].history").value(hasItem(DEFAULT_HISTORY)))
            .andExpect(jsonPath("$.[*].injuries").value(hasItem(DEFAULT_INJURIES)))
            .andExpect(jsonPath("$.[*].condition").value(hasItem(DEFAULT_CONDITION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].closed").value(hasItem(DEFAULT_CLOSED.booleanValue())));

        // Check, that the count call also returns 1
        restEmergencyCallMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmergencyCallShouldNotBeFound(String filter) throws Exception {
        restEmergencyCallMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmergencyCallMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEmergencyCall() throws Exception {
        // Get the emergencyCall
        restEmergencyCallMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmergencyCall() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();

        // Update the emergencyCall
        EmergencyCall updatedEmergencyCall = emergencyCallRepository.findById(emergencyCall.getId()).get();
        // Disconnect from session so that the updates on updatedEmergencyCall are not directly saved in db
        em.detach(updatedEmergencyCall);
        updatedEmergencyCall
            .created(UPDATED_CREATED)
            .open(UPDATED_OPEN)
            .type(UPDATED_TYPE)
            .age(UPDATED_AGE)
            .sexAssignedAtBirth(UPDATED_SEX_ASSIGNED_AT_BIRTH)
            .history(UPDATED_HISTORY)
            .injuries(UPDATED_INJURIES)
            .condition(UPDATED_CONDITION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .closed(UPDATED_CLOSED);

        restEmergencyCallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmergencyCall.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmergencyCall))
            )
            .andExpect(status().isOk());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
        EmergencyCall testEmergencyCall = emergencyCallList.get(emergencyCallList.size() - 1);
        assertThat(testEmergencyCall.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testEmergencyCall.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testEmergencyCall.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testEmergencyCall.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testEmergencyCall.getSexAssignedAtBirth()).isEqualTo(UPDATED_SEX_ASSIGNED_AT_BIRTH);
        assertThat(testEmergencyCall.getHistory()).isEqualTo(UPDATED_HISTORY);
        assertThat(testEmergencyCall.getInjuries()).isEqualTo(UPDATED_INJURIES);
        assertThat(testEmergencyCall.getCondition()).isEqualTo(UPDATED_CONDITION);
        assertThat(testEmergencyCall.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testEmergencyCall.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testEmergencyCall.getClosed()).isEqualTo(UPDATED_CLOSED);
    }

    @Test
    @Transactional
    void putNonExistingEmergencyCall() throws Exception {
        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();
        emergencyCall.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmergencyCallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emergencyCall.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emergencyCall))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmergencyCall() throws Exception {
        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();
        emergencyCall.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmergencyCallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emergencyCall))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmergencyCall() throws Exception {
        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();
        emergencyCall.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmergencyCallMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emergencyCall)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmergencyCallWithPatch() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();

        // Update the emergencyCall using partial update
        EmergencyCall partialUpdatedEmergencyCall = new EmergencyCall();
        partialUpdatedEmergencyCall.setId(emergencyCall.getId());

        partialUpdatedEmergencyCall
            .created(UPDATED_CREATED)
            .open(UPDATED_OPEN)
            .sexAssignedAtBirth(UPDATED_SEX_ASSIGNED_AT_BIRTH)
            .history(UPDATED_HISTORY)
            .condition(UPDATED_CONDITION)
            .closed(UPDATED_CLOSED);

        restEmergencyCallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmergencyCall.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmergencyCall))
            )
            .andExpect(status().isOk());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
        EmergencyCall testEmergencyCall = emergencyCallList.get(emergencyCallList.size() - 1);
        assertThat(testEmergencyCall.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testEmergencyCall.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testEmergencyCall.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testEmergencyCall.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testEmergencyCall.getSexAssignedAtBirth()).isEqualTo(UPDATED_SEX_ASSIGNED_AT_BIRTH);
        assertThat(testEmergencyCall.getHistory()).isEqualTo(UPDATED_HISTORY);
        assertThat(testEmergencyCall.getInjuries()).isEqualTo(DEFAULT_INJURIES);
        assertThat(testEmergencyCall.getCondition()).isEqualTo(UPDATED_CONDITION);
        assertThat(testEmergencyCall.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testEmergencyCall.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testEmergencyCall.getClosed()).isEqualTo(UPDATED_CLOSED);
    }

    @Test
    @Transactional
    void fullUpdateEmergencyCallWithPatch() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();

        // Update the emergencyCall using partial update
        EmergencyCall partialUpdatedEmergencyCall = new EmergencyCall();
        partialUpdatedEmergencyCall.setId(emergencyCall.getId());

        partialUpdatedEmergencyCall
            .created(UPDATED_CREATED)
            .open(UPDATED_OPEN)
            .type(UPDATED_TYPE)
            .age(UPDATED_AGE)
            .sexAssignedAtBirth(UPDATED_SEX_ASSIGNED_AT_BIRTH)
            .history(UPDATED_HISTORY)
            .injuries(UPDATED_INJURIES)
            .condition(UPDATED_CONDITION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .closed(UPDATED_CLOSED);

        restEmergencyCallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmergencyCall.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmergencyCall))
            )
            .andExpect(status().isOk());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
        EmergencyCall testEmergencyCall = emergencyCallList.get(emergencyCallList.size() - 1);
        assertThat(testEmergencyCall.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testEmergencyCall.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testEmergencyCall.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testEmergencyCall.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testEmergencyCall.getSexAssignedAtBirth()).isEqualTo(UPDATED_SEX_ASSIGNED_AT_BIRTH);
        assertThat(testEmergencyCall.getHistory()).isEqualTo(UPDATED_HISTORY);
        assertThat(testEmergencyCall.getInjuries()).isEqualTo(UPDATED_INJURIES);
        assertThat(testEmergencyCall.getCondition()).isEqualTo(UPDATED_CONDITION);
        assertThat(testEmergencyCall.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testEmergencyCall.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testEmergencyCall.getClosed()).isEqualTo(UPDATED_CLOSED);
    }

    @Test
    @Transactional
    void patchNonExistingEmergencyCall() throws Exception {
        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();
        emergencyCall.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmergencyCallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, emergencyCall.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emergencyCall))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmergencyCall() throws Exception {
        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();
        emergencyCall.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmergencyCallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emergencyCall))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmergencyCall() throws Exception {
        int databaseSizeBeforeUpdate = emergencyCallRepository.findAll().size();
        emergencyCall.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmergencyCallMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(emergencyCall))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EmergencyCall in the database
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmergencyCall() throws Exception {
        // Initialize the database
        emergencyCallRepository.saveAndFlush(emergencyCall);

        int databaseSizeBeforeDelete = emergencyCallRepository.findAll().size();

        // Delete the emergencyCall
        restEmergencyCallMockMvc
            .perform(delete(ENTITY_API_URL_ID, emergencyCall.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EmergencyCall> emergencyCallList = emergencyCallRepository.findAll();
        assertThat(emergencyCallList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
