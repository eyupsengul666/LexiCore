plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

kotlin {
    jvmToolchain(25)
}

android {
    namespace = "com.dunyadanuzak.lexicore"
    compileSdk {
        version = release(37) {
            minorApiLevel = 2
        }
    }
    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "com.dunyadanuzak.lexicore"
        minSdk = 24
        targetSdk = 37
        versionCode = 8
        versionName = "1.3.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    lint {
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = true
        checkReleaseBuilds = true
        checkDependencies = true
        checkTestSources = true
        explainIssues = true
        showAll = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(true)
        verbose.set(true)
        progressiveMode.set(true)
        freeCompilerArgs.addAll(
            "-jvm-default=no-compatibility",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xemit-jvm-type-annotations",
            "-Xvalidate-bytecode",
            "-Xreport-all-warnings"
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(
        listOf(
            "-Xlint:all",
            "-Xlint:-options",
            "-Xlint:-processing",
            "-Werror",
            "-parameters",
            "-g"
        )
    )
    options.isDeprecation = true
    options.isWarnings = true
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.play.services.ads)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
}
