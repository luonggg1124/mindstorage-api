package com.server.models.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.models.enums.RoleAction;
import com.server.models.enums.SpaceVisibility;
import com.server.models.extend.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "spaces")
public class Space extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)    
    private String name;


    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "image_file_key", nullable = true)
    private String imageFileKey;

    @Column(name="deleted_at",nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private SpaceVisibility visibility;


    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_role", nullable = false)
    private RoleAction visibilityRole;
    
    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Group> groups;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    private User creator;
    @Column(name = "last_activity_at", nullable = true)
    private LocalDateTime lastActivityAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by",nullable = true)
    @JsonIgnore
    private User deletedBy;

    @PrePersist
    public void prePersist() {
        if (this.visibility == null) {
            this.visibility = SpaceVisibility.PUBLIC;
        }
        if (this.visibilityRole == null) {
            this.visibilityRole = RoleAction.VIEWER;
        }
    }
}
