package com.example.dastgarmi2.controller;

import com.example.dastgarmi2.model.Field;
import com.example.dastgarmi2.model.Form;
import com.example.dastgarmi2.repository.FieldRepository;
import com.example.dastgarmi2.repository.FormRepository;
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
    public List<Field> getFieldsByFormId(@PathVariable Long formId) {
        return fieldRepository.findByFormFormId(formId);
    }

    @PostMapping
    public Field addFieldToForm(@PathVariable Long formId, @RequestBody Field field) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
        field.setForm(form);
        return fieldRepository.save(field);
    }

    @PutMapping("/{fieldId}")
    public Field updateField(@PathVariable Long formId, @PathVariable Long fieldId, @RequestBody Field fieldDetails) {
        Field field = fieldRepository.findById(fieldId).orElseThrow(() -> new RuntimeException("Field not found"));
        field.setFieldName(fieldDetails.getFieldName());
        field.setLabel(fieldDetails.getLabel());
        field.setType(fieldDetails.getType());
        field.setValue(fieldDetails.getValue());
        return fieldRepository.save(field);
    }
}