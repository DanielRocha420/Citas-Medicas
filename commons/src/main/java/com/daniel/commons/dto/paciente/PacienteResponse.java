package com.daniel.commons.dto.paciente;

import com.daniel.commons.enums.EstadoRegistro;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta que representa la información detallada de un paciente")
public record PacienteResponse(
        @Schema(description = "Identificador único del paciente", example = "1")
        Long id,

        @Schema(description = "Nombre(s) del paciente", example = "Daniel")
        String nombre,

        @Schema(description = "Apellido paterno del paciente", example = "Hernández")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del paciente", example = "Rocha")
        String apellidoMaterno,

        @Schema(description = "Edad actual del paciente en años", example = "22")
        Short edad,

        @Schema(description = "Peso en kilogramos", example = "72.5")
        Double peso,

        @Schema(description = "Estatura en metros", example = "1.75")
        Double estatura,

        @Schema(description = "Índice de Masa Corporal (calculado)", example = "23.67")
        Double imc,

        @Schema(description = "Correo electrónico de contacto", example = "daniel@example.com")
        String email,

        @Schema(description = "Número de expediente médico único", example = "EXP-2026-0042")
        String numExpediente,

        @Schema(description = "Número telefónico de contacto", example = "2221234567")
        String telefono,

        @Schema(description = "Dirección de residencia del paciente", example = "Av. Reforma 123, Puebla")
        String direccion,

        @Schema(description = "Estado del registro en el sistema", example = "ACTIVO")
        EstadoRegistro estadoRegistro
) {
}