plugins {
    id("com.android.application")
}

android {
    namespace = "com.immo2n.halalife"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.immo2n.halalife"
        minSdk = 24
        targetSdk = 34
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //Custom
    implementation("com.jsibbold:zoomage:1.3.1")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.github.takusemba:cropme:2.0.8")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.jsibbold:zoomage:1.3.1")
    implementation("com.kaopiz:kprogresshud:1.2.0")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}