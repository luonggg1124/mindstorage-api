package com.server.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "groups", indexes= {
    @Index(name = "idx_group_embedding", columnList = "embedding"),
    @Index(name = "idx_group_space_id", columnList = "space_id")
})
public class Group extends Timestamp {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="embedding", nullable = false, columnDefinition = "vector(1536)")
    @JsonIgnore
    private float[] embedding;

    @ManyToOne
    @JoinColumn(name="space_id")
    private Space space;
}
