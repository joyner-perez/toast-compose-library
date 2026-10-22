# Spec Delta

## Purpose

Define cómo el build publica la librería en Maven Central con firma, tomando credenciales de fuera del repositorio, de forma idéntica desde terminal y desde Android Studio.

## ADDED Requirements

### Requirement: Credenciales fuera del repositorio
El build SHALL obtener credenciales de Sonatype y clave de firma únicamente de propiedades Gradle de nivel usuario o del entorno; ningún archivo versionado SHALL contener valores de esas propiedades.

#### Scenario: Repositorio sin secretos
- **WHEN** se busca en los archivos versionados `mavenCentralPassword`, `signingInMemoryKey` o un bloque `BEGIN PGP PRIVATE KEY`
- **THEN** no aparece ningún valor asignado a esas propiedades

### Requirement: Publicación desde Android Studio
El build SHALL poder publicar y firmar en Maven Central cuando se ejecuta desde Android Studio sin depender de variables de entorno de la shell del usuario.

#### Scenario: IDE lanzado desde el escritorio
- **WHEN** las propiedades están en `~/.gradle/gradle.properties` y se ejecuta `publishToMavenCentral` desde el panel Gradle de Android Studio
- **THEN** los artefactos se firman y se suben a Maven Central

#### Scenario: Paridad con terminal
- **WHEN** se ejecuta la misma tarea desde terminal con la misma configuración de usuario
- **THEN** el resultado es el mismo que desde Android Studio

### Requirement: Error claro si faltan credenciales
Antes de publicar en Maven Central, el build SHALL fallar si falta alguna propiedad requerida, nombrando las propiedades ausentes y la ubicación esperada, sin imprimir valores de ninguna propiedad.

#### Scenario: Falta la clave de firma
- **WHEN** `signingInMemoryKey` no está definida y se ejecuta una tarea de publicación a Maven Central
- **THEN** el build falla antes de subir nada con un mensaje que nombra `signingInMemoryKey` y `~/.gradle/gradle.properties`

#### Scenario: Tareas no relacionadas no se ven afectadas
- **WHEN** faltan credenciales y se ejecuta `assembleDebug`, `allTests` o `publishToMavenLocal`
- **THEN** la tarea no falla por la comprobación de credenciales
