package com.rkey.returnkeyapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.rkey.returnkeyapp.domain.Pending} entity.
 */
public class PendingDTO implements Serializable {

    public PendingDTO() {}

    public PendingDTO(String generatedToken, String tokenReference) {
        this.generatedToken = generatedToken;
        this.tokenReference = tokenReference;
    }

    private Long id;

    private String generatedToken;

    private String tokenReference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneratedToken() {
        return generatedToken;
    }

    public void setGeneratedToken(String generatedToken) {
        this.generatedToken = generatedToken;
    }

    public String getTokenReference() {
        return tokenReference;
    }

    public void setTokenReference(String tokenReference) {
        this.tokenReference = tokenReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PendingDTO)) {
            return false;
        }

        PendingDTO pendingDTO = (PendingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pendingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PendingDTO{" +
            "id=" + getId() +
            ", generatedToken='" + getGeneratedToken() + "'" +
            ", tokenReference='" + getTokenReference() + "'" +
            "}";
    }
}
