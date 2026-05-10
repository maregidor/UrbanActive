package service;

import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoUsuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.SeguimientoUsuarioRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeguimientoUsuarioTest {

@Test
void noDebeSerValidoSeguirseAUnoMismo() {
Usuario usuario = new Usuario();
usuario.setEmail(new Email("usuario@test.com"));
usuario.setNombre("Usuario Test");

SeguimientoUsuario seguimiento = new SeguimientoUsuario(usuario, usuario);

assertFalse(seguimiento.esValido());
}

@Test
void debeSerValidoSeguirAOtroUsuario() {
Usuario seguidor = new Usuario();
seguidor.setEmail(new Email("seguidor@test.com"));
seguidor.setNombre("Seguidor");

Usuario seguido = new Usuario();
seguido.setEmail(new Email("seguido@test.com"));
seguido.setNombre("Seguido");

SeguimientoUsuario seguimiento = new SeguimientoUsuario(seguidor, seguido);

assertTrue(seguimiento.esValido());
}

@Test
void noDebePermitirSeguirDosVecesAlMismoUsuario() {
SeguimientoUsuarioRepository repository = mock(SeguimientoUsuarioRepository.class);

Usuario seguidor = new Usuario();
seguidor.setEmail(new Email("carlos@test.com"));
seguidor.setNombre("Carlos");

Usuario seguido = new Usuario();
seguido.setEmail(new Email("lucia@test.com"));
seguido.setNombre("Lucía");

when(repository.existsBySeguidorAndSeguido(seguidor, seguido))
.thenReturn(true);

boolean yaExiste = repository.existsBySeguidorAndSeguido(seguidor, seguido);

assertTrue(yaExiste);
verify(repository, never()).save(any(SeguimientoUsuario.class));
}
}