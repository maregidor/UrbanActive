# Sistema de Reservas - UrbanActive

## Caso de Uso Implementado: "Como usuario participante quiero solicitar la reserva de una plaza para asegurar mi participación en la actividad"

## Descripción del Sistema

Se ha implementado un sistema completo de gestión de reservas que permite a los usuarios participantes:
- Seleccionar sesiones específicas de actividades
- Reservar plazas disponibles
- Gestionar sus reservas activas
- Cancelar reservas si es necesario

## Arquitectura Implementada

### Modelos (Nuevos)
- **`Sesion.java`**: Representa una sesión específica de una actividad con fecha, hora y plazas
- **`Reserva.java`**: Gestiona las reservas con estados (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA)

### Servicios (Nuevos)
- **`ReservaService.java`**: Lógica de negocio para:
  - Crear y gestionar reservas
  - Validar disponibilidad de plazas
  - Evitar reservas duplicadas
  - Cancelar reservas y liberar plazas

### Controladores (Nuevos)
- **`ReservaController.java`**: Endpoints para:
  - Visualizar sesiones disponibles
  - Procesar formularios de reserva
  - Confirmación de reservas
  - Gestión de reservas del usuario

### Plantillas HTML (Nuevas)
- **`reserva-sesiones.html`**: Selección de sesión para una actividad
- **`reserva-formulario.html`**: Formulario de datos del participante
- **`reserva-confirmacion.html`**: Página de confirmación exitosa
- **`mis-reservas.html`**: Panel de gestión de reservas del usuario
- **`reserva-detalle.html`**: Vista detallada de una reserva específica

## Flujo del Usuario

### 1. Descubrir Actividad
```
/actividades → /actividades/{id} → /actividades/{id}/reservar-sesion
```

### 2. Seleccionar Sesión
```
/reservas/actividad/{actividadId} → /reservas/formulario/{actividadId}/{sesionId}
```

### 3. Completar Reserva
```
/reservas/procesar → /reservas/confirmacion/{reservaId}
```

### 4. Gestionar Reservas
```
/reservas/mis-reservas → /reservas/detalle/{reservaId}
```

## Funcionalidades Implementadas

### Reserva de Plazas
- Validación de email del usuario
- Verificación de disponibilidad de plazas
- Prevención de reservas duplicadas
- Confirmación automática de reserva

### Gestión de Sesiones
- Múltiples sesiones por actividad
- Control de plazas disponibles en tiempo real
- Actualización automática al reservar/cancelar

### Estados de Reserva
- **PENDIENTE_CONFIRMACIÓN**: Reserva creada pero no confirmada
- **CONFIRMADA**: Reserva activa y válida
- **CANCELADA**: Reserva cancelada por el usuario
- **COMPLETADA**: Actividad finalizada

### Validaciones y Seguridad
- Validación de formato de email
- Verificación de existencia de actividades y sesiones
- Control de permisos (solo el titular puede cancelar su reserva)
- Manejo de errores y mensajes informativos

## Características Técnicas

### Persistencia en Memoria
- Datos almacenados en listas en memoria
- IDs autoincrementales usando `AtomicLong`
- Datos inicializados al arrancar la aplicación

### Integración con Código Existente
- **Sin modificaciones** a archivos existentes:
  - `Actividad.java` ✅
  - `Usuario.java` ✅
  - `Nivel.java` ✅
  - `Email.java` ✅
  - `ActividadService.java` ✅

- **Pequeña modificación** a `ActividadController.java`:
  - Añadido endpoint para redirigir al sistema de reservas

### Diseño Responsivo
- Interfaces adaptadas para móvil
- Bootstrap 5 para diseño moderno
- Iconos Bootstrap Icons para mejor UX

## Cómo Probar el Sistema

### 1. Iniciar la Aplicación
```bash
./mvnw spring-boot:run
```

### 2. Navegación de Prueba
1. Visita `http://localhost:8080/actividades`
2. Haz clic en cualquier actividad
3. Click en "Reservar Plaza" → Te llevará al selector de sesiones
4. Selecciona una sesión disponible
5. Completa el formulario con tu email
6. Recibirás confirmación de la reserva

### 3. Gestión de Reservas
- Visita `http://localhost:8080/reservas/mis-reservas`
- Introduce tu email para ver tus reservas
- Puedes cancelar reservas activas
- Ver detalles completos de cada reserva

## Ejemplos de Datos Iniciales

### Actividades Disponibles
1. **Running por El Retiro** - 12€ - 10 plazas
2. **Yoga al Aire Libre** - 9.5€ - 5 plazas  
3. **Ruta Urbana en Bicicleta** - Gratis - 14 plazas

### Sesiones por Actividad
- Running: 26/04/2026 14:00 y 27/04/2026 16:00
- Yoga: 30/04/2026 18:00
- Ciclismo: 06/05/2026 19:00

## Próximos Pasos (Sugerencias)

1. **Persistencia en Base de Datos**: Migrar de memoria a JPA/Hibernate
2. **Sistema de Autenticación**: Integrar Spring Security
3. **Notificaciones por Email**: Enviar confirmaciones reales
4. **Sistema de Pagos**: Integrar pasarelas de pago para actividades de pago
5. **Valoraciones**: Sistema de rating para actividades completadas
6. **Mapa Interactivo**: Integración con Google Maps para ubicaciones

## Contacto

El sistema está listo para ser integrado con el resto de componentes de UrbanActive. Todas las funcionalidades del caso de uso están implementadas y funcionando.
