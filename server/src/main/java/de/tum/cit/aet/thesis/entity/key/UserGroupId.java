package de.tum.cit.aet.thesis.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupId implements Serializable {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "group_id")
    private UUID groupId;
}