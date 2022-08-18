package com.rkey.returnkeyapp.web.rest;

import static com.rkey.returnkeyapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rkey.returnkeyapp.IntegrationTest;
import com.rkey.returnkeyapp.domain.Returns;
import com.rkey.returnkeyapp.domain.enumeration.ReturnStatus;
import com.rkey.returnkeyapp.repository.ReturnsRepository;
import com.rkey.returnkeyapp.service.dto.ReturnsDTO;
import com.rkey.returnkeyapp.service.mapper.ReturnsMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ReturnsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReturnsResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final ReturnStatus DEFAULT_RETURN_STATUS = ReturnStatus.AWAITING_APPROVAL;
    private static final ReturnStatus UPDATED_RETURN_STATUS = ReturnStatus.COMPLETE;

    private static final String ENTITY_API_URL = "/api/returns";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReturnsRepository returnsRepository;

    @Autowired
    private ReturnsMapper returnsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnsMockMvc;

    private Returns returns;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Returns createEntity(EntityManager em) {
        Returns returns = new Returns().totalAmount(DEFAULT_TOTAL_AMOUNT).returnStatus(DEFAULT_RETURN_STATUS);
        return returns;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Returns createUpdatedEntity(EntityManager em) {
        Returns returns = new Returns().totalAmount(UPDATED_TOTAL_AMOUNT).returnStatus(UPDATED_RETURN_STATUS);
        return returns;
    }

    @BeforeEach
    public void initTest() {
        returns = createEntity(em);
    }

    @Test
    @Transactional
    void createReturns() throws Exception {
        int databaseSizeBeforeCreate = returnsRepository.findAll().size();
        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);
        restReturnsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(returnsDTO)))
            .andExpect(status().isCreated());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeCreate + 1);
        Returns testReturns = returnsList.get(returnsList.size() - 1);
        assertThat(testReturns.getTotalAmount()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testReturns.getReturnStatus()).isEqualTo(DEFAULT_RETURN_STATUS);
    }

    @Test
    @Transactional
    void createReturnsWithExistingId() throws Exception {
        // Create the Returns with an existing ID
        returns.setId(1L);
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        int databaseSizeBeforeCreate = returnsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(returnsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReturns() throws Exception {
        // Initialize the database
        returnsRepository.saveAndFlush(returns);

        // Get all the returnsList
        restReturnsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returns.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].returnStatus").value(hasItem(DEFAULT_RETURN_STATUS.toString())));
    }

    @Test
    @Transactional
    void getReturns() throws Exception {
        // Initialize the database
        returnsRepository.saveAndFlush(returns);

        // Get the returns
        restReturnsMockMvc
            .perform(get(ENTITY_API_URL_ID, returns.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returns.getId().intValue()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.returnStatus").value(DEFAULT_RETURN_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingReturns() throws Exception {
        // Get the returns
        restReturnsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReturns() throws Exception {
        // Initialize the database
        returnsRepository.saveAndFlush(returns);

        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();

        // Update the returns
        Returns updatedReturns = returnsRepository.findById(returns.getId()).get();
        // Disconnect from session so that the updates on updatedReturns are not directly saved in db
        em.detach(updatedReturns);
        updatedReturns.totalAmount(UPDATED_TOTAL_AMOUNT).returnStatus(UPDATED_RETURN_STATUS);
        ReturnsDTO returnsDTO = returnsMapper.toDto(updatedReturns);

        restReturnsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(returnsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
        Returns testReturns = returnsList.get(returnsList.size() - 1);
        assertThat(testReturns.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testReturns.getReturnStatus()).isEqualTo(UPDATED_RETURN_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingReturns() throws Exception {
        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();
        returns.setId(count.incrementAndGet());

        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(returnsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturns() throws Exception {
        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();
        returns.setId(count.incrementAndGet());

        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(returnsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturns() throws Exception {
        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();
        returns.setId(count.incrementAndGet());

        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(returnsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnsWithPatch() throws Exception {
        // Initialize the database
        returnsRepository.saveAndFlush(returns);

        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();

        // Update the returns using partial update
        Returns partialUpdatedReturns = new Returns();
        partialUpdatedReturns.setId(returns.getId());

        partialUpdatedReturns.totalAmount(UPDATED_TOTAL_AMOUNT);

        restReturnsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturns.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReturns))
            )
            .andExpect(status().isOk());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
        Returns testReturns = returnsList.get(returnsList.size() - 1);
        assertThat(testReturns.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testReturns.getReturnStatus()).isEqualTo(DEFAULT_RETURN_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateReturnsWithPatch() throws Exception {
        // Initialize the database
        returnsRepository.saveAndFlush(returns);

        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();

        // Update the returns using partial update
        Returns partialUpdatedReturns = new Returns();
        partialUpdatedReturns.setId(returns.getId());

        partialUpdatedReturns.totalAmount(UPDATED_TOTAL_AMOUNT).returnStatus(UPDATED_RETURN_STATUS);

        restReturnsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturns.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReturns))
            )
            .andExpect(status().isOk());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
        Returns testReturns = returnsList.get(returnsList.size() - 1);
        assertThat(testReturns.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testReturns.getReturnStatus()).isEqualTo(UPDATED_RETURN_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingReturns() throws Exception {
        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();
        returns.setId(count.incrementAndGet());

        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(returnsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturns() throws Exception {
        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();
        returns.setId(count.incrementAndGet());

        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(returnsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturns() throws Exception {
        int databaseSizeBeforeUpdate = returnsRepository.findAll().size();
        returns.setId(count.incrementAndGet());

        // Create the Returns
        ReturnsDTO returnsDTO = returnsMapper.toDto(returns);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(returnsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Returns in the database
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturns() throws Exception {
        // Initialize the database
        returnsRepository.saveAndFlush(returns);

        int databaseSizeBeforeDelete = returnsRepository.findAll().size();

        // Delete the returns
        restReturnsMockMvc
            .perform(delete(ENTITY_API_URL_ID, returns.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Returns> returnsList = returnsRepository.findAll();
        assertThat(returnsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
