plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggersHiltAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")

}

android {
    namespace = "com.fedeyruben.proyectofinaldamd"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fedeyruben.proyectofinaldamd"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }



    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ICONOS
    implementation(libs.androidx.material.icons.extended)

    //Google maps
    //Mirar luego con que version va mejor
    //implementation ("com.google.maps.android:maps-compose:2.11.4")
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.android.maps.utils)

    // LiveData
    implementation(libs.androidx.runtime.livedata)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.safetynet)

    //RealtimeDB
    implementation("com.google.firebase:firebase-database-ktx")

    // Agrega esto para Accompanist Permissions
    implementation(libs.accompanist.permissions)

    //Corrutinas
    implementation (libs.androidx.lifecycle.runtime.ktx)

    //DataStore
    implementation (libs.androidx.datastore.preferences)

    // Dagger Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
//    kapt(libs.androidx.hilt.compiler)
//    kapt(libs.hilt.android.compiler)
    kapt (libs.hilt.compiler)


    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Cargar imagen UI Friends
    implementation (libs.coil.compose)

    // Room
    implementation (libs.androidx.room.ktx)
    kapt (libs.androidx.room.compiler)


    //Navigation
    implementation(libs.androidx.navigation.compose)

    // Swipe sirve para hacer swipe en la pantalla hacia la izquierda o derecha
    implementation (libs.swipe)

    // Librería para manejar números de teléfono
    implementation (libs.libphonenumber) // (o la versión más reciente)


}