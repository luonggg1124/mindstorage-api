package com.server.models.entities;

import java.util.UUID;

import com.server.models.extend.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class Activity extends Timestamp{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
