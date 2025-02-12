package de.tum.cit.aet.thesis.entity.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberId implements Serializable {
    private UUID groupId;
    private UUID userId;
}
