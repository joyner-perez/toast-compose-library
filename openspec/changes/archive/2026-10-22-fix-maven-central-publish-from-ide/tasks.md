# Tasks

## 1. Credenciales de usuario (fuera del repo)

- [x] 1.1 Añadir `signingInMemoryKeyId`, `signingInMemoryKeyPassword` y `signingInMemoryKey` (clave armored exportada de `B7BBF330` en una línea con `\n` escapados) a `~/.gradle/gradle.properties` y aplicar `chmod 600`; verificar con `grep -c '^signingInMemoryKey' ~/.gradle/gradle.properties` = 3 y `stat -c %a` = 600
- [x] 1.2 Verificar que la firma funciona sin el entorno de fish: `env -u ORG_GRADLE_PROJECT_signingInMemoryKey -u ORG_GRADLE_PROJECT_signingInMemoryKeyId -u ORG_GRADLE_PROJECT_signingInMemoryKeyPassword -u ORG_GRADLE_PROJECT_mavenCentralUsername -u ORG_GRADLE_PROJECT_mavenCentralPassword ./gradlew --stop` seguido de la tarea `sign*` de `:composeApp` con el mismo `env -u`; pasa y genera `.asc` en `composeApp/build`

## 2. Comprobación previa en el build

- [x] 2.1 En `composeApp/build.gradle.kts`, añadir `doFirst` a las tareas `publish*MavenCentral*` que compruebe las cinco propiedades vía `providers.gradleProperty` y lance `GradleException` nombrando las ausentes y `~/.gradle/gradle.properties`, sin valores; verificar ejecutando `./gradlew :composeApp:publishToMavenCentral -PsigningInMemoryKey=` sin la propiedad real (o con `GRADLE_USER_HOME` temporal vacío) y comprobar mensaje
- [x] 2.2 Verificar que no rompe otras tareas: `./gradlew :composeApp:assembleDebug :composeApp:testDebugUnitTest :composeApp:publishToMavenLocal ktlintCheck detekt --configuration-cache` con un `GRADLE_USER_HOME` sin credenciales pasa (si `publishToMavenLocal` exige firma por `signAllPublications`, documentarlo en README y excluirlo del escenario)
- [x] 2.3 Documentar en `README.md` sección "Publishing": propiedades requeridas, ubicación `~/.gradle/gradle.properties`, comando de exportación de la clave, `chmod 600`, y cómo lanzar `publishToMavenCentral` desde el panel Gradle de Android Studio; verificar que el comando de exportación documentado produce una línea única

## 3. Integración y limpieza

- [x] 3.1 Reiniciar Android Studio y ejecutar `composeApp > Tasks > publishing > publishToMavenCentral` desde el panel Gradle; verificar subida correcta en central.sonatype.com (Deployments) — acción externa, confirmar con el usuario antes de ejecutar
- [x] 3.2 Borrar variables universales de fish (`set -e -U ORG_GRADLE_PROJECT_<nombre>` para las cinco); verificar con `set -U | grep ORG_GRADLE_PROJECT` vacío y repetir 1.2 desde una shell nueva
- [x] 3.3 Verificar repo sin secretos: `git grep -nE 'mavenCentralPassword=|signingInMemoryKey(Password)?=|BEGIN PGP PRIVATE'` sin resultados
