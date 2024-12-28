package com.example.dastgarmi2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;
    private String label;
    private String type;
    private String value;
    @ManyToOne
    @JoinColumn(name = "form_id")
    @JsonIgnore
    private Form form;
    @Transient
    private Object parsedValue;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
    public Object getParsedValue() {
        if (type.equalsIgnoreCase("int")) {
            return Integer.parseInt(value);
        } else if (type.equalsIgnoreCase("boolean")) {
            return Boolean.parseBoolean(value);
        } else if (type.equalsIgnoreCase("date")) {
            return LocalDate.parse(value);
        } else if (type.equalsIgnoreCase("string")) {
            return value;
        } else {
            throw new RuntimeException("Unsupported type: " + type);
        }
    }
}
