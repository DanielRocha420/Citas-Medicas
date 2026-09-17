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
        return medicoMapper.entidadAResponse(obtenerMedicoActivoEntidadPorId(id));
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
        if (nuevaDisponibilidad == null) {
            throw new IllegalArgumentException("No existe el tipo de disponibilidad con codigo: " + idDisponibilidad);
        }

        // Validar que no se pueda cambiar a DISPONIBLE si tiene citas activas
        if (nuevaDisponibilidad == DisponibilidadMedico.DISPONIBLE) {
            validarIntegridadConCitas(idMedico);
        }

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();
        medico.actualizarDisponibilidad(nuevaDisponibilidad);
        medicoRepository.save(medico);
        log.info("Disponibilidad del medico con id {} cambio de {} a {}", idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {
    Medico medico = obtenerMedicoActivoEntidadPorId(id);
    log.info("Eliminado medico con Id: {}", id);

    validarIntegridadConCitas(id);

    medico.eliminar();
    log.info("Medico eliminado exitosamente");
    }

    private Medico obtenerMedicoActivoEntidadPorId(Long id) {
        log.info("Buscando medico con id {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico activo no encontrado con id: " + id));

    }

    private void validarDatosUnicos(MedicoRequest request) {
        log.info("Validando email unico...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el email: "+ request.email());

        log.info("Validando telefono unico...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el telefono: "+ request.telefono());

        log.info("Validando cedula profesional unica...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con la cedula profesonal: "+ request.cedulaProfesional());

    }

    private void validarCambiosUnicos(MedicoRequest request, Long id) {
        log.info("Validando cambio en email unico...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el email: "+ request.email());

        log.info("Validando cambio en  telefono unico...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con el telefono: "+ request.telefono());

        log.info("Validando cedula profesional unica...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un medico activo registrado con la cedula profesonal: "+ request.cedulaProfesional());

    }

    private void validarIntegridadConCitas(Long idMedico) {
        if (citaClient.tieneCitasActivasMedico(idMedico)) {
            throw new IllegalArgumentException("No se puede actualizar ni eliminar el médico porque tiene citas en estado CONFIRMADA o EN_CURSO");
        }
    }
}
