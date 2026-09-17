package com.daniel.msv.citas.repository;

import com.daniel.commons.enums.EstadoRegistro;
import com.daniel.msv.citas.entity.Cita;
import com.daniel.msv.citas.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoCitaInAndEstadoRegistro(Long idPaciente, List<EstadoCita> estadosCita, EstadoRegistro estadoRegistro);

    boolean existsByIdMedicoAndEstadoCitaInAndEstadoRegistro(Long idMedico, List<EstadoCita> estadosCita, EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoCitaInAndEstadoRegistroAndIdNot(
            Long idPaciente, List<EstadoCita> estadosCita, EstadoRegistro estadoRegistro, Long idCita);
}
