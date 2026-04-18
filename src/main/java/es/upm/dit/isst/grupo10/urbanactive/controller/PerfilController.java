package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Identificacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PerfilController {

private final OrganizacionRepository organizacionRepository;

public PerfilController(OrganizacionRepository organizacionRepository) {
this.organizacionRepository = organizacionRepository;
}

@GetMapping("/organizaciones/{tipo}/{numero}")
public String verPerfilOrganizacion(@PathVariable String tipo,
@PathVariable String numero,
Model model) {

Identificacion identificacion = new Identificacion(tipo, numero);
Organizacion organizacion = organizacionRepository.findById(identificacion).orElse(null);

if (organizacion == null) {
return "redirect:/actividades";
}

model.addAttribute("organizacion", organizacion);
return "perfil-organizacion";
}
}