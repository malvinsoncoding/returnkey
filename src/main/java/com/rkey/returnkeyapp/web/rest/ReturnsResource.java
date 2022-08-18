package com.rkey.returnkeyapp.web.rest;

import com.rkey.returnkeyapp.domain.enumeration.QcStatus;
import com.rkey.returnkeyapp.repository.ReturnsRepository;
import com.rkey.returnkeyapp.service.ReturnsService;
import com.rkey.returnkeyapp.service.dto.OrderDTO;
import com.rkey.returnkeyapp.service.dto.ReturnsDTO;
import com.rkey.returnkeyapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rkey.returnkeyapp.domain.Returns}.
 */
@RestController
@RequestMapping("/api")
public class ReturnsResource {

    private final Logger log = LoggerFactory.getLogger(ReturnsResource.class);

    private static final String ENTITY_NAME = "returns";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnsService returnsService;

    private final ReturnsRepository returnsRepository;

    public ReturnsResource(ReturnsService returnsService, ReturnsRepository returnsRepository) {
        this.returnsService = returnsService;
        this.returnsRepository = returnsRepository;
    }

    /**
     * {@code POST  /returns} : Create a new returns.
     *
     * @param generatedToken the param from API pending/returns to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnsDTO, or with status {@code 400 (Bad Request)} if the returns has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/returns")
    public ResponseEntity<ReturnsDTO> createReturns(@RequestParam String generatedToken) throws URISyntaxException {
        log.debug("REST request to save Returns : {}", generatedToken);
        if (StringUtils.isBlank(generatedToken)) {
            throw new BadRequestAlertException("generatedToken is mandatory", ENTITY_NAME, "misingtoken");
        }
        ReturnsDTO result = returnsService.save(generatedToken);
        return ResponseEntity
            .created(new URI("/api/returns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /returns/:id} : Updates an existing returns.
     *
     * @param id the id of the returnsDTO to save.
     * @param itemId the order to update.
     * @param qcStatus the status of order.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnsDTO,
     * or with status {@code 400 (Bad Request)} if the returnsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/returns/{id}/items/{itemId}/qc/status")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @PathVariable Long itemId, @RequestParam QcStatus qcStatus) {
        log.debug("REST request to update Order qcStatus : {}, {}, {}", id, itemId, qcStatus);
        if (id == null && itemId == null && qcStatus == null) {
            throw new BadRequestAlertException("Invalid parameters", ENTITY_NAME, "paramsnull");
        }
        return ResponseEntity.ok(returnsService.partialUpdate(id, itemId, qcStatus));
    }

    /**
     * {@code GET  /returns/:id} : get the "id" returns.
     *
     * @param id the id of the returnsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/returns/{id}")
    public ResponseEntity<ReturnsDTO> getReturns(@PathVariable Long id) {
        log.debug("REST request to get Returns : {}", id);
        Optional<ReturnsDTO> returnsDTO = returnsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnsDTO);
    }
}
