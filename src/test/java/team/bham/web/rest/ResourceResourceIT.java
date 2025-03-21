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
import team.bham.domain.Event;
import team.bham.domain.Resource;
import team.bham.domain.ResourceBreaks;
import team.bham.domain.enumeration.ResourceType;
import team.bham.domain.enumeration.Status;
import team.bham.repository.ResourceRepository;
import team.bham.service.criteria.ResourceCriteria;

/**
 * Integration tests for the {@link ResourceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResourceResourceIT {

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ResourceType DEFAULT_TYPE = ResourceType.AMBULANCE;
    private static final ResourceType UPDATED_TYPE = ResourceType.CYCLERESPONSE;

    private static final Status DEFAULT_STATUS = Status.RED;
    private static final Status UPDATED_STATUS = Status.GREEN;

    private static final String DEFAULT_CALL_SIGN = "AAAAAAAAAA";
    private static final String UPDATED_CALL_SIGN = "BBBBBBBBBB";

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;
    private static final Float SMALLER_LATITUDE = 1F - 1F;

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;
    private static final Float SMALLER_LONGITUDE = 1F - 1F;

    private static final String ENTITY_API_URL = "/api/resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResourceMockMvc;

    private Resource resource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resource createEntity(EntityManager em) {
        Resource resource = new Resource()
            .created(DEFAULT_CREATED)
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS)
            .callSign(DEFAULT_CALL_SIGN)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        // Add required entity
        ResourceBreaks resourceBreaks;
        if (TestUtil.findAll(em, ResourceBreaks.class).isEmpty()) {
            resourceBreaks = ResourceBreaksResourceIT.createEntity(em);
            em.persist(resourceBreaks);
            em.flush();
        } else {
            resourceBreaks = TestUtil.findAll(em, ResourceBreaks.class).get(0);
        }
        resource.setResourceBreak(resourceBreaks);
        return resource;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resource createUpdatedEntity(EntityManager em) {
        Resource resource = new Resource()
            .created(UPDATED_CREATED)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .callSign(UPDATED_CALL_SIGN)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        // Add required entity
        ResourceBreaks resourceBreaks;
        if (TestUtil.findAll(em, ResourceBreaks.class).isEmpty()) {
            resourceBreaks = ResourceBreaksResourceIT.createUpdatedEntity(em);
            em.persist(resourceBreaks);
            em.flush();
        } else {
            resourceBreaks = TestUtil.findAll(em, ResourceBreaks.class).get(0);
        }
        resource.setResourceBreak(resourceBreaks);
        return resource;
    }

    @BeforeEach
    public void initTest() {
        resource = createEntity(em);
    }

    @Test
    @Transactional
    void createResource() throws Exception {
        int databaseSizeBeforeCreate = resourceRepository.findAll().size();
        // Create the Resource
        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isCreated());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate + 1);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testResource.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testResource.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testResource.getCallSign()).isEqualTo(DEFAULT_CALL_SIGN);
        assertThat(testResource.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testResource.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void createResourceWithExistingId() throws Exception {
        // Create the Resource with an existing ID
        resource.setId(1L);

        int databaseSizeBeforeCreate = resourceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        // set the field null
        resource.setCreated(null);

        // Create the Resource, which fails.

        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        // set the field null
        resource.setType(null);

        // Create the Resource, which fails.

        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        // set the field null
        resource.setStatus(null);

        // Create the Resource, which fails.

        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCallSignIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        // set the field null
        resource.setCallSign(null);

        // Create the Resource, which fails.

        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        // set the field null
        resource.setLatitude(null);

        // Create the Resource, which fails.

        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        // set the field null
        resource.setLongitude(null);

        // Create the Resource, which fails.

        restResourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResources() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resource.getId().intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].callSign").value(hasItem(DEFAULT_CALL_SIGN)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }

    @Test
    @Transactional
    void getResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get the resource
        restResourceMockMvc
            .perform(get(ENTITY_API_URL_ID, resource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resource.getId().intValue()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.callSign").value(DEFAULT_CALL_SIGN))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }

    @Test
    @Transactional
    void getResourcesByIdFiltering() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        Long id = resource.getId();

        defaultResourceShouldBeFound("id.equals=" + id);
        defaultResourceShouldNotBeFound("id.notEquals=" + id);

        defaultResourceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultResourceShouldNotBeFound("id.greaterThan=" + id);

        defaultResourceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultResourceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created equals to DEFAULT_CREATED
        defaultResourceShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the resourceList where created equals to UPDATED_CREATED
        defaultResourceShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultResourceShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the resourceList where created equals to UPDATED_CREATED
        defaultResourceShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created is not null
        defaultResourceShouldBeFound("created.specified=true");

        // Get all the resourceList where created is null
        defaultResourceShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created is greater than or equal to DEFAULT_CREATED
        defaultResourceShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the resourceList where created is greater than or equal to UPDATED_CREATED
        defaultResourceShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created is less than or equal to DEFAULT_CREATED
        defaultResourceShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the resourceList where created is less than or equal to SMALLER_CREATED
        defaultResourceShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created is less than DEFAULT_CREATED
        defaultResourceShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the resourceList where created is less than UPDATED_CREATED
        defaultResourceShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllResourcesByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where created is greater than DEFAULT_CREATED
        defaultResourceShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the resourceList where created is greater than SMALLER_CREATED
        defaultResourceShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllResourcesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where type equals to DEFAULT_TYPE
        defaultResourceShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the resourceList where type equals to UPDATED_TYPE
        defaultResourceShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllResourcesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultResourceShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the resourceList where type equals to UPDATED_TYPE
        defaultResourceShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllResourcesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where type is not null
        defaultResourceShouldBeFound("type.specified=true");

        // Get all the resourceList where type is null
        defaultResourceShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where status equals to DEFAULT_STATUS
        defaultResourceShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the resourceList where status equals to UPDATED_STATUS
        defaultResourceShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllResourcesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultResourceShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the resourceList where status equals to UPDATED_STATUS
        defaultResourceShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllResourcesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where status is not null
        defaultResourceShouldBeFound("status.specified=true");

        // Get all the resourceList where status is null
        defaultResourceShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByCallSignIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where callSign equals to DEFAULT_CALL_SIGN
        defaultResourceShouldBeFound("callSign.equals=" + DEFAULT_CALL_SIGN);

        // Get all the resourceList where callSign equals to UPDATED_CALL_SIGN
        defaultResourceShouldNotBeFound("callSign.equals=" + UPDATED_CALL_SIGN);
    }

    @Test
    @Transactional
    void getAllResourcesByCallSignIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where callSign in DEFAULT_CALL_SIGN or UPDATED_CALL_SIGN
        defaultResourceShouldBeFound("callSign.in=" + DEFAULT_CALL_SIGN + "," + UPDATED_CALL_SIGN);

        // Get all the resourceList where callSign equals to UPDATED_CALL_SIGN
        defaultResourceShouldNotBeFound("callSign.in=" + UPDATED_CALL_SIGN);
    }

    @Test
    @Transactional
    void getAllResourcesByCallSignIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where callSign is not null
        defaultResourceShouldBeFound("callSign.specified=true");

        // Get all the resourceList where callSign is null
        defaultResourceShouldNotBeFound("callSign.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByCallSignContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where callSign contains DEFAULT_CALL_SIGN
        defaultResourceShouldBeFound("callSign.contains=" + DEFAULT_CALL_SIGN);

        // Get all the resourceList where callSign contains UPDATED_CALL_SIGN
        defaultResourceShouldNotBeFound("callSign.contains=" + UPDATED_CALL_SIGN);
    }

    @Test
    @Transactional
    void getAllResourcesByCallSignNotContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where callSign does not contain DEFAULT_CALL_SIGN
        defaultResourceShouldNotBeFound("callSign.doesNotContain=" + DEFAULT_CALL_SIGN);

        // Get all the resourceList where callSign does not contain UPDATED_CALL_SIGN
        defaultResourceShouldBeFound("callSign.doesNotContain=" + UPDATED_CALL_SIGN);
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude equals to DEFAULT_LATITUDE
        defaultResourceShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the resourceList where latitude equals to UPDATED_LATITUDE
        defaultResourceShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultResourceShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the resourceList where latitude equals to UPDATED_LATITUDE
        defaultResourceShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude is not null
        defaultResourceShouldBeFound("latitude.specified=true");

        // Get all the resourceList where latitude is null
        defaultResourceShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultResourceShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the resourceList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultResourceShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultResourceShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the resourceList where latitude is less than or equal to SMALLER_LATITUDE
        defaultResourceShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude is less than DEFAULT_LATITUDE
        defaultResourceShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the resourceList where latitude is less than UPDATED_LATITUDE
        defaultResourceShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where latitude is greater than DEFAULT_LATITUDE
        defaultResourceShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the resourceList where latitude is greater than SMALLER_LATITUDE
        defaultResourceShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude equals to DEFAULT_LONGITUDE
        defaultResourceShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the resourceList where longitude equals to UPDATED_LONGITUDE
        defaultResourceShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultResourceShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the resourceList where longitude equals to UPDATED_LONGITUDE
        defaultResourceShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude is not null
        defaultResourceShouldBeFound("longitude.specified=true");

        // Get all the resourceList where longitude is null
        defaultResourceShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultResourceShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the resourceList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultResourceShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultResourceShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the resourceList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultResourceShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude is less than DEFAULT_LONGITUDE
        defaultResourceShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the resourceList where longitude is less than UPDATED_LONGITUDE
        defaultResourceShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where longitude is greater than DEFAULT_LONGITUDE
        defaultResourceShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the resourceList where longitude is greater than SMALLER_LONGITUDE
        defaultResourceShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllResourcesByResourceBreakIsEqualToSomething() throws Exception {
        // Get already existing entity
        ResourceBreaks resourceBreak = resource.getResourceBreak();
        resourceRepository.saveAndFlush(resource);
        Long resourceBreakId = resourceBreak.getId();

        // Get all the resourceList where resourceBreak equals to resourceBreakId
        defaultResourceShouldBeFound("resourceBreakId.equals=" + resourceBreakId);

        // Get all the resourceList where resourceBreak equals to (resourceBreakId + 1)
        defaultResourceShouldNotBeFound("resourceBreakId.equals=" + (resourceBreakId + 1));
    }

    @Test
    @Transactional
    void getAllResourcesByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            resourceRepository.saveAndFlush(resource);
            event = EventResourceIT.createEntity(em);
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        resource.setEvent(event);
        resourceRepository.saveAndFlush(resource);
        Long eventId = event.getId();

        // Get all the resourceList where event equals to eventId
        defaultResourceShouldBeFound("eventId.equals=" + eventId);

        // Get all the resourceList where event equals to (eventId + 1)
        defaultResourceShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultResourceShouldBeFound(String filter) throws Exception {
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resource.getId().intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].callSign").value(hasItem(DEFAULT_CALL_SIGN)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));

        // Check, that the count call also returns 1
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultResourceShouldNotBeFound(String filter) throws Exception {
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingResource() throws Exception {
        // Get the resource
        restResourceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Update the resource
        Resource updatedResource = resourceRepository.findById(resource.getId()).get();
        // Disconnect from session so that the updates on updatedResource are not directly saved in db
        em.detach(updatedResource);
        updatedResource
            .created(UPDATED_CREATED)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .callSign(UPDATED_CALL_SIGN)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedResource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedResource))
            )
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testResource.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testResource.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testResource.getCallSign()).isEqualTo(UPDATED_CALL_SIGN);
        assertThat(testResource.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testResource.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void putNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resource.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resource))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resource))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResourceWithPatch() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Update the resource using partial update
        Resource partialUpdatedResource = new Resource();
        partialUpdatedResource.setId(resource.getId());

        partialUpdatedResource.type(UPDATED_TYPE).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResource.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResource))
            )
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testResource.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testResource.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testResource.getCallSign()).isEqualTo(DEFAULT_CALL_SIGN);
        assertThat(testResource.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testResource.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void fullUpdateResourceWithPatch() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Update the resource using partial update
        Resource partialUpdatedResource = new Resource();
        partialUpdatedResource.setId(resource.getId());

        partialUpdatedResource
            .created(UPDATED_CREATED)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .callSign(UPDATED_CALL_SIGN)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResource.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResource))
            )
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testResource.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testResource.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testResource.getCallSign()).isEqualTo(UPDATED_CALL_SIGN);
        assertThat(testResource.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testResource.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resource.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resource.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resource))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resource))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(resource)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeDelete = resourceRepository.findAll().size();

        // Delete the resource
        restResourceMockMvc
            .perform(delete(ENTITY_API_URL_ID, resource.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
