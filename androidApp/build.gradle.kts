plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Compose Multiplatform resources from the KMP library aren't automatically included
// in the AAR with the com.android.kotlin.multiplatform.library plugin, so we copy
// them into androidApp's assets with the correct namespace prefix.
val composeResourcesSource =
    project(":composeApp")
        .layout.buildDirectory
        .dir("generated/compose/resourceGenerator/preparedResources/commonMain/composeResources")

val generatedAssetsDir = layout.buildDirectory.get().dir("generated/composeResources")

val copyComposeResources by tasks.registering(Copy::class) {
    dependsOn(":composeApp:prepareComposeResourcesTaskForCommonMain")
    from(composeResourcesSource)
    into(
        generatedAssetsDir.dir(
            "composeResources/toastcomposelibrary.composeapp.generated.resources"
        )
    )
}

android {
    namespace = "com.joyner.toastcomposelibrary.app"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.joyner.toastcomposelibrary.app"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    sourceSets {
        getByName("main") {
            assets.directories.add(generatedAssetsDir.asFile.toString())
        }
    }
}

tasks.named("preBuild") {
    dependsOn(copyComposeResources)
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.material.icons.core)
    debugImplementation(libs.compose.uiTooling)
}
