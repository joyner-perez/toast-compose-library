# Design

## Context

Ver proposal.md - Why. Estado observado:

- `composeApp/build.gradle.kts` usa `com.vanniktech.maven.publish` 0.37.0 con `publishToMavenCentral()` y `signAllPublications()`; no lee secretos explícitamente, el plugin los toma como propiedades Gradle.
- `~/.gradle/gradle.properties` tiene `mavenCentralUsername` y `mavenCentralPassword`, pero no `signingInMemoryKey*`.
- Las cinco propiedades existen como variables universales exportadas de fish (`ORG_GRADLE_PROJECT_*`), por eso la terminal funciona. Android Studio arrancado desde el lanzador de escritorio no pasa por fish y no las ve.
- `build/publish/` contiene bundles de 0.0.9 y 0.0.10 pero no de 1.0.0, coherente con el fallo actual.
- La clave de firma es la GPG `B7BBF330` (ed25519) del llavero local.
- `.gitignore` ya excluye `local.properties`; `gradle.properties` del proyecto está versionado y no contiene secretos.

## Goals / Non-Goals

**Goals:**
- Misma configuración sirve para terminal y Android Studio.
- Cero secretos en archivos versionados.
- Fallo temprano y legible si falta algo.

**Non-Goals:**
- CI de publicación en GitHub Actions (el workflow `release.yml` solo crea la GitHub Release).
- Cambiar versión, coordenadas o automatizar el bump de versión.
- Gestores de secretos externos (keyring, pass, 1Password).

## Decisions

1. **`~/.gradle/gradle.properties` como fuente única.** Gradle lo lee en todo daemon, independiente de cómo se lanzó el IDE; está fuera del repo y ya contiene la mitad de las credenciales.
   - Alternativa: exportar las variables en `~/.profile`/`environment.d` para que el escritorio las herede. Rechazada: depende del entorno de sesión, frágil entre DEs y reinicios.
   - Alternativa: `local.properties` del proyecto (ignorado por git). Rechazada: sigue dentro del árbol del proyecto, riesgo de copiarlo/compartirlo, y vanniktech no lo lee sin código extra.
2. **Clave en memoria (`signingInMemoryKey`) en una sola línea con `\n` escapados.** Es el mismo mecanismo que ya funciona hoy vía entorno; `java.util.Properties` decodifica `\n`. Se genera con `gpg --armor --export-secret-keys B7BBF330 | awk 'NR>1{printf "\\n"}{printf "%s",$0}'`.
   - Alternativa: `signing.keyId` + `signing.secretKeyRingFile` (exportar `secring.gpg`). Válida, pero añade un archivo de clave suelto en disco y una segunda vía de configuración.
   - Alternativa: `signing { useGpgCmd() }` con gpg-agent (sin contraseña en disco). Rechazada por ahora: pinentry desde un daemon Gradle del IDE no es fiable y requiere cambiar código de firma.
3. **Comprobación previa en `composeApp/build.gradle.kts`.** Un `doFirst` en las tareas cuyo nombre empieza por `publish` y contiene `MavenCentral` lee las cinco propiedades vía `providers.gradleProperty(...)` (compatible con configuration cache: captura providers, no `project`) y lanza `GradleException` con los nombres que faltan. No se imprimen valores. `publishToMavenLocal`, `assemble*` y tests quedan intactos.
4. **Variables de fish**: se eliminan tras verificar la nueva fuente (`set -e -U ORG_GRADLE_PROJECT_...`), para no tener dos fuentes que diverjan.

## Risks / Trade-offs

- [Contraseña de la clave en texto plano en `~/.gradle/gradle.properties`] → `chmod 600`; es el mismo nivel de exposición que las variables de fish actuales (también en texto plano en `fish_variables`).
- [Escapado incorrecto de la clave multilínea] → verificar con `./gradlew :composeApp:signKotlinMultiplatformPublication` (o la tarea `sign*` equivalente) antes de publicar.
- [El daemon de Android Studio cachea propiedades viejas] → `./gradlew --stop` / reiniciar Android Studio tras editar el archivo.
- [Secretos visibles en logs] → el mensaje de error solo nombra propiedades.

## Migration Plan

1. Añadir propiedades de firma a `~/.gradle/gradle.properties`, `chmod 600`.
2. Verificar firma desde terminal con las variables de fish ocultas (`env -u ...`).
3. Publicar desde Android Studio.
4. Borrar variables universales de fish.

Rollback: las variables de fish siguen funcionando hasta el paso 4; la comprobación previa se revierte borrando el bloque.
