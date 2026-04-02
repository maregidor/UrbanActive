package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Reserva;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findByUsuario(Usuario usuario);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.estado IN ('PENDIENTE_CONFIRMACION', 'CONFIRMADA')")
    List<Reserva> findActivasByUsuario(@Param("usuario") Usuario usuario);
    
    List<Reserva> findByUsuarioAndActividadId(Usuario usuario, Long actividadId);
}
