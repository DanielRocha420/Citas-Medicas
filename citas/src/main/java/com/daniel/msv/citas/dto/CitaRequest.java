package com.daniel.msv.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CitaRequest(

        @Schema(description = "Identificador del medio", example = "5", minimum = "1")
        @NotNull(message = "El id del paciente es requerido")
        @Positive(message = "El id del paciente debe ser positivo")
        Long idPaciente,

        @Schema(description = "Identificador del paciente", example = "5", minimum = "1")
        @NotNull(message = "El id del medico es requerido")
        @Positive(message = "El id del medico debe ser positivo")
        Long idMedico,

        @Schema(description = "Fecha y hora programada para cita. Debe ser una fecha y hora actual o futura",
                example = "25/09/2026 10:30",
                type = "String",
                format = "date-time")
        @NotNull(message = "La fecha de la cita es requerida")
        @FutureOrPresent(message = "La fecha de la cita debe ser futura")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(description = "Descripcion de los sintomas que presenta el paciente",
                example = "El paciente presenta dolor de cabeza intenso",
                minLength = 20,
                maxLength = 500)
        @NotBlank(message = "Los sintomas son requerido")
       @Size(min = 20, max = 500, message = "La descripcion de los sintomas debe tener entre 20 a 500 caracteres")
        String sintomas
) {
}
