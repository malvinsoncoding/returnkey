package com.rkey.returnkeyapp.service.dto;

import com.rkey.returnkeyapp.domain.enumeration.ReturnStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.rkey.returnkeyapp.domain.Returns} entity.
 */
public class ReturnsDTO implements Serializable {

    private Long id;

    private BigDecimal totalAmount;

    private ReturnStatus returnStatus;

    private Set<OrderDTO> orders = new HashSet<>();

    private PendingDTO pending;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public Set<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderDTO> orders) {
        this.orders = orders;
    }

    public PendingDTO getPending() {
        return pending;
    }

    public void setPending(PendingDTO pending) {
        this.pending = pending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnsDTO)) {
            return false;
        }

        ReturnsDTO returnsDTO = (ReturnsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "ReturnsDTO{" +
            "id=" +
            id +
            ", totalAmount=" +
            totalAmount +
            ", returnStatus=" +
            returnStatus +
            ", orders=" +
            orders +
            ", pending=" +
            pending +
            '}'
        );
    }
}
