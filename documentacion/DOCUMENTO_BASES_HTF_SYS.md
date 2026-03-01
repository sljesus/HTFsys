# 🚀 PROMPT INTEGRAL: Inicio del Proyecto Android de Gestión de Sistemas HTF con Planificación

## 🎯 VISIÓN GENERAL
Iniciar el desarrollo de una aplicación Android nativa para gestión de sistemas de HTF por proveedores de tecnología, enfocada en cambios rápidos y eficientes para uso personal.

## ETAPA 1: SCOPING Y ANÁLISIS DEL PROYECTO

### Objetivos del Proyecto
- **Problema Principal:** Miembros de HTF reciben "días gratis" por errores en asignaciones de membresías
- **Solución:** App Android de gestión de sistemas para ajustar asignaciones y asignar multas rápidamente
- **Alcance:** Gestión de asignaciones, multas, y resolución de conflictos de membresías

### Análisis Técnico Inicial
- **Backend:** Supabase (PostgreSQL existente)
- **Frontend:** Android nativo únicamente
- **APIs:** Edge Functions existentes para pagos y multas
- **Base de Datos:** Tablas `asignaciones_activas`, `miembros`, `recargos` 

### Requerimientos de Alto Nivel
1. **Funcionalidad Core:**
   - Buscar miembros con conflictos de asignaciones
   - Ajustar fechas de asignaciones (inicio/fin)
   - Asignar multas por diferentes tipos
   - Resolver conflictos de "días gratis"

2. **Requisitos No Funcionales:**
   - Seguridad robusta con service key
   - Interfaz intuitiva para administradores
   - Operaciones offline limitadas
   - Tiempo de respuesta < 2 segundos

## ETAPA 2: CONSTRUCCIÓN DEL EQUIPO

### Roles Requeridos
- **1 Arquitecto de Software:** Liderazgo técnico y diseño de arquitectura
- **1-2 Desarrolladores Android Senior:** Implementación con Kotlin
- **1 QA Tester:** Testing y calidad
- **1 Product Owner (Tú):** Definición de requerimientos y validación

### Competencias Técnicas
- **Android:** MVVM, Kotlin, Jetpack Components
- **Backend:** Supabase, PostgreSQL
- **Patrones:** Repository, Observer, Factory, Strategy
- **Testing:** Unit tests, Integration tests, UI tests

### Timeline Estimado
- **Total:** 12-16 semanas
- **Fase 1 (Planificación):** Semana 1-2
- **Fase 2 (Desarrollo Core):** Semana 3-10
- **Fase 3 (Testing/Deployment):** Semana 11-12
- **Fase 4 (Mantenimiento Inicial):** Semana 13-16

## ETAPA 3: PLANIFICACIÓN DETALLADA

### Estructura del Proyecto
```
htf-admin/
├── app/src/main/java/com/htf/admin/
│   ├── data/
│   │   ├── models/           # Data classes
│   │   ├── repository/       # Repositorios
│   │   ├── local/            # Room database
│   │   └── remote/           # Supabase API
│   ├── domain/
│   │   ├── usecases/         # Casos de uso
│   │   └── entities/         # Entidades de dominio
│   ├── ui/
│   │   ├── screens/          # Activities/Fragments
│   │   ├── components/       # Componentes reutilizables
│   │   └── theme/            # Tema y estilos
│   ├── di/                   # Inyección de dependencias
│   └── utils/                # Utilidades
```

### Tecnologías y Frameworks
- **Lenguaje:** Kotlin
- **Arquitectura:** MVVM con Clean Architecture
- **UI:** Jetpack Compose
- **Base de Datos Local:** Room
- **Backend:** Supabase Kotlin SDK
- **Inyección de Dependencias:** Hilt
- **Navegación:** Jetpack Navigation
- **Testing:** JUnit, Espresso, MockK

## 📋 MODELOS DE DATOS BASADOS EN ESQUEMA

### Member (Miembro)
```kotlin
data class Member(
    val id: Int, // id_miembro
    val nombre: String, // nombres + " " + apellido_paterno + " " + apellido_materno
    val email: String,
    val telefono: String?,
    val fechaRegistro: String
)
```

### Assignment (Asignación Activa)
```kotlin
data class Assignment(
    val id: Int, // id_asignacion
    val idMiembro: Int,
    val idProductoDigital: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val activa: Boolean,
    val cancelada: Boolean
)
```

### Fine (Recargo Cobrado)
```kotlin
data class Fine(
    val id: Int, // id_recargo
    val idMiembro: Int,
    val monto: Double,
    val pagada: Boolean,
    val fechaCreacion: String // fecha_registro
)
```

### Cronograma de Desarrollo

#### Semana 1-2: Planificación y Diseño
- Definición detallada de requerimientos
- Diseño de arquitectura y patrones
- Creación de wireframes y prototipos
- Setup del proyecto base
- **Entregable:** Documento de requerimientos y diseño de arquitectura

#### Semana 3-6: Desarrollo Core - Parte 1
- Implementación de modelos de datos
- Configuración de Supabase
- Autenticación básica
- Pantalla principal con lista de miembros
- **Entregable:** MVP funcional con navegación básica

#### Semana 7-10: Desarrollo Core - Parte 2
- Búsqueda y filtrado de miembros
- Gestión de asignaciones (ver/editar)
- Sistema de multas
- Detección de conflictos
- **Entregable:** Funcionalidades core completas

#### Semana 11-12: Testing y Optimización
- Testing unitario e integración
- Testing de UI/UX
- Optimización de rendimiento
- Preparación para deployment
- **Entregable:** App lista para producción

#### Semana 13-16: Deployment y Mantenimiento Inicial
- Deployment a stores
- Monitoreo inicial
- Soporte post-lanzamiento
- Documentación final
- **Entregable:** App en producción con soporte

## 🎨 ETAPA 4: DISEÑO Y PROTOTIPADO

### Diseño de UI/UX
- **Colores:** Tema funcional y eficiente para operaciones rápidas
- **Tipografía:** Roboto para claridad y velocidad de lectura
- **Navegación:** Navegación simple y directa con acceso rápido a funciones principales
- **Componentes:** Elementos funcionales para gestión rápida de datos y acciones

### Arquitectura de Software
- **Patrones de Diseño:** Repository, Observer, Factory, Strategy, Singleton
- **Capa de Datos:** Repository pattern con fuentes remota (Supabase) y local (Room)
- **Capa de Dominio:** Casos de uso independientes de frameworks
- **Capa de Presentación:** ViewModels con LiveData/StateFlow

### Prototipo de Alto Nivel
1. **Splash Screen** → Verificación de autenticación
2. **Dashboard** → Resumen de conflictos pendientes
3. **Lista de Miembros** → Búsqueda y filtros
4. **Detalle de Miembro** → Asignaciones y acciones
5. **Gestión de Multas** → Historial y nuevas asignaciones

## 👥 ETAPA 5: COMUNICACIÓN CON STAKEHOLDERS

### Stakeholders Identificados
- **Usuario Principal:** Proveedor de Tecnología (Tú)
- **Equipo de Desarrollo:** Arquitecto + Desarrolladores
- **Cliente (Tú):** Definición de requerimientos y uso

### Plan de Comunicación
- **Reuniones Semanales:** Revisiones de progreso
- **Documentación:** Requerimientos y cambios versionados
- **Feedback Loops:** Demos semanales de funcionalidades
- **Escalation:** Canales claros para bloqueos

## 📈 ETAPA 6: MONITOREO Y AJUSTE

### Métricas de Seguimiento
- **Velocidad de Desarrollo:** Story points completados por semana
- **Calidad de Código:** Cobertura de tests > 80%
- **Rendimiento:** Tiempo de respuesta < 2 segundos
- **Satisfacción:** Feedback de usuario en demos

### Plan de Riesgos
- **Riesgo:** Complejidad de integración con Supabase
  - **Mitigación:** Prototipo temprano de conexión
- **Riesgo:** Cambios en requerimientos
  - **Mitigación:** Sprint planning semanal
- **Riesgo:** Falta de expertise en patrones
  - **Mitigación:** Code reviews y pair programming

## 💻 ETAPA 7: DESARROLLO E IMPLEMENTACIÓN

### Setup Inicial Completado ✅

Archivos creados y configurados según mejores prácticas para 2026:

#### Gradle (Raíz)
- **settings.gradle.kts**: Configuración global con repositorios, nombre del proyecto, módulos incluidos.
- **build.gradle.kts**: Plugins para Android, Kotlin, serialization.

#### App Module
- **app/build.gradle.kts**: Configuración específica con dependencias (Supabase, AndroidX), SDK versions óptimas (minSdk 21, targetSdk 34, compileSdk 34).
- **AndroidManifest.xml**: Permisos (INTERNET), configuración de app, actividad MainActivity como launcher.
- **MainActivity.kt**: Pantalla principal con View Binding, comentarios claros.
- **activity_main.xml**: Layout con TextView usando strings y colores personalizados.
- **strings.xml**: Textos de la app (app_name, hello_world).
- **colors.xml**: Paleta de colores personalizada.
- **themes.xml**: Tema Material 3 con colores definidos.
- **ic_launcher.xml**: Ícono vectorial adaptativo para launcher.

Configuración óptima: minSdk 21 para Jetpack Compose, targetSdk 34, MVVM con ViewModel + StateFlow, Supabase para datos.

#### Primera Iteración Funcional
Objetivo: Lista de miembros desde Supabase con navegación básica.

### Estructura Inicial
- **Objetivo:** Lista de miembros desde Supabase
- **Funcionalidades:** Mostrar lista, navegación básica
- **Criterio de Éxito:** App se conecta a Supabase y muestra datos

## 🧪 ETAPA 8: QUALITY ASSURANCE

### Estrategia de Testing
- **Unit Tests:** Casos de uso y lógica de negocio
- **Integration Tests:** Conexión con Supabase
- **UI Tests:** Flujos principales de usuario
- **Performance Tests:** Operaciones críticas

### Herramientas de Testing
- **JUnit 5** para unit tests
- **MockK** para mocking
- **Espresso** para UI tests
- **Firebase Test Lab** para testing en devices

## 🚀 ETAPA 9: DEPLOYMENT

### Preparación para Release
- **Build Variants:** Debug y Release
- **ProGuard:** Optimización y ofuscación
- **Signing:** Configuración de keystores
- **App Bundle:** Preparación para Play Store

### Deployment Steps
1. **Internal Testing:** Test con usuarios internos
2. **Beta Testing:** Release beta en Play Store
3. **Production Release:** Lanzamiento oficial
4. **Monitoring:** Crashlytics y Analytics

## 🔧 ETAPA 10: SOPORTE Y MANTENIMIENTO

### Plan de Soporte
- **Soporte Inicial:** 30 días post-lanzamiento
- **Bug Fixes:** Releases semanales para corrección
- **Feature Requests:** Evaluación mensual
- **Documentación:** Mantener actualizada

### Monitoreo Continuo
- **Crash Reports:** Firebase Crashlytics
- **Analytics:** Firebase Analytics para uso
- **Performance:** Firebase Performance Monitoring
- **User Feedback:** Sistema de feedback en app

## ✅ CRITERIOS DE ÉXITO

### Métricas Técnicas
- **Disponibilidad:** 99.9% uptime
- **Performance:** < 2 segundos respuesta promedio
- **Calidad:** Cobertura de tests > 80%
- **Compatibilidad:** Funciona en 95% de devices Android

### Métricas de Negocio
- **Eficiencia:** 80% reducción en tiempo de gestión de conflictos
- **Satisfacción:** > 4.5/5 en feedback del equipo de tecnología
- **ROI:** Recuperación de ingresos en < 6 meses

### Métricas de Calidad
- **Mantenibilidad:** Código fácil de extender
- **Escalabilidad:** Arquitectura preparada para crecimiento
- **Seguridad:** Sin vulnerabilidades críticas
- **Usabilidad:** Curva de aprendizaje < 30 minutos

---

**Este prompt integra PLANIFICACIÓN COMPLETA con SETUP INICIAL.** Te da todo lo necesario para comenzar el proyecto de manera profesional y estructurada.

## 📝 COMPORTAMIENTO DEL CHAT PARA DESARROLLO

Para asegurar consistencia y control en el proceso de desarrollo:

- El usuario proporcionará instrucciones específicas para el código parte por parte.
- El agente implementará únicamente lo solicitado, sin anticipar o agregar pasos adicionales.
- El agente esperará nuevas instrucciones o confirmación antes de continuar con el siguiente paso.
- Este comportamiento se mantendrá durante todo el proceso de desarrollo para evitar desviaciones.

¿Quieres que ajuste algún aspecto específico del prompt?
