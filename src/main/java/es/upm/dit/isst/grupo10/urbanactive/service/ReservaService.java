// package es.upm.dit.isst.grupo10.urbanactive.service;

// import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
// import es.upm.dit.isst.grupo10.urbanactive.model.Email;
// import es.upm.dit.isst.grupo10.urbanactive.model.Reserva;
// import es.upm.dit.isst.grupo10.urbanactive.model.Sesion;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
// import java.util.concurrent.atomic.AtomicLong;

// @Service
// public class ReservaService {
    
//     private final List<Reserva> reservas = new ArrayList<>();
//     private final List<Sesion> sesiones = new ArrayList<>();
//     private final ActividadService actividadService;
//     private final AtomicLong contadorReservas = new AtomicLong(1);
//     private final AtomicLong contadorSesiones = new AtomicLong(1);

//     public ReservaService(ActividadService actividadService) {
//         this.actividadService = actividadService;
//         inicializarSesiones();
//     }

//     private void inicializarSesiones() {
//         Actividad actividad1 = actividadService.getActividadById(1L);
//         if (actividad1 != null) {
//             sesiones.add(new Sesion(contadorSesiones.getAndIncrement(), 1L, 
//                 LocalDateTime.of(2026, 4, 26, 14, 0), 10, "Parque el Retiro"));
//             sesiones.add(new Sesion(contadorSesiones.getAndIncrement(), 1L, 
//                 LocalDateTime.of(2026, 4, 27, 16, 0), 10, "Parque el Retiro"));
//         }

//         Actividad actividad2 = actividadService.getActividadById(2L);
//         if (actividad2 != null) {
//             sesiones.add(new Sesion(contadorSesiones.getAndIncrement(), 2L, 
//                 LocalDateTime.of(2026, 4, 30, 18, 0), 5, "Parque del Oeste"));
//         }

//         Actividad actividad3 = actividadService.getActividadById(3L);
//         if (actividad3 != null) {
//             sesiones.add(new Sesion(contadorSesiones.getAndIncrement(), 3L, 
//                 LocalDateTime.of(2026, 5, 6, 19, 0), 14, "El Pardo"));
//         }
//     }

//     public List<Sesion> getSesionesPorActividad(Long actividadId) {
//         return sesiones.stream()
//                 .filter(s -> s.getActividadId().equals(actividadId))
//                 .toList();
//     }

//     public Optional<Sesion> getSesionById(Long sesionId) {
//         return sesiones.stream()
//                 .filter(s -> s.getId().equals(sesionId))
//                 .findFirst();
//     }

//     public ReservaResultado reservarPlaza(String emailUsuario, Long actividadId, Long sesionId) {
//         try {
//             Email email = new Email(emailUsuario);
            
//             Optional<Sesion> sesionOpt = getSesionById(sesionId);
//             if (sesionOpt.isEmpty()) {
//                 return new ReservaResultado(false, "Sesión no encontrada", null);
//             }

//             Sesion sesion = sesionOpt.get();
            
//             if (!sesion.hayPlazasDisponibles()) {
//                 return new ReservaResultado(false, "No hay plazas disponibles para esta sesión", null);
//             }

//             if (yaTieneReservaActiva(email, sesionId)) {
//                 return new ReservaResultado(false, "Ya tienes una reserva activa para esta sesión", null);
//             }

//             Actividad actividad = actividadService.getActividadById(actividadId);
//             if (actividad == null) {
//                 return new ReservaResultado(false, "Actividad no encontrada", null);
//             }

//             Reserva reserva = new Reserva(
//                 contadorReservas.getAndIncrement(),
//                 email,
//                 actividadId,
//                 sesionId,
//                 actividad.getPrecio()
//             );

//             sesion.setPlazasDisponibles(sesion.getPlazasDisponibles() - 1);
//             reserva.confirmarReserva();
//             reservas.add(reserva);

//             return new ReservaResultado(true, "Reserva realizada correctamente", reserva);

//         } catch (IllegalArgumentException e) {
//             return new ReservaResultado(false, "Email inválido: " + e.getMessage(), null);
//         } catch (Exception e) {
//             return new ReservaResultado(false, "Error al procesar la reserva: " + e.getMessage(), null);
//         }
//     }

//     private boolean yaTieneReservaActiva(Email email, Long sesionId) {
//         return reservas.stream()
//                 .anyMatch(r -> r.getEmailUsuario().equals(email) && 
//                               r.getSesionId().equals(sesionId) && 
//                               r.estaActiva());
//     }

//     public List<Reserva> getReservasPorUsuario(String emailUsuario) {
//         try {
//             Email email = new Email(emailUsuario);
//             return reservas.stream()
//                     .filter(r -> r.getEmailUsuario().equals(email))
//                     .toList();
//         } catch (IllegalArgumentException e) {
//             return new ArrayList<>();
//         }
//     }

//     public Reserva getReservaById(Long reservaId) {
//         return reservas.stream()
//                 .filter(r -> r.getId().equals(reservaId))
//                 .findFirst()
//                 .orElse(null);
//     }

//     public boolean cancelarReserva(Long reservaId, String emailUsuario) {
//         try {
//             Email email = new Email(emailUsuario);
//             Optional<Reserva> reservaOpt = reservas.stream()
//                     .filter(r -> r.getId().equals(reservaId) && r.getEmailUsuario().equals(email))
//                     .findFirst();

//             if (reservaOpt.isEmpty()) {
//                 return false;
//             }

//             Reserva reserva = reservaOpt.get();
//             if (!reserva.estaActiva()) {
//                 return false;
//             }

//             Optional<Sesion> sesionOpt = getSesionById(reserva.getSesionId());
//             sesionOpt.ifPresent(sesion -> 
//                 sesion.setPlazasDisponibles(sesion.getPlazasDisponibles() + 1));

//             reserva.cancelarReserva();
//             return true;

//         } catch (IllegalArgumentException e) {
//             return false;
//         }
//     }

//     public List<Reserva> getAllReservas() {
//         return new ArrayList<>(reservas);
//     }

//     public static class ReservaResultado {
//         private final boolean exito;
//         private final String mensaje;
//         private final Reserva reserva;

//         public ReservaResultado(boolean exito, String mensaje, Reserva reserva) {
//             this.exito = exito;
//             this.mensaje = mensaje;
//             this.reserva = reserva;
//         }

//         public boolean isExito() { return exito; }
//         public String getMensaje() { return mensaje; }
//         public Reserva getReserva() { return reserva; }
//     }
// }
