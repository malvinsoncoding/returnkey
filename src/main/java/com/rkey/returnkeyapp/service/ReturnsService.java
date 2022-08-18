package com.rkey.returnkeyapp.service;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.rkey.returnkeyapp.domain.Order;
import com.rkey.returnkeyapp.domain.Returns;
import com.rkey.returnkeyapp.domain.enumeration.QcStatus;
import com.rkey.returnkeyapp.domain.enumeration.ReturnStatus;
import com.rkey.returnkeyapp.repository.OrderRepository;
import com.rkey.returnkeyapp.repository.PendingRepository;
import com.rkey.returnkeyapp.repository.ReturnsRepository;
import com.rkey.returnkeyapp.service.dto.OrderDTO;
import com.rkey.returnkeyapp.service.dto.PendingDTO;
import com.rkey.returnkeyapp.service.dto.ReturnsDTO;
import com.rkey.returnkeyapp.service.mapper.OrderMapper;
import com.rkey.returnkeyapp.service.mapper.PendingMapper;
import com.rkey.returnkeyapp.service.mapper.ReturnsMapper;
import com.rkey.returnkeyapp.utils.StrUtils;
import com.rkey.returnkeyapp.web.rest.errors.InvalidFlowException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Returns}.
 */
@Service
@Transactional
public class ReturnsService {

    private static final String COMMA_DELIMITER = ",";
    private static final String PIPE_DELIMITER = "\\|";

    private final Logger log = LoggerFactory.getLogger(ReturnsService.class);

    private final ReturnsRepository returnsRepository;

    private final PendingRepository pendingRepository;

    private final OrderRepository orderRepository;

    private final ReturnsMapper returnsMapper;

    private final PendingMapper pendingMapper;

    private final OrderMapper orderMapper;

    public ReturnsService(
        ReturnsRepository returnsRepository,
        PendingRepository pendingRepository,
        OrderRepository orderRepository,
        ReturnsMapper returnsMapper,
        PendingMapper pendingMapper,
        OrderMapper orderMapper
    ) {
        this.returnsRepository = returnsRepository;
        this.pendingRepository = pendingRepository;
        this.orderRepository = orderRepository;
        this.returnsMapper = returnsMapper;
        this.pendingMapper = pendingMapper;
        this.orderMapper = orderMapper;
    }

    /**
     * Save a returns.
     *
     * @param generatedToken to save.
     * @return the persisted entity.
     */
    public ReturnsDTO save(String generatedToken) {
        log.debug("Request to save Returns : {}", generatedToken);
        PendingDTO pendingDTO = pendingMapper.toDto(pendingRepository.findPendingByGeneratedToken(generatedToken));
        if (ObjectUtils.isNotEmpty(pendingDTO)) {
            if (pendingDTO.getTokenReference().contains("|") && pendingDTO.getTokenReference().split("|").length > 0) {
                String orderId = pendingDTO.getTokenReference().split(PIPE_DELIMITER)[0];
                String emailAddress = pendingDTO.getTokenReference().split(PIPE_DELIMITER)[1];
                try (BufferedReader br = new BufferedReader(new FileReader("orders.csv"))) {
                    ReturnsDTO returnsDTO = new ReturnsDTO();
                    returnsDTO.setPending(pendingDTO);
                    returnsDTO.setReturnStatus(ReturnStatus.AWAITING_APPROVAL);
                    returnsDTO.setTotalAmount(BigDecimal.ZERO);
                    Returns returns = returnsMapper.toEntity(returnsDTO);
                    returns = returnsRepository.save(returns);
                    String line;
                    List<OrderDTO> orderDTOList = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
                        if (StrUtils.isContains(line, orderId) && StrUtils.isContains(line, emailAddress)) {
                            String values[] = line.split(COMMA_DELIMITER);
                            OrderDTO orderDTO = new OrderDTO();
                            orderDTO.setOrderId(values[0]);
                            orderDTO.setEmailAddress(values[1]);
                            orderDTO.setSku(values[2]);
                            orderDTO.setQuantity(Long.parseLong(values[3]));
                            orderDTO.setPrice(new BigDecimal(values[4]));
                            orderDTO.setItemName(values[5]);
                            orderDTO.setReturns(returnsMapper.toDto(returns));
                            orderDTOList.add(orderDTO);
                            returns.setTotalAmount(
                                returns.getTotalAmount().add((orderDTO.getPrice().multiply(new BigDecimal(orderDTO.getQuantity()))))
                            );
                        }
                    }
                    if (orderDTOList.isEmpty()) {
                        throw new InvalidFlowException("OrderList must not be null", "Order");
                    }
                    returns.setOrders(orderRepository.saveAll(orderMapper.toEntity(orderDTOList)).stream().collect(Collectors.toSet()));
                    return returnsMapper.toDto(returns);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            throw new InvalidFlowException("Missing parameter information", "Pending");
        }
        throw new InvalidFlowException("Invalid token", "Pending");
    }

    /**
     * Partially update a returns.
     *
     * @param returnsId the id of the returnsDTO to save.
     * @param itemId the order to update.
     * @param qcStatus the status of order.
     * @return the persisted entity.
     */
    public OrderDTO partialUpdate(Long returnsId, Long itemId, QcStatus qcStatus) {
        log.debug("Request to partially update Returns : {}, {}, {}", returnsId, itemId, qcStatus);
        return orderMapper.toDto(
            orderRepository
                .findByReturnsIdAndId(returnsId, itemId)
                .map(order -> {
                    if (order.getQcStatus() != null) {
                        throw new InvalidFlowException("This item has already " + order.getQcStatus().name(), "Order");
                    }
                    if (QcStatus.REJECTED.equals(qcStatus)) {
                        BigDecimal deductedAmount = order
                            .getReturns()
                            .getTotalAmount()
                            .subtract((order.getPrice().multiply(new BigDecimal(order.getQuantity()))));
                        order.getReturns().setTotalAmount(deductedAmount);
                    }
                    order.setQcStatus(qcStatus);
                    updateReturnStatus(returnsId);
                    return order;
                })
                .get()
        );
    }

    /**
     * Get all the returns.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ReturnsDTO> findAll() {
        log.debug("Request to get all Returns");
        return returnsRepository.findAll().stream().map(returnsMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one returns by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReturnsDTO> findOne(Long id) {
        log.debug("Request to get Returns : {}", id);
        return returnsRepository
            .findById(id)
            .map(returnsMapper::toDto)
            .map(returnsDTO -> {
                if (ReturnStatus.COMPLETE.equals(returnsDTO.getReturnStatus())) {
                    returnsDTO.getOrders().removeIf(orderDTO -> QcStatus.REJECTED.equals(orderDTO.getQcStatus()));
                }
                return returnsDTO;
            });
    }

    /**
     * Delete the returns by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Returns : {}", id);
        returnsRepository.deleteById(id);
    }

    private void updateReturnStatus(Long returnsId) {
        log.debug("Request to updateReturnStatus: {}", returnsId);
        Returns returns = returnsRepository.findById(returnsId).orElseThrow();
        List<Order> orderList = returns.getOrders().stream().filter(order -> order.getQcStatus() == null).collect(Collectors.toList());
        if (orderList.isEmpty()) {
            returns.setReturnStatus(ReturnStatus.COMPLETE);
        }
    }
}
