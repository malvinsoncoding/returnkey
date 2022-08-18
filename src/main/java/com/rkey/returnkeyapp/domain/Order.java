package com.rkey.returnkeyapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rkey.returnkeyapp.domain.enumeration.QcStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Order extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "sku", unique = true)
    private String sku;

    @Min(value = 0L)
    @Column(name = "quantity")
    private Long quantity;

    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    @Column(name = "item_name")
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "qc_status")
    private QcStatus qcStatus;

    @ManyToOne
    @JsonIgnoreProperties(value = { "orders", "pending" }, allowSetters = true)
    private Returns returns;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public Order orderId(String orderId) {
        this.setOrderId(orderId);
        return this;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Order emailAddress(String emailAddress) {
        this.setEmailAddress(emailAddress);
        return this;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSku() {
        return this.sku;
    }

    public Order sku(String sku) {
        this.setSku(sku);
        return this;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public Order quantity(Long quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Order price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getItemName() {
        return this.itemName;
    }

    public Order itemName(String itemName) {
        this.setItemName(itemName);
        return this;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public QcStatus getQcStatus() {
        return this.qcStatus;
    }

    public Order qcStatus(QcStatus qcStatus) {
        this.setQcStatus(qcStatus);
        return this;
    }

    public void setQcStatus(QcStatus qcStatus) {
        this.qcStatus = qcStatus;
    }

    public Returns getReturns() {
        return this.returns;
    }

    public void setReturns(Returns returns) {
        this.returns = returns;
    }

    public Order returns(Returns returns) {
        this.setReturns(returns);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", orderId='" + getOrderId() + "'" +
            ", emailAddress='" + getEmailAddress() + "'" +
            ", sku='" + getSku() + "'" +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            ", itemName='" + getItemName() + "'" +
            ", qcStatus='" + getQcStatus() + "'" +
            "}";
    }
}
