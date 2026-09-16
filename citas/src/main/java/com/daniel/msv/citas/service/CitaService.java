package com.daniel.msv.citas.service;

import com.daniel.commons.service.CrudService;
import com.daniel.msv.citas.dto.CitaRequest;
import com.daniel.msv.citas.dto.CitaResponse;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {

    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    boolean tieneCitasActivas(Long idPaciente);
}
