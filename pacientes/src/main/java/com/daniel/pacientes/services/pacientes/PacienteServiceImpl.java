package com.daniel.pacientes.services.pacientes;

import com.daniel.commons.dto.paciente.PacienteRequest;
import com.daniel.commons.dto.paciente.PacienteResponse;
import com.daniel.pacientes.entities.Paciente;
import com.daniel.commons.enums.EstadoRegistro;
import com.daniel.pacientes.mappers.PacienteMapper;
import com.daniel.pacientes.repositories.PacienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return listarActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listarActivos() {
        log.info("Listando pacientes activos...");
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        return obtenerActivoPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerActivoPorId(Long id) {
        log.info("Buscando paciente activo con id: {}", id);
        Paciente paciente = pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un paciente activo con el id: " + id));
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerSinValidarEstado(Long id) {
        log.info("Buscando paciente (sin validar estado) con id: {}", id);
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un paciente con el id: " + id));
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando nuevo paciente...");

        validarUnicidad(request, null);

        Paciente paciente = pacienteMapper.requestAEntidad(request);
        pacienteRepository.save(paciente);
        log.info("Paciente registrado con ID: {} y expediente: {}", paciente.getId(), paciente.getNumExpediente());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        log.info("Actualizando paciente con id: {}", id);
        Paciente paciente = pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un paciente activo con el id: " + id));

        // Se eliminó la validación local de citas activas por repositorio
        validarUnicidad(request, paciente);

        paciente.setNombre(request.nombre().trim());
        paciente.setApellidoPaterno(request.apellidoPaterno().trim());
        paciente.setApellidoMaterno(request.apellidoMaterno().trim());
        paciente.setEdad(request.edad());
        paciente.setPeso(request.peso());
        paciente.setEstatura(request.estatura());
        paciente.setEmail(request.email().trim());
        paciente.setTelefono(request.telefono().trim());
        paciente.setDireccion(request.direccion().trim());

        paciente.calcularImc();
        paciente.generarNumExpediente();

        pacienteRepository.save(paciente);
        log.info("Paciente con id {} actualizado correctamente", id);

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        eliminarLogico(id);
    }

    @Override
    public void eliminarLogico(Long id) {
        log.info("Eliminando lógicamente paciente con id: {}", id);
        Paciente paciente = pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un paciente activo con el id: " + id));

        // Se eliminó la validación local de citas activas por repositorio

        paciente.setEstadoRegistro(EstadoRegistro.ELIMINADO);
        pacienteRepository.save(paciente);
        log.info("Paciente con id {} cambiado a estado ELIMINADO", id);
    }

    private void validarUnicidad(PacienteRequest request, Paciente pacienteExistente) {
        if (pacienteExistente == null || !pacienteExistente.getEmail().equalsIgnoreCase(request.email().trim())) {
            if (pacienteRepository.existsByEmail(request.email().trim())) {
                throw new IllegalArgumentException("Ya existe un paciente registrado con el email: " + request.email());
            }
        }

        if (pacienteExistente == null || !pacienteExistente.getTelefono().equals(request.telefono().trim())) {
            if (pacienteRepository.existsByTelefono(request.telefono().trim())) {
                throw new IllegalArgumentException("Ya existe un paciente registrado con el teléfono: " + request.telefono());
            }
        }
    }
}