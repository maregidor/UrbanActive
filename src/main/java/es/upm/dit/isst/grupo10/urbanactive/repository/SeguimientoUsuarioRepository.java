package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoUsuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeguimientoUsuarioRepository extends JpaRepository<SeguimientoUsuario, Long> {

    boolean existsBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    Optional<SeguimientoUsuario> findBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    void deleteBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    long countBySeguido(Usuario seguido);

    long countBySeguidor(Usuario seguidor);

    @Query("""
        select s
        from SeguimientoUsuario s
        join fetch s.seguido
        where s.seguidor = :seguidor
    """)
    List<SeguimientoUsuario> findBySeguidorConSeguido(@Param("seguidor") Usuario seguidor);

    @Query("""
        select s
        from SeguimientoUsuario s
        join fetch s.seguidor
        where s.seguido = :seguido
    """)
    List<SeguimientoUsuario> findBySeguidoConSeguidor(@Param("seguido") Usuario seguido);

    List<SeguimientoUsuario> findBySeguidor(Usuario seguidor);

    List<SeguimientoUsuario> findBySeguido(Usuario seguido);
}