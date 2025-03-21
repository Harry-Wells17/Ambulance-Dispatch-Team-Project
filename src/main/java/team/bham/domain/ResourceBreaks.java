package team.bham.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ResourceBreaks.
 */
@Entity
@Table(name = "resource_breaks")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceBreaks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "last_break", nullable = false)
    private ZonedDateTime lastBreak;

    @NotNull
    @Column(name = "break_requested", nullable = false)
    private Boolean breakRequested;

    @Column(name = "started_break")
    private ZonedDateTime startedBreak;

    @Column(name = "on_break")
    private Boolean onBreak;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResourceBreaks id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getLastBreak() {
        return this.lastBreak;
    }

    public ResourceBreaks lastBreak(ZonedDateTime lastBreak) {
        this.setLastBreak(lastBreak);
        return this;
    }

    public void setLastBreak(ZonedDateTime lastBreak) {
        this.lastBreak = lastBreak;
    }

    public Boolean getBreakRequested() {
        return this.breakRequested;
    }

    public ResourceBreaks breakRequested(Boolean breakRequested) {
        this.setBreakRequested(breakRequested);
        return this;
    }

    public void setBreakRequested(Boolean breakRequested) {
        this.breakRequested = breakRequested;
    }

    public ZonedDateTime getStartedBreak() {
        return this.startedBreak;
    }

    public ResourceBreaks startedBreak(ZonedDateTime startedBreak) {
        this.setStartedBreak(startedBreak);
        return this;
    }

    public void setStartedBreak(ZonedDateTime startedBreak) {
        this.startedBreak = startedBreak;
    }

    public Boolean getOnBreak() {
        return this.onBreak;
    }

    public ResourceBreaks onBreak(Boolean onBreak) {
        this.setOnBreak(onBreak);
        return this;
    }

    public void setOnBreak(Boolean onBreak) {
        this.onBreak = onBreak;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceBreaks)) {
            return false;
        }
        return id != null && id.equals(((ResourceBreaks) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceBreaks{" +
            "id=" + getId() +
            ", lastBreak='" + getLastBreak() + "'" +
            ", breakRequested='" + getBreakRequested() + "'" +
            ", startedBreak='" + getStartedBreak() + "'" +
            ", onBreak='" + getOnBreak() + "'" +
            "}";
    }
}
