package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberId implements Serializable {
    private UUID groupId;
    private UUID userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberId that = (GroupMemberId) o;
        return groupId.equals(that.groupId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return groupId.hashCode() ^ userId.hashCode();
    }
}