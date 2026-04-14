package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long>  {
    
    List<Actividad> findAllByOrderByFechaAsc();
    List<Actividad> findAllByOrderByPrecioAsc();
    List<Actividad> findAllByOrderByOrganizacion_Valoracion_PuntuacionDesc();

}
