package com.daniel.msv.citas.service;

import com.daniel.commons.clients.MedicoClient;
import com.daniel.commons.clients.PacienteClient;
import com.daniel.commons.dto.medicos.MedicoResponse;
import com.daniel.commons.dto.paciente.PacienteResponse;
import com.daniel.commons.enums.DisponibilidadMedico;
import com.daniel.commons.enums.EstadoRegistro;
import com.daniel.commons.exceptions.RecursoNoEncontradoException;
import com.daniel.msv.citas.dto.CitaRequest;
import com.daniel.msv.citas.dto.CitaResponse;
import com.daniel.msv.citas.entity.Cita;
import com.daniel.msv.citas.enums.EstadoCita;
import com.daniel.msv.citas.mapper.CitaMapper;
import com.daniel.msv.citas.repository.CitaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional // @Transactional: la promesa que a veces no se cumple, igual que las de mi ex pipipipi
@Slf4j
public class CitaServiceImpl implements CitaService {
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    private static final List<EstadoCita> ESTADOS_CITAS_ACTIVAS = List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);
    private static final List<EstadoCita> ESTADOS_CITAS_ACTUALIZABLES = List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA);

    @Override
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita...");

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitasActivas(request.idPaciente());

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        log.info("Cita registrada exitósamente");

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOException(id);

        log.info("Actualizando cita con id: {}", id);

        validarCitaActualizable(cita);

        // Validar cambio de paciente
        if (!cita.getIdPaciente().equals(request.idPaciente())) {
            PacienteResponse nuevoPaciente = obtenerPacienteActivo(request.idPaciente());
            validarPacienteSinCitasActivasExcluyendo(request.idPaciente(), id);
        }

        // Validar cambio de médico y reutilizar la respuesta
        Long idMedicoAnterior = cita.getIdMedico();
        boolean cambioMedico = !idMedicoAnterior.equals(request.idMedico());
        MedicoResponse medico = null;

        if (cambioMedico) {
            medico = obtenerMedicoActivo(request.idMedico());
            validarMedicoActivoDisponible(medico);
        } else {
            medico = obtenerMedicoActivo(request.idMedico());
        }

        // Primero actualizar la cita
        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.save(cita);

        // Si todo va bien, ahora sí actualizar disponibilidades
        if (cambioMedico) {
            // Liberar médico anterior
            actualizarDisponibilidadMedico(idMedicoAnterior, DisponibilidadMedico.DISPONIBLE.getCodigo());
            // Ocupar nuevo médico
            actualizarDisponibilidadMedico(request.idMedico(), DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
        }

        log.info("Cita actualizada con id: {}", id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(request.idPaciente()),
                medico
        );
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        citaRepository.save(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());

        log.info("Estado de la cita {} actualizado correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        if(cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());

        log.info("Cita con id {} ha sido marcada como eliminada", id);
    }


    private Cita obtenerCitaOException(Long id) {
        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {
        log.info("Buscando médico activo con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {
        log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private  void validarMedicoActivoDisponible(MedicoResponse medico) {

        log.info("Validando si el medico activo esta disponible");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw  new IllegalStateException("El medico no esta disponible para consulta");

    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        log.info("Actualizando disponibilidad del médico en el servicio remoto...");

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);

        log.info("Disponibilidad del médico en el servicio remoto");
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {
        switch (estadoCita) {
            case PENDIENTE, CONFIRMADA -> actualizarDisponibilidadMedico(idMedico,
                    DisponibilidadMedico.NO_DISPONIBLE.getCodigo());

            case EN_CURSO -> actualizarDisponibilidadMedico(idMedico,
                    DisponibilidadMedico.EN_CONSULTA.getCodigo());

            case FINALIZADA, CANCELADA -> actualizarDisponibilidadMedico(idMedico,
                    DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneCitasActivas(Long idPaciente) {
        log.info("Verificando si el paciente {} tiene citas activas", idPaciente);
        return citaRepository.existsByIdPacienteAndEstadoCitaInAndEstadoRegistro(
                idPaciente,
                ESTADOS_CITAS_ACTIVAS,
                EstadoRegistro.ACTIVO
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneCitasActivasMedico(Long idMedico) {
        log.info("Verificando si el médico {} tiene citas activas", idMedico);
        return citaRepository.existsByIdMedicoAndEstadoCitaInAndEstadoRegistro(
                idMedico,
                ESTADOS_CITAS_ACTIVAS,
                EstadoRegistro.ACTIVO
        );
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {
        log.info("Buscando paciente activo con id {} en el servicio remoto...", id);
        return pacienteClient.obtenerPacienteActivoPorId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {
        log.info("Buscando paciente sin estado con id {} en el servicio remoto...", id);
        return pacienteClient.obtenerPacienteSinEstadoPorId(id);
    }

    private void validarPacienteSinCitasActivas(Long idPaciente) {
        log.info("Validando si el paciente {} tiene citas activas", idPaciente);
        if (tieneCitasActivas(idPaciente)) {
            throw new IllegalStateException("El paciente ya tiene citas activas en estados PENDIENTE, CONFIRMADA o EN_CURSO");
        }
    }

    private void validarPacienteSinCitasActivasExcluyendo(Long idPaciente, Long idCitaExcluir) {
        log.info("Validando si el paciente {} tiene citas activas (excluyendo cita {})", idPaciente, idCitaExcluir);
        if (citaRepository.existsByIdPacienteAndEstadoCitaInAndEstadoRegistroAndIdNot(
                idPaciente,
                ESTADOS_CITAS_ACTIVAS,
                EstadoRegistro.ACTIVO,
                idCitaExcluir)) {
            throw new IllegalStateException("El paciente ya tiene citas activas en estados PENDIENTE, CONFIRMADA o EN_CURSO");
        }
    }

    private void validarCitaActualizable(Cita cita) {
        log.info("Validando si la cita con id {} está en estado actualizable", cita.getId());
        boolean esEstadoActualizable = ESTADOS_CITAS_ACTUALIZABLES.contains(cita.getEstadoCita());
        if (!esEstadoActualizable) {
            throw new IllegalStateException("La cita solo se puede actualizar en estados PENDIENTE o CONFIRMADA");
        }
    }
}
