package com.rkey.returnkeyapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rkey.returnkeyapp.IntegrationTest;
import com.rkey.returnkeyapp.domain.Pending;
import com.rkey.returnkeyapp.repository.PendingRepository;
import com.rkey.returnkeyapp.service.dto.PendingDTO;
import com.rkey.returnkeyapp.service.mapper.PendingMapper;
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

/**
 * Integration tests for the {@link PendingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PendingResourceIT {

    private static final String DEFAULT_GENERATED_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_GENERATED_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pending";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PendingRepository pendingRepository;

    @Autowired
    private PendingMapper pendingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPendingMockMvc;

    private Pending pending;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pending createEntity(EntityManager em) {
        Pending pending = new Pending().generatedToken(DEFAULT_GENERATED_TOKEN).tokenReference(DEFAULT_TOKEN_REFERENCE);
        return pending;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pending createUpdatedEntity(EntityManager em) {
        Pending pending = new Pending().generatedToken(UPDATED_GENERATED_TOKEN).tokenReference(UPDATED_TOKEN_REFERENCE);
        return pending;
    }

    @BeforeEach
    public void initTest() {
        pending = createEntity(em);
    }

    @Test
    @Transactional
    void createPending() throws Exception {
        int databaseSizeBeforeCreate = pendingRepository.findAll().size();
        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);
        restPendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pendingDTO)))
            .andExpect(status().isCreated());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeCreate + 1);
        Pending testPending = pendingList.get(pendingList.size() - 1);
        assertThat(testPending.getGeneratedToken()).isEqualTo(DEFAULT_GENERATED_TOKEN);
        assertThat(testPending.getTokenReference()).isEqualTo(DEFAULT_TOKEN_REFERENCE);
    }

    @Test
    @Transactional
    void createPendingWithExistingId() throws Exception {
        // Create the Pending with an existing ID
        pending.setId(1L);
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        int databaseSizeBeforeCreate = pendingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pendingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPendings() throws Exception {
        // Initialize the database
        pendingRepository.saveAndFlush(pending);

        // Get all the pendingList
        restPendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pending.getId().intValue())))
            .andExpect(jsonPath("$.[*].generatedToken").value(hasItem(DEFAULT_GENERATED_TOKEN)))
            .andExpect(jsonPath("$.[*].tokenReference").value(hasItem(DEFAULT_TOKEN_REFERENCE)));
    }

    @Test
    @Transactional
    void getPending() throws Exception {
        // Initialize the database
        pendingRepository.saveAndFlush(pending);

        // Get the pending
        restPendingMockMvc
            .perform(get(ENTITY_API_URL_ID, pending.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pending.getId().intValue()))
            .andExpect(jsonPath("$.generatedToken").value(DEFAULT_GENERATED_TOKEN))
            .andExpect(jsonPath("$.tokenReference").value(DEFAULT_TOKEN_REFERENCE));
    }

    @Test
    @Transactional
    void getNonExistingPending() throws Exception {
        // Get the pending
        restPendingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPending() throws Exception {
        // Initialize the database
        pendingRepository.saveAndFlush(pending);

        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();

        // Update the pending
        Pending updatedPending = pendingRepository.findById(pending.getId()).get();
        // Disconnect from session so that the updates on updatedPending are not directly saved in db
        em.detach(updatedPending);
        updatedPending.generatedToken(UPDATED_GENERATED_TOKEN).tokenReference(UPDATED_TOKEN_REFERENCE);
        PendingDTO pendingDTO = pendingMapper.toDto(updatedPending);

        restPendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pendingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pendingDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
        Pending testPending = pendingList.get(pendingList.size() - 1);
        assertThat(testPending.getGeneratedToken()).isEqualTo(UPDATED_GENERATED_TOKEN);
        assertThat(testPending.getTokenReference()).isEqualTo(UPDATED_TOKEN_REFERENCE);
    }

    @Test
    @Transactional
    void putNonExistingPending() throws Exception {
        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();
        pending.setId(count.incrementAndGet());

        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pendingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pendingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPending() throws Exception {
        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();
        pending.setId(count.incrementAndGet());

        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pendingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPending() throws Exception {
        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();
        pending.setId(count.incrementAndGet());

        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPendingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pendingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePendingWithPatch() throws Exception {
        // Initialize the database
        pendingRepository.saveAndFlush(pending);

        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();

        // Update the pending using partial update
        Pending partialUpdatedPending = new Pending();
        partialUpdatedPending.setId(pending.getId());

        partialUpdatedPending.tokenReference(UPDATED_TOKEN_REFERENCE);

        restPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPending))
            )
            .andExpect(status().isOk());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
        Pending testPending = pendingList.get(pendingList.size() - 1);
        assertThat(testPending.getGeneratedToken()).isEqualTo(DEFAULT_GENERATED_TOKEN);
        assertThat(testPending.getTokenReference()).isEqualTo(UPDATED_TOKEN_REFERENCE);
    }

    @Test
    @Transactional
    void fullUpdatePendingWithPatch() throws Exception {
        // Initialize the database
        pendingRepository.saveAndFlush(pending);

        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();

        // Update the pending using partial update
        Pending partialUpdatedPending = new Pending();
        partialUpdatedPending.setId(pending.getId());

        partialUpdatedPending.generatedToken(UPDATED_GENERATED_TOKEN).tokenReference(UPDATED_TOKEN_REFERENCE);

        restPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPending))
            )
            .andExpect(status().isOk());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
        Pending testPending = pendingList.get(pendingList.size() - 1);
        assertThat(testPending.getGeneratedToken()).isEqualTo(UPDATED_GENERATED_TOKEN);
        assertThat(testPending.getTokenReference()).isEqualTo(UPDATED_TOKEN_REFERENCE);
    }

    @Test
    @Transactional
    void patchNonExistingPending() throws Exception {
        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();
        pending.setId(count.incrementAndGet());

        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pendingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pendingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPending() throws Exception {
        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();
        pending.setId(count.incrementAndGet());

        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pendingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPending() throws Exception {
        int databaseSizeBeforeUpdate = pendingRepository.findAll().size();
        pending.setId(count.incrementAndGet());

        // Create the Pending
        PendingDTO pendingDTO = pendingMapper.toDto(pending);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPendingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pendingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pending in the database
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePending() throws Exception {
        // Initialize the database
        pendingRepository.saveAndFlush(pending);

        int databaseSizeBeforeDelete = pendingRepository.findAll().size();

        // Delete the pending
        restPendingMockMvc
            .perform(delete(ENTITY_API_URL_ID, pending.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pending> pendingList = pendingRepository.findAll();
        assertThat(pendingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
