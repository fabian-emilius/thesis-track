package de.tum.cit.aet.thesis.entity;

import de.tum.cit.aet.thesis.constants.GroupRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "group_members")
@Getter
@Setter
public class GroupMember {
    @EmbeddedId
    private GroupMemberId id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;
}