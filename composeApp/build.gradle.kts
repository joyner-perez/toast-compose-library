import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "com.joyner.toastcomposelibrary"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

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
            implementation(dependencyNotation = libs.compose.runtime)
            implementation(dependencyNotation = libs.compose.foundation)
            implementation(dependencyNotation = libs.compose.material3)
            implementation(dependencyNotation = libs.compose.ui)
            implementation(dependencyNotation = libs.compose.components.resources)
            implementation(dependencyNotation = libs.compose.uiToolingPreview)
            implementation(dependencyNotation = libs.compose.material.icons.core)
            implementation(dependencyNotation = libs.androidx.lifecycle.viewmodelCompose)
            implementation(dependencyNotation = libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(dependencyNotation = libs.kotlin.test)
        }
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.rules.compose)
    detektPlugins(libs.detekt.rules.libraries)
    detektPlugins(libs.detekt.rules.ruleauthors)
    androidRuntimeClasspath(libs.compose.uiTooling)
}

detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    source.setFrom(
        "src/commonMain/kotlin",
        "src/iosMain/kotlin"
    )
}

mavenPublishing {
    coordinates(
        groupId = "io.github.joyner-perez",
        artifactId = "toastcompose",
        version = "0.0.1"
    )

    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("ToastCompose")
        description.set("Toast notification library for Compose Multiplatform (Android & iOS)")
        url.set("https://github.com/joyner-perez/toast-compose-library")
        inceptionYear.set("2026")
        licenses {
            license {
                name.set("MIT")
                url.set("https://mit-license.org")
                distribution.set("https://mit-license.org")
            }
        }
        developers {
            developer {
                id.set("Joyner")
                name.set("Joyner")
                url.set("https://github.com/joyner-perez")
            }
        }
        scm {
            url.set("https://github.com/joyner-perez/toast-compose-library")
            connection.set("scm:git:git://github.com/joyner-perez/toast-compose-library.git")
            developerConnection.set(
                "scm:git:ssh://git@github.com/joyner-perez/toast-compose-library.git"
            )
        }
    }
}
