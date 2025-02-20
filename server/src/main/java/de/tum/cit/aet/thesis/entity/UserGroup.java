package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;

@Getter
@Setter
@Entity
@Table(name = "user_groups")
public class UserGroup {
    @EmbeddedId
    private UserGroupId id = new UserGroupId();

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    private String role;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        if (group != null && id != null) {
            id.setGroupId(group.getId());
        }
    }
}