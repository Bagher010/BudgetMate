plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.baseline.profile)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

val uploadStoreFile = providers.environmentVariable("BUDGETMATE_UPLOAD_STORE_FILE").orNull
val uploadStorePassword = providers.environmentVariable("BUDGETMATE_UPLOAD_STORE_PASSWORD").orNull
val uploadKeyAlias = providers.environmentVariable("BUDGETMATE_UPLOAD_KEY_ALIAS").orNull
val uploadKeyPassword = providers.environmentVariable("BUDGETMATE_UPLOAD_KEY_PASSWORD").orNull
val hasUploadSigning = listOf(
    uploadStoreFile,
    uploadStorePassword,
    uploadKeyAlias,
    uploadKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.budgetmate"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Support for 16KB page sizes
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Set the BUDGETMATE_UPLOAD_* environment variables to sign release bundles.
            if (hasUploadSigning) {
                signingConfig = signingConfigs.create("upload") {
                    storeFile = file(uploadStoreFile!!)
                    storePassword = uploadStorePassword
                    keyAlias = uploadKeyAlias
                    keyPassword = uploadKeyPassword
                }
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
    
    // ✅ BASELINE PROFILE: Enable baseline profile generation
    buildFeatures {
        buildConfig = true
    }
    
    // Build variant for 16KB page size testing
    flavorDimensions += "pageSize"
    productFlavors {
        create("standard") {
            dimension = "pageSize"
        }
        // Temporarily comment out 16kb flavor for baseline profile generation
        // create("16kb") {
        //     dimension = "pageSize"
        //     // Additional configuration for 16KB page size
        //     buildConfigField("boolean", "ENABLE_16KB_PAGE_SIZE", "true")
        // }
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
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "trace_processor_shell_*"
            excludes += "tracebox_*"
        }
        jniLibs {
            useLegacyPackaging = false
            excludes += "libbenchmarkNative.so"
            excludes += "libtracing_perfetto.so"
        }
    }
    
    // 16KB page size compatibility
    androidComponents {
        onVariants { variant ->
            variant.packaging.resources.excludes.add("trace_processor_shell_*")
            variant.packaging.resources.excludes.add("tracebox_*")
            variant.packaging.jniLibs.excludes.add("libbenchmarkNative.so")
            variant.packaging.jniLibs.excludes.add("libtracing_perfetto.so")
        }
    }
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
    
    // Graphics shapes for Material3 expressive shapes
    implementation("androidx.graphics:graphics-shapes:1.0.1")
    
    // Material Icons
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Room database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Performance optimizations - immutable collections
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
    
    // Benchmarking support - only for testing, not included in release builds
    androidTestImplementation(libs.androidx.benchmark.macro.junit4)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    
    // Baseline profiles for startup optimization
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")
    
    // Startup optimization
    implementation("androidx.startup:startup-runtime:1.2.0")
    
    // WorkManager for scheduled notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Baseline profile dependency (configured separately)
    baselineProfile(project(":baseline-profiles"))
    
    // Unit Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    
    // Android Instrumented Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.room:room-testing:2.8.2")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// ✅ BASELINE PROFILE: Configure baseline profile generation
// Note: Baseline profile will be generated using the enhanced BaselineProfileGenerator

baselineProfile {
    // Automatically generate baseline profiles during build
    automaticGenerationDuringBuild = false
    
    // Save baseline profiles to version control
    saveInSrc = true
    
    // Enable baseline profile for all variants
    mergeIntoMain = true
}
