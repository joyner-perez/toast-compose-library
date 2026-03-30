import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.vanniktechPublish) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.caupain.version.plugin)
}

val ktLintVersion: String = libs.versions.ktlintVersion.get()

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<KtlintExtension> {
        version.set(ktLintVersion)
        debug.set(true)
        verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        filter {
            exclude("**/com/joyner/toastcomposelibrary/app/**")
        }
    }
    afterEvaluate {
        tasks.withType<BaseKtLintCheckTask>().configureEach {
            val srcDir = project.file("src")
            if (srcDir.exists()) {
                setSource(project.fileTree(srcDir) { include("**/*.kt", "**/*.kts") })
            }
        }
    }
}
