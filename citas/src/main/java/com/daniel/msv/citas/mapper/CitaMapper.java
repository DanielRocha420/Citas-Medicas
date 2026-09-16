package com.daniel.msv.citas.mapper;

import com.daniel.commons.dto.medicos.MedicoResponse;
import com.daniel.commons.dto.medicos.DatosMedico;
import com.daniel.commons.dto.paciente.PacienteResponse;
import com.daniel.commons.dto.paciente.DatosPaciente;
import com.daniel.commons.mapper.CommonMapper;
import com.daniel.msv.citas.dto.CitaRequest;
import com.daniel.msv.citas.dto.CitaResponse;
import com.daniel.msv.citas.entity.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {

    @Override
    public Cita requestAEntidad(CitaRequest request) {
        if (request == null) return null;

        return Cita.crear(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        if (entidad == null) return null;

        String estadoDesc = (entidad.getEstadoCita() != null)
                ? entidad.getEstadoCita().getDescripcion()
                : null;

        return new CitaResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                estadoDesc
        );
    }

    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        if (entidad == null) return null;

        String estadoDesc = (entidad.getEstadoCita() != null)
                ? entidad.getEstadoCita().getDescripcion()
                : null;

        return new CitaResponse(
                entidad.getId(),
                pacienteResponseADatosPaciente(paciente),
                medicoResponseADatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                estadoDesc
        );
    }

    private DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente) {
        if (paciente == null) return null;

        double imc = paciente.imc();
        String imcFormateado = Math.round(imc * 100.0) / 100.0 + " " + clasificacionIMC(imc);

        return new DatosPaciente(
                paciente.nombre(),
                paciente.numExpediente(),
                paciente.email(),
                paciente.peso() + " Kg",
                paciente.estatura() + " m.",
                imcFormateado,
                paciente.telefono()
        );
    }

    private String clasificacionIMC(double imc) {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidad grado I";
        if (imc < 40) return "Obesidad de grado II";
        return "Obesidad de grado III";
    }

    private DatosMedico medicoResponseADatosMedico(MedicoResponse medico) {
        if (medico == null) return null;

        return new DatosMedico(
                medico.nombre(),
                medico.cedulaProfesional(),
                medico.especialidad()
        );
    }
}