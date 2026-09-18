package com.daniel.msv.medicos.entity;

import com.daniel.commons.enums.DisponibilidadMedico;
import com.daniel.commons.enums.EspecialidadMedico;
import com.daniel.commons.enums.EstadoRegistro;
import com.daniel.commons.utils.StringCustomUtils;
import com.daniel.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MEDICOS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", length = 50, nullable = false)
    private Short edad;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", length = 12, nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private void validarDtos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad) {
        StringCustomUtils.validarTamanio(nombre, 1,50, "El nombre es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,"El apellido paterno es requerido y debe tener entre 1 y 50 caracterest");
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,"El apellido materno es requerido y debe tener entre 1 y 50 caracterest");
        StringCustomUtils.validarTamanio(email, 1,100, "El email es requerido y debe contener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono, 10,10, "El telefono es requerido y debe contener 10 digitos(0-9)");
        StringCustomUtils.validarTamanio(cedulaProfesional, 12,12, "La cedula profesional es requerido y debe contener exactamente 12 caracteres");
        ValoresNumericosUtils.validarRangoShort(edad, (short) 18,(short) 100, "El nombre es requerido y debe contener entre 1 y 50 caracteres");

        if (especialidad == null)
            throw new IllegalArgumentException("La especialidad es requerida");
    }

    private void validadNoEliminado(){
        if(this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("El medico ya esta eliminado");
    }

    public void eliminar(){
        validadNoEliminado();

        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarEspecialidad(EspecialidadMedico especialidad) {

        validadNoEliminado();

        if (especialidad == null)
            throw new IllegalArgumentException("La especialidad es requerida");

        this.especialidad = especialidad;
    }

    public void actualizarDisponibilidad(DisponibilidadMedico disponibilidad) {
        validadNoEliminado();

        if (disponibilidad == null)
            throw new IllegalArgumentException("La disponibilidad es requerida");

        this.disponibilidad = disponibilidad;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad) {
        validadNoEliminado();

        validarDtos(nombre,apellidoPaterno,apellidoMaterno,edad,email,telefono,cedulaProfesional,especialidad);

        actualizarEspecialidad(especialidad);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.cedulaProfesional = cedulaProfesional.trim();

    }
}