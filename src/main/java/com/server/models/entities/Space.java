package com.server.models.entities;

import com.server.models.extend.Timestamp;

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
@Table(name = "spaces", indexes= {
    @Index(name = "idx_space_embedding", columnList = "embedding")
})
public class Space extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)    
    private String name;


    @Column(name = "embedding", nullable = false, columnDefinition = "vector(1536)")
    private float[] embedding;
    
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

}
