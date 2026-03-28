import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermissions

rootProject.name = "ToastComposeLibrary"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

val hooksDir = rootDir.toPath().resolve(".git/hooks")
val targetHook = hooksDir.resolve("pre-commit")
val sourceHook = rootDir.toPath().resolve(".scripts/pre-commit")

if (Files.notExists(sourceHook)) {
    throw GradleException("❌ No existe el hook fuente: $sourceHook. ¿Está en .scripts/pre-commit?")
}

Files.createDirectories(hooksDir)

println("🔧 Installing Git hook pre-commit…")
Files.copy(sourceHook, targetHook, StandardCopyOption.REPLACE_EXISTING)

try {
    Files.setPosixFilePermissions(
        targetHook,
        PosixFilePermissions.fromString("rwxr-xr-x")
    )
} catch (_: Throwable) {}

println("✅ Hook installed at $targetHook")

include(":composeApp")
include(":androidApp")
