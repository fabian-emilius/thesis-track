package de.tum.cit.aet.thesis.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class UserGroupId implements java.io.Serializable {
    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserGroupId entity = (UserGroupId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.groupId, entity.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash("user-groups", userId, groupId);
    }
}