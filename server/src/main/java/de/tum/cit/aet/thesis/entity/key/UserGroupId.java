package de.tum.cit.aet.thesis.entity.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupId implements Serializable {
    private UUID userId;
    private UUID groupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroupId)) return false;
        UserGroupId that = (UserGroupId) o;
        return userId.equals(that.userId) && groupId.equals(that.groupId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() ^ groupId.hashCode();
    }
}