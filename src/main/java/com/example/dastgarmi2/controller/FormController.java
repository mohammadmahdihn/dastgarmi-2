package com.example.dastgarmi2.controller;

import com.example.dastgarmi2.model.Field;
import com.example.dastgarmi2.model.Form;
import com.example.dastgarmi2.repository.FormRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forms")
public class FormController {

    private final FormRepository formRepository;

    public FormController(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllForms() {
        try {
            List<Form> forms = formRepository.findAll();
            return ResponseEntity.ok(forms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve forms: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createForm(@RequestBody Form form) {
        try {
            for (Field field : form.getFields()) {
                String type = field.getType();
                if (!type.equals("text") && !type.equals("int") && !type.equals("boolean") && !type.equals("date")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Unsupported field type: " + type);
                }
            }
            formRepository.save(form);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Form created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Form creation failed");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFormById(@PathVariable Long id) {
        try {
            Form form = formRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Form not found"));
            return ResponseEntity.ok(form);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateForm(@PathVariable Long id, @RequestBody Form formDetails) {
        try {
            Form form = formRepository.findById(id).orElseThrow(() -> new RuntimeException("Form not found"));
            form.setName(formDetails.getName());
            form.setPublished(formDetails.isPublished());
            return ResponseEntity.ok(formRepository.save(form));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Form update failed");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteForm(@PathVariable Long id) {
        try {
            formRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete form");
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishForm(@PathVariable Long id) {
        try {
            Form form = formRepository.findById(id).orElseThrow(() -> new RuntimeException("Form not found"));
            form.setPublished(!form.isPublished());
            return ResponseEntity.ok(formRepository.save(form));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Form publishing failed");
        }
    }

    @GetMapping("/published")
    public ResponseEntity<?> getPublishedForms() {
        try {
            List<Form> forms = formRepository.findByPublished(true);
            return ResponseEntity.ok(forms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve published forms");
        }
    }
}
