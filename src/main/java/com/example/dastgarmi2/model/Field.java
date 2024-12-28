package com.example.dastgarmi2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fieldId;

    private String fieldName;
    private String label;
    private String type;
    private String value;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;
}