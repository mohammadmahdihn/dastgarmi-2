package com.example.dastgarmi2.controller;

import com.example.dastgarmi2.model.Form;
import com.example.dastgarmi2.repository.FormRepository;
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
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @PostMapping
    public Form createForm(@RequestBody Form form) {
        return formRepository.save(form);
    }

    @GetMapping("/{id}")
    public Form getFormById(@PathVariable Long id) {
        return formRepository.findById(id).orElseThrow(() -> new RuntimeException("Form not found"));
    }

    @PutMapping("/{id}")
    public Form updateForm(@PathVariable Long id, @RequestBody Form formDetails) {
        Form form = formRepository.findById(id).orElseThrow(() -> new RuntimeException("Form not found"));
        form.setName(formDetails.getName());
        form.setPublished(formDetails.isPublished());
        return formRepository.save(form);
    }

    @DeleteMapping("/{id}")
    public void deleteForm(@PathVariable Long id) {
        formRepository.deleteById(id);
    }

    @PostMapping("/{id}/publish")
    public Form publishForm(@PathVariable Long id) {
        Form form = formRepository.findById(id).orElseThrow(() -> new RuntimeException("Form not found"));
        form.setPublished(true);
        return formRepository.save(form);
    }

    @GetMapping("/published")
    public List<Form> getPublishedForms() {
        return formRepository.findByPublished(true);
    }
}