import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.appdistribution)
    jacoco
}



android {
    namespace = "com.example.testci_cd"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.testci_cd"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

}
jacoco {
    toolVersion = "0.8.13"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate JaCoCo coverage reports."

    // Force task to run even if no execution data exists
    onlyIf { true }

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("customJacocoReportDir/html"))
        xml.outputLocation.set(layout.buildDirectory.file("customJacocoReportDir/jacoco.xml"))
        csv.outputLocation.set(layout.buildDirectory.file("customJacocoReportDir/jacoco.csv"))
    }

    val javaClasses = fileTree(layout.buildDirectory.dir("classes/java/debug")) {
        exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*")
    }
    val kotlinClasses = fileTree(layout.buildDirectory.dir("classes/kotlin/debug")) {
        exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*")
    }
    classDirectories.setFrom(files(javaClasses, kotlinClasses))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })

    doLast {
        val reportDir = layout.buildDirectory.dir("customJacocoReportDir").get().asFile
        val execFile = fileTree(layout.buildDirectory) { include("jacoco/testDebugUnitTest.exec") }
        if (execFile.isEmpty()) {
            logger.warn("No JaCoCo execution data found at build/jacoco/testDebugUnitTest.exec")
        } else {
            logger.lifecycle("Execution data found: ${execFile.files.joinToString()}")
        }
        if (reportDir.exists() && reportDir.listFiles()?.isNotEmpty() == true) {
            logger.lifecycle("JaCoCo reports generated at: ${reportDir.absolutePath}")
        } else {
            logger.warn("No JaCoCo reports generated in ${reportDir.absolutePath}")
        }
    }
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("testDebugUnitTest")
    violationRules {
        rule {
            limit {
                minimum = BigDecimal("0.0")
            }
        }
    }
}



firebaseAppDistribution {
    appId = System.getenv("FIREBASE_APP_ID")
    serviceCredentialsFile = "service-account-key.json"
    artifactType = "APK"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}