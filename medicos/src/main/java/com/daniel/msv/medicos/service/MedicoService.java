package com.daniel.msv.medicos.service;

import com.daniel.commons.dto.medicos.MedicoRequest;
import com.daniel.commons.dto.medicos.MedicoResponse;
import com.daniel.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoActivoPorId(Long id);

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
