package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Identificacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizacionRepository extends CrudRepository<Organizacion, Identificacion> {

    Optional<Organizacion> findBySlug(String slug);

}