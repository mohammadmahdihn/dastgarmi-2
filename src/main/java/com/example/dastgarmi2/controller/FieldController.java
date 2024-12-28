package com.example.dastgarmi2.controller;

import com.example.dastgarmi2.model.Field;
import com.example.dastgarmi2.model.Form;
import com.example.dastgarmi2.repository.FieldRepository;
import com.example.dastgarmi2.repository.FormRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forms/{formId}/fields")
public class FieldController {

    private final FieldRepository fieldRepository;
    private final FormRepository formRepository;

    public FieldController(FieldRepository fieldRepository, FormRepository formRepository) {
        this.fieldRepository = fieldRepository;
        this.formRepository = formRepository;
    }

    @GetMapping
    public ResponseEntity<?> getFieldsByFormId(@PathVariable Long formId) {
        try {
            List<Field> fields = fieldRepository.findByFormId(formId);
            if (fields.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No fields found for form with ID " + formId);
            }
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve fields");
        }
    }

    @PostMapping
    public ResponseEntity<?> addFieldToForm(@PathVariable Long formId, @RequestBody Field field) {
        try {
            Form form = formRepository.findById(formId)
                    .orElseThrow(() -> new RuntimeException("Form not found"));
            field.setForm(form);
            Field savedField = fieldRepository.save(field);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedField);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add field");
        }
    }

    @PutMapping("/{fieldId}")
    public ResponseEntity<?> updateField(@PathVariable Long formId, @PathVariable Long fieldId, @RequestBody Field fieldDetails) {
        try {
            Field field = fieldRepository.findById(fieldId)
                    .orElseThrow(() -> new RuntimeException("Field not found"));
            field.setFieldName(fieldDetails.getFieldName());
            field.setLabel(fieldDetails.getLabel());
            field.setType(fieldDetails.getType());
            field.setValue(fieldDetails.getValue());
            Field updatedField = fieldRepository.save(field);
            return ResponseEntity.ok(updatedField);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update field");
        }
    }
}
