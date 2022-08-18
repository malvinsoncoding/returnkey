package com.rkey.returnkeyapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Pending.
 */
@Entity
@Table(name = "pending")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Pending extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "generated_token", unique = true)
    private String generatedToken;

    @Column(name = "token_reference")
    private String tokenReference;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pending id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneratedToken() {
        return this.generatedToken;
    }

    public Pending generatedToken(String generatedToken) {
        this.setGeneratedToken(generatedToken);
        return this;
    }

    public void setGeneratedToken(String generatedToken) {
        this.generatedToken = generatedToken;
    }

    public String getTokenReference() {
        return this.tokenReference;
    }

    public Pending tokenReference(String tokenReference) {
        this.setTokenReference(tokenReference);
        return this;
    }

    public void setTokenReference(String tokenReference) {
        this.tokenReference = tokenReference;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pending)) {
            return false;
        }
        return id != null && id.equals(((Pending) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pending{" +
            "id=" + getId() +
            ", generatedToken='" + getGeneratedToken() + "'" +
            ", tokenReference='" + getTokenReference() + "'" +
            "}";
    }
}
