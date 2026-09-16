package com.daniel.pacientes.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {

    @GetMapping("/validar-paciente/{idPaciente}")
    boolean tieneCitasActivas(@PathVariable("idPaciente") Long idPaciente);
}
