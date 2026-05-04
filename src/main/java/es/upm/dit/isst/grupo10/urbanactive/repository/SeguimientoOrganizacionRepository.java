package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoOrganizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeguimientoOrganizacionRepository extends JpaRepository<SeguimientoOrganizacion, Long> {

    boolean existsBySeguidorAndOrganizacion(Usuario seguidor, Organizacion organizacion);

    Optional<SeguimientoOrganizacion> findBySeguidorAndOrganizacion(Usuario seguidor, Organizacion organizacion);

    void deleteBySeguidorAndOrganizacion(Usuario seguidor, Organizacion organizacion);

    long countByOrganizacion(Organizacion organizacion);

    @Query("""
        select s
        from SeguimientoOrganizacion s
        join fetch s.organizacion
        where s.seguidor = :seguidor
    """)
    List<SeguimientoOrganizacion> findBySeguidorConOrganizacion(@Param("seguidor") Usuario seguidor);

    List<SeguimientoOrganizacion> findBySeguidor(Usuario seguidor);

    List<SeguimientoOrganizacion> findByOrganizacion(Organizacion organizacion);
}