package com.example.dastgarmi2.repository;

import com.example.dastgarmi2.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByPublished(boolean published);
}