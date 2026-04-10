package com.server.models.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.models.extend.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)    
    private String name;


    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name="deleted_at",nullable = true)
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Group> groups;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by",nullable = true)
    @JsonIgnore
    private User deletedBy;
}
