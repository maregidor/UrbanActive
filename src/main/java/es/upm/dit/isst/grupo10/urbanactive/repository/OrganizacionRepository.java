package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Identificacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;




@Repository
public interface OrganizacionRepository extends JpaRepository <Organizacion, Identificacion> {
    Optional<Organizacion> findByIdentificacionNumero(String numero);


    Optional<Organizacion> findByEmailDireccion(Email email);
    

}