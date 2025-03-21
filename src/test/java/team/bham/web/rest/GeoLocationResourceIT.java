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
import team.bham.domain.GeoLocation;
import team.bham.repository.GeoLocationRepository;
import team.bham.service.criteria.GeoLocationCriteria;

/**
 * Integration tests for the {@link GeoLocationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GeoLocationResourceIT {

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;
    private static final Float SMALLER_LATITUDE = 1F - 1F;

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;
    private static final Float SMALLER_LONGITUDE = 1F - 1F;

    private static final String ENTITY_API_URL = "/api/geo-locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGeoLocationMockMvc;

    private GeoLocation geoLocation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GeoLocation createEntity(EntityManager em) {
        GeoLocation geoLocation = new GeoLocation().latitude(DEFAULT_LATITUDE).longitude(DEFAULT_LONGITUDE);
        return geoLocation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GeoLocation createUpdatedEntity(EntityManager em) {
        GeoLocation geoLocation = new GeoLocation().latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);
        return geoLocation;
    }

    @BeforeEach
    public void initTest() {
        geoLocation = createEntity(em);
    }

    @Test
    @Transactional
    void createGeoLocation() throws Exception {
        int databaseSizeBeforeCreate = geoLocationRepository.findAll().size();
        // Create the GeoLocation
        restGeoLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(geoLocation)))
            .andExpect(status().isCreated());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeCreate + 1);
        GeoLocation testGeoLocation = geoLocationList.get(geoLocationList.size() - 1);
        assertThat(testGeoLocation.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testGeoLocation.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void createGeoLocationWithExistingId() throws Exception {
        // Create the GeoLocation with an existing ID
        geoLocation.setId(1L);

        int databaseSizeBeforeCreate = geoLocationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGeoLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(geoLocation)))
            .andExpect(status().isBadRequest());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = geoLocationRepository.findAll().size();
        // set the field null
        geoLocation.setLatitude(null);

        // Create the GeoLocation, which fails.

        restGeoLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(geoLocation)))
            .andExpect(status().isBadRequest());

        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = geoLocationRepository.findAll().size();
        // set the field null
        geoLocation.setLongitude(null);

        // Create the GeoLocation, which fails.

        restGeoLocationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(geoLocation)))
            .andExpect(status().isBadRequest());

        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGeoLocations() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList
        restGeoLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(geoLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }

    @Test
    @Transactional
    void getGeoLocation() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get the geoLocation
        restGeoLocationMockMvc
            .perform(get(ENTITY_API_URL_ID, geoLocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(geoLocation.getId().intValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }

    @Test
    @Transactional
    void getGeoLocationsByIdFiltering() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        Long id = geoLocation.getId();

        defaultGeoLocationShouldBeFound("id.equals=" + id);
        defaultGeoLocationShouldNotBeFound("id.notEquals=" + id);

        defaultGeoLocationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultGeoLocationShouldNotBeFound("id.greaterThan=" + id);

        defaultGeoLocationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultGeoLocationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude equals to DEFAULT_LATITUDE
        defaultGeoLocationShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the geoLocationList where latitude equals to UPDATED_LATITUDE
        defaultGeoLocationShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultGeoLocationShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the geoLocationList where latitude equals to UPDATED_LATITUDE
        defaultGeoLocationShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude is not null
        defaultGeoLocationShouldBeFound("latitude.specified=true");

        // Get all the geoLocationList where latitude is null
        defaultGeoLocationShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultGeoLocationShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the geoLocationList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultGeoLocationShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultGeoLocationShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the geoLocationList where latitude is less than or equal to SMALLER_LATITUDE
        defaultGeoLocationShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude is less than DEFAULT_LATITUDE
        defaultGeoLocationShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the geoLocationList where latitude is less than UPDATED_LATITUDE
        defaultGeoLocationShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where latitude is greater than DEFAULT_LATITUDE
        defaultGeoLocationShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the geoLocationList where latitude is greater than SMALLER_LATITUDE
        defaultGeoLocationShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude equals to DEFAULT_LONGITUDE
        defaultGeoLocationShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the geoLocationList where longitude equals to UPDATED_LONGITUDE
        defaultGeoLocationShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultGeoLocationShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the geoLocationList where longitude equals to UPDATED_LONGITUDE
        defaultGeoLocationShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude is not null
        defaultGeoLocationShouldBeFound("longitude.specified=true");

        // Get all the geoLocationList where longitude is null
        defaultGeoLocationShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultGeoLocationShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the geoLocationList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultGeoLocationShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultGeoLocationShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the geoLocationList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultGeoLocationShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude is less than DEFAULT_LONGITUDE
        defaultGeoLocationShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the geoLocationList where longitude is less than UPDATED_LONGITUDE
        defaultGeoLocationShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllGeoLocationsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        // Get all the geoLocationList where longitude is greater than DEFAULT_LONGITUDE
        defaultGeoLocationShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the geoLocationList where longitude is greater than SMALLER_LONGITUDE
        defaultGeoLocationShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGeoLocationShouldBeFound(String filter) throws Exception {
        restGeoLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(geoLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));

        // Check, that the count call also returns 1
        restGeoLocationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGeoLocationShouldNotBeFound(String filter) throws Exception {
        restGeoLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGeoLocationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGeoLocation() throws Exception {
        // Get the geoLocation
        restGeoLocationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGeoLocation() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();

        // Update the geoLocation
        GeoLocation updatedGeoLocation = geoLocationRepository.findById(geoLocation.getId()).get();
        // Disconnect from session so that the updates on updatedGeoLocation are not directly saved in db
        em.detach(updatedGeoLocation);
        updatedGeoLocation.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restGeoLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGeoLocation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGeoLocation))
            )
            .andExpect(status().isOk());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
        GeoLocation testGeoLocation = geoLocationList.get(geoLocationList.size() - 1);
        assertThat(testGeoLocation.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testGeoLocation.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void putNonExistingGeoLocation() throws Exception {
        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();
        geoLocation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGeoLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, geoLocation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(geoLocation))
            )
            .andExpect(status().isBadRequest());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGeoLocation() throws Exception {
        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();
        geoLocation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeoLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(geoLocation))
            )
            .andExpect(status().isBadRequest());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGeoLocation() throws Exception {
        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();
        geoLocation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeoLocationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(geoLocation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGeoLocationWithPatch() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();

        // Update the geoLocation using partial update
        GeoLocation partialUpdatedGeoLocation = new GeoLocation();
        partialUpdatedGeoLocation.setId(geoLocation.getId());

        partialUpdatedGeoLocation.latitude(UPDATED_LATITUDE);

        restGeoLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGeoLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGeoLocation))
            )
            .andExpect(status().isOk());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
        GeoLocation testGeoLocation = geoLocationList.get(geoLocationList.size() - 1);
        assertThat(testGeoLocation.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testGeoLocation.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void fullUpdateGeoLocationWithPatch() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();

        // Update the geoLocation using partial update
        GeoLocation partialUpdatedGeoLocation = new GeoLocation();
        partialUpdatedGeoLocation.setId(geoLocation.getId());

        partialUpdatedGeoLocation.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restGeoLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGeoLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGeoLocation))
            )
            .andExpect(status().isOk());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
        GeoLocation testGeoLocation = geoLocationList.get(geoLocationList.size() - 1);
        assertThat(testGeoLocation.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testGeoLocation.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingGeoLocation() throws Exception {
        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();
        geoLocation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGeoLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, geoLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(geoLocation))
            )
            .andExpect(status().isBadRequest());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGeoLocation() throws Exception {
        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();
        geoLocation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeoLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(geoLocation))
            )
            .andExpect(status().isBadRequest());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGeoLocation() throws Exception {
        int databaseSizeBeforeUpdate = geoLocationRepository.findAll().size();
        geoLocation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeoLocationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(geoLocation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GeoLocation in the database
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGeoLocation() throws Exception {
        // Initialize the database
        geoLocationRepository.saveAndFlush(geoLocation);

        int databaseSizeBeforeDelete = geoLocationRepository.findAll().size();

        // Delete the geoLocation
        restGeoLocationMockMvc
            .perform(delete(ENTITY_API_URL_ID, geoLocation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GeoLocation> geoLocationList = geoLocationRepository.findAll();
        assertThat(geoLocationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
