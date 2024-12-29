package com.example.dastgarmi2.controller;

import com.example.dastgarmi2.exceptions.ParseException;
import com.example.dastgarmi2.model.Field;
import com.example.dastgarmi2.model.Form;
import com.example.dastgarmi2.repository.FieldRepository;
import com.example.dastgarmi2.repository.FormRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/forms")
public class FormController {

    private final FieldRepository fieldRepository;
    private final FormRepository formRepository;

    public FormController(FieldRepository fieldRepository, FormRepository formRepository) {
        this.fieldRepository = fieldRepository;
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
                if (!isFieldTypeValid(field)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid field type: " + type);
                }
                if (field.getValue() == null) {
                    setDefaultValue(field);
                }
                if (!field.getParsedValue().toString().equals(field.getValue())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in parsing value " + field.getValue());
                }
            }
            formRepository.save(form);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Form created, form id: "+ form.getId());
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Form creation failed");
        }
    }

    private void setDefaultValue(Field field) {
        String type = field.getType();
        if (type.equalsIgnoreCase("int")) {
            field.setValue("0");
            field.setParsedValue(0);
        } else if (type.equalsIgnoreCase("boolean")) {
            field.setValue("true");
            field.setParsedValue(true);
        } else if (type.equalsIgnoreCase("date")) {
            field.setValue("1970-01-01");
            field.setParsedValue(LocalDate.parse("1970-01-01"));
        } else if (type.equalsIgnoreCase("string")) {
            field.setValue("");
            field.setParsedValue(LocalDate.parse(""));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFormById(@PathVariable Long id) {
        try {
            Optional<Form> optionalForm = formRepository.findById(id);
            if (optionalForm.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Form not found");
            return ResponseEntity.ok(optionalForm.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateForm(@PathVariable Long id, @RequestBody Form formDetails) {
        try {
            Optional<Form> optionalForm = formRepository.findById(id);
            if (optionalForm.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Form not found");
            Form form = optionalForm.get();
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
            Optional<Form> optionalForm = formRepository.findById(id);
            if (optionalForm.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Form not found");
            Form form = optionalForm.get();
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

    @GetMapping("/{formId}/fields")
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

    @PostMapping("/{formId}/fields")
    public ResponseEntity<?> addFieldToForm(@PathVariable Long formId, @RequestBody Field field) {
        try {
            if (!isFieldTypeValid(field)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid field type: " + field.getType());
            }
            if (field.getValue() == null) {
                setDefaultValue(field);
            }
            if (!field.getParsedValue().toString().equals(field.getValue())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in parsing value " + field.getValue());
            }
            Optional<Form> optionalForm = formRepository.findById(formId);
            if (optionalForm.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Form not found");
            field.setForm(optionalForm.get());
            Field savedField = fieldRepository.save(field);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedField);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add field");
        }
    }

    @PutMapping("/{formId}/fields/{fieldId}")
    public ResponseEntity<?> updateField(@PathVariable Long formId, @PathVariable Long fieldId, @RequestBody Field fieldDetails) {
        try {
            Optional<Field> optionalField = fieldRepository.findById(fieldId);
            if (optionalField.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Field not found");
            if (!isFieldTypeValid(fieldDetails)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid field type: " + fieldDetails.getType());
            }
            if (fieldDetails.getValue() == null) {
                setDefaultValue(fieldDetails);
            }
            if (!fieldDetails.getParsedValue().toString().equals(fieldDetails.getValue())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in parsing value " + fieldDetails.getValue());
            }
            Field field = optionalField.get();
            field.setFieldName(fieldDetails.getFieldName());
            field.setLabel(fieldDetails.getLabel());
            field.setType(fieldDetails.getType());
            field.setValue(fieldDetails.getValue());
            Field updatedField = fieldRepository.save(field);
            return ResponseEntity.ok(updatedField);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update field");
        }
    }

    private boolean isFieldTypeValid(Field field) {
        String type = field.getType();
        return (type.equals("text") || type.equals("int") ||
                type.equals("boolean") || type.equals("date"));
    }

}
