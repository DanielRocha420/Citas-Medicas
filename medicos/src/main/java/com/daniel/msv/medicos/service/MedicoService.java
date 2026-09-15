package com.daniel.msv.medicos.service;

import com.daniel.commons.dto.medico.MedicoRequest;
import com.daniel.commons.dto.medico.MedicoResponse;
import com.daniel.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
