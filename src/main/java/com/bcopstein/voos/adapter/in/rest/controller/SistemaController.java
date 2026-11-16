package com.bcopstein.voos.adapter.in.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SistemaController {

    @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
    public String inicio() {
        return "Sistema de Gestão de Planos de Voo - data de referência: 2027-01-15 (docs/api/endpoints.md)";
    }
}
