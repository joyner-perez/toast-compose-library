# Proposal

## Why

Publicar en Maven Central funciona desde terminal fish pero no desde Android Studio. Las cinco credenciales (`mavenCentralUsername`, `mavenCentralPassword`, `signingInMemoryKey`, `signingInMemoryKeyId`, `signingInMemoryKeyPassword`) solo existen como variables universales de fish (`ORG_GRADLE_PROJECT_*`); Android Studio lanzado desde el escritorio no hereda ese entorno, así que Gradle no ve la clave de firma (en `~/.gradle/gradle.properties` solo están las credenciales de Sonatype) y la publicación falla. Ninguna clave debe acabar dentro del repositorio.

## What Changes

- Fuente única de credenciales: `~/.gradle/gradle.properties` (GRADLE_USER_HOME, fuera del proyecto), leída por cualquier daemon Gradle, sea de terminal o de Android Studio. Se añaden ahí las tres propiedades de firma que faltan.
- `composeApp/build.gradle.kts`: comprobación temprana en las tareas de publicación a Maven Central que falla con un mensaje claro listando los **nombres** de las propiedades que faltan (nunca valores) y dónde ponerlas.
- README: sección "Publishing" con el procedimiento (dónde van las propiedades, cómo exportar la clave GPG en una línea, qué tarea ejecutar desde el panel Gradle de Android Studio).
- Limpieza opcional de las variables universales de fish duplicadas para tener una sola fuente.
- Sin cambios en la API de la librería ni en coordenadas Maven.

## Capabilities

### New Capabilities

- `library-publishing`: cómo el build resuelve credenciales y firma para publicar en Maven Central sin secretos en el repositorio, y cómo informa cuando faltan.

### Modified Capabilities

(ninguna)

## Impact

- `composeApp/build.gradle.kts` (bloque `mavenPublishing` / tareas de publicación).
- `README.md` (documentación).
- Entorno local del desarrollador: `~/.gradle/gradle.properties`, `~/.config/fish/fish_variables` (fuera del repo, no versionados).
- Sin dependencias nuevas; se mantiene `com.vanniktech.maven.publish` 0.37.0.
