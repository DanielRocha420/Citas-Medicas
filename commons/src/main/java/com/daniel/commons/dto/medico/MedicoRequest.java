package com.daniel.commons.dto.medico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO con la información requerida para registrar o actualizar un médico")
public record MedicoRequest(

        @Schema(description = "Nombre(s) del médico", example = "Carlos", maxLength = 50)
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del médico", example = "Gómez", maxLength = 50)
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del médico", example = "Hernández", maxLength = 50)
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del médico en años (mínimo 18)", example = "35", minimum = "18", maximum = "100")
        @NotNull(message = "La edad es requerida")
        @Min(value = 18, message = "La edad minima es de 18 años")
        @Max(value = 100, message = "La edad maxima es de 100 años")
        Short edad,

        @Schema(description = "Correo electrónico del médico", example = "carlos.gomez@hospital.com", maxLength = 100)
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener el formato correcto (correo@dominio)")
        String email,

        @Schema(description = "Número telefónico a 10 dígitos", example = "2221234567", pattern = "^[0-9]{10}$")
        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefoo debe contener solo 10 digitos numericos")
        String telefono,

        @Schema(description = "Cédula profesional del médico (12 caracteres)", example = "123456789012", minLength = 12, maxLength = 12)
        @NotBlank(message = "La cedula profesional es requerido")
        @Size(min = 12, max = 12, message = "La cedela profesional debe tener exactamente 12 caracteres")
        String cedulaProfesional,

        @Schema(description = "Identificador único de la especialidad médica", example = "1")
        @NotNull(message = "El id de la especialidad es requerida")
        @Positive(message = "El id de la especialidad debe ser positivo")
        Long idEspecialidad
) {}