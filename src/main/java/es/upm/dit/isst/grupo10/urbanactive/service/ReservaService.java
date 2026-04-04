package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Reserva;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    
    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public ReservaService(ActividadRepository actividadRepository, UsuarioRepository usuarioRepository, ReservaRepository reservaRepository) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    public boolean reservarPlaza(Usuario usuario, Long actividadId) {
        try {
            Optional<Actividad> actividadOpt = actividadRepository.findById(actividadId);
            if (actividadOpt.isEmpty()) {
                return false;
            }

            Actividad actividad = actividadOpt.get();

            if (actividad.getPlazasDisponibles() <= 0) {
                return false;
            }

            if (yaTieneReservaActiva(usuario, actividadId)) {
                return false;
            }

            Reserva reserva = new Reserva(
                null, // ID se genera automáticamente
                usuario,
                actividadId,
                actividad.getPrecio()
            );

            // Reducir plazas disponibles
            actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() - 1);
            actividadRepository.save(actividad);
            
            reserva.confirmarReserva();
            reservaRepository.save(reserva);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean yaTieneReservaActiva(Usuario usuario, Long actividadId) {
        List<Reserva> reservasExistentes = reservaRepository.findByUsuarioAndActividadId(usuario, actividadId);
        return reservasExistentes.stream()
                .anyMatch(r -> r.estaActiva());
    }

    public List<Reserva> getReservasPorUsuario(Usuario usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

    public List<Reserva> getReservasPorUsuarioEmail(String email) {
        try {
            Email emailObj = new Email(email);
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(emailObj);
            if (usuarioOpt.isEmpty()) {
                return List.of();
            }
            return getReservasPorUsuario(usuarioOpt.get());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public Reserva getReservaById(Long reservaId) {
        return reservaRepository.findById(reservaId).orElse(null);
    }

    public boolean cancelarReserva(Long reservaId, Usuario usuario) {
        try {
            Optional<Reserva> reservaOpt = reservaRepository.findById(reservaId);
            if (reservaOpt.isEmpty()) {
                return false;
            }

            Reserva reserva = reservaOpt.get();
            if (!reserva.getUsuario().equals(usuario) || !reserva.estaActiva()) {
                return false;
            }

            // Devolver plaza disponible
            Optional<Actividad> actividadOpt = actividadRepository.findById(reserva.getActividadId());
            if (actividadOpt.isPresent()) {
                Actividad actividad = actividadOpt.get();
                actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() + 1);
                actividadRepository.save(actividad);
            }

            reserva.cancelarReserva();
            reservaRepository.save(reserva);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean cancelarReserva(Long reservaId, String email) {
        try {
            Email emailObj = new Email(email);
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(emailObj);
            if (usuarioOpt.isEmpty()) {
                return false;
            }
            return cancelarReserva(reservaId, usuarioOpt.get());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    public static class ReservaResultado {
        private final boolean exito;
        private final String mensaje;
        private final Reserva reserva;

        public ReservaResultado(boolean exito, String mensaje, Reserva reserva) {
            this.exito = exito;
            this.mensaje = mensaje;
            this.reserva = reserva;
        }

        public boolean isExito() { return exito; }
        public String getMensaje() { return mensaje; }
        public Reserva getReserva() { return reserva; }
    }
}
