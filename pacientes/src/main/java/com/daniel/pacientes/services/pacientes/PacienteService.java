package com.daniel.pacientes.services.pacientes;

import com.daniel.commons.dto.paciente.PacienteRequest;
import com.daniel.commons.dto.paciente.PacienteResponse;
import com.daniel.commons.service.CrudService;

import java.util.List;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
    List<PacienteResponse> listarActivos();

    PacienteResponse obtenerActivoPorId(Long id);

    PacienteResponse obtenerSinValidarEstado(Long id);

    PacienteResponse registrar(PacienteRequest request);

    PacienteResponse actualizar(PacienteRequest request, Long id);

    void eliminarLogico(Long id);
}