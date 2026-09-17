package com.daniel.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {

    @GetMapping("/validar-paciente/{idPaciente}")
    boolean tieneCitasActivasPaciente(@PathVariable("idPaciente") Long idPaciente);

    @GetMapping("/validar-medico/{idMedico}")
    boolean tieneCitasActivasMedico(@PathVariable("idMedico") Long idMedico);
}
