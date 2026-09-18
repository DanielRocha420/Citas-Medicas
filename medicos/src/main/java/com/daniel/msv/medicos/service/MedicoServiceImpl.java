package com.daniel.msv.medicos.service;

import com.daniel.commons.clients.CitaClient;
import com.daniel.commons.dto.medicos.MedicoRequest;
import com.daniel.commons.dto.medicos.MedicoResponse;
import com.daniel.commons.enums.DisponibilidadMedico;
import com.daniel.commons.enums.EspecialidadMedico;
import com.daniel.commons.enums.EstadoRegistro;
import com.daniel.commons.exceptions.RecursoNoEncontradoException;
import com.daniel.msv.medicos.entity.Medico;
import com.daniel.msv.medicos.mapper.MedicoMapper;
import com.daniel.msv.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService{
    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final CitaClient citaClient;


    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando todos los medicos activos");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    public MedicoResponse obtenerPorId(Long id) {
        log.info("Buscando medico activo con id {}", id);
        Medico medico = medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico activo no encontrado con id: " + id));
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse obtenerMedicoActivoPorId(Long id) {
        log.info("Buscando medico activo con id {}", id);
        Medico medico = medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico activo no encontrado con id: " + id));
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico sin estado con id {}", id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico sin estado no encontrado con id: " + id)));
    }


    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando nuevo medico: {}", request.nombre());

        Medico medico = medicoMapper.requestAEntidad(request);
        validarDatosUnicos(request);
        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));
        medicoRepository.save(medico);
        log.info("Nuevo medico registrado: {}", medico.getNombre());
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    @Transactional
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoActivoEntidadPorId(id);
        log.info("Actualizando medico con id: {}", id);

        validarIntegridadConCitas(id);
        validarCambiosUnicos(request, id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );

        Medico medicoActualizado = medicoRepository.save(medico);
        log.info("Medico con id {} actualizado correctamente", id);

        return medicoMapper.entidadAResponse(medicoActualizado);
    }
    @Override
    @Transactional
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoEntidadPorId(idMedico);
        log.info("Actualizando disponibilidad del medico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad);
        boolean disponibilidadInvalida = nuevaDisponibilidad == null;
        if (disponibilidadInvalida) {
            throw new IllegalArgumentException("No existe el tipo de disponibilidad con codigo: " + idDisponibilidad);
        }

        boolean esCambioADisponible = nuevaDisponibilidad == DisponibilidadMedico.DISPONIBLE;
        if (esCambioADisponible) {
            validarIntegridadConCitas(idMedico);
        }

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        try {
            medico.actualizarDisponibilidad(nuevaDisponibilidad);
            medicoRepository.save(medico);
            log.info("Disponibilidad del medico con id {} cambio de {} a {}", idMedico, disponibilidadAnterior, nuevaDisponibilidad);
        } catch (Exception e) {
            log.error("Error al actualizar disponibilidad del médico con id {}. Revertiendo cambio.", idMedico, e);
            medico.setDisponibilidad(disponibilidadAnterior);
            medicoRepository.save(medico);
            throw new IllegalStateException("No se pudo actualizar la disponibilidad del médico. Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoEntidadPorId(id);
        log.info("Eliminando médico con Id: {}", id);

        validarIntegridadConCitas(id);

        try {
            medico.eliminar();
            medicoRepository.save(medico);
            log.info("Médico eliminado exitosamente");
        } catch (Exception e) {
            log.error("Error al eliminar médico con id {}. Revertiendo cambio de estado.", id, e);
            medico.setEstadoRegistro(EstadoRegistro.ACTIVO);
            medicoRepository.save(medico);
            throw new IllegalStateException("No se pudo eliminar el médico. Error: " + e.getMessage(), e);
        }
    }

    private Medico obtenerMedicoActivoEntidadPorId(Long id) {
        log.info("Buscando medico con id {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico activo no encontrado con id: " + id));

    }

    private void validarDatosUnicos(MedicoRequest request) {
        boolean emailDuplicado = medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email(), EstadoRegistro.ACTIVO);
        if (emailDuplicado) {
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el email: "+ request.email());
        }

        boolean telefonoDuplicado = medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono(), EstadoRegistro.ACTIVO);
        if (telefonoDuplicado) {
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el telefono: "+ request.telefono());
        }

        boolean cedulaDuplicada = medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO);
        if (cedulaDuplicada) {
            throw new IllegalArgumentException("Ya existe un medico activo registrado con la cedula profesonal: "+ request.cedulaProfesional());
        }
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id) {
        boolean emailDuplicado = medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email(), EstadoRegistro.ACTIVO, id);
        if (emailDuplicado) {
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el email: "+ request.email());
        }

        boolean telefonoDuplicado = medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono(), EstadoRegistro.ACTIVO, id);
        if (telefonoDuplicado) {
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el telefono: "+ request.telefono());
        }

        boolean cedulaDuplicada = medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO, id);
        if (cedulaDuplicada) {
            throw new IllegalArgumentException("Ya existe un medico activo registrado con la cedula profesonal: "+ request.cedulaProfesional());
        }
    }

    private void validarIntegridadConCitas(Long idMedico) {
        if (citaClient.tieneCitasActivasMedico(idMedico)) {
            throw new IllegalArgumentException("No se puede actualizar ni eliminar el médico porque tiene citas en estado CONFIRMADA o EN_CURSO");
        }
    }
}
