package service;

import es.upm.dit.isst.grupo10.urbanactive.model.*;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.ReservaRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import es.upm.dit.isst.grupo10.urbanactive.service.ReservaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

private ActividadRepository actividadRepository;
private UsuarioRepository usuarioRepository;
private ReservaRepository reservaRepository;
private ReservaService reservaService;

private Usuario usuario;
private Actividad actividad;

@BeforeEach
void setUp() {
actividadRepository = mock(ActividadRepository.class);
usuarioRepository = mock(UsuarioRepository.class);
reservaRepository = mock(ReservaRepository.class);

reservaService = new ReservaService(
actividadRepository,
usuarioRepository,
reservaRepository
);

usuario = new Usuario();
usuario.setEmail(new Email("usuario@test.com"));
usuario.setNombre("Usuario Test");

actividad = new Actividad();
actividad.setId(1L);
actividad.setTitulo("Running Madrid");
actividad.setPrecio(0.0);
actividad.setPlazasDisponibles(5);
actividad.setPlazasTotales(5);
}

@Test
void noDebePermitirReservaDuplicada() {
Reserva reservaExistente = new Reserva();
reservaExistente.setUsuario(usuario);
reservaExistente.setActividadId(1L);
reservaExistente.setEstado(Reserva.EstadoReserva.CONFIRMADA);

when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
when(reservaRepository.findByUsuarioAndActividadId(usuario, 1L))
.thenReturn(List.of(reservaExistente));

boolean resultado = reservaService.reservarPlaza(usuario, 1L);

assertFalse(resultado);
verify(reservaRepository, never()).save(any(Reserva.class));
verify(actividadRepository, never()).save(any(Actividad.class));
}

@Test
void noDebeReservarSiNoHayPlazasDisponibles() {
actividad.setPlazasDisponibles(0);

when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

boolean resultado = reservaService.reservarPlaza(usuario, 1L);

assertFalse(resultado);
verify(reservaRepository, never()).save(any(Reserva.class));
verify(actividadRepository, never()).save(any(Actividad.class));
}

@Test
void debeReservarCorrectamenteSiHayPlazasYNoHayReservaPrevia() {
when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
when(reservaRepository.findByUsuarioAndActividadId(usuario, 1L))
.thenReturn(List.of());

boolean resultado = reservaService.reservarPlaza(usuario, 1L);

assertTrue(resultado);
assertEquals(4, actividad.getPlazasDisponibles());

verify(actividadRepository).save(actividad);
verify(reservaRepository).save(any(Reserva.class));
}

@Test
void cancelarReservaDebeLiberarUnaPlazaYCambiarEstado() {
Reserva reserva = new Reserva();
reserva.setId(10L);
reserva.setUsuario(usuario);
reserva.setActividadId(1L);
reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);

actividad.setPlazasDisponibles(3);

when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));
when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

boolean resultado = reservaService.cancelarReserva(10L, usuario);

assertTrue(resultado);
assertEquals(4, actividad.getPlazasDisponibles());
assertEquals(Reserva.EstadoReserva.CANCELADA, reserva.getEstado());

verify(actividadRepository).save(actividad);
verify(reservaRepository).save(reserva);
}
}

