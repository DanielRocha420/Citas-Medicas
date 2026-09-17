package com.daniel.pacientes.controllers;

import com.daniel.commons.controller.CrudController;
import com.daniel.commons.dto.paciente.PacienteRequest;
import com.daniel.commons.dto.paciente.PacienteResponse;
import com.daniel.pacientes.services.pacientes.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "API Pacientes", description = "Endpoints para la gestión e historial de pacientes")
public class PacienteController extends CrudController<PacienteRequest, PacienteResponse, PacienteService> {

    public PacienteController(PacienteService pacienteService) {
        super(pacienteService);
    }

    @GetMapping("/id-paciente/{id}")
    @Operation(summary = "Obtener paciente por ID sin validar el estado del registro")
    public ResponseEntity<PacienteResponse> obtenerSinValidarEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return ResponseEntity.ok(service.obtenerSinValidarEstado(id));
    }
}