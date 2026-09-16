package com.daniel.commons.clients;

import com.daniel.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "medicos")
public interface MedicoClient {

    @GetMapping("/activo/{id}")
    MedicoResponse obtenerMedicoActivoPorId(@PathVariable("id") Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoSinEstadoPorId(@PathVariable("id") Long id);

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    void actualizarDisponibilidadMedico(
            @PathVariable("idMedico") Long idMedico,
            @PathVariable("idDisponibilidad") Long idDisponibilidad);
}