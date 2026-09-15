package com.daniel.pacientes.mappers;

import com.daniel.commons.dto.paciente.PacienteRequest;
import com.daniel.commons.dto.paciente.PacienteResponse;
import com.daniel.commons.mapper.CommonMapper;
import com.daniel.pacientes.entities.Paciente;
import com.daniel.commons.enums.EstadoRegistro;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {

    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request == null) return null;

        Paciente paciente = Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .peso(request.peso())
                .estatura(request.estatura())
                .email(request.email().trim())
                .telefono(request.telefono().trim())
                .direccion(request.direccion().trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

        paciente.calcularImc();
        paciente.generarNumExpediente();

        return paciente;
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null) return null;

        return new PacienteResponse(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getApellidoPaterno(),
                entidad.getApellidoMaterno(),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getNumExpediente(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getEstadoRegistro()
        );
    }
}