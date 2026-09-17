package com.daniel.commons.clients;

import com.daniel.commons.dto.paciente.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacienteClient {

    @GetMapping("/activo/{id}")
    PacienteResponse obtenerPacienteActivoPorId(@PathVariable("id") Long id);

    @GetMapping("/id-paciente/{id}")
    PacienteResponse obtenerPacienteSinEstadoPorId(@PathVariable("id") Long id);
}
