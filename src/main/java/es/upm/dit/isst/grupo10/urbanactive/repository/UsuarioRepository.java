package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Email> {
    Optional<Usuario> findBySlug(String slug);
}