package com.server.models.entities;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
@Table(name = "notes")
public class Note extends Timestamp{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;    

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = true, columnDefinition = "TEXT")
    private String content;

    @Column(name="deleted_at",nullable = true)
    private LocalDateTime deletedAt;

    // include title, content, topic name, parent title
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding", nullable = true, columnDefinition = "vector(384)")
    @JsonIgnore
    private float[] embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @JsonIgnore
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    @JsonIgnore
    private Note parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Note> children;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by",nullable = true)
    @JsonIgnore
    private User deletedBy;
}
