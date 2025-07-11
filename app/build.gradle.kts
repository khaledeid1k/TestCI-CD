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
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(file("${buildDir}/reports/jacoco/test/jacocoTestReport.xml"))
        html.outputLocation.set(file("${buildDir}/reports/jacoco/test/html"))
    }

    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug")
    val mainSrc = "${project.projectDir}/src/main/java"
    val kotlinSrc = "${project.projectDir}/src/main/kotlin"

    sourceDirectories.setFrom(files(listOf(mainSrc, kotlinSrc)))
    classDirectories.setFrom(files(listOf(debugTree)))
    executionData.setFrom(fileTree(buildDir).include("jacoco/testDebugUnitTest.exec"))
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("testDebugUnitTest")

    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug")
    classDirectories.setFrom(files(listOf(debugTree)))
    executionData.setFrom(fileTree(buildDir).include("jacoco/testDebugUnitTest.exec"))

    violationRules {
        rule {
            limit {
                minimum = 0.80.toBigDecimal()
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