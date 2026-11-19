import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.publish)
}

val libraryVersion = "1.1.12"

android {
    namespace = "com.irurueta.android.recyclertablayout"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testOptions.targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val buildNumber = System.getenv("BUILD_NUMBER")
        val apkPrefixLabels = listOf("android-recycler-tab-layout", libraryVersion, buildNumber)
        base.archivesName = apkPrefixLabels.filter({ it != "" }) .joinToString("-")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
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
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { test ->
                // Robolectric loads classes through its own sandbox classloader, which JaCoCo
                // only instruments when classes without a location are included
                test.extensions.configure(JacocoTaskExtension::class.java) {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }
    lint {
        // SARIF report is uploaded to GitHub code scanning by the CI workflows
        sarifReport = true
    }
    packaging {
        resources {
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/LICENSE.md")
        }
    }
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dokka {
    dokkaSourceSets {
        named("main") {
            includes.from("module.md")
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "albertoirurueta_irurueta-android-recycler-tab-layout")
        property("sonar.projectName", "irurueta-android-recycler-tab-layout")
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
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.irurueta.test.utils)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.mockk.android)
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary(
        // the published variant
        variant = "release",
        // whether to publish a sources jar
        sourcesJar = true,
        // whether to publish a javadoc jar
        publishJavadocJar = true,
    ))

    publishToMavenCentral()
    signAllPublications()

    coordinates("com.irurueta", "irurueta-android-recycler-tab-layout", libraryVersion)

    pom {
        name.set("irurueta-android-recycler-tab-layout")
        description.set("Tab layout implemented on top of a RecyclerView that draws and recycles tab views, supporting very large numbers of tabs with low memory usage")
        inceptionYear.set("2025")
        url.set("https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/blob/main/LICENSE")
                distribution.set("https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("albertoirurueta")
                name.set("Alberto Irurueta")
                email.set("alberto@irurueta.com")
                url.set("https://github.com/albertoirurueta/")
            }
        }
        scm {
            url.set("https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/")
            connection.set("scm:git:github.com/albertoirurueta/irurueta-android-recycler-tab-layout.git")
            developerConnection.set("scm:git:ssh://github.com/albertoirurueta/irurueta-android-recycler-tab-layout.git")
        }
    }
}
