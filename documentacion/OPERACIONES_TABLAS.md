# Operaciones Necesarias por Tabla

## Miembros (Members)
- Ver asignaciones activas y visitas asociadas.
- Editar asignaciones/visitas (ej. extender fechas).
- Eliminar asignaciones/visitas (cancelar).

## Productos Digitales (Digital Products)
- Solo lectura informativa para mostrar productos disponibles (no CRUD).

## Asignaciones Activas (Active Assignments)
- Ver asignaciones activas por miembro.
- Asignar nuevos productos. solo en caso de que  no tengan el producto digital con id 13 
- Cancelar asignaciones.

## Registro Entradas (Entry Records)
- Mostrar últimas entradas del día actual.
- Filtrar por fechas específicas.
- Eliminar entradas seleccionadas.

## Recargos Cobrados (Charged Fines)
- Crear multas rápidamente cuando se detecten incumplimientos (usando Edge Function como en el script Node.js).
- Ver multas impagas.
- Marcar multas como pagadas.

## Visitas (Visits)
- Rastrear visitas diarias.
- Ver estadísticas de uso.
- Marcar visitas como usadas. teine qu ever con la tabla de miembros para ver nombres

## Notificaciones POS (POS Notifications)
- Eliminar notificaciones con asunto "pendiente" y fecha de un día anterior.
