package com.rkey.returnkeyapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rkey.returnkeyapp.domain.enumeration.ReturnStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Returns.
 */
@Entity
@Table(name = "returns")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Returns extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "total_amount", precision = 21, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_status")
    private ReturnStatus returnStatus;

    @OneToMany(mappedBy = "returns")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "returns" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    @OneToOne
    @JoinColumn(unique = true)
    private Pending pending;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Returns id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Returns totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ReturnStatus getReturnStatus() {
        return this.returnStatus;
    }

    public Returns returnStatus(ReturnStatus returnStatus) {
        this.setReturnStatus(returnStatus);
        return this;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setReturns(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setReturns(this));
        }
        this.orders = orders;
    }

    public Returns orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Returns addOrder(Order order) {
        this.orders.add(order);
        order.setReturns(this);
        return this;
    }

    public Returns removeOrder(Order order) {
        this.orders.remove(order);
        order.setReturns(null);
        return this;
    }

    public Pending getPending() {
        return this.pending;
    }

    public void setPending(Pending pending) {
        this.pending = pending;
    }

    public Returns pending(Pending pending) {
        this.setPending(pending);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Returns)) {
            return false;
        }
        return id != null && id.equals(((Returns) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Returns{" +
            "id=" + getId() +
            ", totalAmount=" + getTotalAmount() +
            ", returnStatus='" + getReturnStatus() + "'" +
            "}";
    }
}
