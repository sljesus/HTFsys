# Errores Encontrados Durante el Desarrollo de la App HTF

Este documento lista todos los errores encontrados durante el proceso de construcción y ejecución de la app HTF en el dispositivo.

## 25. Error de pluginManagement con Gradle 9
- **Descripción del Error:** BUILD FAILED con 'void Settings_gradle.<init>(org.gradle.kotlin.dsl.support.KotlinScriptHost, org.gradle.plugin.use.PluginDependenciesSpec, org.gradle.api.initialization.Settings)'
- **Causa:** El bloque pluginManagement no es compatible con Gradle 9.3.1 en settings.gradle.kts.
- **Solución Aplicada:** Remover el bloque pluginManagement del settings.gradle.kts para una configuración mínima.
- **Resultado:** El archivo settings.gradle.kts se simplificó al mínimo necesario para Gradle 9.

## Cambios Erroneos en settings.gradle.kts
- **Versión 1:** include ':app' (Sintaxis Groovy en archivo Kotlin DSL) - Erroneo
- **Versión 2:** include(":app") + dependencyResolutionManagement con repositoriesMode.set - Erroneo
- **Versión 3:** include(":app") + pluginManagement con repositories - Erroneo
- **Versión 4:** include(":app") + rootProject.name (Configuración mínima) - Erroneo

## 26. Error con Configuración Mínima en settings.gradle.kts
- **Descripción del Error:** BUILD FAILED con 'void Settings_gradle.<init>(org.gradle.kotlin.dsl.support.KotlinScriptHost, org.gradle.api.initialization.Settings)'
- **Causa:** Gradle 9.3.1 no es compatible con settings.gradle.kts, incluso con configuración mínima.
- **Solución Aplicada:** Convertir settings.gradle.kts a settings.gradle con sintaxis Groovy.
## 27. Error de Gradle no encontrado en PATH
- **Descripción del Error:** PowerShell y CMD no reconocían el comando `gradle`, mostrando "gradle no se reconoce como nombre de un cmdlet, función, archivo de script o programa ejecutable"
- **Causa:** Gradle no estaba instalado globalmente en el sistema Windows o no estaba en el PATH del sistema
- **Solución Aplicada:** Instalación de Gradle mediante Scoop con el comando `scoop install gradle`
- **Por qué funcionó:** Scoop es un gestor de paquetes para Windows que:
  - Instala Gradle en una ubicación estándar
  - Agrega automáticamente el directorio bin de Gradle al PATH del sistema
  - Configura las variables de entorno necesarias para que PowerShell y CMD reconozcan el comando `gradle`
  - Evita conflictos de versiones y permite ejecutar `gradle wrapper` directamente desde cualquier terminal
- **Resultado:** El comando `gradle wrapper` se ejecutó correctamente, permitiendo crear el wrapper y generar el APK
- **Nota:** Esta solución fue crucial porque el gradlew.bat tenía problemas de ejecución y el gradle global vía Scoop proporcionó una alternativa funcional

---

# Resumen Final de Configuración Exitosa

Después de resolver todos los errores anteriores, el proyecto Android HTF está configurado correctamente con:
- Gradle 8.6 (compatible con AGP 8.2.2)
- Kotlin 1.8.10 (compatible con AGP 8.2.2)
- AndroidX habilitado en gradle.properties
- build.gradle en sintaxis Groovy (consistente)
- APK generado exitosamente en app/build/outputs/apk/debug/app-debug.apk
