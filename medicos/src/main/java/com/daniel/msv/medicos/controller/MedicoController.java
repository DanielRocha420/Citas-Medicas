package com.daniel.msv.medicos.controller;

import com.daniel.commons.controller.CrudController;
import com.daniel.commons.dto.medicos.MedicoRequest;
import com.daniel.commons.dto.medicos.MedicoResponse;
import com.daniel.msv.medicos.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API medicos", description = "Metodos para la gestion de medicos")
public class MedicoController extends CrudController<MedicoRequest, MedicoResponse, MedicoService> {

    public MedicoController(MedicoService service) {
        super(service);
    }

    @Override
    @Operation(summary = "Obtener medico por id sin importar el estado del registro")
    public ResponseEntity<MedicoResponse> obtenerPorId(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ){
        return ResponseEntity.ok(service.obtenerMedicoPorIdSinEstado(id));
    }

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    @Operation(summary = "Actualizar disponibilidad del medico (no es endpoint libre, debe gestionarlo el sistema de citas)")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable("idMedico") @Positive(message = "El idMedico debe ser positivo") Long idMedico,
            @PathVariable("idDisponibilidad") @Positive(message = "El idDisponibilidad debe ser positivo") Long idDisponibilidad
    ) {
        service.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }
}