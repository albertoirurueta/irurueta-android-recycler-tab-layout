import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.sonarqube)
}

android {
    namespace = "com.irurueta.android.recyclertablayout.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.irurueta.android.recyclertablayout.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "1.1.13-SNAPSHOT"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val buildNumber = System.getenv("BUILD_NUMBER")
        buildConfigField("String", "BUILD_NUMBER", "\"$buildNumber\"")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        buildConfigField("String", "BUILD_TIMESTAMP", "\"" + dateFormatter.format(Date()) + "\"")
        val gitCommit = System.getenv("GIT_COMMIT")
        buildConfigField("String", "GIT_COMMIT", "\"$gitCommit\"")

        val gitBranch = System.getenv("GIT_BRANCH")
        buildConfigField("String", "GIT_BRANCH", "\"$gitBranch\"")

        val apkPrefixLabels = listOf("irurueta-android-recycler-tab-layout", versionName,
            buildNumber)
        base.archivesName = apkPrefixLabels.filter({ it != "" }) .joinToString("-")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        // SARIF report is uploaded to GitHub code scanning by the CI workflows
        sarifReport = true
    }
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

sonar {
    properties {
        property("sonar.projectKey", "albertoirurueta_irurueta-android-recycler-tab-layout")
        property("sonar.projectName", "irurueta-android-recycler-tab-layout-${project.name}")
        property("sonar.organization", "albertoirurueta-github")
        property("sonar.host.url", "https://sonarcloud.io")

        // Source and test directories, binaries and libraries are detected by the Sonar Gradle plugin from the
        // Android variants. Setting sonar.sources/sonar.tests here as well makes files be indexed twice.
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.exclusions", "**/*.json,**/.gradle/**,**/R.class")

        property("sonar.coverage.jacoco.xmlReportPaths",
            listOf("build/reports/coverage/androidTest/debug/connected/report.xml",
                "build/reports/coverage/test/debug/report.xml"))
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.junit.reportPaths",
            listOf("build/test-results/testDebugUnitTest",
                "build/outputs/androidTest-results/connected/debug"))
        property("sonar.androidLint.reportPaths", "build/reports/lint-results-debug.xml")
    }
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}