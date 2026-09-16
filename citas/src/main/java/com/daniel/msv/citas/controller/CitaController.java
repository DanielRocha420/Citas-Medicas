package com.daniel.msv.citas.controller;

import com.daniel.commons.controller.CrudController;
import com.daniel.msv.citas.dto.CitaRequest;
import com.daniel.msv.citas.dto.CitaResponse;
import com.daniel.msv.citas.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService> {

    public CitaController(CitaService service) {
        super(service);
    }
    @Operation(summary = "Actualizar estado de la cita",
    description = "Actualiza el estado de una cita utilizando el identificador de la cita y el identificador del nuevo estado.")
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "el idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "el idEstado debe ser positivo") Long idEstado) {
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }
}
