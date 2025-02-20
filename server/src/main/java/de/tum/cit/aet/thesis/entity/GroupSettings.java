package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "group_settings")
@Getter
@Setter
public class GroupSettings {
    @Id
    private UUID groupId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(columnDefinition = "TEXT")
    private String acceptanceEmailTemplate;

    @Column(columnDefinition = "TEXT")
    private String postAcceptanceInstructions;

    @Column(columnDefinition = "TEXT")
    private String emailFooter;
}