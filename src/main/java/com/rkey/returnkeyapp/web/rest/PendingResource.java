package com.rkey.returnkeyapp.web.rest;

import com.rkey.returnkeyapp.repository.PendingRepository;
import com.rkey.returnkeyapp.service.PendingService;
import com.rkey.returnkeyapp.service.dto.PendingDTO;
import com.rkey.returnkeyapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rkey.returnkeyapp.domain.Pending}.
 */
@RestController
@RequestMapping("/api")
public class PendingResource {

    private final Logger log = LoggerFactory.getLogger(PendingResource.class);

    private static final String ENTITY_NAME = "pending";

    private final PendingService pendingService;

    private final PendingRepository pendingRepository;

    public PendingResource(PendingService pendingService, PendingRepository pendingRepository) {
        this.pendingService = pendingService;
        this.pendingRepository = pendingRepository;
    }

    /**
     * {@code POST  /pending/return} : Create a new pending.
     *
     * @param orderId the first parameter to create.
     * @param emailAddress the second parameter to create.
     * @return generated token
     */
    @PostMapping("/pending/returns")
    public ResponseEntity<String> createPending(@RequestParam String orderId, @RequestParam String emailAddress) {
        log.debug("REST request to save Pending : {}, {}", orderId, emailAddress);
        if (StringUtils.isBlank(orderId) && StringUtils.isBlank(emailAddress)) {
            throw new BadRequestAlertException("orderId and emailAddress are mandatory", ENTITY_NAME, "misingparams");
        }
        PendingDTO result = pendingService.save(orderId, emailAddress);
        return ResponseEntity.ok(result.getGeneratedToken());
    }
}
