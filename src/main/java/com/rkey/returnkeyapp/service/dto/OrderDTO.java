package com.rkey.returnkeyapp.service.dto;

import com.rkey.returnkeyapp.domain.enumeration.QcStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.rkey.returnkeyapp.domain.Order} entity.
 */
public class OrderDTO implements Serializable {

    private Long id;

    private String orderId;

    private String emailAddress;

    private String sku;

    @Min(value = 0L)
    private Long quantity;

    @DecimalMin(value = "0")
    private BigDecimal price;

    private String itemName;

    private QcStatus qcStatus;

    private ReturnsDTO returns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public QcStatus getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(QcStatus qcStatus) {
        this.qcStatus = qcStatus;
    }

    public ReturnsDTO getReturns() {
        return returns;
    }

    public void setReturns(ReturnsDTO returns) {
        this.returns = returns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", orderId='" + getOrderId() + "'" +
            ", emailAddress='" + getEmailAddress() + "'" +
            ", sku='" + getSku() + "'" +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            ", itemName='" + getItemName() + "'" +
            ", qcStatus='" + getQcStatus() + "'" +
            ", returns=" + getReturns() +
            "}";
    }
}
