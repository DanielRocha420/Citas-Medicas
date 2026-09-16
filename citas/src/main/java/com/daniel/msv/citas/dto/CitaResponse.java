package com.daniel.msv.citas.dto;

import com.daniel.commons.dto.medicos.DatosMedico;
import com.daniel.commons.dto.paciente.DatosPaciente;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Informacion de una cita medica registrada")
public record CitaResponse(

        @Schema(description = "Identificador unico de la cita", example = "1")
        Long id,

        @Schema(description = "Informacion del paciente asociado ala cita")
        DatosPaciente paciente,

        @Schema(description = "Informacion del medico que atendera la cita")
        DatosMedico medico,

        @Schema(description = "Fecha y hora programada para cita",
                example = "25/09/2026 10:30",
                type = "String",
                format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(description = "Descripcion de los sintomas indicados po el paciente",
                example = "El paciente presenta dolor de cabeza intenso")
        String sintomas,

        @Schema(description = "Estado actual de la cita", example = "Confirmada por el paciente")
        String estadoCita

        ) {
}
