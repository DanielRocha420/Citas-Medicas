package com.daniel.commons.dto.medico;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO con la información detallada de respuesta de un médico")
public record MedicoResponse(

        @Schema(description = "Identificador único del médico", example = "1")
        Long id,

        @Schema(description = "Nombre completo del médico", example = "Carlos Gómez Hernández")
        String nombre,

        @Schema(description = "Edad del médico en años", example = "35")
        Short edad,

        @Schema(description = "Correo electrónico del médico", example = "carlos.gomez@hospital.com")
        String email,

        @Schema(description = "Número telefónico del médico", example = "2221234567")
        String telefono,

        @Schema(description = "Cédula profesional del médico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "Nombre de la especialidad médica", example = "Cardiología")
        String especialidad,

        @Schema(description = "Estado o descripción de la disponibilidad del médico", example = "Disponible")
        String disponibilidad,

        @Schema(description = "Identificador único del estado de disponibilidad", example = "1")
        Long idDisponibilidad
) {
}