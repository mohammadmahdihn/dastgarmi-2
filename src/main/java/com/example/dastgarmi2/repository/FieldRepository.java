package com.example.dastgarmi2.repository;

import com.example.dastgarmi2.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findByFormFormId(Long formId);
}