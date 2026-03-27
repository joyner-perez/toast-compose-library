import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktechPublish)
}

kotlin {
    android {
        namespace = "com.joyner.toastcomposelibrary"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    coordinates(
        groupId    = "com.joyner",
        artifactId = "toastcompose",
        version    = "1.0.0"
    )

    publishToMavenCentral()

    // Firma requerida para publicar en Maven Central.
    // Para publicar solo en Maven Local (pruebas) se puede omitir.
    signAllPublications()

    pom {
        name.set("ToastCompose")
        description.set("Toast notification library for Compose Multiplatform (Android & iOS)")
        url.set("https://github.com/joyner/ToastComposeLibrary")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        developers {
            developer {
                id.set("joyner")
                name.set("Joyner")
            }
        }
        scm {
            url.set("https://github.com/joyner/ToastComposeLibrary")
            connection.set("scm:git:git://github.com/joyner/ToastComposeLibrary.git")
            developerConnection.set("scm:git:ssh://git@github.com/joyner/ToastComposeLibrary.git")
        }
    }
}
