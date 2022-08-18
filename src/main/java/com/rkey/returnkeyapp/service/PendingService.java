package com.rkey.returnkeyapp.service;

import com.rkey.returnkeyapp.domain.Pending;
import com.rkey.returnkeyapp.repository.PendingRepository;
import com.rkey.returnkeyapp.service.dto.PendingDTO;
import com.rkey.returnkeyapp.service.mapper.PendingMapper;
import com.rkey.returnkeyapp.utils.StrUtils;
import com.rkey.returnkeyapp.web.rest.errors.InvalidFlowException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Pending}.
 */
@Service
@Transactional
public class PendingService {

    private final Logger log = LoggerFactory.getLogger(PendingService.class);

    private final PendingRepository pendingRepository;

    private final PendingMapper pendingMapper;

    public PendingService(PendingRepository pendingRepository, PendingMapper pendingMapper) {
        this.pendingRepository = pendingRepository;
        this.pendingMapper = pendingMapper;
    }

    /**
     * save a pending
     * @param orderId (String)
     * @param emailAddress (String)
     * @return generated token
     */
    public PendingDTO save(String orderId, String emailAddress) {
        log.debug("Request to save Pending : {}, {}", orderId, emailAddress);
        if (isPendingExist(orderId, emailAddress)) {
            throw new InvalidFlowException("Cannot return the item twice", "Pending");
        }
        if (!validateReturn(orderId, emailAddress)) {
            throw new InvalidFlowException("Invalid orderId and emailAddress", "Pending");
        }
        Pending pending = pendingMapper.toEntity(new PendingDTO(StrUtils.random(8), orderId.concat("|").concat(emailAddress)));
        pending = pendingRepository.save(pending);
        return pendingMapper.toDto(pending);
    }

    private boolean validateReturn(String orderId, String emailAddress) {
        log.debug("Request to validateReturn: {}, {}", orderId, emailAddress);
        try (BufferedReader br = new BufferedReader(new FileReader("orders.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (StrUtils.isContains(line, orderId) && StrUtils.isContains(line, emailAddress)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isPendingExist(String orderId, String emailAddress) {
        log.debug("Request to validate the existence of Pending: {}, {}", orderId, emailAddress);
        return pendingRepository.findPendingByTokenReference(orderId.concat("|").concat(emailAddress)).isPresent();
    }
}
