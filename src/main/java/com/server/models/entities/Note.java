package com.server.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notes", indexes= {
    @Index(name = "idx_note_embedding", columnList = "embedding"),
    @Index(name = "idx_note_parent_id", columnList = "parent_id"),
    @Index(name = "idx_note_creator_id", columnList = "creator_id")
})
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;


    @Column(name="is_deleted")
    private Boolean isDeleted;

    @Column(name = "embedding", nullable = false, columnDefinition = "vector(1536)")
    @JsonIgnore
    private String embedding;

    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Note parent;

    @ManyToOne
    @JoinColumn(name = "creator_id")

    private User creator;
}
